package com.dnastack.discovery.registry;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.hamcrest.Matchers.*;

public class ServicesEndpointE2ET extends BaseE2ET {

    @Test
    public void getServiceInstanceById_instanceNotExistsWithId() {
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
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void getServiceInstances_atLeastTwoInstancesExpected() {
        RestAssured.given()
                .accept(ContentType.JSON)
                .header("Authorization", "Basic " + getBase64Auth())
                .log().method()
                .log().uri()
                .get("/services")
                .then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(HttpStatus.OK.value());
    }

}
