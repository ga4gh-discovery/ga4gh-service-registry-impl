package com.dnastack.discovery.registry.domain;


import com.dnastack.discovery.registry.domain.converter.ZonedDateTimeAttributeConverter;
import com.dnastack.discovery.registry.model.ServiceType;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ServiceNodeEntity {

    private String id;
    private String name;
    private String url;
    private ZonedDateTime created;
    private String description;
    private List<String> aliases;
    private Map<String, String> metadata;
    private ServiceType type;
    private Health health;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    @Convert(converter = ZonedDateTimeAttributeConverter.class)
    public ZonedDateTime getCreated() {
        return created;
    }

    public String getDescription() {
        return description;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    public List<String> getAliases() {
        return aliases;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    public Map<String, String> getMetadata() {
        return metadata;
    }

    @Enumerated(value = EnumType.STRING)
    public ServiceType getType() {
        return type;
    }

    @Embedded
    public Health getHealth() {
        return health;
    }

}
