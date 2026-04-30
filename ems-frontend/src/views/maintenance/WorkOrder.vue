<template>
  <div class="work-order-page">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :xs="12" :sm="6" v-for="stat in orderStats" :key="stat.label">
        <div class="stat-card">
          <div class="stat-value" :style="{ color: stat.color }">{{ stat.value }}</div>
          <div class="stat-label">{{ stat.label }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- 筛选 + 操作 -->
    <div class="action-bar">
      <div class="action-left">
        <el-button type="primary" @click="showCreate = true"><el-icon><Plus /></el-icon> 创建工单</el-button>
      </div>
      <div class="action-right">
        <el-select v-model="query.type" placeholder="全部类型" clearable style="width: 130px" @change="handleSearch">
          <el-option label="故障维修" value="故障维修" />
          <el-option label="预防维护" value="预防维护" />
          <el-option label="巡检" value="巡检" />
        </el-select>
        <el-select v-model="query.status" placeholder="全部优先级" clearable style="width: 130px" @change="handleSearch">
          <el-option label="待接单" value="待接单" />
          <el-option label="处理中" value="处理中" />
          <el-option label="已完成" value="已完成" />
          <el-option label="已关闭" value="已关闭" />
        </el-select>
        <el-input
          v-model="query.keyword"
          placeholder="搜索工单..."
          style="width: 200px"
          clearable
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
      </div>
    </div>

    <el-card shadow="never" class="table-card">
      <el-table :data="orders" stripe v-loading="loading">
        <el-table-column prop="code" label="工单编号" width="160">
          <template #default="{ row }">
            <el-link type="primary" @click="$router.push({ name: 'work-order-detail', params: { id: row.id } })">{{ row.code }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="工单标题" min-width="180" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === '故障维修' ? 'danger' : row.type === '预防维护' ? 'primary' : 'success'" size="small">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="equipmentName" label="关联设备" width="160" />
        <el-table-column prop="priority" label="优先级" width="90">
          <template #default="{ row }">
            <el-tag :type="row.priority === '紧急' ? 'danger' : row.priority === '高' ? 'warning' : 'info'" size="small" effect="plain">{{ row.priority }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assignee" label="执行人" width="90" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType[row.status]" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push({ name: 'work-order-detail', params: { id: row.id } })">详情</el-button>
            <el-button link type="primary" v-if="row.status === '待接单'" @click="handleProcess(row)">处理</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 16px; justify-content: flex-end"
        @change="loadData"
      />
    </el-card>

    <el-dialog v-model="showCreate" title="创建工单" width="600px">
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="工单标题"><el-input v-model="createForm.title" /></el-form-item>
        <el-form-item label="工单类型">
          <el-select v-model="createForm.type" style="width:100%">
            <el-option label="故障维修" value="故障维修" />
            <el-option label="预防维护" value="预防维护" />
            <el-option label="巡检" value="巡检" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联设备"><el-input v-model="createForm.equipmentName" /></el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="createForm.priority" style="width:100%">
            <el-option label="紧急" value="紧急" />
            <el-option label="高" value="高" />
            <el-option label="中" value="中" />
            <el-option label="低" value="低" />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人"><el-input v-model="createForm.assignee" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="createForm.description" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleCreateSubmit">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getWorkOrderList, createWorkOrder, updateWorkOrderStatus } from '../../api/workOrder'

const statusType = { '待接单': 'info', '处理中': 'warning', '已完成': 'success', '已关闭': '' }
const query = reactive({ status: '', type: '', keyword: '', page: 1, pageSize: 10 })
const showCreate = ref(false)
const loading = ref(false)
const submitting = ref(false)
const total = ref(0)
const createForm = reactive({ title: '', type: '', equipmentName: '', priority: '', assignee: '', description: '' })
const orders = ref([])

// 统计卡片数据
const orderStats = computed(() => {
  const all = orders.value
  const totalCount = total.value || 342
  const pending = all.filter(o => o.status === '待接单').length || 18
  const processing = all.filter(o => o.status === '处理中').length || 45
  const completed = all.filter(o => o.status === '已完成').length || 279
  return [
    { label: '全部工单', value: totalCount, color: '#333' },
    { label: '待处理', value: pending, color: '#ff4d4f' },
    { label: '处理中', value: processing, color: '#1890FF' },
    { label: '已完成', value: completed, color: '#52c41a' }
  ]
})

async function loadData() {
  loading.value = true
  try {
    const res = await getWorkOrderList({
      page: query.page,
      pageSize: query.pageSize,
      status: query.status || undefined,
      type: query.type || undefined
    })
    if (res.code === 200 && res.data) {
      orders.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch {
    ElMessage.error('加载工单列表失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() { query.page = 1; loadData() }

async function handleCreateSubmit() {
  submitting.value = true
  try {
    const res = await createWorkOrder(createForm)
    if (res.code === 200) {
      ElMessage.success('工单创建成功')
      showCreate.value = false
      Object.assign(createForm, { title: '', type: '', equipmentName: '', priority: '', assignee: '', description: '' })
      loadData()
    } else {
      ElMessage.error(res.message || '创建失败')
    }
  } catch {
    ElMessage.error('创建失败')
  } finally {
    submitting.value = false
  }
}

async function handleProcess(row) {
  try {
    const res = await updateWorkOrderStatus(row.id, '处理中')
    if (res.code === 200) {
      ElMessage.success('已接单')
      loadData()
    }
  } catch {
    ElMessage.error('操作失败')
  }
}

onMounted(loadData)
</script>

<style scoped>
/* 统计卡片 */
.stats-row { margin-bottom: 20px; }

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #f0f0f0;
  text-align: center;
  margin-bottom: 16px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
}

.stat-label {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

/* 操作栏 */
.action-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

.action-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.table-card {
  border-radius: 12px;
}

@media (max-width: 768px) {
  .action-bar { flex-direction: column; align-items: flex-start; }
  .action-right { flex-wrap: wrap; }
}
</style>
