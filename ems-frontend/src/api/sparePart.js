import request from '../utils/request'

// 备件列表
export const getSparePartList = (params) => request.get('/spare-part', { params })

// 备件详情
export const getSparePartDetail = (id) => request.get(`/spare-part/${id}`)

// 创建备件
export const createSparePart = (data) => request.post('/spare-part', data)

// 更新备件
export const updateSparePart = (id, data) => request.put(`/spare-part/${id}`, data)

// 删除备件
export const deleteSparePart = (id) => request.delete(`/spare-part/${id}`)

// 入库
export const sparePartInbound = (id, data) => request.post(`/spare-part/${id}/inbound`, data)

// 出库
export const sparePartOutbound = (id, data) => request.post(`/spare-part/${id}/outbound`, data)
