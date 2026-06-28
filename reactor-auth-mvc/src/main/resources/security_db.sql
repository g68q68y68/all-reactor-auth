-- ============================================
-- Spring Boot MVC + MyBatis-Plus 数据库
-- ============================================
CREATE DATABASE IF NOT EXISTS `security_db` DEFAULT CHARACTER SET utf8mb4;
USE `security_db`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- --------------------------------------------
-- users
-- --------------------------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` BIGINT NOT NULL COMMENT '雪花ID',
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  `phone` VARCHAR(20) DEFAULT NULL,
  `full_name` VARCHAR(50) DEFAULT NULL,
  `status` TINYINT DEFAULT 1 COMMENT '1启用0禁用',
  `is_account_non_expired` TINYINT DEFAULT 1,
  `is_account_non_locked` TINYINT DEFAULT 1,
  `is_credentials_non_expired` TINYINT DEFAULT 1,
  `is_enabled` TINYINT DEFAULT 1,
  `last_login_time` DATETIME DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` BIGINT DEFAULT NULL,
  `updated_by` BIGINT DEFAULT NULL,
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  `version` INT DEFAULT 0 COMMENT '乐观锁',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------
-- roles
-- --------------------------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
  `id` BIGINT NOT NULL,
  `code` VARCHAR(50) NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `description` VARCHAR(255) DEFAULT NULL,
  `status` TINYINT DEFAULT 1,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` BIGINT DEFAULT NULL,
  `updated_by` BIGINT DEFAULT NULL,
  `deleted` TINYINT DEFAULT 0,
  `version` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------
-- permissions
-- --------------------------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions` (
  `id` BIGINT NOT NULL,
  `code` VARCHAR(100) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `type` VARCHAR(20) DEFAULT 'API' COMMENT 'MENU/BUTTON/API',
  `url` VARCHAR(255) DEFAULT NULL,
  `method` VARCHAR(10) DEFAULT NULL COMMENT 'GET/POST/PUT/DELETE',
  `status` TINYINT DEFAULT 1,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` BIGINT DEFAULT NULL,
  `updated_by` BIGINT DEFAULT NULL,
  `deleted` TINYINT DEFAULT 0,
  `version` INT DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------
-- menus
-- --------------------------------------------
DROP TABLE IF EXISTS `menus`;
CREATE TABLE `menus` (
  `id` BIGINT NOT NULL,
  `parent_id` BIGINT DEFAULT NULL,
  `path` VARCHAR(255) NOT NULL,
  `redirect` VARCHAR(255) DEFAULT NULL,
  `name` VARCHAR(100) NOT NULL,
  `component` VARCHAR(255) DEFAULT NULL,
  `title` VARCHAR(100) NOT NULL,
  `icon` VARCHAR(100) DEFAULT NULL,
  `type` VARCHAR(20) DEFAULT 'MENU',
  `requires_auth` TINYINT DEFAULT 1,
  `hidden` TINYINT DEFAULT 0,
  `sort_order` INT DEFAULT 0,
  `status` TINYINT DEFAULT 1,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` BIGINT DEFAULT NULL,
  `updated_by` BIGINT DEFAULT NULL,
  `deleted` TINYINT DEFAULT 0,
  `version` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------
-- enum_types
-- --------------------------------------------
DROP TABLE IF EXISTS `enum_types`;
CREATE TABLE `enum_types` (
  `id` BIGINT NOT NULL,
  `code` VARCHAR(100) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `description` VARCHAR(255) DEFAULT NULL,
  `status` TINYINT DEFAULT 1,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` BIGINT DEFAULT NULL,
  `updated_by` BIGINT DEFAULT NULL,
  `deleted` TINYINT DEFAULT 0,
  `version` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------
-- enum_values
-- --------------------------------------------
DROP TABLE IF EXISTS `enum_values`;
CREATE TABLE `enum_values` (
  `id` BIGINT NOT NULL,
  `type_id` BIGINT NOT NULL,
  `code` VARCHAR(100) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `sort_order` INT DEFAULT 0,
  `status` TINYINT DEFAULT 1,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` BIGINT DEFAULT NULL,
  `updated_by` BIGINT DEFAULT NULL,
  `deleted` TINYINT DEFAULT 0,
  `version` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_code` (`type_id`, `code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------
-- 关联表
-- --------------------------------------------
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
  `id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `role_permissions`;
CREATE TABLE `role_permissions` (
  `id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  `permission_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `role_menus`;
CREATE TABLE `role_menus` (
  `id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  `menu_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 种子数据
-- ============================================

-- roles
INSERT INTO `roles` (`id`, `code`, `name`, `description`) VALUES
(1, 'ROLE_ADMIN', '管理员', '系统管理员'),
(2, 'ROLE_USER', '普通用户', '普通用户');

-- users (密码都是 123456 的 BCrypt)
INSERT INTO `users` (`id`, `username`, `password`, `email`, `full_name`, `status`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', 'admin@test.com', '管理员', 1),
(2, 'user',  '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', 'user@test.com',  '普通用户', 1);

-- user_roles
INSERT INTO `user_roles` (`id`, `user_id`, `role_id`) VALUES
(1, 1, 1),
(2, 1, 2),
(3, 2, 2);

-- menus
INSERT INTO `menus` (`id`, `parent_id`, `path`, `redirect`, `name`, `component`, `title`, `icon`, `type`, `requires_auth`, `hidden`, `sort_order`, `status`) VALUES
(1,  NULL, '/home', '/dashboard', 'MainLayout', '@/views/layout/MainLayout.vue', '后台管理', NULL, 'MENU', 1, 1, 1, 1),
(11, 1,    'system', NULL, 'system', NULL, '系统操作', 'Menu', 'MENU', 1, 0, 0, 1),
(2,  11,   'dashboard', NULL, 'Dashboard', '@/views/dashboard/Index.vue', '仪表盘', 'DataAnalysis', 'MENU', 1, 0, 1, 1),
(3,  11,   'users', NULL, 'Users', '@/views/users/Index.vue', '用户管理', 'User', 'MENU', 1, 0, 2, 1),
(5,  11,   'roles', NULL, 'Roles', '@/views/roles/Index.vue', '角色管理', 'Avatar', 'MENU', 1, 0, 4, 1),
(6,  11,   'permissions', NULL, 'Permissions', '@/views/permissions/Index.vue', '权限管理', 'Lock', 'MENU', 1, 0, 5, 1),
(7,  11,   'menus', NULL, 'Menus', '@/views/menus/Index.vue', '菜单管理', 'Menu', 'MENU', 1, 0, 6, 1),
(8,  11,   'enums', NULL, 'Enums', '@/views/enums/Index.vue', '枚举管理', 'List', 'MENU', 1, 0, 7, 1),
(10, NULL, '/index', NULL, 'welcome', '@/views/welcome/Index.vue', '欢迎页', NULL, 'MENU', 1, 0, 0, 1);

-- enum_types
INSERT INTO `enum_types` (`id`, `code`, `name`) VALUES (1,'menu_type','菜单类型'),(2,'perm_type','权限类型'),(3,'http_method','请求方法');

-- enum_values
INSERT INTO `enum_values` (`id`, `type_id`, `code`, `name`, `sort_order`) VALUES
(1,1,'MENU','菜单',1),(2,1,'BUTTON','按钮',2),(3,1,'API','接口',3),
(4,2,'MENU','菜单',1),(5,2,'BUTTON','按钮',2),(6,2,'API','接口',3),
(7,3,'GET','GET',1),(8,3,'POST','POST',2),(9,3,'PUT','PUT',3),(10,3,'DELETE','DELETE',4);

-- role_menus（ADMIN 拥有全部菜单）
INSERT INTO `role_menus` (`id`, `role_id`, `menu_id`) SELECT 1, 1, id FROM `menus`;

SET FOREIGN_KEY_CHECKS = 1;
