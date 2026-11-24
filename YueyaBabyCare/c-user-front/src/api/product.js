import request from '@/utils/request'

/**
 * 获取商品列表（带分页、筛选）
 * @param {Object} params - { categoryId, keyword, status, pageNum, pageSize }
 */
export function getProductList(params) {
  return request({
    url: '/api/product/list',
    method: 'get',
    params
  })
}

/**
 * 搜索商品
 * @param {Object} params - { keyword, pageNum, pageSize }
 */
export function searchProducts(params) {
  return request({
    url: '/api/product/search',
    method: 'get',
    params
  })
}

/**
 * 获取商品详情
 * @param {Number} id - 商品ID
 */
export function getProductById(id) {
  return request({
    url: `/api/product/${id}`,
    method: 'get'
  })
}

/**
 * 获取推荐商品
 * @param {Number} limit - 数量限制
 */
export function getRecommendProducts(limit = 10) {
  return request({
    url: '/api/product/recommend',
    method: 'get',
    params: { limit }
  })
}
