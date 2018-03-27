package com.dnastack.dos.registry.service;

import com.dnastack.dos.registry.exception.PageExecutionContextException;
import com.dnastack.dos.registry.exception.ServiceException;
import com.dnastack.dos.registry.execution.PageExecutionContext;
import com.dnastack.dos.registry.model.*;
import com.dnastack.dos.registry.repository.Ga4ghDataNodeRepository;
import com.dnastack.dos.registry.util.PageTokens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @Autowired
    private DataNodeService dataNodeService;

    public Page<Ga4ghDataObjectOnNode> getDataObjects(DataObjectPage dataObjectPage) {

        Assert.notNull(dataObjectPage, "dataObjectPage cannot be null");
        Assert.isTrue(dataObjectPage.getPageSize() > 0,
                String.format("Page Size can not be less than 1. Received page size: %d", new Object[]{dataObjectPage.getPageSize()}));
        Assert.isTrue(dataObjectPage.getPageNumber() >= 0,
                String.format("Page number can not be less than 0. Received page number: %d", new Object[]{dataObjectPage.getPageNumber()}));

        Assert.notNull(dataObjectPage.getPageExecutionContext(), "page execution context cannot be null!");

        List<Ga4ghDataObjectOnNode> dataObjectsForPage = new ArrayList<>();

        int remainingCountForPage = dataObjectPage.getPageSize();
        while(!dataObjectPage.getPageExecutionContext().getCurrentNodePoolIds().isEmpty()
                && remainingCountForPage > 0) {

            List<String> currentNodePoolIds = dataObjectPage.getPageExecutionContext().getCurrentNodePoolIds();
            Assert.notEmpty(currentNodePoolIds, "current node pool cannot be empty!");
            String currentNodeId = dataObjectPage.getPageExecutionContext().getCurrentNodeId();
            Assert.notNull(currentNodeId, "current node cannot be null!");
            Assert.isTrue(currentNodePoolIds.contains(currentNodeId), "current node cannot be a member of currentNodePoolIds!");

            Ga4ghDataNode dataNode = repository.findOne(currentNodeId);
            if (dataNode == null) {
                //in case the data node was deleted during the execution of this request
                //move on to the next node
                dataObjectPage.getPageExecutionContext().getCurrentNodePoolIds().remove(currentNodeId);
            } else {
                /*
                 * 1. Get the current DataNode and query for data objects:
                 */
                String url = dataNode.getUrl();
                String pageToken = dataObjectPage.getPageExecutionContext().getCurrentNodePageToken();
                //NOTE: please use remainingCountForPage as page size!
                //TODO: make the rest call to the actual data node url in a separate private method

                //TODO: use real data from the actual rest call
                List<Ga4ghDataObject> currentDataObjects = new ArrayList<>();
                String nextPageToken = "eyjWvae...";

                //TODO: add logic to handle empty list or error cases
                if(currentDataObjects.size()==0){
                    //move on to the next node
                    dataObjectPage.getPageExecutionContext().getCurrentNodePoolIds().remove(currentNodeId);
                }

                /*
                 * 2. Check if there're enough data objects to form the page
                 */
                if(currentDataObjects.size() == remainingCountForPage){
                    // just enough to form this current page
                    addRecordsToPage(dataObjectsForPage, currentDataObjects, remainingCountForPage, currentNodeId);
                    if(nextPageToken != null) {
                        dataObjectPage.getPageExecutionContext().setCurrentNodePageToken(nextPageToken);
                    } else {
                        //move on to the next node
                        dataObjectPage.getPageExecutionContext().getCurrentNodePoolIds().remove(currentNodeId);
                    }

                    remainingCountForPage = 0; // exit loop
                } else if (currentDataObjects.size() > remainingCountForPage){
                    addRecordsToPage(dataObjectsForPage, currentDataObjects, remainingCountForPage, currentNodeId);
                    //stays on this current node for next page
                    dataObjectPage.getPageExecutionContext().setCurrentNodeOffset(remainingCountForPage);

                    remainingCountForPage = 0; // exit loop
                } else {
                    //NOTE: this situation should NOT happen in the current implementation
                    addRecordsToPage(dataObjectsForPage, currentDataObjects, currentDataObjects.size(), currentNodeId);
                    if(nextPageToken != null) {
                        // it means this data node has more records to retrieve
                        dataObjectPage.getPageExecutionContext().setCurrentNodePageToken(nextPageToken);
                    } else {
                        //move on to the next node
                        dataObjectPage.getPageExecutionContext().getCurrentNodePoolIds().remove(currentNodeId);
                    }

                    remainingCountForPage = remainingCountForPage - currentDataObjects.size();
                }

            }

            /*
             * 3. reset the page execution context if the node pool is empty by reaching this point
             */

            if(dataObjectPage.getPageExecutionContext().getCurrentNodePoolIds().isEmpty()
                    && dataObjectPage.getPageExecutionContext().getCurrentNodePoolNextPageToken() != null){
                //get node node pool

                DataNodePage dataNodePage = PageTokens.fromCursorToDataNodePage(dataObjectPage.getPageExecutionContext().getCurrentNodePoolNextPageToken());
                Page<Ga4ghDataNode> currentNodePool = null;
                try {
                    currentNodePool = dataNodeService.getNodes(dataNodePage);
                } catch (Exception e) {
                    logger.error("Error during invoking dataNodeService", e);
                    throw new ServiceException("Error during invoking dataNodeService", e.getCause());
                }

                if(currentNodePool.hasContent()){

                    dataObjectPage.getPageExecutionContext();
                    //reset the page execution context
                    //initialize the current node pool
                    String currentNodePoolNextPageToken = currentNodePool.isLast() ? null : PageTokens.toDataNodePageCursor(dataNodePage.next());
                    currentNodePoolIds = currentNodePool.getContent().stream()
                            .map(Ga4ghDataNode::getId)
                            .collect(Collectors.toList());
                    currentNodeId = currentNodePool.getContent().stream()
                            .map(Ga4ghDataNode::getId)
                            .findFirst()
                            .orElseThrow(() -> new PageExecutionContextException("No data node is found!"));
                    int currentNodeOffset = 0;
                    String currentNodePageToken = "";

                    PageExecutionContext pageExecutionContext
                            = new PageExecutionContext(currentNodePoolNextPageToken,
                            currentNodePoolIds,
                            currentNodeId,
                            currentNodeOffset,
                            currentNodePageToken);

                    dataObjectPage.setPageExecutionContext(pageExecutionContext);

                }
            }
        }

        /*
         * 4. In the end, beautify the page object to indicate whether there're more pages or not.
         */
        if(!dataObjectPage.getPageExecutionContext().getCurrentNodePoolIds().isEmpty()){
            logger.info("has more pages...");
            //TODO: set the next page token
        }

        return null;

    }

    private void addRecordsToPage(List<Ga4ghDataObjectOnNode> dataObjectsForPage, List<Ga4ghDataObject> currentDataObjects, int offset, String currentNodeId){

        IntStream.range(0,offset).forEach(i -> {dataObjectsForPage.add(new Ga4ghDataObjectOnNode(currentNodeId, currentDataObjects.get(i)));});

    }

}
