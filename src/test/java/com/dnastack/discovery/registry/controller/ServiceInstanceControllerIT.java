package com.dnastack.discovery.registry.controller;

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import com.dnastack.discovery.registry.TestApplication;
import com.dnastack.discovery.registry.domain.Environment;
import com.dnastack.discovery.registry.model.OrganizationModel;
import com.dnastack.discovery.registry.model.ServiceInstanceRegistrationRequestModel;
import com.dnastack.discovery.registry.service.ServiceInstanceService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
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

    private static String TEST_REALM = "e2e-test-" + System.currentTimeMillis();

    @Value("${app.service-registry.spec-url}")
    private String specYamlUrl;
    private OpenApiValidationFilter validationFilter;

    @LocalServerPort
    private Integer port;
    @Inject
    private ServiceInstanceService nodeService;

    @Before
    public void setUp() {
        this.validationFilter = new OpenApiValidationFilter(specYamlUrl);
    }

    @Test
    public void getServiceInstanceById_instanceExistsWithId() {
        ServiceInstanceRegistrationRequestModel service = ServiceInstanceRegistrationRequestModel.builder()
                .name("test-beacon")
                .url("http://beacon-test-random-url.someorg.com")
                .type("org.ga4gh:beacon:1.0.1")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .documentationUrl("http://beacon-test-random-url.someorg.com")
                .version("1.0.1")
                .organization(OrganizationModel.builder().name("MyOrg").url("http://example.com").build())
                .environment(Environment.TEST)
                .build();
        String serviceId = nodeService.registerInstance(TEST_REALM, service).getId();

        // @formatter:off
        RestAssured.given()
                .filter(validationFilter)
                .accept(ContentType.JSON)
                .log().method()
                .log().uri()
                .header("Service-Registry-Realm", TEST_REALM)
                .pathParam("serviceId", serviceId)
                .get("http://localhost:" + port + "/services/{serviceId}")
                .then()
                .log().ifValidationFails()
                .statusCode(HttpStatus.OK.value())
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
    public void getServiceInstanceById_instanceNotExistsWithId() {
        // @formatter:off
        RestAssured.given()
                .accept(ContentType.JSON)
                .log().method()
                .log().uri()
                .header("Service-Registry-Realm", TEST_REALM)
                .pathParam("serviceId", UUID.randomUUID().toString())
                .get("http://localhost:" + port + "/services/{serviceId}")
                .then()
                .log().ifValidationFails()
                .statusCode(HttpStatus.NOT_FOUND.value());
        // @formatter:on
    }

    @Test
    public void getServiceInstances_noInstanceExists() {
        // @formatter:off
        RestAssured.given()
                .filter(validationFilter)
                .accept(ContentType.JSON)
                .log().method()
                .log().uri()
                .header("Service-Registry-Realm", TEST_REALM)
                .get("http://localhost:" + port + "/services")
                .then()
                .log().ifValidationFails()
                .statusCode(HttpStatus.OK.value())
                .body("", empty());
        // @formatter:on
    }

    @Test
    public void getServiceInstances_atLeastOneInstanceExists() {
        ServiceInstanceRegistrationRequestModel service = ServiceInstanceRegistrationRequestModel.builder()
                .name("test-beacon")
                .url("http://beacon-test-random-url.someorg.com")
                .type("org.ga4gh:beacon:1.0.1")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .documentationUrl("http://beacon-test-random-url.someorg.com")
                .version("1.0.1")
                .organization(OrganizationModel.builder().name("MyOrg").url("http://example.com").build())
                .environment(Environment.TEST)
                .build();
        nodeService.registerInstance(TEST_REALM, service);

        // @formatter:off
        RestAssured.given()
                .filter(validationFilter)
                .accept(ContentType.JSON)
                .log().method()
                .log().uri()
                .header("Service-Registry-Realm", TEST_REALM)
                .get("http://localhost:" + port + "/services")
                .then()
                .log().everything()
                .statusCode(HttpStatus.OK.value())
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
        nodeService.registerInstance(TEST_REALM, ServiceInstanceRegistrationRequestModel.builder()
                                             .name("test-beacon-aggregator")
                                             .url("http://beacon-aggregator-test-url.someorg.com")
                                             .type("org.ga4gh:beacon-aggregator:1.0.0")
                                             .contactUrl("beacon-admin@someorg.com")
                                             .description("description")
                                             .documentationUrl("http://beacon-test-random-url.someorg.com")
                                             .version("1.0.0")
                                             .organization(OrganizationModel.builder()
                                                                   .name("MyOrg")
                                                                   .url("http://example.com")
                                                                   .build())
                                             .environment(Environment.TEST)
                                             .build());
        nodeService.registerInstance(TEST_REALM, ServiceInstanceRegistrationRequestModel.builder()
                                             .name("test-beacon")
                                             .url("http://beacon-test-url.someorg.com")
                                             .type("org.ga4gh:beacon:1.0.1")
                                             .contactUrl("beacon-admin@someorg.com")
                                             .description("description")
                                             .documentationUrl("http://beacon-test-random-url.someorg.com")
                                             .version("1.0.1")
                                             .organization(OrganizationModel.builder()
                                                                   .name("MyOrg")
                                                                   .url("http://example.com")
                                                                   .build())
                                             .environment(Environment.TEST)
                                             .build());
        nodeService.registerInstance(TEST_REALM, ServiceInstanceRegistrationRequestModel.builder()
                                             .name("test-portal")
                                             .url("http://user-portal-test-url.someorg.com")
                                             .type("org.ga4gh:user-portal:1")
                                             .contactUrl("beacon-admin@someorg.com")
                                             .description("description")
                                             .documentationUrl("http://beacon-test-random-url.someorg.com")
                                             .version("1")
                                             .organization(OrganizationModel.builder()
                                                                   .name("MyOrg")
                                                                   .url("http://example.com")
                                                                   .build())
                                             .environment(Environment.TEST)
                                             .build());

        // @formatter:off
        RestAssured.given()
                .filter(validationFilter)
                .accept(ContentType.JSON)
                .log().method()
                .log().uri()
                .header("Service-Registry-Realm", TEST_REALM)
                .get("http://localhost:" + port + "/services/types")
                .then()
                .log().ifValidationFails()
                .statusCode(HttpStatus.OK.value())
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
                .header("Service-Registry-Realm", TEST_REALM)
                .get("http://localhost:" + port + "/services/types")
                .then()
                .log().ifValidationFails()
                .statusCode(HttpStatus.OK.value())
                .body(".", empty());
        // @formatter:on
    }

    @Test
    public void deleteServiceInstanceById_instanceExistsWithId() {
        ServiceInstanceRegistrationRequestModel service = ServiceInstanceRegistrationRequestModel.builder()
                .name("test-beacon")
                .url("http://beacon-test-random-url.someorg.com")
                .type("org.ga4gh:beacon:1.0.1")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .documentationUrl("http://beacon-test-random-url.someorg.com")
                .version("1.0.1")
                .organization(OrganizationModel.builder().name("MyOrg").url("http://example.com").build())
                .environment(Environment.TEST)
                .build();
        String serviceId = nodeService.registerInstance(TEST_REALM, service).getId();

        // @formatter:off
        RestAssured.given()
                .accept(ContentType.JSON)
                .log().method()
                .log().uri()
                .header("Service-Registry-Realm", TEST_REALM)
                .pathParam("serviceId", serviceId)
                .auth().basic("dev", "dev")
                .delete("http://localhost:" + port + "/services/{serviceId}")
                .then()
                .log().ifValidationFails()
                .statusCode(HttpStatus.NO_CONTENT.value());
        // @formatter:on
    }

    @Test
    public void deregisterServiceInstanceById_instanceExistsWithId_ensureNoSideEffect() {
        ServiceInstanceRegistrationRequestModel service1 = ServiceInstanceRegistrationRequestModel.builder()
                .name("test-beacon-aggregator")
                .url("http://beacon-aggregator-test-url.someorg.com")
                .type("org.ga4gh:beacon-aggregator:1.0.0")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .documentationUrl("http://beacon-test-random-url.someorg.com")
                .version("1.0.0")
                .organization(OrganizationModel.builder().name("MyOrg").url("http://example.com").build())
                .environment(Environment.TEST)
                .build();
        String service1Id = nodeService.registerInstance(TEST_REALM, service1).getId();
        ServiceInstanceRegistrationRequestModel service2 = ServiceInstanceRegistrationRequestModel.builder()
                .name("test-beacon")
                .url("http://beacon-test-random-url.someorg.com")
                .type("org.ga4gh:beacon:1.0.1")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .documentationUrl("http://beacon-test-random-url.someorg.com")
                .version("1.0.1")
                .organization(OrganizationModel.builder().name("MyOrg").url("http://example.com").build())
                .environment(Environment.TEST)
                .build();
        nodeService.registerInstance(TEST_REALM, service2).getId();

        // @formatter:off
        // 1. Delete service1
        RestAssured.given()
                .accept(ContentType.JSON)
                .log().method()
                .log().uri()
                .header("Service-Registry-Realm", TEST_REALM)
                .pathParam("serviceId", service1Id)
                .auth().basic("dev", "dev")
                .delete("http://localhost:" + port + "/services/{serviceId}")
                .then()
                .log().ifValidationFails()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // 2 Fetch all remaining instances
        RestAssured.given()
                .filter(validationFilter)
                .accept(ContentType.JSON)
                .log().method()
                .log().uri()
                .header("Service-Registry-Realm", TEST_REALM)
                .get("http://localhost:" + port + "/services")
                .then()
                .log().everything()
                .statusCode(HttpStatus.OK.value())
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
