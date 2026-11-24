<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部导航栏 -->
    <div class="bg-white shadow-sm sticky top-0 z-10">
      <div class="max-w-7xl mx-auto px-6 py-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-4">
            <button @click="$router.back()" class="p-2 hover:bg-gray-100 rounded-lg transition-colors">
              <svg class="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
              </svg>
            </button>
            <h1 class="text-xl font-semibold text-gray-800">购物车</h1>
            <span v-if="cartList.length > 0" class="text-sm text-gray-500">({{ cartList.length }}件商品)</span>
          </div>
          <div class="flex items-center gap-4">
            <button
              v-if="cartList.length > 0 && !isEditMode"
              @click="isEditMode = true"
              class="text-blue-500 hover:text-blue-600 text-sm font-medium transition-colors"
            >
              管理
            </button>
            <button
              v-if="isEditMode"
              @click="isEditMode = false"
              class="text-blue-500 hover:text-blue-600 text-sm font-medium transition-colors"
            >
              完成
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="max-w-7xl mx-auto px-6 py-6">
      <!-- 加载状态 -->
      <div v-if="loading" class="flex justify-center py-20">
        <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-500"></div>
      </div>

      <!-- 空购物车 -->
      <div v-else-if="cartList.length === 0" class="bg-white rounded-xl p-16 text-center">
        <svg class="w-24 h-24 mx-auto text-gray-300 mb-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"/>
        </svg>
        <p class="text-gray-500 text-lg mb-2">购物车空空如也</p>
        <p class="text-gray-400 text-sm mb-6">快去挑选心仪的商品吧</p>
        <button
          @click="$router.push('/mall')"
          class="px-6 py-2 bg-blue-500 hover:bg-blue-600 text-white font-medium rounded-lg transition-colors"
        >
          去逛逛
        </button>
      </div>

      <!-- 购物车列表 -->
      <div v-else class="flex gap-6">
        <!-- 左侧商品列表 -->
        <div class="flex-1">
          <!-- 全选栏 -->
          <div class="bg-white rounded-xl shadow p-4 mb-4">
            <div class="flex items-center justify-between">
              <label class="flex items-center gap-3 cursor-pointer">
                <input
                  type="checkbox"
                  :checked="isAllSelected"
                  @change="handleSelectAll"
                  class="w-5 h-5 text-blue-500 border-gray-300 rounded focus:ring-blue-500"
                />
                <span class="text-gray-700 font-medium">全选</span>
              </label>
              <div v-if="isEditMode" class="flex items-center gap-4">
                <button
                  @click="handleBatchDelete"
                  :disabled="selectedCount === 0"
                  class="text-red-500 hover:text-red-600 text-sm font-medium disabled:text-gray-400 disabled:cursor-not-allowed transition-colors"
                >
                  删除选中 ({{ selectedCount }})
                </button>
                <button
                  @click="handleClearCart"
                  class="text-gray-500 hover:text-gray-600 text-sm font-medium transition-colors"
                >
                  清空购物车
                </button>
              </div>
            </div>
          </div>

          <!-- 有效商品 -->
          <div v-if="validItems.length > 0" class="bg-white rounded-xl shadow mb-4">
            <div class="divide-y divide-gray-100">
              <div
                v-for="item in validItems"
                :key="item.id"
                class="p-4 hover:bg-gray-50 transition-colors"
              >
                <div class="flex items-center gap-4">
                  <!-- 选择框 -->
                  <input
                    type="checkbox"
                    :checked="item.selected === 1"
                    @change="handleSelectItem(item)"
                    class="w-5 h-5 text-blue-500 border-gray-300 rounded focus:ring-blue-500 cursor-pointer"
                  />
                  <!-- 商品图片 -->
                  <div
                    class="w-24 h-24 bg-gray-100 rounded-lg overflow-hidden flex-shrink-0 cursor-pointer"
                    @click="$router.push(`/product/${item.productId}`)"
                  >
                    <img
                      :src="getImageUrl(item.productCoverImg)"
                      :alt="item.productName"
                      class="w-full h-full object-cover"
                    />
                  </div>
                  <!-- 商品信息 -->
                  <div class="flex-1 min-w-0">
                    <h3
                      class="text-base font-medium text-gray-800 truncate cursor-pointer hover:text-blue-500 transition-colors"
                      @click="$router.push(`/product/${item.productId}`)"
                    >
                      {{ item.productName }}
                    </h3>
                    <p class="text-sm text-gray-500 mt-1">单位：{{ item.productUnit || '件' }}</p>
                    <div class="flex items-center justify-between mt-3">
                      <div>
                        <span class="text-lg font-bold text-red-500">¥{{ item.productPrice }}</span>
                        <span v-if="item.productOriginalPrice && item.productOriginalPrice > item.productPrice" class="ml-2 text-sm text-gray-400 line-through">
                          ¥{{ item.productOriginalPrice }}
                        </span>
                      </div>
                      <!-- 数量调整 -->
                      <div class="flex items-center gap-2">
                        <button
                          @click="handleQuantityChange(item, item.quantity - 1)"
                          :disabled="item.quantity <= 1"
                          class="w-8 h-8 flex items-center justify-center border border-gray-300 rounded-lg hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                        >
                          <svg class="w-4 h-4 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 12H4"/>
                          </svg>
                        </button>
                        <input
                          type="number"
                          :value="item.quantity"
                          @change="handleQuantityInput(item, $event)"
                          min="1"
                          :max="item.productStock"
                          class="w-14 h-8 text-center border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                        />
                        <button
                          @click="handleQuantityChange(item, item.quantity + 1)"
                          :disabled="item.quantity >= item.productStock"
                          class="w-8 h-8 flex items-center justify-center border border-gray-300 rounded-lg hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                        >
                          <svg class="w-4 h-4 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
                          </svg>
                        </button>
                      </div>
                    </div>
                    <!-- 库存提示 -->
                    <p v-if="item.productStock <= 10" class="text-xs text-amber-600 mt-2">
                      仅剩 {{ item.productStock }} 件
                    </p>
                  </div>
                  <!-- 删除按钮 -->
                  <button
                    v-if="isEditMode"
                    @click="handleDeleteItem(item)"
                    class="p-2 text-gray-400 hover:text-red-500 transition-colors"
                  >
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                    </svg>
                  </button>
                  <!-- 小计 -->
                  <div v-else class="text-right min-w-[80px]">
                    <p class="text-sm text-gray-500">小计</p>
                    <p class="text-lg font-bold text-red-500">¥{{ item.subtotal }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 失效商品 -->
          <div v-if="invalidItems.length > 0" class="bg-white rounded-xl shadow">
            <div class="px-4 py-3 border-b border-gray-100 flex items-center justify-between">
              <span class="text-gray-500 text-sm">失效商品 ({{ invalidItems.length }})</span>
              <button
                @click="handleClearInvalid"
                class="text-blue-500 hover:text-blue-600 text-sm transition-colors"
              >
                清除失效商品
              </button>
            </div>
            <div class="divide-y divide-gray-100 opacity-60">
              <div
                v-for="item in invalidItems"
                :key="item.id"
                class="p-4"
              >
                <div class="flex items-center gap-4">
                  <!-- 失效标记 -->
                  <span class="px-2 py-1 text-xs font-medium text-gray-500 bg-gray-100 rounded">失效</span>
                  <!-- 商品图片 -->
                  <div class="w-24 h-24 bg-gray-100 rounded-lg overflow-hidden flex-shrink-0 grayscale">
                    <img
                      :src="getImageUrl(item.productCoverImg)"
                      :alt="item.productName"
                      class="w-full h-full object-cover"
                    />
                  </div>
                  <!-- 商品信息 -->
                  <div class="flex-1 min-w-0">
                    <h3 class="text-base font-medium text-gray-500 truncate">{{ item.productName }}</h3>
                    <p class="text-sm text-red-500 mt-1">{{ item.invalidReason }}</p>
                    <p class="text-lg font-bold text-gray-400 mt-2">¥{{ item.productPrice || '0.00' }}</p>
                  </div>
                  <!-- 删除按钮 -->
                  <button
                    @click="handleDeleteItem(item)"
                    class="p-2 text-gray-400 hover:text-red-500 transition-colors"
                  >
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧结算栏 -->
        <div class="w-80 flex-shrink-0">
          <div class="bg-white rounded-xl shadow p-6 sticky top-24">
            <h3 class="text-lg font-semibold text-gray-800 mb-4">订单结算</h3>
            <div class="space-y-3 mb-6">
              <div class="flex justify-between text-sm">
                <span class="text-gray-500">已选商品</span>
                <span class="text-gray-700">{{ selectedCount }} 件</span>
              </div>
              <div class="flex justify-between text-sm">
                <span class="text-gray-500">商品总价</span>
                <span class="text-gray-700">¥{{ totalAmount }}</span>
              </div>
              <div class="flex justify-between text-sm">
                <span class="text-gray-500">运费</span>
                <span class="text-emerald-500">免运费</span>
              </div>
            </div>
            <div class="border-t border-gray-200 pt-4 mb-6">
              <div class="flex justify-between items-center">
                <span class="text-gray-700 font-medium">应付金额</span>
                <span class="text-2xl font-bold text-red-500">¥{{ totalAmount }}</span>
              </div>
            </div>
            <button
              @click="handleCheckout"
              :disabled="selectedCount === 0"
              class="w-full py-3 bg-red-500 hover:bg-red-600 disabled:bg-gray-300 disabled:cursor-not-allowed text-white font-medium rounded-lg transition-colors"
            >
              去结算 ({{ selectedCount }})
            </button>
            <p v-if="invalidItems.length > 0" class="text-xs text-amber-600 text-center mt-3">
              有 {{ invalidItems.length }} 件失效商品，请清理后再结算
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- 确认弹窗 -->
    <div v-if="showConfirmModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-white rounded-2xl shadow-xl w-full max-w-md mx-4">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-800">{{ confirmTitle }}</h3>
        </div>
        <div class="px-6 py-4">
          <p class="text-gray-600">{{ confirmMessage }}</p>
        </div>
        <div class="flex justify-end gap-3 px-6 py-4 border-t border-gray-200">
          <button
            @click="showConfirmModal = false"
            class="px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
          >
            取消
          </button>
          <button
            @click="handleConfirm"
            class="px-4 py-2 text-white bg-red-500 rounded-lg hover:bg-red-600 transition-colors"
          >
            确认
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import {
  getCartList,
  updateCartQuantity,
  deleteCartItem,
  batchDeleteCartItems,
  clearCart,
  updateCartSelected,
  selectAllCart
} from '@/api/cart'

// 状态
const router = useRouter()
const loading = ref(false)
const cartList = ref([])
const isEditMode = ref(false)
const showConfirmModal = ref(false)
const confirmTitle = ref('')
const confirmMessage = ref('')
const confirmAction = ref(null)

// 计算属性
const validItems = computed(() => cartList.value.filter(item => item.valid))
const invalidItems = computed(() => cartList.value.filter(item => !item.valid))

const selectedCount = computed(() => {
  return validItems.value.filter(item => item.selected === 1).reduce((sum, item) => sum + item.quantity, 0)
})

const totalAmount = computed(() => {
  return validItems.value
    .filter(item => item.selected === 1)
    .reduce((sum, item) => sum + parseFloat(item.subtotal || 0), 0)
    .toFixed(2)
})

const isAllSelected = computed(() => {
  if (validItems.value.length === 0) return false
  return validItems.value.every(item => item.selected === 1)
})

// 获取图片URL
const getImageUrl = (url) => {
  if (!url) return 'https://via.placeholder.com/200x200?text=No+Image'
  if (url.startsWith('http')) return url
  return url
}

// 加载购物车
const loadCart = async () => {
  loading.value = true
  try {
    const res = await getCartList()
    cartList.value = res.data || []
  } catch (error) {
    console.error('获取购物车失败:', error)
    MessagePlugin.error('获取购物车失败')
  } finally {
    loading.value = false
  }
}

// 全选/取消全选
const handleSelectAll = async () => {
  const newSelected = isAllSelected.value ? 0 : 1
  try {
    await selectAllCart(newSelected)
    cartList.value.forEach(item => {
      if (item.valid) {
        item.selected = newSelected
      }
    })
  } catch (error) {
    console.error('操作失败:', error)
    MessagePlugin.error('操作失败')
  }
}

// 选中/取消选中单个商品
const handleSelectItem = async (item) => {
  const newSelected = item.selected === 1 ? 0 : 1
  try {
    await updateCartSelected(item.id, newSelected)
    item.selected = newSelected
  } catch (error) {
    console.error('操作失败:', error)
    MessagePlugin.error('操作失败')
  }
}

// 修改数量
const handleQuantityChange = async (item, newQuantity) => {
  if (newQuantity < 1 || newQuantity > item.productStock) return
  const oldQuantity = item.quantity
  item.quantity = newQuantity
  item.subtotal = (parseFloat(item.productPrice) * newQuantity).toFixed(2)
  try {
    await updateCartQuantity(item.id, newQuantity)
  } catch (error) {
    item.quantity = oldQuantity
    item.subtotal = (parseFloat(item.productPrice) * oldQuantity).toFixed(2)
    console.error('修改数量失败:', error)
    MessagePlugin.error(error.message || '修改数量失败')
  }
}

// 输入数量
const handleQuantityInput = (item, event) => {
  let value = parseInt(event.target.value)
  if (isNaN(value) || value < 1) value = 1
  if (value > item.productStock) value = item.productStock
  handleQuantityChange(item, value)
}

// 删除单个商品
const handleDeleteItem = (item) => {
  confirmTitle.value = '删除商品'
  confirmMessage.value = `确定要删除"${item.productName}"吗？`
  confirmAction.value = async () => {
    try {
      await deleteCartItem(item.id)
      cartList.value = cartList.value.filter(i => i.id !== item.id)
      MessagePlugin.success('删除成功')
    } catch (error) {
      console.error('删除失败:', error)
      MessagePlugin.error('删除失败')
    }
  }
  showConfirmModal.value = true
}

// 批量删除
const handleBatchDelete = () => {
  const selectedItems = validItems.value.filter(item => item.selected === 1)
  if (selectedItems.length === 0) {
    MessagePlugin.warning('请先选择要删除的商品')
    return
  }
  confirmTitle.value = '批量删除'
  confirmMessage.value = `确定要删除选中的 ${selectedItems.length} 件商品吗？`
  confirmAction.value = async () => {
    try {
      const ids = selectedItems.map(item => item.id)
      await batchDeleteCartItems(ids)
      cartList.value = cartList.value.filter(item => !ids.includes(item.id))
      MessagePlugin.success('删除成功')
    } catch (error) {
      console.error('删除失败:', error)
      MessagePlugin.error('删除失败')
    }
  }
  showConfirmModal.value = true
}

// 清空购物车
const handleClearCart = () => {
  confirmTitle.value = '清空购物车'
  confirmMessage.value = '确定要清空购物车中的所有商品吗？'
  confirmAction.value = async () => {
    try {
      await clearCart()
      cartList.value = []
      isEditMode.value = false
      MessagePlugin.success('购物车已清空')
    } catch (error) {
      console.error('清空失败:', error)
      MessagePlugin.error('清空失败')
    }
  }
  showConfirmModal.value = true
}

// 清除失效商品
const handleClearInvalid = () => {
  confirmTitle.value = '清除失效商品'
  confirmMessage.value = `确定要清除 ${invalidItems.value.length} 件失效商品吗？`
  confirmAction.value = async () => {
    try {
      const ids = invalidItems.value.map(item => item.id)
      await batchDeleteCartItems(ids)
      cartList.value = cartList.value.filter(item => item.valid)
      MessagePlugin.success('清除成功')
    } catch (error) {
      console.error('清除失败:', error)
      MessagePlugin.error('清除失败')
    }
  }
  showConfirmModal.value = true
}

// 确认操作
const handleConfirm = async () => {
  showConfirmModal.value = false
  if (confirmAction.value) {
    await confirmAction.value()
    confirmAction.value = null
  }
}

// 去结算
const handleCheckout = () => {
  if (selectedCount.value === 0) {
    MessagePlugin.warning('请先选择要结算的商品')
    return
  }
  // 获取选中的购物车ID
  const selectedItems = validItems.value.filter(item => item.selected === 1)
  const cartIds = selectedItems.map(item => item.id).join(',')
  // 跳转到结算页面
  router.push(`/checkout?cartIds=${cartIds}`)
}

onMounted(() => {
  loadCart()
})
</script>

<style scoped>
/* 隐藏数字输入框的上下箭头 */
input[type="number"]::-webkit-inner-spin-button,
input[type="number"]::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}
input[type="number"] {
  -moz-appearance: textfield;
}
</style>
