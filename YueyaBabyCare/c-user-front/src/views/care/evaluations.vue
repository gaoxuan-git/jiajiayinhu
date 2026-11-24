<template>
  <div class="evaluations-page">
    <div class="page-container">
      <!-- 返回按钮 -->
      <div class="back-nav">
        <t-button variant="text" @click="$router.back()">
          <template #icon><t-icon name="chevron-left" /></template>
          返回
        </t-button>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-wrapper">
        <t-loading size="large" text="加载中..." />
      </div>

      <template v-else>
        <!-- 页面头部 -->
        <div class="page-header">
          <div class="header-left">
            <h1 class="page-title">我的评价</h1>
            <p class="page-desc">查看您对护理服务的评价记录</p>
          </div>
        </div>

        <!-- 统计卡片 -->
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-icon total-icon">
              <t-icon name="chat" size="28" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statsData.totalCount }}</div>
              <div class="stat-label">评价总数</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon good-icon">
              <t-icon name="thumb-up" size="28" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statsData.goodCount }}</div>
              <div class="stat-label">好评数量</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon replied-icon">
              <t-icon name="mail" size="28" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statsData.repliedCount }}</div>
              <div class="stat-label">已回复</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon pending-icon">
              <t-icon name="time" size="28" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statsData.pendingCount }}</div>
              <div class="stat-label">待回复</div>
            </div>
          </div>
        </div>

        <!-- 评价列表 -->
        <div class="content-card">
          <div class="card-header">
            <h3>评价列表</h3>
            <t-dropdown :options="filterOptions" @click="handleFilter">
              <t-button variant="outline" size="small">
                <template #icon><t-icon name="filter" /></template>
                {{ currentFilterLabel }}
              </t-button>
            </t-dropdown>
          </div>

          <!-- 空状态 -->
          <div v-if="filteredEvaluations.length === 0" class="empty-state">
            <t-icon name="chat-off" size="48" />
            <p>暂无评价记录</p>
            <p class="empty-tip">完成护理服务后可以进行评价</p>
          </div>

          <!-- 评价卡片列表 -->
          <div v-else class="evaluation-list">
            <div v-for="item in paginatedEvaluations" :key="item.id" class="evaluation-card">
              <div class="eval-header">
                <div class="nanny-info">
                  <img :src="item.nannyAvatar || defaultAvatar" class="nanny-avatar" alt="" />
                  <div class="nanny-detail">
                    <span class="nanny-name">{{ item.nannyName || '未知保姆' }}</span>
                    <span class="eval-time">{{ formatDateTime(item.createTime) }}</span>
                  </div>
                </div>
                <div class="rating-stars">
                  <t-icon 
                    v-for="n in 5" 
                    :key="n" 
                    name="star-filled" 
                    :class="['star', { active: n <= item.score }]"
                  />
                </div>
              </div>

              <div class="eval-content">
                <p class="eval-text">{{ item.content || '用户未填写评价内容' }}</p>
                <div v-if="item.images && parseImages(item.images).length > 0" class="eval-images">
                  <img 
                    v-for="(img, idx) in parseImages(item.images).slice(0, 4)" 
                    :key="idx" 
                    :src="getFullImageUrl(img)" 
                    class="eval-thumb"
                    @click="previewImage(parseImages(item.images), idx)"
                  />
                  <span v-if="parseImages(item.images).length > 4" class="more-images">
                    +{{ parseImages(item.images).length - 4 }}
                  </span>
                </div>
              </div>

              <!-- 保姆回复 -->
              <div v-if="item.replyContent" class="eval-reply">
                <div class="reply-header">
                  <t-icon name="chat-bubble" size="16" />
                  <span>保姆回复</span>
                  <span class="reply-time">{{ formatDateTime(item.replyTime) }}</span>
                </div>
                <p class="reply-text">{{ item.replyContent }}</p>
              </div>

              <!-- 操作按钮 -->
              <div class="eval-actions">
                <t-tag v-if="item.babyName" size="small" variant="light">
                  宝宝：{{ item.babyName }}
                </t-tag>
                <div class="action-btns">
                  <t-button 
                    v-if="!item.replyContent" 
                    variant="text" 
                    theme="primary" 
                    size="small"
                    @click="handleEdit(item)"
                  >
                    <t-icon name="edit" /> 编辑
                  </t-button>
                  <t-popconfirm 
                    content="确定要删除这条评价吗？" 
                    @confirm="handleDelete(item.id)"
                  >
                    <t-button variant="text" theme="danger" size="small">
                      <t-icon name="delete" /> 删除
                    </t-button>
                  </t-popconfirm>
                </div>
              </div>
            </div>

            <!-- 分页 -->
            <div v-if="filteredEvaluations.length > pageSize" class="pagination-wrapper">
              <t-pagination
                v-model:current="currentPage"
                :total="filteredEvaluations.length"
                :page-size="pageSize"
                show-jumper
              />
            </div>
          </div>
        </div>
      </template>
    </div>

    <!-- 编辑评价弹窗 -->
    <t-dialog
      v-model:visible="editDialogVisible"
      header="编辑评价"
      width="560px"
      :confirm-btn="{ content: '保存', loading: submitLoading }"
      @confirm="submitEdit"
    >
      <div class="edit-form">
        <div class="form-item">
          <label class="form-label">评分</label>
          <div class="rating-input">
            <t-icon 
              v-for="n in 5" 
              :key="n" 
              name="star-filled" 
              size="28"
              :class="['star-btn', { active: n <= editForm.score }]"
              @click="editForm.score = n"
            />
            <span class="rating-text">{{ ratingText }}</span>
          </div>
        </div>
        <div class="form-item">
          <label class="form-label">评价内容</label>
          <t-textarea 
            v-model="editForm.content" 
            placeholder="请输入您的评价内容（选填）"
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
import { getMyEvaluations, updateCareEvaluation, deleteCareEvaluation } from '@/api/careEvaluation'

