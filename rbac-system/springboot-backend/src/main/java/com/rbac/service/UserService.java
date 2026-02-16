package com.rbac.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rbac.dto.request.UserCreateRequest;
import com.rbac.dto.request.UserUpdateRequest;
import com.rbac.dto.response.UserResponse;
import com.rbac.entity.Role;
import com.rbac.entity.User;
import com.rbac.exception.BusinessException;
import com.rbac.mapper.RoleMapper;
import com.rbac.mapper.UserMapper;
import com.rbac.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(String username, Integer status, Page<User> page) {
        Page<User> userPage = userMapper.selectPage(page, 
            new LambdaQueryWrapper<User>()
                .like(username != null, User::getUsername, username)
                .eq(status != null, User::getStatus, status)
                .orderByDesc(User::getCreatedAt)
        );
        
        List<UserResponse> records = userPage.getRecords().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        Page<UserResponse> result = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        result.setRecords(records);
        return result;
    }
    
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("User not found");
        }
        return convertToResponse(user);
    }
    
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String username) {
        User user = userMapper.selectByUsernameWithRoles(username);
        if (user == null) {
            throw new BusinessException("User not found");
        }
        return convertToResponse(user);
    }
    
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
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
        }
        
        return convertToResponse(user);
    }
    
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("User not found");
        }
        
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        
        userMapper.updateById(user);
        
        if (request.getRoleIds() != null) {
            userRoleMapper.deleteByUserId(id);
            if (!request.getRoleIds().isEmpty()) {
                userRoleMapper.insertBatch(id, request.getRoleIds());
            }
        }
        
        return convertToResponse(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        if (userMapper.selectById(id) == null) {
            throw new BusinessException("User not found");
        }
        userRoleMapper.deleteByUserId(id);
        userMapper.deleteById(id);
    }
    
    @Transactional
    public void resetPassword(Long id, String newPassword) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("User not found");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
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
        
        List<Role> roles = roleMapper.selectRolesByUserId(user.getId());
        response.setRoles(roles.stream()
            .map(Role::getRoleCode)
            .collect(Collectors.toSet()));
        response.setPermissions(roles.stream()
            .flatMap(r -> r.getPermissions() != null ? r.getPermissions().stream() : java.util.stream.Stream.empty())
            .map(p -> p.getPermissionCode())
            .collect(Collectors.toSet()));
        return response;
    }
}
