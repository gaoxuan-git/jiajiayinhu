import request from '@/utils/request'

/**
 * 获取我的预约列表（保姆）
 * @param {Number} status - 状态筛选（0-待确认，1-已确认，2-进行中，3-已完成，4-已取消）
 */
export function getMyAppointments(status) {
  return request({
    url: '/api/appointment/my-appointments',
    method: 'get',
    params: { status }
  })
}

/**
 * 获取预约列表
 * @param {Number} status - 状态筛选
 */
export function getAppointmentList(status) {
  return request({
    url: '/api/appointment/list',
    method: 'get',
    params: { status }
  })
}

/**
 * 获取预约详情
 * @param {Number} id - 预约ID
 */
export function getAppointmentById(id) {
  return request({
    url: `/api/appointment/${id}`,
    method: 'get'
  })
}
