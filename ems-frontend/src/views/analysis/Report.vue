<template>
  <div class="report-page">
    <!-- 顶部筛选栏 -->
    <div class="filter-bar">
      <div class="filter-left">
        <span class="filter-label">统计周期：</span>
        <button
          v-for="tab in periodTabs"
          :key="tab.value"
          :class="['period-tab', { active: activePeriod === tab.value }]"
          @click="activePeriod = tab.value"
        >
          <el-icon v-if="tab.icon"><Calendar /></el-icon>
          {{ tab.label }}
        </button>
      </div>
      <div class="filter-right">
        <el-button @click="handleExport('excel')">
          <el-icon color="#52c41a"><Document /></el-icon> 导出Excel
        </el-button>
        <el-button @click="handleExport('pdf')">
          <el-icon color="#ff4d4f"><Document /></el-icon> 导出PDF
        </el-button>
      </div>
    </div>

    <!-- 核心指标卡片 -->
    <el-row :gutter="16" class="metrics-row">
      <el-col :xs="12" :sm="12" :md="6" v-for="metric in metrics" :key="metric.label">
        <div class="metric-card">
          <div class="metric-label">{{ metric.label }}</div>
          <div class="metric-value" :style="{ color: metric.color }">
            {{ metric.value }}<span v-if="metric.unit" class="metric-unit">{{ metric.unit }}</span>
          </div>
          <div class="metric-trend" :class="metric.trendUp ? 'trend-up' : (metric.trendDown ? 'trend-down-good' : 'trend-up-bad')">
            <el-icon :size="12"><Top v-if="metric.trendUp || metric.trendUpBad" /><Bottom v-else /></el-icon>
            {{ metric.trend }}
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 第二行：柱状图 + 堆叠柱状图 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :md="12">
        <div class="chart-card">
          <h3 class="chart-title">设备运行时长统计（Top 10）</h3>
          <div ref="barChartRef" style="height: 300px"></div>
        </div>
      </el-col>
      <el-col :xs="24" :md="12">
        <div class="chart-card">
          <h3 class="chart-title">维护成本月度趋势</h3>
          <div ref="costChartRef" style="height: 300px"></div>
        </div>
      </el-col>
    </el-row>

    <!-- 第三行：饼图 + 雷达图 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :md="8">
        <div class="chart-card">
          <h3 class="chart-title">故障类型分布</h3>
          <div ref="faultChartRef" style="height: 250px"></div>
        </div>
      </el-col>
      <el-col :xs="24" :md="16">
        <div class="chart-card">
          <h3 class="chart-title">各区域设备效率对比</h3>
          <div ref="radarChartRef" style="height: 250px"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

const barChartRef = ref()
const costChartRef = ref()
const faultChartRef = ref()
const radarChartRef = ref()
const activePeriod = ref('month')
let charts = []

const periodTabs = [
  { label: '本月', value: 'month' },
  { label: '本季度', value: 'quarter' },
  { label: '本年度', value: 'year' },
  { label: '自定义', value: 'custom', icon: true }
]

const metrics = [
  { label: '设备综合效率(OEE)', value: '87.6', unit: '%', color: '#1890FF', trend: '+2.3% 环比', trendUp: true },
  { label: '平均故障间隔(MTBF)', value: '720', unit: 'h', color: '#333', trend: '+48h 环比', trendUp: true },
  { label: '平均修复时间(MTTR)', value: '4.2', unit: 'h', color: '#333', trend: '-0.8h 环比', trendDown: true },
  { label: '维护总成本(万元)', value: '42.8', unit: '', color: '#333', trend: '+5.2% 环比', trendUpBad: true }
]

function handleExport(type) {
  ElMessage.info(`${type === 'excel' ? 'Excel' : 'PDF'} 导出功能开发中`)
}

