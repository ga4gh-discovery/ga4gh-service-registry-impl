package com.dnastack.dos.registry.controller;

import com.dnastack.dos.registry.downstream.passthru.PassThruDataClient;
import com.dnastack.dos.registry.util.SecurityTestUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test cases for DataObjectController
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
@RunWith(SpringRunner.class)
//  @SpringBootTest annotation (loads entire Application context) is required for aspectJ
@SpringBootTest
@WebAppConfiguration
@ActiveProfiles({"it"})
public class DataObjectControllerTest {

    public static final String OBJECTS_ENDPOINT = "/ga4gh/registry/dos/v1/dataobjects";
    public static final String NODE_ENDPOINT = "/ga4gh/registry/dos/v1/nodes";
    public static final String OAUTH_SIGNED_KEY = "Authorization";
    public static final String OAUTH_SIGNED_KEY_VALUE = "Dummy";

    private static final ObjectMapper mapper = new ObjectMapper();

    private Resource set0 = new ClassPathResource("resp-mock/DataNodeResponse_set0.json");
    private Resource set1 = new ClassPathResource("resp-mock/DataNodeResponse_set1.json");

    @Autowired
    private RestTemplate objectsServiceRestTemplate;

    private MockRestServiceServer mockRestServiceServer;

    @Autowired
    private PassThruDataClient dataClient;

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

        this.mockRestServiceServer = MockRestServiceServer.bindTo(this.objectsServiceRestTemplate)
                .build();
    }

    @Test
    public void getDataObjectsTest() throws Exception {

        String dosNodeUrl = "http://dnastack.demo.com";
        //prepare data
        IntStream.range(1, 10).forEach(i -> {
            try {
                createANode("demo_node_"+i, dosNodeUrl, "demo desc "+i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //set up the mocks
        this.mockRestServiceServer
                .expect(ExpectedCount.manyTimes(), requestTo(dosNodeUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(this.set0, MediaType.APPLICATION_JSON_UTF8));

        MvcResult result = mockMvc.perform(
                get(OBJECTS_ENDPOINT)
                        .with(SecurityTestUtil.authDosUser())
                        .header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.dos_objects", isA(List.class)))
                .andExpect(jsonPath("$.dos_objects", hasSize(50))) //default page size
                .andExpect(jsonPath("$.next_page_token").exists())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

    }

    @Test
    public void getDataObjectsTest_WithPageSize() throws Exception {

        String dosNodeUrl = "http://dnastack.demo.com";
        //prepare data
        IntStream.range(1, 10).forEach(i -> {
            try {
                createANode("demo_node_"+i, dosNodeUrl, "demo desc "+i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //set up the mocks
        this.mockRestServiceServer
                .expect(ExpectedCount.manyTimes(), requestTo(dosNodeUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(this.set0, MediaType.APPLICATION_JSON_UTF8));

        MvcResult result = mockMvc.perform(
                get(OBJECTS_ENDPOINT)
                        .with(SecurityTestUtil.authDosUser())
                        .header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .param("page_size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.dos_objects", isA(List.class)))
                .andExpect(jsonPath("$.dos_objects", hasSize(5))) //default page size
                .andExpect(jsonPath("$.next_page_token").exists())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

    }

    @Test
    public void getDataObjectsTest_WithSpecificNodeIds() throws Exception {

        String dosNodeUrl = "http://dnastack.demo.com";
        //prepare data
        List<String> ids = new ArrayList<>();
        IntStream.range(1, 10).forEach(i -> {
            try {
                String nodeId = createANode("demo_node_" + i, dosNodeUrl, "demo desc " + i);
                ids.add(nodeId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        System.out.println(ids);
        String targetedIds = ids.stream().limit(2).collect(Collectors.joining(","));

        //set up the mocks
        this.mockRestServiceServer
                .expect(ExpectedCount.manyTimes(), requestTo(dosNodeUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(this.set0, MediaType.APPLICATION_JSON_UTF8));

        MvcResult result = mockMvc.perform(
                get(OBJECTS_ENDPOINT)
                        .with(SecurityTestUtil.authDosUser())
                        .header(OAUTH_SIGNED_KEY, OAUTH_SIGNED_KEY_VALUE)
                        .param("node_ids", targetedIds)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.dos_objects", isA(List.class)))
                .andExpect(jsonPath("$.dos_objects", hasSize(20))) //default page size
                .andExpect(jsonPath("$.next_page_token").doesNotExist())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

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

    }

}