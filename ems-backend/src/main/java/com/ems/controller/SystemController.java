package com.ems.controller;

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
    public R listSettings() {
        List<SystemSetting> settings = systemSettingService.listAll();
        return R.ok(settings);
    }

    @GetMapping("/settings/{key}")
    public R getSetting(@PathVariable String key) {
        SystemSetting setting = systemSettingService.getByKey(key);
        if (setting == null) {
            return R.fail("配置项不存在");
        }
        return R.ok(setting);
    }

    @PostMapping("/settings")
    public R saveSetting(@RequestBody SystemSetting setting) {
        systemSettingService.saveSetting(setting.getSettingKey(), setting.getSettingValue(), setting.getLabel());
        return R.ok();
    }

    @PutMapping("/settings/batch")
    public R batchSave(@RequestBody Map<String, String> settings) {
        systemSettingService.batchSave(settings);
        return R.ok();
    }

    @DeleteMapping("/settings/{key}")
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
