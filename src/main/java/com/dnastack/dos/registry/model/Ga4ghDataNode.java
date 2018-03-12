package com.dnastack.dos.registry.model;

import lombok.Data;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Entity class for Ga4ghDataNode
 */
@Entity
@Table(name = "dos_node")
@Data
public class Ga4ghDataNode {

    @Id
    private String id;

    private String name = "";
    private String url = "";

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(name = "dos_node_metadata", joinColumns = @JoinColumn(name = "id"))
    private Map<String, String> metaData = new HashMap<>();

    @Enumerated(EnumType.STRING)
    private HealthStatus healthStatus = HealthStatus.UNKNOWN;

    // Mapped as DATETIME (on MySQL)
    // For JSON binding use the format: "1970-01-01T00:00:00.000+0000"
    // @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime created = DateTime.now();

    // Mapped as DATETIME (on MySQL)
    // For JSON binding use the format: "1970-01-01T00:00:00.000+0000"
    // @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastHealthUpdated = DateTime.now();

    private String lastUpdatedBy = "";

    private String description = "";

//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(name = "dos_node_aliases")
//    private Set<String> aliases = new HashSet<>();

    private String aliases = ""; //put these aliases as a json string for the ease of pageable search

    @NotNull
    private String customerId = "";

}

