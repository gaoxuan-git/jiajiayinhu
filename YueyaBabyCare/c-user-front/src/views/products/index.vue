<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部筛选栏 -->
    <div class="bg-white shadow-sm sticky top-0 z-10">
      <div class="max-w-7xl mx-auto px-6 py-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-4">
            <button @click="$router.back()" class="p-2 hover:bg-gray-100 rounded-lg transition-colors">
              <svg class="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
              </svg>
            </button>
            <h1 class="text-xl font-semibold text-gray-800">商品列表</h1>
          </div>
          <div class="flex items-center gap-4">
            <!-- 搜索框 -->
            <div class="relative">
              <input
                v-model="keyword"
                type="text"
                placeholder="搜索商品..."
                class="w-72 px-4 py-2 pl-10 text-gray-700 bg-gray-100 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
                @keyup.enter="handleSearch"
              />
              <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
              </svg>
            </div>
            <!-- 购物车按钮 -->
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
      <div class="flex gap-6">
        <!-- 左侧分类筛选 -->
        <div class="w-56 flex-shrink-0">
          <div class="bg-white rounded-xl shadow p-4 sticky top-24">
            <h3 class="text-base font-semibold text-gray-800 mb-4">商品分类</h3>
            <div class="space-y-1">
              <!-- 全部 -->
              <div
                class="px-3 py-2 rounded-lg cursor-pointer transition-all"
                :class="selectedCategory === null ? 'bg-blue-50 text-blue-600 font-medium' : 'hover:bg-gray-50 text-gray-600'"
                @click="selectCategory(null)"
              >
                全部商品
              </div>
              <!-- 分类列表 -->
              <template v-for="cat in categoryTree" :key="cat.id">
                <div
                  class="px-3 py-2 rounded-lg cursor-pointer transition-all"
                  :class="selectedCategory === cat.id ? 'bg-blue-50 text-blue-600 font-medium' : 'hover:bg-gray-50 text-gray-600'"
                  @click="selectCategory(cat.id)"
                >
                  {{ cat.name }}
                </div>
                <!-- 子分类 -->
                <div v-if="cat.children && cat.children.length > 0" class="ml-4 space-y-1">
                  <div
                    v-for="child in cat.children"
                    :key="child.id"
                    class="px-3 py-1.5 rounded-lg cursor-pointer transition-all text-sm"
                    :class="selectedCategory === child.id ? 'bg-blue-50 text-blue-600 font-medium' : 'hover:bg-gray-50 text-gray-500'"
                    @click="selectCategory(child.id)"
                  >
                    {{ child.name }}
                  </div>
                </div>
              </template>
            </div>
          </div>
        </div>

        <!-- 右侧商品列表 -->
        <div class="flex-1">
          <!-- 排序和统计 -->
          <div class="bg-white rounded-xl shadow p-4 mb-6">
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-4">
                <span class="text-sm text-gray-500">共 {{ total }} 件商品</span>
              </div>
              <div class="flex items-center gap-2">
                <span class="text-sm text-gray-500">排序：</span>
                <button
                  v-for="sort in sortOptions"
                  :key="sort.value"
                  class="px-3 py-1 text-sm rounded-lg transition-colors"
                  :class="currentSort === sort.value ? 'bg-blue-500 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'"
                  @click="changeSort(sort.value)"
                >
                  {{ sort.label }}
                </button>
              </div>
            </div>
          </div>

          <!-- 商品网格 -->
          <div class="grid grid-cols-3 gap-6" v-loading="loading">
            <div
              v-for="product in productList"
              :key="product.id"
              class="bg-white rounded-xl overflow-hidden cursor-pointer border-2 border-transparent hover:border-blue-500 hover:shadow-lg transition-all group"
              @click="$router.push(`/product/${product.id}`)"
            >
              <div class="relative h-52 bg-gray-100 overflow-hidden">
                <img :src="getImageUrl(product.coverImg)" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" />
                <span v-if="product.sales > 100" class="absolute top-3 right-3 px-2 py-1 text-xs font-medium text-white bg-red-500 rounded-full">热卖</span>
                <span v-if="product.originalPrice && product.originalPrice > product.price" class="absolute top-3 left-3 px-2 py-1 text-xs font-medium text-white bg-amber-500 rounded-full">
                  {{ Math.round((1 - product.price / product.originalPrice) * 100) }}% OFF
                </span>
              </div>
              <div class="p-4">
                <h3 class="text-base font-semibold text-gray-800 truncate mb-1">{{ product.name }}</h3>
                <p class="text-sm text-gray-500 line-clamp-2 h-10 mb-3">{{ product.description || '暂无描述' }}</p>
                <div class="flex items-center justify-between">
                  <div>
                    <span class="text-lg font-bold text-red-500">¥{{ product.price }}</span>
                    <span v-if="product.originalPrice" class="ml-2 text-sm text-gray-400 line-through">¥{{ product.originalPrice }}</span>
                  </div>
                  <div class="flex items-center gap-2">
                    <span class="text-xs text-gray-400">库存 {{ product.stock }}</span>
                    <span class="text-xs text-gray-400">|</span>
                    <span class="text-xs text-gray-400">已售 {{ product.sales || 0 }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-if="!loading && productList.length === 0" class="bg-white rounded-xl p-16 text-center">
            <svg class="w-20 h-20 mx-auto text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"/>
            </svg>
            <p class="text-gray-500 text-lg">暂无商品</p>
            <p class="text-gray-400 text-sm mt-2">换个关键词试试吧</p>
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
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getCategoryTree } from '@/api/productCategory'
import { getProductList } from '@/api/product'

const route = useRoute()

// 状态
const loading = ref(false)
const keyword = ref('')
const selectedCategory = ref(null)
const categoryTree = ref([])
const productList = ref([])
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)
const currentSort = ref('default')

// 排序选项
const sortOptions = [
  { label: '默认', value: 'default' },
  { label: '销量', value: 'sales' },
  { label: '价格↑', value: 'price_asc' },
  { label: '价格↓', value: 'price_desc' },
  { label: '最新', value: 'newest' }
]

// 获取图片URL
const getImageUrl = (url) => {
  if (!url) return 'https://via.placeholder.com/300x300?text=No+Image'
  if (url.startsWith('http')) return url
  return url
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

// 加载商品列表
const loadProducts = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      status: 1
    }
    if (selectedCategory.value) {
      params.categoryId = selectedCategory.value
    }
    if (keyword.value) {
      params.keyword = keyword.value
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
  selectedCategory.value = categoryId
  pageNum.value = 1
  loadProducts()
}

// 搜索
const handleSearch = () => {
  pageNum.value = 1
  loadProducts()
}

// 切换排序
const changeSort = (sort) => {
  currentSort.value = sort
  pageNum.value = 1
  loadProducts()
}

// 监听路由参数
watch(() => route.query, (query) => {
  if (query.categoryId) {
    selectedCategory.value = Number(query.categoryId)
  }
  if (query.keyword) {
    keyword.value = query.keyword
  }
  loadProducts()
}, { immediate: true })

onMounted(() => {
  loadCategories()
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
