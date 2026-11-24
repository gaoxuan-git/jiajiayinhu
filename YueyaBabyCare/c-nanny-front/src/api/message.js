import request from '@/utils/request'

/**
 * 发送消息
 */
export function sendMessage(data) {
  return request({
    url: '/api/message/send',
    method: 'post',
    data
  })
}

/**
 * 获取未读消息数
 */
export function getUnreadCount() {
  return request({
    url: '/api/message/unread-count',
    method: 'get'
  })
}

/**
 * 获取消息列表
 */
export function getMessageList(params) {
  return request({
    url: '/api/message/list',
    method: 'get',
    params
  })
}

/**
 * 标记消息已读
 */
export function markAsRead(id) {
  return request({
    url: `/api/message/${id}/read`,
    method: 'put'
  })
}

/**
 * 标记所有消息已读
 */
export function markAllAsRead() {
  return request({
    url: '/api/message/read-all',
    method: 'put'
  })
}

/**
 * 删除消息
 */
export function deleteMessage(id) {
  return request({
    url: `/api/message/${id}`,
    method: 'delete'
  })
}

/**
 * 获取对话记录
 */
export function getConversation(params) {
  return request({
    url: '/api/message/conversation',
    method: 'get',
    params
  })
}

/**
 * 获取联系人列表
 */
export function getContacts() {
  return request({
    url: '/api/message/contacts',
    method: 'get'
  })
}

/**
 * 标记与某联系人的所有消息已读
 */
export function markConversationAsRead(contactType, contactId) {
  return request({
    url: '/api/message/conversation/read',
    method: 'put',
    params: { contactType, contactId }
  })
}

// ==================== 9.3 选择联系人功能 ====================

/**
 * 保姆端：获取服务过的家长列表
 */
export function getNannyParents(keyword) {
  return request({
    url: '/api/message/nanny/parents',
    method: 'get',
    params: { keyword }
  })
}

/**
 * 保姆端：获取所属机构
 */
export function getNannyInstitutions() {
  return request({
    url: '/api/message/nanny/institutions',
    method: 'get'
  })
}