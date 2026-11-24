import request from '@/utils/request'

/**
 * 获取机构仪表板数据
 */
export function getDashboard() {
  return request({
    url: '/institution/dashboard',
    method: 'get'
  })
}

/**
 * 获取保姆绩效排名
 * @param {String} type - 排名类型（order-订单数，revenue-收入，rating-评分）
 */
export function getNannyRanking(type = 'order') {
  return request({
    url: '/institution/dashboard/nanny-ranking',
    method: 'get',
    params: { type }
  })
}

/**
 * 获取服务数据统计
 */
export function getServiceStats() {
  return request({
    url: '/institution/dashboard/service-stats',
    method: 'get'
  })
}

/**
 * 获取评价分析
 */
export function getEvaluationStats() {
  return request({
    url: '/institution/dashboard/evaluation-stats',
    method: 'get'
  })
}

/**
 * 获取财务数据
 */
export function getFinanceStats() {
  return request({
    url: '/institution/dashboard/finance-stats',
    method: 'get'
  })
}

/**
 * 导出报表数据
 * @param {String} type - 报表类型（order-订单报表，revenue-收入报表，service-服务报表）
 * @param {String} startDate - 开始日期（yyyy-MM-dd）
 * @param {String} endDate - 结束日期（yyyy-MM-dd）
 */
export function exportReport(type, startDate, endDate) {
  return request({
    url: '/institution/dashboard/export-report',
    method: 'get',
    params: { type, startDate, endDate }
  })
}
