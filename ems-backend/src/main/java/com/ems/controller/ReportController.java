package com.ems.controller;

import com.ems.dto.R;
import com.ems.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/equipment")
    public R equipmentReport(@RequestParam(required = false) String startDate,
                             @RequestParam(required = false) String endDate) {
        Map<String, Object> result = reportService.getEquipmentReport(startDate, endDate);
        return R.ok(result);
    }

    @GetMapping("/maintenance")
    public R maintenanceReport(@RequestParam(required = false) String startDate,
                               @RequestParam(required = false) String endDate) {
        Map<String, Object> result = reportService.getMaintenanceReport(startDate, endDate);
        return R.ok(result);
    }

    @GetMapping("/alarm")
    public R alarmReport(@RequestParam(required = false) String startDate,
                         @RequestParam(required = false) String endDate) {
        Map<String, Object> result = reportService.getAlarmReport(startDate, endDate);
        return R.ok(result);
    }
}
