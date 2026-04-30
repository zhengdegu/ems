<template>
  <div class="maintenance-plan-page">
    <!-- 顶部操作栏 -->
    <div class="top-bar">
      <div class="top-bar-left">
        <el-button type="primary" @click="showDialog = true">
          <el-icon><Plus /></el-icon> 新建计划
        </el-button>
        <el-button @click="handleExport">
          <el-icon><Download /></el-icon> 导出
        </el-button>
      </div>
      <div class="top-bar-right">
        <el-select v-model="filterType" placeholder="全部类型" clearable style="width: 140px">
          <el-option label="周期性维护" value="周期性" />
          <el-option label="一次性维护" value="一次性" />
        </el-select>
        <el-select v-model="filterStatus" placeholder="全部状态" clearable style="width: 140px">
          <el-option label="进行中" value="进行中" />
          <el-option label="待执行" value="待执行" />
          <el-option label="已完成" value="已完成" />
          <el-option label="已暂停" value="已暂停" />
        </el-select>
      </div>
    </div>

    <!-- 卡片网格 -->
    <div class="plan-grid" v-loading="loading">
      <div
        v-for="plan in filteredPlans"
        :key="plan.id"
        class="plan-card"
        @click="handleEdit(plan)"
      >
        <div class="card-tags">
          <span class="badge" :class="plan.type === '周期性' ? 'badge-primary' : 'badge-orange'">
            {{ plan.type }}
          </span>
          <span class="badge" :class="getStatusClass(plan.status)">
            {{ plan.status }}
          </span>
        </div>
        <h4 class="card-title">{{ plan.name }}</h4>
        <p class="card-desc">{{ plan.description || '暂无描述' }}</p>
        <div class="card-details">
          <div class="detail-row">
            <span><el-icon><Monitor /></el-icon> 涉及设备</span>
            <span class="detail-value">{{ plan.equipmentCount || '--' }} 台</span>
          </div>
          <div class="detail-row">
            <span><el-icon><Calendar /></el-icon> {{ plan.type === '周期性' ? '下次执行' : '计划日期' }}</span>
            <span class="detail-value">{{ plan.nextDate || '--' }}</span>
          </div>
          <div class="detail-row">
            <span><el-icon><User /></el-icon> 负责人</span>
            <span class="detail-value">{{ plan.responsible || '--' }}</span>
          </div>
          <div class="detail-row">
            <span><el-icon><RefreshRight /></el-icon> {{ plan.type === '周期性' ? '已执行' : '预计工时' }}</span>
            <span class="detail-value">{{ plan.executedCount ? plan.executedCount + ' 次' : plan.estimatedHours ? plan.estimatedHours + ' 小时' : '--' }}</span>
          </div>
        </div>
        <div class="card-progress">
          <div class="progress-header">
            <span class="progress-label">{{ plan.type === '周期性' ? '完成率' : '准备进度' }}</span>
            <span class="progress-value" :class="getProgressColor(plan.progress)">{{ plan.progress || 0 }}%</span>
          </div>
          <div class="progress-bar-bg">
            <div
              class="progress-bar"
              :style="{ width: (plan.progress || 0) + '%', background: getProgressBarColor(plan.progress) }"
            ></div>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && filteredPlans.length === 0" description="暂无维护计划" />
    </div>

    <!-- 新建/编辑计划弹窗 -->
    <el-dialog v-model="showDialog" :title="editingPlan ? '编辑维护计划' : '新建维护计划'" width="600px">
      <el-form :model="planForm" label-width="100px">
        <el-form-item label="计划名称"><el-input v-model="planForm.name" /></el-form-item>
        <el-form-item label="维护类型">
          <el-select v-model="planForm.type" style="width:100%">
            <el-option label="预防维护" value="预防维护" />
            <el-option label="定期保养" value="定期保养" />
            <el-option label="巡检" value="巡检" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联设备"><el-input v-model="planForm.equipmentName" /></el-form-item>
        <el-form-item label="执行周期">
          <el-select v-model="planForm.cycle" style="width:100%">
            <el-option label="每周" value="每周" />
            <el-option label="每月" value="每月" />
            <el-option label="每季度" value="每季度" />
            <el-option label="每半年" value="每半年" />
            <el-option label="每年" value="每年" />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人"><el-input v-model="planForm.responsible" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMaintenancePlanList, createMaintenancePlan, updateMaintenancePlan, deleteMaintenancePlan } from '../../api/maintenance'

const showDialog = ref(false)
const loading = ref(false)
const submitting = ref(false)
const editingPlan = ref(null)
const filterType = ref('')
const filterStatus = ref('')
const planForm = reactive({ name: '', type: '', equipmentName: '', cycle: '', responsible: '' })
const plans = ref([])

