<template>
  <div class="appointment-detail-page">
    <t-loading :loading="loading">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-left">
          <t-button theme="default" variant="text" @click="goBack">
            <template #icon><t-icon name="chevron-left" /></template>
            返回
          </t-button>
          <h1 class="page-title">预约详情</h1>
        </div>
        <div class="header-actions" v-if="appointment.status === 0 || appointment.status === 1">
          <t-button theme="danger" variant="outline" @click="handleCancel">
            取消预约
          </t-button>
        </div>
      </div>

      <div v-if="appointment.id" class="detail-content">
        <!-- 左侧主要信息 -->
        <div class="main-section">
          <!-- 状态卡片 -->
          <div class="status-card" :class="getStatusCardClass(appointment.status)">
            <div class="status-icon">
              <t-icon :name="getStatusIcon(appointment.status)" size="32px" />
            </div>
            <div class="status-info">
              <h2 class="status-title">{{ getStatusText(appointment.status) }}</h2>
              <p class="status-desc">{{ getStatusDesc(appointment.status) }}</p>
            </div>
          </div>

          <!-- 预约信息卡片 -->
          <div class="info-card">
            <div class="card-header">
              <t-icon name="calendar" />
              <h3>预约信息</h3>
            </div>
            <div class="card-body">
              <div class="info-grid">
                <div class="info-item">
                  <span class="label">预约编号</span>
                  <span class="value">{{ appointment.id }}</span>
                </div>
                <div class="info-item">
                  <span class="label">服务类型</span>
                  <span class="value">{{ getServiceTypeText(appointment.serviceType) }}</span>
                </div>
                <div class="info-item">
                  <span class="label">预约日期</span>
                  <span class="value highlight">{{ appointment.appointmentDate }}</span>
                </div>
                <div class="info-item">
                  <span class="label">服务时段</span>
                  <span class="value">{{ appointment.timeSlot }}</span>
                </div>
                <div class="info-item">
                  <span class="label">创建时间</span>
                  <span class="value">{{ formatTime(appointment.createTime) }}</span>
                </div>
                <div class="info-item" v-if="appointment.updateTime">
                  <span class="label">更新时间</span>
                  <span class="value">{{ formatTime(appointment.updateTime) }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 服务地址卡片 -->
          <div class="info-card" v-if="appointment.address">
            <div class="card-header">
              <t-icon name="location" />
              <h3>服务地址</h3>
            </div>
            <div class="card-body">
              <p class="address-text">{{ appointment.address }}</p>
            </div>
          </div>

          <!-- 备注信息卡片 -->
          <div class="info-card" v-if="appointment.remark">
            <div class="card-header">
              <t-icon name="file" />
              <h3>备注信息</h3>
            </div>
            <div class="card-body">
              <p class="remark-text">{{ appointment.remark }}</p>
            </div>
          </div>

          <!-- 取消原因卡片 -->
          <div class="info-card warning" v-if="appointment.status === 4 && appointment.cancelReason">
            <div class="card-header">
              <t-icon name="close-circle" />
              <h3>取消原因</h3>
            </div>
            <div class="card-body">
              <p class="cancel-reason">{{ appointment.cancelReason }}</p>
            </div>
          </div>
        </div>

        <!-- 右侧信息 -->
        <div class="side-section">
          <!-- 机构信息卡片 -->
          <div class="info-card">
            <div class="card-header">
              <t-icon name="building" />
              <h3>服务机构</h3>
            </div>
            <div class="card-body">
              <div class="entity-info">
                <div class="entity-avatar">
                  <t-icon name="building" size="32px" />
                </div>
                <div class="entity-detail">
                  <h4 class="entity-name">{{ appointment.institutionName || '待分配' }}</h4>
                  <p class="entity-contact" v-if="appointment.institutionPhone">
                    <t-icon name="call" size="14px" />
                    {{ appointment.institutionPhone }}
                  </p>
                </div>
              </div>
            </div>
          </div>

          <!-- 护理师信息卡片 -->
          <div class="info-card">
            <div class="card-header">
              <t-icon name="user" />
              <h3>护理师</h3>
            </div>
            <div class="card-body">
              <div v-if="appointment.nannyId" class="entity-info">
                <t-avatar size="48px" :image="appointment.nannyAvatar">
                  {{ appointment.nannyName?.substring(0, 1) }}
                </t-avatar>
                <div class="entity-detail">
                  <h4 class="entity-name">{{ appointment.nannyName }}</h4>
                  <p class="entity-meta" v-if="appointment.nannyWorkYears">
                    工作{{ appointment.nannyWorkYears }}年
                  </p>
                  <p class="entity-contact" v-if="appointment.nannyPhone">
                    <t-icon name="call" size="14px" />
                    {{ appointment.nannyPhone }}
                  </p>
                </div>
              </div>
              <div v-else class="empty-entity">
                <t-icon name="user" size="32px" class="empty-icon" />
                <p>护理师待分配</p>
              </div>
              <t-button
                v-if="appointment.nannyPhone && (appointment.status === 1 || appointment.status === 2)"
                theme="primary"
                variant="outline"
                block
                class="contact-btn"
                @click="contactNanny"
              >
                <template #icon><t-icon name="call" /></template>
                联系护理师
              </t-button>
            </div>
          </div>
        </div>
      </div>
    </t-loading>

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
import { useRouter, useRoute } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import { getAppointmentDetail, cancelAppointment } from '@/api/appointment'

const router = useRouter()
const route = useRoute()

const appointment = ref({})
const loading = ref(false)

const cancelDialogVisible = ref(false)
const cancelLoading = ref(false)
const cancelReason = ref('')

const fetchAppointmentDetail = async () => {
  const id = route.params.id
  if (!id) {
    MessagePlugin.error('预约ID不存在')
    router.back()
    return
  }

  loading.value = true
  try {
    const res = await getAppointmentDetail(id)
    appointment.value = res.data || {}
  } catch (error) {
    console.error('获取预约详情失败:', error)
    MessagePlugin.error('获取预约详情失败')
  } finally {
    loading.value = false
  }
}

const getStatusText = (status) => {
  const map = { 0: '待确认', 1: '已确认', 2: '服务中', 3: '已完成', 4: '已取消' }
  return map[status] || '未知状态'
}

const getStatusDesc = (status) => {
  const map = {
    0: '等待机构确认您的预约',
    1: '预约已确认，请按时等待服务',
    2: '护理师正在为您提供服务',
    3: '服务已完成，感谢您的信任',
    4: '预约已取消'
  }
  return map[status] || ''
}

const getStatusIcon = (status) => {
  const map = { 0: 'time', 1: 'check-circle', 2: 'play-circle', 3: 'check-circle-filled', 4: 'close-circle' }
  return map[status] || 'info-circle'
}

const getStatusCardClass = (status) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'default', 4: 'danger' }
  return map[status] || 'default'
}

