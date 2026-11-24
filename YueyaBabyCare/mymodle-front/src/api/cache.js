import request from '@/utils/request'

/**
 * 扫描可缓存方法
 */
export function scanCacheableMethods() {
  return request({
    url: '/cache-annotation/scan',
    method: 'get'
  })
}

/**
 * 添加缓存注解
 */
export function addCacheAnnotations(data) {
  return request({
    url: '/cache-annotation/add',
    method: 'post',
    data
  })
}

