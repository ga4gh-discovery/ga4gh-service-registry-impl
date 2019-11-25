package com.dnastack.discovery.registry.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Environment {
    PROD("prod"), DEV("dev"), TEST("test");

    private final String environment;

    public static Environment fromString(String text) {
        if (text != null) {
            for (Environment b : Environment.values()) {
                if (text.equalsIgnoreCase(b.toString())) {
                    return b;
                }
            }
        }
        return null;
    }

    Environment(String environment) {
        this.environment = environment;
    }

    @Override
    @JsonValue
    public String toString() {
        return environment;
    }

}
