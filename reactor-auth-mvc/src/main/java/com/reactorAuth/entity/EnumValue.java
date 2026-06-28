package com.reactorAuth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("enum_values")
public class EnumValue {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long typeId;
    private String code;
    private String name;
    private Integer sortOrder;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableField(fill = FieldFill.INSERT)
    private Long createdBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updatedBy;

    @TableLogic
    private Integer deleted;
    @Version
    private Integer version;
}
