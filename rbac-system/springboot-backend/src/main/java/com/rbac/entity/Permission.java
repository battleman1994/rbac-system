package com.rbac.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_permission")
@EqualsAndHashCode(exclude = {"children", "roles"})
@ToString(exclude = {"children", "roles"})
public class Permission {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String permissionName;
    private String permissionCode;
    private String type;
    private Long parentId;
    private Integer sortOrder;
    private String icon;
    private String path;
    private String component;
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(exist = false)
    private List<Permission> children;
    
    @TableField(exist = false)
    private List<Role> roles;
}
