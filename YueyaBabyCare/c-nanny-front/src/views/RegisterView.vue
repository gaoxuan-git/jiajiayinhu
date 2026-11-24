<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-blue-100 py-8 px-4">
    <div class="w-full max-w-md mx-auto">
      <!-- 顶部返回按钮 -->
      <div class="mb-6">
        <t-button 
          variant="text" 
          @click="goBack"
          class="text-gray-600"
        >
          <template #icon>
            <chevron-left-icon />
          </template>
          返回登录
        </t-button>
      </div>

      <!-- 标题 -->
      <div class="text-center mb-6">
        <h1 class="text-2xl font-bold text-gray-800 mb-2">保姆注册</h1>
        <p class="text-gray-600">填写以下信息完成注册</p>
      </div>

      <!-- 注册表单 -->
      <div class="bg-white rounded-2xl shadow-xl p-6">
        <t-form :data="formData" :rules="rules" ref="formRef" @submit="handleSubmit">
          <!-- 所属机构 -->
          <t-form-item label="所属机构" name="institutionId">
            <t-select 
              v-model="formData.institutionId"
              placeholder="请选择所属机构"
              :loading="institutionsLoading"
              clearable
            >
              <t-option 
                v-for="item in institutions" 
                :key="item.id" 
                :value="item.id" 
                :label="item.name"
              />
            </t-select>
          </t-form-item>

          <!-- 用户名 -->
          <t-form-item label="用户名" name="username">
            <t-input 
              v-model="formData.username" 
              placeholder="请输入用户名"
              clearable
            />
          </t-form-item>

          <!-- 真实姓名 -->
          <t-form-item label="真实姓名" name="realName">
            <t-input 
              v-model="formData.realName" 
              placeholder="请输入真实姓名"
              clearable
            />
          </t-form-item>

          <!-- 手机号 -->
          <t-form-item label="手机号" name="phone">
            <t-input 
              v-model="formData.phone" 
              placeholder="请输入手机号"
              clearable
            />
          </t-form-item>

          <!-- 密码 -->
          <t-form-item label="密码" name="password">
            <t-input 
              v-model="formData.password" 
              type="password"
              placeholder="请输入密码（至少6位）"
              clearable
            />
          </t-form-item>

          <!-- 确认密码 -->
          <t-form-item label="确认密码" name="confirmPassword">
            <t-input 
              v-model="formData.confirmPassword" 
              type="password"
              placeholder="请再次输入密码"
              clearable
            />
          </t-form-item>

          <!-- 性别 -->
          <t-form-item label="性别" name="gender">
            <t-radio-group v-model="formData.gender">
              <t-radio :value="1">男</t-radio>
              <t-radio :value="2">女</t-radio>
            </t-radio-group>
          </t-form-item>

          <!-- 工作年限 -->
          <t-form-item label="工作年限" name="workYears">
            <t-input-number 
              v-model="formData.workYears" 
              :min="0"
              :max="50"
              placeholder="请输入工作年限"
            />
          </t-form-item>

          <!-- 个人简介 -->
          <t-form-item label="个人简介" name="introduction">
            <t-textarea 
              v-model="formData.introduction" 
              placeholder="请简单介绍您的工作经验和专长"
              :maxlength="500"
              :autosize="{ minRows: 3, maxRows: 6 }"
            />
          </t-form-item>

          <!-- 资格证书 -->
          <t-form-item label="资格证书" name="certificates">
            <FileUpload
              v-model="formData.certificates"
              type="image"
              :max="5"
              :multiple="true"
              placeholder="上传资格证书"
              tip="请上传清晰的证书照片，最多5张，单张不超过5MB"
            />
            <div class="text-xs text-amber-600 mt-2">
              <info-circle-icon class="inline w-4 h-4 mr-1" />
              建议上传：母婴护理证、健康证、身份证等
            </div>
          </t-form-item>

          <!-- 按钮组 -->
          <t-form-item>
            <t-space direction="vertical" style="width: 100%" :size="12">
              <t-button 
                theme="primary" 
                type="submit" 
                block 
                size="large"
                :loading="loading"
              >
                注册
              </t-button>
            </t-space>
          </t-form-item>
        </t-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import { ChevronLeftIcon, InfoCircleIcon } from 'tdesign-icons-vue-next'
import { register, getInstitutions } from '@/api/nanny'
import FileUpload from '@/components/FileUpload.vue'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const institutionsLoading = ref(false)
const institutions = ref([])

const formData = reactive({
  institutionId: null,
  username: '',
  realName: '',
  phone: '',
  password: '',
  confirmPassword: '',
  gender: 2,
  workYears: 0,
  introduction: '',
  certificates: [] // 证书URL列表
})

// 校验规则
const validatePhone = (val) => {
  const phoneReg = /^1[3-9]\d{9}$/
  return phoneReg.test(val)
}

const validateConfirmPassword = (val) => {
  return val === formData.password
}

const rules = {
  institutionId: [
    { required: true, message: '请选择所属机构', type: 'error' }
  ],
  username: [
    { required: true, message: '请输入用户名', type: 'error' },
    { min: 3, message: '用户名至少3位', type: 'error' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', type: 'error' }
  ],
  phone: [
    { required: true, message: '请输入手机号', type: 'error' },
    { validator: validatePhone, message: '手机号格式不正确', type: 'error' }
  ],
  password: [
    { required: true, message: '请输入密码', type: 'error' },
    { min: 6, message: '密码至少6位', type: 'error' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', type: 'error' },
    { validator: validateConfirmPassword, message: '两次密码不一致', type: 'error' }
  ],
  gender: [
    { required: true, message: '请选择性别', type: 'error' }
  ]
}

// 获取机构列表
const fetchInstitutions = async () => {
  institutionsLoading.value = true
  try {
    const res = await getInstitutions()
    institutions.value = res.data || []
  } catch (error) {
    console.error('获取机构列表失败:', error)
    MessagePlugin.error('获取机构列表失败')
  } finally {
    institutionsLoading.value = false
  }
}

const handleSubmit = async ({ validateResult }) => {
  if (validateResult !== true) return
  
  loading.value = true
  try {
    // 提交注册数据（去除确认密码字段）
    const { confirmPassword, certificates, ...submitData } = formData
    
    // 将证书URL数组转换为JSON字符串
    if (certificates && certificates.length > 0) {
      submitData.certificate = JSON.stringify(certificates)
    }
    
    await register(submitData)
    
    MessagePlugin.success('注册成功，请登录')
    
    // 跳转到登录页
    router.push('/login')
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/login')
}

onMounted(() => {
  fetchInstitutions()
})
</script>
