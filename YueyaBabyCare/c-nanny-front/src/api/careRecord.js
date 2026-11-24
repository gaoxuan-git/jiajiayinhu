import request from '@/utils/request'

/**
 * 添加护理记录
 * @param {Number} carePlanId - 护理计划ID
 * @param {Object} data - 记录数据 { careType, careContent, images, remark }
 */
export function addCareRecord(carePlanId, data) {
  return request({
    url: `/api/care-plan/${carePlanId}/record`,
    method: 'post',
    data
  })
}

/**
 * 获取护理计划的记录列表
 * @param {Number} carePlanId - 护理计划ID
 */
export function getCareRecords(carePlanId) {
  return request({
    url: `/api/care-plan/${carePlanId}/records`,
    method: 'get'
  })
}

/**
 * 获取护理记录详情
 * @param {Number} id - 记录ID
 */
export function getCareRecordById(id) {
  return request({
    url: `/api/care-record/${id}`,
    method: 'get'
  })
}

/**
 * 删除护理记录
 * @param {Number} id - 记录ID
 */
export function deleteCareRecord(id) {
  return request({
    url: `/api/care-record/${id}`,
    method: 'delete'
  })
}

/**
 * 检查是否可以执行计划（今日已签到未签退）
 * @param {Number} carePlanId - 护理计划ID
 */
export function canExecutePlan(carePlanId) {
  return request({
    url: `/api/care-plan/${carePlanId}/can-execute`,
    method: 'get'
  })
}

/**
 * 修改护理记录
 * @param {Number} id - 记录ID
 * @param {Object} data - 记录数据 { careType, careContent, images, remark }
 */
export function updateCareRecord(id, data) {
  return request({
    url: `/api/care-record/${id}`,
    method: 'put',
    data
  })
}

/**
 * 获取今日护理记录
 */
export function getTodayRecords() {
  return request({
    url: '/api/care-record/today',
    method: 'get'
  })
}
