/*
 Navicat Premium Dump SQL

 Source Server         : 本地MySQL
 Source Server Type    : MySQL
 Source Server Version : 80029 (8.0.29)
 Source Host           : localhost:3306
 Source Schema         : security_db

 Target Server Type    : MySQL
 Target Server Version : 80029 (8.0.29)
 File Encoding         : 65001

 Date: 28/06/2026 21:35:11
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for enum_types
-- ----------------------------
DROP TABLE IF EXISTS `enum_types`;
CREATE TABLE `enum_types` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `status` tinyint(1) DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of enum_types
-- ----------------------------
BEGIN;
INSERT INTO `enum_types` (`id`, `code`, `name`, `description`, `status`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES (1, 'menu_type', '菜单类型', '菜单的类型分类', 1, '2026-06-27 13:19:33', '2026-06-27 13:19:33', NULL, NULL);
INSERT INTO `enum_types` (`id`, `code`, `name`, `description`, `status`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES (2, 'perm_type', '权限类型', '权限的类型分类', 1, '2026-06-27 13:19:33', '2026-06-27 13:19:33', NULL, NULL);
INSERT INTO `enum_types` (`id`, `code`, `name`, `description`, `status`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES (3, 'http_method', '请求方法', 'HTTP 请求方法', 1, '2026-06-27 13:19:33', '2026-06-27 13:19:33', NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for enum_values
-- ----------------------------
DROP TABLE IF EXISTS `enum_values`;
CREATE TABLE `enum_values` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type_id` bigint NOT NULL,
  `code` varchar(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `sort_order` int DEFAULT '0',
  `status` tinyint(1) DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_code` (`type_id`,`code`),
  CONSTRAINT `enum_values_ibfk_1` FOREIGN KEY (`type_id`) REFERENCES `enum_types` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of enum_values
-- ----------------------------
BEGIN;
INSERT INTO `enum_values` (`id`, `type_id`, `code`, `name`, `sort_order`, `status`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES (1, 1, 'MENU', '菜单', 1, 1, '2026-06-27 13:19:33', '2026-06-27 13:19:33', NULL, NULL);
INSERT INTO `enum_values` (`id`, `type_id`, `code`, `name`, `sort_order`, `status`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES (2, 1, 'BUTTON', '按钮', 2, 1, '2026-06-27 13:19:33', '2026-06-27 13:19:33', NULL, NULL);
INSERT INTO `enum_values` (`id`, `type_id`, `code`, `name`, `sort_order`, `status`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES (3, 1, 'API', '接口', 3, 1, '2026-06-27 13:19:33', '2026-06-27 13:19:33', NULL, NULL);
INSERT INTO `enum_values` (`id`, `type_id`, `code`, `name`, `sort_order`, `status`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES (4, 2, 'MENU', '菜单', 1, 1, '2026-06-27 13:19:33', '2026-06-27 13:19:33', NULL, NULL);
INSERT INTO `enum_values` (`id`, `type_id`, `code`, `name`, `sort_order`, `status`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES (5, 2, 'BUTTON', '按钮', 2, 1, '2026-06-27 13:19:33', '2026-06-27 13:19:33', NULL, NULL);
INSERT INTO `enum_values` (`id`, `type_id`, `code`, `name`, `sort_order`, `status`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES (6, 2, 'API', '接口', 3, 1, '2026-06-27 13:19:33', '2026-06-27 13:19:33', NULL, NULL);
INSERT INTO `enum_values` (`id`, `type_id`, `code`, `name`, `sort_order`, `status`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES (7, 3, 'GET', 'GET', 1, 1, '2026-06-27 13:19:33', '2026-06-27 13:19:33', NULL, NULL);
INSERT INTO `enum_values` (`id`, `type_id`, `code`, `name`, `sort_order`, `status`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES (8, 3, 'POST', 'POST', 2, 1, '2026-06-27 13:19:33', '2026-06-27 13:19:33', NULL, NULL);
INSERT INTO `enum_values` (`id`, `type_id`, `code`, `name`, `sort_order`, `status`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES (9, 3, 'PUT', 'PUT', 3, 1, '2026-06-27 13:19:33', '2026-06-27 13:19:33', NULL, NULL);
INSERT INTO `enum_values` (`id`, `type_id`, `code`, `name`, `sort_order`, `status`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES (10, 3, 'DELETE', 'DELETE', 4, 1, '2026-06-27 13:19:33', '2026-06-27 13:19:33', NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for menus
-- ----------------------------
DROP TABLE IF EXISTS `menus`;
CREATE TABLE `menus` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint DEFAULT NULL,
  `path` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `component` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `title` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `icon` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'MENU',
  `requires_auth` tinyint(1) NOT NULL DEFAULT '1',
  `sort_order` int NOT NULL DEFAULT '0',
  `status` tinyint(1) NOT NULL DEFAULT '1',
  `hidden` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `redirect` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  CONSTRAINT `fk_menu_parent` FOREIGN KEY (`parent_id`) REFERENCES `menus` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of menus
-- ----------------------------
BEGIN;
INSERT INTO `menus` (`id`, `parent_id`, `path`, `name`, `component`, `title`, `icon`, `type`, `requires_auth`, `sort_order`, `status`, `hidden`, `created_at`, `updated_at`, `created_by`, `updated_by`, `redirect`) VALUES (1, NULL, '/home', 'MainLayout', '@/views/layout/MainLayout.vue', '后台管理', '', 'MENU', 1, 1, 1, 1, '2026-06-27 12:43:08', '2026-06-28 19:44:47', NULL, NULL, '');
INSERT INTO `menus` (`id`, `parent_id`, `path`, `name`, `component`, `title`, `icon`, `type`, `requires_auth`, `sort_order`, `status`, `hidden`, `created_at`, `updated_at`, `created_by`, `updated_by`, `redirect`) VALUES (2, 11, 'dashboard', 'Dashboard', '@/views/dashboard/Index.vue', '仪表盘', 'DataAnalysis', 'MENU', 1, 1, 1, 0, '2026-06-27 12:43:08', '2026-06-28 19:35:01', NULL, NULL, NULL);
INSERT INTO `menus` (`id`, `parent_id`, `path`, `name`, `component`, `title`, `icon`, `type`, `requires_auth`, `sort_order`, `status`, `hidden`, `created_at`, `updated_at`, `created_by`, `updated_by`, `redirect`) VALUES (3, 11, 'users', 'Users', '@/views/users/Index.vue', '用户管理', 'User', 'MENU', 1, 2, 1, 0, '2026-06-27 12:43:08', '2026-06-28 19:35:01', NULL, NULL, NULL);
INSERT INTO `menus` (`id`, `parent_id`, `path`, `name`, `component`, `title`, `icon`, `type`, `requires_auth`, `sort_order`, `status`, `hidden`, `created_at`, `updated_at`, `created_by`, `updated_by`, `redirect`) VALUES (5, 11, 'roles', 'Roles', '@/views/roles/Index.vue', '角色管理', 'Avatar', 'MENU', 1, 4, 1, 0, '2026-06-27 12:43:08', '2026-06-28 19:35:01', NULL, NULL, NULL);
INSERT INTO `menus` (`id`, `parent_id`, `path`, `name`, `component`, `title`, `icon`, `type`, `requires_auth`, `sort_order`, `status`, `hidden`, `created_at`, `updated_at`, `created_by`, `updated_by`, `redirect`) VALUES (6, 11, 'permissions', 'Permissions', '@/views/permissions/Index.vue', '权限管理', 'Lock', 'MENU', 1, 5, 1, 0, '2026-06-27 12:43:08', '2026-06-28 19:35:01', NULL, NULL, NULL);
INSERT INTO `menus` (`id`, `parent_id`, `path`, `name`, `component`, `title`, `icon`, `type`, `requires_auth`, `sort_order`, `status`, `hidden`, `created_at`, `updated_at`, `created_by`, `updated_by`, `redirect`) VALUES (7, 11, 'menus', 'Menus', '@/views/menus/Index.vue', '菜单管理', 'Menu', 'MENU', 1, 6, 1, 0, '2026-06-27 12:43:08', '2026-06-28 19:35:01', NULL, NULL, NULL);
INSERT INTO `menus` (`id`, `parent_id`, `path`, `name`, `component`, `title`, `icon`, `type`, `requires_auth`, `sort_order`, `status`, `hidden`, `created_at`, `updated_at`, `created_by`, `updated_by`, `redirect`) VALUES (8, 11, 'enums', 'Enums', '@/views/enums/Index.vue', '枚举管理', 'List', 'MENU', 1, 7, 1, 0, '2026-06-27 13:29:59', '2026-06-28 19:35:01', NULL, NULL, '');
INSERT INTO `menus` (`id`, `parent_id`, `path`, `name`, `component`, `title`, `icon`, `type`, `requires_auth`, `sort_order`, `status`, `hidden`, `created_at`, `updated_at`, `created_by`, `updated_by`, `redirect`) VALUES (10, NULL, '/index', 'welcome', '@/views/welcome/Index.vue', '欢迎页', '', 'MENU', 0, 0, 1, 0, '2026-06-28 18:09:16', '2026-06-28 19:48:16', NULL, NULL, '');
INSERT INTO `menus` (`id`, `parent_id`, `path`, `name`, `component`, `title`, `icon`, `type`, `requires_auth`, `sort_order`, `status`, `hidden`, `created_at`, `updated_at`, `created_by`, `updated_by`, `redirect`) VALUES (11, 1, 'system', 'system', '', '系统操作', 'Menu', 'MENU', 1, 0, 1, 0, '2026-06-28 19:33:03', '2026-06-28 19:35:45', NULL, NULL, '');
COMMIT;

-- ----------------------------
-- Table structure for permissions
-- ----------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(100) NOT NULL COMMENT '权限编码',
  `name` varchar(100) NOT NULL COMMENT '权限名称',
  `type` varchar(20) DEFAULT NULL COMMENT '类型: MENU,BUTTON,API',
  `url` varchar(200) DEFAULT NULL COMMENT 'URL',
  `method` varchar(10) DEFAULT NULL COMMENT '请求方法',
  `status` tinyint DEFAULT '1' COMMENT '状态',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  KEY `idx_code` (`code`),
  KEY `idx_url_method` (`url`,`method`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限表';

-- ----------------------------
-- Records of permissions
-- ----------------------------
BEGIN;
INSERT INTO `permissions` (`id`, `code`, `name`, `type`, `url`, `method`, `status`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES (1, 'user:read', '查看用户', 'API', '/api/users/**', 'GET', 1, '2026-06-26 23:34:20', '2026-06-26 23:34:20', NULL, NULL);
INSERT INTO `permissions` (`id`, `code`, `name`, `type`, `url`, `method`, `status`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES (2, 'user:write', '操作用户', 'API', '/api/users/**', 'POST', 1, '2026-06-26 23:34:20', '2026-06-26 23:34:20', NULL, NULL);
INSERT INTO `permissions` (`id`, `code`, `name`, `type`, `url`, `method`, `status`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES (3, 'user:update', '更新用户', 'API', '/api/users/**', 'PUT', 1, '2026-06-26 23:34:20', '2026-06-26 23:34:20', NULL, NULL);
INSERT INTO `permissions` (`id`, `code`, `name`, `type`, `url`, `method`, `status`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES (4, 'user:delete', '删除用户', 'API', '/api/users/**', 'DELETE', 1, '2026-06-26 23:34:20', '2026-06-26 23:34:20', NULL, NULL);
INSERT INTO `permissions` (`id`, `code`, `name`, `type`, `url`, `method`, `status`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES (5, 'admin:access', '管理访问', 'API', '/api/admin/**', 'ALL', 1, '2026-06-26 23:34:20', '2026-06-26 23:34:20', NULL, NULL);
INSERT INTO `permissions` (`id`, `code`, `name`, `type`, `url`, `method`, `status`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES (6, 'demo:read', '查看Demo', 'API', '/api/demo/**', 'GET', 1, '2026-06-26 23:34:20', '2026-06-26 23:34:20', NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for role_menus
-- ----------------------------
DROP TABLE IF EXISTS `role_menus`;
CREATE TABLE `role_menus` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL,
  `menu_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`,`menu_id`),
  KEY `menu_id` (`menu_id`),
  CONSTRAINT `role_menus_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `role_menus_ibfk_2` FOREIGN KEY (`menu_id`) REFERENCES `menus` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of role_menus
-- ----------------------------
BEGIN;
INSERT INTO `role_menus` (`id`, `role_id`, `menu_id`) VALUES (1, 1, 1);
INSERT INTO `role_menus` (`id`, `role_id`, `menu_id`) VALUES (2, 1, 2);
INSERT INTO `role_menus` (`id`, `role_id`, `menu_id`) VALUES (3, 1, 3);
INSERT INTO `role_menus` (`id`, `role_id`, `menu_id`) VALUES (5, 1, 5);
INSERT INTO `role_menus` (`id`, `role_id`, `menu_id`) VALUES (6, 1, 6);
INSERT INTO `role_menus` (`id`, `role_id`, `menu_id`) VALUES (7, 1, 7);
INSERT INTO `role_menus` (`id`, `role_id`, `menu_id`) VALUES (8, 1, 8);
INSERT INTO `role_menus` (`id`, `role_id`, `menu_id`) VALUES (17, 2, 1);
INSERT INTO `role_menus` (`id`, `role_id`, `menu_id`) VALUES (16, 2, 2);
COMMIT;

-- ----------------------------
-- Table structure for role_permissions
-- ----------------------------
DROP TABLE IF EXISTS `role_permissions`;
CREATE TABLE `role_permissions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`),
  KEY `permission_id` (`permission_id`),
  CONSTRAINT `role_permissions_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `role_permissions_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限关联表';

-- ----------------------------
-- Records of role_permissions
-- ----------------------------
BEGIN;
INSERT INTO `role_permissions` (`id`, `role_id`, `permission_id`) VALUES (4, 1, 1);
INSERT INTO `role_permissions` (`id`, `role_id`, `permission_id`) VALUES (6, 1, 2);
INSERT INTO `role_permissions` (`id`, `role_id`, `permission_id`) VALUES (5, 1, 3);
INSERT INTO `role_permissions` (`id`, `role_id`, `permission_id`) VALUES (3, 1, 4);
INSERT INTO `role_permissions` (`id`, `role_id`, `permission_id`) VALUES (1, 1, 5);
INSERT INTO `role_permissions` (`id`, `role_id`, `permission_id`) VALUES (2, 1, 6);
INSERT INTO `role_permissions` (`id`, `role_id`, `permission_id`) VALUES (9, 2, 1);
INSERT INTO `role_permissions` (`id`, `role_id`, `permission_id`) VALUES (8, 2, 6);
COMMIT;

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL COMMENT '角色编码',
  `name` varchar(50) NOT NULL COMMENT '角色名称',
  `description` varchar(200) DEFAULT NULL COMMENT '角色描述',
  `status` tinyint DEFAULT '1' COMMENT '状态',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  KEY `idx_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';

-- ----------------------------
-- Records of roles
-- ----------------------------
BEGIN;
INSERT INTO `roles` (`id`, `code`, `name`, `description`, `status`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES (1, 'ROLE_ADMIN', '管理员', '系统管理员', 1, '2026-06-26 23:34:20', '2026-06-26 23:34:20', NULL, NULL);
INSERT INTO `roles` (`id`, `code`, `name`, `description`, `status`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES (2, 'ROLE_USER', '普通用户', '普通用户', 1, '2026-06-26 23:34:20', '2026-06-26 23:34:20', NULL, NULL);
INSERT INTO `roles` (`id`, `code`, `name`, `description`, `status`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES (3, 'ROLE_GUEST', '访客', '访客用户', 1, '2026-06-26 23:34:20', '2026-06-26 23:34:20', NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for user_roles
-- ----------------------------
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `user_roles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联表';

-- ----------------------------
-- Records of user_roles
-- ----------------------------
BEGIN;
INSERT INTO `user_roles` (`id`, `user_id`, `role_id`) VALUES (1, 1, 1);
INSERT INTO `user_roles` (`id`, `user_id`, `role_id`) VALUES (2, 2, 2);
COMMIT;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `full_name` varchar(100) DEFAULT NULL COMMENT '真实姓名',
  `status` tinyint DEFAULT '1' COMMENT '状态 1:启用 0:禁用',
  `is_account_non_expired` tinyint(1) DEFAULT '1' COMMENT '账号是否未过期',
  `is_account_non_locked` tinyint(1) DEFAULT '1' COMMENT '账号是否未锁定',
  `is_credentials_non_expired` tinyint(1) DEFAULT '1' COMMENT '凭证是否未过期',
  `is_enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `idx_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- ----------------------------
-- Records of users
-- ----------------------------
BEGIN;
INSERT INTO `users` (`id`, `username`, `password`, `email`, `phone`, `full_name`, `status`, `is_account_non_expired`, `is_account_non_locked`, `is_credentials_non_expired`, `is_enabled`, `last_login_time`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES (1, 'admin', '$2a$10$s2hRuzEDZ1LNBCoRymq/X.0FWF2q/rKzx6dSZEfRUCXTe4hHjP2Q.', 'admin@example.com', NULL, '管理员', 1, 1, 1, 1, 1, '2026-06-28 19:23:45', '2026-06-26 23:34:20', '2026-06-28 19:23:45', NULL, NULL);
INSERT INTO `users` (`id`, `username`, `password`, `email`, `phone`, `full_name`, `status`, `is_account_non_expired`, `is_account_non_locked`, `is_credentials_non_expired`, `is_enabled`, `last_login_time`, `created_at`, `updated_at`, `created_by`, `updated_by`) VALUES (2, 'user', '$2a$10$Rz5MMut/QlCMxpsrWIN0N.KENKpIzRkE8Kd3tGKqMw96sfeAumZWO', 'user@example.com', NULL, '普通用户', 1, 1, 1, 1, 1, '2026-06-27 14:21:09', '2026-06-26 23:34:20', '2026-06-27 14:21:09', NULL, NULL);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
