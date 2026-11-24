import request from '@/utils/request'

/**
 * 测试数据库连接
 */
export function testConnection(data) {
  return request({
    url: '/codegen/test-connection',
    method: 'get',
    params: data
  })
}

/**
 * 获取数据库表列表
 */
export function getTables(data) {
  return request({
    url: '/codegen/tables',
    method: 'get',
    params: data
  })
}

/**
 * 生成代码
 */
export function generateCode(data) {
  return request({
    url: '/codegen/generate',
    method: 'post',
    data
  })
}

