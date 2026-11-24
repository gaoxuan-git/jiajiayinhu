import request from '@/utils/request'

/**
 * 获取机构的预约列表（分页）
 * @param {Object} params - { pageNum, pageSize, status, serviceType, startDate, endDate, keyword }
 */
export function getAppointmentList(params) {
  return request({
    url: '/institution/appointments',
    method: 'get',
    params
  })
}

/**
 * 获取预约详情
 * @param {Number} id - 预约ID
 */
export function getAppointmentById(id) {
  return request({
    url: `/appointment/${id}`,
    method: 'get'
  })
}

/**
 * 修改预约状态
 * @param {Number} id - 预约ID
 * @param {Number} status - 新状态 (0-待确认, 1-已确认, 2-进行中, 3-已完成, 4-已取消)
 */
export function updateAppointmentStatus(id, status) {
  return request({
    url: `/appointment/${id}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 为预约分配保姆
 * @param {Number} id - 预约ID
 * @param {Number} nannyId - 保姆ID
 */
export function assignNanny(id, nannyId) {
  return request({
    url: `/appointment/${id}/assign-nanny`,
    method: 'post',
    params: { nannyId }
  })
}

/**
 * 获取预约统计数据
 * @param {Object} params - { startDate, endDate }
 */
export function getAppointmentStats(params) {
  return request({
    url: '/institution/appointments/stats',
    method: 'get',
    params
  })
}

/**
 * 获取可预约时间段
 * @param {Object} params - { institutionId, nannyId, date }
 */
export function getAvailableSlots(params) {
  return request({
    url: '/appointment/available-slots',
    method: 'get',
    params
  })
}
