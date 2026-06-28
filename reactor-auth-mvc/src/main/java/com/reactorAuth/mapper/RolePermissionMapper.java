package com.reactorAuth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reactorAuth.entity.RolePermission;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    @Select("SELECT permission_id FROM role_permissions WHERE role_id = #{roleId}")
    List<Long> findPermIdsByRoleId(Long roleId);

    @Delete("DELETE FROM role_permissions WHERE role_id = #{roleId}")
    void deleteByRoleId(Long roleId);

    @Select("SELECT DISTINCT p.* FROM permissions p " +
            "INNER JOIN role_permissions rp ON rp.permission_id = p.id " +
            "INNER JOIN user_roles ur ON ur.role_id = rp.role_id " +
            "WHERE ur.user_id = #{userId} AND p.status = 1")
    List<com.reactorAuth.entity.Permission> findPermissionsByUserId(Long userId);
}
