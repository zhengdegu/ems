package com.ems.service;

import java.util.Map;

public interface DashboardService {

    Map<String, Object> getOverview();

    Map<String, Object> getTrend();

    Map<String, Object> getDistribution();
}
