package com.dnastack.dos.registry.util;

import com.dnastack.dos.registry.exception.PageExecutionContextException;
import com.dnastack.dos.registry.exception.ServiceException;
import com.dnastack.dos.registry.execution.PageExecutionContext;
import com.dnastack.dos.registry.model.DataNodePage;
import com.dnastack.dos.registry.model.Ga4ghDataNode;
import com.dnastack.dos.registry.service.DataNodeService;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class PageExecutionContextHelper {

    public static PageExecutionContext formPageExecutionContext(DataNodeService dataNodeService, DataNodePage dataNodePage){

        Page<Ga4ghDataNode> currentNodePool = null;
        try {
            currentNodePool = dataNodeService.getNodes(dataNodePage);
        } catch (Exception e) {
            throw new ServiceException("Error during invoking dataNodeService", e.getCause());
        }

        if(currentNodePool.hasContent()){

            //reset the page execution context
            //initialize the current node pool
            String currentNodePoolNextPageToken = currentNodePool.isLast() ? null : PageTokens.toDataNodePageCursor(dataNodePage.next());
            List<String> currentNodePoolIds = currentNodePool.getContent().stream()
                    .map(Ga4ghDataNode::getId)
                    .collect(Collectors.toList());
            String currentNodeId = currentNodePool.getContent().stream()
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

            return pageExecutionContext;

        }

        return null;
    }

}
