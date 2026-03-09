package com.ems.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ems.dto.R;
import com.ems.entity.WorkOrder;
import com.ems.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/work-order")
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;

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
    public R create(@RequestBody WorkOrder workOrder) {
        workOrderService.createWorkOrder(workOrder);
        return R.ok();
    }

    @PutMapping("/{id}")
    public R update(@PathVariable Long id, @RequestBody WorkOrder workOrder) {
        workOrderService.updateWorkOrder(id, workOrder);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        workOrderService.deleteWorkOrder(id);
        return R.ok();
    }

    @PutMapping("/{id}/status")
    public R updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            workOrderService.updateStatus(id, body.get("status"));
            return R.ok();
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }
}
