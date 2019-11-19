package com.dnastack.discovery.registry.domain;

import com.dnastack.discovery.registry.domain.converter.ZonedDateTimeAttributeConverter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ServiceInstance {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
    private String additionalPropertiesJson;

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

    @ManyToOne
    public Organization getOrganization() {
        return organization;
    }

    @Enumerated(EnumType.STRING)
    public Environment getEnvironment() {
        return environment;
    }

    @Transient
    public Map<String, Object> getAdditionalProperties() {
        try {
            return OBJECT_MAPPER.readValue(additionalPropertiesJson, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Couldn't parse JSON: " + additionalPropertiesJson, e);
        }
    }

    @Transient
    public void setAdditionalProperties(Map<String, Object> properties) {
        try {
            additionalPropertiesJson = OBJECT_MAPPER.writeValueAsString(properties);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Couldn't convert to JSON: " + properties, e);
        }
    }
}
