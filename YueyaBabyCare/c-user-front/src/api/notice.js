import request from '@/utils/request'

/**
 * 获取已发布的公告列表（用户端）
 */
export function getPublishedNoticeList(params) {
  return request({
    url: '/api/notice/published',
    method: 'get',
    params
  })
}

/**
 * 获取公告详情
 */
export function getNoticeById(id) {
  return request({
    url: `/api/notice/${id}`,
    method: 'get'
  })
}

/**
 * 获取最新公告列表
 */
export function getLatestNotices(limit = 5) {
  return request({
    url: '/api/notice/latest',
    method: 'get',
    params: { limit }
  })
}