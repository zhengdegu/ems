import request from '../utils/request'

// 工单列表
export const getWorkOrderList = (params) => request.get('/work-order', { params })

// 工单详情
export const getWorkOrderDetail = (id) => request.get(`/work-order/${id}`)

// 创建工单
export const createWorkOrder = (data) => request.post('/work-order', data)

// 更新工单
export const updateWorkOrder = (id, data) => request.put(`/work-order/${id}`, data)

// 删除工单
export const deleteWorkOrder = (id) => request.delete(`/work-order/${id}`)

// 更新工单状态
export const updateWorkOrderStatus = (id, status) => request.put(`/work-order/${id}/status`, { status })
