package com.ems.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("maintenance_plan")
public class MaintenancePlan {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String type;
    private Long equipmentId;
    private String equipmentName;
    private String cycle;
    private LocalDate nextDate;
    private String responsible;
    private String status;
    private String description;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
