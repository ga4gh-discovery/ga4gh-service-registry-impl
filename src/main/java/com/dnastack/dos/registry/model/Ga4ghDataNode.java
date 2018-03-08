package com.dnastack.dos.registry.model;

import lombok.Data;
import org.joda.time.DateTime;

import javax.persistence.*;
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

    private String name;
    private String url;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(name = "dos_node_metadata", joinColumns = @JoinColumn(name = "id"))
    private Map<String, String> metaData;

    @Enumerated(EnumType.STRING)
    private HealthStatus healthStatus;

    // Mapped as DATETIME (on MySQL)
    // For JSON binding use the format: "1970-01-01T00:00:00.000+0000"
    // @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime created = DateTime.now();

    // Mapped as DATETIME (on MySQL)
    // For JSON binding use the format: "1970-01-01T00:00:00.000+0000"
    // @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastHealthUpdated;

    private String lastUpdatedBy;

    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "dos_node_aliases")
    private Set<String> aliases;

    private String customerId;

}

