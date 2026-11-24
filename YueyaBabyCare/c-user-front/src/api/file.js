import request from '@/utils/request'

// 单文件上传（字段名：file）
export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  // axios 会自动设置 multipart/form-data 的 boundary
  return request.post('/api/file/upload', formData)
}
