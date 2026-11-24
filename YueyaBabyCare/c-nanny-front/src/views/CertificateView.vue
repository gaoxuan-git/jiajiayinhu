<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部导航 -->
    <div class="bg-white border-b border-gray-200 sticky top-0 z-10">
      <div class="flex items-center px-4 py-3">
        <t-button 
          variant="text" 
          @click="goBack"
          class="text-gray-600"
        >
          <template #icon>
            <chevron-left-icon />
          </template>
        </t-button>
        <h1 class="flex-1 text-center text-lg font-semibold text-gray-800">资格证书</h1>
        <div class="w-10"></div>
      </div>
    </div>

    <!-- 内容区 -->
    <div class="p-4">
      <!-- 上传提示 -->
      <div class="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-4">
        <div class="flex items-start gap-3">
          <info-circle-icon class="w-5 h-5 text-blue-500 shrink-0 mt-0.5" />
          <div class="text-sm text-blue-700">
            <p class="font-medium mb-1">证书上传说明</p>
            <p class="text-blue-600">请上传清晰的证书照片，支持 JPG、PNG 格式，单张不超过 5MB</p>
          </div>
        </div>
      </div>

      <!-- 上传按钮 -->
      <div class="mb-6">
        <FileUpload
          v-model="uploadedFiles"
          type="image"
          :max="1"
          placeholder="上传证书"
          tip="请上传清晰的证书照片，支持 JPG、PNG 格式，单张不超过 5MB"
          @success="handleUploadSuccess"
        />
      </div>

      <!-- 证书列表 -->
      <div v-if="loading" class="text-center py-12">
        <t-loading size="large" />
        <p class="text-gray-500 mt-4">加载中...</p>
      </div>

      <div v-else-if="certificates.length === 0" class="text-center py-12">
        <div class="inline-flex items-center justify-center w-16 h-16 bg-gray-100 rounded-full mb-4">
          <file-icon class="w-8 h-8 text-gray-400" />
        </div>
        <p class="text-gray-500">暂无证书</p>
        <p class="text-sm text-gray-400 mt-1">上传您的资格证书以提升信任度</p>
      </div>

      <div v-else class="grid grid-cols-2 gap-4">
        <div 
          v-for="(cert, index) in certificates" 
          :key="index"
          class="relative group"
        >
          <!-- 证书图片 -->
          <div class="aspect-[3/4] bg-white rounded-lg shadow overflow-hidden">
            <img 
              :src="cert" 
              alt="证书"
              class="w-full h-full object-cover"
              @click="previewImage(cert)"
            />
          </div>
          
          <!-- 删除按钮 -->
          <div class="absolute top-2 right-2">
            <t-button 
              theme="danger" 
              shape="circle" 
              size="small"
              @click="deleteCert(cert, index)"
            >
              <template #icon>
                <delete-icon />
              </template>
            </t-button>
          </div>

          <!-- 序号 -->
          <div class="absolute top-2 left-2 px-2 py-1 bg-black/50 text-white text-xs rounded">
            证书 {{ index + 1 }}
          </div>
        </div>
      </div>
    </div>

    <!-- 图片预览对话框 -->
    <t-image-viewer
      v-model:visible="previewVisible"
      :images="[currentPreviewImage]"
      :index="0"
    />

    <!-- 删除确认对话框 -->
    <t-dialog
      v-model:visible="deleteDialogVisible"
      header="确认删除"
      :on-confirm="confirmDelete"
      :on-cancel="() => deleteDialogVisible = false"
    >
      <p>确定要删除这张证书吗？</p>
    </t-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import { 
  ChevronLeftIcon, 
  InfoCircleIcon, 
  FileIcon,
  DeleteIcon 
} from 'tdesign-icons-vue-next'
import { getCertificates, addCertificate, deleteCertificate } from '@/api/nanny'
import FileUpload from '@/components/FileUpload.vue'

const router = useRouter()
const loading = ref(false)
const certificates = ref([])
const uploadedFiles = ref([])
const previewVisible = ref(false)
const currentPreviewImage = ref('')
const deleteDialogVisible = ref(false)
const deletingCert = ref(null)
const deletingIndex = ref(-1)

// 获取证书列表
const fetchCertificates = async () => {
  loading.value = true
  try {
    const res = await getCertificates()
    certificates.value = res.data || []
  } catch (error) {
    console.error('获取证书列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 监听上传文件变化
watch(uploadedFiles, async (newFiles) => {
  if (newFiles && newFiles.length > 0) {
    const newUrl = newFiles[0]
    
    try {
      // 调用添加证书接口
      await addCertificate(newUrl)
      
      MessagePlugin.success('证书添加成功')
      
      // 重新加载证书列表
      await fetchCertificates()
      
      // 清空上传列表
      uploadedFiles.value = []
    } catch (error) {
      console.error('添加证书失败:', error)
      uploadedFiles.value = []
    }
  }
})

// 上传成功回调
const handleUploadSuccess = () => {
  // FileUpload组件会自动处理，这里可以不做额外操作
}

// 预览图片
const previewImage = (url) => {
  currentPreviewImage.value = url
  previewVisible.value = true
}

// 删除证书
const deleteCert = (cert, index) => {
  deletingCert.value = cert
  deletingIndex.value = index
  deleteDialogVisible.value = true
}

// 确认删除
const confirmDelete = async () => {
  try {
    await deleteCertificate(deletingCert.value)
    MessagePlugin.success('删除成功')
    
    // 从列表中移除
    certificates.value.splice(deletingIndex.value, 1)
    
    deleteDialogVisible.value = false
  } catch (error) {
    console.error('删除证书失败:', error)
  }
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  fetchCertificates()
})
</script>
