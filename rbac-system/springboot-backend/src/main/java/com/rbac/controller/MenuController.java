package com.rbac.controller;

import com.rbac.dto.response.ApiResponse;
import com.rbac.entity.Permission;
import com.rbac.entity.User;
import com.rbac.mapper.UserMapper;
import com.rbac.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MenuController {
    
    private final MenuService menuService;
    private final UserMapper userMapper;
    
    @GetMapping("/menus")
    public ResponseEntity<ApiResponse<List<Permission>>> getCurrentUserMenus(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userMapper.selectByUsername(userDetails.getUsername());
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        
        Set<Long> roleIds = user.getRoles().stream()
            .map(r -> r.getId())
            .collect(Collectors.toSet());
        
        List<Permission> menus = menuService.getCurrentUserMenus(roleIds);
        return ResponseEntity.ok(ApiResponse.success(menus));
    }
    
    @GetMapping("/permissions")
    public ResponseEntity<ApiResponse<List<Permission>>> getAllPermissions() {
        List<Permission> permissions = menuService.getAllPermissions();
        return ResponseEntity.ok(ApiResponse.success(permissions));
    }
    
    @GetMapping("/permissions/current")
    public ResponseEntity<ApiResponse<Set<String>>> getCurrentUserPermissions(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userMapper.selectByUsername(userDetails.getUsername());
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        
        Set<Long> roleIds = user.getRoles().stream()
            .map(r -> r.getId())
            .collect(Collectors.toSet());
        
        Set<String> permissions = menuService.getCurrentUserPermissionCodes(roleIds);
        return ResponseEntity.ok(ApiResponse.success(permissions));
    }
}
