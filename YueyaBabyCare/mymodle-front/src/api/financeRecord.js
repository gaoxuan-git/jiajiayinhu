import request from '@/utils/request'

/**
 * 新增财务记录
 * @param {Object} data - 财务记录数据
 */
export function addFinanceRecord(data) {
  return request({
    url: '/finance-record',
    method: 'post',
    data
  })
}

/**
 * 获取财务记录列表
 * @param {Object} params - 查询参数
 */
export function getFinanceRecordList(params) {
  return request({
    url: '/finance-record/list',
    method: 'get',
    params
  })
}

/**
 * 获取财务统计
 * @param {Object} params - 查询参数（startDate, endDate）
 */
export function getFinanceStats(params) {
  return request({
    url: '/finance-record/stats',
    method: 'get',
    params
  })
}

/**
 * 获取月度汇总
 * @param {Number} year - 年份
 */
export function getMonthlySummary(year) {
  return request({
    url: '/finance-record/monthly-summary',
    method: 'get',
    params: { year }
  })
}

/**
 * 导出财务报表
 * @param {String} startDate - 开始日期
 * @param {String} endDate - 结束日期
 */
export function exportFinanceReport(startDate, endDate) {
  return request({
    url: '/finance-record/export',
    method: 'get',
    params: { startDate, endDate }
  })
}
