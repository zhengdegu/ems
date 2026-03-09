package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ems.entity.Equipment;
import com.ems.entity.WorkOrder;
import com.ems.entity.Alarm;
import com.ems.mapper.EquipmentMapper;
import com.ems.mapper.WorkOrderMapper;
import com.ems.mapper.AlarmMapper;
import com.ems.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private WorkOrderMapper workOrderMapper;

    @Autowired
    private AlarmMapper alarmMapper;

    private LambdaQueryWrapper<?> applyDateRange(LambdaQueryWrapper<?> wrapper, String startDate, String endDate) {
        return wrapper;
    }

    private LocalDateTime parseDate(String date) {
        if (date == null || date.isEmpty()) return null;
        return LocalDateTime.parse(date + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Override
    public Map<String, Object> getEquipmentReport(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        LocalDateTime start = parseDate(startDate);
        LocalDateTime end = parseDate(endDate);
        if (start != null) wrapper.ge(Equipment::getCreateTime, start);
        if (end != null) wrapper.le(Equipment::getCreateTime, end.plusDays(1));

        List<Equipment> all = equipmentMapper.selectList(wrapper);
        result.put("total", all.size());
        Map<String, Long> byStatus = all.stream()
                .collect(Collectors.groupingBy(e -> e.getStatus() != null ? e.getStatus() : "unknown", Collectors.counting()));
        Map<String, Long> byType = all.stream()
                .collect(Collectors.groupingBy(e -> e.getType() != null ? e.getType() : "unknown", Collectors.counting()));
        result.put("byStatus", byStatus);
        result.put("byType", byType);
        return result;
    }

    @Override
    public Map<String, Object> getMaintenanceReport(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        LambdaQueryWrapper<WorkOrder> wrapper = new LambdaQueryWrapper<>();
        LocalDateTime start = parseDate(startDate);
        LocalDateTime end = parseDate(endDate);
        if (start != null) wrapper.ge(WorkOrder::getCreateTime, start);
        if (end != null) wrapper.le(WorkOrder::getCreateTime, end.plusDays(1));

        List<WorkOrder> all = workOrderMapper.selectList(wrapper);
        result.put("total", all.size());
        Map<String, Long> byStatus = all.stream()
                .collect(Collectors.groupingBy(w -> w.getStatus() != null ? w.getStatus() : "unknown", Collectors.counting()));
        Map<String, Long> byType = all.stream()
                .collect(Collectors.groupingBy(w -> w.getType() != null ? w.getType() : "unknown", Collectors.counting()));
        result.put("byStatus", byStatus);
        result.put("byType", byType);
        return result;
    }

    @Override
    public Map<String, Object> getAlarmReport(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        LambdaQueryWrapper<Alarm> wrapper = new LambdaQueryWrapper<>();
        LocalDateTime start = parseDate(startDate);
        LocalDateTime end = parseDate(endDate);
        if (start != null) wrapper.ge(Alarm::getCreateTime, start);
        if (end != null) wrapper.le(Alarm::getCreateTime, end.plusDays(1));

        List<Alarm> all = alarmMapper.selectList(wrapper);
        result.put("total", all.size());
        Map<String, Long> byLevel = all.stream()
                .collect(Collectors.groupingBy(a -> a.getLevel() != null ? a.getLevel() : "unknown", Collectors.counting()));
        Map<String, Long> byStatus = all.stream()
                .collect(Collectors.groupingBy(a -> a.getStatus() != null ? a.getStatus() : "unknown", Collectors.counting()));
        result.put("byLevel", byLevel);
        result.put("byStatus", byStatus);
        return result;
    }
}
