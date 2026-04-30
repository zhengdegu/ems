package com.ems.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ems.entity.MaintenancePlan;
import com.ems.entity.WorkOrder;
import com.ems.event.MaintenancePlanDueEvent;
import com.ems.mapper.MaintenancePlanMapper;
import com.ems.mapper.WorkOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
    private ApplicationEventPublisher eventPublisher;

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
            try {
                // 通过 sourcePlanId 精确检查是否已生成过工单（避免重复）
                Long existCount = workOrderMapper.selectCount(
                        new LambdaQueryWrapper<WorkOrder>()
                                .eq(WorkOrder::getSourcePlanId, plan.getId())
                                .ge(WorkOrder::getCreateTime, LocalDate.now().atStartOfDay())
                );
                if (existCount > 0) continue;

                // 发布维护计划到期事件（工单创建、通知等由监听器处理）
                eventPublisher.publishEvent(new MaintenancePlanDueEvent(this, plan));

                log.info("维护计划到期，已发布事件: {}", plan.getName());
            } catch (Exception e) {
                log.error("处理维护计划异常, planId={}, planName={}: {}", plan.getId(), plan.getName(), e.getMessage(), e);
            }
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
