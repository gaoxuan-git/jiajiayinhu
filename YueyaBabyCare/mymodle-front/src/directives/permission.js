/**
 * 权限指令
 * 使用方式: v-permission="['admin', 'user']"
 */

export default {
  mounted(el, binding) {
    const { value } = binding
    
    if (value && value instanceof Array && value.length > 0) {
      const permissionRoles = value
      
      // 这里可以从 store 获取用户角色
      // const roles = store.getters && store.getters.roles
      // 示例：假设当前用户角色为 ['admin']
      const roles = ['admin']
      
      const hasPermission = roles.some(role => {
        return permissionRoles.includes(role)
      })
      
      if (!hasPermission) {
        // 没有权限则移除该元素
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      throw new Error('需要提供权限数组，例如: v-permission="[\'admin\', \'user\']"')
    }
  }
}

