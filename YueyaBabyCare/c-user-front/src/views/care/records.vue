<template>
  <div class="care-records-page">
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
            <h1 class="page-title">护理记录</h1>
            <p class="page-desc">{{ babyInfo?.name }}的护理服务记录</p>
          </div>
          <t-dropdown :options="typeOptions" @click="handleTypeFilter">
            <t-button variant="outline">
              <template #icon><t-icon name="filter" /></template>
              {{ currentTypeLabel }}
            </t-button>
          </t-dropdown>
        </div>

        <!-- 统计卡片 -->
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-icon feeding-icon">
              <t-icon name="heart" size="28" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statsData.feeding }}</div>
              <div class="stat-label">喂养记录</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon bathing-icon">
              <t-icon name="logo-windows-filled" size="28" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statsData.bathing }}</div>
              <div class="stat-label">洗护记录</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon sleep-icon">
              <t-icon name="moon" size="28" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statsData.sleep }}</div>
              <div class="stat-label">哄睡记录</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon education-icon">
              <t-icon name="star" size="28" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statsData.education }}</div>
              <div class="stat-label">早教记录</div>
            </div>
          </div>
        </div>

        <!-- 记录列表 -->
        <div class="table-card">
          <div class="card-header">
            <h3>护理记录列表</h3>
            <span class="record-count">共 {{ filteredRecords.length }} 条记录</span>
          </div>

          <!-- 空状态 -->
          <div v-if="filteredRecords.length === 0" class="empty-state">
            <t-icon name="file-paste" size="48" />
            <p>暂无护理记录</p>
            <p class="empty-tip">保姆完成护理后会在此展示记录</p>
          </div>

          <t-table
            v-else
            :data="filteredRecords"
            :columns="columns"
            row-key="id"
            stripe
            hover
            :pagination="pagination"
            @page-change="onPageChange"
          >
            <template #recordDate="{ row }">
              <span class="date-text">{{ row.recordDate }}</span>
            </template>
            <template #recordTime="{ row }">
              <span class="time-text">{{ formatTime(row.recordTime) }}</span>
            </template>
            <template #careTypeName="{ row }">
              <t-tag :style="{ background: getCareTypeBg(row.careType), color: getCareTypeColor(row.careType) }" size="small">
                {{ row.careTypeName }}
              </t-tag>
            </template>
            <template #careContent="{ row }">
              <span class="content-text">{{ row.careContent }}</span>
            </template>
            <template #images="{ row }">
              <div v-if="row.images && parseImages(row.images).length > 0" class="table-images">
                <img 
                  v-for="(img, idx) in parseImages(row.images).slice(0, 3)" 
                  :key="idx" 
                  :src="getFullImageUrl(img)" 
                  class="table-thumb"
                  @click.stop="previewImage(parseImages(row.images), idx)"
                />
                <span v-if="parseImages(row.images).length > 3" class="more-count">
                  +{{ parseImages(row.images).length - 3 }}
                </span>
              </div>
              <span v-else class="empty-text">--</span>
            </template>
            <template #nannyName="{ row }">
              <span class="nanny-text">{{ row.nannyName }}</span>
            </template>
            <template #action="{ row }">
              <t-button variant="text" theme="primary" size="small" @click="showRecordDetail(row)">
                <t-icon name="browse" /> 查看
              </t-button>
            </template>
          </t-table>
        </div>
      </template>
    </div>

    <!-- 记录详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      :header="currentRecord?.careTypeName + '记录详情'"
      width="600px"
      :footer="false"
    >
      <div v-if="currentRecord" class="record-detail">
        <div class="detail-row">
          <span class="detail-label">护理类型</span>
          <t-tag :style="{ background: getCareTypeBg(currentRecord.careType), color: getCareTypeColor(currentRecord.careType) }">
            {{ currentRecord.careTypeName }}
          </t-tag>
        </div>
        <div class="detail-row">
          <span class="detail-label">记录时间</span>
          <span class="detail-value">{{ currentRecord.recordDate }} {{ formatTime(currentRecord.recordTime) }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">护理人员</span>
          <span class="detail-value">{{ currentRecord.nannyName }}</span>
        </div>
        <div class="detail-row full">
          <span class="detail-label">护理内容</span>
          <div class="detail-content">{{ currentRecord.careContent }}</div>
        </div>
        <div v-if="currentRecord.remark" class="detail-row full">
          <span class="detail-label">备注</span>
          <div class="detail-remark">{{ currentRecord.remark }}</div>
        </div>
        <div v-if="currentRecord.images && parseImages(currentRecord.images).length > 0" class="detail-row full">
          <span class="detail-label">护理照片</span>
          <div class="detail-images">
            <img 
              v-for="(img, idx) in parseImages(currentRecord.images)" 
              :key="idx" 
              :src="getFullImageUrl(img)" 
              class="detail-thumb"
              @click="previewImage(parseImages(currentRecord.images), idx)"
            />
          </div>
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
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import { getCareRecordsByBaby } from '@/api/careRecord'
import { getBabyById } from '@/api/baby'

const route = useRoute()
const loading = ref(false)
const records = ref([])
const babyInfo = ref(null)
const currentType = ref(null)
const detailVisible = ref(false)
const currentRecord = ref(null)
const previewVisible = ref(false)
const previewImages = ref([])
const previewIndex = ref(0)

// 表格列配置
const columns = [
  { colKey: 'recordDate', title: '日期', width: 120 },
  { colKey: 'recordTime', title: '时间', width: 80 },
  { colKey: 'careTypeName', title: '类型', width: 100 },
  { colKey: 'careContent', title: '护理内容', ellipsis: true },
  { colKey: 'images', title: '图片', width: 180 },
  { colKey: 'nannyName', title: '护理人员', width: 100 },
  { colKey: 'action', title: '操作', width: 100, fixed: 'right' }
]

// 分页配置
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showJumper: true,
  showPageSize: true
})

