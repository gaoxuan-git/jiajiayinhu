import request from '@/utils/request'

export function getAttendanceList(params) {
  return request({
    url: '/nanny-attendance/list',
    method: 'get',
    params
  })
}

export function checkIn(data) {
  return request({
    url: '/nanny-attendance/check-in',
    method: 'post',
    data
  })
}

export function checkOut(data) {
  return request({
    url: '/nanny-attendance/check-out',
    method: 'post',
    data
  })
}

export function updateAttendanceStatus(id, data) {
  return request({
    url: `/nanny-attendance/${id}/status`,
    method: 'put',
    data
  })
}

export function getAttendanceStatistics(params) {
  return request({
    url: '/nanny-attendance/statistics',
    method: 'get',
    params
  })
}
