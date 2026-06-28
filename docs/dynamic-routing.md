# 动态路由系统设计文档

## 一、架构概览

```mermaid
graph TB
    subgraph Frontend["前端 (Vue 3)"]
        direction TB
        subgraph Router["路由层"]
            SR["静态路由<br/>/login /register /404<br/>meta: hidden=true"]
            DRC["动态路由容器<br/>/ MainLayout<br/>children: []"]
        end

        LDR["loadDynamicRoutes()<br/>① 拉取菜单树<br/>② 逐个 addRoute()"]

        RGR["router.getRoutes()<br/>合并后的完整路由表"]

        subgraph UI["UI 层"]
            ML["MainLayout.vue<br/>侧边栏从 router.getRoutes() 渲染<br/>- 按 sortOrder 排序<br/>- 按 group 分组 (el-sub-menu)<br/>- hidden=true 的跳过"]
            MP["views/menus/Index.vue<br/>菜单管理页 (树形表格)"]
        end

        SR --> RGR
        DRC --> LDR
        LDR -->|router.addRoute| RGR
        RGR --> ML
        RGR --> MP
    end

    subgraph Backend["后端 (Spring Boot)"]
        direction TB
        MC["MenuController<br/>GET /api/menus/tree<br/>GET/POST/PUT/DELETE /api/menus"]
        MS["MenuService.buildTree()<br/>扁平数据 → 递归树"]
        MR["MenuRepository<br/>R2DBC 响应式查询"]
        MC --> MS --> MR
    end

    subgraph DB["数据库"]
        MT[("menus 表")]
    end

    LDR -->|HTTP GET /api/menus/tree| MC
    MP -->|CRUD /api/menus| MC
    MR --> MT
```

---

## 二、数据库设计

### menus 表结构

```sql
CREATE TABLE `menus` (
    `id`            BIGINT       AUTO_INCREMENT PRIMARY KEY,
    `parent_id`     BIGINT       DEFAULT NULL,
    `path`          VARCHAR(255) NOT NULL,
    `name`          VARCHAR(100) NOT NULL,
    `component`     VARCHAR(255) DEFAULT NULL,
    `title`         VARCHAR(100) NOT NULL,
    `icon`          VARCHAR(100) DEFAULT NULL,
    `type`          VARCHAR(20)  DEFAULT 'MENU',
    `requires_auth` TINYINT(1)   DEFAULT 1,
    `sort_order`    INT          DEFAULT 0,
    `status`        TINYINT(1)   DEFAULT 1,
    `created_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`parent_id`) REFERENCES `menus`(`id`) ON DELETE CASCADE
);
```

### 种子数据 -> 树形结构

```mermaid
graph TD
    ROOT["① 根节点<br/>id=1, component=NULL<br/>🔒 结构根，不在侧边栏显示"]
    ROOT --> DASH["② 仪表盘<br/>id=2, component 有值<br/>✅ 叶子 → 注册为路由"]
    ROOT --> USER["③ 用户管理<br/>id=3, component 有值<br/>✅ 叶子 → 注册为路由"]
    ROOT --> DEMO["④ 数据演示<br/>id=4, component 有值<br/>✅ 叶子 → 注册为路由"]
    ROOT --> ROLE["⑤ 角色管理<br/>id=5, component 有值<br/>✅ 叶子 → 注册为路由"]
    ROOT --> PERM["⑥ 权限管理<br/>id=6, component 有值<br/>✅ 叶子 → 注册为路由"]
    ROOT --> MENU["⑦ 菜单管理<br/>id=7, component 有值<br/>✅ 叶子 → 注册为路由"]
```

### 扩展：添加分组后的结构

```mermaid
graph TD
    ROOT["① 根节点<br/>component=NULL"]
    ROOT --> DASH["② 仪表盘<br/>component 有值"]
    ROOT --> GROUP["⑧ 系统管理<br/>component=NULL<br/>📁 纯分组节点"]
    GROUP --> USER["③ 用户管理"]
    GROUP --> ROLE["⑤ 角色管理"]
    GROUP --> MENU["⑦ 菜单管理"]
    ROOT --> DEMO["④ 数据演示"]
    ROOT --> PERM["⑥ 权限管理"]

    style GROUP fill:#f9f,stroke:#333,stroke-width:2px
