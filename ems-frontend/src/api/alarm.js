import request from '../utils/request'

// 告警列表
export const getAlarmList = (params) => request.get('/alarm', { params })

// 告警详情
export const getAlarmDetail = (id) => request.get(`/alarm/${id}`)

// 处理告警
export const handleAlarm = (id, data) => request.put(`/alarm/${id}/handle`, data)

// 告警规则列表
export const getAlarmRuleList = (params) => request.get('/alarm-rule', { params })

// 创建告警规则
export const createAlarmRule = (data) => request.post('/alarm-rule', data)

// 更新告警规则
export const updateAlarmRule = (id, data) => request.put(`/alarm-rule/${id}`, data)

// 删除告警规则
export const deleteAlarmRule = (id) => request.delete(`/alarm-rule/${id}`)

// 切换告警规则状态
export const toggleAlarmRule = (id) => request.put(`/alarm-rule/${id}/toggle`)
