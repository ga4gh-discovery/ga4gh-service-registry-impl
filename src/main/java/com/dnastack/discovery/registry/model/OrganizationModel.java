package com.dnastack.discovery.registry.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationModel {

    private String name;
    private String url;

}

