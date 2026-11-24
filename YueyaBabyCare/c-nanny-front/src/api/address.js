import request from '@/utils/request'

/**
 * 获取当前用户地址列表
 */
export function getAddressList() {
  return request({
    url: '/api/address',
    method: 'get'
  })
}

/**
 * 获取地址详情
 * @param {Number} id - 地址ID
 */
export function getAddressById(id) {
  return request({
    url: `/api/address/${id}`,
    method: 'get'
  })
}

/**
 * 新增地址
 * @param {Object} data - 地址信息
 */
export function addAddress(data) {
  return request({
    url: '/api/address',
    method: 'post',
    data
  })
}

/**
 * 修改地址
 * @param {Number} id - 地址ID
 * @param {Object} data - 地址信息
 */
export function updateAddress(id, data) {
  return request({
    url: `/api/address/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除地址
 * @param {Number} id - 地址ID
 */
export function deleteAddress(id) {
  return request({
    url: `/api/address/${id}`,
    method: 'delete'
  })
}

/**
 * 设置默认地址
 * @param {Number} id - 地址ID
 */
export function setDefaultAddress(id) {
  return request({
    url: `/api/address/${id}/set-default`,
    method: 'put'
  })
}

/**
 * 获取默认地址
 */
export function getDefaultAddress() {
  return request({
    url: '/api/address/default',
    method: 'get'
  })
}
