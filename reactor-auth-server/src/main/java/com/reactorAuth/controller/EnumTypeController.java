package com.reactorAuth.controller;

import com.reactorAuth.dto.EnumTypeWithValues;
import com.reactorAuth.dto.Result;
import com.reactorAuth.entity.EnumType;
import com.reactorAuth.service.EnumTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/enum-types")
public class EnumTypeController extends BaseController<EnumType, EnumTypeService> {

    private final EnumTypeService enumTypeService;

    public EnumTypeController(EnumTypeService enumTypeService) {
        super(enumTypeService);
        this.enumTypeService = enumTypeService;
    }

    @GetMapping("/with-values")
    public Mono<Result<List<EnumTypeWithValues>>> withValues() {
        return enumTypeService.findAllWithValues().collectList().map(Result::success);
    }
}
