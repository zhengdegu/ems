package com.ems.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ems.entity.Alarm;
import com.ems.entity.AlarmRule;
import com.ems.entity.Equipment;
import com.ems.enums.AlarmStatus;
import com.ems.enums.EquipmentStatus;
import com.ems.event.AlarmCreatedEvent;
import com.ems.mapper.AlarmMapper;
import com.ems.mapper.EquipmentMapper;
import com.ems.service.AlarmRuleService;
import com.ems.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class AlarmScheduler {

    @Autowired
    private AlarmRuleService alarmRuleService;
    @Autowired
    private EquipmentMapper equipmentMapper;
    @Autowired
    private AlarmMapper alarmMapper;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private CacheService cacheService;

    /**
     * 每分钟扫描告警规则，模拟设备数据检查
     * 实际生产中这里会读取设备上报的实时数据
     * 当前实现：随机模拟设备参数，匹配规则后自动创建告警
     */
    @Scheduled(fixedRate = 60000)
    public void checkAlarmRules() {
        log.info("=== 告警规则扫描开始 ===");

        // 优先从缓存获取告警规则
        @SuppressWarnings("unchecked")
        List<AlarmRule> rules = (List<AlarmRule>) cacheService.getCachedAlarmRules();
        if (rules == null) {
            rules = alarmRuleService.list(
                    new LambdaQueryWrapper<AlarmRule>().eq(AlarmRule::getEnabled, 1)
            );
            cacheService.cacheAlarmRules(rules);
        }

        for (AlarmRule rule : rules) {
            try {
                // 模拟：5%概率触发告警（实际应该读取设备实时数据）
                if (Math.random() < 0.05) {
                    // 随机选一个对应类型的设备
                    LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<Equipment>()
                            .eq(Equipment::getStatus, EquipmentStatus.RUNNING.getCode());
                    if (rule.getEquipmentType() != null && !rule.getEquipmentType().isEmpty()) {
                        wrapper.eq(Equipment::getType, rule.getEquipmentType());
                    }
                    List<Equipment> equipments = equipmentMapper.selectList(wrapper);
                    if (equipments.isEmpty()) continue;
                    Equipment eq = equipments.get((int) (Math.random() * equipments.size()));

                    // 创建告警
                    Alarm alarm = new Alarm();
                    alarm.setEquipmentId(eq.getId());
                    alarm.setEquipmentName(eq.getCode() + " " + eq.getName());
                    alarm.setLevel(rule.getLevel());
                    String ruleName = rule.getRuleName() != null ? rule.getRuleName() : rule.getName();
                    alarm.setMessage("自动检测: " + ruleName + " - " + eq.getName());
                    alarm.setStatus(AlarmStatus.UNHANDLED.getCode());
                    alarmMapper.insert(alarm);

                    // 发布告警事件（通知、WebSocket推送、自动创建工单等由监听器处理）
                    eventPublisher.publishEvent(new AlarmCreatedEvent(this, alarm));

                    log.info("触发告警: {} - {}", rule.getLevel(), alarm.getMessage());
                }
            } catch (Exception e) {
                log.error("处理告警规则异常, ruleId={}, ruleName={}: {}", rule.getId(),
                        rule.getRuleName() != null ? rule.getRuleName() : rule.getName(), e.getMessage(), e);
            }
        }
        log.info("=== 告警规则扫描完成 ===");
    }
}
