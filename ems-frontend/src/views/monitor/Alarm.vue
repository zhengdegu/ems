<template>
  <div class="alarm-page">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-card-inner">
            <div>
              <div class="stat-value" style="color: #ff4d4f">{{ alarmStats.critical }}</div>
              <div class="stat-label">紧急告警</div>
            </div>
            <div class="stat-icon" style="background: #fff2f0">
              <el-icon color="#ff4d4f"><WarningFilled /></el-icon>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-card-inner">
            <div>
              <div class="stat-value" style="color: #faad14">{{ alarmStats.warning }}</div>
              <div class="stat-label">警告</div>
            </div>
            <div class="stat-icon" style="background: #fffbe6">
              <el-icon color="#faad14"><WarnTriangleFilled /></el-icon>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-card-inner">
            <div>
              <div class="stat-value" style="color: #1890FF">{{ alarmStats.info }}</div>
              <div class="stat-label">提示</div>
            </div>
            <div class="stat-icon" style="background: #e6f4ff">
              <el-icon color="#1890FF"><InfoFilled /></el-icon>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-card-inner">
            <div>
              <div class="stat-value" style="color: #52c41a">{{ alarmStats.handled }}</div>
              <div class="stat-label">今日已处理</div>
            </div>
            <div class="stat-icon" style="background: #f6ffed">
              <el-icon color="#52c41a"><CircleCheckFilled /></el-icon>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 告警列表 -->
    <div class="alarm-list-card">
      <!-- 顶部筛选 -->
      <div class="list-header">
        <div class="tab-group">
          <button
            v-for="tab in statusTabs"
            :key="tab.value"
            :class="['tab-btn', { active: activeTab === tab.value }]"
            @click="activeTab = tab.value; handleSearch()"
          >{{ tab.label }}</button>
        </div>
        <div class="header-actions">
          <el-button size="small"><el-icon><Setting /></el-icon> 告警规则</el-button>
          <el-button size="small"><el-icon><Bell /></el-icon> 通知设置</el-button>
        </div>
      </div>

      <!-- 告警条目 -->
      <div class="alarm-entries" v-loading="loading">
        <div
          v-for="(alarm, i) in alarms"
          :key="i"
          class="alarm-entry"
          :class="{ 'alarm-handled': alarm.status === '已处理' }"
        >
          <div
            class="alarm-dot"
            :class="getDotClass(alarm)"
          ></div>
          <div class="alarm-content">
            <div class="alarm-title-row">
              <span class="alarm-title">{{ alarm.equipmentName ? alarm.equipmentName + ' ' : '' }}{{ alarm.message }}</span>
              <span class="alarm-level-tag" :class="getLevelTagClass(alarm.level)">
                {{ getLevelText(alarm.level) }}
              </span>
            </div>
            <div class="alarm-meta">
              {{ alarm.area || '' }}{{ alarm.area ? ' · ' : '' }}{{ alarm.rule || '' }}{{ alarm.duration ? ' · 已持续 ' + alarm.duration : '' }}
            </div>
          </div>
          <div class="alarm-actions">
            <div class="alarm-time">{{ alarm.createTime || alarm.time || '' }}</div>
            <el-link
              v-if="alarm.status !== '已处理'"
          type="primary"
              :underline="false"
              class="alarm-handle-btn"
              @click="handleAlarmAction(alarm)"
            >处理</el-link>
          </div>
        </div>

        <el-empty v-if="!loading && alarms.length === 0" description="暂无告警" />
      </div>

      <!-- 分页 -->
      <div class="list-footer">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @change="loadData"
        /> </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAlarmList, handleAlarm } from '../../api/alarm'

const activeTab = ref('all')
const statusTabs = [
  { label: '全部', value: 'all' },
  { label: '未处理', value: '未处理' },
  { label: '已处理', value: '已处理' },
  { label: '已忽略', value: '已忽略' }
]

const query = reactive({ level: '', status: '', page: 1, pageSize: 10 })
const alarms = ref([])
const total = ref(0)
const loading = ref(false)

// 演示数据
const demoAlarms = [
  { equipmentName: 'CNC-A003', message: '主轴温度超过阈值（78°C > 55°C）', level: '紧急', status: '未处理', area: 'A区-精密加工车间', rule: '触发规则：主轴温度 > 55°C', duration: '45 分钟', time: '3 分钟前' },
  { equipmentName: 'ROBOT-D003', message: '关节扭矩异常波动', level: '紧急', status: '未处理', area: 'D区-焊接车间', rule: '触发规则：扭矩波动 > ±15%', duration: '12 分钟', time: '12 分钟前' },
  { equipmentName: 'PLC-B012', message: '通信延迟超过阈值（250ms > 100ms）', level: '重要', status: '未处理', area: 'B区-装配车间', rule: '触发规则：通信延迟 > 100ms', time: '15 分钟前' },
  { equipmentName: 'AGV-C005', message: '电池电量低于20%', level: '重要', status: '未处理', area: 'C区-仓储物流', rule: '触发规则：电池电量 < 20%', time: '28 分钟前' },
  { equipmentName: 'ROBOT-D008', message: '距下次保养周期还有 3 天', level: '一般', status: '未处理', area: 'D区-焊接车间', rule: '触发规则：保养周期提前提醒', time: '1 小时前' },
  { equipmentName: 'CNC-A002', message: '振动值轻微偏高', level: '一般', status: '已处理', area: 'A区-精密加工车间', rule: '处理人：王建华 · 已调整参数恢复正常', time: '2 小时前' }
]

