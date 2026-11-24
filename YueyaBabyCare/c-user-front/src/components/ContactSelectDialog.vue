<template>
  <div v-if="visible" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-2xl max-h-[80vh] overflow-hidden">
      <!-- 头部 -->
      <div class="flex items-center justify-between px-6 py-4 border-b border-gray-200">
        <h3 class="text-lg font-semibold text-gray-800">选择联系人</h3>
        <button @click="handleClose" class="text-gray-400 hover:text-gray-600 transition-colors">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
          </svg>
        </button>
      </div>

      <!-- 搜索栏 -->
      <div class="px-6 py-4 border-b border-gray-200">
        <div class="flex gap-3">
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="搜索联系人姓名或手机号"
            class="flex-1 px-4 py-2 text-gray-700 bg-gray-50 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200"
            @keyup.enter="handleSearch"
          />
          <button
            @click="handleSearch"
            class="px-4 py-2 text-white bg-blue-500 rounded-lg hover:bg-blue-600 transition-colors"
          >
            搜索
          </button>
          <button
            @click="handleReset"
            class="px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
          >
            重置
          </button>
        </div>
      </div>

      <!-- Tab切换 -->
      <div class="flex border-b border-gray-200">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          @click="activeTab = tab.key"
          :class="[
            'flex-1 px-4 py-3 text-sm font-medium transition-colors duration-200',
            activeTab === tab.key
              ? 'text-blue-600 border-b-2 border-blue-500 bg-blue-50'
              : 'text-gray-600 hover:text-gray-800 hover:bg-gray-50'
          ]"
        >
          {{ tab.label }}
        </button>
      </div>

      <!-- 联系人列表 -->
      <div class="overflow-y-auto max-h-[400px] min-h-[300px]">
        <div v-if="loading" class="flex items-center justify-center h-64">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
        </div>
        <div v-else-if="currentList.length === 0" class="flex items-center justify-center h-64">
          <div class="text-center">
            <svg class="w-12 h-12 text-gray-300 mx-auto mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"/>
            </svg>
            <p class="text-gray-500">暂无{{ activeTab === 'institution' ? '机构' : '保姆' }}数据</p>
          </div>
        </div>
        <div v-else class="divide-y divide-gray-100">
          <div
            v-for="item in currentList"
            :key="item.contactId"
            @click="handleSelect(item)"
            :class="[
              'flex items-center gap-4 px-6 py-4 cursor-pointer transition-colors duration-200',
              isSelected(item) ? 'bg-blue-50 border-l-4 border-blue-500' : 'hover:bg-gray-50'
            ]"
          >
            <div class="w-12 h-12 rounded-full bg-gray-200 flex items-center justify-center text-gray-600 font-medium shrink-0">
              {{ item.contactName?.charAt(0) || '?' }}
            </div>
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2 mb-1">
                <span class="font-medium text-gray-800">{{ item.contactName }}</span>
                <span :class="getContactTypeClass(item.contactType)">
                  {{ item.contactTypeName }}
                </span>
              </div>
              <p class="text-gray-500 text-sm">{{ item.contactPhone || '暂无手机号' }}</p>
              <p v-if="item.relationDesc" class="text-gray-400 text-xs mt-1">{{ item.relationDesc }}</p>
            </div>
            <div v-if="isSelected(item)" class="text-blue-500">
              <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 24 24">
                <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
              </svg>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="currentList.length > 0" class="px-6 py-3 border-t border-gray-200 flex items-center justify-between">
        <span class="text-sm text-gray-500">共 {{ currentTotal }} 条</span>
        <div class="flex items-center gap-2">
          <button
            @click="handlePageChange(currentPage - 1)"
            :disabled="currentPage <= 1"
            :class="[
              'px-3 py-1 text-sm rounded transition-colors',
              currentPage <= 1 ? 'text-gray-400 cursor-not-allowed' : 'text-gray-700 hover:bg-gray-100'
            ]"
          >
            上一页
          </button>
          <span class="text-sm text-gray-600">{{ currentPage }} / {{ totalPages }}</span>
          <button
            @click="handlePageChange(currentPage + 1)"
            :disabled="currentPage >= totalPages"
            :class="[
              'px-3 py-1 text-sm rounded transition-colors',
              currentPage >= totalPages ? 'text-gray-400 cursor-not-allowed' : 'text-gray-700 hover:bg-gray-100'
            ]"
          >
            下一页
          </button>
        </div>
      </div>

      <!-- 底部 -->
      <div class="flex items-center justify-between px-6 py-4 border-t border-gray-200 bg-gray-50">
        <div v-if="selectedContact" class="flex items-center gap-2 text-sm text-gray-600">
          <span>已选择:</span>
          <span class="font-medium text-gray-800">{{ selectedContact.contactName }}</span>
          <span :class="getContactTypeClass(selectedContact.contactType)">
            {{ selectedContact.contactTypeName }}
          </span>
        </div>
        <div v-else class="text-sm text-gray-500">请选择一个联系人</div>
        <div class="flex gap-3">
          <button
            @click="handleClose"
            class="px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
          >
            取消
          </button>
          <button
            @click="handleConfirm"
            :disabled="!selectedContact"
            :class="[
              'px-4 py-2 rounded-lg transition-colors',
              selectedContact
                ? 'text-white bg-blue-500 hover:bg-blue-600'
                : 'text-gray-400 bg-gray-100 cursor-not-allowed'
            ]"
          >
            确定
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { getParentInstitutions, getParentNannies } from '@/api/message'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'select'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const tabs = [
  { key: 'institution', label: '机构' },
  { key: 'nanny', label: '保姆' }
]

