package com.reactorAuth.service;

import com.reactorAuth.entity.Role;
import com.reactorAuth.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RoleService extends BaseService<Role, RoleRepository> {

    public RoleService(RoleRepository roleRepository) {
        super(roleRepository, "角色");
    }

    @Override
    protected void prepareCreate(Role role) {
        role.setId(null);
        if (role.getStatus() == null) role.setStatus(1);
    }

    @Override
    protected void mergeEntity(Role existing, Role incoming) {
        if (incoming.getName() != null) existing.setName(incoming.getName());
        if (incoming.getDescription() != null) existing.setDescription(incoming.getDescription());
        if (incoming.getStatus() != null) existing.setStatus(incoming.getStatus());
    }
}
