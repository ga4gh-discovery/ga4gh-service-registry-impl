package com.dnastack.discovery.registry.controller;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import com.dnastack.discovery.registry.TestApplication;
import com.dnastack.discovery.registry.domain.Health;
import com.dnastack.discovery.registry.model.HealthStatus;
import com.dnastack.discovery.registry.model.ServiceNode;
import com.dnastack.discovery.registry.model.ServiceType;
import com.dnastack.discovery.registry.service.ServiceNodeService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.ZonedDateTime;
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
public class ServiceNodeControllerIT {

    @LocalServerPort
    private Integer port;
    @Inject
    private ServiceNodeService nodeService;

    // TODO:
    @Ignore
    @Test
    public void getServiceNodeById_identifierIsInvalidUUID() {
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
    public void getServiceNodeById_nodeExistsWithId() {
        ZonedDateTime now = ZonedDateTime.now();
        ServiceNode service = ServiceNode.builder()
            .name("test-beacon")
            .type(ServiceType.BEACON)
            .description("description")
            .aliases(asList("key1:value1", "key2:value2"))
            .health(Health.builder()
                .status(HealthStatus.UP)
                .updatedAt(now)
                .build())
            .build();
        String serviceId = nodeService.save(service).getId();

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
            .body("name", equalTo(service.getName()))
            .body("description", equalTo(service.getDescription()))
            .body("type", equalTo(service.getType().name()))
            .body("aliases", containsInAnyOrder("key1:value1", "key2:value2"))
            .body("health.status", equalTo(service.getHealth().getStatus().name()))
            .body("health.updatedAt", notNullValue());
    }

    @Test
    public void getServiceNodeById_nodeNotExistsWithId() {
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
    public void getServiceNodes_noNodeExists() {
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
    public void getServiceNodes_atLeastOneNodeExists() {
        ServiceNode service = ServiceNode.builder()
            .name("test-beacon")
            .type(ServiceType.BEACON)
            .description("description")
            .aliases(asList("key1:value1", "key2:value2"))
            .build();
        nodeService.save(service);

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
            .body("content[0].name", equalTo(service.getName()))
            .body("content[0].description", equalTo(service.getDescription()))
            .body("content[0].type", equalTo(service.getType().name()))
            .body("content[0].aliases", containsInAnyOrder("key1:value1", "key2:value2"));
    }

    @Test
    public void getServiceNodes_filterByQueryMatchingName_oneMatchExpected() {
        ServiceNode service1 = ServiceNode.builder()
            .name("test-beacon-1")
            .type(ServiceType.BEACON)
            .description("description for beacon 1")
            .aliases(asList("key1:value1", "key2:value2"))
            .build();
        nodeService.save(service1);
        ServiceNode service2 = ServiceNode.builder()
            .name("test-beacon-2")
            .type(ServiceType.BEACON)
            .description("description for beacon 2")
            .aliases(asList("key1:value1", "key2:value2"))
            .build();
        nodeService.save(service2);

        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .queryParam("query", "beacon-1")
            .get("http://localhost:" + port + "/services")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(1))
            .body("content[0].id", notNullValue())
            .body("content[0].name", equalTo(service1.getName()))
            .body("content[0].description", equalTo(service1.getDescription()))
            .body("content[0].type", equalTo(service1.getType().name()))
            .body("content[0].aliases", containsInAnyOrder("key1:value1", "key2:value2"));
    }

    @Test
    public void getServiceNodes_filterByQueryMatchingName_multiMatchExpected() {
        ServiceNode service1 = ServiceNode.builder()
            .name("test-beacon-1")
            .type(ServiceType.BEACON)
            .description("description for 1")
            .aliases(asList("key1:value1", "key2:value2"))
            .build();
        nodeService.save(service1);
        ServiceNode service2 = ServiceNode.builder()
            .name("test-beacon-2")
            .type(ServiceType.BEACON)
            .description("description for 2")
            .aliases(asList("key1:value1", "key2:value2"))
            .build();
        nodeService.save(service2);

        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .queryParam("query", "beacon")
            .get("http://localhost:" + port + "/services")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(2))
            .body("content[0].id", notNullValue())
            .body("content[0].name", equalTo(service1.getName()))
            .body("content[0].description", equalTo(service1.getDescription()))
            .body("content[0].type", equalTo(service1.getType().name()))
            .body("content[0].aliases", containsInAnyOrder("key1:value1", "key2:value2"))
            .body("content[1].id", notNullValue())
            .body("content[1].name", equalTo(service2.getName()))
            .body("content[1].description", equalTo(service2.getDescription()))
            .body("content[1].type", equalTo(service2.getType().name()))
            .body("content[1].aliases", containsInAnyOrder("key1:value1", "key2:value2"));
    }

    @Test
    public void getServiceNodes_filterByQueryMatchingDescription_oneMatchExpected() {
        ServiceNode service1 = ServiceNode.builder()
            .name("test-beacon-1")
            .type(ServiceType.BEACON)
            .description("description for beacon 1")
            .aliases(asList("key1:value1", "key2:value2"))
            .build();
        nodeService.save(service1);
        ServiceNode service2 = ServiceNode.builder()
            .name("test-beacon-2")
            .type(ServiceType.BEACON)
            .description("description for beacon 2")
            .aliases(asList("key1:value1", "key2:value2"))
            .build();
        nodeService.save(service2);

        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .queryParam("query", "beacon 1")
            .get("http://localhost:" + port + "/services")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(1))
            .body("content[0].id", notNullValue())
            .body("content[0].name", equalTo(service1.getName()))
            .body("content[0].description", equalTo(service1.getDescription()))
            .body("content[0].type", equalTo(service1.getType().name()))
            .body("content[0].aliases", containsInAnyOrder("key1:value1", "key2:value2"));
    }

