<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部标题 -->
    <div class="bg-white px-4 py-4 border-b border-gray-200">
      <h1 class="text-xl font-semibold text-gray-800">我的护理计划</h1>
    </div>

    <!-- 状态筛选标签 -->
    <div class="bg-white px-4 py-3 border-b border-gray-200">
      <div class="flex gap-2 overflow-x-auto">
        <button
          v-for="tab in statusTabs"
          :key="tab.value"
          @click="currentStatus = tab.value"
          :class="[
            'px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-colors duration-200',
            currentStatus === tab.value
              ? 'bg-blue-500 text-white'
              : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
          ]"
        >
          {{ tab.label }}
        </button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex justify-center items-center py-20">
      <t-loading size="large" text="加载中..." />
    </div>

    <!-- 计划列表 -->
    <div v-else-if="plans.length > 0" class="p-4 space-y-4">
      <div
        v-for="item in plans"
        :key="item.id"
        @click="goToDetail(item.id)"
        class="bg-white rounded-xl shadow p-4 cursor-pointer hover:shadow-md transition-shadow duration-200"
      >
        <!-- 卡片头部 -->
        <div class="flex justify-between items-start mb-3">
          <div>
            <h3 class="text-base font-semibold text-gray-800 mb-1">{{ item.planName }}</h3>
            <div v-if="item.appointmentNo" class="flex items-center gap-1 text-xs text-blue-600">
              <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.828 10.172a4 4 0 00-5.656 0l-4 4a4 4 0 105.656 5.656l1.102-1.101m-.758-4.899a4 4 0 005.656 0l4-4a4 4 0 00-5.656-5.656l-1.1 1.1"/>
              </svg>
              <span>{{ item.appointmentNo }}</span>
            </div>
          </div>
          <span :class="getStatusClass(item.status)">
            {{ item.statusName }}
          </span>
        </div>

        <!-- 计划内容预览 -->
        <p class="text-sm text-gray-500 line-clamp-2 mb-3">{{ item.planContent }}</p>

        <!-- 关联信息 -->
        <div class="space-y-2 mb-3">
          <div class="flex items-center gap-2">
            <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
            </svg>
            <span class="text-sm text-gray-600">宝宝: {{ item.babyName }}</span>
          </div>
          <div class="flex items-center gap-2">
            <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
            </svg>
            <span class="text-sm text-gray-600">{{ item.startDate }} ~ {{ item.endDate }}</span>
          </div>
        </div>

        <!-- 进度条 -->
        <div class="mb-3">
          <div class="flex justify-between text-xs text-gray-500 mb-1">
            <span>执行进度</span>
            <span>{{ item.executionDays || 0 }}/{{ item.totalDays || 0 }}天</span>
          </div>
          <div class="w-full bg-gray-200 rounded-full h-2">
            <div
              class="h-2 rounded-full transition-all duration-300"
              :class="getProgressClass(item.status)"
              :style="{ width: `${item.progressPercent || 0}%` }"
            ></div>
          </div>
        </div>

        <!-- 底部 -->
        <div class="flex items-center justify-between pt-3 border-t border-gray-100">
          <span class="text-xs text-gray-400">{{ item.institutionName }}</span>
          <div class="flex items-center text-gray-400">
            <span class="text-sm">查看详情</span>
            <svg class="w-4 h-4 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="flex flex-col items-center justify-center py-20">
      <svg class="w-16 h-16 text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
      </svg>
      <p class="text-gray-400 text-sm">暂无分配给您的护理计划</p>
    </div>

    <!-- 详情抽屉 -->
    <t-drawer
      v-model:visible="detailVisible"
      :header="'计划详情'"
      placement="bottom"
      size="90%"
      :footer="false"
    >
      <div v-if="currentPlan" class="space-y-4 pb-20">
        <!-- 基本信息 -->
        <div class="bg-gray-50 rounded-xl p-4">
          <h3 class="text-base font-semibold text-gray-800 mb-2">{{ currentPlan.planName }}</h3>
          <div class="flex items-center gap-2 mb-3">
            <span :class="getStatusClass(currentPlan.status)">{{ currentPlan.statusName }}</span>
            <span v-if="currentPlan.appointmentNo" class="text-xs text-blue-600">
              关联预约: {{ currentPlan.appointmentNo }}
            </span>
            <span v-if="canExecute" class="text-xs text-green-600 bg-green-50 px-2 py-0.5 rounded-full">
              已签到
            </span>
          </div>
          
          <!-- 进度 -->
          <div class="mb-3">
            <div class="flex justify-between text-sm text-gray-600 mb-1">
              <span>已执行 {{ currentPlan.executionDays || 0 }} 天</span>
              <span>共 {{ currentPlan.totalDays || 0 }} 天</span>
            </div>
            <div class="w-full bg-gray-200 rounded-full h-2.5">
              <div
                class="h-2.5 rounded-full"
                :class="getProgressClass(currentPlan.status)"
                :style="{ width: `${currentPlan.progressPercent || 0}%` }"
              ></div>
            </div>
          </div>

          <div class="text-sm text-gray-600">
            <p>{{ currentPlan.startDate }} ~ {{ currentPlan.endDate }}</p>
          </div>
        </div>

        <!-- 关联信息 -->
        <div class="space-y-3">
          <div class="flex justify-between py-2 border-b border-gray-100">
            <span class="text-sm text-gray-500">宝宝</span>
            <span class="text-sm text-gray-800">{{ currentPlan.babyName }}</span>
          </div>
          <div class="flex justify-between py-2 border-b border-gray-100">
            <span class="text-sm text-gray-500">家长</span>
            <span class="text-sm text-gray-800">{{ currentPlan.parentName }} {{ currentPlan.parentPhone ? `(${currentPlan.parentPhone})` : '' }}</span>
          </div>
          <div class="flex justify-between py-2 border-b border-gray-100">
            <span class="text-sm text-gray-500">机构</span>
            <span class="text-sm text-gray-800">{{ currentPlan.institutionName }}</span>
          </div>
        </div>

        <!-- 计划内容 -->
        <div>
          <h4 class="text-sm font-medium text-gray-700 mb-2">计划内容</h4>
          <div class="bg-gray-50 rounded-lg p-3 text-sm text-gray-600 whitespace-pre-wrap">
            {{ currentPlan.planContent || '暂无详细内容' }}
          </div>
        </div>

        <!-- 护理记录列表 -->
        <div>
          <div class="flex items-center justify-between mb-3">
            <h4 class="text-sm font-medium text-gray-700">护理记录</h4>
            <span class="text-xs text-gray-400">{{ careRecords.length }} 条记录</span>
          </div>
          
          <div v-if="recordsLoading" class="flex justify-center py-4">
            <t-loading size="small" />
          </div>
          <div v-else-if="careRecords.length > 0" class="space-y-3">
            <div 
              v-for="record in careRecords" 
              :key="record.id" 
              class="bg-white border border-gray-100 rounded-lg p-3"
            >
              <div class="flex items-start justify-between mb-2">
                <div class="flex items-center gap-2">
                  <span :class="getCareTypeClass(record.careType)">{{ record.careTypeName }}</span>
                  <span class="text-xs text-gray-400">{{ formatRecordTime(record.recordDate, record.recordTime) }}</span>
                </div>
                <button 
                  v-if="canExecute" 
                  @click.stop="handleDeleteRecord(record.id)" 
                  class="text-gray-400 hover:text-red-500"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                  </svg>
                </button>
              </div>
              <p class="text-sm text-gray-700">{{ record.careContent }}</p>
              <p v-if="record.remark" class="text-xs text-gray-500 mt-1">备注: {{ record.remark }}</p>
            </div>
          </div>
          <div v-else class="text-center py-6 text-gray-400 text-sm">
            暂无护理记录
          </div>
        </div>

        <!-- 添加记录按钮（固定在底部） -->
        <div v-if="canExecute && (currentPlan.status === 0 || currentPlan.status === 1)" class="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 p-4">
          <button 
            @click="showAddRecordDialog = true"
            class="w-full py-3 bg-blue-500 text-white rounded-xl font-medium hover:bg-blue-600 transition-colors"
          >
            + 添加护理记录
          </button>
        </div>
        <div v-else-if="!canExecute && (currentPlan.status === 0 || currentPlan.status === 1)" class="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 p-4">
          <div class="text-center text-gray-500 text-sm py-2">
            请先到关联预约签到后再添加护理记录
          </div>
        </div>
      </div>
    </t-drawer>

    <!-- 添加护理记录弹窗 -->
    <t-dialog
      v-model:visible="showAddRecordDialog"
      header="添加护理记录"
      :confirm-btn="{ content: '提交', loading: submitLoading }"
      cancel-btn="取消"
      @confirm="handleAddRecord"
    >
      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">护理类型 <span class="text-red-500">*</span></label>
          <div class="flex flex-wrap gap-2">
            <button
              v-for="type in careTypes"
              :key="type.value"
              @click="recordForm.careType = type.value"
              :class="[
                'px-3 py-1.5 rounded-full text-sm transition-colors',
                recordForm.careType === type.value
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
            v-model="recordForm.careContent"
            placeholder="请输入护理内容"
            :maxlength="500"
            :autosize="{ minRows: 3, maxRows: 6 }"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">备注</label>
          <t-textarea
            v-model="recordForm.remark"
            placeholder="选填，如有特殊情况请备注"
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
import { ref, watch, onMounted } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import { getMyCarePlans, getCarePlanById } from '@/api/carePlan'
import { getCareRecords, addCareRecord, deleteCareRecord, canExecutePlan } from '@/api/careRecord'

