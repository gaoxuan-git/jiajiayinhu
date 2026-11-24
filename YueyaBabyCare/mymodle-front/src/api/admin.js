import request from '@/utils/request'

// ==================== 用户管理 ====================

/**
 * 用户登录
 */
export function login(data) {
  return request({
    url: '/admin/login',
    method: 'post',
    data
  })
}

/**
 * 用户登出
 */
export function logout() {
  return request({
    url: '/admin/logout',
    method: 'post'
  })
}

/**
 * 获取当前用户信息
 */
export function getUserInfo() {
  return request({
    url: '/admin/info',
    method: 'get'
  })
}

/**
 * 用户注册
 */
export function register(data) {
  return request({
    url: '/admin/register',
    method: 'post',
    data
  })
}

/**
 * 分页查询用户列表
 */
export function getUserPage(params) {
  return request({
    url: '/admin/page',
    method: 'get',
    params
  })
}

/**
 * 根据ID查询用户
 */
export function getUserById(id) {
  return request({
    url: `/admin/${id}`,
    method: 'get'
  })
}

/**
 * 新增用户
 */
export function saveUser(data) {
  return request({
    url: '/admin',
    method: 'post',
    data
  })
}

/**
 * 更新用户
 */
export function updateUser(data) {
  return request({
    url: '/admin',
    method: 'put',
    data
  })
}

/**
 * 删除用户
 */
export function deleteUser(id) {
  return request({
    url: `/admin/${id}`,
    method: 'delete'
  })
}

// ==================== 家长管理 ====================

/**
 * 获取家长列表
 */
export function getParents(params) {
  return request({
    url: '/admin/manage/parents',
    method: 'get',
    params
  })
}

/**
 * 查询家长详情
 */
export function getParentById(id) {
  return request({
    url: `/admin/manage/parents/${id}`,
    method: 'get'
  })
}

/**
 * 更新家长状态
 */
export function updateParentStatus(id, status) {
  return request({
    url: `/admin/manage/parents/${id}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 删除家长
 */
export function deleteParent(id) {
  return request({
    url: `/admin/manage/parents/${id}`,
    method: 'delete'
  })
}

// ==================== 机构管理 ====================

/**
 * 获取机构列表
 */
export function getInstitutions(params) {
  return request({
    url: '/admin/manage/institutions',
    method: 'get',
    params
  })
}

/**
 * 查询机构详情
 */
export function getInstitutionById(id) {
  return request({
    url: `/admin/manage/institutions/${id}`,
    method: 'get'
  })
}

/**
 * 更新机构状态
 */
export function updateInstitutionStatus(id, status) {
  return request({
    url: `/admin/manage/institutions/${id}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 删除机构
 */
export function deleteInstitution(id) {
  return request({
    url: `/admin/manage/institutions/${id}`,
    method: 'delete'
  })
}

// ==================== 保姆管理 ====================

/**
 * 获取保姆列表
 */
export function getNannies(params) {
  return request({
    url: '/admin/manage/nannies',
    method: 'get',
    params
  })
}

/**
 * 查询保姆详情
 */
export function getNannyById(id) {
  return request({
    url: `/admin/manage/nannies/${id}`,
    method: 'get'
  })
}

/**
 * 更新保姆状态
 */
export function updateNannyStatus(id, status) {
  return request({
    url: `/admin/manage/nannies/${id}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 删除保姆
 */
export function deleteNanny(id) {
  return request({
    url: `/admin/manage/nannies/${id}`,
    method: 'delete'
  })
}
