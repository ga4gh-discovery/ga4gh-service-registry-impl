package com.dnastack.dos.registry.repository;

import com.dnastack.dos.registry.model.ServiceNodePage;
import com.dnastack.dos.registry.model.ServiceNode;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
public class ServiceNodeRepositoryTest {

    @Autowired
    ServiceNodeRepository repository;

    Gson gson = new Gson();

    @Test
    public void whenAddDataNode_thenReturnDataNode() {
        // given
        String id = "aaa-bbb-ccc";
        String name = "test_dos_node";
        ServiceNode serviceNode = new ServiceNode();
        serviceNode.setId(id);
        serviceNode.setName(name);
        Set<String> aliases = Stream.of("test1", "test2").collect(Collectors.toSet());
        serviceNode.setAliases(gson.toJson(aliases));
        repository.save(serviceNode);

        // when
        ServiceNode found = repository.findOne(serviceNode.getId());

        // then
        assertTrue(found.getName().equals(serviceNode.getName()));
    }

    @Test
    public void whenFetchDataNodeByPage_thenReturnDataNodePage_EmptyCustomerId() {
        // given
        final String ownerId1 = "";

        IntStream.range(0,50).forEach(i -> {
            String id = UUID.randomUUID().toString();
            String name = "test_dos_node1-"+i;
            String description = UUID.randomUUID().toString();
            ServiceNode serviceNode = new ServiceNode();
            serviceNode.setOwnerId(ownerId1);
            serviceNode.setId(id);
            serviceNode.setName(name);
            serviceNode.setDescription(description);

            repository.save(serviceNode);

        });

        // when
        Page<ServiceNode> nodesByNameLike =
                repository.findByOwnerIdAndNameLike("", "%", new PageRequest(0,10));

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
        final String ownerId1 = "demo-customer-1";

        IntStream.range(0,50).forEach(i -> {
            String id = UUID.randomUUID().toString();
            String name = "test_dos_node1-"+i;
            String description = UUID.randomUUID().toString();
            ServiceNode serviceNode = new ServiceNode();
            serviceNode.setOwnerId(ownerId1);
            serviceNode.setId(id);
            serviceNode.setName(name);
            serviceNode.setDescription(description);

            repository.save(serviceNode);

        });

        // when
        Page<ServiceNode> nodesByNameLike =
                repository.findByOwnerIdAndNameLike(ownerId1, "%", new PageRequest(0,10));

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
        Page<ServiceNode> nodesByNameLike_nonExsist =
                repository.findByOwnerIdAndNameLike(ownerId1, "not_exist_%", new PageRequest(0,50));

        Assert.assertEquals(0, nodesByNameLike_nonExsist.getTotalElements());
        Assert.assertTrue(nodesByNameLike_nonExsist.isFirst());
        Assert.assertTrue(nodesByNameLike_nonExsist.isLast());
        Assert.assertFalse(nodesByNameLike_nonExsist.hasNext());
        Assert.assertFalse(nodesByNameLike_nonExsist.hasContent());
        Assert.assertFalse(nodesByNameLike_nonExsist.hasPrevious());
        Assert.assertEquals(0, nodesByNameLike_nonExsist.getTotalPages());
        Assert.assertEquals(50, nodesByNameLike_nonExsist.getSize());
        Assert.assertEquals(0, nodesByNameLike_nonExsist.getNumberOfElements());

    }

