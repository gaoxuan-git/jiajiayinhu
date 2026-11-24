<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部标题 -->
    <div class="bg-white px-4 py-4 border-b border-gray-200">
      <h1 class="text-xl font-semibold text-gray-800">我的评价</h1>
      <p class="text-sm text-gray-500 mt-1">家长对您服务的评价反馈</p>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex justify-center items-center py-20">
      <t-loading size="large" text="加载中..." />
    </div>

    <!-- 内容区 -->
    <div v-else class="p-4 space-y-4">
      <!-- 评分统计卡片 -->
      <div class="bg-white rounded-xl shadow p-4">
        <div class="flex items-center">
          <!-- 综合评分 -->
          <div class="flex-shrink-0 text-center pr-6 border-r border-gray-200">
            <div class="text-4xl font-bold text-amber-500">{{ stats.avgScore || '0.0' }}</div>
            <div class="flex items-center justify-center mt-1">
              <svg 
                v-for="n in 5" 
                :key="n" 
                class="w-4 h-4"
                :class="n <= Math.round(stats.avgScore || 0) ? 'text-amber-400' : 'text-gray-200'"
                fill="currentColor" 
                viewBox="0 0 24 24"
              >
                <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
              </svg>
            </div>
            <div class="text-xs text-gray-400 mt-1">综合评分</div>
          </div>
          
          <!-- 评分分布 -->
          <div class="flex-1 pl-6 space-y-2">
            <div v-for="star in [5,4,3,2,1]" :key="star" class="flex items-center gap-2">
              <span class="text-xs text-gray-500 w-8">{{ star }}星</span>
              <div class="flex-1 h-2 bg-gray-100 rounded-full overflow-hidden">
                <div 
                  class="h-full bg-amber-400 rounded-full transition-all duration-500"
                  :style="{ width: getStarPercent(star) + '%' }"
                ></div>
              </div>
              <span class="text-xs text-gray-400 w-8">{{ getStarCount(star) }}</span>
            </div>
          </div>
        </div>
        
        <!-- 统计数据 -->
        <div class="grid grid-cols-3 gap-4 mt-4 pt-4 border-t border-gray-100">
          <div class="text-center">
            <div class="text-xl font-semibold text-gray-800">{{ stats.totalCount || 0 }}</div>
            <div class="text-xs text-gray-500">总评价数</div>
          </div>
          <div class="text-center">
            <div class="text-xl font-semibold text-emerald-600">{{ stats.positiveRate || '0' }}%</div>
            <div class="text-xs text-gray-500">好评率</div>
          </div>
          <div class="text-center">
            <div class="text-xl font-semibold text-blue-600">{{ repliedCount }}</div>
            <div class="text-xs text-gray-500">已回复</div>
          </div>
        </div>
      </div>

      <!-- 筛选标签 -->
      <div class="bg-white rounded-xl shadow p-3">
        <div class="flex gap-2 overflow-x-auto">
          <button
            v-for="filter in filters"
            :key="filter.value"
            @click="currentFilter = filter.value"
            :class="[
              'px-3 py-1.5 rounded-full text-sm whitespace-nowrap transition-colors',
              currentFilter === filter.value
                ? 'bg-blue-500 text-white'
                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
            ]"
          >
            {{ filter.label }}
          </button>
        </div>
      </div>

      <!-- 评价列表 -->
      <div class="bg-white rounded-xl shadow">
        <div v-if="filteredEvaluations.length > 0" class="divide-y divide-gray-100">
          <div 
            v-for="evaluation in filteredEvaluations" 
            :key="evaluation.id" 
            class="p-4"
          >
            <!-- 评价头部 -->
            <div class="flex items-start justify-between mb-3">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 font-medium">
                  {{ getInitial(evaluation.parentName) }}
                </div>
                <div>
                  <div class="font-medium text-gray-800">{{ evaluation.parentName || '匿名用户' }}</div>
                  <div class="flex items-center gap-1 mt-0.5">
                    <svg 
                      v-for="n in 5" 
                      :key="n" 
                      class="w-3.5 h-3.5"
                      :class="n <= evaluation.score ? 'text-amber-400' : 'text-gray-200'"
                      fill="currentColor" 
                      viewBox="0 0 24 24"
                    >
                      <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                    </svg>
                    <span class="text-xs text-gray-400 ml-1">{{ getRatingText(evaluation.score) }}</span>
                  </div>
                </div>
              </div>
              <span class="text-xs text-gray-400">{{ formatDateTime(evaluation.createTime) }}</span>
            </div>

            <!-- 评价内容 -->
            <p class="text-sm text-gray-700 leading-relaxed">{{ evaluation.content || '该用户未填写评价内容' }}</p>

            <!-- 评价图片 -->
            <div v-if="evaluation.images && parseImages(evaluation.images).length > 0" class="flex gap-2 mt-3 overflow-x-auto">
              <img 
                v-for="(img, idx) in parseImages(evaluation.images)" 
                :key="idx" 
                :src="getFullImageUrl(img)" 
                class="w-16 h-16 rounded-lg object-cover flex-shrink-0 cursor-pointer"
                @click="previewImage(parseImages(evaluation.images), idx)"
              />
            </div>

            <!-- 关联信息 -->
            <div class="flex items-center gap-3 mt-3 text-xs text-gray-400">
              <span v-if="evaluation.babyName">宝宝: {{ evaluation.babyName }}</span>
              <span v-if="evaluation.carePlanName">计划: {{ evaluation.carePlanName }}</span>
            </div>

            <!-- 回复区域 -->
            <div v-if="evaluation.replyContent" class="mt-3 bg-gray-50 rounded-lg p-3">
              <div class="flex items-center gap-2 mb-1">
                <svg class="w-4 h-4 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h10a8 8 0 018 8v2M3 10l6 6m-6-6l6-6"/>
                </svg>
                <span class="text-xs text-blue-600">我的回复</span>
                <span class="text-xs text-gray-400">{{ formatDateTime(evaluation.replyTime) }}</span>
              </div>
              <p class="text-sm text-gray-600">{{ evaluation.replyContent }}</p>
            </div>

            <!-- 回复按钮 -->
            <div v-else class="mt-3">
              <button 
                @click="openReplyDialog(evaluation)"
                class="text-sm text-blue-500 hover:text-blue-600 transition-colors flex items-center gap-1"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h10a8 8 0 018 8v2M3 10l6 6m-6-6l6-6"/>
                </svg>
                回复评价
              </button>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else class="flex flex-col items-center justify-center py-12 text-gray-400">
          <svg class="w-16 h-16 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z"/>
          </svg>
          <p class="text-sm">{{ getEmptyText() }}</p>
        </div>
      </div>
    </div>

    <!-- 回复弹窗 -->
    <t-dialog
      v-model:visible="replyDialogVisible"
      header="回复评价"
      :confirm-btn="{ content: '发送回复', loading: replyLoading }"
      cancel-btn="取消"
      @confirm="handleReply"
    >
      <div class="space-y-4">
        <!-- 原评价信息 -->
        <div class="bg-gray-50 rounded-lg p-3">
          <div class="flex items-center gap-2 mb-2">
            <span class="font-medium text-gray-800">{{ currentEvaluation?.parentName || '匿名用户' }}</span>
            <div class="flex items-center">
              <svg 
                v-for="n in 5" 
                :key="n" 
                class="w-3 h-3"
                :class="n <= (currentEvaluation?.score || 0) ? 'text-amber-400' : 'text-gray-200'"
                fill="currentColor" 
                viewBox="0 0 24 24"
              >
                <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
              </svg>
            </div>
          </div>
          <p class="text-sm text-gray-600">{{ currentEvaluation?.content || '该用户未填写评价内容' }}</p>
        </div>

        <!-- 回复输入 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">您的回复 <span class="text-red-500">*</span></label>
          <t-textarea
            v-model="replyContent"
            placeholder="请输入您的回复内容，展示专业和诚意..."
            :maxlength="500"
            :autosize="{ minRows: 3, maxRows: 6 }"
          />
        </div>
      </div>
    </t-dialog>

    <!-- 图片预览 -->
    <t-image-viewer
      v-model:visible="previewVisible"
      :images="previewImages"
      :default-index="previewIndex"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import { getMyEvaluations, getMyRatingStats, replyEvaluation } from '@/api/careEvaluation'

