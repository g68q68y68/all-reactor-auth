# 前端开发架构文档

## 一、技术栈

| 技术 | 版本 | 用途 |
|---|---|---|
| Vue | 3.3+ | Composition API + `<script setup>` |
| Vite | 5 | 构建 |
| Vue Router | 4.2+ | 路由（静态 + 动态 addRoute） |
| Pinia | 2.1+ | 状态管理 + 持久化 |
| Element Plus | 2.4+ | UI 组件 |
| Axios | 1.6 | HTTP |
| dayjs | 1.11 | 日期 |

## 二、目录结构

```
src/
├── api/                         # 接口层（按模块拆分）
│   ├── auth.js                  # 登录 / 注册 / 登出
│   ├── user.js                  # 用户 CRUD
│   ├── role.js                  # 角色 CRUD + 角色菜单权限
│   ├── permission.js            # 权限 CRUD + 缓存刷新
│   ├── menu.js                  # 菜单树 + 菜单 CRUD
│   ├── enum.js                  # 枚举类型 + 枚举值
│   └── index.js                 # 统一导出
│
├── assets/styles/               # 全局样式
│   ├── variables.css            # 设计 token
│   └── index.css                # 重置 + Element Plus 覆盖
│
├── components/common/           # 通用组件
│   └── AppPagination.vue        # 统一分页
│
├── composables/
│   └── usePagination.js         # 分页逻辑封装
│
├── router/
│   ├── index.js                 # 静态路由 + 守卫 + 动态路由加载入口
│   └── dynamicRoutes.js         # loadDynamicRoutes / addRoutesFromTree / resetDynamicRoutes
│
├── store/modules/               # Pinia（仅全局共享状态）
│   ├── auth.js                  # token / userInfo / roles / login / logout
│   └── menu.js                  # menuTree / dynamicRoutesLoaded / fetchMenuTree
│
├── utils/
│   ├── request.js               # Axios 实例 + 拦截器（Token 注入 / 过期处理）
│   ├── auth.js                  # 前端角色 / 权限判断工具
│   └── validator.js             # 表单校验规则
│
├── views/                       # 页面
│   ├── auth/                    # 登录 / 注册
│   ├── layout/                  # 布局组件
│   │   ├── MainLayout.vue       # 后台壳（顶栏 + 侧边栏 + 内容区）
│   │   ├── AppSidebar.vue       # 侧边栏容器
│   │   └── SidebarMenuItem.vue  # 递归菜单项（多层展开折叠）
│   ├── dashboard/Index.vue      # 仪表盘
│   ├── users/                   # 用户管理（Index + UserForm）
│   ├── roles/                   # 角色管理（Index + RoleForm + RoleMenuDialog）
│   ├── permissions/             # 权限管理（Index + PermissionForm）
│   ├── menus/                   # 菜单管理（Index + MenuForm）
│   ├── enums/                   # 枚举管理（Index + EnumTypeForm + EnumValueForm）
│   ├── welcome/Index.vue        # 欢迎页
│   └── error/NotFound.vue       # 404
│
├── App.vue                      # 根组件 <router-view>
└── main.js                      # 入口（Pinia → Router → ElementPlus → mount）
```

## 三、路由架构

### 3.1 路由构成

```
全部路由 = 静态路由 + 动态路由 + 404 兜底

静态路由（硬编码，始终存在）：
  /login       → views/auth/Login.vue
  /register    → views/auth/Register.vue

动态路由容器：
  /             → redirect /home（静态占位，后面被动态路由覆盖）

动态路由（数据库 menus 表驱动，登录后动态注册）：
  根节点数组（来自 /api/menus/tree）：
    ├── /home → MainLayout.vue（后台壳，hidden）
    │   └── /home/system/xxx → 各页面
    └── /index → 欢迎页

  动态加载完毕后追加：
  /:pathMatch(.*)* → 404
```

### 3.2 路径计算

