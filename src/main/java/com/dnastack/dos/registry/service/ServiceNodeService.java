package com.dnastack.dos.registry.service;

import com.dnastack.dos.registry.controller.ServiceNodeCreationRequestDto;
import com.dnastack.dos.registry.controller.ServiceNodeUpdateRequestDto;
import com.dnastack.dos.registry.exception.BusinessValidationException;
import com.dnastack.dos.registry.exception.DataNodeNotFoundException;
import com.dnastack.dos.registry.exception.DataNodeOwnershipException;
import com.dnastack.dos.registry.model.ServiceNodePage;
import com.dnastack.dos.registry.model.ServiceNode;
import com.dnastack.dos.registry.repository.ServiceNodeRepository;
import com.dnastack.dos.registry.repository.QueryServiceNodesSpec;
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

import java.util.UUID;

/**
 * This is the service to perform CRUD operations on downstream data layer (e.g., MySQL)
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
@Service
public class ServiceNodeService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ServiceNodeRepository repository;

    @Autowired
    public ServiceNodeService(ServiceNodeRepository repository) {
        this.repository = repository;
    }

    public Page<ServiceNode> getNodes(ServiceNodePage serviceNodePage) {

        Assert.notNull(serviceNodePage, "serviceNodePage cannot be null");
        Assert.isTrue(serviceNodePage.getPageSize() > 0,
                String.format("Page Size can not be less than 1. Received page size: %d", new Object[]{serviceNodePage.getPageSize()}));
        Assert.isTrue(serviceNodePage.getPageNumber() >= 0,
                String.format("Page number can not be less than 0. Received page number: %d", new Object[]{serviceNodePage.getPageNumber()}));

        Pageable pageable = new PageRequest(serviceNodePage.getPageNumber(), serviceNodePage.getPageSize());

        return repository.findAll(new QueryServiceNodesSpec(serviceNodePage),
                pageable);

    }

    public ServiceNode createNode(String ownerId, ServiceNodeCreationRequestDto creationRequestDto) {

        ServiceNode serviceNode = new ServiceNode();

        String id = UUID.randomUUID().toString();
        serviceNode.setId(id);

        serviceNode.setOwnerId(ownerId);

        //TODO: Ask Jim if we need to validate the uniqueness of node name
        ConverterHelper.convertFromDataNodeCreationRequestDto(serviceNode, creationRequestDto);
        repository.save(serviceNode);

        return serviceNode;
    }

    public ServiceNode deleteNode(String nodeId) {

        String currentUserId = SecurityContextUtil.getUserId();
        ServiceNode serviceNode = validateServiceNode(nodeId, currentUserId);

        repository.delete(nodeId);

        return serviceNode;

    }

    public ServiceNode getNodeById(String nodeId) {

        ServiceNode serviceNode = repository.findOne(nodeId);
        if (serviceNode == null) {
            String message = "Resource not found with nodeId=" + nodeId;
            throw new DataNodeNotFoundException(message);
        }
        return serviceNode;
    }

    public ServiceNode updateNode(String nodeId, ServiceNodeUpdateRequestDto updateRequestDto) {

        String currentUserId = SecurityContextUtil.getUserId();

        validateServiceNode(nodeId, currentUserId);

        ServiceNode serviceNode = repository.findOne(nodeId);
        if (StringUtils.isEmpty(updateRequestDto.getName())
                && StringUtils.isEmpty(updateRequestDto.getDescription())
                && (updateRequestDto.getMetaData() == null || updateRequestDto.getMetaData().size() <= 0)
                && (updateRequestDto.getAliases() == null || updateRequestDto.getAliases().size() <= 0)) {
            String message = "Nothing to update";
            //should return http 422
            throw new BusinessValidationException(message);
        }

        ConverterHelper.convertFromDataNodeUpdateRequestDto(serviceNode, updateRequestDto);
        repository.save(serviceNode);

        return serviceNode;
    }

    private ServiceNode validateServiceNode(String nodeId, String currentUserId) {
        ServiceNode serviceNode = repository.findOne(nodeId);
        if (serviceNode == null) {
            String message = "Resource not found with nodeId=" + nodeId;
            throw new DataNodeNotFoundException(message);
        }

        if (!currentUserId.equals(serviceNode.getOwnerId())) {
            String message = "Operation can only be performed by its expected owner";
            throw new DataNodeOwnershipException(message);
        }

        return serviceNode;
    }
}
