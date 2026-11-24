<template>
  <div class="institution-detail-page">
    <t-loading :loading="loading">
      <!-- 机构头部信息 -->
      <div v-if="institution" class="institution-header">
        <div class="header-content">
          <!-- Logo -->
          <div class="logo-section">
            <img
              v-if="institution.logo"
              :src="institution.logo"
              :alt="institution.name"
              class="logo"
            />
            <div v-else class="logo-placeholder">
              <t-icon name="building" size="64px" />
            </div>
          </div>

          <!-- 基本信息 -->
          <div class="info-section">
            <div class="name-row">
              <h1 class="institution-name">{{ institution.name }}</h1>
              <t-button theme="primary" size="large" @click="openAppointmentDialog()">
                <template #icon><t-icon name="calendar" /></template>
                立即预约
              </t-button>
            </div>

            <div class="contact-grid">
              <div class="contact-item">
                <t-icon name="user" class="icon" />
                <div>
                  <div class="label">联系人</div>
                  <div class="value">{{ institution.contactName }}</div>
                </div>
              </div>

              <div class="contact-item">
                <t-icon name="call" class="icon" />
                <div>
                  <div class="label">电话</div>
                  <div class="value">{{ institution.contactPhone }}</div>
                </div>
              </div>

              <div v-if="institution.email" class="contact-item">
                <t-icon name="mail" class="icon" />
                <div>
                  <div class="label">邮箱</div>
                  <div class="value">{{ institution.email }}</div>
                </div>
              </div>

              <div v-if="institution.licenseNo" class="contact-item">
                <t-icon name="file-text" class="icon" />
                <div>
                  <div class="label">营业执照号</div>
                  <div class="value">{{ institution.licenseNo }}</div>
                </div>
              </div>
            </div>

            <!-- 统计数据 -->
            <div class="stats-row">
              <div class="stat-card stat-card-primary">
                <div class="stat-icon">
                  <t-icon name="user-avatar" size="24px" />
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ stats.staffCount }}</div>
                  <div class="stat-label">保姆数量</div>
                </div>
              </div>
              <div class="stat-card stat-card-success">
                <div class="stat-icon">
                  <t-icon name="chart-line" size="24px" />
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ stats.serviceCount }}</div>
                  <div class="stat-label">服务订单</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 机构简介 -->
        <div v-if="institution.introduction" class="introduction-section">
          <h3 class="section-title">机构简介</h3>
          <p class="introduction-text">{{ institution.introduction }}</p>
        </div>

        <!-- 营业执照 -->
        <div v-if="institution.licenseImg" class="license-section">
          <h3 class="section-title">营业执照</h3>
          <img
            :src="institution.licenseImg"
            alt="营业执照"
            class="license-img"
            @click="previewLicense"
          />
        </div>
      </div>

      <!-- 保姆列表 -->
      <div class="nannies-section">
        <div class="section-header">
          <h2 class="section-title">机构保姆</h2>
          <span class="total-count">共 {{ nannyTotal }} 名</span>
        </div>

        <t-loading :loading="nannyLoading">
          <div v-if="nannies.length > 0" class="nannies-grid">
            <div
              v-for="nanny in nannies"
              :key="nanny.id"
              class="nanny-card"
            >
              <!-- 头像 -->
              <div class="nanny-avatar" @click="viewNannyDetail(nanny.id)">
                <img
                  v-if="nanny.avatar"
                  :src="nanny.avatar"
                  :alt="nanny.realName"
                />
                <t-icon v-else name="user" size="48px" />
              </div>

              <!-- 信息 -->
              <div class="nanny-info">
                <div class="nanny-name" @click="viewNannyDetail(nanny.id)">{{ nanny.realName }}</div>
                <div class="nanny-meta">
                  <span v-if="nanny.workYears" class="meta-item">
                    <t-icon name="time" />
                    {{ nanny.workYears }}年经验
                  </span>
                  <span v-if="nanny.gender !== undefined" class="meta-item">
                    <t-icon name="user" />
                    {{ nanny.gender === 1 ? '男' : nanny.gender === 2 ? '女' : '未知' }}
                  </span>
                </div>

                <p v-if="nanny.introduction" class="nanny-intro">
                  {{ nanny.introduction }}
                </p>

                <!-- 操作按钮 -->
                <div class="nanny-actions">
                  <t-tag v-if="nanny.status === 1" theme="success" variant="light">
                    可预约
                  </t-tag>
                  <t-button
                    v-if="nanny.status === 1"
                    theme="primary"
                    size="small"
                    @click="openAppointmentDialog(nanny)"
                  >
                    预约TA
                  </t-button>
                </div>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-else-if="!nannyLoading" class="empty-state">
            <t-icon name="user-clear" size="64px" style="color: #ddd" />
            <p>暂无保姆信息</p>
          </div>
        </t-loading>

        <!-- 分页 -->
        <div v-if="nannyTotal > 0" class="pagination-wrapper">
          <t-pagination
            v-model="nannyPage"
            v-model:page-size="nannyPageSize"
            :total="nannyTotal"
            @change="handleNannyPageChange"
          />
        </div>
      </div>
    </t-loading>

    <!-- 营业执照预览弹窗 -->
    <t-image-viewer
      v-model:visible="licenseVisible"
      :images="[institution?.licenseImg]"
    />

    <!-- 预约弹窗 -->
    <t-dialog
      v-model:visible="appointmentDialogVisible"
      header="预约服务"
      :footer="false"
      width="600px"
      destroy-on-close
    >
      <div class="appointment-form">
        <!-- 选中的保姆信息 -->
        <div v-if="selectedNanny" class="selected-nanny">
          <t-avatar size="48px" :image="selectedNanny.avatar">
            {{ selectedNanny.realName?.substring(0, 1) }}
          </t-avatar>
          <div class="nanny-detail">
            <div class="name">{{ selectedNanny.realName }}</div>
            <div class="meta">工作{{ selectedNanny.workYears || 0 }}年</div>
          </div>
          <t-button variant="text" theme="default" size="small" @click="clearSelectedNanny">
            更换
          </t-button>
        </div>
        <div v-else class="no-nanny-hint">
          <t-icon name="info-circle" />
          <span>未指定护理师，将由机构分配</span>
        </div>

        <!-- 选择宝宝 -->
        <div class="form-section">
          <div class="form-label required">选择宝宝</div>
          <t-select
            v-model="appointmentForm.babyId"
            placeholder="请选择需要护理的宝宝"
            :loading="babiesLoading"
            style="width: 100%"
          >
            <t-option
              v-for="baby in babies"
              :key="baby.id"
              :value="baby.id"
              :label="baby.name"
            >
              <div class="baby-option">
                <span class="baby-name">{{ baby.name }}</span>
                <span class="baby-age">{{ baby.age || '未知年龄' }}</span>
              </div>
            </t-option>
          </t-select>
          <div v-if="babies.length === 0 && !babiesLoading" class="no-baby-hint">
            <t-icon name="info-circle" />
            <span>暂无宝宝信息，请先添加宝宝</span>
            <t-button variant="text" theme="primary" size="small" @click="goToAddBaby">去添加</t-button>
          </div>
        </div>

        <!-- 服务类型 -->
        <div class="form-section">
          <div class="form-label">服务类型</div>
          <div class="service-types">
            <div
              v-for="type in serviceTypes"
              :key="type.value"
              :class="['service-type-item', { active: appointmentForm.serviceType === type.value }]"
              @click="appointmentForm.serviceType = type.value"
            >
              <t-icon :name="type.icon" size="24px" />
              <span>{{ type.label }}</span>
            </div>
          </div>
        </div>

        <!-- 预约日期 -->
        <div class="form-section">
          <div class="form-label required">预约日期</div>
          <t-date-picker
            v-model="appointmentForm.appointmentDate"
            placeholder="请选择预约日期"
            format="YYYY-MM-DD"
            :disable-date="disablePastDate"
            style="width: 100%"
          />
        </div>

        <!-- 服务时间 -->
        <div class="form-section">
          <div class="form-label required">服务时间</div>
          <div class="time-picker-row">
            <t-time-picker
              v-model="appointmentForm.startTime"
              placeholder="开始时间"
              format="HH:mm"
              :steps="[1, 30, 1]"
              style="flex: 1"
            />
            <span class="time-separator">至</span>
            <t-time-picker
              v-model="appointmentForm.endTime"
              placeholder="结束时间"
              format="HH:mm"
              :steps="[1, 30, 1]"
              style="flex: 1"
            />
          </div>
        </div>

        <!-- 服务地址 -->
        <div class="form-section">
          <div class="form-label required">服务地址</div>
          <t-select
            v-model="appointmentForm.addressId"
            placeholder="请选择服务地址"
            :loading="addressesLoading"
            style="width: 100%"
          >
            <t-option
              v-for="addr in addresses"
              :key="addr.id"
              :value="addr.id"
              :label="formatAddress(addr)"
            >
              <div class="address-option">
                <div class="address-main">
                  <span class="address-text">{{ formatAddress(addr) }}</span>
                  <t-tag v-if="addr.isDefault === 1" theme="primary" size="small" variant="light">默认</t-tag>
                </div>
                <div class="address-contact">
                  {{ addr.contactName }} {{ addr.contactPhone }}
                </div>
              </div>
            </t-option>
          </t-select>
          <div v-if="addresses.length === 0 && !addressesLoading" class="no-address-hint">
            <t-icon name="info-circle" />
            <span>暂无地址信息，请先添加地址</span>
            <t-button variant="text" theme="primary" size="small" @click="goToAddAddress">去添加</t-button>
          </div>
        </div>

        <!-- 特殊需求 -->
        <div class="form-section">
          <div class="form-label">特殊需求（选填）</div>
          <t-textarea
            v-model="appointmentForm.requirement"
            placeholder="请输入特殊需求或备注，如宝宝的特殊情况、护理偏好等"
            :maxlength="500"
            :autosize="{ minRows: 3, maxRows: 5 }"
          />
        </div>

        <!-- 提交按钮 -->
        <div class="form-actions">
          <t-button theme="default" @click="appointmentDialogVisible = false">取消</t-button>
          <t-button
            theme="primary"
            :loading="submitting"
            :disabled="!canSubmit"
            @click="submitAppointment"
          >
            提交预约
          </t-button>
        </div>
      </div>
    </t-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import {
  getInstitutionDetail,
  getInstitutionStaffCount,
  getInstitutionServiceCount,
  getInstitutionNannies
} from '@/api/institution'
import { createAppointment } from '@/api/appointment'
import { getBabyList } from '@/api/baby'
import { getAddressList } from '@/api/address'

