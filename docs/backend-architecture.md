# 后端开发架构文档

## 一、技术栈

| 技术 | 版本 | 用途 |
|---|---|---|
| Java | 17 | 开发语言 |
| Spring Boot | 3.1.5 | 应用框架 |
| Spring WebFlux | — | 响应式 Web（Reactor） |
| Spring Security | — | 认证与授权 |
| Spring Data R2DBC | — | 响应式数据库访问 |
| r2dbc-mysql | 1.0.5 | MySQL 响应式驱动 |
| Spring Data Redis Reactive | — | Redis 响应式访问（可选，权限缓存） |
| jjwt | 0.12.3 | JWT 令牌 |
| Lombok | — | 样板代码 |

## 二、项目结构

```
src/main/java/com/reactorAuth/
├── App.java                           # 启动类
│
src/main/resources/
├── application.yml                    # 主配置
├── whiteList.properties               # 白名单路径
└── security_db.sql                    # 建库建表 + 种子数据（10 张表，305 行）

src/main/java/com/reactorAuth/
├── config/                            # 配置
│   ├── SecurityConfig.java            # Spring Security 配置（过滤器链、CORS）
│   ├── R2dbcAuditingConfig.java       # R2DBC 审计（@CreatedDate / @LastModifiedDate）
│   ├── WhitelistProperties.java       # 白名单路径（从 whiteList.properties 加载）
│   ├── UrlAuthProperties.java         # URL 权限验证开关
│   └── SpringSecurityAuditorAware.java # 审计获取当前用户（@CreatedBy / @LastModifiedBy）
│
├── security/                          # 安全组件
│   ├── JwtTokenProvider.java          # JWT 生成 / 验证 / 解析
│   ├── JwtAuthenticationFilter.java   # JWT 认证 WebFilter
│   ├── SecurityContextRepository.java # 安全上下文仓库
│   ├── ReactiveUserDetailsServiceImpl.java # 用户信息加载
│   ├── CustomUserDetails.java         # 用户凭证（含 userId / roles / permissions）
│   ├── UrlAuthorizationFilter.java    # URL 权限 WebFilter（条件启用）
│   └── PermissionCacheService.java    # 权限缓存（Redis，条件启用）
│
├── controller/                        # 控制器
│   ├── BaseController.java            # 通用 CRUD（page / findById / create / update / delete）
│   ├── AuthController.java            # /auth/**（登录 / 注册 / 刷新 / 登出）
│   ├── UserController.java            # /api/users
│   ├── RoleController.java            # /api/roles + 角色菜单权限
│   ├── PermissionController.java      # /api/permissions + 缓存刷新
│   ├── MenuController.java            # /api/menus + /tree
│   ├── EnumTypeController.java        # /api/enum-types + /with-values
│   └── EnumValueController.java       # /api/enum-values
│
├── service/                           # 业务层
│   ├── BaseService.java               # 通用 CRUD（分页 / 查询 / 新增 / 更新 / 删除）
│   ├── AuthService.java               # 登录验证 / 注册 / Token 刷新
│   ├── UserService.java               # 用户 CRUD + 密码加密 + 角色关联
│   ├── RoleService.java               # 角色 CRUD
│   ├── PermissionService.java         # 权限 CRUD
│   ├── MenuService.java               # 菜单 CRUD + buildTree / buildTreeForUser
│   ├── RoleMenuService.java           # 角色-菜单中间表
│   ├── EnumTypeService.java           # 枚举类型 CRUD + findAllWithValues
│   └── EnumValueService.java          # 枚举值 CRUD
│
├── entity/                            # 实体（映射表）
│   ├── User.java                      # users
│   ├── Role.java                      # roles
│   ├── Permission.java                # permissions
│   ├── UserRole.java                  # user_roles
│   ├── RolePermission.java            # role_permissions
│   ├── RoleMenu.java                  # role_menus
│   ├── Menu.java                      # menus
│   ├── EnumType.java                  # enum_types
│   └── EnumValue.java                 # enum_values
│
├── repository/                        # 数据访问
│   ├── PageableRepository.java        # 分页接口（count / findPage）
│   ├── UserRepository.java
│   ├── RoleRepository.java
│   ├── PermissionRepository.java
│   ├── RoleMenuRepository.java
│   ├── MenuRepository.java
│   ├── EnumTypeRepository.java
│   └── EnumValueRepository.java
│
├── dto/                               # 数据传输对象
│   ├── Result.java                    # 统一响应 { code, message, data }
│   ├── ResultCode.java                # 响应码枚举（200 / 401 / 40101 / 403 ...）
│   ├── PageResult.java                # 分页结果 { records, total, page, size }
│   ├── LoginRequest.java              # 登录请求
│   ├── LoginResponse.java             # 登录响应（含 token / userInfo）
│   ├── RegisterRequest.java           # 注册请求
│   ├── MenuTreeNode.java              # 菜单树节点（含 children）
│   ├── EnumTypeWithValues.java        # 枚举类型 + 值列表
│   └── PermissionUrlRole.java         # 权限缓存 DTO
│
└── exception/                         # 异常处理
    ├── GlobalExceptionHandler.java     # 全局异常 → Result 转换
    ├── BusinessException.java          # 业务异常
    └── CustomAuthenticationException.java # 认证异常
```

## 三、分层架构

```
HTTP 请求 → WebFilter → Controller → Service → Repository → R2DBC → MySQL
                              ↕
                            Entity
                              ↕
                        DTO / Result
```

### 3.1 Controller 层

- 所有 controller 返回 `Mono<Result<T>>`（响应式）
- `BaseController<T, S>` 提供通用 CRUD 端点：`GET /` / `GET /{id}` / `POST /` / `PUT /{id}` / `DELETE /{id}`
- 具体 controller 继承后只需声明 `@RequestMapping` + 构造注入 Service

