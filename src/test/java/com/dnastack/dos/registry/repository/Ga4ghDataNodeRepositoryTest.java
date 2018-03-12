package com.dnastack.dos.registry.repository;

import com.dnastack.dos.registry.model.Ga4ghDataNode;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * This class servers as ...
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class Ga4ghDataNodeRepositoryTest {

    @Autowired
    Ga4ghDataNodeRepository repository;

    Gson gson = new Gson();

    @Test
    public void whenAddDataNode_thenReturnDataNode() {
        // given
        String id = "aaa-bbb-ccc";
        String name = "test_dos_node";
        Ga4ghDataNode dataNode = new Ga4ghDataNode();
        dataNode.setId(id);
        dataNode.setName(name);
        Set<String> aliases = Stream.of("test1", "test2").collect(Collectors.toSet());
        dataNode.setAliases(gson.toJson(aliases));
        repository.save(dataNode);

        // when
        Ga4ghDataNode found = repository.findOne(dataNode.getId());

        // then
        assertTrue(found.getName().equals(dataNode.getName()));
    }

    //TODO: for now, for the sake of testing, customerId can be set as "" EMPTY STRING.
    //TODO: needs further discussion with Jim on the validity of this customerId field.
    @Test
    public void whenFetchDataNodeByPage_thenReturnDataNodePage_EmptyCustomerId() {
        // given
        final String customerId1 = "";

        IntStream.range(0,50).forEach(i -> {
            String id = UUID.randomUUID().toString();
            String name = "test_dos_node1-"+i;
            String description = UUID.randomUUID().toString();
            Ga4ghDataNode dataNode = new Ga4ghDataNode();
            dataNode.setCustomerId(customerId1);
            dataNode.setId(id);
            dataNode.setName(name);
            dataNode.setDescription(description);

            repository.save(dataNode);

        });

        // when
        Page<Ga4ghDataNode> nodesByNameLike =
                repository.findByCustomerIdAndNameLike("", "%", new PageRequest(0,10));

        Assert.assertEquals(50, nodesByNameLike.getTotalElements());
        Assert.assertTrue(nodesByNameLike.isFirst());
        Assert.assertFalse(nodesByNameLike.isLast());
        Assert.assertTrue(nodesByNameLike.hasNext());
        Assert.assertTrue(nodesByNameLike.hasContent());
        Assert.assertFalse(nodesByNameLike.hasPrevious());
        Assert.assertEquals(5, nodesByNameLike.getTotalPages());
        Assert.assertEquals(10, nodesByNameLike.getSize());
        Assert.assertEquals(10, nodesByNameLike.getNumberOfElements());

    }

    @Test
    public void whenFetchDataNodeByPage_thenReturnDataNodePage_HasPageOrNot() {
        // given
        final String customerId1 = "demo-customer-1";

        IntStream.range(0,50).forEach(i -> {
            String id = UUID.randomUUID().toString();
            String name = "test_dos_node1-"+i;
            String description = UUID.randomUUID().toString();
            Ga4ghDataNode dataNode = new Ga4ghDataNode();
            dataNode.setCustomerId(customerId1);
            dataNode.setId(id);
            dataNode.setName(name);
            dataNode.setDescription(description);

            repository.save(dataNode);

        });

        // when
        Page<Ga4ghDataNode> nodesByNameLike =
                repository.findByCustomerIdAndNameLike(customerId1, "%", new PageRequest(0,10));

        Assert.assertEquals(50, nodesByNameLike.getTotalElements());
        Assert.assertTrue(nodesByNameLike.isFirst());
        Assert.assertFalse(nodesByNameLike.isLast());
        Assert.assertTrue(nodesByNameLike.hasNext());
        Assert.assertTrue(nodesByNameLike.hasContent());
        Assert.assertFalse(nodesByNameLike.hasPrevious());
        Assert.assertEquals(5, nodesByNameLike.getTotalPages());
        Assert.assertEquals(10, nodesByNameLike.getSize());
        Assert.assertEquals(10, nodesByNameLike.getNumberOfElements());


        // when
        Page<Ga4ghDataNode> nodesByNameLike_nonExsist =
                repository.findByCustomerIdAndNameLike(customerId1, "not_exist_%", new PageRequest(0,50));

        Assert.assertEquals(0, nodesByNameLike.getTotalElements());
        Assert.assertTrue(nodesByNameLike.isFirst());
        Assert.assertFalse(nodesByNameLike.isLast());
        Assert.assertFalse(nodesByNameLike.hasNext());
        Assert.assertFalse(nodesByNameLike.hasContent());
        Assert.assertFalse(nodesByNameLike.hasPrevious());
        Assert.assertEquals(0, nodesByNameLike.getTotalPages());
        Assert.assertEquals(0, nodesByNameLike.getSize());
        Assert.assertEquals(0, nodesByNameLike.getNumberOfElements());

    }

    @Test
    public void whenFetchDataNodeByPage_thenReturnDataNodePage_CaseInsensitive() {
        // given
        final String customerId = "demo-customer-1";
        IntStream.range(0,50).forEach(i -> {
            String id = UUID.randomUUID().toString();
            String name = "test_dos_node1-"+i;
            String description = UUID.randomUUID().toString();
            Ga4ghDataNode dataNode = new Ga4ghDataNode();
            dataNode.setCustomerId(customerId);
            dataNode.setId(id);
            dataNode.setName(name);
            dataNode.setDescription(description);
            Set<String> aliases = Stream.of("test1", "test2").collect(Collectors.toSet());
            dataNode.setAliases(gson.toJson(aliases));
            Map<String, String> metadata = new HashMap<>();
            metadata.put("category", "cancer");
            metadata.put("kind", "kids");
            dataNode.setMetaData(metadata);

            repository.save(dataNode);

        });

        String customerId2 = "demo-customer-2";
        IntStream.range(0,50).forEach(i -> {
            String id = UUID.randomUUID().toString();
            String name = "test_dos_node2-"+i;
            Ga4ghDataNode dataNode = new Ga4ghDataNode();
            dataNode.setCustomerId(customerId2);
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

        String customerId3 = "demo-customer-3";
        IntStream.range(0,50).forEach(i -> {
            String id = UUID.randomUUID().toString();
            String name = "test_dos_node3-"+i;
            Ga4ghDataNode dataNode = new Ga4ghDataNode();
            dataNode.setCustomerId(customerId3);
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
        Page<Ga4ghDataNode> dataNodesLowerCase =
                repository.findByCustomerIdAndNameIgnoreCaseContainingAndAliasesIgnoreCaseContainingAndDescriptionIgnoreCaseContaining(
                        customerId,
                        "test",
                        "",
                        "",
                        new PageRequest(0,10));

        Page<Ga4ghDataNode> dataNodesUpperCase =
                repository.findByCustomerIdAndNameIgnoreCaseContainingAndAliasesIgnoreCaseContainingAndDescriptionIgnoreCaseContaining(
                        customerId,
                        "TEST",
                        "",
                        "",
                        new PageRequest(0,10));


        Assert.assertEquals(dataNodesLowerCase.getTotalElements(), dataNodesUpperCase.getTotalElements());

    }
}