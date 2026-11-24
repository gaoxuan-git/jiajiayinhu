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
 * 批量标记消息已读
 */
export function batchMarkAsRead(ids) {
  return request({
    url: '/api/message/batch-read',
    method: 'put',
    data: ids
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
 * 家长端：获取有业务关系的机构列表
 */
export function getParentInstitutions(keyword) {
  return request({
    url: '/api/message/parent/institutions',
    method: 'get',
    params: { keyword }
  })
}

/**
 * 家长端：获取服务过的保姆列表
 */
export function getParentNannies(keyword) {
  return request({
    url: '/api/message/parent/nannies',
    method: 'get',
    params: { keyword }
  })
}