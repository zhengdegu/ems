import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('ems_token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('ems_user') || '{}'))

  function login(data) {
    token.value = data.token
    userInfo.value = data.user
    localStorage.setItem('ems_token', data.token)
    localStorage.setItem('ems_user', JSON.stringify(data.user))
  }

  function logout() {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('ems_token')
    localStorage.removeItem('ems_user')
  }

  return { token, userInfo, login, logout }
})
