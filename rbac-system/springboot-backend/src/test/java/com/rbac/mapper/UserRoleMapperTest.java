package com.rbac.mapper;

import com.rbac.entity.UserRole;
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
class UserRoleMapperTest {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Test
    void testInsertBatch() {
        int result = userRoleMapper.insertBatch(1L, Arrays.asList(2L, 3L));
        assertEquals(2, result);
    }

    @Test
    void testDeleteByUserId() {
        int result = userRoleMapper.deleteByUserId(1L);
        assertTrue(result >= 0);
    }

    @Test
    void testInsert() {
        UserRole userRole = new UserRole();
        userRole.setUserId(2L);
        userRole.setRoleId(1L);

        int result = userRoleMapper.insert(userRole);
        assertEquals(1, result);
        assertNotNull(userRole.getId());
    }

    @Test
    void testSelectList() {
        List<UserRole> list = userRoleMapper.selectList(null);
        assertNotNull(list);
    }
}
