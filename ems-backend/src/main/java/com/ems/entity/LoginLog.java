package com.ems.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("login_log")
public class LoginLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String username;
    private String ip;
    private String status;
    private String message;
    private String userAgent;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
