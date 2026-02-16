package com.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rbac.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
    
    int insertBatch(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);
    
    int deleteByUserId(@Param("userId") Long userId);
}
