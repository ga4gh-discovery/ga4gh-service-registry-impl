package com.dnastack.discovery.registry.mapper;

import com.dnastack.discovery.registry.domain.ServiceEntity;
import com.dnastack.discovery.registry.domain.ServiceModel;

public class ServiceNodeMapper {

    public static ServiceModel map(ServiceEntity document) {
        return ServiceModel.builder()
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

    public static ServiceEntity reverseMap(ServiceModel model) {
        return ServiceEntity.builder()
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
