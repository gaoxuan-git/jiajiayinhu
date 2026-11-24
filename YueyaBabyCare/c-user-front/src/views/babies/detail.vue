<template>
  <div class="baby-detail-page">
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

      <!-- 详情内容 -->
      <div v-else-if="baby" class="detail-layout">
        <!-- 左侧信息卡片 -->
        <div class="info-card">
          <div class="baby-header">
            <div class="avatar-wrapper">
              <t-avatar size="100px" :image="baby.avatar">
                <template #icon>
                  <svg fill="none" stroke="currentColor" viewBox="0 0 24 24" style="width: 48px; height: 48px;">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.828 14.828a4 4 0 01-5.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
                  </svg>
                </template>
              </t-avatar>
              <span
                class="gender-badge"
                :class="baby.gender === 1 ? 'male' : baby.gender === 2 ? 'female' : 'unknown'"
              >
                {{ baby.gender === 1 ? '♂' : baby.gender === 2 ? '♀' : '?' }}
              </span>
            </div>
            <div class="baby-basic">
              <h2 class="baby-name">
                {{ baby.name }}
                <span v-if="baby.nickname" class="nickname">({{ baby.nickname }})</span>
              </h2>
              <p class="baby-age">{{ formatAge(baby.ageInMonths) }}</p>
              <p class="baby-birth">出生于 {{ baby.birthDate }}</p>
            </div>
          </div>

          <div class="info-list">
            <div class="info-item">
              <span class="label">血型</span>
              <span class="value">{{ baby.bloodType ? baby.bloodType + '型' : '未填写' }}</span>
            </div>
            <div class="info-item">
              <span class="label">过敏信息</span>
              <span class="value" :class="{ warning: baby.allergyInfo }">
                {{ baby.allergyInfo || '无' }}
              </span>
            </div>
            <div class="info-item">
              <span class="label">备注</span>
              <span class="value">{{ baby.remark || '无' }}</span>
            </div>
          </div>

          <div class="action-buttons">
            <t-button theme="primary" block @click="goToEdit">
              <template #icon><t-icon name="edit" /></template>
              编辑信息
            </t-button>
            <t-button variant="outline" block @click="goToAvatar">
              <template #icon><t-icon name="image" /></template>
              修改头像
            </t-button>
          </div>
        </div>

        <!-- 右侧功能区 -->
        <div class="right-section">
          <!-- 功能服务 -->
          <div class="function-card">
            <div class="card-header">
              <h3>功能服务</h3>
            </div>
            <div class="service-grid">
              <div class="service-item" @click="goToHealthRecords">
                <div class="service-icon health">
                  <t-icon name="heart" size="28" />
                </div>
                <div class="service-info">
                  <h4>健康档案</h4>
                  <p>记录健康数据</p>
                </div>
                <t-icon name="chevron-right" class="arrow" />
              </div>
              <div class="service-item" @click="goToGrowthRecords">
                <div class="service-icon growth">
                  <t-icon name="chart" size="28" />
                </div>
                <div class="service-info">
                  <h4>成长记录</h4>
                  <p>记录成长点滴</p>
                </div>
                <t-icon name="chevron-right" class="arrow" />
              </div>
              <div class="service-item" @click="goToCarePlans">
                <div class="service-icon care">
                  <t-icon name="calendar" size="28" />
                </div>
                <div class="service-info">
                  <h4>护理计划</h4>
                  <p>查看护理安排</p>
                </div>
                <t-icon name="chevron-right" class="arrow" />
              </div>
              <div class="service-item" @click="goToCareRecords">
                <div class="service-icon record">
                  <t-icon name="file-paste" size="28" />
                </div>
                <div class="service-info">
                  <h4>护理记录</h4>
                  <p>查看护理详情</p>
                </div>
                <t-icon name="chevron-right" class="arrow" />
              </div>
              <div class="service-item" @click="goToEvaluations">
                <div class="service-icon eval">
                  <t-icon name="star" size="28" />
                </div>
                <div class="service-info">
                  <h4>护理评价</h4>
                  <p>评价护理服务</p>
                </div>
                <t-icon name="chevron-right" class="arrow" />
              </div>
            </div>
          </div>

          <!-- 护理人员 -->
          <div class="function-card">
            <div class="card-header">
              <h3>护理人员</h3>
              <t-button variant="text" theme="primary" size="small">查看全部</t-button>
            </div>
            <div v-if="caregivers.length === 0" class="empty-caregivers">
              <t-icon name="user" size="32" />
              <p>暂无护理人员</p>
            </div>
            <div v-else class="caregiver-list">
              <div v-for="caregiver in caregivers" :key="caregiver.id" class="caregiver-item">
                <t-avatar size="48px" :image="caregiver.avatar">{{ caregiver.name?.substring(0, 1) }}</t-avatar>
                <div class="caregiver-info">
                  <h4>{{ caregiver.name }}</h4>
                  <p>{{ caregiver.phone }}</p>
                </div>
                <t-tag theme="success" variant="light" size="small">在职</t-tag>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 错误状态 -->
      <div v-else class="error-state">
        <t-icon name="error-circle" size="64" />
        <h3>宝宝信息不存在</h3>
        <t-button theme="primary" @click="$router.push('/babies')">返回列表</t-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import { getBabyById, getBabyCaregivers } from '@/api/baby'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const baby = ref(null)
const caregivers = ref([])

