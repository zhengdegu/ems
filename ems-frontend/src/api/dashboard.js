import request from '../utils/request'

// Dashboard概览
export const getDashboardOverview = () => request.get('/dashboard/overview')

// 设备状态趋势
export const getEquipmentTrend = () => request.get('/dashboard/trend')

// 设备分类分布
export const getEquipmentDistribution = () => request.get('/dashboard/distribution')

// 设备区域分布
export const getEquipmentByArea = () => request.get('/dashboard/equipment')

// 告警信息
export const getAlarmInfo = () => request.get('/dashboard/alarm')

// 维护信息
export const getMaintenanceInfo = () => request.get('/dashboard/maintenance')
