package com.reactorAuth.dto;

import com.reactorAuth.entity.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnumTypeWithValues {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer status;
    private List<EnumValue> values;
}
