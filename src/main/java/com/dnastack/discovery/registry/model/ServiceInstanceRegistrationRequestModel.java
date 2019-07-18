package com.dnastack.discovery.registry.model;

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

    private String url;
    private String type;
    private String contactUrl;
    private String name;
    private String description;
    private Map<String, String> extension;

}
