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
                .contactUrl(entity.getContactUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .description(entity.getDescription())
                .type(entity.getType())
                .version(entity.getVersion())
                .organization(OrganizationMapper.map(entity.getOrganization()))
                .documentationUrl(entity.getDocumentationUrl())
                .environment(entity.getEnvironment())
                .build();
    }

    public static ServiceInstance reverseMap(ServiceInstanceModel model) {
        return ServiceInstance.builder()
                .id(model.getId())
                .name(model.getName())
                .url(model.getUrl())
                .contactUrl(model.getContactUrl())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .description(model.getDescription())
                .type(model.getType())
                .version(model.getVersion())
                .organization(OrganizationMapper.reverseMap(model.getOrganization()))
                .documentationUrl(model.getDocumentationUrl())
                .environment(model.getEnvironment())
                .build();
    }

    public static ServiceInstance reverseMap(ServiceInstanceRegistrationRequestModel model) {
        return ServiceInstance.builder()
                .name(model.getName())
                .url(model.getUrl())
                .contactUrl(model.getContactUrl())
                .description(model.getDescription())
                .type(model.getType())
                .version(model.getVersion())
                .organization(OrganizationMapper.reverseMap(model.getOrganization()))
                .documentationUrl(model.getDocumentationUrl())
                .environment(model.getEnvironment())
                .build();
    }

}
