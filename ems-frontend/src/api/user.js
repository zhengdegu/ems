import request from '../utils/request'

// 用户列表
export const getUserList = (params) => request.get('/user', { params })

// 用户详情
export const getUserDetail = (id) => request.get(`/user/${id}`)

// 创建用户
export const createUser = (data) => request.post('/user', data)

// 更新用户
export const updateUser = (id, data) => request.put(`/user/${id}`, data)

// 删除用户
export const deleteUser = (id) => request.delete(`/user/${id}`)

// 更新个人资料
export const updateProfile = (id, data) => request.put(`/user/${id}/profile`, data)

// 修改密码
export const changePassword = (id, data) => request.put(`/user/${id}/password`, data)

// 重置密码
export const resetPassword = (id) => request.put(`/user/${id}/reset-password`)

// 角色列表
export const getRoleList = (params) => request.get('/roles', { params })

// 创建角色
export const createRole = (data) => request.post('/roles', data)

// 更新角色
export const updateRole = (id, data) => request.put(`/roles/${id}`, data)

// 删除角色
export const deleteRole = (id) => request.delete(`/roles/${id}`)
