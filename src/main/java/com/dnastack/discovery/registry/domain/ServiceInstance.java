package com.dnastack.discovery.registry.domain;


import com.dnastack.discovery.registry.domain.converter.ZonedDateTimeAttributeConverter;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Map;

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
    private String url;
    private String contactUrl;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private String description;
    private Map<String, String> extension;
    private String type;

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

    public String getContactUrl() {
        return contactUrl;
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

    @ElementCollection(fetch = FetchType.EAGER)
    public Map<String, String> getExtension() {
        return extension;
    }

    public String getType() {
        return type;
    }

}
