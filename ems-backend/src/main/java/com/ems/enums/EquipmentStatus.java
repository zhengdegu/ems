package com.ems.enums;

import lombok.Getter;

@Getter
public enum EquipmentStatus {
    RUNNING("running", "运行中"),
    STOPPED("stopped", "停机"),
    MAINTENANCE("maintenance", "维护中"),
    FAULT("fault", "故障");

    private final String code;
    private final String label;

    EquipmentStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
