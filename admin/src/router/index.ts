import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import Layout from '@/components/layout/index.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', hidden: true }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页', icon: 'House' }
      }
    ]
  },
  {
    path: '/content',
    component: Layout,
    redirect: '/content/banners',
    meta: { title: '内容管理', icon: 'Document' },
    children: [
      {
        path: 'banners',
        name: 'Banners',
        component: () => import('@/views/content/Banners.vue'),
        meta: { title: 'Banner管理' }
      },
      {
        path: 'news',
        name: 'News',
        component: () => import('@/views/content/News.vue'),
        meta: { title: '新闻管理' }
      },
      {
        path: 'news/create',
        name: 'NewsCreate',
        component: () => import('@/views/content/NewsEdit.vue'),
        meta: { title: '新增新闻', hidden: true }
      },
      {
        path: 'news/:id/edit',
        name: 'NewsEdit',
        component: () => import('@/views/content/NewsEdit.vue'),
        meta: { title: '编辑新闻', hidden: true }
      },
      {
        path: 'activities',
        name: 'Activities',
        component: () => import('@/views/content/Activities.vue'),
        meta: { title: '活动管理' }
      },
      {
        path: 'activities/create',
        name: 'ActivityCreate',
        component: () => import('@/views/content/ActivityEdit.vue'),
        meta: { title: '新增活动', hidden: true }
      },
      {
        path: 'activities/:id/edit',
        name: 'ActivityEdit',
        component: () => import('@/views/content/ActivityEdit.vue'),
        meta: { title: '编辑活动', hidden: true }
      },
      {
        path: 'resources',
        name: 'Resources',
        component: () => import('@/views/content/Resources.vue'),
        meta: { title: '资源管理' }
      },
      {
        path: 'certificates',
        name: 'Certificates',
        component: () => import('@/views/content/Certificates.vue'),
        meta: { title: '证件管理' }
      },
      {
        path: 'certificates/create',
        name: 'CertificateCreate',
        component: () => import('@/views/content/CertificateEdit.vue'),
        meta: { title: '新增证件', hidden: true }
      },
      {
        path: 'certificates/:id/edit',
        name: 'CertificateEdit',
        component: () => import('@/views/content/CertificateEdit.vue'),
        meta: { title: '编辑证件', hidden: true }
      }
    ]
  },
  {
    path: '/member',
    component: Layout,
    redirect: '/member/list',
    meta: { title: '会员管理', icon: 'User' },
    children: [
      {
        path: 'list',
        name: 'MemberList',
        component: () => import('@/views/member/MemberList.vue'),
        meta: { title: '会员列表' }
      },
      {
        path: 'applications',
        name: 'MemberApplications',
        component: () => import('@/views/member/Applications.vue'),
        meta: { title: '入会申请' }
      }
    ]
  },
  {
    path: '/system',
    component: Layout,
    redirect: '/system/users',
    meta: { title: '系统管理', icon: 'Setting' },
    children: [
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/system/Users.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'roles',
        name: 'Roles',
        component: () => import('@/views/system/Roles.vue'),
        meta: { title: '角色管理' }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/system/Settings.vue'),
        meta: { title: '系统设置' }
      },
      {
        path: 'feedback',
        name: 'Feedback',
        component: () => import('@/views/system/Feedback.vue'),
        meta: { title: '投诉反馈' }
      },
      {
        path: 'contact',
        name: 'Contact',
        component: () => import('@/views/system/Contact.vue'),
        meta: { title: '联系我们' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory('/admin/'),
  routes
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  // 获取token
  const token = localStorage.getItem('token')

  if (to.path === '/login') {
    if (token) {
      next('/')
    } else {
      next()
    }
  } else {
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router