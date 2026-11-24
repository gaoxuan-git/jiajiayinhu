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
          <p class="brand-subtitle">专业母婴护理服务平台</p>
          <div class="feature-list">
            <div class="feature-item">
              <svg class="feature-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
              <span>专业护理认证</span>
            </div>
            <div class="feature-item">
              <svg class="feature-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
              <span>灵活工作时间</span>
            </div>
            <div class="feature-item">
              <svg class="feature-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"/>
              </svg>
              <span>优质服务保障</span>
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
          <p class="form-subtitle">请登录您的保姆端账号</p>
      </div>

      <!-- 登录表单 -->
        <t-form :data="formData" :rules="rules" ref="formRef" @submit="handleSubmit" class="login-form">
          <!-- 用户名 -->
          <t-form-item label="用户名" name="username">
            <t-input 
              v-model="formData.username" 
              placeholder="请输入用户名"
              size="large"
              clearable
            >
              <template #prefix-icon>
                <user-icon />
              </template>
            </t-input>
          </t-form-item>

          <!-- 密码 -->
          <t-form-item label="密码" name="password">
            <t-input 
              v-model="formData.password" 
              type="password"
              placeholder="请输入密码"
              size="large"
              clearable
            >
              <template #prefix-icon>
                <lock-on-icon />
              </template>
            </t-input>
          </t-form-item>

          <!-- 按钮组 -->
          <t-form-item>
            <t-space direction="vertical" style="width: 100%" :size="16">
              <t-button 
                theme="primary" 
                type="submit" 
                block 
                size="large"
                :loading="loading"
                class="login-button"
              >
                登录
              </t-button>
              <t-button 
                theme="default" 
                variant="outline"
                block 
                size="large"
                @click="goToRegister"
                class="register-button"
              >
                没有账号？立即注册
              </t-button>
            </t-space>
          </t-form-item>
        </t-form>

        <!-- 底部提示 -->
        <p class="form-footer">
          登录即表示同意《用户协议》和《隐私政策》
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import { UserIcon, LockOnIcon } from 'tdesign-icons-vue-next'
import { login } from '@/api/nanny'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const formData = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', type: 'error' }
  ],
  password: [
    { required: true, message: '请输入密码', type: 'error' },
    { min: 6, message: '密码至少6位', type: 'error' }
  ]
}

const handleSubmit = async ({ validateResult }) => {
  if (validateResult !== true) return
  
  loading.value = true
  try {
    const res = await login(formData)
    
    // 保存 token
    localStorage.setItem('nanny_token', res.data.token)
    
    MessagePlugin.success('登录成功')
    
    // 跳转到首页
    router.push('/schedule')
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  router.push('/register')
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
  background-image: url('https://images.unsplash.com/photo-1516627145497-ae6968895b74?ixlib=rb-4.0.3&auto=format&fit=crop&w=1920&q=80');
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

.login-form :deep(.t-form-item__label) {
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
}

.login-form :deep(.t-input) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.login-form :deep(.t-input__inner:focus) {
  border-color: #0052d9;
  box-shadow: 0 0 0 2px rgba(0, 82, 217, 0.1);
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

.register-button {
  height: 48px;
  font-size: 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.register-button:hover {
  border-color: #0052d9;
  color: #0052d9;
  transform: translateY(-2px);
  }

.form-footer {
  text-align: center;
  font-size: 12px;
  color: #999;
  margin-top: 24px;
  line-height: 1.6;
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
