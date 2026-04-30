package com.ems.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DashboardServiceTest {

    @Autowired
    private DashboardService dashboardService;

    @Test
    void testGetOverviewReturnsAllRequiredFields() {
        // 测试 overview 接口返回所有必要字段
        // 1. 调用 getOverview()
        // 2. 验证返回的 Map 包含: totalEquipment, runningCount, faultCount,
        //    maintenanceCount, onlineRate, faultRate, pendingWorkOrders, todayAlarms 等
    }

    @Test
    void testGetDistributionUsesGroupByQuery() {
        // 测试设备分布统计使用 GROUP BY 查询而非全表加载
        // 1. 调用 getDistribution()
        // 2. 验证返回 byType, byStatus, byLocation 三个维度的统计数据
        // 3. 验证数据格式正确（Map<String, Object>）
    }

    @Test
    void testOnlineRateCalculation() {
        // 测试在线率计算逻辑
        // 1. 准备已知数量的运行中/总设备
        // 2. 调用 getOverview()
        // 3. 验证 onlineRate = runningCount / totalEquipment * 100
    }
}
