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
@Table("permissions")
public class Permission {
    @Id
    private Long id;

    @Column("code")
    private String code; // user:read, user:write, admin:access

    @Column("name")
    private String name;

    @Column("type")
    private String type; // MENU, BUTTON, API

    @Column("url")
    private String url;

    @Column("method")
    private String method; // GET, POST, PUT, DELETE

    @Column("status")
    private Integer status;

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
    private Long updatedBy;}
