import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
    },
    {
      path: '/',
      component: () => import('@/layouts/AdminLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        { path: '', redirect: '/dashboard' },
        { path: 'dashboard', name: 'dashboard', component: () => import('@/views/DashboardView.vue') },
        { path: 'site-config', name: 'site-config', meta: { roles: ['ADMIN'] }, component: () => import('@/views/SiteConfigView.vue') },
        { path: 'navigation', name: 'navigation', meta: { roles: ['ADMIN'] }, component: () => import('@/views/NavigationView.vue') },
        { path: 'rfq', name: 'rfq-list', meta: { roles: ['ADMIN', 'SALES'] }, component: () => import('@/views/RfqListView.vue') },
        { path: 'rfq/:id', name: 'rfq-detail', meta: { roles: ['ADMIN', 'SALES'] }, component: () => import('@/views/RfqDetailView.vue') },
        { path: 'products', name: 'products', meta: { roles: ['ADMIN', 'EDITOR'] }, component: () => import('@/views/ProductListView.vue') },
        { path: 'industries', name: 'industries', meta: { roles: ['ADMIN', 'EDITOR'] }, component: () => import('@/views/IndustryListView.vue') },
        { path: 'media', name: 'media', meta: { roles: ['ADMIN', 'EDITOR'] }, component: () => import('@/views/MediaLibraryView.vue') },
        { path: 'audit-logs', name: 'audit-logs', meta: { roles: ['ADMIN'] }, component: () => import('@/views/AuditLogView.vue') },
        { path: 'users', name: 'users', meta: { roles: ['ADMIN'] }, component: () => import('@/views/AdminUserView.vue') },
        { path: 'reference-data', name: 'reference-data', meta: { roles: ['ADMIN', 'EDITOR'] }, component: () => import('@/views/ReferenceDataView.vue') },
        { path: 'articles', name: 'articles', meta: { roles: ['ADMIN', 'EDITOR'] }, component: () => import('@/views/ArticleListView.vue') },
        { path: 'contact-messages', name: 'contact-messages', meta: { roles: ['ADMIN', 'SALES'] }, component: () => import('@/views/ContactMessageView.vue') },
        { path: 'customers', name: 'customers', meta: { roles: ['ADMIN', 'SALES'] }, component: () => import('@/views/CustomerListView.vue') },
        { path: 'customers/:id', name: 'customer-detail', meta: { roles: ['ADMIN', 'SALES'] }, component: () => import('@/views/CustomerDetailView.vue') },
      ],
    },
    { path: '/:pathMatch(.*)*', redirect: '/dashboard' },
  ],
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isLoggedIn) return '/login'
  if (to.path === '/login' && auth.isLoggedIn) return '/dashboard'
  const roles = to.meta.roles as string[] | undefined
  if (roles?.length && !roles.includes(auth.role)) return '/dashboard'
  return true
})

export default router
