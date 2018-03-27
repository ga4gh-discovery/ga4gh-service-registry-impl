package com.dnastack.dos.registry.controller;

import com.dnastack.dos.registry.exception.PageExecutionContextException;
import com.dnastack.dos.registry.exception.InvalidPageTokenException;
import com.dnastack.dos.registry.execution.PageExecutionContext;
import com.dnastack.dos.registry.model.*;
import com.dnastack.dos.registry.service.DataNodeService;
import com.dnastack.dos.registry.service.DataObjectService;
import com.dnastack.dos.registry.util.PageTokens;
import com.google.gson.Gson;
import io.swagger.annotations.ApiParam;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("ga4gh/registry/dos/v1")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class DataObjectsController implements DataobjectsApi {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${service.objects.default_page_size:50}")
    private int defaultPageSize;

    @Value("${service.objects.default_pool_size:5}")
    private int defaultPoolSize;

    private final static Gson gson = new Gson();

    @Autowired
    private DataObjectService dataObjectService;

    @Autowired
    private DataNodeService dataNodeService;

    //Tihs is the model to dto converter
    @Autowired
    private ModelMapper modelMapper;

    @Override
    @PreAuthorize("hasAuthority('ROLE_dos_user')")
    public ResponseEntity<Ga4ghDataObjectsResponseDto> getDataobjects(
            @ApiParam(value = "The auth token") @RequestHeader(value = "Authorization", required = false) String authorization,
            @ApiParam(value = "query data objects by specifying a list of comma separated node_ids") @RequestParam(value = "node_ids", required = false) List<String> nodeIds,
            @ApiParam(value = "query data objects by specifying a list of comma separated dos_ids") @RequestParam(value = "dos_ids", required = false) List<String> dosIds,
            @ApiParam(value = "A keyword to search in the field of `name` from data node.") @RequestParam(value = "node_name", required = false) String nodeName,
            @ApiParam(value = "A keyword to search in the field of `name` from data object.") @RequestParam(value = "dos_name", required = false) String dosName,
            @ApiParam(value = "A keyword to search in the field of `version` from data object.") @RequestParam(value = "dos_version", required = false) String dosVersion,
            @ApiParam(value = "query data objects by specific mime_type to match the value in the field of `mime_type` from data object.") @RequestParam(value = "dos_mime_type", required = false) String dosMimeType,
            @ApiParam(value = "A keyword to search in the field of `description` from data node.") @RequestParam(value = "node_description", required = false) String nodeDescription,
            @ApiParam(value = "A keyword to search in the field of `description` from data object.") @RequestParam(value = "dos_description", required = false) String dosDescription,
            @ApiParam(value = "A keyword to search in the field of `aliases` from data node.") @RequestParam(value = "node_alias", required = false) String nodeAlias,
            @ApiParam(value = "Query data nodes by specifying a list of <key, value> pairs AS STRING type, to match against the meta_date field of the data nodes. NOTE: as for now, OpenAPI does not support object as query parameter properly, this is a work-around solution until it support it!") @RequestParam(value = "node_metadata", required = false) List<String> nodeMetadata,
            @ApiParam(value = "A keyword to search in the field of `aliases` from data object.") @RequestParam(value = "dos_alias", required = false) String dosAlias,
            @ApiParam(value = "A combination of `type,value` pair to search in the field of `chechsum` from data object.") @RequestParam(value = "dos_checksum", required = false) String dosChecksum,
            @ApiParam(value = "query data objects by specific creation date range lower bound") @RequestParam(value = "dos_date_created_from", required = false) DateTime dosDateCreatedFrom,
            @ApiParam(value = "query data objects by specific creation date range upper bound") @RequestParam(value = "dos_date_created_to", required = false) DateTime dosDateCreatedTo,
            @ApiParam(value = "query data objects by specific updated date range lower bound") @RequestParam(value = "dos_date_updated_from", required = false) DateTime dosDateUpdatedFrom,
            @ApiParam(value = "query data objects by specific updated date range upper bound") @RequestParam(value = "dos_date_updated_to", required = false) DateTime dosDateUpdatedTo,
            @ApiParam(value = "Page token to identify the record to start retrieval from.") @RequestParam(value = "page_token", required = false) String pageToken,
            @ApiParam(value = "The number of entries to be retrieved.") @RequestParam(value = "page_size", required = false) Integer pageSize) {

        Ga4ghDataObjectsResponseDto ga4ghDataObjectsResponseDto = new Ga4ghDataObjectsResponseDto();

        DataObjectPage dataObjectPage = null;

        if (!StringUtils.isEmpty(pageToken)) { // with a cursor key from previous response
            try {
                dataObjectPage = PageTokens.fromCursorToDataObjectPage(pageToken);
            } catch (Exception e) {
                throw new InvalidPageTokenException("Invalid page token: " + pageToken, e.getCause());
            }

            //page size can be re-specified
            if (pageSize != null) {
                Assert.isTrue(pageSize > 0, "Per page must be 1 or greater");
                dataObjectPage.setPageSize(pageSize.intValue());
            }

        } else {

            if (pageSize == null) {
                pageSize = defaultPageSize;
            }

            Map<String, String> nodeMeta = null;
            if (nodeMetadata != null) {
                nodeMeta = nodeMetadata.stream()
                        .map(m -> {
                            return gson.fromJson(m, KeyValuePair.class);
                        })
                        .collect(Collectors.toMap(KeyValuePair::getKey, KeyValuePair::getValue,
                                (oldValue, newValue) -> oldValue,       // if same key, take the old key
                                HashMap::new
                        ));
            }
            DataNodePage dataNodePage = new DataNodePage(0, defaultPoolSize, nodeName, nodeAlias, nodeDescription, nodeMeta, nodeIds);
            Page<Ga4ghDataNode> currentNodePool = null;
            try {
                currentNodePool = dataNodeService.getNodes(dataNodePage);
            } catch (Exception e) {
                logger.error("Error during invoking dataNodeService", e);
            }

            if (currentNodePool == null || !currentNodePool.hasContent() || currentNodePool.getTotalPages() <= 0) {
                //TODO: discuss with Jim if it makes sense if this returns 204 instead of this empty list
                ga4ghDataObjectsResponseDto.setDosObjects(new ArrayList<>());
                return new ResponseEntity(ga4ghDataObjectsResponseDto, HttpStatus.OK);
            }
            //initialize the current node pool
            int currentNodePoolNextPageNumber = dataNodePage.getPageNumber() + 1;
            List<String> currentNodePoolIds = currentNodePool.getContent().stream()
                    .map(Ga4ghDataNode::getId)
                    .collect(Collectors.toList());
            String currentNodeId = currentNodePool.getContent().stream()
                    .map(Ga4ghDataNode::getId)
                    .findFirst()
                    .orElseThrow(() -> new PageExecutionContextException("No data node is found!"));
            int currentNodeOffset = 0;
            String currentNodePageToken = "";

            //TODO: discuss with Jim about the best practise of holding this context. In a session? or in a page token?
            PageExecutionContext pageExecutionContext
                    = new PageExecutionContext(currentNodePoolNextPageNumber,
                                                currentNodePoolIds,
                                                currentNodeId,
                                                currentNodeOffset,
                                                currentNodePageToken);

            dataObjectPage = new DataObjectPage(0, pageSize, dosIds, dosName,
                    dosVersion, dosMimeType, dosDescription, dosAlias, dosChecksum,
                    dosDateCreatedFrom, dosDateCreatedTo, dosDateUpdatedFrom, dosDateUpdatedTo,
                    pageExecutionContext);
        }

        Page<Ga4ghDataObjectOnNode> dataObjectsPage = dataObjectService.getDataObjects(dataObjectPage);

        if (dataObjectsPage.hasContent()) {
            ga4ghDataObjectsResponseDto.setDosObjects(
                    dataObjectsPage.getContent()
                            .stream()
                            .map(data -> {
                                return modelMapper.map(data, Ga4ghDataObjectOnNodeDto.class);
                            })
                            .collect(Collectors.toList())
            );
        } else {
            //TODO: discuss with Jim if it makes sense if this returns 204 instead of this empty list
            ga4ghDataObjectsResponseDto.setDosObjects(new ArrayList<>());
        }

        if (dataObjectsPage.hasNext()) {
            ga4ghDataObjectsResponseDto.setNextPageToken(PageTokens.toDataObjectPageCursor(dataObjectPage.next()));
        }

        return new ResponseEntity(ga4ghDataObjectsResponseDto, HttpStatus.OK);
    }

}
