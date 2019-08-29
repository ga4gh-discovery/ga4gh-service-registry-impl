package com.dnastack.discovery.registry.mapper;

import com.dnastack.discovery.registry.domain.Organization;
import com.dnastack.discovery.registry.model.OrganizationModel;

public class OrganizationMapper {

    public static OrganizationModel map(Organization entity) {
        return OrganizationModel.builder().name(entity.getName()).url(entity.getUrl()).build();
    }

    public static Organization reverseMap(OrganizationModel model) {
        return Organization.builder().name(model.getName()).url(model.getUrl()).build();
    }

}
