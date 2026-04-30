<template>
  <div>
    <el-card shadow="never">
      <div style="display:flex;justify-content:space-between;margin-bottom:16px">
        <el-radio-group v-model="filterType" size="small" @change="loadData">
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button label="unread">未读</el-radio-button>
          <el-radio-button label="alarm">告警</el-radio-button>
          <el-radio-button label="order">工单</el-radio-button>
          <el-radio-button label="system">系统</el-radio-button>
        </el-radio-group>
        <el-button size="small" @click="handleMarkAllRead">全部已读</el-button>
      </div>

      <div v-loading="loading">
        <div v-for="msg in messages" :key="msg.id"
          :style="{ background: msg.isRead ? '#fff' : '#f6ffed', padding: '16px', borderBottom: '1px solid #f0f0f0', cursor: 'pointer' }"
          @click="handleRead(msg)">
          <div style="display:flex;align-items:center;gap:10px">
            <el-badge :is-dot="!msg.isRead">
              <el-icon :size="20" :color="typeColor[msg.type] || '#1890FF'"><component :is="typeIcon[msg.type] || 'Bell'" /></el-icon>
            </el-badge>
            <div style="flex:1">
              <div style="display:flex;justify-content:space-between">
                <span style="font-weight:600;font-size:14px;color:#333">{{ msg.title }}</span>
                <span style="font-size:12px;color:#999">{{ msg.createTime }}</span>
              </div>
              <p style="color:#666;font-size:13px;margin-top:6px">{{ msg.content }}</p>
            </div>
          </div>
        </div>

        <el-empty v-if="!messages.length" description="暂无消息" />
      </div>

      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        style="margin-top:16px;justify-content:flex-end"
        @change="loadData"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../stores/user'
import { getNotificationList, markAsRead, markAllAsRead } from '../../api/notification'

const userStore = useUserStore()
const filterType = ref('all')
const loading = ref(false)
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)
const messages = ref([])

const typeIcon = { alarm: 'Bell', order: 'Document', system: 'Setting' }
const typeColor = { alarm: '#ff4d4f', order: '#1890FF', system: '#722ed1' }

async function loadData() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize: pageSize.value, userId: userStore.userInfo.id }
    if (filterType.value === 'unread') params.isRead = 0
    else if (filterType.value !== 'all') params.type = filterType.value

    const res = await getNotificationList(params)
    if (res.code === 200 && res.data) {
      messages.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch {
    ElMessage.error('加载通知失败')
  } finally {
    loading.value = false
  }
}

async function handleRead(msg) {
  if (msg.isRead) return
  try {
    await markAsRead(msg.id)
    msg.isRead = 1
  } catch {
    // 静默失败
  }
}

async function handleMarkAllRead() {
  try {
    await markAllAsRead({ userId: userStore.userInfo.id })
    ElMessage.success('已全部标记为已读')
    loadData()
  } catch {
    ElMessage.error('操作失败')
  }
}

onMounted(loadData)
</script>
