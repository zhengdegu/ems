package com.ems.statemachine;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 工单状态机 — 纯静态工具类
 * 定义工单状态的合法转换路径，防止非法状态跳转
 */
public class WorkOrderStateMachine {

    private static final Map<String, Set<String>> TRANSITIONS = new HashMap<>();

    static {
        TRANSITIONS.put("pending", Set.of("待接单", "已取消"));
        TRANSITIONS.put("待接单", Set.of("处理中", "已取消"));
        TRANSITIONS.put("处理中", Set.of("已完成", "待验收", "已取消"));
        TRANSITIONS.put("待验收", Set.of("已完成", "处理中"));  // 可退回
        TRANSITIONS.put("已完成", Set.of());  // 终态
        TRANSITIONS.put("已取消", Set.of());  // 终态
    }

    /**
     * 检查状态转换是否合法
     */
    public static boolean canTransit(String fromStatus, String toStatus) {
        if (fromStatus == null || toStatus == null) return false;
        Set<String> allowedTargets = TRANSITIONS.get(fromStatus);
        if (allowedTargets == null) return false;
        return allowedTargets.contains(toStatus);
    }

    /**
     * 获取当前状态可以转换到的所有状态
     */
    public static Set<String> getAvailableTransitions(String currentStatus) {
        return TRANSITIONS.getOrDefault(currentStatus, Set.of());
    }

    /**
     * 执行状态转换，不合法则抛异常
     */
    public static void transit(String fromStatus, String toStatus) {
        if (!canTransit(fromStatus, toStatus)) {
            throw new IllegalStateException(
                    String.format("工单状态不允许从 [%s] 转换为 [%s]，允许的目标状态: %s",
                            fromStatus, toStatus, getAvailableTransitions(fromStatus)));
        }
    }

    private WorkOrderStateMachine() {
        // 工具类，禁止实例化
    }
}
