<template>
  <div>
    <el-card shadow="never">
      <div style="display:flex;justify-content:space-between;margin-bottom:16px">
        <el-radio-group v-model="filterType" size="small">
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button label="unread">未读</el-radio-button>
          <el-radio-button label="alarm">告警</el-radio-button>
          <el-radio-button label="order">工单</el-radio-button>
          <el-radio-button label="system">系统</el-radio-button>
        </el-radio-group>
        <el-button size="small" @click="markAllRead">全部已读</el-button>
      </div>

      <div v-for="(msg, i) in filteredMessages" :key="i"
        :style="{ background: msg.read ? '#fff' : '#f6ffed', padding: '16px', borderBottom: '1px solid #f0f0f0', cursor: 'pointer' }"
        @click="msg.read = true">
        <div style="display:flex;align-items:center;gap:10px">
          <el-badge :is-dot="!msg.read">
            <el-icon :size="20" :color="typeColor[msg.type]"><component :is="typeIcon[msg.type]" /></el-icon>
          </el-badge>
          <div style="flex:1">
            <div style="display:flex;justify-content:space-between">
              <span style="font-weight:600;font-size:14px;color:#333">{{ msg.title }}</span>
              <span style="font-size:12px;color:#999">{{ msg.time }}</span>
            </div>
            <p style="color:#666;font-size:13px;margin-top:6px">{{ msg.content }}</p>
          </div>
        </div>
      </div>

      <el-empty v-if="!filteredMessages.length" description="暂无消息" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

const filterType = ref('all')

const typeIcon = { alarm: 'Bell', order: 'Document', system: 'Setting' }
const typeColor = { alarm: '#ff4d4f', order: '#1890FF', system: '#722ed1' }

const messages = ref([
  { type: 'alarm', title: 'CNC-001 主轴温度异常', content: '设备CNC-001主轴温度达到92°C，已超过安全阈值85°C，请及时处理。', time: '10分钟前', read: false },
  { type: 'order', title: '新工单分配', content: '工单 WO-20240308-001 已分配给您，请及时处理。', time: '30分钟前', read: false },
  { type: 'alarm', title: 'ROBOT-005 急停触发', content: '搬运机器人ROBOT-005触发急停信号，设备已停机。', time: '2小时前', read: false },
  { type: 'system', title: '系统维护通知', content: '系统将于今晚22:00-23:00进行例行维护，届时可能短暂无法访问。', time: '3小时前', read: true },
  { type: 'order', title: '工单已完成', content: '工单 WO-20240305-001 PLC-003通信模块更换已完成，请确认验收。', time: '昨天', read: true },
  { type: 'alarm', title: 'CONV-008 皮带张力偏低', content: '皮带输送机CONV-008皮带张力低于正常范围，建议安排检查。', time: '昨天', read: true },
  { type: 'system', title: '密码即将过期', content: '您的密码将在7天后过期，请及时修改密码。', time: '3天前', read: true },
])

const filteredMessages = computed(() => {
  if (filterType.value === 'all') return messages.value
  if (filterType.value === 'unread') return messages.value.filter(m => !m.read)
  return messages.value.filter(m => m.type === filterType.value)
})

function markAllRead() {
  messages.value.forEach(m => m.read = true)
  ElMessage.success('已全部标记为已读')
}
</script>
