import request from '@/utils/request'

/**
 * 保姆API
 */

// 保姆注册
export function register(data) {
  return request({
    url: '/api/nanny/register',
    method: 'post',
    data
  })
}

// 保姆登录
export function login(data) {
  return request({
    url: '/api/nanny/login',
    method: 'post',
    data
  })
}

// 保姆登出
export function logout() {
  return request({
    url: '/api/nanny/logout',
    method: 'post'
  })
}

// 获取保姆信息
export function getProfile() {
  return request({
    url: '/api/nanny/profile',
    method: 'get'
  })
}

// 修改保姆信息
export function updateProfile(data) {
  return request({
    url: '/api/nanny/profile',
    method: 'put',
    data
  })
}

// 上传资格证书
export function addCertificate(certificateUrl) {
  return request({
    url: '/api/nanny/certificate',
    method: 'post',
    data: { certificateUrl }
  })
}

// 获取证书列表
export function getCertificates() {
  return request({
    url: '/api/nanny/certificates',
    method: 'get'
  })
}

// 删除证书
export function deleteCertificate(certificateUrl) {
  return request({
    url: '/api/nanny/certificate',
    method: 'delete',
    data: { certificateUrl }
  })
}

// 获取所有机构列表
export function getInstitutions() {
  return request({
    url: '/api/institution/nanny/list',
    method: 'get'
  })
}

// ============ 排班管理 API ============

// 获取保姆的排班列表
export function getMySchedules(params) {
  return request({
    url: '/api/nanny-schedule/nanny/my',
    method: 'get',
    params
  })
}

// 获取排班详情
export function getScheduleDetail(id) {
  return request({
    url: `/api/nanny-schedule/${id}`,
    method: 'get'
  })
}

// 申请调班
export function applySwap(id, data) {
  return request({
    url: `/api/nanny-schedule/${id}/swap`,
    method: 'post',
    data
  })
}

// 获取同机构可调班的保姆列表
export function getSwapNannies(scheduleId) {
  return request({
    url: `/api/nanny-schedule/${scheduleId}/swap-nannies`,
    method: 'get'
  })
}
