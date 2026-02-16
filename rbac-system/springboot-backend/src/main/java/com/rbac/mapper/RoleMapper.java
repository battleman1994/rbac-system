package com.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rbac.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    
    @Select("SELECT * FROM sys_role WHERE role_code = #{roleCode}")
    Role selectByRoleCode(String roleCode);
    
    List<Role> selectRolesByUserId(@Param("userId") Long userId);
    
    List<Role> selectAllWithPermissions();
    
    Role selectByIdWithPermissions(@Param("id") Long id);
}
