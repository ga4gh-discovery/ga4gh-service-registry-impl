package com.dnastack.dos.registry.execution;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Represents the data node page used to encapsulate the request to retrieve data nodes
 */
@Data
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class PageExecutionContext {

    @JsonProperty("current_nodepool_next_page_number")
    private int currentNodePoolNextPageNumber;
    @JsonProperty("current_nodepool_ids")
    private List<String> currentNodePoolIds;
    @JsonProperty("current_node_id")
    private String currentNodeId;
    @JsonProperty("current_node_offset")
    private int currentNodeOffset;
    @JsonProperty("current_node_page_token")
    private String currentNodePageToken;

    @JsonCreator
    public PageExecutionContext(
            @JsonProperty("current_nodepool_next_page_number") int currentNodePoolNextPageNumber,
            @JsonProperty("current_nodepool_ids") List<String> currentNodePoolIds,
            @JsonProperty("current_node_id") String currentNodeId,
            @JsonProperty("current_node_offset") int currentNodeOffset,
            @JsonProperty("current_node_page_token") String currentNodePageToken) {

        this.currentNodePoolNextPageNumber = currentNodePoolNextPageNumber;
        this.currentNodePoolIds = currentNodePoolIds;
        this.currentNodeId = currentNodeId;
        this.currentNodeOffset = currentNodeOffset;
        this.currentNodePageToken = currentNodePageToken;
    }

}
