package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ems.entity.Alarm;
import com.ems.entity.Equipment;
import com.ems.entity.MaintenancePlan;
import com.ems.entity.WorkOrder;
import com.ems.mapper.AlarmMapper;
import com.ems.mapper.EquipmentMapper;
import com.ems.mapper.MaintenancePlanMapper;
import com.ems.mapper.WorkOrderMapper;
import com.ems.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        // 设备总数
        long totalEquipment = equipmentMapper.selectCount(null);
        result.put("totalEquipment", totalEquipment);

        // 运行中设备
        long runningCount = equipmentMapper.selectCount(
                new LambdaQueryWrapper<Equipment>().eq(Equipment::getStatus, "running"));
        result.put("runningCount", runningCount);

        // 故障设备
        long faultCount = equipmentMapper.selectCount(
                new LambdaQueryWrapper<Equipment>()
                        .eq(Equipment::getStatus, "stopped")
                        .or().eq(Equipment::getStatus, "fault"));
        result.put("faultCount", faultCount);

        // 维护中设备
        long maintenanceCount = equipmentMapper.selectCount(
                new LambdaQueryWrapper<Equipment>().eq(Equipment::getStatus, "maintenance"));
        result.put("maintenanceCount", maintenanceCount);

        // 在线率
        double onlineRate = totalEquipment > 0
                ? BigDecimal.valueOf(runningCount).multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalEquipment), 1, RoundingMode.HALF_UP).doubleValue()
                : 0;
        result.put("onlineRate", onlineRate);

        // 故障率
        double faultRate = totalEquipment > 0
                ? BigDecimal.valueOf(faultCount).multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalEquipment), 1, RoundingMode.HALF_UP).doubleValue()
                : 0;
        result.put("faultRate", faultRate);

        // 待维护计划数
        long pendingMaintenance = maintenancePlanMapper.selectCount(
                new LambdaQueryWrapper<MaintenancePlan>()
                        .le(MaintenancePlan::getNextDate, LocalDate.now())
              .eq(MaintenancePlan::getStatus, "启用"));
        result.put("pendingMaintenance", pendingMaintenance);

        // 待处理工单
        long pendingWorkOrders = workOrderMapper.selectCount(
                new LambdaQueryWrapper<WorkOrder>()
                        .in(WorkOrder::getStatus, "pending", "待接单"));
        result.put("pendingWorkOrders", pendingWorkOrders);

        // 今日告警
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        long todayAlarms = alarmMapper.selectCount(
                new LambdaQueryWrapper<Alarm>()
                        .ge(Alarm::getCreateTime, todayStart)
                        .lt(Alarm::getCreateTime, todayEnd));
        result.put("todayAlarms", todayAlarms);

        // 本月工单
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = monthStart.plusMonths(1);
        long monthWorkOrders = workOrderMapper.selectCount(
                new LambdaQueryWrapper<WorkOrder>()
                        .ge(WorkOrder::getCreateTime, monthStart)
                        .lt(WorkOrder::getCreateTime, monthEnd));
        result.put("monthWorkOrders", monthWorkOrders);

        // 设备分类统计
        List<Equipment> allEquipment = equipmentMapper.selectList(null);
        Map<String, Long> equipmentByType = allEquipment.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getType() != null ? e.getType() : "未分类",
                        Collectors.counting()));
        result.put("equipmentByType", equipmentByType);

        // 告警按级别统计（未处理的）
        List<Alarm> unhandledAlarms = alarmMapper.selectList(
                new LambdaQueryWrapper<Alarm>().ne(Alarm::getStatus, "已处理"));
        Map<String, Long> alarmByLevel = unhandledAlarms.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getLevel() != null ? a.getLevel() : "未知",
                        Collectors.counting()));
        result.put("alarmByLevel", alarmByLevel);

        return result;
    }

    @Override
    public Map<String, Object> getTrend() {
        Map<String, Object> result = new HashMap<>();
        List<WorkOrder> allOrders = workOrderMapper.selectList(null);
        Map<String, Long> monthlyOrders = allOrders.stream()
                .filter(w -> w.getCreateTime() != null)
                .collect(Collectors.groupingBy(
                        w -> w.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.counting()));
        result.put("workOrderTrend", monthlyOrders);

        List<Alarm> allAlarms = alarmMapper.selectList(null);
        Map<String, Long> monthlyAlarms = allAlarms.stream()
                .filter(a -> a.getCreateTime() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.counting()));
        result.put("alarmTrend", monthlyAlarms);
        return result;
    }

    @Override
    public Map<String, Object> getDistribution() {
        Map<String, Object> result = new HashMap<>();
        List<Equipment> allEquipment = equipmentMapper.selectList(null);

        Map<String, Long> byType = allEquipment.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getType() != null ? e.getType() : "未分类",
                        Collectors.counting()));
        result.put("byType", byType);

        Map<String, Long> byStatus = allEquipment.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getStatus() != null ? e.getStatus() : "unknown",
                        Collectors.counting()));
        result.put("byStatus", byStatus);

        Map<String, Long> byLocation = allEquipment.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getLocation() != null ? e.getLocation() : "未知区域",
                        Collectors.counting()));
        result.put("byLocation", byLocation);
        return result;
    }
}
