<template>
  <div class="min-h-screen bg-gray-50 py-6">
    <div class="max-w-4xl mx-auto px-4">
      <!-- 页面标题 -->
      <div class="mb-6">
        <h1 class="text-2xl font-semibold text-gray-800">确认订单</h1>
        <p class="text-sm text-gray-500 mt-1">请确认订单信息并填写收货地址</p>
      </div>

      <!-- 收货地址 -->
      <div class="bg-white rounded-xl shadow p-6 mb-6">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">收货信息</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">收货人姓名 <span class="text-red-500">*</span></label>
            <input
              v-model="orderForm.receiverName"
              type="text"
              placeholder="请输入收货人姓名"
              class="w-full px-3 py-2 text-gray-700 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">联系电话 <span class="text-red-500">*</span></label>
            <input
              v-model="orderForm.receiverPhone"
              type="tel"
              placeholder="请输入联系电话"
              class="w-full px-3 py-2 text-gray-700 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">收货地址 <span class="text-red-500">*</span></label>
            <textarea
              v-model="orderForm.receiverAddress"
              rows="2"
              placeholder="请输入详细收货地址"
              class="w-full px-3 py-2 text-gray-700 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            ></textarea>
          </div>
          <!-- 快速选择地址 -->
          <div v-if="addressList.length > 0">
            <p class="text-sm text-gray-500 mb-2">快速选择已保存的地址：</p>
            <div class="flex flex-wrap gap-2">
              <button
                v-for="addr in addressList"
                :key="addr.id"
                @click="selectAddress(addr)"
                class="px-3 py-1.5 text-sm border rounded-lg transition-colors"
                :class="selectedAddressId === addr.id ? 'border-blue-500 bg-blue-50 text-blue-600' : 'border-gray-300 hover:bg-gray-50'"
              >
                {{ addr.receiverName }} - {{ getAddressPreview(addr) }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 商品清单 -->
      <div class="bg-white rounded-xl shadow p-6 mb-6">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">商品清单</h3>
        <div v-if="loading" class="flex justify-center py-8">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
        </div>
        <div v-else-if="cartItems.length === 0" class="text-center py-8">
          <p class="text-gray-500">没有可结算的商品</p>
          <button @click="$router.push('/cart')" class="mt-4 px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600">
            返回购物车
          </button>
        </div>
        <div v-else class="space-y-4">
          <div
            v-for="item in cartItems"
            :key="item.id"
            class="flex items-center gap-4 p-3 bg-gray-50 rounded-lg"
          >
            <img
              :src="item.productCoverImg || '/placeholder.png'"
              :alt="item.productName"
              class="w-20 h-20 object-cover rounded-lg bg-gray-200"
            />
            <div class="flex-1 min-w-0">
              <h4 class="text-sm font-medium text-gray-800 truncate">{{ item.productName }}</h4>
              <p class="text-xs text-gray-500 mt-1">单价：¥{{ item.productPrice }}</p>
            </div>
            <div class="text-right">
              <div class="text-sm text-gray-500">x{{ item.quantity }}</div>
              <div class="text-sm font-medium text-gray-800">¥{{ item.subtotal }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 订单备注 -->
      <div class="bg-white rounded-xl shadow p-6 mb-6">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">订单备注</h3>
        <textarea
          v-model="orderForm.remark"
          rows="2"
          placeholder="选填，可以告诉商家您的特殊需求"
          class="w-full px-3 py-2 text-gray-700 bg-white border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
        ></textarea>
      </div>

      <!-- 费用汇总 -->
      <div class="bg-white rounded-xl shadow p-6 mb-6">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">费用明细</h3>
        <div class="space-y-3">
          <div class="flex justify-between text-sm">
            <span class="text-gray-500">商品总额</span>
            <span class="text-gray-700">¥{{ totalAmount }}</span>
          </div>
          <div class="flex justify-between text-sm">
            <span class="text-gray-500">运费</span>
            <span class="text-gray-700">¥0.00</span>
          </div>
          <div class="border-t border-gray-200 pt-3 flex justify-between">
            <span class="text-gray-700 font-medium">应付金额</span>
            <span class="text-xl font-bold text-red-500">¥{{ totalAmount }}</span>
          </div>
        </div>
      </div>

      <!-- 提交按钮 -->
      <div class="flex justify-between items-center bg-white rounded-xl shadow p-4">
        <div>
          <span class="text-gray-500">共 {{ totalQuantity }} 件商品，合计：</span>
          <span class="text-xl font-bold text-red-500 ml-2">¥{{ totalAmount }}</span>
        </div>
        <div class="flex gap-3">
          <button
            @click="$router.back()"
            class="px-6 py-2 text-gray-600 border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            返回
          </button>
          <button
            @click="submitOrder"
            :disabled="submitting || cartItems.length === 0"
            class="px-6 py-2 text-white bg-blue-500 rounded-lg hover:bg-blue-600 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {{ submitting ? '提交中...' : '提交订单' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getCartList } from '@/api/cart'
import { createOrder } from '@/api/order'
import { getAddressList } from '@/api/address'

const router = useRouter()
const route = useRoute()

const loading = ref(true)
const submitting = ref(false)
const cartItems = ref([])
const addressList = ref([])
const selectedAddressId = ref(null)

const orderForm = ref({
  receiverName: '',
  receiverPhone: '',
  receiverAddress: '',
  remark: ''
})

// 计算总金额
const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + parseFloat(item.subtotal || 0), 0).toFixed(2)
})

