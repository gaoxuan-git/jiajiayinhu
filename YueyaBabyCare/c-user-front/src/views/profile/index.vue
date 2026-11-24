<template>
  <div class="profile-page">
    <div class="profile-layout">
      <!-- 左侧信息卡片 -->
      <div class="info-card">
        <div class="user-header">
          <div class="avatar-wrapper">
            <t-avatar 
              size="80px" 
              :image="avatarUrl" 
              :icon="!avatarUrl ? UserIcon : undefined"
            />
          </div>
          <div class="user-info">
            <h2>{{ userInfo?.realName || userInfo?.username || '悦芽用户' }}</h2>
            <p class="phone">{{ maskPhone(userInfo?.phone) }}</p>
          </div>
          <t-button theme="primary" @click="$router.push('/profile/edit')" class="edit-btn">
            编辑资料
          </t-button>
        </div>
        <div class="info-list">
          <div class="info-item">
            <span class="label">用户名</span>
            <span class="value">{{ userInfo?.username || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="label">真实姓名</span>
            <span class="value">{{ userInfo?.realName || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="label">性别</span>
            <span class="value">{{ userInfo?.genderName || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="label">手机号</span>
            <span class="value">{{ maskPhone(userInfo?.phone) }}</span>
          </div>
          <div class="info-item">
            <span class="label">邮箱</span>
            <span class="value">{{ userInfo?.email || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="label">注册时间</span>
            <span class="value">{{ formatDate(userInfo?.createTime) }}</span>
          </div>
        </div>
        <div class="logout-section">
          <t-button theme="default" variant="outline" block @click="handleLogout" class="logout-btn">
            退出登录
          </t-button>
        </div>
      </div>

      <!-- 右侧功能区 -->
      <div class="right-section">
        <!-- 宝宝管理 -->
        <div class="function-card">
          <div class="card-header">
            <h3>宝宝管理</h3>
            <a href="javascript:;" class="more-link" @click="$router.push('/babies')">查看全部</a>
          </div>
          <div class="quick-grid">
            <div class="quick-item" @click="$router.push('/babies')">
              <div class="quick-icon baby-icon">
                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.828 14.828a4 4 0 01-5.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
              </div>
              <span>宝宝档案</span>
            </div>
            <div class="quick-item" @click="$router.push('/babies')">
              <div class="quick-icon health-icon">
                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
                </svg>
              </div>
              <span>健康档案</span>
            </div>
            <div class="quick-item" @click="$router.push('/babies')">
              <div class="quick-icon growth-icon">
                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
                </svg>
              </div>
              <span>成长记录</span>
            </div>
          </div>
        </div>

        <!-- 护理服务 -->
        <div class="function-card">
          <div class="card-header">
            <h3>护理服务</h3>
          </div>
          <div class="quick-grid">
            <div class="quick-item" @click="$router.push('/care-plans')">
              <div class="quick-icon care-icon">
                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01"/>
                </svg>
              </div>
              <span>护理计划</span>
            </div>
            <div class="quick-item" @click="$router.push('/evaluations')">
              <div class="quick-icon eval-icon">
                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z"/>
                </svg>
              </div>
              <span>护理评价</span>
            </div>
            <div class="quick-item" @click="$router.push('/appointments')">
              <div class="quick-icon appt-icon">
                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                </svg>
              </div>
              <span>我的预约</span>
            </div>
          </div>
        </div>

        <!-- 订单与消息 -->
        <div class="function-card">
          <div class="card-header">
            <h3>订单与消息</h3>
          </div>
          <div class="menu-list">
            <div class="menu-item" @click="$router.push('/dashboard')">
              <div class="menu-left">
                <div class="menu-icon dashboard-icon">
                  <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
                  </svg>
                </div>
                <div class="menu-text">
                  <span class="menu-title">数据中心</span>
                  <span class="menu-desc">查看宝宝成长、护理和消费统计</span>
                </div>
              </div>
              <svg class="arrow-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
              </svg>
            </div>
            <div class="menu-item" @click="$router.push('/orders')">
              <div class="menu-left">
                <div class="menu-icon order-icon">
                  <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
                  </svg>
                </div>
                <div class="menu-text">
                  <span class="menu-title">我的订单</span>
                  <span class="menu-desc">查看商城购物订单</span>
                </div>
              </div>
              <svg class="arrow-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
              </svg>
            </div>
            <div class="menu-item" @click="$router.push('/messages')">
              <div class="menu-left">
                <div class="menu-icon msg-icon">
                  <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/>
                  </svg>
                </div>
                <div class="menu-text">
                  <span class="menu-title">消息中心</span>
                  <span class="menu-desc">查看系统通知与消息</span>
                </div>
              </div>
              <svg class="arrow-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
              </svg>
            </div>
            <div class="menu-item" @click="$router.push('/addresses')">
              <div class="menu-left">
                <div class="menu-icon addr-icon">
                  <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/>
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/>
                  </svg>
                </div>
                <div class="menu-text">
                  <span class="menu-title">收货地址</span>
                  <span class="menu-desc">管理收货地址信息</span>
                </div>
              </div>
              <svg class="arrow-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
              </svg>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next'
import { UserIcon } from 'tdesign-icons-vue-next'
import { useUserStore } from '@/store/user'
import { getMemberInfo } from '@/api/member'
import { getImageUrl } from '@/utils/image'

const router = useRouter()
const userStore = useUserStore()
const userInfo = ref(null)

// 处理头像路径
const avatarUrl = computed(() => getImageUrl(userInfo.value?.avatar))

onMounted(async () => {
  await fetchProfile()
})

const fetchProfile = async () => {
  try {
    const res = await getMemberInfo()
    userInfo.value = res.data
  } catch (e) {
    // 错误处理
  }
}

const maskPhone = (phone) => {
  if (!phone) return '-'
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const handleLogout = () => {
  const dialog = DialogPlugin.confirm({
    header: '提示',
    body: '确定要退出登录吗？',
    confirmBtn: '确定',
    cancelBtn: '取消',
    onConfirm: async () => {
      await userStore.logout()
      dialog.destroy()
      MessagePlugin.success('已退出登录')
      router.push('/login')
    },
    onClose: () => {
      dialog.destroy()
    }
  })
}
</script>

<style scoped>
.profile-page {
  padding: 24px;
  background: #F9FAFB;
  min-height: calc(100vh - 120px);
}

.profile-layout {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 左侧信息卡片 */
.info-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  height: fit-content;
}

.user-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #F3F4F6;
  margin-bottom: 20px;
}

.avatar-wrapper {
  margin-bottom: 12px;
}

.avatar-wrapper :deep(.t-avatar) {
  background: linear-gradient(135deg, #3B82F6 0%, #2563EB 100%);
}

.user-info h2 {
  margin: 0 0 8px;
  font-size: 20px;
  font-weight: 600;
  color: #1F2937;
}

.user-info .phone {
  margin: 0;
  font-size: 14px;
  color: #6B7280;
}

.edit-btn {
  margin-top: 16px;
  border-radius: 8px;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-item .label {
  font-size: 14px;
  color: #6B7280;
}

.info-item .value {
  font-size: 14px;
  color: #1F2937;
}

.logout-section {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #F3F4F6;
}

.logout-btn {
  border-radius: 8px;
  color: #6B7280;
}

/* 右侧功能区 */
.right-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.function-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1F2937;
}

.more-link {
  font-size: 14px;
  color: #3B82F6;
  text-decoration: none;
}

.more-link:hover {
  color: #2563EB;
}

/* 快捷功能网格 */
.quick-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px;
  border-radius: 10px;
  background: #F9FAFB;
  cursor: pointer;
  transition: all 0.2s;
}

.quick-item:hover {
  background: #F3F4F6;
  transform: translateY(-2px);
}

.quick-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
}

.quick-icon svg {
  width: 24px;
  height: 24px;
}

.baby-icon { background: #DBEAFE; color: #3B82F6; }
.health-icon { background: #FCE7F3; color: #EC4899; }
.growth-icon { background: #D1FAE5; color: #10B981; }
.care-icon { background: #E0E7FF; color: #6366F1; }
.eval-icon { background: #FEF3C7; color: #F59E0B; }
.appt-icon { background: #CFFAFE; color: #06B6D4; }

.quick-item span {
  font-size: 14px;
  color: #374151;
}

/* 菜单列表 */
.menu-list {
  display: flex;
  flex-direction: column;
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 0;
  border-bottom: 1px solid #F3F4F6;
  cursor: pointer;
  transition: background 0.2s;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:hover {
  background: #F9FAFB;
  margin: 0 -20px;
  padding: 14px 20px;
}

.menu-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.menu-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.menu-icon svg {
  width: 20px;
  height: 20px;
}

.order-icon { background: #DBEAFE; color: #3B82F6; }
.msg-icon { background: #FEE2E2; color: #EF4444; }
.addr-icon { background: #D1FAE5; color: #10B981; }
.dashboard-icon { background: #E0E7FF; color: #6366F1; }

.menu-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.menu-title {
  font-size: 15px;
  font-weight: 500;
  color: #1F2937;
}

.menu-desc {
  font-size: 12px;
  color: #9CA3AF;
}

.arrow-icon {
  width: 16px;
  height: 16px;
  color: #9CA3AF;
}

/* 响应式 */
@media (max-width: 900px) {
  .profile-layout {
    grid-template-columns: 1fr;
  }
  
  .quick-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>
