<template>
  <div class="nanny-detail-page">
    <t-loading :loading="loading">
      <div v-if="nanny" class="nanny-content">
        <!-- 个人信息卡片 -->
        <div class="info-card">
          <div class="profile-header">
            <!-- 头像 -->
            <div class="avatar-section">
              <div class="avatar-wrapper">
                <img
                  v-if="nanny.avatar"
                  :src="nanny.avatar"
                  :alt="nanny.realName"
                  class="avatar"
                />
                <div v-else class="avatar-placeholder">
                  <t-icon name="user" size="64px" />
                </div>
              </div>
              <t-tag v-if="nanny.status === 1" theme="success" size="large">可预约</t-tag>
            </div>

            <!-- 基本信息 -->
            <div class="basic-info">
              <h1 class="nanny-name">{{ nanny.realName }}</h1>
              
              <div class="meta-row">
                <div v-if="nanny.gender !== undefined" class="meta-item">
                  <t-icon name="user" />
                  <span>{{ nanny.gender === 1 ? '男' : nanny.gender === 2 ? '女' : '未知' }}</span>
                </div>
                <div v-if="nanny.workYears" class="meta-item">
                  <t-icon name="time" />
                  <span>{{ nanny.workYears }}年经验</span>
                </div>
                <div v-if="nanny.phone" class="meta-item">
                  <t-icon name="call" />
                  <span>{{ nanny.phone }}</span>
                </div>
                <div v-if="nanny.email" class="meta-item">
                  <t-icon name="mail" />
                  <span>{{ nanny.email }}</span>
                </div>
              </div>

              <!-- 所属机构 -->
              <div v-if="institution" class="institution-info">
                <t-icon name="building" />
                <span>所属机构：</span>
                <a @click="goToInstitution(nanny.institutionId)" class="institution-link">
                  {{ institution.name }}
                </a>
              </div>
            </div>
          </div>
        </div>

        <!-- 个人简介 -->
        <div v-if="nanny.introduction" class="section-card">
          <h3 class="section-title">
            <t-icon name="user-talk" />
            个人简介
          </h3>
          <p class="introduction-text">{{ nanny.introduction }}</p>
        </div>

        <!-- 资格证书 -->
        <div v-if="certificates.length > 0" class="section-card">
          <h3 class="section-title">
            <t-icon name="file-certificate" />
            资格证书
          </h3>
          <div class="certificates-grid">
            <div
              v-for="(cert, index) in certificates"
              :key="index"
              class="certificate-item"
              @click="previewCertificate(index)"
            >
              <img :src="cert" alt="资格证书" />
              <div class="certificate-overlay">
                <t-icon name="browse" size="32px" />
              </div>
            </div>
          </div>
        </div>

        <!-- 预约按钮 -->
        <div class="action-section">
          <t-button
            theme="primary"
            size="large"
            block
            @click="handleBooking"
            :disabled="nanny.status !== 1"
          >
            <t-icon name="calendar" />
            {{ nanny.status === 1 ? '立即预约' : '暂不可预约' }}
          </t-button>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else-if="!loading" class="empty-state">
        <t-icon name="user-clear" size="64px" style="color: #ddd" />
        <p>保姆信息不存在</p>
        <t-button theme="primary" @click="$router.back()">返回</t-button>
      </div>
    </t-loading>

    <!-- 证书预览 -->
    <t-image-viewer
      v-model:visible="certVisible"
      :images="certificates"
      :initial-index="certIndex"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import { getNannyDetail } from '@/api/nanny'
import { getInstitutionDetail } from '@/api/institution'

const route = useRoute()
const router = useRouter()

// 保姆信息
const nanny = ref(null)
const institution = ref(null)
const loading = ref(false)

// 证书预览
const certVisible = ref(false)
const certIndex = ref(0)

// 解析证书列表
const certificates = computed(() => {
  if (!nanny.value?.certificate) return []
  try {
    const certs = JSON.parse(nanny.value.certificate)
    return Array.isArray(certs) ? certs : []
  } catch {
    return []
  }
})

