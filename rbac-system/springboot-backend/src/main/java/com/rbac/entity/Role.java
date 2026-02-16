package com.rbac.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_role")
@EqualsAndHashCode(exclude = {"users", "permissions"})
public class Role {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String roleName;
    private String roleCode;
    private String description;
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableField(exist = false)
    private List<User> users;
    
    @TableField(exist = false)
    private List<Permission> permissions;
}
