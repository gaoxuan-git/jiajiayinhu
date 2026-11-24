import request from '@/utils/request'

/**
 * 发送消息
 */
export function sendMessage(data) {
  return request({
    url: '/message/send',
    method: 'post',
    data
  })
}

/**
 * 获取未读消息数
 */
export function getUnreadCount() {
  return request({
    url: '/message/unread-count',
    method: 'get'
  })
}

/**
 * 获取消息列表
 */
export function getMessageList(params) {
  return request({
    url: '/message/list',
    method: 'get',
    params
  })
}

/**
 * 标记消息已读
 */
export function markAsRead(id) {
  return request({
    url: `/message/${id}/read`,
    method: 'put'
  })
}

/**
 * 批量标记消息已读
 */
export function batchMarkAsRead(ids) {
  return request({
    url: '/message/batch-read',
    method: 'put',
    data: ids
  })
}

/**
 * 标记所有消息已读
 */
export function markAllAsRead() {
  return request({
    url: '/message/read-all',
    method: 'put'
  })
}

/**
 * 删除消息
 */
export function deleteMessage(id) {
  return request({
    url: `/message/${id}`,
    method: 'delete'
  })
}

/**
 * 获取对话记录
 */
export function getConversation(params) {
  return request({
    url: '/message/conversation',
    method: 'get',
    params
  })
}

/**
 * 获取联系人列表
 */
export function getContacts() {
  return request({
    url: '/message/contacts',
    method: 'get'
  })
}

/**
 * 标记与某联系人的所有消息已读
 */
export function markConversationAsRead(contactType, contactId) {
  return request({
    url: '/message/conversation/read',
    method: 'put',
    params: { contactType, contactId }
  })
}

// ==================== 9.3 选择联系人功能 ====================

/**
 * 管理员端：获取所有家长列表
 */
export function getAdminParents(keyword) {
  return request({
    url: '/message/admin/parents',
    method: 'get',
    params: { keyword }
  })
}

/**
 * 管理员端：获取所有保姆列表
 */
export function getAdminNannies(keyword) {
  return request({
    url: '/message/admin/nannies',
    method: 'get',
    params: { keyword }
  })
}

/**
 * 管理员端：获取所有机构列表
 */
export function getAdminInstitutions(keyword) {
  return request({
    url: '/message/admin/institutions',
    method: 'get',
    params: { keyword }
  })
}

/**
 * 机构端：获取有业务关系的家长列表
 */
export function getInstitutionParents(keyword) {
  return request({
    url: '/message/institution/parents',
    method: 'get',
    params: { keyword }
  })
}