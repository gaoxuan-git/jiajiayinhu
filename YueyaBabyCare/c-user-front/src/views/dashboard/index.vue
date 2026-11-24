<template>
  <div class="dashboard-page">
    <div class="page-header">
      <h2>个人数据中心</h2>
      <p class="subtitle">查看您的宝宝成长数据、护理记录和消费统计</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card baby-card">
        <div class="stat-icon">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.828 14.828a4 4 0 01-5.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ babyStats.totalBabies || 0 }}</div>
          <div class="stat-label">宝宝数量</div>
        </div>
      </div>
      <div class="stat-card growth-card">
        <div class="stat-icon">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ babyStats.totalGrowthRecords || 0 }}</div>
          <div class="stat-label">成长记录</div>
        </div>
      </div>
      <div class="stat-card care-card">
        <div class="stat-icon">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ careStats.totalCareRecords || 0 }}</div>
          <div class="stat-label">护理记录</div>
        </div>
      </div>
      <div class="stat-card appt-card">
        <div class="stat-icon">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ careStats.totalAppointments || 0 }}</div>
          <div class="stat-label">预约次数</div>
        </div>
      </div>
      <div class="stat-card rating-card">
        <div class="stat-icon">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ careStats.avgRating || 0 }}</div>
          <div class="stat-label">平均评分</div>
        </div>
      </div>
      <div class="stat-card order-card">
        <div class="stat-icon">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ orderTotal }}</div>
          <div class="stat-label">订单数量</div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <!-- 宝宝信息 -->
      <div class="chart-card babies-card">
        <div class="card-header">
          <h3>我的宝宝</h3>
          <a href="javascript:;" class="more-link" @click="$router.push('/babies')">查看全部</a>
        </div>
        <div class="babies-list" v-if="babyStats.babies && babyStats.babies.length > 0">
          <div class="baby-item" v-for="baby in babyStats.babies" :key="baby.id" @click="$router.push(`/baby/${baby.id}`)">
            <t-avatar size="48px" :image="getImageUrl(baby.avatar)" :icon="!baby.avatar ? UserIcon : undefined" />
            <div class="baby-info">
              <div class="baby-name">{{ baby.nickname || baby.name }}</div>
              <div class="baby-meta">
                <span>{{ baby.gender === 1 ? '男宝' : baby.gender === 2 ? '女宝' : '未知' }}</span>
                <span>{{ baby.ageMonths }}个月</span>
              </div>
            </div>
            <div class="baby-stats">
              <div class="mini-stat">
                <span class="num">{{ baby.growthRecordCount || 0 }}</span>
                <span class="label">成长记录</span>
              </div>
              <div class="mini-stat">
                <span class="num">{{ baby.healthRecordCount || 0 }}</span>
                <span class="label">健康档案</span>
              </div>
            </div>
          </div>
        </div>
        <t-empty v-else description="暂无宝宝信息" />
      </div>

      <!-- 成长记录趋势 -->
      <div class="chart-card">
        <div class="card-header">
          <h3>成长记录趋势（近7天）</h3>
        </div>
        <div ref="growthChartRef" class="chart-container"></div>
      </div>
    </div>

    <!-- 护理数据 -->
    <div class="charts-section">
      <!-- 护理类型分布 -->
      <div class="chart-card">
        <div class="card-header">
          <h3>护理类型分布</h3>
        </div>
        <div ref="careTypeChartRef" class="chart-container"></div>
      </div>

      <!-- 预约状态分布 -->
      <div class="chart-card">
        <div class="card-header">
          <h3>预约状态分布</h3>
        </div>
        <div ref="appointmentChartRef" class="chart-container"></div>
      </div>
    </div>

    <!-- 护理计划统计 -->
    <div class="care-stats-section">
      <div class="care-stat-card">
        <div class="care-stat-icon active">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
          </svg>
        </div>
        <div class="care-stat-info">
          <div class="care-stat-value">{{ careStats.activeCarePlans || 0 }}</div>
          <div class="care-stat-label">进行中的护理计划</div>
        </div>
      </div>
      <div class="care-stat-card">
        <div class="care-stat-icon completed">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
          </svg>
        </div>
        <div class="care-stat-info">
          <div class="care-stat-value">{{ careStats.completedCarePlans || 0 }}</div>
          <div class="care-stat-label">已完成的护理计划</div>
        </div>
      </div>
      <div class="care-stat-card">
        <div class="care-stat-icon pending">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
          </svg>
        </div>
        <div class="care-stat-info">
          <div class="care-stat-value">{{ careStats.pendingAppointments || 0 }}</div>
          <div class="care-stat-label">待处理预约</div>
        </div>
      </div>
      <div class="care-stat-card">
        <div class="care-stat-icon done">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
          </svg>
        </div>
        <div class="care-stat-info">
          <div class="care-stat-value">{{ careStats.completedAppointments || 0 }}</div>
          <div class="care-stat-label">已完成预约</div>
        </div>
      </div>
    </div>

    <!-- 订单历史 -->
    <div class="order-section">
      <div class="section-header">
        <h3>消费记录</h3>
        <a href="javascript:;" class="more-link" @click="$router.push('/orders')">查看全部</a>
      </div>
      <div class="order-list" v-if="orderList.length > 0">
        <div class="order-item" v-for="order in orderList" :key="order.id" @click="$router.push(`/order/${order.id}`)">
          <div class="order-header">
            <span class="order-no">订单号：{{ order.orderNo }}</span>
            <t-tag :theme="getOrderStatusTheme(order.status)" size="small">{{ order.statusName }}</t-tag>
          </div>
          <div class="order-body">
            <div class="order-items" v-if="order.items && order.items.length > 0">
              <div class="order-product" v-for="(item, idx) in order.items.slice(0, 2)" :key="idx">
                <img :src="getImageUrl(item.productImage)" :alt="item.productName" class="product-img" />
                <div class="product-info">
                  <div class="product-name">{{ item.productName }}</div>
                  <div class="product-price">¥{{ item.price }} × {{ item.quantity }}</div>
                </div>
              </div>
              <div class="more-items" v-if="order.items.length > 2">
                +{{ order.items.length - 2 }}件商品
              </div>
            </div>
            <div class="order-amount">
              <span class="label">实付</span>
              <span class="amount">¥{{ order.payAmount }}</span>
            </div>
          </div>
          <div class="order-footer">
            <span class="order-time">{{ order.createTime }}</span>
            <span class="institution">{{ order.institutionName }}</span>
          </div>
        </div>
      </div>
      <t-empty v-else description="暂无消费记录" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { UserIcon } from 'tdesign-icons-vue-next'
