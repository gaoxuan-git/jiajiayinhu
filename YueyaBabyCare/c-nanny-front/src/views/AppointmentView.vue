<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部标题 -->
    <div class="bg-white px-4 py-4 border-b border-gray-200">
      <h1 class="text-xl font-semibold text-gray-800">我的预约</h1>
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

    <!-- 预约列表 -->
    <div v-else-if="appointments.length > 0" class="p-4 space-y-4">
      <div
        v-for="item in appointments"
        :key="item.id"
        @click="goToDetail(item.id)"
        class="bg-white rounded-xl shadow p-4 cursor-pointer hover:shadow-md transition-shadow duration-200"
      >
        <!-- 卡片头部 -->
        <div class="flex justify-between items-start mb-3">
          <div class="flex items-center gap-2">
            <span class="text-sm text-gray-500">预约编号</span>
            <span class="text-sm font-medium text-gray-700">{{ item.appointmentNo || item.id }}</span>
          </div>
          <span :class="getStatusClass(item.status)">
            {{ getStatusText(item.status) }}
          </span>
        </div>

        <!-- 服务信息 -->
        <div class="space-y-2 mb-3">
          <div class="flex items-center gap-2">
            <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
            </svg>
            <span class="text-sm text-gray-600">{{ item.parentName || '家长' }}</span>
          </div>
          <div class="flex items-center gap-2">
            <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
            </svg>
            <span class="text-sm text-gray-600">{{ formatDate(item.appointmentDate) }} {{ formatTime(item.startTime) }}</span>
          </div>
          <div class="flex items-center gap-2">
            <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/>
            </svg>
            <span class="text-sm text-gray-600 line-clamp-1">{{ item.address || '待确认地址' }}</span>
          </div>
        </div>

        <!-- 服务类型 -->
        <div class="flex items-center justify-between pt-3 border-t border-gray-100">
          <span class="px-2 py-1 text-xs font-medium text-blue-700 bg-blue-50 rounded-full">
            {{ getServiceTypeText(item.serviceType) }}
          </span>
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
      <p class="text-gray-400 text-sm">暂无预约记录</p>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyAppointments } from '@/api/appointment'

const router = useRouter()
const loading = ref(false)
const appointments = ref([])
const currentStatus = ref(null)

// 状态筛选标签
const statusTabs = [
  { label: '全部', value: null },
  { label: '待确认', value: 0 },
  { label: '已确认', value: 1 },
  { label: '进行中', value: 2 },
  { label: '已完成', value: 3 },
  { label: '已取消', value: 4 }
]

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '待确认',
    1: '已确认',
    2: '进行中',
    3: '已完成',
    4: '已取消'
  }
  return statusMap[status] || '未知'
}

// 获取状态样式
const getStatusClass = (status) => {
  const classMap = {
    0: 'px-2 py-1 text-xs font-medium text-blue-700 bg-blue-50 rounded-full',
    1: 'px-2 py-1 text-xs font-medium text-emerald-700 bg-emerald-50 rounded-full',
    2: 'px-2 py-1 text-xs font-medium text-amber-700 bg-amber-50 rounded-full',
    3: 'px-2 py-1 text-xs font-medium text-gray-700 bg-gray-100 rounded-full',
    4: 'px-2 py-1 text-xs font-medium text-red-700 bg-red-50 rounded-full'
  }
  return classMap[status] || 'px-2 py-1 text-xs font-medium text-gray-700 bg-gray-100 rounded-full'
}

// 获取服务类型文本
const getServiceTypeText = (type) => {
  const typeMap = {
    1: '日常护理',
    2: '月嫂服务',
    3: '育儿嫂',
    4: '早教服务',
    5: '其他服务'
  }
  return typeMap[type] || '护理服务'
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  let date
  // 处理数组格式 [year, month, day] (Java LocalDate序列化格式)
  if (Array.isArray(dateStr)) {
    date = new Date(dateStr[0], dateStr[1] - 1, dateStr[2])
  } else if (typeof dateStr === 'string') {
    // 处理字符串格式
    date = new Date(dateStr)
  } else {
    return ''
  }
  if (isNaN(date.getTime())) return ''
  const month = date.getMonth() + 1
  const day = date.getDate()
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const weekDay = weekDays[date.getDay()]
  return `${month}月${day}日 ${weekDay}`
}

// 格式化时间 (处理 LocalTime 数组格式 [hour, minute] 或 [hour, minute, second])
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  if (Array.isArray(timeStr)) {
    const hour = String(timeStr[0] || 0).padStart(2, '0')
    const minute = String(timeStr[1] || 0).padStart(2, '0')
    return `${hour}:${minute}`
  } else if (typeof timeStr === 'string') {
    return timeStr.substring(0, 5) // 取 HH:mm 部分
  }
  return ''
}

// 跳转到详情页
const goToDetail = (id) => {
  router.push({ name: 'AppointmentDetail', params: { id } })
}

// 加载预约列表
const loadAppointments = async () => {
  loading.value = true
  try {
    const res = await getMyAppointments(currentStatus.value)
    appointments.value = res.data || []
  } catch (error) {
    console.error('获取预约列表失败:', error)
    appointments.value = []
  } finally {
    loading.value = false
  }
}

// 监听状态变化
watch(currentStatus, () => {
  loadAppointments()
})

onMounted(() => {
  loadAppointments()
})
</script>
