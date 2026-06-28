package com.reactorAuth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reactorAuth.entity.RoleMenu;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    @Select("SELECT menu_id FROM role_menus WHERE role_id = #{roleId}")
    List<Long> findMenuIdsByRoleId(Long roleId);

    @Delete("DELETE FROM role_menus WHERE role_id = #{roleId}")
    void deleteByRoleId(Long roleId);
}
