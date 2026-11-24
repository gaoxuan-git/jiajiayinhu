<template>
  <div class="growth-page">
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
            <h1 class="page-title">成长记录</h1>
            <p class="page-desc">{{ baby?.name }}的成长历程</p>
          </div>
          <t-button theme="primary" @click="openAddDialog">
            <template #icon><t-icon name="add" /></template>
            添加记录
          </t-button>
        </div>

        <!-- 记录类型筛选 -->
        <div class="filter-bar">
          <t-radio-group v-model="currentType" variant="default-filled" @change="onTypeChange">
            <t-radio-button :value="null">全部</t-radio-button>
            <t-radio-button :value="1">饮食</t-radio-button>
            <t-radio-button :value="2">睡眠</t-radio-button>
            <t-radio-button :value="3">排便</t-radio-button>
            <t-radio-button :value="4">活动</t-radio-button>
            <t-radio-button :value="5">其他</t-radio-button>
          </t-radio-group>
          <span class="record-count">共 {{ records.length }} 条记录</span>
        </div>

        <!-- 空状态 -->
        <div v-if="records.length === 0" class="empty-state">
          <t-icon name="file-paste" size="64" />
          <p>暂无成长记录</p>
          <t-button theme="primary" @click="openAddDialog">添加第一条记录</t-button>
        </div>

        <!-- 时间轴记录 -->
        <div v-else class="timeline-card">
          <div class="card-header">
            <h2>成长时间轴</h2>
          </div>
          <div class="timeline">
            <div v-for="record in records" :key="record.id" class="timeline-item">
              <div class="timeline-dot" :class="getTypeClass(record.recordType)"></div>
              <div class="timeline-date">
                <span class="date">{{ record.recordDate }}</span>
                <t-tag size="small" :theme="getTypeTheme(record.recordType)" variant="light">
                  {{ record.recordTypeName }}
                </t-tag>
              </div>
              <div class="timeline-content">
                <div class="content-header">
                  <h3>{{ record.title }}</h3>
                  <div class="actions">
                    <t-button variant="text" theme="primary" size="small" @click="openDetailDialog(record)">
                      <t-icon name="browse" />
                    </t-button>
                    <t-button variant="text" theme="primary" size="small" @click="openEditDialog(record)">
                      <t-icon name="edit" />
                    </t-button>
                    <t-button variant="text" theme="danger" size="small" @click="handleDelete(record)">
                      <t-icon name="delete" />
                    </t-button>
                  </div>
                </div>
                <p class="content-text">{{ record.content || '无详细内容' }}</p>
                <div class="content-images" v-if="record.images && record.images.length > 0">
                  <t-image
                    v-for="(img, idx) in record.images"
                    :key="idx"
                    :src="getImageUrl(img)"
                    fit="cover"
                    class="record-image"
                    @click="previewImages(record.images, idx)"
                  />
                </div>
                <div class="content-footer">
                  <span class="recorder">
                    <t-icon name="user" size="14" />
                    {{ record.recorderName || (record.recorderType === 1 ? '家长' : '保姆') }}
                  </span>
                  <span class="time">
                    <t-icon name="time" size="14" />
                    {{ formatTime(record.createTime) }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>

    <!-- 新增/编辑弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="isEdit ? '编辑成长记录' : '添加成长记录'"
      :confirm-btn="{ content: '保存', theme: 'primary', loading: submitting }"
      cancel-btn="取消"
      width="640px"
      @confirm="handleSubmit"
      @close="resetForm"
    >
      <t-form ref="formRef" :data="form" :rules="rules" label-align="top">
        <div class="form-row">
          <t-form-item label="记录日期" name="recordDate">
            <t-date-picker
              v-model="form.recordDate"
              placeholder="请选择记录日期"
              :disable-date="disableFutureDate"
              style="width: 100%"
            />
          </t-form-item>
          <t-form-item label="记录类型" name="recordType">
            <t-select v-model="form.recordType" placeholder="请选择记录类型" style="width: 100%">
              <t-option :value="1" label="饮食" />
              <t-option :value="2" label="睡眠" />
              <t-option :value="3" label="排便" />
              <t-option :value="4" label="活动" />
              <t-option :value="5" label="其他" />
            </t-select>
          </t-form-item>
        </div>

        <t-form-item label="标题" name="title">
          <t-input v-model="form.title" placeholder="请输入记录标题" maxlength="50" />
        </t-form-item>

        <t-form-item label="详细内容" name="content">
          <t-textarea
            v-model="form.content"
            placeholder="请输入详细内容（选填）"
            :maxlength="500"
            :rows="4"
          />
        </t-form-item>

        <t-form-item label="图片" name="images">
          <div class="image-upload-area">
            <div class="image-list">
              <div v-for="(img, idx) in form.images" :key="idx" class="image-item">
                <t-image :src="getImageUrl(img)" fit="cover" class="preview-image" />
                <t-button
                  class="remove-btn"
                  theme="danger"
                  size="small"
                  shape="circle"
                  @click="removeImage(idx)"
                >
                  <t-icon name="close" size="12" />
                </t-button>
              </div>
              <div v-if="form.images.length < 9" class="upload-trigger" @click="triggerUpload">
                <t-loading v-if="uploading" size="24px" />
                <template v-else>
                  <t-icon name="add" size="24" />
                  <span>上传图片</span>
                </template>
              </div>
              <input
                ref="fileInputRef"
                type="file"
                accept="image/*"
                style="display: none"
                @change="handleFileChange"
              />
            </div>
            <p class="upload-tip">最多上传9张图片，支持jpg、png格式</p>
          </div>
        </t-form-item>
      </t-form>
    </t-dialog>

    <!-- 详情弹窗 -->
    <t-dialog
      v-model:visible="detailDialogVisible"
      header="成长记录详情"
      :footer="false"
      width="600px"
    >
      <div class="detail-content" v-if="currentRecord">
        <div class="detail-header">
          <t-tag :theme="getTypeTheme(currentRecord.recordType)" size="medium">
            {{ currentRecord.recordTypeName }}
          </t-tag>
          <span class="detail-date">{{ currentRecord.recordDate }}</span>
        </div>
        <h3 class="detail-title">{{ currentRecord.title }}</h3>
        <p class="detail-text">{{ currentRecord.content || '无详细内容' }}</p>
        
        <div v-if="currentRecord.images && currentRecord.images.length > 0" class="detail-images">
          <t-image
            v-for="(img, idx) in currentRecord.images"
            :key="idx"
            :src="getImageUrl(img)"
            fit="cover"
            class="detail-image"
            @click="previewImages(currentRecord.images, idx)"
          />
        </div>

        <div class="detail-footer">
          <div class="detail-item">
            <span class="label">记录人</span>
            <span class="value">{{ currentRecord.recorderName || (currentRecord.recorderType === 1 ? '家长' : '保姆') }}</span>
          </div>
          <div class="detail-item">
            <span class="label">创建时间</span>
            <span class="value">{{ currentRecord.createTime }}</span>
          </div>
        </div>
      </div>
    </t-dialog>

    <!-- 删除确认弹窗 -->
    <t-dialog
      v-model:visible="deleteDialogVisible"
      header="确认删除"
      :confirm-btn="{ content: '删除', theme: 'danger', loading: deleting }"
      cancel-btn="取消"
      @confirm="confirmDelete"
    >
      <p>确定要删除这条成长记录吗？此操作不可恢复。</p>
    </t-dialog>

    <!-- 图片预览 -->
    <t-image-viewer
      v-model:visible="imageViewerVisible"
      :images="previewImageList"
      :default-index="previewIndex"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import { getBabyById } from '@/api/baby'
import {
  getGrowthRecords,
  addGrowthRecord,
  updateGrowthRecord,
  deleteGrowthRecord
} from '@/api/growth'
import { uploadFile } from '@/api/file'
import { getImageUrl } from '@/utils/image'

const route = useRoute()
const babyId = route.params.babyId

// 数据状态
const loading = ref(false)
const baby = ref(null)
const records = ref([])
const currentType = ref(null)

// 弹窗相关
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const deleteDialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const deleting = ref(false)
const currentRecord = ref(null)
const formRef = ref(null)

// 图片预览
const imageViewerVisible = ref(false)
const previewImageList = ref([])
const previewIndex = ref(0)

// 上传相关
const fileInputRef = ref(null)
const uploading = ref(false)

// 表单数据
const form = reactive({
  recordDate: '',
  recordType: null,
  title: '',
  content: '',
  images: []
})

// 表单验证规则
const rules = {
  recordDate: [{ required: true, message: '请选择记录日期', trigger: 'change' }],
  recordType: [{ required: true, message: '请选择记录类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }]
}

// 禁用未来日期
const disableFutureDate = (date) => date > new Date()

// 获取类型样式类
const getTypeClass = (type) => {
  const map = { 1: 'type-food', 2: 'type-sleep', 3: 'type-toilet', 4: 'type-activity', 5: 'type-other' }
  return map[type] || ''
}

// 获取类型主题色
const getTypeTheme = (type) => {
  const map = { 1: 'warning', 2: 'primary', 3: 'success', 4: 'danger', 5: 'default' }
  return map[type] || 'default'
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 16)
}

// 获取宝宝信息
const fetchBaby = async () => {
  try {
    const res = await getBabyById(babyId)
    if (res.code === 200) {
      baby.value = res.data
    }
  } catch (error) {
    console.error('获取宝宝信息失败:', error)
  }
}

// 获取成长记录列表
const fetchRecords = async () => {
  loading.value = true
  try {
    const res = await getGrowthRecords(babyId, currentType.value)
    if (res.code === 200) {
      records.value = res.data || []
    } else {
      MessagePlugin.error(res.message || '获取成长记录失败')
    }
  } catch (error) {
    console.error('获取成长记录失败:', error)
  } finally {
    loading.value = false
  }
}

// 类型切换
const onTypeChange = () => {
  fetchRecords()
}

// 打开新增弹窗
const openAddDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 打开编辑弹窗
const openEditDialog = (row) => {
  isEdit.value = true
  currentRecord.value = row
  Object.assign(form, {
    recordDate: row.recordDate,
    recordType: row.recordType,
    title: row.title,
    content: row.content || '',
    images: row.images || []
  })
  dialogVisible.value = true
}

// 打开详情弹窗
const openDetailDialog = (row) => {
  currentRecord.value = row
  detailDialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    recordDate: '',
    recordType: null,
    title: '',
    content: '',
    images: []
  })
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value?.validate()
  if (valid !== true) return

  submitting.value = true
  try {
    const data = { ...form }
    let res
    if (isEdit.value) {
      res = await updateGrowthRecord(currentRecord.value.id, data)
    } else {
      res = await addGrowthRecord(babyId, data)
    }

    if (res.code === 200) {
      MessagePlugin.success(isEdit.value ? '修改成功' : '添加成功')
      dialogVisible.value = false
      fetchRecords()
    } else {
      MessagePlugin.error(res.message || (isEdit.value ? '修改失败' : '添加失败'))
    }
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitting.value = false
  }
}

