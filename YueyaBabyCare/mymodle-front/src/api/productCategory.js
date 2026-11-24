import request from '@/utils/request'

/**
 * 新增分类
 */
export function createCategory(data) {
  return request({
    url: '/product-category',
    method: 'post',
    data
  })
}

/**
 * 获取分类树
 */
export function getCategoryTree() {
  return request({
    url: '/product-category/tree',
    method: 'get'
  })
}

/**
 * 获取分类详情
 */
export function getCategoryById(id) {
  return request({
    url: `/product-category/${id}`,
    method: 'get'
  })
}

/**
 * 修改分类
 */
export function updateCategory(id, data) {
  return request({
    url: `/product-category/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除分类
 */
export function deleteCategory(id) {
  return request({
    url: `/product-category/${id}`,
    method: 'delete'
  })
}

/**
 * 获取分类列表（平铺）
 */
export function getCategoryList(status) {
  return request({
    url: '/product-category/list',
    method: 'get',
    params: { status }
  })
}

/**
 * 修改分类状态
 */
export function updateCategoryStatus(id, status) {
  return request({
    url: `/product-category/${id}/status`,
    method: 'put',
    params: { status }
  })
}
