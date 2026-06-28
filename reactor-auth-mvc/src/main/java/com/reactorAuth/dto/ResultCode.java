package com.reactorAuth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未认证"),
    TOKEN_EXPIRED(40101, "Token 已过期"),
    TOKEN_INVALID(40102, "Token 无效"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "资源冲突"),
    USERNAME_EXISTS(40901, "用户名已存在"),
    EMAIL_EXISTS(40902, "邮箱已被注册"),
    INTERNAL_ERROR(500, "服务器错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用");

    private final int code;
    private final String message;
}
