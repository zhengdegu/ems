import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录', noAuth: true }
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../views/Register.vue'),
    meta: { title: '注册', noAuth: true }
  },
  {
    path: '/',
    component: () => import('../layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'dashboard', component: () => import('../views/Dashboard.vue'), meta: { title: '工作台', icon: 'Odometer', affix: true } },
      { path: 'equipment-list', name: 'equipment-list', component: () => import('../views/equipment/EquipmentList.vue'), meta: { title: '设备列表', icon: 'Monitor' } },
      { path: 'equipment-detail/:id?', name: 'equipment-detail', component: () => import('../views/equipment/EquipmentDetail.vue'), meta: { title: '设备详情', icon: 'InfoFilled' } },
      { path: 'equipment-form/:id?', name: 'equipment-form', component: () => import('../views/equipment/EquipmentForm.vue'), meta: { title: '新增设备', icon: 'CirclePlus' } },
      { path: 'maintenance-plan', name: 'maintenance-plan', component: () => import('../views/maintenance/MaintenancePlan.vue'), meta: { title: '维护计划', icon: 'Calendar' } },
      { path: 'work-order', name: 'work-order', component: () => import('../views/maintenance/WorkOrder.vue'), meta: { title: '工单管理', icon: 'Document' } },
      { path: 'work-order-detail/:id?', name: 'work-order-detail', component: () => import('../views/maintenance/WorkOrderDetail.vue'), meta: { title: '工单详情', icon: 'DocumentChecked' } },
      { path: 'alarm', name: 'alarm', component: () => import('../views/monitor/Alarm.vue'), meta: { title: '告警中心', icon: 'Bell' } },
      { path: 'spare-part', name: 'spare-part', component: () => import('../views/sparePart/SparePartList.vue'), meta: { title: '备件管理', icon: 'Box' } },
      { path: 'report', name: 'report', component: () => import('../views/analysis/Report.vue'), meta: { title: '报表统计', icon: 'DataAnalysis' } },
      { path: 'user-manage', name: 'user-manage', component: () => import('../views/system/UserManage.vue'), meta: { title: '用户管理', icon: 'User' } },
      { path: 'role-permission', name: 'role-permission', component: () => import('../views/system/RolePermission.vue'), meta: { title: '角色权限', icon: 'Lock' } },
      { path: 'system-settings', name: 'system-settings', component: () => import('../views/system/SystemSettings.vue'), meta: { title: '系统设置', icon: 'Setting' } },
      { path: 'profile', name: 'profile', component: () => import('../views/personal/Profile.vue'), meta: { title: '个人中心', icon: 'UserFilled' } },
      { path: 'notification', name: 'notification', component: () => import('../views/personal/Notification.vue'), meta: { title: '消息通知', icon: 'Message' } },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('ems_token')
  if (!to.meta.noAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
