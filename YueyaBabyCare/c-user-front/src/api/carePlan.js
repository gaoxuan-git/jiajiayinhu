import request from '@/utils/request'

/**
 * 获取家长的护理计划列表
 * @param {Object} params - { status }
 */
export function getCarePlanList(params) {
  return request.get('/api/care-plan/list', { params })
}

/**
 * 获取护理计划详情
 * @param {Number} id - 计划ID
 */
export function getCarePlanById(id) {
  return request.get(`/api/care-plan/${id}`)
}
