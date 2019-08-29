package com.dnastack.discovery.registry;

import io.restassured.RestAssured;
import org.junit.Before;

import java.util.Base64;

import static junit.framework.TestCase.fail;

public class BaseE2ET {

    @Before
    public void setUp() {
        RestAssured.baseURI = requiredEnv("E2E_BASE_URI");
    }

    protected String getBase64Auth() {
        String username = requiredEnv("E2E_BASIC_USERNAME");
        String password = requiredEnv("E2E_BASIC_PASSWORD");
        String credentials = username + ":" + password;
        return Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    protected String requiredEnv(String name) {
        String val = System.getenv(name);
        if (val == null) {
            fail("Environnment variable `" + name + "` is required");
        }
        return val;
    }

    protected String optionalEnv(String name, String defaultValue) {
        String val = System.getenv(name);
        if (val == null) {
            return defaultValue;
        }
        return val;
    }

}
