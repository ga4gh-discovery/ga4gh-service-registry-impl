package com.dnastack.discovery.registry.mapper;

import com.dnastack.discovery.registry.domain.ServiceNodeEntity;
import com.dnastack.discovery.registry.model.ServiceNode;

public class ServiceNodeMapper {

    public static ServiceNode map(ServiceNodeEntity document) {
        return ServiceNode.builder()
            .id(document.getId())
            .name(document.getName())
            .url(document.getUrl())
            .created(document.getCreated())
            .description(document.getDescription())
            .aliases(document.getAliases())
            .metadata(document.getMetadata())
            .serviceType(document.getServiceType())
            .healthStatus(document.getHealthStatus())
            .lastHealthUpdated(document.getLastHealthUpdated())
            .build();
    }

    public static ServiceNodeEntity reverseMap(ServiceNode model) {
        return ServiceNodeEntity.builder()
            .id(model.getId())
            .name(model.getName())
            .url(model.getUrl())
            .created(model.getCreated())
            .description(model.getDescription())
            .aliases(model.getAliases())
            .metadata(model.getMetadata())
            .serviceType(model.getServiceType())
            .healthStatus(model.getHealthStatus())
            .lastHealthUpdated(model.getLastHealthUpdated())
            .build();
    }

}