// 删除记录
const handleDelete = (row) => {
  currentRecord.value = row
  deleteDialogVisible.value = true
}

const confirmDelete = async () => {
  if (!currentRecord.value) return
  
  deleting.value = true
  try {
    const res = await deleteGrowthRecord(currentRecord.value.id)
    if (res.code === 200) {
      MessagePlugin.success('删除成功')
      fetchRecords()
    } else {
      MessagePlugin.error(res.message || '删除失败')
    }
  } catch (error) {
    console.error('删除失败:', error)
  } finally {
    deleting.value = false
    deleteDialogVisible.value = false
  }
}

// 触发文件选择
const triggerUpload = () => {
  if (uploading.value) return
  fileInputRef.value?.click()
}

// 处理文件选择
const handleFileChange = async (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  
  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    MessagePlugin.error('请选择图片文件')
    return
  }
  
  // 验证文件大小 (最大5MB)
  if (file.size > 5 * 1024 * 1024) {
    MessagePlugin.error('图片大小不能超过5MB')
    return
  }
  
  uploading.value = true
  try {
    const res = await uploadFile(file)
    if (res?.code === 200 && res?.data?.fileUrl) {
      form.images.push(res.data.fileUrl)
    } else {
      MessagePlugin.error(res?.message || '图片上传失败')
    }
  } catch (error) {
    console.error('上传图片失败:', error)
    MessagePlugin.error('图片上传失败')
  } finally {
    uploading.value = false
    // 清空input，允许再次选择同一文件
    if (fileInputRef.value) {
      fileInputRef.value.value = ''
    }
  }
}

