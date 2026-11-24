import axios from 'axios'
import { MessagePlugin } from 'tdesign-vue-next'
import router from '@/router'

const request = axios.create({
  baseURL: '',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = (localStorage.getItem('c_token') || '').trim()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      MessagePlugin.error(res.message || '请求失败')
      if (res.code === 401) {
        localStorage.removeItem('c_token')
        localStorage.removeItem('c_user')
        router.push('/login')
      }
      return Promise.reject(res)
    }
    return res
  },
  error => {
    MessagePlugin.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