```

> 分组节点的 `sortOrder` 控制分组在侧边栏的位置；组内子节点的 `sortOrder` 控制组内顺序。最终排序号 = 父 sortOrder × 1000 + 子 sortOrder。

---

## 三、后端实现

### 3.1 核心文件

| 文件 | 职责 |
|---|---|
| `entity/Menu.java` | 实体类，映射 `menus` 表 |
| `dto/MenuTreeNode.java` | 树节点 DTO，含 `List<MenuTreeNode> children` |
| `repository/MenuRepository.java` | 数据访问，继承 `R2dbcRepository` + `PageableRepository` |
| `service/MenuService.java` | **核心：`buildTree()` 将扁平数据构建为递归树** |
| `controller/MenuController.java` | REST 控制器，继承 `BaseController` 获得 CRUD，新增 `/tree` |

### 3.2 树构建算法 `buildTree()`

```mermaid
flowchart TD
    INPUT["输入: 数据库查出的扁平 List&lt;Menu&gt;<br/>1(NULL), 2(1), 3(1), 4(1)"]
    STEP1["步骤1: 全部转为 MenuTreeNode<br/>每个节点 children=[]"]
    STEP2["步骤2: 按 parentId 分组<br/>Map&lt;parentId, List&lt;MenuTreeNode&gt;&gt;<br/>{1: [2,3,4], NULL: [1]}"]
    STEP3["步骤3: 遍历每个节点<br/>node.children = map.get(node.id)"]
    STEP4["步骤4: 过滤返回<br/>parentId == NULL 的节点"]
    OUTPUT["输出: 根节点(含所有 children 嵌套)"]

    INPUT --> STEP1 --> STEP2 --> STEP3 --> STEP4 --> OUTPUT
```

```mermaid
graph LR
    subgraph 扁平输入
        A["id=1<br/>parent=NULL"]
        B["id=2 parent=1"]
        C["id=3 parent=1"]
        D["id=4 parent=1"]
    end

    subgraph 树形输出
        A2["id=1"]
        A2 --> B2["id=2"]
        A2 --> C2["id=3"]
        A2 --> D2["id=4"]
    end

    A -.->|buildTree| A2
    B -.->|buildTree| B2
    C -.->|buildTree| C2
    D -.->|buildTree| D2
```

### 3.3 API 端点

| 方法 | 路径 | 说明 |
|---|---|---|
| `GET` | `/api/menus/tree` | **返回完整菜单树**（前端路由注册 + 侧边栏渲染） |
| `GET` | `/api/menus?page=1&size=10` | 分页查询（菜单管理页） |
| `GET` | `/api/menus/{id}` | 查询单个菜单 |
| `POST` | `/api/menus` | 新增菜单 |
| `PUT` | `/api/menus/{id}` | 更新菜单 |
| `DELETE` | `/api/menus/{id}` | 删除菜单（级联删除子菜单） |

---

## 四、前端实现

### 4.1 路由拆分

```mermaid
graph TD
    ALL["全部路由"]

    ALL --> STATIC["静态路由 (router/index.js 硬编码)"]
    STATIC --> S1["/login<br/>meta: { hidden, sortOrder:0 }"]
    STATIC --> S2["/register<br/>meta: { hidden, sortOrder:0 }"]
    STATIC --> S3["/:pathMatch(.*)*<br/>meta: { hidden, sortOrder:999 }<br/>404 兜底"]

    ALL --> DYNAMIC["动态路由容器 (壳子)"]
    DYNAMIC --> D1["/ (MainLayout)<br/>meta: { requiresAuth:true }<br/>children: [] ← 空壳"]
    D1 --> D2["子路由由后端动态注入<br/>router.addRoute('MainLayout', ...)"]

    style D2 fill:#f96,stroke:#333
