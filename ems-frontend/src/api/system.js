import request from '../utils/request'

// 系统设置列表
export const getSystemSettings = () => request.get('/system/settings')

// 获取单个设置
export const getSystemSetting = (key) => request.get(`/system/settings/${key}`)

// 保存设置
export const saveSystemSettings = (data) => request.post('/system/settings', data)

// 批量保存设置
export const batchSaveSettings = (data) => request.put('/system/settings/batch', data)

// 删除设置
export const deleteSystemSetting = (key) => request.delete(`/system/settings/${key}`)

// 操作日志
export const getOperationLog = (params) => request.get('/system/operation-log', { params })

// 登录日志
export const getLoginLog = (params) => request.get('/system/login-log', { params })
