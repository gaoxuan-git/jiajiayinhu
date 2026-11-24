<template>
  <div class="min-h-screen bg-gray-50 pb-8">
    <!-- Header -->
    <div class="bg-gradient-to-br from-blue-500 to-blue-600 pb-24 pt-6 px-4 text-white relative overflow-hidden">
      <!-- 装饰圆形 -->
      <div class="absolute top-0 right-0 w-40 h-40 bg-white/10 rounded-full -mr-16 -mt-16"></div>
      <div class="absolute bottom-0 left-0 w-32 h-32 bg-white/10 rounded-full -ml-12 -mb-12"></div>

      <!-- 顶部导航 -->
      <div class="flex items-center justify-between relative z-10 mb-6">
        <div class="flex items-center gap-3">
          <chevron-left-icon
            size="24px"
            class="text-white/90 cursor-pointer hover:text-white transition-colors"
            @click="goBack"
          />
          <h1 class="text-lg font-semibold">个人信息</h1>
        </div>
        <t-button
          v-if="!isEditing"
          theme="default"
          size="medium"
          class="bg-white text-blue-600 font-medium shadow-md hover:bg-blue-50 border-0 px-5"
          @click="startEdit"
        >
          <edit-icon size="18px" class="mr-1" />
          编辑资料
        </t-button>
        <div v-else class="flex items-center gap-2">
          <t-button
            theme="default"
            variant="text"
            size="small"
            class="text-white/80 hover:text-white"
            @click="cancelEdit"
          >
            取消
          </t-button>
          <t-button
            theme="primary"
            size="small"
            :loading="saving"
            class="bg-white text-blue-600 hover:bg-blue-50"
            @click="handleSave"
          >
            保存
          </t-button>
        </div>
      </div>

      <!-- 头像区域 -->
      <div class="flex flex-col items-center relative z-10">
        <div class="relative">
          <t-avatar
            size="96px"
            :image="displayAvatar"
            class="border-4 border-white/30 shadow-lg"
          />
          <div
            v-if="isEditing"
            class="absolute bottom-0 right-0 w-8 h-8 bg-white rounded-full shadow-md flex items-center justify-center cursor-pointer hover:bg-gray-50 transition-colors"
            @click="triggerAvatarUpload"
          >
            <camera-icon size="16px" class="text-blue-500" />
          </div>
        </div>
        <h2 class="text-xl font-bold mt-3">{{ formData.realName || '未设置姓名' }}</h2>
        <p class="text-blue-100 text-sm mt-1">{{ formData.phone || '未绑定手机' }}</p>

        <!-- 隐藏的文件上传 -->
        <input
          ref="avatarInput"
          type="file"
          accept="image/*"
          class="hidden"
          @change="handleAvatarChange"
        />
      </div>
    </div>

    <!-- Content -->
    <div v-if="loading" class="flex items-center justify-center py-20">
      <t-loading size="large" />
    </div>

    <div v-else class="px-4 -mt-16 relative z-20 space-y-4">
      <!-- 基本信息卡片 -->
      <div class="bg-white rounded-xl shadow-md overflow-hidden">
        <div class="px-5 py-4 border-b border-gray-100 flex items-center gap-2">
          <user-icon size="18px" class="text-blue-500" />
          <h3 class="text-base font-semibold text-gray-800">基本信息</h3>
        </div>

        <div class="divide-y divide-gray-50">
          <!-- 真实姓名 -->
          <div class="px-5 py-4 flex items-center justify-between">
            <span class="text-gray-500 text-sm">真实姓名</span>
            <div v-if="!isEditing" class="text-gray-800 text-sm font-medium">
              {{ formData.realName || '未设置' }}
            </div>
            <t-input
              v-else
              v-model="formData.realName"
              placeholder="请输入真实姓名"
              class="w-48 text-right"
              align="right"
              borderless
            />
          </div>

          <!-- 手机号 -->
          <div class="px-5 py-4 flex items-center justify-between">
            <span class="text-gray-500 text-sm">手机号</span>
            <div v-if="!isEditing" class="text-gray-800 text-sm font-medium">
              {{ formData.phone || '未绑定' }}
            </div>
            <t-input
              v-else
              v-model="formData.phone"
              placeholder="请输入手机号"
              class="w-48 text-right"
              align="right"
              borderless
            />
          </div>

          <!-- 邮箱 -->
          <div class="px-5 py-4 flex items-center justify-between">
            <span class="text-gray-500 text-sm">邮箱</span>
            <div v-if="!isEditing" class="text-gray-800 text-sm font-medium">
              {{ formData.email || '未绑定' }}
            </div>
            <t-input
              v-else
              v-model="formData.email"
              placeholder="请输入邮箱"
              class="w-48 text-right"
              align="right"
              borderless
            />
          </div>

          <!-- 性别 -->
          <div class="px-5 py-4 flex items-center justify-between">
            <span class="text-gray-500 text-sm">性别</span>
            <div v-if="!isEditing" class="text-gray-800 text-sm font-medium">
              {{ formData.gender === 1 ? '男' : '女' }}
            </div>
            <t-radio-group v-else v-model="formData.gender" variant="default-filled">
              <t-radio-button :value="1">男</t-radio-button>
              <t-radio-button :value="2">女</t-radio-button>
            </t-radio-group>
          </div>

          <!-- 身份证号 -->
          <div class="px-5 py-4 flex items-center justify-between">
            <span class="text-gray-500 text-sm">身份证号</span>
            <div v-if="!isEditing" class="text-gray-800 text-sm font-medium">
              {{ maskedIdCard || '未设置' }}
            </div>
            <t-input
              v-else
              v-model="formData.idCard"
              placeholder="请输入身份证号"
              class="w-48 text-right"
              align="right"
              borderless
            />
          </div>

          <!-- 出生日期 -->
          <div class="px-5 py-4 flex items-center justify-between">
            <span class="text-gray-500 text-sm">出生日期</span>
            <div v-if="!isEditing" class="text-gray-800 text-sm font-medium">
              {{ formData.birthDate || '未设置' }}
            </div>
            <t-date-picker
              v-else
              v-model="formData.birthDate"
              placeholder="请选择"
              format="YYYY-MM-DD"
              class="w-48"
              borderless
            />
          </div>

          <!-- 工作年限 -->
          <div class="px-5 py-4 flex items-center justify-between">
            <span class="text-gray-500 text-sm">工作年限</span>
            <div v-if="!isEditing" class="text-gray-800 text-sm font-medium">
              {{ formData.workYears || 0 }} 年
            </div>
            <div v-else class="flex items-center gap-2">
              <t-input-number
                v-model="formData.workYears"
                :min="0"
                :max="50"
                theme="normal"
                class="w-24"
              />
              <span class="text-gray-500 text-sm">年</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 个人介绍卡片 -->
      <div class="bg-white rounded-xl shadow-md overflow-hidden">
        <div class="px-5 py-4 border-b border-gray-100 flex items-center gap-2">
          <file-icon size="18px" class="text-emerald-500" />
          <h3 class="text-base font-semibold text-gray-800">个人介绍</h3>
        </div>

        <div class="p-5">
          <div v-if="!isEditing" class="text-gray-600 text-sm leading-relaxed">
            {{ formData.introduction || '暂无个人介绍，点击编辑添加...' }}
          </div>
          <div v-else>
            <t-textarea
              v-model="formData.introduction"
              placeholder="介绍一下自己的工作经验、服务特长、性格特点等..."
              :maxlength="500"
              :autosize="{ minRows: 4, maxRows: 8 }"
            />
            <div class="mt-2 flex items-center justify-between text-xs">
              <span class="text-gray-400">建议包含：工作经验、服务特长、获得证书、性格特点等</span>
              <span class="text-gray-500">{{ formData.introduction?.length || 0 }}/500</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 资格证书卡片 -->
      <div class="bg-white rounded-xl shadow-md overflow-hidden">
        <div class="px-5 py-4 border-b border-gray-100 flex items-center justify-between">
          <div class="flex items-center gap-2">
            <secured-icon size="18px" class="text-amber-500" />
            <h3 class="text-base font-semibold text-gray-800">资格证书</h3>
          </div>
          <t-button
            theme="primary"
            size="small"
            variant="text"
            @click="goToCertificates"
          >
            管理证书
            <chevron-right-icon size="16px" />
          </t-button>
        </div>

        <div class="p-5">
          <div v-if="certificates.length > 0" class="grid grid-cols-3 gap-3">
            <div
              v-for="(cert, index) in displayCertificates"
              :key="index"
              class="aspect-square rounded-lg overflow-hidden border border-gray-100 shadow-sm hover:shadow-md transition-shadow cursor-pointer"
              @click="previewCert(cert)"
            >
              <img :src="cert" class="w-full h-full object-cover" alt="证书" />
            </div>
          </div>
          <div v-else class="text-center py-8">
            <div class="w-16 h-16 mx-auto mb-3 bg-gray-100 rounded-full flex items-center justify-center">
              <file-icon size="32px" class="text-gray-300" />
            </div>
            <p class="text-gray-400 text-sm">暂无证书</p>
            <p class="text-gray-300 text-xs mt-1">点击"管理证书"上传您的资格证书</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 图片预览 -->
    <t-image-viewer
      v-model:visible="previewVisible"
      :images="previewImages"
      :default-index="previewIndex"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import {
  ChevronLeftIcon,
  ChevronRightIcon,
  FileIcon,
  EditIcon,
  UserIcon,
  CameraIcon,
  SecuredIcon
} from 'tdesign-icons-vue-next'
import { getProfile, updateProfile } from '@/api/nanny'
import { API_BASE_URL, UPLOAD_URL } from '@/config'
import request from '@/utils/request'

