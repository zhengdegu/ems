package com.ems.enums;

import lombok.Getter;

@Getter
public enum WorkOrderStatus {
    PENDING("pending", "待处理"),
    WAITING("待接单", "待接单"),
    PROCING("处理中", "处理中"),
    COMPLETED("已完成", "已完成");

    private final String code;
    private final String label;

    WorkOrderStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
