package com.dnastack.discovery.registry.model;

import lombok.*;

@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationModel {

    private String name;
    private String url;

}

