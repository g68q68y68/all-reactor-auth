package com.reactorAuth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("menus")
public class Menu {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long parentId;
    private String path;
    private String redirect;
    private String name;
    private String component;
    private String title;
    private String icon;
    private String type;
    private Boolean requiresAuth;
    private Integer sortOrder;
    private Integer status;
    private Boolean hidden;

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

    @TableField(exist = false)
    private java.util.List<Menu> children;
}
