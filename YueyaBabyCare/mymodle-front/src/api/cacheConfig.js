import request from '@/utils/request'

/**
 * 获取所有缓存配置
 */
export function getAllCacheConfigs() {
  return request({
    url: '/cache-config/list',
    method: 'get'
  })
}

/**
 * 获取指定缓存配置
 */
export function getCacheConfig(cacheName) {
  return request({
    url: `/cache-config/${cacheName}`,
    method: 'get'
  })
}

/**
 * 更新缓存配置
 */
export function updateCacheConfig(data) {
  return request({
    url: '/cache-config',
    method: 'put',
    data
  })
}

/**
 * 清空指定缓存
 */
export function clearCache(cacheName) {
  return request({
    url: `/cache-config/${cacheName}`,
    method: 'delete'
  })
}

/**
 * 清空所有缓存
 */
export function clearAllCaches() {
  return request({
    url: '/cache-config/clear-all',
    method: 'delete'
  })
}

/**
 * 获取 Redis 信息
 */
export function getRedisInfo() {
  return request({
    url: '/cache-config/redis-info',
    method: 'get'
  })
}

