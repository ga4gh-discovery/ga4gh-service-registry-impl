package com.dnastack.discovery.registry.client;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.LinkedHashMap;

/**
 * Service type object for end-to-end tests.
 * <p>
 *     Implementation note: we chose to subclass LinkedHashMap instead of using a Lombok data class because
 *     there are several places in the test suite where RestAssured has already deserialized the response as
 *     generic types (String, List, Map) and this implementation choice allows us to use TestingServiceType
 *     in test assertions where Map instances are needed.
 * </p>
 */
public class TestingServiceType extends LinkedHashMap<String, String> {

    @JsonCreator
    public TestingServiceType(String group, String artifact, String version) {
        put("group", group);
        put("artifact", artifact);
        put("version", version);
    }
}
