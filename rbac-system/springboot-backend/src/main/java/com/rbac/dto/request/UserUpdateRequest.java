package com.rbac.dto.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class UserUpdateRequest {
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String phone;
    private String avatar;
    private Integer status;
    private Set<Long> roleIds;
}