const getServiceTypeText = (type) => {
  const map = { 1: '全日护理', 2: '半日护理', 3: '小时护理', 4: '夜间护理' }
  return map[type] || '其他服务'
}

const formatTime = (time) => {
  if (!time) return ''
  return time.substring(0, 16).replace('T', ' ')
}

const goBack = () => router.back()

const handleCancel = () => {
  cancelReason.value = ''
  cancelDialogVisible.value = true
}

const confirmCancel = async () => {
  cancelLoading.value = true
  try {
    await cancelAppointment(appointment.value.id, cancelReason.value)
    MessagePlugin.success('预约已取消')
    cancelDialogVisible.value = false
    fetchAppointmentDetail()
  } catch (error) {
    console.error('取消预约失败:', error)
    MessagePlugin.error('取消预约失败')
  } finally {
    cancelLoading.value = false
  }
}

const contactNanny = () => {
  if (appointment.value.nannyPhone) {
    window.location.href = `tel:${appointment.value.nannyPhone}`
  }
}

onMounted(() => fetchAppointmentDetail())
</script>

<style scoped>
.appointment-detail-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.detail-content {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 24px;
}

.main-section, .side-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 状态卡片 */
.status-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px;
  border-radius: 12px;
  background: white;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.status-card.warning { background: linear-gradient(135deg, #FEF3C7 0%, #FDE68A 100%); }
.status-card.warning .status-icon { background: #F59E0B; color: white; }
.status-card.primary { background: linear-gradient(135deg, #DBEAFE 0%, #BFDBFE 100%); }
.status-card.primary .status-icon { background: #3B82F6; color: white; }
.status-card.success { background: linear-gradient(135deg, #D1FAE5 0%, #A7F3D0 100%); }
.status-card.success .status-icon { background: #10B981; color: white; }
.status-card.danger { background: linear-gradient(135deg, #FEE2E2 0%, #FECACA 100%); }
.status-card.danger .status-icon { background: #EF4444; color: white; }
.status-card.default { background: linear-gradient(135deg, #F3F4F6 0%, #E5E7EB 100%); }
.status-card.default .status-icon { background: #6B7280; color: white; }

.status-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.status-title {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 4px 0;
}

.status-desc {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

/* 信息卡片 */
.info-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  overflow: hidden;
}

.info-card.warning {
  border-left: 4px solid #EF4444;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 20px;
  border-bottom: 1px solid #f3f4f6;
  background: #fafafa;
}

.card-header h3 {
  font-size: 15px;
  font-weight: 600;
  color: #374151;
  margin: 0;
}

.card-header .t-icon { color: #3b82f6; }

.card-body { padding: 20px; }

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item .label {
  font-size: 12px;
  color: #9ca3af;
}

.info-item .value {
  font-size: 14px;
  color: #374151;
  font-weight: 500;
}

.info-item .value.highlight {
  color: #3b82f6;
  font-weight: 600;
}

.address-text, .remark-text, .cancel-reason {
  font-size: 14px;
  color: #4b5563;
  line-height: 1.6;
  margin: 0;
}

.cancel-reason { color: #dc2626; }

/* 实体信息 */
.entity-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.entity-avatar {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: #eff6ff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #3b82f6;
  flex-shrink: 0;
}

.entity-name {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 4px 0;
}

.entity-meta {
  font-size: 13px;
  color: #6b7280;
  margin: 0 0 4px 0;
}

.entity-contact {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #6b7280;
  margin: 0;
}

.empty-entity {
  text-align: center;
  padding: 20px;
  color: #9ca3af;
}

.empty-icon { color: #d1d5db; margin-bottom: 8px; }

.contact-btn { margin-top: 16px; }

.cancel-dialog-content { padding: 8px 0; }
.cancel-warning { color: #6b7280; margin: 0 0 16px 0; }

@media (max-width: 900px) {
  .detail-content {
    grid-template-columns: 1fr;
  }
}
</style>
