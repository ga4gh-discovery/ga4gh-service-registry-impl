package com.dnastack.dos.registry.repository;

import com.dnastack.dos.registry.DosRegistryApplication;
import com.dnastack.dos.registry.model.Ga4ghDataNode;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test cases against a local MySQL instance.
 * Assumption: Local MySQL instance is up and running.
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
@SpringBootTest(classes = DosRegistryApplication.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("local")
public class Ga4ghDataNodeRepositoryLocalIT {

    @Autowired
    Ga4ghDataNodeRepository repository;

    Gson gson = new Gson();

    @Test
    public void whenAddDataNode_thenReturnDataNode() {
        // given
        String id = "aaa-bbb-ccc";

        boolean exists = repository.exists(id);
        Ga4ghDataNode dataNode;

        if (!exists) {
            //brand new entity
            String name = "test_dos_node";
            dataNode = new Ga4ghDataNode();
            dataNode.setId(id);
            dataNode.setName(name);

            Set<String> aliases = Stream.of("test1", "test2").collect(Collectors.toSet());
            dataNode.setAliases(gson.toJson(aliases));

            Map<String, String> metadata = new HashMap<>();
            metadata.put("category", "cancer");
            metadata.put("kind", "kids");
            dataNode.setMetaData(metadata);
        } else {
            dataNode = repository.findOne(id);
            //update what needs to be updated only...
            Map<String, String> metadata = dataNode.getMetaData();
            metadata.put("additoinal", "additional");

            dataNode.setMetaData(metadata);
        }
        repository.save(dataNode);

        // when
        Ga4ghDataNode found = repository.findOne(dataNode.getId());

        // then
        assertTrue(found.getName().equals(dataNode.getName()));

        if(exists) {
            assertEquals(3, found.getMetaData().size());
            assertEquals(2, gson.toJson(found.getAliases()).length());
        } else {
            assertEquals(2, found.getMetaData().size());
            assertEquals(2, gson.toJson(found.getAliases()).length());

        }
    }

    @Test
    public void whenFetchDataNodeByPage_thenReturnDataNodePage() {
        // given
        String customerId = "demo-customer";
        IntStream.range(0,100).forEach(i -> {
            String id = UUID.randomUUID().toString();
            String name = "test_dos_node-"+i;
            Ga4ghDataNode dataNode = new Ga4ghDataNode();
            dataNode.setCustomerId(customerId);
            dataNode.setId(id);
            dataNode.setName(name);
            Set<String> aliases = Stream.of("test1", "test2").collect(Collectors.toSet());
            dataNode.setAliases(gson.toJson(aliases));
            Map<String, String> metadata = new HashMap<>();
            metadata.put("category", "cancer");
            metadata.put("kind", "kids");
            dataNode.setMetaData(metadata);

            repository.save(dataNode);

        });

        // when
        Page<Ga4ghDataNode> dataNodes = repository.findByCustomerId(customerId, new PageRequest(0, 10));
        Page<Ga4ghDataNode> dataNodesNextPage = repository.findByCustomerId(customerId, new PageRequest(1, 10));


        // then
        assertEquals(10, dataNodes.getSize());
    }

}