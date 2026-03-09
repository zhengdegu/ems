package com.ems.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("alarm")
public class Alarm {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long equipmentId;
    private String equipmentName;
    private String level;
    private String message;
    private String status;
    private String handler;
    private LocalDateTime confirmTime;
    private String handleNote;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    private LocalDateTime handleTime;
}
