package com.dnastack.dos.registry.controller;

import com.dnastack.dos.registry.exception.InvalidPageTokenException;
import com.dnastack.dos.registry.model.ServiceNodePage;
import com.dnastack.dos.registry.model.ServiceNode;
import com.dnastack.dos.registry.model.KeyValuePair;
import com.dnastack.dos.registry.service.ServiceNodeService;
import com.dnastack.dos.registry.util.ConverterHelper;
import com.dnastack.dos.registry.util.PageTokens;
import com.dnastack.dos.registry.util.SecurityContextUtil;
import com.google.gson.Gson;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("ga4gh/registry/v1")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ServiceNodeController implements NodesApi {

    @Value("${service.nodes.default_page_size:10}")
    private int defaultPageSize;

    private final static Gson gson = new Gson();

    @Autowired
    private ServiceNodeService serviceNodeService;

    @Override
    @PreAuthorize("hasAuthority('ROLE_dos_owner')")
    public ResponseEntity<ServiceNodeResponseDto> createNode(
            @ApiParam(value = "The auth token" ,required=true)
                @RequestHeader(value="Authorization", required=true) String authorization,
            @ApiParam(value = "Service node creation request" ,required=true )
                @Valid @RequestBody ServiceNodeCreationRequestDto requestBody)
    {

        //Technically, the authorization is not needed at this point,
        // as the information it contains has been sessioned into SecurityContextHolder (if using Spring security)

        // proposal: ownerId is the same as the id from KeycloakAuthenticationToken token field
        String ownerId = SecurityContextUtil.getUserId();
        ServiceNode serviceNode = serviceNodeService.createNode(ownerId, requestBody);
        return formResponseEntity(serviceNode, HttpStatus.CREATED);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_dos_owner')")
    public ResponseEntity<ServiceNodeResponseDto> deleteNode(
            @ApiParam(value = "UUID of the service node to delete",required=true )
                @PathVariable("node_id") String nodeId,
            @ApiParam(value = "The auth token" ,required=true)
                @RequestHeader(value="Authorization", required=true) String authorization)
    {
        ServiceNode serviceNode = serviceNodeService.deleteNode(nodeId);
        return formResponseEntity(serviceNode);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_dos_user') or hasAuthority('ROLE_dos_owner')")
    public ResponseEntity<ServiceNodeResponseDto> getNodeById(
            @ApiParam(value = "UUID of the service node to get",required=true )
                @PathVariable("node_id") String nodeId,
            @ApiParam(value = "The auth token" ,required=true)
                @RequestHeader(value="Authorization", required=true) String authorization)
    {
        ServiceNode serviceNode = serviceNodeService.getNodeById(nodeId);
        return formResponseEntity(serviceNode);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_dos_user') or hasAuthority('ROLE_dos_owner')")
    public ResponseEntity<ServiceNodesResponseDto> getNodes(
            @ApiParam(value = "The auth token" ,required=true)
                @RequestHeader(value="Authorization", required=true) String authorization,
            @ApiParam(value = "A keyword to search in the field of `name` from service nodes.")
                @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "A keyword to search in the field of `aliases` from service nodes.")
                @RequestParam(value = "alias", required = false) String alias,
            @ApiParam(value = "A keyword to search in the field of `description` from service nodes.")
                @RequestParam(value = "description", required = false) String description,
            @ApiParam(value = "Query service nodes by specifying a list of <key, value> pairs AS STRING type, to match against the meta_date field of the service nodes. NOTE: as for now, OpenAPI does not support object as query parameter properly, this is a work-around solution until it support it!")
                @RequestParam(value = "metadata", required = false) List<String> metadata,
            @ApiParam(value = "Page token to identify the record to start retrieval from.")
                @RequestParam(value = "page_token", required = false) String pageToken,
            @ApiParam(value = "The number of entries to be retrieved.")
                @RequestParam(value = "page_size", required = false) Integer pageSize)
    {

        ServiceNodePage serviceNodePage = null;

        //form teh meta object
        Map<String, String> meta = null;
        if (metadata != null) {
            meta = metadata.stream()
                    .map(m -> {
                        return gson.fromJson(m, KeyValuePair.class);
                    })
                    .collect(Collectors.toMap(KeyValuePair::getKey, KeyValuePair::getValue,
                            (oldValue, newValue) -> oldValue,       // if same key, take the old key
                            HashMap::new
                    ));
        }

        if (!StringUtils.isEmpty(pageToken)) { // with a cursor key from previous response
            try {
                serviceNodePage = PageTokens.fromCursorToDataNodePage(pageToken);
            } catch (Exception e) {
                throw new InvalidPageTokenException("Invalid page token: " + pageToken, e.getCause());
            }

            //page size can be re-specified
            if (pageSize != null) {
                Assert.isTrue(pageSize > 0, "Per page must be 1 or greater");
                serviceNodePage.setPageSize(pageSize.intValue());
            }

            //make sure user intend to use the cursor only for pagination
            validatePageFromCursorAgainstInput(serviceNodePage, name, alias, description, meta);

        } else {

            if (pageSize == null) {
                pageSize = defaultPageSize;
            }

            serviceNodePage = new ServiceNodePage(0, pageSize, name, alias, description, meta, null);
        }

        Page<ServiceNode> serviceNodesPage = serviceNodeService.getNodes(serviceNodePage);

        ServiceNodesResponseDto serviceNodesResponseDto = new ServiceNodesResponseDto();
        if (serviceNodesPage.hasContent()) {
            serviceNodesResponseDto.setDosNodes(
                    serviceNodesPage.getContent()
                            .stream()
                            .map(ConverterHelper::convertToDto)
                            .collect(Collectors.toList())
            );
        } else {
            //does it makes more sense if this returns 204 instead of this empty list?
            serviceNodesResponseDto.setDosNodes(new ArrayList<>());
        }

        if (serviceNodesPage.hasNext()) {
            serviceNodesResponseDto.setNextPageToken(PageTokens.toDataNodePageCursor(serviceNodePage.next()));
        }

        return new ResponseEntity(serviceNodesResponseDto, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_dos_owner')")
    public ResponseEntity<ServiceNodeResponseDto> updateNode(
            @ApiParam(value = "UUID of the service node to update",required=true )
                @PathVariable("node_id") String nodeId,
            @ApiParam(value = "The auth token" ,required=true)
                @RequestHeader(value="Authorization", required=true) String authorization,
            @ApiParam(value = "Service node update request" ,required=true )
                @Valid @RequestBody ServiceNodeUpdateRequestDto requestBody)
    {
        ServiceNode serviceNode = serviceNodeService.updateNode(nodeId, requestBody);
        return formResponseEntity(serviceNode);
    }

    /**
     * Forms an http response entity with OK
     */
    private ResponseEntity formResponseEntity(ServiceNode serviceNode) {
        return formResponseEntity(serviceNode, HttpStatus.OK);
    }

    /**
     * Forms an http response entity with OK
     */
    private ResponseEntity formResponseEntity(ServiceNode serviceNode, HttpStatus status) {

        Assert.notNull(serviceNode, "serviceNode cannot be null");
        ServiceNodeResponseDto serviceNodeResponseDto = new ServiceNodeResponseDto();
        serviceNodeResponseDto.setDosNode(ConverterHelper.convertToDto(serviceNode));

        return new ResponseEntity(serviceNodeResponseDto, status);
    }

    private void validatePageFromCursorAgainstInput(ServiceNodePage serviceNodePage,
                                                    String name,
                                                    String alias,
                                                    String description,
                                                    Map<String, String> meta) {
        try {

            if (!StringUtils.isEmpty(name)) {
                Assert.isTrue(serviceNodePage.getName().equals(name), "name specified (" + name
                        + ") is different from the cursor (" + serviceNodePage.getDescription() + ")");
            }

            if (!StringUtils.isEmpty(alias)) {
                Assert.isTrue(serviceNodePage.getAlias().equals(alias), "alias specified (" + alias
                        + ") is different from the cursor (" + serviceNodePage.getAlias() + ")");
            }

            if (!StringUtils.isEmpty(description)) {
                Assert.isTrue(serviceNodePage.getDescription().equals(description), "description specified (" + description
                        + ") is different from the cursor (" + serviceNodePage.getDescription() + ")");
            }

            if (!CollectionUtils.isEmpty(meta)) {
                Assert.isTrue(serviceNodePage.getMeta().toString().equals(meta.toString()), "meta specified (" + description
                        + ") is different from the cursor (" + serviceNodePage.getMeta().toString() + ")");
            }

        } catch (Exception e) {
            throw new InvalidPageTokenException(e.getMessage());
        }
    }

}