const onPageChange = (pageInfo) => {
  pagination.value.current = pageInfo.current
  pagination.value.pageSize = pageInfo.pageSize
}

const typeOptions = [
  { content: '全部类型', value: null },
  { content: '喂养', value: 1 },
  { content: '洗护', value: 2 },
  { content: '哄睡', value: 3 },
  { content: '早教', value: 4 },
  { content: '其他', value: 5 }
]

const currentTypeLabel = computed(() => {
  const item = typeOptions.find(o => o.value === currentType.value)
  return item ? item.content : '全部类型'
})

// 统计数据
const statsData = computed(() => {
  const stats = { feeding: 0, bathing: 0, sleep: 0, education: 0, other: 0 }
  records.value.forEach(r => {
    switch (r.careType) {
      case 1: stats.feeding++; break
      case 2: stats.bathing++; break
      case 3: stats.sleep++; break
      case 4: stats.education++; break
      case 5: stats.other++; break
    }
  })
  return stats
})

// 过滤后的记录
const filteredRecords = computed(() => {
  let list = records.value
  if (currentType.value !== null) {
    list = list.filter(r => r.careType === currentType.value)
  }
  return list
})

// 监听过滤后的记录变化，更新分页总数
watch(filteredRecords, (newList) => {
  pagination.value.total = newList.length
}, { immediate: true })

// 护理类型颜色
const getCareTypeColor = (type) => {
  const colors = {
    1: '#10b981', // 喂养 - 绿色
    2: '#3b82f6', // 洗护 - 蓝色
    3: '#8b5cf6', // 哄睡 - 紫色
    4: '#f59e0b', // 早教 - 橙色
    5: '#6b7280'  // 其他 - 灰色
  }
  return colors[type] || colors[5]
}

const getCareTypeBg = (type) => {
  const bgs = {
    1: '#ecfdf5',
    2: '#eff6ff',
    3: '#f5f3ff',
    4: '#fffbeb',
    5: '#f9fafb'
  }
  return bgs[type] || bgs[5]
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return time.substring(0, 5)
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

// 显示详情
const showRecordDetail = (record) => {
  currentRecord.value = record
  detailVisible.value = true
}

// 获取宝宝信息
const fetchBabyInfo = async () => {
  const babyId = route.params.babyId
  if (!babyId) return
  try {
    const res = await getBabyById(babyId)
    if (res.code === 200) {
      babyInfo.value = res.data
    }
  } catch (e) {
    console.error('获取宝宝信息失败:', e)
  }
}

// 获取护理记录
const fetchRecords = async () => {
  const babyId = route.params.babyId
  if (!babyId) return

  loading.value = true
  try {
    const res = await getCareRecordsByBaby(babyId)
    if (res.code === 200) {
      records.value = res.data || []
    } else {
      MessagePlugin.error(res.message || '获取失败')
    }
  } catch (error) {
    console.error('获取护理记录失败:', error)
    MessagePlugin.error('获取护理记录失败')
  } finally {
    loading.value = false
  }
}

// 类型筛选
const handleTypeFilter = (data) => {
  currentType.value = data.value
  pagination.value.current = 1
}

onMounted(() => {
  fetchBabyInfo()
  fetchRecords()
})
</script>

<style scoped>
.care-records-page {
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

.feeding-icon { background: linear-gradient(135deg, #10b981, #059669); }
.bathing-icon { background: linear-gradient(135deg, #3b82f6, #2563eb); }
.sleep-icon { background: linear-gradient(135deg, #8b5cf6, #7c3aed); }
.education-icon { background: linear-gradient(135deg, #f59e0b, #d97706); }

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

/* 表格卡片 */
.table-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
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

.record-count {
  font-size: 14px;
  color: #6b7280;
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

/* 表格样式 */
.date-text {
  font-weight: 500;
  color: #374151;
}

.time-text {
  color: #6b7280;
}

.content-text {
  color: #374151;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.nanny-text {
  color: #6b7280;
}

.empty-text {
  color: #d1d5db;
}

/* 表格图片 */
.table-images {
  display: flex;
  align-items: center;
  gap: 6px;
}

.table-thumb {
  width: 40px;
  height: 40px;
  border-radius: 6px;
  object-fit: cover;
  cursor: pointer;
  transition: transform 0.2s;
}

.table-thumb:hover {
  transform: scale(1.1);
}

.more-count {
  font-size: 12px;
  color: #6b7280;
  padding: 4px 8px;
  background: #f3f4f6;
  border-radius: 4px;
}

/* 详情弹窗 */
.record-detail {
  padding: 8px 0;
}

.detail-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16px;
  gap: 16px;
}

.detail-row.full {
  flex-direction: column;
  gap: 8px;
}

.detail-label {
  width: 80px;
  flex-shrink: 0;
  font-size: 14px;
  color: #6b7280;
}

.detail-value {
  font-size: 14px;
  color: #1f2937;
}

.detail-content {
  padding: 16px;
  background: #f9fafb;
  border-radius: 8px;
  font-size: 14px;
  color: #374151;
  line-height: 1.6;
  width: 100%;
  box-sizing: border-box;
}

.detail-remark {
  padding: 12px 16px;
  background: #fffbeb;
  border-radius: 8px;
  font-size: 14px;
  color: #92400e;
  width: 100%;
  box-sizing: border-box;
}

.detail-images {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.detail-thumb {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  object-fit: cover;
  cursor: pointer;
  transition: transform 0.2s;
}

.detail-thumb:hover {
  transform: scale(1.05);
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
}
</style>
