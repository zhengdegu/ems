<template>
  <div>
    <el-card shadow="never">
      <!-- 顶部筛选栏 -->
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:20px">
        <div style="display:flex;gap:12px">
          <el-button :type="filterType === 'all' ? 'primary' : 'default'" @click="switchFilter('all')">
            全部 <el-badge :value="total" :max="99" style="margin-left:6px" />
          </el-button>
          <el-button :type="filterType === 'unread' ? 'primary' : 'default'" @click="switchFilter('unread')">
            未读 <el-badge :value="unreadCount" :max="99" style="margin-left:6px" type="danger" />
          </el-button>
        </div>
        <el-button @click="handleMarkAllRead">全部已读</el-button>
      </div>

      <div v-loading="loading">
        <!-- 消息卡片列表 -->
        <div
          v-for="msg in messages"
          :key="msg.id"
          class="notification-card"
          :class="{ 'is-read': msg.isRead }"
          :style="{ borderLeftColor: getBorderColor(msg.type) }"
        >
          <div class="notification-icon" :style="{ background: getBgColor(msg.type) }">
            <el-icon :size="20" :color="getBorderColor(msg.type)">
              <component :is="getIcon(msg.type)" />
            </el-icon>
          </div>
          <div class="notification-body">
            <div class="notification-header">
              <span class="notification-title">{{ msg.title }}</span>
              <span class="notification-time">{{ msg.createTime }}</span>
            </div>
            <p class="notification-content">{{ msg.content }}</p>
          </div>
          <div class="notification-actions">
            <el-button link type="primary" size="small" @click="handleView(msg)">查看详情</el-button>
            <el-button v-if="!msg.isRead" link type="info" size="small" @click="handleRead(msg)">标为已读</el-button>
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
import { ref, onMounted, computed } from 'vue'
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

const unreadCount = computed(() => {
  // 从总数据中估算，或使用后端返回的未读数
  return messages.value.filter(m => !m.isRead).length
})

function getBorderColor(type) {
  const map = { alarm: '#ff4d4f', order: '#1890FF', system: '#999' }
  return map[type] || '#1890FF'
}

function getBgColor(type) {
  const map = { alarm: 'rgba(255,77,79,0.1)', order: 'rgba(24,144,255,0.1)', system: 'rgba(153,153,153,0.1)' }
  return map[type] || 'rgba(24,144,255,0.1)'
}

function getIcon(type) {
  const map = { alarm: 'Bell', order: 'Document', system: 'Setting' }
  return map[type] || 'Bell'
}

function switchFilter(type) {
  filterType.value = type
  page.value = 1
  loadData()
}

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

function handleView(msg) {
  if (!msg.isRead) {
    handleRead(msg)
  }
  // 可根据消息类型跳转到对应详情页
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

<style scoped>
.notification-card {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px 20px;
  margin-bottom: 12px;
  background: #fff;
  border-radius: 8px;
  border-left: 4px solid #1890FF;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  transition: all 0.2s;
}
.notification-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.notification-card.is-read {
  opacity: 0.6;
}
.notification-icon {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.notification-body {
  flex: 1;
  min-width: 0;
}
.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}
.notification-title {
  font-weight: 600;
  font-size: 14px;
  color: #333;
}
.notification-time {
  font-size: 12px;
  color: #999;
  flex-shrink: 0;
  margin-left: 12px;
}
.notification-content {
  color: #666;
  font-size: 13px;
  margin: 0;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.notification-actions {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: flex-end;
}
</style>
