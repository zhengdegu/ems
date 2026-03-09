package com.ems.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("spare_part_record")
public class SparePartRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long sparePartId;
    private String sparePartName;
    private String type;
    private Integer quantity;
    private String operator;
    private String relatedOrder;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
