package com.ems.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ems.entity.MaintenancePlan;
import com.ems.entity.Notification;
import com.ems.entity.WorkOrder;
import com.ems.mapper.MaintenancePlanMapper;
import com.ems.mapper.NotificationMapper;
import com.ems.mapper.WorkOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class MaintenanceScheduler {

    @Autowired
    private MaintenancePlanMapper maintenancePlanMapper;
    @Autowired
    private WorkOrderMapper workOrderMapper;
    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * 每天凌晨1点检查维护计划
     * 到期或即将到期的计划自动生成工单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void checkMaintenancePlans() {
        log.info("=== 维护计划检查开始 ===");

        // 查询所有启用的维护计划，3天内到期
        List<MaintenancePlan> plans = maintenancePlanMapper.selectList(
                new LambdaQueryWrapper<MaintenancePlan>()
                        .eq(MaintenancePlan::getStatus, "启用")
                        .le(MaintenancePlan::getNextDate, LocalDate.now().plusDays(3))
        );

        for (MaintenancePlan plan : plans) {
            // 检查是否已经为该计划生成过工单（避免重复）
            Long existCount = workOrderMapper.selectCount(
                    new LambdaQueryWrapper<WorkOrder>()
                            .like(WorkOrder::getTitle, plan.getName())
                            .ge(WorkOrder::getCreateTime, LocalDate.now().atStartOfDay())
            );
            if (existCount > 0) continue;

            // 创建工单
            WorkOrder wo = new WorkOrder();
            wo.setCode("WO-MP-" + System.currentTimeMillis());
            wo.setTitle("[维护计划] " + plan.getName());
            wo.setType(plan.getType());
            wo.setEquipmentId(plan.getEquipmentId());
            wo.setEquipmentName(plan.getEquipmentName());
            wo.setPriority("中");
            wo.setStatus("待接单");
            wo.setAssignee(plan.getResponsible());
            wo.setCreator("系统自动");
            wo.setDescription("维护计划自动生成工单，计划: " + plan.getName() + "，周期: " + plan.getCycle());
            workOrderMapper.insert(wo);

            // 创建通知
            Notification notification = new Notification();
            notification.setTitle("维护计划提醒: " + plan.getName());
            notification.setContent("维护计划即将到期，已自动生成工单 " + wo.getCode());
            notification.setType("system");
            notification.setUserId(1L);
            notification.setIsRead(0);
            notificationMapper.insert(notification);

            log.info("维护计划自动生成工单: {} -> {}", plan.getName(), wo.getCode());
        }
        log.info("=== 维护计划检查完成 ===");
    }

    /**
     * 每5分钟执行一次快速检查（用于演示，生产环境可改为每天）
     */
    @Scheduled(fixedRate = 300000)
    public void quickCheck() {
        log.info("维护计划快速检查...");
    }
}
