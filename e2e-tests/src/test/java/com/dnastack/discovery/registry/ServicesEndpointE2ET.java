package com.dnastack.discovery.registry;

import com.dnastack.discovery.registry.client.OrganizationModel;
import com.dnastack.discovery.registry.client.TestingServiceInstance;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Slf4j
public class ServicesEndpointE2ET extends BaseE2ET {
    private static String TEST_REALM = "e2e-test-" + System.currentTimeMillis();

    String registerServiceInstance(String realm, TestingServiceInstance service) {
        // @formatter:off
        String createdServiceUrl = RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .header("Authorization", "Basic " + getBase64Auth())
            .header("Service-Registry-Realm", realm)
            .contentType("application/json")
            .body(service)
            .post("/services")
            .then()
            .log().ifValidationFails()
            .statusCode(201)
            .extract().header("Location");
        // @formatter:on

        assertThat("HTTP Created must include a Location header pointing to the created entity",
                createdServiceUrl, notNullValue());
        log.info("Created service {} in realm {}", createdServiceUrl, TEST_REALM);
        String id = createdServiceUrl.substring(createdServiceUrl.lastIndexOf('/') + 1);
        return id;
    }

    private void deleteServiceInstance(String serviceId) {
        // @formatter:off
        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .header("Authorization", "Basic " + getBase64Auth())
            .header("Service-Registry-Realm", TEST_REALM)
            .pathParam("serviceId", serviceId)
            .auth().basic("dev", "dev")
            .delete("/services/{serviceId}")
            .then()
            .log().ifValidationFails()
            .statusCode(204);
        // @formatter:on
    }

    private List<TestingServiceInstance> getServiceInstances() {
        // @formatter:off
        return RestAssured.given()
                .accept(ContentType.JSON)
                .log().method()
                .log().uri()
                .header("Authorization", "Basic " + getBase64Auth())
                .header("Service-Registry-Realm", TEST_REALM)
                .get("/services")
                .then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(200)
                .extract().body().as(new TypeRef<>() {});
        // @formatter:on
    }

    @Before
    public void clearTestRealm() {
        // make sure we don't destroy the default realm!
        assertThat(TEST_REALM, not(equalTo("default")));

        List<TestingServiceInstance> allServiceInstances = getServiceInstances();
        log.info("Deleting {} service instances from {}", allServiceInstances.size(), TEST_REALM);
        for (TestingServiceInstance serviceInstance : allServiceInstances) {
            assertThat(serviceInstance.getId(), notNullValue());
            deleteServiceInstance(serviceInstance.getId());
        }
    }

    @Test
    public void getServiceInstanceById_instanceNotExistsWithId() {
        // @formatter:off
        RestAssured.given()
                .accept(ContentType.JSON)
                .log().method()
                .log().uri()
                .header("Authorization", "Basic " + getBase64Auth())
                .header("Service-Registry-Realm", TEST_REALM)
                .pathParam("serviceId", UUID.randomUUID().toString())
                .get("/services/{serviceId}")
                .then()
                .log().ifValidationFails()
                .statusCode(404);
        // @formatter:on
    }

    @Test
    public void getServiceInstances_atLeastTwoInstancesExpected() {
        List<TestingServiceInstance> instances = getServiceInstances();
        assertThat(instances, hasSize(greaterThanOrEqualTo(2)));
    }

