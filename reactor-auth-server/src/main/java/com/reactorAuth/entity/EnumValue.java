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
@Table("sys_enum_values")
public class EnumValue {
    @Id
    private Long id;

    @Column("type_id")
    private Long typeId;

    @Column("code")
    private String code;

    @Column("name")
    private String name;

    @Column("sort_order")
    private Integer sortOrder;

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
