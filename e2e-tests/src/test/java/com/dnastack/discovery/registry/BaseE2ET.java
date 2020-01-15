package com.dnastack.discovery.registry;

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
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