    @Test
    public void getServiceInstanceById_instanceExistsWithId() {
        TestingServiceInstance service = TestingServiceInstance.builder()
                .name("test-beacon")
                .url("http://beacon-test-random-url.someorg.com")
                .type("org.ga4gh:beacon:1.0.1")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .documentationUrl("http://beacon-test-random-url.someorg.com")
                .version("1.0.1")
                .organization(new OrganizationModel("MyOrg", "http://example.com"))
                .environment("test")
                .build();
        String serviceId = registerServiceInstance(TEST_REALM, service);

        // @formatter:off
        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .header("Authorization", "Basic " + getBase64Auth())
            .header("Service-Registry-Realm", TEST_REALM)
            .pathParam("serviceId", serviceId)
            .get("/services/{serviceId}")
            .then()
            .log().ifValidationFails()
            .statusCode(200)
            .body("id", notNullValue())
            .body("version", equalTo(service.getVersion()))
            .body("type", equalTo(service.getType()))
            .body("url", equalTo(service.getUrl()))
            .body("name", equalTo(service.getName()))
            .body("organization.name", equalTo(service.getOrganization().getName()))
            .body("organization.url", equalTo(service.getOrganization().getUrl()))
            .body("environment", equalTo(service.getEnvironment().toString()))
            .body("createdAt", notNullValue())
            .body("updatedAt", notNullValue())
            .body("contactUrl", equalTo(service.getContactUrl()))
            .body("documentationUrl", equalTo(service.getDocumentationUrl()))
            .body("description", equalTo(service.getDescription()));
        // @formatter:on
    }

    @Test
    public void getServiceInstances_noInstanceExists() {
        // @formatter:off
        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .header("Authorization", "Basic " + getBase64Auth())
            .header("Service-Registry-Realm", TEST_REALM)
            .get("/services")
            .then()
            .log().ifValidationFails()
            .statusCode(200)
            .body("", empty());
        // @formatter:on
    }

    @Test
    public void getServiceInstances_atLeastOneInstanceExists() {
        TestingServiceInstance service = TestingServiceInstance.builder()
                .name("test-beacon")
                .url("http://beacon-test-random-url.someorg.com")
                .type("org.ga4gh:beacon:1.0.1")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .documentationUrl("http://beacon-test-random-url.someorg.com")
                .version("1.0.1")
                .organization(new OrganizationModel("MyOrg", "http://example.com"))
                .environment("test")
                .build();
        registerServiceInstance(TEST_REALM, service);

        // @formatter:off
        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .header("Authorization", "Basic " + getBase64Auth())
            .header("Service-Registry-Realm", TEST_REALM)
            .get("/services")
            .then()
            .log().everything()
            .statusCode(200)
            .body("[0].id", notNullValue())
            .body("[0].version", equalTo(service.getVersion()))
            .body("[0].type", equalTo(service.getType()))
            .body("[0].url", equalTo(service.getUrl()))
            .body("[0].name", equalTo(service.getName()))
            .body("[0].organization.name", equalTo(service.getOrganization().getName()))
            .body("[0].organization.url", equalTo(service.getOrganization().getUrl()))
            .body("[0].environment", equalTo(service.getEnvironment().toString()))
            .body("[0].createdAt", notNullValue())
            .body("[0].updatedAt", notNullValue())
            .body("[0].contactUrl", equalTo(service.getContactUrl()))
            .body("[0].documentationUrl", equalTo(service.getDocumentationUrl()))
            .body("[0].description", equalTo(service.getDescription()));
        // @formatter:on
    }

    @Test
    public void getServiceInstanceTypes() {
        registerServiceInstance(TEST_REALM, TestingServiceInstance.builder()
                .name("test-beacon-aggregator")
                .url("http://beacon-aggregator-test-url.someorg.com")
                .type("org.ga4gh:beacon-aggregator:1.0.0")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .documentationUrl("http://beacon-test-random-url.someorg.com")
                .version("1.0.0")
                .organization(new OrganizationModel("MyOrg", "http://example.com"))
                .environment("test")
                .build());
        registerServiceInstance(TEST_REALM, TestingServiceInstance.builder()
                .name("test-beacon")
                .url("http://beacon-test-url.someorg.com")
                .type("org.ga4gh:beacon:1.0.1")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .documentationUrl("http://beacon-test-random-url.someorg.com")
                .version("1.0.1")
                .organization(new OrganizationModel("MyOrg", "http://example.com"))
                .environment("test")
                .build());
        registerServiceInstance(TEST_REALM, TestingServiceInstance.builder()
                .name("test-portal")
                .url("http://user-portal-test-url.someorg.com")
                .type("org.ga4gh:user-portal:1")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .documentationUrl("http://beacon-test-random-url.someorg.com")
                .version("1")
                .organization(new OrganizationModel("MyOrg", "http://example.com"))
                .environment("test")
                .build());

        // @formatter:off
        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .header("Authorization", "Basic " + getBase64Auth())
            .header("Service-Registry-Realm", TEST_REALM)
            .get("/services/types")
            .then()
            .log().ifValidationFails()
            .statusCode(200)
            .body(".",
                  containsInAnyOrder("org.ga4gh:beacon:1.0.1",
                                     "org.ga4gh:beacon-aggregator:1.0.0",
                                     "org.ga4gh:user-portal:1"));
        // @formatter:on
    }

