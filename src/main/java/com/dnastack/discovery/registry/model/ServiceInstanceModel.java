package com.dnastack.discovery.registry.model;

import com.dnastack.discovery.registry.domain.Environment;
import lombok.*;

import java.time.ZonedDateTime;

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

}