const loading = ref(false)
const evaluations = ref([])
const currentFilter = ref('all')
const currentPage = ref(1)
const pageSize = 10
const editDialogVisible = ref(false)
const submitLoading = ref(false)
const previewVisible = ref(false)
const previewImages = ref([])
const previewIndex = ref(0)

const defaultAvatar = 'https://tdesign.gtimg.com/site/avatar.jpg'

const editForm = ref({
  id: null,
  carePlanId: null,
  score: 5,
  content: '',
  images: ''
})

const filterOptions = [
  { content: '全部评价', value: 'all' },
  { content: '好评（4-5分）', value: 'good' },
  { content: '中评（3分）', value: 'medium' },
  { content: '差评（1-2分）', value: 'bad' },
  { content: '已回复', value: 'replied' },
  { content: '未回复', value: 'pending' }
]

const currentFilterLabel = computed(() => {
  const item = filterOptions.find(o => o.value === currentFilter.value)
  return item ? item.content : '全部评价'
})

// 统计数据
const statsData = computed(() => {
  const list = evaluations.value
  return {
    totalCount: list.length,
    goodCount: list.filter(e => e.score >= 4).length,
    repliedCount: list.filter(e => e.replyContent).length,
    pendingCount: list.filter(e => !e.replyContent).length
  }
})

// 过滤后的评价
const filteredEvaluations = computed(() => {
  let list = evaluations.value
  switch (currentFilter.value) {
    case 'good':
      list = list.filter(e => e.score >= 4)
      break
    case 'medium':
      list = list.filter(e => e.score === 3)
      break
    case 'bad':
      list = list.filter(e => e.score <= 2)
      break
    case 'replied':
      list = list.filter(e => e.replyContent)
      break
    case 'pending':
      list = list.filter(e => !e.replyContent)
      break
  }
  return list
})

// 分页后的评价
const paginatedEvaluations = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredEvaluations.value.slice(start, start + pageSize)
})

// 评分文字
const ratingText = computed(() => {
  const texts = ['', '非常差', '较差', '一般', '满意', '非常满意']
  return texts[editForm.value.score] || ''
})

const handleFilter = (data) => {
  currentFilter.value = data.value
  currentPage.value = 1
}

const fetchEvaluations = async () => {
  loading.value = true
  try {
    const res = await getMyEvaluations()
    if (res.code === 200) {
      evaluations.value = res.data || []
    } else {
      MessagePlugin.error(res.message || '获取失败')
    }
  } catch (error) {
    console.error('获取评价列表失败:', error)
    MessagePlugin.error('获取评价列表失败')
  } finally {
    loading.value = false
  }
}

const handleEdit = (item) => {
  editForm.value = {
    id: item.id,
    carePlanId: item.carePlanId,
    score: item.score,
    content: item.content || '',
    images: item.images || ''
  }
  editDialogVisible.value = true
}

