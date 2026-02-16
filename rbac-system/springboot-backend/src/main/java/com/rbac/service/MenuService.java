package com.rbac.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rbac.entity.Permission;
import com.rbac.mapper.PermissionMapper;
import com.rbac.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {
    
    private final PermissionMapper permissionMapper;
    private final RoleMapper roleMapper;
    
    @Transactional(readOnly = true)
    public List<Permission> getCurrentUserMenus(Set<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<Permission> allPermissions = permissionMapper.selectPermissionsByRoleIds(new ArrayList<>(roleIds));
        
        return allPermissions.stream()
            .filter(p -> "menu".equals(p.getType()))
            .filter(p -> p.getStatus() == 1)
            .sorted(Comparator.comparing(Permission::getSortOrder, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<Permission> getAllMenus() {
        return permissionMapper.selectList(
            new LambdaQueryWrapper<Permission>()
                .isNull(Permission::getParentId)
                .eq(Permission::getStatus, 1)
                .orderByAsc(Permission::getSortOrder)
        );
    }
    
    @Transactional(readOnly = true)
    public List<Permission> getAllPermissions() {
        return permissionMapper.selectList(
            new LambdaQueryWrapper<Permission>()
                .eq(Permission::getStatus, 1)
                .orderByAsc(Permission::getSortOrder)
        );
    }
    
    @Transactional(readOnly = true)
    public Set<String> getCurrentUserPermissionCodes(Set<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptySet();
        }
        
        List<Permission> permissions = permissionMapper.selectPermissionsByRoleIds(new ArrayList<>(roleIds));
        return permissions.stream()
            .map(Permission::getPermissionCode)
            .collect(Collectors.toSet());
    }
}
