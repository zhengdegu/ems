package com.ems.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ems.dto.R;
import com.ems.entity.MaintenancePlan;
import com.ems.service.MaintenancePlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/maintenance-plan")
public class MaintenancePlanController {

    @Autowired
    private MaintenancePlanService maintenancePlanService;

    @GetMapping
    public R list(@RequestParam(defaultValue = "1") int page,
                  @RequestParam(defaultValue = "10") int pageSize,
                  @RequestParam(required = false) String keyword,
                  @RequestParam(required = false) String status,
                  @RequestParam(required = false) String type) {
        Page<MaintenancePlan> result = maintenancePlanService.listPage(page, pageSize, keyword, status, type);
        return R.ok(result);
    }

    @GetMapping("/{id}")
    public R detail(@PathVariable Long id) {
        MaintenancePlan plan = maintenancePlanService.detail(id);
        if (plan == null) {
            return R.fail("维护计划不存在");
        }
        return R.ok(plan);
    }

    @PostMapping
    public R create(@RequestBody MaintenancePlan plan) {
        maintenancePlanService.createPlan(plan);
        return R.ok();
    }

    @PutMapping("/{id}")
    public R update(@PathVariable Long id, @RequestBody MaintenancePlan plan) {
        maintenancePlanService.updatePlan(id, plan);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        maintenancePlanService.deletePlan(id);
        return R.ok();
    }

    @PutMapping("/{id}/toggle")
    public R toggleEnabled(@PathVariable Long id) {
        try {
            maintenancePlanService.toggleEnabled(id);
            return R.ok();
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }
}
