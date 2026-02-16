package com.rbac.mapper;

import com.rbac.entity.RolePermission;
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
class RolePermissionMapperTest {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Test
    void testInsertBatch() {
        int result = rolePermissionMapper.insertBatch(1L, Arrays.asList(10L, 20L));
        assertEquals(2, result);
    }

    @Test
    void testDeleteByRoleId() {
        int result = rolePermissionMapper.deleteByRoleId(1L);
        assertTrue(result >= 0);
    }

    @Test
    void testSelectPermissionIdsByRoleId() {
        List<Long> permissionIds = rolePermissionMapper.selectPermissionIdsByRoleId(1L);
        assertNotNull(permissionIds);
    }

    @Test
    void testInsert() {
        RolePermission rp = new RolePermission();
        rp.setRoleId(2L);
        rp.setPermissionId(5L);

        int result = rolePermissionMapper.insert(rp);
        assertEquals(1, result);
        assertNotNull(rp.getId());
    }
}
