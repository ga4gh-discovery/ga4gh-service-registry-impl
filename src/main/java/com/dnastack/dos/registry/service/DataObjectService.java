package com.dnastack.dos.registry.service;

import com.dnastack.dos.registry.model.DataObjectPage;
import com.dnastack.dos.registry.model.Ga4ghDataObjectOnNode;
import com.dnastack.dos.registry.repository.Ga4ghDataNodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private Ga4ghDataNodeRepository repository;

    public Page<Ga4ghDataObjectOnNode> getDataObjects(DataObjectPage dataObjectPage) {

        //TODO: more validation here
        Assert.notNull(dataObjectPage, "dataObjectPage cannot be null");
        Assert.isTrue(dataObjectPage.getPageSize() > 0,
                String.format("Page Size can not be less than 1. Received page size: %d", new Object[]{dataObjectPage.getPageSize()}));
        Assert.isTrue(dataObjectPage.getPageNumber() >= 0,
                String.format("Page number can not be less than 0. Received page number: %d", new Object[]{dataObjectPage.getPageNumber()}));


        /*
         * 1. Get the current DataNode and query for data objects: ->
         *     (1) update the current node page token
         */


        /*
         * 2. Check if there're enough data objects to form the page
         */

        /*
         * 3.1 If no, Loop thru the remaining DataNode in the pool and query for data objects: ->
         *     (0) Add the data objects to the Page
         *     (1) Remove the current DataNode from the pool
         *     (2) Update current node offset to 0
         *     (3) Get the next DataNode
         *     (4) Update the current DataNode Id
         *     (5) Update the current DataNode page token
         *     (6) Query for data objects with the current DataNode
         *     ...
         * 3.2 If yes: ->
         *     (0) Add the appropriate number of data objects to the Page
         *     (1) Update current node offset to whatever offset it means to be
         */

        /*
         * 4. In the end, beautify the page object to indicate whether there're more pages or not.
         */

        return null;

    }

}
