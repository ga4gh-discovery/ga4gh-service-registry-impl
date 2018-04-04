package com.dnastack.dos.registry.model;

import lombok.*;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Ga4ghDataObjectOnNode
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ga4ghDataObject {
    private String id;
    private String name;
    private String size;
    private DateTime created;
    private DateTime updated;
    private String version;
    private String mimeType;
    @Singular
    private List<Checksum> checksums;
    @Singular
    private List<URL> urls;
    private String description;
    @Singular
    private List<String> aliases;

    public Ga4ghDataObject id(String id) {
        this.id = id;
        return this;
    }
}

