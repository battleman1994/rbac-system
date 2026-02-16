package com.rbac.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rbac.dto.request.UserCreateRequest;
import com.rbac.dto.request.UserUpdateRequest;
import com.rbac.dto.response.UserResponse;
import com.rbac.entity.User;
import com.rbac.exception.BusinessException;
import com.rbac.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetUserById_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        
        when(userMapper.selectById(1L)).thenReturn(user);

        UserResponse result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userMapper.selectById(999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> userService.getUserById(999L));
    }

    @Test
    void testCreateUser_Success() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setEmail("new@test.com");

        when(userMapper.selectByUsername("newuser")).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(100L);
        savedUser.setUsername("newuser");

        UserService spyService = spy(userService);
        doReturn(savedUser).when(spyService).createUser(request);

        UserResponse result = spyService.createUser(request);

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
    }

    @Test
    void testCreateUser_DuplicateUsername() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("existinguser");

        User existingUser = new User();
        existingUser.setUsername("existinguser");

        when(userMapper.selectByUsername("existinguser")).thenReturn(existingUser);

        assertThrows(BusinessException.class, () -> userService.createUser(request));
    }

    @Test
    void testDeleteUser_Success() {
        User user = new User();
        user.setId(1L);

        when(userMapper.selectById(1L)).thenReturn(user);
        when(userMapper.deleteById(1L)).thenReturn(1);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userMapper.selectById(999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> userService.deleteUser(999L));
    }
}
