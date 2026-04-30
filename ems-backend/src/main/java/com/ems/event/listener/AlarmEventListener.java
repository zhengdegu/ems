package com.ems.event.listener;

import com.ems.entity.Alarm;
import com.ems.entity.Notification;
import com.ems.entity.WorkOrder;
import com.ems.event.AlarmCreatedEvent;
import com.ems.mapper.NotificationMapper;
import com.ems.mapper.WorkOrderMapper;
import com.ems.service.CacheService;
import com.ems.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AlarmEventListener {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private WorkOrderMapper workOrderMapper;

    @Autowired(required = false)
    private WebSocketService webSocketService;

    @Autowired
    private CacheService cacheService;

    @EventListener
    public void onAlarmCreated(AlarmCreatedEvent event) {
        Alarm alarm = event.getAlarm();

        // 1. 创建通知
        Notification notification = new Notification();
        notification.setTitle("告警通知: " + alarm.getEquipmentName());
        notification.setContent(alarm.getMessage());
        notification.setType("alarm");
        notification.setUserId(1L);
        notification.setIsRead(0);
        notificationMapper.insert(notification);

        // 2. WebSocket 推送
        if (webSocketService != null) {
            webSocketService.pushAlarm(alarm);
            webSocketService.pushNotification(1L, notification);
        }

        // 3. 紧急告警自动创建工单
        if ("紧急".equals(alarm.getLevel())) {
            WorkOrder wo = new WorkOrder();
            wo.setCode("WO-AUTO-" + System.currentTimeMillis());
            wo.setTitle("[自动] " + alarm.getMessage());
            wo.setType("故障维修");
            wo.setEquipmentId(alarm.getEquipmentId());
            wo.setEquipmentName(alarm.getEquipmentName());
            wo.setPriority("紧急");
            wo.setStatus("待接单");
            wo.setCreator("系统自动");
            workOrderMapper.insert(wo);
            log.info("告警事件触发自动创建工单: {}", wo.getTitle());
        }

        // 4. 清除 Dashboard 缓存
        cacheService.evictDashboard();

        log.info("告警事件处理完成: level={}, equipment={}", alarm.getLevel(), alarm.getEquipmentName());
    }
}
