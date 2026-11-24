import request from '@/utils/request'

/**
 * 添加商品到购物车
 * @param {Object} data - { productId, quantity }
 */
export function addToCart(data) {
  return request({
    url: '/api/cart',
    method: 'post',
    data
  })
}

/**
 * 获取购物车列表
 */
export function getCartList() {
  return request({
    url: '/api/cart',
    method: 'get'
  })
}

/**
 * 修改购物车商品数量
 * @param {number} id - 购物车ID
 * @param {number} quantity - 数量
 */
export function updateCartQuantity(id, quantity) {
  return request({
    url: `/api/cart/${id}`,
    method: 'put',
    params: { quantity }
  })
}

/**
 * 删除购物车商品
 * @param {number} id - 购物车ID
 */
export function deleteCartItem(id) {
  return request({
    url: `/api/cart/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除购物车商品
 * @param {Array} cartIds - 购物车ID列表
 */
export function batchDeleteCartItems(cartIds) {
  return request({
    url: '/api/cart/batch',
    method: 'delete',
    data: cartIds
  })
}

/**
 * 清空购物车
 */
export function clearCart() {
  return request({
    url: '/api/cart/clear',
    method: 'delete'
  })
}

/**
 * 选中/取消选中商品
 * @param {number} id - 购物车ID
 * @param {number} selected - 是否选中（0-否，1-是）
 */
export function updateCartSelected(id, selected) {
  return request({
    url: `/api/cart/${id}/select`,
    method: 'put',
    params: { selected }
  })
}

/**
 * 全选/取消全选
 * @param {number} selected - 是否选中（0-否，1-是）
 */
export function selectAllCart(selected) {
  return request({
    url: '/api/cart/selectAll',
    method: 'put',
    params: { selected }
  })
}

/**
 * 获取购物车商品数量
 */
export function getCartCount() {
  return request({
    url: '/api/cart/count',
    method: 'get'
  })
}

/**
 * 获取购物车选中商品总额
 */
export function getCartTotal() {
  return request({
    url: '/api/cart/total',
    method: 'get'
  })
}
