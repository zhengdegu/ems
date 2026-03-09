<template>
  <div>
    <!-- KPI 卡片 -->
    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :span="6" v-for="card in kpiCards" :key="card.i18nKey">
        <div class="stat-card">
          <div style="display:flex;justify-content:space-between;align-items:center">
            <div>
              <div style="color:#999;font-size:13px;margin-bottom:8px">{{ $t(`dashboard.${card.i18nKey}`) }}</div>
              <div style="font-size:28px;font-weight:700;color:#333">{{ card.value }}</div>
              <div style="margin-top:6px;font-size:12px">
                <span :style="{ color: card.trend > 0 ? '#52c41a' : '#ff4d4f' }">
                  {{ card.trend > 0 ? '↑' : '↓' }} {{ Math.abs(card.trend) }}%
                </span>
                <span style="color:#999;margin-left:4px">{{ $t('dashboard.lastMonth') }}</span>
              </div>
            </div>
            <el-icon :size="40" :color="card.color" style="opacity:0.2"><component :is="card.icon" /></el-icon>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :span="16">
        <el-card shadow="never">
          <template #header><span style="font-weight:600">{{ $t('dashboard.equipmentTrend') }}</span></template>
          <div ref="trendChartRef" style="height:320px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <template #header><span style="font-weight:600">{{ $t('dashboard.equipmentDistribution') }}</span></template>
          <div ref="pieChartRef" style="height:320px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="8">
        <el-card shadow="never">
          <template #header><span style="font-weight:600">{{ $t('dashboard.alarmInfo') }}</span></template>
          <div v-for="(alarm, i) in alarms" :key="i" style="display:flex;align-items:center;padding:10px 0;border-bottom:1px solid #f5f5f5">
            <el-tag :type="getAlarmType(alarm.level)" size="small" style="margin-right:10px">{{ $t(`alarm.${alarm.level}`) }}</el-tag>
            <div style="flex:1;font-size:13px;color:#333">{{ alarm.message }}</div>
            <span style="font-size:12px;color:#999">{{ alarm.time }}</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <template #header><span style="font-weight:600">{{ $t('dashboard.todoList') }}</span></template>
          <div v-for="(todo, i) in todos" :key="i" style="display:flex;align-items:center;padding:10px 0;border-bottom:1px solid #f5f5f5">
            <el-tag :type="getPriorityType(todo.priority)" size="small" effect="plain" style="margin-right:10px">{{ $t(`maintenance.${todo.priority}`) }}</el-tag>
            <div style="flex:1;font-size:13px;color:#333">{{ todo.title }}</div>
            <span style="font-size:12px;color:#999">{{ todo.deadline }}</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <template #header><span style="font-weight:600">{{ $t('dashboard.areaDistribution') }}</span></template>
          <div ref="barChartRef" style="height:280px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useI18n } from 'vue-i18n'
import * as echarts from 'echarts'

const { t } = useI18n()
const trendChartRef = ref()
const pieChartRef = ref()
const barChartRef = ref()
let charts = []

const kpiCards = [
  { i18nKey: 'totalEquipment', value: '1,286', trend: 3.2, color: '#1890FF', icon: 'Monitor' },
  { i18nKey: 'runningEquipment', value: '1,024', trend: 1.5, color: '#52c41a', icon: 'CircleCheck' },
  { i18nKey: 'maintenanceEquipment', value: '48', trend: -12, color: '#faad14', icon: 'WarnTriangleFilled' },
  { i18nKey: 'monthlyWorkOrder', value: '156', trend: 8.3, color: '#722ed1', icon: 'Document' }
]

const alarms = [
  { level: 'critical', message: 'CNC-001 主轴温度异常 (92°C)', time: '10分钟前' },
  { level: 'warning', message: 'PLC-003 通信中断', time: '25分钟前' },
  { level: 'info', message: 'PUMP-012 运行时长超过维护周期', time: '1小时前' },
  { level: 'critical', message: 'ROBOT-005 急停触发', time: '2小时前' },
  { level: 'info', message: 'CONV-008 皮带张力偏低', time: '3小时前' }
]

const todos = [
  { priority: 'high', title: 'CNC-001 紧急维修工单', deadline: '今天' },
  { priority: 'high', title: '月度设备巡检计划执行', deadline: '明天' },
  { priority: 'medium', title: 'ROBOT-005 故障分析报告', deadline: '03-10' },
  { priority: 'medium', title: '备件库存盘点', deadline: '03-12' },
  { priority: 'low', title: '设备台账信息更新', deadline: '03-15' }
]

function getAlarmType(level) {
  return level === 'critical' ? 'danger' : level === 'warning' ? 'warning' : 'info'
}

function getPriorityType(priority) {
  return priority === 'high' ? 'danger' : priority === 'medium' ? 'warning' : 'info'
}

function initCharts() {
  const trendChart = echarts.init(trendChartRef.value)
  charts.push(trendChart)
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: [t('dashboard.running'), t('dashboard.stopped'), t('dashboard.maintenance')], top: 0 },
    grid: { left: 40, right: 20, top: 40, bottom: 30 },
    xAxis: { type: 'category', data: t('dashboard.months') },
    yAxis: { type: 'value' },
    series: [
      { name: t('dashboard.running'), type: 'line', smooth: true, data: [980,990,1010,1000,1020,1015,1030,1024,1018,1025,1020,1024], areaStyle: { opacity: 0.1 }, color: '#1890FF' },
      { name: t('dashboard.stopped'), type: 'line', smooth: true, data: [50,45,40,55,38,42,35,48,52,40,45,48], areaStyle: { opacity: 0.1 }, color: '#faad14' },
      { name: t('dashboard.maintenance'), type: 'line', smooth: true, data: [30,35,28,32,25,30,22,28,35,25,30,28], areaStyle: { opacity: 0.1 }, color: '#ff4d4f' }
    ]
  })

  const pieChart = echarts.init(pieChartRef.value)
  charts.push(pieChart)
  pieChart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie', radius: ['45%', '70%'], center: ['50%', '50%'],
      label: { formatter: '{b}\n{d}%' },
      data: [
        { value: 420, name: t('equipment.cnc'), itemStyle: { color: '#1890FF' } },
        { value: 280, name: t('equipment.robot'), itemStyle: { color: '#52c41a' } },
        { value: 200, name: t('equipment.conveyor'), itemStyle: { color: '#faad14' } },
        { value: 180, name: t('equipment.plc'), itemStyle: { color: '#722ed1' } },
        { value: 206, name: t('equipment.other'), itemStyle: { color: '#13c2c2' } }
      ]
    }]
  })

  const barChart = echarts.init(barChartRef.value)
  charts.push(barChart)
  barChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 60, right: 20, top: 10, bottom: 30 },
    xAxis: { type: 'value' },
    yAxis: { type: 'category', data: ['A区-生产线', 'B区-装配线', 'C区-仓储', 'D区-检测', 'E区-办公'] },
    series: [{ type: 'bar', data: [420, 320, 240, 180, 126], barWidth: 20, itemStyle: { color: '#1890FF', borderRadius: [0, 4, 4, 0] } }]
  })
}

function handleResize() {
  charts.forEach(c => c.resize())
}

onMounted(() => {
  initCharts()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  charts.forEach(c => c.dispose())
})
</script>

<style scoped>
.stat-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}
</style>
