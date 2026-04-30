<template>
  <div class="login-page">
    <!-- 左侧品牌区 -->
    <div class="login-brand">
      <div class="brand-decoration">
        <div class="deco-circle deco-circle-1"></div>
        <div class="deco-circle deco-circle-2"></div>
        <div class="deco-circle deco-circle-3"></div>
      </div>
      <div class="brand-content">
        <div class="brand-logo">
          <div class="logo-icon">
            <el-icon :size="28" color="#fff"><Setting /></el-icon>
          </div>
          <span class="logo-text">EMS Pro</span>
        </div>
        <h1 class="brand-title">企业级设备管理系统</h1>
        <p class="brand-subtitle">智能化设备全生命周期管理平台，助力企业数字化转型</p>
        <div class="brand-features">
          <div class="feature-item">
            <el-icon color="#86efac"><CircleCheck /></el-icon>
            <span>实时设备监控与预警</span>
          </div>
          <div class="feature-item">
            <el-icon color="#86efac"><CircleCheck /></el-icon>
            <span>智能维护计划管理</span>
          </div>
          <div class="feature-item">
            <el-icon color="#86efac"><CircleCheck /></el-icon>
            <span>多维度数据报表分析</span>
          </div>
          <div class="feature-item">
            <el-icon color="#86efac"><CircleCheck /></el-icon>
            <span>灵活的权限控制体系</span>
          </div>
        </div>
        <div class="brand-stats">
          <div class="stat-item">
            <div class="stat-value">2,847</div>
            <div class="stat-label">设备接入</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">96.8%</div>
            <div class="stat-label">在线率</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">99.9%</div>
            <div class="stat-label">系统可用</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧登录表单 -->
    <div class="login-form-area">
      <div class="login-form-wrapper">
        <!-- 移动端 logo -->
        <div class="mobile-logo">
          <div class="mobile-logo-icon">
            <el-icon :size="20" color="#fff"><Setting /></el-icon>
          </div>
          <span class="mobile-logo-text">EMS Pro</span>
        </div>

        <h2 class="form-title">欢迎回来</h2>
        <p class="form-subtitle">请登录您的账户</p>

        <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleLogin" class="login-form">
          <div class="form-field">
            <label class="field-label">用户名 / 邮箱</label>
            <el-form-item prop="username">
              <el-input
                v-model="form.username"
                placeholder="请输入用户名或邮箱"
                prefix-icon="User"
                size="large"
              />
            </el-form-item>
          </div>

          <div class="form-field">
            <label class="field-label">密码</label>
            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="请输入密码"
                prefix-icon="Lock"
                size="large"
              >
                <template #suffix>
                  <el-icon class="password-toggle" @click="showPassword = !showPassword">
                    <View v-if="showPassword" />
                    <Hide v-else />
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>
          </div>

          <div class="login-options">
            <el-checkbox v-model="form.remember">记住我</el-checkbox>
            <el-link type="primary" :underline="false">忘记密码？</el-link>
          </div>

          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            native-type="submit"
          >
            登 录
          </el-button>
        </el-form>

        <!-- 其他登录方式 -->
        <div class="other-login">
          <div class="divider">
            <span class="divider-text">其他登录方式</span>
          </div>
          <div class="social-buttons">
            <button class="social-btn social-wechat" type="button" title="微信登录">
              <svg viewBox="0 0 1024 1024" width="20" height="20" fill="currentColor">
                <path d="M690.1 377.4c5.9 0 11.8 0.2 17.6 0.5-24.4-128.7-158.3-227.1-313.4-227.1C209 150.8 56.7 278.2 56.7 435.7c0 90.5 49.2 164.3 131.3 221.6l-32.8 98.6 114.6-57.3c41 16.4 73.7 24.6 114.6 24.6 5.5 0 10.9-0.2 16.3-0.5-6.8-23.4-10.7-47.9-10.7-73.2 0-154.8 131.3-272.1 300.1-272.1zM485.2 310.5c24.6 0 41 16.4 41 41s-16.4 41-41 41-49.2-16.4-49.2-41 24.6-41 49.2-41zM287.8 392.5c-24.6 0-49.2-16.4-49.2-41s24.6-41 49.2-41 41 16.4 41 41-16.4 41-41 41z"/>
                <path d="M946.5 649.8c0-131.3-131.3-238.5-278.9-238.5S388.7 518.5 388.7 649.8s131.3 238.5 278.9 238.5c32.8 0 65.5-8.2 98.3-16.4l90.2 49.2-24.6-81.9c65.5-49.3 114.6-106.6 115-189.4zM569.5 608.8c-16.4 0-32.8-16.4-32.8-32.8s16.4-32.8 32.8-32.8 32.8 16.4 32.8 32.8-16.4 32.8-32.8 32.8z m197.2 0c-16.4 0-32.8-16.4-32.8-32.8s16.4-32.8 32.8-32.8 32.8 16.4 32.8 32.8-16.4 32.8-32.8 32.8z"/>
              </svg>
            </button>
            <button class="social-btn social-qq" type="button" title="QQ登录">
              <svg viewBox="0 0 1024 1024" width="20" height="20" fill="currentColor">
                <path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm210.5 612.4c-11.5 1.4-44.9-52.7-44.9-52.7 0 31.3-16.2 72.2-51.1 101.8 16.9 5.2 54.9 19.2 45.9 34.4-7.3 12.2-125.6 3.2-160.4 0.2-34.8 3-153.1 12-160.4-0.2-9-15.2 28.9-29.2 45.8-34.4-34.9-29.6-51.1-70.5-51.1-101.8 0 0-33.4 54.1-44.9 52.7-5.4-0.7-12.4-29.6 9.4-99.7 10.3-33 22-60.5 40.2-105.8-3.1-116.9 45.3-215 160.4-215 113.9 0 163.3 96.1 160.4 215 18.1 45.2 29.9 72.8 40.2 105.8 21.7 70.1 14.6 99.1 9.5 99.7z"/>
              </svg>
            </button>
            <button class="social-btn social-phone" type="button" title="手机登录">
              <el-icon :size="20"><Iphone /></el-icon>
            </button>
          </div>
        </div>

        <p class="register-link">
          还没有账户？<el-link type="primary" @click="$router.push('/register')">立即注册</el-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import { login } from '../api/auth'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const showPassword = ref(false)

