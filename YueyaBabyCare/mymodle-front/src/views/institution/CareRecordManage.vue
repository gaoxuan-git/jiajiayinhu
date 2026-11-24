<template>
  <div class="care-record-manage">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="4">
        <el-card class="stats-card" shadow="hover">
          <div class="stats-content">
            <div class="stats-value">{{ statsData.total || 0 }}</div>
            <div class="stats-label">总记录数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card feeding" shadow="hover">
          <div class="stats-content">
            <div class="stats-value">{{ statsData.feeding || 0 }}</div>
            <div class="stats-label">喂养</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card bathing" shadow="hover">
          <div class="stats-content">
            <div class="stats-value">{{ statsData.bathing || 0 }}</div>
            <div class="stats-label">洗护</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card sleep" shadow="hover">
          <div class="stats-content">
            <div class="stats-value">{{ statsData.sleep || 0 }}</div>
            <div class="stats-label">哄睡</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card education" shadow="hover">
          <div class="stats-content">
            <div class="stats-value">{{ statsData.education || 0 }}</div>
            <div class="stats-label">早教</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card other" shadow="hover">
          <div class="stats-content">
            <div class="stats-value">{{ statsData.other || 0 }}</div>
            <div class="stats-label">其他</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">护理记录监管</span>
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
        <el-form-item label="宝宝">
          <el-select v-model="queryParams.babyId" placeholder="全部宝宝" clearable filterable style="width: 150px">
            <el-option
              v-for="baby in babyList"
              :key="baby.id"
              :label="baby.name"
              :value="baby.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="护理类型">
          <el-select v-model="queryParams.careType" placeholder="全部类型" clearable style="width: 130px">
            <el-option label="全部" :value="null" />
            <el-option label="喂养" :value="1" />
            <el-option label="洗护" :value="2" />
            <el-option label="哄睡" :value="3" />
            <el-option label="早教" :value="4" />
            <el-option label="其他" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table v-loading="loading" :data="recordList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="recordDate" label="记录日期" width="120" />
        <el-table-column prop="recordTime" label="记录时间" width="100">
          <template #default="{ row }">
            {{ formatTime(row.recordTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="careTypeName" label="护理类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getCareTypeTag(row.careType)">{{ row.careTypeName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="babyName" label="宝宝" width="100" />
        <el-table-column prop="nannyName" label="保姆" width="100" />
        <el-table-column prop="careContent" label="护理内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="remark" label="备注" width="150" show-overflow-tooltip />
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
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleViewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
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
    <el-dialog v-model="detailVisible" title="护理记录详情" width="600px">
      <el-descriptions v-if="currentRecord" :column="2" border>
        <el-descriptions-item label="记录ID">{{ currentRecord.id }}</el-descriptions-item>
        <el-descriptions-item label="护理类型">
          <el-tag :type="getCareTypeTag(currentRecord.careType)">{{ currentRecord.careTypeName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="宝宝姓名">{{ currentRecord.babyName }}</el-descriptions-item>
        <el-descriptions-item label="护理保姆">{{ currentRecord.nannyName }}</el-descriptions-item>
        <el-descriptions-item label="记录日期">{{ currentRecord.recordDate }}</el-descriptions-item>
        <el-descriptions-item label="记录时间">{{ formatTime(currentRecord.recordTime) }}</el-descriptions-item>
        <el-descriptions-item label="护理内容" :span="2">{{ currentRecord.careContent }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentRecord.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="护理图片" :span="2">
          <div v-if="currentRecord.images && parseImages(currentRecord.images).length > 0" class="image-list">
            <el-image
              v-for="(img, index) in parseImages(currentRecord.images)"
              :key="index"
              :src="getFullImageUrl(img)"
              :preview-src-list="parseImages(currentRecord.images).map(i => getFullImageUrl(i))"
              :initial-index="index"
              style="width: 100px; height: 100px; margin-right: 10px; margin-bottom: 10px"
              fit="cover"
            />
          </div>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ currentRecord.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog v-model="imagePreviewVisible" title="护理图片" width="800px">
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
import { getInstitutionCareRecords, getCareRecordById } from '@/api/careRecord'
import { getNannyList } from '@/api/institution'

const loading = ref(false)
const recordList = ref([])
const total = ref(0)
const detailVisible = ref(false)
const currentRecord = ref(null)
const imagePreviewVisible = ref(false)
const previewImages = ref([])

// 保姆和宝宝列表（用于筛选）
const nannyList = ref([])
const babyList = ref([])

const queryParams = reactive({
  nannyId: null,
  babyId: null,
  careType: null,
  pageNum: 1,
  pageSize: 10
})

// 统计数据
const statsData = computed(() => {
  const stats = {
    total: recordList.value.length,
    feeding: 0,
    bathing: 0,
    sleep: 0,
    education: 0,
    other: 0
  }
  recordList.value.forEach(record => {
    switch (record.careType) {
      case 1: stats.feeding++; break
      case 2: stats.bathing++; break
      case 3: stats.sleep++; break
      case 4: stats.education++; break
      case 5: stats.other++; break
    }
  })
  return stats
})

// 获取护理类型标签颜色
const getCareTypeTag = (type) => {
  const typeMap = {
    1: 'success',   // 喂养
    2: 'primary',   // 洗护
    3: 'warning',   // 哄睡
    4: 'danger',    // 早教
    5: 'info'       // 其他
  }
  return typeMap[type] || 'info'
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  // 处理可能的时间格式 (HH:mm:ss 或 HH:mm:ss.SSS)
  if (typeof time === 'string') {
    return time.substring(0, 5)
  }
  return time
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
    return (import.meta.env.VITE_API_BASE_URL || '') + img
  }
  return img
}

// 获取护理记录列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getInstitutionCareRecords(queryParams)
    recordList.value = res.data || []
    // 从列表中提取宝宝列表（去重）
    const babyMap = new Map()
    recordList.value.forEach(record => {
      if (record.babyId && record.babyName && !babyMap.has(record.babyId)) {
        babyMap.set(record.babyId, { id: record.babyId, name: record.babyName })
      }
    })
    babyList.value = Array.from(babyMap.values())
  } catch (error) {
    console.error('获取护理记录失败:', error)
    ElMessage.error('获取护理记录失败')
  } finally {
    loading.value = false
  }
}

// 获取保姆列表
const loadNannyList = async () => {
  try {
    const res = await getNannyList({ pageNum: 1, pageSize: 100, status: 1 })
    nannyList.value = res.data?.records || []
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
  queryParams.babyId = null
  queryParams.careType = null
  queryParams.pageNum = 1
  handleQuery()
}

// 查看详情
const handleViewDetail = async (row) => {
  try {
    const res = await getCareRecordById(row.id)
    currentRecord.value = res.data
    detailVisible.value = true
  } catch (error) {
    console.error('获取记录详情失败:', error)
    ElMessage.error('获取记录详情失败')
  }
}

// 查看图片
const handleViewImages = (row) => {
  previewImages.value = parseImages(row.images)
  imagePreviewVisible.value = true
}

onMounted(() => {
  getList()
  loadNannyList()
})
</script>

<style scoped>
.care-record-manage {
  padding: 10px;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  text-align: center;
  transition: all 0.3s;
}

.stats-card:hover {
  transform: translateY(-3px);
}

.stats-content {
  padding: 10px 0;
}

.stats-value {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
}

.stats-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.stats-card.feeding .stats-value {
  color: #67C23A;
}

.stats-card.bathing .stats-value {
  color: #409EFF;
}

.stats-card.sleep .stats-value {
  color: #E6A23C;
}

.stats-card.education .stats-value {
  color: #F56C6C;
}

.stats-card.other .stats-value {
  color: #909399;
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
  font-weight: bold;
}

.search-form {
  margin-bottom: 20px;
}

.text-muted {
  color: #909399;
}

.image-list,
.image-preview-list {
  display: flex;
  flex-wrap: wrap;
}
</style>
