<template>
  <div v-loading="loading">
    <el-page-header @back="$router.back()" style="margin-bottom:16px">
      <template #content><span style="font-size:16px;font-weight:600">工单详情 - {{ order.code }}</span></template>
      <template #extra>
        <el-button type="primary" v-if="order.status === '待接单'" @click="handleAccept">接单处理</el-button>
        <el-button>转派</el-button>
      </template>
    </el-page-header>

    <el-row :gutter="16">
      <el-col :span="16">
        <!-- 基本信息 -->
        <el-card shadow="never" style="margin-bottom:16px">
          <template #header><span style="font-weight:600">工单信息</span></template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="工单编号">{{ order.code }}</el-descriptions-item>
            <el-descriptions-item label="工单标题">{{ order.title }}</el-descriptions-item>
            <el-descriptions-item label="工单类型">
              <el-tag :type="order.type === '故障维修' ? 'danger' : 'primary'" size="small">{{ order.type }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="优先级">
              <el-tag :type="order.priority === '紧急' ? 'danger' : order.priority === '高' ? 'warning' : 'info'" size="small" effect="plain">{{ order.priority }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="关联设备">{{ order.equipmentName }}</el-descriptions-item>
            <el-descriptions-item label="负责人">{{ order.assignee }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ order.createTime }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="order.status === '处理中' ? 'warning' : order.status === '已完成' ? 'success' : 'info'" size="small">{{ order.status }}</el-tag>
            </el-descriptions-item>
          </el-descriptions>
          <div style="margin-top:16px">
            <div style="font-weight:600;margin-bottom:8px">问题描述</div>
            <p style="color:#666;font-size:13px;line-height:1.8">{{ order.description }}</p>
          </div>
        </el-card>

        <!-- 处理流程 -->
        <el-card shadow="never" style="margin-bottom:16px">
          <template #header><span style="font-weight:600">处理流程</span></template>
          <el-timeline>
            <el-timeline-item v-for="(step, i) in timeline" :key="i" :timestamp="step.time" placement="top" :type="step.type">
              <div style="font-weight:600">{{ step.title }}</div>
              <p style="color:#666;font-size:13px;margin-top:4px">{{ step.desc }}</p>
              <span style="color:#999;font-size:12px">操作人：{{ step.operator }}</span>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>

      <el-col :span="8">
        <!-- 备件消耗 -->
        <el-card shadow="never" style="margin-bottom:16px">
          <template #header><span style="font-weight:600">备件消耗</span></template>
          <el-table :data="parts" size="small">
            <el-table-column prop="name" label="备件名称" />
            <el-table-column prop="quantity" label="数量" width="60" />
            <el-table-column prop="cost" label="费用" width="80" />
          </el-table>
          <div style="text-align:right;margin-top:12px;font-weight:600;color:#333">
            合计：¥{{ parts.reduce((s, p) => s + parseFloat(p.cost.replace(/,/g, '')), 0).toFixed(2) }}
          </div>
        </el-card>

        <!-- 工时记录 -->
        <el-card shadow="never">
          <template #header><span style="font-weight:600">工时记录</span></template>
          <el-table :data="workHours" size="small">
            <el-table-column prop="person" label="人员" width="70" />
            <el-table-column prop="hours" label="工时(h)" width="70" />
            <el-table-column prop="content" label="工作内容" />
          </el-table>
          <div style="text-align:right;margin-top:12px;font-weight:600;color:#333">
            总工时：{{ workHours.reduce((s, w) => s + w.hours, 0) }}h
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getWorkOrderDetail, updateWorkOrderStatus } from '../../api/workOrder'

const route = useRoute()
const loading = ref(false)

const order = ref({})

const timeline = [
  { time: '2024-03-08 09:30', title: '工单创建', desc: '系统自动创建紧急维修工单', operator: '系统', type: 'primary' },
  { time: '2024-03-08 09:35', title: '工单派发', desc: '派发给张工处理', operator: '李主管', type: 'primary' },
  { time: '2024-03-08 09:45', title: '开始处理', desc: '已到达现场，开始排查故障原因', operator: '张工', type: 'warning' },
  { time: '2024-03-08 10:30', title: '故障定位', desc: '确认为冷却液循环泵故障，需更换泵体', operator: '张工', type: 'warning' },
]

const parts = [
  { name: '冷却液循环泵', quantity: 1, cost: '2,800.00' },
  { name: '密封圈套件', quantity: 1, cost: '120.00' },
  { name: '冷却液 10L', quantity: 2, cost: '360.00' },
]

const workHours = [
  { person: '张工', hours: 3, content: '故障排查与维修' },
  { person: '王工', hours: 1.5, content: '协助拆装' },
]

async function loadDetail() {
  const id = route.params.id
  if (!id) return
  loading.value = true
  try {
    const res = await getWorkOrderDetail(id)
    if (res.code === 200 && res.data) {
      order.value = res.data
    }
  } catch {
    ElMessage.error('加载工单详情失败')
  } finally {
    loading.value = false
  }
}

async function handleAccept() {
  try {
    const res = await updateWorkOrderStatus(order.value.id, '处理中')
    if (res.code === 200) {
      ElMessage.success('已接单')
      loadDetail()
    }
  } catch {
    ElMessage.error('操作失败')
  }
}

onMounted(loadDetail)
</script>
