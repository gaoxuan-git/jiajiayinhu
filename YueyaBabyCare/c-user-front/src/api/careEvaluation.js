import request from '@/utils/request'

/**
 * 新增评价（家长）
 * @param {Object} data - 评价信息
 */
export function addCareEvaluation(data) {
  return request.post('/api/care-evaluation', data)
}

/**
 * 修改评价（家长）
 * @param {Number} id - 评价ID
 * @param {Object} data - 评价信息
 */
export function updateCareEvaluation(id, data) {
  return request.put(`/api/care-evaluation/${id}`, data)
}

/**
 * 删除评价（家长）
 * @param {Number} id - 评价ID
 */
export function deleteCareEvaluation(id) {
  return request.delete(`/api/care-evaluation/${id}`)
}

/**
 * 获取评价详情
 * @param {Number} id - 评价ID
 */
export function getCareEvaluationById(id) {
  return request.get(`/api/care-evaluation/${id}`)
}

/**
 * 获取保姆的评价列表
 * @param {Number} nannyId - 保姆ID
 */
export function getNannyEvaluations(nannyId) {
  return request.get(`/api/nanny/${nannyId}/evaluations`)
}

/**
 * 获取保姆的评分统计
 * @param {Number} nannyId - 保姆ID
 */
export function getNannyRatingStats(nannyId) {
  return request.get(`/api/nanny/${nannyId}/rating-stats`)
}

/**
 * 获取我的评价列表（家长）
 */
export function getMyEvaluations() {
  return request.get('/api/care-evaluation/my-list')
}

/**
 * 检查护理计划是否已评价
 * @param {Number} carePlanId - 护理计划ID
 */
export function checkEvaluated(carePlanId) {
  return request.get(`/api/care-evaluation/check/${carePlanId}`)
}
