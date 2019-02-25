package com.dnastack.discovery.registry.mapper;

import com.dnastack.discovery.registry.domain.ServiceInstance;
import com.dnastack.discovery.registry.domain.ServiceInstanceModel;

public class ServiceInstanceMapper {

    public static ServiceInstanceModel map(ServiceInstance document) {
        return ServiceInstanceModel.builder()
            .id(document.getId())
            .name(document.getName())
            .url(document.getUrl())
            .createdAt(document.getCreatedAt())
            .description(document.getDescription())
            .aliases(document.getAliases())
            .metadata(document.getMetadata())
            .type(document.getType())
            .build();
    }

    public static ServiceInstance reverseMap(ServiceInstanceModel model) {
        return ServiceInstance.builder()
            .id(model.getId())
            .name(model.getName())
            .url(model.getUrl())
            .createdAt(model.getCreatedAt())
            .description(model.getDescription())
            .aliases(model.getAliases())
            .metadata(model.getMetadata())
            .type(model.getType())
            .build();
    }

}
