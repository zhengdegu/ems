import request from '../utils/request'

// 设备报表
export const getEquipmentReport = (params) => request.get('/report/equipment', { params })

// 维护报表
export const getMaintenanceReport = (params) => request.get('/report/maintenance', { params })

// 告警报表
export const getAlarmReport = (params) => request.get('/report/alarm', { params })
