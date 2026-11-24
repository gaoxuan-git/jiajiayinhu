<template>
  <div>
    <!-- 图片上传模式 -->
    <t-upload
      v-if="type === 'image'"
      v-model="fileListInternal"
      theme="image"
      :action="uploadAction"
      :headers="uploadHeaders"
      :max="max"
      :multiple="multiple"
      :before-upload="handleBeforeUpload"
      :on-success="handleSuccess"
      :on-fail="handleFail"
      :on-remove="handleRemove"
      accept="image/*"
      :disabled="disabled"
    >
      <template #default>
        <div class="flex flex-col items-center justify-center w-full h-32 bg-gray-100 border-2 border-dashed border-gray-300 rounded-lg hover:border-blue-500 transition-colors cursor-pointer">
          <add-icon class="w-8 h-8 text-gray-400 mb-2" />
          <p class="text-sm text-gray-600">{{ placeholder || '点击上传图片' }}</p>
          <p v-if="tip" class="text-xs text-gray-400 mt-1">{{ tip }}</p>
        </div>
      </template>
    </t-upload>

    <!-- 文件上传模式 -->
    <t-upload
      v-else-if="type === 'file'"
      v-model="fileListInternal"
      :action="uploadAction"
      :headers="uploadHeaders"
      :max="max"
      :multiple="multiple"
      :before-upload="handleBeforeUpload"
      :on-success="handleSuccess"
      :on-fail="handleFail"
      :on-remove="handleRemove"
      :accept="accept"
      :disabled="disabled"
    >
      <template #default>
        <t-button theme="primary" :disabled="disabled">
          <template #icon><upload-icon /></template>
          {{ buttonText || '选择文件' }}
        </t-button>
        <div v-if="tip" class="text-xs text-gray-500 mt-2">{{ tip }}</div>
      </template>
    </t-upload>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import { AddIcon, UploadIcon } from 'tdesign-icons-vue-next'
import { UPLOAD_URL, API_BASE_URL } from '@/config'

const props = defineProps({
  // 上传类型：image（图片）、file（文件）
  type: {
    type: String,
    default: 'image'
  },
  // 文件列表（双向绑定）
  modelValue: {
    type: Array,
    default: () => []
  },
  // 最大上传数量
  max: {
    type: Number,
    default: 1
  },
  // 是否允许多选
  multiple: {
    type: Boolean,
    default: false
  },
  // 接受的文件类型
  accept: {
    type: String,
    default: 'image/*'
  },
  // 最大文件大小（MB）
  maxSize: {
    type: Number,
    default: 5
  },
  // 占位提示
  placeholder: {
    type: String,
    default: ''
  },
  // 提示文字
  tip: {
    type: String,
    default: ''
  },
  // 按钮文字（文件模式）
  buttonText: {
    type: String,
    default: ''
  },
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'success', 'fail'])

// 内部文件列表
const fileListInternal = ref([])

// 上传地址
const uploadAction = ref(UPLOAD_URL)

// 上传请求头（不需要token，因为已在拦截器中排除）
const uploadHeaders = ref({})

// 监听外部文件列表变化
watch(() => props.modelValue, (newVal) => {
  if (newVal && newVal.length > 0) {
    fileListInternal.value = newVal.map((url, index) => {
      // 开发环境通过代理直接使用相对路径
      // 生产环境如果 API_BASE_URL 有值则拼接完整URL
      let fullUrl = url
      if (url && url.startsWith('/') && API_BASE_URL) {
        fullUrl = API_BASE_URL + url
      }
      
      return {
        url: fullUrl,
        name: `file-${index}`,
        status: 'success'
      }
    })
  } else {
    fileListInternal.value = []
  }
}, { immediate: true })

// 上传前校验
const handleBeforeUpload = (file) => {
  // 检查文件大小
  const isLtMaxSize = file.size / 1024 / 1024 < props.maxSize
  if (!isLtMaxSize) {
    MessagePlugin.error(`文件大小不能超过 ${props.maxSize}MB`)
    return false
  }

  // 如果是图片模式，检查文件类型
  if (props.type === 'image') {
    const isImage = file.type.startsWith('image/')
    if (!isImage) {
      MessagePlugin.error('只能上传图片文件')
      return false
    }
  }

  return true
}

// 上传成功
const handleSuccess = (context) => {
  try {
    console.log('上传成功回调 context:', context)

    // TDesign Upload 的 success 回调参数是 { response, file, ... }
    let response = null

    // 尝试不同的响应格式
    if (context && context.response) {
      response = context.response
    } else if (context && context.XMLHttpRequest) {
      // 有些情况下可能在 XMLHttpRequest.response 中
      response = context.XMLHttpRequest.response
    } else if (context) {
      // 直接就是 response
      response = context
    }

    // 如果 response 是字符串，尝试解析为 JSON
    if (typeof response === 'string') {
      try {
        response = JSON.parse(response)
      } catch (e) {
        console.error('解析响应 JSON 失败:', e)
      }
    }

    console.log('解析后的 response:', response)

    if (!response) {
      console.error('上传响应无效:', context)
      throw new Error('上传响应数据为空')
    }

    // 后端返回的数据结构：{ code: 200, data: { fileUrl: '...' }, message: '...' }
    // 获取文件URL，支持多种可能的字段名
    let fileUrl = null

    if (response.data && typeof response.data === 'object') {
      fileUrl = response.data.fileUrl || response.data.url
    } else if (response.data && typeof response.data === 'string') {
      fileUrl = response.data
    } else if (response.fileUrl) {
      fileUrl = response.fileUrl
    } else if (response.url) {
      fileUrl = response.url
    }

    console.log('提取的 fileUrl:', fileUrl)
    
    if (fileUrl) {
      // 将新上传的文件URL添加到现有列表中
      const currentUrls = Array.isArray(props.modelValue) ? [...props.modelValue] : []
      
      // 如果不是多选模式，清空现有列表
      if (!props.multiple) {
        currentUrls.length = 0
      }
      
      // 添加新的文件URL（避免重复）
      // 注意：保存的是相对路径，显示时会自动拼接 API_BASE_URL
      if (!currentUrls.includes(fileUrl)) {
        currentUrls.push(fileUrl)
      }
      
      // 更新外部数据
      emit('update:modelValue', currentUrls)
      emit('success', fileUrl, response)
      
      MessagePlugin.success('上传成功')
    } else {
      throw new Error('未返回文件地址')
    }
  } catch (error) {
    console.error('处理上传成功回调失败:', error)
    MessagePlugin.error('上传失败：' + (error?.message || '未知错误'))
    emit('fail', error)
  }
}

// 上传失败
const handleFail = (context) => {
  console.error('上传失败:', context)
  const fileName = context?.file?.name || '文件'
  const response = context?.response
  MessagePlugin.error(`${fileName} 上传失败`)
  emit('fail', response)
}

// 移除文件
const handleRemove = (context) => {
  if (!context || !context.file) {
    return
  }
  
  const file = context.file
  // 获取剩余文件的URL
  const urls = fileListInternal.value
    .filter(item => item && item.url !== file.url && item.status === 'success')
    .map(item => item.url)
  
  // 更新外部数据
  emit('update:modelValue', urls)
}
</script>
