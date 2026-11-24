<template>
  <div class="health-records-page">
    <div class="page-container">
      <!-- 返回按钮 -->
      <div class="back-nav">
        <t-button variant="text" @click="$router.back()">
          <template #icon><t-icon name="chevron-left" /></template>
          返回
        </t-button>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-wrapper">
        <t-loading size="large" text="加载中..." />
      </div>

      <template v-else>
        <!-- 页面头部 -->
        <div class="page-header">
          <div class="header-left">
            <h1 class="page-title">健康档案</h1>
            <p class="page-desc">{{ baby?.name }}的健康成长记录</p>
          </div>
          <t-button theme="primary" @click="openAddDialog">
            <template #icon><t-icon name="add" /></template>
            添加记录
          </t-button>
        </div>

        <!-- 统计卡片 -->
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-icon height-icon">
              <t-icon name="trending-up" size="28" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ latestRecord?.height || '--' }}<span class="unit">cm</span></div>
              <div class="stat-label">最新身高</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon weight-icon">
              <t-icon name="chart-bar" size="28" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ latestRecord?.weight || '--' }}<span class="unit">kg</span></div>
              <div class="stat-label">最新体重</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon head-icon">
              <t-icon name="circle" size="28" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ latestRecord?.headCircumference || '--' }}<span class="unit">cm</span></div>
              <div class="stat-label">最新头围</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon temp-icon">
              <t-icon name="thermometer" size="28" />
            </div>
            <div class="stat-content">
              <div class="stat-value" :class="{ 'warning': latestRecord?.temperature > 37.5 }">
                {{ latestRecord?.temperature || '--' }}<span class="unit">℃</span>
              </div>
              <div class="stat-label">最新体温</div>
            </div>
          </div>
        </div>

        <!-- 图表区域 -->
        <div class="chart-card">
          <div class="card-header">
            <h3>成长曲线</h3>
            <div class="chart-tabs">
              <t-radio-group v-model="chartType" variant="default-filled" size="small">
                <t-radio-button value="height">身高</t-radio-button>
                <t-radio-button value="weight">体重</t-radio-button>
                <t-radio-button value="head">头围</t-radio-button>
              </t-radio-group>
            </div>
          </div>
          <div v-if="records.length === 0" class="chart-empty">
            <t-icon name="chart-line" size="48" />
            <p>暂无数据，添加健康记录后可查看成长曲线</p>
          </div>
          <div v-else class="chart-container" ref="chartRef"></div>
        </div>

        <!-- 记录列表 -->
        <div class="table-card">
          <div class="card-header">
            <h3>健康记录列表</h3>
            <span class="record-count">共 {{ records.length }} 条记录</span>
          </div>

          <!-- 空状态 -->
          <div v-if="records.length === 0" class="empty-state">
            <t-icon name="file-paste" size="48" />
            <p>暂无健康记录</p>
            <t-button theme="primary" @click="openAddDialog">添加第一条记录</t-button>
          </div>

          <t-table
            v-else
            :data="records"
            :columns="columns"
            row-key="id"
            stripe
            hover
            :pagination="pagination"
            @page-change="onPageChange"
          >
            <template #recordDate="{ row }">
              <span class="date-text">{{ row.recordDate }}</span>
            </template>
            <template #height="{ row }">
              <span class="value-text">{{ row.height ? row.height + ' cm' : '--' }}</span>
            </template>
            <template #weight="{ row }">
              <span class="value-text">{{ row.weight ? row.weight + ' kg' : '--' }}</span>
            </template>
            <template #headCircumference="{ row }">
              <span class="value-text">{{ row.headCircumference ? row.headCircumference + ' cm' : '--' }}</span>
            </template>
            <template #temperature="{ row }">
              <span :class="['temp-value', row.temperature > 37.5 ? 'warning' : '']">
                {{ row.temperature ? row.temperature + ' ℃' : '--' }}
              </span>
            </template>
            <template #healthStatus="{ row }">
              <t-tag v-if="row.healthStatus" :theme="getHealthStatusTheme(row.healthStatus)" variant="light" size="small">
                {{ row.healthStatus }}
              </t-tag>
              <span v-else class="empty-text">--</span>
            </template>
            <template #recorderTypeName="{ row }">
              <t-tag :theme="row.recorderType === 1 ? 'primary' : 'success'" variant="light" size="small">
                {{ row.recorderType === 1 ? '家长' : '保姆' }}
              </t-tag>
            </template>
            <template #action="{ row }">
              <t-button variant="text" theme="primary" size="small" @click="openDetailDialog(row)">
                <t-icon name="browse" />
              </t-button>
              <t-button variant="text" theme="primary" size="small" @click="openEditDialog(row)">
                <t-icon name="edit" />
              </t-button>
              <t-button variant="text" theme="danger" size="small" @click="handleDelete(row)">
                <t-icon name="delete" />
              </t-button>
            </template>
          </t-table>
        </div>
      </template>
    </div>

    <!-- 新增/编辑弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="isEdit ? '编辑健康记录' : '添加健康记录'"
      :confirm-btn="{ content: '保存', theme: 'primary', loading: submitting }"
      cancel-btn="取消"
      width="680px"
      @confirm="handleSubmit"
      @close="resetForm"
    >
      <t-form ref="formRef" :data="form" :rules="rules" label-align="top">
        <t-form-item label="记录日期" name="recordDate">
          <t-date-picker
            v-model="form.recordDate"
            placeholder="请选择记录日期"
            :disable-date="disableFutureDate"
            style="width: 100%"
          />
        </t-form-item>

        <div class="form-section-title">生长指标</div>
        <div class="form-row">
          <t-form-item label="身高 (cm)" name="height">
            <t-input-number
              v-model="form.height"
              placeholder="请输入身高"
              :min="0"
              :max="200"
              :decimal-places="1"
              style="width: 100%"
            />
          </t-form-item>
          <t-form-item label="体重 (kg)" name="weight">
            <t-input-number
              v-model="form.weight"
              placeholder="请输入体重"
              :min="0"
              :max="100"
              :decimal-places="2"
              style="width: 100%"
            />
          </t-form-item>
        </div>
        <div class="form-row">
          <t-form-item label="头围 (cm)" name="headCircumference">
            <t-input-number
              v-model="form.headCircumference"
              placeholder="请输入头围"
              :min="0"
              :max="100"
              :decimal-places="1"
              style="width: 100%"
            />
          </t-form-item>
          <t-form-item label="体温 (℃)" name="temperature">
            <t-input-number
              v-model="form.temperature"
              placeholder="请输入体温"
              :min="35"
              :max="42"
              :decimal-places="1"
              style="width: 100%"
            />
          </t-form-item>
        </div>

        <div class="form-section-title">健康状况</div>
        <t-form-item label="健康状态" name="healthStatus">
          <t-select v-model="form.healthStatus" placeholder="请选择健康状态" clearable style="width: 100%">
            <t-option value="健康" label="健康" />
            <t-option value="良好" label="良好" />
            <t-option value="一般" label="一般" />
            <t-option value="感冒" label="感冒" />
            <t-option value="发烧" label="发烧" />
            <t-option value="腹泻" label="腹泻" />
            <t-option value="其他" label="其他" />
          </t-select>
        </t-form-item>
        <t-form-item label="疫苗接种情况" name="vaccination">
          <t-textarea
            v-model="form.vaccination"
            placeholder="请输入疫苗接种情况（选填）"
            :maxlength="500"
            :rows="2"
          />
        </t-form-item>
        <t-form-item label="疾病记录" name="illnessRecord">
          <t-textarea
            v-model="form.illnessRecord"
            placeholder="请输入疾病记录（选填）"
            :maxlength="500"
            :rows="2"
          />
        </t-form-item>
      </t-form>
    </t-dialog>

    <!-- 详情弹窗 -->
    <t-dialog
      v-model:visible="detailDialogVisible"
      header="健康记录详情"
      :footer="false"
      width="600px"
    >
      <div class="detail-content" v-if="currentRecord">
        <div class="detail-section">
          <div class="detail-title">基本信息</div>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="label">记录日期</span>
              <span class="value">{{ currentRecord.recordDate }}</span>
            </div>
            <div class="detail-item">
              <span class="label">记录人</span>
              <span class="value">{{ currentRecord.recorderName || (currentRecord.recorderType === 1 ? '家长' : '保姆') }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <div class="detail-title">生长指标</div>
          <div class="detail-grid four-cols">
            <div class="detail-item">
              <span class="label">身高</span>
              <span class="value highlight">{{ currentRecord.height ? currentRecord.height + ' cm' : '--' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">体重</span>
              <span class="value highlight">{{ currentRecord.weight ? currentRecord.weight + ' kg' : '--' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">头围</span>
              <span class="value highlight">{{ currentRecord.headCircumference ? currentRecord.headCircumference + ' cm' : '--' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">体温</span>
              <span class="value highlight" :class="{ 'warning': currentRecord.temperature > 37.5 }">
                {{ currentRecord.temperature ? currentRecord.temperature + ' ℃' : '--' }}
              </span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <div class="detail-title">健康状况</div>
          <div class="detail-item full">
            <span class="label">健康状态</span>
            <t-tag v-if="currentRecord.healthStatus" :theme="getHealthStatusTheme(currentRecord.healthStatus)" variant="light">
              {{ currentRecord.healthStatus }}
            </t-tag>
            <span v-else class="value">--</span>
          </div>
          <div class="detail-item full">
            <span class="label">疫苗接种</span>
            <span class="value">{{ currentRecord.vaccination || '无记录' }}</span>
          </div>
          <div class="detail-item full">
            <span class="label">疾病记录</span>
            <span class="value">{{ currentRecord.illnessRecord || '无记录' }}</span>
          </div>
        </div>
      </div>
    </t-dialog>

    <!-- 删除确认弹窗 -->
    <t-dialog
      v-model:visible="deleteDialogVisible"
      header="确认删除"
      :confirm-btn="{ content: '删除', theme: 'danger' }"
      cancel-btn="取消"
      @confirm="confirmDelete"
    >
      <p>确定要删除这条健康记录吗？此操作不可恢复。</p>
    </t-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import * as echarts from 'echarts'
import { getBabyById } from '@/api/baby'
import {
  getHealthRecords,
  getLatestHealthRecord,
  addHealthRecord,
  updateHealthRecord,
  deleteHealthRecord
} from '@/api/health'

const route = useRoute()
const babyId = route.params.babyId

// 数据状态
const loading = ref(false)
const baby = ref(null)
const records = ref([])
const latestRecord = ref(null)

// 图表相关
const chartRef = ref(null)
const chartType = ref('height')
let chartInstance = null

// 表格分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

// 表格列配置
const columns = [
  { colKey: 'recordDate', title: '记录日期', width: 120, align: 'center' },
  { colKey: 'height', title: '身高', width: 90, align: 'center' },
  { colKey: 'weight', title: '体重', width: 90, align: 'center' },
  { colKey: 'headCircumference', title: '头围', width: 90, align: 'center' },
  { colKey: 'temperature', title: '体温', width: 90, align: 'center' },
  { colKey: 'healthStatus', title: '健康状态', width: 100, align: 'center' },
  { colKey: 'recorderTypeName', title: '记录人', width: 80, align: 'center' },
  { colKey: 'action', title: '操作', width: 120, align: 'center', fixed: 'right' }
]

// 弹窗相关
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const deleteDialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const currentRecord = ref(null)
const formRef = ref(null)

// 表单数据
const form = reactive({
  recordDate: '',
  height: null,
  weight: null,
  headCircumference: null,
  temperature: null,
  healthStatus: '',
  vaccination: '',
  illnessRecord: ''
})

// 表单验证规则
const rules = {
  recordDate: [{ required: true, message: '请选择记录日期', trigger: 'change' }]
}

// 禁用未来日期
const disableFutureDate = (date) => date > new Date()

// 获取健康状态主题色
const getHealthStatusTheme = (status) => {
  const map = {
    '健康': 'success',
    '良好': 'success',
    '一般': 'warning',
    '感冒': 'warning',
    '发烧': 'danger',
    '腹泻': 'danger',
    '其他': 'default'
  }
  return map[status] || 'default'
}

// 获取宝宝信息
const fetchBaby = async () => {
  try {
    const res = await getBabyById(babyId)
    if (res.code === 200) {
      baby.value = res.data
    }
  } catch (error) {
    console.error('获取宝宝信息失败:', error)
  }
}

// 获取健康记录列表
const fetchRecords = async () => {
  loading.value = true
  try {
    const res = await getHealthRecords(babyId)
    console.log('[健康档案] API响应:', res)
    if (res.code === 200) {
      records.value = res.data || []
      pagination.total = records.value.length
      console.log('[健康档案] 记录数量:', records.value.length)
      console.log('[健康档案] 记录数据:', records.value)
    } else {
      MessagePlugin.error(res.message || '获取健康记录失败')
    }
  } catch (error) {
    console.error('获取健康记录失败:', error)
    MessagePlugin.error('获取健康记录失败')
  } finally {
    loading.value = false
  }
}

// 获取最新健康记录
const fetchLatestRecord = async () => {
  try {
    const res = await getLatestHealthRecord(babyId)
    if (res.code === 200) {
      latestRecord.value = res.data
    }
  } catch (error) {
    console.error('获取最新健康记录失败:', error)
  }
}

// 初始化图表
const initChart = () => {
  console.log('[图表] initChart 被调用')
  console.log('[图表] chartRef.value:', chartRef.value)

  if (!chartRef.value) {
    console.error('[图表] chartRef.value 为空，无法初始化图表')
    return
  }

  // 检查容器尺寸
  const rect = chartRef.value.getBoundingClientRect()
  console.log('[图表] 容器尺寸:', rect.width, 'x', rect.height)

  if (rect.width === 0 || rect.height === 0) {
    console.error('[图表] 容器尺寸为0，延迟初始化')
    // 延迟重试
    setTimeout(() => {
      console.log('[图表] 延迟重试初始化')
      initChart()
    }, 100)
    return
  }

  if (chartInstance) {
    console.log('[图表] 销毁旧实例')
    chartInstance.dispose()
  }

  console.log('[图表] 创建新的 echarts 实例')
  chartInstance = echarts.init(chartRef.value)
  console.log('[图表] echarts 实例创建成功:', chartInstance)

  updateChart()
  window.addEventListener('resize', handleResize)
}

// 更新图表
const updateChart = () => {
  console.log('[图表] updateChart 被调用')
  console.log('[图表] chartInstance:', chartInstance)
  console.log('[图表] records.value.length:', records.value.length)

  if (!chartInstance) {
    console.error('[图表] chartInstance 为空')
    return
  }

  if (records.value.length === 0) {
    console.log('[图表] 没有数据，跳过更新')
    return
  }

  const sortedRecords = [...records.value].sort((a, b) =>
    new Date(a.recordDate) - new Date(b.recordDate)
  )

  const dates = sortedRecords.map(r => r.recordDate)
  let data = []
  let unit = ''
  let color = ''
  let title = ''

  switch (chartType.value) {
    case 'height':
      data = sortedRecords.map(r => r.height)
      unit = 'cm'
      color = '#667eea'
      title = '身高'
      break
    case 'weight':
      data = sortedRecords.map(r => r.weight)
      unit = 'kg'
      color = '#f5576c'
      title = '体重'
      break
    case 'head':
      data = sortedRecords.map(r => r.headCircumference)
      unit = 'cm'
      color = '#4facfe'
      title = '头围'
      break
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#E5E7EB',
      borderWidth: 1,
      textStyle: { color: '#1F2937' },
      formatter: (params) => {
        const p = params[0]
        return `<div style="font-weight:600;margin-bottom:4px">${p.axisValue}</div>
                <div>${title}: <span style="color:${color};font-weight:600">${p.value || '--'} ${unit}</span></div>`
      }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
    xAxis: {
      type: 'category',
      data: dates,
      axisLine: { lineStyle: { color: '#E5E7EB' } },
      axisLabel: { color: '#6B7280', fontSize: 11 },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisLabel: { color: '#6B7280', formatter: `{value}` },
      splitLine: { lineStyle: { color: '#F3F4F6', type: 'dashed' } }
    },
    series: [{
      name: title,
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      data: data,
      lineStyle: { color: color, width: 3 },
      itemStyle: { color: color, borderWidth: 2, borderColor: '#fff' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: color + '40' },
          { offset: 1, color: color + '05' }
        ])
      }
    }]
  }

  chartInstance.setOption(option, true)
}

const handleResize = () => {
  chartInstance?.resize()
}

// 打开新增弹窗
const openAddDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 打开编辑弹窗
const openEditDialog = (row) => {
  isEdit.value = true
  currentRecord.value = row
  Object.assign(form, {
    recordDate: row.recordDate,
    height: row.height,
    weight: row.weight,
    headCircumference: row.headCircumference,
    temperature: row.temperature,
    healthStatus: row.healthStatus || '',
    vaccination: row.vaccination || '',
    illnessRecord: row.illnessRecord || ''
  })
  dialogVisible.value = true
}

// 打开详情弹窗
const openDetailDialog = (row) => {
  currentRecord.value = row
  detailDialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    recordDate: '',
    height: null,
    weight: null,
    headCircumference: null,
    temperature: null,
    healthStatus: '',
    vaccination: '',
    illnessRecord: ''
  })
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value?.validate()
  if (valid !== true) return

  submitting.value = true
  try {
    const data = { ...form }
    let res
    if (isEdit.value) {
      res = await updateHealthRecord(currentRecord.value.id, data)
    } else {
      res = await addHealthRecord(babyId, data)
    }

    if (res.code === 200) {
      MessagePlugin.success(isEdit.value ? '修改成功' : '添加成功')
      dialogVisible.value = false
      fetchRecords()
      fetchLatestRecord()
    } else {
      MessagePlugin.error(res.message || (isEdit.value ? '修改失败' : '添加失败'))
    }
  } catch (error) {
    console.error('提交失败:', error)
    MessagePlugin.error(isEdit.value ? '修改失败' : '添加失败')
  } finally {
    submitting.value = false
  }
}

// 删除记录
const handleDelete = (row) => {
  console.log('[删除] handleDelete 被调用, row:', row)
  currentRecord.value = row
  deleteDialogVisible.value = true
  console.log('[删除] deleteDialogVisible:', deleteDialogVisible.value)
}

const confirmDelete = async () => {
  console.log('[删除] confirmDelete 被调用')
  console.log('[删除] currentRecord:', currentRecord.value)

  if (!currentRecord.value || !currentRecord.value.id) {
    console.error('[删除] currentRecord 无效')
    MessagePlugin.error('删除失败：记录无效')
    deleteDialogVisible.value = false
    return
  }

  try {
    console.log('[删除] 调用 API, id:', currentRecord.value.id)
    const res = await deleteHealthRecord(currentRecord.value.id)
    console.log('[删除] API 响应:', res)

    if (res.code === 200) {
      MessagePlugin.success('删除成功')
      fetchRecords()
      fetchLatestRecord()
    } else {
      MessagePlugin.error(res.message || '删除失败')
    }
  } catch (error) {
    console.error('[删除] 异常:', error)
    MessagePlugin.error('删除失败')
  }
  deleteDialogVisible.value = false
}

// 分页变化
const onPageChange = (pageInfo) => {
  pagination.current = pageInfo.current
  pagination.pageSize = pageInfo.pageSize
}

// 监听图表类型变化
watch(chartType, () => {
  if (chartInstance && records.value.length > 0) {
    updateChart()
  }
})

// 监听数据和loading状态变化，初始化图表
watch(
  [() => records.value.length, loading],
  ([recordsLen, isLoading]) => {
    console.log('[Watch] records.length:', recordsLen, 'loading:', isLoading)
    if (!isLoading && recordsLen > 0) {
      // 使用 setTimeout 确保 DOM 完全渲染
      setTimeout(() => {
        console.log('[Watch] 延迟后检查 chartRef:', chartRef.value)
        if (chartRef.value && !chartInstance) {
          initChart()
        }
      }, 200)
    }
  },
  { immediate: true }
)

onMounted(() => {
  fetchBaby()
  fetchRecords()
  fetchLatestRecord()
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>

<style scoped>
.health-records-page {
  padding: 24px;
  background: #F9FAFB;
  min-height: calc(100vh - 120px);
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
}

.back-nav {
  margin-bottom: 16px;
}

.loading-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.page-title {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  color: #1F2937;
}

.page-desc {
  margin: 0;
  font-size: 14px;
  color: #6B7280;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-icon.height-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.weight-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.head-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.temp-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1F2937;
  margin-bottom: 2px;
}

.stat-value.warning {
  color: #EF4444;
}

.stat-value .unit {
  font-size: 14px;
  font-weight: 400;
  color: #6B7280;
  margin-left: 2px;
}

.stat-label {
  font-size: 13px;
  color: #6B7280;
}

/* 图表卡片 */
.chart-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1F2937;
}

.record-count {
  font-size: 14px;
  color: #6B7280;
}

.chart-container {
  height: 320px;
}

.chart-empty {
  height: 280px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #9CA3AF;
}

.chart-empty p {
  margin: 16px 0 0;
  font-size: 14px;
}

/* 表格卡片 */
.table-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.empty-state {
  padding: 60px 20px;
  text-align: center;
  color: #9CA3AF;
}

.empty-state p {
  margin: 16px 0 24px;
  font-size: 14px;
}

.date-text {
  color: #3B82F6;
  font-weight: 500;
}

.value-text {
  font-weight: 600;
  color: #1F2937;
}

.temp-value {
  font-weight: 500;
}

.temp-value.warning {
  color: #EF4444;
}

.empty-text {
  color: #9CA3AF;
}

/* 表单样式 */
.form-section-title {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  margin: 20px 0 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #F3F4F6;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

/* 详情弹窗 */
.detail-content {
  padding: 8px 0;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.detail-title {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #F3F4F6;
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.detail-grid.four-cols {
  grid-template-columns: repeat(4, 1fr);
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-item.full {
  margin-bottom: 12px;
}

.detail-item .label {
  font-size: 12px;
  color: #6B7280;
}

.detail-item .value {
  font-size: 14px;
  color: #1F2937;
}

.detail-item .value.highlight {
  font-size: 18px;
  font-weight: 600;
  color: #3B82F6;
}

.detail-item .value.warning {
  color: #EF4444;
}

/* 响应式 */
@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .detail-grid.four-cols {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  .detail-grid.four-cols {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
