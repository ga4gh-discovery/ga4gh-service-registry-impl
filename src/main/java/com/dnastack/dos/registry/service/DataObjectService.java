package com.dnastack.dos.registry.service;

import com.dnastack.dos.registry.model.DataObjectPage;
import com.dnastack.dos.registry.model.Ga4ghDataObjectOnNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * This is the service to perform CRUD operations on DOS service node
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
@Service
public class DataObjectService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Page<Ga4ghDataObjectOnNode> getDataObjects(DataObjectPage dataObjectPage) {

        Assert.notNull(dataObjectPage, "dataObjectPage cannot be null");
        Assert.isTrue(dataObjectPage.getPageSize() > 0,
                String.format("Page Size can not be less than 1. Received page size: %d", new Object[]{dataObjectPage.getPageSize()}));
        Assert.isTrue(dataObjectPage.getPageNumber() >= 0,
                String.format("Page number can not be less than 0. Received page number: %d", new Object[]{dataObjectPage.getPageNumber()}));

        return null;

    }

}
