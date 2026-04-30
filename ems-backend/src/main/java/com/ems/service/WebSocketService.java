package com.ems.service;

import com.ems.entity.Alarm;
import com.ems.entity.Notification;
import com.ems.entity.WorkOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 推送新告警到所有订阅者
     */
    public void pushAlarm(Alarm alarm) {
        messagingTemplate.convertAndSend("/topic/alarm", alarm);
        log.info("WebSocket推送告警: {}", alarm.getMessage());
    }

    /**
     * 推送通知给指定用户
     */
    public void pushNotification(Long userId, Notification notification) {
        messagingTemplate.convertAndSendToUser(
                String.valueOf(userId), "/queue/notification", notification);
        log.info("WebSocket推送通知给用户{}: {}", userId, notification.getTitle());
    }

    /**
     * 推送Dashboard数据更新
     */
    public void pushDashboardUpdate(Map<String, Object> data) {
        messagingTemplate.convertAndSend("/topic/dashboard", data);
    }

    /**
     * 推送工单状态变更
     */
    public void pushWorkOrderUpdate(WorkOrder workOrder) {
        messagingTemplate.convertAndSend("/topic/workorder", workOrder);
    }
}