const router = useRouter()
const loading = ref(false)
const saving = ref(false)
const isEditing = ref(false)
const avatarInput = ref(null)
const certificates = ref([])
const previewVisible = ref(false)
const previewImages = ref([])
const previewIndex = ref(0)

// 原始数据备份（用于取消编辑时恢复）
const originalData = ref(null)

const formData = ref({
  realName: '',
  phone: '',
  email: '',
  avatar: '',
  gender: 2,
  idCard: '',
  birthDate: '',
  workYears: 0,
  introduction: ''
})

// 计算属性：显示头像
const displayAvatar = computed(() => {
  if (formData.value.avatar) {
    if (formData.value.avatar.startsWith('data:') || formData.value.avatar.startsWith('http')) {
      return formData.value.avatar
    }
    return API_BASE_URL + formData.value.avatar
  }
  return 'https://tdesign.gtimg.com/site/avatar.jpg'
})

// 计算属性：脱敏身份证号
const maskedIdCard = computed(() => {
  const id = formData.value.idCard
  if (!id || id.length < 10) return id
  return id.substring(0, 6) + '********' + id.substring(id.length - 4)
})

// 计算属性：处理证书URL
const displayCertificates = computed(() => {
  return certificates.value.map(cert => {
    if (cert && cert.startsWith('/')) {
      return API_BASE_URL + cert
    }
    return cert
  })
})

