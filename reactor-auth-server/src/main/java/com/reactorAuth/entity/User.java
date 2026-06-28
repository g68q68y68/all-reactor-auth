package com.reactorAuth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User {
    @Id
    private Long id;

    @Column("username")
    private String username;

    @Column("password")
    private String password;

    @Column("email")
    private String email;

    @Column("phone")
    private String phone;

    @Column("full_name")
    private String fullName;

    @Column("status")
    private Integer status; // 1:启用 0:禁用

    @Column("is_account_non_expired")
    private Boolean isAccountNonExpired;

    @Column("is_account_non_locked")
    private Boolean isAccountNonLocked;

    @Column("is_credentials_non_expired")
    private Boolean isCredentialsNonExpired;

    @Column("is_enabled")
    private Boolean isEnabled;

    @Column("last_login_time")
    private LocalDateTime lastLoginTime;

    @Column("created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column("updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column("created_by")
    @CreatedBy
    private Long createdBy;

    @Column("updated_by")
    @LastModifiedBy
    private Long updatedBy;

    @org.springframework.data.annotation.Transient
    private java.util.List<String> roles;
}
