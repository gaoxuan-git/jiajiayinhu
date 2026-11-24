import axios from 'axios'
import { MessagePlugin } from 'tdesign-vue-next'
import { API_BASE_URL } from '@/config'

// 创建 axios 实例
const request = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从 localStorage 获取 token
    const token = localStorage.getItem('nanny_token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    
    // 判断是否成功
    if (res.code === 200 || res.code === 0) {
      return res
    } else {
      // 业务错误
      MessagePlugin.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  error => {
    console.error('响应错误:', error)
    
    // 处理401未授权
    if (error.response && error.response.status === 401) {
      MessagePlugin.error('登录已过期，请重新登录')
      localStorage.removeItem('nanny_token')
      localStorage.removeItem('nanny_user')
      // 跳转到登录页
      window.location.href = '/#/login'
      return Promise.reject(error)
    }
    
    // 其他错误
    const message = error.response?.data?.message || error.message || '网络错误'
    MessagePlugin.error(message)
    return Promise.reject(error)
  }
)

export default request
