package com.reactorAuth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reactorAuth.dto.*;
import com.reactorAuth.entity.*;
import com.reactorAuth.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/enum-types")
@RequiredArgsConstructor
public class EnumTypeController {
    private final EnumTypeMapper enumTypeMapper;
    private final EnumValueMapper enumValueMapper;

    @GetMapping
    public Result<PageResult<EnumType>> page(@RequestParam(defaultValue="0") int page,
                                              @RequestParam(defaultValue="10") int size) {
        Page<EnumType> p = enumTypeMapper.selectPage(new Page<>(page+1, size),
                new LambdaQueryWrapper<EnumType>().orderByAsc(EnumType::getId));
        return Result.success(PageResult.of(p));
    }

    @GetMapping("/with-values")
    public Result<List<EnumTypeWithValues>> withValues() {
        List<EnumType> types = enumTypeMapper.selectList(null);
        List<EnumTypeWithValues> result = types.stream().map(t -> {
            EnumTypeWithValues dto = new EnumTypeWithValues();
            dto.setId(t.getId()); dto.setCode(t.getCode()); dto.setName(t.getName());
            dto.setDescription(t.getDescription()); dto.setStatus(t.getStatus());
            dto.setValues(enumValueMapper.findByTypeId(t.getId()));
            return dto;
        }).collect(Collectors.toList());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<EnumType> findById(@PathVariable Long id) { return Result.success(enumTypeMapper.selectById(id)); }

    @PostMapping public Result<EnumType> create(@RequestBody EnumType t) { t.setId(null); enumTypeMapper.insert(t); return Result.success(t); }

    @PutMapping("/{id}")
    public Result<EnumType> update(@PathVariable Long id, @RequestBody EnumType t) { t.setId(id); enumTypeMapper.updateById(t); return Result.success(enumTypeMapper.selectById(id)); }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { enumTypeMapper.deleteById(id); return Result.success(); }
}
