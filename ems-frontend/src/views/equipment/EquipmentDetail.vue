<template>
  <div>
    <el-page-header @back="$router.back()" style="margin-bottom:16px">
      <template #content><span style="font-size:16px;font-weight:600">设备详情 - {{ equipment.name }}</span></template>
      <template #extra>
        <el-button type="primary" @click="$router.push({ name: 'equipment-form', params: { id: equipment.id } })">编辑</el-button>
        <el-button @click="$router.push({ name: 'work-order' })">创建工单</el-button>
      </template>
    </el-page-header>

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
          <el-tag :type="equipment.status === 'running' ? 'success' : equipment.status === 'maintenance' ? 'warning' : 'info'" size="small">
            {{ { running: '运行中', stopped: '停机', maintenance: '维修中' }[equipment.status] }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-tabs v-model="activeTab" type="border-card">
      <!-- 运行参数 -->
      <el-tab-pane label="运行参数" name="params">
        <el-row :gutter="16">
          <el-col :span="6" v-for="param in runParams" :key="param.label">
            <div class="stat-card" style="text-align:center">
              <div style="color:#999;font-size:13px;margin-bottom:8px">{{ param.label }}</div>
              <div style="font-size:24px;font-weight:700" :style="{ color: param.color }">{{ param.value }}</div>
              <div style="font-size:12px;color:#999;margin-top:4px">{{ param.unit }}</div>
            </div>
          </el-col>
        </el-row>
      </el-tab-pane>

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
          <el-step title="采购入库" description="2020-03-15" />
          <el-step title="安装调试" description="2020-04-01" />
          <el-step title="投入使用" description="2020-04-10" />
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
  </div>
</template>

<script setup>
import { ref } from 'vue'

const activeTab = ref('params')

const equipment = ref({
  id: 1, code: 'CNC-001', name: '五轴数控加工中心', type: '数控机床',
  model: 'DMG MORI DMU 50', manufacturer: 'DMG MORI', manufactureDate: '2020-01-15',
  location: 'A区-生产线', responsible: '张工', status: 'running'
})

const runParams = [
  { label: '主轴转速', value: '8,500', unit: 'RPM', color: '#1890FF' },
  { label: '主轴温度', value: '62', unit: '°C', color: '#52c41a' },
  { label: '运行时长', value: '12,480', unit: '小时', color: '#722ed1' },
  { label: '健康评分', value: '92', unit: '分', color: '#52c41a' }
]

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
</script>