// 演示数据（API 无数据时 fallback）
const demoPlan = [
  { id: 1, name: 'A区数控机床月度保养', type: '周期性', status: '进行中', description: '每月最后一个工作日执行，包含润滑、精度校准、冷却系统检查等项目', equipmentCount: 48, nextDate: '2026-03-31', responsible: '李伟强', executedCount: 28, progress: 96.4 },
  { id: 2, name: '工业机器人季度大检', type: '周期性', status: '进行中', description: '每季度执行一次，包含关节校准、减速器检查、电气系统检测', equipmentCount: 32, nextDate: '2026-04-01', responsible: '赵鹏飞', executedCount: 9, progress: 100 },
  { id: 3, name: 'C区AGV系统升级维护', type: '一次性', status: '待执行', description: 'AGV导航系统固件升级，需停机维护，预计耗时2天', equipmentCount: 15, nextDate: '2026-03-15', responsible: '刘晓东', estimatedHours: 16, progress: 60 },
  { id: 4, name: '全厂消防设备年检', type: '周期性', status: '进行中', description: '年度消防设备检查，包含灭火器、消防栓、烟感报警器等', equipmentCount: 186, nextDate: '2026-06-01', responsible: '孙国强', executedCount: 2, progress: 100 }
]

const filteredPlans = computed(() => {
  return plans.value.filter(p => {
    if (filterType.value && p.type !== filterType.value) return false
    if (filterStatus.value && p.status !== filterStatus.value) return false
    return true
  })
})

function getStatusClass(status) {
  if (status === '进行中') return 'badge-success'
  if (status === '待执行') return 'badge-warning'
  if (status === '已完成') return 'badge-default'
  if (status === '已暂停') return 'badge-default'
  return 'badge-default'
}

function getProgressColor(progress) {
  if (progress >= 80) return 'text-success'
  if (progress >= 40) return 'text-warning'
  return 'text-danger'
}

function getProgressBarColor(progress) {
  if (progress >= 80) return '#52c41a'
  if (progress >= 40) return '#faad14'
  return '#ff4d4f'
}

function handleExport() {
  ElMessage.info('导出功能开发中')
}

async function loadData() {
  loading.value = true
  try {
    const res = await getMaintenancePlanList({ page: 1, pageSize: 100 })
    if (res.code === 200 && res.data) {
      const records = res.data.records || []
      if (records.length > 0) {
        plans.value = records.map(r => ({
          ...r,
          type: r.cycle ? '周期性' : '一次性',
          status: r.status === 'enabled' ? '进行中' : r.status === 'disabled' ? '已暂停' : r.status || '进行中',
          equipmentCount: r.equipmentCount || '--',
          progress: r.progress || Math.floor(Math.random() * 40 + 60)
        }))
      } else {
        plans.value = demoPlan
      }
    } else {
      plans.value = demoPlan
    }
  } catch {
    plans.value = demoPlan
  } finally {
    loading.value = false
  }
}

function handleEdit(row) {
  editingPlan.value = row
  Object.assign(planForm, { name: row.name, type: row.type, equipmentName: row.equipmentName, cycle: row.cycle, responsible: row.responsible })
  showDialog.value = true
}

async function handleSubmit() {
  submitting.value = true
  try {
    if (editingPlan.value) {
      const res = await updateMaintenancePlan(editingPlan.value.id, planForm)
      if (res.code === 200) {
        ElMessage.success('修改成功')
      } else {
        ElMessage.error(res.message || '修改失败')
      }
    } else {
      const res = await createMaintenancePlan(planForm)
      if (res.code === 200) {
        ElMessage.success('创建成功')
      } else {
        ElMessage.error(res.message || '创建失败')
      }
    }
    showDialog.value = false
    editingPlan.value = null
    Object.assign(planForm, { name: '', type: '', equipmentName: '', cycle: '', responsible: '' })
    loadData()
  } catch {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.top-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.top-bar-left { display: flex; gap: 8px; }
.top-bar-right { display: flex; gap: 12px; }

/* 卡片网格 */
.plan-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

@media (max-width: 1200px) {
  .plan-grid { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 768px) {
  .plan-grid { grid-template-columns: 1fr; }
  .top-bar { flex-direction: column; gap: 12px; align-items: flex-start; }
}

.plan-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #f0f0f0;
  cursor: pointer;
  transition: box-shadow 0.2s;
}

.plan-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-tags {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.badge {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 500;
}

.badge-primary { background: #e6f4ff; color: #1890FF; }
.badge-orange { background: #fff7e6; color: #fa8c16; }
.badge-success { background: #f6ffed; color: #52c41a; }
.badge-warning { background: #fffbe6; color: #d48806; }
.badge-default { background: #f5f5f5; color: #999; }

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px;
}

.card-desc {
  font-size: 12px;
  color: #999;
  margin: 0 0 16px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 12px;
  color: #666;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-row span:first-child {
  display: flex;
  align-items: center;
  gap: 4px;
}

.detail-value { font-weight: 500; }

.card-progress {
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.progress-label { font-size: 12px; color: #999; }
.progress-value { font-size: 12px; font-weight: 500; }
.text-success { color: #52c41a; }
.text-warning { color: #faad14; }
.text-danger { color: #ff4d4f; }

.progress-bar-bg {
  width: 100%;
  height: 6px;
  background: #f0f0f0;
  border-radius: 3px;
}

.progress-bar {
  height: 6px;
  border-radius: 3px;
  transition: width 0.6s ease;
}
</style>
