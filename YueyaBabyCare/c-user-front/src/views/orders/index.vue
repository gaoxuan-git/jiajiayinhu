<template>
  <div class="min-h-screen bg-gray-50 py-6">
    <div class="max-w-6xl mx-auto px-4">
      <!-- 页面标题 -->
      <div class="mb-6">
        <h1 class="text-2xl font-semibold text-gray-800">我的订单</h1>
        <p class="text-sm text-gray-500 mt-1">查看和管理您的所有订单</p>
      </div>

      <!-- 订单统计卡片 -->
      <div class="grid grid-cols-4 gap-4 mb-6">
        <div
          v-for="stat in orderStats"
          :key="stat.key"
          class="bg-white rounded-xl shadow p-4 cursor-pointer hover:shadow-md transition-shadow"
          :class="{ 'ring-2 ring-blue-500': currentStatus === stat.status }"
          @click="filterByStatus(stat.status)"
        >
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-full flex items-center justify-center" :class="stat.bgColor">
              <svg class="w-5 h-5" :class="stat.iconColor" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" :d="stat.icon" />
              </svg>
            </div>
            <div>
              <div class="text-2xl font-bold text-gray-800">{{ stat.count }}</div>
              <div class="text-xs text-gray-500">{{ stat.label }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 状态筛选标签 -->
      <div class="bg-white rounded-xl shadow mb-6">
        <div class="flex border-b border-gray-200">
          <button
            v-for="tab in statusTabs"
            :key="tab.value"
            class="px-6 py-3 text-sm font-medium transition-colors relative"
            :class="currentStatus === tab.value
              ? 'text-blue-600'
              : 'text-gray-500 hover:text-gray-700'"
            @click="filterByStatus(tab.value)"
          >
            {{ tab.label }}
            <span
              v-if="currentStatus === tab.value"
              class="absolute bottom-0 left-0 right-0 h-0.5 bg-blue-500"
            ></span>
          </button>
        </div>

        <!-- 订单列表 -->
        <div class="p-4">
          <!-- 加载状态 -->
          <div v-if="loading" class="flex justify-center py-12">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
          </div>

          <!-- 空状态 -->
          <div v-else-if="orderList.length === 0" class="text-center py-12">
            <svg class="w-16 h-16 mx-auto text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
            <p class="text-gray-500">暂无订单</p>
            <button
              @click="$router.push('/products')"
              class="mt-4 px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
            >
              去逛逛
            </button>
          </div>

          <!-- 订单卡片列表 -->
          <div v-else class="space-y-4">
            <div
              v-for="order in orderList"
              :key="order.id"
              class="border border-gray-200 rounded-xl overflow-hidden hover:shadow-md transition-shadow"
            >
              <!-- 订单头部 -->
              <div class="bg-gray-50 px-4 py-3 flex items-center justify-between">
                <div class="flex items-center gap-4">
                  <span class="text-sm text-gray-500">订单号：{{ order.orderNo }}</span>
                  <span class="text-sm text-gray-400">{{ formatTime(order.createTime) }}</span>
                </div>
                <span
                  class="px-2 py-1 text-xs font-medium rounded-full"
                  :class="getStatusClass(order.status)"
                >
                  {{ order.statusName }}
                </span>
              </div>

              <!-- 订单商品 -->
              <div class="p-4">
                <div
                  v-for="item in order.items"
                  :key="item.id"
                  class="flex items-center gap-4 py-3 border-b border-gray-100 last:border-0"
                >
                  <img
                    :src="item.productImg || '/placeholder.png'"
                    :alt="item.productName"
                    class="w-20 h-20 object-cover rounded-lg bg-gray-100"
                  />
                  <div class="flex-1 min-w-0">
                    <h4 class="text-sm font-medium text-gray-800 truncate">{{ item.productName }}</h4>
                    <p class="text-xs text-gray-500 mt-1">单价：¥{{ item.price }}</p>
                  </div>
                  <div class="text-right">
                    <div class="text-sm text-gray-500">x{{ item.quantity }}</div>
                    <div class="text-sm font-medium text-gray-800">¥{{ item.totalAmount }}</div>
                  </div>
                </div>
              </div>

              <!-- 订单底部 -->
              <div class="bg-gray-50 px-4 py-3 flex items-center justify-between">
                <div class="text-sm text-gray-600">
                  共 <span class="font-medium">{{ order.totalQuantity }}</span> 件商品，
                  实付：<span class="text-lg font-semibold text-red-500">¥{{ order.payAmount }}</span>
                </div>
                <div class="flex items-center gap-2">
                  <!-- 查看详情 -->
                  <button
                    @click="$router.push('/order/' + order.id)"
                    class="px-4 py-1.5 text-sm text-gray-600 border border-gray-300 rounded-lg hover:bg-gray-100 transition-colors"
                  >
                    查看详情
                  </button>
                  <!-- 待付款：支付、取消 -->
                  <template v-if="order.status === 0">
                    <button
                      @click="handleCancel(order)"
                      class="px-4 py-1.5 text-sm text-gray-600 border border-gray-300 rounded-lg hover:bg-gray-100 transition-colors"
                    >
                      取消订单
                    </button>
                    <button
                      @click="handlePay(order)"
                      class="px-4 py-1.5 text-sm text-white bg-blue-500 rounded-lg hover:bg-blue-600 transition-colors"
                    >
                      立即支付
                    </button>
                  </template>
                  <!-- 待收货：确认收货 -->
                  <template v-if="order.status === 2">
                    <button
                      @click="handleConfirmReceipt(order)"
                      class="px-4 py-1.5 text-sm text-white bg-blue-500 rounded-lg hover:bg-blue-600 transition-colors"
                    >
                      确认收货
                    </button>
                  </template>
                </div>
              </div>
            </div>
          </div>

          <!-- 分页 -->
          <div v-if="total > pageSize" class="flex justify-center mt-6">
            <div class="flex items-center gap-2">
              <button
                :disabled="pageNum <= 1"
                @click="changePage(pageNum - 1)"
                class="px-3 py-1.5 text-sm border border-gray-300 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
              >
                上一页
              </button>
              <span class="text-sm text-gray-600">{{ pageNum }} / {{ totalPages }}</span>
              <button
                :disabled="pageNum >= totalPages"
                @click="changePage(pageNum + 1)"
                class="px-3 py-1.5 text-sm border border-gray-300 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
              >
                下一页
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 支付弹窗 -->
    <div v-if="showPayModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-white rounded-2xl shadow-xl w-full max-w-md mx-4">
        <div class="flex items-center justify-between px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-800">选择支付方式</h3>
          <button @click="showPayModal = false" class="text-gray-400 hover:text-gray-600">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
          </button>
        </div>
        <div class="px-6 py-4">
          <div class="mb-4">
            <p class="text-sm text-gray-500">订单金额</p>
            <p class="text-2xl font-bold text-red-500">¥{{ currentOrder?.payAmount }}</p>
          </div>
          <div class="space-y-3">
            <label
              v-for="method in payMethods"
              :key="method.value"
              class="flex items-center gap-3 p-3 border rounded-lg cursor-pointer transition-colors"
              :class="selectedPayType === method.value ? 'border-blue-500 bg-blue-50' : 'border-gray-200 hover:bg-gray-50'"
            >
              <input
                type="radio"
                :value="method.value"
                v-model="selectedPayType"
                class="w-4 h-4 text-blue-500"
              />
              <span class="text-sm font-medium text-gray-700">{{ method.label }}</span>
            </label>
          </div>
        </div>
        <div class="flex justify-end gap-3 px-6 py-4 border-t border-gray-200">
          <button
            @click="showPayModal = false"
            class="px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            取消
          </button>
          <button
            @click="confirmPay"
            :disabled="paying"
            class="px-4 py-2 text-white bg-blue-500 rounded-lg hover:bg-blue-600 disabled:opacity-50"
          >
            {{ paying ? '支付中...' : '确认支付' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getOrderList, getOrderStats, payOrder, cancelOrder, confirmReceipt } from '@/api/order'

// 状态
const loading = ref(false)
const orderList = ref([])
const currentStatus = ref(null)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 统计数据
const stats = ref({
  pendingPay: 0,
  pendingDelivery: 0,
  pendingReceive: 0,
  completed: 0
})

// 支付弹窗
const showPayModal = ref(false)
const currentOrder = ref(null)
const selectedPayType = ref(1)
const paying = ref(false)

// 支付方式
const payMethods = [
  { value: 1, label: '微信支付' },
  { value: 2, label: '支付宝' },
  { value: 3, label: '余额支付' }
]

// 状态标签
const statusTabs = [
  { value: null, label: '全部' },
  { value: 0, label: '待付款' },
  { value: 1, label: '待发货' },
  { value: 2, label: '待收货' },
  { value: 3, label: '已完成' },
  { value: 4, label: '已取消' }
]

// 统计卡片配置
const orderStats = computed(() => [
  {
    key: 'pendingPay',
    status: 0,
    label: '待付款',
    count: stats.value.pendingPay,
    icon: 'M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z',
    bgColor: 'bg-amber-100',
    iconColor: 'text-amber-600'
  },
  {
    key: 'pendingDelivery',
    status: 1,
    label: '待发货',
    count: stats.value.pendingDelivery,
    icon: 'M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4',
    bgColor: 'bg-blue-100',
    iconColor: 'text-blue-600'
  },
  {
    key: 'pendingReceive',
    status: 2,
    label: '待收货',
    count: stats.value.pendingReceive,
    icon: 'M5 8h14M5 8a2 2 0 110-4h14a2 2 0 110 4M5 8v10a2 2 0 002 2h10a2 2 0 002-2V8m-9 4h4',
    bgColor: 'bg-purple-100',
    iconColor: 'text-purple-600'
  },
  {
    key: 'completed',
    status: 3,
    label: '已完成',
    count: stats.value.completed,
    icon: 'M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z',
    bgColor: 'bg-emerald-100',
    iconColor: 'text-emerald-600'
  }
])

// 计算总页数
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

// 获取订单列表
const fetchOrders = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (currentStatus.value !== null) {
      params.status = currentStatus.value
    }
    const res = await getOrderList(params)
    if (res.code === 200) {
      orderList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取订单统计
const fetchStats = async () => {
  try {
    const res = await getOrderStats()
    if (res.code === 200) {
      stats.value = res.data
    }
  } catch (error) {
    console.error('获取订单统计失败:', error)
  }
}

// 按状态筛选
const filterByStatus = (status) => {
  currentStatus.value = status
  pageNum.value = 1
  fetchOrders()
}

// 切换页码
const changePage = (page) => {
  pageNum.value = page
  fetchOrders()
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 16)
}

// 获取状态样式
const getStatusClass = (status) => {
  const classMap = {
    0: 'bg-amber-50 text-amber-700',
    1: 'bg-blue-50 text-blue-700',
    2: 'bg-purple-50 text-purple-700',
    3: 'bg-emerald-50 text-emerald-700',
    4: 'bg-gray-100 text-gray-700',
    5: 'bg-red-50 text-red-700'
  }
  return classMap[status] || 'bg-gray-100 text-gray-700'
}

// 支付订单
const handlePay = (order) => {
  currentOrder.value = order
  selectedPayType.value = 1
  showPayModal.value = true
}

// 确认支付
const confirmPay = async () => {
  if (!currentOrder.value) return
  paying.value = true
  try {
    const res = await payOrder(currentOrder.value.id, selectedPayType.value)
    if (res.code === 200) {
      showPayModal.value = false
      alert('支付成功')
      fetchOrders()
      fetchStats()
    } else {
      alert(res.msg || '支付失败')
    }
  } catch (error) {
    alert('支付失败')
  } finally {
    paying.value = false
  }
}

// 取消订单
const handleCancel = async (order) => {
  if (!confirm('确定要取消该订单吗？')) return
  try {
    const res = await cancelOrder(order.id)
    if (res.code === 200) {
      alert('订单已取消')
      fetchOrders()
      fetchStats()
    } else {
      alert(res.msg || '取消失败')
    }
  } catch (error) {
    alert('取消失败')
  }
}

// 确认收货
const handleConfirmReceipt = async (order) => {
  if (!confirm('确定已收到商品吗？')) return
  try {
    const res = await confirmReceipt(order.id)
    if (res.code === 200) {
      alert('已确认收货')
      fetchOrders()
      fetchStats()
    } else {
      alert(res.msg || '操作失败')
    }
  } catch (error) {
    alert('操作失败')
  }
}

onMounted(() => {
  fetchOrders()
  fetchStats()
})
</script>
