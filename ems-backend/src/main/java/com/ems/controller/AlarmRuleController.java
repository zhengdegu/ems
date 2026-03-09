package com.ems.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ems.dto.R;
import com.ems.entity.AlarmRule;
import com.ems.service.AlarmRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alarm-rule")
public class AlarmRuleController {

    @Autowired
    private AlarmRuleService alarmRuleService;

    @GetMapping
    public R list(@RequestParam(defaultValue = "1") int page,
                  @RequestParam(defaultValue = "10") int pageSize,
                  @RequestParam(required = false) String keyword) {
        Page<AlarmRule> result = alarmRuleService.listPage(page, pageSize, keyword);
        return R.ok(result);
    }

    @PostMapping
    public R create(@RequestBody AlarmRule rule) {
        alarmRuleService.createRule(rule);
        return R.ok();
    }

    @PutMapping("/{id}")
    public R update(@PathVariable Long id, @RequestBody AlarmRule rule) {
        alarmRuleService.updateRule(id, rule);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        alarmRuleService.deleteRule(id);
        return R.ok();
    }

    @PutMapping("/{id}/toggle")
    public R toggleEnabled(@PathVariable Long id) {
        try {
            alarmRuleService.toggleEnabled(id);
            return R.ok();
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }
}
