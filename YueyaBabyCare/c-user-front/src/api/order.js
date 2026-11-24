import request from '@/utils/request'

/**
 * 创建订单
 * @param {Object} data - { cartIds, receiverName, receiverPhone, receiverAddress, remark }
 */
export function createOrder(data) {
  return request({
    url: '/api/order',
    method: 'post',
    data
  })
}

/**
 * 获取订单详情
 * @param {number} id - 订单ID
 */
export function getOrderDetail(id) {
  return request({
    url: `/api/order/${id}`,
    method: 'get'
  })
}

/**
 * 获取订单列表
 * @param {Object} params - { status, pageNum, pageSize }
 */
export function getOrderList(params) {
  return request({
    url: '/api/order/list',
    method: 'get',
    params
  })
}

/**
 * 支付订单
 * @param {number} id - 订单ID
 * @param {number} payType - 支付方式（1-微信，2-支付宝，3-余额）
 */
export function payOrder(id, payType) {
  return request({
    url: `/api/order/${id}/pay`,
    method: 'post',
    params: { payType }
  })
}

/**
 * 取消订单
 * @param {number} id - 订单ID
 */
export function cancelOrder(id) {
  return request({
    url: `/api/order/${id}/cancel`,
    method: 'post'
  })
}

/**
 * 确认收货
 * @param {number} id - 订单ID
 */
export function confirmReceipt(id) {
  return request({
    url: `/api/order/${id}/confirm-receipt`,
    method: 'put'
  })
}

/**
 * 获取订单统计
 */
export function getOrderStats() {
  return request({
    url: '/api/order/stats',
    method: 'get'
  })
}