const activeTab = ref('institution')
const searchKeyword = ref('')
const selectedContact = ref(null)
const loading = ref(false)

// 机构列表数据
const institutionList = ref([])
const institutionPage = ref(1)
const institutionTotal = ref(0)

// 保姆列表数据
const nannyList = ref([])
const nannyPage = ref(1)
const nannyTotal = ref(0)

const pageSize = 10

// 当前列表
const currentList = computed(() => {
  return activeTab.value === 'institution' ? institutionList.value : nannyList.value
})

const currentTotal = computed(() => {
  return activeTab.value === 'institution' ? institutionTotal.value : nannyTotal.value
})

const currentPage = computed(() => {
  return activeTab.value === 'institution' ? institutionPage.value : nannyPage.value
})

const totalPages = computed(() => {
  return Math.ceil(currentTotal.value / pageSize) || 1
})

// 监听弹窗显示
watch(() => props.modelValue, (val) => {
  if (val) {
    loadCurrentTabData()
  }
})

// 监听Tab切换
watch(activeTab, () => {
  loadCurrentTabData()
})

// 加载当前Tab数据
const loadCurrentTabData = () => {
  if (activeTab.value === 'institution') {
    loadInstitutionList()
  } else {
    loadNannyList()
  }
}

// 加载机构列表
const loadInstitutionList = async () => {
  loading.value = true
  try {
    const res = await getParentInstitutions(searchKeyword.value)
    const allData = res.data || []
    institutionTotal.value = allData.length
    const start = (institutionPage.value - 1) * pageSize
    const end = start + pageSize
    institutionList.value = allData.slice(start, end).map(item => ({
      ...item,
      contactType: 3
    }))
  } catch (error) {
    console.error('获取机构列表失败:', error)
    institutionList.value = []
  } finally {
    loading.value = false
  }
}

// 加载保姆列表
const loadNannyList = async () => {
  loading.value = true
  try {
    const res = await getParentNannies(searchKeyword.value)
    const allData = res.data || []
    nannyTotal.value = allData.length
    const start = (nannyPage.value - 1) * pageSize
    const end = start + pageSize
    nannyList.value = allData.slice(start, end).map(item => ({
      ...item,
      contactType: 2
    }))
  } catch (error) {
    console.error('获取保姆列表失败:', error)
    nannyList.value = []
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  institutionPage.value = 1
  nannyPage.value = 1
  loadCurrentTabData()
}

// 重置
const handleReset = () => {
  searchKeyword.value = ''
  institutionPage.value = 1
  nannyPage.value = 1
  loadCurrentTabData()
}

// 分页
const handlePageChange = (page) => {
  if (page < 1 || page > totalPages.value) return
  if (activeTab.value === 'institution') {
    institutionPage.value = page
    loadInstitutionList()
  } else {
    nannyPage.value = page
    loadNannyList()
  }
}

// 判断是否选中
const isSelected = (item) => {
  return selectedContact.value &&
    selectedContact.value.contactType === item.contactType &&
    selectedContact.value.contactId === item.contactId
}

// 选择联系人
const handleSelect = (item) => {
  if (isSelected(item)) {
    selectedContact.value = null
  } else {
    selectedContact.value = item
  }
}

// 获取联系人类型样式
const getContactTypeClass = (type) => {
  const classes = {
    2: 'px-2 py-0.5 text-xs font-medium text-amber-700 bg-amber-50 rounded-full',
    3: 'px-2 py-0.5 text-xs font-medium text-blue-700 bg-blue-50 rounded-full'
  }
  return classes[type] || 'px-2 py-0.5 text-xs font-medium text-gray-700 bg-gray-100 rounded-full'
}

// 确认选择
const handleConfirm = () => {
  if (selectedContact.value) {
    emit('select', selectedContact.value)
    handleClose()
  }
}

// 关闭弹窗
const handleClose = () => {
  visible.value = false
  selectedContact.value = null
  searchKeyword.value = ''
}
</script>