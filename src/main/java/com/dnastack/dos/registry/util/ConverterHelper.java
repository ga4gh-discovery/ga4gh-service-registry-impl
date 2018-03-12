package com.dnastack.dos.registry.util;

import com.dnastack.dos.registry.controller.Ga4ghDataNodeCreationRequestDto;
import com.dnastack.dos.registry.controller.Ga4ghDataNodeDto;
import com.dnastack.dos.registry.controller.Ga4ghDataNodeUpdateRequestDto;
import com.dnastack.dos.registry.model.Ga4ghDataNode;
import com.dnastack.dos.registry.model.HealthStatus;
import com.google.gson.Gson;
import com.sun.tools.javac.util.List;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

/**
 * Helper class to assist in terms of conversion between business model object and dtos.
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
public class ConverterHelper {

    public static Gson gson = new Gson();

    public static Ga4ghDataNodeDto convertToDto(Ga4ghDataNode dataNode){
        Ga4ghDataNodeDto dto = new Ga4ghDataNodeDto();
        dto.setId(dataNode.getId());
        dto.setName(dataNode.getName());
        dto.setDescription(dataNode.getDescription());
        dto.setUrl(dataNode.getUrl());
        dto.setCreated(dataNode.getCreated());
        if(dataNode.getHealthStatus() != null) {
            dto.setHealthStatus(dataNode.getHealthStatus().name());
        }
        dto.setLastHealthUpdated(dataNode.getLastHealthUpdated());
        dto.setMetaData(dataNode.getMetaData());
        if(!StringUtils.isEmpty(dataNode.getAliases())) {
            //dto.setAliases(dataNode.getAliases().stream().collect(Collectors.toList()));
            //convert it to a list
            List fromJson = gson.fromJson(dataNode.getAliases(), List.class);
            dto.setAliases(fromJson);
        }

        return dto;
    }

    public static void convertFromDataNodeCreationRequestDto(Ga4ghDataNode dataNode,
                                                             Ga4ghDataNodeCreationRequestDto creationRequestDto){
        if(!StringUtils.isEmpty(creationRequestDto.getName())) {
            dataNode.setName(creationRequestDto.getName());
        }
        if(!StringUtils.isEmpty(creationRequestDto.getUrl())) {
            dataNode.setUrl(creationRequestDto.getUrl());
        }
        if(!StringUtils.isEmpty(creationRequestDto.getDescription())) {
            dataNode.setDescription(creationRequestDto.getDescription());
        }
        if(creationRequestDto.getMetaData() != null) {
            dataNode.setMetaData(creationRequestDto.getMetaData());
        }
        if(creationRequestDto.getAliases()!=null) {
            //dataNode.setAliases(creationRequestDto.getAliases().stream().collect(Collectors.toSet()));
            dataNode.setAliases(gson.toJson(creationRequestDto.getAliases()));
        }

        dataNode.setHealthStatus(HealthStatus.UNKNOWN);
        dataNode.setLastHealthUpdated(DateTime.now());

        //TODO: make it a enum
        dataNode.setLastUpdatedBy("CREATOR");
    }

    public static void convertFromDataNodeUpdateRequestDto(Ga4ghDataNode dataNode,
                                                             Ga4ghDataNodeUpdateRequestDto updateRequestDto){
        if(!StringUtils.isEmpty(updateRequestDto.getName())) {
            dataNode.setName(updateRequestDto.getName());
        }
        if(!StringUtils.isEmpty(updateRequestDto.getDescription())) {
            dataNode.setDescription(updateRequestDto.getDescription());
        }
        if(updateRequestDto.getMetaData() != null) {
            dataNode.setMetaData(updateRequestDto.getMetaData());
        }
        if(updateRequestDto.getAliases()!=null) {
            //dataNode.setAliases(updateRequestDto.getAliases().stream().collect(Collectors.toSet()));
            dataNode.setAliases(gson.toJson(updateRequestDto.getAliases()));
        }

        //TODO: make it a enum
        dataNode.setLastUpdatedBy("UPDATER");
    }

}
