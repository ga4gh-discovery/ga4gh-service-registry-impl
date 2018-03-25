package com.dnastack.dos.registry.controller;

import com.dnastack.dos.registry.util.SecurityTestUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.collection.IsMapContaining;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test cases for DataNodeController
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
@RunWith(SpringRunner.class)
//  @SpringBootTest annotation (loads entire Application context) is required for aspectJ
@SpringBootTest
@WebAppConfiguration
@ActiveProfiles({"it"})
public class DataNodeControllerTest {

    public static final String NODE_ENDPOINT = "/ga4gh/registry/dos/v1/nodes";
    public static final String OAUTH_SIGNED_KEY = "Authorization";
    public static final String OAUTH_SIGNED_KEY_VALUE = "Dummy";

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    DataNodeController dataNodeController;

    @Autowired
    DataNodeControllerAdvice dataNodeControllerAdvice;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    FilterChainProxy springSecurityFilterChain;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc =
                MockMvcBuilders.webAppContextSetup(wac)
                        .apply(springSecurity())
                        .build();

        /*
        mockMvc = MockMvcBuilders.standaloneSetup(dataNodeController)
                .setControllerAdvice(dataNodeControllerAdvice)
                .addFilters(springSecurityFilterChain)
                .apply(springSecurity())
                .build();

        */
    }

    @Test
    public void createNodeTest() throws Exception {

        Ga4ghDataNodeCreationRequestDto requestDto = new Ga4ghDataNodeCreationRequestDto();
        requestDto.setName("test-test");
        requestDto.setUrl("http://dnastack.com");
        requestDto.setDescription("dummy desc");
        requestDto.setMetaData(new HashMap<String, String>() {{
            put("test-key", "test-value");
        }});

        String reqeustBody = mapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                post(NODE_ENDPOINT)
                        .with(SecurityTestUtil.authDosOwner())
                        .header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqeustBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dos_node.id", notNullValue()))
                .andExpect(jsonPath("$.dos_node.name", is("test-test")))
                .andExpect(jsonPath("$.dos_node.url", is("http://dnastack.com")))
                .andExpect(jsonPath("$.dos_node.description", is("dummy desc")))
                //.andExpect(jsonPath("$.dos_node.aliases", isA(List.class)))
                .andExpect(jsonPath("$.dos_node.meta_data", isA(Map.class)))
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

    }

    @Test
    public void createNodeTest_WithoutAuthorizationHeader_ShouldReturn400() throws Exception {

        Ga4ghDataNodeCreationRequestDto requestDto = new Ga4ghDataNodeCreationRequestDto();
        requestDto.setName("test-test");
        requestDto.setUrl("http://dnastack.com");
        requestDto.setDescription("dummy desc");
        requestDto.setMetaData(new HashMap<String, String>() {{
            put("test-key", "test-value");
        }});

        String reqeustBody = mapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                post(NODE_ENDPOINT)
                        .with(SecurityTestUtil.authDosOwner())
                        //.header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqeustBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", isA(List.class)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].code", is("E9998")))
                .andExpect(jsonPath("$.errors[0].message", containsString("Missing request header 'Authorization' ")))
                .andExpect(jsonPath("$.errors[0].metadata", IsMapContaining.hasEntry("source", "VALIDATION")))
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

    }

    @Test
    public void createNodeTest_WithEmptyRequestBody_ShouldReturn400() throws Exception {

        MvcResult result = mockMvc.perform(
                post(NODE_ENDPOINT)
                        .with(SecurityTestUtil.authDosOwner())
                        .header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", isA(List.class)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].code", is("E9998")))
                .andExpect(jsonPath("$.errors[0].message", containsString("Required request body is missing")))
                .andExpect(jsonPath("$.errors[0].metadata", IsMapContaining.hasEntry("source", "VALIDATION")))
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

    }

    @Test
    public void createNodeTest_WithInvalidRequestBody_ShouldReturn400() throws Exception {

        String reqeustBody = "{\"invalid_field\": \"invalid_value\"}";

        MvcResult result = mockMvc.perform(
                post(NODE_ENDPOINT)
                        .with(SecurityTestUtil.authDosOwner())
                        .header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqeustBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", isA(List.class)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].code", is("E9998")))
                .andExpect(jsonPath("$.errors[0].message", containsString("Validation failed ")))
                .andExpect(jsonPath("$.errors[0].metadata", IsMapContaining.hasEntry("source", "VALIDATION")))
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

    }

    @Test
    public void createNodeTest_WithInvalidAuthority_ShouldReturn403() throws Exception {

        Ga4ghDataNodeCreationRequestDto requestDto = new Ga4ghDataNodeCreationRequestDto();
        requestDto.setName("test-test");
        requestDto.setUrl("http://dnastack.com");
        requestDto.setDescription("dummy desc");
        requestDto.setMetaData(new HashMap<String, String>() {{
            put("test-key", "test-value");
        }});

        String reqeustBody = mapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                post(NODE_ENDPOINT)
                        .with(SecurityTestUtil.authDosUser())
                        .header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqeustBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errors", isA(List.class)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].code", is("E4003")))
                .andExpect(jsonPath("$.errors[0].message", containsString("Access is denied")))
                .andExpect(jsonPath("$.errors[0].metadata", IsMapContaining.hasEntry("source", "SECURITY")))
                .andReturn();

        System.out.println("RESULT: " + result.getResponse().getContentAsString());

    }

    @Test
    public void deleteNodeTest() throws Exception {
        String node_id = createANode("test_to_delete", "http://dummy-delete.org", "dummy one to delete");

        mockMvc.perform(
                delete(NODE_ENDPOINT + "/" + node_id)
                        .with(SecurityTestUtil.authDosOwner())
                        .header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dos_node.id", is(node_id)))
                .andExpect(jsonPath("$.dos_node.name", isEmptyOrNullString()))
                .andExpect(jsonPath("$.dos_node.url", isEmptyOrNullString()))
                .andExpect(jsonPath("$.dos_node.description", isEmptyOrNullString()))
                .andReturn();

    }

    @Test
    public void deleteNodeTest_WithInvalidAuthority_ShouldReturn403() throws Exception {

        String node_id = createANode("test_to_delete", "http://dummy-delete.org", "dummy one to delete");

        MvcResult result = mockMvc.perform(
                delete(NODE_ENDPOINT + "/" + node_id)
                        .with(SecurityTestUtil.authDosUser())
                        .header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errors", isA(List.class)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].code", is("E4003")))
                .andExpect(jsonPath("$.errors[0].message", containsString("Access is denied")))
                .andExpect(jsonPath("$.errors[0].metadata", IsMapContaining.hasEntry("source", "SECURITY")))
                .andReturn();

        System.out.println("RESULT: " + result.getResponse().getContentAsString());

    }

    @Test
    public void deleteNodeTest_WithoutOwnership_ShouldReturn403() throws Exception {

        String node_id = createANode("test_to_delete", "http://dummy-delete.org", "dummy one to delete");

        MvcResult result = mockMvc.perform(
                delete(NODE_ENDPOINT + "/" + node_id)
                        .with(SecurityTestUtil.authDosOwner2())
                        .header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errors", isA(List.class)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].code", is("E4003")))
                .andExpect(jsonPath("$.errors[0].message", containsString("Operation can only be performed by its expected owner")))
                .andExpect(jsonPath("$.errors[0].metadata", IsMapContaining.hasEntry("source", "SECURITY")))
                .andReturn();

        System.out.println("RESULT: " + result.getResponse().getContentAsString());

    }

    @Test
    public void deleteNodeTest_WithInvalidNodeId_ShouldReturn404() throws Exception {

        String node_id = "ID_NOT_EXIST";

        MvcResult result = mockMvc.perform(
                delete(NODE_ENDPOINT + "/" + node_id)
                        .with(SecurityTestUtil.authDosOwner())
                        .header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors", isA(List.class)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].code", is("E1000")))
                .andExpect(jsonPath("$.errors[0].message", is("Resource not found with nodeId=ID_NOT_EXIST")))
                .andExpect(jsonPath("$.errors[0].metadata", IsMapContaining.hasEntry("source", "VALIDATION")))
                .andReturn();

    }

    @Test
    public void getNodeByIdTest() throws Exception {
        String node_id = createANode("test_to_get", "http://dummy-get.org", "dummy one to retrieve");

        mockMvc.perform(
                get(NODE_ENDPOINT + "/" + node_id)
                        .with(SecurityTestUtil.authDosUser())
                        .header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dos_node.id", is(node_id)))
                .andExpect(jsonPath("$.dos_node.name", is("test_to_get")))
                .andExpect(jsonPath("$.dos_node.url", is("http://dummy-get.org")))
                .andExpect(jsonPath("$.dos_node.description", is("dummy one to retrieve")))
                .andReturn();

    }

    @Test
    public void getNodeByIdTest_WithoutAuthorizationHeader_ShouldReturn400() throws Exception {
        String node_id = createANode("test_to_get", "http://dummy-get.org", "dummy one to retrieve");

        MvcResult result = mockMvc.perform(
                get(NODE_ENDPOINT + "/" + node_id)
                        .with(SecurityTestUtil.authDosUser())
                        //.header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", isA(List.class)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].code", is("E9998")))
                .andExpect(jsonPath("$.errors[0].message", containsString("Missing request header 'Authorization' ")))
                .andExpect(jsonPath("$.errors[0].metadata", IsMapContaining.hasEntry("source", "VALIDATION")))
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

    }

    @Test
    public void getNodeByIdTest_WithInvalidNodeId_ShouldReturn404() throws Exception {

        String node_id = "ID_NOT_EXIST";

        MvcResult result = mockMvc.perform(
                get(NODE_ENDPOINT + "/" + node_id)
                        .with(SecurityTestUtil.authDosUser())
                        .header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors", isA(List.class)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].code", is("E1000")))
                .andExpect(jsonPath("$.errors[0].message", is("Resource not found with nodeId=ID_NOT_EXIST")))
                .andExpect(jsonPath("$.errors[0].metadata", IsMapContaining.hasEntry("source", "VALIDATION")))
                .andReturn();

    }

    @Test
    public void getNodesTest() throws Exception {

        //prepare data
        IntStream.range(1, 50).forEach(i -> {
            try {
                createANode("demo_node_"+i, "http://demo."+i, "demo desc "+i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        MvcResult result = mockMvc.perform(
                get(NODE_ENDPOINT)
                        .with(SecurityTestUtil.authDosUser())
                        .header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dos_nodes", isA(List.class)))
                .andExpect(jsonPath("$.dos_nodes", hasSize(DataNodeController.DEFAULT_PAGE_SIZE))) // default page size
                .andExpect(jsonPath("$.next_page_token").exists())
                .andExpect(jsonPath("$.dos_nodes[0].name", containsString("demo_node_")))
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

    }

    @Test
    public void getNodesTest_EmptyList() throws Exception {

        MvcResult result = mockMvc.perform(
                get(NODE_ENDPOINT)
                        .with(SecurityTestUtil.authDosUser())
                        .header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dos_nodes", isA(List.class)))
                .andExpect(jsonPath("$.dos_nodes", hasSize(0)))
                .andExpect(jsonPath("$.next_page_token").doesNotExist())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

    }

    @Test
    public void getNodesTest_WithPageSize() throws Exception {

        //prepare data
        IntStream.range(1, 50).forEach(i -> {
            try {
                createANode("demo_node_"+i, "http://demo."+i, "demo desc "+i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        MvcResult result = mockMvc.perform(
                get(NODE_ENDPOINT)
                        .with(SecurityTestUtil.authDosUser())
                        .header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .param("page_size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dos_nodes", isA(List.class)))
                .andExpect(jsonPath("$.dos_nodes", hasSize(5)))
                .andExpect(jsonPath("$.next_page_token").exists())
                .andExpect(jsonPath("$.dos_nodes[0].name", containsString("demo_node_")))
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

    }

    @Test
    public void getNodesTest_WithPageToken() throws Exception {

        //prepare data
        IntStream.range(1, 50).forEach(i -> {
            try {
                createANode("demo_node_"+i, "http://demo."+i, "demo desc "+i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        MvcResult interim = mockMvc.perform(
                get(NODE_ENDPOINT)
                        .with(SecurityTestUtil.authDosUser())
                        .header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .param("page_size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dos_nodes", isA(List.class)))
                .andExpect(jsonPath("$.dos_nodes", hasSize(5)))
                .andExpect(jsonPath("$.next_page_token").exists())
                .andExpect(jsonPath("$.dos_nodes[0].name", containsString("demo_node_")))
                .andReturn();

        TypeReference<HashMap<String, Object>> typeRef
                = new TypeReference<HashMap<String, Object>>() {
        };

        HashMap<String, Object> map = mapper.readValue(interim.getResponse().getContentAsByteArray(), typeRef);

        String nextPageToken = (String) map.get("next_page_token");

        MvcResult result = mockMvc.perform(
                get(NODE_ENDPOINT)
                        .with(SecurityTestUtil.authDosUser())
                        .header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .param("page_token", nextPageToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dos_nodes", isA(List.class)))
                .andExpect(jsonPath("$.dos_nodes", hasSize(DataNodeController.DEFAULT_PAGE_SIZE)))
                .andExpect(jsonPath("$.next_page_token").exists())
                .andExpect(jsonPath("$.dos_nodes[0].name", containsString("demo_node_")))
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

    }

    @Test
    public void getNodesTest_WithInvalidPageToken_ShouldReturn500() throws Exception {

        String pageToken = "SOMETHING_INVALID";

        MvcResult result = mockMvc.perform(
                get(NODE_ENDPOINT)
                        .with(SecurityTestUtil.authDosUser())
                        .header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .param("page_token", pageToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errors", isA(List.class)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].code", is("E9999")))
                .andExpect(jsonPath("$.errors[0].message", is("Page Token (SOMETHING_INVALID) is not decode-able ")))
                .andExpect(jsonPath("$.errors[0].metadata", IsMapContaining.hasEntry("source", "INTERNAL")))
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

    }

    @Test
    public void updateNodeTest() throws Exception {

        String node_id = createANode("test_to_update", "http://dummy-update.org", "dummy one to update");

        Ga4ghDataNodeUpdateRequestDto requestDto = new Ga4ghDataNodeUpdateRequestDto();
        requestDto.setName("test_to_update2");
        requestDto.setDescription("dummy one to update2");
        requestDto.setMetaData(new HashMap<String, String>() {{
            put("test-key", "test-value");
            put("test-key2", "test-value2");
        }});

        String reqeustBody = mapper.writeValueAsString(requestDto);

        mockMvc.perform(
                put(NODE_ENDPOINT + "/" + node_id)
                        .with(SecurityTestUtil.authDosOwner())
                        .header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqeustBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dos_node.id", is(node_id)))
                .andExpect(jsonPath("$.dos_node.name", is("test_to_update2")))
                .andExpect(jsonPath("$.dos_node.url", is("http://dummy-update.org")))
                .andExpect(jsonPath("$.dos_node.description", is("dummy one to update2")))
                .andReturn();


    }

    @Test
    public void updateNodeTest_WithInvalidNodeId_ShouldReturn404() throws Exception {

        String node_id = "ID_NOT_EXIST";

        Ga4ghDataNodeUpdateRequestDto requestDto = new Ga4ghDataNodeUpdateRequestDto();
        requestDto.setName("test_to_update2");
        requestDto.setDescription("dummy one to update2");
        requestDto.setMetaData(new HashMap<String, String>() {{
            put("test-key", "test-value");
            put("test-key2", "test-value2");
        }});

        String reqeustBody = mapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                put(NODE_ENDPOINT + "/" + node_id)
                        .with(SecurityTestUtil.authDosOwner())
                        .header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqeustBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors", isA(List.class)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].code", is("E1000")))
                .andExpect(jsonPath("$.errors[0].message", is("Resource not found with nodeId=ID_NOT_EXIST")))
                .andExpect(jsonPath("$.errors[0].metadata", IsMapContaining.hasEntry("source", "VALIDATION")))
                .andReturn();

    }

    @Test
    public void updateNodeTest_WithEmptyRequestBody_ShouldReturn422() throws Exception {
        String node_id = createANode("test_to_update", "http://dummy-update.org", "dummy one to update");

        Ga4ghDataNodeUpdateRequestDto requestDto = new Ga4ghDataNodeUpdateRequestDto();

        String reqeustBody = mapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                put(NODE_ENDPOINT + "/" + node_id)
                        .with(SecurityTestUtil.authDosOwner())
                        .header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqeustBody))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors", isA(List.class)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].code", is("E8888")))
                //.andExpect(jsonPath("$.errors[0].timestamp", isA(DateTime.class)))
                .andExpect(jsonPath("$.errors[0].message", is("Nothing to update")))
                .andExpect(jsonPath("$.errors[0].metadata", IsMapContaining.hasEntry("source", "BIZ_VALIDATION")))
                .andReturn();


    }

    @Test
    public void updateNodeTest_WithInvalidAuthority_ShouldReturn403() throws Exception {

        String node_id = createANode("test_to_update", "http://dummy-update.org", "dummy one to update");

        Ga4ghDataNodeUpdateRequestDto requestDto = new Ga4ghDataNodeUpdateRequestDto();
        requestDto.setName("test_to_update2");
        requestDto.setDescription("dummy one to update2");
        requestDto.setMetaData(new HashMap<String, String>() {{
            put("test-key", "test-value");
            put("test-key2", "test-value2");
        }});

        String reqeustBody = mapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                put(NODE_ENDPOINT + "/" + node_id)
                        .with(SecurityTestUtil.authDosUser())
                        .header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqeustBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errors", isA(List.class)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].code", is("E4003")))
                .andExpect(jsonPath("$.errors[0].message", containsString("Access is denied")))
                .andExpect(jsonPath("$.errors[0].metadata", IsMapContaining.hasEntry("source", "SECURITY")))
                .andReturn();

        System.out.println("RESULT: " + result.getResponse().getContentAsString());

    }

    @Test
    public void updateNodeTest_WithoutOwnership_ShouldReturn403() throws Exception {

        String node_id = createANode("test_to_update", "http://dummy-update.org", "dummy one to update");

        Ga4ghDataNodeUpdateRequestDto requestDto = new Ga4ghDataNodeUpdateRequestDto();
        requestDto.setName("test_to_update2");
        requestDto.setDescription("dummy one to update2");
        requestDto.setMetaData(new HashMap<String, String>() {{
            put("test-key", "test-value");
            put("test-key2", "test-value2");
        }});

        String reqeustBody = mapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                put(NODE_ENDPOINT + "/" + node_id)
                        .with(SecurityTestUtil.authDosOwner2())
                        .header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqeustBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errors", isA(List.class)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].code", is("E4003")))
                .andExpect(jsonPath("$.errors[0].message", containsString("Operation can only be performed by its expected owner")))
                .andExpect(jsonPath("$.errors[0].metadata", IsMapContaining.hasEntry("source", "SECURITY")))
                .andReturn();

        System.out.println("RESULT: " + result.getResponse().getContentAsString());

    }


    public String createANode(String name, String url, String desc) throws Exception {

        Ga4ghDataNodeCreationRequestDto requestDto = new Ga4ghDataNodeCreationRequestDto();
        requestDto.setName(name);
        requestDto.setUrl(url);
        requestDto.setDescription(desc);
        requestDto.setMetaData(new HashMap<String, String>() {{
            put("test-key", "test-value");
        }});

        String reqeustBody = mapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                post(NODE_ENDPOINT)
                        .with(SecurityTestUtil.authDosOwner())
                        .header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqeustBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dos_node.id", notNullValue()))
                .andExpect(jsonPath("$.dos_node.name", is(name)))
                .andExpect(jsonPath("$.dos_node.url", is(url)))
                .andExpect(jsonPath("$.dos_node.description", is(desc)))
                //.andExpect(jsonPath("$.dos_node.aliases", isA(List.class)))
                .andExpect(jsonPath("$.dos_node.meta_data", isA(Map.class)))
                .andReturn();

        TypeReference<HashMap<String, Object>> typeRef
                = new TypeReference<HashMap<String, Object>>() {
        };

        HashMap<String, Object> map = mapper.readValue(result.getResponse().getContentAsByteArray(), typeRef);

        return (String) ((Map) map.get("dos_node")).get("id");

//        Ga4ghDataNodeResponseDto ga4ghDataNodeResponseDto = mapper.readValue(result.getResponse().getContentAsString(), Ga4ghDataNodeResponseDto.class);
//
//        return ga4ghDataNodeResponseDto.getDosNode().getId();

    }

}