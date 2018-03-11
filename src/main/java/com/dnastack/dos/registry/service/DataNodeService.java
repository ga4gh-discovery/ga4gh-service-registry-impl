package com.dnastack.dos.registry.service;

import com.dnastack.dos.registry.controller.Ga4ghDataNodeCreationRequestDto;
import com.dnastack.dos.registry.controller.Ga4ghDataNodeDto;
import com.dnastack.dos.registry.controller.Ga4ghDataNodeUpdateRequestDto;
import com.dnastack.dos.registry.exception.BusinessValidationException;
import com.dnastack.dos.registry.exception.DataNodeNotFoundException;
import com.dnastack.dos.registry.model.Ga4ghDataNode;
import com.dnastack.dos.registry.repository.Ga4ghDataNodeRepository;
import com.dnastack.dos.registry.util.ConverterHelper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * This is the service to perform CRUD operations on downstream data layer (e.g., MySQL)
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
@Service
public class DataNodeService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Ga4ghDataNodeRepository repository;

    @Autowired
    public DataNodeService(Ga4ghDataNodeRepository repository) {
        this.repository = repository;
    }

    public List<Ga4ghDataNode> getNodes(String customerId, String name, String alias, String description, String pageToken, Integer pageSize) {

        Ga4ghDataNode node1 = new Ga4ghDataNode() {
            {
                setId("1");
                setName("SickKids");
                setCreated(DateTime.now());
                setUrl("http://sickkids.com/public/data");
            }
        };

        Ga4ghDataNode node2 = new Ga4ghDataNode() {{
            setId("2");
            setName("MountSinai");
            setCreated(DateTime.now());
            setUrl("http://mountsinai.com/public/data");
        }};

        return Arrays.asList(node1, node2);
    }

    public Ga4ghDataNode createNode(Ga4ghDataNodeCreationRequestDto creationRequestDto) {

        Ga4ghDataNode dataNode = new Ga4ghDataNode();

        String id = UUID.randomUUID().toString();
        dataNode.setId(id);

        //TODO: get customerId from security context holder
        String customerId = "demo-customer";
        dataNode.setCustomerId(customerId);

        //TODO: Ask Jim if we need to validate the uniqueness of node name

        ConverterHelper.convertFromDataNodeCreationRequestDto(dataNode, creationRequestDto);
        repository.save(dataNode);

        return dataNode;
    }

    public Ga4ghDataNode deleteNode(String nodeId) {

        validateDataNodeExistence(nodeId);

        repository.delete(nodeId);

        Ga4ghDataNode dataNode = new Ga4ghDataNode();
        dataNode.setId(nodeId);

        return dataNode;

    }

    public Ga4ghDataNode getNodeById(String nodeId) {

        validateDataNodeExistence(nodeId);

        return repository.findOne(nodeId);
    }

    public Ga4ghDataNode updateNode(String nodeId, Ga4ghDataNodeUpdateRequestDto updateRequestDto) {

        validateDataNodeExistence(nodeId);

        Ga4ghDataNode dataNode = repository.findOne(nodeId);
        if (StringUtils.isEmpty(updateRequestDto.getName())
                && StringUtils.isEmpty(updateRequestDto.getDescription())
                && (updateRequestDto.getMetaData() == null || updateRequestDto.getMetaData().size() <= 0)
                && (updateRequestDto.getAliases() == null || updateRequestDto.getAliases().size() <= 0)) {
            String message = "Nothing to update";
            //should return http 422
            throw new BusinessValidationException(message, null, null);
        }

        ConverterHelper.convertFromDataNodeUpdateRequestDto(dataNode, updateRequestDto);
        repository.save(dataNode);

        return dataNode;
    }

    private void validateDataNodeExistence(String nodeId) {
        boolean exists = repository.exists(nodeId);
        if (!exists) {
            String message = "Resource not found with nodeId=" + nodeId;
            throw new DataNodeNotFoundException(message, null, null);
        }
    }
}
