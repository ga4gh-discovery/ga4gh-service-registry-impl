package com.dnastack.dos.registry.controller;

import com.dnastack.dos.registry.model.Ga4ghDataNode;
import com.dnastack.dos.registry.service.DataNodeService;
import com.dnastack.dos.registry.util.ConverterHelper;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public ResponseEntity<Ga4ghDataNodeResponseDto> createNode(@ApiParam(value = "The auth token" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization,
                                                               @ApiParam(value = "Node creation request" ,required=true ) @Valid @RequestBody Ga4ghDataNodeCreationRequestDto requestBody)
    {

        //Technically, the authorization is not needed as this point,
        // as the information it contains has been sessioned into SecurityContextHolder (if using Spring security)
        //TODO: discuss with Jim how the original `customer` should be maintained...
        // proposal: resource owner is one `customer`, it can grant users with different scope of authority

        //TODO: ask Jim if the `id` filed in the dos_nod object should be encoded?

        Ga4ghDataNode dataNode = dataNodeService.createNode(requestBody);
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