```

### 4.2 动态路由加载流程

```mermaid
flowchart TD
    START(["应用启动 / 页面刷新 / 登录成功"]) --> GUARD["router.beforeEach 触发"]

    GUARD --> NEED_AUTH{"to.meta.requiresAuth<br/>=== false ?"}
    NEED_AUTH -->|"是 (登录/注册/404)"| PASS["next() 直接放行"]
    NEED_AUTH -->|"否 (需要认证)"| AUTH_CHECK{"authStore<br/>.isAuthenticated ?"}

    AUTH_CHECK -->|"否"| REDIRECT["重定向到<br/>/login?redirect=原路径"]
    AUTH_CHECK -->|"是"| ROUTE_LOADED{"menuStore<br/>.dynamicRoutesLoaded ?"}

    ROUTE_LOADED -->|"是"| PASS2["next() 直接放行"]
    ROUTE_LOADED -->|"否"| LOAD["loadDynamicRoutes(router)"]

    LOAD --> FETCH["① GET /api/menus/tree<br/>→ 菜单树 JSON"]
    FETCH --> ITER["② 遍历树节点<br/>找 type=MENU, status=1, component 有值"]
    ITER --> ADD_ROUTE["③ router.addRoute('MainLayout', {<br/>  path, name,<br/>  component: componentMap[路径],<br/>  meta: { title, icon, sortOrder, group, menuId }<br/>})"]
    ADD_ROUTE --> MARK["④ dynamicRoutesLoaded = true"]
    MARK --> RETRY["⑤ next({...to, replace:true})<br/>重新解析导航"]
    RETRY --> PASS3["路由匹配 → 渲染页面"]
```

### 4.3 componentMap 原理

```mermaid
flowchart LR
    subgraph WRONG["❌ 错误方式"]
        VAR["const path = '/views/users/Index.vue'"]
        DYN["() => import(`@/${path}`)"]
        VAR --> DYN
        ERR["Vite: ⚠️ 无法静态分析"]
        DYN --> ERR
    end

    subgraph RIGHT["✅ 正确方式"]
        MAP["const componentMap = {<br/>  '/views/users/Index.vue':<br/>    () => import('@/views/users/Index.vue'),<br/>  ...<br/>}"]
        LOOKUP["componentMap[node.component]"]
        MAP --> LOOKUP
        OK["Vite: ✅ 构建时全量分析"]
        LOOKUP --> OK
    end
```

### 4.4 侧边栏生成

```mermaid
flowchart TD
    SRC["router.getRoutes()<br/>已注册的全部路由"] --> FILTER["过滤<br/>- hidden !== true<br/>- meta.title 存在<br/>- name !== 'MainLayout'"]
    FILTER --> MAP["映射<br/>{ path, title, icon, sortOrder, group }"]
    MAP --> SORT["排序<br/>按 sortOrder 升序"]
    SORT --> GROUP{"item.group<br/>是否有值？"}
    GROUP -->|有| SUB["归入 el-sub-menu<br/>同名 group 分组"]
    GROUP -->|无| ITEM["独立 el-menu-item"]
    SUB --> RENDER["el-menu 渲染<br/>router 模式，点击自动导航"]
    ITEM --> RENDER
```

### 4.5 核心文件总览

```mermaid
graph LR
    subgraph API["api/"]
        MENUAPI["menu.js"]
    end

    subgraph Store["store/modules/"]
        MENUSTORE["menu.js<br/>state: menuTree, dynamicRoutesLoaded"]
    end

    subgraph Router["router/"]
        INDEX["index.js<br/>静态路由 + 守卫"]
        DYNROUTE["dynamicRoutes.js<br/>loadDynamicRoutes()<br/>resetDynamicRoutes()"]
    end

    subgraph Views["views/"]
        MENUVIEW["menus/Index.vue<br/>菜单管理 (树形表格)"]
        MENUFORM["menus/MenuForm.vue<br/>菜单表单弹窗"]
        NOTFOUND["error/NotFound.vue"]
    end

    subgraph Layout["components/"]
        ML["layout/MainLayout.vue<br/>侧边栏动态渲染"]
    end

    MENUAPI --> MENUSTORE
    MENUSTORE --> DYNROUTE
    DYNROUTE --> INDEX
    INDEX --> ML
    MENUAPI --> MENUSTORE --> MENUVIEW --> MENUFORM
