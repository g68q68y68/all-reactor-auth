package com.reactorAuth.service;

import com.reactorAuth.entity.EnumValue;
import com.reactorAuth.repository.EnumValueRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EnumValueService extends BaseService<EnumValue, EnumValueRepository> {

    public EnumValueService(EnumValueRepository enumValueRepository) {
        super(enumValueRepository, "枚举值");
    }

    @Override
    protected void prepareCreate(EnumValue entity) {
        entity.setId(null);
        if (entity.getStatus() == null) entity.setStatus(1);
        if (entity.getSortOrder() == null) entity.setSortOrder(0);
    }

    @Override
    protected void mergeEntity(EnumValue existing, EnumValue incoming) {
        if (incoming.getTypeId() != null) existing.setTypeId(incoming.getTypeId());
        if (incoming.getCode() != null) existing.setCode(incoming.getCode());
        if (incoming.getName() != null) existing.setName(incoming.getName());
        if (incoming.getSortOrder() != null) existing.setSortOrder(incoming.getSortOrder());
        if (incoming.getStatus() != null) existing.setStatus(incoming.getStatus());
    }
}
