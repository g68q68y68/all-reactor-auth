package com.reactorAuth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reactorAuth.entity.UserRole;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    @Select("SELECT r.code FROM user_roles ur INNER JOIN roles r ON r.id = ur.role_id WHERE ur.user_id = #{userId}")
    List<String> findRoleCodesByUserId(Long userId);

    @Select("SELECT ur.role_id, r.code FROM user_roles ur INNER JOIN roles r ON r.id = ur.role_id WHERE ur.user_id IN (#{userIds})")
    List<Object[]> findRolesByUserIds(@Param("userIds") List<Long> userIds);

    @Delete("DELETE FROM user_roles WHERE user_id = #{userId}")
    void deleteByUserId(Long userId);
}
