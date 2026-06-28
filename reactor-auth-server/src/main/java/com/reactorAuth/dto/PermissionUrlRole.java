package com.reactorAuth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PermissionUrlRole {
    private String method;
    private String url;
    private String authority; // 授权标识：可能是角色码 ROLE_ADMIN，也可能是权限码 user:read
}
