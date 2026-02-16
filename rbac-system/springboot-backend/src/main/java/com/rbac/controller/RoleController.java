package com.rbac.controller;

import com.rbac.dto.request.RoleCreateRequest;
import com.rbac.dto.response.ApiResponse;
import com.rbac.dto.response.RoleResponse;
import com.rbac.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    
    private final RoleService roleService;
    
    @GetMapping
    @PreAuthorize("hasAuthority('system:role:list')")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {
        List<RoleResponse> roles = roleService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.success(roles));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:list')")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleById(@PathVariable Long id) {
        RoleResponse role = roleService.getRoleById(id);
        return ResponseEntity.ok(ApiResponse.success(role));
    }
    
    @PostMapping
    @PreAuthorize("hasAuthority('system:role:create')")
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@Valid @RequestBody RoleCreateRequest request) {
        RoleResponse role = roleService.createRole(request);
        return ResponseEntity.ok(ApiResponse.success(role, "Role created successfully"));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:update')")
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleCreateRequest request) {
        RoleResponse role = roleService.updateRole(id, request);
        return ResponseEntity.ok(ApiResponse.success(role, "Role updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Role deleted successfully"));
    }
    
    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('system:role:list')")
    public ResponseEntity<ApiResponse<Set<String>>> getRolePermissions(@PathVariable Long id) {
        Set<String> permissions = roleService.getRolePermissions(id);
        return ResponseEntity.ok(ApiResponse.success(permissions));
    }
    
    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('system:role:permission')")
    public ResponseEntity<ApiResponse<Void>> updateRolePermissions(
            @PathVariable Long id,
            @RequestBody Set<Long> permissionIds) {
        roleService.updateRolePermissions(id, permissionIds);
        return ResponseEntity.ok(ApiResponse.success(null, "Role permissions updated successfully"));
    }
}
