import request from '@/utils/request'

// ==================== 成长记录管理 ====================

/**
 * 获取宝宝的成长记录列表
 * @param {Number} babyId - 宝宝ID
 * @param {Number} recordType - 记录类型（1-饮食，2-睡眠，3-排便，4-活动，5-其他）可选
 */
export function getGrowthRecords(babyId, recordType) {
  const params = {}
  if (recordType) params.recordType = recordType
  return request.get(`/api/baby/${babyId}/growth-records`, { params })
}

/**
 * 获取成长记录详情
 * @param {Number} id - 记录ID
 */
export function getGrowthRecordById(id) {
  return request.get(`/api/baby-growth-record/${id}`)
}

/**
 * 新增成长记录
 * @param {Number} babyId - 宝宝ID
 * @param {Object} data - 成长记录数据
 */
export function addGrowthRecord(babyId, data) {
  return request.post(`/api/baby/${babyId}/growth-record`, data)
}

/**
 * 修改成长记录
 * @param {Number} id - 记录ID
 * @param {Object} data - 成长记录数据
 */
export function updateGrowthRecord(id, data) {
  return request.put(`/api/baby-growth-record/${id}`, data)
}

/**
 * 删除成长记录
 * @param {Number} id - 记录ID
 */
export function deleteGrowthRecord(id) {
  return request.delete(`/api/baby-growth-record/${id}`)
}

/**
 * 获取成长记录时间线
 * @param {Number} babyId - 宝宝ID
 * @param {String} startDate - 开始日期 (可选)
 * @param {String} endDate - 结束日期 (可选)
 */
export function getGrowthRecordsTimeline(babyId, startDate, endDate) {
  const params = {}
  if (startDate) params.startDate = startDate
  if (endDate) params.endDate = endDate
  return request.get(`/api/baby/${babyId}/growth-records/timeline`, { params })
}

/**
 * 为成长记录上传图片
 * @param {Number} id - 记录ID
 * @param {Array} imageUrls - 图片URL列表
 */
export function uploadGrowthRecordImages(id, imageUrls) {
  return request.post(`/api/baby-growth-record/${id}/images`, imageUrls)
}
