<template>
  <div class="plan-detail-page">
    <div class="page-container">
      <!-- 返回导航 -->
      <div class="back-nav">
        <t-button variant="text" @click="goBack">
          <template #icon><t-icon name="chevron-left" /></template>
          返回
        </t-button>
      </div>

      <!-- 加载中 -->
      <div v-if="loading" class="loading-container">
        <t-loading size="large" text="加载中..." />
      </div>

      <template v-else-if="plan">
        <!-- 页面头部 -->
        <div class="page-header">
          <div class="header-left">
            <h1 class="page-title">{{ plan.planName }}</h1>
            <p class="page-desc">护理计划详情</p>
          </div>
          <t-tag :theme="getStatusTheme(plan.status)" size="large">
            {{ plan.statusName }}
          </t-tag>
        </div>

        <!-- 两列布局 -->
        <div class="content-layout">
          <!-- 左侧主内容 -->
          <div class="main-content">
            <!-- 基本信息卡片 -->
            <div class="info-card">
              <div class="card-header">
                <h3><t-icon name="info-circle" /> 基本信息</h3>
                <span v-if="plan.appointmentNo" class="appointment-tag">
                  <t-icon name="link" size="14" />
                  关联预约: {{ plan.appointmentNo }}
                </span>
              </div>
              <div class="info-grid">
                <div class="info-row">
                  <span class="info-label">宝宝姓名</span>
                  <span class="info-value">{{ plan.babyName }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">护理人员</span>
                  <span class="info-value">
                    {{ plan.nannyName || '待分配' }}
                    <span v-if="plan.nannyPhone" class="sub-text">({{ plan.nannyPhone }})</span>
                  </span>
                </div>
                <div class="info-row">
                  <span class="info-label">服务机构</span>
                  <span class="info-value">{{ plan.institutionName }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">创建时间</span>
                  <span class="info-value">{{ plan.createTime }}</span>
                </div>
              </div>
            </div>

            <!-- 计划内容卡片 -->
            <div class="info-card">
              <div class="card-header">
                <h3><t-icon name="file-copy" /> 计划内容</h3>
              </div>
              <div class="plan-content-text">
                {{ plan.planContent || '暂无详细内容' }}
              </div>
            </div>

            <!-- 评价卡片 -->
            <div class="info-card">
              <div class="card-header">
                <h3><t-icon name="star" /> 服务评价</h3>
              </div>
              <div v-if="evaluation" class="evaluation-content">
                <div class="eval-header">
                  <div class="rating-display">
                    <t-icon 
                      v-for="n in 5" 
                      :key="n" 
                      name="star-filled" 
                      :class="['star', { active: n <= evaluation.score }]"
                    />
                    <span class="rating-text">{{ getRatingText(evaluation.score) }}</span>
                  </div>
                  <span class="eval-time">{{ formatDateTime(evaluation.createTime) }}</span>
                </div>
                <p class="eval-text">{{ evaluation.content || '未填写评价内容' }}</p>
                <div v-if="evaluation.images && parseImages(evaluation.images).length > 0" class="eval-images">
                  <img 
                    v-for="(img, idx) in parseImages(evaluation.images)" 
                    :key="idx" 
                    :src="getFullImageUrl(img)" 
                    class="eval-thumb"
                    @click="previewImage(parseImages(evaluation.images), idx)"
                  />
                </div>
                <div v-if="evaluation.replyContent" class="eval-reply">
                  <div class="reply-header">
                    <t-icon name="chat-bubble" size="14" />
                    <span>保姆回复</span>
                    <span v-if="evaluation.replyTime" class="reply-time">{{ formatDateTime(evaluation.replyTime) }}</span>
                  </div>
                  <p class="reply-text">{{ evaluation.replyContent }}</p>
                </div>
              </div>
              <div v-else class="no-evaluation">
                <p>您还未对该服务进行评价</p>
                <t-button 
                  theme="primary" 
                  :disabled="plan.status !== 2"
                  @click="openEvaluateDialog"
                >
                  <t-icon name="star" /> 立即评价
                </t-button>
                <p v-if="plan.status !== 2" class="tip-text">请在护理计划完成后进行评价</p>
              </div>
            </div>
          </div>

        <!-- 右侧边栏 -->
        <div class="side-content">
          <!-- 进度卡片 -->
          <div class="info-card">
            <div class="card-header">
              <h3><t-icon name="chart-line" /> 执行进度</h3>
            </div>
            <div class="progress-box">
              <div class="progress-info">
                <span>已执行 {{ plan.executionDays || 0 }} 天</span>
                <span>共 {{ plan.totalDays || 0 }} 天</span>
              </div>
              <t-progress
                :percentage="plan.progressPercent || 0"
                :status="getProgressStatus(plan.status)"
                size="large"
              />
            </div>
            <div class="date-range">
              <div class="date-item">
                <span class="date-label">开始日期</span>
                <span class="date-value">{{ plan.startDate }}</span>
              </div>
              <div class="date-divider">
                <t-icon name="arrow-right" size="16" />
              </div>
              <div class="date-item">
                <span class="date-label">结束日期</span>
                <span class="date-value">{{ plan.endDate }}</span>
              </div>
            </div>
          </div>

          <!-- 状态卡片 -->
          <div class="info-card status-card">
            <div class="status-icon" :style="{ background: getStatusColor(plan.status) }">
              <t-icon name="file-paste" size="32" />
            </div>
            <div class="status-info">
              <span class="status-label">当前状态</span>
              <t-tag :theme="getStatusTheme(plan.status)" size="large">
                {{ plan.statusName }}
              </t-tag>
            </div>
          </div>
        </div>
      </div>
      </template>

      <!-- 未找到 -->
      <div v-else class="empty-container">
        <t-icon name="file-unknown" size="64" style="color: #d1d5db" />
        <p>计划不存在或无权查看</p>
        <t-button theme="primary" @click="goBack">返回列表</t-button>
      </div>
    </div>

    <!-- 评价弹窗 -->
    <t-dialog
      v-model:visible="evaluateDialogVisible"
      header="评价护理服务"
      width="560px"
      :confirm-btn="{ content: '提交评价', loading: submitLoading }"
      @confirm="submitEvaluation"
    >
      <div class="evaluate-form">
        <div class="form-item">
          <label class="form-label">评分 <span class="required">*</span></label>
          <div class="rating-input">
            <t-icon 
              v-for="n in 5" 
              :key="n" 
              name="star-filled" 
              size="32"
              :class="['star-btn', { active: n <= evaluateForm.score }]"
              @click="evaluateForm.score = n"
            />
            <span class="rating-text">{{ getRatingText(evaluateForm.score) }}</span>
          </div>
        </div>
        <div class="form-item">
          <label class="form-label">评价内容</label>
          <t-textarea 
            v-model="evaluateForm.content" 
            placeholder="请输入您对护理服务的评价（选填）"
            :maxlength="500"
            :autosize="{ minRows: 4, maxRows: 8 }"
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
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import { getCarePlanById } from '@/api/carePlan'
import { checkEvaluated, addCareEvaluation } from '@/api/careEvaluation'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const plan = ref(null)
const evaluation = ref(null)
const evaluateDialogVisible = ref(false)
const submitLoading = ref(false)
const previewVisible = ref(false)
const previewImages = ref([])
const previewIndex = ref(0)

const evaluateForm = ref({
  score: 5,
  content: ''
})

// 状态主题
const getStatusTheme = (status) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'default' }
  return map[status] || 'default'
}

