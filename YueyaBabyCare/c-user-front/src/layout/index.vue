<template>
  <div class="layout">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-container">
        <!-- Logo -->
        <div class="logo" @click="$router.push('/')">
          <t-icon name="shop" size="28" style="color: #3B82F6" />
          <span class="logo-text">悦芽婴护管理系统</span>
        </div>

        <!-- 主导航菜单 -->
        <nav class="nav-menu">
          <div 
            v-for="item in navMenu" 
            :key="item.value"
            class="nav-item"
            :class="{ active: activeNav === item.value }"
            @click="handleNavClick(item.path)"
          >
            {{ item.label }}
          </div>
        </nav>

        <!-- 用户区域 -->
        <div class="user-area">
          <!-- 游客模式 -->
          <template v-if="!isLogin">
            <t-button variant="outline" size="medium" @click="$router.push('/login')">登录</t-button>
            <t-button theme="primary" size="medium" @click="$router.push('/register')">注册</t-button>
          </template>
          <!-- 登录状态 -->
          <template v-else>
            <t-badge :count="3" :offset="[6, 0]">
              <t-button variant="text" shape="square" @click="$router.push('/messages')">
                <t-icon name="mail" size="20" />
              </t-button>
            </t-badge>
            <t-dropdown>
              <div class="user-info">
                <t-avatar size="small">{{ nickname?.substring(0, 1) }}</t-avatar>
                <span class="user-name">{{ nickname }}</span>
                <t-icon name="chevron-down" size="14" />
              </div>
              <t-dropdown-menu>
                <t-dropdown-item @click="$router.push('/profile')">
                  <t-icon name="user" /> 个人中心
                </t-dropdown-item>
                <t-dropdown-item @click="$router.push('/addresses')">
                  <t-icon name="location" /> 地址管理
                </t-dropdown-item>
                <t-dropdown-item theme="error" @click="handleLogout">
                  <t-icon name="poweroff" /> 退出登录
                </t-dropdown-item>
              </t-dropdown-menu>
            </t-dropdown>
          </template>
        </div>
      </div>
    </header>
    
    <!-- 主内容区 -->
    <main class="main">
      <router-view />
    </main>

    <!-- 底部 -->
    <footer class="footer">
      <div class="footer-main">
        <div class="footer-container">
          <!-- 服务项目 -->
          <div class="footer-column">
            <h4>服务项目</h4>
            <ul>
              <li @click="$router.push('/babies')">宝宝管理</li>
              <li @click="$router.push('/health-records/1')">健康档案</li>
              <li @click="$router.push('/growth-records/1')">成长记录</li>
              <li @click="$router.push('/care-plans')">护理计划</li>
            </ul>
          </div>

          <!-- 预约服务 -->
          <div class="footer-column">
            <h4>预约服务</h4>
            <ul>
              <li @click="$router.push('/appointments')">全日护理</li>
              <li @click="$router.push('/appointments')">半日护理</li>
              <li @click="$router.push('/appointments')">早教服务</li>
              <li @click="$router.push('/appointments')">上门服务</li>
            </ul>
          </div>

          <!-- 母婴商城 -->
          <div class="footer-column">
            <h4>母婴商城</h4>
            <ul>
              <li @click="$router.push('/mall')">奶粉辅食</li>
              <li @click="$router.push('/mall')">纸尿裤湿巾</li>
              <li @click="$router.push('/mall')">婴儿服饰</li>
              <li @click="$router.push('/mall')">玩具早教</li>
            </ul>
          </div>

          <!-- 关于我们 -->
          <div class="footer-column">
            <h4>关于我们</h4>
            <ul>
              <li>公司简介</li>
              <li>联系我们</li>
              <li>加入我们</li>
              <li>帮助中心</li>
            </ul>
          </div>

          <!-- 联系方式 -->
          <div class="footer-column footer-contact">
            <div class="footer-logo">
              <t-icon name="shop" size="32" style="color: #60A5FA" />
              <span>悦芽婴护</span>
            </div>
            <div class="contact-info">
              <p class="label">客服热线：</p>
              <p class="phone">400-888-6666</p>
            </div>
            <div class="contact-info">
              <p class="label">商务咨询：</p>
              <p class="email">contact@yueya.com</p>
            </div>
            <div class="social-links">
              <span class="social-icon"><t-icon name="logo-wechat" size="18" /></span>
              <span class="social-icon"><t-icon name="logo-wecom" size="18" /></span>
              <span class="social-icon"><t-icon name="call" size="18" /></span>
            </div>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <div class="footer-container">
          <p>Copyright &copy; 2024 悦芽婴护管理系统 &nbsp;&nbsp; 备案号：粤ICP备2024XXXXXX号</p>
          <div class="footer-links">
            <span>用户协议</span>
            <span>隐私政策</span>
            <span>技术支持</span>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { MessagePlugin } from 'tdesign-vue-next'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const activeNav = ref('home')

