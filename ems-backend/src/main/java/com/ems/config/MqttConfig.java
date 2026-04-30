package com.ems.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * MQTT 配置（默认禁用）
 * 当 ems.mqtt.enabled=true 时启用
 * 订阅 ems/device/# topic，收到消息后解析设备数据，触发告警检查
 */
@Configuration
@ConditionalOnProperty(name = "ems.mqtt.enabled", havingValue = "true")
@Slf4j
public class MqttConfig {

    // MQTT 连接工厂、入站通道适配器
    // 订阅 ems/device/# topic
    // 收到消息后解析设备数据，写入数据库，触发告警检查
    // 当前为占位配置，需要 MQTT Broker 时再完善实现

    // @Bean
    // public MqttPahoClientFactory mqttClientFactory() { ... }

    // @Bean
    // public MessageChannel mqttInputChannel() { ... }

    // @Bean
    // public MessageProducer inbound() { ... }
}
