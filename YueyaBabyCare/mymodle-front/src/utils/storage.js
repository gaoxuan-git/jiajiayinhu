/**
 * 本地存储工具类
 */

const TOKEN_KEY = 'token'
const USER_INFO_KEY = 'userInfo'

export default {
  /**
   * 设置 Token
   */
  setToken(token) {
    localStorage.setItem(TOKEN_KEY, token)
  },

  /**
   * 获取 Token
   */
  getToken() {
    return localStorage.getItem(TOKEN_KEY)
  },

  /**
   * 移除 Token
   */
  removeToken() {
    localStorage.removeItem(TOKEN_KEY)
  },

  /**
   * 设置用户信息
   */
  setUserInfo(userInfo) {
    localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo))
  },

  /**
   * 获取用户信息
   */
  getUserInfo() {
    const userInfo = localStorage.getItem(USER_INFO_KEY)
    return userInfo ? JSON.parse(userInfo) : null
  },

  /**
   * 移除用户信息
   */
  removeUserInfo() {
    localStorage.removeItem(USER_INFO_KEY)
  },

  /**
   * 清空所有存储
   */
  clear() {
    localStorage.clear()
  },

  /**
   * 通用的 set 方法
   */
  set(key, value) {
    localStorage.setItem(key, JSON.stringify(value))
  },

  /**
   * 通用的 get 方法
   */
  get(key) {
    const value = localStorage.getItem(key)
    try {
      return value ? JSON.parse(value) : null
    } catch (e) {
      return value
    }
  },

  /**
   * 通用的 remove 方法
   */
  remove(key) {
    localStorage.removeItem(key)
  }
}