import * as echarts from 'echarts'
import { getBabyStats, getCareStats, getOrderHistory } from '@/api/dashboard'
import { getImageUrl } from '@/utils/image'

// 数据
const babyStats = ref({})
const careStats = ref({})
const orderList = ref([])
const orderTotal = ref(0)

// 图表引用
const growthChartRef = ref(null)
const careTypeChartRef = ref(null)
const appointmentChartRef = ref(null)

let growthChart = null
let careTypeChart = null
let appointmentChart = null

// 加载婴幼儿数据统计
const loadBabyStats = async () => {
  try {
    const res = await getBabyStats()
    babyStats.value = res.data || {}
    nextTick(() => {
      initGrowthChart()
    })
  } catch (e) {
    console.error('获取婴幼儿数据失败:', e)
  }
}

// 加载护理数据统计
const loadCareStats = async () => {
  try {
    const res = await getCareStats()
    careStats.value = res.data || {}
    nextTick(() => {
      initCareTypeChart()
      initAppointmentChart()
    })
  } catch (e) {
    console.error('获取护理数据失败:', e)
  }
}

// 加载订单历史
const loadOrderHistory = async () => {
  try {
    const res = await getOrderHistory({ pageNum: 1, pageSize: 5 })
    orderList.value = res.data?.records || []
    orderTotal.value = res.data?.total || 0
  } catch (e) {
    console.error('获取订单历史失败:', e)
  }
}

// 初始化成长记录趋势图表
const initGrowthChart = () => {
  if (!growthChartRef.value) return
  if (growthChart) growthChart.dispose()
  growthChart = echarts.init(growthChartRef.value)

  const trend = babyStats.value.growthTrend || []
  const option = {
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: trend.map(item => item.date) },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{
      name: '记录数',
      type: 'line',
      smooth: true,
      areaStyle: { opacity: 0.3 },
      itemStyle: { color: '#3B82F6' },
      data: trend.map(item => item.count)
    }]
  }
  growthChart.setOption(option)
}

// 初始化护理类型分布图表
const initCareTypeChart = () => {
  if (!careTypeChartRef.value) return
  if (careTypeChart) careTypeChart.dispose()
  careTypeChart = echarts.init(careTypeChartRef.value)

  const dist = careStats.value.careTypeDist || []
  const option = {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: false, position: 'center' },
      emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
      labelLine: { show: false },
      data: dist.map(item => ({ value: item.count, name: item.name }))
    }]
  }
  careTypeChart.setOption(option)
}

// 初始化预约状态分布图表
const initAppointmentChart = () => {
  if (!appointmentChartRef.value) return
  if (appointmentChart) appointmentChart.dispose()
  appointmentChart = echarts.init(appointmentChartRef.value)

  const dist = careStats.value.appointmentStatusDist || []
  const colors = ['#F59E0B', '#3B82F6', '#10B981', '#EF4444']
  const option = {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left' },
    series: [{
      type: 'pie',
      radius: '70%',
      data: dist.map((item, idx) => ({
        value: item.count,
        name: item.name,
        itemStyle: { color: colors[idx] }
      })),
      emphasis: {
        itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' }
      }
    }]
  }
  appointmentChart.setOption(option)
}

