package com.dnastack.dos.registry.service;

import com.dnastack.dos.registry.downstream.dto.ListDataObjectsResponseDto;
import com.dnastack.dos.registry.downstream.passthru.PassThruDataClient;
import com.dnastack.dos.registry.execution.PageExecutionContext;
import com.dnastack.dos.registry.model.*;
import com.dnastack.dos.registry.repository.Ga4ghDataNodeRepository;
import com.dnastack.dos.registry.util.PageExecutionContextHelper;
import com.dnastack.dos.registry.util.PageTokens;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This is the service to perform GET operations on actual DOS service node
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

    @Autowired
    private PassThruDataClient passThruDataClient;

    //Tihs is the model to dto converter
    @Autowired
    private ModelMapper modelMapper;

    public List<Ga4ghDataObjectOnNode> getDataObjects(DataObjectPage dataObjectPage) {

        Assert.notNull(dataObjectPage, "dataObjectPage cannot be null");
        Assert.isTrue(dataObjectPage.getPageSize() > 0,
                String.format("Page Size can not be less than 1. Received page size: %d", new Object[]{dataObjectPage.getPageSize()}));
        Assert.isTrue(dataObjectPage.getPageNumber() >= 0,
                String.format("Page number can not be less than 0. Received page number: %d", new Object[]{dataObjectPage.getPageNumber()}));

        Assert.notNull(dataObjectPage.getPageExecutionContext(), "page execution context cannot be null!");

        List<Ga4ghDataObjectOnNode> dataObjectsForPage = new ArrayList<>();

        int remainingCountForPage = dataObjectPage.getPageSize();
        while(dataObjectPage.getPageExecutionContext() != null
                && !CollectionUtils.isEmpty(dataObjectPage.getPageExecutionContext().getCurrentNodePoolIds())
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
                String dataObjectsNodeUrl = dataNode.getUrl();
                //NOTE: please use remainingCountForPage as page size!
                dataObjectPage.getPageExecutionContext().setRemainingCountForPage(remainingCountForPage);
                passThruDataClient.setDataObjectsNodeURL(dataObjectsNodeUrl);
                passThruDataClient.setDataObjectPage(dataObjectPage);
                ListDataObjectsResponseDto dataObjectsResponseDto = passThruDataClient.getDataObjects();

                List<Ga4ghDataObject> currentDataObjects =
                        dataObjectsResponseDto.getDataObjects().stream()
                            .map(data -> modelMapper.map(data, Ga4ghDataObject.class))
                            .collect(Collectors.toList());
                String nextPageToken = dataObjectsResponseDto.getNextPageToken();

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
                    //NOTE: this situation should NOT happen in the current implementation
                    addRecordsToPage(dataObjectsForPage, currentDataObjects, remainingCountForPage, currentNodeId);
                    //stays on this current node for next page
                    dataObjectPage.getPageExecutionContext().setCurrentNodeOffset(remainingCountForPage);

                    remainingCountForPage = 0; // exit loop
                } else {
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
                PageExecutionContext pageExecutionContext = PageExecutionContextHelper.formPageExecutionContext(dataNodeService, dataNodePage);

                dataObjectPage.setPageExecutionContext(pageExecutionContext);
            }
        }

        return dataObjectsForPage;

    }

    private void addRecordsToPage(List<Ga4ghDataObjectOnNode> dataObjectsForPage, List<Ga4ghDataObject> currentDataObjects, int offset, String currentNodeId){

        IntStream.range(0,offset).forEach(i -> {dataObjectsForPage.add(new Ga4ghDataObjectOnNode(currentNodeId, currentDataObjects.get(i)));});

    }

}
