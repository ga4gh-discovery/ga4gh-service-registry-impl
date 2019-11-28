package com.dnastack.discovery.registry;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class ServiceInfoE2ET extends BaseE2ET {

    @Test
    public void getServiceInfo() {
        // @formatter:off
        RestAssured.given()
                .filter(validationFilter)
                .accept(ContentType.JSON)
                .log().method()
                .log().uri()
                .get("/service-info")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("id", equalTo("org.ga4gh.service-registry"))
                .body("name", equalTo(optionalEnv("E2E_SERVICE_INFO_EXPECTED_NAME", "GA4GH Service Registry Reference Implementation")))
                .body("type", equalTo("org.ga4gh:service-registry:1.0.0"))
                .body("description", equalTo(optionalEnv("E2E_SERVICE_INFO_EXPECTED_DESCRIPTION", "Reference implementation of GA4GH Service Registry")))
                .body("organization.name", equalTo(optionalEnv("E2E_SERVICE_INFO_EXPECTED_ORGANIZATION_NAME", "GA4GH")))
                .body("organization.url", equalTo(optionalEnv("E2E_SERVICE_INFO_EXPECTED_ORGANIZATION_URL", "https://example.com")))
                .body("contactUrl", equalTo(optionalEnv("E2E_SERVICE_INFO_EXPECTED_CONTACT_URL", "mailto:support@example.com")))
                .body("documentationUrl", equalTo(optionalEnv("E2E_SERVICE_INFO_EXPECTED_DOCUMENTATION_URL", "https://github.com/ga4gh-discovery/ga4gh-service-registry")))
                .body("environment", equalTo(optionalEnv("E2E_SERVICE_INFO_EXPECTED_ENVIRONMENT", "dev")))
                .body("version", equalTo(optionalEnv("E2E_SERVICE_INFO_EXPECTED_VERSION", "1.0.0")));
        // @formatter:on

    }

}
