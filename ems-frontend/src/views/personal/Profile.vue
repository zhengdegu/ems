<template>
  <div>
    <el-row :gutter="16">
      <el-col :span="8">
        <el-card shadow="never">
          <div style="text-align:center;padding:20px 0">
            <el-avatar :size="80" style="background:#1890FF;font-size:32px">
              {{ userStore.userInfo.name?.charAt(0) || 'U' }}
            </el-avatar>
            <h3 style="margin:16px 0 4px">{{ userStore.userInfo.name }}</h3>
            <p style="color:#999;font-size:13px">{{ userStore.userInfo.role }}</p>
          </div>
          <el-divider />
          <div style="padding:0 16px">
            <div style="display:flex;justify-content:space-between;padding:8px 0;font-size:13px">
              <span style="color:#999">用户名</span><span>{{ userStore.userInfo.username }}</span>
            </div>
            <div style="display:flex;justify-content:space-between;padding:8px 0;font-size:13px">
              <span style="color:#999">手机号</span><span>{{ userStore.userInfo.phone || '--' }}</span>
            </div>
            <div style="display:flex;justify-content:space-between;padding:8px 0;font-size:13px">
              <span style="color:#999">邮箱</span><span>{{ userStore.userInfo.email || '--' }}</span>
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
                  <el-button type="primary" :loading="saving" @click="saveInfo">保存修改</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <el-tab-pane label="安全设置" name="security">
              <el-form :model="pwdForm" label-width="100px" style="max-width:500px">
                <el-form-item label="当前密码"><el-input v-model="pwdForm.oldPassword" type="password" show-password /></el-form-item>
                <el-form-item label="新密码"><el-input v-model="pwdForm.newPassword" type="password" show-password /></el-form-item>
                <el-form-item label="确认新密码"><el-input v-model="pwdForm.confirmPassword" type="password" show-password /></el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="changingPwd" @click="changePwd">修改密码</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <el-tab-pane label="登录记录" name="loginLog">
              <el-table :data="loginLogs" stripe size="small" v-loading="logLoading">
                <el-table-column prop="createTime" label="登录时间" width="180" />
                <el-table-column prop="ip" label="IP地址" width="140" />
                <el-table-column prop="userAgent" label="设备" />
                <el-table-column prop="status" label="状态" width="80">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 'success' ? 'success' : 'danger'" size="small">{{ row.status === 'success' ? '成功' : '失败' }}</el-tag>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../stores/user'
import { updateProfile, changePassword } from '../../api/user'
import { getLoginLog } from '../../api/system'

const userStore = useUserStore()
const activeTab = ref('info')
const saving = ref(false)
const changingPwd = ref(false)
const logLoading = ref(false)

const infoForm = reactive({
  name: userStore.userInfo.name || '',
  phone: userStore.userInfo.phone || '',
  email: userStore.userInfo.email || ''
})

const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const loginLogs = ref([])

async function saveInfo() {
  saving.value = true
  try {
    const userId = userStore.userInfo.id
    const res = await updateProfile(userId, infoForm)
    if (res.code === 200) {
      ElMessage.success('资料已更新')
      const updated = { ...userStore.userInfo, ...infoForm }
      userStore.userInfo = updated
      localStorage.setItem('ems_user', JSON.stringify(updated))
    } else {
      ElMessage.error(res.message || '更新失败')
    }
  } catch {
    ElMessage.error('更新失败')
  } finally {
    saving.value = false
  }
}

async function changePwd() {
  if (pwdForm.newPassword !== pwdForm.confirmPassword) {
    ElMessage.error('两次密码不一致')
    return
  }
  if (pwdForm.newPassword.length < 6) {
    ElMessage.error('密码至少6位')
    return
  }
  changingPwd.value = true
  try {
    const userId = userStore.userInfo.id
    const res = await changePassword(userId, {
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    if (res.code === 200) {
      ElMessage.success('密码已修改')
      Object.assign(pwdForm, { oldPassword: '', newPassword: '', confirmPassword: '' })
    } else {
      ElMessage.error(res.message || '修改失败')
    }
  } catch {
    ElMessage.error('修改失败')
  } finally {
    changingPwd.value = false
  }
}

async function loadLoginLogs() {
  logLoading.value = true
  try {
    const res = await getLoginLog({ page: 1, pageSize: 20 })
    if (res.code === 200 && res.data) {
      loginLogs.value = res.data.records || []
    }
  } catch {
    // 静默失败
  } finally {
    logLoading.value = false
  }
}

onMounted(loadLoginLogs)
</script>
