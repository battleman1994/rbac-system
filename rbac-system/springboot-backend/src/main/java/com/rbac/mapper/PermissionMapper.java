package com.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rbac.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
    
    @Select("SELECT * FROM sys_permission WHERE permission_code = #{permissionCode}")
    Permission selectByPermissionCode(String permissionCode);
    
    List<Permission> selectPermissionsByRoleId(@Param("roleId") Long roleId);
    
    List<Permission> selectPermissionsByRoleIds(@Param("roleIds") List<Long> roleIds);
    
    List<Permission> selectAllMenuTree();
    
    List<Permission> selectChildrenByParentId(@Param("parentId") Long parentId);
}
