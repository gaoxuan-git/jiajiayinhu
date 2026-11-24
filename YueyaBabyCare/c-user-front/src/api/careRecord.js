import request from '@/utils/request'

/**
 * 获取婴幼儿的护理记录
 * @param {Number} babyId - 婴幼儿ID
 */
export function getCareRecordsByBaby(babyId) {
  return request.get(`/api/baby/${babyId}/care-records`)
}

/**
 * 获取护理计划的护理记录
 * @param {Number} carePlanId - 护理计划ID
 */
export function getCareRecordsByPlan(carePlanId) {
  return request.get(`/api/care-plan/${carePlanId}/records`)
}

/**
 * 获取护理记录详情
 * @param {Number} id - 记录ID
 */
export function getCareRecordById(id) {
  return request.get(`/api/care-record/${id}`)
}