    @Test
    public void whenFetchDataNodeByPage_thenReturnDataNodePage_CaseInsensitive() {
        // given
        final String ownerId = "demo-customer-1";
        IntStream.range(0,50).forEach(i -> {
            String id = UUID.randomUUID().toString();
            String name = "test_dos_node1-"+i;
            String description = UUID.randomUUID().toString();
            ServiceNode serviceNode = new ServiceNode();
            serviceNode.setOwnerId(ownerId);
            serviceNode.setId(id);
            serviceNode.setName(name);
            serviceNode.setDescription(description);
            Set<String> aliases = Stream.of("test1", "test2").collect(Collectors.toSet());
            serviceNode.setAliases(gson.toJson(aliases));
            Map<String, String> metadata = new HashMap<>();
            metadata.put("category", "cancer");
            metadata.put("kind", "kids");
            serviceNode.setMetaData(metadata);

            repository.save(serviceNode);

        });

        String ownerId2 = "demo-customer-2";
        IntStream.range(0,50).forEach(i -> {
            String id = UUID.randomUUID().toString();
            String name = "test_dos_node2-"+i;
            ServiceNode serviceNode = new ServiceNode();
            serviceNode.setOwnerId(ownerId2);
            serviceNode.setId(id);
            serviceNode.setName(name);
            Set<String> aliases = Stream.of("test1", "test2").collect(Collectors.toSet());
            serviceNode.setAliases(gson.toJson(aliases));
            Map<String, String> metadata = new HashMap<>();
            metadata.put("category", "cancer");
            metadata.put("kind", "kids");
            serviceNode.setMetaData(metadata);

            repository.save(serviceNode);

        });

        String ownerId3 = "demo-customer-3";
        IntStream.range(0,50).forEach(i -> {
            String id = UUID.randomUUID().toString();
            String name = "test_dos_node3-"+i;
            ServiceNode serviceNode = new ServiceNode();
            serviceNode.setOwnerId(ownerId3);
            serviceNode.setId(id);
            serviceNode.setName(name);
            Set<String> aliases = Stream.of("test1", "test2").collect(Collectors.toSet());
            serviceNode.setAliases(gson.toJson(aliases));
            Map<String, String> metadata = new HashMap<>();
            metadata.put("category", "cancer");
            metadata.put("kind", "kids");
            serviceNode.setMetaData(metadata);

            repository.save(serviceNode);

        });

        // when
        Page<ServiceNode> serviceNodesLowerCase =
                repository.findByOwnerIdAndNameIgnoreCaseContainingAndAliasesIgnoreCaseContainingAndDescriptionIgnoreCaseContaining(
                        ownerId,
                        "test",
                        "",
                        "",
                        new PageRequest(0,10));

        Page<ServiceNode> serviceNodesUpperCase =
                repository.findByOwnerIdAndNameIgnoreCaseContainingAndAliasesIgnoreCaseContainingAndDescriptionIgnoreCaseContaining(
                        ownerId,
                        "TEST",
                        "",
                        "",
                        new PageRequest(0,10));


        Assert.assertEquals(serviceNodesLowerCase.getTotalElements(), serviceNodesUpperCase.getTotalElements());

    }

    @Test
    public void testQuerySpec() {

        // given
        final String ownerId = "demo-customer-1";
        IntStream.range(0,50).forEach(i -> {
            String id = UUID.randomUUID().toString();
            String name = "test_dos_node1-"+i;
            String description = UUID.randomUUID().toString();
            ServiceNode serviceNode = new ServiceNode();
            serviceNode.setOwnerId(ownerId);
            serviceNode.setId(id);
            serviceNode.setName(name);
            serviceNode.setDescription(description);
            Set<String> aliases = Stream.of("test1", "test2").collect(Collectors.toSet());
            serviceNode.setAliases(gson.toJson(aliases));
            Map<String, String> metadata = new HashMap<>();
            metadata.put("category", "cancer");
            metadata.put("kind", "kids");
            serviceNode.setMetaData(metadata);

            repository.save(serviceNode);

        });

        IntStream.range(0,50).forEach(i -> {
            String id = UUID.randomUUID().toString();
            String name = "test_dos_node2-"+i;
            String description = UUID.randomUUID().toString();
            ServiceNode serviceNode = new ServiceNode();
            serviceNode.setOwnerId(ownerId);
            serviceNode.setId(id);
            serviceNode.setName(name);
            serviceNode.setDescription(description);
            Set<String> aliases = Stream.of("demo1", "demo2").collect(Collectors.toSet());
            serviceNode.setAliases(gson.toJson(aliases));
            Map<String, String> metadata = new HashMap<>();
            metadata.put("category", "cancer");
            metadata.put("kind", "kids");
            serviceNode.setMetaData(metadata);

            repository.save(serviceNode);

        });

        Map<String, String> meta = new HashMap<String, String>(){{
            put("category", "cancer");
        }};

        ServiceNodePage page = new ServiceNodePage(0, 51, null, "demo", null, null, null);
        Page<ServiceNode> bySpecDefault = repository.findAll(new QueryServiceNodesSpec(page), new PageRequest(0,51));
        Assert.assertEquals(50, bySpecDefault.getTotalElements());
        Assert.assertTrue(bySpecDefault.isFirst());
        Assert.assertTrue(bySpecDefault.isLast());
        Assert.assertFalse(bySpecDefault.hasNext());
        Assert.assertTrue(bySpecDefault.hasContent());
        Assert.assertFalse(bySpecDefault.hasPrevious());
        Assert.assertEquals(1, bySpecDefault.getTotalPages());
        Assert.assertEquals(51, bySpecDefault.getSize());
        Assert.assertEquals(50, bySpecDefault.getNumberOfElements());

        ServiceNodePage pageSpec = new ServiceNodePage(0, 51, null, "test", null, meta, null);
        Page<ServiceNode> bySpec = repository.findAll(new QueryServiceNodesSpec(pageSpec), new PageRequest(0,51));
        Assert.assertEquals(50, bySpec.getTotalElements());
        Assert.assertTrue(bySpec.isFirst());
        Assert.assertTrue(bySpec.isLast());
        Assert.assertFalse(bySpec.hasNext());
        Assert.assertTrue(bySpec.hasContent());
        Assert.assertFalse(bySpec.hasPrevious());
        Assert.assertEquals(1, bySpec.getTotalPages());
        Assert.assertEquals(51, bySpec.getSize());
        Assert.assertEquals(50, bySpec.getNumberOfElements());

        System.out.println(bySpec);
    }

