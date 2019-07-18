package com.dnastack.discovery.registry.controller;

import com.dnastack.discovery.registry.TestApplication;
import com.dnastack.discovery.registry.model.ServiceInstanceRegistrationRequestModel;
import com.dnastack.discovery.registry.service.ServiceInstanceService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.UUID;

import static org.hamcrest.Matchers.*;

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

    @Test
    public void getServiceInstanceById_instanceExistsWithId() {
        ServiceInstanceRegistrationRequestModel service = ServiceInstanceRegistrationRequestModel.builder()
                .name("test-beacon")
                .url("http://beacon-test-random-url.someorg.com")
                .type("urn:ga4gh:beacon")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
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
                .body("contactUrl", equalTo(service.getContactUrl()))
                .body("description", equalTo(service.getDescription()))
                .body("type", equalTo(service.getType()));
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
                .type("urn:ga4gh:beacon")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
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
                .body("content[0].contactUrl", equalTo(service.getContactUrl()))
                .body("content[0].description", equalTo(service.getDescription()))
                .body("content[0].type", equalTo(service.getType()));
    }

    @Test
    public void getServiceInstanceTypes() {
        nodeService.registerInstance(ServiceInstanceRegistrationRequestModel.builder()
                .name("test-beacon")
                .url("http://beacon-test-random-url.someorg.com")
                .type("urn:ga4gh:beacon")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .build());
        nodeService.registerInstance(ServiceInstanceRegistrationRequestModel.builder()
                .name("test-beacon")
                .url("http://beacon-test-random-url.someorg.com")
                .type("urn:ga4gh:beacon-aggregator")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .build());
        nodeService.registerInstance(ServiceInstanceRegistrationRequestModel.builder()
                .name("test-beacon")
                .url("http://beacon-test-random-url.someorg.com")
                .type("urn:ga4gh:user-portal")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .build());

        RestAssured.given()
                .accept(ContentType.JSON)
                .log().method()
                .log().uri()
                .get("http://localhost:" + port + "/services/types")
                .then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(".", contains(
                        "urn:ga4gh:beacon",
                        "urn:ga4gh:beacon-aggregator",
                        "urn:ga4gh:user-portal"
                ));
    }

    @Test
    public void getServiceInstanceTypes_empty() {
        RestAssured.given()
                .accept(ContentType.JSON)
                .log().method()
                .log().uri()
                .get("http://localhost:" + port + "/services/types")
                .then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(".", empty());
    }

    @Test
    public void deleteServiceInstanceById_instanceExistsWithId() {
        ServiceInstanceRegistrationRequestModel service = ServiceInstanceRegistrationRequestModel.builder()
                .name("test-beacon")
                .url("http://beacon-test-random-url.someorg.com")
                .type("urn:ga4gh:beacon")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .build();
        String serviceId = nodeService.registerInstance(service).getId();

        RestAssured.given()
                .accept(ContentType.JSON)
                .log().method()
                .log().uri()
                .pathParam("serviceId", serviceId)
                .auth().basic("dev", "dev")
                .delete("http://localhost:" + port + "/services/{serviceId}")
                .then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void deregisterServiceInstanceById_instanceExistsWithId_ensureNoSideEffect() {
        ServiceInstanceRegistrationRequestModel service1 = ServiceInstanceRegistrationRequestModel.builder()
                .name("test-beacon-1")
                .url("http://beacon-test-random-url1.someorg.com")
                .type("urn:ga4gh:beacon")
                .contactUrl("beacon-admin@someorg.com")
                .description("description1")
                .build();
        String service1Id = nodeService.registerInstance(service1).getId();
        ServiceInstanceRegistrationRequestModel service2 = ServiceInstanceRegistrationRequestModel.builder()
                .name("test-beacon-2")
                .url("http://beacon-test-random-url2.someorg.com")
                .type("urn:ga4gh:beacon")
                .contactUrl("beacon-admin@someorg.com")
                .description("description2")
                .build();
        nodeService.registerInstance(service2).getId();

        // 1. Delete service1
        RestAssured.given()
                .accept(ContentType.JSON)
                .log().method()
                .log().uri()
                .pathParam("serviceId", service1Id)
                .auth().basic("dev", "dev")
                .delete("http://localhost:" + port + "/services/{serviceId}")
                .then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // 2 Fetch all remaining instances
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
                .body("content[0].url", equalTo(service2.getUrl()))
                .body("content[0].name", equalTo(service2.getName()))
                .body("content[0].contactUrl", equalTo(service2.getContactUrl()))
                .body("content[0].description", equalTo(service2.getDescription()))
                .body("content[0].type", equalTo(service2.getType()));
    }

}
