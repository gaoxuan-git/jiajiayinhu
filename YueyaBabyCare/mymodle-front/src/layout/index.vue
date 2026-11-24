<template>
  <div class="app-wrapper">
    <div class="sidebar-container">
      <el-menu
        :default-active="activeMenu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <div class="logo-container">
          <h1 class="logo-title"></h1>
        </div>
        
        <!-- 动态菜单 -->
        <template v-for="route in menuRoutes" :key="route.path">
          <!-- 有子菜单的情况 -->
          <el-sub-menu v-if="route.children && route.children.length > 0" :index="route.path">
            <template #title>
              <el-icon>
                <component :is="getIconComponent(route.meta.icon)" />
              </el-icon>
              <span>{{ route.meta.title }}</span>
            </template>
            <el-menu-item 
              v-for="child in route.children" 
              :key="child.path" 
              :index="`/${route.path}/${child.path}`"
            >
              <el-icon>
                <component :is="getIconComponent(child.meta.icon)" />
              </el-icon>
              <span>{{ child.meta.title }}</span>
            </el-menu-item>
          </el-sub-menu>
          <!-- 无子菜单的情况 -->
          <el-menu-item v-else :index="`/${route.path}`">
            <el-icon>
              <component :is="getIconComponent(route.meta.icon)" />
            </el-icon>
            <span>{{ route.meta.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </div>

    <div class="main-container">
      <div class="navbar">
        <div class="navbar-left">
          <span class="page-title">{{ pageTitle }}</span>
        </div>
        <div class="navbar-right">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              <el-icon><UserFilled /></el-icon>
              {{ userInfo.username || '用户' }}
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <div class="app-main">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'
import { HomeFilled, User, UserFilled, Document, Coin, DocumentCopy, Reading, Location, Calendar, EditPen, Goods, ShoppingBag, Bell, Message, FirstAidKit, OfficeBuilding, Avatar, Setting, Box, ShoppingCartFull, DataAnalysis, Folder, Promotion, Service, Star } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const store = useStore()
const router = useRouter()
const route = useRoute()

const userInfo = computed(() => store.state.user.userInfo || {})
const userType = computed(() => store.state.user.userType || 'admin')
const activeMenu = computed(() => route.path)
const pageTitle = computed(() => route.meta.title || '')

// 根据用户类型过滤菜单
const menuRoutes = computed(() => {
  const routes = router.options.routes.find(r => r.path === '/')?.children || []
  return routes.filter(route => {
    const roles = route.meta?.roles
    if (!roles || roles.length === 0) return true
    return roles.includes(userType.value)
  })
})

// 图标映射
const iconMap = {
  home: HomeFilled,
  user: User,
  document: Document,
  coin: Coin,
  'document-copy': DocumentCopy,
  reading: Reading,
  location: Location,
  calendar: Calendar,
  'edit-pen': EditPen,
  'document-checked': Document,
  goods: Goods,
  'shopping-bag': ShoppingBag,
  'shopping-cart-full': ShoppingCartFull,
  bell: Bell,
  message: Message,
  'first-aid-kit': FirstAidKit,
  'office-building': OfficeBuilding,
  avatar: Avatar,
  setting: Setting,
  box: Box,
  'data-analysis': DataAnalysis,
  folder: Folder,
  promotion: Promotion,
  service: Service,
  star: Star
}

// 获取图标组件
const getIconComponent = (iconName) => {
  return iconMap[iconName] || Document
}

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      store.dispatch('user/logout').then(() => {
        ElMessage.success('退出成功')
        router.push('/login')
      })
    })
  }
}
</script>

<style lang="scss" scoped>
.app-wrapper {
  display: flex;
  width: 100%;
  height: 100vh;

  .sidebar-container {
    width: 210px;
    height: 100%;
    background-color: #304156;
    overflow-y: auto;
    flex-shrink: 0;

    .logo-container {
      height: 50px;
      line-height: 50px;
      text-align: center;
      background: #2b2f3a;

      .logo-title {
        margin: 0;
        font-size: 16px;
        color: #fff;
        font-weight: 600;
      }
    }

    .el-menu {
      border: none;
    }
  }

  .main-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-height: 0;
    overflow: hidden;

    .navbar {
      height: 50px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 20px;
      background: #fff;
      box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
      flex-shrink: 0;

      .navbar-left {
        .page-title {
          font-size: 18px;
          font-weight: 500;
        }
      }

      .navbar-right {
        .el-dropdown-link {
          cursor: pointer;
          display: flex;
          align-items: center;
          
          .el-icon {
            margin-right: 5px;
          }
        }
      }
    }

    .app-main {
      flex: 1;
      padding: 20px;
      background: #f0f2f5;
      overflow-y: auto;
      overflow-x: hidden;
    }
  }
}
</style>

