<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部搜索栏 -->
    <div class="bg-white shadow-sm sticky top-0 z-10">
      <div class="max-w-7xl mx-auto px-6 py-4">
        <div class="flex items-center justify-between">
          <h1 class="text-2xl font-semibold text-gray-800">母婴商城</h1>
          <div class="flex items-center gap-4">
            <div class="relative">
              <input
                v-model="searchKeyword"
                type="text"
                placeholder="搜索商品..."
                class="w-80 px-4 py-2 pl-10 text-gray-700 bg-gray-100 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
                @keyup.enter="handleSearch"
              />
              <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
              </svg>
            </div>
            <button
              @click="$router.push('/cart')"
              class="flex items-center gap-2 px-4 py-2 bg-blue-500 hover:bg-blue-600 text-white font-medium rounded-lg transition-colors"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"/>
              </svg>
              购物车
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="max-w-7xl mx-auto px-6 py-6">
      <!-- 分类导航 -->
      <div class="bg-white rounded-xl shadow p-6 mb-6">
        <div class="flex items-center gap-4 overflow-x-auto pb-2">
          <!-- 全部分类 -->
          <div
            class="flex flex-col items-center gap-2 px-4 py-3 rounded-lg cursor-pointer transition-all min-w-[80px]"
            :class="activeCategory === null ? 'bg-blue-50' : 'hover:bg-gray-50'"
            @click="selectCategory(null)"
          >
            <div class="w-12 h-12 rounded-lg flex items-center justify-center text-white" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z"/>
              </svg>
            </div>
            <span class="text-sm font-medium" :class="activeCategory === null ? 'text-blue-600' : 'text-gray-600'">全部</span>
          </div>
          <!-- 动态分类 -->
          <div
            v-for="(cat, index) in categoryTree"
            :key="cat.id"
            class="flex flex-col items-center gap-2 px-4 py-3 rounded-lg cursor-pointer transition-all min-w-[80px]"
            :class="activeCategory === cat.id ? 'bg-blue-50' : 'hover:bg-gray-50'"
            @click="selectCategory(cat.id)"
          >
            <div class="w-12 h-12 rounded-lg flex items-center justify-center text-white" :style="{ background: gradientColors[index % gradientColors.length] }">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" :d="categoryIcons[index % categoryIcons.length]"/>
              </svg>
            </div>
            <span class="text-sm font-medium" :class="activeCategory === cat.id ? 'text-blue-600' : 'text-gray-600'">{{ cat.name }}</span>
          </div>
        </div>
      </div>

      <!-- 推荐商品区域 -->
      <div class="mb-8" v-if="!activeCategory && !searchKeyword">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-xl font-semibold text-gray-800">热门推荐</h2>
          <button @click="$router.push('/products')" class="text-blue-500 hover:text-blue-600 text-sm font-medium">
            查看更多 →
          </button>
        </div>
        <div class="grid grid-cols-4 gap-6" v-loading="recommendLoading">
          <div
            v-for="product in recommendProducts"
            :key="product.id"
            class="bg-white rounded-xl overflow-hidden cursor-pointer border-2 border-transparent hover:border-blue-500 hover:shadow-lg transition-all group"
            @click="$router.push(`/product/${product.id}`)"
          >
            <div class="relative h-48 bg-gray-100 overflow-hidden">
              <img :src="getImageUrl(product.coverImg)" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" />
              <span v-if="product.sales > 100" class="absolute top-3 right-3 px-2 py-1 text-xs font-medium text-white bg-red-500 rounded-full">热卖</span>
            </div>
            <div class="p-4">
              <h3 class="text-base font-semibold text-gray-800 truncate mb-1">{{ product.name }}</h3>
              <p class="text-sm text-gray-500 line-clamp-2 h-10 mb-3">{{ product.description || '暂无描述' }}</p>
              <div class="flex items-center justify-between">
                <div>
                  <span class="text-lg font-bold text-red-500">¥{{ product.price }}</span>
                  <span v-if="product.originalPrice" class="ml-2 text-sm text-gray-400 line-through">¥{{ product.originalPrice }}</span>
                </div>
                <span class="text-xs text-gray-400">已售 {{ product.sales || 0 }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 商品列表 -->
      <div>
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-xl font-semibold text-gray-800">
            {{ activeCategory ? getCategoryName(activeCategory) : '全部商品' }}
          </h2>
          <div class="flex items-center gap-2">
            <span class="text-sm text-gray-500">共 {{ total }} 件商品</span>
          </div>
        </div>

        <!-- 商品网格 -->
        <div class="grid grid-cols-4 gap-6" v-loading="loading">
          <div
            v-for="product in productList"
            :key="product.id"
            class="bg-white rounded-xl overflow-hidden cursor-pointer border-2 border-transparent hover:border-blue-500 hover:shadow-lg transition-all group"
            @click="$router.push(`/product/${product.id}`)"
          >
            <div class="relative h-48 bg-gray-100 overflow-hidden">
              <img :src="getImageUrl(product.coverImg)" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" />
              <span v-if="product.sales > 100" class="absolute top-3 right-3 px-2 py-1 text-xs font-medium text-white bg-red-500 rounded-full">热卖</span>
            </div>
            <div class="p-4">
              <h3 class="text-base font-semibold text-gray-800 truncate mb-1">{{ product.name }}</h3>
              <p class="text-sm text-gray-500 line-clamp-2 h-10 mb-3">{{ product.description || '暂无描述' }}</p>
              <div class="flex items-center justify-between">
                <div>
                  <span class="text-lg font-bold text-red-500">¥{{ product.price }}</span>
                  <span v-if="product.originalPrice" class="ml-2 text-sm text-gray-400 line-through">¥{{ product.originalPrice }}</span>
                </div>
                <span class="text-xs text-gray-400">已售 {{ product.sales || 0 }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="!loading && productList.length === 0" class="bg-white rounded-xl p-12 text-center">
          <svg class="w-16 h-16 mx-auto text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"/>
          </svg>
          <p class="text-gray-500">暂无商品</p>
        </div>

        <!-- 分页 -->
        <div v-if="total > pageSize" class="flex justify-center mt-8">
          <t-pagination
            v-model:current="pageNum"
            v-model:pageSize="pageSize"
            :total="total"
            :page-size-options="[12, 24, 48]"
            show-jumper
            @change="loadProducts"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { getCategoryTree } from '@/api/productCategory'
import { getProductList, getRecommendProducts } from '@/api/product'

// 状态
const loading = ref(false)
const recommendLoading = ref(false)
const searchKeyword = ref('')
const activeCategory = ref(null)
const categoryTree = ref([])
const productList = ref([])
const recommendProducts = ref([])
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)

// 预定义的渐变色
const gradientColors = [
  'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
  'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
  'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
  'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
  'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
  'linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)',
  'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)'
]

// 预定义的图标路径
const categoryIcons = [
  'M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253',
  'M12 8v13m0-13V6a2 2 0 112 2h-2zm0 0V5.5A2.5 2.5 0 109.5 8H12zm-7 4h14M5 12a2 2 0 110-4h14a2 2 0 110 4M5 12v7a2 2 0 002 2h10a2 2 0 002-2v-7',
  'M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z',
  'M19.428 15.428a2 2 0 00-1.022-.547l-2.387-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z',
  'M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z',
  'M14.828 14.828a4 4 0 01-5.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z',
  'M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z',
  'M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z'
]

// 获取图片URL
const getImageUrl = (url) => {
  if (!url) return 'https://via.placeholder.com/300x300?text=No+Image'
  if (url.startsWith('http')) return url
  return url
}

// 获取分类名称
const getCategoryName = (categoryId) => {
  const findCategory = (list, id) => {
    for (const cat of list) {
      if (cat.id === id) return cat.name
      if (cat.children) {
        const found = findCategory(cat.children, id)
        if (found) return found
      }
    }
    return null
  }
  return findCategory(categoryTree.value, categoryId) || '商品列表'
}

// 加载分类
const loadCategories = async () => {
  try {
    const res = await getCategoryTree()
    categoryTree.value = res.data || []
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 加载推荐商品
const loadRecommendProducts = async () => {
  recommendLoading.value = true
  try {
    const res = await getRecommendProducts(8)
    recommendProducts.value = res.data || []
  } catch (error) {
    console.error('获取推荐商品失败:', error)
  } finally {
    recommendLoading.value = false
  }
}

// 加载商品列表
const loadProducts = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      status: 1 // 只查询上架商品
    }
    if (activeCategory.value) {
      params.categoryId = activeCategory.value
    }
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    const res = await getProductList(params)
    productList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取商品列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 选择分类
const selectCategory = (categoryId) => {
  activeCategory.value = categoryId
  pageNum.value = 1
  loadProducts()
}

// 搜索
const handleSearch = () => {
  pageNum.value = 1
  loadProducts()
}

// 监听搜索关键词变化
watch(searchKeyword, (val) => {
  if (!val) {
    loadProducts()
  }
})

onMounted(() => {
  loadCategories()
  loadRecommendProducts()
  loadProducts()
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
