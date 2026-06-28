import { useMenuStore } from '@/store/modules/menu'

const modules = import.meta.glob('@/views/**/*.vue')
const NotFoundComponent = () => import('@/views/error/NotFound.vue')

export async function loadDynamicRoutes(router) {
  const menuStore = useMenuStore()
  if (menuStore.dynamicRoutesLoaded) return

  try {
    const tree = await menuStore.fetchMenuTree()
    if (!tree || tree.length === 0) {
      menuStore.setDynamicRoutesLoaded(true)
      return
    }

    addRoutesFromTree(router, tree, null)
    // 最后追加 404 兜底（必须在所有动态路由之后注册）
    router.addRoute({
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: NotFoundComponent,
      meta: { requiresAuth: false, title: '页面不存在', sortOrder: 999, hidden: true }
    })
    menuStore.setDynamicRoutesLoaded(true)
  } catch (error) {
    console.error('[DynamicRoutes] 加载失败:', error)
    menuStore.setDynamicRoutesLoaded(true)
  }
}

/**
 * 递归添加路由
 * @param {Router} router - Vue Router 实例
 * @param {Array} nodes - 菜单节点数组
 * @param {string|null} parentRouteName - 父路由名称（null 表示根路由）
 */
const addRoutesFromTree = (router, nodes, parentRouteName = null, parentPath = '') => {
  for (const node of nodes) {
    if (node.type !== 'MENU' || node.status !== 1) continue

    const p = node.path || ''
    const fullPath = p.startsWith('/') ? p : `${parentPath}/${p}`.replace(/\/+/g, '/')
    const routeConfig = buildRouteConfig(node, fullPath)

    // hidden 只影响侧边栏显示，不影响路由注册（布局路由如 MainLayout 需要 hidden 但仍需注册）
    if (routeConfig.component) {
      if (parentRouteName === null) {
        router.addRoute(routeConfig)
      } else {
        router.addRoute(parentRouteName, routeConfig)
      }
    }

    if (node.children?.length) {
      const nextParent = node.component ? routeConfig.name : parentRouteName
      addRoutesFromTree(router, node.children, nextParent, fullPath)
    }
  }
}

/**
 * 构建路由配置对象
 */
function buildRouteConfig(node, fullPath) {
  const nodeTitle = node.title || ''
  const nodeSortOrder = node.sortOrder != null ? node.sortOrder : 0

  const routeName = `route_${node.id}`

  const config = {
    path: fullPath,
    name: routeName,
    meta: {
      title: nodeTitle,
      icon: node.icon || null,
      sortOrder: nodeSortOrder,
      requiresAuth: node.requiresAuth !== false,
      hidden: node.hidden === true,
      menuId: node.id,
      // 保留原始 name 用于其他用途
      originalName: node.name
    }
  }

  // 如果有 component，添加组件加载器
  if (node.component) {
    const componentPath = node.component.replace('@/', '/src/')
    const componentLoader = modules[componentPath]
    if (componentLoader) {
      config.component = componentLoader
    } else {
      console.warn(`[DynamicRoutes] 组件未找到: ${componentPath}`)
    }
  }

  // 如果有 redirect，添加重定向
  if (node.redirect) {
    config.redirect = node.redirect
  }

  // 如果有子节点且当前节点有 component，初始化 children 数组
  if (node.children?.length && node.component) {
    config.children = []
  }

  return config
}

export function resetDynamicRoutes(router) {
  const menuStore = useMenuStore()

  // 获取所有动态添加的路由
  const allRoutes = router.getRoutes()

  for (const route of allRoutes) {
    // ✅ 根据 menuId 或 route name 前缀判断是否为动态路由
    if (route.meta?.menuId) {
      router.removeRoute(route.name)
      console.log(`[DynamicRoutes] 移除路由: ${route.path}`)
    }
  }

  menuStore.setDynamicRoutesLoaded(false)
  menuStore.menuTree = []
}