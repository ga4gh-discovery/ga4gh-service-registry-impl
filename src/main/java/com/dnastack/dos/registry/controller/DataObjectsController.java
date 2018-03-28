package com.dnastack.dos.registry.controller;

import com.dnastack.dos.registry.exception.InvalidPageTokenException;
import com.dnastack.dos.registry.execution.PageExecutionContext;
import com.dnastack.dos.registry.model.DataNodePage;
import com.dnastack.dos.registry.model.DataObjectPage;
import com.dnastack.dos.registry.model.Ga4ghDataObjectOnNode;
import com.dnastack.dos.registry.model.KeyValuePair;
import com.dnastack.dos.registry.service.DataNodeService;
import com.dnastack.dos.registry.service.DataObjectService;
import com.dnastack.dos.registry.util.PageExecutionContextHelper;
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
import org.springframework.util.CollectionUtils;
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
            @ApiParam(value = "The auth token" ) @RequestHeader(value="Authorization", required=false) String authorization,
            @ApiParam(value = "query data objects by specifying a list of comma separated node_ids") @RequestParam(value = "node_ids", required = false) List<String> nodeIds,
            @ApiParam(value = "query data objects by specifying a list of comma separated dos_ids NOTE: current not supported in the dos-schema spec") @RequestParam(value = "dos_ids", required = false) List<String> dosIds,
            @ApiParam(value = "A keyword to search in the field of `name` from data node.") @RequestParam(value = "node_name", required = false) String nodeName,
            @ApiParam(value = "If provided will only return Data Objects with the given `name`. NOTE: current not supported in the dos-schema spec") @RequestParam(value = "dos_name", required = false) String dosName,
            @ApiParam(value = "If provided will only return Data Objects with the given `version`. NOTE: current not supported in the dos-schema spec") @RequestParam(value = "dos_version", required = false) String dosVersion,
            @ApiParam(value = "If provided will only return Data Objects with the given `mime_type`. NOTE: current not supported in the dos-schema spec") @RequestParam(value = "dos_mime_type", required = false) String dosMimeType,
            @ApiParam(value = "A keyword to search in the field of `description` from data node.") @RequestParam(value = "node_description", required = false) String nodeDescription,
            @ApiParam(value = "A keyword to search in the field of `description` from data object. NOTE: current not supported in the dos-schema spec") @RequestParam(value = "dos_description", required = false) String dosDescription,
            @ApiParam(value = "A keyword to search in the field of `aliases` from data node.") @RequestParam(value = "node_alias", required = false) String nodeAlias,
            @ApiParam(value = "Query data nodes by specifying a list of <key, value> pairs AS STRING type, to match against the meta_date field of the data nodes. NOTE: as for now, OpenAPI does not support object as query parameter properly, this is a work-around solution until it support it!") @RequestParam(value = "node_metadata", required = false) List<String> nodeMetadata,
            @ApiParam(value = "If provided will only return Data Objects with the given alias.") @RequestParam(value = "dos_alias", required = false) String dosAlias,
            @ApiParam(value = "If provided will return only Data Objects with a that URL matches this string.") @RequestParam(value = "dos_url", required = false) String dosUrl,
            @ApiParam(value = "The hexlified checksum that one would like to match on.") @RequestParam(value = "dos_checksum", required = false) String dosChecksum,
            @ApiParam(value = "query data objects by specific creation date range lower bound NOTE: current not supported in the dos-schema spec") @RequestParam(value = "dos_date_created_from", required = false) DateTime dosDateCreatedFrom,
            @ApiParam(value = "query data objects by specific creation date range upper bound NOTE: current not supported in the dos-schema spec") @RequestParam(value = "dos_date_created_to", required = false) DateTime dosDateCreatedTo,
            @ApiParam(value = "query data objects by specific updated date range lower bound NOTE: current not supported in the dos-schema spec") @RequestParam(value = "dos_date_updated_from", required = false) DateTime dosDateUpdatedFrom,
            @ApiParam(value = "query data objects by specific updated date range upper bound NOTE: current not supported in the dos-schema spec") @RequestParam(value = "dos_date_updated_to", required = false) DateTime dosDateUpdatedTo,
            @ApiParam(value = "The continuation token, which is used to page through large result sets. To get the next page of results, set this parameter to the value of `next_page_token` from the previous response.") @RequestParam(value = "page_token", required = false) String pageToken,
            @ApiParam(value = "Specifies the maximum number of results to return in a single page. If unspecified, a system default will be used.") @RequestParam(value = "page_size", required = false) Integer pageSize) {

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

            //initialize the current node pool
            //TODO: discuss with Jim about the best practise of holding this context. In a session? or in a page token?
            PageExecutionContext pageExecutionContext
                    = PageExecutionContextHelper.formPageExecutionContext(dataNodeService, dataNodePage);
            if (pageExecutionContext == null) {
                //TODO: discuss with Jim if it makes sense if this returns 204 instead of this empty list
                ga4ghDataObjectsResponseDto.setDosObjects(new ArrayList<>());
                return new ResponseEntity(ga4ghDataObjectsResponseDto, HttpStatus.OK);
            }

            dataObjectPage = new DataObjectPage(0, pageSize, dosIds, dosName,
                    dosVersion, dosMimeType, dosDescription, dosAlias, dosUrl, dosChecksum,
                    dosDateCreatedFrom, dosDateCreatedTo, dosDateUpdatedFrom, dosDateUpdatedTo,
                    pageExecutionContext);
        }

        List<Ga4ghDataObjectOnNode> dataObjects = dataObjectService.getDataObjects(dataObjectPage);

        if (!CollectionUtils.isEmpty(dataObjects)) {
            ga4ghDataObjectsResponseDto.setDosObjects(
                    dataObjects
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

        if (dataObjectPage.getPageExecutionContext()!=null
                && CollectionUtils.isEmpty(dataObjectPage.getPageExecutionContext().getCurrentNodePoolIds())) {
            ga4ghDataObjectsResponseDto.setNextPageToken(PageTokens.toDataObjectPageCursor(dataObjectPage.next()));
        }

        return new ResponseEntity(ga4ghDataObjectsResponseDto, HttpStatus.OK);
    }

}
