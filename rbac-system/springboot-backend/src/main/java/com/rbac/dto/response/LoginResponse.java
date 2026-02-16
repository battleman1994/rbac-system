package com.rbac.dto.response;

import lombok.Data;

import java.util.Set;

@Data
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserInfo user;
    
    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String avatar;
        private Set<String> roles;
        private Set<String> permissions;
    }
}
