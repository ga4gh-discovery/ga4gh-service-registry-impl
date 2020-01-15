package com.dnastack.discovery.registry.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.jdbi.v3.core.annotation.Unmappable;
import org.jdbi.v3.core.mapper.Nested;
import org.jdbi.v3.json.Json;
import org.springframework.lang.Nullable;

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
    private ServiceType type;
    private @Nullable String url;
    private @Nullable String description;
    @Getter(onMethod_ = @Nested("org"))
    private OrganizationModel organization;
    private @Nullable String contactUrl;
    private @Nullable String documentationUrl;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private @Nullable Environment environment;
    private String version;

    // catch-all for additional attributes (in service registry spec and custom)
    @JsonIgnore
    @Builder.Default
    @Getter(onMethod_ = {@JsonAnyGetter, @Json})
    private Map<String, Object> additionalProperties = new LinkedHashMap<>();

    @JsonAnySetter
    @Unmappable
    public void setAdditionalProperty(String key, Object value) {
        additionalProperties.put(key, value);
    }

}
