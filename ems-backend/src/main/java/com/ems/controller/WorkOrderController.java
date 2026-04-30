package com.ems.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ems.annotation.OpLog;
import com.ems.dto.R;
import com.ems.entity.WorkOrder;
import com.ems.event.WorkOrderCreatedEvent;
import com.ems.event.WorkOrderStatusChangedEvent;
import com.ems.service.WorkOrderService;
import com.ems.statemachine.WorkOrderStateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/work-order")
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @GetMapping
    public R list(@RequestParam(defaultValue = "1") int page,
                  @RequestParam(defaultValue = "10") int pageSize,
                  @RequestParam(required = false) String keyword,
                  @RequestParam(required = false) String status,
                  @RequestParam(required = false) String type) {
        Page<WorkOrder> result = workOrderService.listPage(page, pageSize, keyword, status, type);
        return R.ok(result);
    }

    @GetMapping("/{id}")
    public R detail(@PathVariable Long id) {
        WorkOrder workOrder = workOrderService.detail(id);
        if (workOrder == null) {
            return R.fail("工单不存在");
        }
        return R.ok(workOrder);
    }

    @PostMapping
    @OpLog(module = "工单管理", action = "创建")
    public R create(@RequestBody WorkOrder workOrder) {
        workOrderService.createWorkOrder(workOrder);
        // 发布工单创建事件
        eventPublisher.publishEvent(new WorkOrderCreatedEvent(this, workOrder));
        return R.ok();
    }

    @PutMapping("/{id}")
    @OpLog(module = "工单管理", action = "编辑")
    public R update(@PathVariable Long id, @RequestBody WorkOrder workOrder) {
        workOrderService.updateWorkOrder(id, workOrder);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @OpLog(module = "工单管理", action = "删除")
    public R delete(@PathVariable Long id) {
        workOrderService.deleteWorkOrder(id);
        return R.ok();
    }

    @PutMapping("/{id}/status")
    @OpLog(module = "工单管理", action = "状态变更")
    public R updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        WorkOrder existing = workOrderService.detail(id);
        if (existing == null) {
            return R.fail("工单不存在");
        }

        // 状态机校验
        String oldStatus = existing.getStatus();
        try {
            WorkOrderStateMachine.transit(oldStatus, newStatus);
        } catch (IllegalStateException e) {
            return R.fail(e.getMessage());
        }

        workOrderService.updateStatus(id, newStatus);

        // 发布状态变更事件
        WorkOrder updated = workOrderService.detail(id);
        eventPublisher.publishEvent(new WorkOrderStatusChangedEvent(this, updated, oldStatus, newStatus));

        return R.ok();
    }

    /**
     * 获取工单当前状态可用的转换目标
     */
    @GetMapping("/{id}/available-transitions")
    public R getAvailableTransitions(@PathVariable Long id) {
        WorkOrder workOrder = workOrderService.detail(id);
        if (workOrder == null) {
            return R.fail("工单不存在");
        }
        Set<String> transitions = WorkOrderStateMachine.getAvailableTransitions(workOrder.getStatus());
        return R.ok(transitions);
    }
}