const loading = ref(false)
const plans = ref([])
const currentStatus = ref(null)
const detailVisible = ref(false)
const currentPlan = ref(null)

// 护理记录相关
const careRecords = ref([])
const recordsLoading = ref(false)
const canExecute = ref(false)
const showAddRecordDialog = ref(false)
const submitLoading = ref(false)
const recordForm = ref({
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

// 状态筛选标签
const statusTabs = [
  { label: '全部', value: null },
  { label: '待执行', value: 0 },
  { label: '进行中', value: 1 },
  { label: '已完成', value: 2 },
  { label: '已取消', value: 3 }
]

// 获取状态样式
const getStatusClass = (status) => {
  const classMap = {
    0: 'px-2 py-1 text-xs font-medium text-amber-700 bg-amber-50 rounded-full',
    1: 'px-2 py-1 text-xs font-medium text-blue-700 bg-blue-50 rounded-full',
    2: 'px-2 py-1 text-xs font-medium text-emerald-700 bg-emerald-50 rounded-full',
    3: 'px-2 py-1 text-xs font-medium text-gray-700 bg-gray-100 rounded-full'
  }
  return classMap[status] || 'px-2 py-1 text-xs font-medium text-gray-700 bg-gray-100 rounded-full'
}

// 获取进度条样式
const getProgressClass = (status) => {
  const classMap = {
    0: 'bg-amber-400',
    1: 'bg-blue-500',
    2: 'bg-emerald-500',
    3: 'bg-gray-400'
  }
  return classMap[status] || 'bg-blue-500'
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

// 格式化记录时间
const formatRecordTime = (date, time) => {
  let dateStr = ''
  let timeStr = ''
  
  // 处理日期
  if (Array.isArray(date)) {
    dateStr = `${date[0]}-${String(date[1]).padStart(2, '0')}-${String(date[2]).padStart(2, '0')}`
  } else if (date) {
    dateStr = date
  }
  
  // 处理时间
  if (Array.isArray(time)) {
    timeStr = `${String(time[0]).padStart(2, '0')}:${String(time[1]).padStart(2, '0')}`
  } else if (time) {
    timeStr = time.substring(0, 5)
  }
  
  return `${dateStr} ${timeStr}`
}

// 跳转到详情
const goToDetail = async (id) => {
  try {
    const res = await getCarePlanById(id)
    if (res.code === 200) {
      currentPlan.value = res.data
      detailVisible.value = true
      // 加载护理记录和签到状态
      loadCareRecords(id)
      checkCanExecute(id)
    } else {
      MessagePlugin.error(res.message || '获取详情失败')
    }
  } catch (error) {
    console.error('获取详情失败:', error)
    MessagePlugin.error('获取详情失败')
  }
}

// 加载护理记录
const loadCareRecords = async (carePlanId) => {
  recordsLoading.value = true
  try {
    const res = await getCareRecords(carePlanId)
    if (res.code === 200) {
      careRecords.value = res.data || []
    } else {
      careRecords.value = []
    }
  } catch (error) {
    console.error('获取护理记录失败:', error)
    careRecords.value = []
  } finally {
    recordsLoading.value = false
  }
}

// 检查是否可以执行计划
const checkCanExecute = async (carePlanId) => {
  try {
    const res = await canExecutePlan(carePlanId)
    if (res.code === 200) {
      canExecute.value = res.data === true
    } else {
      canExecute.value = false
    }
  } catch (error) {
    console.error('检查执行状态失败:', error)
    canExecute.value = false
  }
}

// 添加护理记录
const handleAddRecord = async () => {
  if (!recordForm.value.careContent.trim()) {
    MessagePlugin.warning('请输入护理内容')
    return
  }
  
  submitLoading.value = true
  try {
    const res = await addCareRecord(currentPlan.value.id, {
      careType: recordForm.value.careType,
      careContent: recordForm.value.careContent,
      remark: recordForm.value.remark
    })
    if (res.code === 200) {
      MessagePlugin.success('添加成功')
      showAddRecordDialog.value = false
      // 重置表单
      recordForm.value = { careType: 1, careContent: '', remark: '' }
      // 刷新记录列表
      loadCareRecords(currentPlan.value.id)
    } else {
      MessagePlugin.error(res.message || '添加失败')
    }
  } catch (error) {
    console.error('添加护理记录失败:', error)
    MessagePlugin.error('添加失败')
  } finally {
    submitLoading.value = false
  }
}

// 删除护理记录
const deleteConfirmVisible = ref(false)
const deleteRecordId = ref(null)

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
      loadCareRecords(currentPlan.value.id)
    } else {
      MessagePlugin.error(res.message || '删除失败')
    }
  } catch (error) {
    console.error('删除护理记录失败:', error)
    MessagePlugin.error('删除失败')
  } finally {
    deleteConfirmVisible.value = false
    deleteRecordId.value = null
  }
}

// 加载计划列表
const loadPlans = async () => {
  loading.value = true
  try {
    const res = await getMyCarePlans(currentStatus.value)
    if (res.code === 200) {
      plans.value = res.data || []
    } else {
      plans.value = []
    }
  } catch (error) {
    console.error('获取护理计划列表失败:', error)
    plans.value = []
  } finally {
    loading.value = false
  }
}

// 监听状态变化
watch(currentStatus, () => {
  loadPlans()
})

// 监听抽屉关闭，重置状态
watch(detailVisible, (val) => {
  if (!val) {
    careRecords.value = []
    canExecute.value = false
  }
})

onMounted(() => {
  loadPlans()
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