const route = useRoute()
const router = useRouter()

// 机构信息
const institution = ref(null)
const loading = ref(false)

// 统计数据
const stats = ref({
  staffCount: 0,
  serviceCount: 0
})

// 保姆列表
const nannies = ref([])
const nannyLoading = ref(false)
const nannyPage = ref(1)
const nannyPageSize = ref(12)
const nannyTotal = ref(0)

// 营业执照预览
const licenseVisible = ref(false)

// 预约相关
const appointmentDialogVisible = ref(false)
const selectedNanny = ref(null)
const submitting = ref(false)

// 宝宝列表
const babies = ref([])
const babiesLoading = ref(false)

// 地址列表
const addresses = ref([])
const addressesLoading = ref(false)

const appointmentForm = ref({
  babyId: null,
  serviceType: 1,
  appointmentDate: '',
  startTime: '',
  endTime: '',
  requirement: '',
  addressId: null
})

const serviceTypes = [
  { value: 1, label: '日常护理', icon: 'heart' },
  { value: 2, label: '月嫂服务', icon: 'user' },
  { value: 3, label: '育儿指导', icon: 'chat' },
  { value: 4, label: '其他服务', icon: 'ellipsis' }
]

// 是否可以提交
const canSubmit = computed(() => {
  return appointmentForm.value.babyId &&
    appointmentForm.value.serviceType &&
    appointmentForm.value.appointmentDate &&
    appointmentForm.value.startTime &&
    appointmentForm.value.endTime &&
    appointmentForm.value.addressId
})

