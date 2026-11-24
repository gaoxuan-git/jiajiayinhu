<template>
  <div class="homepage">
    <!-- 轮播图 -->
    <div class="carousel-section">
      <t-swiper 
        :autoplay="true" 
        :duration="500" 
        :interval="4000"
        :navigation="{ type: 'dots-bar' }"
        style="height: 450px;"
      >
        <t-swiper-item v-for="(banner, index) in banners" :key="index">
          <div class="carousel-item" :style="{ backgroundImage: `url(${banner.image})` }">
            <div class="carousel-content">
              <h2 class="carousel-title">{{ banner.title }}</h2>
              <p class="carousel-desc">{{ banner.desc }}</p>
              <t-button theme="primary" size="large" @click="$router.push(banner.link)">
                {{ banner.btnText }}
              </t-button>
            </div>
          </div>
        </t-swiper-item>
      </t-swiper>
    </div>

    <!-- 数据概览 -->
    <div class="stats-section">
      <div class="stat-card" v-for="stat in stats" :key="stat.id" @click="$router.push(stat.link)">
        <div class="stat-icon" :style="{ background: stat.color }">
          <SvgIcon :name="stat.icon" :size="28" color="white" />
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stat.value }}</div>
          <div class="stat-label">{{ stat.label }}</div>
        </div>
      </div>
    </div>

    <!-- 我的宝宝 -->
    <div class="content-section">
      <div class="section-header">
        <h2 class="section-title">我的宝宝</h2>
        <t-button theme="primary" variant="text" @click="$router.push('/babies')">
          查看全部
          <template #suffix><t-icon name="chevron-right" size="16" /></template>
        </t-button>
      </div>
      <div class="babies-grid">
        <div v-for="baby in babies" :key="baby.id" class="baby-card" @click="goToBaby(baby.id)">
          <div class="baby-card-inner">
            <img :src="baby.avatar" alt="" class="baby-avatar" />
            <div class="baby-info">
              <h3 class="baby-name">{{ baby.name }}</h3>
              <p class="baby-age">{{ baby.age }}</p>
              <div class="baby-stats">
                <div class="stat-item">
                  <span class="stat-label">身高</span>
                  <span class="stat-value">{{ baby.height }}cm</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">体重</span>
                  <span class="stat-value">{{ baby.weight }}kg</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="baby-card add-baby-card" @click="$router.push('/babies')">
          <div class="add-baby-content">
            <t-icon name="add" size="48" />
            <p>添加宝宝</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 护理计划 & 预约管理 -->
    <div class="two-columns">
      <!-- 护理计划 -->
      <div class="content-section">
        <div class="section-header">
          <h2 class="section-title">护理计划</h2>
          <t-button theme="primary" variant="text" @click="$router.push('/care-plans')">
            查看全部
            <template #suffix><t-icon name="chevron-right" size="16" /></template>
          </t-button>
        </div>
        <div class="plan-list">
          <div v-for="plan in carePlans" :key="plan.id" class="plan-item">
            <div class="plan-icon" :style="{ background: plan.color }">
              <SvgIcon :name="plan.icon" :size="20" color="white" />
            </div>
            <div class="plan-content">
              <div class="plan-header">
                <h4>{{ plan.title }}</h4>
                <t-tag :theme="plan.status === '进行中' ? 'success' : 'warning'" size="small">{{ plan.status }}</t-tag>
              </div>
              <p class="plan-time">{{ plan.time }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 预约管理 -->
      <div class="content-section">
        <div class="section-header">
          <h2 class="section-title">近期预约</h2>
          <t-button theme="primary" variant="text" @click="$router.push('/appointments')">
            查看全部
            <template #suffix><t-icon name="chevron-right" size="16" /></template>
          </t-button>
        </div>
        <div class="appointment-list">
          <div v-for="apt in appointments" :key="apt.id" class="appointment-item">
            <div class="apt-date">
              <span class="day">{{ apt.day }}</span>
              <span class="month">{{ apt.month }}</span>
            </div>
            <div class="apt-content">
              <h4>{{ apt.service }}</h4>
              <p>{{ apt.nanny }} · {{ apt.time }}</p>
            </div>
            <t-tag :theme="apt.status === '已确认' ? 'success' : 'warning'" size="small">{{ apt.status }}</t-tag>
          </div>
        </div>
      </div>
    </div>

    <!-- 健康档案 & 成长里程碑 -->
    <div class="two-columns">
      <!-- 健康档案 -->
      <div class="content-section">
        <div class="section-header">
          <h2 class="section-title">健康档案</h2>
          <t-button theme="primary" variant="text" @click="$router.push('/health-records/1')">
            查看详情
            <template #suffix><t-icon name="chevron-right" size="16" /></template>
          </t-button>
        </div>
        <div class="health-stats">
          <div v-for="item in healthStats" :key="item.id" class="health-stat-item">
            <div class="health-icon" :style="{ background: item.color }">
              <SvgIcon :name="item.icon" :size="20" color="white" />
            </div>
            <div class="health-info">
              <span class="health-label">{{ item.label }}</span>
              <div class="health-value-wrap">
                <span class="health-value">{{ item.value }}</span>
                <span class="health-unit">{{ item.unit }}</span>
                <t-tag v-if="item.status" :theme="item.status === '正常' ? 'success' : 'warning'" size="small">{{ item.status }}</t-tag>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 成长里程碑 -->
      <div class="content-section">
        <div class="section-header">
          <h2 class="section-title">成长里程碑</h2>
          <t-button theme="primary" variant="text" @click="$router.push('/growth-records/1')">
            查看更多
            <template #suffix><t-icon name="chevron-right" size="16" /></template>
          </t-button>
        </div>
        <div class="milestone-list">
          <div v-for="milestone in milestones" :key="milestone.id" class="milestone-item">
            <div class="milestone-dot" :style="{ background: milestone.color }"></div>
            <div class="milestone-content">
              <div class="milestone-header">
                <h4>{{ milestone.title }}</h4>
                <span class="milestone-date">{{ milestone.date }}</span>
              </div>
              <p class="milestone-desc">{{ milestone.desc }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 商品推荐 -->
    <div class="content-section">
      <div class="section-header">
        <h2 class="section-title">热门商品</h2>
        <t-button theme="primary" variant="text" @click="$router.push('/mall')">
          查看全部
          <template #suffix><t-icon name="chevron-right" size="16" /></template>
        </t-button>
      </div>
      <div class="products-grid">
        <div v-for="product in hotProducts" :key="product.id" class="product-card" @click="$router.push(`/product/${product.id}`)">
          <div class="product-img">
            <img :src="product.img" :alt="product.name" />
            <t-tag v-if="product.hot" theme="danger" size="small" class="hot-tag">热卖</t-tag>
          </div>
          <div class="product-info">
            <h4>{{ product.name }}</h4>
            <p class="product-desc">{{ product.desc }}</p>
            <div class="product-price">
              <span class="price">￥{{ product.price }}</span>
              <span class="points">+{{ product.points }}积分</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 服务特色 -->
    <div class="content-section">
      <div class="section-header">
        <h2 class="section-title">服务特色</h2>
      </div>
      <div class="features-grid">
        <div v-for="feature in features" :key="feature.id" class="feature-card">
          <div class="feature-icon" :style="{ background: feature.color }">
            <SvgIcon :name="feature.icon" :size="32" color="white" />
          </div>
          <h3 class="feature-title">{{ feature.title }}</h3>
          <p class="feature-desc">{{ feature.desc }}</p>
        </div>
      </div>
    </div>

    <!-- 快速操作 -->
    <div class="content-section">
      <div class="section-header">
        <h2 class="section-title">快速操作</h2>
      </div>
      <div class="quick-actions">
        <div v-for="action in quickActions" :key="action.id" class="action-card" @click="$router.push(action.path)">
          <div class="action-icon" :style="{ background: action.color }">
            <SvgIcon :name="action.icon" :size="28" color="#3B82F6" />
          </div>
          <div class="action-label">{{ action.label }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import SvgIcon from '@/components/SvgIcon.vue'
import { getBabyList } from '@/api/baby'
import { getMyAppointments } from '@/api/appointment'
import { getCarePlanList } from '@/api/carePlan'
import { getBabyStats, getCareStats } from '@/api/dashboard'
import { getImageUrl } from '@/utils/image'

const router = useRouter()

// 加载状态
const loading = ref(false)

// 轮播图数据
const banners = ref([
  {
    title: '专业的婴幼儿护理服务',
    desc: '为您的宝宝提供科学、专业、贴心的护理服务',
    btnText: '立即预约',
    link: '/appointments',
    image: 'https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?w=1200&h=400&fit=crop'
  },
  {
    title: '健康成长档案管理',
    desc: '记录宝宝每一天的成长轨迹，科学关注发育情况',
    btnText: '查看档案',
    link: '/babies',
    image: 'https://images.unsplash.com/photo-1519689680058-324335c77eba?w=1200&h=400&fit=crop'
  },
  {
    title: '优质母婴产品',
    desc: '精选品质母婴用品，守护宝宝健康成长',
    btnText: '马上选购',
    link: '/mall',
    image: 'https://images.unsplash.com/photo-1522771930-78848d9293e8?w=1200&h=400&fit=crop'
  }
])

// 宝宝数据统计
const babyStats = ref({})
const careStatsData = ref({})

// 数据统计卡片 - 动态计算
const stats = computed(() => [
  { id: 1, label: '我的宝宝', value: babyStats.value.totalBabies || 0, icon: 'baby', color: 'linear-gradient(135deg, #818CF8 0%, #A78BFA 100%)', link: '/babies' },
  { id: 2, label: '护理记录', value: careStatsData.value.totalCareRecords || 0, icon: 'document', color: 'linear-gradient(135deg, #F9A8D4 0%, #FDA4AF 100%)', link: '/care-plans' },
  { id: 3, label: '健康档案', value: babyStats.value.totalHealthRecords || 0, icon: 'heart', color: 'linear-gradient(135deg, #7DD3FC 0%, #67E8F9 100%)', link: '/babies' },
  { id: 4, label: '待处理预约', value: careStatsData.value.pendingAppointments || 0, icon: 'clock', color: 'linear-gradient(135deg, #86EFAC 0%, #6EE7B7 100%)', link: '/appointments' }
])

// 宝宝列表 - 来自API
const babies = ref([])

const features = ref([
  { 
    id: 1, 
    title: '专业护理团队', 
    desc: '经验丰富的护理专家，为宝宝提供科学护理方案', 
    icon: 'users', 
    color: 'linear-gradient(135deg, #818CF8 0%, #A78BFA 100%)'
  },
  { 
    id: 2, 
    title: '全程跟踪记录', 
    desc: '详细记录宝宝成长每一刻，科学分析发育状况', 
    icon: 'chart', 
    color: 'linear-gradient(135deg, #7DD3FC 0%, #67E8F9 100%)'
  },
  { 
    id: 3, 
    title: '智能提醒服务', 
    desc: '精准提醒护理事项，不错过任何重要时刻', 
    icon: 'notification', 
    color: 'linear-gradient(135deg, #F9A8D4 0%, #FDA4AF 100%)'
  },
  { 
    id: 4, 
    title: '优质母婴产品', 
    desc: '精选品质母婴用品，一站式购物体验', 
    icon: 'shop', 
    color: 'linear-gradient(135deg, #86EFAC 0%, #6EE7B7 100%)'
  }
])


const quickActions = ref([
  { id: 1, label: '选择机构', icon: 'shop', color: '#DBEAFE', path: '/institutions' },
  { id: 2, label: '预约护理服务', icon: 'calendar', color: '#FEF3C7', path: '/appointments' },
  { id: 3, label: '记录健康数据', icon: 'heart', color: '#FEE2E2', path: '/babies' },
  { id: 4, label: '浏览商城', icon: 'cart', color: '#D1FAE5', path: '/mall' },
  { id: 5, label: '消息中心', icon: 'notification', color: '#FCE7F3', path: '/messages' }
])

// 护理计划 - 来自API
const carePlans = ref([])

// 预约管理 - 来自API
const appointments = ref([])

// 热门商品
const hotProducts = ref([
  { id: 1, name: '进口婴儿奶粉', desc: '适合0-6个月宝宝', price: '298.00', points: 298, img: 'https://images.unsplash.com/photo-1515694346937-94d85e41e6f0?w=300&h=300&fit=crop', hot: true },
  { id: 2, name: '超薄纸尿裤', desc: 'XL码 透气干爽', price: '159.00', points: 159, img: 'https://images.unsplash.com/photo-1522771930-78848d9293e8?w=300&h=300&fit=crop', hot: true },
  { id: 3, name: '纯棉婴儿衣服', desc: '柔软亲肤', price: '89.00', points: 89, img: 'https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=300&h=300&fit=crop', hot: false },
  { id: 4, name: '益智早教玩具', desc: '开发宝宝智力', price: '128.00', points: 128, img: 'https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?w=300&h=300&fit=crop', hot: false }
])

// 健康档案数据 - 来自第一个宝宝
const healthStats = computed(() => {
  const firstBaby = babies.value[0]
  if (!firstBaby) return []
  return [
    { id: 1, label: '身高', value: firstBaby.height || '--', unit: 'cm', icon: 'trending-up', color: 'linear-gradient(135deg, #7DD3FC 0%, #67E8F9 100%)', status: '正常' },
    { id: 2, label: '体重', value: firstBaby.weight || '--', unit: 'kg', icon: 'chart', color: 'linear-gradient(135deg, #818CF8 0%, #A78BFA 100%)', status: '正常' },
    { id: 3, label: '月龄', value: firstBaby.ageMonths || '--', unit: '个月', icon: 'heart', color: 'linear-gradient(135deg, #86EFAC 0%, #6EE7B7 100%)', status: '' },
    { id: 4, label: '头围', value: firstBaby.headCircumference || '--', unit: 'cm', icon: 'user', color: 'linear-gradient(135deg, #F9A8D4 0%, #FDA4AF 100%)', status: '正常' }
  ]
})

// 成长里程碑 - 静态示例数据
const milestones = ref([
  { id: 1, title: '第一次翻身', date: '2024-12-15', desc: '宝宝今天第一次独立翻身成功！', color: '#3B82F6' },
  { id: 2, title: '第一次块食', date: '2024-12-10', desc: '宝宝开始尝试辅食，吃了第一口米糊', color: '#10B981' },
  { id: 3, title: '长出第一颗牙', date: '2024-12-01', desc: '下门牙开始萨出，要注意口腔护理', color: '#F59E0B' },
  { id: 4, title: '会笑了', date: '2024-11-20', desc: '宝宝第一次对人笑，笑容很甘甜', color: '#EC4899' }
])

const goToBaby = (id) => {
  router.push(`/baby/${id}`)
}

// 加载宝宝列表
const loadBabies = async () => {
  try {
    const res = await getBabyList()
    const babyList = res.data || []
    // 转换数据格式
    babies.value = babyList.map(baby => ({
      id: baby.id,
      name: baby.nickname || baby.name,
      age: formatAge(baby.birthDate),
      ageMonths: calcAgeMonths(baby.birthDate),
      height: baby.height,
      weight: baby.weight,
      headCircumference: baby.headCircumference,
      avatar: getImageUrl(baby.avatar) || 'https://images.unsplash.com/photo-1519689680058-324335c77eba?w=200&h=200&fit=crop'
    }))
  } catch (e) {
    console.error('获取宝宝列表失败:', e)
  }
}

// 加载宝宝统计数据
const loadBabyStats = async () => {
  try {
    const res = await getBabyStats()
    babyStats.value = res.data || {}
  } catch (e) {
    console.error('获取宝宝统计失败:', e)
  }
}

// 加载护理统计数据
const loadCareStats = async () => {
  try {
    const res = await getCareStats()
    careStatsData.value = res.data || {}
  } catch (e) {
    console.error('获取护理统计失败:', e)
  }
}

// 加载护理计划
const loadCarePlans = async () => {
  try {
    const res = await getCarePlanList({ status: null })
    const planList = res.data || []
    const icons = ['baby', 'star', 'calendar', 'clock']
    const colors = [
      'linear-gradient(135deg, #818CF8 0%, #A78BFA 100%)',
      'linear-gradient(135deg, #7DD3FC 0%, #67E8F9 100%)',
      'linear-gradient(135deg, #F9A8D4 0%, #FDA4AF 100%)',
      'linear-gradient(135deg, #86EFAC 0%, #6EE7B7 100%)'
    ]
    carePlans.value = planList.slice(0, 4).map((plan, idx) => ({
      id: plan.id,
      title: plan.planName || plan.title || '护理计划',
      status: plan.status === 1 ? '进行中' : plan.status === 2 ? '已完成' : '待执行',
      time: formatPlanTime(plan),
      icon: icons[idx % 4],
      color: colors[idx % 4]
    }))
  } catch (e) {
    console.error('获取护理计划失败:', e)
  }
}

// 加载预约列表
const loadAppointments = async () => {
  try {
    const res = await getMyAppointments()
    const aptList = res.data || []
    appointments.value = aptList.slice(0, 3).map(apt => {
      const date = parseDate(apt.appointmentDate)
      return {
        id: apt.id,
        day: date ? date.getDate() : '--',
        month: date ? `${date.getMonth() + 1}月` : '',
        service: getServiceTypeText(apt.serviceType),
        nanny: apt.nannyName || '待分配',
        time: formatTimeRange(apt.startTime, apt.endTime),
        status: getAppointmentStatusText(apt.status)
      }
    })
  } catch (e) {
    console.error('获取预约列表失败:', e)
  }
}

// 格式化年龄
const formatAge = (birthDate) => {
  if (!birthDate) return '未知'
  const birth = parseDate(birthDate)
  if (!birth) return '未知'
  const now = new Date()
  let months = (now.getFullYear() - birth.getFullYear()) * 12 + (now.getMonth() - birth.getMonth())
  if (months < 12) {
    return `${months}个月`
  } else {
    const years = Math.floor(months / 12)
    const remainMonths = months % 12
    return remainMonths > 0 ? `${years}岁${remainMonths}个月` : `${years}岁`
  }
}

// 计算月龄
const calcAgeMonths = (birthDate) => {
  if (!birthDate) return 0
  const birth = parseDate(birthDate)
  if (!birth) return 0
  const now = new Date()
  return (now.getFullYear() - birth.getFullYear()) * 12 + (now.getMonth() - birth.getMonth())
}

// 解析日期
const parseDate = (dateVal) => {
  if (!dateVal) return null
  if (Array.isArray(dateVal)) {
    return new Date(dateVal[0], dateVal[1] - 1, dateVal[2])
  }
  if (typeof dateVal === 'string') {
    return new Date(dateVal)
  }
  return null
}

// 格式化护理计划时间
const formatPlanTime = (plan) => {
  if (plan.startDate && plan.endDate) {
    const start = parseDate(plan.startDate)
    const end = parseDate(plan.endDate)
    if (start && end) {
      return `${start.getMonth() + 1}/${start.getDate()} - ${end.getMonth() + 1}/${end.getDate()}`
    }
  }
  return plan.planContent ? plan.planContent.substring(0, 20) : '暂无时间'
}

// 格式化时间范围
const formatTimeRange = (startTime, endTime) => {
  const formatTime = (t) => {
    if (!t) return ''
    if (Array.isArray(t)) {
      return `${String(t[0]).padStart(2, '0')}:${String(t[1] || 0).padStart(2, '0')}`
    }
    if (typeof t === 'string') {
      return t.substring(0, 5)
    }
    return ''
  }
  return `${formatTime(startTime)}-${formatTime(endTime)}`
}

// 获取服务类型文本
const getServiceTypeText = (type) => {
  const typeMap = { 1: '日常护理', 2: '月嫂服务', 3: '育儿嫂', 4: '早教服务', 5: '其他服务' }
  return typeMap[type] || '护理服务'
}

// 获取预约状态文本
const getAppointmentStatusText = (status) => {
  const statusMap = { 0: '待确认', 1: '已确认', 2: '进行中', 3: '已完成', 4: '已取消' }
  return statusMap[status] || '未知'
}

// 加载所有数据
const loadAllData = async () => {
  loading.value = true
  const token = localStorage.getItem('c_token')
  if (token) {
    await Promise.all([
      loadBabies(),
      loadBabyStats(),
      loadCareStats(),
      loadCarePlans(),
      loadAppointments()
    ])
  }
  loading.value = false
}

onMounted(() => {
  loadAllData()
})
</script>

<style scoped>
.homepage {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

/* 轮播图 */
.carousel-section {
  width: 100%;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.carousel-section :deep(.t-swiper) {
  height: 450px !important;
}

.carousel-section :deep(.t-swiper__container) {
  height: 450px !important;
}

.carousel-section :deep(.t-swiper-item) {
  height: 450px !important;
}

.carousel-item {
  width: 100%;
  height: 450px;
  min-height: 450px;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.carousel-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.4) 0%, rgba(0, 0, 0, 0.2) 100%);
}

.carousel-content {
  position: relative;
  z-index: 1;
  text-align: center;
  color: white;
  padding: 40px;
}

.carousel-title {
  font-size: 40px;
  font-weight: 700;
  margin: 0 0 16px 0;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.carousel-desc {
  font-size: 18px;
  margin: 0 0 32px 0;
  opacity: 0.95;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
}

/* 数据概览 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #1F2937;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #6B7280;
}

/* 内容分区 */
.content-section {
  background: white;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #1F2937;
}

/* 宝宝卡片 */
.babies-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.baby-card {
  background: #F9FAFB;
  border-radius: 12px;
  padding: 24px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.baby-card:hover {
  border-color: #3B82F6;
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.15);
  transform: translateY(-4px);
}

.baby-card-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.baby-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 16px;
  border: 4px solid white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.baby-info {
  width: 100%;
}

.baby-name {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 600;
  color: #1F2937;
}

.baby-age {
  margin: 0 0 16px 0;
  color: #3B82F6;
  font-size: 14px;
  font-weight: 500;
}

.baby-stats {
  display: flex;
  justify-content: center;
  gap: 24px;
  padding-top: 16px;
  border-top: 1px solid #E5E7EB;
}

.baby-stats .stat-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.baby-stats .stat-label {
  font-size: 12px;
  color: #9CA3AF;
}

.baby-stats .stat-value {
  font-size: 16px;
  font-weight: 600;
  color: #1F2937;
}

.add-baby-card {
  background: linear-gradient(135deg, #EFF6FF 0%, #DBEAFE 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px dashed #3B82F6;
}

.add-baby-card:hover {
  background: linear-gradient(135deg, #DBEAFE 0%, #BFDBFE 100%);
  border-color: #2563EB;
}

.add-baby-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: #3B82F6;
}

.add-baby-content p {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

/* 服务特色 */
.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.feature-card {
  text-align: center;
  padding: 32px 24px;
  background: #F9FAFB;
  border-radius: 12px;
  transition: all 0.3s;
}

.feature-card:hover {
  background: white;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  transform: translateY(-4px);
}

.feature-icon {
  width: 80px;
  height: 80px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  color: white;
}

.feature-title {
  font-size: 18px;
  font-weight: 600;
  color: #1F2937;
  margin: 0 0 12px 0;
}

.feature-desc {
  font-size: 14px;
  color: #6B7280;
  line-height: 1.6;
  margin: 0;
}

/* 快速操作 */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.action-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px 20px;
  background: #F9FAFB;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
  min-height: 160px;
}

.action-card:hover {
  background: white;
  border-color: #3B82F6;
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.12);
  transform: translateY(-4px);
}

.action-icon {
  width: 72px;
  height: 72px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-bottom: 16px;
}

.action-label {
  font-size: 16px;
  font-weight: 600;
  color: #1F2937;
}

/* 两栏布局 */
.two-columns {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

/* 护理计划列表 */
.plan-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.plan-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #F9FAFB;
  border-radius: 12px;
  transition: all 0.3s;
}

.plan-item:hover {
  background: #F3F4F6;
  transform: translateX(4px);
}

.plan-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.plan-content {
  flex: 1;
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.plan-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1F2937;
}

.plan-time {
  margin: 0;
  font-size: 13px;
  color: #6B7280;
}

/* 预约列表 */
.appointment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.appointment-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #F9FAFB;
  border-radius: 12px;
  transition: all 0.3s;
}

.appointment-item:hover {
  background: #F3F4F6;
  transform: translateX(4px);
}

.apt-date {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #3B82F6 0%, #2563EB 100%);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.apt-date .day {
  font-size: 20px;
  font-weight: 700;
  line-height: 1;
}

.apt-date .month {
  font-size: 11px;
  opacity: 0.9;
}

.apt-content {
  flex: 1;
}

.apt-content h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: #1F2937;
}

.apt-content p {
  margin: 0;
  font-size: 13px;
  color: #6B7280;
}

/* 商品网格 */
.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.product-card {
  background: #F9FAFB;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.product-card:hover {
  border-color: #3B82F6;
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.15);
  transform: translateY(-4px);
}

.product-img {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.product-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.product-card:hover .product-img img {
  transform: scale(1.05);
}

.hot-tag {
  position: absolute;
  top: 12px;
  right: 12px;
}

.product-info {
  padding: 16px;
}

.product-info h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #1F2937;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-desc {
  margin: 0 0 12px 0;
  font-size: 13px;
  color: #6B7280;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-price {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-price .price {
  font-size: 18px;
  font-weight: 700;
  color: #EF4444;
}

.product-price .points {
  font-size: 12px;
  color: #F59E0B;
  background: #FEF3C7;
  padding: 2px 8px;
  border-radius: 4px;
}

/* 健康档案 */
.health-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.health-stat-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #F9FAFB;
  border-radius: 12px;
  transition: all 0.3s;
}

.health-stat-item:hover {
  background: #F3F4F6;
}

.health-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.health-info {
  flex: 1;
}

.health-label {
  font-size: 13px;
  color: #6B7280;
  display: block;
  margin-bottom: 4px;
}

.health-value-wrap {
  display: flex;
  align-items: center;
  gap: 4px;
}

.health-value {
  font-size: 24px;
  font-weight: 700;
  color: #1F2937;
}

.health-unit {
  font-size: 14px;
  color: #6B7280;
  margin-right: 8px;
}

/* 成长里程碑 */
.milestone-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.milestone-item {
  display: flex;
  gap: 16px;
  position: relative;
}

.milestone-item:not(:last-child)::before {
  content: '';
  position: absolute;
  left: 7px;
  top: 20px;
  bottom: -16px;
  width: 2px;
  background: #E5E7EB;
}

.milestone-dot {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 4px;
}

.milestone-content {
  flex: 1;
  padding-bottom: 16px;
}

.milestone-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.milestone-header h4 {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  color: #1F2937;
}

.milestone-date {
  font-size: 12px;
  color: #9CA3AF;
}

.milestone-desc {
  margin: 0;
  font-size: 13px;
  color: #6B7280;
  line-height: 1.5;
}
</style>
