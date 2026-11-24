const state = {
  sidebar: {
    opened: true
  },
  device: 'desktop'
}

const mutations = {
  TOGGLE_SIDEBAR(state) {
    state.sidebar.opened = !state.sidebar.opened
  },
  CLOSE_SIDEBAR(state) {
    state.sidebar.opened = false
  },
  SET_DEVICE(state, device) {
    state.device = device
  }
}

const actions = {
  toggleSidebar({ commit }) {
    commit('TOGGLE_SIDEBAR')
  },
  closeSidebar({ commit }) {
    commit('CLOSE_SIDEBAR')
  },
  setDevice({ commit }, device) {
    commit('SET_DEVICE', device)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

