package com.rbac.mapper;

import com.rbac.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RoleMapperTest {

    @Autowired
    private RoleMapper roleMapper;

    @Test
    void testInsert() {
        Role role = new Role();
        role.setRoleName("Test Role");
        role.setRoleCode("ROLE_TEST");
        role.setDescription("Test role description");
        role.setStatus(1);

        int result = roleMapper.insert(role);
        assertEquals(1, result);
        assertNotNull(role.getId());
    }

    @Test
    void testSelectById() {
        Role role = roleMapper.selectById(1L);
        assertNotNull(role);
        assertEquals("ROLE_SUPER_ADMIN", role.getRoleCode());
    }

    @Test
    void testSelectByRoleCode() {
        Role role = roleMapper.selectByRoleCode("ROLE_ADMIN");
        assertNotNull(role);
        assertEquals("Admin", role.getRoleName());
    }

    @Test
    void testSelectRolesByUserId() {
        List<Role> roles = roleMapper.selectRolesByUserId(1L);
        assertNotNull(roles);
        assertFalse(roles.isEmpty());
    }

    @Test
    void testSelectByIdWithPermissions() {
        Role role = roleMapper.selectByIdWithPermissions(1L);
        assertNotNull(role);
        assertNotNull(role.getPermissions());
    }

    @Test
    void testUpdateById() {
        Role role = roleMapper.selectById(1L);
        role.setDescription("Updated description");
        
        int result = roleMapper.updateById(role);
        assertEquals(1, result);
        
        Role updated = roleMapper.selectById(1L);
        assertEquals("Updated description", updated.getDescription());
    }

    @Test
    void testDeleteById() {
        int result = roleMapper.deleteById(4L);
        assertEquals(1, result);
        
        Role deleted = roleMapper.selectById(4L);
        assertNull(deleted);
    }
}
