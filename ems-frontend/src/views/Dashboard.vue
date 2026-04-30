<template>
  <div class="dashboard-page">
    <!-- KPI 卡片 -->
    <el-row :gutter="20" class="kpi-row">
      <el-col :xs="12" :sm="12" :md="6" v-for="card in kpiCards" :key="card.label">
        <div class="kpi-card">
          <div class="kpi-header">
            <span class="kpi-label">{{ card.label }}</span>
            <div class="kpi-icon" :style="{ background: card.iconBg }">
              <el-icon :color="card.color"><component :is="card.icon" /></el-icon>
            </div>
          </div>
          <div class="kpi-value">{{ card.value }}<span v-if="card.unit" class="kpi-unit">{{ card.unit }}</span></div>
          <div class="kpi-trend">
            <span :class="card.trendUp ? 'trend-up' : 'trend-down'">
              <el-icon :size="12"><Top v-if="card.trendUp" /><Bottom v-else /></el-icon>
              {{ card.trend }}
            </span>
            <span class="trend-label">{{ card.trendLabel }}</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 第二行：趋势图 + 饼图 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :md="16">
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">设备运行趋势</h3>
            <div class="chart-tabs">
              <button
                v-for="tab in ['周', '月', '年']"
                :key="tab"
                :class="['chart-tab', { active: trendTab === tab }]"
                @click="trendTab = tab"
              >{{ tab }}</button>
            </div>
          </div>
          <div ref="trendChartRef" style="height: 280px"></div>
        </div>
      </el-col>
      <el-col :xs="24" :md="8">
        <div class="chart-card">
          <h3 class="chart-title">设备分类占比</h3>
          <div ref="pieChartRef" style="height: 280px"></div>
        </div>
      </el-col>
    </el-row>

    <!-- 第三行：预警 + 待办 + 快捷操作 -->
    <el-row :gutter="20" class="info-row">
      <!-- 实时预警 -->
      <el-col :xs="24" :md="8">
        <div class="info-card">
          <div class="info-header">
            <h3 class="chart-title">实时预警</h3>
            <el-link type="primary" :underline="false" @click="$router.push('/alarm')">查看全部 →</el-link>
          </div>
          <div class="alarm-list">
            <div
              v-for="(alarm, i) in alarmList"
              :key="i"
              class="alarm-item"
              :class="'alarm-' + alarm.type"
            >
              <div class="alarm-dot" :class="'dot-' + alarm.type"></div>
              <div class="alarm-content">
                <div class="alarm-title">{{ alarm.title }}</div>
                <div class="alarm-meta">{{ alarm.area }} · {{ alarm.time }}</div>
              </div>
              <span class="alarm-tag" :class="'tag-' + alarm.type">{{ alarm.levelText }}</span>
            </div>
          </div>
        </div>
      </el-col>

      <!-- 待办事项 -->
      <el-col :xs="24" :md="8">
        <div class="info-card">
          <div class="info-header">
            <h3 class="chart-title">待办事项</h3>
            <span class="todo-count">共 {{ todoList.length }} 项</span>
          </div>
          <div class="todo-list">
            <div v-for="(todo, i) in todoList" :key="i" class="todo-item">
              <div class="todo-icon" :style="{ background: todo.iconBg }">
                <el-icon :color="todo.iconColor" :size="12"><component :is="todo.icon" /></el-icon>
              </div>
              <div class="todo-content">
                <div class="todo-title">{{ todo.title }}</div>
                <div class="todo-meta">优先级：{{ todo.priority }} · 截止：{{ todo.deadline }}</div>
              </div>
            </div>
          </div>
        </div>
      </el-col>

      <!-- 设备区域分布 -->
      <el-col :xs="24" :md="8">
        <div class="info-card">
          <h3 class="chart-title" style="margin-bottom: 16px">设备区域分布</h3>
          <div class="area-list">
            <div v-for="(area, i) in areaList" :key="i" class="area-item">
              <div class="area-header">
                <span class="area-name">{{ area.name }}</span>
                <span class="area-count">{{ area.count }}台</span>
              </div>
              <div class="area-bar-bg">
                <div class="area-bar" :style="{ width: area.percent + '%', background: area.color }"></div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getDashboardOverview, getEquipmentDistribution } from '../api/dashboard'
