<template>
  <div>
    <el-row :gutter="16">
      <!-- 角色列表 -->
      <el-col :span="10">
        <el-card shadow="never">
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center">
              <span style="font-weight:600">角色列表</span>
              <el-button type="primary" size="small" @click="openRoleDialog"><el-icon><Plus /></el-icon> 新增</el-button>
            </div>
          </template>
          <el-table :data="roles" highlight-current-row @current-change="handleRoleChange" stripe v-loading="loading">
            <el-table-column prop="name" label="角色名称" />
            <el-table-column prop="code" label="角色编码" width="130" />
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click.stop="handleEditRole(row)">编辑</el-button>
                <el-popconfirm title="确定删除该角色？" @confirm="handleDeleteRole(row)">
                  <template #reference><el-button link type="danger" size="small">删除</el-button></template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 权限配置 -->
      <el-col :span="14">
        <el-card shadow="never">
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center">
              <span style="font-weight:600">权限配置 - {{ currentRole?.name || '请选择角色' }}</span>
              <el-button type="primary" size="small" :disabled="!currentRole" @click="savePermissions">保存权限</el-button>
            </div>
          </template>
          <el-tree
            ref="treeRef"
            :data="permissionTree"
            show-checkbox
            node-key="id"
            :default-checked-keys="checkedKeys"
            :props="{ label: 'label', children: 'children' }"
          />
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="showRoleDialog" :title="editingRole ? '编辑角色' : '新增角色'" width="400px">
      <el-form :model="roleForm" label-width="80px">
        <el-form-item label="角色名称"><el-input v-model="roleForm.name" /></el-form-item>
        <el-form-item label="角色编码"><el-input v-model="roleForm.code" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="roleForm.description" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRoleDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleRoleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getRoleList, createRole, updateRole, deleteRole } from '../../api/user'

const treeRef = ref()
const showRoleDialog = ref(false)
const currentRole = ref(null)
const checkedKeys = ref([])
const loading = ref(false)
const submitting = ref(false)
const editingRole = ref(null)
const roleForm = reactive({ name: '', code: '', description: '' })
const roles = ref([])

const permissionTree = [
  { id: 'dashboard', label: '工作台', children: [
    { id: 'dashboard:view', label: '查看' }
  ]},
  { id: 'equipment', label: '设备管理', children: [
    { id: 'equipment:view', label: '查看' },
    { id: 'equipment:create', label: '新增' },
    { id: 'equipment:edit', label: '编辑' },
    { id: 'equipment:delete', label: '删除' },
    { id: 'equipment:export', label: '导出' }
  ]},
  { id: 'maintenance', label: '维护管理', children: [
    { id: 'maintenance:plan', label: '维护计划' },
    { id: 'maintenance:order', label: '工单管理' },
    { id: 'maintenance:create', label: '创建工单' }
  ]},
  { id: 'alarm', label: '告警中心', children: [
    { id: 'alarm:view', label: '查看' },
    { id: 'alarm:handle', label: '处理' }
  ]},
  { id: 'report', label: '报表统计', children: [
    { id: 'report:view', label: '查看' },
    { id: 'report:export', label: '导出' }
  ]},
  { id: 'system', label: '系统管理', children: [
    { id: 'system:user', label: '用户管理' },
    { id: 'system:role', label: '角色权限' },
    { id: 'system:settings', label: '系统设置' }
  ]}
]

async function loadRoles() {
  loading.value = true
  try {
    const res = await getRoleList({ page: 1, size: 100 })
    if (res.code === 200 && res.data) {
      roles.value = res.data.records || res.data || []
    }
  } catch {
    ElMessage.error('加载角色列表失败')
  } finally {
    loading.value = false
  }
}

function handleRoleChange(role) {
  currentRole.value = role
  if (role?.permissions) {
    try {
      checkedKeys.value = JSON.parse(role.permissions)
    } catch {
      checkedKeys.value = []
    }
  } else {
    checkedKeys.value = []
  }
}

function openRoleDialog() {
  editingRole.value = null
  Object.assign(roleForm, { name: '', code: '', description: '' })
  showRoleDialog.value = true
}

function handleEditRole(row) {
  editingRole.value = row
  Object.assign(roleForm, { name: row.name, code: row.code, description: row.description })
  showRoleDialog.value = true
}

async function handleRoleSubmit() {
  submitting.value = true
  try {
    if (editingRole.value) {
      const res = await updateRole(editingRole.value.id, roleForm)
      if (res.code === 200) ElMessage.success('修改成功')
      else ElMessage.error(res.message || '修改失败')
    } else {
      const res = await createRole(roleForm)
      if (res.code === 200) ElMessage.success('创建成功')
      else ElMessage.error(res.message || '创建失败')
    }
    showRoleDialog.value = false
    loadRoles()
  } catch {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

async function handleDeleteRole(row) {
  try {
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    if (currentRole.value?.id === row.id) {
      currentRole.value = null
      checkedKeys.value = []
    }
    loadRoles()
  } catch {
    ElMessage.error('删除失败')
  }
}

async function savePermissions() {
  const checked = treeRef.value.getCheckedKeys(false)
  try {
    const res = await updateRole(currentRole.value.id, { permissions: JSON.stringify(checked) })
    if (res.code === 200) {
      ElMessage.success('权限保存成功')
      loadRoles()
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch {
    ElMessage.error('保存失败')
  }
}

onMounted(loadRoles)
</script>
