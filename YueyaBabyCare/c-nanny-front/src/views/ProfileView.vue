<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Loading -->
    <div v-if="loading" class="flex items-center justify-center min-h-screen">
      <t-loading size="large" />
    </div>

    <div v-else>
      <!-- Header Section -->
      <div class="bg-blue-600 pb-20 pt-12 px-6 text-white rounded-b-[2rem] shadow-lg relative overflow-hidden">
        <!-- Decorative circles -->
        <div class="absolute top-0 right-0 w-32 h-32 bg-white/10 rounded-full -mr-8 -mt-8"></div>
        <div class="absolute bottom-0 left-0 w-24 h-24 bg-white/10 rounded-full -ml-8 -mb-8"></div>
        
        <div class="flex items-center justify-between relative z-10">
          <div class="flex items-center gap-5">
            <t-avatar 
              size="80px" 
              :image="userInfo.avatar || 'https://tdesign.gtimg.com/site/avatar.jpg'" 
              class="border-4 border-white/30" 
            />
            <div>
              <h2 class="text-2xl font-bold mb-1">{{ userInfo.realName || '保姆' }}</h2>
              <div class="flex items-center gap-2">
                <t-tag theme="warning" size="small" variant="light">
                  {{ userInfo.status === 1 ? '在职' : '离职' }}
                </t-tag>
                <span class="text-blue-100 text-sm">从业{{ userInfo.workYears || 0 }}年</span>
              </div>
            </div>
          </div>
          <!-- 编辑按钮 -->
          <t-button 
            theme="primary" 
            variant="text" 
            size="small"
            class="text-white"
            @click="goToEdit"
          >
            编辑
          </t-button>
        </div>
      </div>
    
    <!-- Stats Card -->
    <div class="px-4 -mt-12 relative z-20 mb-6">
      <div class="bg-white rounded-2xl shadow-md p-6 flex justify-between text-center divide-x divide-gray-100">
        <div class="flex-1">
          <div class="text-2xl font-bold text-gray-800">{{ userInfo.workYears || 0 }}</div>
          <div class="text-xs text-gray-500 mt-1">从业年限</div>
        </div>
        <div class="flex-1 cursor-pointer" @click="goToEvaluation">
          <div class="text-2xl font-bold text-gray-800">{{ ratingStats.totalCount || 0 }}</div>
          <div class="text-xs text-gray-500 mt-1">评价数</div>
        </div>
        <div class="flex-1 cursor-pointer" @click="goToEvaluation">
          <div class="text-2xl font-bold text-amber-500">{{ ratingStats.avgScore || '0.0' }}</div>
          <div class="text-xs text-gray-500 mt-1">综合评分</div>
        </div>
      </div>
    </div>

      <!-- Menu List -->
      <div class="px-4 space-y-4">
        <div class="bg-white rounded-xl shadow-sm overflow-hidden">
          <t-list :split="true">
            <t-list-item hover @click="goToEdit">
              <template #action><chevron-right-icon size="20px" class="text-gray-400" /></template>
              <div class="flex items-center gap-3">
                <user-icon size="20px" class="text-orange-500" />
                <span class="text-gray-700">个人信息</span>
              </div>
            </t-list-item>
            <t-list-item hover @click="goToCertificates">
              <template #action><chevron-right-icon size="20px" class="text-gray-400" /></template>
              <div class="flex items-center gap-3">
                <file-icon size="20px" class="text-blue-500" />
                <span class="text-gray-700">我的证书</span>
              </div>
            </t-list-item>
            <t-list-item hover @click="goToAddress">
              <template #action><chevron-right-icon size="20px" class="text-gray-400" /></template>
              <div class="flex items-center gap-3">
                <location-icon size="20px" class="text-red-500" />
                <span class="text-gray-700">地址管理</span>
              </div>
            </t-list-item>
            <t-list-item hover @click="goToEvaluation">
              <template #action><chevron-right-icon size="20px" class="text-gray-400" /></template>
              <div class="flex items-center gap-3">
                <star-icon size="20px" class="text-amber-500" />
                <span class="text-gray-700">我的评价</span>
              </div>
            </t-list-item>
            <t-list-item hover>
              <template #action><chevron-right-icon size="20px" class="text-gray-400" /></template>
              <div class="flex items-center gap-3">
                <wallet-icon size="20px" class="text-green-500" />
                <span class="text-gray-700">工资明细</span>
              </div>
            </t-list-item>
            <t-list-item hover>
              <template #action><chevron-right-icon size="20px" class="text-gray-400" /></template>
              <div class="flex items-center gap-3">
                <chart-icon size="20px" class="text-purple-500" />
                <span class="text-gray-700">服务数据</span>
              </div>
            </t-list-item>
          </t-list>
        </div>

        <div class="bg-white rounded-xl shadow-sm overflow-hidden">
          <t-list>
            <t-list-item hover>
              <template #action><chevron-right-icon size="20px" class="text-gray-400" /></template>
              <div class="flex items-center gap-3">
                <setting-icon size="20px" class="text-gray-500" />
                <span class="text-gray-700">系统设置</span>
              </div>
            </t-list-item>
          </t-list>
        </div>
        
        <div class="pt-4 pb-8">
          <t-button 
            theme="danger" 
            variant="outline" 
            block 
            size="large"
            @click="handleLogout"
            :loading="logoutLoading"
          >
            退出登录
          </t-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next'
