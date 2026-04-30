package com.ems.event.listener;

import com.ems.entity.Notification;
import com.ems.entity.WorkOrder;
import com.ems.event.WorkOrderCreatedEvent;
import com.ems.event.WorkOrderStatusChangedEvent;
import com.ems.mapper.NotificationMapper;
import com.ems.service.CacheService;
import com.ems.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WorkOrderEventListener {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired(required = false)
    private WebSocketService webSocketService;

    @Autowired
    private CacheService cacheService;

    @EventListener
    public void onWorkOrderCreated(WorkOrderCreatedEvent event) {
        WorkOrder wo = event.getWorkOrder();

        // 通知执行人
        Notification notification = new Notification();
        notification.setTitle("新工单: " + wo.getTitle());
        notification.setContent("您有一个新的工单需要处理: " + wo.getCode());
        notification.setType("workorder");
        notification.setUserId(1L);
        notification.setIsRead(0);
        notificationMapper.insert(notification);

        if (webSocketService != null) {
            webSocketService.pushWorkOrderUpdate(wo);
        }

        // 清除 Dashboard 缓存
        cacheService.evictDashboard();

        log.info("工单创建事件处理: {}", wo.getCode());
    }

    @EventListener
    public void onWorkOrderStatusChanged(WorkOrderStatusChangedEvent event) {
        WorkOrder wo = event.getWorkOrder();

        // 通知相关人员
        Notification notification = new Notification();
        notification.setTitle("工单状态变更: " + wo.getCode());
        notification.setContent("工单 " + wo.getCode() + " 状态从 " + event.getOldStatus() + " 变为 " + event.getNewStatus());
        notification.setType("workorder");
        notification.setUserId(1L);
        notification.setIsRead(0);
        notificationMapper.insert(notification);

        if (webSocketService != null) {
            webSocketService.pushWorkOrderUpdate(wo);
        }

        // 清除 Dashboard 缓存
        cacheService.evictDashboard();

        log.info("工单状态变更事件: {} -> {}", event.getOldStatus(), event.getNewStatus());
    }
}
