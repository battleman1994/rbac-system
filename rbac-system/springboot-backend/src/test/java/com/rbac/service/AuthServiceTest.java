package com.rbac.service;

import com.rbac.dto.request.LoginRequest;
import com.rbac.dto.response.LoginResponse;
import com.rbac.entity.User;
import com.rbac.exception.BusinessException;
import com.rbac.mapper.RoleMapper;
import com.rbac.mapper.UserMapper;
import com.rbac.mapper.UserRoleMapper;
import com.rbac.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private UserRoleMapper userRoleMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void testLogin_InvalidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setUsername("invalid");
        request.setPassword("wrong");

        when(authenticationManager.authenticate(any()))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void testRefreshToken_InvalidToken() {
        String invalidToken = "invalid.token.here";

        when(jwtTokenProvider.validateToken(invalidToken)).thenReturn(false);

        assertThrows(BusinessException.class, () -> authService.refreshToken(invalidToken));
    }

    @Test
    void testRefreshToken_UserNotFound() {
        String validToken = "valid.token.here";

        when(jwtTokenProvider.validateToken(validToken)).thenReturn(true);
        when(jwtTokenProvider.isRefreshToken(validToken)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(validToken)).thenReturn("unknownuser");
        when(userMapper.selectByUsernameWithRoles("unknownuser")).thenReturn(null);

        assertThrows(BusinessException.class, () -> authService.refreshToken(validToken));
    }
}
