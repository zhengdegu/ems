<template>
  <div v-loading="loading">
    <!-- 设备头部大卡片 -->
    <el-card shadow="never" style="margin-bottom:16px">
      <div class="equipment-header">
        <div class="equipment-header-left">
          <div class="equipment-avatar">
            <el-icon :size="48" color="#1890FF"><Monitor /></el-icon>
          </div>
          <div class="equipment-info">
            <div style="display:flex;align-items:center;gap:12px;margin-bottom:8px">
              <h2 style="margin:0;font-size:20px;color:#333">{{ equipment.name || '--' }}</h2>
              <el-tag :type="statusType" size="default">{{ statusText }}</el-tag>
            </div>
            <div class="equipment-meta">
              <span><el-icon><Ticket /></el-icon> {{ equipment.code || '--' }}</span>
              <span><el-icon><Grid /></el-icon> {{ equipment.type || '--' }}</span>
              <span><el-icon><Location /></el-icon> {{ equipment.location || '--' }}</span>
              <span><el-icon><User /></el-icon> {{ equipment.responsible || '--' }}</span>
            </div>
          </div>
        </div>
        <div class="equipment-header-right">
          <el-button @click="showQrCode = true"><el-icon><Connection /></el-icon> 二维码</el-button>
          <el-button type="primary" @click="$router.push({ name: 'equipment-form', params: { id: equipment.id } })"><el-icon><Edit /></el-icon> 编辑</el-button>
          <el-button type="warning" @click="$router.push({ name: 'work-order' })"><el-icon><Tools /></el-icon> 发起维护</el-button>
        </div>
      </div>
    </el-card>

    <el-row :gutter="16" style="margin-bottom:16px">
      <!-- 健康度 -->
      <el-col :span="8">
        <el-card shadow="never" style="height:280px">
          <template #header><span style="font-weight:600">健康度</span></template>
          <div ref="gaugeRef" style="width:100%;height:200px"></div>
        </el-card>
      </el-col>

      <!-- 运行参数 -->
      <el-col :span="16">
        <el-card shadow="never" style="height:280px">
          <template #header><span style="font-weight:600">运行参数</span></template>
          <el-row :gutter="16">
            <el-col :span="6" v-for="param in runParams" :key="param.label">
              <div class="param-card">
                <div class="param-label">{{ param.label }}</div>
                <div class="param-value" :style="{ color: param.color }">{{ param.value }}</div>
                <div class="param-unit">{{ param.unit }}</div>
                <el-progress
                  :percentage="param.percent"
                  :stroke-width="6"
                  :color="param.color"
                  :show-text="false"
                  style="margin-top:12px"
                />
                <div class="param-threshold">阈值: {{ param.threshold }}{{ param.unit }}</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <!-- 基本信息 -->
    <el-card shadow="never" style="margin-bottom:16px">
      <template #header><span style="font-weight:600">基本信息</span></template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="设备编号">{{ equipment.code }}</el-descriptions-item>
        <el-descriptions-item label="设备名称">{{ equipment.name }}</el-descriptions-item>
        <el-descriptions-item label="设备类型">{{ equipment.type }}</el-descriptions-item>
        <el-descriptions-item label="规格型号">{{ equipment.model }}</el-descriptions-item>
        <el-descriptions-item label="制造商">{{ equipment.manufacturer }}</el-descriptions-item>
        <el-descriptions-item label="出厂日期">{{ equipment.manufactureDate }}</el-descriptions-item>
        <el-descriptions-item label="所在位置">{{ equipment.location }}</el-descriptions-item>
        <el-descriptions-item label="责任人">{{ equipment.responsible }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType" size="small">{{ statusText }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-tabs v-model="activeTab" type="border-card">
      <!-- 维护记录 -->
      <el-tab-pane label="维护记录" name="maintenance">
        <el-timeline>
          <el-timeline-item v-for="(record, i) in maintenanceRecords" :key="i" :timestamp="record.date" placement="top"
            :type="record.type === '故障维修' ? 'danger' : record.type === '预防维护' ? 'primary' : 'success'">
            <el-card shadow="never">
              <div style="display:flex;justify-content:space-between">
                <div>
                  <el-tag size="small" :type="record.type === '故障维修' ? 'danger' : 'primary'" style="margin-right:8px">{{ record.type }}</el-tag>
                  <span style="font-weight:600">{{ record.title }}</span>
                </div>
                <span style="color:#999;font-size:12px">{{ record.operator }}</span>
              </div>
              <p style="color:#666;font-size:13px;margin-top:8px">{{ record.desc }}</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </el-tab-pane>

      <!-- 生命周期 -->
      <el-tab-pane label="生命周期" name="lifecycle">
        <el-steps :active="3" align-center>
          <el-step title="采购入库" :description="equipment.purchaseDate || ''" />
          <el-step title="安装调试" description="" />
          <el-step title="投入使用" :description="equipment.manufactureDate || ''" />
          <el-step title="运行中" description="至今" />
          <el-step title="报废" description="" />
        </el-steps>
      </el-tab-pane>

      <!-- 关联文档 -->
      <el-tab-pane label="关联文档" name="docs">
        <el-table :data="documents">
          <el-table-column prop="name" label="文档名称" />
          <el-table-column prop="type" label="类型" width="120" />
          <el-table-column prop="size" label="大小" width="100" />
          <el-table-column prop="uploadTime" label="上传时间" width="160" />
          <el-table-column label="操作" width="120">
            <template #default><el-button link type="primary">下载</el-button></template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 二维码弹窗 -->
    <el-dialog v-model="showQrCode" title="设备二维码" width="300px" align-center>
      <div style="text-align:center;padding:20px">
        <div style="width:200px;height:200px;margin:0 auto;background:#f5f5f5;display:flex;align-items:center;justify-content:center;border-radius:8px">
          <span style="color:#999;font-size:13px">{{ equipment.code }}</span>
        </div>
        <p style="color:#999;font-size:12px;margin-top:12px">扫描二维码查看设备信息</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getEquipmentDetail } from '../../api/equipment'
