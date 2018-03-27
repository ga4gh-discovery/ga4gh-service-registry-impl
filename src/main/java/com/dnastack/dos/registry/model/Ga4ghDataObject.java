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
    private String id = null;
    private String name = null;
    private String size = null;
    private DateTime created = null;
    private DateTime updated = null;
    private String version = null;
    private String mimeType = null;
    @Singular
    private List<Checksum> checksums = new ArrayList<Checksum>();
    @Singular
    private List<URL> urls = null;
    private String description = null;
    @Singular
    private List<String> aliases = null;

    public Ga4ghDataObject id(String id) {
        this.id = id;
        return this;
    }
}

