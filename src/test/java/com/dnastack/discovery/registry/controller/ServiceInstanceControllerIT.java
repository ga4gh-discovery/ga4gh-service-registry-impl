package com.dnastack.discovery.registry.controller;

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import com.dnastack.discovery.registry.TestApplication;
import com.dnastack.discovery.registry.domain.Maturity;
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
                .organization("MyOrg")
                .maturity(Maturity.TEST)
                .build();
        String serviceId = nodeService.registerInstance(service).getId();

        RestAssured.given()
                .filter(validationFilter)
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
                .body("version", equalTo(service.getVersion()))
                .body("type", equalTo(service.getType()))
                .body("url", equalTo(service.getUrl()))
                .body("name", equalTo(service.getName()))
                .body("organization", equalTo(service.getOrganization()))
                .body("maturity", equalTo(service.getMaturity().name()))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue())
                .body("contactUrl", equalTo(service.getContactUrl()))
                .body("documentationUrl", equalTo(service.getDocumentationUrl()))
                .body("description", equalTo(service.getDescription()));
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
                .filter(validationFilter)
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
                .type("org.ga4gh:beacon:1.0.1")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .documentationUrl("http://beacon-test-random-url.someorg.com")
                .version("1.0.1")
                .organization("MyOrg")
                .maturity(Maturity.TEST)
                .build();
        nodeService.registerInstance(service);

        RestAssured.given()
                .filter(validationFilter)
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
                .body("content[0].version", equalTo(service.getVersion()))
                .body("content[0].type", equalTo(service.getType()))
                .body("content[0].url", equalTo(service.getUrl()))
                .body("content[0].name", equalTo(service.getName()))
                .body("content[0].organization", equalTo(service.getOrganization()))
                .body("content[0].maturity", equalTo(service.getMaturity().name()))
                .body("content[0].createdAt", notNullValue())
                .body("content[0].updatedAt", notNullValue())
                .body("content[0].contactUrl", equalTo(service.getContactUrl()))
                .body("content[0].documentationUrl", equalTo(service.getDocumentationUrl()))
                .body("content[0].description", equalTo(service.getDescription()));
    }

    @Test
    public void getServiceInstanceTypes() {
        nodeService.registerInstance(ServiceInstanceRegistrationRequestModel.builder()
                .name("test-beacon-aggregator")
                .url("http://beacon-aggregator-test-url.someorg.com")
                .type("org.ga4gh:beacon-aggregator:1.0.0")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .documentationUrl("http://beacon-test-random-url.someorg.com")
                .version("1.0.0")
                .organization("MyOrg")
                .maturity(Maturity.TEST)
                .build());
        nodeService.registerInstance(ServiceInstanceRegistrationRequestModel.builder()
                .name("test-beacon")
                .url("http://beacon-test-url.someorg.com")
                .type("org.ga4gh:beacon:1.0.1")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .documentationUrl("http://beacon-test-random-url.someorg.com")
                .version("1.0.1")
                .organization("MyOrg")
                .maturity(Maturity.TEST)
                .build());
        nodeService.registerInstance(ServiceInstanceRegistrationRequestModel.builder()
                .name("test-portal")
                .url("http://user-portal-test-url.someorg.com")
                .type("org.ga4gh:user-portal:1")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .documentationUrl("http://beacon-test-random-url.someorg.com")
                .version("1")
                .organization("MyOrg")
                .maturity(Maturity.TEST)
                .build());

        RestAssured.given()
                .filter(validationFilter)
                .accept(ContentType.JSON)
                .log().method()
                .log().uri()
                .get("http://localhost:" + port + "/services/types")
                .then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(".", containsInAnyOrder(
                        "org.ga4gh:beacon:1.0.1",
                        "org.ga4gh:beacon-aggregator:1.0.0",
                        "org.ga4gh:user-portal:1"
                ));
    }

    @Test
    public void getServiceInstanceTypes_empty() {
        RestAssured.given()
                .filter(validationFilter)
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
                .type("org.ga4gh:beacon:1.0.1")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .documentationUrl("http://beacon-test-random-url.someorg.com")
                .version("1.0.1")
                .organization("MyOrg")
                .maturity(Maturity.TEST)
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
                .name("test-beacon-aggregator")
                .url("http://beacon-aggregator-test-url.someorg.com")
                .type("org.ga4gh:beacon-aggregator:1.0.0")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .documentationUrl("http://beacon-test-random-url.someorg.com")
                .version("1.0.0")
                .organization("MyOrg")
                .maturity(Maturity.TEST)
                .build();
        String service1Id = nodeService.registerInstance(service1).getId();
        ServiceInstanceRegistrationRequestModel service2 = ServiceInstanceRegistrationRequestModel.builder()
                .name("test-beacon")
                .url("http://beacon-test-random-url.someorg.com")
                .type("org.ga4gh:beacon:1.0.1")
                .contactUrl("beacon-admin@someorg.com")
                .description("description")
                .documentationUrl("http://beacon-test-random-url.someorg.com")
                .version("1.0.1")
                .organization("MyOrg")
                .maturity(Maturity.TEST)
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
                .filter(validationFilter)
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
                .body("content[0].version", equalTo(service2.getVersion()))
                .body("content[0].type", equalTo(service2.getType()))
                .body("content[0].url", equalTo(service2.getUrl()))
                .body("content[0].name", equalTo(service2.getName()))
                .body("content[0].organization", equalTo(service2.getOrganization()))
                .body("content[0].maturity", equalTo(service2.getMaturity().name()))
                .body("content[0].createdAt", notNullValue())
                .body("content[0].updatedAt", notNullValue())
                .body("content[0].contactUrl", equalTo(service2.getContactUrl()))
                .body("content[0].documentationUrl", equalTo(service2.getDocumentationUrl()))
                .body("content[0].description", equalTo(service2.getDescription()));
    }

}
