import request from '@/utils/request'

/**
 * 获取保姆详情
 */
export function getNannyDetail(id) {
  return request({
    url: `/api/nanny/${id}`,
    method: 'get'
  })
}

/**
 * 搜索保姆
 */
export function searchNannies(params) {
  return request({
    url: '/api/nanny/search',
    method: 'get',
    params
  })
}
