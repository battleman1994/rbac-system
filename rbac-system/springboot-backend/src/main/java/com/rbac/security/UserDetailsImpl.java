package com.rbac.security;

import com.rbac.entity.Permission;
import com.rbac.entity.Role;
import com.rbac.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String username;
    
    @JsonIgnore
    private String password;
    
    private String email;
    private String phone;
    private String avatar;
    private Integer status;
    
    private Collection<? extends GrantedAuthority> authorities;
    
    public static UserDetailsImpl build(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        List<Role> roles = user.getRoles();
        if (roles != null) {
            roles.forEach(role -> {
                authorities.add(new SimpleGrantedAuthority(role.getRoleCode()));
                List<Permission> permissions = role.getPermissions();
                if (permissions != null) {
                    permissions.forEach(perm -> 
                        authorities.add(new SimpleGrantedAuthority(perm.getPermissionCode()))
                    );
                }
            });
        }
        
        return new UserDetailsImpl(
            user.getId(),
            user.getUsername(),
            user.getPassword(),
            user.getEmail(),
            user.getPhone(),
            user.getAvatar(),
            user.getStatus(),
            authorities
        );
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return status != null && status == 1;
    }
}
