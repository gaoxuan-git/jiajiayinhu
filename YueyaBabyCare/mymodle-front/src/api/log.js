import request from '@/utils/request'

/**
 * 获取日志文件列表
 */
export function getLogFiles() {
  return request({
    url: '/log/files',
    method: 'get'
  })
}

/**
 * 分页查询日志内容
 * @param {object} params 查询参数
 */
export function queryLogs(params) {
  return request({
    url: '/log/query',
    method: 'get',
    params
  })
}

/**
 * 下载日志文件
 * @param {string} fileName 文件名
 */
export function downloadLog(fileName) {
  return request({
    url: '/log/download',
    method: 'get',
    params: { fileName },
    responseType: 'blob'
  })
}

/**
 * 清空日志文件
 * @param {string} fileName 文件名
 */
export function clearLog(fileName) {
  return request({
    url: '/log/clear',
    method: 'delete',
    params: { fileName }
  })
}

/**
 * 删除日志文件
 * @param {string} fileName 文件名
 */
export function deleteLog(fileName) {
  return request({
    url: '/log/delete',
    method: 'delete',
    params: { fileName }
  })
}

