<template>
  <div>
    <el-row :gutter="16">
      <!-- 角色列表 -->
      <el-col :span="10">
        <el-card shadow="never">
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center">
              <span style="font-weight:600">角色列表</span>
              <el-button type="primary" size="small" @click="showRoleDialog = true"><el-icon><Plus /></el-icon> 新增</el-button>
            </div>
          </template>
          <el-table :data="roles" highlight-current-row @current-change="handleRoleChange" stripe>
            <el-table-column prop="name" label="角色名称" />
            <el-table-column prop="code" label="角色编码" width="130" />
            <el-table-column prop="userCount" label="用户数" width="80" />
            <el-table-column label="操作" width="100">
              <template #default>
                <el-button link type="primary" size="small">编辑</el-button>
                <el-button link type="danger" size="small">删除</el-button>
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

    <el-dialog v-model="showRoleDialog" title="新增角色" width="400px">
      <el-form :model="roleForm" label-width="80px">
        <el-form-item label="角色名称"><el-input v-model="roleForm.name" /></el-form-item>
        <el-form-item label="角色编码"><el-input v-model="roleForm.code" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="roleForm.desc" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRoleDialog = false">取消</el-button>
        <el-button type="primary" @click="showRoleDialog = false">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const treeRef = ref()
const showRoleDialog = ref(false)
const currentRole = ref(null)
const checkedKeys = ref([])
const roleForm = reactive({ name: '', code: '', desc: '' })

const roles = ref([
  { id: 1, name: '管理员', code: 'admin', userCount: 2 },
  { id: 2, name: '设备主管', code: 'supervisor', userCount: 3 },
  { id: 3, name: '维修工程师', code: 'engineer', userCount: 8 },
  { id: 4, name: '操作员', code: 'operator', userCount: 15 },
])

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

const rolePermissions = {
  1: ['dashboard:view', 'equipment:view', 'equipment:create', 'equipment:edit', 'equipment:delete', 'equipment:export', 'maintenance:plan', 'maintenance:order', 'maintenance:create', 'alarm:view', 'alarm:handle', 'report:view', 'report:export', 'system:user', 'system:role', 'system:settings'],
  2: ['dashboard:view', 'equipment:view', 'equipment:create', 'equipment:edit', 'equipment:export', 'maintenance:plan', 'maintenance:order', 'maintenance:create', 'alarm:view', 'alarm:handle', 'report:view', 'report:export'],
  3: ['dashboard:view', 'equipment:view', 'maintenance:order', 'maintenance:create', 'alarm:view', 'alarm:handle'],
  4: ['dashboard:view', 'equipment:view', 'alarm:view']
}

function handleRoleChange(role) {
  currentRole.value = role
  checkedKeys.value = rolePermissions[role?.id] || []
}

function savePermissions() {
  ElMessage.success('权限保存成功')
}
</script>