const submitEdit = async () => {
  if (!editForm.value.score) {
    MessagePlugin.warning('请选择评分')
    return
  }
  submitLoading.value = true
  try {
    const res = await updateCareEvaluation(editForm.value.id, {
      carePlanId: editForm.value.carePlanId,
      score: editForm.value.score,
      content: editForm.value.content,
      images: editForm.value.images
    })
    if (res.code === 200) {
      MessagePlugin.success('修改成功')
      editDialogVisible.value = false
      fetchEvaluations()
    } else {
      MessagePlugin.error(res.message || '修改失败')
    }
  } catch (error) {
    console.error('修改评价失败:', error)
    MessagePlugin.error('修改评价失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (id) => {
  try {
    const res = await deleteCareEvaluation(id)
    if (res.code === 200) {
      MessagePlugin.success('删除成功')
      fetchEvaluations()
    } else {
      MessagePlugin.error(res.message || '删除失败')
    }
  } catch (error) {
    console.error('删除评价失败:', error)
    MessagePlugin.error('删除评价失败')
  }
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return dateTime.replace('T', ' ').substring(0, 16)
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

onMounted(() => {
  fetchEvaluations()
})
</script>

<style scoped>
.evaluations-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

/* 返回导航 */
.back-nav {
  margin-bottom: 16px;
}

/* 加载状态 */
.loading-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
}

.page-desc {
  margin: 0;
  font-size: 14px;
  color: #6b7280;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.total-icon { background: linear-gradient(135deg, #3b82f6, #2563eb); }
.good-icon { background: linear-gradient(135deg, #10b981, #059669); }
.replied-icon { background: linear-gradient(135deg, #8b5cf6, #7c3aed); }
.pending-icon { background: linear-gradient(135deg, #f59e0b, #d97706); }

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
}

/* 内容卡片 */
.content-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f3f4f6;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  color: #9ca3af;
}

.empty-state p {
  margin: 16px 0 0;
  font-size: 16px;
  color: #6b7280;
}

.empty-tip {
  font-size: 14px !important;
  color: #9ca3af !important;
}

/* 评价列表 */
.evaluation-list {
  padding: 16px 24px;
}

.evaluation-card {
  padding: 20px;
  margin-bottom: 16px;
  background: #f9fafb;
  border-radius: 12px;
  transition: all 0.2s;
}

.evaluation-card:last-child {
  margin-bottom: 0;
}

.evaluation-card:hover {
  background: #f3f4f6;
}

/* 评价头部 */
.eval-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.nanny-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.nanny-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
}

.nanny-detail {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.nanny-name {
  font-size: 15px;
  font-weight: 500;
  color: #1f2937;
}

.eval-time {
  font-size: 12px;
  color: #9ca3af;
}

.rating-stars {
  display: flex;
  gap: 2px;
}

.rating-stars .star {
  font-size: 18px;
  color: #e5e7eb;
}

.rating-stars .star.active {
  color: #fbbf24;
}

/* 评价内容 */
.eval-content {
  margin-bottom: 12px;
}

.eval-text {
  margin: 0 0 12px;
  font-size: 14px;
  color: #374151;
  line-height: 1.6;
}

.eval-images {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  align-items: center;
}

.eval-thumb {
  width: 72px;
  height: 72px;
  border-radius: 8px;
  object-fit: cover;
  cursor: pointer;
  transition: transform 0.2s;
}

.eval-thumb:hover {
  transform: scale(1.05);
}

.more-images {
  font-size: 12px;
  color: #6b7280;
  padding: 6px 12px;
  background: #e5e7eb;
  border-radius: 6px;
}

/* 保姆回复 */
.eval-reply {
  padding: 12px 16px;
  background: white;
  border-radius: 8px;
  margin-bottom: 12px;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 8px;
}

.reply-time {
  margin-left: auto;
  font-size: 12px;
  color: #9ca3af;
}

.reply-text {
  margin: 0;
  font-size: 14px;
  color: #374151;
  line-height: 1.6;
}

/* 操作按钮 */
.eval-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #e5e7eb;
}

.action-btns {
  display: flex;
  gap: 8px;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px 0 4px;
}

/* 编辑表单 */
.edit-form {
  padding: 8px 0;
}

.form-item {
  margin-bottom: 20px;
}

.form-item:last-child {
  margin-bottom: 0;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 8px;
}

.rating-input {
  display: flex;
  align-items: center;
  gap: 4px;
}

.star-btn {
  cursor: pointer;
  color: #e5e7eb;
  transition: all 0.2s;
}

.star-btn:hover,
.star-btn.active {
  color: #fbbf24;
  transform: scale(1.1);
}

.rating-text {
  margin-left: 12px;
  font-size: 14px;
  color: #6b7280;
}

/* 响应式 */
@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .page-container {
    padding: 16px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .stat-card {
    padding: 16px;
  }
  
  .stat-icon {
    width: 48px;
    height: 48px;
  }
  
  .stat-value {
    font-size: 24px;
  }

  .eval-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .eval-actions {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
}
</style>
