import request from '@/utils/request'

/**
 * 新增公告（管理员）
 */
export function createNotice(data) {
  return request({
    url: '/notice',
    method: 'post',
    data
  })
}

/**
 * 获取公告列表（管理员）
 */
export function getNoticeList(params) {
  return request({
    url: '/notice/list',
    method: 'get',
    params
  })
}

/**
 * 获取公告详情
 */
export function getNoticeById(id) {
  return request({
    url: `/notice/${id}`,
    method: 'get'
  })
}

/**
 * 修改公告（管理员）
 */
export function updateNotice(id, data) {
  return request({
    url: `/notice/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除公告（管理员）
 */
export function deleteNotice(id) {
  return request({
    url: `/notice/${id}`,
    method: 'delete'
  })
}

/**
 * 发布/下线公告（管理员）
 */
export function updateNoticeStatus(id, status) {
  return request({
    url: `/notice/${id}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 获取已发布的公告列表（用户端）
 */
export function getPublishedNoticeList(params) {
  return request({
    url: '/notice/published',
    method: 'get',
    params
  })
}

/**
 * 获取最新公告列表
 */
export function getLatestNotices(limit = 5) {
  return request({
    url: '/notice/latest',
    method: 'get',
    params: { limit }
  })
}