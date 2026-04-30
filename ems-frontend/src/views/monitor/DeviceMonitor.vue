<template>
  <div class="device-monitor-page">
    <!-- 顶部：设备选择 -->
    <div class="monitor-header">
      <div class="header-left">
        <h3 class="page-title">设备实时监控</h3>
        <el-select
          v-model="selectedDevice"
          placeholder="选择设备"
          filterable
          style="width: 280px"
          @change="handleDeviceChange"
        >
          <el-option
            v-for="device in deviceList"
            :key="device.code"
            :label="device.code + ' ' + device.name"
            :value="device.code"
          />
        </el-select>
        <el-tag :type="wsConnected ? 'success' : 'danger'" size="small">
          {{ wsConnected ? '已连接' : '未连接' }}
        </el-tag>
      </div>
      <div class="header-right">
        <el-tag type="info" size="small">数据刷新间隔: 30s</el-tag>
      </div>
    </div>

    <!-- 中间：4个实时参数卡片 -->
    <el-row :gutter="20" class="param-row">
      <el-col :xs="12" :sm="6" v-for="param in paramCards" :key="param.key">
        <div class="param-card" :style="{ borderTop: '3px solid ' + param.color }">
          <div class="param-header">
            <el-icon :size="20" :color="param.color"><component :is="param.icon" /></el-icon>
            <span class="param-label">{{ param.label }}</span>
          </div>
          <div class="param-value" :style="{ color: param.color }">
            {{ param.value }}
            <span class="param-unit">{{ param.unit }}</span>
          </div>
          <div class="param-range">正常范围: {{ param.range }}</div>
          <div class="param-status">
            <el-tag :type="param.statusType" size="small">{{ param.statusText }}</el-tag>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 下方：ECharts 实时折线图 -->
    <div class="chart-card">
      <div class="chart-header">
        <h3 class="chart-title">实时数据趋势</h3>
        <div class="chart-legend">
          <span v-for="param in paramCards" :key="param.key" class="legend-item">
            <span class="legend-dot" :style="{ background: param.color }"></span>
            {{ param.label }}
          </span>
        </div>
      </div>
      <div ref="trendChartRef" style="height: 360px"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'
import { getEquipmentList } from '../../api/equipment'
import wsClient from '../../utils/websocket'

const selectedDevice = ref('')
const deviceList = ref([])
const wsConnected = ref(false)
const trendChartRef = ref()
let chart = null

// 最近20个数据点
const MAX_POINTS = 20
const dataHistory = reactive({
  timestamps: [],
  temperature: [],
  speed: [],
  vibration: [],
  power: []
})

// 当前实时值
const currentValues = reactive({
  temperature: '--',
  speed: '--',
  vibration: '--',
  power: '--'
})

const paramCards = computed(() => [
  {
    key: 'temperature',
    label: '温度',
    value: currentValues.temperature,
    unit: '°C',
    range: '40-70°C',
    color: '#ff4d4f',
    icon: 'Sunny',
    statusType: getStatus('temperature'),
    statusText: getStatusText('temperature')
  },
  {
    key: 'speed',
    label: '转速',
    value: currentValues.speed,
    unit: 'RPM',
    range: '5000-8000',
    color: '#1890FF',
    icon: 'Odometer',
    statusType: getStatus('speed'),
    statusText: getStatusText('speed')
  },
  {
    key: 'vibration',
    label: '振动',
    value: currentValues.vibration,
    unit: 'mm/s',
    range: '0-3.0',
    color: '#faad14',
    icon: 'DataLine',
    statusType: getStatus('vibration'),
    statusText: getStatusText('vibration')
  },
  {
    key: 'power',
    label: '功率',
    value: currentValues.power,
    unit: 'kW',
    range: '30-80',
    color: '#52c41a',
    icon: 'Lightning',
    statusType: getStatus('power'),
    statusText: getStatusText('power')
  }
])

function getStatus(key) {
  const v = parseFloat(currentValues[key])
  if (isNaN(v)) return 'info'
  const limits = {
    temperature: [40, 70],
    speed: [5000, 8000],
    vibration: [0, 3],
    power: [30, 80]
  }
  const [min, max] = limits[key]
  if (v < min || v > max) return 'danger'
  if (v > max * 0.9 || v < min * 1.1) return 'warning'
  return 'success'
}

function getStatusText(key) {
  const type = getStatus(key)
  if (type === 'danger') return '异常'
  if (type === 'warning') return '偏高'
  if (type === 'success') return '正常'
  return '等待数据'
}

function handleDeviceChange() {
  // 切换设备时清空历史数据
  dataHistory.timestamps = []
  dataHistory.temperature = []
  dataHistory.speed = []
  dataHistory.vibration = []
  dataHistory.power = []
  currentValues.temperature = '--'
  currentValues.speed = '--'
  currentValues.vibration = '--'
  currentValues.power = '--'
  updateChart()
}

