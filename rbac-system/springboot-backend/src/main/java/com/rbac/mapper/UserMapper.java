package com.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rbac.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    User selectByUsername(String username);
    
    User selectByUsernameWithRoles(@Param("username") String username);
    
    List<User> selectUserList(Page<User> page, 
                               @Param("username") String username, 
                               @Param("status") Integer status);
    
    int updateLastLoginTime(@Param("id") Long id);
}
