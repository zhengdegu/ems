import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
api.interceptors.request.use(config => {
  const token = localStorage.getItem('ems_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器
api.interceptors.response.use(
  res => res.data,
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('ems_token')
      window.location.href = '/login'
    } else {
      ElMessage.error(err.response?.data?.message || '请求失败')
    }
    return Promise.reject(err)
  }
)

export default api