const isLogin = computed(() => userStore.isLogin)
const nickname = computed(() => userStore.nickname || '家长')

// 顶部导航菜单
const navMenu = [
  { label: '首页', value: 'home', path: '/' },
  { label: '选择机构', value: 'institutions', path: '/institutions' },
  { label: '我的宝宝', value: 'babies', path: '/babies' },
  { label: '护理计划', value: 'care', path: '/care-plans' },
  { label: '预约服务', value: 'appointments', path: '/appointments' },
  { label: '母婴商城', value: 'mall', path: '/mall' },
  { label: '我的订单', value: 'orders', path: '/orders' },
  { label: '消息中心', value: 'messages', path: '/messages' }
]

watch(() => route.path, (path) => {
  for (const item of navMenu) {
    if (path === item.path || (item.path !== '/' && path.startsWith(item.path))) {
      activeNav.value = item.value
      return
    }
  }
  if (path === '/' || path === '/home') {
    activeNav.value = 'home'
  }
}, { immediate: true })

const handleNavClick = (path) => {
  router.push(path)
}

const handleLogout = async () => {
  await userStore.logout()
  MessagePlugin.success('已退出登录')
  router.push('/')
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #F9FAFB;
}

/* 顶部导航栏 */
.header {
  background: white;
  border-bottom: 1px solid #E5E7EB;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 32px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* Logo */
.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  flex-shrink: 0;
}

.logo-text {
  font-size: 20px;
  font-weight: 600;
  background: linear-gradient(135deg, #3B82F6 0%, #2563EB 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  white-space: nowrap;
}

/* 导航菜单 */
.nav-menu {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
  justify-content: center;
  padding: 0 32px;
}

.nav-item {
  padding: 8px 20px;
  color: #6B7280;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;
  white-space: nowrap;
  user-select: none;
}

.nav-item:hover {
  background: #F3F4F6;
  color: #3B82F6;
}

.nav-item.active {
  background: #EFF6FF;
  color: #3B82F6;
  font-weight: 600;
}

/* 用户区域 */
.user-area {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  border-radius: 20px;
  cursor: pointer;
  transition: background 0.2s;
}

.user-info:hover {
  background: #F9FAFB;
}

.user-name {
  font-size: 14px;
  color: #374151;
  font-weight: 500;
}

/* 主内容区 */
.main {
  flex: 1;
  width: 100%;
}

/* 底部 */
.footer {
  background: #1F2937;
  margin-top: auto;
}

.footer-main {
  padding: 60px 0 40px;
  border-bottom: 1px solid #374151;
}

.footer-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 32px;
  display: flex;
  justify-content: space-between;
  gap: 40px;
}

.footer-column {
  flex: 1;
}

.footer-column h4 {
  color: white;
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 20px 0;
}

.footer-column ul {
  list-style: none;
  margin: 0;
  padding: 0;
}

.footer-column li {
  color: #9CA3AF;
  font-size: 14px;
  padding: 8px 0;
  cursor: pointer;
  transition: color 0.2s;
}

.footer-column li:hover {
  color: #60A5FA;
}

.footer-contact {
  flex: 1.2;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.footer-logo span {
  font-size: 22px;
  font-weight: 700;
  color: white;
}

.contact-info {
  margin-bottom: 12px;
}

.contact-info .label {
  color: #9CA3AF;
  font-size: 13px;
  margin: 0 0 4px 0;
}

.contact-info .phone {
  color: white;
  font-size: 28px;
  font-weight: 700;
  margin: 0;
  letter-spacing: 1px;
}

.contact-info .email {
  color: white;
  font-size: 16px;
  margin: 0;
}

.social-links {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.social-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background: #374151;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9CA3AF;
  cursor: pointer;
  transition: all 0.2s;
}

.social-icon:hover {
  background: #60A5FA;
  color: white;
}

.footer-bottom {
  padding: 20px 0;
}

.footer-bottom .footer-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-bottom p {
  color: #6B7280;
  font-size: 13px;
  margin: 0;
}

.footer-links {
  display: flex;
  gap: 24px;
}

.footer-links span {
  color: #6B7280;
  font-size: 13px;
  cursor: pointer;
  transition: color 0.2s;
}

.footer-links span:hover {
  color: #60A5FA;
}
</style>
