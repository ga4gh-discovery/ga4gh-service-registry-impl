package com.dnastack.discovery.registry.model;

import com.dnastack.discovery.registry.domain.Environment;
import lombok.*;

import java.util.Map;

@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInstanceRegistrationRequestModel {

    private String name;
    private String url;
    private String type;
    private OrganizationModel organization;
    private String version;
    private String description;
    private String documentationUrl;
    private String contactUrl;
    private Environment environment;

}
