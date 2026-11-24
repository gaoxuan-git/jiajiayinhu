<template>
  <div class="manage-page">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="选择宝宝">
          <el-select
            v-model="searchForm.babyId"
            placeholder="全部宝宝"
            clearable
            filterable
            style="width: 200px"
          >
            <el-option
              v-for="baby in babyList"
              :key="baby.id"
              :label="`${baby.name} (${baby.ageInMonths || 0}个月)`"
              :value="baby.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="记录人类型">
          <el-select
            v-model="searchForm.recorderType"
            placeholder="全部"
            clearable
            style="width: 120px"
          >
            <el-option label="家长" :value="1" />
            <el-option label="保姆" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="记录日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span class="title">健康档案列表</span>
          <span class="total">共 {{ total }} 条</span>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="babyName" label="宝宝姓名" width="100" />
        <el-table-column prop="recordDate" label="记录日期" width="110" />
        <el-table-column label="身高(cm)" width="90">
          <template #default="{ row }">
            {{ row.height || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="体重(kg)" width="90">
          <template #default="{ row }">
            {{ row.weight || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="头围(cm)" width="90">
          <template #default="{ row }">
            {{ row.headCircumference || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="体温(℃)" width="90">
          <template #default="{ row }">
            <span :class="{ 'fever': row.temperature > 37.5 }">
              {{ row.temperature || '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="healthStatus" label="健康状况" min-width="150" show-overflow-tooltip />
        <el-table-column prop="vaccination" label="疫苗接种" width="150" show-overflow-tooltip />
        <el-table-column label="记录人" width="100">
          <template #default="{ row }">
            <el-tag :type="row.recorderType === 1 ? 'primary' : 'success'" size="small">
              {{ row.recorderTypeName }}
            </el-tag>
            <div class="recorder-name">{{ row.recorderName || '-' }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleViewDetail(row)">
              详情
            </el-button>
            <el-button type="success" size="small" @click="handleViewChart(row)">
              图表
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="健康记录详情" width="650px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="记录ID">{{ currentRecord.id }}</el-descriptions-item>
        <el-descriptions-item label="宝宝姓名">{{ currentRecord.babyName }}</el-descriptions-item>
        <el-descriptions-item label="记录日期">{{ currentRecord.recordDate }}</el-descriptions-item>
        <el-descriptions-item label="记录人">
          <el-tag :type="currentRecord.recorderType === 1 ? 'primary' : 'success'" size="small">
            {{ currentRecord.recorderTypeName }}
          </el-tag>
          {{ currentRecord.recorderName }}
        </el-descriptions-item>
        <el-descriptions-item label="身高">
          {{ currentRecord.height ? currentRecord.height + ' cm' : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="体重">
          {{ currentRecord.weight ? currentRecord.weight + ' kg' : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="头围">
          {{ currentRecord.headCircumference ? currentRecord.headCircumference + ' cm' : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="体温">
          <span :class="{ 'fever': currentRecord.temperature > 37.5 }">
            {{ currentRecord.temperature ? currentRecord.temperature + ' ℃' : '-' }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="健康状况" :span="2">
          {{ currentRecord.healthStatus || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="疫苗接种" :span="2">
          {{ currentRecord.vaccination || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="疾病记录" :span="2">
          {{ currentRecord.illnessRecord || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentRecord.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ currentRecord.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 图表对话框 -->
    <el-dialog v-model="chartDialogVisible" title="健康数据图表" width="900px" @opened="onChartDialogOpened">
      <div class="chart-header">
        <span class="baby-name">{{ chartBabyName }} 的健康数据趋势</span>
        <el-date-picker
          v-model="chartDateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          @change="loadChartData"
        />
      </div>
      <div v-loading="chartLoading" class="chart-container">
        <div ref="heightWeightChartRef" class="chart" :style="{ display: hasChartData ? 'block' : 'none' }"></div>
        <div ref="temperatureChartRef" class="chart" :style="{ display: hasChartData ? 'block' : 'none' }"></div>
        <el-empty v-if="!chartLoading && !hasChartData" description="暂无数据" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { pageHealthRecords, getHealthChartData } from '@/api/babyHealthRecord'
import { getAllBabies } from '@/api/baby'
import * as echarts from 'echarts'

// 宝宝列表
const babyList = ref([])

// 搜索表单
const searchForm = ref({
  babyId: null,
  recorderType: null
})
const dateRange = ref([])

// 表格数据
const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

// 详情对话框
const detailDialogVisible = ref(false)
const currentRecord = ref({})

// 图表对话框
const chartDialogVisible = ref(false)
const chartLoading = ref(false)
const chartBabyId = ref(null)
const chartBabyName = ref('')
const chartDateRange = ref([])
const chartData = ref({})
const heightWeightChartRef = ref(null)
const temperatureChartRef = ref(null)
let heightWeightChart = null
let temperatureChart = null

// 计算属性：是否有图表数据
const hasChartData = computed(() => {
  return chartData.value.dates && chartData.value.dates.length > 0
})

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (searchForm.value.babyId) {
      params.babyId = searchForm.value.babyId
    }
    if (searchForm.value.recorderType) {
      params.recorderType = searchForm.value.recorderType
    }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }

    const res = await pageHealthRecords(params)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取健康记录失败:', error)
    ElMessage.error('获取健康记录失败')
  } finally {
    loading.value = false
  }
}

// 加载宝宝列表
const loadBabyList = async () => {
  try {
    const res = await getAllBabies()
    babyList.value = res.data || []
  } catch (error) {
    console.error('获取宝宝列表失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pageNum.value = 1
  fetchData()
}

// 重置
const handleReset = () => {
  searchForm.value = {
    babyId: null,
    recorderType: null
  }
  dateRange.value = []
  pageNum.value = 1
  fetchData()
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchData()
}

const handleCurrentChange = (val) => {
  pageNum.value = val
  fetchData()
}

// 查看详情
const handleViewDetail = (row) => {
  currentRecord.value = row
  detailDialogVisible.value = true
}

// 查看图表
const handleViewChart = (row) => {
  chartBabyId.value = row.babyId
  chartBabyName.value = row.babyName
  chartDateRange.value = []
  chartData.value = {}
  chartDialogVisible.value = true
}

// 对话框打开后加载数据
const onChartDialogOpened = async () => {
  await loadChartData()
}

// 加载图表数据
const loadChartData = async () => {
  if (!chartBabyId.value) return

  chartLoading.value = true
  try {
    const startDate = chartDateRange.value?.[0] || null
    const endDate = chartDateRange.value?.[1] || null
    const res = await getHealthChartData(chartBabyId.value, startDate, endDate)
    chartData.value = res.data || {}
  } catch (error) {
    console.error('获取图表数据失败:', error)
    ElMessage.error('获取图表数据失败')
  } finally {
    chartLoading.value = false
    // 使用较长延迟确保 DOM 完全准备好
    setTimeout(() => {
      renderCharts()
    }, 300)
  }
}

// 渲染图表
const renderCharts = () => {
  if (!chartData.value.dates || chartData.value.dates.length === 0) {
    return
  }

  // 将 Proxy 数据转换为普通数组
  const dates = [...chartData.value.dates]
  const heights = [...(chartData.value.heights || [])]
  const weights = [...(chartData.value.weights || [])]
  const headCircumferences = [...(chartData.value.headCircumferences || [])]
  const temperatures = [...(chartData.value.temperatures || [])]

  // 身高体重图表
  const chartDom1 = heightWeightChartRef.value
  if (chartDom1) {
    // 确保容器有尺寸
    if (chartDom1.offsetWidth === 0 || chartDom1.offsetHeight === 0) {
      console.warn('Chart container has no size')
      return
    }

    if (heightWeightChart) {
      heightWeightChart.dispose()
      heightWeightChart = null
    }

    heightWeightChart = echarts.init(chartDom1)
    heightWeightChart.setOption({
      title: { text: '身高体重趋势', left: 'center' },
      tooltip: { trigger: 'axis' },
      legend: { data: ['身高(cm)', '体重(kg)', '头围(cm)'], bottom: 0 },
      grid: { left: 60, right: 60, bottom: 60, top: 50 },
      xAxis: {
        type: 'category',
        data: dates
      },
      yAxis: [
        { type: 'value', name: '身高/头围(cm)' },
        { type: 'value', name: '体重(kg)' }
      ],
      series: [
        { name: '身高(cm)', type: 'line', data: heights, smooth: true },
        { name: '头围(cm)', type: 'line', data: headCircumferences, smooth: true },
        { name: '体重(kg)', type: 'line', yAxisIndex: 1, data: weights, smooth: true }
      ]
    })
  }

  // 体温图表
  const chartDom2 = temperatureChartRef.value
  if (chartDom2) {
    if (chartDom2.offsetWidth === 0 || chartDom2.offsetHeight === 0) {
      console.warn('Temperature chart container has no size')
      return
    }

    if (temperatureChart) {
      temperatureChart.dispose()
      temperatureChart = null
    }

    temperatureChart = echarts.init(chartDom2)
    temperatureChart.setOption({
      title: { text: '体温记录', left: 'center' },
      tooltip: { trigger: 'axis' },
      grid: { left: 60, right: 40, bottom: 40, top: 50 },
      xAxis: {
        type: 'category',
        data: dates
      },
      yAxis: {
        type: 'value',
        name: '体温(℃)',
        min: 35,
        max: 40
      },
      series: [
        {
          name: '体温(℃)',
          type: 'line',
          data: temperatures,
          smooth: true,
          markLine: {
            data: [{ yAxis: 37.5, name: '发热线', lineStyle: { color: '#f56c6c' } }]
          }
        }
      ]
    })
  }
}

// 监听图表对话框关闭
watch(chartDialogVisible, (val) => {
  if (!val) {
    if (heightWeightChart) {
      heightWeightChart.dispose()
      heightWeightChart = null
    }
    if (temperatureChart) {
      temperatureChart.dispose()
      temperatureChart = null
    }
  }
})

onMounted(() => {
  loadBabyList()
  fetchData()
})
</script>

<style scoped lang="scss">
.manage-page {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .title {
      font-size: 16px;
      font-weight: 600;
    }

    .total {
      font-size: 14px;
      color: #909399;
    }
  }
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.fever {
  color: #f56c6c;
  font-weight: bold;
}

.recorder-name {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  .baby-name {
    font-size: 16px;
    font-weight: 600;
  }
}

.chart-container {
  min-height: 400px;
}

.chart {
  width: 100%;
  height: 300px;
  margin-bottom: 20px;
}
</style>
