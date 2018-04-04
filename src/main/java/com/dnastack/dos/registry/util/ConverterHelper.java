package com.dnastack.dos.registry.util;

import com.dnastack.dos.registry.controller.ServiceNodeCreationRequestDto;
import com.dnastack.dos.registry.controller.ServiceNodeDto;
import com.dnastack.dos.registry.controller.ServiceNodeTypeEnumDto;
import com.dnastack.dos.registry.controller.ServiceNodeUpdateRequestDto;
import com.dnastack.dos.registry.model.ServiceNode;
import com.dnastack.dos.registry.model.HealthStatus;
import com.dnastack.dos.registry.model.ServiceNodeTypeEnum;
import com.google.gson.Gson;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Helper class to assist in terms of conversion between business model object and dtos.
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
public class ConverterHelper {

    public static Gson gson = new Gson();

    public static ServiceNodeDto convertToDto(ServiceNode serviceNode) {
        ServiceNodeDto dto = new ServiceNodeDto();
        dto.setId(serviceNode.getId());
        dto.setName(serviceNode.getName());
        dto.setDescription(serviceNode.getDescription());
        dto.setUrl(serviceNode.getUrl());
        dto.setCreated(serviceNode.getCreated());
        if (serviceNode.getHealthStatus() != null) {
            dto.setHealthStatus(serviceNode.getHealthStatus().name());
        }
        dto.setLastHealthUpdated(serviceNode.getLastHealthUpdated());
        dto.setMetaData(serviceNode.getMetaData());
        if (!StringUtils.isEmpty(serviceNode.getAliases())) {
            //dto.setAliases(serviceNode.getAliases().stream().collect(Collectors.toList()));
            //convert it to a list
            List fromJson = gson.fromJson(serviceNode.getAliases(), List.class);
            dto.setAliases(fromJson);
        }
        dto.setServiceType(ServiceNodeTypeEnumDto.valueOf(serviceNode.getServiceType()));

        return dto;
    }

    public static void convertFromDataNodeCreationRequestDto(ServiceNode serviceNode,
                                                             ServiceNodeCreationRequestDto creationRequestDto) {
        if (!StringUtils.isEmpty(creationRequestDto.getName())) {
            serviceNode.setName(creationRequestDto.getName());
        }
        if (!StringUtils.isEmpty(creationRequestDto.getUrl())) {
            serviceNode.setUrl(creationRequestDto.getUrl());
        }
        if (!StringUtils.isEmpty(creationRequestDto.getDescription())) {
            serviceNode.setDescription(creationRequestDto.getDescription());
        }
        if (creationRequestDto.getMetaData() != null) {
            serviceNode.setMetaData(creationRequestDto.getMetaData());
        }
        if (creationRequestDto.getAliases() != null) {
            //serviceNode.setAliases(creationRequestDto.getAliases().stream().collect(Collectors.toSet()));
            serviceNode.setAliases(gson.toJson(creationRequestDto.getAliases()));
        }

        serviceNode.setHealthStatus(HealthStatus.UNKNOWN);
        serviceNode.setLastHealthUpdated(DateTime.now());

        //TODO: make it a enum
        serviceNode.setLastUpdatedBy("CREATOR");

        if(creationRequestDto.getServiceType() != null) {
            serviceNode.setServiceType(creationRequestDto.getServiceType().name());
        } else {
            //default to DOS
            serviceNode.setServiceType(ServiceNodeTypeEnum.DOS.name());
        }
    }

    public static void convertFromDataNodeUpdateRequestDto(ServiceNode serviceNode,
                                                           ServiceNodeUpdateRequestDto updateRequestDto) {
        if (!StringUtils.isEmpty(updateRequestDto.getName())) {
            serviceNode.setName(updateRequestDto.getName());
        }
        if (!StringUtils.isEmpty(updateRequestDto.getDescription())) {
            serviceNode.setDescription(updateRequestDto.getDescription());
        }
        if (updateRequestDto.getMetaData() != null) {
            serviceNode.setMetaData(updateRequestDto.getMetaData());
        }
        if (updateRequestDto.getAliases() != null) {
            //serviceNode.setAliases(updateRequestDto.getAliases().stream().collect(Collectors.toSet()));
            serviceNode.setAliases(gson.toJson(updateRequestDto.getAliases()));
        }

        //TODO: make it a enum
        serviceNode.setLastUpdatedBy("UPDATER");
    }

}
