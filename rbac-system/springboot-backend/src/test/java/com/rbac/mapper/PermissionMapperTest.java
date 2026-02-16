package com.rbac.mapper;

import com.rbac.entity.Permission;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PermissionMapperTest {

    @Autowired
    private PermissionMapper permissionMapper;

    @Test
    void testInsert() {
        Permission permission = new Permission();
        permission.setPermissionName("Test Permission");
        permission.setPermissionCode("test:permission");
        permission.setType("button");
        permission.setStatus(1);
        permission.setSortOrder(1);

        int result = permissionMapper.insert(permission);
        assertEquals(1, result);
        assertNotNull(permission.getId());
    }

    @Test
    void testSelectById() {
        Permission permission = permissionMapper.selectById(1L);
        assertNotNull(permission);
        assertEquals("dashboard", permission.getPermissionCode());
    }

    @Test
    void testSelectByPermissionCode() {
        Permission permission = permissionMapper.selectByPermissionCode("system:user");
        assertNotNull(permission);
        assertEquals("User Management", permission.getPermissionName());
    }

    @Test
    void testSelectPermissionsByRoleId() {
        List<Permission> permissions = permissionMapper.selectPermissionsByRoleId(1L);
        assertNotNull(permissions);
        assertFalse(permissions.isEmpty());
    }

    @Test
    void testSelectPermissionsByRoleIds() {
        List<Permission> permissions = permissionMapper.selectPermissionsByRoleIds(Arrays.asList(1L, 2L));
        assertNotNull(permissions);
        assertFalse(permissions.isEmpty());
    }

    @Test
    void testSelectAllMenuTree() {
        List<Permission> menus = permissionMapper.selectAllMenuTree();
        assertNotNull(menus);
        assertFalse(menus.isEmpty());
    }

    @Test
    void testSelectChildrenByParentId() {
        List<Permission> children = permissionMapper.selectChildrenByParentId(10L);
        assertNotNull(children);
    }

    @Test
    void testUpdateById() {
        Permission permission = permissionMapper.selectById(1L);
        permission.setPermissionName("Updated Dashboard");
        
        int result = permissionMapper.updateById(permission);
        assertEquals(1, result);
        
        Permission updated = permissionMapper.selectById(1L);
        assertEquals("Updated Dashboard", updated.getPermissionName());
    }

    @Test
    void testDeleteById() {
        int result = permissionMapper.deleteById(5L);
        assertEquals(1, result);
        
        Permission deleted = permissionMapper.selectById(5L);
        assertNull(deleted);
    }
}
