package com.reactorAuth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("role_permissions")
public class RolePermission {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long roleId;
    private Long permissionId;
}
