<template>
  <div class="min-h-screen bg-gray-50 pb-24">
    <!-- 顶部导航 -->
    <div class="bg-white px-4 py-4 border-b border-gray-200 flex items-center gap-3">
      <button @click="goBack" class="text-gray-600 hover:text-gray-800 transition-colors">
        <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
        </svg>
      </button>
      <h1 class="text-xl font-semibold text-gray-800">预约详情</h1>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex justify-center items-center py-20">
      <t-loading size="large" text="加载中..." />
    </div>

    <!-- 详情内容 -->
    <div v-else-if="appointment" class="p-4 space-y-4">
      <!-- 状态卡片 -->
      <div class="bg-white rounded-xl shadow p-6">
        <div class="flex items-center justify-between mb-4">
          <span class="text-lg font-semibold text-gray-800">预约状态</span>
          <span :class="getStatusClass(appointment.status)">
            {{ getStatusText(appointment.status) }}
          </span>
        </div>
        <div class="text-sm text-gray-500">
          预约编号：{{ appointment.appointmentNo || appointment.id }}
        </div>
        <!-- 今日签到状态 -->
        <div v-if="canCheckin" class="mt-4 pt-4 border-t border-gray-100">
          <div class="flex items-center justify-between">
            <span class="text-sm text-gray-500">今日签到状态</span>
            <span :class="checkinStatusClass">{{ checkinStatusText }}</span>
          </div>
        </div>
      </div>

      <!-- 服务信息 -->
      <div class="bg-white rounded-xl shadow p-6">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">服务信息</h3>
        <div class="space-y-3">
          <div class="flex items-start gap-3">
            <div class="w-8 h-8 rounded-full bg-blue-50 flex items-center justify-center shrink-0">
              <svg class="w-4 h-4 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
              </svg>
            </div>
            <div>
              <div class="text-sm text-gray-500">服务类型</div>
              <div class="text-base text-gray-800 font-medium">{{ getServiceTypeText(appointment.serviceType) }}</div>
            </div>
          </div>
          <div class="flex items-start gap-3">
            <div class="w-8 h-8 rounded-full bg-emerald-50 flex items-center justify-center shrink-0">
              <svg class="w-4 h-4 text-emerald-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
              </svg>
            </div>
            <div>
              <div class="text-sm text-gray-500">预约时间</div>
              <div class="text-base text-gray-800 font-medium">{{ formatDateTime(appointment.appointmentDate) }} {{ formatTime(appointment.startTime) }}-{{ formatTime(appointment.endTime) }}</div>
            </div>
          </div>
          <div class="flex items-start gap-3">
            <div class="w-8 h-8 rounded-full bg-amber-50 flex items-center justify-center shrink-0">
              <svg class="w-4 h-4 text-amber-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
            </div>
            <div>
              <div class="text-sm text-gray-500">服务时长</div>
              <div class="text-base text-gray-800 font-medium">{{ appointment.duration || '-' }} 小时</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 家长信息 -->
      <div class="bg-white rounded-xl shadow p-6">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">家长信息</h3>
        <div class="space-y-3">
          <div class="flex items-center gap-3">
            <div class="w-12 h-12 rounded-full bg-gray-100 flex items-center justify-center">
              <svg class="w-6 h-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
              </svg>
            </div>
            <div>
              <div class="text-base text-gray-800 font-medium">{{ appointment.parentName || '家长' }}</div>
              <div class="text-sm text-gray-500">{{ appointment.parentPhone || '暂无联系方式' }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 服务地址 -->
      <div class="bg-white rounded-xl shadow p-6">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">服务地址</h3>
        <div class="flex items-start gap-3">
          <div class="w-8 h-8 rounded-full bg-red-50 flex items-center justify-center shrink-0">
            <svg class="w-4 h-4 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/>
            </svg>
          </div>
          <div class="flex-1">
            <div class="text-base text-gray-800">{{ appointment.address || '待确认地址' }}</div>
          </div>
        </div>
      </div>

      <!-- 备注信息 -->
      <div v-if="appointment.remark" class="bg-white rounded-xl shadow p-6">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">备注信息</h3>
        <p class="text-gray-600 text-sm leading-relaxed">{{ appointment.remark }}</p>
      </div>

      <!-- 签到记录 -->
      <div v-if="checkinRecords.length > 0" class="bg-white rounded-xl shadow p-6">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">签到记录</h3>
        <div class="space-y-3">
          <div v-for="record in checkinRecords" :key="record.id" class="flex items-center justify-between py-2 border-b border-gray-50 last:border-0">
            <div class="flex items-center gap-3">
              <div :class="[
                'w-8 h-8 rounded-full flex items-center justify-center',
                record.checkinType === 1 ? 'bg-green-50' : 'bg-orange-50'
              ]">
                <svg v-if="record.checkinType === 1" class="w-4 h-4 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1"/>
                </svg>
                <svg v-else class="w-4 h-4 text-orange-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/>
                </svg>
              </div>
              <div>
                <div class="text-sm font-medium text-gray-800">{{ record.checkinType === 1 ? '签到' : '签退' }}</div>
                <div class="text-xs text-gray-500">{{ formatFullDateTime(record.checkinTime) }}</div>
              </div>
            </div>
            <div class="text-right">
              <div class="text-xs text-gray-500">距离 {{ record.distance }}米</div>
              <div :class="['text-xs', record.isInRange === 1 ? 'text-green-500' : 'text-red-500']">
                {{ record.isInRange === 1 ? '范围内' : '范围外' }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 时间信息 -->
      <div class="bg-white rounded-xl shadow p-6">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">时间记录</h3>
        <div class="space-y-2 text-sm">
          <div class="flex justify-between">
            <span class="text-gray-500">创建时间</span>
            <span class="text-gray-700">{{ formatFullDateTime(appointment.createTime) }}</span>
          </div>
          <div v-if="appointment.updateTime" class="flex justify-between">
            <span class="text-gray-500">更新时间</span>
            <span class="text-gray-700">{{ formatFullDateTime(appointment.updateTime) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部签到/签退按钮 -->
    <div v-if="appointment && canCheckin" class="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 p-4 safe-area-bottom">
      <div v-if="locationLoading" class="flex items-center justify-center gap-2 py-3">
        <t-loading size="small" />
        <span class="text-gray-500 text-sm">正在获取位置...</span>
      </div>
      <div v-else-if="locationError" class="text-center">
        <p class="text-red-500 text-sm mb-2">{{ locationError }}</p>
        <button @click="getLocation" class="text-blue-500 text-sm">重新获取位置</button>
      </div>
      <div v-else class="flex gap-3">
        <button
          v-if="!todayCheckin || todayCheckin.checkinType === 2"
          @click="handleCheckin"
          :disabled="checkinLoading"
          class="flex-1 py-3 bg-green-500 text-white rounded-xl font-medium hover:bg-green-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {{ checkinLoading ? '签到中...' : '签到' }}
        </button>
        <button
          v-if="todayCheckin && todayCheckin.checkinType === 1"
          @click="handleCheckout"
          :disabled="checkinLoading"
          class="flex-1 py-3 bg-orange-500 text-white rounded-xl font-medium hover:bg-orange-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {{ checkinLoading ? '签退中...' : '签退' }}
        </button>
      </div>
      <div v-if="currentLocation" class="mt-2 text-center text-xs text-gray-400">
        当前位置: {{ currentAddress || `${currentLocation.longitude.toFixed(6)}, ${currentLocation.latitude.toFixed(6)}` }}
      </div>
    </div>

    <!-- 错误状态 -->
    <div v-else class="flex flex-col items-center justify-center py-20">
      <svg class="w-16 h-16 text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
      </svg>
      <p class="text-gray-400 text-sm">预约信息加载失败</p>
      <button @click="loadAppointment" class="mt-4 px-4 py-2 text-blue-500 hover:text-blue-600 text-sm font-medium">
        重新加载
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import { getAppointmentById } from '@/api/appointment'
import { checkin, checkout, getTodayCheckin, getCheckinRecords } from '@/api/checkin'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const appointment = ref(null)

// 签到相关
const todayCheckin = ref(null)
const checkinRecords = ref([])
const checkinLoading = ref(false)
const locationLoading = ref(false)
const locationError = ref('')
const currentLocation = ref(null)
const currentAddress = ref('')

// 是否可以签到（已确认或进行中状态）
const canCheckin = computed(() => {
  return appointment.value && (appointment.value.status === 1 || appointment.value.status === 2)
})

// 签到状态文本
const checkinStatusText = computed(() => {
  if (!todayCheckin.value) return '未签到'
  return todayCheckin.value.checkinType === 1 ? '已签到' : '已签退'
})

// 签到状态样式
const checkinStatusClass = computed(() => {
  if (!todayCheckin.value) return 'text-xs text-gray-500'
  return todayCheckin.value.checkinType === 1 
    ? 'text-xs text-green-600 font-medium' 
    : 'text-xs text-orange-600 font-medium'
})

// 返回上一页
const goBack = () => {
  router.back()
}

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
    0: 'px-3 py-1 text-sm font-medium text-blue-700 bg-blue-50 rounded-full',
    1: 'px-3 py-1 text-sm font-medium text-emerald-700 bg-emerald-50 rounded-full',
    2: 'px-3 py-1 text-sm font-medium text-amber-700 bg-amber-50 rounded-full',
    3: 'px-3 py-1 text-sm font-medium text-gray-700 bg-gray-100 rounded-full',
    4: 'px-3 py-1 text-sm font-medium text-red-700 bg-red-50 rounded-full'
  }
  return classMap[status] || 'px-3 py-1 text-sm font-medium text-gray-700 bg-gray-100 rounded-full'
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

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  let date
  // 处理数组格式 [year, month, day] (Java LocalDate序列化格式)
  if (Array.isArray(dateStr)) {
    date = new Date(dateStr[0], dateStr[1] - 1, dateStr[2])
  } else if (typeof dateStr === 'string') {
    date = new Date(dateStr)
  } else {
    return '-'
  }
  if (isNaN(date.getTime())) return '-'
  const month = date.getMonth() + 1
  const day = date.getDate()
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const weekDay = weekDays[date.getDay()]
  return `${date.getFullYear()}年${month}月${day}日 ${weekDay}`
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

// 格式化完整日期时间
const formatFullDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return '-'
  let date
  // 处理数组格式 [year, month, day, hour, minute, second] (Java LocalDateTime序列化格式)
  if (Array.isArray(dateTimeStr)) {
    date = new Date(dateTimeStr[0], dateTimeStr[1] - 1, dateTimeStr[2], dateTimeStr[3] || 0, dateTimeStr[4] || 0, dateTimeStr[5] || 0)
  } else if (typeof dateTimeStr === 'string') {
    date = new Date(dateTimeStr)
  } else {
    return '-'
  }
  if (isNaN(date.getTime())) return '-'
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 高德地图配置
const AMAP_KEY = '2485169badfddc8c22704294c0954f88'
const AMAP_SECRET = 'a773abde427cfa6b967324f2e8373e5a'
let amapLoaded = false

// 加载高德地图JS API
const loadAMapScript = () => {
  return new Promise((resolve, reject) => {
    if (typeof AMap !== 'undefined') {
      resolve()
      return
    }
    if (amapLoaded) {
      // 等待加载完成
      const checkLoaded = setInterval(() => {
        if (typeof AMap !== 'undefined') {
          clearInterval(checkLoaded)
          resolve()
        }
      }, 100)
      return
    }
    amapLoaded = true
    window._AMapSecurityConfig = { securityJsCode: AMAP_SECRET }
    const script = document.createElement('script')
    script.src = `https://webapi.amap.com/maps?v=2.0&key=${AMAP_KEY}&plugin=AMap.Geolocation`
    script.onload = resolve
    script.onerror = reject
    document.head.appendChild(script)
  })
}

// 获取当前位置（使用高德地图API，返回GCJ-02坐标）
const getLocation = async () => {
  locationLoading.value = true
  locationError.value = ''
  
  try {
    await loadAMapScript()
    
    const geolocation = new AMap.Geolocation({
      enableHighAccuracy: true,
      timeout: 10000,
      zoomToAccuracy: true
    })
    
    geolocation.getCurrentPosition((status, result) => {
      if (status === 'complete') {
        // 高德地图返回的已经是GCJ-02坐标
        currentLocation.value = {
          longitude: result.position.lng,
          latitude: result.position.lat
        }
        currentAddress.value = result.formattedAddress || ''
        locationLoading.value = false
      } else {
        console.error('定位失败:', result)
        locationError.value = result.message || '定位失败，请检查定位权限'
        locationLoading.value = false
      }
    })
  } catch (e) {
    console.error('加载地图失败:', e)
    locationError.value = '加载地图失败'
    locationLoading.value = false
  }
}

// 签到
const handleCheckin = async () => {
  if (!currentLocation.value) {
    MessagePlugin.warning('请先获取位置信息')
    getLocation()
    return
  }

  checkinLoading.value = true
  try {
    await checkin(appointment.value.id, {
      longitude: currentLocation.value.longitude,
      latitude: currentLocation.value.latitude,
      address: currentAddress.value
    })
    MessagePlugin.success('签到成功')
    // 刷新状态
    await loadTodayCheckin()
    await loadCheckinRecords()
    await loadAppointment()
  } catch (error) {
    MessagePlugin.error(error.response?.data?.msg || '签到失败')
  } finally {
    checkinLoading.value = false
  }
}

// 签退
const handleCheckout = async () => {
  if (!currentLocation.value) {
    MessagePlugin.warning('请先获取位置信息')
    getLocation()
    return
  }

  checkinLoading.value = true
  try {
    const res = await checkout(appointment.value.id, {
      longitude: currentLocation.value.longitude,
      latitude: currentLocation.value.latitude,
      address: currentAddress.value
    })
    // 显示后端返回的提示消息（包含护理计划剩余天数等信息）
    MessagePlugin.success(res.msg || '签退成功')
    // 刷新状态
    await loadTodayCheckin()
    await loadCheckinRecords()
  } catch (error) {
    MessagePlugin.error(error.response?.data?.msg || '签退失败')
  } finally {
    checkinLoading.value = false
  }
}

// 加载今日签到状态
const loadTodayCheckin = async () => {
  const id = route.params.id
  if (!id) return
  try {
    const res = await getTodayCheckin(id)
    todayCheckin.value = res.data
  } catch (error) {
    console.error('获取今日签到状态失败:', error)
  }
}

// 加载签到记录
const loadCheckinRecords = async () => {
  const id = route.params.id
  if (!id) return
  try {
    const res = await getCheckinRecords(id)
    checkinRecords.value = res.data || []
  } catch (error) {
    console.error('获取签到记录失败:', error)
  }
}

// 加载预约详情
const loadAppointment = async () => {
  const id = route.params.id
  if (!id) {
    return
  }

  loading.value = true
  try {
    const res = await getAppointmentById(id)
    appointment.value = res.data
  } catch (error) {
    console.error('获取预约详情失败:', error)
    appointment.value = null
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadAppointment()
  if (canCheckin.value) {
    getLocation()
    await loadTodayCheckin()
    await loadCheckinRecords()
  }
})
</script>
