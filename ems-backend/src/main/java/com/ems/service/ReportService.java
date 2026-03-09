package com.ems.service;

import java.util.Map;

public interface ReportService {

    Map<String, Object> getEquipmentReport(String startDate, String endDate);

    Map<String, Object> getMaintenanceReport(String startDate, String endDate);

    Map<String, Object> getAlarmReport(String startDate, String endDate);
}