// 获取订单状态主题
const getOrderStatusTheme = (status) => {
  const themes = { 0: 'warning', 1: 'primary', 2: 'primary', 3: 'success', 4: 'default', 5: 'danger' }
  return themes[status] || 'default'
}

// 窗口大小变化时重绘图表
const handleResize = () => {
  growthChart?.resize()
  careTypeChart?.resize()
  appointmentChart?.resize()
}

onMounted(() => {
  loadBabyStats()
  loadCareStats()
  loadOrderHistory()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  growthChart?.dispose()
  careTypeChart?.dispose()
  appointmentChart?.dispose()
})
</script>

<style scoped>
.dashboard-page {
  padding: 24px;
  background: #F9FAFB;
  min-height: calc(100vh - 120px);
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0 0 8px;
  font-size: 24px;
  font-weight: 600;
  color: #1F2937;
}

.page-header .subtitle {
  margin: 0;
  font-size: 14px;
  color: #6B7280;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon svg {
  width: 24px;
  height: 24px;
}

.baby-card .stat-icon { background: #DBEAFE; color: #3B82F6; }
.growth-card .stat-icon { background: #D1FAE5; color: #10B981; }
.care-card .stat-icon { background: #E0E7FF; color: #6366F1; }
.appt-card .stat-icon { background: #CFFAFE; color: #06B6D4; }
.rating-card .stat-icon { background: #FEF3C7; color: #F59E0B; }
.order-card .stat-icon { background: #FCE7F3; color: #EC4899; }

.stat-info .stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1F2937;
}

.stat-info .stat-label {
  font-size: 13px;
  color: #6B7280;
}

/* 图表区域 */
.charts-section {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.chart-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1F2937;
}

.more-link {
  font-size: 14px;
  color: #3B82F6;
  text-decoration: none;
}

.more-link:hover {
  color: #2563EB;
}

.chart-container {
  height: 260px;
}

/* 宝宝列表 */
.babies-card {
  grid-column: span 1;
}

.babies-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.baby-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #F9FAFB;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
}

.baby-item:hover {
  background: #F3F4F6;
}

.baby-info {
  flex: 1;
}

.baby-name {
  font-size: 15px;
  font-weight: 500;
  color: #1F2937;
}

.baby-meta {
  display: flex;
  gap: 8px;
  font-size: 12px;
  color: #6B7280;
  margin-top: 4px;
}

.baby-stats {
  display: flex;
  gap: 16px;
}

.mini-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.mini-stat .num {
  font-size: 16px;
  font-weight: 600;
  color: #3B82F6;
}

.mini-stat .label {
  font-size: 11px;
  color: #9CA3AF;
}

/* 护理计划统计 */
.care-stats-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.care-stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.care-stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.care-stat-icon svg {
  width: 24px;
  height: 24px;
}

.care-stat-icon.active { background: #DBEAFE; color: #3B82F6; }
.care-stat-icon.completed { background: #D1FAE5; color: #10B981; }
.care-stat-icon.pending { background: #FEF3C7; color: #F59E0B; }
.care-stat-icon.done { background: #E0E7FF; color: #6366F1; }

.care-stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1F2937;
}

.care-stat-label {
  font-size: 13px;
  color: #6B7280;
}

/* 订单历史 */
.order-section {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1F2937;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-item {
  border: 1px solid #E5E7EB;
  border-radius: 10px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.order-item:hover {
  border-color: #D1D5DB;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.order-no {
  font-size: 13px;
  color: #6B7280;
}

.order-body {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.order-items {
  display: flex;
  gap: 12px;
  align-items: center;
}

.order-product {
  display: flex;
  gap: 10px;
  align-items: center;
}

.product-img {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  object-fit: cover;
  background: #F3F4F6;
}

.product-info .product-name {
  font-size: 14px;
  color: #1F2937;
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-info .product-price {
  font-size: 12px;
  color: #9CA3AF;
  margin-top: 4px;
}

.more-items {
  font-size: 12px;
  color: #6B7280;
  padding: 8px 12px;
  background: #F3F4F6;
  border-radius: 6px;
}

.order-amount {
  text-align: right;
}

.order-amount .label {
  font-size: 12px;
  color: #9CA3AF;
}

.order-amount .amount {
  font-size: 18px;
  font-weight: 600;
  color: #EF4444;
  margin-left: 4px;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #F3F4F6;
  font-size: 12px;
  color: #9CA3AF;
}

/* 响应式 */
@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 900px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .charts-section {
    grid-template-columns: 1fr;
  }
  .care-stats-section {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  .care-stats-section {
    grid-template-columns: 1fr;
  }
}
</style>
