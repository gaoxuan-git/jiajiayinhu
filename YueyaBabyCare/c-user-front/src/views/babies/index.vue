<template>
  <div class="babies-page">
    <div class="page-container">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-left">
          <h1 class="page-title">我的宝宝</h1>
          <p class="page-desc">管理您的宝宝信息，记录成长点滴</p>
        </div>
        <t-button theme="primary" @click="goToAdd">
          <template #icon><t-icon name="add" /></template>
          添加宝宝
        </t-button>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-wrapper">
        <t-loading size="large" text="加载中..." />
      </div>

      <!-- 宝宝列表 -->
      <div v-else>
        <!-- 空状态 -->
        <div v-if="babies.length === 0" class="empty-state">
          <div class="empty-icon">
            <t-icon name="user-add" size="64" />
          </div>
          <h3>还没有添加宝宝</h3>
          <p>点击下方按钮添加您的第一个宝宝</p>
          <t-button theme="primary" size="large" @click="goToAdd">
            <template #icon><t-icon name="add" /></template>
            添加宝宝
          </t-button>
        </div>

        <!-- 宝宝卡片网格 -->
        <div v-else class="babies-grid">
          <div
            v-for="baby in babies"
            :key="baby.id"
            class="baby-card"
          >
            <!-- 卡片头部 -->
            <div class="card-header" @click="goToDetail(baby.id)">
              <div class="avatar-wrapper">
                <t-avatar
                  size="80px"
                  :image="baby.avatar"
                  :icon="!baby.avatar ? BabyIcon : undefined"
                />
                <span
                  class="gender-badge"
                  :class="baby.gender === 1 ? 'male' : baby.gender === 2 ? 'female' : 'unknown'"
                >
                  {{ baby.gender === 1 ? '♂' : baby.gender === 2 ? '♀' : '?' }}
                </span>
              </div>
              <div class="baby-info">
                <h3 class="baby-name">
                  {{ baby.name }}
                  <span v-if="baby.nickname" class="nickname">({{ baby.nickname }})</span>
                </h3>
                <p class="baby-age">{{ formatAge(baby.ageInMonths) }}</p>
                <p class="baby-birth">出生于 {{ baby.birthDate }}</p>
              </div>
            </div>

            <!-- 标签信息 -->
            <div class="card-tags" v-if="baby.bloodType || baby.allergyInfo">
              <t-tag v-if="baby.bloodType" theme="danger" variant="light" size="small">
                {{ baby.bloodType }}型血
              </t-tag>
              <t-tag v-if="baby.allergyInfo" theme="warning" variant="light" size="small">
                过敏: {{ baby.allergyInfo }}
              </t-tag>
            </div>

            <!-- 快捷操作 -->
            <div class="card-actions">
              <div class="action-item" @click="goToHealthRecords(baby.id)">
                <div class="action-icon health">
                  <t-icon name="heart" />
                </div>
                <span>健康档案</span>
              </div>
              <div class="action-item" @click="goToGrowthRecords(baby.id)">
                <div class="action-icon growth">
                  <t-icon name="chart" />
                </div>
                <span>成长记录</span>
              </div>
              <div class="action-item" @click="goToDetail(baby.id)">
                <div class="action-icon detail">
                  <t-icon name="browse" />
                </div>
                <span>查看详情</span>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="card-footer">
              <t-button variant="outline" size="small" @click="goToEdit(baby.id)">
                <template #icon><t-icon name="edit" /></template>
                编辑
              </t-button>
              <t-button variant="outline" size="small" @click="goToAvatar(baby.id)">
                <template #icon><t-icon name="image" /></template>
                头像
              </t-button>
              <t-button variant="outline" theme="danger" size="small" @click="handleDelete(baby)">
                <template #icon><t-icon name="delete" /></template>
                删除
              </t-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 删除确认弹窗 -->
    <t-dialog
      v-model:visible="deleteDialogVisible"
      header="确认删除"
      :confirm-btn="{ content: '删除', theme: 'danger' }"
      cancel-btn="取消"
      @confirm="confirmDelete"
    >
      <p>确定要删除 <strong>{{ selectedBaby?.name }}</strong> 的信息吗？此操作不可恢复。</p>
    </t-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, h } from 'vue'
import { useRouter } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import { getBabyList, deleteBaby } from '@/api/baby'

// 自定义宝宝图标
const BabyIcon = () => h('svg', {
  fill: 'none',
  stroke: 'currentColor',
  viewBox: '0 0 24 24',
  style: { width: '40px', height: '40px' }
}, [
  h('path', {
    'stroke-linecap': 'round',
    'stroke-linejoin': 'round',
    'stroke-width': '2',
    d: 'M14.828 14.828a4 4 0 01-5.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z'
  })
])

