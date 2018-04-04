package com.dnastack.dos.registry.execution;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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

    @JsonProperty("current_nodepool_next_page_token")
    // null values of this field indicates no more node available
    private String currentNodePoolNextPageToken;
    @JsonProperty("current_nodepool_ids")
    private List<String> currentNodePoolIds;
    @JsonProperty("current_node_id")
    private String currentNodeId;
    @JsonProperty("current_node_offset")
    //should always be 0 in the current implementation
    private int currentNodeOffset;
    @JsonProperty("current_node_page_token")
    private String currentNodePageToken;
    @JsonProperty("remaining_count_for_page")
    private int remainingCountForPage;

    @JsonCreator
    public PageExecutionContext(
            @JsonProperty("current_nodepool_next_page_token") String currentNodePoolNextPageToken,
            @JsonProperty("current_nodepool_ids") List<String> currentNodePoolIds,
            @JsonProperty("current_node_id") String currentNodeId,
            @JsonProperty("current_node_offset") int currentNodeOffset,
            @JsonProperty("current_node_page_token") String currentNodePageToken,
            @JsonProperty("remaining_count_for_page") int remainingCountForPage) {

        this.currentNodePoolNextPageToken = currentNodePoolNextPageToken;
        this.currentNodePoolIds = currentNodePoolIds;
        this.currentNodeId = currentNodeId;
        this.currentNodeOffset = currentNodeOffset;
        this.currentNodePageToken = currentNodePageToken;
        this.remainingCountForPage = remainingCountForPage;
    }

}
