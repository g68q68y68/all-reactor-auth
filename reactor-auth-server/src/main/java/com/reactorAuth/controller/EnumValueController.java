package com.reactorAuth.controller;

import com.reactorAuth.entity.EnumValue;
import com.reactorAuth.service.EnumValueService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enum-values")
public class EnumValueController extends BaseController<EnumValue, EnumValueService> {

    public EnumValueController(EnumValueService enumValueService) {
        super(enumValueService);
    }
}
