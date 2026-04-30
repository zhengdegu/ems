package com.ems.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ems.dto.R;
import com.ems.entity.Alarm;
import com.ems.service.AlarmService;
import com.ems.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/alarm")
public class AlarmController {

    @Autowired
    private AlarmService alarmService;
    @Autowired(required = false)
    private WebSocketService webSocketService;

    @GetMapping
    public R list(@RequestParam(defaultValue = "1") int page,
                  @RequestParam(defaultValue = "10") int pageSize,
                  @RequestParam(required = false) String keyword,
                  @RequestParam(required = false) String level,
                  @RequestParam(required = false) String status) {
        Page<Alarm> result = alarmService.listPage(page, pageSize, keyword, level, status);
        return R.ok(result);
    }

    @GetMapping("/{id}")
    public R detail(@PathVariable Long id) {
        Alarm alarm = alarmService.detail(id);
        if (alarm == null) {
            return R.fail("告警不存在");
        }
        return R.ok(alarm);
    }

    @PutMapping("/{id}/confirm")
    public R confirm(@PathVariable Long id) {
        try {
            alarmService.confirmAlarm(id);
            return R.ok();
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @PutMapping("/{id}/handle")
    public R handle(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            alarmService.handleAlarm(id, body.get("handleNote"));
            // WebSocket 推送告警状态更新
            Alarm updated = alarmService.detail(id);
            if (updated != null && webSocketService != null) {
                webSocketService.pushAlarm(updated);
            }
            return R.ok();
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        alarmService.deleteAlarm(id);
        return R.ok();
    }
}
