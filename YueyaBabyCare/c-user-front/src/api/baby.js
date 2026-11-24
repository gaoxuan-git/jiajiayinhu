import request from '@/utils/request'

// ==================== 宝宝管理 ====================

/**
 * 获取当前家长的宝宝列表
 */
export function getBabyList() {
  return request.get('/api/baby/list')
}

/**
 * 获取宝宝详情
 * @param {Number} id - 宝宝ID
 */
export function getBabyById(id) {
  return request.get(`/api/baby/${id}`)
}

/**
 * 新增宝宝
 * @param {Object} data - 宝宝信息
 */
export function addBaby(data) {
  return request.post('/api/baby', data)
}

/**
 * 修改宝宝信息
 * @param {Number} id - 宝宝ID
 * @param {Object} data - 宝宝信息
 */
export function updateBaby(id, data) {
  return request.put(`/api/baby/${id}`, data)
}

/**
 * 删除宝宝
 * @param {Number} id - 宝宝ID
 */
export function deleteBaby(id) {
  return request.delete(`/api/baby/${id}`)
}

/**
 * 修改宝宝头像
 * @param {Number} id - 宝宝ID
 * @param {String} avatarUrl - 头像URL
 */
export function updateBabyAvatar(id, avatarUrl) {
  return request.put(`/api/baby/${id}/avatar`, null, { params: { avatarUrl } })
}

/**
 * 获取宝宝的保姆列表
 * @param {Number} id - 宝宝ID
 */
export function getBabyCaregivers(id) {
  return request.get(`/api/baby/${id}/caregivers`)
}
