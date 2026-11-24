import request from '@/utils/request'

/**
 * 预约管理API
 */

/**
 * 创建预约
 */
export function createAppointment(data) {
  return request({
    url: '/api/appointment',
    method: 'post',
    data
  })
}

/**
 * 获取预约详情
 */
export function getAppointmentDetail(id) {
  return request({
    url: `/api/appointment/${id}`,
    method: 'get'
  })
}

/**
 * 修改预约
 */
export function updateAppointment(id, data) {
  return request({
    url: `/api/appointment/${id}`,
    method: 'put',
    data
  })
}

/**
 * 取消预约
 */
export function cancelAppointment(id, cancelReason) {
  return request({
    url: `/api/appointment/${id}`,
    method: 'delete',
    params: { cancelReason }
  })
}

/**
 * 获取我的预约列表
 */
export function getMyAppointments(status) {
  return request({
    url: '/api/appointment/my-appointments',
    method: 'get',
    params: { status }
  })
}

/**
 * 获取可预约时间段
 */
export function getAvailableSlots(institutionId, nannyId, date) {
  return request({
    url: '/api/appointment/available-slots',
    method: 'get',
    params: { institutionId, nannyId, date }
  })
}
