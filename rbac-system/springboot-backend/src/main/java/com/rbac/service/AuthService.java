package com.rbac.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rbac.dto.request.LoginRequest;
import com.rbac.dto.request.UserCreateRequest;
import com.rbac.dto.response.LoginResponse;
import com.rbac.dto.response.UserResponse;
import com.rbac.entity.Role;
import com.rbac.entity.User;
import com.rbac.exception.BusinessException;
import com.rbac.mapper.RoleMapper;
import com.rbac.mapper.UserMapper;
import com.rbac.mapper.UserRoleMapper;
import com.rbac.security.JwtTokenProvider;
import com.rbac.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userMapper.selectById(userDetails.getId());
        if (user == null) {
            throw new BusinessException("User not found");
        }
        
        userMapper.updateLastLoginTime(user.getId());
        
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(1800L);
        
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(userDetails.getId());
        userInfo.setUsername(userDetails.getUsername());
        userInfo.setEmail(userDetails.getEmail());
        userInfo.setAvatar(userDetails.getAvatar());
        userInfo.setRoles(userDetails.getAuthorities().stream()
            .filter(a -> a.getAuthority().startsWith("ROLE_"))
            .map(a -> a.getAuthority())
            .collect(Collectors.toSet()));
        userInfo.setPermissions(userDetails.getAuthorities().stream()
            .filter(a -> !a.getAuthority().startsWith("ROLE_"))
            .map(a -> a.getAuthority())
            .collect(Collectors.toSet()));
        
        response.setUser(userInfo);
        return response;
    }
    
    @Transactional
    public UserResponse register(UserCreateRequest request) {
        if (userMapper.selectByUsername(request.getUsername()) != null) {
            throw new BusinessException("Username already exists");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAvatar(request.getAvatar());
        user.setStatus(request.getStatus());
        
        userMapper.insert(user);
        
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            userRoleMapper.insertBatch(user.getId(), request.getRoleIds());
        } else {
            Role defaultRole = roleMapper.selectByRoleCode("ROLE_USER");
            if (defaultRole == null) {
                throw new BusinessException("Default role not found");
            }
            userRoleMapper.insertBatch(user.getId(), List.of(defaultRole.getId()));
        }
        
        return convertToResponse(user);
    }
    
    @Transactional(readOnly = true)
    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new BusinessException("Invalid refresh token", 401);
        }
        
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        User user = userMapper.selectByUsernameWithRoles(username);
        if (user == null) {
            throw new BusinessException("User not found");
        }
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            UserDetailsImpl.build(user), null, UserDetailsImpl.build(user).getAuthorities()
        );
        
        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        
        LoginResponse response = new LoginResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        response.setExpiresIn(1800L);
        
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setAvatar(user.getAvatar());
        
        response.setUser(userInfo);
        return response;
    }
    
    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setAvatar(user.getAvatar());
        response.setStatus(user.getStatus());
        response.setCreatedAt(user.getCreatedAt());
        response.setLastLoginAt(user.getLastLoginAt());
        return response;
    }
}
