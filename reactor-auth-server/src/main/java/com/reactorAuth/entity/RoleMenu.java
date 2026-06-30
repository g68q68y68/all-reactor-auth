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
@Table("sys_role_menus")
public class RoleMenu {
    @Id
    private Long id;

    @Column("role_id")
    private Long roleId;

    @Column("menu_id")
    private Long menuId;
}
