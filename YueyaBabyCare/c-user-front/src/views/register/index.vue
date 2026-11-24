<template>
  <div class="register-page">
    <div class="register-container">
      <!-- 左侧品牌区 -->
      <div class="register-left">
        <div class="brand">
          <div class="brand-icon">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
            </svg>
          </div>
          <h1>加入悦芽婴护</h1>
          <p>开启专业母婴护理之旅</p>
        </div>
        <div class="benefits">
          <div class="benefit-item">
            <div class="benefit-icon">
              <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"/>
              </svg>
            </div>
            <div>
              <h4>专业护理服务</h4>
              <p>资深护理团队为宝宝提供全方位照护</p>
            </div>
          </div>
          <div class="benefit-item">
            <div class="benefit-icon">
              <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
              </svg>
            </div>
            <div>
              <h4>成长记录追踪</h4>
              <p>详细记录宝宝每一步成长轨迹</p>
            </div>
          </div>
          <div class="benefit-item">
            <div class="benefit-icon">
              <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"/>
              </svg>
            </div>
            <div>
              <h4>母婴精品商城</h4>
              <p>优质母婴产品一站式购物</p>
            </div>
          </div>
        </div>
      </div>
      <!-- 右侧注册表单 -->
      <div class="register-right">
        <div class="register-form-wrapper">
          <h2>家长注册</h2>
          <p class="register-subtitle">创建账号，开始使用悦芽婴护</p>
          
          <t-form ref="formRef" :data="form" :rules="rules" label-width="0" @submit="handleRegister" class="register-form">
            <t-form-item name="username">
              <t-input 
                v-model="form.username" 
                placeholder="请设置用户名" 
                size="large" 
                clearable
              >
                <template #prefix-icon>
                  <svg class="input-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
                  </svg>
                </template>
              </t-input>
            </t-form-item>
            <t-form-item name="phone">
              <t-input 
                v-model="form.phone" 
                placeholder="请输入手机号" 
                size="large" 
                clearable
              >
                <template #prefix-icon>
                  <svg class="input-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 18h.01M8 21h8a2 2 0 002-2V5a2 2 0 00-2-2H8a2 2 0 00-2 2v14a2 2 0 002 2z"/>
                  </svg>
                </template>
              </t-input>
            </t-form-item>
            <t-form-item name="password">
              <t-input 
                v-model="form.password" 
                type="password" 
                placeholder="请设置密码（6-20位）" 
                size="large" 
                clearable
              >
                <template #prefix-icon>
                  <svg class="input-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/>
                  </svg>
                </template>
              </t-input>
            </t-form-item>
            <t-form-item name="confirmPassword">
              <t-input 
                v-model="form.confirmPassword" 
                type="password" 
                placeholder="请确认密码" 
                size="large" 
                clearable
              >
                <template #prefix-icon>
                  <svg class="input-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/>
                  </svg>
                </template>
              </t-input>
            </t-form-item>
            <t-form-item>
              <t-button theme="primary" type="submit" block size="large" :loading="loading" class="register-btn">
                立即注册
              </t-button>
            </t-form-item>
          </t-form>
          
          <div class="register-footer">
            <span>已有账号？</span>
            <a href="javascript:;" class="login-link" @click="$router.push('/login')">立即登录</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const formRef = ref(null)

const form = ref({
  username: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const rules = {
  username: [
    { required: true, message: '请设置用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度3-20个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请设置密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: (val) => val === form.value.password, message: '两次密码输入不一致', trigger: 'blur' }
  ]
}

const handleRegister = async ({ validateResult }) => {
  if (validateResult !== true) return
  
  loading.value = true
  try {
    await userStore.register({
      username: form.value.username,
      phone: form.value.phone,
      password: form.value.password,
      confirmPassword: form.value.confirmPassword
    })
    MessagePlugin.success('注册成功')
    router.push('/')
  } catch (e) {
    // 错误已在request中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background: #F9FAFB;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.register-container {
  display: flex;
  width: 1000px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
  overflow: hidden;
}

/* 左侧品牌区 */
.register-left {
  flex: 1;
  background: linear-gradient(135deg, #3B82F6 0%, #2563EB 100%);
  padding: 60px 48px;
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.brand-icon {
  width: 48px;
  height: 48px;
}

.brand-icon svg {
  width: 100%;
  height: 100%;
}

.brand h1 {
  margin: 24px 0 12px;
  font-size: 28px;
  font-weight: 600;
  letter-spacing: 1px;
}

.brand p {
  opacity: 0.9;
  font-size: 16px;
  line-height: 1.6;
}

.benefits {
  margin-top: 48px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.benefit-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  background: rgba(255, 255, 255, 0.1);
  padding: 16px;
  border-radius: 12px;
}

.benefit-icon {
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.benefit-icon svg {
  width: 20px;
  height: 20px;
}

.benefit-item h4 {
  margin: 0 0 4px;
  font-size: 15px;
  font-weight: 600;
}

.benefit-item p {
  margin: 0;
  font-size: 13px;
  opacity: 0.85;
  line-height: 1.5;
}

/* 右侧注册区 */
.register-right {
  flex: 1.1;
  padding: 48px 56px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.register-form-wrapper {
  width: 100%;
  max-width: 380px;
}

.register-form-wrapper h2 {
  margin: 0 0 8px;
  font-size: 24px;
  font-weight: 600;
  color: #1F2937;
}

.register-subtitle {
  margin: 0 0 28px;
  font-size: 14px;
  color: #6B7280;
}

.register-form {
  margin-bottom: 20px;
}

.register-form :deep(.t-input) {
  border-radius: 8px;
}

.register-form :deep(.t-form__item) {
  margin-bottom: 18px;
}

.register-btn {
  border-radius: 8px;
  font-weight: 500;
  height: 44px;
}

.register-footer {
  text-align: center;
  font-size: 14px;
  color: #6B7280;
}

.login-link {
  color: #3B82F6;
  font-weight: 500;
  margin-left: 4px;
  text-decoration: none;
  transition: color 0.2s;
}

.login-link:hover {
  color: #2563EB;
}

.input-icon {
  width: 20px;
  height: 20px;
  color: #9CA3AF;
}

/* 响应式 */
@media (max-width: 900px) {
  .register-container {
    flex-direction: column;
    width: 100%;
    max-width: 440px;
  }
  
  .register-left {
    padding: 40px 32px;
  }
  
  .register-right {
    padding: 40px 32px;
  }
}
</style>
