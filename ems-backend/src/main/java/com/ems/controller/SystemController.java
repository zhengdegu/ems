package com.ems.controller;

import com.ems.annotation.OpLog;
import com.ems.annotation.RequirePermission;
import com.ems.dto.R;
import com.ems.entity.SystemSetting;
import com.ems.service.SystemSettingService;
import com.ems.service.LoginLogService;
import com.ems.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    @Autowired
    private SystemSettingService systemSettingService;

    @Autowired
    private LoginLogService loginLogService;

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping("/settings")
    @RequirePermission("system:settings")
    public R listSettings() {
        List<SystemSetting> settings = systemSettingService.listAll();
        return R.ok(settings);
    }

    @GetMapping("/settings/{key}")
    @RequirePermission("system:settings")
    public R getSetting(@PathVariable String key) {
        SystemSetting setting = systemSettingService.getByKey(key);
        if (setting == null) {
            return R.fail("配置项不存在");
        }
        return R.ok(setting);
    }

    @PostMapping("/settings")
    @RequirePermission("system:settings")
    @OpLog(module = "系统设置", action = "保存配置")
    public R saveSetting(@RequestBody SystemSetting setting) {
        systemSettingService.saveSetting(setting.getSettingKey(), setting.getSettingValue(), setting.getLabel());
        return R.ok();
    }

    @PutMapping("/settings/batch")
    @RequirePermission("system:settings")
    @OpLog(module = "系统设置", action = "批量保存配置")
    public R batchSave(@RequestBody Map<String, String> settings) {
        systemSettingService.batchSave(settings);
        return R.ok();
    }

    @DeleteMapping("/settings/{key}")
    @RequirePermission("system:settings")
    @OpLog(module = "系统设置", action = "删除配置")
    public R deleteSetting(@PathVariable String key) {
        systemSettingService.deleteSetting(key);
        return R.ok();
    }

    @GetMapping("/login-log")
    public R loginLogs(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int pageSize,
                       @RequestParam(required = false) String keyword) {
        return R.ok(loginLogService.listPage(page, pageSize, keyword));
    }

    @GetMapping("/operation-log")
    public R operationLogs(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int pageSize,
                           @RequestParam(required = false) String keyword,
                           @RequestParam(required = false) String module) {
        return R.ok(operationLogService.listPage(page, pageSize, keyword, module));
    }
}
