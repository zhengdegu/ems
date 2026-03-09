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
            <el-option :label="$t('equipment.cnc')" value="cnc" />
            <el-option :label="$t('equipment.robot')" value="robot" />
            <el-option :label="$t('equipment.conveyor')" value="conveyor" />
            <el-option :label="$t('equipment.plc')" value="plc" />
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
      <el-table :data="tableData" @selection-change="handleSelectionChange" stripe>
        <el-table-column type="selection" width="50" />
        <el-table-column prop="code" :label="$t('equipment.code')" width="130" />
        <el-table-column prop="name" :label="$t('equipment.name')" min-width="150" />
        <el-table-column prop="type" :label="$t('equipment.type')" width="120">
          <template #default="{ row }">{{ $t(`equipment.${typeMap[row.type]}`) }}</template>
        </el-table-column>
        <el-table-column prop="model" :label="$t('equipment.model')" width="140" />
        <el-table-column prop="location" :label="$t('equipment.location')" width="130" />
        <el-table-column prop="status" :label="$t('equipment.status')" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type" size="small">{{ $t(`equipment.${row.status}`) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="healthScore" :label="$t('equipment.healthScore')" width="100">
          <template #default="{ row }">
            <el-progress :percentage="row.healthScore" :color="row.healthScore > 80 ? '#52c41a' : row.healthScore > 60 ? '#faad14' : '#ff4d4f'" :stroke-width="6" />
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

const { t } = useI18n()

const statusMap = {
  running: { type: 'success' },
  stopped: { type: 'info' },
  maintenance: { type: 'warning' },
  scrapped: { type: 'danger' }
}

const typeMap = {
  '数控机床': 'cnc',
  '工业机器人': 'robot',
  '输送设备': 'conveyor',
  'PLC控制器': 'plc'
}

const query = reactive({ name: '', status: '', type: '', page: 1, pageSize: 10 })
const tableData = ref([])
const total = ref(0)
const selectedRows = ref([])

const mockData = [
  { id: 1, code: 'CNC-001', name: '五轴数控加工中心', type: '数控机床', model: 'DMG MORI DMU 50', location: 'A区-生产线', status: 'running', healthScore: 92 },
  { id: 2, code: 'CNC-002', name: '数控车床', type: '数控机床', model: 'MAZAK QT-250', location: 'A区-生产线', status: 'running', healthScore: 88 },
  { id: 3, code: 'ROBOT-001', name: '焊接机器人', type: '工业机器人', model: 'FANUC R-2000iC', location: 'B区-装配线', status: 'running', healthScore: 95 },
  { id: 4, code: 'ROBOT-005', name: '搬运机器人', type: '工业机器人', model: 'KUKA KR 210', location: 'C区-仓储', status: 'maintenance', healthScore: 45 },
  { id: 5, code: 'PLC-003', name: 'PLC控制器', type: 'PLC控制器', model: 'Siemens S7-1500', location: 'A区-生产线', status: 'stopped', healthScore: 60 },
  { id: 6, code: 'CONV-008', name: '皮带输送机', type: '输送设备', model: 'Hytrol TA', location: 'C区-仓储', status: 'running', healthScore: 72 },
  { id: 7, code: 'CNC-003', name: '龙门加工中心', type: '数控机床', model: 'Haas VF-6SS', location: 'A区-生产线', status: 'running', healthScore: 85 },
  { id: 8, code: 'PUMP-012', name: '液压泵站', type: '输送设备', model: 'Parker PV270', location: 'D区-检测', status: 'running', healthScore: 68 },
]

function loadData() {
  let filtered = [...mockData]
  if (query.name) filtered = filtered.filter(d => d.name.includes(query.name))
  if (query.status) filtered = filtered.filter(d => d.status === query.status)
  if (query.type) {
    const typeKey = Object.keys(typeMap).find(k => typeMap[k] === query.type)
    if (typeKey) filtered = filtered.filter(d => d.type === typeKey)
  }
  total.value = filtered.length
  const start = (query.page - 1) * query.pageSize
  tableData.value = filtered.slice(start, start + query.pageSize)
}

function handleSearch() { query.page = 1; loadData() }
function resetQuery() { Object.assign(query, { name: '', status: '', type: '', page: 1 }); loadData() }
function handleSelectionChange(rows) { selectedRows.value = rows }
function handleDelete(row) { ElMessage.success(t('message.deleted', { name: row.name })); loadData() }
function handleBatchDelete() { ElMessage.success(t('message.batchDeleted', { count: selectedRows.value.length })); loadData() }
function handleExport() { ElMessage.success(t('message.exportSuccess')) }

onMounted(loadData)
</script>
