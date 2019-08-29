package com.dnastack.discovery.registry.domain;

import com.dnastack.discovery.registry.domain.converter.ZonedDateTimeAttributeConverter;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ServiceInstance {

    private String id;
    private String name;
    private String type;
    private String url;
    private String description;
    private Organization organization;
    private String contactUrl;
    private String documentationUrl;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private Environment environment;
    private String version;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    public String getId() {
        return id;
    }

    @Convert(converter = ZonedDateTimeAttributeConverter.class)
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    @Convert(converter = ZonedDateTimeAttributeConverter.class)
    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Lob
    public String getDescription() {
        return description;
    }

    @ManyToOne
    public Organization getOrganization() {
        return organization;
    }

    @Enumerated(EnumType.STRING)
    public Environment getEnvironment() {
        return environment;
    }

}
