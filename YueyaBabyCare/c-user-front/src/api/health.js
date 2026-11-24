import request from '@/utils/request'

// ==================== 健康档案管理 ====================

/**
 * 获取宝宝的健康记录列表
 * @param {Number} babyId - 宝宝ID
 */
export function getHealthRecords(babyId) {
  return request.get(`/api/baby/${babyId}/health-records`)
}

/**
 * 获取健康记录详情
 * @param {Number} id - 记录ID
 */
export function getHealthRecordById(id) {
  return request.get(`/api/baby-health-record/${id}`)
}

/**
 * 新增健康记录
 * @param {Number} babyId - 宝宝ID
 * @param {Object} data - 健康记录数据
 */
export function addHealthRecord(babyId, data) {
  return request.post(`/api/baby/${babyId}/health-record`, data)
}

/**
 * 修改健康记录
 * @param {Number} id - 记录ID
 * @param {Object} data - 健康记录数据
 */
export function updateHealthRecord(id, data) {
  return request.put(`/api/baby-health-record/${id}`, data)
}

/**
 * 删除健康记录
 * @param {Number} id - 记录ID
 */
export function deleteHealthRecord(id) {
  return request.delete(`/api/baby-health-record/${id}`)
}

/**
 * 获取健康数据图表
 * @param {Number} babyId - 宝宝ID
 * @param {String} startDate - 开始日期 (可选)
 * @param {String} endDate - 结束日期 (可选)
 */
export function getHealthChartData(babyId, startDate, endDate) {
  const params = {}
  if (startDate) params.startDate = startDate
  if (endDate) params.endDate = endDate
  return request.get(`/api/baby/${babyId}/health-records/chart`, { params })
}

/**
 * 获取宝宝的最新健康记录
 * @param {Number} babyId - 宝宝ID
 */
export function getLatestHealthRecord(babyId) {
  return request.get(`/api/baby/${babyId}/latest-health-record`)
}
