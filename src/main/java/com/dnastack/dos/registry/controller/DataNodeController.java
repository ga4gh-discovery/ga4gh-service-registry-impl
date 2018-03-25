package com.dnastack.dos.registry.controller;

import com.dnastack.dos.registry.exception.InvalidPageTokenException;
import com.dnastack.dos.registry.model.DataNodePage;
import com.dnastack.dos.registry.model.Ga4ghDataNode;
import com.dnastack.dos.registry.model.KeyValuePair;
import com.dnastack.dos.registry.service.DataNodeService;
import com.dnastack.dos.registry.util.ConverterHelper;
import com.dnastack.dos.registry.util.PageTokens;
import com.dnastack.dos.registry.util.SecurityContextUtil;
import com.google.gson.Gson;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("ga4gh/registry/dos/v1")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class DataNodeController implements NodesApi {

    public static final int DEFAULT_PAGE_SIZE = 10;

    private final static Gson gson = new Gson();

    @Autowired
    private DataNodeService dataNodeService;

    @Autowired
    HttpServletRequest httpReq;

    @Override
    @PreAuthorize("hasAuthority('ROLE_dos_owner')")
    public ResponseEntity<Ga4ghDataNodeResponseDto> createNode(@ApiParam(value = "The auth token", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
                                                               @ApiParam(value = "Node creation request", required = true) @Valid @RequestBody Ga4ghDataNodeCreationRequestDto requestBody) {

        //Technically, the authorization is not needed at this point,
        // as the information it contains has been sessioned into SecurityContextHolder (if using Spring security)

        // proposal: ownerId is the same as the id from KeycloakAuthenticationToken token field
        String ownerId = SecurityContextUtil.getUserId();
        Ga4ghDataNode dataNode = dataNodeService.createNode(ownerId, requestBody);
        return formResponseEntity(dataNode, HttpStatus.CREATED);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_dos_owner')")
    public ResponseEntity<Ga4ghDataNodeResponseDto> deleteNode(@ApiParam(value = "UUID of the data node to delete", required = true) @PathVariable("node_id") String nodeId,
                                                               @ApiParam(value = "The auth token", required = true) @RequestHeader(value = "Authorization", required = true) String authorization)

    {
        Ga4ghDataNode dataNode = dataNodeService.deleteNode(nodeId);
        return formResponseEntity(dataNode);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_dos_user') or hasAuthority('ROLE_dos_owner')")
    public ResponseEntity<Ga4ghDataNodeResponseDto> getNodeById(@ApiParam(value = "UUID of the data node to get", required = true) @PathVariable("node_id") String nodeId,
                                                                @ApiParam(value = "The auth token", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        Ga4ghDataNode dataNode = dataNodeService.getNodeById(nodeId);
        return formResponseEntity(dataNode);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_dos_user') or hasAuthority('ROLE_dos_owner')")
    public ResponseEntity<Ga4ghDataNodesResponseDto> getNodes(@ApiParam(value = "The auth token", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
                                                              @ApiParam(value = "A keyword to search in the field of `name` from data nodes.") @RequestParam(value = "name", required = false) String name,
                                                              @ApiParam(value = "A keyword to search in the field of `aliases` from data nodes.") @RequestParam(value = "alias", required = false) String alias,
                                                              @ApiParam(value = "A keyword to search in the field of `description` from data nodes.") @RequestParam(value = "description", required = false) String description,
                                                              @ApiParam(value = "Query data nodes by specifying a list of <key, value> pairs AS STRING type, to match against the meta_date field of the data nodes. NOTE: as for now, OpenAPI does not support object as query parameter properly, this is a work-around solution until it support it!") @RequestParam(value = "metadata", required = false) List<String> metadata,
                                                              @ApiParam(value = "Page token to identify the record to start retrieval from.") @RequestParam(value = "page_token", required = false) String pageToken,
                                                              @ApiParam(value = "The number of entries to be retrieved.") @RequestParam(value = "page_size", required = false) Integer pageSize) {

        DataNodePage dataNodePage = null;

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
                dataNodePage = PageTokens.fromCursorToDataNodePage(pageToken);
            } catch (Exception e) {
                throw new InvalidPageTokenException("Invalid page token: " + pageToken, e.getCause());
            }

            //page size can be re-specified
            if (pageSize != null) {
                Assert.isTrue(pageSize > 0, "Per page must be 1 or greater");
                dataNodePage.setPageSize(pageSize.intValue());
            }

            //make sure user intend to use the cursor only for pagination
            validatePageFromCursorAgainstInput(dataNodePage, name, alias, description, meta);

        } else {

            if (pageSize == null) {
                pageSize = DEFAULT_PAGE_SIZE;
            }

            dataNodePage = new DataNodePage(0, pageSize, name, alias, description, meta);
        }

        Page<Ga4ghDataNode> dataNodesPage = dataNodeService.getNodes(dataNodePage);

        Ga4ghDataNodesResponseDto ga4ghDataNodesResponseDto = new Ga4ghDataNodesResponseDto();
        if (dataNodesPage.hasContent()) {
            ga4ghDataNodesResponseDto.setDosNodes(
                    dataNodesPage.getContent()
                            .stream()
                            .map(ConverterHelper::convertToDto)
                            .collect(Collectors.toList())
            );
        } else {
            //TODO: discuss with Jim if it makes sense if this returns 204 instead of this empty list
            ga4ghDataNodesResponseDto.setDosNodes(new ArrayList<>());
        }

        if (dataNodesPage.hasNext()) {
            ga4ghDataNodesResponseDto.setNextPageToken(PageTokens.toDataNodePageCursor(dataNodePage.next()));
        }

        return new ResponseEntity(ga4ghDataNodesResponseDto, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_dos_owner')")
    public ResponseEntity<Ga4ghDataNodeResponseDto> updateNode(@ApiParam(value = "UUID of the data node to update", required = true) @PathVariable("node_id") String nodeId,
                                                               @ApiParam(value = "The auth token", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
                                                               @ApiParam(value = "Node update request", required = true) @Valid @RequestBody Ga4ghDataNodeUpdateRequestDto requestBody) {
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

    private void validatePageFromCursorAgainstInput(DataNodePage dataNodePage,
                                                    String name,
                                                    String alias,
                                                    String description,
                                                    Map<String, String> meta) {
        try {

            if (!StringUtils.isEmpty(name)) {
                Assert.isTrue(dataNodePage.getName().equals(name), "name specified (" + name
                        + ") is different from the cursor (" + dataNodePage.getDescription() + ")");
            }

            if (!StringUtils.isEmpty(alias)) {
                Assert.isTrue(dataNodePage.getAlias().equals(alias), "alias specified (" + alias
                        + ") is different from the cursor (" + dataNodePage.getAlias() + ")");
            }

            if (!StringUtils.isEmpty(description)) {
                Assert.isTrue(dataNodePage.getDescription().equals(description), "description specified (" + description
                        + ") is different from the cursor (" + dataNodePage.getDescription() + ")");
            }

            if (!CollectionUtils.isEmpty(meta)) {
                Assert.isTrue(dataNodePage.getMeta().toString().equals(meta.toString()), "meta specified (" + description
                        + ") is different from the cursor (" + dataNodePage.getMeta().toString() + ")");
            }

        } catch (Exception e) {
            throw new InvalidPageTokenException(e.getMessage());
        }
    }

}
