import request from '@/utils/request'

/**
 * 机构登录
 */
export function institutionLogin(data) {
  return request({
    url: '/institution/login',
    method: 'post',
    data
  })
}

/**
 * 机构登出
 */
export function institutionLogout() {
  return request({
    url: '/institution/logout',
    method: 'post'
  })
}

/**
 * 获取机构信息
 */
export function getInstitutionInfo() {
  return request({
    url: '/institution/profile',
    method: 'get'
  })
}

/**
 * 更新机构信息
 */
export function updateInstitutionInfo(data) {
  return request({
    url: '/institution/profile',
    method: 'put',
    data
  })
}

/**
 * 获取机构下的保姆列表
 * @param {Object} params - { status, pageNum, pageSize }
 */
export function getNannyList(params) {
  return request({
    url: '/institution/nannies',
    method: 'get',
    params
  })
}

/**
 * 审核保姆
 * @param {Number} nannyId - 保姆ID
 * @param {Number} status - 审核状态（1-通过，2-拒绝）
 */
export function auditNanny(nannyId, status) {
  return request({
    url: `/institution/nannies/${nannyId}/audit`,
    method: 'put',
    params: { status }
  })
}

/**
 * 修改保姆信息（机构专用）
 * @param {Number} nannyId - 保姆ID
 * @param {Object} data - 保姆信息
 */
export function updateNannyInfo(nannyId, data) {
  return request({
    url: `/institution/nanny/${nannyId}/info`,
    method: 'put',
    data
  })
}

/**
 * 删除保姆（机构解雇）
 * @param {Number} nannyId - 保姆ID
 */
export function deleteNanny(nannyId) {
  return request({
    url: `/institution/nanny/${nannyId}`,
    method: 'delete'
  })
}

/**
 * 获取保姆绩效数据
 * @param {Number} nannyId - 保姆ID
 */
export function getNannyPerformance(nannyId) {
  return request({
    url: `/institution/nanny/${nannyId}/performance`,
    method: 'get'
  })
}

/**
 * 获取所有机构列表（管理员用，用于下拉选择）
 */
export function getInstitutionList() {
  return request({
    url: '/institution/admin/all',
    method: 'get'
  })
}

// ==================== 排班管理 ====================

/**
 * 新增排班
 * @param {Object} data - 排班数据
 */
export function createSchedule(data) {
  return request({
    url: '/nanny-schedule',
    method: 'post',
    data
  })
}

/**
 * 批量新增排班
 * @param {Array} data - 排班数据数组
 */
export function batchCreateSchedule(data) {
  return request({
    url: '/nanny-schedule/batch',
    method: 'post',
    data
  })
}

/**
 * 获取排班列表
 * @param {Object} params - { nannyId, startDate, endDate, pageNum, pageSize }
 */
export function getScheduleList(params) {
  return request({
    url: '/nanny-schedule/list',
    method: 'get',
    params
  })
}

/**
 * 修改排班
 * @param {Number} id - 排班ID
 * @param {Object} data - 排班数据
 */
export function updateSchedule(id, data) {
  return request({
    url: `/nanny-schedule/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除排班
 * @param {Number} id - 排班ID
 */
export function deleteSchedule(id) {
  return request({
    url: `/nanny-schedule/${id}`,
    method: 'delete'
  })
}

/**
 * 获取日历视图
 * @param {Number} year - 年份
 * @param {Number} month - 月份
 */
export function getCalendarView(year, month) {
  return request({
    url: '/nanny-schedule/calendar',
    method: 'get',
    params: { year, month }
  })
}

/**
 * 获取保姆的排班
 * @param {Number} nannyId - 保姆ID
 * @param {Object} params - { startDate, endDate }
 */
export function getNannySchedule(nannyId, params) {
  return request({
    url: `/nanny-schedule/nanny/${nannyId}`,
    method: 'get',
    params
  })
}

/**
 * 审核调班申请
 * @param {Number} id - 排班ID
 * @param {Boolean} approved - 是否批准
 */
export function approveSwap(id, approved) {
  return request({
    url: `/nanny-schedule/${id}/swap-approve`,
    method: 'put',
    params: { approved }
  })
}

/**
 * 获取调班申请列表
 * @param {Object} params - { pageNum, pageSize }
 */
export function getSwapRequests(params) {
  return request({
    url: '/nanny-schedule/swap-requests',
    method: 'get',
    params
  })
}

/**
 * 检测排班冲突
 * @param {Object} params - { nannyId, scheduleDate, shiftType, excludeId }
 */
export function checkScheduleConflicts(params) {
  return request({
    url: '/nanny-schedule/conflicts',
    method: 'get',
    params
  })
}
