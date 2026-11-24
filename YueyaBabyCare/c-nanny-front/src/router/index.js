import { createRouter, createWebHashHistory } from 'vue-router'
import AppLayout from '../components/AppLayout.vue'
import ScheduleView from '../views/ScheduleView.vue'
import RecordView from '../views/RecordView.vue'
import ProfileView from '../views/ProfileView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import CertificateView from '../views/CertificateView.vue'
import AppointmentView from '../views/AppointmentView.vue'
import CarePlanView from '../views/CarePlanView.vue'
import EvaluationView from '../views/EvaluationView.vue'
import MessageView from '../views/MessageView.vue'
import ContactSelectView from '../views/ContactSelectView.vue'

const routes = [
  // 登录页
  {
    path: '/login',
    name: 'login',
    component: LoginView
  },
  // 注册页
  {
    path: '/register',
    name: 'register',
    component: RegisterView
  },
  // 证书管理页
  {
    path: '/certificates',
    name: 'certificates',
    component: CertificateView,
    meta: { requiresAuth: true }
  },
  // 个人信息编辑页
  {
    path: '/profile/edit',
    name: 'ProfileEdit',
    component: () => import('../views/ProfileEditView.vue'),
    meta: { requiresAuth: true }
  },
  // 地址管理页
  {
    path: '/address',
    name: 'Address',
    component: () => import('../views/AddressView.vue'),
    meta: { requiresAuth: true }
  },
  // 预约详情页
  {
    path: '/appointment/:id',
    name: 'AppointmentDetail',
    component: () => import('../views/AppointmentDetailView.vue'),
    meta: { requiresAuth: true }
  },
  // 选择联系人页面
  {
    path: '/contact-select',
    name: 'ContactSelect',
    component: ContactSelectView,
    meta: { requiresAuth: true }
  },
  // 主应用布局
  {
    path: '/',
    component: AppLayout,
    redirect: '/schedule',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'schedule',
        name: 'schedule',
        component: ScheduleView
      },
      {
        path: 'appointment',
        name: 'appointment',
        component: AppointmentView
      },
      {
        path: 'care-plan',
        name: 'carePlan',
        component: CarePlanView
      },
      {
        path: 'record',
        name: 'record',
        component: RecordView
      },
      {
        path: 'evaluation',
        name: 'evaluation',
        component: EvaluationView
      },
      {
        path: 'message',
        name: 'message',
        component: MessageView
      },
      {
        path: 'profile',
        name: 'profile',
        component: ProfileView
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 路由守卫 - 检查登录状态
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('nanny_token')
  
  // 如果页面需要认证
  if (to.meta.requiresAuth) {
    if (!token) {
      // 未登录，跳转到登录页
      next('/login')
    } else {
      next()
    }
  } else {
    // 如果已登录且访问登录页，跳转到首页
    if (to.path === '/login' && token) {
      next('/schedule')
    } else {
      next()
    }
  }
})

export default router
