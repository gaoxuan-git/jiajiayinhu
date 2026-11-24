import request from '@/utils/request'

// ==================== 机构端接口 ====================

/**
 * 新增商品（机构）
 */
export function createProduct(data) {
  return request({
    url: '/product',
    method: 'post',
    data
  })
}

/**
 * 获取商品详情
 */
export function getProductById(id) {
  return request({
    url: `/product/${id}`,
    method: 'get'
  })
}

/**
 * 修改商品（机构）
 */
export function updateProduct(id, data) {
  return request({
    url: `/product/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除商品（机构）
 */
export function deleteProduct(id) {
  return request({
    url: `/product/${id}`,
    method: 'delete'
  })
}

/**
 * 获取机构的商品列表
 */
export function getInstitutionProducts(params) {
  return request({
    url: '/product/institution/products',
    method: 'get',
    params
  })
}

/**
 * 上架/下架商品（机构）
 */
export function updateProductStatus(id, status) {
  return request({
    url: `/product/${id}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 上传商品图片（机构）
 */
export function uploadProductImages(id, imageUrls) {
  return request({
    url: `/product/${id}/images`,
    method: 'post',
    data: imageUrls
  })
}

// ==================== 管理员端接口 ====================

/**
 * 获取所有商品列表（管理员）
 */
export function adminGetProductList(params) {
  return request({
    url: '/admin/product/list',
    method: 'get',
    params
  })
}

/**
 * 获取商品详情（管理员）
 */
export function adminGetProductById(id) {
  return request({
    url: `/admin/product/${id}`,
    method: 'get'
  })
}

/**
 * 修改商品（管理员）
 */
export function adminUpdateProduct(id, data) {
  return request({
    url: `/admin/product/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除商品（管理员）
 */
export function adminDeleteProduct(id) {
  return request({
    url: `/admin/product/${id}`,
    method: 'delete'
  })
}

/**
 * 上架/下架商品（管理员）
 */
export function adminUpdateProductStatus(id, status) {
  return request({
    url: `/admin/product/${id}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 批量删除商品（管理员）
 */
export function adminBatchDeleteProducts(ids) {
  return request({
    url: '/admin/product/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 批量上架/下架商品（管理员）
 */
export function adminBatchUpdateStatus(ids, status) {
  return request({
    url: '/admin/product/batch/status',
    method: 'put',
    data: ids,
    params: { status }
  })
}

// ==================== 公共接口 ====================

/**
 * 获取商品列表（带分页、筛选）
 */
export function getProductList(params) {
  return request({
    url: '/product/list',
    method: 'get',
    params
  })
}

/**
 * 搜索商品
 */
export function searchProducts(params) {
  return request({
    url: '/product/search',
    method: 'get',
    params
  })
}

/**
 * 获取推荐商品
 */
export function getRecommendProducts(limit) {
  return request({
    url: '/product/recommend',
    method: 'get',
    params: { limit }
  })
}