import { getAlarmList } from '../api/alarm'
import { getWorkOrderList } from '../api/workOrder'
import wsClient from '../utils/websocket'

const trendChartRef = ref()
const pieChartRef = ref()
const trendTab = ref('周')
let charts = []

// KPI 卡片数据
const kpiCards = ref([
  { label: '设备总数', value: '0', unit: '', trend: '-', trendUp: true, trendLabel: '', color: '#1890FF', iconBg: '#e6f4ff', icon: 'Monitor' },
  { label: '在线率', value: '0', unit: '%', trend: '-', trendUp: true, trendLabel: '', color: '#52c41a', iconBg: '#f6ffed', icon: 'Connection' },
  { label: '故障率', value: '0', unit: '%', trend: '-', trendUp: false, trendLabel: '', color: '#ff4d4f', iconBg: '#fff2f0', icon: 'WarnTriangleFilled' },
  { label: '待维护', value: '0', unit: '', trend: '-', trendUp: true, trendLabel: '', color: '#faad14', iconBg: '#fffbe6', icon: 'SetUp' }
])

// 预警列表
const alarmList = ref([])

// 待办事项
const todoList = ref([])

// 区域分布
const areaList = ref([])

// 饼图数据（从 API 获取后更新）
const pieData = ref([])

// 加载 API 数据
async function loadDashboardData() {
  try {
    const res = await getDashboardOverview()
    if (res.code === 200 && res.data) {
      const d = res.data
      const total = d.totalEquipment || 0
      const running = d.runningCount || 0
      const fault = d.faultCount || 0
      const maintenance = d.maintenanceCount || 0
      const onlineRate = d.onlineRate !== undefined ? d.onlineRate : (total > 0 ? (running / total * 100).toFixed(1) : 0)
      const faultRate = d.faultRate !== undefined ? d.faultRate : (total > 0 ? (fault / total * 100).toFixed(1) : 0)

      kpiCards.value[0].value = total.toLocaleString()
      kpiCards.value[0].trend = `运行 ${running}`
      kpiCards.value[0].trendLabel = `故障 ${fault}`

      kpiCards.value[1].value = String(onlineRate)
      kpiCards.value[1].trend = `${running}/${total}`
      kpiCards.value[1].trendLabel = '运行/总数'

      kpiCards.value[2].value = String(faultRate)
      kpiCards.value[2].trend = `${fault} 台`
      kpiCards.value[2].trendLabel = '故障设备'

      kpiCards.value[3].value = String(d.pendingMaintenance || maintenance)
      kpiCards.value[3].trend = `${d.pendingWorkOrders || 0} 单`
      kpiCards.value[3].trendLabel = '待处理工单'

      // 设备分类统计 - 更新饼图
      if (d.equipmentByType) {
        const colors = ['#1890FF', '#36cfc9', '#faad14', '#722ed1', '#13c2c2', '#9ca3af', '#ff4d4f', '#52c41a']
        pieData.value = Object.entries(d.equipmentByType).map(([name, value], i) => ({
          value,
          name,
          itemStyle: { color: colors[i % colors.length] }
        }))
        updatePieChart()
      }
    }
  } catch {
    // 使用默认数据
  }

  // 加载区域分布
  try {
    const distRes = await getEquipmentDistribution()
    if (distRes.code === 200 && distRes.data?.byLocation) {
      const locations = distRes.data.byLocation
      const maxCount = Math.max(...Object.values(locations), 1)
      const colors = ['#1890FF', '#52c41a', '#faad14', '#722ed1', '#13c2c2', '#ff4d4f']
      areaList.value = Object.entries(locations).map(([name, count], i) => ({
        name,
        count,
        percent: Math.round(count / maxCount * 100),
        color: colors[i % colors.length]
      }))
    }
  } catch {
    // 使用默认数据
  }

  try {
    const alarmRes = await getAlarmList({ page: 1, pageSize: 4 })
    if (alarmRes.code === 200 && alarmRes.data?.records?.length) {
      alarmList.value = alarmRes.data.records.map(a => ({
        title: `${a.equipmentName || ''} ${a.message || ''}`.trim(),
        area: a.area || '',
        time: a.createTime || '',
        type: a.level === '紧急' ? 'danger' : a.level === '重要' ? 'warning' : 'info',
        levelText: a.level === '重要' ? '警告' : a.level || '提示'
      }))
    }
  } catch {
    // 使用默认数据
  }

  try {
    const todoRes = await getWorkOrderList({ page: 1, pageSize: 4, status: '待接单' })
    if (todoRes.code === 200 && todoRes.data?.records?.length) {
      todoList.value = todoRes.data.records.map(t => ({
        title: t.title,
        priority: t.priority || '中',
        deadline: t.deadline || t.createTime || '',
        icon: t.priority === '紧急' ? 'SetUp' : 'Document',
        iconColor: t.priority === '紧急' ? '#ff4d4f' : t.priority === '高' ? '#faad14' : '#1890FF',
        iconBg: t.priority === '紧急' ? '#fff2f0' : t.priority === '高' ? '#fffbe6' : '#e6f4ff'
      }))
    }
  } catch {
    // 使用默认数据
  }
}

