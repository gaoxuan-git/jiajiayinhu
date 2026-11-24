import request from '@/utils/request'

/**
 * 搜索机构列表
 */
export function searchInstitutions(params) {
  return request({
    url: '/api/institution/search',
    method: 'get',
    params
  })
}

/**
 * 获取机构详情
 */
export function getInstitutionDetail(id) {
  return request({
    url: `/api/institution/${id}`,
    method: 'get'
  })
}

/**
 * 获取机构员工数
 */
export function getInstitutionStaffCount(id) {
  return request({
    url: `/api/institution/${id}/staff-count`,
    method: 'get'
  })
}

/**
 * 获取机构服务订单数
 */
export function getInstitutionServiceCount(id) {
  return request({
    url: `/api/institution/${id}/service-count`,
    method: 'get'
  })
}

/**
 * 获取机构下的保姆列表
 */
export function getInstitutionNannies(institutionId, params) {
  return request({
    url: `/api/institution/${institutionId}/nannies`,
    method: 'get',
    params
  })
}
