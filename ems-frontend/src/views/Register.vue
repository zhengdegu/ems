<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <el-icon :size="36" color="#1890FF"><Monitor /></el-icon>
        <h2>注册账号</h2>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleRegister">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="name">
          <el-input v-model="form.name" placeholder="姓名" prefix-icon="UserFilled" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width:100%" :loading="loading" native-type="submit">注 册</el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        已有账号？<el-link type="primary" @click="$router.push('/login')">返回登录</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', name: '', password: '', confirmPassword: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' }, {
    validator: (rule, value, callback) => {
      if (value !== form.password) callback(new Error('两次密码不一致'))
      else callback()
    }, trigger: 'blur'
  }]
}

async function handleRegister() {
  await formRef.value?.validate()
  loading.value = true
  try {
    await new Promise(r => setTimeout(r, 500))
    ElMessage.success('注册成功，请登录')
    router.push('/login')
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
.login-footer {
  text-align: center;
  font-size: 13px;
  color: #999;
}
</style>
