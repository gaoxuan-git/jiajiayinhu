import request from '@/utils/request'

// ==================== 机构端接口 ====================

/**
 * 获取机构订单列表
 */
export function getInstitutionOrders(params) {
  return request({
    url: '/order/institution/list',
    method: 'get',
    params
  })
}

/**
 * 获取机构订单详情
 */
export function getInstitutionOrderDetail(id) {
  return request({
    url: `/order/institution/${id}`,
    method: 'get'
  })
}

/**
 * 添加物流信息（发货）
 */
export function addLogistics(id, deliveryCompany, deliveryNo) {
  return request({
    url: `/order/${id}/logistics`,
    method: 'post',
    params: { deliveryCompany, deliveryNo }
  })
}

/**
 * 获取机构订单统计
 */
export function getInstitutionOrderStats() {
  return request({
    url: '/order/institution/stats',
    method: 'get'
  })
}

// ==================== 管理员端接口 ====================

/**
 * 修改订单状态（管理员）
 */
export function updateOrderStatus(id, status) {
  return request({
    url: `/order/${id}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 获取全部订单统计（管理员）
 */
export function getAllOrderStats() {
  return request({
    url: '/order/admin/stats',
    method: 'get'
  })
}
