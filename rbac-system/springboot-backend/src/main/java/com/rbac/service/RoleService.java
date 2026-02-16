package com.rbac.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rbac.dto.request.RoleCreateRequest;
import com.rbac.dto.response.RoleResponse;
import com.rbac.entity.Permission;
import com.rbac.entity.Role;
import com.rbac.exception.BusinessException;
import com.rbac.mapper.PermissionMapper;
import com.rbac.mapper.RoleMapper;
import com.rbac.mapper.RolePermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;
    
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        List<Role> roles = roleMapper.selectList(null);
        return roles.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("Role not found");
        }
        return convertToResponse(role);
    }
    
    @Transactional
    public RoleResponse createRole(RoleCreateRequest request) {
        if (roleMapper.selectByRoleCode(request.getRoleCode()) != null) {
            throw new BusinessException("Role code already exists");
        }
        
        Role role = new Role();
        role.setRoleName(request.getRoleName());
        role.setRoleCode(request.getRoleCode());
        role.setDescription(request.getDescription());
        role.setStatus(request.getStatus());
        
        roleMapper.insert(role);
        
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            rolePermissionMapper.insertBatch(role.getId(), request.getPermissionIds());
        }
        
        return convertToResponse(role);
    }
    
    @Transactional
    public RoleResponse updateRole(Long id, RoleCreateRequest request) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("Role not found");
        }
        
        Role existingRole = roleMapper.selectByRoleCode(request.getRoleCode());
        if (existingRole != null && !existingRole.getId().equals(id)) {
            throw new BusinessException("Role code already exists");
        }
        
        role.setRoleName(request.getRoleName());
        role.setRoleCode(request.getRoleCode());
        role.setDescription(request.getDescription());
        role.setStatus(request.getStatus());
        
        roleMapper.updateById(role);
        
        if (request.getPermissionIds() != null) {
            rolePermissionMapper.deleteByRoleId(id);
            if (!request.getPermissionIds().isEmpty()) {
                rolePermissionMapper.insertBatch(id, request.getPermissionIds());
            }
        }
        
        return convertToResponse(role);
    }
    
    @Transactional
    public void deleteRole(Long id) {
        if (roleMapper.selectById(id) == null) {
            throw new BusinessException("Role not found");
        }
        rolePermissionMapper.deleteByRoleId(id);
        roleMapper.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public Set<String> getRolePermissions(Long roleId) {
        List<Permission> permissions = permissionMapper.selectPermissionsByRoleId(roleId);
        return permissions.stream()
            .map(Permission::getPermissionCode)
            .collect(Collectors.toSet());
    }
    
    @Transactional
    public void updateRolePermissions(Long roleId, Set<Long> permissionIds) {
        if (roleMapper.selectById(roleId) == null) {
            throw new BusinessException("Role not found");
        }
        
        rolePermissionMapper.deleteByRoleId(roleId);
        if (!permissionIds.isEmpty()) {
            rolePermissionMapper.insertBatch(roleId, permissionIds.stream().toList());
        }
    }
    
    private RoleResponse convertToResponse(Role role) {
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setRoleName(role.getRoleName());
        response.setRoleCode(role.getRoleCode());
        response.setDescription(role.getDescription());
        response.setStatus(role.getStatus());
        response.setCreatedAt(role.getCreatedAt());
        
        List<Permission> permissions = permissionMapper.selectPermissionsByRoleId(role.getId());
        response.setPermissions(permissions.stream()
            .map(Permission::getPermissionCode)
            .collect(Collectors.toSet()));
        return response;
    }
}
