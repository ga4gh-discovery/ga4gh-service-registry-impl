package com.dnastack.discovery.registry;

import com.dnastack.discovery.registry.BaseE2ET;
import com.dnastack.discovery.registry.client.TestingServiceType;
import io.restassured.http.ContentType;
import org.junit.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;

public class ServiceAuthenticationE2ET extends BaseE2ET {

    @Test
    public void listServicesShouldNotRequireAuthorization() {
        // @formatter:off
        given()
                .accept(ContentType.JSON)
                .log().method()
                .log().uri()
                .header("Service-Registry-Realm", TEST_REALM)
                .when()
                .get("/services")
                .then()
                .log().ifValidationFails()
                .statusCode(200);
        // @formatter:on
    }

    @Test
    public void postServicesShouldRequireAuthorization() {
        // @formatter:off
        String createdServiceUrl = given()
                .accept(ContentType.JSON)
                .log().method()
                .log().uri()
                .header("Service-Registry-Realm", TEST_REALM)
                .contentType("application/json")
                .body(makeServiceInstance("test", "http://test.com", new TestingServiceType("test", "test", "test")))
         .when()
                .post("/services")
         .then()
                .log().ifValidationFails()
                .statusCode(401)
                .extract().header("Location");
        // @formatter:on
    }

    @Test
    public void putServicesShouldRequireAuthorization() {
        // @formatter:off
        String createdServiceUrl = given()
                .accept(ContentType.JSON)
                .log().method()
                .log().uri()
                .header("Service-Registry-Realm", TEST_REALM)
                .contentType("application/json")
                .body(makeServiceInstance("test", "http://test.com", new TestingServiceType("test", "test", "test")))
        .when()
                .put("/services/" + UUID.randomUUID())
        .then()
                .log().ifValidationFails()
                .statusCode(401)
                .extract().header("Location");
        // @formatter:on
    }

    @Test
    public void deleteServicesShouldRequireAuthorization() {
        // @formatter:off
        String createdServiceUrl = given()
                .accept(ContentType.JSON)
                .log().method()
                .log().uri()
                .header("Service-Registry-Realm", TEST_REALM)
        .when()
                .delete("/services/" + UUID.randomUUID())
        .then()
                .log().ifValidationFails()
                .statusCode(401)
                .extract().header("Location");
        // @formatter:on
    }

        @Test
    public void listTypesServicesShouldNotRequireAuthorization() {
        // @formatter:off
        given()
                .accept(ContentType.JSON)
                .log().method()
                .log().uri()
                .header("Service-Registry-Realm", TEST_REALM)
        .when()
                .get("/services/types")
        .then()
                .log().ifValidationFails()
                .statusCode(200);
        // @formatter:on
    }

}