```

---

## 五、时序图

### 5.1 登录流程

```mermaid
sequenceDiagram
    actor U as 用户
    participant F as 前端 (Vue)
    participant B as 后端 (Spring)
    participant DB as MySQL

    U->>F: 访问 /
    F->>F: router.beforeEach<br/>isAuthenticated = false
    F-->>U: 302 重定向到 /login

    U->>F: 输入账号密码
    F->>B: POST /auth/login
    B->>DB: 验证密码
    DB-->>B: User
    B-->>F: {accessToken, refreshToken, userInfo}

    F->>F: authStore.login() 存储 token
    F->>F: redirect to /

    Note over F: router.beforeEach 再次触发<br/>isAuthenticated = true<br/>dynamicRoutesLoaded = false

    F->>B: GET /api/menus/tree
    B->>DB: SELECT * FROM menus
    DB-->>B: 扁平数据
    B->>B: buildTree() 构建树
    B-->>F: 菜单树 JSON

    F->>F: router.addRoute() × N
    F->>F: dynamicRoutesLoaded = true
    F->>F: next() → 渲染 Dashboard

    F-->>U: 看到仪表盘
```

### 5.2 页面刷新恢复

```mermaid
sequenceDiagram
    actor U as 用户
    participant F as 前端
    participant B as 后端

    U->>F: 刷新 /users
    F->>F: Pinia 从 localStorage 恢复<br/>accessToken = "eyJ..."

    Note over F: router.beforeEach 触发<br/>isAuthenticated = true<br/>dynamicRoutesLoaded = false

    F->>B: GET /api/menus/tree
    B-->>F: 菜单树 JSON

    F->>F: router.addRoute() 重新注册所有动态路由
    F->>F: next({...to, replace:true})
    F->>F: /users 匹配成功 → 渲染

    F-->>U: 看到用户管理页面
```

### 5.3 新增菜单后路由同步

```mermaid
sequenceDiagram
    actor A as 管理员
    participant F as 前端
    participant B as 后端
    participant DB as MySQL

    A->>F: 在菜单管理页点击"新增菜单"
    F->>B: POST /api/menus<br/>{title, path, component, ...}
    B->>DB: INSERT INTO menus
    DB-->>B: OK
    B-->>F: 200

    F->>F: menuStore.setDynamicRoutesLoaded(false)

    A->>F: 点击新菜单链接
    Note over F: router.beforeEach 触发<br/>dynamicRoutesLoaded = false
    F->>B: GET /api/menus/tree (重新拉取)
    B-->>F: 包含新菜单的树 JSON
    F->>F: router.addRoute() 注册新路由
    F->>F: next() 导航成功

    F-->>A: 看到新页面
```

### 5.4 登出清理

```mermaid
sequenceDiagram
    actor U as 用户
    participant F as 前端
    participant B as 后端

    U->>F: 点击退出登录
    F->>F: 确认弹窗
    F->>B: POST /auth/logout
    F->>F: authStore.clearAuth()
    F->>F: resetDynamicRoutes(router)<br/>移除所有 menuId 标记的路由
    F->>F: dynamicRoutesLoaded = false
    F->>F: router.push('/login')
    F-->>U: 跳转到登录页
```

---

## 六、关键设计决策

| 决策 | 原因 |
|---|---|
| **componentMap 静态映射** | Vite 构建时分析 `import()` 必须静态，不能用运行时字符串拼接 |
| **路由守卫中加载动态路由** | 保证刷新页面后路由能恢复；避免 main.js 初始化时依赖异步 |
| **`dynamicRoutesLoaded` 标记** | 防止每次导航都重复请求菜单树 |
| **侧边栏从 `router.getRoutes()` 读取** | 单一数据源，已注册的路由才是可访问的；与 menuStore 解耦 |
| **排序号 `父*1000 + 子`** | 保证分组间有序，分组内也有序，支持最多 999 个子菜单数量 |
| **菜单 CRUD 后重置 `dynamicRoutesLoaded`** | 下次导航自动重新加载路由，无需手动操作 |
| **根层级不传 group** | 根节点只是结构容器，不应成为侧边栏分组名 |
| **`hidden: true` 标记** | 登录/注册/404 等路由注册但不显示在侧边栏 |
