package com.dnastack.discovery.registry.controller;

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import com.dnastack.discovery.registry.TestApplication;
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

import static org.hamcrest.Matchers.equalTo;

@SuppressWarnings("Duplicates")
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(classes = TestApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ServiceInfoControllerIT {

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
    public void getServiceInfo() {
        RestAssured.given()
                .filter(validationFilter)
                .accept(ContentType.JSON)
                .log()
                .method()
                .log()
                .uri()
                .get("http://localhost:" + port + "/service-info")
                .then()
                .log()
                .ifValidationFails()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo("org.ga4gh.service-registry"))
                .body("name", equalTo("GA4GH Service Registry Reference Implementation"))
                .body("type", equalTo("org.ga4gh.service-registry:1.0.0"))
                .body("description", equalTo("Reference implementation of GA4GH Service Registry"))
                .body("organization.name", equalTo("GA4GH"))
                .body("organization.url", equalTo("https://example.com"))
                .body("contactUrl", equalTo("mailto:support@example.com"))
                .body("documentationUrl", equalTo("https://github.com/ga4gh-discovery/ga4gh-service-registry"))
                .body("environment", equalTo("dev"))
                .body("version", equalTo("1.0.0"));
    }

}
