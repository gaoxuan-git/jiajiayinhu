/**
 * 应用配置文件
 * 统一管理所有配置项
 */

// API 基础地址
// 开发环境：通过代理访问，使用相对路径（vue.config.js 配置了代理到 8080）
// 生产环境：使用环境变量或默认值
export const API_BASE_URL = process.env.VUE_APP_API_BASE_URL || ''

// 文件上传地址
export const UPLOAD_URL = API_BASE_URL + '/api/file/upload'

// 批量文件上传地址
export const BATCH_UPLOAD_URL = API_BASE_URL + '/api/file/upload/batch'

// 默认导出
export default {
  API_BASE_URL,
  UPLOAD_URL,
  BATCH_UPLOAD_URL
}
