<template>
  <el-container class="main-layout">
    <!-- 侧边栏 -->
    <el-aside :width="sidebarCollapsed ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <el-icon :size="24" color="#1890FF"><Monitor /></el-icon>
        <span v-show="!sidebarCollapsed" class="logo-text">{{ $t('login.title') }}</span>
      </div>
      <el-menu
        :default-active="$route.name"
        :collapse="sidebarCollapsed"
        :collapse-transition="false"
        background-color="#001529"
        text-color="#ffffffa6"
        active-text-color="#fff"
        router
      >
        <template v-for="group in menuGroups" :key="group.label">
          <el-menu-item-group :title="sidebarCollapsed ? '' : group.label">
            <el-menu-item
              v-for="item in group.items"
              :key="item.name"
              :index="item.name"
              :route="{ name: item.name }"
            >
              <el-icon><component :is="item.icon" /></el-icon>
              <template #title>{{ $t(`menu.${item.i18nKey}`) }}</template>
            </el-menu-item>
          </el-menu-item-group>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶部栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="sidebarCollapsed = !sidebarCollapsed">
            <Fold v-if="!sidebarCollapsed" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">{{ $t('menu.dashboard') }}</el-breadcrumb-item>
            <el-breadcrumb-item>{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <!-- 语言切换 -->
          <el-dropdown trigger="click" @command="handleLocaleChange">
            <el-icon :size="18" class="header-icon"><Globe /></el-icon>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="zh-CN" :disabled="locale === 'zh-CN'">简体中文</el-dropdown-item>
                <el-dropdown-item command="en-US" :disabled="locale === 'en-US'">English</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          
          <el-badge :value="3" :max="99" class="notify-badge">
            <el-icon :size="18" class="header-icon" @click="$router.push({ name: 'notification' })"><Bell /></el-icon>
          </el-badge>
          <el-dropdown trigger="click">
            <div class="user-info">
              <el-avatar :size="28" style="background:#1890FF">
                {{ userStore.userInfo.name?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="user-name">{{ userStore.userInfo.name || $t('system.username') }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push({ name: 'profile' })">{{ $t('menu.profile') }}</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">{{ $t('message.logoutSuccess') }}</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 标签页栏 -->
      <div class="tab-bar">
        <div
          v-for="tab in tabStore.tabs"
          :key="tab.name"
          :class="['tab-item', { active: tabStore.activeTab === tab.name }]"
          @click="switchTab(tab)"
        >
          {{ tab.title }}
          <el-icon v-if="tab.closable" class="close-btn" @click.stop="closeTab(tab.name)"><Close /></el-icon>
        </div>
      </div>

      <!-- 内容区 -->
      <el-main class="page-content">
        <router-view v-slot="{ Component }">
          <keep-alive>
            <component :is="Component" />
          </keep-alive>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '../stores/user'
import { useTabStore } from '../stores/tab'
import { useLocaleStore } from '../stores/locale'

const route = useRoute()
const router = useRouter()
const { t, locale: i18nLocale } = useI18n()
const userStore = useUserStore()
const tabStore = useTabStore()
const localeStore = useLocaleStore()
const sidebarCollapsed = ref(false)

const locale = computed(() => localeStore.locale)

const menuGroups = [
  { label: '概览', items: [
    { label: '工作台', name: 'dashboard', icon: 'Odometer', i18nKey: 'dashboard' }
  ]},
  { label: '设备管理', items: [
    { label: '设备列表', name: 'equipment-list', icon: 'Monitor', i18nKey: 'equipmentList' },
    { label: '设备详情', name: 'equipment-detail', icon: 'InfoFilled', i18nKey: 'equipmentDetail' },
    { label: '新增设备', name: 'equipment-form', icon: 'CirclePlus', i18nKey: 'equipmentForm' }
  ]},
  { label: '维护管理', items: [
    { label: '维护计划', name: 'maintenance-plan', icon: 'Calendar', i18nKey: 'maintenancePlan' },
    { label: '工单管理', name: 'work-order', icon: 'Document', i18nKey: 'workOrder' },
    { label: '工单详情', name: 'work-order-detail', icon: 'DocumentChecked', i18nKey: 'workOrderDetail' }
  ]},
  { label: '监控预警', items: [
    { label: '告警中心', name: 'alarm', icon: 'Bell', i18nKey: 'alarm' }
  ]},
  { label: '数据分析', items: [
    { label: '报表统计', name: 'report', icon: 'DataAnalysis', i18nKey: 'report' }
  ]},
  { label: '系统管理', items: [
    { label: '用户管理', name: 'user-manage', icon: 'User', i18nKey: 'userManage' },
    { label: '角色权限', name: 'role-permission', icon: 'Lock', i18nKey: 'rolePermission' },
    { label: '系统设置', name: 'system-settings', icon: 'Setting', i18nKey: 'systemSettings' }
  ]},
  { label: '个人', items: [
    { label: '个人中心', name: 'profile', icon: 'UserFilled', i18nKey: 'profile' },
    { label: '消息通知', name: 'notification', icon: 'Message', i18nKey: 'notification' }
  ]}
]

// 监听路由变化，自动添加标签页
watch(() => route.name, (name) => {
  if (name && name !== 'login' && name !== 'register') {
    tabStore.addTab(route)
  }
}, { immediate: true })

function switchTab(tab) {
  router.push({ name: tab.name })
}

function closeTab(name) {
  const next = tabStore.removeTab(name)
  if (next) router.push({ name: next })
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}

function handleLocaleChange(command) {
  localeStore.setLocale(command)
}
</script>

<style scoped>
.main-layout {
  height: 100vh;
}

.sidebar {
  background: #001529;
  transition: width 0.2s;
  overflow: hidden;
}

.logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-bottom: 1px solid #ffffff1a;
}

.logo-text {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
}

.header {
  height: 56px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  border-bottom: 1px solid #f0f0f0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.collapse-btn {
  font-size: 18px;
  cursor: pointer;
  color: #666;
}

.collapse-btn:hover {
  color: #1890FF;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-icon {
  cursor: pointer;
  color: #666;
}

.header-icon:hover {
  color: #1890FF;
}

.notify-badge {
  line-height: 1;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.user-name {
  font-size: 14px;
  color: #333;
}

:deep(.el-menu-item-group__title) {
  padding-left: 20px !important;
  color: #ffffff45;
  font-size: 12px;
}

:deep(.el-menu-item) {
  height: 44px;
  line-height: 44px;
}

:deep(.el-menu-item.is-active) {
  background: #1890FF !important;
}
</style>
