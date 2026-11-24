<template>
  <div class="avatar-uploader">
    <div class="avatar-wrapper">
      <t-avatar
        :size="size"
        :image="avatarUrl"
        :icon="!avatarUrl ? UserIcon : undefined"
      />
      <div v-if="loading" class="avatar-mask">
        <t-loading size="small" />
      </div>
    </div>

    <div class="avatar-actions">
      <input
        ref="inputRef"
        class="file-input"
        type="file"
        accept="image/*"
        @change="onFileChange"
      />
      <t-button variant="outline" size="small" :loading="loading" @click="chooseFile">
        更换头像
      </t-button>
      <p class="avatar-tips">支持 JPG、PNG 格式，最大 {{ maxSizeMB }}MB</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import { UserIcon } from 'tdesign-icons-vue-next'
import { uploadFile } from '@/api/file'
import { updateAvatar } from '@/api/member'
import { getImageUrl } from '@/utils/image'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  size: {
    type: String,
    default: '72px'
  },
  maxSizeMB: {
    type: Number,
    default: 2
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const inputRef = ref(null)
const loading = ref(false)

// 处理头像路径，确保可以正确显示
const avatarUrl = computed(() => getImageUrl(props.modelValue))

const chooseFile = () => {
  if (loading.value) return
  inputRef.value?.click()
}

const validateFile = (file) => {
  if (!file) return '请选择图片文件'

  const isImage = (file.type || '').startsWith('image/')
  if (!isImage) return '仅支持图片格式'

  // 只放行常见图片类型，避免某些浏览器/系统给出奇怪的 image/*
  const okTypes = ['image/jpeg', 'image/png', 'image/jpg', 'image/webp']
  if (file.type && !okTypes.includes(file.type)) {
    return '仅支持 JPG/PNG/WEBP 图片'
  }

  const maxBytes = props.maxSizeMB * 1024 * 1024
  if (file.size > maxBytes) return `图片大小不能超过 ${props.maxSizeMB}MB`

  return null
}

const onFileChange = async (e) => {
  const file = e?.target?.files?.[0]
  // 清空 input，确保同一文件重复选择也会触发 change
  if (e?.target) e.target.value = ''

  const err = validateFile(file)
  if (err) {
    MessagePlugin.warning(err)
    return
  }

  loading.value = true
  try {
    // 1) 上传文件
    const uploadRes = await uploadFile(file)
    const fileUrl = uploadRes?.data?.fileUrl
    if (!fileUrl) {
      MessagePlugin.error('上传成功但未返回文件地址')
      return
    }

    // 2) 更新家长头像
    await updateAvatar(fileUrl)

    emit('update:modelValue', fileUrl)
    emit('success', fileUrl)
    MessagePlugin.success('头像更新成功')
  } catch (error) {
    // 错误提示已由 request 拦截器统一处理，这里不再重复弹窗
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.avatar-uploader {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-wrapper {
  position: relative;
}

.avatar-wrapper :deep(.t-avatar) {
  background: linear-gradient(135deg, #3B82F6 0%, #2563EB 100%);
}

.avatar-mask {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.avatar-tips {
  margin: 0;
  font-size: 12px;
  color: #9CA3AF;
}

.file-input {
  display: none;
}
</style>
