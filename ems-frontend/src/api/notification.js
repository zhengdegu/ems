import request from '../utils/request'

// 通知列表
export const getNotificationList = (params) => request.get('/notification', { params })

// 未读数量
export const getUnreadCount = () => request.get('/notification/unread-count')

// 标记已读
export const markAsRead = (id) => request.put(`/notification/${id}/read`)

// 全部已读
export const markAllAsRead = (params) => request.put('/notification/read-all', null, { params })

// 删除通知
export const deleteNotification = (id) => request.delete(`/notification/${id}`)
