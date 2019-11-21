package com.dnastack.discovery.registry.client;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestingServiceInstance {

    private String id;
    private String name;
    private String url;
    private String type;
    private OrganizationModel organization;
    private String version;
    private String description;
    private String documentationUrl;
    private String contactUrl;
    private String environment;

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