    @Test
    public void getServiceInstanceTypes_empty() {
        // @formatter:off
        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .header("Authorization", "Basic " + getBase64Auth())
            .header("Service-Registry-Realm", TEST_REALM)
            .get("/services/types")
            .then()
            .log().ifValidationFails()
            .statusCode(200)
            .body(".", empty());
        // @formatter:on
    }

    @Test
    public void deleteServiceInstanceById_instanceExistsWithId() {
        TestingServiceInstance service = TestingServiceInstance.builder()
                .name("test-beacon")
                .url("http://beacon-test-random-url.someorg.com")
                .type("org.ga4gh:beacon:1.0.1")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .documentationUrl("http://beacon-test-random-url.someorg.com")
                .version("1.0.1")
                .organization(new OrganizationModel("MyOrg", "http://example.com"))
                .environment("test")
                .build();
        String serviceId = registerServiceInstance(TEST_REALM, service);
        deleteServiceInstance(serviceId);

    }

    @Test
    public void deregisterServiceInstanceById_instanceExistsWithId_ensureNoSideEffect() {
        TestingServiceInstance service1 = TestingServiceInstance.builder()
                .name("test-beacon-aggregator")
                .url("http://beacon-aggregator-test-url.someorg.com")
                .type("org.ga4gh:beacon-aggregator:1.0.0")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .documentationUrl("http://beacon-test-random-url.someorg.com")
                .version("1.0.0")
                .organization(new OrganizationModel("MyOrg", "http://example.com"))
                .environment("test")
                .build();
        String service1Id = registerServiceInstance(TEST_REALM, service1);
        TestingServiceInstance service2 = TestingServiceInstance.builder()
                .name("test-beacon")
                .url("http://beacon-test-random-url.someorg.com")
                .type("org.ga4gh:beacon:1.0.1")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .documentationUrl("http://beacon-test-random-url.someorg.com")
                .version("1.0.1")
                .organization(new OrganizationModel("MyOrg", "http://example.com"))
                .environment("test")
                .build();
        registerServiceInstance(TEST_REALM, service2);

        // 1. Delete service1
        deleteServiceInstance(service1Id);

        // @formatter:off
        // 2 Fetch all remaining instances
        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .header("Authorization", "Basic " + getBase64Auth())
            .header("Service-Registry-Realm", TEST_REALM)
            .get("/services")
            .then()
            .log().everything()
            .statusCode(200)
            .body("[0].id", notNullValue())
            .body("[0].version", equalTo(service2.getVersion()))
            .body("[0].type", equalTo(service2.getType()))
            .body("[0].url", equalTo(service2.getUrl()))
            .body("[0].name", equalTo(service2.getName()))
            .body("[0].organization.name", equalTo(service2.getOrganization().getName()))
            .body("[0].organization.url", equalTo(service2.getOrganization().getUrl()))
            .body("[0].environment", equalTo(service2.getEnvironment().toString()))
            .body("[0].createdAt", notNullValue())
            .body("[0].updatedAt", notNullValue())
            .body("[0].contactUrl", equalTo(service2.getContactUrl()))
            .body("[0].documentationUrl", equalTo(service2.getDocumentationUrl()))
            .body("[0].description", equalTo(service2.getDescription()));
        // @formatter:on
    }

}