// 获取宝宝详情
const fetchBaby = async () => {
  const id = route.params.id
  if (!id) {
    MessagePlugin.error('缺少宝宝ID')
    return
  }

  loading.value = true
  try {
    const res = await getBabyById(id)
    if (res.code === 200) {
      baby.value = res.data
      // 获取护理人员
      fetchCaregivers(id)
    } else {
      MessagePlugin.error(res.message || '获取宝宝信息失败')
    }
  } catch (error) {
    console.error('获取宝宝信息失败:', error)
    MessagePlugin.error('获取宝宝信息失败')
  } finally {
    loading.value = false
  }
}

// 获取护理人员
const fetchCaregivers = async (babyId) => {
  try {
    const res = await getBabyCaregivers(babyId)
    if (res.code === 200) {
      caregivers.value = res.data || []
    }
  } catch (error) {
    console.error('获取护理人员失败:', error)
  }
}

// 格式化年龄
const formatAge = (months) => {
  if (months === null || months === undefined) return ''
  if (months < 1) return '不足1个月'
  if (months < 12) return `${months}个月`
  const years = Math.floor(months / 12)
  const remainMonths = months % 12
  if (remainMonths === 0) return `${years}岁`
  return `${years}岁${remainMonths}个月`
}

// 页面跳转
const goToEdit = () => router.push(`/baby/edit/${baby.value.id}`)
const goToAvatar = () => router.push(`/baby/avatar/${baby.value.id}`)
const goToHealthRecords = () => router.push(`/health-records/${baby.value.id}`)
const goToGrowthRecords = () => router.push(`/growth-records/${baby.value.id}`)
const goToCarePlans = () => router.push('/care-plans')
const goToCareRecords = () => router.push(`/care-records/${baby.value.id}`)
const goToEvaluations = () => router.push('/evaluations')

onMounted(() => {
  fetchBaby()
})
</script>

<style scoped>
.baby-detail-page {
  padding: 24px;
  background: #F9FAFB;
  min-height: calc(100vh - 120px);
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
}

.back-nav {
  margin-bottom: 16px;
}

.loading-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

/* 详情布局 */
.detail-layout {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 24px;
}

/* 左侧信息卡片 */
.info-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  height: fit-content;
}

.baby-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #F3F4F6;
  margin-bottom: 20px;
}

.avatar-wrapper {
  position: relative;
  margin-bottom: 16px;
}

.avatar-wrapper :deep(.t-avatar) {
  background: linear-gradient(135deg, #818CF8 0%, #A78BFA 100%);
}

.gender-badge {
  position: absolute;
  bottom: 4px;
  right: 4px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 600;
  border: 3px solid white;
}

.gender-badge.male {
  background: #3B82F6;
  color: white;
}

.gender-badge.female {
  background: #EC4899;
  color: white;
}

.gender-badge.unknown {
  background: #9CA3AF;
  color: white;
}

.baby-name {
  margin: 0 0 8px;
  font-size: 24px;
  font-weight: 600;
  color: #1F2937;
}

.nickname {
  font-size: 16px;
  font-weight: 400;
  color: #6B7280;
}

.baby-age {
  margin: 0 0 4px;
  font-size: 18px;
  font-weight: 500;
  color: #3B82F6;
}

.baby-birth {
  margin: 0;
  font-size: 14px;
  color: #9CA3AF;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-bottom: 24px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-item .label {
  font-size: 14px;
  color: #6B7280;
}

.info-item .value {
  font-size: 14px;
  color: #1F2937;
}

.info-item .value.warning {
  color: #F59E0B;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 右侧功能区 */
.right-section {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.function-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1F2937;
}

/* 服务网格 */
.service-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.service-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #F9FAFB;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.service-item:hover {
  background: #F3F4F6;
  transform: translateX(4px);
}

.service-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.service-icon.health {
  background: linear-gradient(135deg, #FCE7F3 0%, #FBCFE8 100%);
  color: #EC4899;
}

.service-icon.growth {
  background: linear-gradient(135deg, #D1FAE5 0%, #A7F3D0 100%);
  color: #10B981;
}

.service-icon.care {
  background: linear-gradient(135deg, #E0E7FF 0%, #C7D2FE 100%);
  color: #6366F1;
}

.service-icon.eval {
  background: linear-gradient(135deg, #FEF3C7 0%, #FDE68A 100%);
  color: #F59E0B;
}

.service-icon.record {
  background: linear-gradient(135deg, #DBEAFE 0%, #BFDBFE 100%);
  color: #3B82F6;
}

.service-info {
  flex: 1;
}

.service-info h4 {
  margin: 0 0 4px;
  font-size: 16px;
  font-weight: 600;
  color: #1F2937;
}

.service-info p {
  margin: 0;
  font-size: 13px;
  color: #6B7280;
}

.service-item .arrow {
  color: #9CA3AF;
}

/* 护理人员 */
.empty-caregivers {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px;
  color: #9CA3AF;
}

.empty-caregivers p {
  margin: 12px 0 0;
  font-size: 14px;
}

.caregiver-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.caregiver-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #F9FAFB;
  border-radius: 8px;
}

.caregiver-info {
  flex: 1;
}

.caregiver-info h4 {
  margin: 0 0 2px;
  font-size: 15px;
  font-weight: 500;
  color: #1F2937;
}

.caregiver-info p {
  margin: 0;
  font-size: 13px;
  color: #6B7280;
}

/* 错误状态 */
.error-state {
  background: white;
  border-radius: 12px;
  padding: 80px 40px;
  text-align: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  color: #9CA3AF;
}

.error-state h3 {
  margin: 16px 0 24px;
  font-size: 18px;
  color: #6B7280;
}

/* 响应式 */
@media (max-width: 900px) {
  .detail-layout {
    grid-template-columns: 1fr;
  }

  .service-grid {
    grid-template-columns: 1fr;
  }
}
</style>
