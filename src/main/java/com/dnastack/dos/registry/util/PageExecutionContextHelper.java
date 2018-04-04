package com.dnastack.dos.registry.util;

import com.dnastack.dos.registry.exception.PageExecutionContextException;
import com.dnastack.dos.registry.exception.ServiceException;
import com.dnastack.dos.registry.execution.PageExecutionContext;
import com.dnastack.dos.registry.model.ServiceNodePage;
import com.dnastack.dos.registry.model.ServiceNode;
import com.dnastack.dos.registry.service.ServiceNodeService;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class PageExecutionContextHelper {

    public static PageExecutionContext formPageExecutionContext(ServiceNodeService serviceNodeService, ServiceNodePage serviceNodePage){

        Page<ServiceNode> currentNodePool = null;
        try {
            currentNodePool = serviceNodeService.getNodes(serviceNodePage);
        } catch (Exception e) {
            throw new ServiceException("Error during invoking serviceNodeService", e.getCause());
        }

        if(currentNodePool.hasContent()){

            //reset the page execution context
            //initialize the current node pool
            String currentNodePoolNextPageToken = currentNodePool.isLast() ? null : PageTokens.toDataNodePageCursor(serviceNodePage.next());
            List<String> currentNodePoolIds = currentNodePool.getContent().stream()
                    .map(ServiceNode::getId)
                    .collect(Collectors.toList());
            String currentNodeId = currentNodePool.getContent().stream()
                    .map(ServiceNode::getId)
                    .findFirst()
                    .orElseThrow(() -> new PageExecutionContextException("No data node is found!"));
            int currentNodeOffset = 0;
            String currentNodePageToken = "";

            PageExecutionContext pageExecutionContext
                    = new PageExecutionContext(currentNodePoolNextPageToken,
                    currentNodePoolIds,
                    currentNodeId,
                    currentNodeOffset,
                    currentNodePageToken,
                    0);

            return pageExecutionContext;

        }

        return null;
    }

}