// 状态颜色
const getStatusColor = (status) => {
  const map = {
    0: 'linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%)',
    1: 'linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)',
    2: 'linear-gradient(135deg, #10b981 0%, #059669 100%)',
    3: 'linear-gradient(135deg, #9ca3af 0%, #6b7280 100%)'
  }
  return map[status] || map[0]
}

// 进度条状态
const getProgressStatus = (status) => {
  if (status === 2) return 'success'
  if (status === 3) return 'error'
  return 'active'
}

// 获取详情
const fetchDetail = async () => {
  const id = route.params.id
  if (!id) return

  loading.value = true
  try {
    const res = await getCarePlanById(id)
    if (res.code === 200) {
      plan.value = res.data
    } else {
      MessagePlugin.error(res.message || '获取失败')
    }
  } catch (error) {
    console.error('获取护理计划详情失败:', error)
    MessagePlugin.error('获取详情失败')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.back()
}

// 获取评分文字
const getRatingText = (score) => {
  const texts = ['', '非常差', '较差', '一般', '满意', '非常满意']
  return texts[score] || ''
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

// 检查是否已评价
const checkPlanEvaluation = async () => {
  const id = route.params.id
  if (!id) return
  try {
    const res = await checkEvaluated(id)
    if (res.code === 200 && res.data) {
      // 已评价，获取评价详情
      fetchEvaluationDetail()
    }
  } catch (e) {
    console.error('检查评价失败:', e)
  }
}

// 获取评价详情（通过我的评价列表查找）
const fetchEvaluationDetail = async () => {
  const { getMyEvaluations } = await import('@/api/careEvaluation')
  try {
    const res = await getMyEvaluations()
    if (res.code === 200 && res.data) {
      const id = Number(route.params.id)
      const found = res.data.find(e => e.carePlanId === id)
      if (found) {
        evaluation.value = found
      }
    }
  } catch (e) {
    console.error('获取评价详情失败:', e)
  }
}

// 打开评价弹窗
const openEvaluateDialog = () => {
  evaluateForm.value = { score: 5, content: '' }
  evaluateDialogVisible.value = true
}

// 提交评价
const submitEvaluation = async () => {
  if (!evaluateForm.value.score) {
    MessagePlugin.warning('请选择评分')
    return
  }
  submitLoading.value = true
  try {
    const res = await addCareEvaluation({
      carePlanId: Number(route.params.id),
      score: evaluateForm.value.score,
      content: evaluateForm.value.content
    })
    if (res.code === 200) {
      MessagePlugin.success('评价成功')
      evaluateDialogVisible.value = false
      checkPlanEvaluation()
    } else {
      MessagePlugin.error(res.message || '评价失败')
    }
  } catch (error) {
    console.error('提交评价失败:', error)
    MessagePlugin.error('提交评价失败')
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  fetchDetail()
  checkPlanEvaluation()
})
</script>

<style scoped>
.plan-detail-page {
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

/* 加载和空状态 */
.loading-container,
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  color: #6b7280;
  gap: 16px;
}

.empty-container p {
  margin: 0;
  font-size: 16px;
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

/* 两列布局 */
.content-layout {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 24px;
}

.main-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.side-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 信息卡片 */
.info-card {
  background: white;
  border-radius: 12px;
  padding: 20px 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-header h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #374151;
}

.appointment-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  background: #eff6ff;
  color: #3b82f6;
  border-radius: 4px;
  font-size: 13px;
}

/* 信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 12px 16px;
  background: #f9fafb;
  border-radius: 8px;
}

.info-label {
  color: #6b7280;
  font-size: 14px;
}

.info-value {
  color: #1f2937;
  font-size: 14px;
  font-weight: 500;
}

.sub-text {
  color: #9ca3af;
  font-weight: normal;
}

/* 计划内容 */
.plan-content-text {
  padding: 16px;
  background: #f9fafb;
  border-radius: 8px;
  color: #374151;
  font-size: 14px;
  line-height: 1.8;
  white-space: pre-wrap;
  min-height: 100px;
}

/* 进度 */
.progress-box {
  margin-bottom: 16px;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 8px;
}

.date-range {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: #f9fafb;
  border-radius: 8px;
}

.date-item {
  text-align: center;
  flex: 1;
}

.date-divider {
  color: #d1d5db;
}

.date-label {
  display: block;
  font-size: 12px;
  color: #9ca3af;
  margin-bottom: 4px;
}

.date-value {
  font-size: 15px;
  font-weight: 500;
  color: #374151;
}

/* 状态卡片 */
.status-card {
  display: flex;
  align-items: center;
  gap: 16px;
}

.status-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.status-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.status-label {
  font-size: 13px;
  color: #6b7280;
}

/* 评价区域 */
.evaluation-content {
  padding: 16px;
  background: #f9fafb;
  border-radius: 8px;
}

.eval-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.rating-display {
  display: flex;
  align-items: center;
  gap: 4px;
}

.rating-display .star {
  font-size: 20px;
  color: #e5e7eb;
}

.rating-display .star.active {
  color: #fbbf24;
}

.rating-display .rating-text {
  margin-left: 8px;
  font-size: 14px;
  color: #6b7280;
}

.eval-time {
  font-size: 12px;
  color: #9ca3af;
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
  margin-bottom: 12px;
}

.eval-thumb {
  width: 72px;
  height: 72px;
  border-radius: 8px;
  object-fit: cover;
  cursor: pointer;
}

.eval-reply {
  padding: 12px;
  background: white;
  border-radius: 6px;
  margin-top: 12px;
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
}

.no-evaluation {
  text-align: center;
  padding: 24px 0;
}

.no-evaluation p {
  margin: 0 0 16px;
  font-size: 14px;
  color: #6b7280;
}

.no-evaluation .tip-text {
  margin: 12px 0 0;
  font-size: 12px;
  color: #9ca3af;
}

/* 评价表单 */
.evaluate-form {
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

.form-label .required {
  color: #ef4444;
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

.rating-input .rating-text {
  margin-left: 12px;
  font-size: 14px;
  color: #6b7280;
}

/* 响应式布局 */
@media (max-width: 1024px) {
  .content-layout {
    grid-template-columns: 1fr;
  }
  
  .side-content {
    order: -1;
  }
}

@media (max-width: 768px) {
  .page-container {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .info-card {
    padding: 16px;
  }
}
</style>
