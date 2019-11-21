package com.dnastack.discovery.registry.domain;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Organization {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class Key implements Serializable {
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

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String url;
}
