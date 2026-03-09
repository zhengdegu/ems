-- 初始设备
INSERT OR IGNORE INTO equipment (code, name, type, model, manufacturer, manufacture_date, location, responsible, status, health_score) VALUES
('CNC-001', '五轴数控加工中心', '数控机床', 'DMG MORI DMU 50', 'DMG MORI', '2020-01-15', 'A区-生产线', '张工', 'running', 92),
('CNC-002', '数控车床', '数控机床', 'MAZAK QT-250', 'MAZAK', '2019-06-20', 'A区-生产线', '张工', 'running', 88),
('ROBOT-001', '焊接机器人', '工业机器人', 'FANUC R-2000iC', 'FANUC', '2021-03-10', 'B区-装配线', '李工', 'running', 95),
('ROBOT-005', '搬运机器人', '工业机器人', 'KUKA KR 210', 'KUKA', '2020-08-15', 'C区-仓储', '李工', 'maintenance', 45),
('PLC-003', 'PLC控制器', 'PLC控制器', 'Siemens S7-1500', 'Siemens', '2019-11-01', 'A区-生产线', '赵工', 'stopped', 60),
('CONV-008', '皮带输送机', '输送设备', 'Hytrol TA', 'Hytrol', '2020-05-20', 'C区-仓储', '王工', 'running', 72),
('CNC-003', '龙门加工中心', '数控机床', 'Haas VF-6SS', 'Haas', '2021-01-10', 'A区-生产线', '张工', 'running', 85),
('PUMP-012', '液压泵站', '输送设备', 'Parker PV270', 'Parker', '2019-09-15', 'D区-检测', '王工', 'running', 68);

-- 初始工单
INSERT OR IGNORE INTO work_order (code, title, type, equipment_id, equipment_name, priority, assignee, status, description) VALUES
('WO-20240308-001', 'CNC-001 主轴温度异常维修', '故障维修', 1, 'CNC-001 五轴加工中心', '紧急', '张工', '处理中', '主轴温度持续升高至92°C'),
('WO-20240307-003', 'ROBOT-005 急停故障排查', '故障维修', 4, 'ROBOT-005 搬运机器人', '高', '李工', '待接单', '急停信号触发，设备已停机'),
('WO-20240306-002', '月度设备巡检-A区', '巡检', NULL, '多台设备', '中', '王工', '处理中', '月度例行巡检');

-- 初始告警
INSERT OR IGNORE INTO alarm (equipment_id, equipment_name, level, message, status) VALUES
(1, 'CNC-001 五轴加工中心', '紧急', '主轴温度异常，当前92°C，阈值85°C', '处理中'),
(4, 'ROBOT-005 搬运机器人', '紧急', '急停信号触发，设备已停机', '未处理'),
(5, 'PLC-003 控制器', '重要', '通信中断，持续时间超过5分钟', '处理中'),
(6, 'CONV-008 皮带输送机', '一般', '皮带张力偏低，建议检查', '未处理');

-- 初始角色
INSERT OR IGNORE INTO sys_role (name, code, description) VALUES
('管理员', 'admin', '系统管理员，拥有所有权限'),
('设备主管', 'supervisor', '设备部门主管'),
('维修工程师', 'engineer', '负责设备维修'),
('操作员', 'operator', '设备操作人员');

-- 初始维护计划
INSERT OR IGNORE INTO maintenance_plan (name, type, equipment_id, equipment_name, cycle, next_date, responsible, status) VALUES
('CNC机床月度保养', '定期保养', 1, 'CNC-001 五轴加工中心', '每月', '2024-04-01', '张工', '启用'),
('机器人季度检查', '预防维护', 3, 'ROBOT-001 焊接机器人', '每季度', '2024-06-01', '李工', '启用'),
('输送线周巡检', '巡检', 6, 'CONV-008 皮带输送机', '每周', '2024-03-11', '王工', '启用');
