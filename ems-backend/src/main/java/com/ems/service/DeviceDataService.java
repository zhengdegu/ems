package com.ems.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ems.entity.Alarm;
import com.ems.entity.AlarmRule;
import com.ems.entity.Equipment;
import com.ems.entity.Notification;
import com.ems.mapper.AlarmMapper;
import com.ems.mapper.EquipmentMapper;
import com.ems.mapper.NotificationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DeviceDataService {

    @Autowired
    private EquipmentMapper equipmentMapper;
    @Autowired
    private AlarmRuleService alarmRuleService;
    @Autowired
    private AlarmMapper alarmMapper;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired(required = false)
    private WebSocketService webSocketService;

    /**
     * 处理设备上报数据
     * 消息格式: {"deviceCode":"CNC-001","params":{"temperature":72.5,"speed":8500,"vibration":2.3}}
     */
    public void processDeviceData(String deviceCode, Map<String, Object> params) {
        log.info("收到设备数据: {} -> {}", deviceCode, params);

        // 1. 查找设备
        Equipment eq = equipmentMapper.selectOne(
                new LambdaQueryWrapper<Equipment>().eq(Equipment::getCode, deviceCode)
        );
        if (eq == null) {
            log.warn("未知设备: {}", deviceCode);
            return;
        }

        // 2. 检查告警规则
        List<AlarmRule> rules = alarmRuleService.list(
                new LambdaQueryWrapper<AlarmRule>()
                        .eq(AlarmRule::getEnabled, 1)
        );
        for (AlarmRule rule : rules) {
            // 匹配设备类型
            if (rule.getEquipmentType() != null && !rule.getEquipmentType().isEmpty()
                    && !rule.getEquipmentType().equals(eq.getType())) {
                continue;
            }
            // 匹配参数
            String paramName = rule.getParameter();
            if (paramName != null && params.containsKey(paramName)) {
                double value = ((Number) params.get(paramName)).doubleValue();
                double threshold = rule.getThreshold().doubleValue();
                boolean triggered = false;
                String op = rule.getOperator();
                if (">".equals(op) && value > threshold) triggered = true;
                else if (">=".equals(op) && value >= threshold) triggered = true;
                else if ("<".equals(op) && value < threshold) triggered = true;
                else if ("<=".equals(op) && value <= threshold) triggered = true;

                if (triggered) {
                    Alarm alarm = new Alarm();
                    alarm.setEquipmentId(eq.getId());
                    alarm.setEquipmentName(eq.getCode() + " " + eq.getName());
                    alarm.setLevel(rule.getLevel());
                    String ruleName = rule.getRuleName() != null ? rule.getRuleName() : rule.getName();
                    alarm.setMessage(ruleName + ": " + paramName + "=" + String.format("%.1f", value)
                            + " " + op + " " + threshold);
                    alarm.setStatus("未处理");
                    alarmMapper.insert(alarm);

                    if (webSocketService != null) {
                        webSocketService.pushAlarm(alarm);
                    }
                    log.info("设备数据触发告警: {} - {}", rule.getLevel(), alarm.getMessage());
                }
            }
        }

        // 3. 通过WebSocket推送实时数据到前端
        if (webSocketService != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("deviceCode", deviceCode);
            data.put("params", params);
            data.put("timestamp", LocalDateTime.now().toString());
            webSocketService.pushDashboardUpdate(data);
        }
    }
}