| 场景 | DB path | 父路径 | 计算结果 |
|---|---|---|---|
| 根路由（绝对） | `/home` | — | `/home` |
| 子路由（相对） | `dashboard` | `/home` | `/home/dashboard` |
| 深层相对 | `users` | `/home/system` | `/home/system/users` |
| hidden 节点 | — | — | 跳过自身，路径累加到子节点 |

### 3.3 动态路由注册流程

```
login 成功或刷新页面
  → router.beforeEach 守卫触发
  → isAuthenticated=true && dynamicRoutesLoaded=false
  → loadDynamicRoutes(router)
    → menuStore.fetchMenuTree()
      → GET /api/menus/tree → 菜单树 JSON
    → addRoutesFromTree(router, tree, null)
      → 遍历树：type=MENU, status=1
        → hidden 节点：跳过路由注册，子节点上提到父级
        → 有 component：buildRouteConfig → router.addRoute
        → 无 component：纯分组，递归子节点
    → 追加 404 路由
    → dynamicRoutesLoaded = true
  → next({ path, query, replace: true }) 重试导航
```

### 3.4 新增页面

1. 创建 `src/views/xxx/Index.vue`
2. 数据库 `menus` 表插入记录（`component` 填 `@/views/xxx/Index.vue`）
3. 重建项目——`import.meta.glob('@/views/**/*.vue')` 自动扫描，无需改代码

## 四、数据流

### 4.1 请求链路

```
页面组件 → API 函数（@/api/xxx.js）
  → Axios 实例（@/utils/request.js）
    → 请求拦截器：注入 Authorization: Bearer <token>
    → 发送请求
    → 响应拦截器：
        code=200    → 解包返回 data
        code=40101  → Token 过期 → clearAuth + 跳转 /login
        其他错误    → ElMessage.error + reject
```

### 4.2 状态管理

| Store | 内容 | 作用域 |
|---|---|---|
| `auth.js` | accessToken / userInfo / roles / login / logout | 全局（路由守卫、请求拦截器、侧边栏） |
| `menu.js` | menuTree / dynamicRoutesLoaded / fetchMenuTree | 全局（侧边栏渲染、动态路由注册） |

**页面状态**：各页面 Index.vue 内用 `ref`/`reactive` 管理本地状态，通过 `usePagination()` 统一分页逻辑。不创建额外的 Pinia Store。

### 4.3 组件通信

```
MainLayout.vue
  ├── :app-title            → AppSidebar.vue
  ├── authStore             → 用户信息（共享）
  └── <router-view>         → 当前页面

AppSidebar.vue
  ├── menuStore.menuTree    → 菜单数据（共享）
  └── SidebarMenuItem.vue   → 递归渲染

页面 Index.vue
  ├── usePagination(apiFn)  → 分页状态
  ├── <AppPagination>       → 分页 UI
  └── <XxxForm>             → v-model:visible + @success → 刷新列表
```

## 五、通用组件

### 5.1 AppPagination

统一分页，封装 `el-pagination`。

```html
<AppPagination v-model:page="page" v-model:size="size" :total="total" @change="fetchData" />
```

| Props | 类型 | 默认值 | 说明 |
|---|---|---|---|
| page | Number | — | v-model |
| size | Number | — | v-model |
| total | Number | — | 总条数 |
| pageSizes | Array | [10,20,50,100] | 每页条数选项 |
| layout | String | 'total,sizes,prev,pager,next,jumper' | 布局 |

| Events | 说明 |
|---|---|
| change | 页码或每页条数变化 |

### 5.2 SidebarMenuItem

递归菜单项，支持无限层级折叠展开。

| Props | 说明 |
|---|---|
| item | 菜单节点（含 children 和 \_inheritedPath） |
| parentPath | 父级累加路径 |

- hidden 子节点不渲染，路径累加到可见子节点
- 分组节点（有 children）→ `el-sub-menu`，点击标题跳转第一个可见子路由
- 叶子节点 → `el-menu-item`

## 六、usePagination 组合函数