// 获取机构详情
const fetchInstitutionDetail = async () => {
  loading.value = true
  try {
    const institutionId = route.params.id
    const [detailRes, staffRes, serviceRes] = await Promise.all([
      getInstitutionDetail(institutionId),
      getInstitutionStaffCount(institutionId),
      getInstitutionServiceCount(institutionId)
    ])
    institution.value = detailRes.data
    stats.value.staffCount = staffRes.data || 0
    stats.value.serviceCount = serviceRes.data || 0
  } catch (error) {
    console.error('获取机构详情失败:', error)
    MessagePlugin.error('获取机构详情失败')
  } finally {
    loading.value = false
  }
}

// 获取保姆列表
const fetchNannies = async () => {
  nannyLoading.value = true
  try {
    const institutionId = route.params.id
    const res = await getInstitutionNannies(institutionId, {
      pageNum: nannyPage.value,
      pageSize: nannyPageSize.value
    })
    nannies.value = res.data.records || []
    nannyTotal.value = res.data.total || 0
  } catch (error) {
    console.error('获取保姆列表失败:', error)
    MessagePlugin.error('获取保姆列表失败')
  } finally {
    nannyLoading.value = false
  }
}

// 保姆分页变化
const handleNannyPageChange = () => {
  fetchNannies()
  document.querySelector('.nannies-section')?.scrollIntoView({ behavior: 'smooth' })
}

