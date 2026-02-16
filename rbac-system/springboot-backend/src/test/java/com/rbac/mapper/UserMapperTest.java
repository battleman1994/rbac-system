package com.rbac.mapper;

import com.rbac.entity.User;
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
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testInsert() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@test.com");
        user.setStatus(1);

        int result = userMapper.insert(user);
        assertEquals(1, result);
        assertNotNull(user.getId());
    }

    @Test
    void testSelectById() {
        User user = userMapper.selectById(1L);
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
    }

    @Test
    void testSelectByUsername() {
        User user = userMapper.selectByUsername("admin");
        assertNotNull(user);
        assertEquals("admin@rbac.com", user.getEmail());
    }

    @Test
    void testSelectByUsernameWithRoles() {
        User user = userMapper.selectByUsernameWithRoles("admin");
        assertNotNull(user);
        assertNotNull(user.getRoles());
        assertFalse(user.getRoles().isEmpty());
    }

    @Test
    void testUpdateById() {
        User user = userMapper.selectById(1L);
        user.setEmail("updated@example.com");
        
        int result = userMapper.updateById(user);
        assertEquals(1, result);
        
        User updated = userMapper.selectById(1L);
        assertEquals("updated@example.com", updated.getEmail());
    }

    @Test
    void testDeleteById() {
        int result = userMapper.deleteById(3L);
        assertEquals(1, result);
        
        User deleted = userMapper.selectById(3L);
        assertNull(deleted);
    }

    @Test
    void testUpdateLastLoginTime() {
        int result = userMapper.updateLastLoginTime(1L);
        assertEquals(1, result);
    }
}
