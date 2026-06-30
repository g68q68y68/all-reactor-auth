package com.reactorAuth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("sys_role_permissions")
public class RolePermission {
    @Id
    private Long id;

    @Column("role_id")
    private Long roleId;

    @Column("permission_id")
    private Long permissionId;
}
