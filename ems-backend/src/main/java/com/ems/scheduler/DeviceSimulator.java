package com.ems.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ems.entity.Equipment;
import com.ems.mapper.EquipmentMapper;
import com.ems.service.DeviceDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备数据模拟器
 * 在 MQTT 禁用时自动模拟设备上报数据，用于演示
 */
@Component
@Slf4j
public class DeviceSimulator {

    @Autowired
    private DeviceDataService deviceDataService;
    @Autowired
    private EquipmentMapper equipmentMapper;

    @Value("${ems.mqtt.enabled:false}")
    private boolean mqttEnabled;

    /**
     * 每30秒模拟设备上报数据（仅在没有真实MQTT时使用）
     */
    @Scheduled(fixedRate = 30000)
    public void simulateDeviceData() {
        if (mqttEnabled) {
            return; // MQTT 已启用，不需要模拟
        }

        List<Equipment> runningDevices = equipmentMapper.selectList(
                new LambdaQueryWrapper<Equipment>().eq(Equipment::getStatus, "running")
        );

        if (runningDevices.isEmpty()) return;

        // 随机选一台设备模拟数据
        Equipment device = runningDevices.get((int) (Math.random() * runningDevices.size()));

        Map<String, Object> params = new HashMap<>();
        params.put("temperature", Math.round((40 + Math.random() * 50) * 10.0) / 10.0);  // 40-90°C
        params.put("speed", 5000 + (int) (Math.random() * 5000));  // 5000-10000 RPM
        params.put("vibration", Math.round(Math.random() * 5 * 10.0) / 10.0);  // 0-5 mm/s
        params.put("power", Math.round((30 + Math.random() * 70) * 10.0) / 10.0);  // 30-100 kW

        deviceDataService.processDeviceData(device.getCode(), params);
    }
}
