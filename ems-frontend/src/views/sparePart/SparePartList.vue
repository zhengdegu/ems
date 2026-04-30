<template>
  <div>
    <!-- 搜索栏 -->
    <el-card shadow="never" style="margin-bottom:16px">
      <el-form :inline="true" :model="query">
        <el-form-item label="备件名称">
          <el-input v-model="query.keyword" placeholder="搜索备件名称/编码" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.type" placeholder="全部" clearable>
            <el-option label="机械件" value="机械件" />
            <el-option label="电气件" value="电气件" />
            <el-option label="液压件" value="液压件" />
            <el-option label="耗材" value="耗材" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="Object.assign(query, { keyword: '', type: '' }); handleSearch()">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 库存预警 -->
    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :span="8">
        <div class="stat-card" style="border-left:4px solid #1890FF">
          <div style="color:#999;font-size:13px">备件总数</div>
          <div style="font-size:28px;font-weight:700;color:#1890FF;margin-top:8px">{{ total }}</div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card" style="border-left:4px solid #52c41a">
          <div style="color:#999;font-size:13px">库存充足</div>
          <div style="font-size:28px;font-weight:700;color:#52c41a;margin-top:8px">{{ stockNormal }}</div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card" style="border-left:4px solid #ff4d4f">
          <div style="color:#999;font-size:13px">库存不足</div>
          <div style="font-size:28px;font-weight:700;color:#ff4d4f;margin-top:8px">{{ stockLow }}</div>
        </div>
      </el-col>
    </el-row>

    <el-card shadow="never">
      <div style="display:flex;justify-content:space-between;margin-bottom:16px">
        <div>
          <el-button type="primary" @click="openCreateDialog"><el-icon><Plus /></el-icon> 新增备件</el-button>
        </div>
        <span style="color:#999;font-size:13px;line-height:32px">共 {{ total }} 条记录</span>
      </div>

      <el-table :data="spareparts" stripe v-loading="loading">
        <el-table-column prop="code" label="备件编码" width="120" />
        <el-table-column prop="name" label="备件名称" min-width="150" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ row.type || row.category || '--' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="specification" label="规格型号" width="140" />
        <el-table-column prop="unit" label="单位" width="70" />
        <el-table-column prop="stock" label="库存" width="80">
          <template #default="{ row }">
            <span :style="{ color: row.stock <= row.minStock ? '#ff4d4f' : '#333', fontWeight: row.stock <= row.minStock ? '700' : '400' }">
              {{ row.stock }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="minStock" label="最低库存" width="90" />
        <el-table-column prop="price" label="单价(¥)" width="100" />
        <el-table-column prop="location" label="存放位置" width="120" />
        <el-table-column prop="supplier" label="供应商" width="120" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleInbound(row)">入库</el-button>
            <el-button link type="primary" @click="handleOutbound(row)">出库</el-button>
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除该备件？" @confirm="handleDelete(row)">
              <template #reference><el-button link type="danger">删除</el-button></template>
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

    <!-- 新增/编辑备件弹窗 -->
    <el-dialog v-model="showDialog" :title="editingItem ? '编辑备件' : '新增备件'" width="600px">
      <el-form :model="spareForm" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="备件编码"><el-input v-model="spareForm.code" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备件名称"><el-input v-model="spareForm.name" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="类型">
              <el-select v-model="spareForm.type" style="width:100%">
                <el-option label="机械件" value="机械件" />
                <el-option label="电气件" value="电气件" />
                <el-option label="液压件" value="液压件" />
                <el-option label="耗材" value="耗材" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格型号"><el-input v-model="spareForm.specification" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="单位"><el-input v-model="spareForm.unit" placeholder="如：个、套、升" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单价">
              <el-input-number v-model="spareForm.price" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="最低库存">
              <el-input-number v-model="spareForm.minStock" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="存放位置"><el-input v-model="spareForm.location" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="供应商"><el-input v-model="spareForm.supplier" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 入库/出库弹窗 -->
    <el-dialog v-model="showStockDialog" :title="stockAction === 'inbound' ? '备件入库' : '备件出库'" width="400px">
      <el-form :model="stockForm" label-width="80px">
        <el-form-item label="备件">{{ stockTarget?.name }} ({{ stockTarget?.code }})</el-form-item>
        <el-form-item label="当前库存">{{ stockTarget?.stock }} {{ stockTarget?.unit }}</el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="stockForm.quantity" :min="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="stockForm.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showStockDialog = false">取消</el-button>
        <el-button type="primary" :loading="stockSubmitting" @click="handleStockSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSparePartList, createSparePart, updateSparePart, deleteSparePart, sparePartInbound, sparePartOutbound } from '../../api/sparePart'

const query = reactive({ keyword: '', type: '', page: 1, pageSize: 10 })
const spareparts = ref([])
const total = ref(0)
const loading = ref(false)
const submitting = ref(false)
const showDialog = ref(false)
const editingItem = ref(null)
const spareForm = reactive({ code: '', name: '', type: '', specification: '', unit: '', price: 0, minStock: 0, location: '', supplier: '' })

const showStockDialog = ref(false)
const stockAction = ref('inbound')
const stockTarget = ref(null)
const stockForm = reactive({ quantity: 1, remark: '' })
const stockSubmitting = ref(false)

const stockNormal = computed(() => spareparts.value.filter(s => s.stock > s.minStock).length)
const stockLow = computed(() => spareparts.value.filter(s => s.stock <= s.minStock).length)

async function loadData() {
  loading.value = true
  try {
    const res = await getSparePartList({
      page: query.page,
      pageSize: query.pageSize,
      keyword: query.keyword || undefined,
      type: query.type || undefined
    })
    if (res.code === 200 && res.data) {
      spareparts.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch {
    ElMessage.error('加载备件列表失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() { query.page = 1; loadData() }

function openCreateDialog() {
  editingItem.value = null
  Object.assign(spareForm, { code: '', name: '', type: '', specification: '', unit: '', price: 0, minStock: 0, location: '', supplier: '' })
  showDialog.value = true
}

function handleEdit(row) {
  editingItem.value = row
  Object.assign(spareForm, { code: row.code, name: row.name, type: row.type || row.category, specification: row.specification, unit: row.unit, price: row.price, minStock: row.minStock, location: row.location, supplier: row.supplier })
  showDialog.value = true
}

async function handleSubmit() {
  submitting.value = true
  try {
    if (editingItem.value) {
      const res = await updateSparePart(editingItem.value.id, spareForm)
      if (res.code === 200) ElMessage.success('修改成功')
      else ElMessage.error(res.message || '修改失败')
    } else {
      const res = await createSparePart(spareForm)
      if (res.code === 200) ElMessage.success('创建成功')
      else ElMessage.error(res.message || '创建失败')
    }
    showDialog.value = false
    loadData()
  } catch {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row) {
  try {
    await deleteSparePart(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    ElMessage.error('删除失败')
  }
}

function handleInbound(row) {
  stockAction.value = 'inbound'
  stockTarget.value = row
  Object.assign(stockForm, { quantity: 1, remark: '' })
  showStockDialog.value = true
}

function handleOutbound(row) {
  stockAction.value = 'outbound'
  stockTarget.value = row
  Object.assign(stockForm, { quantity: 1, remark: '' })
  showStockDialog.value = true
}

async function handleStockSubmit() {
  stockSubmitting.value = true
  try {
    const fn = stockAction.value === 'inbound' ? sparePartInbound : sparePartOutbound
    const res = await fn(stockTarget.value.id, stockForm)
    if (res.code === 200) {
      ElMessage.success(stockAction.value === 'inbound' ? '入库成功' : '出库成功')
      showStockDialog.value = false
      loadData()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch {
    ElMessage.error('操作失败')
  } finally {
    stockSubmitting.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.stat-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}
</style>