const form = reactive({ username: '', password: '', remember: false })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  await formRef.value?.validate()
  loading.value = true
  try {
    const res = await login({ username: form.username, password: form.password })
    if (res.code === 200) {
      userStore.login({ token: res.data.token, user: res.data.user })
      ElMessage.success('登录成功')
      router.push('/dashboard')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (e) {
    ElMessage.error('登录失败，请检查网络')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
}

/* 左侧品牌区 */
.login-brand {
  display: none;
  width: 50%;
  background: linear-gradient(135deg, #0958d9 0%, #1890FF 40%, #4338ca 100%);
  position: relative;
  overflow: hidden;
  align-items: center;
  justify-content: center;
  padding: 48px;
}

.brand-decoration {
  position: absolute;
  inset: 0;
  opacity: 0.1;
}

.deco-circle {
  position: absolute;
  border: 1px solid #fff;
  border-radius: 50%;
}

.deco-circle-1 { top: 80px; left: 80px; width: 160px; height: 160px; }
.deco-circle-2 { bottom: 128px; right: 0; width: 240px; height: 240px; }
.deco-circle-3 { top: 50%; left: 33%; width: 80px; height: 80px; }

.brand-content {
  position: relative;
  z-index: 1;
  text-align: center;
  color: #fff;
}

.brand-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
}

.logo-icon {
  width: 52px;
  height: 52px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  backdrop-filter: blur(4px);
}

.logo-text { font-size: 30px; font-weight: 700; }
.brand-title { font-size: 32px; font-weight: 700; margin-bottom: 12px; }

.brand-subtitle {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 32px;
  max-width: 400px;
  margin-left: auto;
  margin-right: auto;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-items: center;
  margin-bottom: 40px;
}

.feature-item { display: flex; align-items: center; gap: 12px; font-size: 15px; }

.brand-stats {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 48px;
  margin-top: 40px;
  color: rgba(191, 219, 254, 1);
  font-size: 14px;
}

.stat-value { font-size: 24px; font-weight: 700; color: #fff; }
.stat-label { margin-top: 4px; }

/* 右侧表单区 */
.login-form-area {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px;
  background: #f5f5f5;
}

.login-form-wrapper { width: 100%; max-width: 420px; }

.mobile-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
}

.mobile-logo-icon {
  width: 40px;
  height: 40px;
  background: #1890FF;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.mobile-logo-text { font-size: 22px; font-weight: 700; color: #333; }
.form-title { font-size: 24px; font-weight: 700; color: #333; margin-bottom: 4px; }
.form-subtitle { color: #999; font-size: 14px; margin-bottom: 32px; }
.login-form { margin-bottom: 24px; }
.form-field { margin-bottom: 4px; }

.field-label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: #666;
  margin-bottom: 6px;
}

.password-toggle { cursor: pointer; color: #bfbfbf; }
.password-toggle:hover { color: #666; }

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.login-btn { width: 100%; height: 42px; font-size: 14px; border-radius: 8px; }

.other-login { margin-bottom: 24px; }

.divider {
  position: relative;
  text-align: center;
  margin: 20px 0;
}

.divider::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  width: 100%;
  height: 1px;
  background: #f0f0f0;
}

.divider-text {
  position: relative;
  background: #f5f5f5;
  padding: 0 16px;
  font-size: 12px;
  color: #999;
}

.social-buttons { display: flex; justify-content: center; gap: 24px; }

.social-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 1px solid #d9d9d9;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #999;
  transition: all 0.2s;
}

.social-btn:hover { transform: translateY(-1px); }
.social-wechat:hover { color: #07c160; border-color: #07c160; }
.social-qq:hover { color: #1890FF; border-color: #1890FF; }
.social-phone:hover { color: #1890FF; border-color: #1890FF; }
.register-link { text-align: center; font-size: 13px; color: #999; }

@media (min-width: 992px) {
  .login-brand { display: flex; }
  .login-form-area { width: 50%; }
  .mobile-logo { display: none; }
}
</style>
