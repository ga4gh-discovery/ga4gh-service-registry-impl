package com.dnastack.discovery.registry.mapper;

import com.dnastack.discovery.registry.domain.ServiceNodeEntity;
import com.dnastack.discovery.registry.model.ServiceNode;

public class ServiceNodeMapper {

    public static ServiceNode map(ServiceNodeEntity document) {
        return ServiceNode.builder()
            .id(document.getId())
            .name(document.getName())
            .url(document.getUrl())
            .createdAt(document.getCreatedAt())
            .description(document.getDescription())
            .aliases(document.getAliases())
            .metadata(document.getMetadata())
            .type(document.getType())
            .health(document.getHealth())
            .build();
    }

    public static ServiceNodeEntity reverseMap(ServiceNode model) {
        return ServiceNodeEntity.builder()
            .id(model.getId())
            .name(model.getName())
            .url(model.getUrl())
            .createdAt(model.getCreatedAt())
            .description(model.getDescription())
            .aliases(model.getAliases())
            .metadata(model.getMetadata())
            .type(model.getType())
            .health(model.getHealth())
            .build();
    }

}
