package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ems.entity.Alarm;
import com.ems.entity.Equipment;
import com.ems.entity.MaintenancePlan;
import com.ems.entity.WorkOrder;
import com.ems.enums.AlarmStatus;
import com.ems.enums.EquipmentStatus;
import com.ems.enums.WorkOrderStatus;
import com.ems.mapper.AlarmMapper;
import com.ems.mapper.EquipmentMapper;
import com.ems.mapper.MaintenancePlanMapper;
import com.ems.mapper.WorkOrderMapper;
import com.ems.service.CacheService;
import com.ems.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Autowired
    private CacheService cacheService;

    @Override
    public Map<String, Object> getOverview() {
        // 先查缓存
        Map<String, Object> cached = cacheService.getCachedDashboard();
        if (cached != null) {
            return cached;
        }

        Map<String, Object> result = new HashMap<>();

        // 设备总数
        long totalEquipment = equipmentMapper.selectCount(null);
        result.put("totalEquipment", totalEquipment);

        // 运行中设备
        long runningCount = equipmentMapper.selectCount(
                new LambdaQueryWrapper<Equipment>().eq(Equipment::getStatus, EquipmentStatus.RUNNING.getCode()));
        result.put("runningCount", runningCount);

        // 故障设备
        long faultCount = equipmentMapper.selectCount(
                new LambdaQueryWrapper<Equipment>()
                        .eq(Equipment::getStatus, EquipmentStatus.STOPPED.getCode())
                        .or().eq(Equipment::getStatus, EquipmentStatus.FAULT.getCode()));
        result.put("faultCount", faultCount);

        // 维护中设备
        long maintenanceCount = equipmentMapper.selectCount(
                new LambdaQueryWrapper<Equipment>().eq(Equipment::getStatus, EquipmentStatus.MAINTENANCE.getCode()));
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
                        .in(WorkOrder::getStatus, WorkOrderStatus.PENDING.getCode(), WorkOrderStatus.WAITING.getCode()));
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

        // 设备分类统计（使用 SQL GROUP BY 替代全表加载）
        List<Map<String, Object>> typeCountList = equipmentMapper.countByType();
        Map<String, Object> equipmentByType = typeCountList.stream()
                .collect(Collectors.toMap(
                        m -> m.get("type") != null ? m.get("type").toString() : "未分类",
                        m -> m.get("count"),
                        (a, b) -> a));
        result.put("equipmentByType", equipmentByType);

        // 告警按级别统计（使用 SQL GROUP BY 替代全表加载）
        List<Map<String, Object>> levelCountList = alarmMapper.countByLevel();
        Map<String, Object> alarmByLevel = levelCountList.stream()
                .collect(Collectors.toMap(
                        m -> m.get("level") != null ? m.get("level").toString() : "未知",
                        m -> m.get("count"),
                        (a, b) -> a));
        result.put("alarmByLevel", alarmByLevel);

        // 写入缓存
        cacheService.cacheDashboard(result);

        return result;
    }

    @Override
    public Map<String, Object> getTrend() {
        Map<String, Object> result = new HashMap<>();
        List<WorkOrder> allOrders = workOrderMapper.selectList(null);
        Map<String, Long> monthlyOrders = allOrders.stream()
                .filter(w -> w.getCreateTime() != null)
                .collect(Collectors.groupingBy(
                        w -> w.getCreateTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.counting()));
        result.put("workOrderTrend", monthlyOrders);

        List<Alarm> allAlarms = alarmMapper.selectList(null);
        Map<String, Long> monthlyAlarms = allAlarms.stream()
                .filter(a -> a.getCreateTime() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getCreateTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.counting()));
        result.put("alarmTrend", monthlyAlarms);
        return result;
    }

    @Override
    public Map<String, Object> getDistribution() {
        Map<String, Object> result = new HashMap<>();

        // 使用 SQL GROUP BY 替代全表加载到内存
        List<Map<String, Object>> typeList = equipmentMapper.countByType();
        Map<String, Object> byType = typeList.stream()
                .collect(Collectors.toMap(
                        m -> m.get("type") != null ? m.get("type").toString() : "未分类",
                        m -> m.get("count"),
                        (a, b) -> a));
        result.put("byType", byType);

        List<Map<String, Object>> statusList = equipmentMapper.countByStatus();
        Map<String, Object> byStatus = statusList.stream()
                .collect(Collectors.toMap(
                        m -> m.get("status") != null ? m.get("status").toString() : "unknown",
                        m -> m.get("count"),
                        (a, b) -> a));
        result.put("byStatus", byStatus);

        List<Map<String, Object>> locationList = equipmentMapper.countByLocation();
        Map<String, Object> byLocation = locationList.stream()
                .collect(Collectors.toMap(
                        m -> m.get("location") != null ? m.get("location").toString() : "未知区域",
                        m -> m.get("count"),
                        (a, b) -> a));
        result.put("byLocation", byLocation);

        return result;
    }
}