    @Test
    public void testQuerySpecWithNodeIds() {

        // given
        final String ownerId = "demo-customer-1";
        IntStream.range(0,50).forEach(i -> {
            String id = "uuid-"+i;
            String name = "test_dos_node1-"+i;
            String description = UUID.randomUUID().toString();
            ServiceNode serviceNode = new ServiceNode();
            serviceNode.setOwnerId(ownerId);
            serviceNode.setId(id);
            serviceNode.setName(name);
            serviceNode.setDescription(description);
            Set<String> aliases = Stream.of("test1", "test2").collect(Collectors.toSet());
            serviceNode.setAliases(gson.toJson(aliases));
            Map<String, String> metadata = new HashMap<>();
            metadata.put("category", "cancer");
            metadata.put("kind", "kids");
            serviceNode.setMetaData(metadata);

            repository.save(serviceNode);

        });

        IntStream.range(0,50).forEach(i -> {
            String id = UUID.randomUUID().toString();
            String name = "test_dos_node2-"+i;
            String description = UUID.randomUUID().toString();
            ServiceNode serviceNode = new ServiceNode();
            serviceNode.setOwnerId(ownerId);
            serviceNode.setId(id);
            serviceNode.setName(name);
            serviceNode.setDescription(description);
            Set<String> aliases = Stream.of("demo1", "demo2").collect(Collectors.toSet());
            serviceNode.setAliases(gson.toJson(aliases));
            Map<String, String> metadata = new HashMap<>();
            metadata.put("category", "cancer");
            metadata.put("kind", "kids");
            serviceNode.setMetaData(metadata);

            repository.save(serviceNode);

        });

        List<String> ids = IntStream.range(0, 10)
                .mapToObj(i -> {
                    return "uuid-" + i;
                })
                .collect(Collectors.toList());

        ServiceNodePage page = new ServiceNodePage(0, 11, null, null, null, null, ids);
        Page<ServiceNode> bySpecWithIds = repository.findAll(new QueryServiceNodesSpec(page), new PageRequest(0,11));
        Assert.assertEquals(10, bySpecWithIds.getTotalElements());
        Assert.assertTrue(bySpecWithIds.isFirst());
        Assert.assertTrue(bySpecWithIds.isLast());
        Assert.assertFalse(bySpecWithIds.hasNext());
        Assert.assertTrue(bySpecWithIds.hasContent());
        Assert.assertFalse(bySpecWithIds.hasPrevious());
        Assert.assertEquals(1, bySpecWithIds.getTotalPages());
        Assert.assertEquals(11, bySpecWithIds.getSize());
        Assert.assertEquals(10, bySpecWithIds.getNumberOfElements());

        System.out.println(bySpecWithIds);
    }

    @Test
    public void testQuerySpec_expectNoRecords() {

        // given
        final String ownerId = "demo-customer-1";
        IntStream.range(0,50).forEach(i -> {
            String id = "uuid-"+i;
            String name = "test_dos_node1-"+i;
            String description = UUID.randomUUID().toString();
            ServiceNode serviceNode = new ServiceNode();
            serviceNode.setOwnerId(ownerId);
            serviceNode.setId(id);
            serviceNode.setName(name);
            serviceNode.setDescription(description);
            Set<String> aliases = Stream.of("test1", "test2").collect(Collectors.toSet());
            serviceNode.setAliases(gson.toJson(aliases));
            Map<String, String> metadata = new HashMap<>();
            metadata.put("category", "cancer");
            metadata.put("kind", "kids");
            serviceNode.setMetaData(metadata);

            repository.save(serviceNode);

        });

        ServiceNodePage page = new ServiceNodePage(0, 11, "NON_EXIST", null, null, null, null);
        Page<ServiceNode> bySpec = repository.findAll(new QueryServiceNodesSpec(page), new PageRequest(0,11));
        Assert.assertEquals(0, bySpec.getTotalElements());
        Assert.assertTrue(bySpec.isFirst());
        Assert.assertTrue(bySpec.isLast());
        Assert.assertFalse(bySpec.hasNext());
        Assert.assertFalse(bySpec.hasContent());
        Assert.assertFalse(bySpec.hasPrevious());
        Assert.assertEquals(0, bySpec.getTotalPages());
        Assert.assertEquals(0, bySpec.getContent().size());
        Assert.assertEquals(11, bySpec.getSize());
        Assert.assertEquals(0, bySpec.getNumberOfElements());

        System.out.println(bySpec);
    }
}