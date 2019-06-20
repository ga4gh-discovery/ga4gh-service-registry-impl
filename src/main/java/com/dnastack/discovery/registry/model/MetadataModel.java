package com.dnastack.discovery.registry.model;

import lombok.*;

@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class MetadataModel {

    private String nextPage;
    private String previousPage;

}
