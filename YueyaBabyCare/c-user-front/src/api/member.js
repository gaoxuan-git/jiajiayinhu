import request from '@/utils/request'

// 家长登录
export function login(data) {
  return request.post('/api/parent/login', data)
}

// 家长注册
export function register(data) {
  return request.post('/api/parent/register', data)
}

// 家长登出
export function logout() {
  return request.post('/api/parent/logout')
}

// 获取家长个人信息
export function getMemberInfo() {
  return request.get('/api/parent/profile')
}

// 更新个人信息
export function updateProfile(data) {
  return request.put('/api/parent/profile', data)
}

// 修改密码
export function updatePassword(data) {
  return request.post('/api/parent/change-password', data)
}

// 修改头像
export function updateAvatar(avatarUrl) {
  return request.put('/api/parent/avatar', null, { params: { avatarUrl } })
}
