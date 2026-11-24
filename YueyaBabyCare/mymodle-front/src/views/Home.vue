<template>
  <div class="home-container">
    <!-- 管理员数据看板 -->
    <div v-if="isAdmin" class="admin-dashboard">
      <div class="page-header">
        <h2>管理员数据看板</h2>
        <span class="welcome-text">欢迎回来，{{ userInfo.username || '管理员' }}</span>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon blue">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ adminData.totalParents || 0 }}</div>
            <div class="stat-label">家长总数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon emerald">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ adminData.totalInstitutions || 0 }}</div>
            <div class="stat-label">机构总数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon purple">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ adminData.totalNannies || 0 }}</div>
            <div class="stat-label">保姆总数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon amber">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ adminData.totalOrders || 0 }}</div>
            <div class="stat-label">订单总数</div>
          </div>
          <div class="stat-extra">今日 {{ adminData.todayOrders || 0 }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-icon rose">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">¥{{ formatMoney(adminData.totalRevenue) }}</div>
            <div class="stat-label">总收入</div>
          </div>
          <div class="stat-extra">今日 ¥{{ formatMoney(adminData.todayRevenue) }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-icon cyan">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ adminData.pendingOrders || 0 }}</div>
            <div class="stat-label">待处理订单</div>
          </div>
        </div>
      </div>

      <!-- 图表区域 -->
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <span>用户增长趋势（近7天）</span>
            </template>
            <div ref="adminUserChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <span>订单状态分布</span>
            </template>
            <div ref="adminOrderChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 报表导出（管理员） -->
      <el-card style="margin-top: 20px;">
        <template #header>
          <div class="card-header">
            <span>数据报表</span>
          </div>
        </template>
        <el-form :inline="true" :model="reportForm" class="report-form">
          <el-form-item label="报表类型">
            <el-select v-model="reportForm.type" placeholder="请选择" style="width: 150px">
              <el-option label="订单报表" value="order" />
              <el-option label="收入报表" value="revenue" />
              <el-option label="用户报表" value="user" />
            </el-select>
          </el-form-item>
          <el-form-item label="日期范围">
            <el-date-picker
              v-model="reportForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              style="width: 260px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Download" @click="handleAdminExportReport" :loading="exportLoading">生成报表</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 机构数据看板 -->
    <div v-else-if="isInstitution" class="institution-dashboard">
      <div class="page-header">
        <h2>机构数据看板</h2>
        <span class="welcome-text">欢迎回来，{{ userInfo.name || '机构管理员' }}</span>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon blue">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ instData.totalNannies || 0 }}</div>
            <div class="stat-label">员工总数</div>
          </div>
          <div class="stat-extra">在职 {{ instData.activeNannies || 0 }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-icon emerald">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ instData.totalAppointments || 0 }}</div>
            <div class="stat-label">预约总数</div>
          </div>
          <div class="stat-extra">待处理 {{ instData.pendingAppointments || 0 }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-icon amber">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ instData.totalOrders || 0 }}</div>
            <div class="stat-label">订单总数</div>
          </div>
          <div class="stat-extra">今日 {{ instData.todayOrders || 0 }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-icon rose">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">¥{{ formatMoney(instData.totalRevenue) }}</div>
            <div class="stat-label">总收入</div>
          </div>
          <div class="stat-extra">本月 ¥{{ formatMoney(instData.monthRevenue) }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-icon purple">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ instData.totalProducts || 0 }}</div>
            <div class="stat-label">商品总数</div>
          </div>
          <div class="stat-extra warning" v-if="instData.lowStockProducts > 0">预警 {{ instData.lowStockProducts }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-icon cyan">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ instData.avgRating || 0 }}</div>
            <div class="stat-label">平均评分</div>
          </div>
          <div class="stat-extra">满分5分</div>
        </div>
      </div>

      <!-- 图表区域 -->
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <span>订单趋势（近7天）</span>
            </template>
            <div ref="instOrderChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <span>收入趋势（近7天）</span>
            </template>
            <div ref="instRevenueChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 员工绩效排名 -->
      <el-card style="margin-top: 20px;">
        <template #header>
          <div class="card-header">
            <span>员工绩效排名</span>
            <el-radio-group v-model="rankingType" size="small" @change="loadNannyRanking">
              <el-radio-button value="order">订单数</el-radio-button>
              <el-radio-button value="revenue">收入</el-radio-button>
              <el-radio-button value="rating">评分</el-radio-button>
            </el-radio-group>
          </div>
        </template>
        <el-table :data="nannyRanking" v-loading="rankingLoading" style="width: 100%">
          <el-table-column label="排名" width="80" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.rank === 1" type="warning" effect="dark">🥇</el-tag>
              <el-tag v-else-if="row.rank === 2" type="info" effect="dark">🥈</el-tag>
              <el-tag v-else-if="row.rank === 3" type="danger" effect="dark">🥉</el-tag>
              <span v-else>{{ row.rank }}</span>
            </template>
          </el-table-column>
          <el-table-column label="员工" min-width="150">
            <template #default="{ row }">
              <div class="nanny-info">
                <el-avatar :size="32" :src="getImageUrl(row.avatar)">{{ row.nannyName?.charAt(0) }}</el-avatar>
                <span class="nanny-name">{{ row.nannyName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="orderCount" label="订单数" width="100" align="center" />
          <el-table-column label="收入" width="120" align="center">
            <template #default="{ row }">
              ¥{{ formatMoney(row.revenue) }}
            </template>
          </el-table-column>
          <el-table-column label="评分" width="100" align="center">
            <template #default="{ row }">
              <el-rate v-model="row.avgRating" disabled show-score text-color="#ff9900" :max="5" />
            </template>
          </el-table-column>
          <el-table-column prop="evaluationCount" label="评价数" width="100" align="center" />
        </el-table>
      </el-card>

      <!-- 报表导出 -->
      <el-card style="margin-top: 20px;">
        <template #header>
          <div class="card-header">
            <span>报表导出</span>
          </div>
        </template>
        <el-form :inline="true" :model="reportForm" class="report-form">
          <el-form-item label="报表类型">
            <el-select v-model="reportForm.type" placeholder="请选择" style="width: 150px">
              <el-option label="订单报表" value="order" />
              <el-option label="收入报表" value="revenue" />
              <el-option label="服务报表" value="service" />
            </el-select>
          </el-form-item>
          <el-form-item label="日期范围">
            <el-date-picker
              v-model="reportForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              style="width: 260px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Download" @click="handleExportReport" :loading="exportLoading">导出报表</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { getAdminDashboard, getUserStats, getOrderStats, getReports } from '@/api/adminDashboard'
import { getDashboard, getNannyRanking, exportReport } from '@/api/dashboard'

const store = useStore()

// 用户信息
const userInfo = computed(() => store.state.user.userInfo || {})
const userType = computed(() => store.state.user.userType) // 'admin' 或 'institution'
const isAdmin = computed(() => userInfo.value.userType === 4 || userType.value === 'admin')
const isInstitution = computed(() => userInfo.value.userType === 3 || userType.value === 'institution')

// 管理员数据
const adminData = ref({})
const adminUserStats = ref({})
const adminOrderStats = ref({})

// 机构数据
const instData = ref({})

// 员工排名
const rankingType = ref('order')
const rankingLoading = ref(false)
const nannyRanking = ref([])

// 报表导出
const exportLoading = ref(false)
const reportForm = reactive({
  type: 'order',
  dateRange: []
})

// 图表引用
const adminUserChartRef = ref(null)
const adminOrderChartRef = ref(null)
const instOrderChartRef = ref(null)
const instRevenueChartRef = ref(null)

let adminUserChart = null
let adminOrderChart = null
let instOrderChart = null
let instRevenueChart = null

// 格式化金额
const formatMoney = (value) => {
  if (!value) return '0.00'
  return Number(value).toFixed(2)
}

// 获取图片URL
const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return process.env.VUE_APP_API_BASE_URL + url
}

// 加载管理员数据
const loadAdminData = async () => {
  try {
    const [dashRes, userRes, orderRes] = await Promise.all([
      getAdminDashboard(),
      getUserStats(),
      getOrderStats()
    ])
    adminData.value = dashRes.data || {}
    adminUserStats.value = userRes.data || {}
    adminOrderStats.value = orderRes.data || {}
    nextTick(() => {
      initAdminUserChart()
      initAdminOrderChart()
    })
  } catch (error) {
    console.error('获取管理员数据失败:', error)
  }
}

// 加载机构数据
const loadInstData = async () => {
  try {
    const res = await getDashboard()
    instData.value = res.data || {}
    nextTick(() => {
      initInstOrderChart()
      initInstRevenueChart()
    })
    // 同时加载员工排名
    loadNannyRanking()
  } catch (error) {
    console.error('获取机构数据失败:', error)
  }
}

// 加载员工排名
const loadNannyRanking = async () => {
  rankingLoading.value = true
  try {
    const res = await getNannyRanking(rankingType.value)
    nannyRanking.value = res.data || []
  } catch (error) {
    console.error('获取员工排名失败:', error)
  } finally {
    rankingLoading.value = false
  }
}

// 管理员导出报表
const handleAdminExportReport = async () => {
  if (!reportForm.dateRange || reportForm.dateRange.length !== 2) {
    ElMessage.warning('请选择日期范围')
    return
  }
  exportLoading.value = true
  try {
    const res = await getReports(reportForm.type, reportForm.dateRange[0], reportForm.dateRange[1])
    const data = res.data
    if (data && data.data && data.data.length > 0) {
      downloadCSV(data)
      ElMessage.success('报表生成并导出成功')
    } else {
      ElMessage.warning('所选时间范围内暂无数据')
    }
  } catch (error) {
    console.error('生成报表失败:', error)
    ElMessage.error('生成报表失败')
  } finally {
    exportLoading.value = false
  }
}

// 机构导出报表
const handleExportReport = async () => {
  if (!reportForm.dateRange || reportForm.dateRange.length !== 2) {
    ElMessage.warning('请选择日期范围')
    return
  }
  exportLoading.value = true
  try {
    const res = await exportReport(reportForm.type, reportForm.dateRange[0], reportForm.dateRange[1])
    const data = res.data
    if (data && data.data && data.data.length > 0) {
      downloadCSV(data)
      ElMessage.success('导出成功')
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
  const typeNames = { order: '订单报表', revenue: '收入报表', service: '服务报表', user: '用户报表' }
  const headers = reportData.reportType === 'order'
    ? ['日期', '总订单数', '已完成订单']
    : reportData.reportType === 'revenue'
    ? ['日期', '收入(元)']
    : reportData.reportType === 'user'
    ? ['日期', '新增家长', '新增保姆', '新增机构']
    : ['日期', '总预约数', '已完成预约']

  let csv = headers.join(',') + '\n'
  reportData.data.forEach(row => {
    if (reportData.reportType === 'order') {
      csv += `${row.date},${row.totalOrders},${row.completedOrders}\n`
    } else if (reportData.reportType === 'revenue') {
      csv += `${row.date},${row.revenue}\n`
    } else if (reportData.reportType === 'user') {
      csv += `${row.date},${row.newParents || 0},${row.newNannies || 0},${row.newInstitutions || 0}\n`
    } else {
      csv += `${row.date},${row.totalAppointments},${row.completedAppointments}\n`
    }
  })

  const blob = new Blob(['\ufeff' + csv], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `${typeNames[reportData.reportType]}_${reportData.startDate}_${reportData.endDate}.csv`
  link.click()
}

// 初始化管理员用户增长图表
const initAdminUserChart = () => {
  if (!adminUserChartRef.value) return
  if (adminUserChart) adminUserChart.dispose()
  adminUserChart = echarts.init(adminUserChartRef.value)
  const trend = adminUserStats.value.parentTrend || []
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['新增家长', '新增保姆'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: trend.map(item => item.date) },
    yAxis: { type: 'value' },
    series: [
      { name: '新增家长', type: 'line', smooth: true, itemStyle: { color: '#409EFF' }, data: trend.map(item => item.count) },
      { name: '新增保姆', type: 'line', smooth: true, itemStyle: { color: '#67C23A' }, data: (adminUserStats.value.nannyTrend || []).map(item => item.count) }
    ]
  }
  adminUserChart.setOption(option)
}

// 初始化管理员订单状态图表
const initAdminOrderChart = () => {
  if (!adminOrderChartRef.value) return
  if (adminOrderChart) adminOrderChart.dispose()
  adminOrderChart = echarts.init(adminOrderChartRef.value)
  const dist = adminOrderStats.value.orderStatusDist || []
  const option = {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left' },
    series: [{
      type: 'pie', radius: ['40%', '70%'], avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: false, position: 'center' },
      emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
      labelLine: { show: false },
      data: dist.map(item => ({ value: item.count, name: item.name }))
    }]
  }
  adminOrderChart.setOption(option)
}

// 初始化机构订单趋势图表
const initInstOrderChart = () => {
  if (!instOrderChartRef.value) return
  if (instOrderChart) instOrderChart.dispose()
  instOrderChart = echarts.init(instOrderChartRef.value)
  const trend = instData.value.orderTrend || []
  const option = {
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: trend.map(item => item.date) },
    yAxis: { type: 'value' },
    series: [{
      name: '订单数', type: 'line', smooth: true,
      areaStyle: { opacity: 0.3 }, itemStyle: { color: '#409EFF' },
      data: trend.map(item => item.count)
    }]
  }
  instOrderChart.setOption(option)
}

// 初始化机构收入趋势图表
const initInstRevenueChart = () => {
  if (!instRevenueChartRef.value) return
  if (instRevenueChart) instRevenueChart.dispose()
  instRevenueChart = echarts.init(instRevenueChartRef.value)
  const trend = instData.value.revenueTrend || []
  const option = {
    tooltip: { trigger: 'axis', formatter: '{b}<br/>{a}: ¥{c}' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: trend.map(item => item.date) },
    yAxis: { type: 'value' },
    series: [{
      name: '收入', type: 'line', smooth: true,
      areaStyle: { opacity: 0.3 }, itemStyle: { color: '#67C23A' },
      data: trend.map(item => item.revenue)
    }]
  }
  instRevenueChart.setOption(option)
}

// 窗口大小变化时重绘图表
const handleResize = () => {
  adminUserChart?.resize()
  adminOrderChart?.resize()
  instOrderChart?.resize()
  instRevenueChart?.resize()
}

onMounted(() => {
  if (isAdmin.value) {
    loadAdminData()
  } else if (isInstitution.value) {
    loadInstData()
  }
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  adminUserChart?.dispose()
  adminOrderChart?.dispose()
  instOrderChart?.dispose()
  instRevenueChart?.dispose()
})
</script>

<style scoped lang="scss">
.home-container {
  padding: 20px;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    h2 {
      margin: 0;
      font-size: 24px;
      font-weight: 600;
      color: #1F2937;
    }

    .welcome-text {
      color: #6B7280;
      font-size: 14px;
    }
  }

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(6, 1fr);
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

        svg { width: 22px; height: 22px; }

        &.blue { background: #DBEAFE; color: #3B82F6; }
        &.emerald { background: #D1FAE5; color: #10B981; }
        &.amber { background: #FEF3C7; color: #F59E0B; }
        &.rose { background: #FCE7F3; color: #EC4899; }
        &.purple { background: #EDE9FE; color: #8B5CF6; }
        &.cyan { background: #CFFAFE; color: #06B6D4; }
      }

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
        &.warning { color: #EF4444; font-weight: 500; }
      }
    }
  }

  .chart-card {
    height: 100%;
    .chart-container { height: 300px; }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .nanny-info {
    display: flex;
    align-items: center;
    gap: 10px;

    .nanny-name {
      font-weight: 500;
    }
  }

  .report-form {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
  }
}

@media (max-width: 1400px) {
  .home-container .stats-grid { grid-template-columns: repeat(3, 1fr); }
}

@media (max-width: 768px) {
  .home-container .stats-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
