<template>
  <div class="cache-container">
    <el-card class="header-card">
      <div class="header-content">
        <div>
          <h2>💾 缓存管理中心</h2>
          <p>智能缓存注解管理 + 缓存配置管理</p>
        </div>
      </div>
    </el-card>

    <el-tabs v-model="activeTab" class="cache-tabs">
      <!-- 缓存注解管理 -->
      <el-tab-pane label="📝 注解管理" name="annotation">
        <div class="tab-header-actions">
          <el-button 
            type="primary" 
            :icon="Refresh" 
            @click="handleScan"
            :loading="scanning">
            {{ scanning ? '扫描中...' : '扫描方法' }}
          </el-button>
          <el-button 
            type="success" 
            :icon="Check"
            @click="handleAddAnnotations"
            :disabled="selectedMethods.length === 0"
            :loading="adding">
            添加缓存注解 ({{ selectedMethods.length }})
          </el-button>
        </div>

    <el-card class="table-card" v-if="methods.length > 0">
      <div class="filter-bar">
        <el-input
          v-model="searchText"
          placeholder="搜索类名或方法名"
          :prefix-icon="Search"
          clearable
          style="width: 300px">
        </el-input>
        
        <el-select 
          v-model="filterType" 
          placeholder="筛选方法类型"
          clearable
          style="width: 180px; margin-left: 10px">
          <el-option label="全部" value=""></el-option>
          <el-option label="查询方法" value="query"></el-option>
          <el-option label="新增方法" value="insert"></el-option>
          <el-option label="更新方法" value="update"></el-option>
          <el-option label="删除方法" value="delete"></el-option>
        </el-select>

        <div class="stats">
          <el-tag type="info">共 {{ filteredMethods.length }} 个方法</el-tag>
          <el-tag type="success">已添加 {{ addedMethodsCount }} 个</el-tag>
          <el-tag type="warning">未添加 {{ notAddedMethodsCount }} 个</el-tag>
          <el-tag type="primary" v-if="selectedMethods.length > 0">
            已选 {{ selectedMethods.length }} 个
          </el-tag>
        </div>
      </div>

      <el-table
        :data="filteredMethods"
        style="width: 100%; margin-top: 20px"
        @selection-change="handleSelectionChange"
        stripe
        border>
        <el-table-column type="selection" width="55" :selectable="row => !row.hasCacheAnnotation" />
        
        <el-table-column label="类名" prop="simpleClassName" width="180">
          <template #default="scope">
            <el-tag type="primary">{{ scope.row.simpleClassName }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="方法签名" min-width="280">
          <template #default="scope">
            <code class="method-signature">{{ scope.row.methodSignature }}</code>
          </template>
        </el-table-column>
        
        <el-table-column label="方法类型" width="110">
          <template #default="scope">
            <el-tag 
              :type="getMethodTypeColor(scope.row.methodType)"
              effect="dark">
              {{ getMethodTypeLabel(scope.row.methodType) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="建议注解" width="130">
          <template #default="scope">
            <el-tag 
              :type="getAnnotationTypeColor(scope.row.suggestedAnnotation)"
              effect="plain">
              {{ scope.row.suggestedAnnotation }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="缓存名称" prop="cacheName" width="140">
          <template #default="scope">
            <code>{{ scope.row.cacheName }}</code>
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag 
              v-if="scope.row.hasCacheAnnotation"
              type="success"
              effect="dark">
              已添加
            </el-tag>
            <el-tag 
              v-else
              type="info"
              effect="plain">
              未添加
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button 
              v-if="!scope.row.hasCacheAnnotation"
              type="primary" 
              size="small"
              @click="handleAddSingle(scope.row)">
              添加
            </el-button>
            <el-tag 
              v-else
              type="success"
              size="small">
              已添加
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-empty 
      v-else-if="!scanning"
      description="暂无数据，点击【扫描方法】开始扫描"
      :image-size="200">
      <el-button type="primary" @click="handleScan">开始扫描</el-button>
    </el-empty>
      </el-tab-pane>

      <!-- 缓存配置管理 -->
      <el-tab-pane label="⚙️ 配置管理" name="config">
        <CacheConfig />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Refresh, Check, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { scanCacheableMethods, addCacheAnnotations } from '@/api/cache'
import CacheConfig from './components/CacheConfig.vue'

const activeTab = ref('annotation')
const methods = ref([])
const selectedMethods = ref([])
const scanning = ref(false)
const adding = ref(false)
const searchText = ref('')
const filterType = ref('')

// 过滤后的方法列表
const filteredMethods = computed(() => {
  let result = methods.value

  // 按搜索文本过滤
  if (searchText.value) {
    const text = searchText.value.toLowerCase()
    result = result.filter(m => 
      m.simpleClassName.toLowerCase().includes(text) ||
      m.methodName.toLowerCase().includes(text)
    )
  }

  // 按方法类型过滤
  if (filterType.value) {
    result = result.filter(m => m.methodType === filterType.value)
  }

  return result
})

// 已添加缓存注解的方法数量
const addedMethodsCount = computed(() => {
  return filteredMethods.value.filter(m => m.hasCacheAnnotation).length
})

// 未添加缓存注解的方法数量
const notAddedMethodsCount = computed(() => {
  return filteredMethods.value.filter(m => !m.hasCacheAnnotation).length
})

// 扫描方法
const handleScan = async () => {
  scanning.value = true
  try {
    const res = await scanCacheableMethods()
    methods.value = res.data || []
    
    if (methods.value.length === 0) {
      ElMessage.warning('未找到可添加缓存的方法')
    } else {
      ElMessage.success(`扫描完成，找到 ${methods.value.length} 个可缓存方法`)
    }
  } catch (error) {
    ElMessage.error('扫描失败：' + error.message)
  } finally {
    scanning.value = false
  }
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedMethods.value = selection
}

// 批量添加注解
const handleAddAnnotations = async () => {
  if (selectedMethods.value.length === 0) {
    ElMessage.warning('请先选择要添加缓存的方法')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要为选中的 ${selectedMethods.value.length} 个方法添加缓存注解吗？`,
      '确认添加',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    adding.value = true
    
    const request = {
      methods: selectedMethods.value.map(m => ({
        className: m.className,
        methodName: m.methodName,
        annotationType: m.suggestedAnnotation.replace('@', ''),
        cacheName: m.cacheName,
        filePath: m.filePath
      }))
    }

    const res = await addCacheAnnotations(request)
    ElMessage.success(res.message || '添加成功')
    
    // 重新扫描
    await handleScan()
    selectedMethods.value = []
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('添加失败：' + error.message)
    }
  } finally {
    adding.value = false
  }
}

// 单个添加
const handleAddSingle = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要为方法 ${row.methodName} 添加 ${row.suggestedAnnotation} 注解吗？`,
      '确认添加',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    adding.value = true
    
    const request = {
      methods: [{
        className: row.className,
        methodName: row.methodName,
        annotationType: row.suggestedAnnotation.replace('@', ''),
        cacheName: row.cacheName,
        filePath: row.filePath
      }]
    }

    const res = await addCacheAnnotations(request)
    ElMessage.success(res.message || '添加成功')
    
    // 重新扫描
    await handleScan()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('添加失败：' + error.message)
    }
  } finally {
    adding.value = false
  }
}

// 获取方法类型标签
const getMethodTypeLabel = (type) => {
  const map = {
    query: '查询',
    insert: '新增',
    update: '更新',
    delete: '删除'
  }
  return map[type] || type
}

// 获取方法类型颜色
const getMethodTypeColor = (type) => {
  const map = {
    query: 'success',
    insert: 'primary',
    update: 'warning',
    delete: 'danger'
  }
  return map[type] || 'info'
}

// 获取注解类型颜色
const getAnnotationTypeColor = (annotation) => {
  const map = {
    '@Cacheable': 'success',
    '@CachePut': 'warning',
    '@CacheEvict': 'danger'
  }
  return map[annotation] || 'info'
}
</script>

<style lang="scss" scoped>
.cache-container {
  .header-card {
    margin-bottom: 20px;

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

  .cache-tabs {
    .tab-header-actions {
      margin-bottom: 20px;
      display: flex;
      gap: 10px;
    }
  }

  .table-card {
    .filter-bar {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .stats {
        display: flex;
        gap: 10px;
      }
    }

    .method-signature {
      background: #f5f7fa;
      padding: 4px 8px;
      border-radius: 4px;
      font-size: 13px;
      color: #333;
      font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
    }
  }
}
</style>

