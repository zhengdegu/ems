import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '../api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('ems_token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('ems_user') || '{}'))
  const permissions = ref(JSON.parse(localStorage.getItem('ems_permissions') || '[]'))

  function login(data) {
    token.value = data.token
    userInfo.value = data.user
    localStorage.setItem('ems_token', data.token)
    localStorage.setItem('ems_user', JSON.stringify(data.user))

    // 根据用户角色加载权限
    loadPermissions(data.user)
  }

  function loadPermissions(user) {
    if (!user || !user.role) {
      permissions.value = []
      localStorage.setItem('ems_permissions', '[]')
      return
    }

    // admin 拥有所有权限
    if (user.role === 'admin') {
      const allPerms = [
        'equipment:view', 'equipment:edit', 'equipment:delete',
        'work_order:view', 'work_order:edit', 'work_order:delete',
        'maintenance:view', 'maintenance:edit',
        'alarm:view', 'alarm:handle',
        'spare_part:view', 'spare_part:edit',
        'report:view',
        'user:manage', 'role:manage', 'system:settings',
        'notification:view', 'profile:view'
      ]
      permissions.value = allPerms
      localStorage.setItem('ems_permissions', JSON.stringify(allPerms))
      return
    }

    // 非 admin 用户，尝试从角色接口获取权限
    // 如果角色信息中包含 permissions，直接使用
    // 否则给予基础权限
    const basePerms = [
      'equipment:view', 'work_order:view', 'maintenance:view',
      'alarm:view', 'spare_part:view', 'report:view',
      'notification:view', 'profile:view'
    ]
    permissions.value = basePerms
    localStorage.setItem('ems_permissions', JSON.stringify(basePerms))

    // 异步加载角色权限
    fetchRolePermissions(user.role)
  }

  async function fetchRolePermissions(roleCode) {
    try {
      const res = await import('../utils/request').then(m => m.default.get('/roles', { params: { page: 1, size: 100 } }))
      if (res.code === 200 && res.data?.records) {
        const role = res.data.records.find(r => r.code === roleCode)
        if (role && role.permissions) {
          try {
            const perms = JSON.parse(role.permissions)
            if (Array.isArray(perms)) {
              permissions.value = perms
              localStorage.setItem('ems_permissions', JSON.stringify(perms))
            }
          } catch {
            // permissions 不是有效 JSON，保持基础权限
          }
        }
      }
    } catch {
      // 获取角色权限失败，保持基础权限
    }
  }

  function hasPermission(perm) {
    if (!perm) return true
    // admin 角色拥有所有权限
    if (userInfo.value?.role === 'admin') return true
    return permissions.value.includes(perm)
  }

  function logout() {
    token.value = ''
    userInfo.value = {}
    permissions.value = []
    localStorage.removeItem('ems_token')
    localStorage.removeItem('ems_user')
    localStorage.removeItem('ems_permissions')
  }

  return { token, userInfo, permissions, login, logout, hasPermission, loadPermissions }
})
