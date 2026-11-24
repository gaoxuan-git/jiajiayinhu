/**
 * 获取图片完整URL
 * @param {string} path - 图片路径（可能是相对路径或完整URL）
 * @returns {string} 完整的图片URL
 */
export function getImageUrl(path) {
  if (!path) return ''
  
  // 如果已经是完整URL（http/https开头），直接返回
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path
  }
  
  // 开发环境：使用代理，路径已经正确
  // 生产环境：需要根据实际部署情况调整
  // 这里直接返回相对路径，由vue.config.js的proxy或nginx处理
  return path
}
