package com.dnastack.dos.registry.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
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

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        //mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        mockMvc = MockMvcBuilders.standaloneSetup(dataNodeController)
                .setControllerAdvice(dataNodeControllerAdvice)
                .build();
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

        mockMvc.perform(
                post(NODE_ENDPOINT)
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


    }

    @Test
    public void deleteNodeTest() throws Exception {
        String node_id = createANode("test_to_delete", "http://dummy-delete.org", "dummy one to delete");

        mockMvc.perform(
                delete(NODE_ENDPOINT + "/" + node_id)
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
    public void getNodeByIdTest() throws Exception {
        String node_id = createANode("test_to_get", "http://dummy-get.org", "dummy one to retrieve");

        mockMvc.perform(
                get(NODE_ENDPOINT + "/" + node_id)
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
    public void getNodesTest() throws Exception {
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