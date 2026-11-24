import request from '@/utils/request'

/**
 * 获取婴幼儿数据统计
 */
export function getBabyStats() {
  return request({
    url: '/api/parent/baby-stats',
    method: 'get'
  })
}

/**
 * 获取护理数据统计
 */
export function getCareStats() {
  return request({
    url: '/api/parent/care-stats',
    method: 'get'
  })
}

/**
 * 获取订单历史
 * @param {Object} params - 分页参数
 * @param {Number} params.pageNum - 页码
 * @param {Number} params.pageSize - 每页数量
 */
export function getOrderHistory(params) {
  return request({
    url: '/api/parent/order-history',
    method: 'get',
    params
  })
}
