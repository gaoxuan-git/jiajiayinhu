<template>
  <div class="login-container">
    <!-- 左侧图片区域 -->
    <div class="login-left">
      <div class="image-overlay">
        <div class="brand-content">
          <div class="brand-logo">
            <svg class="w-16 h-16 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
            </svg>
          </div>
          <h1 class="brand-title">悦芽婴护</h1>
          <p class="brand-subtitle">专业母婴护理，呵护宝宝健康成长</p>
          <div class="feature-list">
            <div class="feature-item">
              <svg class="feature-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"/>
              </svg>
              <span>专业护理团队</span>
            </div>
            <div class="feature-item">
              <svg class="feature-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
              </svg>
              <span>宝宝健康档案</span>
            </div>
            <div class="feature-item">
              <svg class="feature-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
              </svg>
              <span>成长记录追踪</span>
            </div>
            <div class="feature-item">
              <svg class="feature-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"/>
              </svg>
              <span>母婴精品商城</span>
            </div>
          </div>
        </div>
      </div>
      <div class="background-image"></div>
    </div>

    <!-- 右侧登录表单 -->
    <div class="login-right">
      <div class="login-form-wrapper">
        <!-- Logo 和标题 -->
        <div class="form-header">
          <div class="form-logo">
            <svg class="w-12 h-12 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
            </svg>
          </div>
          <h2 class="form-title">欢迎回来</h2>
          <p class="form-subtitle">请登录您的家长账号</p>
        </div>

        <!-- 登录表单 -->
        <t-form ref="formRef" :data="form" :rules="rules" label-width="0" @submit="handleLogin" class="login-form">
          <t-form-item name="username">
            <t-input 
              v-model="form.username" 
              placeholder="请输入用户名" 
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
          <t-form-item name="password">
            <t-input 
              v-model="form.password" 
              type="password" 
              placeholder="请输入密码" 
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
            <t-button theme="primary" type="submit" block size="large" :loading="loading" class="login-button">
              登录
            </t-button>
          </t-form-item>
        </t-form>
        
        <!-- 底部注册链接 -->
        <div class="login-footer">
          <span>还没有账号？</span>
          <a href="javascript:;" class="register-link" @click="$router.push('/register')">立即注册</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const loading = ref(false)
const formRef = ref(null)

const form = ref({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ]
}

const handleLogin = async ({ validateResult }) => {
  if (validateResult !== true) return
  
  loading.value = true
  try {
    await userStore.login(form.value)
    MessagePlugin.success('登录成功')
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (e) {
    // 错误已在request中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  width: 100%;
  display: flex;
  overflow: hidden;
  background: #f5f7fa;
}

/* 左侧图片区域 */
.login-left {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  min-width: 0;
}

.background-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-image: url('https://images.unsplash.com/photo-1503454537195-1dcabb73ffb9?ixlib=rb-4.0.3&auto=format&fit=crop&w=1920&q=80');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.background-image::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.85) 0%, rgba(118, 75, 162, 0.85) 100%);
}

.image-overlay {
  position: relative;
  z-index: 1;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
}

.brand-content {
  max-width: 500px;
  color: white;
  text-align: center;
}

.brand-logo {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
  animation: fadeInDown 0.8s ease-out;
}

.brand-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 16px;
  letter-spacing: 2px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  animation: fadeInUp 0.8s ease-out 0.2s both;
}

.brand-subtitle {
  font-size: 20px;
  margin-bottom: 48px;
  opacity: 0.95;
  font-weight: 300;
  animation: fadeInUp 0.8s ease-out 0.4s both;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
  animation: fadeInUp 0.8s ease-out 0.6s both;
}

.feature-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  font-size: 18px;
  font-weight: 400;
  padding: 16px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(8px);
}

.feature-icon {
  width: 24px;
  height: 24px;
  flex-shrink: 0;
}

/* 右侧登录表单 */
.login-right {
  width: 520px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ffffff;
  box-shadow: -4px 0 20px rgba(0, 0, 0, 0.08);
  position: relative;
  z-index: 10;
}

.login-form-wrapper {
  width: 100%;
  max-width: 400px;
  padding: 40px;
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
}

.form-logo {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.form-title {
  font-size: 32px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 8px;
}

.form-subtitle {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
}

.login-form {
  margin-bottom: 24px;
}

.login-form :deep(.t-form-item) {
  margin-bottom: 24px;
}

.login-form :deep(.t-input) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.login-form :deep(.t-input__inner:focus) {
  border-color: #0052d9;
  box-shadow: 0 0 0 2px rgba(0, 82, 217, 0.1);
}

.input-icon {
  width: 20px;
  height: 20px;
  color: #9CA3AF;
}

.login-button {
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 82, 217, 0.3);
}

.login-footer {
  text-align: center;
  font-size: 14px;
  color: #6B7280;
  margin-top: 24px;
}

.register-link {
  color: #0052d9;
  font-weight: 500;
  margin-left: 4px;
  text-decoration: none;
  transition: all 0.3s ease;
}

.register-link:hover {
  color: #003ba3;
  text-decoration: underline;
}

/* 动画效果 */
@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media screen and (max-width: 1024px) {
  .login-left {
    flex: 0 0 45%;
  }
  
  .login-right {
    width: 55%;
  }
}

@media screen and (max-width: 768px) {
  .login-container {
    flex-direction: column;
  }
  
  .login-left {
    display: none;
  }
  
  .login-right {
    width: 100%;
    box-shadow: none;
  }
  
  .login-form-wrapper {
    padding: 32px 24px;
  }
}

@media screen and (max-width: 480px) {
  .login-form-wrapper {
    padding: 24px 20px;
  }
  
  .form-title {
    font-size: 28px;
  }
}
</style>
