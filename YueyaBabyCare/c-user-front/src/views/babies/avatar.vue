<template>
  <div class="baby-avatar-page">
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

      <!-- 内容区域 -->
      <div v-else class="content-wrapper">
        <div class="avatar-card">
          <div class="card-header">
            <h2>修改头像</h2>
            <p>为宝宝上传一张可爱的头像</p>
          </div>

          <div class="avatar-content">
            <!-- 左侧：当前头像 -->
            <div class="current-avatar-section">
              <h3>当前头像</h3>
              <div class="avatar-display">
                <t-avatar size="160px" :image="currentAvatar">
                  <template #icon>
                    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24" style="width: 64px; height: 64px;">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.828 14.828a4 4 0 01-5.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
                    </svg>
                  </template>
                </t-avatar>
              </div>
            </div>

            <!-- 右侧：上传新头像 -->
            <div class="upload-section">
              <h3>上传新头像</h3>

              <!-- 预览区域 -->
              <div v-if="previewUrl" class="preview-area">
                <p class="preview-label">预览效果</p>
                <div class="preview-avatar">
                  <img :src="previewUrl" alt="预览" />
                </div>
              </div>

              <!-- 上传区域 -->
              <div class="upload-area">
                <input
                  type="file"
                  accept="image/*"
                  @change="handleFileChange"
                  class="file-input"
                  ref="fileInput"
                />
                <div class="upload-trigger" @click="$refs.fileInput.click()">
                  <t-icon name="upload" size="32" />
                  <p class="upload-text">点击选择图片</p>
                  <p class="upload-hint">支持 JPG、PNG 格式，最大 5MB</p>
                </div>
              </div>

              <!-- 操作按钮 -->
              <div class="action-buttons" v-if="previewUrl">
                <t-button variant="outline" @click="cancelPreview">取消</t-button>
                <t-button theme="primary" :loading="submitting" @click="handleSubmit">
                  确认上传
                </t-button>
              </div>

              <!-- 提示信息 -->
              <div class="tips-box">
                <t-icon name="info-circle" />
                <span>建议上传正方形图片，系统会自动裁剪为圆形显示</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import { getBabyById, updateBabyAvatar } from '@/api/baby'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const submitting = ref(false)
const currentAvatar = ref('')
const previewUrl = ref('')
const selectedFile = ref(null)
const fileInput = ref(null)

// 获取宝宝当前头像
const fetchBaby = async () => {
  const id = route.params.id
  if (!id) {
    MessagePlugin.error('缺少宝宝ID')
    router.back()
    return
  }

  loading.value = true
  try {
    const res = await getBabyById(id)
    if (res.code === 200 && res.data) {
      currentAvatar.value = res.data.avatar || ''
    } else {
      MessagePlugin.error(res.message || '获取宝宝信息失败')
      router.back()
    }
  } catch (error) {
    console.error('获取宝宝信息失败:', error)
    MessagePlugin.error('获取宝宝信息失败')
    router.back()
  } finally {
    loading.value = false
  }
}

// 处理文件选择
const handleFileChange = (e) => {
  const file = e.target.files[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    MessagePlugin.warning('请选择图片文件')
    return
  }

  // 验证文件大小 (最大 5MB)
  if (file.size > 5 * 1024 * 1024) {
    MessagePlugin.warning('图片大小不能超过 5MB')
    return
  }

  selectedFile.value = file

  // 创建预览
  const reader = new FileReader()
  reader.onload = (e) => {
    previewUrl.value = e.target.result
  }
  reader.readAsDataURL(file)
}

// 取消预览
const cancelPreview = () => {
  previewUrl.value = ''
  selectedFile.value = null
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

// 上传头像
const handleSubmit = async () => {
  if (!selectedFile.value) {
    MessagePlugin.warning('请先选择图片')
    return
  }

  submitting.value = true
  try {
    // 先上传图片获取URL
    const formData = new FormData()
    formData.append('file', selectedFile.value)

    const uploadRes = await request.post('/api/file/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })

    if (uploadRes.code === 200 && uploadRes.data) {
      // 更新宝宝头像
      const avatarUrl = uploadRes.data.fileUrl
      const updateRes = await updateBabyAvatar(route.params.id, avatarUrl)

      if (updateRes.code === 200) {
        MessagePlugin.success('头像更新成功')
        router.back()
      } else {
        MessagePlugin.error(updateRes.message || '更新头像失败')
      }
    } else {
      MessagePlugin.error(uploadRes.message || '图片上传失败')
    }
  } catch (error) {
    console.error('上传失败:', error)
    MessagePlugin.error('上传失败，请重试')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchBaby()
})
</script>

<style scoped>
.baby-avatar-page {
  padding: 24px;
  background: #F9FAFB;
  min-height: calc(100vh - 120px);
}

.page-container {
  max-width: 900px;
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

/* 头像卡片 */
.content-wrapper {
  display: flex;
  justify-content: center;
}

.avatar-card {
  background: white;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  width: 100%;
}

.card-header {
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 1px solid #F3F4F6;
}

.card-header h2 {
  margin: 0 0 8px;
  font-size: 24px;
  font-weight: 600;
  color: #1F2937;
}

.card-header p {
  margin: 0;
  font-size: 14px;
  color: #6B7280;
}

/* 内容布局 */
.avatar-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 48px;
}

.current-avatar-section h3,
.upload-section h3 {
  margin: 0 0 20px;
  font-size: 16px;
  font-weight: 600;
  color: #374151;
}

/* 当前头像 */
.avatar-display {
  display: flex;
  justify-content: center;
  padding: 40px;
  background: #F9FAFB;
  border-radius: 12px;
}

.avatar-display :deep(.t-avatar) {
  background: linear-gradient(135deg, #818CF8 0%, #A78BFA 100%);
}

/* 预览区域 */
.preview-area {
  margin-bottom: 20px;
}

.preview-label {
  margin: 0 0 12px;
  font-size: 14px;
  color: #6B7280;
  text-align: center;
}

.preview-avatar {
  display: flex;
  justify-content: center;
}

.preview-avatar img {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #3B82F6;
}

/* 上传区域 */
.upload-area {
  margin-bottom: 20px;
}

.file-input {
  display: none;
}

.upload-trigger {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px;
  border: 2px dashed #D1D5DB;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  color: #9CA3AF;
}

.upload-trigger:hover {
  border-color: #3B82F6;
  background: #EFF6FF;
  color: #3B82F6;
}

.upload-text {
  margin: 12px 0 4px;
  font-size: 15px;
  font-weight: 500;
}

.upload-hint {
  margin: 0;
  font-size: 13px;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.action-buttons :deep(.t-button) {
  flex: 1;
}

/* 提示信息 */
.tips-box {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #EFF6FF;
  border-radius: 8px;
  color: #3B82F6;
  font-size: 13px;
}

/* 响应式 */
@media (max-width: 768px) {
  .avatar-content {
    grid-template-columns: 1fr;
    gap: 32px;
  }

  .avatar-card {
    padding: 24px;
  }
}
</style>
