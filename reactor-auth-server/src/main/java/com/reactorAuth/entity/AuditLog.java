package com.reactorAuth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("sys_audit_logs")
public class AuditLog {
    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("username")
    private String username;

    @Column("module")
    private String module;   // 操作模块：用户管理、角色管理...

    @Column("action")
    private String action;   // 操作类型：新增、编辑、删除、登录

    @Column("description")
    private String description; // 详情：修改了用户 xxx 的角色

    @Column("method")
    private String method;   // HTTP 方法：POST / PUT / DELETE

    @Column("url")
    private String url;      // 请求路径：/api/users/1

    @Column("ip")
    private String ip;

    @Column("params")
    private String params;   // 请求参数（JSON，截断）

    @Column("status")
    private Integer status;  // 1=成功 0=失败

    @Column("error_msg")
    private String errorMsg; // 失败原因

    @Column("created_at")
    @CreatedDate
    private LocalDateTime createdAt;
}
