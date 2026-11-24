import request from '@/utils/request'

// ==================== 机构端库存管理接口 ====================

/**
 * 新增库存操作记录
 */
export function addInventoryRecord(data) {
  return request({
    url: '/inventory',
    method: 'post',
    data
  })
}

/**
 * 获取库存操作记录列表
 */
export function getInventoryList(params) {
  return request({
    url: '/inventory/list',
    method: 'get',
    params
  })
}

/**
 * 获取当前库存列表
 */
export function getCurrentStock(params) {
  return request({
    url: '/inventory/current',
    method: 'get',
    params
  })
}

/**
 * 获取库存不足商品列表
 */
export function getLowStockProducts() {
  return request({
    url: '/inventory/low-stock',
    method: 'get'
  })
}

/**
 * 设置库存预警值
 */
export function setStockAlert(productId, alertValue) {
  return request({
    url: '/inventory/set-alert',
    method: 'put',
    params: { productId, alertValue }
  })
}

/**
 * 获取库存统计数据
 */
export function getInventoryStats() {
  return request({
    url: '/inventory/stats',
    method: 'get'
  })
}
