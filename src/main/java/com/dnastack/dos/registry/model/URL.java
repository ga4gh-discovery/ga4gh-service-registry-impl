package com.dnastack.dos.registry.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class URL {
    private String url = null;
    @Singular private SystemMetadata systemMetadata = null;
    @Singular private UserMetadata userMetadata = null;

}

