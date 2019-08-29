package com.dnastack.discovery.registry.domain;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Organization {

    private String id;
    private String name;
    private String url;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    public String getId() {
        return id;
    }

    @Column(unique = true, nullable = false)
    public String getName() {
        return name;
    }

    @Column(unique = true, nullable = false)
    public String getUrl() {
        return url;
    }
}
