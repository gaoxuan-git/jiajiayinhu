import request from '@/utils/request'

/**
 * 获取机构的护理计划列表（分页）
 * @param {Object} params - { pageNum, pageSize, status, babyId, nannyId, keyword }
 */
export function getCarePlanList(params) {
  return request({
    url: '/institution/care-plans',
    method: 'get',
    params
  })
}

/**
 * 获取护理计划详情
 * @param {Number} id - 计划ID
 */
export function getCarePlanById(id) {
  return request({
    url: `/care-plan/${id}`,
    method: 'get'
  })
}

/**
 * 创建护理计划
 * @param {Object} data - { appointmentId, planName, planContent, startDate, endDate, babyId?, nannyId? }
 */
export function createCarePlan(data) {
  return request({
    url: '/care-plan',
    method: 'post',
    data
  })
}

/**
 * 修改护理计划
 * @param {Number} id - 计划ID
 * @param {Object} data - { planName, planContent, startDate, endDate }
 */
export function updateCarePlan(id, data) {
  return request({
    url: `/care-plan/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除护理计划
 * @param {Number} id - 计划ID
 */
export function deleteCarePlan(id) {
  return request({
    url: `/care-plan/${id}`,
    method: 'delete'
  })
}

/**
 * 修改护理计划状态
 * @param {Number} id - 计划ID
 * @param {Number} status - 新状态 (0-待执行, 1-进行中, 2-已完成, 3-已取消)
 */
export function updateCarePlanStatus(id, status) {
  return request({
    url: `/care-plan/${id}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 为护理计划分配保姆
 * @param {Number} id - 计划ID
 * @param {Number} nannyId - 保姆ID
 */
export function assignNanny(id, nannyId) {
  return request({
    url: `/care-plan/${id}/assign-nanny`,
    method: 'post',
    params: { nannyId }
  })
}

/**
 * 获取可创建护理计划的预约列表（已确认/进行中且未创建过计划的预约）
 */
export function getAvailableAppointments() {
  return request({
    url: '/institution/care-plans/available-appointments',
    method: 'get'
  })
}
