import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/news',
    name: 'News',
    component: () => import('@/views/News.vue')
  },
  {
    path: '/news/:id',
    name: 'NewsDetail',
    component: () => import('@/views/NewsDetail.vue')
  },
  {
    path: '/notice',
    name: 'Notice',
    component: () => import('@/views/Notice.vue')
  },
  {
    path: '/notice/:id',
    name: 'NoticeDetail',
    component: () => import('@/views/NewsDetail.vue')
  },
  {
    path: '/policy',
    name: 'Policy',
    component: () => import('@/views/Policy.vue')
  },
  {
    path: '/policy/:id',
    name: 'PolicyDetail',
    component: () => import('@/views/NewsDetail.vue')
  },
  {
    path: '/industry',
    name: 'Industry',
    component: () => import('@/views/Industry.vue')
  },
  {
    path: '/industry/:id',
    name: 'IndustryDetail',
    component: () => import('@/views/NewsDetail.vue')
  },
  {
    path: '/study',
    name: 'Study',
    component: () => import('@/views/Study.vue')
  },
  {
    path: '/study/:id',
    name: 'StudyDetail',
    component: () => import('@/views/NewsDetail.vue')
  },
  {
    path: '/certificate-intro',
    name: 'CertificateIntro',
    component: () => import('@/views/CertificateIntro.vue')
  },
  {
    path: '/certificate-query',
    name: 'CertificateQuery',
    component: () => import('@/views/Certificates.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

export default router