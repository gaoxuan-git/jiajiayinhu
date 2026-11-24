import { createRouter, createWebHashHistory } from 'vue-router'
import store from '@/store'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', noAuth: true }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页', icon: 'home', roles: ['admin', 'institution'] }
      },
      
      // ==================== 管理员专属菜单 ====================
      // 用户管理
      {
        path: 'user-manage',
        name: 'UserManage',
        meta: { title: '用户管理', icon: 'user', roles: ['admin'] },
        children: [
          {
            path: 'admin',
            name: 'Admin',
            component: () => import('@/views/admin/index.vue'),
            meta: { title: '管理员管理', icon: 'user', roles: ['admin'] }
          },
          {
            path: 'parents',
            name: 'ManageParents',
            component: () => import('@/views/admin/manage/Parents.vue'),
            meta: { title: '家长管理', icon: 'avatar', roles: ['admin'] }
          },
          {
            path: 'institutions',
            name: 'ManageInstitutions',
            component: () => import('@/views/admin/manage/Institutions.vue'),
            meta: { title: '机构管理', icon: 'office-building', roles: ['admin'] }
          },
          {
            path: 'nannies',
            name: 'ManageNannies',
            component: () => import('@/views/admin/manage/Nannies.vue'),
            meta: { title: '保姆管理', icon: 'avatar', roles: ['admin'] }
          }
        ]
      },
      // 业务数据
      {
        path: 'business-data',
        name: 'BusinessData',
        meta: { title: '业务数据', icon: 'data-analysis', roles: ['admin'] },
        children: [
          {
            path: 'babies',
            name: 'ManageBabies',
            component: () => import('@/views/admin/manage/Babies.vue'),
            meta: { title: '婴幼儿管理', icon: 'avatar', roles: ['admin'] }
          },
          {
            path: 'health-records',
            name: 'ManageHealthRecords',
            component: () => import('@/views/admin/manage/HealthRecords.vue'),
            meta: { title: '健康档案', icon: 'first-aid-kit', roles: ['admin'] }
          }
        ]
      },
      // 商城管理
      {
        path: 'mall-manage',
        name: 'AdminMallManage',
        meta: { title: '商城管理', icon: 'shopping-bag', roles: ['admin'] },
        children: [
          {
            path: 'category',
            name: 'ProductCategoryManage',
            component: () => import('@/views/admin/ProductCategoryManage.vue'),
            meta: { title: '商品分类', icon: 'folder', roles: ['admin'] }
          },
          {
            path: 'products',
            name: 'AdminProductManage',
            component: () => import('@/views/admin/ProductManage.vue'),
            meta: { title: '商品管理', icon: 'goods', roles: ['admin'] }
          }
        ]
      },
      // 运营管理
      {
        path: 'operation-manage',
        name: 'OperationManage',
        meta: { title: '运营管理', icon: 'promotion', roles: ['admin'] },
        children: [
          {
            path: 'notice',
            name: 'NoticeManage',
            component: () => import('@/views/admin/NoticeManage.vue'),
            meta: { title: '公告管理', icon: 'bell', roles: ['admin'] }
          },
          {
            path: 'message',
            name: 'MessageCenter',
            component: () => import('@/views/admin/MessageCenter.vue'),
            meta: { title: '消息中心', icon: 'message', roles: ['admin'] }
          }
        ]
      },
      // 系统工具
      {
        path: 'system-tools',
        name: 'SystemTools',
        meta: { title: '系统工具', icon: 'setting', roles: ['admin'] },
        children: [
          {
            path: 'codegen',
            name: 'CodeGen',
            component: () => import('@/views/codegen/index.vue'),
            meta: { title: '代码生成', icon: 'document', roles: ['admin'] }
          },
          {
            path: 'cache',
            name: 'Cache',
            component: () => import('@/views/cache/index.vue'),
            meta: { title: '缓存管理', icon: 'coin', roles: ['admin'] }
          },
          {
            path: 'log',
            name: 'Log',
            component: () => import('@/views/log/index.vue'),
            meta: { title: '日志管理', icon: 'document-copy', roles: ['admin'] }
          },
          {
            path: 'api-doc',
            name: 'ApiDoc',
            component: () => import('@/views/api-doc/index.vue'),
            meta: { title: 'API文档', icon: 'reading', roles: ['admin'] }
          }
        ]
      },
      
      // ==================== 机构专属菜单 ====================
      // 机构设置
      {
        path: 'institution-settings',
        name: 'InstitutionSettings',
        meta: { title: '机构设置', icon: 'office-building', roles: ['institution'] },
        children: [
          {
            path: 'profile',
            name: 'InstitutionProfile',
            component: () => import('@/views/institution/Profile.vue'),
            meta: { title: '机构信息', icon: 'document', roles: ['institution'] }
          },
          {
            path: 'address',
            name: 'InstitutionAddress',
            component: () => import('@/views/institution/Address.vue'),
            meta: { title: '地址管理', icon: 'location', roles: ['institution'] }
          }
        ]
      },
      // 人员管理
      {
        path: 'staff-manage',
        name: 'StaffManage',
        meta: { title: '人员管理', icon: 'user', roles: ['institution'] },
        children: [
          {
            path: 'nanny',
            name: 'NannyManage',
            component: () => import('@/views/institution/NannyManage.vue'),
            meta: { title: '员工管理', icon: 'avatar', roles: ['institution'] }
          },
          {
            path: 'schedule',
            name: 'ScheduleManage',
            component: () => import('@/views/institution/ScheduleManage.vue'),
            meta: { title: '排班管理', icon: 'calendar', roles: ['institution'] }
          },
          {
            path: 'attendance',
            name: 'AttendanceManage',
            component: () => import('@/views/institution/AttendanceManage.vue'),
            meta: { title: '考勤管理', icon: 'document-checked', roles: ['institution'] }
          }
        ]
      },
      // 服务管理
      {
        path: 'service-manage',
        name: 'ServiceManage',
        meta: { title: '服务管理', icon: 'service', roles: ['institution'] },
        children: [
          {
            path: 'appointment',
            name: 'AppointmentManage',
            component: () => import('@/views/institution/AppointmentManage.vue'),
            meta: { title: '预约管理', icon: 'calendar', roles: ['institution'] }
          },
          {
            path: 'care-plan',
            name: 'CarePlanManage',
            component: () => import('@/views/institution/CarePlanManage.vue'),
            meta: { title: '护理计划', icon: 'document-checked', roles: ['institution'] }
          },
          {
            path: 'care-record',
            name: 'CareRecordManage',
            component: () => import('@/views/institution/CareRecordManage.vue'),
            meta: { title: '护理记录', icon: 'edit-pen', roles: ['institution'] }
          },
          {
            path: 'evaluation',
            name: 'EvaluationManage',
            component: () => import('@/views/institution/EvaluationManage.vue'),
            meta: { title: '评价监管', icon: 'star', roles: ['institution'] }
          }
        ]
      },
      // 商城管理
      {
        path: 'institution-mall',
        name: 'InstitutionMallManage',
        meta: { title: '商城管理', icon: 'shopping-bag', roles: ['institution'] },
        children: [
          {
            path: 'products',
            name: 'InstitutionProductManage',
            component: () => import('@/views/institution/ProductManage.vue'),
            meta: { title: '商品管理', icon: 'goods', roles: ['institution'] }
          },
          {
            path: 'orders',
            name: 'InstitutionOrderManage',
            component: () => import('@/views/institution/OrderManage.vue'),
            meta: { title: '订单管理', icon: 'shopping-cart-full', roles: ['institution'] }
          },
          {
            path: 'inventory',
            name: 'InstitutionInventoryManage',
            component: () => import('@/views/institution/InventoryManage.vue'),
            meta: { title: '库存管理', icon: 'box', roles: ['institution'] }
          }
        ]
      },
      // 财务管理
      {
        path: 'institution-finance',
        name: 'InstitutionFinanceManage',
        component: () => import('@/views/institution/FinanceManage.vue'),
        meta: { title: '财务管理', icon: 'money', roles: ['institution'] }
      },
      // 消息中心
      {
        path: 'institution-message',
        name: 'InstitutionMessageCenter',
        component: () => import('@/views/institution/MessageCenter.vue'),
        meta: { title: '消息中心', icon: 'message', roles: ['institution'] }
      }
    ]
  },
  {
    path: '/404',
    name: '404',
    component: () => import('@/views/404.vue'),
    meta: { title: '404', noAuth: true }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 白名单（不需要登录的页面）
const whiteList = ['/login', '/404']

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 悦芽婴护` : '悦芽婴护'
  
  // 从 localStorage 直接获取 token，确保刷新页面后也能正确判断
  const token = localStorage.getItem('token') || store.getters.token
  
  console.log('路由守卫 - 当前路径:', to.path, '是否有token:', !!token)
  
  if (token) {
    // 已登录
    if (to.path === '/login') {
      console.log('已登录，跳转到首页')
      next({ path: '/' })
    } else {
      next()
    }
  } else {
    // 未登录
    if (whiteList.includes(to.path) || to.meta.noAuth) {
      console.log('白名单页面，允许访问')
      next()
    } else {
      console.log('未登录，跳转到登录页')
      ElMessage.warning('请先登录')
      next({ path: '/login', replace: true })
    }
  }
})

export default router