const loading = ref(false)
const evaluations = ref([])
const stats = ref({
  totalCount: 0,
  avgScore: 0,
  star5Count: 0,
  star4Count: 0,
  star3Count: 0,
  star2Count: 0,
  star1Count: 0,
  positiveRate: 0
})
const currentFilter = ref('all')
const replyDialogVisible = ref(false)
const replyLoading = ref(false)
const currentEvaluation = ref(null)
const replyContent = ref('')
const previewVisible = ref(false)
const previewImages = ref([])
const previewIndex = ref(0)

// 筛选选项
const filters = [
  { label: '全部', value: 'all' },
  { label: '5星好评', value: '5' },
  { label: '4星', value: '4' },
  { label: '3星及以下', value: 'low' },
  { label: '待回复', value: 'pending' },
  { label: '已回复', value: 'replied' }
]

// 已回复数量
const repliedCount = computed(() => {
  return evaluations.value.filter(e => e.replyContent).length
})

// 筛选后的评价列表
const filteredEvaluations = computed(() => {
  let list = evaluations.value
  switch (currentFilter.value) {
    case '5':
      return list.filter(e => e.score === 5)
    case '4':
      return list.filter(e => e.score === 4)
    case 'low':
      return list.filter(e => e.score <= 3)
    case 'pending':
      return list.filter(e => !e.replyContent)
    case 'replied':
      return list.filter(e => e.replyContent)
    default:
      return list
  }
})

