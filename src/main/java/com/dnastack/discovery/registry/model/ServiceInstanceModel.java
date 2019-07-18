package com.dnastack.discovery.registry.model;

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
    private String contactUrl;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private String description;
    private Map<String, String> extension;
    private String type;

}

