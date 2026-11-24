<template>
  <div class="cache-config">
    <div class="action-bar">
      <el-button type="primary" :icon="Refresh" @click="loadConfigs" :loading="loading">
        刷新
      </el-button>
      <el-button type="danger" :icon="Delete" @click="handleClearAll">
        清空所有缓存
      </el-button>
      <el-button :icon="InfoFilled" @click="showRedisInfo">
        Redis 信息
      </el-button>
    </div>

    <el-table :data="configs" v-loading="loading" border stripe>
      <el-table-column label="缓存名称" prop="cacheName" width="200">
        <template #default="scope">
          <el-tag type="primary">{{ scope.row.cacheName }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="描述" prop="description" min-width="200" />

      <el-table-column label="过期时间" width="150">
        <template #default="scope">
          <el-tag v-if="scope.row.ttl === -1" type="danger">永不过期</el-tag>
          <el-tag v-else type="success">{{ formatTime(scope.row.ttl) }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="当前数量" prop="currentSize" width="120" align="center">
        <template #default="scope">
          <el-text type="primary" size="large">{{ scope.row.currentSize || 0 }}</el-text>
        </template>
      </el-table-column>

      <el-table-column label="最大数量" width="120" align="center">
        <template #default="scope">
          <span v-if="scope.row.maxSize === -1">无限制</span>
          <span v-else>{{ scope.row.maxSize }}</span>
        </template>
      </el-table-column>

      <el-table-column label="状态" width="100" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.enabled ? 'success' : 'danger'">
            {{ scope.row.enabled ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="250" fixed="right" align="center">
        <template #default="scope">
          <el-button type="primary" size="small" :icon="Edit" @click="handleEdit(scope.row)">
            编辑
          </el-button>
          <el-button type="danger" size="small" :icon="Delete" @click="handleClear(scope.row)">
            清空
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑缓存配置"
      width="600px">
      <el-form :model="editForm" label-width="120px">
        <el-form-item label="缓存名称">
          <el-input v-model="editForm.cacheName" disabled />
        </el-form-item>

        <el-form-item label="描述">
          <el-input v-model="editForm.description" placeholder="请输入描述" />
        </el-form-item>

        <el-form-item label="过期时间">
          <el-input-number
            v-model="editForm.ttl"
            :min="-1"
            :max="31536000"
            :step="60"
            style="width: 100%">
          </el-input-number>
          <div style="color: #999; font-size: 12px; margin-top: 5px;">
            单位：秒，-1 表示永不过期
          </div>
        </el-form-item>

        <el-form-item label="最大数量">
          <el-input-number
            v-model="editForm.maxSize"
            :min="-1"
            :max="100000"
            style="width: 100%">
          </el-input-number>
          <div style="color: #999; font-size: 12px; margin-top: 5px;">
            -1 表示不限制
          </div>
        </el-form-item>

        <el-form-item label="状态">
          <el-switch v-model="editForm.enabled" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <!-- Redis 信息对话框 -->
    <el-dialog
      v-model="infoDialogVisible"
      title="Redis 信息"
      width="600px">
      <el-input
        v-model="redisInfo"
        type="textarea"
        :rows="10"
        readonly />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Refresh, Delete, Edit, InfoFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAllCacheConfigs,
  updateCacheConfig,
  clearCache,
  clearAllCaches,
  getRedisInfo
} from '@/api/cacheConfig'

const configs = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const infoDialogVisible = ref(false)
const saving = ref(false)
const redisInfo = ref('')
const editForm = ref({
  cacheName: '',
  description: '',
  ttl: 3600,
  maxSize: -1,
  enabled: true
})

onMounted(() => {
  loadConfigs()
})

// 加载配置列表
const loadConfigs = async () => {
  loading.value = true
  try {
    const res = await getAllCacheConfigs()
    configs.value = res.data || []
    ElMessage.success('加载成功')
  } catch (error) {
    ElMessage.error('加载失败：' + error.message)
  } finally {
    loading.value = false
  }
}

// 编辑配置
const handleEdit = (row) => {
  editForm.value = { ...row }
  dialogVisible.value = true
}

// 保存配置
const handleSave = async () => {
  saving.value = true
  try {
    await updateCacheConfig(editForm.value)
    ElMessage.success('配置已更新')
    dialogVisible.value = false
    loadConfigs()
  } catch (error) {
    ElMessage.error('保存失败：' + error.message)
  } finally {
    saving.value = false
  }
}

// 清空指定缓存
const handleClear = (row) => {
  ElMessageBox.confirm(
    `确定要清空缓存 "${row.cacheName}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await clearCache(row.cacheName)
      ElMessage.success('缓存已清空')
      loadConfigs()
    } catch (error) {
      ElMessage.error('清空失败：' + error.message)
    }
  }).catch(() => {
    // 用户取消
  })
}

// 清空所有缓存
const handleClearAll = () => {
  ElMessageBox.confirm(
    '确定要清空所有缓存吗？此操作不可恢复！',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      confirmButtonClass: 'el-button--danger'
    }
  ).then(async () => {
    try {
      await clearAllCaches()
      ElMessage.success('所有缓存已清空')
      loadConfigs()
    } catch (error) {
      ElMessage.error('清空失败：' + error.message)
    }
  }).catch(() => {
    // 用户取消
  })
}

// 显示 Redis 信息
const showRedisInfo = async () => {
  try {
    const res = await getRedisInfo()
    redisInfo.value = res.data || '无法获取 Redis 信息'
    infoDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取失败：' + error.message)
  }
}

// 格式化时间
const formatTime = (seconds) => {
  if (!seconds || seconds <= 0) return '未设置'
  
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  
  const parts = []
  if (hours > 0) parts.push(`${hours}小时`)
  if (minutes > 0) parts.push(`${minutes}分钟`)
  if (secs > 0 || parts.length === 0) parts.push(`${secs}秒`)
  
  return parts.join('')
}
</script>

<style lang="scss" scoped>
.cache-config {
  .action-bar {
    margin-bottom: 20px;
    display: flex;
    gap: 10px;
  }
}
</style>