let pieChart = null

function updatePieChart() {
  if (pieChart && pieData.value.length > 0) {
    pieChart.setOption({
      series: [{
        data: pieData.value
      }]
    })
  }
}

function initCharts() {
  // 趋势图
  const trendChart = echarts.init(trendChartRef.value)
  charts.push(trendChart)
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['运行设备', '故障设备', '维护设备'], top: 0, textStyle: { fontSize: 11 } },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '40px', containLabel: true },
    xAxis: {
      type: 'category',
      data: ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月'],
      axisLine: { lineStyle: { color: '#e5e7eb' } },
      axisLabel: { color: '#9ca3af', fontSize: 11 }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#f3f4f6' } },
      axisLabel: { color: '#9ca3af', fontSize: 11 }
    },
    series: [
      {
        name: '运行设备', type: 'line', smooth: true,
        data: [2680,2700,2720,2750,2780,2790,2800,2810,2820,2830,2840,2847],
        itemStyle: { color: '#1890FF' },
        areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(24,144,255,0.15)' }, { offset: 1, color: 'rgba(24,144,255,0)' }] } }
      },
      {
        name: '故障设备', type: 'line', smooth: true,
        data: [35,28,42,30,25,38,22,18,28,20,15,34],
        itemStyle: { color: '#ff4d4f' }
      },
      {
        name: '维护设备', type: 'line', smooth: true,
        data: [120,135,110,145,130,125,140,155,148,160,150,156],
        itemStyle: { color: '#faad14' }
      }
    ]
  })

  // 饼图
  pieChart = echarts.init(pieChartRef.value)
  charts.push(pieChart)
  const defaultPieData = pieData.value.length > 0 ? pieData.value : [
    { value: 0, name: '暂无数据', itemStyle: { color: '#e5e7eb' } }
  ]
  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', right: '5%', top: 'center', textStyle: { fontSize: 11 } },
    series: [{
      type: 'pie', radius: ['40%', '70%'], center: ['35%', '50%'],
      avoidLabelOverlap: false,
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
      data: defaultPieData
    }]
  })
}

function handleResize() {
  charts.forEach(c => c.resize())
}

onMounted(() => {
  loadDashboardData()
  initCharts()
  window.addEventListener('resize', handleResize)

  // WebSocket 订阅 Dashboard 数据更新
  wsClient.subscribe('/topic/dashboard', () => {
    // 收到实时数据更新时刷新 KPI
    loadDashboardData()
  })

  // WebSocket 订阅告警更新
  wsClient.subscribe('/topic/alarm', (alarm) => {
    if (alarm && alarm.status === '未处理') {
      // 实时更新告警列表（插入到最前面）
      const newAlarm = {
        title: `${alarm.equipmentName || ''} ${alarm.message || ''}`.trim(),
        area: alarm.area || '',
        time: alarm.createTime || '刚刚',
        type: alarm.level === '紧急' ? 'danger' : alarm.level === '重要' ? 'warning' : 'info',
        levelText: alarm.level === '重要' ? '警告' : alarm.level || '提示'
      }
      alarmList.value.unshift(newAlarm)
      if (alarmList.value.length > 4) alarmList.value.pop()
    }
  })
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  wsClient.unsubscribe('/topic/dashboard')
  wsClient.unsubscribe('/topic/alarm')
  charts.forEach(c => c.dispose())
})
</script>

