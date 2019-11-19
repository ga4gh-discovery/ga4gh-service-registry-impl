package com.dnastack.discovery.registry.mapper;

import com.dnastack.discovery.registry.domain.ServiceInstance;
import com.dnastack.discovery.registry.model.ServiceInstanceModel;
import com.dnastack.discovery.registry.model.ServiceInstanceRegistrationRequestModel;

public class ServiceInstanceMapper {

    public static ServiceInstanceModel toDto(ServiceInstance entity) {
        return ServiceInstanceModel.builder()
                .id(entity.getId())
                .name(entity.getName())
                .url(entity.getUrl())
                .contactUrl(entity.getContactUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .description(entity.getDescription())
                .type(entity.getType())
                .version(entity.getVersion())
                .organization(OrganizationMapper.toDto(entity.getOrganization()))
                .documentationUrl(entity.getDocumentationUrl())
                .environment(entity.getEnvironment())
                .additionalProperties(entity.getAdditionalProperties())
                .build();
    }

    public static ServiceInstance toEntity(ServiceInstanceModel model) {
        ServiceInstance entity = ServiceInstance.builder()
                .id(model.getId())
                .name(model.getName())
                .url(model.getUrl())
                .contactUrl(model.getContactUrl())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .description(model.getDescription())
                .type(model.getType())
                .version(model.getVersion())
                .organization(OrganizationMapper.toEntity(model.getOrganization()))
                .documentationUrl(model.getDocumentationUrl())
                .environment(model.getEnvironment())
                .build();
        entity.setAdditionalProperties(model.getAdditionalProperties());
        return entity;
    }

    public static ServiceInstance toEntity(ServiceInstanceRegistrationRequestModel model) {
        ServiceInstance entity = ServiceInstance.builder()
                .name(model.getName())
                .url(model.getUrl())
                .contactUrl(model.getContactUrl())
                .description(model.getDescription())
                .type(model.getType())
                .version(model.getVersion())
                .organization(OrganizationMapper.toEntity(model.getOrganization()))
                .documentationUrl(model.getDocumentationUrl())
                .environment(model.getEnvironment())
                .build();
        entity.setAdditionalProperties(model.getAdditionalProperties());
        return entity;
    }

}
