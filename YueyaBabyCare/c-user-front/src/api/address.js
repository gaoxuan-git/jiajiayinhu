import request from '@/utils/request'

/**
 * 获取当前用户地址列表
 */
export function getAddressList() {
  return request.get('/api/address')
}

/**
 * 获取地址详情
 * @param {Number} id - 地址ID
 */
export function getAddressById(id) {
  return request.get(`/api/address/${id}`)
}

/**
 * 新增地址
 * @param {Object} data - 地址信息
 */
export function addAddress(data) {
  return request.post('/api/address', data)
}

/**
 * 修改地址
 * @param {Number} id - 地址ID
 * @param {Object} data - 地址信息
 */
export function updateAddress(id, data) {
  return request.put(`/api/address/${id}`, data)
}

/**
 * 删除地址
 * @param {Number} id - 地址ID
 */
export function deleteAddress(id) {
  return request.delete(`/api/address/${id}`)
}

/**
 * 设置默认地址
 * @param {Number} id - 地址ID
 */
export function setDefaultAddress(id) {
  return request.put(`/api/address/${id}/set-default`)
}

/**
 * 获取默认地址
 */
export function getDefaultAddress() {
  return request.get('/api/address/default')
}
