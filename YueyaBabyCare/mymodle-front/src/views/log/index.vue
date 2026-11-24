<template>
  <div class="log-container">
    <!-- 顶部卡片 -->
    <el-card class="header-card">
      <div class="header-content">
        <div>
          <h2>📋 日志管理中心</h2>
          <p>实时查看、搜索、下载系统日志</p>
        </div>
      </div>
    </el-card>

    <!-- 搜索条件卡片 -->
    <el-card class="filter-card" style="margin-top: 20px;">
      <el-form :model="queryParams" label-width="100px" inline>
        <el-form-item label="日志文件">
          <el-select
            v-model="queryParams.fileName"
            placeholder="选择日志文件"
            style="width: 200px"
            @change="handleFileChange">
            <el-option
              v-for="file in logFiles"
              :key="file.fileName"
              :label="file.fileName"
              :value="file.fileName">
              <span>{{ file.fileName }}</span>
              <span style="float: right; color: #8492a6; font-size: 12px; margin-left: 15px;">
                {{ file.fileSizeFormatted }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="日志级别">
          <el-select
            v-model="queryParams.level"
            placeholder="全部级别"
            style="width: 150px"
            clearable
            @change="handleSearch">
            <el-option label="全部" value="ALL"></el-option>
            <el-option label="TRACE" value="TRACE"></el-option>
            <el-option label="DEBUG" value="DEBUG"></el-option>
            <el-option label="INFO" value="INFO"></el-option>
            <el-option label="WARN" value="WARN"></el-option>
            <el-option label="ERROR" value="ERROR"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="关键词">
          <el-input
            v-model="queryParams.keyword"
            placeholder="搜索日志内容或类名"
            style="width: 250px"
            clearable
            @keyup.enter="handleSearch"
            @clear="handleSearch">
            <template #append>
              <el-button :icon="Search" @click="handleSearch" />
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 380px"
            @change="handleSearch">
          </el-date-picker>
        </el-form-item>

        <el-form-item label=" " label-width="20px">
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮卡片 -->
    <el-card class="action-card" style="margin-top: 20px;">
      <div class="action-buttons">
        <div>
          <el-button type="primary" :icon="Refresh" @click="handleRefresh">
            刷新日志
          </el-button>
          <el-button type="success" :icon="Download" @click="handleDownload">
            下载日志
          </el-button>
        </div>
        <div>
          <el-button type="warning" :icon="Delete" @click="handleClear">
            清空日志
          </el-button>
          <el-button type="danger" :icon="Delete" @click="handleDelete">
            删除文件
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 日志列表卡片 -->
    <el-card class="log-table-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>日志列表</span>
          <div class="stats">
            <el-tag type="info">当前文件: {{ queryParams.fileName }}</el-tag>
            <el-tag type="primary">共 {{ total }} 条</el-tag>
          </div>
        </div>
      </template>

      <el-table
        :data="logList"
        style="width: 100%"
        v-loading="loading"
        stripe
        border
        :max-height="500">
        <el-table-column prop="id" label="行号" width="80" align="center" />
        
        <el-table-column prop="timestamp" label="时间" width="180" align="center" />
        
        <el-table-column label="级别" width="90" align="center">
          <template #default="scope">
            <el-tag
              :type="getLevelColor(scope.row.level)"
              effect="dark"
              size="small">
              {{ scope.row.level }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="thread" label="线程" width="200" show-overflow-tooltip />
        
        <el-table-column prop="logger" label="日志来源" width="300" show-overflow-tooltip />
        
        <el-table-column prop="message" label="日志内容" min-width="400" show-overflow-tooltip>
          <template #default="scope">
            <span class="log-message">{{ scope.row.message }}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              link
              @click="handleViewDetail(scope.row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-if="total > 0"
        v-model:current-page="queryParams.current"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[50, 100, 200, 500]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: flex-end"
        @size-change="handleSearch"
        @current-change="handleSearch" />
    </el-card>

    <!-- 日志详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="日志详情"
      width="800px"
      :close-on-click-modal="false">
      <el-descriptions :column="1" border v-if="currentLog">
        <el-descriptions-item label="行号">{{ currentLog.id }}</el-descriptions-item>
        <el-descriptions-item label="时间">{{ currentLog.timestamp }}</el-descriptions-item>
        <el-descriptions-item label="级别">
          <el-tag :type="getLevelColor(currentLog.level)" effect="dark">
            {{ currentLog.level }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="线程">{{ currentLog.thread }}</el-descriptions-item>
        <el-descriptions-item label="日志来源">{{ currentLog.logger }}</el-descriptions-item>
        <el-descriptions-item label="日志内容">
          <pre class="log-content">{{ currentLog.message }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="原始日志">
          <pre class="log-content">{{ currentLog.rawLog }}</pre>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Download, Delete } from '@element-plus/icons-vue'
import { getLogFiles, queryLogs, downloadLog, clearLog, deleteLog } from '@/api/log'

const logFiles = ref([])
const logList = ref([])
const total = ref(0)
const loading = ref(false)
const dateRange = ref([])
const detailDialogVisible = ref(false)
const currentLog = ref(null)

const queryParams = ref({
  fileName: 'app.log',
  level: 'ALL',
  keyword: '',
  startTime: '',
  endTime: '',
  current: 1,
  size: 50
})

onMounted(() => {
  fetchLogFiles()
  fetchLogs()
})

// 获取日志文件列表
const fetchLogFiles = async () => {
  try {
    const res = await getLogFiles()
    logFiles.value = res.data || []
  } catch (error) {
    ElMessage.error('获取日志文件列表失败: ' + error.message)
  }
}

// 获取日志内容
const fetchLogs = async () => {
  loading.value = true
  try {
    // 处理时间范围
    if (dateRange.value && dateRange.value.length === 2) {
      queryParams.value.startTime = dateRange.value[0]
      queryParams.value.endTime = dateRange.value[1]
    } else {
      queryParams.value.startTime = ''
      queryParams.value.endTime = ''
    }
    
    const res = await queryLogs(queryParams.value)
    logList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('查询日志失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 文件切换
const handleFileChange = () => {
  queryParams.value.current = 1
  fetchLogs()
}

// 搜索
const handleSearch = () => {
  queryParams.value.current = 1
  fetchLogs()
}

// 重置
const handleReset = () => {
  queryParams.value = {
    fileName: 'app.log',
    level: 'ALL',
    keyword: '',
    startTime: '',
    endTime: '',
    current: 1,
    size: 50
  }
  dateRange.value = []
  fetchLogs()
}

// 刷新
const handleRefresh = () => {
  fetchLogFiles()
  fetchLogs()
  ElMessage.success('刷新成功')
}

// 下载
const handleDownload = async () => {
  try {
    const res = await downloadLog(queryParams.value.fileName)
    
    // 创建 Blob
    const blob = new Blob([res], { type: 'text/plain' })
    const url = window.URL.createObjectURL(blob)
    
    // 创建隐藏的 a 标签进行下载
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', queryParams.value.fileName)
    document.body.appendChild(link)
    link.click()
    
    // 清理
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('下载成功')
  } catch (error) {
    ElMessage.error('下载失败: ' + error.message)
  }
}

// 清空
const handleClear = () => {
  ElMessageBox.confirm(
    `确定要清空日志文件【${queryParams.value.fileName}】吗？此操作不可恢复！`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await clearLog(queryParams.value.fileName)
      ElMessage.success('清空成功')
      fetchLogs()
      fetchLogFiles()
    } catch (error) {
      ElMessage.error('清空失败: ' + error.message)
    }
  })
}

// 删除
const handleDelete = () => {
  if (queryParams.value.fileName === 'app.log') {
    ElMessage.warning('不允许删除当前主日志文件')
    return
  }
  
  ElMessageBox.confirm(
    `确定要删除日志文件【${queryParams.value.fileName}】吗？此操作不可恢复！`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    }
  ).then(async () => {
    try {
      await deleteLog(queryParams.value.fileName)
      ElMessage.success('删除成功')
      queryParams.value.fileName = 'app.log'
      fetchLogFiles()
      fetchLogs()
    } catch (error) {
      ElMessage.error('删除失败: ' + error.message)
    }
  })
}

// 查看详情
const handleViewDetail = (row) => {
  currentLog.value = row
  detailDialogVisible.value = true
}

// 获取日志级别颜色
const getLevelColor = (level) => {
  const colorMap = {
    'TRACE': 'info',
    'DEBUG': '',
    'INFO': 'success',
    'WARN': 'warning',
    'ERROR': 'danger',
    'UNKNOWN': 'info'
  }
  return colorMap[level] || 'info'
}
</script>

<style lang="scss" scoped>
.log-container {
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
    }
  }

  .action-card {
    .action-buttons {
      display: flex;
      justify-content: space-between;
    }
  }

  .log-table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .stats {
        display: flex;
        gap: 10px;
      }
    }

    .log-message {
      font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
      font-size: 13px;
    }
  }

  .log-content {
    white-space: pre-wrap;
    word-wrap: break-word;
    background: #f5f7fa;
    padding: 10px;
    border-radius: 4px;
    font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
    font-size: 13px;
    line-height: 1.5;
    margin: 0;
  }
}
</style>

