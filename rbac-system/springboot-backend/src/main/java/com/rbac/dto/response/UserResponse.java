package com.rbac.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String avatar;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private Set<String> roles;
    private Set<String> permissions;
}
