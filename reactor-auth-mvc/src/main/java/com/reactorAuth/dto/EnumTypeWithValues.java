package com.reactorAuth.dto;

import com.reactorAuth.entity.EnumValue;
import lombok.Data;
import java.util.List;

@Data
public class EnumTypeWithValues {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer status;
    private List<EnumValue> values;
}
