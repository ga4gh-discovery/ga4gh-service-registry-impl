package com.dnastack.dos.registry.util;

import com.dnastack.dos.registry.controller.Ga4ghDataNodeCreationRequestDto;
import com.dnastack.dos.registry.controller.Ga4ghDataNodeDto;
import com.dnastack.dos.registry.controller.Ga4ghDataNodeUpdateRequestDto;
import com.dnastack.dos.registry.model.Ga4ghDataNode;
import com.dnastack.dos.registry.model.HealthStatus;
import org.joda.time.DateTime;

import java.util.stream.Collectors;

/**
 * Helper class to assist in terms of conversion between business model object and dtos.
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
public class ConverterHelper {

    public static Ga4ghDataNodeDto convertToDto(Ga4ghDataNode dataNode){
        Ga4ghDataNodeDto dto = new Ga4ghDataNodeDto();
        dto.setId(dataNode.getId());
        dto.setName(dataNode.getName());
        dto.setDescription(dataNode.getDescription());
        dto.setUrl(dataNode.getUrl());
        dto.setCreated(dataNode.getCreated());
        dto.setHealthStatus(dataNode.getHealthStatus().name());
        dto.setLastHealthUpdated(dataNode.getLastHealthUpdated());
        dto.setMetaData(dataNode.getMetaData());
        dto.setAliases(dataNode.getAliases().stream().collect(Collectors.toList()));

        return dto;
    }

    public static void convertFromDataNodeCreationRequestDto(Ga4ghDataNode dataNode,
                                                             Ga4ghDataNodeCreationRequestDto creationRequestDto){
        dataNode.setName(creationRequestDto.getName());
        dataNode.setUrl(creationRequestDto.getUrl());
        dataNode.setMetaData(creationRequestDto.getMetaData());
        dataNode.setAliases(creationRequestDto.getAliases().stream().collect(Collectors.toSet()));
        dataNode.setHealthStatus(HealthStatus.UNKNOWN);
        dataNode.setLastHealthUpdated(DateTime.now());

        //TODO: make it a enum
        dataNode.setLastUpdatedBy("CREATOR");
    }

    public static void convertFromDataNodeUpdateRequestDto(Ga4ghDataNode dataNode,
                                                             Ga4ghDataNodeUpdateRequestDto updateRequestDto){
        dataNode.setName(updateRequestDto.getName());
        dataNode.setDescription(updateRequestDto.getDescription());
        dataNode.setMetaData(updateRequestDto.getMetaData());
        dataNode.setAliases(updateRequestDto.getAliases().stream().collect(Collectors.toSet()));

        //TODO: make it a enum
        dataNode.setLastUpdatedBy("UPDATER");
    }

}
