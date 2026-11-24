import { createStore } from 'vuex'
import user from './modules/user'
import app from './modules/app'

const store = createStore({
  modules: {
    user,
    app
  },
  getters: {
    token: state => state.user.token,
    userInfo: state => state.user.userInfo,
    sidebar: state => state.app.sidebar
  }
})

export default store
