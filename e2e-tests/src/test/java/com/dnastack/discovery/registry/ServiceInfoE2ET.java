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
                .body("name", equalTo("GA4GH Service Registry Reference Implementation"))
                .body("type", equalTo("org.ga4gh.service-registry:1.0.0"))
                .body("description", equalTo("Reference implementation of GA4GH Service Registry"))
                .body("organization.name", equalTo("GA4GH"))
                .body("organization.url", equalTo("https://example.com"))
                .body("contactUrl", equalTo("mailto:support@example.com"))
                .body("documentationUrl", equalTo("https://github.com/ga4gh-discovery/ga4gh-service-registry"))
                .body("environment", equalTo("dev"))
                .body("version", equalTo("1.0.0"));
        // @formatter:on

    }

}
