package com.dnastack.dos.registry.controller;

import com.dnastack.dos.registry.exception.ServiceException;
import com.dnastack.dos.registry.model.Ga4ghDataNode;
import com.dnastack.dos.registry.util.PageTokens;
import com.dnastack.dos.registry.service.DataNodeService;
import com.dnastack.dos.registry.util.ConverterHelper;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("ga4gh/registry/dos/v1")
public class DataNodeController implements NodesApi{

    //TODO: remove this field after discussion with Jim to finalize the way how authentication works
    public static final String OWNER_ID = "demo-customer";

    public static final int DEFAULT_PAGE_SIZE = 10;

    @Autowired
    private DataNodeService dataNodeService;

    @Override
    public ResponseEntity<Ga4ghDataNodeResponseDto> createNode(@ApiParam(value = "The auth token" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization,
                                                               @ApiParam(value = "Node creation request" ,required=true ) @Valid @RequestBody Ga4ghDataNodeCreationRequestDto requestBody)
    {

        //Technically, the authorization is not needed as this point,
        // as the information it contains has been sessioned into SecurityContextHolder (if using Spring security)
        //TODO: discuss with Jim how the original `owner` should be maintained...
        // proposal: resource owner is one `customer`, it can grant users with different scope of authority


        //TODO: get ownerId from security context holder
        String customerId = OWNER_ID;
        Ga4ghDataNode dataNode = dataNodeService.createNode(customerId, requestBody);
        return formResponseEntity(dataNode, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Ga4ghDataNodeResponseDto> deleteNode(@ApiParam(value = "UUID of the data node to delete",required=true ) @PathVariable("node_id") String nodeId,
                                                               @ApiParam(value = "The auth token" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization)

    {
        Ga4ghDataNode dataNode = dataNodeService.deleteNode(nodeId);
        return formResponseEntity(dataNode);
    }

    @Override
    public ResponseEntity<Ga4ghDataNodeResponseDto> getNodeById(@ApiParam(value = "UUID of the data node to get",required=true ) @PathVariable("node_id") String nodeId,
                                                                @ApiParam(value = "The auth token" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization)
    {
        Ga4ghDataNode dataNode = dataNodeService.getNodeById(nodeId);
        return formResponseEntity(dataNode);
    }

    @Override
    public ResponseEntity<Ga4ghDataNodesResponseDto> getNodes(@ApiParam(value = "The auth token" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization,
                                                              @ApiParam(value = "A keyword to search in the field of `name` from data nodes.") @RequestParam(value = "name", required = false) String name,
                                                              @ApiParam(value = "A keyword to search in the field of `aliases` from data nodes.") @RequestParam(value = "alias", required = false) String alias,
                                                              @ApiParam(value = "A keyword to search in the field of `description` from data nodes.") @RequestParam(value = "description", required = false) String description,
                                                              @ApiParam(value = "Page token to identify the record to start retrieval from.") @RequestParam(value = "page_token", required = false) String pageToken,
                                                              @ApiParam(value = "The number of entries to be retrieved.") @RequestParam(value = "page_size", required = false) Integer pageSize)
    {


        //TODO: get ownerId from security context holder
        String ownerId = OWNER_ID;

        com.dnastack.dos.registry.model.Page page;

        try {
            page = Objects.isNull(pageToken) ?
                    new com.dnastack.dos.registry.model.Page(1) : PageTokens.fromCursor(pageToken);
        } catch (Exception e){
            throw new ServiceException("Page Token (" + pageToken + ") is not decode-able ");
        }

        if(pageSize==null){
            pageSize = DEFAULT_PAGE_SIZE;
        }

        Pageable pageable = new PageRequest(page.getPageNumber(), pageSize);

        if(name==null){
            name = "";
        }
        if(alias==null){
            alias = "";
        }
        if(description==null){
            description = "";
        }

        Page<Ga4ghDataNode> dataNodesPage = dataNodeService.getNodes(ownerId, name, alias, description, pageable);

        Ga4ghDataNodesResponseDto ga4ghDataNodesResponseDto = new Ga4ghDataNodesResponseDto();
        if(dataNodesPage.hasContent()) {
            ga4ghDataNodesResponseDto.setDosNodes(
                    dataNodesPage.getContent()
                            .stream()
                            .map(ConverterHelper::convertToDto)
                            .collect(Collectors.toList())
            );
        } else {
            ga4ghDataNodesResponseDto.setDosNodes(new ArrayList<>());
        }

        if(dataNodesPage.hasNext()) {
            ga4ghDataNodesResponseDto.setNextPageToken(PageTokens.toCursor(page.next()));
        }

        return new ResponseEntity(ga4ghDataNodesResponseDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Ga4ghDataNodeResponseDto> updateNode(@ApiParam(value = "UUID of the data node to update",required=true ) @PathVariable("node_id") String nodeId,
                                                               @ApiParam(value = "The auth token" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization,
                                                               @ApiParam(value = "Node update request" ,required=true )  @Valid @RequestBody Ga4ghDataNodeUpdateRequestDto requestBody)
    {
        Ga4ghDataNode dataNode = dataNodeService.updateNode(nodeId, requestBody);
        return formResponseEntity(dataNode);
    }

    /**
     * Forms an http response entity with OK
     */
    private ResponseEntity formResponseEntity(Ga4ghDataNode ga4ghDataNode) {
        return formResponseEntity(ga4ghDataNode, HttpStatus.OK);
    }

    /**
     * Forms an http response entity with OK
     */
    private ResponseEntity formResponseEntity(Ga4ghDataNode ga4ghDataNode, HttpStatus status) {

        Assert.notNull(ga4ghDataNode, "ga4ghDataNode cannot be null");
        Ga4ghDataNodeResponseDto ga4ghDataNodeResponseDto = new Ga4ghDataNodeResponseDto();
        ga4ghDataNodeResponseDto.setDosNode(ConverterHelper.convertToDto(ga4ghDataNode));

        return new ResponseEntity(ga4ghDataNodeResponseDto, status);
    }

}