```js
const { list, total, loading, page, size, fetchData, refresh, handlePageChange, handleSizeChange }
  = usePagination(getUsers, { defaultSize: 20 })

// list:     响应式数据列表
// total:    总条数
// loading:  加载态
// page:     当前页（0-based → 后端）
// size:     每页条数
// fetchData(extraParams): 加载数据
// refresh(): 回到第 1 页重新加载
```

## 七、Token 认证

```
登录 → POST /auth/login → { accessToken(24h), userInfo }
  → 存 Pinia authStore + localStorage

请求 → 拦截器注入 Authorization: Bearer <token>
  → 后端校验
    → 有效：正常响应
    → 过期：HTTP 401 + { code: 40101 }
      → ElMessage.error("登录已过期")
      → clearAuth + 800ms → window.location.href = '/login'

页面刷新 → localStorage 恢复 token
  → 路由守卫检测 isAuthenticated=true, dynamicRoutesLoaded=false
  → 重新 loadDynamicRoutes → 导航恢复
```

## 八、开发模板

### 8.1 API 函数

```js
import request from '@/utils/request'

export const getXxxs = (params) => request({ url: '/api/xxxs', method: 'get', params })
export const createXxx = (data) => request({ url: '/api/xxxs', method: 'post', data })
export const updateXxx = (id, data) => request({ url: `/api/xxxs/${id}`, method: 'put', data })
export const deleteXxx = (id) => request({ url: `/api/xxxs/${id}`, method: 'delete' })
```

### 8.2 列表页

```vue
<script setup>
import { ref, onMounted } from 'vue'
import { usePagination } from '@/composables/usePagination'
import { getXxxs, deleteXxx } from '@/api/xxx'
import AppPagination from '@/components/common/AppPagination.vue'
import XxxForm from './XxxForm.vue'

const { list, total, loading, page, size, fetchData } = usePagination(getXxxs)
const dialogVisible = ref(false), current = ref(null), isEdit = ref(false)

const handleAdd = () => { current.value = null; isEdit.value = false; dialogVisible.value = true }
const handleEdit = (r) => { current.value = { ...r }; isEdit.value = true; dialogVisible.value = true }
const handleDelete = (r) => {
  ElMessageBox.confirm('...').then(async () => {
    await deleteXxx(r.id); ElMessage.success('删除成功'); fetchData()
  })
}
const onFormSuccess = () => { dialogVisible.value = false; fetchData() }
onMounted(() => fetchData())
</script>
```

### 8.3 表单弹窗

```vue
<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { createXxx, updateXxx } from '@/api/xxx'

const props = defineProps({ visible: Boolean, xxx: Object, isEdit: Boolean })
const emit = defineEmits(['update:visible', 'success'])

const form = reactive({ /* ... */ })
watch(() => props.xxx, (v) => { /* 填充 */ }, { immediate: true })

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    if (props.isEdit) await updateXxx(props.xxx.id, { ...form })
    else await createXxx({ ...form })
    ElMessage.success(props.isEdit ? '更新成功' : '创建成功')
    emit('success')
  })
}
</script>
```

## 九、菜单表结构

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | PK |
| parent_id | BIGINT | 父菜单ID |
| path | VARCHAR | 路由路径（绝对或相对） |
| redirect | VARCHAR | 重定向 |
| name | VARCHAR | 路由名 |
| component | VARCHAR | `@/views/xxx/Index.vue` |
| title | VARCHAR | 显示名 |
| icon | VARCHAR | Element Plus 图标 |
| type | VARCHAR | MENU / BUTTON / API |
| requires_auth | TINYINT | 需认证 |
| hidden | TINYINT | 侧边栏隐藏（子节点上提） |
| sort_order | INT | 排序（负数支持） |
| status | TINYINT | 1=启用 |

## 十、环境变量

| 变量 | 说明 |
|---|---|
| `VITE_API_BASE_URL` | 后端地址 |
| `VITE_APP_TITLE` | 应用标题 |
| `VITE_APP_VERSION` | 版本号 |
