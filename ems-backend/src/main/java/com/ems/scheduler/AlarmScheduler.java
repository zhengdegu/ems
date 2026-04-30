package com.ems.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ems.entity.*;
import com.ems.mapper.*;
import com.ems.service.AlarmRuleService;
import com.ems.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class AlarmScheduler {

    @Autowired
    private AlarmRuleService alarmRuleService;
    @Autowired
    private EquipmentMapper equipmentMapper;
    @Autowired
    private AlarmMapper alarmMapper;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private WorkOrderMapper workOrderMapper;
    @Autowired(required = false)
    private WebSocketService webSocketService;

    /**
     * 每分钟扫描告警规则，模拟设备数据检查
     * 实际生产中这里会读取设备上报的实时数据
     * 当前实现：随机模拟设备参数，匹配规则后自动创建告警
     */
    @Scheduled(fixedRate = 60000)
    public void checkAlarmRules() {
        log.info("=== 告警规则扫描开始 ===");

        List<AlarmRule> rules = alarmRuleService.list(
                new LambdaQueryWrapper<AlarmRule>().eq(AlarmRule::getEnabled, 1)
        );

        for (AlarmRule rule : rules) {
            // 模拟：5%概率触发告警（实际应该读取设备实时数据）
            if (Math.random() < 0.05) {
                // 随机选一个对应类型的设备
                LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<Equipment>()
                        .eq(Equipment::getStatus, "running");
                if (rule.getEquipmentType() != null && !rule.getEquipmentType().isEmpty()) {
                    wrapper.eq(Equipment::getType, rule.getEquipmentType());
                }
                List<Equipment> equipments = equipmentMapper.selectList(wrapper);
                if (equipments.isEmpty()) continue;
                Equipment eq = equipments.get((int) (Math.random() * equipments.size()));

                // 创建告警
                Alarm alarm = new Alarm();
                alarm.setEquipmentId(eq.getId());
                alarm.setEquipmentName(eq.getCode() + " " + eq.getName());
                alarm.setLevel(rule.getLevel());
                String ruleName = rule.getRuleName() != null ? rule.getRuleName() : rule.getName();
                alarm.setMessage("自动检测: " + ruleName + " - " + eq.getName());
                alarm.setStatus("未处理");
                alarmMapper.insert(alarm);

                // 创建通知
                Notification notification = new Notification();
                notification.setTitle("告警通知: " + eq.getName());
                notification.setContent(alarm.getMessage());
                notification.setType("alarm");
                notification.setUserId(1L);
                notification.setIsRead(0);
                notificationMapper.insert(notification);

                // WebSocket 推送告警
                if (webSocketService != null) {
                    webSocketService.pushAlarm(alarm);
                    webSocketService.pushNotification(1L, notification);
                }

                // 紧急告警自动创建工单
                if ("紧急".equals(rule.getLevel())) {
                    WorkOrder wo = new WorkOrder();
                    wo.setCode("WO-AUTO-" + System.currentTimeMillis());
                    wo.setTitle("[自动] " + alarm.getMessage());
                    wo.setType("故障维修");
                    wo.setEquipmentId(eq.getId());
                    wo.setEquipmentName(eq.getCode() + " " + eq.getName());
                    wo.setPriority("紧急");
                    wo.setStatus("待接单");
                    wo.setAssignee(eq.getResponsible());
                    workOrderMapper.insert(wo);
                    log.info("自动创建紧急工单: {}", wo.getTitle());

                    if (webSocketService != null) {
                        webSocketService.pushWorkOrderUpdate(wo);
                    }
                }

                log.info("触发告警: {} - {}", rule.getLevel(), alarm.getMessage());
            }
        }
        log.info("=== 告警规则扫描完成 ===");
    }
}
