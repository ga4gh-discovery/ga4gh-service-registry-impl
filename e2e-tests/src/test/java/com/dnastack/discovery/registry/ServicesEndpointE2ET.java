package com.dnastack.discovery.registry;

import com.dnastack.discovery.registry.client.TestingOrganizationModel;
import com.dnastack.discovery.registry.client.TestingServiceInstance;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Slf4j
public class ServicesEndpointE2ET extends BaseE2ET {
    private static String TEST_REALM = "e2e-test-" + System.currentTimeMillis();

    /**
     * Creates a service instance with the given info and a randomly named organization.
     */
    private TestingServiceInstance makeServiceInstance(String name, String url, String type) {
        TestingOrganizationModel org = new TestingOrganizationModel(
                "Org" + Math.random(),
                "http://example.com/" + Math.random());
        return makeServiceInstance(name, url, type, org);
    }

    /**
     * Creates a service instance with the given info.
     */
    private TestingServiceInstance makeServiceInstance(String name, String url, String type, TestingOrganizationModel org) {
        return TestingServiceInstance.builder()
                .name(name)
                .url(url)
                .type(type)
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .documentationUrl("http://beacon-test-random-url.someorg.com")
                .version("1." + Math.random())
                .organization(org)
                .environment("test")
                .build();
    }

    /**
     * Registers the given service instance in the service registry under test.
     */
    String registerServiceInstance(String realm, TestingServiceInstance service, int expectedStatus) {
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
            .statusCode(expectedStatus)
            .extract().header("Location");
        // @formatter:on

        if (expectedStatus == 201) {
            assertThat("HTTP Created must include a Location header pointing to the created entity",
                    createdServiceUrl, notNullValue());
            log.info("Created service {} in realm {}", createdServiceUrl, TEST_REALM);
            String id = createdServiceUrl.substring(createdServiceUrl.lastIndexOf('/') + 1);
            return id;
        } else {
            return null;
        }
    }

    /**
     * Registers the given service instance in the service registry under test.
     */
    void updateServiceInstance(String realm, String serviceId, TestingServiceInstance service, int expectedStatus) {
        // @formatter:off
        RestAssured.given()
            .accept(ContentType.JSON)
            .log().method()
            .log().uri()
            .header("Authorization", "Basic " + getBase64Auth())
            .header("Service-Registry-Realm", realm)
            .contentType("application/json")
            .body(service)
            .put("/services/" + serviceId)
            .then()
            .log().ifValidationFails()
            .statusCode(expectedStatus);
        // @formatter:on
    }

    /**
     * Deregisters the given service instance in the service registry under test.
     */
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