    @Test
    public void getServiceNodes_filterByQueryMatchingDescription_multiMatchExpected() {
        ServiceNode service1 = ServiceNode.builder()
            .name("test-beacon-1")
            .type(ServiceType.BEACON)
            .description("description for beacon 1")
            .aliases(asList("key1:value1", "key2:value2"))
            .build();
        nodeService.save(service1);
        ServiceNode service2 = ServiceNode.builder()
            .name("test-beacon-2")
            .type(ServiceType.BEACON)
            .description("description for beacon 2")
            .aliases(asList("key1:value1", "key2:value2"))
            .build();
        nodeService.save(service2);

        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .queryParam("query", "description")
            .get("http://localhost:" + port + "/services")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(2))
            .body("content[0].id", notNullValue())
            .body("content[0].name", equalTo(service1.getName()))
            .body("content[0].description", equalTo(service1.getDescription()))
            .body("content[0].type", equalTo(service1.getType().name()))
            .body("content[0].aliases", containsInAnyOrder("key1:value1", "key2:value2"))
            .body("content[1].id", notNullValue())
            .body("content[1].name", equalTo(service2.getName()))
            .body("content[1].description", equalTo(service2.getDescription()))
            .body("content[1].type", equalTo(service2.getType().name()))
            .body("content[1].aliases", containsInAnyOrder("key1:value1", "key2:value2"));
    }

    @Test
    public void getServiceNodes_filterByQueryMatchingAlias_oneMatchExpected() {
        ServiceNode service1 = ServiceNode.builder()
            .name("test-beacon-1")
            .type(ServiceType.BEACON)
            .description("description for beacon 1")
            .aliases(asList("beacon", "beaconA"))
            .build();
        nodeService.save(service1);
        ServiceNode service2 = ServiceNode.builder()
            .name("test-beacon-2")
            .type(ServiceType.BEACON)
            .description("description for beacon 2")
            .aliases(asList("beacon", "beaconB"))
            .build();
        nodeService.save(service2);

        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .queryParam("query", "beaconA")
            .get("http://localhost:" + port + "/services")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(1))
            .body("content[0].id", notNullValue())
            .body("content[0].name", equalTo(service1.getName()))
            .body("content[0].description", equalTo(service1.getDescription()))
            .body("content[0].type", equalTo(service1.getType().name()))
            .body("content[0].aliases", containsInAnyOrder("beacon", "beaconA"));
    }

    @Test
    public void getServiceNodes_filterByQueryMatchingAlias_multiMatchExpected() {
        ServiceNode service1 = ServiceNode.builder()
            .name("test-1")
            .type(ServiceType.BEACON)
            .description("description for 1")
            .aliases(asList("beacon", "beaconA"))
            .build();
        nodeService.save(service1);
        ServiceNode service2 = ServiceNode.builder()
            .name("test-2")
            .type(ServiceType.BEACON)
            .description("description for 2")
            .aliases(asList("beacon", "beaconB"))
            .build();
        nodeService.save(service2);

        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .queryParam("query", "beacon")
            .get("http://localhost:" + port + "/services")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(2))
            .body("content[0].id", notNullValue())
            .body("content[0].name", equalTo(service1.getName()))
            .body("content[0].description", equalTo(service1.getDescription()))
            .body("content[0].type", equalTo(service1.getType().name()))
            .body("content[0].aliases", containsInAnyOrder("beacon", "beaconA"))
            .body("content[1].id", notNullValue())
            .body("content[1].name", equalTo(service2.getName()))
            .body("content[1].description", equalTo(service2.getDescription()))
            .body("content[1].type", equalTo(service2.getType().name()))
            .body("content[1].aliases", containsInAnyOrder("beacon", "beaconB"));
    }

    @Test
    public void getServiceNodes_filterByQueryMatchingNameAndAlias() {
        ServiceNode service1 = ServiceNode.builder()
            .name("test-beacon-1")
            .type(ServiceType.BEACON)
            .description("description for beacon 1")
            .aliases(asList("beacon", "beaconA", "test-beacon-2"))
            .build();
        nodeService.save(service1);
        ServiceNode service2 = ServiceNode.builder()
            .name("test-beacon-2")
            .type(ServiceType.BEACON)
            .description("description for beacon 2")
            .aliases(asList("beacon", "beaconB"))
            .build();
        nodeService.save(service2);

        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .queryParam("query", "test-beacon-2")
            .get("http://localhost:" + port + "/services")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(2))
            .body("content[0].id", notNullValue())
            .body("content[0].name", equalTo(service1.getName()))
            .body("content[0].description", equalTo(service1.getDescription()))
            .body("content[0].type", equalTo(service1.getType().name()))
            .body("content[0].aliases", containsInAnyOrder("beacon", "beaconA", "test-beacon-2"))
            .body("content[1].id", notNullValue())
            .body("content[1].name", equalTo(service2.getName()))
            .body("content[1].description", equalTo(service2.getDescription()))
            .body("content[1].type", equalTo(service2.getType().name()))
            .body("content[1].aliases", containsInAnyOrder("beacon", "beaconB"));
    }

    @Test
    public void getServiceNodes_filterByQueryMatchingNameAndDescription() {
        ServiceNode service1 = ServiceNode.builder()
            .name("test-beacon-1")
            .type(ServiceType.BEACON)
            .description("description for beacon 1")
            .aliases(asList("beacon", "beaconA"))
            .build();
        nodeService.save(service1);
        ServiceNode service2 = ServiceNode.builder()
            .name("test-beacon-2")
            .type(ServiceType.BEACON)
            .description("description for beacon 2 but also test-beacon-1")
            .aliases(asList("beacon", "beaconB"))
            .build();
        nodeService.save(service2);

        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .queryParam("query", "beacon-1")
            .get("http://localhost:" + port + "/services")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(2))
            .body("content[0].id", notNullValue())
            .body("content[0].name", equalTo(service1.getName()))
            .body("content[0].description", equalTo(service1.getDescription()))
            .body("content[0].type", equalTo(service1.getType().name()))
            .body("content[0].aliases", containsInAnyOrder("beacon", "beaconA"))
            .body("content[1].id", notNullValue())
            .body("content[1].name", equalTo(service2.getName()))
            .body("content[1].description", equalTo(service2.getDescription()))
            .body("content[1].type", equalTo(service2.getType().name()))
            .body("content[1].aliases", containsInAnyOrder("beacon", "beaconB"));
    }

    @Test
    public void getServiceNodes_filterByQueryMatchingAliasAndDescription() {
        ServiceNode service1 = ServiceNode.builder()
            .name("test-beacon-1")
            .type(ServiceType.BEACON)
            .description("description for beaconB")
            .aliases(asList("beacon", "beaconA"))
            .build();
        nodeService.save(service1);
        ServiceNode service2 = ServiceNode.builder()
            .name("test-beacon-2")
            .type(ServiceType.BEACON)
            .description("description for beacon 2")
            .aliases(asList("beacon", "beaconB"))
            .build();
        nodeService.save(service2);

        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .queryParam("query", "beaconB")
            .get("http://localhost:" + port + "/services")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(2))
            .body("content[0].id", notNullValue())
            .body("content[0].name", equalTo(service1.getName()))
            .body("content[0].description", equalTo(service1.getDescription()))
            .body("content[0].type", equalTo(service1.getType().name()))
            .body("content[0].aliases", containsInAnyOrder("beacon", "beaconA"))
            .body("content[1].id", notNullValue())
            .body("content[1].name", equalTo(service2.getName()))
            .body("content[1].description", equalTo(service2.getDescription()))
            .body("content[1].type", equalTo(service2.getType().name()))
            .body("content[1].aliases", containsInAnyOrder("beacon", "beaconB"));
    }

    @Test
    public void getServiceNodes_filterByQueryMatchingNameAndAliasAndDescription() {
        ServiceNode service1 = ServiceNode.builder()
            .name("test-beacon-1")
            .type(ServiceType.BEACON)
            .description("description for beacon 1")
            .aliases(asList("beacon", "beaconA"))
            .build();
        nodeService.save(service1);
        ServiceNode service2 = ServiceNode.builder()
            .name("test-beacon-2")
            .type(ServiceType.BEACON)
            .description("description for beacon 2")
            .aliases(asList("beacon", "beaconB"))
            .build();
        nodeService.save(service2);
        ServiceNode service3 = ServiceNode.builder()
            .name("test-beacon-3")
            .type(ServiceType.BEACON)
            .description("description for beacon 3")
            .aliases(asList("beacon", "beaconC"))
            .build();
        nodeService.save(service3);

        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .queryParam("query", "beacon")
            .get("http://localhost:" + port + "/services")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(3))
            .body("content[0].id", notNullValue())
            .body("content[0].name", equalTo(service1.getName()))
            .body("content[0].description", equalTo(service1.getDescription()))
            .body("content[0].type", equalTo(service1.getType().name()))
            .body("content[0].aliases", containsInAnyOrder("beacon", "beaconA"))
            .body("content[1].id", notNullValue())
            .body("content[1].name", equalTo(service2.getName()))
            .body("content[1].description", equalTo(service2.getDescription()))
            .body("content[1].type", equalTo(service2.getType().name()))
            .body("content[1].aliases", containsInAnyOrder("beacon", "beaconB"))
            .body("content[2].id", notNullValue())
            .body("content[2].name", equalTo(service3.getName()))
            .body("content[2].description", equalTo(service3.getDescription()))
            .body("content[2].type", equalTo(service3.getType().name()))
            .body("content[2].aliases", containsInAnyOrder("beacon", "beaconC"));
    }

}
