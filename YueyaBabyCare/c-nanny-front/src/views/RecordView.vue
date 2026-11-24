<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部标题 -->
    <div class="bg-white px-4 py-4 border-b border-gray-200">
      <h1 class="text-xl font-semibold text-gray-800">今日护理记录</h1>
      <p class="text-sm text-gray-500 mt-1">{{ todayStr }}</p>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex justify-center items-center py-20">
      <t-loading size="large" text="加载中..." />
    </div>

    <!-- 内容区 -->
    <div v-else class="p-4 space-y-4">
      <!-- 统计卡片 -->
      <div class="bg-white rounded-xl shadow p-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <div class="w-12 h-12 rounded-full bg-blue-50 flex items-center justify-center">
              <svg class="w-6 h-6 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01"/>
              </svg>
            </div>
            <div>
              <div class="text-2xl font-bold text-gray-800">{{ records.length }}</div>
              <div class="text-sm text-gray-500">今日记录</div>
            </div>
          </div>
          <div class="flex gap-2">
            <div v-for="(count, type) in typeCounts" :key="type" class="text-center px-3">
              <div class="text-lg font-semibold" :class="getTypeTextClass(type)">{{ count }}</div>
              <div class="text-xs text-gray-500">{{ getTypeName(type) }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 记录列表 -->
      <div class="bg-white rounded-xl shadow">
        <div class="px-4 py-3 border-b border-gray-100">
          <h3 class="text-base font-semibold text-gray-800">记录明细</h3>
        </div>
        
        <div v-if="records.length > 0" class="divide-y divide-gray-100">
          <div 
            v-for="record in records" 
            :key="record.id" 
            class="p-4 hover:bg-gray-50 transition-colors cursor-pointer"
            @click="showRecordDetail(record)"
          >
            <div class="flex items-start justify-between mb-2">
              <div class="flex items-center gap-2">
                <span :class="getCareTypeClass(record.careType)">{{ record.careTypeName }}</span>
                <span class="text-xs text-gray-400">{{ formatTime(record.recordTime) }}</span>
              </div>
              <div class="flex items-center gap-2">
                <button 
                  @click.stop="editRecord(record)" 
                  class="text-gray-400 hover:text-blue-500 transition-colors"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/>
                  </svg>
                </button>
                <button 
                  @click.stop="handleDeleteRecord(record.id)" 
                  class="text-gray-400 hover:text-red-500 transition-colors"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                  </svg>
                </button>
              </div>
            </div>
            <p class="text-sm text-gray-700 line-clamp-2">{{ record.careContent }}</p>
            <div class="flex items-center gap-4 mt-2 text-xs text-gray-400">
              <span v-if="record.babyName">宝宝: {{ record.babyName }}</span>
              <span v-if="record.planName">计划: {{ record.planName }}</span>
            </div>
          </div>
        </div>
        
        <div v-else class="flex flex-col items-center justify-center py-12 text-gray-400">
          <svg class="w-16 h-16 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
          </svg>
          <p class="text-sm">今日暂无护理记录</p>
          <p class="text-xs mt-1">请先到护理计划中签到后添加记录</p>
        </div>
      </div>
    </div>

    <!-- 记录详情抽屉 -->
    <t-drawer
      v-model:visible="detailVisible"
      header="记录详情"
      placement="bottom"
      size="60%"
      :footer="false"
    >
      <div v-if="currentRecord" class="space-y-4">
        <div class="flex items-center gap-2">
          <span :class="getCareTypeClass(currentRecord.careType)">{{ currentRecord.careTypeName }}</span>
          <span class="text-sm text-gray-500">{{ formatDateTime(currentRecord.recordDate, currentRecord.recordTime) }}</span>
        </div>
        
        <div class="space-y-3">
          <div class="flex justify-between py-2 border-b border-gray-100">
            <span class="text-sm text-gray-500">宝宝</span>
            <span class="text-sm text-gray-800">{{ currentRecord.babyName || '-' }}</span>
          </div>
          <div class="flex justify-between py-2 border-b border-gray-100">
            <span class="text-sm text-gray-500">关联计划</span>
            <span class="text-sm text-gray-800">{{ currentRecord.planName || '-' }}</span>
          </div>
        </div>
        
        <div>
          <h4 class="text-sm font-medium text-gray-700 mb-2">护理内容</h4>
          <div class="bg-gray-50 rounded-lg p-3 text-sm text-gray-600 whitespace-pre-wrap">
            {{ currentRecord.careContent }}
          </div>
        </div>
        
        <div v-if="currentRecord.remark">
          <h4 class="text-sm font-medium text-gray-700 mb-2">备注</h4>
          <div class="bg-gray-50 rounded-lg p-3 text-sm text-gray-500">
            {{ currentRecord.remark }}
          </div>
        </div>
      </div>
    </t-drawer>

    <!-- 编辑记录弹窗 -->
    <t-dialog
      v-model:visible="editDialogVisible"
      header="编辑护理记录"
      :confirm-btn="{ content: '保存', loading: submitLoading }"
      cancel-btn="取消"
      @confirm="handleUpdateRecord"
    >
      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">护理类型 <span class="text-red-500">*</span></label>
          <div class="flex flex-wrap gap-2">
            <button
              v-for="type in careTypes"
              :key="type.value"
              @click="editForm.careType = type.value"
              :class="[
                'px-3 py-1.5 rounded-full text-sm transition-colors',
                editForm.careType === type.value
                  ? 'bg-blue-500 text-white'
                  : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
              ]"
            >
              {{ type.label }}
            </button>
          </div>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">护理内容 <span class="text-red-500">*</span></label>
          <t-textarea
            v-model="editForm.careContent"
            placeholder="请输入护理内容"
            :maxlength="500"
            :autosize="{ minRows: 3, maxRows: 6 }"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">备注</label>
          <t-textarea
            v-model="editForm.remark"
            placeholder="选填"
            :maxlength="200"
            :autosize="{ minRows: 2, maxRows: 4 }"
          />
        </div>
      </div>
    </t-dialog>

    <!-- 删除确认弹窗 -->
    <t-dialog
      v-model:visible="deleteConfirmVisible"
      header="确认删除"
      confirm-btn="删除"
      cancel-btn="取消"
      @confirm="confirmDeleteRecord"
    >
      <p>确定要删除这条护理记录吗？</p>
    </t-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import { getTodayRecords, updateCareRecord, deleteCareRecord } from '@/api/careRecord'

