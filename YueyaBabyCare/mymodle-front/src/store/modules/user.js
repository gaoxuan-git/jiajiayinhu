import { login, logout, getUserInfo } from '@/api/admin'
import { institutionLogin, institutionLogout, getInstitutionInfo } from '@/api/institution'
import storage from '@/utils/storage'

const state = {
  token: storage.getToken() || '',
  userInfo: storage.getUserInfo() || null,
  userType: storage.get('userType') || 'admin' // 用户类型：admin-管理员, institution-机构
}

const mutations = {
  SET_TOKEN(state, token) {
    state.token = token
    storage.setToken(token)
  },
  SET_USER_INFO(state, userInfo) {
    state.userInfo = userInfo
    storage.setUserInfo(userInfo)
  },
  SET_USER_TYPE(state, userType) {
    state.userType = userType
    storage.set('userType', userType)
  },
  CLEAR_USER(state) {
    state.token = ''
    state.userInfo = null
    state.userType = 'admin'
    storage.removeToken()
    storage.removeUserInfo()
    storage.remove('userType')
  }
}

const actions = {
  // 管理员登录
  login({ commit, dispatch }, userInfo) {
    const { username, password } = userInfo
    return new Promise((resolve, reject) => {
      login({ username: username.trim(), password })
        .then(response => {
          const { data } = response
          commit('SET_TOKEN', data.token)
          commit('SET_USER_TYPE', 'admin')
          // 登录成功后获取用户信息
          dispatch('getUserInfo')
            .then(() => resolve())
            .catch(() => resolve()) // 即使获取用户信息失败也算登录成功
        })
        .catch(error => {
          reject(error)
        })
    })
  },

  // 机构登录
  institutionLogin({ commit, dispatch }, userInfo) {
    const { username, password } = userInfo
    return new Promise((resolve, reject) => {
      institutionLogin({ username: username.trim(), password })
        .then(response => {
          const { data } = response
          commit('SET_TOKEN', data.token)
          commit('SET_USER_TYPE', 'institution')
          // 登录成功后获取机构信息
          dispatch('getUserInfo')
            .then(() => resolve())
            .catch(() => resolve())
        })
        .catch(error => {
          reject(error)
        })
    })
  },

  // 获取用户信息（从 Redis）- 根据用户类型调用不同接口
  getUserInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      const apiFunc = state.userType === 'institution' ? getInstitutionInfo : getUserInfo
      apiFunc()
        .then(response => {
          const { data } = response
          commit('SET_USER_INFO', data)
          resolve(data)
        })
        .catch(error => {
          reject(error)
        })
    })
  },

  // 用户登出 - 根据用户类型调用不同接口
  logout({ commit, state }) {
    return new Promise((resolve, reject) => {
      const apiFunc = state.userType === 'institution' ? institutionLogout : logout
      apiFunc()
        .then(() => {
          commit('CLEAR_USER')
          resolve()
        })
        .catch(error => {
          // 即使后端登出失败，也清除前端数据
          commit('CLEAR_USER')
          reject(error)
        })
    })
  },

  // 设置用户信息
  setUserInfo({ commit }, userInfo) {
    commit('SET_USER_INFO', userInfo)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