// 获取保姆详情
const fetchNannyDetail = async () => {
  loading.value = true
  try {
    const nannyId = route.params.id
    const res = await getNannyDetail(nannyId)
    nanny.value = res.data

    // 获取机构信息
    if (nanny.value.institutionId) {
      try {
        const instRes = await getInstitutionDetail(nanny.value.institutionId)
        institution.value = instRes.data
      } catch (error) {
        console.error('获取机构信息失败:', error)
      }
    }
  } catch (error) {
    console.error('获取保姆详情失败:', error)
    MessagePlugin.error(error.message || '获取保姆详情失败')
  } finally {
    loading.value = false
  }
}

// 预览证书
const previewCertificate = (index) => {
  certIndex.value = index
  certVisible.value = true
}

// 跳转到机构详情
const goToInstitution = (institutionId) => {
  router.push(`/institutions/${institutionId}`)
}

// 预约
const handleBooking = () => {
  // TODO: 跳转到预约页面
  MessagePlugin.info('预约功能开发中...')
}

onMounted(() => {
  fetchNannyDetail()
})
</script>

<style scoped>
.nanny-detail-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px;
}

/* 信息卡片 */
.info-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 48px;
  margin-bottom: 32px;
}

.profile-header {
  display: flex;
  gap: 48px;
  align-items: flex-start;
}

/* 头像区 */
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  flex-shrink: 0;
}

.avatar-wrapper {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  overflow: hidden;
  border: 6px solid #fff;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.9);
}

/* 基本信息 */
.basic-info {
  flex: 1;
  padding-top: 8px;
}

.nanny-name {
  font-size: 36px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 24px 0;
  letter-spacing: -0.5px;
}

.meta-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px 32px;
  margin-bottom: 28px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  color: #4b5563;
  font-weight: 500;
}

.meta-item :deep(.t-icon) {
  color: #3b82f6;
  font-size: 18px;
}

.institution-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px 20px;
  background: linear-gradient(135deg, #EFF6FF 0%, #DBEAFE 100%);
  border-radius: 12px;
  font-size: 15px;
  color: #1e40af;
  margin-top: 24px;
  border: 1px solid #bfdbfe;
}

.institution-info :deep(.t-icon) {
  color: #3b82f6;
  font-size: 20px;
}

.institution-link {
  color: #2563eb;
  font-weight: 600;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.2s;
}

.institution-link:hover {
  color: #1d4ed8;
  text-decoration: underline;
}

/* 区块卡片 */
.section-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 40px;
  margin-bottom: 32px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 28px 0;
  padding-bottom: 16px;
  border-bottom: 2px solid #f3f4f6;
}

.section-title :deep(.t-icon) {
  color: #3b82f6;
  font-size: 26px;
}

.introduction-text {
  font-size: 16px;
  color: #374151;
  line-height: 2;
  margin: 0;
  white-space: pre-wrap;
  text-indent: 2em;
}

/* 证书网格 */
.certificates-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.certificate-item {
  position: relative;
  aspect-ratio: 4/3;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.certificate-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.certificate-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.certificate-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(59, 130, 246, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  color: white;
}

.certificate-item:hover .certificate-overlay {
  opacity: 1;
}

/* 操作区 */
.action-section {
  background: white;
  border-radius: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 32px;
}

.action-section :deep(.t-button) {
  height: 56px;
  font-size: 18px;
  font-weight: 600;
  border-radius: 12px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 120px 40px;
  color: #9ca3af;
}

.empty-state p {
  margin: 24px 0 32px 0;
  font-size: 18px;
}

/* 响应式 */
@media (max-width: 768px) {
  .nanny-detail-page {
    padding: 16px;
  }

  .info-card {
    padding: 24px;
  }

  .profile-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .nanny-name {
    font-size: 24px;
  }

  .meta-row {
    justify-content: center;
  }

  .section-card {
    padding: 20px;
  }

  .certificates-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