const loading = ref(false)
const records = ref([])
const detailVisible = ref(false)
const currentRecord = ref(null)
const editDialogVisible = ref(false)
const submitLoading = ref(false)
const deleteConfirmVisible = ref(false)
const deleteRecordId = ref(null)
const editForm = ref({
  id: null,
  careType: 1,
  careContent: '',
  remark: ''
})

// 护理类型选项
const careTypes = [
  { label: '喂养', value: 1 },
  { label: '洗护', value: 2 },
  { label: '哄睡', value: 3 },
  { label: '早教', value: 4 },
  { label: '其他', value: 5 }
]

// 今日日期字符串
const todayStr = computed(() => {
  const today = new Date()
  const year = today.getFullYear()
  const month = today.getMonth() + 1
  const day = today.getDate()
  const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  return `${year}年${month}月${day}日 ${weekDays[today.getDay()]}`
})

// 类型统计
const typeCounts = computed(() => {
  const counts = {}
  records.value.forEach(r => {
    counts[r.careType] = (counts[r.careType] || 0) + 1
  })
  return counts
})

// 获取类型名称
const getTypeName = (type) => {
  const names = { 1: '喂养', 2: '洗护', 3: '哄睡', 4: '早教', 5: '其他' }
  return names[type] || '其他'
}

// 获取类型文字颜色样式
const getTypeTextClass = (type) => {
  const classes = {
    1: 'text-pink-600',
    2: 'text-blue-600',
    3: 'text-purple-600',
    4: 'text-orange-600',
    5: 'text-gray-600'
  }
  return classes[type] || 'text-gray-600'
}

