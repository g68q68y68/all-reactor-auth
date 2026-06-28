package com.reactorAuth.service;

import com.reactorAuth.entity.Permission;
import com.reactorAuth.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PermissionService extends BaseService<Permission, PermissionRepository> {

    public PermissionService(PermissionRepository permissionRepository) {
        super(permissionRepository, "权限");
    }

    @Override
    protected void prepareCreate(Permission permission) {
        permission.setId(null);
        if (permission.getStatus() == null) permission.setStatus(1);
    }

    @Override
    protected void mergeEntity(Permission existing, Permission incoming) {
        if (incoming.getName() != null) existing.setName(incoming.getName());
        if (incoming.getType() != null) existing.setType(incoming.getType());
        if (incoming.getUrl() != null) existing.setUrl(incoming.getUrl());
        if (incoming.getMethod() != null) existing.setMethod(incoming.getMethod());
        if (incoming.getStatus() != null) existing.setStatus(incoming.getStatus());
    }
}
