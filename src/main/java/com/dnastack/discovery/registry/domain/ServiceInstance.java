package com.dnastack.discovery.registry.domain;

import com.dnastack.discovery.registry.domain.converter.ZonedDateTimeAttributeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class Key implements Serializable {
        @ManyToOne
        @JoinColumn(name = "realm")
        private String realm;
        private String id;

        /**
         * Creates a new key with a strong random ID for use in the given realm.
         */
        public static Key inRealm(String realm) {
            return new Key(realm, UUID.randomUUID().toString());
        }
    }

    @EmbeddedId
    private Key key;

    private String name;
    private String type;
    private String url;
    private String description;

    @ManyToOne
    @MapsId("key.realm")
    @JoinColumns({
            @JoinColumn(name = "realm", referencedColumnName = "realm"),
            @JoinColumn(name = "organization_id", referencedColumnName = "id")
    })
    private Organization organization;

    private String contactUrl;
    private String documentationUrl;

    @Convert(converter = ZonedDateTimeAttributeConverter.class)
    private ZonedDateTime createdAt;

    @Convert(converter = ZonedDateTimeAttributeConverter.class)
    private ZonedDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private Environment environment;

    private String version;
    private String additionalPropertiesJson;

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
