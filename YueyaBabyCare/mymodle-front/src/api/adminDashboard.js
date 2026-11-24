import request from '@/utils/request'

/**
 * 获取管理后台首页数据
 */
export function getAdminDashboard() {
  return request({
    url: '/admin/dashboard',
    method: 'get'
  })
}

/**
 * 获取用户统计
 */
export function getUserStats() {
  return request({
    url: '/admin/user-stats',
    method: 'get'
  })
}

/**
 * 获取机构统计
 */
export function getInstitutionStats() {
  return request({
    url: '/admin/institution-stats',
    method: 'get'
  })
}

/**
 * 获取订单统计
 */
export function getOrderStats() {
  return request({
    url: '/admin/order-stats',
    method: 'get'
  })
}

/**
 * 获取收入统计
 */
export function getRevenueStats() {
  return request({
    url: '/admin/revenue-stats',
    method: 'get'
  })
}

/**
 * 生成报表
 * @param {String} type - 报表类型（order-订单报表，revenue-收入报表，user-用户报表）
 * @param {String} startDate - 开始日期（yyyy-MM-dd）
 * @param {String} endDate - 结束日期（yyyy-MM-dd）
 */
export function getReports(type, startDate, endDate) {
  return request({
    url: '/admin/reports',
    method: 'get',
    params: { type, startDate, endDate }
  })
}
