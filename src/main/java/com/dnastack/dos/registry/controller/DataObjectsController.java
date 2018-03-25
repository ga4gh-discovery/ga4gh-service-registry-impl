package com.dnastack.dos.registry.controller;

import io.swagger.annotations.ApiParam;
import org.joda.time.DateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("ga4gh/registry/dos/v1")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class DataObjectsController implements DataobjectsApi{

    @Override
    @PreAuthorize("hasAuthority('ROLE_dos_user')")
    public ResponseEntity<Ga4ghDataObjectsResponseDto> getDataobjects(
            @ApiParam(value = "The auth token" ) @RequestHeader(value="Authorization", required=false) String authorization,
            @ApiParam(value = "query data objects by specifying a list of comma separated node_ids") @RequestParam(value = "node_ids", required = false) List<String> nodeIds,
            @ApiParam(value = "query data objects by specifying a list of comma separated dos_ids") @RequestParam(value = "dos_ids", required = false) List<String> dosIds,
            @ApiParam(value = "A keyword to search in the field of `name` from data node.") @RequestParam(value = "node_name", required = false) String nodeName,
            @ApiParam(value = "A keyword to search in the field of `name` from data object.") @RequestParam(value = "dos_name", required = false) String dosName,
            @ApiParam(value = "A keyword to search in the field of `version` from data object.") @RequestParam(value = "dos_version", required = false) String dosVersion,
            @ApiParam(value = "query data objects by specific mime_type to match the value in the field of `mime_type` from data object.") @RequestParam(value = "dos_mime_type", required = false) String dosMimeType,
            @ApiParam(value = "A keyword to search in the field of `description` from data node.") @RequestParam(value = "node_description", required = false) String nodeDescription,
            @ApiParam(value = "A keyword to search in the field of `description` from data object.") @RequestParam(value = "dos_description", required = false) String dosDescription,
            @ApiParam(value = "A keyword to search in the field of `aliases` from data node.") @RequestParam(value = "node_alias", required = false) String nodeAlias,
            @ApiParam(value = "A keyword to search in the field of `aliases` from data object.") @RequestParam(value = "dos_alias", required = false) String dosAlias,
            @ApiParam(value = "A combination of `type,value` pair to search in the field of `chechsum` from data object.") @RequestParam(value = "dos_checksum", required = false) String dosChecksum,
            @ApiParam(value = "query data objects by specific creation date range lower bound") @RequestParam(value = "dos_date_created_from", required = false) DateTime dosDateCreatedFrom,
            @ApiParam(value = "query data objects by specific creation date range upper bound") @RequestParam(value = "dos_date_created_to", required = false) DateTime dosDateCreatedTo,
            @ApiParam(value = "query data objects by specific updated date range lower bound") @RequestParam(value = "dos_date_updated_from", required = false) DateTime dosDateUpdatedFrom,
            @ApiParam(value = "query data objects by specific updated date range upper bound") @RequestParam(value = "dos_date_updated_to", required = false) DateTime dosDateUpdatedTo,
            @ApiParam(value = "Page token to identify the record to start retrieval from.") @RequestParam(value = "page_token", required = false) String pageToken,
            @ApiParam(value = "The number of entries to be retrieved.") @RequestParam(value = "page_size", required = false) Integer pageSize){


        return null;
    }
}
