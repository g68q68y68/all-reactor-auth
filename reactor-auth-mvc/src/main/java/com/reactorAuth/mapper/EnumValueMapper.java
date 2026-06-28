package com.reactorAuth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reactorAuth.entity.EnumValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EnumValueMapper extends BaseMapper<EnumValue> {

    @Select("SELECT * FROM enum_values WHERE type_id = #{typeId} ORDER BY sort_order")
    List<EnumValue> findByTypeId(Long typeId);
}
