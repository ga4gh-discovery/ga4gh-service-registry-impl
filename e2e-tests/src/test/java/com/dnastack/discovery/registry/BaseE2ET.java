package com.dnastack.discovery.registry;

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import com.dnastack.discovery.registry.client.TestingOrganizationModel;
import com.dnastack.discovery.registry.client.TestingServiceInstance;
import com.dnastack.discovery.registry.client.TestingServiceType;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;
import java.util.Base64;

import static junit.framework.TestCase.fail;

public class BaseE2ET {
    public static OpenApiValidationFilter validationFilter;
    public static String TEST_REALM = "e2e-test-" + System.currentTimeMillis();

    /**
     * Creates a service instance with the given info and a randomly named organization.
     */
    TestingServiceInstance makeServiceInstance(String name, String url, TestingServiceType type) {
        TestingOrganizationModel org = new TestingOrganizationModel(
                "Org" + Math.random(),
                "http://example.com/" + Math.random());
        return makeServiceInstance(name, url, type, org);
    }

    /**
     * Creates a service instance with the given info.
     */
    TestingServiceInstance makeServiceInstance(String name, String url, TestingServiceType type, TestingOrganizationModel org) {
        return TestingServiceInstance.builder()
                .name(name)
                .url(url)
                .type(type)
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .documentationUrl("http://beacon-test-random-url.someorg.com")
                .version("1." + Math.random())
                .organization(org)
                .environment("test")
                .build();
    }

    @BeforeClass
    public static void setupValidation() throws IOException {
        if (validationFilter == null) {
            validationFilter = new OpenApiValidationFilter("/service-registry-schema.yaml");
        }
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = requiredEnv("E2E_BASE_URI");
        RestAssured.config = RestAssuredConfig.config()
                .objectMapperConfig(new ObjectMapperConfig().jackson2ObjectMapperFactory(
                        (cls, charset) -> {
                            ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
                            mapper.registerModule(new ParameterNamesModule());
                            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                            return mapper;
                        }
                ));
    }

    public static String getBase64Auth() {
        String username = requiredEnv("E2E_BASIC_USERNAME");
        String password = requiredEnv("E2E_BASIC_PASSWORD");
        String credentials = username + ":" + password;
        return Base64.getEncoder().encodeToString(credentials.getBytes());
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