// 移除图片
const removeImage = (idx) => {
  form.images.splice(idx, 1)
}

// 预览图片
const previewImages = (images, idx) => {
  previewImageList.value = images.map(img => getImageUrl(img))
  previewIndex.value = idx
  imageViewerVisible.value = true
}

onMounted(() => {
  fetchBaby()
  fetchRecords()
})
</script>

<style scoped>
.growth-page {
  padding: 24px;
  background: #F9FAFB;
  min-height: calc(100vh - 120px);
}

.page-container {
  max-width: 1000px;
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
  font-size: 24px;
  font-weight: 600;
  color: #1F2937;
}

.page-desc {
  margin: 0;
  font-size: 14px;
  color: #6B7280;
}

/* 筛选栏 */
.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.record-count {
  font-size: 14px;
  color: #6B7280;
}

/* 空状态 */
.empty-state {
  padding: 80px 20px;
  text-align: center;
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.empty-state :deep(.t-icon) {
  color: #D1D5DB;
}

.empty-state p {
  margin: 16px 0 24px;
  font-size: 14px;
  color: #6B7280;
}

/* 时间轴卡片 */
.timeline-card {
  background: white;
  border-radius: 12px;
  padding: 24px 32px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.card-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #F3F4F6;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1F2937;
}

/* 时间轴 */
.timeline {
  position: relative;
  padding-left: 32px;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 5px;
  top: 8px;
  bottom: 8px;
  width: 2px;
  background: linear-gradient(180deg, #3B82F6 0%, #DBEAFE 100%);
}

.timeline-item {
  position: relative;
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
}

.timeline-item:last-child {
  margin-bottom: 0;
}

/* 时间轴圆点 - 不同类型颜色 */
.timeline-dot {
  position: absolute;
  left: -29px;
  top: 8px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #3B82F6;
  border: 2px solid white;
  box-shadow: 0 0 0 3px #DBEAFE;
  z-index: 1;
}

.timeline-dot.type-food { background: #F59E0B; box-shadow: 0 0 0 3px #FEF3C7; }
.timeline-dot.type-sleep { background: #3B82F6; box-shadow: 0 0 0 3px #DBEAFE; }
.timeline-dot.type-toilet { background: #10B981; box-shadow: 0 0 0 3px #D1FAE5; }
.timeline-dot.type-activity { background: #EF4444; box-shadow: 0 0 0 3px #FEE2E2; }
.timeline-dot.type-other { background: #6B7280; box-shadow: 0 0 0 3px #E5E7EB; }

/* 时间轴日期 */
.timeline-date {
  flex-shrink: 0;
  width: 110px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding-top: 4px;
}

.timeline-date .date {
  color: #3B82F6;
  font-weight: 600;
  font-size: 14px;
}

/* 时间轴内容 */
.timeline-content {
  flex: 1;
  background: #F9FAFB;
  border-radius: 12px;
  padding: 20px;
  border: 2px solid transparent;
  transition: all 0.2s;
}

.timeline-content:hover {
  border-color: #3B82F6;
  background: white;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.1);
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.content-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1F2937;
}

.actions {
  display: flex;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.2s;
}

.timeline-content:hover .actions {
  opacity: 1;
}

.content-text {
  margin: 0 0 12px 0;
  color: #4B5563;
  font-size: 14px;
  line-height: 1.6;
}

/* 图片列表 */
.content-images {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.record-image {
  width: 100px;
  height: 80px;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.2s;
}

.record-image:hover {
  transform: scale(1.05);
}

/* 底部信息 */
.content-footer {
  display: flex;
  gap: 16px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #E5E7EB;
  font-size: 12px;
  color: #9CA3AF;
}

.content-footer .recorder,
.content-footer .time {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 表单样式 */
.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

/* 图片上传 */
.image-upload-area {
  width: 100%;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.image-item {
  position: relative;
  width: 100px;
  height: 100px;
}

.preview-image {
  width: 100%;
  height: 100%;
  border-radius: 8px;
}

.remove-btn {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 24px !important;
  height: 24px !important;
  min-width: 24px !important;
  padding: 0 !important;
}

.upload-trigger {
  width: 100px;
  height: 100px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  background: #F9FAFB;
  border: 2px dashed #D1D5DB;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  color: #9CA3AF;
  font-size: 12px;
}

.upload-trigger:hover {
  border-color: #3B82F6;
  color: #3B82F6;
  background: #EFF6FF;
}

.upload-tip {
  margin: 8px 0 0;
  font-size: 12px;
  color: #9CA3AF;
}

/* 详情弹窗 */
.detail-content {
  padding: 8px 0;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.detail-date {
  font-size: 14px;
  color: #6B7280;
}

.detail-title {
  margin: 0 0 12px;
  font-size: 20px;
  font-weight: 600;
  color: #1F2937;
}

.detail-text {
  margin: 0 0 16px;
  font-size: 14px;
  line-height: 1.7;
  color: #4B5563;
}

.detail-images {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 20px;
}

.detail-image {
  width: 120px;
  height: 90px;
  border-radius: 8px;
  cursor: pointer;
}

.detail-footer {
  display: flex;
  gap: 24px;
  padding-top: 16px;
  border-top: 1px solid #F3F4F6;
}

.detail-footer .detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-footer .label {
  font-size: 12px;
  color: #9CA3AF;
}

.detail-footer .value {
  font-size: 14px;
  color: #374151;
}

/* 响应式 */
@media (max-width: 768px) {
  .filter-bar {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .timeline-item {
    flex-direction: column;
    gap: 8px;
  }

  .timeline-date {
    width: auto;
    flex-direction: row;
    align-items: center;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .actions {
    opacity: 1;
  }
}
</style>
