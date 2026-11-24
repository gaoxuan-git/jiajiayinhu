<template>
  <div class="min-h-screen bg-gray-50 py-6">
    <div class="max-w-4xl mx-auto px-4">
      <!-- 返回按钮 -->
      <button
        @click="$router.back()"
        class="flex items-center gap-2 text-gray-600 hover:text-gray-800 mb-6"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
        </svg>
        返回订单列表
      </button>

      <!-- 加载状态 -->
      <div v-if="loading" class="flex justify-center py-20">
        <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-500"></div>
      </div>

      <template v-else-if="order">
        <!-- 订单状态卡片 -->
        <div
          class="rounded-xl shadow p-6 mb-6"
          :class="getStatusBgClass(order.status)"
        >
          <div class="flex items-center gap-4">
            <div class="w-14 h-14 rounded-full flex items-center justify-center" :class="getStatusIconBgClass(order.status)">
              <svg class="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" :d="getStatusIcon(order.status)" />
              </svg>
            </div>
            <div>
              <h2 class="text-xl font-semibold" :class="getStatusTextClass(order.status)">{{ order.statusName }}</h2>
              <p class="text-sm text-gray-600 mt-1">{{ getStatusDesc(order.status) }}</p>
            </div>
          </div>
          <!-- 操作按钮 -->
          <div class="mt-4 flex gap-3" v-if="order.status === 0 || order.status === 2">
            <template v-if="order.status === 0">
              <button
                @click="handleCancel"
                class="px-4 py-2 text-gray-600 bg-white border border-gray-300 rounded-lg hover:bg-gray-50"
              >
                取消订单
              </button>
              <button
                @click="handlePay"
                class="px-4 py-2 text-white bg-blue-500 rounded-lg hover:bg-blue-600"
              >
                立即支付
              </button>
            </template>
            <template v-if="order.status === 2">
              <button
                @click="handleConfirmReceipt"
                class="px-4 py-2 text-white bg-blue-500 rounded-lg hover:bg-blue-600"
              >
                确认收货
              </button>
            </template>
          </div>
        </div>

        <!-- 物流信息 -->
        <div v-if="order.status >= 2 && order.deliveryNo" class="bg-white rounded-xl shadow p-6 mb-6">
          <h3 class="text-lg font-semibold text-gray-800 mb-4">物流信息</h3>
          <div class="flex items-start gap-4">
            <div class="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center flex-shrink-0">
              <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 8h14M5 8a2 2 0 110-4h14a2 2 0 110 4M5 8v10a2 2 0 002 2h10a2 2 0 002-2V8m-9 4h4" />
              </svg>
            </div>
            <div>
              <p class="text-sm text-gray-800">{{ order.deliveryCompany }}</p>
              <p class="text-sm text-gray-500 mt-1">运单号：{{ order.deliveryNo }}</p>
              <p class="text-xs text-gray-400 mt-1">发货时间：{{ formatTime(order.deliveryTime) }}</p>
            </div>
          </div>
        </div>

        <!-- 收货信息 -->
        <div class="bg-white rounded-xl shadow p-6 mb-6">
          <h3 class="text-lg font-semibold text-gray-800 mb-4">收货信息</h3>
          <div class="space-y-3">
            <div class="flex items-center gap-3">
              <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
              <span class="text-sm text-gray-700">{{ order.receiverName }}</span>
              <span class="text-sm text-gray-500">{{ order.receiverPhone }}</span>
            </div>
            <div class="flex items-start gap-3">
              <svg class="w-5 h-5 text-gray-400 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
              <span class="text-sm text-gray-700">{{ order.receiverAddress }}</span>
            </div>
          </div>
        </div>

        <!-- 商品信息 -->
        <div class="bg-white rounded-xl shadow p-6 mb-6">
          <h3 class="text-lg font-semibold text-gray-800 mb-4">商品信息</h3>
          <div class="space-y-4">
            <div
              v-for="item in order.items"
              :key="item.id"
              class="flex items-center gap-4 p-3 bg-gray-50 rounded-lg"
            >
              <img
                :src="item.productImg || '/placeholder.png'"
                :alt="item.productName"
                class="w-20 h-20 object-cover rounded-lg bg-gray-200"
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
        </div>

        <!-- 订单信息 -->
        <div class="bg-white rounded-xl shadow p-6 mb-6">
          <h3 class="text-lg font-semibold text-gray-800 mb-4">订单信息</h3>
          <div class="grid grid-cols-2 gap-4 text-sm">
            <div>
              <span class="text-gray-500">订单编号：</span>
              <span class="text-gray-700">{{ order.orderNo }}</span>
            </div>
            <div>
              <span class="text-gray-500">下单时间：</span>
              <span class="text-gray-700">{{ formatTime(order.createTime) }}</span>
            </div>
            <div v-if="order.payTime">
              <span class="text-gray-500">支付时间：</span>
              <span class="text-gray-700">{{ formatTime(order.payTime) }}</span>
            </div>
            <div v-if="order.payTypeName">
              <span class="text-gray-500">支付方式：</span>
              <span class="text-gray-700">{{ order.payTypeName }}</span>
            </div>
            <div v-if="order.receiveTime">
              <span class="text-gray-500">收货时间：</span>
              <span class="text-gray-700">{{ formatTime(order.receiveTime) }}</span>
            </div>
            <div v-if="order.remark" class="col-span-2">
              <span class="text-gray-500">订单备注：</span>
              <span class="text-gray-700">{{ order.remark }}</span>
            </div>
          </div>
        </div>

        <!-- 费用明细 -->
        <div class="bg-white rounded-xl shadow p-6">
          <h3 class="text-lg font-semibold text-gray-800 mb-4">费用明细</h3>
          <div class="space-y-3">
            <div class="flex justify-between text-sm">
              <span class="text-gray-500">商品总额</span>
              <span class="text-gray-700">¥{{ order.totalAmount }}</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-500">运费</span>
              <span class="text-gray-700">¥{{ order.freightAmount || '0.00' }}</span>
            </div>
            <div v-if="order.discountAmount > 0" class="flex justify-between text-sm">
              <span class="text-gray-500">优惠金额</span>
              <span class="text-emerald-600">-¥{{ order.discountAmount }}</span>
            </div>
            <div class="border-t border-gray-200 pt-3 flex justify-between">
              <span class="text-gray-700 font-medium">实付金额</span>
              <span class="text-xl font-bold text-red-500">¥{{ order.payAmount }}</span>
            </div>
          </div>
        </div>
      </template>

      <!-- 订单不存在 -->
      <div v-else class="text-center py-20">
        <svg class="w-16 h-16 mx-auto text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
        </svg>
        <p class="text-gray-500">订单不存在</p>
        <button
          @click="$router.push('/orders')"
          class="mt-4 px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600"
        >
          返回订单列表
        </button>
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
            <p class="text-2xl font-bold text-red-500">¥{{ order?.payAmount }}</p>
          </div>
          <div class="space-y-3">
            <label
              v-for="method in payMethods"
              :key="method.value"
              class="flex items-center gap-3 p-3 border rounded-lg cursor-pointer transition-colors"
              :class="selectedPayType === method.value ? 'border-blue-500 bg-blue-50' : 'border-gray-200 hover:bg-gray-50'"
            >
              <input type="radio" :value="method.value" v-model="selectedPayType" class="w-4 h-4 text-blue-500" />
              <span class="text-sm font-medium text-gray-700">{{ method.label }}</span>
            </label>
          </div>
        </div>
        <div class="flex justify-end gap-3 px-6 py-4 border-t border-gray-200">
          <button @click="showPayModal = false" class="px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50">取消</button>
          <button @click="confirmPay" :disabled="paying" class="px-4 py-2 text-white bg-blue-500 rounded-lg hover:bg-blue-600 disabled:opacity-50">
            {{ paying ? '支付中...' : '确认支付' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getOrderDetail, payOrder, cancelOrder, confirmReceipt } from '@/api/order'

const route = useRoute()

const loading = ref(true)
const order = ref(null)
const showPayModal = ref(false)
const selectedPayType = ref(1)
const paying = ref(false)

const payMethods = [
  { value: 1, label: '微信支付' },
  { value: 2, label: '支付宝' },
  { value: 3, label: '余额支付' }
]

// 获取订单详情
const fetchOrder = async () => {
  loading.value = true
  try {
    const res = await getOrderDetail(route.params.id)
    if (res.code === 200) {
      order.value = res.data
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
  } finally {
    loading.value = false
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

// 状态相关样式
const getStatusBgClass = (status) => {
  const map = {
    0: 'bg-amber-50',
    1: 'bg-blue-50',
    2: 'bg-purple-50',
    3: 'bg-emerald-50',
    4: 'bg-gray-100',
    5: 'bg-red-50'
  }
  return map[status] || 'bg-gray-100'
}

const getStatusIconBgClass = (status) => {
  const map = {
    0: 'bg-amber-500',
    1: 'bg-blue-500',
    2: 'bg-purple-500',
    3: 'bg-emerald-500',
    4: 'bg-gray-500',
    5: 'bg-red-500'
  }
  return map[status] || 'bg-gray-500'
}

const getStatusTextClass = (status) => {
  const map = {
    0: 'text-amber-700',
    1: 'text-blue-700',
    2: 'text-purple-700',
    3: 'text-emerald-700',
    4: 'text-gray-700',
    5: 'text-red-700'
  }
  return map[status] || 'text-gray-700'
}

const getStatusIcon = (status) => {
  const map = {
    0: 'M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z',
    1: 'M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4',
    2: 'M5 8h14M5 8a2 2 0 110-4h14a2 2 0 110 4M5 8v10a2 2 0 002 2h10a2 2 0 002-2V8m-9 4h4',
    3: 'M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z',
    4: 'M6 18L18 6M6 6l12 12',
    5: 'M3 10h10a8 8 0 018 8v2M3 10l6 6m-6-6l6-6'
  }
  return map[status] || 'M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z'
}

const getStatusDesc = (status) => {
  const map = {
    0: '请尽快完成支付，超时订单将自动取消',
    1: '商家正在准备商品，请耐心等待',
    2: '商品已发出，请注意查收',
    3: '交易已完成，感谢您的购买',
    4: '订单已取消',
    5: '退款已处理'
  }
  return map[status] || ''
}

// 支付
const handlePay = () => {
  selectedPayType.value = 1
  showPayModal.value = true
}

const confirmPay = async () => {
  paying.value = true
  try {
    const res = await payOrder(order.value.id, selectedPayType.value)
    if (res.code === 200) {
      showPayModal.value = false
      alert('支付成功')
      fetchOrder()
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
const handleCancel = async () => {
  if (!confirm('确定要取消该订单吗？')) return
  try {
    const res = await cancelOrder(order.value.id)
    if (res.code === 200) {
      alert('订单已取消')
      fetchOrder()
    } else {
      alert(res.msg || '取消失败')
    }
  } catch (error) {
    alert('取消失败')
  }
}

// 确认收货
const handleConfirmReceipt = async () => {
  if (!confirm('确定已收到商品吗？')) return
  try {
    const res = await confirmReceipt(order.value.id)
    if (res.code === 200) {
      alert('已确认收货')
      fetchOrder()
    } else {
      alert(res.msg || '操作失败')
    }
  } catch (error) {
    alert('操作失败')
  }
}

onMounted(() => {
  fetchOrder()
})
</script>
