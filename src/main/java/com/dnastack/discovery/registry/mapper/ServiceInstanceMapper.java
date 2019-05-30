package com.dnastack.discovery.registry.mapper;

import com.dnastack.discovery.registry.domain.ServiceInstance;
import com.dnastack.discovery.registry.model.ServiceInstanceModel;
import com.dnastack.discovery.registry.model.ServiceInstanceRegistrationRequestModel;

public class ServiceInstanceMapper {

    public static ServiceInstanceModel map(ServiceInstance entity) {
        return ServiceInstanceModel.builder()
            .id(entity.getId())
            .name(entity.getName())
            .url(entity.getUrl())
            .email(entity.getContactUrl())
            .createdAt(entity.getCreatedAt())
            .description(entity.getDescription())
            .aliases(entity.getAliases())
            .metadata(entity.getMetadata())
            .type(entity.getType())
            .build();
    }

    public static ServiceInstance reverseMap(ServiceInstanceRegistrationRequestModel model) {
        return ServiceInstance.builder()
            .name(model.getName())
            .url(model.getUrl())
            .contactUrl(model.getEmail())
            .description(model.getDescription())
            .aliases(model.getAliases())
            .metadata(model.getMetadata())
            .type(model.getType())
            .build();
    }

}
