package com.ems.enums;

import lombok.Getter;

@Getter
public enum AlarmStatus {
    UNHANDLED("未处理", "未处理"),
    HANDLING("处理中", "处理中"),
    HANDLED("已处理", "已处理");

    private final String code;
    private final String label;

    AlarmStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
