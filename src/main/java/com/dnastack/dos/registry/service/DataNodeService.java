package com.dnastack.dos.registry.service;

import com.dnastack.dos.registry.controller.Ga4ghDataNodeCreationRequestDto;
import com.dnastack.dos.registry.controller.Ga4ghDataNodeUpdateRequestDto;
import com.dnastack.dos.registry.exception.BusinessValidationException;
import com.dnastack.dos.registry.exception.DataNodeNotFoundException;
import com.dnastack.dos.registry.model.Ga4ghDataNode;
import com.dnastack.dos.registry.repository.Ga4ghDataNodeRepository;
import com.dnastack.dos.registry.repository.QueryDataNodesSpec;
import com.dnastack.dos.registry.util.ConverterHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
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
    HttpServletRequest httpReq;

    @Autowired
    public DataNodeService(Ga4ghDataNodeRepository repository) {
        this.repository = repository;
    }

    //TODO: remove customerId from the method signature
    public Page<Ga4ghDataNode> getNodes(String customerId, String name, String alias, String description, Pageable pageable) {

        logger.debug("User principle: " + httpReq.getUserPrincipal());

        Assert.notNull(pageable, "Pageable cannot be null");
        Assert.isTrue(pageable.getPageSize() > 0,
                String.format("Page Size can not be less than 1. Received page size: %d", new Object[]{pageable.getPageSize()}));
        Assert.isTrue(pageable.getPageNumber() > 0,
                String.format("Page number can not be less than 1. Received page number: %d", new Object[]{pageable.getPageNumber()}));
        Assert.notNull(customerId, "CustomerId cannot be null");
        Assert.notNull(name, "Name cannot be null");
        Assert.notNull(alias, "Alias cannot be null");
        Assert.notNull(description, "Description cannot be null");

        //TODO: get meta into picture after api.yaml change
        return repository.findAll(new QueryDataNodesSpec(name,alias,description,null), pageable);

    }

    public Ga4ghDataNode createNode(String customerId, Ga4ghDataNodeCreationRequestDto creationRequestDto) {

        Ga4ghDataNode dataNode = new Ga4ghDataNode();

        String id = UUID.randomUUID().toString();
        dataNode.setId(id);

        dataNode.setOwnerId(customerId);

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
