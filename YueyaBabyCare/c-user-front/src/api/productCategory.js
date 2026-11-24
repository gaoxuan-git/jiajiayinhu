import request from '@/utils/request'

/**
 * 获取分类树
 */
export function getCategoryTree() {
  return request({
    url: '/api/product-category/tree',
    method: 'get'
  })
}

/**
 * 获取分类列表（平铺）
 */
export function getCategoryList(status) {
  return request({
    url: '/api/product-category/list',
    method: 'get',
    params: { status }
  })
}
