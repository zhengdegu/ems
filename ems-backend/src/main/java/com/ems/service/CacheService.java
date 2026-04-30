package com.ems.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存服务
 * 所有方法 try-catch，Redis 不可用时降级为返回 null（由调用方回源 DB）
 */
@Service
@Slf4j
public class CacheService {

    private static final String DASHBOARD_KEY = "ems:dashboard:overview";
    private static final String ALARM_RULES_KEY = "ems:alarm:rules";
    private static final String EQUIPMENT_STATS_KEY = "ems:equipment:stats";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // ========== Dashboard 缓存（60秒过期）==========

    public void cacheDashboard(Map<String, Object> data) {
        try {
            redisTemplate.opsForValue().set(DASHBOARD_KEY, data, 60, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Redis cacheDashboard 失败，降级跳过: {}", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getCachedDashboard() {
        try {
            Object cached = redisTemplate.opsForValue().get(DASHBOARD_KEY);
            return cached != null ? (Map<String, Object>) cached : null;
        } catch (Exception e) {
            log.warn("Redis getCachedDashboard 失败，降级返回 null: {}", e.getMessage());
            return null;
        }
    }

    public void evictDashboard() {
        try {
            redisTemplate.delete(DASHBOARD_KEY);
        } catch (Exception e) {
            log.warn("Redis evictDashboard 失败，降级跳过: {}", e.getMessage());
        }
    }

    // ========== 告警规则缓存（5分钟过期）==========

    public void cacheAlarmRules(List<?> rules) {
        try {
            redisTemplate.opsForValue().set(ALARM_RULES_KEY, rules, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("Redis cacheAlarmRules 失败，降级跳过: {}", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<?> getCachedAlarmRules() {
        try {
            Object cached = redisTemplate.opsForValue().get(ALARM_RULES_KEY);
            return cached != null ? (List<?>) cached : null;
        } catch (Exception e) {
            log.warn("Redis getCachedAlarmRules 失败，降级返回 null: {}", e.getMessage());
            return null;
        }
    }

    public void evictAlarmRules() {
        try {
            redisTemplate.delete(ALARM_RULES_KEY);
        } catch (Exception e) {
            log.warn("Redis evictAlarmRules 失败，降级跳过: {}", e.getMessage());
        }
    }

    // ========== 通用缓存方法 ==========

    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            log.warn("Redis set 失败, key={}: {}", key, e.getMessage());
        }
    }

    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.warn("Redis get 失败, key={}: {}", key, e.getMessage());
            return null;
        }
    }

    public void evict(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("Redis evict 失败, key={}: {}", key, e.getMessage());
        }
    }
}