import { ChevronRightIcon, FileIcon, WalletIcon, ChartIcon, SettingIcon, UserIcon, LocationIcon, StarIcon } from 'tdesign-icons-vue-next'
import { getProfile, logout } from '@/api/nanny'
import { getMyRatingStats } from '@/api/careEvaluation'

const router = useRouter()
const loading = ref(false)
const logoutLoading = ref(false)
const userInfo = ref({})
const ratingStats = ref({
  totalCount: 0,
  avgScore: '0.0'
})

// 获取用户信息
const fetchUserInfo = async () => {
  loading.value = true
  try {
    const res = await getProfile()
    userInfo.value = res.data || {}
  } catch (error) {
    console.error('获取用户信息失败:', error)
    // 如果获取失败，可能是未登录，跳转到登录页
    MessagePlugin.error('请先登录')
    router.push('/login')
  } finally {
    loading.value = false
  }
}

// 获取评分统计
const fetchRatingStats = async () => {
  try {
    const res = await getMyRatingStats()
    if (res.code === 200 && res.data) {
      ratingStats.value = res.data
    }
  } catch (error) {
    console.error('获取评分统计失败:', error)
  }
}

// 跳转到编辑页面
const goToEdit = () => {
  router.push('/profile/edit')
}

// 跳转到证书管理页
const goToCertificates = () => {
  router.push('/certificates')
}

// 跳转到地址管理页
const goToAddress = () => {
  router.push('/address')
}

// 跳转到评价页面
const goToEvaluation = () => {
  router.push('/evaluation')
}

// 退出登录
const handleLogout = () => {
  const confirmDialog = DialogPlugin.confirm({
    header: '退出登录',
    body: '确定要退出登录吗？',
    confirmBtn: '确定',
    cancelBtn: '取消',
    onConfirm: async () => {
      logoutLoading.value = true
      try {
        await logout()
        
        // 清除本地存储
        localStorage.removeItem('nanny_token')
        localStorage.removeItem('nanny_user')
        
        MessagePlugin.success('已退出登录')
        
        // 跳转到登录页
        router.push('/login')
      } catch (error) {
        console.error('退出登录失败:', error)
      } finally {
        logoutLoading.value = false
      }
      confirmDialog.hide()
    },
    onCancel: () => {
      confirmDialog.hide()
    }
  })
}

onMounted(() => {
  fetchUserInfo()
  fetchRatingStats()
})
</script>
