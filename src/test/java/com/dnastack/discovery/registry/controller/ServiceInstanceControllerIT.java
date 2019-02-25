package com.dnastack.discovery.registry.controller;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import com.dnastack.discovery.registry.TestApplication;
import com.dnastack.discovery.registry.domain.ServiceInstanceType;
import com.dnastack.discovery.registry.model.ServiceInstanceRegistrationRequestModel;
import com.dnastack.discovery.registry.service.ServiceInstanceService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.UUID;
import javax.inject.Inject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SuppressWarnings("Duplicates")
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(classes = TestApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ServiceInstanceControllerIT {

    @LocalServerPort
    private Integer port;
    @Inject
    private ServiceInstanceService nodeService;

    // TODO:
    @Ignore
    @Test
    public void getServiceInstanceById_identifierIsInvalidUUID() {
        String invalidUUID = "abcdefgh-ijkl";
        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .pathParam("serviceId", invalidUUID)
            .get("http://localhost:" + port + "/services/{serviceId}")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void getServiceInstanceById_instanceExistsWithId() {
        ServiceInstanceRegistrationRequestModel service = ServiceInstanceRegistrationRequestModel.builder()
            .name("test-beacon")
            .url("http://beacon-test-random-url.someorg.com")
            .type(ServiceInstanceType.BEACON)
            .email("beacon-admin@someorg.com")
            .description("description")
            .aliases(asList("key1:value1", "key2:value2"))
            .build();
        String serviceId = nodeService.registerInstance(service).getId();

        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .pathParam("serviceId", serviceId)
            .get("http://localhost:" + port + "/services/{serviceId}")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("id", notNullValue())
            .body("createdAt", notNullValue())
            .body("url", equalTo(service.getUrl()))
            .body("name", equalTo(service.getName()))
            .body("email", equalTo(service.getEmail()))
            .body("description", equalTo(service.getDescription()))
            .body("type", equalTo(service.getType().name()))
            .body("aliases", containsInAnyOrder("key1:value1", "key2:value2"));
    }

    @Test
    public void getServiceInstanceById_instanceNotExistsWithId() {
        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .pathParam("serviceId", UUID.randomUUID().toString())
            .get("http://localhost:" + port + "/services/{serviceId}")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void getServiceInstances_noInstanceExists() {
        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .get("http://localhost:" + port + "/services")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("content", empty());
    }

    @Test
    public void getServiceInstances_atLeastOneInstanceExists() {
        ServiceInstanceRegistrationRequestModel service = ServiceInstanceRegistrationRequestModel.builder()
            .name("test-beacon")
            .url("http://beacon-test-random-url.someorg.com")
            .type(ServiceInstanceType.BEACON)
            .email("beacon-admin@someorg.com")
            .description("description")
            .aliases(asList("key1:value1", "key2:value2"))
            .build();
        nodeService.registerInstance(service);

        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .get("http://localhost:" + port + "/services")
            .then()
            .log().everything()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(1))
            .body("content[0].id", notNullValue())
            .body("content[0].url", equalTo(service.getUrl()))
            .body("content[0].name", equalTo(service.getName()))
            .body("content[0].email", equalTo(service.getEmail()))
            .body("content[0].description", equalTo(service.getDescription()))
            .body("content[0].type", equalTo(service.getType().name()))
            .body("content[0].aliases", containsInAnyOrder("key1:value1", "key2:value2"));
    }

    @Test
    public void getServiceInstanceTypes() {
        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .get("http://localhost:" + port + "/services/types")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body(".", contains(ServiceInstanceType.BEACON.name(), ServiceInstanceType.DOS.name()));
    }

}
