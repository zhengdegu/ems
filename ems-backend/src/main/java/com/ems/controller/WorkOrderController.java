package com.ems.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ems.annotation.OpLog;
import com.ems.dto.R;
import com.ems.entity.WorkOrder;
import com.ems.service.WebSocketService;
import com.ems.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/work-order")
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;
    @Autowired(required = false)
    private WebSocketService webSocketService;

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
        try {
            workOrderService.updateStatus(id, body.get("status"));
            // WebSocket 推送工单状态变更
            WorkOrder updated = workOrderService.detail(id);
            if (updated != null && webSocketService != null) {
                webSocketService.pushWorkOrderUpdate(updated);
            }
            return R.ok();
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }
}
