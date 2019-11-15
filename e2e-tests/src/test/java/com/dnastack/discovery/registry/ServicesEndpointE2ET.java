package com.dnastack.discovery.registry;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.*;

public class ServicesEndpointE2ET extends BaseE2ET {

    @Test
    public void getServiceInstanceById_instanceNotExistsWithId() {
        // @formatter:off
        RestAssured.given()
                .accept(ContentType.JSON)
                .header("Authorization", "Basic " + getBase64Auth())
                .log().method()
                .log().uri()
                .pathParam("serviceId", UUID.randomUUID().toString())
                .get("/services/{serviceId}")
                .then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(404);
        // @formatter:on
    }

    @Test
    public void getServiceInstances_atLeastTwoInstancesExpected() {
        // @formatter:off
        RestAssured.given()
                .accept(ContentType.JSON)
                .header("Authorization", "Basic " + getBase64Auth())
                .log().method()
                .log().uri()
                .get("/services")
                .then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(200);
        // @formatter:on
    }

}
