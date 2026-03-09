import request from '../utils/request'

// 设备列表
export const getEquipmentList = (params) => request.get('/equipment', { params })

// 设备详情
export const getEquipmentDetail = (id) => request.get(`/equipment/${id}`)

// 创建设备
export const createEquipment = (data) => request.post('/equipment', data)

// 更新设备
export const updateEquipment = (id, data) => request.put(`/equipment/${id}`, data)

// 删除设备
export const deleteEquipment = (id) => request.delete(`/equipment/${id}`)
