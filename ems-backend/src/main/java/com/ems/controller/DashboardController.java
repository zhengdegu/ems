package com.ems.controller;

import com.ems.dto.R;
import com.ems.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/overview")
    public R overview() {
        Map<String, Object> result = dashboardService.getOverview();
        return R.ok(result);
    }

    @GetMapping("/trend")
    public R trend() {
        Map<String, Object> result = dashboardService.getTrend();
        return R.ok(result);
    }

    @GetMapping("/distribution")
    public R distribution() {
        Map<String, Object> result = dashboardService.getDistribution();
        return R.ok(result);
    }
}
