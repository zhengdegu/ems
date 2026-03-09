import request from '../utils/request'

// 维护计划列表
export const getMaintenancePlanList = (params) => request.get('/maintenance-plan', { params })

// 维护计划详情
export const getMaintenancePlanDetail = (id) => request.get(`/maintenance-plan/${id}`)

// 创建维护计划
export const createMaintenancePlan = (data) => request.post('/maintenance-plan', data)

// 更新维护计划
export const updateMaintenancePlan = (id, data) => request.put(`/maintenance-plan/${id}`, data)

// 删除维护计划
export const deleteMaintenancePlan = (id) => request.delete(`/maintenance-plan/${id}`)

// 确认执行
export const confirmMaintenancePlan = (id) => request.put(`/maintenance-plan/${id}/confirm`)
