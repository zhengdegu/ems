<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="login-title">{{ $t('login.title') }}</h2>
      
      <!-- 语言切换 -->
      <div class="locale-switch">
        <el-radio-group v-model="currentLocale" @change="handleLocaleChange" size="small">
          <el-radio-button label="zh-CN">中文</el-radio-button>
          <el-radio-button label="en-US">EN</el-radio-button>
        </el-radio-group>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input 
            v-model="form.username" 
            :placeholder="$t('login.username')"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="form.password" 
            type="password"
            :placeholder="$t('login.password')"
            prefix-icon="Lock"
          />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="form.remember">{{ $t('login.rememberMe') }}</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width:100%" @click="handleLogin">
            {{ $t('login.login') }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <span>{{ $t('login.noAccount') }}</span>
        <el-link type="primary" @click="$router.push('/register')">
          {{ $t('login.register') }}
        </el-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useLocaleStore } from '../stores/locale'

const { t } = useI18n()
const localeStore = useLocaleStore()
const currentLocale = ref(localeStore.locale)
const formRef = ref()

const form = ref({
  username: '',
  password: '',
  remember: false
})

const rules = {
  username: [{ required: true, message: t('common.pleaseInput'), trigger: 'blur' }],
  password: [{ required: true, message: t('common.pleaseInput'), trigger: 'blur' }]
}

function handleLocaleChange(locale) {
  localeStore.setLocale(locale)
}

function handleLogin() {
  formRef.value.validate((valid) => {
    if (valid) {
      console.log('Login:', form.value)
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
  padding: 20px;
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.locale-switch {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #666;
}
</style>