const router = useRouter()
const loading = ref(false)
const babies = ref([])
const deleteDialogVisible = ref(false)
const selectedBaby = ref(null)

// 获取宝宝列表
const fetchBabies = async () => {
  loading.value = true
  try {
    const res = await getBabyList()
    if (res.code === 200) {
      babies.value = res.data || []
    } else {
      MessagePlugin.error(res.message || '获取宝宝列表失败')
    }
  } catch (error) {
    console.error('获取宝宝列表失败:', error)
    MessagePlugin.error('获取宝宝列表失败')
  } finally {
    loading.value = false
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

// 删除操作
const handleDelete = (baby) => {
  selectedBaby.value = baby
  deleteDialogVisible.value = true
}

const confirmDelete = async () => {
  if (!selectedBaby.value) return
  try {
    const res = await deleteBaby(selectedBaby.value.id)
    if (res.code === 200) {
      MessagePlugin.success('删除成功')
      fetchBabies()
    } else {
      MessagePlugin.error(res.message || '删除失败')
    }
  } catch (error) {
    console.error('删除失败:', error)
    MessagePlugin.error('删除失败')
  }
  deleteDialogVisible.value = false
  selectedBaby.value = null
}

// 页面跳转
const goToAdd = () => router.push('/baby/add')
const goToDetail = (id) => router.push(`/baby/${id}`)
const goToEdit = (id) => router.push(`/baby/edit/${id}`)
const goToAvatar = (id) => router.push(`/baby/avatar/${id}`)
const goToHealthRecords = (id) => router.push(`/health-records/${id}`)
const goToGrowthRecords = (id) => router.push(`/growth-records/${id}`)

onMounted(() => {
  fetchBabies()
})
</script>

<style scoped>
.babies-page {
  padding: 24px;
  background: #F9FAFB;
  min-height: calc(100vh - 120px);
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.page-title {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  color: #1F2937;
}

.page-desc {
  margin: 0;
  font-size: 14px;
  color: #6B7280;
}

/* 加载状态 */
.loading-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

/* 空状态 */
.empty-state {
  background: white;
  border-radius: 12px;
  padding: 80px 40px;
  text-align: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.empty-icon {
  width: 120px;
  height: 120px;
  margin: 0 auto 24px;
  background: linear-gradient(135deg, #EFF6FF 0%, #DBEAFE 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #3B82F6;
}

.empty-state h3 {
  margin: 0 0 8px;
  font-size: 20px;
  color: #1F2937;
}

.empty-state p {
  margin: 0 0 24px;
  color: #6B7280;
}

/* 宝宝卡片网格 */
.babies-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 24px;
}

.baby-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
}

.baby-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  transform: translateY(-4px);
}

/* 卡片头部 */
.card-header {
  display: flex;
  gap: 20px;
  cursor: pointer;
  margin-bottom: 16px;
}

.avatar-wrapper {
  position: relative;
  flex-shrink: 0;
}

.avatar-wrapper :deep(.t-avatar) {
  background: linear-gradient(135deg, #818CF8 0%, #A78BFA 100%);
}

.gender-badge {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  border: 2px solid white;
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

.baby-info {
  flex: 1;
  min-width: 0;
}

.baby-name {
  margin: 0 0 8px;
  font-size: 20px;
  font-weight: 600;
  color: #1F2937;
}

.nickname {
  font-size: 14px;
  font-weight: 400;
  color: #6B7280;
}

.baby-age {
  margin: 0 0 4px;
  font-size: 16px;
  font-weight: 500;
  color: #3B82F6;
}

.baby-birth {
  margin: 0;
  font-size: 13px;
  color: #9CA3AF;
}

/* 标签信息 */
.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #F3F4F6;
}

/* 快捷操作 */
.card-actions {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: #F9FAFB;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.action-item:hover {
  background: #F3F4F6;
  transform: translateY(-2px);
}

.action-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-icon.health {
  background: #FCE7F3;
  color: #EC4899;
}

.action-icon.growth {
  background: #D1FAE5;
  color: #10B981;
}

.action-icon.detail {
  background: #DBEAFE;
  color: #3B82F6;
}

.action-item span {
  font-size: 13px;
  color: #374151;
}

/* 卡片底部 */
.card-footer {
  display: flex;
  gap: 8px;
  padding-top: 16px;
  border-top: 1px solid #F3F4F6;
}

.card-footer :deep(.t-button) {
  flex: 1;
}
</style>