function initCharts() {
  const months = ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月']

  // 横向柱状图 - 设备运行时长 Top10
  const barChart = echarts.init(barChartRef.value)
  charts.push(barChart)
  barChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '15px', containLabel: true },
    xAxis: {
      type: 'value',
      axisLabel: { color: '#9ca3af', fontSize: 11 },
      splitLine: { lineStyle: { color: '#f3f4f6' } }
    },
    yAxis: {
      type: 'category',
      data: ['PLC-B015','ROBOT-D008','AGV-C005','CNC-A003','ROBOT-B003','PLC-B012','AGV-C001','CNC-A002','ROBOT-B001','CNC-A001'],
      axisLabel: { color: '#6b7280', fontSize: 11 },
      axisLine: { lineStyle: { color: '#e5e7eb' } }
    },
    series: [{
      type: 'bar',
      data: [490,510,520,540,560,575,590,620,645,680],
      itemStyle: {
        color: {
          type: 'linear', x: 0, y: 0, x2: 1, y2: 0,
          colorStops: [{ offset: 0, color: '#1890FF' }, { offset: 1, color: '#36cfc9' }]
        }
      },
      barWidth: 16,
      borderRadius: [0, 4, 4, 0]
    }]
  })

  // 堆叠柱状图 - 维护成本月度趋势
  const costChart = echarts.init(costChartRef.value)
  charts.push(costChart)
  costChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['人工成本', '备件成本', '外包成本'], top: 0, textStyle: { fontSize: 11 } },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '40px', containLabel: true },
    xAxis: {
      type: 'category', data: months,
      axisLine: { lineStyle: { color: '#e5e7eb' } },
      axisLabel: { color: '#9ca3af', fontSize: 11 }
    },
    yAxis: {
      type: 'value', name: '万元',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#f3f4f6' } },
      axisLabel: { color: '#9ca3af', fontSize: 11 }
    },
    series: [
      { name: '人工成本', type: 'bar', stack: 'cost', data: [8.5,7.2,9.1,8.8,8.2,9.5,8.0,7.8,8.6,9.2,8.4,8.5], itemStyle: { color: '#1890FF' }, barWidth: 20 },
      { name: '备件成本', type: 'bar', stack: 'cost', data: [12.3,10.5,14.2,11.8,13.5,12.0,15.1,11.2,13.8,12.5,14.0,13.2], itemStyle: { color: '#36cfc9' } },
      { name: '外包成本', type: 'bar', stack: 'cost', data: [5.2,4.8,6.1,5.5,4.2,5.8,6.5,4.5,5.0,5.3,6.2,5.8], itemStyle: { color: '#faad14' } }
    ]
  })

  // 饼图 - 故障类型分布
  const faultChart = echarts.init(faultChartRef.value)
  charts.push(faultChart)
  faultChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c}次 ({d}%)' },
    series: [{
      type: 'pie', radius: ['35%', '65%'],
      data: [
        { value: 45, name: '机械故障', itemStyle: { color: '#ff4d4f' } },
        { value: 32, name: '电气故障', itemStyle: { color: '#faad14' } },
        { value: 28, name: '软件故障', itemStyle: { color: '#1890FF' } },
        { value: 18, name: '传感器故障', itemStyle: { color: '#722ed1' } },
        { value: 12, name: '其他', itemStyle: { color: '#9ca3af' } }
      ],
      label: { fontSize: 11 }
    }]
  })

  // 雷达图 - 各区域设备效率对比
  const radarChart = echarts.init(radarChartRef.value)
  charts.push(radarChart)
  radarChart.setOption({
    tooltip: {},
    legend: { data: ['A区','B区','C区','D区','E区'], top: 0, textStyle: { fontSize: 11 } },
    radar: {
      indicator: [
        { name: 'OEE', max: 100 },
        { name: '可用率', max: 100 },
        { name: '性能率', max: 100 },
        { name: '合格率', max: 100 },
        { name: 'MTBF(h)', max: 1000 },
        { name: 'MTTR(h)', max: 10 }
      ],
      center: ['50%', '55%'],
      radius: '60%'
    },
    series: [{
      type: 'radar',
      data: [
        { value: [92,95,94,98,850,3.2], name: 'A区', lineStyle: { color: '#1890FF' }, itemStyle: { color: '#1890FF' } },
        { value: [88,92,90,96,720,4.5], name: 'B区', lineStyle: { color: '#36cfc9' }, itemStyle: { color: '#36cfc9' } },
        { value: [85,90,88,95,680,5.0], name: 'C区', lineStyle: { color: '#faad14' }, itemStyle: { color: '#faad14' } },
        { value: [82,88,86,94,650,5.5], name: 'D区', lineStyle: { color: '#722ed1' }, itemStyle: { color: '#722ed1' } },
        { value: [90,93,92,97,780,3.8], name: 'E区', lineStyle: { color: '#13c2c2' }, itemStyle: { color: '#13c2c2' } }
      ]
    }]
  })
}

function handleResize() { charts.forEach(c => c.resize()) }

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
/* 筛选栏 */
.filter-bar {
  background: #fff;
  border-radius: 12px;
  padding: 16px 20px;
  border: 1px solid #f0f0f0;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
}

.filter-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-label { font-size: 13px; color: #666; }

.period-tab {
  padding: 6px 12px;
  font-size: 13px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  background: transparent;
  color: #999;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 4px;
}

.period-tab.active { background: #e6f4ff; color: #1890FF; }
.period-tab:hover:not(.active) { background: #f5f5f5; }

.filter-right { display: flex; gap: 8px; }

/* 指标卡片 */
.metrics-row { margin-bottom: 20px; }

.metric-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #f0f0f0;
  margin-bottom: 16px;
}

.metric-label { font-size: 12px; color: #999; margin-bottom: 4px; }

.metric-value {
  font-size: 30px;
  font-weight: 700;
  line-height: 1;
}

.metric-unit { font-size: 18px; }

.metric-trend {
  font-size: 12px;
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 2px;
}

.trend-up { color: #52c41a; }
.trend-down-good { color: #52c41a; }
.trend-up-bad { color: #ff4d4f; }

/* 图表卡片 */
.chart-row { margin-bottom: 20px; }

.chart-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #f0f0f0;
  margin-bottom: 16px;
}

.chart-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px;
}
</style>
