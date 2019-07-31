package com.dnastack.discovery.registry.model;

import com.dnastack.discovery.registry.domain.Maturity;
import lombok.*;

import java.time.ZonedDateTime;
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
    private String url;
    private String type;
    private String organization;
    private String version;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private String description;
    private String documentationUrl;
    private String contactUrl;
    private Maturity maturity;
    private Map<String, String> extension;

}