    /**
     * Retrieves all the service instances in the testing realm from the service registry under test.
     */
    private List<TestingServiceInstance> getServiceInstances() {
        // @formatter:off
        return RestAssured.given()
                .filter(validationFilter)
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

    /**
     * Retrieves all the service instances in the testing realm from the service registry under test.
     */
    private TestingServiceInstance getServiceInstance(String id) {
        // @formatter:off
        return RestAssured.given()
                .filter(validationFilter)
                .accept(ContentType.JSON)
                .log().method()
                .log().uri()
                .header("Authorization", "Basic " + getBase64Auth())
                .header("Service-Registry-Realm", TEST_REALM)
                .get("/services/" + id)
                .then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(200)
                .extract().body().as(TestingServiceInstance.class);
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
    public void getServiceInstanceById_should_return404_when_idIsNotFound() {
        // @formatter:off
        RestAssured.given()
                .filter(validationFilter)
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
    public void postServiceInstance_shouldWork_when_instanceHasNewOrg() {
        TestingServiceInstance service = makeServiceInstance("test-beacon", "http://beacon-test-random-url.someorg.com", "org.ga4gh:beacon:1.0.1");
        String serviceId = registerServiceInstance(TEST_REALM, service, 201);

        // @formatter:off
        RestAssured.given()
            .filter(validationFilter)
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
    public void postServiceInstance_should_return400_when_instanceHasNoOrg() {
        TestingServiceInstance service = makeServiceInstance("test-no-org", "http://beacon-test-random-url.noorg.com", "org.ga4gh:beacon:1.0.1", null);
        registerServiceInstance(TEST_REALM, service, 400);
    }

    @Test
    public void putServiceInstanceRoundTrip_should_preserveAllValuesExceptUpdatedAt() {
        TestingServiceInstance service = makeServiceInstance("test-beacon", "http://beacon-test-random-url.someorg.com", "org.ga4gh:beacon:1.0.1");
        String serviceId = registerServiceInstance(TEST_REALM, service, 201);
        TestingServiceInstance origFromServer = getServiceInstance(serviceId);
        updateServiceInstance(TEST_REALM, serviceId, origFromServer, 200);
        TestingServiceInstance updatedFromServer = getServiceInstance(serviceId);

        // updatedAt should be a later timestamp
        String origUpdatedAt = (String) origFromServer.getAdditionalProperties().get("updatedAt");
        String newUpdatedAt = (String) updatedFromServer.getAdditionalProperties().get("updatedAt");
        assertThat(newUpdatedAt, greaterThan(origUpdatedAt));
        origFromServer.getAdditionalProperties().remove("updatedAt");
        updatedFromServer.getAdditionalProperties().remove("updatedAt");

        // all remaining properties should be identical
        assertThat(updatedFromServer, equalTo(origFromServer));
    }

    @Test
    public void getServiceInstances_noInstanceExists() {
        // @formatter:off
        RestAssured.given()
            .filter(validationFilter)
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
        TestingServiceInstance service = makeServiceInstance("test-beacon", "http://beacon-test-random-url.someorg.com", "org.ga4gh:beacon:1.0.1");
        registerServiceInstance(TEST_REALM, service, 201);

        // @formatter:off
        RestAssured.given()
            .filter(validationFilter)
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
        registerServiceInstance(TEST_REALM, makeServiceInstance("test-beacon-aggregator", "http://beacon-aggregator-test-url.someorg.com", "org.ga4gh:beacon-aggregator:1.0.0"), 201);
        registerServiceInstance(TEST_REALM, makeServiceInstance("test-beacon", "http://beacon-test-url.someorg.com", "org.ga4gh:beacon:1.0.1"), 201);
        registerServiceInstance(TEST_REALM, makeServiceInstance("test-portal", "http://user-portal-test-url.someorg.com", "org.ga4gh:user-portal:1"), 201);

        // @formatter:off
        RestAssured.given()
            .filter(validationFilter)
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
            .filter(validationFilter)
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
        TestingServiceInstance service = makeServiceInstance("test-beacon", "http://beacon-test-random-url.someorg.com", "org.ga4gh:beacon:1.0.1");
        String serviceId = registerServiceInstance(TEST_REALM, service, 201);
        deleteServiceInstance(serviceId);

    }

    @Test
    public void registerServiceInstancesConcurrently_should_workWhenServicesShareSameOrganization() {
        TestingOrganizationModel sharedOrg = new TestingOrganizationModel("Shared Org", "https://sharing.is/caring");

        int numberOfInstancesToRegister = 5;
        IntStream.range(0, numberOfInstancesToRegister)
                .parallel()
                .forEach(n -> {
                    TestingServiceInstance s = makeServiceInstance(
                            "test-concurrent-registration-" + n,
                            "http://concurrency.is/hard/" + n,
                            "org.ga4gh:beacon:1.0.0",
                            sharedOrg);
                    registerServiceInstance(TEST_REALM, s, 201);
                });

        List<TestingServiceInstance> serviceInstances = getServiceInstances();
        assertThat(serviceInstances, hasSize(numberOfInstancesToRegister));
    }

    @Test
    public void registerThenUpdateServiceInstancesConcurrently_should_workWhenServicesShareSameOrganization() {
        TestingOrganizationModel sharedOrg = new TestingOrganizationModel("Shared Org", "https://sharing.is/caring");

        int numberOfInstancesToRegister = 50;
        IntStream.range(0, numberOfInstancesToRegister)
                .parallel()
                .forEach(n -> {
                    TestingServiceInstance s = makeServiceInstance(
                            "test-concurrent-registration-" + n,
                            "http://concurrency.is/hard/" + n,
                            "org.ga4gh:beacon:1.0.0",
                            sharedOrg);
                    String id = registerServiceInstance(TEST_REALM, s, 201);
                    updateServiceInstance(TEST_REALM, id, s, 200);
                });

        List<TestingServiceInstance> serviceInstances = getServiceInstances();
        assertThat(serviceInstances, hasSize(numberOfInstancesToRegister));
    }

    @Test
    public void deleteServiceInstance_shouldNot_affectAnotherServiceSharingSameOrganization() {
        TestingOrganizationModel sharedOrg = new TestingOrganizationModel("Shared Org", "https://sharing.is/caring");
        TestingServiceInstance service1 = makeServiceInstance("test-beacon-aggregator", "http://beacon-aggregator-test-url.someorg.com", "org.ga4gh:beacon-aggregator:1.0.0", sharedOrg);
        String service1Id = registerServiceInstance(TEST_REALM, service1, 201);
        TestingServiceInstance service2 = makeServiceInstance("test-beacon", "http://beacon-test-random-url.someorg.com", "org.ga4gh:beacon:1.0.1", sharedOrg);
        registerServiceInstance(TEST_REALM, service2, 201);

        // 1. Delete service1
        deleteServiceInstance(service1Id);

        // @formatter:off
        // 2 Fetch all remaining instances
        RestAssured.given()
            .filter(validationFilter)
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
            .body("[0].organization.name", equalTo(sharedOrg.getName()))
            .body("[0].organization.url", equalTo(sharedOrg.getUrl()))
            .body("[0].environment", equalTo(service2.getEnvironment().toString()))
            .body("[0].createdAt", notNullValue())
            .body("[0].updatedAt", notNullValue())
            .body("[0].contactUrl", equalTo(service2.getContactUrl()))
            .body("[0].documentationUrl", equalTo(service2.getDocumentationUrl()))
            .body("[0].description", equalTo(service2.getDescription()));
        // @formatter:on
    }

}
