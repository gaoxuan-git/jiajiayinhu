import request from '@/utils/request'

/**
 * 获取保姆的护理计划列表
 * @param {Number} status - 状态筛选（0-待执行，1-进行中，2-已完成，3-已取消）
 */
export function getMyCarePlans(status) {
  return request({
    url: '/api/care-plan/list',
    method: 'get',
    params: { status }
  })
}

/**
 * 获取护理计划详情
 * @param {Number} id - 计划ID
 */
export function getCarePlanById(id) {
  return request({
    url: `/api/care-plan/${id}`,
    method: 'get'
  })
}
