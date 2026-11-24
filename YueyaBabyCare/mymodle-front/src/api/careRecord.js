import request from '@/utils/request'

/**
 * 获取机构护理记录列表
 * @param {Object} params - { nannyId, babyId, careType }
 */
export function getInstitutionCareRecords(params) {
  return request({
    url: '/institution/care-records',
    method: 'get',
    params
  })
}

/**
 * 获取护理记录详情
 * @param {Number} id - 记录ID
 */
export function getCareRecordById(id) {
  return request({
    url: `/care-record/${id}`,
    method: 'get'
  })
}

/**
 * 获取护理计划的记录列表
 * @param {Number} carePlanId - 计划ID
 */
export function getRecordsByCarePlan(carePlanId) {
  return request({
    url: `/care-plan/${carePlanId}/records`,
    method: 'get'
  })
}

/**
 * 获取婴幼儿的护理记录
 * @param {Number} babyId - 婴幼儿ID
 */
export function getRecordsByBaby(babyId) {
  return request({
    url: `/baby/${babyId}/care-records`,
    method: 'get'
  })
}