// 计算总数量
const totalQuantity = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
})

// 获取地址预览
const getAddressPreview = (addr) => {
  const fullAddress = (addr.province || '') + (addr.city || '') + (addr.district || '') + (addr.address || '')
  if (fullAddress.length > 15) {
    return fullAddress.substring(0, 15) + '...'
  }
  return fullAddress || '未填写地址'
}

// 获取购物车数据
const fetchCartItems = async () => {
  loading.value = true
  try {
    const res = await getCartList()
    if (res.code === 200) {
      // 从路由参数获取选中的购物车ID
      const cartIds = route.query.cartIds
      if (cartIds) {
        const selectedIds = cartIds.split(',').map(id => parseInt(id))
        cartItems.value = (res.data || []).filter(item =>
          selectedIds.includes(item.id) && item.valid && item.selected === 1
        )
      } else {
        // 如果没有指定，则获取所有选中的商品
        cartItems.value = (res.data || []).filter(item => item.valid && item.selected === 1)
      }
    }
  } catch (error) {
    console.error('获取购物车失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取地址列表
const fetchAddressList = async () => {
  try {
    const res = await getAddressList()
    if (res.code === 200) {
      addressList.value = res.data || []
      // 自动选择默认地址
      const defaultAddr = addressList.value.find(a => a.isDefault === 1)
      if (defaultAddr) {
        selectAddress(defaultAddr)
      }
    }
  } catch (error) {
    console.error('获取地址列表失败:', error)
  }
}

// 选择地址
const selectAddress = (addr) => {
  selectedAddressId.value = addr.id
  orderForm.value.receiverName = addr.receiverName || ''
  orderForm.value.receiverPhone = addr.receiverPhone || ''
  orderForm.value.receiverAddress = (addr.province || '') + (addr.city || '') + (addr.district || '') + (addr.address || '')
}

// 提交订单
const submitOrder = async () => {
  // 验证表单
  if (!orderForm.value.receiverName) {
    alert('请输入收货人姓名')
    return
  }
  if (!orderForm.value.receiverPhone) {
    alert('请输入联系电话')
    return
  }
  if (!orderForm.value.receiverAddress) {
    alert('请输入收货地址')
    return
  }
  if (cartItems.value.length === 0) {
    alert('没有可结算的商品')
    return
  }

  submitting.value = true
  try {
    const data = {
      cartIds: cartItems.value.map(item => item.id),
      receiverName: orderForm.value.receiverName,
      receiverPhone: orderForm.value.receiverPhone,
      receiverAddress: orderForm.value.receiverAddress,
      remark: orderForm.value.remark
    }
    const res = await createOrder(data)
    if (res.code === 200) {
      alert('订单创建成功')
      // 跳转到订单详情页
      router.push('/order/' + res.data)
    } else {
      alert(res.msg || '创建订单失败')
    }
  } catch (error) {
    alert('创建订单失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchCartItems()
  fetchAddressList()
})
</script>