// 开始编辑
const startEdit = () => {
  originalData.value = JSON.parse(JSON.stringify(formData.value))
  isEditing.value = true
}

// 取消编辑
const cancelEdit = () => {
  if (originalData.value) {
    formData.value = JSON.parse(JSON.stringify(originalData.value))
  }
  isEditing.value = false
}

// 触发头像上传
const triggerAvatarUpload = () => {
  avatarInput.value?.click()
}

// 处理头像选择 - 上传到服务器
const handleAvatarChange = async (e) => {
  const file = e.target.files?.[0]
  if (!file) return

  if (file.size > 2 * 1024 * 1024) {
    MessagePlugin.warning('图片大小不能超过2MB')
    return
  }

  try {
    // 创建 FormData 上传文件
    const formDataUpload = new FormData()
    formDataUpload.append('file', file)

    const res = await request({
      url: UPLOAD_URL,
      method: 'post',
      data: formDataUpload,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    if (res.code === 200 && res.data?.fileUrl) {
      // 保存相对路径
      formData.value.avatar = res.data.fileUrl
      MessagePlugin.success('头像上传成功')
    } else {
      MessagePlugin.error(res.message || '头像上传失败')
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    MessagePlugin.error('头像上传失败')
  }

  // 清空 input，允许重复选择同一文件
  e.target.value = ''
}

// 获取用户信息
const fetchUserInfo = async () => {
  loading.value = true
  try {
    const res = await getProfile()
    const data = res.data || {}

    formData.value = {
      realName: data.realName || '',
      phone: data.phone || '',
      email: data.email || '',
      avatar: data.avatar || '',
      gender: data.gender || 2,
      idCard: data.idCard || '',
      birthDate: data.birthDate || '',
      workYears: data.workYears || 0,
      introduction: data.introduction || ''
    }

    // 设置证书
    if (data.certificate) {
      try {
        const parsed = JSON.parse(data.certificate)
        certificates.value = Array.isArray(parsed) ? parsed : []
      } catch (e) {
        certificates.value = []
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    MessagePlugin.error('获取用户信息失败')
  } finally {
    loading.value = false
  }
}

// 保存
const handleSave = async () => {
  // 简单验证
  if (!formData.value.realName) {
    MessagePlugin.warning('请输入真实姓名')
    return
  }
  if (formData.value.phone && !/^1[3-9]\d{9}$/.test(formData.value.phone)) {
    MessagePlugin.warning('手机号格式不正确')
    return
  }

  saving.value = true
  try {
    await updateProfile(formData.value)
    MessagePlugin.success('保存成功')
    isEditing.value = false
    originalData.value = null
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

// 预览证书
const previewCert = (cert) => {
  previewImages.value = displayCertificates.value
  previewIndex.value = displayCertificates.value.indexOf(cert)
  previewVisible.value = true
}

// 跳转到证书管理
const goToCertificates = () => {
  router.push('/certificates')
}

// 返回
const goBack = () => {
  router.back()
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
/* 输入框右对齐样式 */
:deep(.t-input__inner) {
  text-align: right;
}

:deep(.t-input--borderless) {
  background: transparent;
}

:deep(.t-date-picker--borderless .t-input) {
  background: transparent;
}

/* 单选按钮组样式 */
:deep(.t-radio-group) {
  --td-radius-default: 16px;
}

:deep(.t-radio-button__label) {
  padding: 4px 12px;
  font-size: 13px;
}
</style>