function processDeviceData(data) {
  // 只处理当前选中设备的数据，或者未选择设备时处理所有数据
  if (selectedDevice.value && data.deviceCode !== selectedDevice.value) return

  const params = data.params || {}
  const timestamp = data.timestamp
    ? new Date(data.timestamp).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
    : new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })

  // 更新当前值
  if (params.temperature !== undefined) currentValues.temperature = Number(params.temperature).toFixed(1)
  if (params.speed !== undefined) currentValues.speed = Math.round(params.speed)
  if (params.vibration !== undefined) currentValues.vibration = Number(params.vibration).toFixed(1)
  if (params.power !== undefined) currentValues.power = Number(params.power).toFixed(1)

  // 追加历史数据
  dataHistory.timestamps.push(timestamp)
  dataHistory.temperature.push(params.temperature !== undefined ? Number(params.temperature) : null)
  dataHistory.speed.push(params.speed !== undefined ? Number(params.speed) : null)
  dataHistory.vibration.push(params.vibration !== undefined ? Number(params.vibration) : null)
  dataHistory.power.push(params.power !== undefined ? Number(params.power) : null)

  // 保持最近 MAX_POINTS 个数据点
  if (dataHistory.timestamps.length > MAX_POINTS) {
    dataHistory.timestamps.shift()
    dataHistory.temperature.shift()
    dataHistory.speed.shift()
    dataHistory.vibration.shift()
    dataHistory.power.shift()
  }

  // 如果未选择设备，自动选中
  if (!selectedDevice.value && data.deviceCode) {
    selectedDevice.value = data.deviceCode
  }

  updateChart()
}

function updateChart() {
  if (!chart) return
  chart.setOption({
    xAxis: { data: [...dataHistory.timestamps] },
    series: [
      { data: [...dataHistory.temperature] },
      { data: [...dataHistory.speed] },
      { data: [...dataHistory.vibration] },
      { data: [...dataHistory.power] }
    ]
  })
}

function initChart() {
  chart = echarts.init(trendChartRef.value)
  chart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    legend: {
      data: ['温度(°C)', '转速(RPM)', '振动(mm/s)', '功率(kW)'],
      top: 0,
      textStyle: { fontSize: 11 }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '40px',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: [],
      axisLine: { lineStyle: { color: '#e5e7eb' } },
      axisLabel: { color: '#9ca3af', fontSize: 11 }
    },
    yAxis: [
      {
        type: 'value',
        name: '温度/振动/功率',
        axisLine: { show: false },
        splitLine: { lineStyle: { color: '#f3f4f6' } },
        axisLabel: { color: '#9ca3af', fontSize: 11 }
      },
      {
        type: 'value',
        name: '转速(RPM)',
        axisLine: { show: false },
        splitLine: { show: false },
        axisLabel: { color: '#9ca3af', fontSize: 11 }
      }
    ],
    series: [
      {
        name: '温度(°C)',
        type: 'line',
        smooth: true,
        data: [],
        itemStyle: { color: '#ff4d4f' },
        areaStyle: {
          color: {
            type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(255,77,79,0.15)' },
              { offset: 1, color: 'rgba(255,77,79,0)' }
            ]
          }
        }
      },
      {
        name: '转速(RPM)',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        data: [],
        itemStyle: { color: '#1890FF' }
      },
      {
        name: '振动(mm/s)',
        type: 'line',
        smooth: true,
        data: [],
        itemStyle: { color: '#faad14' }
      },
      {
        name: '功率(kW)',
        type: 'line',
        smooth: true,
        data: [],
        itemStyle: { color: '#52c41a' },
        areaStyle: {
          color: {
            type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(82,196,26,0.1)' },
              { offset: 1, color: 'rgba(82,196,26,0)' }
            ]
          }
        }
      }
    ]
  })
}

async function loadDevices() {
  try {
    const res = await getEquipmentList({ page: 1, pageSize: 200 })
    if (res.code === 200 && res.data?.records) {
      deviceList.value = res.data.records
    }
  } catch {
    // 使用空列表
  }
}

function handleResize() {
  if (chart) chart.resize()
}

onMounted(async () => {
  await loadDevices()
  initChart()
  window.addEventListener('resize', handleResize)

  // 订阅 WebSocket 实时数据
  wsClient.subscribe('/topic/dashboard', (data) => {
    wsConnected.value = true
    processDeviceData(data)
  })
  wsConnected.value = wsClient.connected
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  wsClient.unsubscribe('/topic/dashboard')
  if (chart) {
    chart.dispose()
    chart = null
  }
})
</script>

<style scoped>
.device-monitor-page { }

.monitor-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 12px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

/* 参数卡片 */
.param-row { margin-bottom: 20px; }

.param-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #f0f0f0;
  margin-bottom: 16px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.param-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.param-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.param-label {
  font-size: 13px;
  color: #999;
}

.param-value {
  font-size: 32px;
  font-weight: 700;
  line-height: 1;
  margin-bottom: 8px;
}

.param-unit {
  font-size: 16px;
  font-weight: 400;
}

.param-range {
  font-size: 12px;
  color: #bbb;
  margin-bottom: 8px;
}

.param-status {
  display: flex;
  align-items: center;
}

/* 图表卡片 */
.chart-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #f0f0f0;
}

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

.chart-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.chart-legend {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #666;
}

.legend-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}
</style>
