package com.ems.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("equipment")
public class Equipment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String name;
    private String type;
    private String model;
    private String manufacturer;
    private LocalDate manufactureDate;
    private String location;
    private String responsible;
    private String status;
    private Integer healthScore;
    private LocalDate purchaseDate;
    private BigDecimal price;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