// 获取星级数量
const getStarCount = (star) => {
  const countKey = `star${star}Count`
  return stats.value[countKey] || 0
}

// 获取星级百分比
const getStarPercent = (star) => {
  const count = getStarCount(star)
  const total = stats.value.totalCount || 0
  if (total === 0) return 0
  return Math.round((count / total) * 100)
}

// 获取评分文字
const getRatingText = (score) => {
  const texts = ['', '非常差', '较差', '一般', '满意', '非常满意']
  return texts[score] || ''
}

// 获取姓名首字
const getInitial = (name) => {
  if (!name) return '匿'
  return name.charAt(0)
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  if (typeof dateTime === 'string') {
    return dateTime.replace('T', ' ').substring(0, 16)
  }
  return dateTime
}

// 解析图片JSON
const parseImages = (imagesStr) => {
  if (!imagesStr) return []
  try {
    const parsed = JSON.parse(imagesStr)
    return Array.isArray(parsed) ? parsed : []
  } catch (e) {
    return []
  }
}

// 获取完整图片URL
const getFullImageUrl = (img) => {
  if (!img) return ''
  if (img.startsWith('http')) return img
  if (img.startsWith('/')) {
    return (process.env.VUE_APP_API_URL || 'http://localhost:8080') + img
  }
  return img
}

// 预览图片
const previewImage = (images, index) => {
  previewImages.value = images.map(img => getFullImageUrl(img))
  previewIndex.value = index
  previewVisible.value = true
}

// 获取空状态文字
const getEmptyText = () => {
  switch (currentFilter.value) {
    case '5':
      return '暂无5星好评'
    case '4':
      return '暂无4星评价'
    case 'low':
      return '暂无3星及以下评价'
    case 'pending':
      return '没有待回复的评价'
    case 'replied':
      return '暂无已回复的评价'
    default:
      return '暂无评价记录'
  }
}

// 加载评价列表
const loadEvaluations = async () => {
  try {
    const res = await getMyEvaluations()
    if (res.code === 200) {
      evaluations.value = res.data || []
    } else {
      evaluations.value = []
    }
  } catch (error) {
    console.error('获取评价列表失败:', error)
    evaluations.value = []
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    const res = await getMyRatingStats()
    if (res.code === 200 && res.data) {
      stats.value = res.data
    }
  } catch (error) {
    console.error('获取评分统计失败:', error)
  }
}

// 加载数据
const loadData = async () => {
  loading.value = true
  await Promise.all([loadEvaluations(), loadStats()])
  loading.value = false
}

// 打开回复弹窗
const openReplyDialog = (evaluation) => {
  currentEvaluation.value = evaluation
  replyContent.value = ''
  replyDialogVisible.value = true
}

// 提交回复
const handleReply = async () => {
  if (!replyContent.value.trim()) {
    MessagePlugin.warning('请输入回复内容')
    return
  }
  
  replyLoading.value = true
  try {
    const res = await replyEvaluation(currentEvaluation.value.id, replyContent.value)
    if (res.code === 200) {
      MessagePlugin.success('回复成功')
      replyDialogVisible.value = false
      loadEvaluations()
    } else {
      MessagePlugin.error(res.message || '回复失败')
    }
  } catch (error) {
    console.error('回复失败:', error)
    MessagePlugin.error('回复失败')
  } finally {
    replyLoading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>
