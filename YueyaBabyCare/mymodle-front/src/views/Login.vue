<template>
  <div class="login-container">
    <!-- 左侧图片区域 -->
    <div class="login-left">
      <div class="image-overlay">
        <div class="brand-content">
          <div class="brand-logo">
            <svg class="logo-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"/>
            </svg>
          </div>
          <h1 class="brand-title">悦芽婴护</h1>
          <p class="brand-subtitle">专业母婴护理管理平台</p>
          <div class="feature-list">
            <div class="feature-item">
              <svg class="feature-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"/>
              </svg>
              <span>系统管理</span>
            </div>
            <div class="feature-item">
              <svg class="feature-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"/>
              </svg>
              <span>机构管理</span>
            </div>
            <div class="feature-item">
              <svg class="feature-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
              </svg>
              <span>数据统计</span>
            </div>
            <div class="feature-item">
              <svg class="feature-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/>
              </svg>
              <span>安全可靠</span>
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
            <svg class="form-logo-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"/>
            </svg>
          </div>
          <h2 class="form-title">欢迎回来</h2>
          <p class="form-subtitle">请选择身份并登录</p>
        </div>

        <!-- 登录表单 -->
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          auto-complete="on"
          label-position="top"
        >
          <!-- 身份选择 -->
          <el-form-item>
            <el-segmented 
              v-model="loginForm.userType" 
              :options="userTypeOptions" 
              block 
              size="large"
              class="user-type-selector"
            />
          </el-form-item>

          <el-form-item prop="username" label="用户名">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              name="username"
              type="text"
              tabindex="1"
              auto-complete="on"
              prefix-icon="User"
              size="large"
            />
          </el-form-item>

          <el-form-item prop="password" label="密码">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              name="password"
              tabindex="2"
              auto-complete="on"
              prefix-icon="Lock"
              size="large"
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              :loading="loading"
              type="primary"
              size="large"
              class="login-button"
              @click="handleLogin"
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { InfoFilled } from '@element-plus/icons-vue'

const store = useStore()
const router = useRouter()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  userType: 'admin',
  username: 'admin',
  password: '123456'
})

const userTypeOptions = [
  { label: '管理员', value: 'admin' },
  { label: '机构', value: 'institution' }
]

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = () => {
  loginFormRef.value.validate(valid => {
    if (valid) {
      loading.value = true
      // 根据用户类型调用不同的登录接口
      const actionName = loginForm.userType === 'institution' 
        ? 'user/institutionLogin' 
        : 'user/login'
      
      store.dispatch(actionName, {
        username: loginForm.username,
        password: loginForm.password,
        userType: loginForm.userType
      })
        .then(() => {
          const userTypeName = loginForm.userType === 'institution' ? '机构' : '管理员'
          ElMessage.success(`${userTypeName}登录成功`)
          router.push({ path: '/' })
        })
        .catch(error => {
          console.error('登录失败:', error)
        })
        .finally(() => {
          loading.value = false
        })
    }
  })
}
</script>

<style lang="scss" scoped>
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
  background-image: url('https://images.unsplash.com/photo-1551434678-e076c223a692?ixlib=rb-4.0.3&auto=format&fit=crop&w=1920&q=80');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  animation: backgroundShift 20s ease-in-out infinite;
}

.background-image::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.85) 0%, rgba(118, 75, 162, 0.85) 100%);
  animation: gradientShift 15s ease-in-out infinite;
}

@keyframes backgroundShift {
  0%, 100% {
    background-position: center center;
    transform: scale(1);
  }
  50% {
    background-position: center 60%;
    transform: scale(1.05);
  }
}

@keyframes gradientShift {
  0%, 100% {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.85) 0%, rgba(118, 75, 162, 0.85) 100%);
  }
  50% {
    background: linear-gradient(135deg, rgba(118, 75, 162, 0.85) 0%, rgba(102, 126, 234, 0.85) 100%);
  }
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

.logo-icon {
  width: 64px;
  height: 64px;
  color: white;
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
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
}

.feature-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.feature-item:hover::before {
  left: 100%;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateX(8px) translateY(-2px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
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
  box-shadow: -4px 0 20px rgba(0, 0, 0, 0.08), -8px 0 40px rgba(0, 0, 0, 0.04);
  position: relative;
  z-index: 10;
  overflow-y: auto;
}

.login-right::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  opacity: 0.8;
}

.login-form-wrapper {
  width: 100%;
  max-width: 400px;
  padding: 40px;
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
  animation: fadeInUp 0.6s ease-out 0.3s both;
}

.form-logo {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.form-logo-icon {
  width: 48px;
  height: 48px;
  color: #409eff;
  filter: drop-shadow(0 2px 4px rgba(64, 158, 255, 0.3));
  transition: all 0.3s ease;
  animation: logoFloat 3s ease-in-out infinite;
}

@keyframes logoFloat {
  0%, 100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-5px) scale(1.05);
  }
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

.login-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

.login-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
  padding-bottom: 0;
  line-height: 1.5;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  background: #fafbfc;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset, 0 2px 4px rgba(0, 0, 0, 0.04);
  background: #ffffff;
  transform: translateY(-1px);
}

.login-form :deep(.el-input.is-focus .el-input__wrapper) {
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2) inset, 0 0 0 1px #409eff inset;
  background: #ffffff;
  transform: translateY(-1px);
}

.user-type-selector {
  margin-bottom: 8px;
}

.user-type-selector :deep(.el-segmented) {
  border-radius: 8px;
  padding: 4px;
  background: #f5f7fa;
}

.user-type-selector :deep(.el-segmented__item) {
  border-radius: 6px;
  transition: all 0.3s ease;
  padding: 12px 24px;
  font-size: 15px;
  font-weight: 500;
  min-width: 120px;
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
  border: none;
  position: relative;
  overflow: hidden;
}

.login-button::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  transform: translate(-50%, -50%);
  transition: width 0.6s, height 0.6s;
}

.login-button:hover::before {
  width: 300px;
  height: 300px;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.4), 0 4px 8px rgba(64, 158, 255, 0.2);
  background: linear-gradient(135deg, #66b1ff 0%, #409eff 100%);
}

.login-button:active {
  transform: translateY(0);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.tips {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  color: #909399;
  margin-top: 24px;
  padding: 12px;
  background: linear-gradient(135deg, #f5f7fa 0%, #eef2f6 100%);
  border-radius: 8px;
  line-height: 1.5;
  word-break: break-all;
  border: 1px solid #e5e7eb;
  transition: all 0.3s ease;
}

.tips:hover {
  background: linear-gradient(135deg, #eef2f6 0%, #e5e7eb 100%);
  border-color: #d1d5db;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.tips .el-icon {
  margin-right: 8px;
  font-size: 16px;
  flex-shrink: 0;
}

.tips span {
  flex: 1;
  text-align: center;
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
  
  .tips {
    font-size: 12px;
    padding: 10px;
  }
}
</style>

