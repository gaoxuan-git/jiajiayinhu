import request from '@/utils/request'

// ==================== 婴幼儿健康档案管理 ====================

/**
 * 分页查询健康记录（管理员）
 * @param {Object} params - 查询参数
 * @param {Number} params.pageNum - 页码
 * @param {Number} params.pageSize - 每页数量
 * @param {Number} params.babyId - 宝宝ID（可选）
 * @param {Number} params.parentId - 家长ID（可选）
 * @param {Number} params.recorderType - 记录人类型（可选）
 * @param {String} params.startDate - 开始日期（可选）
 * @param {String} params.endDate - 结束日期（可选）
 */
export function pageHealthRecords(params) {
  return request({
    url: '/admin/baby-health-records',
    method: 'get',
    params
  })
}

/**
 * 根据家长ID查询健康记录（管理员）
 * @param {Number} parentId - 家长ID
 */
export function getHealthRecordsByParentId(parentId) {
  return request({
    url: `/admin/parent/${parentId}/health-records`,
    method: 'get'
  })
}

/**
 * 根据宝宝ID查询健康记录（管理员）
 * @param {Number} babyId - 宝宝ID
 */
export function getHealthRecordsByBabyId(babyId) {
  return request({
    url: `/admin/baby/${babyId}/health-records`,
    method: 'get'
  })
}

/**
 * 获取健康记录详情
 * @param {Number} id - 记录ID
 */
export function getHealthRecordById(id) {
  return request({
    url: `/baby-health-record/${id}`,
    method: 'get'
  })
}

/**
 * 获取健康数据图表（管理员）
 * @param {Number} babyId - 宝宝ID
 * @param {String} startDate - 开始日期（可选）
 * @param {String} endDate - 结束日期（可选）
 */
export function getHealthChartData(babyId, startDate, endDate) {
  return request({
    url: `/admin/baby/${babyId}/health-records/chart`,
    method: 'get',
    params: { startDate, endDate }
  })
}

/**
 * 获取最新健康记录
 * @param {Number} babyId - 宝宝ID
 */
export function getLatestHealthRecord(babyId) {
  return request({
    url: `/baby/${babyId}/latest-health-record`,
    method: 'get'
  })
}
