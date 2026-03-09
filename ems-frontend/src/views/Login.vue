<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <el-icon :size="36" color="#1890FF"><Monitor /></el-icon>
        <h2>设备管理系统</h2>
        <p>Equipment Management System</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <div class="login-options">
            <el-checkbox v-model="form.remember">记住我</el-checkbox>
            <el-link type="primary" :underline="false">忘记密码？</el-link>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width:100%" :loading="loading" native-type="submit">登 录</el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        还没有账号？<el-link type="primary" @click="$router.push('/register')">立即注册</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', password: '', remember: false })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  await formRef.value?.validate()
  loading.value = true
  try {
    // 模拟登录 — 后端对接后替换
    await new Promise(r => setTimeout(r, 500))
    userStore.login({
      token: 'mock-jwt-token-' + Date.now(),
      user: { id: 1, name: form.username, role: 'admin', avatar: '' }
    })
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 400px;
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.15);
}
.login-header {
  text-align: center;
  margin-bottom: 32px;
}
.login-header h2 {
  margin: 12px 0 4px;
  font-size: 22px;
  color: #333;
}
.login-header p {
  color: #999;
  font-size: 13px;
}
.login-options {
  display: flex;
  justify-content: space-between;
  width: 100%;
}
.login-footer {
  text-align: center;
  font-size: 13px;
  color: #999;
}
</style>
