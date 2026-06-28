package com.reactorAuth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reactorAuth.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    @Select("SELECT DISTINCT m.* FROM menus m " +
            "INNER JOIN role_menus rm ON rm.menu_id = m.id " +
            "INNER JOIN user_roles ur ON ur.role_id = rm.role_id " +
            "WHERE ur.user_id = #{userId} AND m.status = 1 ORDER BY m.sort_order")
    List<Menu> findByUserId(Long userId);
}
