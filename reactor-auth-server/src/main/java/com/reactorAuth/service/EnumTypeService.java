package com.reactorAuth.service;

import com.reactorAuth.dto.EnumTypeWithValues;
import com.reactorAuth.entity.EnumType;
import com.reactorAuth.repository.EnumTypeRepository;
import com.reactorAuth.repository.EnumValueRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@Service
public class EnumTypeService extends BaseService<EnumType, EnumTypeRepository> {

    private final EnumTypeRepository enumTypeRepository;
    private final EnumValueRepository enumValueRepository;

    public EnumTypeService(EnumTypeRepository enumTypeRepository, EnumValueRepository enumValueRepository) {
        super(enumTypeRepository, "枚举类型");
        this.enumTypeRepository = enumTypeRepository;
        this.enumValueRepository = enumValueRepository;
    }

    /**
     * 查询所有枚举类型及其枚举值（嵌套结构）
     */
    public Flux<EnumTypeWithValues> findAllWithValues() {
        return enumTypeRepository.findAll()
                .flatMap(type -> enumValueRepository.findByTypeId(type.getId())
                        .collectList()
                        .map(values -> EnumTypeWithValues.builder()
                                .id(type.getId())
                                .code(type.getCode())
                                .name(type.getName())
                                .description(type.getDescription())
                                .status(type.getStatus())
                                .values(values)
                                .build()));
    }

    @Override
    protected void prepareCreate(EnumType entity) {
        entity.setId(null);
        if (entity.getStatus() == null) entity.setStatus(1);
    }

    @Override
    protected void mergeEntity(EnumType existing, EnumType incoming) {
        if (incoming.getCode() != null) existing.setCode(incoming.getCode());
        if (incoming.getName() != null) existing.setName(incoming.getName());
        if (incoming.getDescription() != null) existing.setDescription(incoming.getDescription());
        if (incoming.getStatus() != null) existing.setStatus(incoming.getStatus());
    }
}
