package com.reactorAuth.dto;

import lombok.Data;
import java.util.List;

@Data
public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private int expiresIn;
    private UserInfo userInfo;

    @Data
    public static class UserInfo {
        private Long userId;
        private String username;
        private String email;
        private String phone;
        private String fullName;
        private List<String> roles;
        private List<String> permissions;
    }
}
