<template>
  <div class="edit-page">
    <div class="page-header">
      <a href="javascript:;" class="back-link" @click="$router.back()">
        <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
        </svg>
        返回
      </a>
      <h1>编辑资料</h1>
    </div>

    <div class="edit-container">
      <!-- 基本信息 -->
      <div class="edit-card">
        <h3>基本信息</h3>
        <t-form 
          ref="profileFormRef" 
          :data="form" 
          :rules="rules" 
          label-width="100px" 
          class="edit-form"
        >
          <t-form-item label="头像">
            <AvatarUploader v-model="form.avatar" />
          </t-form-item>
          <t-form-item label="用户名">
            <t-input v-model="form.username" disabled placeholder="用户名不可修改" />
          </t-form-item>
          <t-form-item label="真实姓名" name="realName">
            <t-input v-model="form.realName" placeholder="请输入真实姓名" clearable />
          </t-form-item>
          <t-form-item label="性别" name="gender">
            <t-radio-group v-model="form.gender">
              <t-radio :value="0">未知</t-radio>
              <t-radio :value="1">男</t-radio>
              <t-radio :value="2">女</t-radio>
            </t-radio-group>
          </t-form-item>
          <t-form-item label="手机号">
            <t-input :value="maskPhone(form.phone)" disabled />
          </t-form-item>
          <t-form-item label="邮箱" name="email">
            <t-input v-model="form.email" placeholder="请输入邮箱地址" clearable />
          </t-form-item>
          <t-form-item>
            <t-space>
              <t-button theme="primary" :loading="saveLoading" @click="handleSave">保存修改</t-button>
              <t-button variant="outline" @click="$router.back()">取消</t-button>
            </t-space>
          </t-form-item>
        </t-form>
      </div>

      <!-- 修改密码 -->
      <div class="edit-card">
        <h3>修改密码</h3>
        <t-form 
          ref="passwordFormRef" 
          :data="passwordForm" 
          :rules="passwordRules" 
          label-width="100px" 
          class="edit-form"
        >
          <t-form-item label="原密码" name="oldPassword">
            <t-input 
              v-model="passwordForm.oldPassword" 
              type="password" 
              placeholder="请输入原密码" 
              clearable
            />
          </t-form-item>
          <t-form-item label="新密码" name="newPassword">
            <t-input 
              v-model="passwordForm.newPassword" 
              type="password" 
              placeholder="请输入新密码（6-20位）" 
              clearable
            />
          </t-form-item>
          <t-form-item label="确认密码" name="confirmPassword">
            <t-input 
              v-model="passwordForm.confirmPassword" 
              type="password" 
              placeholder="请再次输入新密码" 
              clearable
            />
          </t-form-item>
          <t-form-item>
            <t-button theme="primary" :loading="pwdLoading" @click="handleChangePassword">
              修改密码
            </t-button>
          </t-form-item>
        </t-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import { getMemberInfo, updateProfile, updatePassword } from '@/api/member'
import AvatarUploader from '@/components/AvatarUploader.vue'

const profileFormRef = ref(null)
const passwordFormRef = ref(null)
const saveLoading = ref(false)
const pwdLoading = ref(false)

const form = ref({
  username: '',
  realName: '',
  gender: 0,
  phone: '',
  email: '',
  avatar: ''
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = {
  realName: [{ max: 20, message: '姓名最多20个字符', trigger: 'blur' }],
  email: [{ email: true, message: '请输入正确的邮箱格式', trigger: 'blur' }]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { 
      validator: (val) => val === passwordForm.value.newPassword, 
      message: '两次密码输入不一致', 
      trigger: 'blur' 
    }
  ]
}

onMounted(async () => {
  await fetchProfile()
})

const fetchProfile = async () => {
  try {
    const res = await getMemberInfo()
    const data = res.data
    form.value = {
      username: data.username || '',
      realName: data.realName || '',
      gender: data.gender || 0,
      phone: data.phone || '',
      email: data.email || '',
      avatar: data.avatar || ''
    }
  } catch (e) {
    // 错误处理
  }
}

const maskPhone = (phone) => {
  if (!phone) return '-'
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}


const handleSave = async () => {
  const valid = await profileFormRef.value?.validate()
  if (valid !== true) return
  
  saveLoading.value = true
  try {
    await updateProfile({
      realName: form.value.realName,
      gender: form.value.gender,
      email: form.value.email
    })
    MessagePlugin.success('保存成功')
  } catch (e) {
    // 错误已在request中处理
  } finally {
    saveLoading.value = false
  }
}

const handleChangePassword = async () => {
  const valid = await passwordFormRef.value?.validate()
  if (valid !== true) return
  
  pwdLoading.value = true
  try {
    await updatePassword({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword,
      confirmPassword: passwordForm.value.confirmPassword
    })
    MessagePlugin.success('密码修改成功')
    passwordForm.value = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
  } catch (e) {
    // 错误已在request中处理
  } finally {
    pwdLoading.value = false
  }
}
</script>

<style scoped>
.edit-page {
  padding: 24px;
  background: #F9FAFB;
  min-height: calc(100vh - 120px);
}

.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.back-link {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #6B7280;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.2s;
}

.back-link:hover {
  color: #3B82F6;
}

.back-link svg {
  width: 18px;
  height: 18px;
}

.page-header h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1F2937;
}

.edit-container {
  max-width: 600px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.edit-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.edit-card h3 {
  margin: 0 0 20px;
  font-size: 16px;
  font-weight: 600;
  color: #1F2937;
  padding-bottom: 12px;
  border-bottom: 1px solid #F3F4F6;
}

.edit-form {
  max-width: 100%;
}

.edit-form :deep(.t-input) {
  border-radius: 6px;
}

.edit-form :deep(.t-form__item) {
  margin-bottom: 20px;
}


.edit-form :deep(.t-button--theme-primary) {
  border-radius: 8px;
}

.edit-form :deep(.t-button--variant-outline) {
  border-radius: 8px;
}
</style>
