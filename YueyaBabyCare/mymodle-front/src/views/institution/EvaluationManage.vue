<template>
  <div class="evaluation-manage">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="4">
        <el-card class="stats-card" shadow="hover">
          <div class="stats-content">
            <div class="stats-value">{{ statsData.totalCount || 0 }}</div>
            <div class="stats-label">总评价数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card avg-score" shadow="hover">
          <div class="stats-content">
            <div class="stats-value">{{ statsData.avgScore || '0.0' }}</div>
            <div class="stats-label">平均评分</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card star5" shadow="hover">
          <div class="stats-content">
            <div class="stats-value">{{ statsData.star5Count || 0 }}</div>
            <div class="stats-label">5星好评</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card star4" shadow="hover">
          <div class="stats-content">
            <div class="stats-value">{{ statsData.star4Count || 0 }}</div>
            <div class="stats-label">4星评价</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card star3" shadow="hover">
          <div class="stats-content">
            <div class="stats-value">{{ statsData.star3Count || 0 }}</div>
            <div class="stats-label">3星评价</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card positive" shadow="hover">
          <div class="stats-content">
            <div class="stats-value">{{ statsData.positiveRate || 0 }}%</div>
            <div class="stats-label">好评率</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">评价监管</span>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="保姆">
          <el-select v-model="queryParams.nannyId" placeholder="全部保姆" clearable filterable style="width: 150px">
            <el-option
              v-for="nanny in nannyList"
              :key="nanny.id"
              :label="nanny.realName"
              :value="nanny.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="评分">
          <el-select v-model="queryParams.score" placeholder="全部评分" clearable style="width: 130px">
            <el-option label="全部" :value="null" />
            <el-option label="5星" :value="5" />
            <el-option label="4星" :value="4" />
            <el-option label="3星" :value="3" />
            <el-option label="2星" :value="2" />
            <el-option label="1星" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table v-loading="loading" :data="evaluationList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="parentName" label="评价家长" width="100" />
        <el-table-column prop="nannyName" label="被评保姆" width="100" />
        <el-table-column prop="babyName" label="宝宝" width="100" />
        <el-table-column prop="carePlanName" label="护理计划" min-width="150" show-overflow-tooltip />
        <el-table-column label="评分" width="140">
          <template #default="{ row }">
            <div class="rating-display">
              <el-rate v-model="row.score" disabled text-color="#ff9900" />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评价内容" min-width="200" show-overflow-tooltip />
        <el-table-column label="图片" width="100">
          <template #default="{ row }">
            <el-button
              v-if="row.images && parseImages(row.images).length > 0"
              type="primary"
              link
              @click="handleViewImages(row)"
            >
              查看({{ parseImages(row.images).length }})
            </el-button>
            <span v-else class="text-muted">无</span>
          </template>
        </el-table-column>
        <el-table-column label="回复状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.replyContent ? 'success' : 'info'" size="small">
              {{ row.replyContent ? '已回复' : '待回复' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="评价时间" width="170">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleViewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-if="total > queryParams.pageSize"
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="getList"
        @current-change="getList"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="评价详情" width="650px">
      <el-descriptions v-if="currentEvaluation" :column="2" border>
        <el-descriptions-item label="评价ID">{{ currentEvaluation.id }}</el-descriptions-item>
        <el-descriptions-item label="评分">
          <el-rate v-model="currentEvaluation.score" disabled text-color="#ff9900" />
        </el-descriptions-item>
        <el-descriptions-item label="评价家长">{{ currentEvaluation.parentName }}</el-descriptions-item>
        <el-descriptions-item label="被评保姆">{{ currentEvaluation.nannyName }}</el-descriptions-item>
        <el-descriptions-item label="宝宝姓名">{{ currentEvaluation.babyName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="护理计划">{{ currentEvaluation.carePlanName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="评价内容" :span="2">
          <div class="content-text">{{ currentEvaluation.content || '该用户未填写评价内容' }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="评价图片" :span="2">
          <div v-if="currentEvaluation.images && parseImages(currentEvaluation.images).length > 0" class="image-list">
            <el-image
              v-for="(img, index) in parseImages(currentEvaluation.images)"
              :key="index"
              :src="getFullImageUrl(img)"
              :preview-src-list="parseImages(currentEvaluation.images).map(i => getFullImageUrl(i))"
              :initial-index="index"
              style="width: 100px; height: 100px; margin-right: 10px; margin-bottom: 10px"
              fit="cover"
            />
          </div>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="评价时间" :span="2">{{ formatDateTime(currentEvaluation.createTime) }}</el-descriptions-item>
      </el-descriptions>
      
      <!-- 回复区域 -->
      <div v-if="currentEvaluation" class="reply-section">
        <div class="reply-title">保姆回复</div>
        <div v-if="currentEvaluation.replyContent" class="reply-content">
          <div class="reply-text">{{ currentEvaluation.replyContent }}</div>
          <div class="reply-time">回复时间: {{ formatDateTime(currentEvaluation.replyTime) }}</div>
        </div>
        <div v-else class="no-reply">保姆尚未回复</div>
      </div>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog v-model="imagePreviewVisible" title="评价图片" width="800px">
      <div class="image-preview-list">
        <el-image
          v-for="(img, index) in previewImages"
          :key="index"
          :src="getFullImageUrl(img)"
          :preview-src-list="previewImages.map(i => getFullImageUrl(i))"
          :initial-index="index"
          style="width: 150px; height: 150px; margin: 10px"
          fit="cover"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getInstitutionEvaluations } from '@/api/careEvaluation'
import { getNannyList } from '@/api/institution'

const loading = ref(false)
const evaluationList = ref([])
const total = ref(0)
const nannyList = ref([])
const detailVisible = ref(false)
const currentEvaluation = ref(null)
const imagePreviewVisible = ref(false)
const previewImages = ref([])

// 查询参数
const queryParams = reactive({
  nannyId: null,
  score: null,
  pageNum: 1,
  pageSize: 10
})

// 统计数据
const statsData = computed(() => {
  const list = evaluationList.value
  const totalCount = list.length
  if (totalCount === 0) {
    return {
      totalCount: 0,
      avgScore: '0.0',
      star5Count: 0,
      star4Count: 0,
      star3Count: 0,
      positiveRate: 0
    }
  }
  
  const star5Count = list.filter(e => e.score === 5).length
  const star4Count = list.filter(e => e.score === 4).length
  const star3Count = list.filter(e => e.score === 3).length
  const totalScore = list.reduce((sum, e) => sum + (e.score || 0), 0)
  const avgScore = (totalScore / totalCount).toFixed(1)
  const positiveRate = Math.round(((star5Count + star4Count) / totalCount) * 100)
  
  return {
    totalCount,
    avgScore,
    star5Count,
    star4Count,
    star3Count,
    positiveRate
  }
})

// 获取评价列表
const getList = async () => {
  loading.value = true
  try {
    const params = {}
    if (queryParams.nannyId) params.nannyId = queryParams.nannyId
    if (queryParams.score) params.score = queryParams.score
    
    const res = await getInstitutionEvaluations(params)
    if (res.code === 200) {
      evaluationList.value = res.data || []
      total.value = evaluationList.value.length
    } else {
      evaluationList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取评价列表失败:', error)
    evaluationList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 获取保姆列表
const loadNannyList = async () => {
  try {
    const res = await getNannyList()
    if (res.code === 200) {
      nannyList.value = res.data || []
    }
  } catch (error) {
    console.error('获取保姆列表失败:', error)
  }
}

// 查询
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置
const handleReset = () => {
  queryParams.nannyId = null
  queryParams.score = null
  queryParams.pageNum = 1
  getList()
}

// 查看详情
const handleViewDetail = (row) => {
  currentEvaluation.value = row
  detailVisible.value = true
}

// 查看图片
const handleViewImages = (row) => {
  previewImages.value = parseImages(row.images)
  imagePreviewVisible.value = true
}

// 解析图片JSON
const parseImages = (imagesStr) => {
  if (!imagesStr) return []
  try {
    const parsed = JSON.parse(imagesStr)
    return Array.isArray(parsed) ? parsed : []
  } catch (e) {
    return []
  }
}

// 获取完整图片URL
const getFullImageUrl = (img) => {
  if (!img) return ''
  if (img.startsWith('http')) return img
  if (img.startsWith('/')) {
    return (import.meta.env.VITE_API_URL || 'http://localhost:8080') + img
  }
  return img
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  if (typeof dateTime === 'string') {
    return dateTime.replace('T', ' ').substring(0, 19)
  }
  return dateTime
}

onMounted(() => {
  getList()
  loadNannyList()
})
</script>

<style scoped>
.evaluation-manage {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.stats-card:hover {
  transform: translateY(-2px);
}

.stats-content {
  padding: 10px 0;
}

.stats-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stats-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.stats-card.avg-score .stats-value {
  color: #f59e0b;
}

.stats-card.star5 .stats-value {
  color: #10b981;
}

.stats-card.star4 .stats-value {
  color: #3b82f6;
}

.stats-card.star3 .stats-value {
  color: #f97316;
}

.stats-card.positive .stats-value {
  color: #8b5cf6;
}

.box-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header .title {
  font-size: 16px;
  font-weight: 600;
}

.search-form {
  margin-bottom: 20px;
}

.rating-display {
  display: flex;
  align-items: center;
}

.text-muted {
  color: #909399;
}

.content-text {
  line-height: 1.6;
  color: #303133;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
}

.image-preview-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
}

.reply-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.reply-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.reply-content {
  background: #f5f7fa;
  padding: 12px 16px;
  border-radius: 8px;
}

.reply-text {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.reply-time {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.no-reply {
  font-size: 14px;
  color: #909399;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
  text-align: center;
}
</style>