// 预览营业执照
const previewLicense = () => {
  licenseVisible.value = true
}

// 查看保姆详情
const viewNannyDetail = (nannyId) => {
  router.push(`/nannies/${nannyId}`)
}

// 打开预约弹窗
const openAppointmentDialog = async (nanny = null) => {
  selectedNanny.value = nanny
  appointmentForm.value = {
    babyId: null,
    serviceType: 1,
    appointmentDate: '',
    startTime: '',
    endTime: '',
    requirement: '',
    addressId: null
  }
  appointmentDialogVisible.value = true

  // 并行加载宝宝列表和地址列表
  const loadPromises = []

  if (babies.value.length === 0) {
    babiesLoading.value = true
    loadPromises.push(
      getBabyList()
        .then(res => { babies.value = res.data || [] })
        .catch(error => { console.error('获取宝宝列表失败:', error) })
        .finally(() => { babiesLoading.value = false })
    )
  }

  if (addresses.value.length === 0) {
    addressesLoading.value = true
    loadPromises.push(
      getAddressList()
        .then(res => {
          addresses.value = res.data || []
          // 自动选择默认地址
          const defaultAddr = addresses.value.find(a => a.isDefault === 1)
          if (defaultAddr) {
            appointmentForm.value.addressId = defaultAddr.id
          }
        })
        .catch(error => { console.error('获取地址列表失败:', error) })
        .finally(() => { addressesLoading.value = false })
    )
  } else {
    // 如果已有地址列表，自动选择默认地址
    const defaultAddr = addresses.value.find(a => a.isDefault === 1)
    if (defaultAddr) {
      appointmentForm.value.addressId = defaultAddr.id
    }
  }

  await Promise.all(loadPromises)
}

// 清除选中的保姆
const clearSelectedNanny = () => {
  selectedNanny.value = null
}

// 跳转添加宝宝
const goToAddBaby = () => {
  router.push('/baby/add')
}

// 跳转添加地址
const goToAddAddress = () => {
  router.push('/addresses')
}

// 格式化地址
const formatAddress = (addr) => {
  if (!addr) return ''
  const parts = [addr.province, addr.city, addr.district, addr.detailAddress].filter(Boolean)
  return parts.join('')
}

// 禁用过去的日期
const disablePastDate = (date) => {
  return date < new Date(new Date().setHours(0, 0, 0, 0))
}

// 提交预约
const submitAppointment = async () => {
  if (!canSubmit.value) {
    MessagePlugin.warning('请填写完整信息')
    return
  }

  submitting.value = true
  try {
    await createAppointment({
      babyId: appointmentForm.value.babyId,
      institutionId: Number(route.params.id),
      nannyId: selectedNanny.value?.id || null,
      serviceType: appointmentForm.value.serviceType,
      appointmentDate: appointmentForm.value.appointmentDate,
      startTime: appointmentForm.value.startTime,
      endTime: appointmentForm.value.endTime,
      requirement: appointmentForm.value.requirement || null,
      addressId: appointmentForm.value.addressId
    })
    MessagePlugin.success('预约提交成功')
    appointmentDialogVisible.value = false
    router.push('/appointments')
  } catch (error) {
    console.error('提交预约失败:', error)
    MessagePlugin.error(error.message || '提交预约失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchInstitutionDetail()
  fetchNannies()
})
</script>

<style scoped>
.institution-detail-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

/* 机构头部 */
.institution-header {
  background: white;
  border-radius: 16px;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1);
  padding: 32px;
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  gap: 32px;
  margin-bottom: 32px;
}

.logo-section { flex-shrink: 0; }

.logo {
  width: 120px;
  height: 120px;
  border-radius: 12px;
  object-fit: cover;
}

.logo-placeholder {
  width: 120px;
  height: 120px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.8);
}

.info-section { flex: 1; }

