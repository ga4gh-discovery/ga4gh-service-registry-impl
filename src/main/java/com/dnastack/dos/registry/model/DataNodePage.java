package com.dnastack.dos.registry.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Represents the data node page used to encapsulate the request to retrieve data nodes
 */
@Data
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataNodePage {

    private final int pageNumber;
    //pageSize can be reset
    private int pageSize;
    private final String name;
    private final String alias;
    private final String description;
    @JsonProperty("meta")
    private final Map<String, String> meta;

    @JsonCreator
    public DataNodePage(@JsonProperty("pageNumber") final int pageNumber,
                        @JsonProperty("pageSize") int pageSize,
                        @JsonProperty("name") final String name,
                        @JsonProperty("alias") final String alias,
                        @JsonProperty("description")final String description,
                        @JsonProperty("meta") final Map<String, String> meta) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.name = name;
        this.alias = alias;
        this.description = description;
        this.meta = meta;
    }

    @JsonIgnore
    public DataNodePage next() {
        return new DataNodePage(
                pageNumber + 1,
                pageSize,
                this.name,
                this.alias,
                this.description,
                this.meta);
    }

}
