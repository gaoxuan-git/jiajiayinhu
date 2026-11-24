import request from '@/utils/request'

/**
 * 获取保姆的评价列表（当前登录保姆）
 */
export function getMyEvaluations() {
  return request.get('/api/care-evaluation/nanny-list')
}

/**
 * 获取保姆的评分统计（当前登录保姆）
 */
export function getMyRatingStats() {
  return request.get('/api/care-evaluation/nanny-stats')
}

/**
 * 回复评价
 * @param {Number} id - 评价ID
 * @param {String} replyContent - 回复内容
 */
export function replyEvaluation(id, replyContent) {
  return request.post(`/api/care-evaluation/${id}/reply`, replyContent, {
    headers: {
      'Content-Type': 'text/plain'
    }
  })
}

/**
 * 获取评价详情
 * @param {Number} id - 评价ID
 */
export function getEvaluationById(id) {
  return request.get(`/api/care-evaluation/${id}`)
}
