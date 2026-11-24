import request from '@/utils/request'

/**
 * 签到
 * @param {Number} appointmentId - 预约ID
 * @param {Object} data - 签到信息 { longitude, latitude, address, remark }
 */
export function checkin(appointmentId, data) {
  return request({
    url: `/api/appointment/${appointmentId}/checkin`,
    method: 'post',
    data
  })
}

/**
 * 签退
 * @param {Number} appointmentId - 预约ID
 * @param {Object} data - 签退信息 { longitude, latitude, address, remark }
 */
export function checkout(appointmentId, data) {
  return request({
    url: `/api/appointment/${appointmentId}/checkout`,
    method: 'post',
    data
  })
}

/**
 * 获取预约的签到记录列表
 * @param {Number} appointmentId - 预约ID
 */
export function getCheckinRecords(appointmentId) {
  return request({
    url: `/api/appointment/${appointmentId}/checkin-records`,
    method: 'get'
  })
}

/**
 * 获取预约今日签到状态
 * @param {Number} appointmentId - 预约ID
 */
export function getTodayCheckin(appointmentId) {
  return request({
    url: `/api/appointment/${appointmentId}/today-checkin`,
    method: 'get'
  })
}

/**
 * 获取签到记录详情
 * @param {Number} id - 签到记录ID
 */
export function getCheckinById(id) {
  return request({
    url: `/api/checkin/${id}`,
    method: 'get'
  })
}
