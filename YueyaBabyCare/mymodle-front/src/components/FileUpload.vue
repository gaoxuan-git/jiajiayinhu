<template>
  <div class="file-upload-component">
    <el-upload
      ref="uploadRef"
      :action="uploadUrl"
      :headers="uploadHeaders"
      :data="uploadData"
      :multiple="multiple"
      :limit="limit"
      :accept="accept"
      :file-list="fileList"
      :auto-upload="autoUpload"
      :show-file-list="showFileList"
      :list-type="listType"
      :on-preview="handlePreview"
      :on-remove="handleRemove"
      :on-success="handleSuccess"
      :on-error="handleError"
      :on-progress="handleProgress"
      :on-exceed="handleExceed"
      :before-upload="beforeUpload"
      :drag="drag"
      :disabled="disabled"
    >
      <template v-if="drag">
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
      </template>
      <template v-else>
        <el-button type="primary" :icon="Upload">{{ buttonText }}</el-button>
      </template>
      <template v-if="tip" #tip>
        <div class="el-upload__tip">{{ tip }}</div>
      </template>
    </el-upload>

    <!-- 上传进度显示 -->
    <el-progress
      v-if="showProgress && uploadPercent > 0 && uploadPercent < 100"
      :percentage="uploadPercent"
      :status="uploadStatus"
    ></el-progress>

    <!-- 图片预览对话框 -->
    <el-dialog v-model="previewVisible" title="图片预览" width="60%">
      <img :src="previewImageUrl" style="width: 100%" alt="预览图片" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Upload, UploadFilled } from '@element-plus/icons-vue'
import { uploadFile, uploadFiles } from '@/api/file'

// Props
const props = defineProps({
  // 是否多选
  multiple: {
    type: Boolean,
    default: false
  },
  // 最大上传文件数
  limit: {
    type: Number,
    default: 10
  },
  // 接受的文件类型
  accept: {
    type: String,
    default: ''
  },
  // 文件列表
  modelValue: {
    type: Array,
    default: () => []
  },
  // 是否自动上传
  autoUpload: {
    type: Boolean,
    default: true
  },
  // 是否显示文件列表
  showFileList: {
    type: Boolean,
    default: true
  },
  // 文件列表类型
  listType: {
    type: String,
    default: 'text' // text/picture/picture-card
  },
  // 是否启用拖拽上传
  drag: {
    type: Boolean,
    default: false
  },
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false
  },
  // 按钮文字
  buttonText: {
    type: String,
    default: '点击上传'
  },
  // 提示文字
  tip: {
    type: String,
    default: ''
  },
  // 文件大小限制（MB）
  maxSize: {
    type: Number,
    default: 10
  },
  // 是否显示上传进度
  showProgress: {
    type: Boolean,
    default: true
  },
  // 额外的上传数据
  uploadData: {
    type: Object,
    default: () => ({})
  }
})

// Emits
const emit = defineEmits(['update:modelValue', 'success', 'error', 'remove', 'preview'])

// Data
const uploadRef = ref(null)
const fileList = ref([])
const uploadPercent = ref(0)
const uploadStatus = ref('')
const previewVisible = ref(false)
const previewImageUrl = ref('')

// Computed
const uploadUrl = computed(() => {
  return process.env.VUE_APP_BASE_API + '/file/upload'
})

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
})

// Watch
watch(() => props.modelValue, (newVal) => {
  if (newVal && newVal.length > 0) {
    fileList.value = newVal.map(url => ({
      name: getFileNameFromUrl(url),
      url: url
    }))
  } else {
    fileList.value = []
  }
}, { immediate: true })

// Methods
const beforeUpload = (file) => {
  // 检查文件大小
  const maxSize = props.maxSize * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error(`文件大小不能超过 ${props.maxSize}MB`)
    return false
  }

  // 检查文件类型
  if (props.accept) {
    const acceptTypes = props.accept.split(',').map(t => t.trim())
    const fileType = '.' + file.name.split('.').pop().toLowerCase()
    if (!acceptTypes.includes(fileType)) {
      ElMessage.error(`只能上传 ${props.accept} 格式的文件`)
      return false
    }
  }

  uploadPercent.value = 0
  uploadStatus.value = ''
  return true
}

const handleProgress = (event, file) => {
  uploadPercent.value = Math.floor(event.percent)
  uploadStatus.value = 'success'
}

const handleSuccess = (response, file, fileList) => {
  if (response.code === 200) {
    ElMessage.success('上传成功')
    const fileUrl = response.data.fileUrl
    
    // 更新文件列表
    const newFileList = fileList.map(f => {
      if (f.uid === file.uid) {
        return {
          ...f,
          url: fileUrl,
          response: response
        }
      }
      return f
    })
    
    // 提取URL列表
    const urls = newFileList
      .filter(f => f.url)
      .map(f => f.url)
    
    emit('update:modelValue', urls)
    emit('success', response.data, file)
    
    uploadPercent.value = 100
    setTimeout(() => {
      uploadPercent.value = 0
    }, 1000)
  } else {
    ElMessage.error(response.message || '上传失败')
    uploadStatus.value = 'exception'
  }
}

const handleError = (error, file) => {
  ElMessage.error('上传失败：' + error.message)
  uploadStatus.value = 'exception'
  emit('error', error, file)
}

const handleRemove = (file, fileList) => {
  const urls = fileList
    .filter(f => f.url)
    .map(f => f.url)
  emit('update:modelValue', urls)
  emit('remove', file)
}

const handlePreview = (file) => {
  // 判断是否为图片
  const imageTypes = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp']
  const fileType = file.name.split('.').pop().toLowerCase()
  
  if (imageTypes.includes(fileType)) {
    previewImageUrl.value = file.url
    previewVisible.value = true
  }
  
  emit('preview', file)
}

const handleExceed = (files, fileList) => {
  ElMessage.warning(`最多只能上传 ${props.limit} 个文件`)
}

const getFileNameFromUrl = (url) => {
  return url.split('/').pop()
}

// 暴露方法
const submit = () => {
  uploadRef.value.submit()
}

const clearFiles = () => {
  uploadRef.value.clearFiles()
  emit('update:modelValue', [])
}

defineExpose({
  submit,
  clearFiles
})
</script>

<style scoped lang="scss">
.file-upload-component {
  .el-upload__tip {
    margin-top: 7px;
    color: #999;
    font-size: 12px;
  }

  .el-progress {
    margin-top: 10px;
  }
}
</style>
