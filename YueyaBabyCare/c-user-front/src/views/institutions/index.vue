<template>
  <div class="institutions-page">
    <!-- 头部搜索区 -->
    <div class="search-header">
      <h1 class="page-title">选择机构</h1>
      <p class="page-subtitle">为您精选专业的母婴护理机构</p>
      
      <div class="search-box">
        <t-input
          v-model="searchKeyword"
          placeholder="搜索机构名称"
          clearable
          @enter="handleSearch"
        >
          <template #suffix-icon>
            <t-icon name="search" @click="handleSearch" />
          </template>
        </t-input>
      </div>
    </div>

    <!-- 机构列表 -->
    <div class="institutions-list">
      <t-loading :loading="loading">
        <div v-if="institutions.length > 0" class="grid-container">
          <div
            v-for="institution in institutions"
            :key="institution.id"
            class="institution-card"
            @click="goToDetail(institution.id)"
          >
            <!-- Logo -->
            <div class="logo-wrapper">
              <img
                v-if="institution.logo"
                :src="institution.logo"
                :alt="institution.name"
                class="logo"
              />
              <div v-else class="logo-placeholder">
                <t-icon name="building" size="48px" />
              </div>
            </div>

            <!-- 信息区 -->
            <div class="info-section">
              <h3 class="institution-name">{{ institution.name }}</h3>
              
              <!-- 联系方式 -->
              <div class="contact-info">
                <div class="info-item">
                  <t-icon name="user" />
                  <span>{{ institution.contactName }}</span>
                </div>
                <div class="info-item">
                  <t-icon name="call" />
                  <span>{{ institution.contactPhone }}</span>
                </div>
              </div>

              <!-- 简介 -->
              <p v-if="institution.introduction" class="introduction">
                {{ institution.introduction }}
              </p>

              <!-- 统计信息 -->
              <div class="stats">
                <div class="stat-item">
                  <span class="stat-value">{{ institution.staffCount || 0 }}</span>
                  <span class="stat-label">保姆数量</span>
                </div>
                <div class="stat-divider"></div>
                <div class="stat-item">
                  <span class="stat-value">{{ institution.serviceCount || 0 }}</span>
                  <span class="stat-label">服务订单</span>
                </div>
              </div>

              <!-- 查看详情按钮 -->
              <div class="action-area">
                <t-button theme="primary" variant="outline" block>
                  查看详情
                </t-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else-if="!loading" class="empty-state">
          <t-icon name="building" size="64px" style="color: #ddd" />
          <p>暂无机构信息</p>
        </div>
      </t-loading>
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="pagination-wrapper">
      <t-pagination
        v-model="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-size-options="[10, 20, 30]"
        show-page-size
        @change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import { searchInstitutions, getInstitutionStaffCount, getInstitutionServiceCount } from '@/api/institution'

const router = useRouter()

// 搜索关键词
const searchKeyword = ref('')

// 机构列表
const institutions = ref([])
const loading = ref(false)

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 获取机构列表
const fetchInstitutions = async () => {
  loading.value = true
  try {
    const res = await searchInstitutions({
      keyword: searchKeyword.value,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })

    institutions.value = res.data.records || []
    total.value = res.data.total || 0

    // 并行获取每个机构的统计信息
    if (institutions.value.length > 0) {
      const promises = institutions.value.map(async (institution) => {
        try {
          const [staffRes, serviceRes] = await Promise.all([
            getInstitutionStaffCount(institution.id),
            getInstitutionServiceCount(institution.id)
          ])
          institution.staffCount = staffRes.data || 0
          institution.serviceCount = serviceRes.data || 0
        } catch (error) {
          console.error(`获取机构${institution.id}统计信息失败:`, error)
        }
      })
      await Promise.all(promises)
    }
  } catch (error) {
    console.error('获取机构列表失败:', error)
    MessagePlugin.error('获取机构列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchInstitutions()
}

// 分页变化
const handlePageChange = () => {
  fetchInstitutions()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 跳转详情
const goToDetail = (id) => {
  router.push(`/institutions/${id}`)
}

onMounted(() => {
  fetchInstitutions()
})
</script>

<style scoped>
.institutions-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

/* 头部搜索区 */
.search-header {
  text-align: center;
  margin-bottom: 32px;
}

.page-title {
  font-size: 32px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 16px;
  color: #6b7280;
  margin: 0 0 24px 0;
}

.search-box {
  max-width: 600px;
  margin: 0 auto;
}

.search-box :deep(.t-input) {
  height: 48px;
  border-radius: 24px;
}

/* 机构列表 */
.institutions-list {
  min-height: 400px;
}

.grid-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.institution-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s;
  cursor: pointer;
}

.institution-card:hover {
  box-shadow: 0 4px 12px 0 rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

/* Logo区域 */
.logo-wrapper {
  width: 100%;
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
}

.logo {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.logo-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.8);
}

/* 信息区 */
.info-section {
  padding: 20px;
}

.institution-name {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 12px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.contact-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #6b7280;
}

.introduction {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
  margin: 0 0 16px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 统计信息 */
.stats {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 16px 0;
  margin-bottom: 16px;
  border-top: 1px solid #f3f4f6;
  border-bottom: 1px solid #f3f4f6;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #3b82f6;
}

.stat-label {
  font-size: 12px;
  color: #9ca3af;
}

.stat-divider {
  width: 1px;
  height: 32px;
  background: #e5e7eb;
}

/* 操作区 */
.action-area {
  margin-top: 16px;
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

/* 响应式 */
@media (max-width: 768px) {
  .institutions-page {
    padding: 16px;
  }

  .page-title {
    font-size: 24px;
  }

  .grid-container {
    grid-template-columns: 1fr;
  }
}
</style>
