package com.reactorAuth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {
    private int code;
    private String message;
    private T data;
    private long timestamp = System.currentTimeMillis();

    public static <T> Result<T> success(T data) { return new Result<>(200, "操作成功", data, System.currentTimeMillis()); }
    public static <T> Result<T> success() { return success(null); }
    public static <T> Result<T> error(int code, String message) { return new Result<>(code, message, null, System.currentTimeMillis()); }
    public static <T> Result<T> error(ResultCode rc) { return error(rc.getCode(), rc.getMessage()); }
    public static <T> Result<T> error(ResultCode rc, String msg) { return error(rc.getCode(), msg); }
}
