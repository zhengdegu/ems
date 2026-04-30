-- EMS 设备管理系统 - 数据库建表脚本 (MySQL)

CREATE DATABASE IF NOT EXISTS ems DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE ems;

-- ============================================
-- 1. 用户表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL COMMENT '用户名',
  password VARCHAR(255) NOT NULL COMMENT '密码',
  name VARCHAR(50) DEFAULT NULL COMMENT '姓名',
  phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  department VARCHAR(50) DEFAULT NULL COMMENT '部门',
  employee_no VARCHAR(50) DEFAULT NULL COMMENT '工号',
  avatar VARCHAR(255) DEFAULT NULL COMMENT '头像',
  role VARCHAR(20) DEFAULT 'user' COMMENT '角色',
  enabled TINYINT DEFAULT 1 COMMENT '是否启用',
  lock_time DATETIME DEFAULT NULL COMMENT '锁定时间',
  login_fail_count INT DEFAULT 0 COMMENT '登录失败次数',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================
-- 2. 角色表
-- ============================================
CREATE TABLE IF NOT EXISTS role (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL COMMENT '角色名称',
  code VARCHAR(50) DEFAULT NULL COMMENT '角色编码',
  description VARCHAR(255) DEFAULT NULL COMMENT '描述',
  enabled TINYINT DEFAULT 1 COMMENT '是否启用',
  permissions TEXT DEFAULT NULL COMMENT '权限JSON',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ============================================
-- 3. 设备表
-- ============================================
CREATE TABLE IF NOT EXISTS equipment (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL COMMENT '设备名称',
  code VARCHAR(50) DEFAULT NULL COMMENT '设备编号',
  type VARCHAR(50) DEFAULT NULL COMMENT '设备类型',
  model VARCHAR(100) DEFAULT NULL COMMENT '设备型号',
  manufacturer VARCHAR(100) DEFAULT NULL COMMENT '制造商',
  location VARCHAR(200) DEFAULT NULL COMMENT '安装位置',
  status VARCHAR(20) DEFAULT 'normal' COMMENT '状态',
  image VARCHAR(255) DEFAULT NULL COMMENT '设备图片',
  description TEXT DEFAULT NULL COMMENT '描述',
  install_date DATETIME DEFAULT NULL COMMENT '安装日期',
  responsible VARCHAR(50) DEFAULT NULL COMMENT '负责人',
  health_score INT DEFAULT NULL COMMENT '健康评分',
  manufacture_date DATE DEFAULT NULL COMMENT '出厂日期',
  purchase_date DATE DEFAULT NULL COMMENT '购入日期',
  price DECIMAL(10,2) DEFAULT NULL COMMENT '资产原值',
  remark TEXT DEFAULT NULL COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备表';

-- ============================================
-- 4. 工单表
-- ============================================
CREATE TABLE IF NOT EXISTS work_order (
  id BIGINT NOT NULL AUTO_INCREMENT,
  code VARCHAR(50) DEFAULT NULL COMMENT '工单编号',
  title VARCHAR(200) NOT NULL COMMENT '工单标题',
  type VARCHAR(50) DEFAULT NULL COMMENT '工单类型',
  priority VARCHAR(20) DEFAULT 'normal' COMMENT '优先级',
  status VARCHAR(20) DEFAULT 'pending' COMMENT '状态',
  equipment_id BIGINT DEFAULT NULL COMMENT '关联设备ID',
  equipment_name VARCHAR(100) DEFAULT NULL COMMENT '关联设备名称',
  description TEXT DEFAULT NULL COMMENT '描述',
  assignee VARCHAR(50) DEFAULT NULL COMMENT '执行人',
  creator VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  deadline DATETIME DEFAULT NULL COMMENT '截止时间',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_equipment_id (equipment_id),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单表';

-- ============================================
-- 5. 维护计划表
-- ============================================
CREATE TABLE IF NOT EXISTS maintenance_plan (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL COMMENT '计划名称',
  type VARCHAR(50) DEFAULT NULL COMMENT '计划类型',
  equipment_id BIGINT DEFAULT NULL COMMENT '关联设备ID',
  equipment_name VARCHAR(100) DEFAULT NULL COMMENT '关联设备名称',
  cycle VARCHAR(50) DEFAULT NULL COMMENT '执行周期',
  description TEXT DEFAULT NULL COMMENT '描述',
  status VARCHAR(20) DEFAULT 'enabled' COMMENT '状态',
  last_time DATETIME DEFAULT NULL COMMENT '上次执行时间',
  next_time DATETIME DEFAULT NULL COMMENT '下次执行时间',
  next_date DATE DEFAULT NULL COMMENT '下次执行日期',
  assignee VARCHAR(50) DEFAULT NULL COMMENT '执行人',
  responsible VARCHAR(50) DEFAULT NULL COMMENT '负责人',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_equipment_id (equipment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='维护计划表';

-- ============================================
-- 6. 告警表
-- ============================================
CREATE TABLE IF NOT EXISTS alarm (
  id BIGINT NOT NULL AUTO_INCREMENT,
  equipment_id BIGINT DEFAULT NULL COMMENT '设备ID',
  equipment_name VARCHAR(100) DEFAULT NULL COMMENT '设备名称',
  level VARCHAR(20) DEFAULT NULL COMMENT '告警级别',
  message TEXT DEFAULT NULL COMMENT '告警信息',
  status VARCHAR(20) DEFAULT 'pending' COMMENT '状态',
  handler VARCHAR(50) DEFAULT NULL COMMENT '处理人',
  confirm_time DATETIME DEFAULT NULL COMMENT '确认时间',
  handle_note VARCHAR(500) DEFAULT NULL COMMENT '处理备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  handle_time DATETIME DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (id),
  KEY idx_equipment_id (equipment_id),
  KEY idx_status (status),
  KEY idx_level (level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警表';

-- ============================================
-- 7. 告警规则表
-- ============================================
CREATE TABLE IF NOT EXISTS alarm_rule (
  id BIGINT NOT NULL AUTO_INCREMENT,
  rule_name VARCHAR(100) DEFAULT NULL COMMENT '规则名称',
  name VARCHAR(100) DEFAULT NULL COMMENT '名称',
  type VARCHAR(50) DEFAULT NULL COMMENT '类型',
  equipment_type VARCHAR(50) DEFAULT NULL COMMENT '设备类型',
  condition_expr VARCHAR(500) DEFAULT NULL COMMENT '条件表达式',
  level VARCHAR(20) DEFAULT NULL COMMENT '告警级别',
  enabled TINYINT DEFAULT 1 COMMENT '是否启用',
  description VARCHAR(500) DEFAULT NULL COMMENT '描述',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警规则表';

-- ============================================
-- 8. 通知表
-- ============================================
CREATE TABLE IF NOT EXISTS notification (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT DEFAULT NULL COMMENT '用户ID',
  title VARCHAR(200) DEFAULT NULL COMMENT '标题',
  content TEXT DEFAULT NULL COMMENT '内容',
  type VARCHAR(50) DEFAULT NULL COMMENT '类型',
  is_read TINYINT DEFAULT 0 COMMENT '是否已读',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_user_id (user_id),
  KEY idx_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- ============================================
-- 9. 备件表
-- ============================================
CREATE TABLE IF NOT EXISTS spare_part (
  id BIGINT NOT NULL AUTO_INCREMENT,
  part_code VARCHAR(50) DEFAULT NULL COMMENT '备件编码',
  code VARCHAR(50) DEFAULT NULL COMMENT '编码',
  name VARCHAR(100) NOT NULL COMMENT '备件名称',
  category VARCHAR(50) DEFAULT NULL COMMENT '分类',
  type VARCHAR(50) DEFAULT NULL COMMENT '类型',
  specification VARCHAR(200) DEFAULT NULL COMMENT '规格',
  unit VARCHAR(20) DEFAULT NULL COMMENT '单位',
  stock INT DEFAULT 0 COMMENT '库存数量',
  min_stock INT DEFAULT 0 COMMENT '最低库存',
  location VARCHAR(200) DEFAULT NULL COMMENT '存放位置',
  supplier VARCHAR(100) DEFAULT NULL COMMENT '供应商',
  price DECIMAL(10,2) DEFAULT NULL COMMENT '单价',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='备件表';

-- ============================================
-- 10. 备件出入库记录表
-- ============================================
CREATE TABLE IF NOT EXISTS spare_part_record (
  id BIGINT NOT NULL AUTO_INCREMENT,
  spare_part_id BIGINT NOT NULL COMMENT '备件ID',
  type VARCHAR(20) NOT NULL COMMENT '类型(入库/出库)',
  quantity INT NOT NULL COMMENT '数量',
  operator VARCHAR(50) DEFAULT NULL COMMENT '操作人',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_spare_part_id (spare_part_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='备件出入库记录表';

-- ============================================
-- 11. 系统设置表
-- ============================================
CREATE TABLE IF NOT EXISTS system_setting (
  id BIGINT NOT NULL AUTO_INCREMENT,
  setting_key VARCHAR(100) NOT NULL COMMENT '设置键',
  setting_value TEXT DEFAULT NULL COMMENT '设置值',
  label VARCHAR(100) DEFAULT NULL COMMENT '标签',
  description VARCHAR(255) DEFAULT NULL COMMENT '描述',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_setting_key (setting_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统设置表';

-- ============================================
-- 12. 登录日志表
-- ================================
CREATE TABLE IF NOT EXISTS login_log (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT DEFAULT NULL COMMENT '用户ID',
  username VARCHAR(50) DEFAULT NULL COMMENT '用户名',
  ip VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
  status VARCHAR(20) DEFAULT NULL COMMENT '状态',
  message VARCHAR(255) DEFAULT NULL COMMENT '消息',
  user_agent VARCHAR(500) DEFAULT NULL COMMENT 'User-Agent',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_user_id (user_id),
  KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- ============================================
-- 13. 操作日志表
-- ============================================
CREATE TABLE IF NOT EXISTS operation_log (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT DEFAULT NULL COMMENT '用户ID',
  username VARCHAR(50) DEFAULT NULL COMMENT '用户名',
  operator VARCHAR(50) DEFAULT NULL COMMENT '操作人',
  module VARCHAR(50) DEFAULT NULL COMMENT '模块',
  target VARCHAR(100) DEFAULT NULL COMMENT '操作对象',
  action VARCHAR(50) DEFAULT NULL COMMENT '操作类型',
  detail TEXT DEFAULT NULL COMMENT '详情',
  ip VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_user_id (user_id),
  KEY idx_module (module),
  KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ============================================
-- 14. 附件表
-- ============================================
CREATE TABLE IF NOT EXISTS attachment (
  id BIGINT NOT NULL AUTO_INCREMENT,
  file_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
  file_path VARCHAR(500) NOT NULL COMMENT '存储路径',
  file_type VARCHAR(100) DEFAULT NULL COMMENT 'MIME类型',
  file_size BIGINT DEFAULT NULL COMMENT '文件大小',
  biz_type VARCHAR(50) DEFAULT NULL COMMENT '业务类型',
  biz_id BIGINT DEFAULT NULL COMMENT '业务ID',
  uploader VARCHAR(50) DEFAULT NULL COMMENT '上传人',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_biz (biz_type, biz_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='附件表';
