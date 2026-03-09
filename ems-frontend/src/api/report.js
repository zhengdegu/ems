import request from '../utils/request'

// 生成报表
export const generateReport = (params) => request.get('/report', { params })
