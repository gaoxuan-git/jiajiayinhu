<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部标题 -->
    <div class="bg-white px-4 py-4 border-b border-gray-200">
      <h1 class="text-xl font-semibold text-gray-800">我的排班</h1>
    </div>

    <!-- 统计卡片 -->
    <div class="p-4 grid grid-cols-3 gap-3">
      <div class="bg-white rounded-xl p-3 text-center shadow-sm">
        <div class="text-2xl font-bold text-blue-500">{{ stats.todayCount }}</div>
        <div class="text-xs text-gray-500 mt-1">今日排班</div>
      </div>
      <div class="bg-white rounded-xl p-3 text-center shadow-sm">
        <div class="text-2xl font-bold text-amber-500">{{ stats.swapPending }}</div>
        <div class="text-xs text-gray-500 mt-1">调班待审</div>
      </div>
      <div class="bg-white rounded-xl p-3 text-center shadow-sm">
        <div class="text-2xl font-bold text-gray-700">{{ stats.monthTotal }}</div>
        <div class="text-xs text-gray-500 mt-1">本月总班</div>
      </div>
    </div>

    <!-- 视图切换标签 -->
    <div class="bg-white px-4 py-3 border-b border-gray-200">
      <div class="flex gap-2">
        <button
          v-for="tab in viewTabs"
          :key="tab.value"
          @click="currentView = tab.value"
          :class="[
            'px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-colors duration-200',
            currentView === tab.value
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

    <!-- 日历视图 -->
    <div v-else-if="currentView === 'calendar'" class="p-4">
      <!-- 月份切换 -->
      <div class="bg-white rounded-xl shadow-sm mb-4 p-4">
        <div class="flex justify-between items-center mb-4">
          <button @click="prevMonth" class="p-2 hover:bg-gray-100 rounded-lg transition-colors">
            <svg class="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
            </svg>
          </button>
          <span class="text-lg font-semibold text-gray-800">{{ currentYear }}年{{ currentMonth }}月</span>
          <button @click="nextMonth" class="p-2 hover:bg-gray-100 rounded-lg transition-colors">
            <svg class="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
            </svg>
          </button>
        </div>

        <!-- 星期头 -->
        <div class="grid grid-cols-7 gap-1 mb-2">
          <div v-for="day in weekDays" :key="day" class="text-center text-xs text-gray-500 py-2">
            {{ day }}
          </div>
        </div>

        <!-- 日期网格 -->
        <div class="grid grid-cols-7 gap-1">
          <div
            v-for="(day, index) in calendarDays"
            :key="index"
            @click="day.date && selectDate(day.date)"
            :class="[
              'relative p-2 rounded-lg text-center text-sm transition-colors cursor-pointer min-h-[48px]',
              !day.date ? 'invisible' : '',
              day.isToday ? 'bg-blue-500 text-white' : '',
              day.isSelected && !day.isToday ? 'bg-blue-100 text-blue-700' : '',
              !day.isToday && !day.isSelected && day.date ? 'hover:bg-gray-100' : ''
            ]"
          >
            <span>{{ day.day }}</span>
            <div v-if="day.schedules && day.schedules.length > 0" class="flex justify-center gap-0.5 mt-1">
              <span 
                v-for="(s, i) in day.schedules.slice(0, 3)" 
                :key="i" 
                :class="[
                  'w-1.5 h-1.5 rounded-full',
                  s.status === 2 ? 'bg-amber-500' : s.status === 3 ? 'bg-emerald-500' : day.isToday ? 'bg-white' : 'bg-blue-500'
                ]"
              />
            </div>
          </div>
        </div>
      </div>

      <!-- 选中日期的排班列表 -->
      <div v-if="selectedDate" class="space-y-3">
        <div class="flex items-center justify-between">
          <h3 class="text-base font-semibold text-gray-800">{{ formatSelectedDate }}的排班</h3>
          <span class="text-sm text-gray-500">{{ selectedDateSchedules.length }}个班次</span>
        </div>

        <div v-if="selectedDateSchedules.length === 0" class="bg-white rounded-xl p-8 text-center">
          <svg class="w-12 h-12 text-gray-300 mx-auto mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
          </svg>
          <p class="text-gray-400 text-sm">该日期没有排班</p>
        </div>

        <div
          v-for="schedule in selectedDateSchedules"
          :key="schedule.id"
          @click="showScheduleDetail(schedule)"
          class="bg-white rounded-xl shadow-sm p-4 cursor-pointer hover:shadow-md transition-shadow duration-200"
        >
          <div class="flex justify-between items-start mb-3">
            <div class="flex items-center gap-2">
              <span :class="getShiftTypeClass(schedule.shiftType)">
                {{ getShiftTypeText(schedule.shiftType) }}
              </span>
              <span class="text-sm text-gray-500">{{ getShiftTimeRange(schedule.shiftType) }}</span>
            </div>
            <span :class="getStatusClass(schedule.status)">
              {{ getStatusText(schedule.status) }}
            </span>
          </div>
          
          <div v-if="schedule.remark" class="text-sm text-gray-600 mb-2">
            <span class="text-gray-400">备注：</span>{{ schedule.remark }}
          </div>

          <div class="flex items-center justify-between pt-2 border-t border-gray-100">
            <div class="flex items-center text-gray-400">
              <span class="text-sm">查看详情</span>
              <svg class="w-4 h-4 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
              </svg>
            </div>
            <button
              v-if="schedule.status === 1"
              @click.stop="openSwapDialog(schedule)"
              class="text-sm text-blue-500 hover:text-blue-600"
            >
              申请调班
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 列表视图 -->
    <div v-else-if="currentView === 'list'" class="p-4">
      <!-- 状态筛选 -->
      <div class="flex gap-2 mb-4 overflow-x-auto pb-2">
        <button
          v-for="tab in statusTabs"
          :key="tab.value"
          @click="listStatusFilter = tab.value"
          :class="[
            'px-3 py-1.5 rounded-full text-xs font-medium whitespace-nowrap transition-colors duration-200',
            listStatusFilter === tab.value
              ? 'bg-blue-500 text-white'
              : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
          ]"
        >
          {{ tab.label }}
        </button>
      </div>

      <div v-if="filteredSchedules.length === 0" class="flex flex-col items-center justify-center py-16">
        <svg class="w-16 h-16 text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
        </svg>
        <p class="text-gray-400 text-sm">暂无排班记录</p>
      </div>

      <div v-else class="space-y-3">
        <div
          v-for="schedule in filteredSchedules"
          :key="schedule.id"
          @click="showScheduleDetail(schedule)"
          class="bg-white rounded-xl shadow-sm p-4 cursor-pointer hover:shadow-md transition-shadow duration-200"
        >
          <div class="flex justify-between items-start mb-3">
            <div>
              <div class="flex items-center gap-2 mb-1">
                <span class="text-sm font-medium text-gray-800">{{ formatDate(schedule.scheduleDate) }}</span>
                <span :class="getShiftTypeClass(schedule.shiftType)">
                  {{ getShiftTypeText(schedule.shiftType) }}
                </span>
              </div>
              <div class="text-xs text-gray-500">{{ getShiftTimeRange(schedule.shiftType) }}</div>
            </div>
            <span :class="getStatusClass(schedule.status)">
              {{ getStatusText(schedule.status) }}
            </span>
          </div>
          
          <div v-if="schedule.remark" class="text-sm text-gray-600 mb-2">
            <span class="text-gray-400">备注：</span>{{ schedule.remark }}
          </div>

          <div class="flex items-center justify-between pt-2 border-t border-gray-100">
            <div class="flex items-center text-gray-400">
              <span class="text-sm">查看详情</span>
              <svg class="w-4 h-4 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
              </svg>
            </div>
            <button
              v-if="schedule.status === 1"
              @click.stop="openSwapDialog(schedule)"
              class="text-sm text-blue-500 hover:text-blue-600"
            >
              申请调班
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 调班记录视图 -->
    <div v-else-if="currentView === 'swap'" class="p-4">
      <div v-if="swapRecords.length === 0" class="flex flex-col items-center justify-center py-16">
        <svg class="w-16 h-16 text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4"/>
        </svg>
        <p class="text-gray-400 text-sm">暂无调班记录</p>
      </div>

      <div v-else class="space-y-3">
        <div
          v-for="record in swapRecords"
          :key="record.id"
          class="bg-white rounded-xl shadow-sm p-4"
        >
          <div class="flex justify-between items-start mb-3">
            <div>
              <div class="text-sm font-medium text-gray-800 mb-1">{{ formatDate(record.scheduleDate) }}</div>
              <div class="text-xs text-gray-500">{{ getShiftTypeText(record.shiftType) }} {{ getShiftTimeRange(record.shiftType) }}</div>
            </div>
            <span :class="getSwapStatusClass(record.status)">
              {{ getSwapStatusText(record.status) }}
            </span>
          </div>
          
          <div class="bg-gray-50 rounded-lg p-3 mb-3">
            <div class="flex items-center gap-2 text-sm text-gray-600 mb-2">
              <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
              </svg>
              <span>目标保姆：{{ record.targetNannyName || '未指定' }}</span>
            </div>
            <div class="flex items-start gap-2 text-sm text-gray-600">
              <svg class="w-4 h-4 text-gray-400 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z"/>
              </svg>
              <span>原因：{{ record.swapReason || '未填写' }}</span>
            </div>
          </div>

          <div class="text-xs text-gray-400">
            申请时间：{{ formatDateTime(record.updateTime || record.createTime) }}
          </div>
        </div>
      </div>
    </div>

    <!-- 排班详情弹窗 -->
    <div v-if="showDetailDialog" class="fixed inset-0 bg-black/50 flex items-end justify-center z-50" @click.self="showDetailDialog = false">
      <div class="bg-white rounded-t-2xl w-full max-h-[80vh] overflow-y-auto animate-slide-up">
        <div class="sticky top-0 bg-white px-4 py-4 border-b border-gray-200 flex justify-between items-center">
          <h3 class="text-lg font-semibold text-gray-800">排班详情</h3>
          <button @click="showDetailDialog = false" class="p-1 hover:bg-gray-100 rounded-lg">
            <svg class="w-5 h-5 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
          </button>
        </div>
        
        <div class="p-4 space-y-4">
          <div class="flex items-center justify-between">
            <span class="text-sm text-gray-500">排班日期</span>
            <span class="text-sm font-medium text-gray-800">{{ formatDate(currentSchedule?.scheduleDate) }}</span>
          </div>
          <div class="flex items-center justify-between">
            <span class="text-sm text-gray-500">班次类型</span>
            <span :class="getShiftTypeClass(currentSchedule?.shiftType)">
              {{ getShiftTypeText(currentSchedule?.shiftType) }}
            </span>
          </div>
          <div class="flex items-center justify-between">
            <span class="text-sm text-gray-500">工作时间</span>
            <span class="text-sm text-gray-800">{{ getShiftTimeRange(currentSchedule?.shiftType) }}</span>
          </div>
          <div class="flex items-center justify-between">
            <span class="text-sm text-gray-500">当前状态</span>
            <span :class="getStatusClass(currentSchedule?.status)">
              {{ getStatusText(currentSchedule?.status) }}
            </span>
          </div>
          <div v-if="currentSchedule?.remark">
            <span class="text-sm text-gray-500 block mb-1">备注信息</span>
            <div class="bg-gray-50 rounded-lg p-3 text-sm text-gray-600">
              {{ currentSchedule.remark }}
            </div>
          </div>
          
          <!-- 调班信息 -->
          <div v-if="currentSchedule?.status === 2 || currentSchedule?.status === 3" class="bg-amber-50 rounded-lg p-3">
            <div class="text-sm font-medium text-amber-700 mb-2">调班信息</div>
            <div class="text-sm text-amber-600">
              <div v-if="currentSchedule?.targetNannyName">目标保姆：{{ currentSchedule.targetNannyName }}</div>
              <div v-if="currentSchedule?.swapReason">调班原因：{{ currentSchedule.swapReason }}</div>
            </div>
          </div>
        </div>

        <div class="p-4 border-t border-gray-200">
          <button
            v-if="currentSchedule?.status === 1"
            @click="openSwapDialog(currentSchedule); showDetailDialog = false"
            class="w-full bg-blue-500 hover:bg-blue-600 text-white font-medium py-3 rounded-lg transition-colors duration-200"
          >
            申请调班
          </button>
          <button
            v-else
            @click="showDetailDialog = false"
            class="w-full bg-gray-100 text-gray-700 font-medium py-3 rounded-lg hover:bg-gray-200 transition-colors duration-200"
          >
            关闭
          </button>
        </div>
      </div>
    </div>

    <!-- 调班申请弹窗 -->
    <div v-if="showSwapDialog" class="fixed inset-0 bg-black/50 flex items-end justify-center z-50" @click.self="showSwapDialog = false">
      <div class="bg-white rounded-t-2xl w-full max-h-[80vh] overflow-y-auto animate-slide-up">
        <div class="sticky top-0 bg-white px-4 py-4 border-b border-gray-200 flex justify-between items-center">
          <h3 class="text-lg font-semibold text-gray-800">申请调班</h3>
          <button @click="showSwapDialog = false" class="p-1 hover:bg-gray-100 rounded-lg">
            <svg class="w-5 h-5 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
          </button>
        </div>
        
        <div class="p-4 space-y-4">
          <!-- 当前排班信息 -->
          <div class="bg-blue-50 rounded-lg p-3">
            <div class="text-sm font-medium text-blue-700 mb-2">当前排班</div>
            <div class="text-sm text-blue-600">
              {{ formatDate(swapSchedule?.scheduleDate) }} · {{ getShiftTypeText(swapSchedule?.shiftType) }}
            </div>
          </div>

          <!-- 选择目标保姆 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">选择目标保姆（可选）</label>
            <div v-if="loadingNannies" class="text-center py-4">
              <t-loading size="small" text="加载中..." />
            </div>
            <div v-else-if="availableNannies.length === 0" class="text-sm text-gray-500 py-2">
              暂无可调班的保姆，可直接提交由机构安排
            </div>
            <div v-else class="space-y-2">
              <div
                v-for="nanny in availableNannies"
                :key="nanny.id"
                @click="swapForm.targetNannyId = swapForm.targetNannyId === nanny.id ? null : nanny.id"
                :class="[
                  'flex items-center justify-between p-3 rounded-lg border cursor-pointer transition-colors',
                  swapForm.targetNannyId === nanny.id
                    ? 'border-blue-500 bg-blue-50'
                    : 'border-gray-200 hover:bg-gray-50'
                ]"
              >
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center">
                    <svg class="w-5 h-5 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
                    </svg>
                  </div>
                  <span class="text-sm font-medium text-gray-800">{{ nanny.realName }}</span>
                </div>
                <div v-if="swapForm.targetNannyId === nanny.id" class="text-blue-500">
                  <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
                  </svg>
                </div>
              </div>
            </div>
          </div>

          <!-- 调班原因 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">调班原因 <span class="text-red-500">*</span></label>
            <textarea
              v-model="swapForm.reason"
              rows="3"
              class="w-full px-3 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200"
              placeholder="请输入调班原因..."
            />
          </div>
        </div>

        <div class="p-4 border-t border-gray-200 flex gap-3">
          <button
            @click="showSwapDialog = false"
            class="flex-1 bg-gray-100 text-gray-700 font-medium py-3 rounded-lg hover:bg-gray-200 transition-colors duration-200"
          >
            取消
          </button>
          <button
            @click="submitSwapRequest"
            :disabled="!swapForm.reason || submitting"
            :class="[
              'flex-1 font-medium py-3 rounded-lg transition-colors duration-200',
              !swapForm.reason || submitting
                ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                : 'bg-blue-500 hover:bg-blue-600 text-white'
            ]"
          >
            {{ submitting ? '提交中...' : '提交申请' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import { getMySchedules, applySwap, getSwapNannies } from '@/api/nanny'

// 视图切换
const currentView = ref('calendar')
const viewTabs = [
  { label: '日历视图', value: 'calendar' },
  { label: '排班列表', value: 'list' },
  { label: '调班记录', value: 'swap' }
]

// 数据状态
const loading = ref(false)
const submitting = ref(false)
const schedules = ref([])
const stats = ref({
  todayCount: 0,
  swapPending: 0,
  monthTotal: 0
})

// 日历相关
const currentYear = ref(new Date().getFullYear())
const currentMonth = ref(new Date().getMonth() + 1)
const selectedDate = ref(null)
const weekDays = ['日', '一', '二', '三', '四', '五', '六']

// 列表筛选
const listStatusFilter = ref(null)
const statusTabs = [
  { label: '全部', value: null },
  { label: '正常', value: 1 },
  { label: '调班申请中', value: 2 },
  { label: '调班已批准', value: 3 },
  { label: '已取消', value: 0 }
]

// 弹窗状态
const showDetailDialog = ref(false)
const showSwapDialog = ref(false)
const currentSchedule = ref(null)
const swapSchedule = ref(null)

// 调班表单
const swapForm = ref({
  targetNannyId: null,
  reason: ''
})
const loadingNannies = ref(false)
const availableNannies = ref([])

// 计算属性
const calendarDays = computed(() => {
  const year = currentYear.value
  const month = currentMonth.value
  
  // 获取本月第一天是星期几
  const firstDay = new Date(year, month - 1, 1).getDay()
  // 获取本月有多少天
  const daysInMonth = new Date(year, month, 0).getDate()
  
  const days = []
  const today = new Date()
  const todayStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`
  
  // 填充前置空白
  for (let i = 0; i < firstDay; i++) {
    days.push({ date: null, day: '' })
  }
  
  // 填充日期
  for (let d = 1; d <= daysInMonth; d++) {
    const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(d).padStart(2, '0')}`
    const daySchedules = schedules.value.filter(s => formatDateString(s.scheduleDate) === dateStr)
    
    days.push({
      date: dateStr,
      day: d,
      isToday: dateStr === todayStr,
      isSelected: dateStr === selectedDate.value,
      schedules: daySchedules
    })
  }
  
  return days
})

const selectedDateSchedules = computed(() => {
  if (!selectedDate.value) return []
  return schedules.value.filter(s => formatDateString(s.scheduleDate) === selectedDate.value)
})

const filteredSchedules = computed(() => {
  if (listStatusFilter.value === null) return schedules.value
  return schedules.value.filter(s => s.status === listStatusFilter.value)
})

const swapRecords = computed(() => {
  return schedules.value.filter(s => s.status === 2 || s.status === 3)
})

const formatSelectedDate = computed(() => {
  if (!selectedDate.value) return ''
  const date = new Date(selectedDate.value)
  const month = date.getMonth() + 1
  const day = date.getDate()
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return `${month}月${day}日 ${weekDays[date.getDay()]}`
})

// 方法
const loadSchedules = async () => {
  loading.value = true
  try {
    // 获取当前月的排班数据
    const startDate = `${currentYear.value}-${String(currentMonth.value).padStart(2, '0')}-01`
    const endDate = `${currentYear.value}-${String(currentMonth.value).padStart(2, '0')}-${new Date(currentYear.value, currentMonth.value, 0).getDate()}`
    
    const res = await getMySchedules({ startDate, endDate })
    schedules.value = res.data || []
    
    // 计算统计数据
    const today = new Date()
    const todayStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`
    
    stats.value.todayCount = schedules.value.filter(s => formatDateString(s.scheduleDate) === todayStr && s.status === 1).length
    stats.value.swapPending = schedules.value.filter(s => s.status === 2).length
    stats.value.monthTotal = schedules.value.filter(s => s.status !== 0).length
    
    // 默认选中今天
    if (!selectedDate.value) {
      selectedDate.value = todayStr
    }
  } catch (error) {
    console.error('获取排班数据失败:', error)
    MessagePlugin.error('获取排班数据失败')
  } finally {
    loading.value = false
  }
}

const prevMonth = () => {
  if (currentMonth.value === 1) {
    currentMonth.value = 12
    currentYear.value--
  } else {
    currentMonth.value--
  }
  selectedDate.value = null
  loadSchedules()
}

const nextMonth = () => {
  if (currentMonth.value === 12) {
    currentMonth.value = 1
    currentYear.value++
  } else {
    currentMonth.value++
  }
  selectedDate.value = null
  loadSchedules()
}

const selectDate = (date) => {
  selectedDate.value = date
}

const showScheduleDetail = (schedule) => {
  currentSchedule.value = schedule
  showDetailDialog.value = true
}

const openSwapDialog = async (schedule) => {
  swapSchedule.value = schedule
  swapForm.value = { targetNannyId: null, reason: '' }
  showSwapDialog.value = true
  
  // 加载可调班保姆列表
  loadingNannies.value = true
  try {
    const res = await getSwapNannies(schedule.id)
    availableNannies.value = res.data || []
  } catch (error) {
    console.error('获取可调班保姆失败:', error)
    availableNannies.value = []
  } finally {
    loadingNannies.value = false
  }
}

const submitSwapRequest = async () => {
  if (!swapForm.value.reason) {
    MessagePlugin.warning('请输入调班原因')
    return
  }
  
  submitting.value = true
  try {
    await applySwap(swapSchedule.value.id, {
      targetNannyId: swapForm.value.targetNannyId,
      reason: swapForm.value.reason
    })
    MessagePlugin.success('调班申请已提交')
    showSwapDialog.value = false
    loadSchedules()
  } catch (error) {
    console.error('提交调班申请失败:', error)
    MessagePlugin.error(error.response?.data?.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

// 格式化方法
const formatDateString = (dateVal) => {
  if (!dateVal) return ''
  // 处理数组格式 [year, month, day]
  if (Array.isArray(dateVal)) {
    return `${dateVal[0]}-${String(dateVal[1]).padStart(2, '0')}-${String(dateVal[2]).padStart(2, '0')}`
  }
  // 处理字符串格式
  if (typeof dateVal === 'string') {
    return dateVal.substring(0, 10)
  }
  return ''
}

const formatDate = (dateVal) => {
  if (!dateVal) return ''
  let date
  if (Array.isArray(dateVal)) {
    date = new Date(dateVal[0], dateVal[1] - 1, dateVal[2])
  } else if (typeof dateVal === 'string') {
    date = new Date(dateVal)
  } else {
    return ''
  }
  if (isNaN(date.getTime())) return ''
  const month = date.getMonth() + 1
  const day = date.getDate()
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return `${month}月${day}日 ${weekDays[date.getDay()]}`
}

const formatDateTime = (dateVal) => {
  if (!dateVal) return ''
  let date
  if (Array.isArray(dateVal)) {
    // LocalDateTime 格式: [year, month, day, hour, minute, second]
    if (dateVal.length >= 5) {
      date = new Date(dateVal[0], dateVal[1] - 1, dateVal[2], dateVal[3] || 0, dateVal[4] || 0)
    } else {
      date = new Date(dateVal[0], dateVal[1] - 1, dateVal[2])
    }
  } else if (typeof dateVal === 'string') {
    date = new Date(dateVal)
  } else {
    return ''
  }
  if (isNaN(date.getTime())) return ''
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  return `${month}月${day}日 ${hour}:${minute}`
}

const getShiftTypeText = (type) => {
  const typeMap = { 1: '早班', 2: '中班', 3: '晚班', 4: '全天' }
  return typeMap[type] || '未知'
}

const getShiftTypeClass = (type) => {
  const classMap = {
    1: 'px-2 py-0.5 text-xs font-medium text-amber-700 bg-amber-50 rounded-full',
    2: 'px-2 py-0.5 text-xs font-medium text-blue-700 bg-blue-50 rounded-full',
    3: 'px-2 py-0.5 text-xs font-medium text-purple-700 bg-purple-50 rounded-full',
    4: 'px-2 py-0.5 text-xs font-medium text-emerald-700 bg-emerald-50 rounded-full'
  }
  return classMap[type] || 'px-2 py-0.5 text-xs font-medium text-gray-700 bg-gray-100 rounded-full'
}

const getShiftTimeRange = (type) => {
  const timeMap = { 1: '08:00 - 12:00', 2: '12:00 - 18:00', 3: '18:00 - 22:00', 4: '08:00 - 22:00' }
  return timeMap[type] || ''
}

const getStatusText = (status) => {
  const statusMap = { 0: '已取消', 1: '正常', 2: '调班申请中', 3: '调班已批准' }
  return statusMap[status] || '未知'
}

const getStatusClass = (status) => {
  const classMap = {
    0: 'px-2 py-0.5 text-xs font-medium text-gray-700 bg-gray-100 rounded-full',
    1: 'px-2 py-0.5 text-xs font-medium text-emerald-700 bg-emerald-50 rounded-full',
    2: 'px-2 py-0.5 text-xs font-medium text-amber-700 bg-amber-50 rounded-full',
    3: 'px-2 py-0.5 text-xs font-medium text-blue-700 bg-blue-50 rounded-full'
  }
  return classMap[status] || 'px-2 py-0.5 text-xs font-medium text-gray-700 bg-gray-100 rounded-full'
}

const getSwapStatusText = (status) => {
  if (status === 2) return '待审核'
  if (status === 3) return '已批准'
  return '未知'
}

const getSwapStatusClass = (status) => {
  if (status === 2) return 'px-2 py-0.5 text-xs font-medium text-amber-700 bg-amber-50 rounded-full'
  if (status === 3) return 'px-2 py-0.5 text-xs font-medium text-emerald-700 bg-emerald-50 rounded-full'
  return 'px-2 py-0.5 text-xs font-medium text-gray-700 bg-gray-100 rounded-full'
}

// 监听视图切换
watch(currentView, (newView) => {
  if (newView === 'list' || newView === 'swap') {
    // 切换到列表或调班视图时重新加载数据
    loadSchedules()
  }
})

onMounted(() => {
  loadSchedules()
})
</script>

<style scoped>
.animate-slide-up {
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}
</style>
