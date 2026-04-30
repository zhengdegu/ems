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
            <el-timeline-item
              v-for="(step, i) in timeline"
              :key="i"
              :timestamp="step.time"
              placement="top"
              :type="step.active ? step.type : 'info'"
              :hollow="!step.active"
            >
              <div style="font-weight:600">{{ step.title }}</div>
              <p v-if="step.desc" style="color:#666;font-size:13px;margin-top:4px">{{ step.desc }}</p>
              <span v-if="step.operator" style="color:#999;font-size:12px">操作人：{{ step.operator }}</span>
            </el-timeline-item>
          </el-timeline>
        </el-card>

        <!-- 工时统计 -->
        <el-card shadow="never" style="margin-bottom:16px">
          <template #header><span style="font-weight:600">工时统计</span></template>
          <el-row :gutter="24" style="margin-bottom:20px">
            <el-col :span="8">
              <div class="hour-stat">
                <div class="hour-stat-label">计划工时</div>
                <div class="hour-stat-value">{{ workHourSummary.planned }}h</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="hour-stat">
                <div class="hour-stat-label">实际工时</div>
                <div class="hour-stat-value" style="color:#1890FF">{{ workHourSummary.actual }}h</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="hour-stat">
                <div class="hour-stat-label">加班工时</div>
                <div class="hour-stat-value" style="color:#faad14">{{ workHourSummary.overtime }}h</div>
              </div>
            </el-col>
          </el-row>
          <el-table :data="workHours" size="small">
            <el-table-column prop="person" label="人员" width="100" />
            <el-table-column prop="hours" label="工时(h)" width="80" />
            <el-table-column prop="content" label="工作内容" />
            <el-table-column prop="date" label="日期" width="120" />
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="8">
        <!-- 备件消耗 -->
        <el-card shadow="never" style="margin-bottom:16px">
          <template #header><span style="font-weight:600">备件消耗</span></template>
          <el-table :data="parts" size="small">
            <el-table-column prop="name" label="备件名称" />
            <el-table-column prop="spec" label="规格" width="80" />
            <el-table-column prop="quantity" label="数量" width="50" />
            <el-table-column prop="unitPrice" label="单价" width="70">
              <template #default="{ row }">¥{{ row.unitPrice }}</template>
            </el-table-column>
            <el-table-column label="小计" width="80">
              <template #default="{ row }">¥{{ (row.quantity * row.unitPrice).toFixed(2) }}</template>
            </el-table-column>
          </el-table>
          <div style="text-align:right;margin-top:12px;font-weight:600;color:#333">
            合计：¥{{ partsTotal }}
          </div>
        </el-card>

        <!-- 关联信息 -->
        <el-card shadow="never">
          <template #header><span style="font-weight:600">关联信息</span></template>
          <div style="padding:4px 0">
            <div class="info-row">
              <span class="info-label">关联设备</span>
              <span class="info-value">{{ order.equipmentName || '--' }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">创建人</span>
              <span class="info-value">{{ order.creator || '--' }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">截止时间</span>
              <span class="info-value">{{ order.deadline || '--' }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getWorkOrderDetail, updateWorkOrderStatus } from '../../api/workOrder'

const route = useRoute()
const loading = ref(false)

const order = ref({})

const timeline = [
  { time: '2024-03-08 09:30', title: '创建工单', desc: '系统自动创建紧急维修工单', operator: '系统', type: 'primary', active: true },
  { time: '2024-03-08 09:35', title: '派发工单', desc: '派发给张工处理', operator: '李主管', type: 'primary', active: true },
  { time: '2024-03-08 09:45', title: '接单', desc: '张工已接单', operator: '张工', type: 'primary', active: true },
  { time: '2024-03-08 09:50', title: '开始处理', desc: '已到达现场，开始排查故障原因', operator: '张工', type: 'warning', active: true },
  { time: '', title: '完成', desc: '', operator: '', type: 'success', active: false },
]

const parts = [
  { name: '冷却液循环泵', spec: 'CP-200', quantity: 1, unitPrice: 2800.00 },
  { name: '密封圈套件', spec: 'SR-15', quantity: 1, unitPrice: 120.00 },
  { name: '冷却液 10L', spec: 'CL-10', quantity: 2, unitPrice: 180.00 },
]

const partsTotal = computed(() => {
  return parts.reduce((sum, p) => sum + p.quantity * p.unitPrice, 0).toFixed(2)
})

const workHourSummary = {
  planned: 4,
  actual: 4.5,
  overtime: 0.5
}

const workHours = [
  { person: '张工', hours: 3, content: '故障排查与维修', date: '2024-03-08' },
  { person: '王工', hours: 1.5, content: '协助拆装', date: '2024-03-08' },
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

<style scoped>
.hour-stat {
  text-align: center;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}
.hour-stat-label {
  font-size: 13px;
  color: #999;
  margin-bottom: 8px;
}
.hour-stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #333;
}
.info-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
  font-size: 13px;
}
.info-row:last-child {
  border-bottom: none;
}
.info-label {
  color: #999;
}
.info-value {
  color: #333;
}
</style>
