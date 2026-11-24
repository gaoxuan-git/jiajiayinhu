import request from '@/utils/request'

// ==================== 宝宝管理 ====================

/**
 * 管理员获取所有宝宝列表
 */
export function getAllBabies() {
  return request({
    url: '/baby/admin/all',
    method: 'get'
  })
}

/**
 * 管理员根据家长ID查询宝宝列表
 * @param {Number} parentId - 家长ID
 */
export function getBabiesByParentId(parentId) {
  return request({
    url: `/baby/admin/parent/${parentId}`,
    method: 'get'
  })
}

/**
 * 获取宝宝详情
 * @param {Number} id - 宝宝ID
 */
export function getBabyById(id) {
  return request({
    url: `/baby/${id}`,
    method: 'get'
  })
}

/**
 * 修改宝宝信息
 * @param {Number} id - 宝宝ID
 * @param {Object} data - 宝宝信息
 */
export function updateBaby(id, data) {
  return request({
    url: `/baby/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除宝宝
 * @param {Number} id - 宝宝ID
 */
export function deleteBaby(id) {
  return request({
    url: `/baby/${id}`,
    method: 'delete'
  })
}

/**
 * 修改宝宝头像
 * @param {Number} id - 宝宝ID
 * @param {String} avatarUrl - 头像URL
 */
export function updateBabyAvatar(id, avatarUrl) {
  return request({
    url: `/baby/${id}/avatar`,
    method: 'put',
    params: { avatarUrl }
  })
}
