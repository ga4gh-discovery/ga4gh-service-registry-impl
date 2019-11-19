package com.dnastack.discovery.registry.model;

import com.dnastack.discovery.registry.domain.Environment;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInstanceModel {

    private String id;
    private String name;
    private String type;
    private String url;
    private String description;
    private OrganizationModel organization;
    private String contactUrl;
    private String documentationUrl;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private Environment environment;
    private String version;

    // catch-all for additional attributes (in service registry spec and custom)
    @JsonIgnore
    @Builder.Default
    private Map<String, Object> additionalProperties = new LinkedHashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String key, Object value) {
        additionalProperties.put(key, value);
    }

}