<style scoped>
.dashboard-page { }

/* KPI 卡片 */
.kpi-row { margin-bottom: 20px; }

.kpi-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #f0f0f0;
  transition: transform 0.2s, box-shadow 0.2s;
  margin-bottom: 16px;
}

.kpi-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.kpi-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.kpi-label { font-size: 13px; color: #999; }

.kpi-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.kpi-value {
  font-size: 30px;
  font-weight: 700;
  color: #333;
  line-height: 1;
}

.kpi-unit { font-size: 18px; }

.kpi-trend {
  display: flex;
  align-items: center;
  margin-top: 8px;
  font-size: 12px;
}

.trend-up { color: #52c41a; display: flex; align-items: center; gap: 2px; }
.trend-down { color: #ff4d4f; display: flex; align-items: center; gap: 2px; }
.trend-label { color: #999; margin-left: 8px; }

/* 图表卡片 */
.chart-row { margin-bottom: 20px; }

.chart-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #f0f0f0;
  margin-bottom: 16px;
}

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.chart-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.chart-tabs { display: flex; gap: 8px; }

.chart-tab {
  padding: 4px 12px;
  font-size: 12px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  background: transparent;
  color: #999;
  transition: all 0.2s;
}

.chart-tab.active {
  background: #e6f4ff;
  color: #1890FF;
}

.chart-tab:hover:not(.active) { background: #f5f5f5; }

/* 信息卡片 */
.info-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #f0f0f0;
  margin-bottom: 16px;
}

.info-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

/* 预警列表 */
.alarm-list { display: flex; flex-direction: column; gap: 12px; }

.alarm-item {
  display: flex;
  align-items: flex-start;
  padding: 12px;
  border-radius: 8px;
}

.alarm-danger { background: #fff2f0; }
.alarm-warning { background: #fffbe6; }
.alarm-info { background: #e6f4ff; }

.alarm-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-top: 6px;
  margin-right: 12px;
  flex-shrink: 0;
  animation: pulse 2s infinite;
}

.dot-danger { background: #ff4d4f; }
.dot-warning { background: #faad14; }
.dot-info { background: #1890FF; }

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.alarm-content { flex: 1; min-width: 0; }
.alarm-title { font-size: 13px; font-weight: 500; color: #333; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.alarm-meta { font-size: 12px; color: #999; margin-top: 4px; }

.alarm-tag {
  padding: 1px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: #fff;
  flex-shrink: 0;
  margin-left: 8px;
}

.tag-danger { background: #ff4d4f; }
.tag-warning { background: #faad14; }
.tag-info { background: #1890FF; }

/* 待办列表 */
.todo-list { display: flex; flex-direction: column; gap: 12px; }

.todo-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  cursor: pointer;
  transition: border-color 0.2s;
}

.todo-item:hover { border-color: #bae0ff; }

.todo-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  flex-shrink: 0;
}

.todo-content { flex: 1; min-width: 0; }
.todo-title { font-size: 13px; font-weight: 500; color: #333; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.todo-meta { font-size: 12px; color: #999; margin-top: 2px; }
.todo-count { font-size: 12px; color: #999; }

/* 区域分布 */
.area-list { display: flex; flex-direction: column; gap: 16px; }

.area-header {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  margin-bottom: 4px;
}

.area-name { color: #555; }
.area-count { color: #999; }

.area-bar-bg {
  width: 100%;
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
}

.area-bar {
  height: 8px;
  border-radius: 4px;
  transition: width 0.6s ease;
}
</style>
