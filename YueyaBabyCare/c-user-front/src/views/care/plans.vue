<template>
  <div class="care-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1>护理计划</h1>
      <t-dropdown :options="statusOptions" @click="handleStatusFilter">
        <t-button variant="outline" size="large">
          <t-icon name="filter" /> {{ currentStatusLabel }}
        </t-button>
      </t-dropdown>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="loading-container">
      <t-loading size="large" text="加载中..." />
    </div>

    <!-- 空状态 -->
    <div v-else-if="plans.length === 0" class="empty-container">
      <t-icon name="file" size="64" style="color: #d1d5db" />
      <p>暂无护理计划</p>
      <p class="empty-tip">机构确认预约后会为您制定护理计划</p>
    </div>

    <!-- 计划列表 -->
    <div v-else class="plans-grid">
      <div 
        v-for="plan in plans" 
        :key="plan.id" 
        class="plan-card"
        @click="goToDetail(plan.id)"
      >
        <div class="card-header">
          <div class="plan-icon" :style="{ background: getStatusColor(plan.status) }">
            <t-icon name="file-paste" size="32" />
          </div>
          <t-tag :theme="getStatusTheme(plan.status)" size="large">
            {{ plan.statusName }}
          </t-tag>
        </div>
        <h3 class="plan-title">{{ plan.planName }}</h3>
        <p class="plan-desc">{{ plan.planContent }}</p>
        
        <!-- 关联预约 -->
        <div v-if="plan.appointmentNo" class="appointment-tag">
          <t-icon name="link" size="14" />
          <span>关联预约: {{ plan.appointmentNo }}</span>
        </div>

        <div class="plan-info">
          <div class="info-item">
            <t-icon name="user" size="16" />
            <span>宝宝: {{ plan.babyName }}</span>
          </div>
          <div class="info-item">
            <t-icon name="user-avatar" size="16" />
            <span>护理人员: {{ plan.nannyName || '待分配' }}</span>
          </div>
          <div class="info-item">
            <t-icon name="time" size="16" />
            <span>{{ plan.startDate }} ~ {{ plan.endDate }}</span>
          </div>
        </div>

        <!-- 进度条 -->
        <div class="progress-section">
          <div class="progress-header">
            <span>执行进度</span>
            <span>{{ plan.executionDays || 0 }}/{{ plan.totalDays || 0 }}天</span>
          </div>
          <t-progress 
            :percentage="plan.progressPercent || 0" 
            :status="getProgressStatus(plan.status)"
            size="small"
          />
        </div>

        <div class="card-footer">
          <t-button variant="outline" block size="large">
            <t-icon name="browse" /> 查看详情
          </t-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import { getCarePlanList } from '@/api/carePlan'

const router = useRouter()
const loading = ref(false)
const plans = ref([])
const currentStatus = ref(null)

const statusOptions = [
  { content: '全部', value: null },
  { content: '待执行', value: 0 },
  { content: '进行中', value: 1 },
  { content: '已完成', value: 2 },
  { content: '已取消', value: 3 }
]

const currentStatusLabel = computed(() => {
  const item = statusOptions.find(o => o.value === currentStatus.value)
  return item ? item.content : '全部'
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

// 获取列表
const fetchList = async () => {
  loading.value = true
  try {
    const res = await getCarePlanList({ status: currentStatus.value })
    if (res.code === 200) {
      plans.value = res.data || []
    } else {
      MessagePlugin.error(res.message || '获取失败')
    }
  } catch (error) {
    console.error('获取护理计划失败:', error)
    MessagePlugin.error('获取护理计划失败')
  } finally {
    loading.value = false
  }
}

// 状态筛选
const handleStatusFilter = (data) => {
  currentStatus.value = data.value
  fetchList()
}

// 跳转详情
const goToDetail = (id) => {
  router.push(`/care-plan/${id}`)
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.care-page {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.page-header h1 {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  color: #1F2937;
}

.loading-container,
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  color: #6b7280;
}

.empty-container p {
  margin: 16px 0 0;
  font-size: 16px;
}

.empty-tip {
  font-size: 14px !important;
  color: #9ca3af !important;
}

/* 计划网格 */
.plans-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 24px;
}

.plan-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
  border: 2px solid transparent;
  cursor: pointer;
}

.plan-card:hover {
  border-color: #3B82F6;
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.12);
  transform: translateY(-4px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.plan-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.plan-title {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #1F2937;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.plan-desc {
  margin: 0 0 12px 0;
  color: #6B7280;
  font-size: 14px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 42px;
}

.appointment-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  background: #eff6ff;
  color: #3b82f6;
  border-radius: 4px;
  font-size: 12px;
  margin-bottom: 12px;
}

.plan-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 14px;
  background: #F9FAFB;
  border-radius: 8px;
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #4B5563;
  font-size: 13px;
}

.progress-section {
  margin-bottom: 16px;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 6px;
}

.card-footer {
  padding-top: 16px;
  border-top: 1px solid #F3F4F6;
}
</style>
