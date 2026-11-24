<template>
  <div class="appointments-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">我的预约</h1>
        <p class="page-subtitle">查看和管理您的护理服务预约</p>
      </div>
      <t-button theme="primary" size="large" @click="goToInstitutions">
        <template #icon><t-icon name="add" /></template>
        新建预约
      </t-button>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-card">
      <div class="filter-tabs">
        <button
          v-for="tab in statusTabs"
          :key="tab.value"
          :class="['filter-tab', { active: currentStatus === tab.value }]"
          @click="handleStatusChange(tab.value)"
        >
          {{ tab.label }}
          <span v-if="tab.count !== undefined" class="tab-count">{{ tab.count }}</span>
        </button>
      </div>
    </div>

    <!-- 预约列表 -->
    <div class="appointments-content">
      <t-loading :loading="loading">
        <!-- 有数据 -->
        <div v-if="appointments.length > 0" class="appointments-grid">
          <div
            v-for="appointment in appointments"
            :key="appointment.id"
            class="appointment-card"
          >
            <!-- 卡片头部 -->
            <div class="card-header">
              <div class="order-info">
                <span class="order-label">预约编号</span>
                <span class="order-id">{{ appointment.id }}</span>
              </div>
              <t-tag :theme="getStatusTheme(appointment.status)" variant="light">
                {{ getStatusText(appointment.status) }}
              </t-tag>
            </div>

            <!-- 卡片内容 -->
            <div class="card-body">
              <!-- 机构信息 -->
              <div class="info-row">
                <div class="info-icon">
                  <t-icon name="building" />
                </div>
                <div class="info-content">
                  <div class="info-label">服务机构</div>
                  <div class="info-value">{{ appointment.institutionName || '待分配' }}</div>
                </div>
              </div>

              <!-- 护理师信息 -->
              <div class="info-row">
                <div class="info-icon">
                  <t-icon name="user" />
                </div>
                <div class="info-content">
                  <div class="info-label">护理师</div>
                  <div class="info-value">{{ appointment.nannyName || '待分配' }}</div>
                </div>
              </div>

              <!-- 服务类型 -->
              <div class="info-row">
                <div class="info-icon">
                  <t-icon name="service" />
                </div>
                <div class="info-content">
                  <div class="info-label">服务类型</div>
                  <div class="info-value">{{ getServiceTypeText(appointment.serviceType) }}</div>
                </div>
              </div>

              <!-- 预约时间 -->
              <div class="info-row highlight">
                <div class="info-icon">
                  <t-icon name="calendar" />
                </div>
                <div class="info-content">
                  <div class="info-label">预约时间</div>
                  <div class="info-value primary">{{ appointment.appointmentDate }} {{ appointment.timeSlot }}</div>
                </div>
              </div>
            </div>

            <!-- 卡片底部 -->
            <div class="card-footer">
              <span class="create-time">创建于 {{ formatTime(appointment.createTime) }}</span>
              <div class="card-actions">
                <t-button
                  v-if="appointment.status === 0 || appointment.status === 1"
                  theme="danger"
                  variant="text"
                  size="small"
                  @click="handleCancel(appointment)"
                >
                  取消预约
                </t-button>
                <t-button
                  theme="primary"
                  variant="text"
                  size="small"
                  @click="goToDetail(appointment.id)"
                >
                  查看详情
                </t-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else-if="!loading" class="empty-state">
          <t-icon name="calendar" size="64px" class="empty-icon" />
          <p class="empty-text">暂无预约记录</p>
          <p class="empty-hint">前往机构列表选择心仪的护理服务</p>
          <t-button theme="primary" @click="goToInstitutions">
            立即预约
          </t-button>
        </div>
      </t-loading>
    </div>

    <!-- 分页 -->
    <div v-if="total > pageSize" class="pagination-wrapper">
      <t-pagination
        v-model="currentPage"
        :total="total"
        :page-size="pageSize"
        @change="handlePageChange"
      />
    </div>

    <!-- 取消预约弹窗 -->
    <t-dialog
      v-model:visible="cancelDialogVisible"
      header="取消预约"
      :confirm-btn="{ content: '确认取消', theme: 'danger', loading: cancelLoading }"
      @confirm="confirmCancel"
    >
      <div class="cancel-dialog-content">
        <p class="cancel-warning">确定要取消这个预约吗？取消后无法恢复。</p>
        <t-textarea
          v-model="cancelReason"
          placeholder="请输入取消原因（选填）"
          :maxlength="200"
          :autosize="{ minRows: 3, maxRows: 5 }"
        />
      </div>
    </t-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import { getMyAppointments, cancelAppointment } from '@/api/appointment'

const router = useRouter()

// 状态筛选标签
const statusTabs = [
  { label: '全部', value: null },
  { label: '待确认', value: 0 },
  { label: '已确认', value: 1 },
  { label: '进行中', value: 2 },
  { label: '已完成', value: 3 },
  { label: '已取消', value: 4 }
]

