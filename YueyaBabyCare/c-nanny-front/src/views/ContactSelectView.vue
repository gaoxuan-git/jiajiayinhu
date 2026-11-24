<template>
  <div class="min-h-screen bg-gray-50 flex flex-col">
    <!-- 顶部导航栏 -->
    <div class="bg-white px-4 py-3 border-b border-gray-200 flex items-center gap-3 sticky top-0 z-10">
      <button @click="goBack" class="text-gray-600">
        <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
        </svg>
      </button>
      <h1 class="text-lg font-semibold text-gray-800 flex-1">选择联系人</h1>
    </div>

    <!-- Tab切换 -->
    <div class="bg-white border-b border-gray-200">
      <div class="flex">
        <button
          @click="activeTab = 'parents'"
          :class="[
            'flex-1 py-3 text-sm font-medium text-center border-b-2 transition-colors',
            activeTab === 'parents'
              ? 'text-blue-600 border-blue-500'
              : 'text-gray-500 border-transparent hover:text-gray-700'
          ]"
        >
          家长
        </button>
        <button
          @click="activeTab = 'institutions'"
          :class="[
            'flex-1 py-3 text-sm font-medium text-center border-b-2 transition-colors',
            activeTab === 'institutions'
              ? 'text-blue-600 border-blue-500'
              : 'text-gray-500 border-transparent hover:text-gray-700'
          ]"
        >
          机构
        </button>
      </div>
    </div>

    <!-- 搜索框 -->
    <div class="bg-white px-4 py-3 border-b border-gray-200">
      <div class="relative">
        <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
        </svg>
        <input
          v-model="searchKeyword"
          type="text"
          :placeholder="activeTab === 'parents' ? '搜索家长姓名或手机号' : '搜索机构名称'"
          class="w-full pl-10 pr-4 py-2 text-sm bg-gray-100 border-0 rounded-full focus:outline-none focus:ring-2 focus:ring-blue-500"
          @input="handleSearch"
        />
        <button
          v-if="searchKeyword"
          @click="clearSearch"
          class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
          </svg>
        </button>
      </div>
    </div>

    <!-- 联系人列表 -->
    <div class="flex-1 overflow-y-auto">
      <!-- 加载中 -->
      <div v-if="loading" class="flex justify-center py-12">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
      </div>

      <!-- 家长列表 -->
      <template v-else-if="activeTab === 'parents'">
        <div v-if="parents.length > 0">
          <div
            v-for="parent in parents"
            :key="parent.contactId"
            @click="selectContact(parent)"
            class="flex items-center gap-3 px-4 py-3 bg-white border-b border-gray-100 active:bg-gray-50 cursor-pointer"
          >
            <div class="w-12 h-12 rounded-full bg-emerald-100 flex items-center justify-center text-emerald-600 font-medium text-lg shrink-0">
              {{ parent.contactName?.charAt(0) || '?' }}
            </div>
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2 mb-1">
                <span class="font-medium text-gray-800 truncate">{{ parent.contactName }}</span>
                <span class="px-1.5 py-0.5 text-xs font-medium text-emerald-700 bg-emerald-50 rounded">家长</span>
              </div>
              <p class="text-gray-500 text-sm truncate">{{ parent.contactPhone || '暂无手机号' }}</p>
            </div>
            <svg class="w-5 h-5 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
            </svg>
          </div>
        </div>
        <div v-else class="flex flex-col items-center justify-center py-16">
          <svg class="w-16 h-16 text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"/>
          </svg>
          <p class="text-gray-500">暂无服务过的家长</p>
          <p class="text-gray-400 text-sm mt-1">完成护理服务后会显示在这里</p>
        </div>
      </template>

      <!-- 机构列表 -->
      <template v-else>
        <div v-if="institutions.length > 0">
          <div
            v-for="institution in institutions"
            :key="institution.contactId"
            @click="selectContact(institution)"
            class="flex items-center gap-3 px-4 py-3 bg-white border-b border-gray-100 active:bg-gray-50 cursor-pointer"
          >
            <div class="w-12 h-12 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 font-medium text-lg shrink-0">
              {{ institution.contactName?.charAt(0) || '?' }}
            </div>
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2 mb-1">
                <span class="font-medium text-gray-800 truncate">{{ institution.contactName }}</span>
                <span class="px-1.5 py-0.5 text-xs font-medium text-blue-700 bg-blue-50 rounded">机构</span>
              </div>
              <p class="text-gray-500 text-sm truncate">{{ institution.contactPhone || '暂无联系电话' }}</p>
            </div>
            <svg class="w-5 h-5 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
            </svg>
          </div>
        </div>
        <div v-else class="flex flex-col items-center justify-center py-16">
          <svg class="w-16 h-16 text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"/>
          </svg>
          <p class="text-gray-500">暂无所属机构</p>
          <p class="text-gray-400 text-sm mt-1">加入机构后会显示在这里</p>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getNannyParents, getNannyInstitutions } from '@/api/message'

const router = useRouter()

// 状态
const activeTab = ref('parents')
const searchKeyword = ref('')
const loading = ref(false)
const parents = ref([])
const institutions = ref([])

// 加载家长列表
const loadParents = async () => {
  loading.value = true
  try {
    const res = await getNannyParents(searchKeyword.value)
    parents.value = (res.data || []).map(item => ({
      ...item,
      contactType: 1,
      contactTypeName: '家长'
    }))
  } catch (error) {
    console.error('获取家长列表失败:', error)
    parents.value = []
  } finally {
    loading.value = false
  }
}

// 加载机构列表
const loadInstitutions = async () => {
  loading.value = true
  try {
    const res = await getNannyInstitutions()
    institutions.value = (res.data || []).map(item => ({
      ...item,
      contactType: 3,
      contactTypeName: '机构'
    }))
  } catch (error) {
    console.error('获取机构列表失败:', error)
    institutions.value = []
  } finally {
    loading.value = false
  }
}

// 搜索处理
let searchTimer = null
const handleSearch = () => {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    if (activeTab.value === 'parents') {
      loadParents()
    }
  }, 300)
}

// 清除搜索
const clearSearch = () => {
  searchKeyword.value = ''
  if (activeTab.value === 'parents') {
    loadParents()
  }
}

// 选择联系人
const selectContact = (contact) => {
  // 将选中的联系人信息存储到 sessionStorage
  sessionStorage.setItem('selectedContact', JSON.stringify(contact))
  // 返回消息页面
  router.push('/message')
}

// 返回
const goBack = () => {
  router.back()
}

// 监听Tab切换
watch(activeTab, (newTab) => {
  searchKeyword.value = ''
  if (newTab === 'parents') {
    loadParents()
  } else {
    loadInstitutions()
  }
})

// 初始化
onMounted(() => {
  loadParents()
  loadInstitutions()
})
</script>

<style scoped>
/* 移动端安全区域适配 */
.safe-area-bottom {
  padding-bottom: max(12px, env(safe-area-inset-bottom));
}
</style>