// 获取护理类型样式
const getCareTypeClass = (type) => {
  const classMap = {
    1: 'px-2 py-0.5 text-xs font-medium text-pink-700 bg-pink-50 rounded',
    2: 'px-2 py-0.5 text-xs font-medium text-blue-700 bg-blue-50 rounded',
    3: 'px-2 py-0.5 text-xs font-medium text-purple-700 bg-purple-50 rounded',
    4: 'px-2 py-0.5 text-xs font-medium text-orange-700 bg-orange-50 rounded',
    5: 'px-2 py-0.5 text-xs font-medium text-gray-700 bg-gray-100 rounded'
  }
  return classMap[type] || 'px-2 py-0.5 text-xs font-medium text-gray-700 bg-gray-100 rounded'
}

// 格式化时间
const formatTime = (time) => {
  if (Array.isArray(time)) {
    return `${String(time[0]).padStart(2, '0')}:${String(time[1]).padStart(2, '0')}`
  } else if (time) {
    return time.substring(0, 5)
  }
  return ''
}

// 格式化日期时间
const formatDateTime = (date, time) => {
  let dateStr = ''
  let timeStr = ''
  
  if (Array.isArray(date)) {
    dateStr = `${date[0]}-${String(date[1]).padStart(2, '0')}-${String(date[2]).padStart(2, '0')}`
  } else if (date) {
    dateStr = date
  }
  
  if (Array.isArray(time)) {
    timeStr = `${String(time[0]).padStart(2, '0')}:${String(time[1]).padStart(2, '0')}`
  } else if (time) {
    timeStr = time.substring(0, 5)
  }
  
  return `${dateStr} ${timeStr}`
}

// 加载今日记录
const loadRecords = async () => {
  loading.value = true
  try {
    const res = await getTodayRecords()
    if (res.code === 200) {
      records.value = res.data || []
    } else {
      records.value = []
    }
  } catch (error) {
    console.error('获取今日记录失败:', error)
    records.value = []
  } finally {
    loading.value = false
  }
}

// 显示记录详情
const showRecordDetail = (record) => {
  currentRecord.value = record
  detailVisible.value = true
}

// 编辑记录
const editRecord = (record) => {
  editForm.value = {
    id: record.id,
    careType: record.careType,
    careContent: record.careContent,
    remark: record.remark || ''
  }
  editDialogVisible.value = true
}

// 更新记录
const handleUpdateRecord = async () => {
  if (!editForm.value.careContent.trim()) {
    MessagePlugin.warning('请输入护理内容')
    return
  }
  
  submitLoading.value = true
  try {
    const res = await updateCareRecord(editForm.value.id, {
      careType: editForm.value.careType,
      careContent: editForm.value.careContent,
      remark: editForm.value.remark
    })
    if (res.code === 200) {
      MessagePlugin.success('修改成功')
      editDialogVisible.value = false
      loadRecords()
    } else {
      MessagePlugin.error(res.message || '修改失败')
    }
  } catch (error) {
    console.error('修改记录失败:', error)
    MessagePlugin.error('修改失败')
  } finally {
    submitLoading.value = false
  }
}

// 删除记录
const handleDeleteRecord = (id) => {
  deleteRecordId.value = id
  deleteConfirmVisible.value = true
}

const confirmDeleteRecord = async () => {
  if (!deleteRecordId.value) return
  try {
    const res = await deleteCareRecord(deleteRecordId.value)
    if (res.code === 200) {
      MessagePlugin.success('删除成功')
      loadRecords()
    } else {
      MessagePlugin.error(res.message || '删除失败')
    }
  } catch (error) {
    console.error('删除记录失败:', error)
    MessagePlugin.error('删除失败')
  } finally {
    deleteConfirmVisible.value = false
    deleteRecordId.value = null
  }
}

onMounted(() => {
  loadRecords()
})
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
