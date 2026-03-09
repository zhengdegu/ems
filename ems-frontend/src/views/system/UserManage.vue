<template>
  <div>
    <el-card shadow="never">
      <div style="display:flex;justify-content:space-between;margin-bottom:16px">
        <div>
          <el-button type="primary" @click="showDialog = true"><el-icon><Plus /></el-icon> 新增用户</el-button>
        </div>
        <el-input v-model="searchKey" placeholder="搜索用户" prefix-icon="Search" style="width:240px" clearable />
      </div>

      <el-table :data="filteredUsers" stripe>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="department" label="部门" width="120" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ row.role }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-switch v-model="row.enabled" size="small" />
          </template>
        </el-table-column>
        <el-table-column prop="lastLogin" label="最后登录" width="160" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default>
            <el-button link type="primary">编辑</el-button>
            <el-button link type="primary">重置密码</el-button>
            <el-button link type="danger">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showDialog" title="新增用户" width="500px">
      <el-form :model="userForm" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="userForm.username" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="userForm.name" /></el-form-item>
        <el-form-item label="部门">
          <el-select v-model="userForm.department" style="width:100%">
            <el-option label="设备部" value="设备部" />
            <el-option label="生产部" value="生产部" />
            <el-option label="质量部" value="质量部" />
            <el-option label="IT部" value="IT部" />
          </el-select>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="userForm.role" style="width:100%">
            <el-option label="管理员" value="管理员" />
            <el-option label="设备主管" value="设备主管" />
            <el-option label="维修工程师" value="维修工程师" />
            <el-option label="操作员" value="操作员" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号"><el-input v-model="userForm.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="userForm.email" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="showDialog = false">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'

const searchKey = ref('')
const showDialog = ref(false)
const userForm = reactive({ username: '', name: '', department: '', role: '', phone: '', email: '' })

const users = ref([
  { username: 'admin', name: '管理员', department: '设备部', role: '管理员', phone: '138****1234', email: 'admin@ems.com', enabled: true, lastLogin: '2024-03-08 09:00' },
  { username: 'zhangsan', name: '张三', department: '设备部', role: '设备主管', phone: '139****5678', email: 'zhang@ems.com', enabled: true, lastLogin: '2024-03-08 08:30' },
  { username: 'lisi', name: '李四', department: '设备部', role: '维修工程师', phone: '137****9012', email: 'li@ems.com', enabled: true, lastLogin: '2024-03-07 17:00' },
  { username: 'wangwu', name: '王五', department: '生产部', role: '操作员', phone: '136****3456', email: 'wang@ems.com', enabled: true, lastLogin: '2024-03-07 16:30' },
  { username: 'zhaoliu', name: '赵六', department: '质量部', role: '操作员', phone: '135****7890', email: 'zhao@ems.com', enabled: false, lastLogin: '2024-02-28 10:00' },
])

const filteredUsers = computed(() => {
  if (!searchKey.value) return users.value
  return users.value.filter(u => u.name.includes(searchKey.value) || u.username.includes(searchKey.value))
})
</script>
