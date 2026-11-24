import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, register as registerApi, logout as logoutApi, getMemberInfo } from '@/api/member'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('c_token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('c_user') || 'null'))

  const isLogin = computed(() => !!token.value)
  const nickname = computed(() => userInfo.value?.nickname || '会员')

  // 登录
  async function login(data) {
    const res = await loginApi(data)
    token.value = res.data.token
    userInfo.value = res.data
    localStorage.setItem('c_token', res.data.token)
    localStorage.setItem('c_user', JSON.stringify(res.data))
    return res
  }

  // 注册
  async function register(data) {
    const res = await registerApi(data)
    token.value = res.data.token
    userInfo.value = res.data
    localStorage.setItem('c_token', res.data.token)
    localStorage.setItem('c_user', JSON.stringify(res.data))
    return res
  }

  // 登出
  async function logout() {
    try {
      await logoutApi()
    } catch (e) {
      // 忽略错误
    }
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('c_token')
    localStorage.removeItem('c_user')
  }

  // 获取用户信息
  async function fetchUserInfo() {
    if (!token.value) return
    try {
      const res = await getMemberInfo()
      userInfo.value = res.data
      localStorage.setItem('c_user', JSON.stringify(res.data))
    } catch (e) {
      logout()
    }
  }

  return {
    token,
    userInfo,
    isLogin,
    nickname,
    login,
    register,
    logout,
    fetchUserInfo
  }
})
