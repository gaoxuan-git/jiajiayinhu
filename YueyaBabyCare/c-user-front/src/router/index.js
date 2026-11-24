import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

const routes = [
  {
    path: '/',
    component: Layout,
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/homepage/index.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'babies',
        name: 'Babies',
        component: () => import('@/views/babies/index.vue'),
        meta: { title: '我的宝宝', requireAuth: true }
      },
      {
        path: 'baby/add',
        name: 'BabyAdd',
        component: () => import('@/views/babies/edit.vue'),
        meta: { title: '添加宝宝', requireAuth: true }
      },
      {
        path: 'baby/edit/:id',
        name: 'BabyEdit',
        component: () => import('@/views/babies/edit.vue'),
        meta: { title: '编辑宝宝', requireAuth: true }
      },
      {
        path: 'baby/avatar/:id',
        name: 'BabyAvatar',
        component: () => import('@/views/babies/avatar.vue'),
        meta: { title: '修改头像', requireAuth: true }
      },
      {
        path: 'baby/:id',
        name: 'BabyDetail',
        component: () => import('@/views/babies/detail.vue'),
        meta: { title: '宝宝详情', requireAuth: true }
      },
      {
        path: 'health-records/:babyId',
        name: 'HealthRecords',
        component: () => import('@/views/health/records.vue'),
        meta: { title: '健康档案', requireAuth: true }
      },
      {
        path: 'growth-records/:babyId',
        name: 'GrowthRecords',
        component: () => import('@/views/growth/records.vue'),
        meta: { title: '成长记录', requireAuth: true }
      },
      {
        path: 'care-plans',
        name: 'CarePlans',
        component: () => import('@/views/care/plans.vue'),
        meta: { title: '护理计划', requireAuth: true }
      },
      {
        path: 'care-plan/:id',
        name: 'CarePlanDetail',
        component: () => import('@/views/care/planDetail.vue'),
        meta: { title: '计划详情', requireAuth: true }
      },
      {
        path: 'care-records/:babyId',
        name: 'CareRecords',
        component: () => import('@/views/care/records.vue'),
        meta: { title: '护理记录', requireAuth: true }
      },
      {
        path: 'evaluations',
        name: 'Evaluations',
        component: () => import('@/views/care/evaluations.vue'),
        meta: { title: '护理评价', requireAuth: true }
      },
      {
        path: 'appointments',
        name: 'Appointments',
        component: () => import('@/views/appointments/index.vue'),
        meta: { title: '我的预约', requireAuth: true }
      },
      {
        path: 'appointment/:id',
        name: 'AppointmentDetail',
        component: () => import('@/views/appointments/detail.vue'),
        meta: { title: '预约详情', requireAuth: true }
      },
      {
        path: 'mall',
        name: 'Mall',
        component: () => import('@/views/mall/index.vue'),
        meta: { title: '商城' }
      },
      {
        path: 'products',
        name: 'Products',
        component: () => import('@/views/products/index.vue'),
        meta: { title: '商品浏览' }
      },
      {
        path: 'product/:id',
        name: 'ProductDetail',
        component: () => import('@/views/products/detail.vue'),
        meta: { title: '商品详情' }
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('@/views/cart/index.vue'),
        meta: { title: '购物车', requireAuth: true }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/orders/index.vue'),
        meta: { title: '订单列表', requireAuth: true }
      },
      {
        path: 'order/:id',
        name: 'OrderDetail',
        component: () => import('@/views/orders/detail.vue'),
        meta: { title: '订单详情', requireAuth: true }
      },
      {
        path: 'checkout',
        name: 'Checkout',
        component: () => import('@/views/orders/checkout.vue'),
        meta: { title: '确认订单', requireAuth: true }
      },
      {
        path: 'messages',
        name: 'Messages',
        component: () => import('@/views/messages/index.vue'),
        meta: { title: '消息中心', requireAuth: true }
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '数据中心', requireAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', requireAuth: true }
      },
      {
        path: 'profile/edit',
        name: 'ProfileEdit',
        component: () => import('@/views/profile/edit.vue'),
        meta: { title: '编辑资料', requireAuth: true }
      },
      {
        path: 'addresses',
        name: 'Addresses',
        component: () => import('@/views/profile/addresses.vue'),
        meta: { title: '地址管理', requireAuth: true }
      },
      {
        path: 'institutions',
        name: 'Institutions',
        component: () => import('@/views/institutions/index.vue'),
        meta: { title: '选择机构' }
      },
      {
        path: 'institutions/:id',
        name: 'InstitutionDetail',
        component: () => import('@/views/institutions/detail.vue'),
        meta: { title: '机构详情' }
      },
      {
        path: 'nannies/:id',
        name: 'NannyDetail',
        component: () => import('@/views/nannies/detail.vue'),
        meta: { title: '保姆详情' }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'),
    meta: { title: '注册' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 悦芽婴护` : '悦芽婴护'
  
  // 检查是否需要登录
  const token = localStorage.getItem('c_token')
  if (to.meta.requireAuth && !token) {
    // 需要登录但未登录，跳转到登录页
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else if (token && (to.path === '/login' || to.path === '/register')) {
    // 已登录但访问登录/注册页，跳转到首页
    next('/')
  } else {
    next()
  }
})

export default router
