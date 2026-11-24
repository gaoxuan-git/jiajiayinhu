<template>
  <div class="finance-container">
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card income">
        <div class="stat-icon">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">¥{{ formatMoney(stats.totalIncome) }}</div>
          <div class="stat-label">总收入</div>
        </div>
        <div class="stat-extra">今日 ¥{{ formatMoney(stats.todayIncome) }}</div>
      </div>
      <div class="stat-card expense">
        <div class="stat-icon">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">¥{{ formatMoney(stats.totalExpense) }}</div>
          <div class="stat-label">总支出</div>
        </div>
        <div class="stat-extra">今日 ¥{{ formatMoney(stats.todayExpense) }}</div>
      </div>
      <div class="stat-card net" :class="{ negative: stats.netIncome < 0 }">
        <div class="stat-icon">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">¥{{ formatMoney(stats.netIncome) }}</div>
          <div class="stat-label">净收入</div>
        </div>
        <div class="stat-extra">本月 ¥{{ formatMoney(stats.monthIncome - stats.monthExpense) }}</div>
      </div>
      <div class="stat-card month">
        <div class="stat-icon">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalRecords || 0 }}</div>
          <div class="stat-label">记录总数</div>
        </div>
        <div class="stat-extra">本月收入 ¥{{ formatMoney(stats.monthIncome) }}</div>
      </div>
    </div>

    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>收支趋势（近7天）</span>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>收入来源分布</span>
            </div>
          </template>
          <div ref="incomeSourceChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 财务记录列表 -->
    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>财务记录</span>
          <div class="header-actions">
            <el-button type="primary" icon="Plus" @click="handleAdd">新增记录</el-button>
            <el-button icon="Download" @click="showExportDialog = true">导出报表</el-button>
          </div>
        </div>
      </template>

      <!-- 筛选条件 -->
      <el-form :inline="true" :model="queryForm" class="filter-form">
        <el-form-item label="记录类型">
          <el-select v-model="queryForm.recordType" placeholder="全部" clearable style="width: 120px">
            <el-option label="收入" :value="1" />
            <el-option label="支出" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="queryForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="loadRecordList">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 记录表格 -->
      <el-table :data="recordList" v-loading="tableLoading" style="width: 100%">
        <el-table-column prop="recordDate" label="日期" width="120" />
        <el-table-column label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.recordType === 1 ? 'success' : 'danger'" effect="plain">
              {{ row.recordTypeName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="金额" width="140" align="right">
          <template #default="{ row }">
            <span :class="row.recordType === 1 ? 'text-success' : 'text-danger'">
              {{ row.recordType === 1 ? '+' : '-' }}¥{{ formatMoney(row.amount) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="sourceTypeName" label="来源类型" width="120" />
        <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
        <el-table-column prop="operatorName" label="操作人" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryForm.pageNum"
          v-model:page-size="queryForm.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadRecordList"
          @current-change="loadRecordList"
        />
      </div>
    </el-card>

    <!-- 月度汇总 -->
    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>月度汇总</span>
          <el-select v-model="summaryYear" style="width: 120px" @change="loadMonthlySummary">
            <el-option v-for="year in yearOptions" :key="year" :label="`${year}年`" :value="year" />
          </el-select>
        </div>
      </template>
      <el-table :data="monthlySummary" v-loading="summaryLoading" style="width: 100%">
        <el-table-column prop="monthName" label="月份" width="100" align="center" />
        <el-table-column label="收入" align="right">
          <template #default="{ row }">
            <span class="text-success">¥{{ formatMoney(row.income) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="支出" align="right">
          <template #default="{ row }">
            <span class="text-danger">¥{{ formatMoney(row.expense) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="净收入" align="right">
          <template #default="{ row }">
            <span :class="row.netIncome >= 0 ? 'text-success' : 'text-danger'">
              ¥{{ formatMoney(row.netIncome) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="recordCount" label="记录数" width="100" align="center" />
      </el-table>
    </el-card>

    <!-- 新增记录对话框 -->
    <el-dialog v-model="showAddDialog" title="新增财务记录" width="500px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="记录类型" prop="recordType">
          <el-radio-group v-model="form.recordType">
            <el-radio :value="1">收入</el-radio>
            <el-radio :value="2">支出</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number v-model="form.amount" :min="0.01" :precision="2" :step="100" style="width: 200px" />
        </el-form-item>
        <el-form-item label="来源类型" prop="sourceType">
          <el-select v-model="form.sourceType" placeholder="请选择" style="width: 200px">
            <template v-if="form.recordType === 1">
              <el-option label="订单收入" :value="1" />
              <el-option label="服务收入" :value="2" />
              <el-option label="其他" :value="5" />
            </template>
            <template v-else>
              <el-option label="物资采购" :value="3" />
              <el-option label="人员工资" :value="4" />
              <el-option label="其他" :value="5" />
            </template>
          </el-select>
        </el-form-item>
        <el-form-item label="记录日期" prop="recordDate">
          <el-date-picker v-model="form.recordDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 200px" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 导出对话框 -->
    <el-dialog v-model="showExportDialog" title="导出财务报表" width="400px">
      <el-form :model="exportForm" label-width="100px">
        <el-form-item label="日期范围" required>
          <el-date-picker
            v-model="exportForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showExportDialog = false">取消</el-button>
        <el-button type="primary" @click="handleExport" :loading="exportLoading">导出</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick, computed } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import {
  addFinanceRecord,
  getFinanceRecordList,
  getFinanceStats,
  getMonthlySummary,
  exportFinanceReport
} from '@/api/financeRecord'

// 统计数据
const stats = ref({})

// 记录列表
const recordList = ref([])
const tableLoading = ref(false)
const total = ref(0)

// 查询表单
const queryForm = reactive({
  recordType: null,
  dateRange: [],
  pageNum: 1,
  pageSize: 10
})

// 月度汇总
const monthlySummary = ref([])
const summaryLoading = ref(false)
const summaryYear = ref(new Date().getFullYear())

// 年份选项
const yearOptions = computed(() => {
  const currentYear = new Date().getFullYear()
  return [currentYear, currentYear - 1, currentYear - 2]
})

// 新增表单
const showAddDialog = ref(false)
const formRef = ref(null)
const submitLoading = ref(false)
const form = reactive({
  recordType: 1,
  amount: null,
  sourceType: null,
  recordDate: null,
  remark: ''
})

const rules = {
  recordType: [{ required: true, message: '请选择记录类型', trigger: 'change' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
  sourceType: [{ required: true, message: '请选择来源类型', trigger: 'change' }],
  recordDate: [{ required: true, message: '请选择记录日期', trigger: 'change' }]
}

// 导出
const showExportDialog = ref(false)
const exportLoading = ref(false)
const exportForm = reactive({
  dateRange: []
})

// 图表引用
const trendChartRef = ref(null)
const incomeSourceChartRef = ref(null)
let trendChart = null
let incomeSourceChart = null

// 格式化金额
const formatMoney = (value) => {
  if (value === null || value === undefined) return '0.00'
  return Number(value).toFixed(2)
}

// 加载统计数据
const loadStats = async () => {
  try {
    const res = await getFinanceStats()
    stats.value = res.data || {}
    nextTick(() => {
      initTrendChart()
      initIncomeSourceChart()
    })
  } catch (error) {
    console.error('获取财务统计失败:', error)
  }
}

// 加载记录列表
const loadRecordList = async () => {
  tableLoading.value = true
  try {
    const params = {
      pageNum: queryForm.pageNum,
      pageSize: queryForm.pageSize
    }
    if (queryForm.recordType) {
      params.recordType = queryForm.recordType
    }
    if (queryForm.dateRange && queryForm.dateRange.length === 2) {
      params.startDate = queryForm.dateRange[0]
      params.endDate = queryForm.dateRange[1]
    }
    const res = await getFinanceRecordList(params)
    recordList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取财务记录失败:', error)
  } finally {
    tableLoading.value = false
  }
}

// 加载月度汇总
const loadMonthlySummary = async () => {
  summaryLoading.value = true
  try {
    const res = await getMonthlySummary(summaryYear.value)
    monthlySummary.value = res.data || []
  } catch (error) {
    console.error('获取月度汇总失败:', error)
  } finally {
    summaryLoading.value = false
  }
}

// 重置查询
const resetQuery = () => {
  queryForm.recordType = null
  queryForm.dateRange = []
  queryForm.pageNum = 1
  loadRecordList()
}

// 新增记录
const handleAdd = () => {
  showAddDialog.value = true
}

// 重置表单
const resetForm = () => {
  form.recordType = 1
  form.amount = null
  form.sourceType = null
  form.recordDate = null
  form.remark = ''
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  submitLoading.value = true
  try {
    await addFinanceRecord(form)
    ElMessage.success('添加成功')
    showAddDialog.value = false
    loadStats()
    loadRecordList()
    loadMonthlySummary()
  } catch (error) {
    console.error('添加财务记录失败:', error)
    ElMessage.error('添加失败')
  } finally {
    submitLoading.value = false
  }
}

// 导出报表
const handleExport = async () => {
  if (!exportForm.dateRange || exportForm.dateRange.length !== 2) {
    ElMessage.warning('请选择日期范围')
    return
  }
  exportLoading.value = true
  try {
    const res = await exportFinanceReport(exportForm.dateRange[0], exportForm.dateRange[1])
    const data = res.data
    if (data && data.records && data.records.length > 0) {
      downloadCSV(data)
      ElMessage.success('导出成功')
      showExportDialog.value = false
    } else {
      ElMessage.warning('暂无数据可导出')
    }
  } catch (error) {
    console.error('导出报表失败:', error)
    ElMessage.error('导出失败')
  } finally {
    exportLoading.value = false
  }
}

// 下载CSV
const downloadCSV = (reportData) => {
  const headers = ['日期', '类型', '金额', '来源类型', '备注', '操作人', '创建时间']
  let csv = headers.join(',') + '\n'
  reportData.records.forEach(row => {
    csv += `${row.recordDate},${row.recordTypeName},${row.amount},${row.sourceTypeName},"${row.remark || ''}",${row.operatorName || ''},${row.createTime}\n`
  })
  csv += `\n汇总,,,,,\n`
  csv += `总收入,¥${formatMoney(reportData.totalIncome)},,,,\n`
  csv += `总支出,¥${formatMoney(reportData.totalExpense)},,,,\n`
  csv += `净收入,¥${formatMoney(reportData.netIncome)},,,,\n`

  const blob = new Blob(['\ufeff' + csv], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `财务报表_${reportData.startDate}_${reportData.endDate}.csv`
  link.click()
}

// 初始化收支趋势图表
const initTrendChart = () => {
  if (!trendChartRef.value) return
  if (trendChart) trendChart.dispose()
  trendChart = echarts.init(trendChartRef.value)

  const incomeTrend = stats.value.incomeTrend || []
  const expenseTrend = stats.value.expenseTrend || []

  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['收入', '支出'], top: 0 },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: incomeTrend.map(item => item.date)
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '收入',
        type: 'line',
        smooth: true,
        areaStyle: { opacity: 0.3 },
        itemStyle: { color: '#67C23A' },
        data: incomeTrend.map(item => item.amount)
      },
      {
        name: '支出',
        type: 'line',
        smooth: true,
        areaStyle: { opacity: 0.3 },
        itemStyle: { color: '#F56C6C' },
        data: expenseTrend.map(item => item.amount)
      }
    ]
  }
  trendChart.setOption(option)
}

// 初始化收入来源分布图表
const initIncomeSourceChart = () => {
  if (!incomeSourceChartRef.value) return
  if (incomeSourceChart) incomeSourceChart.dispose()
  incomeSourceChart = echarts.init(incomeSourceChartRef.value)

  const sourceData = stats.value.incomeBySource || []
  const option = {
    tooltip: { trigger: 'item', formatter: '{b}: ¥{c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left', top: 'center' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
      labelLine: { show: false },
      data: sourceData.map(item => ({
        value: item.amount,
        name: item.name
      }))
    }]
  }
  incomeSourceChart.setOption(option)
}

// 窗口大小变化时重绘图表
const handleResize = () => {
  trendChart?.resize()
  incomeSourceChart?.resize()
}

onMounted(() => {
  loadStats()
  loadRecordList()
  loadMonthlySummary()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  incomeSourceChart?.dispose()
})
</script>

<style scoped lang="scss">
.finance-container {
  padding: 20px;

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;

    .stat-card {
      background: #FFFFFF;
      border: 1px solid #E5E7EB;
      border-radius: 12px;
      padding: 16px;
      display: flex;
      flex-direction: column;
      gap: 8px;
      transition: all 0.2s;

      &:hover {
        border-color: #D1D5DB;
        box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
      }

      .stat-icon {
        width: 44px;
        height: 44px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;

        svg {
          width: 22px;
          height: 22px;
        }
      }

      &.income .stat-icon { background: #D1FAE5; color: #10B981; }
      &.expense .stat-icon { background: #FEE2E2; color: #EF4444; }
      &.net .stat-icon { background: #DBEAFE; color: #3B82F6; }
      &.net.negative .stat-icon { background: #FEE2E2; color: #EF4444; }
      &.month .stat-icon { background: #EDE9FE; color: #8B5CF6; }

      .stat-info {
        .stat-value {
          font-size: 22px;
          font-weight: 700;
          color: #1F2937;
          line-height: 1.2;
        }

        .stat-label {
          font-size: 13px;
          color: #6B7280;
          margin-top: 2px;
        }
      }

      .stat-extra {
        font-size: 12px;
        color: #9CA3AF;
      }
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-actions {
      display: flex;
      gap: 10px;
    }
  }

  .chart-card {
    height: 100%;

    .chart-container {
      height: 300px;
    }
  }

  .filter-form {
    margin-bottom: 16px;
  }

  .pagination-wrapper {
    margin-top: 16px;
    display: flex;
    justify-content: flex-end;
  }

  .text-success {
    color: #67C23A;
    font-weight: 500;
  }

  .text-danger {
    color: #F56C6C;
    font-weight: 500;
  }
}

@media (max-width: 1200px) {
  .finance-container .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .finance-container .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
