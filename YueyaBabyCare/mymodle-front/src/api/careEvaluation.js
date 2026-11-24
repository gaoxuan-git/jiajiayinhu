import request from '@/utils/request'

/**
 * 获取机构的评价列表
 * @param {Object} params - 查询参数
 * @param {Number} params.nannyId - 保姆ID（可选）
 * @param {Number} params.score - 评分筛选（可选）
 */
export function getInstitutionEvaluations(params) {
  return request({
    url: '/institution/evaluations',
    method: 'get',
    params
  })
}

/**
 * 获取评价详情
 * @param {Number} id - 评价ID
 */
export function getEvaluationById(id) {
  return request({
    url: `/care-evaluation/${id}`,
    method: 'get'
  })
}

/**
 * 获取保姆的评分统计
 * @param {Number} nannyId - 保姆ID
 */
export function getNannyRatingStats(nannyId) {
  return request({
    url: `/nanny/${nannyId}/rating-stats`,
    method: 'get'
  })
}

/**
 * 获取保姆的评价列表
 * @param {Number} nannyId - 保姆ID
 */
export function getNannyEvaluations(nannyId) {
  return request({
    url: `/nanny/${nannyId}/evaluations`,
    method: 'get'
  })
}