### 3.2 Service 层

- `BaseService<T, R>` 提供通用 CRUD 逻辑
- `prepareCreate(entity)` — 新增前设置默认值（子类覆写）
- `mergeEntity(existing, incoming)` — 更新时字段合并（子类必须实现）
- `page(page, size)` — 分页（count + findPage → PageResult）

### 3.3 Repository 层

- 所有 repository 继承 `R2dbcRepository<T, Long>` + `PageableRepository<T>`
- 使用 `@Query` 注解写原生 SQL，`:` 参数绑定
- 分页方法：`count()` / `findPage(limit, offset)`

## 四、认证流程

```
POST /auth/login { username, password }
  → AuthService.login()
    → authenticationManager.authenticate()
      → ReactiveUserDetailsServiceImpl.findByUsername()
        → 查 User + Role + Permission
        → BCrypt 验证密码
    → 生成 accessToken(24h)
    → 更新 last_login_time
    → 返回 LoginResponse { accessToken, userInfo(含 roles/permissions) }

后续请求：
  → JwtAuthenticationFilter（WebFilter）
    → 解析 Authorization: Bearer <token>
    → 验证签名 + 过期
    → 提取 userId / roles → 设 SecurityContext
    → 过期 → HTTP 401 + { code: 40101 }
    → 无效 → HTTP 401 + { code: 40102 }
```

## 五、RBAC 数据模型

```
User ──N:M── UserRole ──N:M── Role ──N:M── RolePermission ──N:M── Permission
                                                  │
Role ──N:M── RoleMenu ──N:M── Menu                  │
                                                      │
                                              授权标识（authority）
                                        角色码 ROLE_ADMIN + 权限码 user:read
                                        合并为 GrantedAuthority，鉴权时统一匹配
```

### 菜单查询（角色过滤）

```
GET /api/menus/tree
  → MenuController.tree()
    → 从 SecurityContext 取 userId + roles
    → ADMIN → 查全部菜单
    → 其他 → users → user_roles → role_menus → menus
    → buildTree（扁平→递归排序）
```

## 六、R2DBC 审计

对标 MyBatis-Plus MetaObjectHandler：

| Spring Data | 注解 | 效果 |
|---|---|---|
| 创建时间 | `@CreatedDate` | insert 时自动填入 |
| 更新时间 | `@LastModifiedDate` | update 时自动填入 |
| 创建人 | `@CreatedBy` | 从 SecurityContext 取 userId |
| 更新人 | `@LastModifiedBy` | 从 SecurityContext 取 userId |

- `R2dbcAuditingConfig` — `@EnableR2dbcAuditing`
- `SpringSecurityAuditorAware` — `ReactiveAuditorAware<Long>` 从 JWT 解析 userId
- 所有 Service 的 `prepareCreate` / `mergeEntity` 不再手写时间戳

## 七、URL 权限验证

**条件启用**：`spring.security.url-auth.enabled=true`

```
请求 → JwtAuthenticationFilter（设 SecurityContext）
     → UrlAuthorizationFilter
         → 从 SecurityContext 取 authorities（角色+权限）
         → 查 Redis 缓存（perm:list → JSON）
         → AntPathMatcher 匹配 URL 模式（如 /api/admin/**）
         → 匹配到规则 → 校验 authority
         → 未匹配到 → 放行（URL 未受限）
```

**Redis 缓存结构**：

```json
[{"method":"GET","url":"/api/users","roles":"ROLE_ADMIN,user:read"},
 {"method":"POST","url":"/api/admin/**","roles":"ROLE_ADMIN"}]
```

**缓存刷新**：`POST /api/permissions/refresh-cache`

## 八、统一响应格式

```json
// Result<T>
{ "code": 200, "message": "操作成功", "data": {...}, "timestamp": 1700000000 }

// PageResult<T>
{ "records": [...], "total": 100, "page": 1, "size": 20, "totalPages": 5 }
```

| 响应码 | 含义 |
|---|---|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未认证 |
| 40101 | Token 已过期 |
| 40102 | Token 无效 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 409 | 冲突 |

## 九、配置说明

```yaml
server.port: 9999

# JWT
spring.security.jwt.secret: xxx
spring.security.jwt.expiration: 86400000        # 24h

# URL 权限验证开关
spring.security.url-auth.enabled: false

# 开启时需要取消 autoconfigure.exclude 注释 + 启动 Redis
spring.data.redis.host: localhost
spring.data.redis.password: 123456
```

## 十、数据库

建库建表脚本：[security_db.sql](../reactor-auth-server/src/main/resources/security_db.sql)（305 行，含种子数据）

```bash
mysql -u root -p < src/main/resources/security_db.sql
```

### 表清单（10 张）

| 表 | 说明 |
|---|---|
| `users` | 用户 |
| `roles` | 角色 |
| `permissions` | 权限 |
| `menus` | 菜单/路由 |
| `enum_types` | 枚举类型 |
| `enum_values` | 枚举值 |
| `user_roles` | 用户-角色关联 |
| `role_permissions` | 角色-权限关联 |
| `role_menus` | 角色-菜单关联 |
| — | 含 `created_by` / `updated_by` / `deleted` 审计字段 |

### 种子数据

- 管理员 `admin / admin123`（ROLE_ADMIN）
- 普通用户 `user / user123`（ROLE_USER）
- 仪表盘、用户管理、角色管理、权限管理、菜单管理、枚举管理 菜单项
- 菜单类型、权限类型、请求方法 枚举值
- ADMIN 角色分配全部菜单权限
```
