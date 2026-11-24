<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部导航 -->
    <div class="bg-white shadow-sm sticky top-0 z-10">
      <div class="max-w-7xl mx-auto px-6 py-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-4">
            <button @click="$router.back()" class="p-2 hover:bg-gray-100 rounded-lg transition-colors">
              <svg class="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
              </svg>
            </button>
            <h1 class="text-xl font-semibold text-gray-800">商品详情</h1>
          </div>
          <div class="flex items-center gap-4">
            <button @click="$router.push('/cart')" class="flex items-center gap-2 px-4 py-2 bg-blue-500 hover:bg-blue-600 text-white font-medium rounded-lg transition-colors">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"/>
              </svg>
              购物车
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="max-w-7xl mx-auto px-6 py-6" v-loading="loading">
      <!-- 商品主体信息 -->
      <div class="bg-white rounded-xl shadow p-6 mb-6">
        <div class="grid grid-cols-2 gap-8">
          <!-- 左侧：商品图片 -->
          <div>
            <!-- 主图 -->
            <div class="relative h-96 bg-gray-100 rounded-xl overflow-hidden mb-4">
              <img :src="getImageUrl(currentImage)" class="w-full h-full object-contain" />
              <span v-if="product.sales > 100" class="absolute top-4 right-4 px-3 py-1 text-sm font-medium text-white bg-red-500 rounded-full">热卖</span>
            </div>
            <!-- 缩略图列表 -->
            <div class="flex gap-3" v-if="allImages.length > 1">
              <div
                v-for="(img, index) in allImages"
                :key="index"
                class="w-20 h-20 rounded-lg overflow-hidden cursor-pointer border-2 transition-all"
                :class="currentImage === img ? 'border-blue-500' : 'border-transparent hover:border-gray-300'"
                @click="currentImage = img"
              >
                <img :src="getImageUrl(img)" class="w-full h-full object-cover" />
              </div>
            </div>
          </div>

          <!-- 右侧：商品信息 -->
          <div>
            <!-- 商品名称 -->
            <h1 class="text-2xl font-bold text-gray-800 mb-3">{{ product.name }}</h1>

            <!-- 商品描述 -->
            <p class="text-gray-500 mb-6 leading-relaxed">{{ product.description || '暂无描述' }}</p>

            <!-- 价格区域 -->
            <div class="bg-gradient-to-r from-red-50 to-amber-50 rounded-xl p-6 mb-6">
              <div class="flex items-baseline gap-4">
                <span class="text-4xl font-bold text-red-500">¥{{ product.price }}</span>
                <span v-if="product.originalPrice" class="text-xl text-gray-400 line-through">¥{{ product.originalPrice }}</span>
                <span v-if="product.originalPrice && product.originalPrice > product.price" class="px-2 py-1 text-sm font-medium text-white bg-red-500 rounded">
                  省 ¥{{ (product.originalPrice - product.price).toFixed(2) }}
                </span>
              </div>
            </div>

            <!-- 商品属性 -->
            <div class="space-y-4 mb-6">
              <div class="flex items-center">
                <span class="w-20 text-gray-500">分类</span>
                <span class="text-gray-700">{{ product.categoryName || '未分类' }}</span>
              </div>
              <div class="flex items-center">
                <span class="w-20 text-gray-500">库存</span>
                <span :class="product.stock > 10 ? 'text-emerald-600' : product.stock > 0 ? 'text-amber-600' : 'text-red-600'">
                  {{ product.stock > 0 ? `${product.stock} ${product.unit || '件'}` : '暂时缺货' }}
                </span>
              </div>
              <div class="flex items-center">
                <span class="w-20 text-gray-500">销量</span>
                <span class="text-gray-700">{{ product.sales || 0 }} 件</span>
              </div>
              <div class="flex items-center" v-if="product.institutionName">
                <span class="w-20 text-gray-500">商家</span>
                <span class="text-blue-600">{{ product.institutionName }}</span>
              </div>
            </div>

            <!-- 数量选择 -->
            <div class="flex items-center gap-4 mb-8">
              <span class="text-gray-500">数量</span>
              <div class="flex items-center">
                <button
                  @click="quantity > 1 && quantity--"
                  class="w-10 h-10 flex items-center justify-center bg-gray-100 hover:bg-gray-200 rounded-l-lg transition-colors"
                  :class="{ 'opacity-50 cursor-not-allowed': quantity <= 1 }"
                >
                  <svg class="w-4 h-4 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 12H4"/>
                  </svg>
                </button>
                <input
                  v-model.number="quantity"
                  type="number"
                  min="1"
                  :max="product.stock"
                  class="w-16 h-10 text-center border-y border-gray-200 focus:outline-none"
                />
                <button
                  @click="quantity < product.stock && quantity++"
                  class="w-10 h-10 flex items-center justify-center bg-gray-100 hover:bg-gray-200 rounded-r-lg transition-colors"
                  :class="{ 'opacity-50 cursor-not-allowed': quantity >= product.stock }"
                >
                  <svg class="w-4 h-4 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
                  </svg>
                </button>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="flex gap-4">
              <button
                @click="addToCart"
                :disabled="product.stock <= 0"
                class="flex-1 flex items-center justify-center gap-2 px-6 py-3 bg-amber-500 hover:bg-amber-600 text-white font-medium rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"/>
                </svg>
                加入购物车
              </button>
              <button
                @click="buyNow"
                :disabled="product.stock <= 0"
                class="flex-1 flex items-center justify-center gap-2 px-6 py-3 bg-blue-500 hover:bg-blue-600 text-white font-medium rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"/>
                </svg>
                立即购买
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 商品详情 -->
      <div class="bg-white rounded-xl shadow p-6 mb-6">
        <h2 class="text-lg font-semibold text-gray-800 mb-4 pb-4 border-b border-gray-200">商品详情</h2>
        <div class="prose max-w-none text-gray-600 leading-relaxed">
          <p v-if="product.description">{{ product.description }}</p>
          <p v-else class="text-gray-400">暂无详细描述</p>
        </div>
      </div>

      <!-- 推荐商品 -->
      <div class="bg-white rounded-xl shadow p-6" v-if="recommendProducts.length > 0">
        <h2 class="text-lg font-semibold text-gray-800 mb-4 pb-4 border-b border-gray-200">猜你喜欢</h2>
        <div class="grid grid-cols-4 gap-6">
          <div
            v-for="item in recommendProducts"
            :key="item.id"
            class="bg-gray-50 rounded-xl overflow-hidden cursor-pointer hover:shadow-md transition-all group"
            @click="goToProduct(item.id)"
          >
            <div class="relative h-40 bg-gray-100 overflow-hidden">
              <img :src="getImageUrl(item.coverImg)" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" />
            </div>
            <div class="p-3">
              <h3 class="text-sm font-medium text-gray-800 truncate mb-1">{{ item.name }}</h3>
              <span class="text-base font-bold text-red-500">¥{{ item.price }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import { getProductById, getRecommendProducts } from '@/api/product'
import { addToCart as addToCartApi } from '@/api/cart'

const route = useRoute()
const router = useRouter()

// 状态
const loading = ref(false)
const product = ref({})
const quantity = ref(1)
const currentImage = ref('')
const recommendProducts = ref([])

// 计算所有图片
const allImages = computed(() => {
  const images = []
  if (product.value.coverImg) {
    images.push(product.value.coverImg)
  }
  if (product.value.images && Array.isArray(product.value.images)) {
    images.push(...product.value.images.filter(img => img && img !== product.value.coverImg))
  }
  return images
})

// 获取图片URL
const getImageUrl = (url) => {
  if (!url) return 'https://via.placeholder.com/400x400?text=No+Image'
  if (url.startsWith('http')) return url
  return url
}

// 加载商品详情
const loadProduct = async () => {
  const id = route.params.id
  if (!id) return

  loading.value = true
  try {
    const res = await getProductById(id)
    product.value = res.data || {}
    currentImage.value = product.value.coverImg || ''
    quantity.value = 1
  } catch (error) {
    console.error('获取商品详情失败:', error)
    MessagePlugin.error('获取商品详情失败')
  } finally {
    loading.value = false
  }
}

// 加载推荐商品
const loadRecommendProducts = async () => {
  try {
    const res = await getRecommendProducts(4)
    // 过滤掉当前商品
    recommendProducts.value = (res.data || []).filter(item => item.id !== Number(route.params.id))
  } catch (error) {
    console.error('获取推荐商品失败:', error)
  }
}

// 加入购物车
const addToCart = async () => {
  if (product.value.stock <= 0) {
    MessagePlugin.warning('商品暂时缺货')
    return
  }
  try {
    await addToCartApi({
      productId: product.value.id,
      quantity: quantity.value
    })
    MessagePlugin.success(`已将 ${quantity.value} 件商品加入购物车`)
  } catch (error) {
    console.error('加入购物车失败:', error)
    // 错误消息已在拦截器中处理
  }
}

// 立即购买
const buyNow = () => {
  if (product.value.stock <= 0) {
    MessagePlugin.warning('商品暂时缺货')
    return
  }
  // TODO: 跳转到订单确认页
  MessagePlugin.info('功能开发中...')
}

// 跳转到商品详情
const goToProduct = (id) => {
  router.push(`/product/${id}`)
}

// 监听路由变化
watch(() => route.params.id, (newId) => {
  if (newId) {
    loadProduct()
    loadRecommendProducts()
  }
})

onMounted(() => {
  loadProduct()
  loadRecommendProducts()
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
