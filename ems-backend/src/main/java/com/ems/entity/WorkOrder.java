package com.ems.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("work_order")
public class WorkOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String title;
    private String type;
    private Long equipmentId;
    private String equipmentName;
    private String priority;
    private String assignee;
    private String status;
    private String description;
    private String creator;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    private LocalDateTime finishTime;
    private Long sourcePlanId;
}
