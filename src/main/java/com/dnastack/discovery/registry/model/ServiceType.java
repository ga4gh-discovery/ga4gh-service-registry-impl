package com.dnastack.discovery.registry.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceType {
    String group;
    String artifact;
    String version;

    @JsonCreator
    public static ServiceType fromString(String oldStyle) {
        if (oldStyle == null) {
            return null;
        }
        String[] gav = oldStyle.split(":");
        return new ServiceType(gav[0], gav[1], gav[2]);
    }

    public String toString() {
        return group + ":" + artifact + ":" + version;
    }
}
