import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/store/modules/auth'
import { useMenuStore } from '@/store/modules/menu'
import { loadDynamicRoutes } from './dynamicRoutes'

// ========== 静态路由（始终存在） ==========
const staticRoutes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { requiresAuth: false, title: '登录', sortOrder: 0, hidden: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { requiresAuth: false, title: '注册', sortOrder: 0, hidden: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/NotFound.vue'),
    meta: { requiresAuth: false, title: '页面不存在', sortOrder: 999, hidden: true }
  }
]

// 动态路由全部由后端菜单树提供（含 MainLayout 布局路由）

const router = createRouter({
  history: createWebHistory(),
  routes: staticRoutes
})

/**
 * 获取真实的认证状态。
 * Pinia persist 插件在第一次 useAuthStore() 时水合，但可能存在时序问题。
 * 此处同时检查 store 和原始 localStorage，确保刷新页面时不会误判。
 */
function getRealAuthState(authStore) {
  // 先显式触发 restore（兜底 Pinia 插件水合时序问题）
  if (!authStore.accessToken) {
    authStore.restore()
  }
  // 双重检查：store 或 localStorage 中任一有 token 即认为已登录
  if (authStore.accessToken) return true
  try {
    const raw = localStorage.getItem('auth-store')
    if (raw) {
      const data = JSON.parse(raw)
      return !!data.accessToken
    }
  } catch (e) { /* ignore */ }
  return false
}

// ========== 路由守卫 ==========
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  const menuStore = useMenuStore()

  // 设置页面标题
  document.title = to.meta.title
    ? `${to.meta.title} - ${import.meta.env.VITE_APP_TITLE}`
    : import.meta.env.VITE_APP_TITLE

  const isAuthenticated = getRealAuthState(authStore)

  // ===== 第一步：已登录用户优先加载动态路由 =====
  // 必须放在所有判断之前，否则刷新页面时动态路径会被 404 通配路由拦截
  if (isAuthenticated && !menuStore.dynamicRoutesLoaded) {
    // 登录/注册页不需要加载动态路由
    if (to.path !== '/login' && to.path !== '/register') {
      try {
        await loadDynamicRoutes(router)
        // 路由已注册，用原始路径重新导航（不展开 to，避免携带 404 匹配的 params）
        next({ path: to.path, query: to.query, replace: true })
        return
      } catch (error) {
        console.error('[Router] 加载动态路由异常:', error)
        next({ path: '/login' })
        return
      }
    }
  }

  // ===== 第二步：需要认证的路由检查 =====
  if (to.meta.requiresAuth !== false) {
    if (!isAuthenticated) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }
  }

  // ===== 第三步：已登录用户访问登录页 → 跳转首页 =====
  if (to.path === '/login' && isAuthenticated) {
    next('/')
    return
  }

  next()
})

export default router
