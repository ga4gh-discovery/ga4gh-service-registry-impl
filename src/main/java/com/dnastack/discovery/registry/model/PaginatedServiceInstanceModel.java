package com.dnastack.discovery.registry.model;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedServiceInstanceModel {

    private List<ServiceInstanceModel> content;
    private MetadataModel metadata;

}
