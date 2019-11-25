package com.dnastack.discovery.registry.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.lang.Nullable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationModel {

    @JsonIgnore
    private String id;

    private String name;
    private @Nullable String url;

}

