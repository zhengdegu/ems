package com.ems.service.impl;

import com.ems.entity.Equipment;
import com.ems.entity.WorkOrder;
import com.ems.entity.Alarm;
import com.ems.mapper.EquipmentMapper;
import com.ems.mapper.WorkOrderMapper;
import com.ems.mapper.AlarmMapper;
import com.ems.mapper.MaintenancePlanMapper;
import com.ems.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private WorkOrderMapper workOrderMapper;

    @Autowired
    private AlarmMapper alarmMapper;

    @Autowired
    private MaintenancePlanMapper maintenancePlanMapper;

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();
        result.put("equipmentTotal", equipmentMapper.selectCount(null));
        result.put("equipmentRunning", equipmentMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Equipment>()
                        .eq(Equipment::getStatus, "running")));
        result.put("workOrderPending", workOrderMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WorkOrder>()
                        .eq(WorkOrder::getStatus, "pending")));
        result.put("alarmUnhandled", alarmMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Alarm>()
                        .eq(Alarm::getStatus, "pending")));
        return result;
    }

    @Override
    public Map<String, Object> getTrend() {
        Map<String, Object> result = new HashMap<>();
        // 按月统计工单数量（近12个月）
        List<WorkOrder> allOrders = workOrderMapper.selectList(null);
        Map<String, Long> monthlyOrders = allOrders.stream()
                .filter(w -> w.getCreateTime() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        w -> w.getCreateTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")),
                        java.util.stream.Collectors.counting()));
        result.put("workOrderTrend", monthlyOrders);

        // 按月统计告警数量
        List<Alarm> allAlarms = alarmMapper.selectList(null);
        Map<String, Long> monthlyAlarms = allAlarms.stream()
                .filter(a -> a.getCreateTime() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        a -> a.getCreateTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")),
                        java.util.stream.Collectors.counting()));
        result.put("alarmTrend", monthlyAlarms);
        return result;
    }

    @Override
    public Map<String, Object> getDistribution() {
        Map<String, Object> result = new HashMap<>();
        // 按类型统计设备分布
        List<Equipment> allEquipment = equipmentMapper.selectList(null);
        Map<String, Long> byType = allEquipment.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        e -> e.getType() != null ? e.getType() : "未分类",
                        java.util.stream.Collectors.counting()));
        result.put("byType", byType);

        // 按状态统计设备分布
        Map<String, Long> byStatus = allEquipment.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        e -> e.getStatus() != null ? e.getStatus() : "unknown",
                        java.util.stream.Collectors.counting()));
        result.put("byStatus", byStatus);

        // 按区域统计设备分布
        Map<String, Long> byLocation = allEquipment.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        e -> e.getLocation() != null ? e.getLocation() : "未知区域",
                        java.util.stream.Collectors.counting()));
        result.put("byLocation", byLocation);
        return result;
    }
}
