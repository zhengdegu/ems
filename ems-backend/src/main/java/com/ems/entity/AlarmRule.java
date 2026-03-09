package com.ems.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("alarm_rule")
public class AlarmRule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String ruleName;
    private String name;
    private String equipmentType;
    private String parameter;
    private String operator;
    private BigDecimal threshold;
    private String level;
    private Integer enabled;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
