import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { useAuthStore } from '@/stores/auth'
import { usePermissionStore } from '@/stores/permission'

NProgress.configure({ showSpinner: false })

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { public: true, title: 'Login' }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: 'Dashboard', icon: 'Dashboard' }
      },
      {
        path: '/system',
        name: 'System',
        meta: { title: 'System Management', icon: 'Setting' },
        children: [
          {
            path: '/system/user',
            name: 'UserManagement',
            component: () => import('@/views/system/user/index.vue'),
            meta: { title: 'User Management', icon: 'User', permission: 'system:user:list' }
          },
          {
            path: '/system/role',
            name: 'RoleManagement',
            component: () => import('@/views/system/role/index.vue'),
            meta: { title: 'Role Management', icon: 'UserFilled', permission: 'system:role:list' }
          },
          {
            path: '/system/menu',
            name: 'MenuManagement',
            component: () => import('@/views/system/menu/index.vue'),
            meta: { title: 'Menu Management', icon: 'Menu', permission: 'system:menu:list' }
          }
        ]
      }
    ]
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/error/403.vue'),
    meta: { public: true, title: 'Access Denied' }
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { public: true, title: 'Page Not Found' }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach(async (to, from, next) => {
  NProgress.start()
  
  const authStore = useAuthStore()
  const permissionStore = usePermissionStore()
  
  const isPublic = to.meta?.public
  const hasToken = !!authStore.token
  
  if (isPublic) {
    if (to.path === '/login' && hasToken) {
      next('/')
      return
    }
    next()
    return
  }
  
  if (!hasToken) {
    next('/login')
    return
  }
  
  if (!authStore.userInfo) {
    try {
      await authStore.fetchUserInfo()
      await permissionStore.fetchUserMenus()
      await permissionStore.fetchUserPermissions()
      next({ ...to, replace: true })
      return
    } catch (error) {
      authStore.logout()
      next('/login')
      return
    }
  }
  
  const requiredPermission = to.meta?.permission as string
  if (requiredPermission && !authStore.hasPermission(requiredPermission)) {
    next('/403')
    return
  }
  
  next()
})

router.afterEach((to) => {
  NProgress.done()
  document.title = `${to.meta?.title || 'RBAC System'} - RBAC Management`
})

export default router