// 当前筛选状态
const currentStatus = ref(null)

// 预约列表
const appointments = ref([])
const loading = ref(false)

// 分页
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

// 取消预约相关
const cancelDialogVisible = ref(false)
const cancelLoading = ref(false)
const cancelReason = ref('')
const currentAppointment = ref(null)

// 获取预约列表
const fetchAppointments = async () => {
  loading.value = true
  try {
    const res = await getMyAppointments(currentStatus.value)
    appointments.value = res.data || []
    total.value = appointments.value.length
  } catch (error) {
    console.error('获取预约列表失败:', error)
    MessagePlugin.error('获取预约列表失败')
  } finally {
    loading.value = false
  }
}

// 状态筛选变化
const handleStatusChange = (status) => {
  currentStatus.value = status
  currentPage.value = 1
  fetchAppointments()
}

// 分页变化
const handlePageChange = () => {
  fetchAppointments()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '待确认',
    1: '已确认',
    2: '进行中',
    3: '已完成',
    4: '已取消'
  }
  return statusMap[status] || '未知'
}

// 获取状态主题
const getStatusTheme = (status) => {
  const themeMap = {
    0: 'warning',
    1: 'primary',
    2: 'success',
    3: 'default',
    4: 'danger'
  }
  return themeMap[status] || 'default'
}

// 获取服务类型文本
const getServiceTypeText = (type) => {
  const typeMap = {
    1: '全日护理',
    2: '半日护理',
    3: '小时护理',
    4: '夜间护理'
  }
  return typeMap[type] || '其他服务'
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return time.substring(0, 16).replace('T', ' ')
}

// 跳转详情
const goToDetail = (id) => {
  router.push(`/appointment/${id}`)
}

// 跳转机构列表
const goToInstitutions = () => {
  router.push('/institutions')
}

// 打开取消弹窗
const handleCancel = (appointment) => {
  currentAppointment.value = appointment
  cancelReason.value = ''
  cancelDialogVisible.value = true
}

// 确认取消
const confirmCancel = async () => {
  if (!currentAppointment.value) return

  cancelLoading.value = true
  try {
    await cancelAppointment(currentAppointment.value.id, cancelReason.value)
    MessagePlugin.success('预约已取消')
    cancelDialogVisible.value = false
    fetchAppointments()
  } catch (error) {
    console.error('取消预约失败:', error)
    MessagePlugin.error('取消预约失败')
  } finally {
    cancelLoading.value = false
  }
}

onMounted(() => {
  fetchAppointments()
})
</script>

<style scoped>
.appointments-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.header-left {
  flex: 1;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

/* 筛选区域 */
.filter-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 16px 24px;
  margin-bottom: 24px;
}

.filter-tabs {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.filter-tab {
  padding: 8px 20px;
  border: none;
  background: #f3f4f6;
  border-radius: 20px;
  font-size: 14px;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.filter-tab:hover {
  background: #e5e7eb;
}

.filter-tab.active {
  background: #3b82f6;
  color: white;
}

.tab-count {
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
}

.filter-tab.active .tab-count {
  background: rgba(255, 255, 255, 0.3);
}

/* 预约列表 */
.appointments-content {
  min-height: 400px;
}

.appointments-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 20px;
}

/* 预约卡片 */
.appointment-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s;
}

.appointment-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f3f4f6;
  background: #fafafa;
}

.order-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.order-label {
  font-size: 13px;
  color: #9ca3af;
}

.order-id {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}

.card-body {
  padding: 20px;
}

.info-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 16px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6b7280;
  flex-shrink: 0;
}

.info-row.highlight .info-icon {
  background: #eff6ff;
  color: #3b82f6;
}

.info-content {
  flex: 1;
}

.info-label {
  font-size: 12px;
  color: #9ca3af;
  margin-bottom: 4px;
}

.info-value {
  font-size: 14px;
  color: #374151;
  font-weight: 500;
}

.info-value.primary {
  color: #3b82f6;
  font-weight: 600;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  border-top: 1px solid #f3f4f6;
  background: #fafafa;
}

.create-time {
  font-size: 12px;
  color: #9ca3af;
}

.card-actions {
  display: flex;
  gap: 8px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  color: #d1d5db;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 18px;
  color: #6b7280;
  margin: 0 0 8px 0;
}

.empty-hint {
  font-size: 14px;
  color: #9ca3af;
  margin: 0 0 24px 0;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #f3f4f6;
}

/* 取消弹窗 */
.cancel-dialog-content {
  padding: 8px 0;
}

.cancel-warning {
  color: #6b7280;
  margin: 0 0 16px 0;
}

/* 响应式 */
@media (max-width: 768px) {
  .appointments-page {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    gap: 16px;
  }

  .page-title {
    font-size: 24px;
  }

  .appointments-grid {
    grid-template-columns: 1fr;
  }

  .filter-tabs {
    overflow-x: auto;
    flex-wrap: nowrap;
    padding-bottom: 8px;
  }
}
</style>
