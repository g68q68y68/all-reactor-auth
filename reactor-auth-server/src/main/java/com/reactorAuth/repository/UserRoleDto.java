package com.reactorAuth.repository;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRoleDto {
    private Long userId;
    private String code;
}
