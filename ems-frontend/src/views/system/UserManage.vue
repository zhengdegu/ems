<template>
  <div>
    <el-card shadow="never">
      <div style="display:flex;justify-content:space-between;margin-bottom:16px">
        <div>
          <el-button type="primary" @click="openCreateDialog"><el-icon><Plus /></el-icon> 新增用户</el-button>
        </div>
        <el-input v-model="searchKey" placeholder="搜索用户" prefix-icon="Search" style="width:240px" clearable @change="loadData" />
      </div>

      <el-table :data="users" stripe v-loading="loading">
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ row.role }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="enabled" label="状态" width="90">
          <template #default="{ row }">
            <el-switch :model-value="row.enabled === 1" size="small" @change="handleToggle(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确定重置密码为123456？" @confirm="handleResetPwd(row)">
              <template #reference><el-button link type="primary">重置密码</el-button></template>
            </el-popconfirm>
            <el-popconfirm title="确定删除该用户？" @confirm="handleDelete(row)">
              <template #reference><el-button link type="danger">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top:16px;justify-content:flex-end"
        @change="loadData"
      />
    </el-card>

    <el-dialog v-model="showDialog" :title="editingUser ? '编辑用户' : '新增用户'" width="500px">
      <el-form :model="userForm" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="userForm.username" :disabled="!!editingUser" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="userForm.name" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="userForm.role" style="width:100%">
            <el-option label="管理员" value="admin" />
            <el-option label="设备主管" value="supervisor" />
            <el-option label="维修工程师" value="engineer" />
            <el-option label="操作员" value="operator" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号"><el-input v-model="userForm.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="userForm.email" /></el-form-item>
        <el-form-item v-if="!editingUser" label="密码"><el-input v-model="userForm.password" type="password" placeholder="默认密码: 123456" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserList, createUser, updateUser, deleteUser, resetPassword } from '../../api/user'

const searchKey = ref('')
const showDialog = ref(false)
const loading = ref(false)
const submitting = ref(false)
const editingUser = ref(null)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const userForm = reactive({ username: '', name: '', role: '', phone: '', email: '', password: '123456' })
const users = ref([])

async function loadData() {
  loading.value = true
  try {
    const res = await getUserList({ page: page.value, pageSize: pageSize.value, keyword: searchKey.value || undefined })
    if (res.code === 200 && res.data) {
      users.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch {
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

function openCreateDialog() {
  editingUser.value = null
  Object.assign(userForm, { username: '', name: '', role: '', phone: '', email: '', password: '123456' })
  showDialog.value = true
}

function handleEdit(row) {
  editingUser.value = row
  Object.assign(userForm, { username: row.username, name: row.name, role: row.role, phone: row.phone, email: row.email })
  showDialog.value = true
}

async function handleSubmit() {
  submitting.value = true
  try {
    if (editingUser.value) {
      const res = await updateUser(editingUser.value.id, userForm)
      if (res.code === 200) {
        ElMessage.success('修改成功')
      } else {
        ElMessage.error(res.message || '修改失败')
      }
    } else {
      const res = await createUser(userForm)
      if (res.code === 200) {
        ElMessage.success('创建成功')
      } else {
        ElMessage.error(res.message || '创建失败')
      }
    }
    showDialog.value = false
    loadData()
  } catch {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

async function handleToggle(row) {
  try {
    const res = await updateUser(row.id, { enabled: row.enabled === 1 ? 0 : 1 })
    if (res.code === 200) {
      ElMessage.success('状态已更新')
      loadData()
    }
  } catch {
    ElMessage.error('操作失败')
  }
}

async function handleResetPwd(row) {
  try {
    const res = await resetPassword(row.id)
    if (res.code === 200) {
      ElMessage.success('密码已重置为 123456')
    }
  } catch {
    ElMessage.error('重置失败')
  }
}

async function handleDelete(row) {
  try {
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    ElMessage.error('删除失败')
  }
}

onMounted(loadData)
</script>
