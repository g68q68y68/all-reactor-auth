package com.reactorAuth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reactorAuth.dto.*;
import com.reactorAuth.entity.EnumValue;
import com.reactorAuth.mapper.EnumValueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enum-values")
@RequiredArgsConstructor
public class EnumValueController {
    private final EnumValueMapper enumValueMapper;

    @GetMapping
    public Result<PageResult<EnumValue>> page(@RequestParam(defaultValue="0") int page,
                                               @RequestParam(defaultValue="10") int size) {
        Page<EnumValue> p = enumValueMapper.selectPage(new Page<>(page+1, size),
                new LambdaQueryWrapper<EnumValue>().orderByAsc(EnumValue::getSortOrder));
        return Result.success(PageResult.of(p));
    }

    @GetMapping("/{id}")
    public Result<EnumValue> findById(@PathVariable Long id) { return Result.success(enumValueMapper.selectById(id)); }

    @PostMapping public Result<EnumValue> create(@RequestBody EnumValue v) { v.setId(null); enumValueMapper.insert(v); return Result.success(v); }

    @PutMapping("/{id}")
    public Result<EnumValue> update(@PathVariable Long id, @RequestBody EnumValue v) { v.setId(id); enumValueMapper.updateById(v); return Result.success(enumValueMapper.selectById(id)); }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { enumValueMapper.deleteById(id); return Result.success(); }
}