.name-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.institution-name {
  font-size: 28px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.contact-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.contact-item {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.contact-item .icon {
  color: #3b82f6;
  margin-top: 2px;
}

.label {
  font-size: 12px;
  color: #9ca3af;
  margin-bottom: 4px;
}

.value {
  font-size: 14px;
  color: #374151;
  font-weight: 500;
}

/* 统计卡片 */
.stats-row {
  display: flex;
  gap: 20px;
}

.stat-card {
  flex: 1;
  padding: 24px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  gap: 20px;
  transition: all 0.3s;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.stat-card-primary {
  background: linear-gradient(135deg, #EFF6FF 0%, #DBEAFE 100%);
}

.stat-card-success {
  background: linear-gradient(135deg, #ECFDF5 0%, #D1FAE5 100%);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-card-primary .stat-icon {
  background: white;
  color: #3B82F6;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.15);
}

.stat-card-success .stat-icon {
  background: white;
  color: #10B981;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.15);
}

.stat-content { flex: 1; }

.stat-value {
  font-size: 36px;
  font-weight: 700;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-card-primary .stat-value { color: #1E40AF; }
.stat-card-success .stat-value { color: #047857; }

.stat-label {
  font-size: 14px;
  font-weight: 500;
}

.stat-card-primary .stat-label { color: #3B82F6; }
.stat-card-success .stat-label { color: #10B981; }

/* 简介区 */
.introduction-section {
  padding-top: 24px;
  border-top: 1px solid #f3f4f6;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 16px 0;
}

.introduction-text {
  font-size: 15px;
  color: #6b7280;
  line-height: 1.8;
  margin: 0;
}

/* 营业执照 */
.license-section {
  padding-top: 24px;
  border-top: 1px solid #f3f4f6;
  margin-top: 24px;
}

.license-img {
  max-width: 400px;
  width: 100%;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.3s;
}

.license-img:hover { transform: scale(1.02); }

/* 保姆列表区 */
.nannies-section {
  background: white;
  border-radius: 16px;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1);
  padding: 32px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.total-count {
  font-size: 14px;
  color: #6b7280;
}

.nannies-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

/* 保姆卡片 */
.nanny-card {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.nanny-card:hover {
  border-color: #3b82f6;
  box-shadow: 0 4px 12px 0 rgba(59, 130, 246, 0.1);
}

.nanny-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  cursor: pointer;
}

.nanny-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.nanny-info { width: 100%; }

.nanny-name {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 8px;
  cursor: pointer;
}

.nanny-name:hover { color: #3b82f6; }

.nanny-meta {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #6b7280;
}

.nanny-intro {
  font-size: 13px;
  color: #9ca3af;
  line-height: 1.6;
  margin: 0 0 12px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.nanny-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #9ca3af;
}

.empty-state p {
  margin-top: 16px;
  font-size: 16px;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #f3f4f6;
}

/* 预约弹窗表单 */
.appointment-form {
  padding: 8px 0;
}

.selected-nanny {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f0f9ff;
  border-radius: 12px;
  margin-bottom: 20px;
}

.selected-nanny .nanny-detail {
  flex: 1;
}

.selected-nanny .name {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
}

.selected-nanny .meta {
  font-size: 13px;
  color: #6b7280;
}

.no-nanny-hint {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #fef3c7;
  border-radius: 8px;
  margin-bottom: 20px;
  font-size: 14px;
  color: #92400e;
}

.form-label.required::after {
  content: '*';
  color: #ef4444;
  margin-left: 4px;
}

.baby-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.baby-name {
  font-weight: 500;
}

.baby-age {
  font-size: 12px;
  color: #9ca3af;
}

.no-baby-hint {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  font-size: 13px;
  color: #f59e0b;
}

.address-option {
  padding: 4px 0;
}

.address-main {
  display: flex;
  align-items: center;
  gap: 8px;
}

.address-text {
  font-size: 14px;
  color: #374151;
}

.address-contact {
  font-size: 12px;
  color: #9ca3af;
  margin-top: 4px;
}

.no-address-hint {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  font-size: 13px;
  color: #f59e0b;
}

.time-picker-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.time-separator {
  color: #6b7280;
  font-size: 14px;
}

.form-section {
  margin-bottom: 20px;
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 8px;
}

.service-types {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.service-type-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 12px;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  color: #6b7280;
}

.service-type-item:hover {
  border-color: #93c5fd;
}

.service-type-item.active {
  border-color: #3b82f6;
  background: #eff6ff;
  color: #3b82f6;
}

.service-type-item span {
  font-size: 13px;
  font-weight: 500;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #f3f4f6;
}

/* 响应式 */
@media (max-width: 768px) {
  .institution-detail-page { padding: 16px; }
  .institution-header { padding: 20px; }
  .header-content {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  .name-row {
    flex-direction: column;
    align-items: center;
    gap: 16px;
  }
  .contact-grid { grid-template-columns: 1fr; }
  .nannies-grid { grid-template-columns: 1fr; }
  .nannies-section { padding: 20px; }
  .service-types { grid-template-columns: repeat(2, 1fr); }
  .time-picker-row { flex-direction: column; gap: 8px; }
  .time-separator { display: none; }
}
</style>
