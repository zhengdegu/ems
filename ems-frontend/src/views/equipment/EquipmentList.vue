<template>
  <div>
    <!-- 搜索栏 -->
    <el-card shadow="never" style="margin-bottom:16px">
      <el-form :inline="true" :model="query">
        <el-form-item :label="$t('equipment.name')">
          <el-input v-model="query.name" :placeholder="$t('equipment.inputName')" clearable />
        </el-form-item>
        <el-form-item :label="$t('equipment.status')">
          <el-select v-model="query.status" :placeholder="$t('common.all')" clearable>
            <el-option :label="$t('equipment.running')" value="running" />
            <el-option :label="$t('equipment.stopped')" value="stopped" />
            <el-option :label="$t('equipment.maintenance')" value="maintenance" />
            <el-option :label="$t('equipment.scrapped')" value="scrapped" />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('equipment.type')">
          <el-select v-model="query.type" :placeholder="$t('common.all')" clearable>
            <el-option :label="$t('equipment.cnc')" value="数控机床" />
            <el-option :label="$t('equipment.robot')" value="工业机器人" />
            <el-option :label="$t('equipment.conveyor')" value="输送设备" />
            <el-option :label="$t('equipment.plc')" value="PLC控制器" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">{{ $t('common.search') }}</el-button>
          <el-button @click="resetQuery">{{ $t('common.reset') }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <el-card shadow="never">
      <div style="display:flex;justify-content:space-between;margin-bottom:16px">
        <div>
          <el-button type="primary" @click="$router.push({ name: 'equipment-form' })">
            <el-icon><Plus /></el-icon> {{ $t('equipment.addEquipment') }}
          </el-button>
          <el-button :disabled="!selectedRows.length" @click="handleBatchDelete">{{ $t('common.batchDelete') }}</el-button>
          <el-button @click="handleExport">{{ $t('common.export') }}</el-button>
        </div>
        <div style="color:#999;font-size:13px;line-height:32px">{{ $t('common.total', { count: total }) }}</div>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" @selection-change="handleSelectionChange" stripe v-loading="loading">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="code" :label="$t('equipment.code')" width="130" />
        <el-table-column prop="name" :label="$t('equipment.name')" min-width="150" />
        <el-table-column prop="type" :label="$t('equipment.type')" width="120" />
        <el-table-column prop="model" :label="$t('equipment.model')" width="140" />
        <el-table-column prop="location" :label="$t('equipment.location')" width="130" />
        <el-table-column prop="status" :label="$t('equipment.status')" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type" size="small">{{ statusLabel[row.status] || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="healthScore" :label="$t('equipment.healthScore')" width="100">
          <template #default="{ row }">
            <el-progress v-if="row.healthScore != null" :percentage="row.healthScore" :color="row.healthScore > 80 ? '#52c41a' : row.healthScore > 60 ? '#faad14' : '#ff4d4f'" :stroke-width="6" />
            <span v-else style="color:#999">--</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('common.operation')" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push({ name: 'equipment-detail', params: { id: row.id } })">{{ $t('common.detail') }}</el-button>
            <el-button link type="primary" @click="$router.push({ name: 'equipment-form', params: { id: row.id } })">{{ $t('common.edit') }}</el-button>
            <el-popconfirm :title="$t('equipment.deleteConfirm')" @confirm="handleDelete(row)">
              <template #reference><el-button link type="danger">{{ $t('common.delete') }}</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top:16px;justify-content:flex-end"
        @change="loadData"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { getEquipmentList, deleteEquipment } from '../../api/equipment'

const { t } = useI18n()

const statusMap = {
  running: { type: 'success' },
  stopped: { type: 'info' },
  maintenance: { type: 'warning' },
  scrapped: { type: 'danger' }
}

const statusLabel = {
  running: '运行中',
  stopped: '停机',
  maintenance: '维修中',
  scrapped: '已报废'
}

const query = reactive({ name: '', status: '', type: '', page: 1, pageSize: 10 })
const tableData = ref([])
const total = ref(0)
const selectedRows = ref([])
const loading = ref(false)

async function loadData() {
  loading.value = true
  try {
    const res = await getEquipmentList({
      page: query.page,
      pageSize: query.pageSize,
      keyword: query.name || undefined,
      status: query.status || undefined,
      type: query.type || undefined
    })
    if (res.code === 200 && res.data) {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch {
    ElMessage.error('加载设备列表失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() { query.page = 1; loadData() }
function resetQuery() { Object.assign(query, { name: '', status: '', type: '', page: 1 }); loadData() }
function handleSelectionChange(rows) { selectedRows.value = rows }

async function handleDelete(row) {
  try {
    await deleteEquipment(row.id)
    ElMessage.success(t('message.deleted', { name: row.name }))
    loadData()
  } catch {
    ElMessage.error('删除失败')
  }
}

async function handleBatchDelete() {
  try {
    for (const row of selectedRows.value) {
      await deleteEquipment(row.id)
    }
    ElMessage.success(t('message.batchDeleted', { count: selectedRows.value.length }))
    loadData()
  } catch {
    ElMessage.error('批量删除失败')
  }
}

function handleExport() { ElMessage.success(t('message.exportSuccess')) }

onMounted(loadData)
</script>
