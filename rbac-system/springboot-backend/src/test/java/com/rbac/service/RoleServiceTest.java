package com.rbac.service;

import com.rbac.dto.request.RoleCreateRequest;
import com.rbac.dto.response.RoleResponse;
import com.rbac.entity.Role;
import com.rbac.exception.BusinessException;
import com.rbac.mapper.PermissionMapper;
import com.rbac.mapper.RoleMapper;
import com.rbac.mapper.RolePermissionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private PermissionMapper permissionMapper;

    @Mock
    private RolePermissionMapper rolePermissionMapper;

    @InjectMocks
    private RoleService roleService;

    @Test
    void testGetRoleById_Success() {
        Role role = new Role();
        role.setId(1L);
        role.setRoleName("Admin");
        role.setRoleCode("ROLE_ADMIN");

        when(roleMapper.selectById(1L)).thenReturn(role);

        RoleResponse result = roleService.getRoleById(1L);

        assertNotNull(result);
        assertEquals("Admin", result.getRoleName());
    }

    @Test
    void testGetRoleById_NotFound() {
        when(roleMapper.selectById(999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> roleService.getRoleById(999L));
    }

    @Test
    void testCreateRole_DuplicateRoleCode() {
        RoleCreateRequest request = new RoleCreateRequest();
        request.setRoleCode("ROLE_ADMIN");

        Role existingRole = new Role();
        existingRole.setRoleCode("ROLE_ADMIN");

        when(roleMapper.selectByRoleCode("ROLE_ADMIN")).thenReturn(existingRole);

        assertThrows(BusinessException.class, () -> roleService.createRole(request));
    }

    @Test
    void testUpdateRolePermissions() {
        Role role = new Role();
        role.setId(1L);

        when(roleMapper.selectById(1L)).thenReturn(role);
        when(rolePermissionMapper.deleteByRoleId(1L)).thenReturn(1);
        when(rolePermissionMapper.insertBatch(eq(1L), anyList())).thenReturn(2);

        Set<Long> permissionIds = new HashSet<>(Arrays.asList(1L, 2L));

        assertDoesNotThrow(() -> roleService.updateRolePermissions(1L, permissionIds));
    }

    @Test
    void testDeleteRole_Success() {
        Role role = new Role();
        role.setId(1L);

        when(roleMapper.selectById(1L)).thenReturn(role);

        assertDoesNotThrow(() -> roleService.deleteRole(1L));
    }
}
