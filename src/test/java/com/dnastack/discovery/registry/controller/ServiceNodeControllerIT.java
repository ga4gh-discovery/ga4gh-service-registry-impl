package com.dnastack.discovery.registry.controller;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import com.dnastack.discovery.registry.TestApplication;
import com.dnastack.discovery.registry.model.ServiceNode;
import com.dnastack.discovery.registry.model.ServiceType;
import com.dnastack.discovery.registry.service.ServiceNodeService;
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
            .pathParam("nodeId", invalidUUID)
            .get("http://localhost:" + port + "/nodes/{nodeId}")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void getServiceNodeById_nodeExistsWithId() {
        ServiceNode service = ServiceNode.builder()
            .name("test-beacon")
            .serviceType(ServiceType.BEACON)
            .description("description")
            .aliases(asList("key1:value1", "key2:value2"))
            .build();
        String nodeId = nodeService.save(service).getId();

        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .pathParam("nodeId", nodeId)
            .get("http://localhost:" + port + "/nodes/{nodeId}")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("id", notNullValue())
            .body("name", equalTo(service.getName()))
            .body("description", equalTo(service.getDescription()))
            .body("serviceType", equalTo(service.getServiceType().name()))
            .body("aliases", containsInAnyOrder("key1:value1", "key2:value2"));
    }

    @Test
    public void getServiceNodeById_nodeNotExistsWithId() {
        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .pathParam("nodeId", UUID.randomUUID().toString())
            .get("http://localhost:" + port + "/nodes/{nodeId}")
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
            .get("http://localhost:" + port + "/nodes")
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
            .serviceType(ServiceType.BEACON)
            .description("description")
            .aliases(asList("key1:value1", "key2:value2"))
            .build();
        nodeService.save(service);

        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .get("http://localhost:" + port + "/nodes")
            .then()
            .log().everything()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(1))
            .body("content[0].id", notNullValue())
            .body("content[0].name", equalTo(service.getName()))
            .body("content[0].description", equalTo(service.getDescription()))
            .body("content[0].serviceType", equalTo(service.getServiceType().name()))
            .body("content[0].aliases", containsInAnyOrder("key1:value1", "key2:value2"));
    }

    @Test
    public void getServiceNodes_filterByQueryMatchingName_oneMatchExpected() {
        ServiceNode service1 = ServiceNode.builder()
            .name("test-beacon-1")
            .serviceType(ServiceType.BEACON)
            .description("description for beacon 1")
            .aliases(asList("key1:value1", "key2:value2"))
            .build();
        nodeService.save(service1);
        ServiceNode service2 = ServiceNode.builder()
            .name("test-beacon-2")
            .serviceType(ServiceType.BEACON)
            .description("description for beacon 2")
            .aliases(asList("key1:value1", "key2:value2"))
            .build();
        nodeService.save(service2);

        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .queryParam("query", "beacon-1")
            .get("http://localhost:" + port + "/nodes")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(1))
            .body("content[0].id", notNullValue())
            .body("content[0].name", equalTo(service1.getName()))
            .body("content[0].description", equalTo(service1.getDescription()))
            .body("content[0].serviceType", equalTo(service1.getServiceType().name()))
            .body("content[0].aliases", containsInAnyOrder("key1:value1", "key2:value2"));
    }

    @Test
    public void getServiceNodes_filterByQueryMatchingName_multiMatchExpected() {
        ServiceNode service1 = ServiceNode.builder()
            .name("test-beacon-1")
            .serviceType(ServiceType.BEACON)
            .description("description for 1")
            .aliases(asList("key1:value1", "key2:value2"))
            .build();
        nodeService.save(service1);
        ServiceNode service2 = ServiceNode.builder()
            .name("test-beacon-2")
            .serviceType(ServiceType.BEACON)
            .description("description for 2")
            .aliases(asList("key1:value1", "key2:value2"))
            .build();
        nodeService.save(service2);

        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .queryParam("query", "beacon")
            .get("http://localhost:" + port + "/nodes")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(2))
            .body("content[0].id", notNullValue())
            .body("content[0].name", equalTo(service1.getName()))
            .body("content[0].description", equalTo(service1.getDescription()))
            .body("content[0].serviceType", equalTo(service1.getServiceType().name()))
            .body("content[0].aliases", containsInAnyOrder("key1:value1", "key2:value2"))
            .body("content[1].id", notNullValue())
            .body("content[1].name", equalTo(service2.getName()))
            .body("content[1].description", equalTo(service2.getDescription()))
            .body("content[1].serviceType", equalTo(service2.getServiceType().name()))
            .body("content[1].aliases", containsInAnyOrder("key1:value1", "key2:value2"));
    }

    @Test
    public void getServiceNodes_filterByQueryMatchingDescription_oneMatchExpected() {
        ServiceNode service1 = ServiceNode.builder()
            .name("test-beacon-1")
            .serviceType(ServiceType.BEACON)
            .description("description for beacon 1")
            .aliases(asList("key1:value1", "key2:value2"))
            .build();
        nodeService.save(service1);
        ServiceNode service2 = ServiceNode.builder()
            .name("test-beacon-2")
            .serviceType(ServiceType.BEACON)
            .description("description for beacon 2")
            .aliases(asList("key1:value1", "key2:value2"))
            .build();
        nodeService.save(service2);

        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .queryParam("query", "beacon 1")
            .get("http://localhost:" + port + "/nodes")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(1))
            .body("content[0].id", notNullValue())
            .body("content[0].name", equalTo(service1.getName()))
            .body("content[0].description", equalTo(service1.getDescription()))
            .body("content[0].serviceType", equalTo(service1.getServiceType().name()))
            .body("content[0].aliases", containsInAnyOrder("key1:value1", "key2:value2"));
    }

    @Test
    public void getServiceNodes_filterByQueryMatchingDescription_multiMatchExpected() {
        ServiceNode service1 = ServiceNode.builder()
            .name("test-beacon-1")
            .serviceType(ServiceType.BEACON)
            .description("description for beacon 1")
            .aliases(asList("key1:value1", "key2:value2"))
            .build();
        nodeService.save(service1);
        ServiceNode service2 = ServiceNode.builder()
            .name("test-beacon-2")
            .serviceType(ServiceType.BEACON)
            .description("description for beacon 2")
            .aliases(asList("key1:value1", "key2:value2"))
            .build();
        nodeService.save(service2);

        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .queryParam("query", "description")
            .get("http://localhost:" + port + "/nodes")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(2))
            .body("content[0].id", notNullValue())
            .body("content[0].name", equalTo(service1.getName()))
            .body("content[0].description", equalTo(service1.getDescription()))
            .body("content[0].serviceType", equalTo(service1.getServiceType().name()))
            .body("content[0].aliases", containsInAnyOrder("key1:value1", "key2:value2"))
            .body("content[1].id", notNullValue())
            .body("content[1].name", equalTo(service2.getName()))
            .body("content[1].description", equalTo(service2.getDescription()))
            .body("content[1].serviceType", equalTo(service2.getServiceType().name()))
            .body("content[1].aliases", containsInAnyOrder("key1:value1", "key2:value2"));
    }

    @Test
    public void getServiceNodes_filterByQueryMatchingAlias_oneMatchExpected() {
        ServiceNode service1 = ServiceNode.builder()
            .name("test-beacon-1")
            .serviceType(ServiceType.BEACON)
            .description("description for beacon 1")
            .aliases(asList("beacon", "beaconA"))
            .build();
        nodeService.save(service1);
        ServiceNode service2 = ServiceNode.builder()
            .name("test-beacon-2")
            .serviceType(ServiceType.BEACON)
            .description("description for beacon 2")
            .aliases(asList("beacon", "beaconB"))
            .build();
        nodeService.save(service2);

        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .queryParam("query", "beaconA")
            .get("http://localhost:" + port + "/nodes")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(1))
            .body("content[0].id", notNullValue())
            .body("content[0].name", equalTo(service1.getName()))
            .body("content[0].description", equalTo(service1.getDescription()))
            .body("content[0].serviceType", equalTo(service1.getServiceType().name()))
            .body("content[0].aliases", containsInAnyOrder("beacon", "beaconA"));
    }

    @Test
    public void getServiceNodes_filterByQueryMatchingAlias_multiMatchExpected() {
        ServiceNode service1 = ServiceNode.builder()
            .name("test-1")
            .serviceType(ServiceType.BEACON)
            .description("description for 1")
            .aliases(asList("beacon", "beaconA"))
            .build();
        nodeService.save(service1);
        ServiceNode service2 = ServiceNode.builder()
            .name("test-2")
            .serviceType(ServiceType.BEACON)
            .description("description for 2")
            .aliases(asList("beacon", "beaconB"))
            .build();
        nodeService.save(service2);

        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .queryParam("query", "beacon")
            .get("http://localhost:" + port + "/nodes")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(2))
            .body("content[0].id", notNullValue())
            .body("content[0].name", equalTo(service1.getName()))
            .body("content[0].description", equalTo(service1.getDescription()))
            .body("content[0].serviceType", equalTo(service1.getServiceType().name()))
            .body("content[0].aliases", containsInAnyOrder("beacon", "beaconA"))
            .body("content[1].id", notNullValue())
            .body("content[1].name", equalTo(service2.getName()))
            .body("content[1].description", equalTo(service2.getDescription()))
            .body("content[1].serviceType", equalTo(service2.getServiceType().name()))
            .body("content[1].aliases", containsInAnyOrder("beacon", "beaconB"));
    }

    @Test
    public void getServiceNodes_filterByQueryMatchingNameAndAlias() {
        ServiceNode service1 = ServiceNode.builder()
            .name("test-beacon-1")
            .serviceType(ServiceType.BEACON)
            .description("description for beacon 1")
            .aliases(asList("beacon", "beaconA", "test-beacon-2"))
            .build();
        nodeService.save(service1);
        ServiceNode service2 = ServiceNode.builder()
            .name("test-beacon-2")
            .serviceType(ServiceType.BEACON)
            .description("description for beacon 2")
            .aliases(asList("beacon", "beaconB"))
            .build();
        nodeService.save(service2);

        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .queryParam("query", "test-beacon-2")
            .get("http://localhost:" + port + "/nodes")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(2))
            .body("content[0].id", notNullValue())
            .body("content[0].name", equalTo(service1.getName()))
            .body("content[0].description", equalTo(service1.getDescription()))
            .body("content[0].serviceType", equalTo(service1.getServiceType().name()))
            .body("content[0].aliases", containsInAnyOrder("beacon", "beaconA", "test-beacon-2"))
            .body("content[1].id", notNullValue())
            .body("content[1].name", equalTo(service2.getName()))
            .body("content[1].description", equalTo(service2.getDescription()))
            .body("content[1].serviceType", equalTo(service2.getServiceType().name()))
            .body("content[1].aliases", containsInAnyOrder("beacon", "beaconB"));
    }

    @Test
    public void getServiceNodes_filterByQueryMatchingNameAndDescription() {
        ServiceNode service1 = ServiceNode.builder()
            .name("test-beacon-1")
            .serviceType(ServiceType.BEACON)
            .description("description for beacon 1")
            .aliases(asList("beacon", "beaconA"))
            .build();
        nodeService.save(service1);
        ServiceNode service2 = ServiceNode.builder()
            .name("test-beacon-2")
            .serviceType(ServiceType.BEACON)
            .description("description for beacon 2 but also test-beacon-1")
            .aliases(asList("beacon", "beaconB"))
            .build();
        nodeService.save(service2);

        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .queryParam("query", "beacon-1")
            .get("http://localhost:" + port + "/nodes")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(2))
            .body("content[0].id", notNullValue())
            .body("content[0].name", equalTo(service1.getName()))
            .body("content[0].description", equalTo(service1.getDescription()))
            .body("content[0].serviceType", equalTo(service1.getServiceType().name()))
            .body("content[0].aliases", containsInAnyOrder("beacon", "beaconA"))
            .body("content[1].id", notNullValue())
            .body("content[1].name", equalTo(service2.getName()))
            .body("content[1].description", equalTo(service2.getDescription()))
            .body("content[1].serviceType", equalTo(service2.getServiceType().name()))
            .body("content[1].aliases", containsInAnyOrder("beacon", "beaconB"));
    }

    @Test
    public void getServiceNodes_filterByQueryMatchingAliasAndDescription() {
        ServiceNode service1 = ServiceNode.builder()
            .name("test-beacon-1")
            .serviceType(ServiceType.BEACON)
            .description("description for beaconB")
            .aliases(asList("beacon", "beaconA"))
            .build();
        nodeService.save(service1);
        ServiceNode service2 = ServiceNode.builder()
            .name("test-beacon-2")
            .serviceType(ServiceType.BEACON)
            .description("description for beacon 2")
            .aliases(asList("beacon", "beaconB"))
            .build();
        nodeService.save(service2);

        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .queryParam("query", "beaconB")
            .get("http://localhost:" + port + "/nodes")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(2))
            .body("content[0].id", notNullValue())
            .body("content[0].name", equalTo(service1.getName()))
            .body("content[0].description", equalTo(service1.getDescription()))
            .body("content[0].serviceType", equalTo(service1.getServiceType().name()))
            .body("content[0].aliases", containsInAnyOrder("beacon", "beaconA"))
            .body("content[1].id", notNullValue())
            .body("content[1].name", equalTo(service2.getName()))
            .body("content[1].description", equalTo(service2.getDescription()))
            .body("content[1].serviceType", equalTo(service2.getServiceType().name()))
            .body("content[1].aliases", containsInAnyOrder("beacon", "beaconB"));
    }

    @Test
    public void getServiceNodes_filterByQueryMatchingNameAndAliasAndDescription() {
        ServiceNode service1 = ServiceNode.builder()
            .name("test-beacon-1")
            .serviceType(ServiceType.BEACON)
            .description("description for beacon 1")
            .aliases(asList("beacon", "beaconA"))
            .build();
        nodeService.save(service1);
        ServiceNode service2 = ServiceNode.builder()
            .name("test-beacon-2")
            .serviceType(ServiceType.BEACON)
            .description("description for beacon 2")
            .aliases(asList("beacon", "beaconB"))
            .build();
        nodeService.save(service2);
        ServiceNode service3 = ServiceNode.builder()
            .name("test-beacon-3")
            .serviceType(ServiceType.BEACON)
            .description("description for beacon 3")
            .aliases(asList("beacon", "beaconC"))
            .build();
        nodeService.save(service3);

        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .queryParam("query", "beacon")
            .get("http://localhost:" + port + "/nodes")
            .then()
            .log().ifValidationFails()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(3))
            .body("content[0].id", notNullValue())
            .body("content[0].name", equalTo(service1.getName()))
            .body("content[0].description", equalTo(service1.getDescription()))
            .body("content[0].serviceType", equalTo(service1.getServiceType().name()))
            .body("content[0].aliases", containsInAnyOrder("beacon", "beaconA"))
            .body("content[1].id", notNullValue())
            .body("content[1].name", equalTo(service2.getName()))
            .body("content[1].description", equalTo(service2.getDescription()))
            .body("content[1].serviceType", equalTo(service2.getServiceType().name()))
            .body("content[1].aliases", containsInAnyOrder("beacon", "beaconB"))
            .body("content[2].id", notNullValue())
            .body("content[2].name", equalTo(service3.getName()))
            .body("content[2].description", equalTo(service3.getDescription()))
            .body("content[2].serviceType", equalTo(service3.getServiceType().name()))
            .body("content[2].aliases", containsInAnyOrder("beacon", "beaconC"));
    }

}
