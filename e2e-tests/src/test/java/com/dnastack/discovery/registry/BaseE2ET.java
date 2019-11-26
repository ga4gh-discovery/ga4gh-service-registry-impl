package com.dnastack.discovery.registry;

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.Base64;

import static junit.framework.TestCase.fail;

public class BaseE2ET {
    public static OpenApiValidationFilter validationFilter;

    @BeforeClass
    public static void setupValidation() {
        if (validationFilter == null) {
            validationFilter = new OpenApiValidationFilter(openapiSpecUrl());
        }
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = requiredEnv("E2E_BASE_URI");
    }

    public static String getBase64Auth() {
        String username = requiredEnv("E2E_BASIC_USERNAME");
        String password = requiredEnv("E2E_BASIC_PASSWORD");
        String credentials = username + ":" + password;
        return Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    public static String openapiSpecUrl() {
        return optionalEnv("E2E_OPENAPI_SPEC_URL", "https://raw.githubusercontent.com/ga4gh-discovery/ga4gh-service-registry/develop/service-registry.yaml");
    }

    public static String requiredEnv(String name) {
        String val = System.getenv(name);
        if (val == null) {
            fail("Environnment variable `" + name + "` is required");
        }
        return val;
    }

    public static String optionalEnv(String name, String defaultValue) {
        String val = System.getenv(name);
        if (val == null) {
            return defaultValue;
        }
        return val;
    }

}
