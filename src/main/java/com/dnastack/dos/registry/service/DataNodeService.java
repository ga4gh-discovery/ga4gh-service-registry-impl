package com.dnastack.dos.registry.service;

import com.dnastack.dos.registry.controller.Ga4ghDataNodeCreationRequestDto;
import com.dnastack.dos.registry.controller.Ga4ghDataNodeUpdateRequestDto;
import com.dnastack.dos.registry.exception.BusinessValidationException;
import com.dnastack.dos.registry.exception.DataNodeNotFoundException;
import com.dnastack.dos.registry.exception.DataNodeOwnershipException;
import com.dnastack.dos.registry.model.DataNodePage;
import com.dnastack.dos.registry.model.Ga4ghDataNode;
import com.dnastack.dos.registry.repository.Ga4ghDataNodeRepository;
import com.dnastack.dos.registry.repository.QueryDataNodesSpec;
import com.dnastack.dos.registry.util.ConverterHelper;
import com.dnastack.dos.registry.util.SecurityContextUtil;
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

    public Page<Ga4ghDataNode> getNodes(DataNodePage dataNodePage) {

        Assert.notNull(dataNodePage, "dataNodePage cannot be null");
        Assert.isTrue(dataNodePage.getPageSize() > 0,
                String.format("Page Size can not be less than 1. Received page size: %d", new Object[]{dataNodePage.getPageSize()}));
        Assert.isTrue(dataNodePage.getPageNumber() >= 0,
                String.format("Page number can not be less than 0. Received page number: %d", new Object[]{dataNodePage.getPageNumber()}));

        Pageable pageable = new PageRequest(dataNodePage.getPageNumber(), dataNodePage.getPageSize());

        return repository.findAll(new QueryDataNodesSpec(dataNodePage),
                pageable);

    }

    public Ga4ghDataNode createNode(String ownerId, Ga4ghDataNodeCreationRequestDto creationRequestDto) {

        Ga4ghDataNode dataNode = new Ga4ghDataNode();

        String id = UUID.randomUUID().toString();
        dataNode.setId(id);

        dataNode.setOwnerId(ownerId);

        //TODO: Ask Jim if we need to validate the uniqueness of node name
        ConverterHelper.convertFromDataNodeCreationRequestDto(dataNode, creationRequestDto);
        repository.save(dataNode);

        return dataNode;
    }

    public Ga4ghDataNode deleteNode(String nodeId) {

        String currentUserId = SecurityContextUtil.getUserId();
        validateDataNode(nodeId, currentUserId);

        repository.delete(nodeId);

        Ga4ghDataNode dataNode = new Ga4ghDataNode();
        dataNode.setId(nodeId);

        return dataNode;

    }

    public Ga4ghDataNode getNodeById(String nodeId) {

        Ga4ghDataNode dataNode = repository.findOne(nodeId);
        if (dataNode == null) {
            String message = "Resource not found with nodeId=" + nodeId;
            throw new DataNodeNotFoundException(message);
        }
        return dataNode;
    }

    public Ga4ghDataNode updateNode(String nodeId, Ga4ghDataNodeUpdateRequestDto updateRequestDto) {

        String currentUserId = SecurityContextUtil.getUserId();

        validateDataNode(nodeId, currentUserId);

        Ga4ghDataNode dataNode = repository.findOne(nodeId);
        if (StringUtils.isEmpty(updateRequestDto.getName())
                && StringUtils.isEmpty(updateRequestDto.getDescription())
                && (updateRequestDto.getMetaData() == null || updateRequestDto.getMetaData().size() <= 0)
                && (updateRequestDto.getAliases() == null || updateRequestDto.getAliases().size() <= 0)) {
            String message = "Nothing to update";
            //should return http 422
            throw new BusinessValidationException(message);
        }

        ConverterHelper.convertFromDataNodeUpdateRequestDto(dataNode, updateRequestDto);
        repository.save(dataNode);

        return dataNode;
    }

    private void validateDataNode(String nodeId, String currentUserId) {
        Ga4ghDataNode dataNode = repository.findOne(nodeId);
        if (dataNode == null) {
            String message = "Resource not found with nodeId=" + nodeId;
            throw new DataNodeNotFoundException(message);
        }

        if (!currentUserId.equals(dataNode.getOwnerId())) {
            String message = "Operation can only be performed by its expected owner";
            throw new DataNodeOwnershipException(message);
        }
    }
}
