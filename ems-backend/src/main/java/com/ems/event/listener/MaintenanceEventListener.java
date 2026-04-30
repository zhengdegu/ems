package com.ems.event.listener;

import com.ems.entity.MaintenancePlan;
import com.ems.entity.Notification;
import com.ems.entity.WorkOrder;
import com.ems.event.MaintenancePlanDueEvent;
import com.ems.mapper.NotificationMapper;
import com.ems.mapper.WorkOrderMapper;
import com.ems.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MaintenanceEventListener {

    @Autowired
    private WorkOrderMapper workOrderMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private CacheService cacheService;

    @EventListener
    public void onMaintenancePlanDue(MaintenancePlanDueEvent event) {
        MaintenancePlan plan = event.getPlan();

        // 自动创建工单
        WorkOrder wo = new WorkOrder();
        wo.setCode("WO-MP-" + System.currentTimeMillis());
        wo.setTitle("[维护计划] " + plan.getName());
        wo.setType(plan.getType());
        wo.setEquipmentId(plan.getEquipmentId());
        wo.setEquipmentName(plan.getEquipmentName());
        wo.setPriority("中");
        wo.setStatus("待接单");
        wo.setSourcePlanId(plan.getId());
        wo.setAssignee(plan.getResponsible());
        wo.setCreator("系统自动");
        wo.setDescription("维护计划自动生成: " + plan.getName() + "，周期: " + plan.getCycle());
        workOrderMapper.insert(wo);

        // 创建通知
        Notification notification = new Notification();
        notification.setTitle("维护计划提醒: " + plan.getName());
        notification.setContent("维护计划即将到期，已自动生成工单 " + wo.getCode());
        notification.setType("system");
        notification.setUserId(1L);
        notification.setIsRead(0);
        notificationMapper.insert(notification);

        // 清除 Dashboard 缓存
        cacheService.evictDashboard();

        log.info("维护计划到期事件处理: {} -> 工单 {}", plan.getName(), wo.getCode());
    }
}
