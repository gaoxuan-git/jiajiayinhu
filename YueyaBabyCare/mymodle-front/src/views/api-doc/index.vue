<template>
  <div class="api-doc-container">
    <el-card class="header-card">
      <div class="header-content">
        <div>
          <h2>📚 API 接口文档</h2>
          <p>基于 Knife4j 的在线 API 文档和测试工具</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" :icon="RefreshRight" @click="handleRefresh">
            刷新文档
          </el-button>
          <el-button :icon="Link" @click="openInNewTab">
            新窗口打开
          </el-button>
        </div>
      </div>
    </el-card>

    <el-card class="iframe-card" style="margin-top: 20px;">
      <div class="iframe-wrapper">
        <iframe
          ref="iframeRef"
          :src="apiDocUrl"
          frameborder="0"
          class="api-iframe"
          @load="handleIframeLoad">
        </iframe>
        <div v-if="loading" class="loading-mask">
          <el-icon class="is-loading">
            <Loading />
          </el-icon>
          <p>加载中...</p>
        </div>
      </div>
    </el-card>

    <!-- 提示信息 -->
    <el-alert
      v-if="!isBackendRunning"
      title="提示"
      type="warning"
      description="如果文档无法加载，请确保后端服务已启动（http://localhost:8080）"
      :closable="false"
      style="margin-top: 20px;">
    </el-alert>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { RefreshRight, Link, Loading } from '@element-plus/icons-vue'

const iframeRef = ref(null)
const loading = ref(true)
const isBackendRunning = ref(true)

// API 文档地址
const apiDocUrl = computed(() => {
  return 'http://localhost:8080/doc.html'
})

// 页面加载时检测后端是否运行
onMounted(() => {
  checkBackendStatus()
})

// 检查后端状态
const checkBackendStatus = async () => {
  try {
    const response = await fetch(apiDocUrl.value, { method: 'HEAD', mode: 'no-cors' })
    isBackendRunning.value = true
  } catch (error) {
    isBackendRunning.value = false
    ElMessage.warning('后端服务未启动，请先启动后端')
  }
}

// iframe 加载完成
const handleIframeLoad = () => {
  loading.value = false
  console.log('API 文档加载完成')
}

// 刷新文档
const handleRefresh = () => {
  loading.value = true
  if (iframeRef.value) {
    iframeRef.value.src = apiDocUrl.value
  }
  ElMessage.success('正在刷新文档...')
}

// 新窗口打开
const openInNewTab = () => {
  window.open(apiDocUrl.value, '_blank')
  ElMessage.success('已在新窗口打开 API 文档')
}
</script>

<style lang="scss" scoped>
.api-doc-container {
  .header-card {
    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;

      h2 {
        margin: 0 0 10px 0;
        color: #333;
      }

      p {
        margin: 0;
        color: #666;
      }

      .header-actions {
        display: flex;
        gap: 10px;
      }
    }
  }

  .iframe-card {
    .iframe-wrapper {
      position: relative;
      width: 100%;
      height: calc(100vh - 240px);
      min-height: 600px;

      .api-iframe {
        width: 100%;
        height: 100%;
        border: none;
        background: #fff;
      }

      .loading-mask {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        background: rgba(255, 255, 255, 0.9);
        z-index: 10;

        .el-icon {
          font-size: 40px;
          color: #409eff;
          margin-bottom: 15px;
        }

        p {
          font-size: 16px;
          color: #666;
        }
      }
    }
  }
}
</style>