const alarmStats = computed(() => {
  if (alarms.value.length > 0) {
    const all = alarms.value
    return {
      critical: all.filter(a => a.level === '紧急').length,
      warning: all.filter(a => a.level === '重要').length,
      info: all.filter(a => a.level === '一般').length,
      handled: all.filter(a => a.status === '已处理').length
    }
  }
  return { critical: 8, warning: 23, info: 45, handled: 156 }
})

function getDotClass(alarm) {
  if (alarm.status === '已处理') return 'dot-handled'
  if (alarm.level === '紧急') return 'dot-danger pulse-dot'
  if (alarm.level === '重要') return 'dot-warning'
  return 'dot-info'
}

function getLevelTagClass(level) {
  if (level === '紧急') return 'tag-danger'
  if (level === '重要') return 'tag-warning'
  if (level === '一般') return 'tag-info'
  return 'tag-handled'
}

function getLevelText(level) {
  if (level === '重要') return '警告'
  return level
}

async function loadData() {
  loading.value = true
  try {
    const params = {
      page: query.page,
      pageSize: query.pageSize,
      level: query.level || undefined,
      status: activeTab.value !== 'all' ? activeTab.value : undefined
    }
    const res = await getAlarmList(params)
    if (res.code === 200 && res.data) {
      const records = res.data.records || []
      if (records.length > 0) {
        alarms.value = records
      } else {
        alarms.value = demoAlarms
      }
      total.value = res.data.total || demoAlarms.length
    } else {
      alarms.value = demoAlarms
      total.value = demoAlarms.length
    }
  } catch {
    alarms.value = demoAlarms
    total.value = demoAlarms.length
  } finally {
    loading.value = false
  }
}

function handleSearch() { query.page = 1; loadData() }

async function handleAlarmAction(row) {
  try {
    const res = await handleAlarm(row.id, { handleNote: '已处理' })
    if (res.code === 200) {
      ElMessage.success('处理成功')
      loadData()
    }
  } catch {
    ElMessage.error('处理失败')
  }
}

onMounted(loadData)
</script>

<style scoped>
/* 统计卡片 */
.stats-row { margin-bottom: 20px; }

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #f0f0f0;
  margin-bottom: 16px;
}

.stat-card-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stat-value { font-size: 24px; font-weight: 700; }
.stat-label { font-size: 12px; color: #999; margin-top: 4px; }

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 告警列表卡片 */
.alarm-list-card {
  background: #fff;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
  overflow: hidden;
}

.list-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  flex-wrap: wrap;
  gap: 12px;
}

.tab-group { display: flex; gap: 8px; }

.tab-btn {
  padding: 6px 12px;
  font-size: 13px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  background: transparent;
  color: #999;
  transition: all 0.2s;
}

.tab-btn.active { background: #e6f4ff; color: #1890FF; }
.tab-btn:hover:not(.active) { background: #f5f5f5; }

.header-actions { display: flex; gap: 8px; }

/* 告警条目 */
.alarm-entries { }

.alarm-entry {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #fafafa;
  cursor: pointer;
  transition: background 0.2s;
}

.alarm-entry:hover { background: #fafafa; }
.alarm-entry.alarm-handled { opacity: 0.6; }

.alarm-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 16px;
  flex-shrink: 0;
}

.dot-danger { background: #ff4d4f; }
.dot-warning { background: #faad14; }
.dot-info { background: #1890FF; }
.dot-handled { background: #d9d9d9; }

.pulse-dot { animation: pulse 2s infinite; }

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.alarm-content { flex: 1; min-width: 0; }

.alarm-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.alarm-title {
  font-size: 13px;
  font-weight: 500;
  color: #333;
}

.alarm-handled .alarm-title { color: #999; }

.alarm-level-tag {
  padding: 1px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: #fff;
  flex-shrink: 0;
}

.tag-danger { background: #ff4d4f; }
.tag-warning { background: #faad14; }
.tag-info { background: #1890FF; }
.tag-handled { background: #d9d9d9; color: #666; }

.alarm-meta {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.alarm-actions {
  text-align: right;
  flex-shrink: 0;
  margin-left: 16px;
}

.alarm-time { font-size: 12px; color: #999; }
.alarm-handle-btn { font-size: 12px; margin-top: 4px; }

/* 分页 */
.list-footer {
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
}
</style>
