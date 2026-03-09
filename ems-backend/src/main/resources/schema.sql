-- EMS 设备管理系统 - 数据库建表脚本
-- 数据库: ems

CREATE DATABASE IF NOT EXISTS ems DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE ems;

-- ============================================
-- 1. 用户表
-- ============================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `name` VARCHAR(50) DEFAULT NULL COMMENT '姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
  `role` VARCHAR(20) DEFAULT 'user' COMMENT '角色',
  `enabled` INT DEFAULT 1 COMMENT '是否启用 1-启用 0-禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================
-- 2. 角色表
-- ============================================
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `code` VARCHAR(50) DEFAULT NULL COMMENT '角色编码',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
  `enabled` INT DEFAULT 1 COMMENT '是否启用 1-启用 0-禁用',
  `permissions` TEXT DEFAULT NULL COMMENT '权限列表(JSON)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ============================================
-- 3. 设备表
-- ============================================
DROP TABLE IF EXISTS `equipment`;
CREATE TABLE `equipment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(100) NOT NULL COMMENT '设备名称',
  `code` VARCHAR(50) DEFAULT NULL COMMENT '设备编码',
  `type` VARCHAR(50) DEFAULT NULL COMMENT '设备类型',
  `model` VARCHAR(100) DEFAULT NULL COMMENT '设备型号',
  `manufacturer` VARCHAR(100) DEFAULT NULL COMMENT '制造商',
  `location` VARCHAR(200) DEFAULT NULL COMMENT '安装位置',
  `status` VARCHAR(20) DEFAULT 'normal' COMMENT '状态: normal-正常 warning-预警 fault-故障 offline-离线',
  `image` VARCHAR(255) DEFAULT NULL COMMENT '设备图片',
  `description` TEXT DEFAULT NULL COMMENT '设备描述',
  `install_date` DATETIME DEFAULT NULL COMMENT '安装日期',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备表';

-- ============================================
-- 4. 工单表
-- ============================================
DROP TABLE IF EXISTS `work_order`;
CREATE TABLE `work_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` VARCHAR(50) DEFAULT NULL COMMENT '工单编号',
  `title` VARCHAR(200) NOT NULL COMMENT '工单标题',
  `type` VARCHAR(50) DEFAULT NULL COMMENT '工单类型',
  `priority` VARCHAR(20) DEFAULT 'normal' COMMENT '优先级: low-低 normal-普通 high-高 urgent-紧急',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending-待处理 processing-处理中 completed-已完成 closed-已关闭',
  `equipment_id` BIGINT DEFAULT NULL COMMENT '关联设备ID',
  `equipment_name` VARCHAR(100) DEFAULT NULL COMMENT '设备名称',
  `description` TEXT DEFAULT NULL COMMENT '工单描述',
  `assignee` VARCHAR(50) DEFAULT NULL COMMENT '负责人',
  `creator` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  `deadline` DATETIME DEFAULT NULL COMMENT '截止时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_equipment_id` (`equipment_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单表';

-- ============================================
-- 5. 维护计划表
-- ============================================
DROP TABLE IF EXISTS `maintenance_plan`;
CREATE TABLE `maintenance_plan` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(100) NOT NULL COMMENT '计划名称',
  `type` VARCHAR(50) DEFAULT NULL COMMENT '维护类型',
  `equipment_id` BIGINT DEFAULT NULL COMMENT '关联设备ID',
  `equipment_name` VARCHAR(100) DEFAULT NULL COMMENT '设备名称',
  `cycle` VARCHAR(50) DEFAULT NULL COMMENT '维护周期',
  `description` TEXT DEFAULT NULL COMMENT '维护描述',
  `status` VARCHAR(20) DEFAULT 'enabled' COMMENT '状态: enabled-启用 disabled-禁用',
  `last_time` DATETIME DEFAULT NULL COMMENT '上次维护时间',
  `next_time` DATETIME DEFAULT NULL COMMENT '下次维护时间',
  `assignee` VARCHAR(50) DEFAULT NULL COMMENT '负责人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_equipment_id` (`equipment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='维护计划表';

-- ============================================
-- 6. 告警表
-- ============================================
DROP TABLE IF EXISTS `alarm`;
CREATE TABLE `alarm` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `equipment_id` BIGINT DEFAULT NULL COMMENT '设备ID',
  `equipment_name` VARCHAR(100) DEFAULT NULL COMMENT '设备名称',
  `level` VARCHAR(20) DEFAULT NULL COMMENT '告警级别: info-信息 warning-警告 error-错误 critical-严重',
  `message` TEXT DEFAULT NULL COMMENT '告警消息',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending-待处理 confirmed-已确认 handled-已处理',
  `handler` VARCHAR(50) DEFAULT NULL COMMENT '处理人',
  `confirm_time` DATETIME DEFAULT NULL COMMENT '确认时间',
  `handle_note` VARCHAR(500) DEFAULT NULL COMMENT '处理备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `handle_time` DATETIME DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (`id`),
  KEY `idx_equipment_id` (`equipment_id`),
  KEY `idx_status` (`status`),
  KEY `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警表';

-- ============================================
-- 7. 告警规则表
-- ============================================
DROP TABLE IF EXISTS `alarm_rule`;
CREATE TABLE `alarm_rule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_name` VARCHAR(100) DEFAULT NULL COMMENT '规则名称(旧)',
  `name` VARCHAR(100) DEFAULT NULL COMMENT '规则名称',
  `type` VARCHAR(50) DEFAULT NULL COMMENT '规则类型',
  `equipment_type` VARCHAR(50) DEFAULT NULL COMMENT '适用设备类型',
  `condition_expr` VARCHAR(500) DEFAULT NULL COMMENT '触发条件表达式',
  `level` VARCHAR(20) DEFAULT NULL COMMENT '告警级别',
  `enabled` INT DEFAULT 1 COMMENT '是否启用 1-启用 0-禁用',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警规则表';

-- ============================================
-- 8. 通知表
-- ============================================
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '接收用户ID',
  `title` VARCHAR(200) DEFAULT NULL COMMENT '通知标题',
  `content` TEXT DEFAULT NULL COMMENT '通知内容',
  `type` VARCHAR(50) DEFAULT NULL COMMENT '通知类型',
  `is_read` INT DEFAULT 0 COMMENT '是否已读 0-未读 1-已读',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- ============================================
-- 9. 备件表
-- ============================================
DROP TABLE IF EXISTS `spare_part`;
CREATE TABLE `spare_part` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `part_code` VARCHAR(50) DEFAULT NULL COMMENT '备件编码(旧)',
  `code` VARCHAR(50) DEFAULT NULL COMMENT '备件编码',
  `name` VARCHAR(100) NOT NULL COMMENT '备件名称',
  `category` VARCHAR(50) DEFAULT NULL COMMENT '分类(旧)',
  `type` VARCHAR(50) DEFAULT NULL COMMENT '类型',
  `specification` VARCHAR(200) DEFAULT NULL COMMENT '规格型号',
  `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
  `stock` INT DEFAULT 0 COMMENT '库存数量',
  `min_stock` INT DEFAULT 0 COMMENT '最低库存',
  `location` VARCHAR(200) DEFAULT NULL COMMENT '存放位置',
  `supplier` VARCHAR(100) DEFAULT NULL COMMENT '供应商',
  `price` DECIMAL(10,2) DEFAULT NULL COMMENT '单价',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='备件表';

-- ============================================
-- 10. 备件出入库记录表
-- ============================================
DROP TABLE IF EXISTS `spare_part_record`;
CREATE TABLE `spare_part_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `spare_part_id` BIGINT NOT NULL COMMENT '备件ID',
  `type` VARCHAR(20) NOT NULL COMMENT '类型: inbound-入库 outbound-出库',
  `quantity` INT NOT NULL COMMENT '数量',
  `operator` VARCHAR(50) DEFAULT NULL COMMENT '操作人',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_spare_part_id` (`spare_part_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='备件出入库记录表';

-- ============================================
-- 11. 系统设置表
-- ============================================
DROP TABLE IF EXISTS `system_setting`;
CREATE TABLE `system_setting` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `setting_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `setting_value` TEXT DEFAULT NULL COMMENT '配置值',
  `label` VARCHAR(100) DEFAULT NULL COMMENT '配置标签',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_setting_key` (`setting_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统设置表';

-- ============================================
-- 12. 登录日志表
-- ============================================
DROP TABLE IF EXISTS `login_log`;
CREATE TABLE `login_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
  `username` VARCHAR(50) DEFAULT NULL COMMENT '用户名',
  `ip` VARCHAR(50) DEFAULT NULL COMMENT '登录IP',
  `status` VARCHAR(20) DEFAULT NULL COMMENT '状态: success-成功 fail-失败',
  `message` VARCHAR(255) DEFAULT NULL COMMENT '消息',
  `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '浏览器UA',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- ============================================
-- 13. 操作日志表
-- ============================================
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
  `username` VARCHAR(50) DEFAULT NULL COMMENT '用户名',
  `operator` VARCHAR(50) DEFAULT NULL COMMENT '操作人',
  `module` VARCHAR(50) DEFAULT NULL COMMENT '操作模块',
  `target` VARCHAR(100) DEFAULT NULL COMMENT '操作对象',
  `action` VARCHAR(50) DEFAULT NULL COMMENT '操作类型',
  `detail` TEXT DEFAULT NULL COMMENT '操作详情',
  `ip` VARCHAR(50) DEFAULT NULL COMMENT '操作IP',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_module` (`module`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ============================================
-- 初始数据
-- ============================================

-- 默认管理员账号 (密码: admin123，BCrypt加密)
INSERT INTO `sys_user` (`username`, `password`, `name`, `role`, `enabled`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'admin', 1);

-- 默认角色
INSERT INTO `role` (`name`, `code`, `description`, `enabled`) VALUES
('管理员', 'admin', '系统管理员，拥有所有权限', 1),
('普通用户', 'user', '普通用户，拥有基本操作权限', 1),
('维护人员', 'maintainer', '维护人员，负责设备维护和工单处理', 1);

-- 默认系统设置
INSERT INTO `system_setting` (`setting_key`, `setting_value`, `label`, `description`) VALUES
('system.name', 'EMS设备管理系统', '系统名称', '系统显示名称'),
('system.logo', '/logo.png', '系统Logo', '系统Logo路径'),
('alarm.auto_notify', 'true', '告警自动通知', '告警触发时是否自动发送通知'),
('maintenance.remind_days', '7', '维护提醒天数', '维护计划到期前多少天提醒');
