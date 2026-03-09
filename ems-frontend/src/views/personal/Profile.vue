<template>
  <div>
    <el-row :gutter="16">
      <el-col :span="8">
        <el-card shadow="never">
          <div style="text-align:center;padding:20px 0">
            <el-avatar :size="80" style="background:#1890FF;font-size:32px">
              {{ userInfo.name?.charAt(0) || 'U' }}
            </el-avatar>
            <h3 style="margin:16px 0 4px">{{ userInfo.name }}</h3>
            <p style="color:#999;font-size:13px">{{ userInfo.role }}</p>
            <p style="color:#999;font-size:12px">{{ userInfo.department }}</p>
          </div>
          <el-divider />
          <div style="padding:0 16px">
            <div style="display:flex;justify-content:space-between;padding:8px 0;font-size:13px">
              <span style="color:#999">用户名</span><span>{{ userInfo.username }}</span>
            </div>
            <div style="display:flex;justify-content:space-between;padding:8px 0;font-size:13px">
              <span style="color:#999">手机号</span><span>{{ userInfo.phone }}</span>
            </div>
            <div style="display:flex;justify-content:space-between;padding:8px 0;font-size:13px">
              <span style="color:#999">邮箱</span><span>{{ userInfo.email }}</span>
            </div>
            <div style="display:flex;justify-content:space-between;padding:8px 0;font-size:13px">
              <span style="color:#999">注册时间</span><span>{{ userInfo.createTime }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card shadow="never">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本资料" name="info">
              <el-form :model="infoForm" label-width="80px" style="max-width:500px">
                <el-form-item label="姓名"><el-input v-model="infoForm.name" /></el-form-item>
                <el-form-item label="手机号"><el-input v-model="infoForm.phone" /></el-form-item>
                <el-form-item label="邮箱"><el-input v-model="infoForm.email" /></el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="saveInfo">保存修改</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <el-tab-pane label="安全设置" name="security">
              <el-form :model="pwdForm" label-width="100px" style="max-width:500px">
                <el-form-item label="当前密码"><el-input v-model="pwdForm.oldPwd" type="password" show-password /></el-form-item>
                <el-form-item label="新密码"><el-input v-model="pwdForm.newPwd" type="password" show-password /></el-form-item>
                <el-form-item label="确认新密码"><el-input v-model="pwdForm.confirmPwd" type="password" show-password /></el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="changePwd">修改密码</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <el-tab-pane label="登录记录" name="loginLog">
              <el-table :data="loginLogs" stripe size="small">
                <el-table-column prop="time" label="登录时间" width="180" />
                <el-table-column prop="ip" label="IP地址" width="140" />
                <el-table-column prop="location" label="登录地点" width="140" />
              <el-table-column prop="device" label="设备" />
                <el-table-column prop="status" label="状态" width="80">
                  <template #default="{ row }">
                    <el-tag :type="row.status === '成功' ? 'success' : 'danger'" size="small">{{ row.status }}</el-tag>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('info')

const userInfo = reactive({
  username: 'admin', name: '管理员', role: '系统管理员', department: '设备部',
  phone: '138****1234', email: 'admin@ems.com', createTime: '2023-01-01'
})

const infoForm = reactive({ name: userInfo.name, phone: userInfo.phone, email: userInfo.email })
const pwdForm = reactive({ oldPwd: '', newPwd: '', confirmPwd: '' })

const loginLogs = [
  { time: '2024-03-08 09:00', ip: '192.168.1.100', location: '本地', device: 'Chrome / Windows', status: '成功' },
  { time: '2024-03-07 08:30', ip: '192.168.1.100', location: '本地', device: 'Chrome / Windows', status: '成功' },
  { time: '2024-03-06 09:15', ip: '10.0.0.50', location: '本地', device: 'Firefox / macOS', status: '成功' },
  { time: '2024-03-05 22:00', ip: '203.0.113.45', location: '未知', device: 'Chrome / Android', status: '失败' },
]

function saveInfo() { ElMessage.success('资料已更新') }
function changePwd() { ElMessage.success('密码已修改') }
</script>