import * as echarts from 'echarts'

const route = useRoute()
const activeTab = ref('maintenance')
const loading = ref(false)
const showQrCode = ref(false)
const gaugeRef = ref()
let gaugeChart = null

const equipment = ref({})
const healthScore = ref(92)

const statusMap = {
  running: { text: '运行中', type: 'success' },
  stopped: { text: '停机', type: 'info' },
  maintenance: { text: '维修中', type: 'warning' }
}
const statusText = computed(() => statusMap[equipment.value.status]?.text || equipment.value.status || '--')
const statusType = computed(() => statusMap[equipment.value.status]?.type || 'info')

const runParams = reactive([
  { label: '主轴转速', value: '8,500', unit: 'RPM', color: '#1890FF', threshold: '12,000', percent: 71 },
  { label: '主轴温度', value: '62', unit: '°C', color: '#52c41a', threshold: '85', percent: 73 },
  { label: '振动幅度', value: '3.2', unit: 'mm/s', color: '#faad14', threshold: '10', percent: 32 },
  { label: '运行时长', value: '12,480', unit: '小时', color: '#722ed1', threshold: '20,000', percent: 62 }
])

const maintenanceRecords = [
  { date: '2024-03-01', type: '预防维护', title: '季度保养', desc: '更换润滑油、检查主轴精度、清洁冷却系统', operator: '李工' },
  { date: '2024-01-15', type: '故障维修', title: '主轴异响处理', desc: '更换主轴轴承，重新校准精度', operator: '王工' },
  { date: '2023-12-01', type: '预防维护', title: '年度大保养', desc: '全面检查电气系统、液压系统、气动系统', operator: '张工' }
]

const documents = [
  { name: '设备操作手册.pdf', type: 'PDF', size: '12.5MB', uploadTime: '2020-04-01' },
  { name: '维护保养规程.docx', type: 'Word', size: '3.2MB', uploadTime: '2020-04-05' },
  { name: '电气原理图.dwg', type: 'CAD', size: '8.7MB', uploadTime: '2020-04-01' }
]

function initGauge() {
  if (!gaugeRef.value) return
  gaugeChart = echarts.init(gaugeRef.value)
  const score = healthScore.value
  const color = score >= 80 ? '#52c41a' : score >= 60 ? '#faad14' : '#ff4d4f'
  gaugeChart.setOption({
    series: [{
      type: 'gauge',
      startAngle: 220,
      endAngle: -40,
      min: 0,
      max: 100,
      radius: '90%',
      progress: { show: true, width: 14, roundCap: true, itemStyle: { color } },
      pointer: { show: false },
      axisLine: { lineStyle: { width: 14, color: [[1, '#f0f0f0']] } },
      axisTick: { show: false },
      splitLine: { show: false },
      axisLabel: { show: false },
      title: { show: true, offsetCenter: [0, '60%'], fontSize: 14, color: '#999' },
      detail: {
        valueAnimation: true, offsetCenter: [0, '10%'],
        fontSize: 36, fontWeight: 700, color,
        formatter: '{value}分'
      },
      data: [{ value: score, name: '健康评分' }]
    }]
  })
}

async function loadDetail() {
  const id = route.params.id
  if (!id) return
  loading.value = true
  try {
    const res = await getEquipmentDetail(id)
    if (res.code === 200 && res.data) {
      equipment.value = res.data
      if (res.data.healthScore != null) {
        healthScore.value = res.data.healthScore
      }
      await nextTick()
      initGauge()
    }
  } catch {
    ElMessage.error('加载设备详情失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadDetail)

onUnmounted(() => {
  gaugeChart?.dispose()
})
</script>

<style scoped>
.equipment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.equipment-header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}
.equipment-avatar {
  width: 80px;
  height: 80px;
  border-radius: 12px;
  background: rgba(24, 144, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.equipment-info {
  min-width: 0;
}
.equipment-meta {
  display: flex;
  gap: 24px;
  color: #666;
  font-size: 13px;
}
.equipment-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}
.equipment-header-right {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}
.param-card {
  text-align: center;
  padding: 16px 12px;
  background: #fafafa;
  border-radius: 8px;
}
.param-label {
  color: #999;
  font-size: 13px;
  margin-bottom: 8px;
}
.param-value {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.2;
}
.param-unit {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}
.param-threshold {
  font-size: 11px;
  color: #bbb;
  margin-top: 6px;
}
</style>
