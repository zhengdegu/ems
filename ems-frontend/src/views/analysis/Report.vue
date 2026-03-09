<template>
  <div>
    <!-- 指标卡片 -->
    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :span="6" v-for="card in metrics" :key="card.label">
        <div class="stat-card" style="text-align:center">
          <div style="color:#999;font-size:13px;margin-bottom:8px">{{ card.label }}</div>
          <div style="font-size:28px;font-weight:700" :style="{ color: card.color }">{{ card.value }}</div>
          <div style="font-size:12px;color:#999;margin-top:4px">{{ card.desc }}</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center">
              <span style="font-weight:600">OEE趋势</span>
              <el-radio-group v-model="oeeRange" size="small">
                <el-radio-button label="week">周</el-radio-button>
                <el-radio-button label="month">月</el-radio-button>
                <el-radio-button label="year">年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="oeeChartRef" style="height:300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header><span style="font-weight:600">故障类型分布</span></template>
          <div ref="faultChartRef" style="height:300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header><span style="font-weight:600">MTBF / MTTR 对比</span></template>
          <div ref="mtChartRef" style="height:300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header><span style="font-weight:600">维修成本趋势</span></template>
          <div ref="costChartRef" style="height:300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'

const oeeChartRef = ref()
const faultChartRef = ref()
const mtChartRef = ref()
const costChartRef = ref()
const oeeRange = ref('month')
let charts = []

const metrics = [
  { label: 'OEE综合效率', value: '85.6%', color: '#1890FF', desc: '目标 ≥ 85%' },
  { label: 'MTBF 平均故障间隔', value: '720h', color: '#52c41a', desc: '较上月 +5.2%' },
  { label: 'MTTR 平均修复时间', value: '4.2h', color: '#faad14', desc: '较上月 -8.1%' },
  { label: '设备可用率', value: '96.8%', color: '#722ed1', desc: '目标 ≥ 95%' }
]

function initCharts() {
  const months = ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月']

  // OEE趋势
  const oeeChart = echarts.init(oeeChartRef.value)
  charts.push(oeeChart)
  oeeChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['可用率', '性能率', '良品率', 'OEE'], top: 0 },
    grid: { left: 40, right: 20, top: 40, bottom: 30 },
    xAxis: { type: 'category', data: months },
    yAxis: { type: 'value', max: 100, axisLabel: { formatter: '{value}%' } },
    series: [
      { name: '可用率', type: 'line', smooth: true, data: [95,96,94,97,96,95,97,96,95,96,97,97] },
      { name: '性能率', type: 'line', smooth: true, data: [90,91,89,92,91,90,92,91,90,91,92,91] },
      { name: '良品率', type: 'line', smooth: true, data: [97,98,96,98,97,98,98,97,98,97,98,98] },
      { name: 'OEE', type: 'line', smooth: true, data: [83,85,81,87,85,84,87,85,84,85,87,86], lineStyle: { width: 3 }, color: '#1890FF' }
    ]
  })

  // 故障类型饼图
  const faultChart = echarts.init(faultChartRef.value)
  charts.push(faultChart)
  faultChart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie', radius: ['40%', '65%'],
      label: { formatter: '{b}\n{d}%' },
      data: [
        { value: 35, name: '机械故障' },
        { value: 25, name: '电气故障' },
        { value: 18, name: '软件故障' },
        { value: 12, name: '液压故障' },
        { value: 10, name: '其他' }
      ]
    }]
  })

  // MTBF/MTTR
  const mtChart = echarts.init(mtChartRef.value)
  charts.push(mtChart)
  mtChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['MTBF(h)', 'MTTR(h)'], top: 0 },
    grid: { left: 50, right: 20, top: 40, bottom: 30 },
    xAxis: { type: 'category', data: months },
    yAxis: [
      { type: 'value', name: 'MTBF(h)' },
      { type: 'value', name: 'MTTR(h)', max: 10 }
    ],
    series: [
      { name: 'MTBF(h)', type: 'bar', data: [680,700,660,720,710,690,730,720,700,710,730,720], color: '#1890FF' },
      { name: 'MTTR(h)', type: 'line', yAxisIndex: 1, data: [5.2,4.8,5.5,4.3,4.5,4.8,4.1,4.2,4.6,4.4,4.0,4.2], color: '#ff4d4f' }
    ]
  })

  // 维修成本
  const costChart = echarts.init(costChartRef.value)
  charts.push(costChart)
  costChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['备件费用', '人工费用', '外包费用'], top: 0 },
    grid: { left: 60, right: 20, top: 40, bottom: 30 },
    xAxis: { type: 'category', data: months },
    yAxis: { type: 'value', axisLabel: { formatter: '¥{value}' } },
    series: [
      { name: '备件费用', type: 'bar', stack: 'cost', data: [12000,15000,11000,18000,14000,13000,16000,15000,12000,14000,17000,15000], color: '#1890FF' },
      { name: '人工费用', type: 'bar', stack: 'cost', data: [8000,9000,7500,10000,8500,8000,9500,9000,8000,8500,10000,9000], color: '#52c41a' },
      { name: '外包费用', type: 'bar', stack: 'cost', data: [3000,2000,4000,5000,2500,3000,4500,3000,3500,2000,5000,3500], color: '#faad14' }
    ]
  })
}

function handleResize() { charts.forEach(c => c.resize()) }

onMounted(() => { initCharts(); window.addEventListener('resize', handleResize) })
onUnmounted(() => { window.removeEventListener('resize', handleResize); charts.forEach(c => c.dispose()) })
</script>
