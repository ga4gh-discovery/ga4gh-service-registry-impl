package com.dnastack.dos.registry.controller;

import com.dnastack.dos.registry.model.Ga4ghDataNode;
import com.dnastack.dos.registry.service.DataNodeService;
import com.dnastack.dos.registry.util.ConverterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("ga4gh/registry/dos/v1")
public class DataNodeController implements NodesApi{

    @Autowired
    private DataNodeService dataNodeService;

    @Override
    public ResponseEntity<Ga4ghDataNodeResponseDto> createNode(String authorization, Ga4ghDataNodeCreationRequestDto requestBody) {

        //Technically, the authorization is not needed as this point,
        // as the information it contains has been sessioned into SecurityContextHolder (if using Spring security)
        //TODO: discuss with Jim how the original `customer` should be maintained...
        // proposal: resource owner is one `customer`, it can grant users with different scope of authority

        Ga4ghDataNode dataNode = dataNodeService.createNode(requestBody);
        return formResponseEntity(dataNode);
    }

    @Override
    public ResponseEntity<Ga4ghDataNodeResponseDto> deleteNode(String nodeId, String authorization) {
        Ga4ghDataNode dataNode = dataNodeService.deleteNode(nodeId);
        return formResponseEntity(dataNode);
    }

    @Override
    public ResponseEntity<Ga4ghDataNodeResponseDto> getNodeById(String nodeId, String authorization) {
        Ga4ghDataNode dataNode = dataNodeService.getNodeById(nodeId);
        return formResponseEntity(dataNode);
    }

    @Override
    public ResponseEntity<Ga4ghDataNodesResponseDto> getNodes(String authorization, String name, String alias, String description, String pageToken, Integer pageSize) {

        //TODO: ask Jim if about the alias data-type... should be a Set or a simple JSON string?

        //TODO: get customerId from security context holder
        String customerId = "";
        List<Ga4ghDataNode> dataNodes = dataNodeService.getNodes(customerId, name, alias, description, pageToken, pageSize);
        Ga4ghDataNodesResponseDto ga4ghDataNodesResponseDto = new Ga4ghDataNodesResponseDto();
        ga4ghDataNodesResponseDto.setDosNodes(
            dataNodes
                    .stream()
                    .map(ConverterHelper::convertToDto)
                    .collect(Collectors.toList())
        );

        return new ResponseEntity(ga4ghDataNodesResponseDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Ga4ghDataNodeResponseDto> updateNode(String nodeId, String authorization, Ga4ghDataNodeUpdateRequestDto requestBody) {
        Ga4ghDataNode dataNode = dataNodeService.updateNode(nodeId, requestBody);
        return formResponseEntity(dataNode);
    }

    /**
     * Forms an http response entity
     */
    private ResponseEntity formResponseEntity(Ga4ghDataNode ga4ghDataNode) {

        Assert.notNull(ga4ghDataNode, "ga4ghDataNode cannot be null");
        Ga4ghDataNodeResponseDto ga4ghDataNodeResponseDto = new Ga4ghDataNodeResponseDto();
        ga4ghDataNodeResponseDto.setDosNode(ConverterHelper.convertToDto(ga4ghDataNode));

        return new ResponseEntity(ga4ghDataNodeResponseDto, HttpStatus.OK);
    }

}
