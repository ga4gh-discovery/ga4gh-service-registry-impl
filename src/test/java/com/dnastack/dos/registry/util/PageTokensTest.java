package com.dnastack.dos.registry.util;

import com.dnastack.dos.registry.execution.PageExecutionContext;
import com.dnastack.dos.registry.model.DataNodePage;
import com.dnastack.dos.registry.model.DataObjectPage;
import com.dnastack.dos.registry.service.DataObjectService;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * This class servers as ...
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
public class PageTokensTest {

    @Test
    public void testEncode(){
        DataNodePage page = new DataNodePage(0, 10, null, null, null, null, null);

        System.out.println(PageTokens.toDataNodePageCursor(page));
        System.out.println(PageTokens.toDataNodePageCursor(page.next()));

        Assert.assertEquals(PageTokens.toDataNodePageCursor(page), PageTokens.toDataNodePageCursor(page));
        Assert.assertNotEquals(PageTokens.toDataNodePageCursor(page), PageTokens.toDataNodePageCursor(page.next()));
    }

    @Test
    public void testDecode(){
        DataNodePage page = new DataNodePage(1, 20, null, null, null, null, null);
        String toCursor = PageTokens.toDataNodePageCursor(page.next());
        System.out.println(toCursor);

        DataNodePage fromCursor = PageTokens.fromCursorToDataNodePage(toCursor);

        System.out.println(fromCursor.getPageNumber());

        Assert.assertEquals(2, fromCursor.getPageNumber());
    }

    @Test
    public void testEncodeDataObjectsPage(){


        List<String> currentNodePoolIds = Arrays.asList("1","2","3","4","5");
        String currentNodeId = "1";
        int currentNodeOffset = 0;
        String currentNodePageToken = "";
        DataNodePage dataNodePage = new DataNodePage(0, 10, "tes", "est", "demo", null, null);
        PageExecutionContext pageExecutionContext
                = new PageExecutionContext(PageTokens.toDataNodePageCursor(dataNodePage.next()),
                currentNodePoolIds,
                currentNodeId,
                currentNodeOffset,
                currentNodePageToken);

        List<String> dosIds = Arrays.asList("aa","bb","cc");
        String dosName = "test";
        String dosDescription = "demo";
        String dosAlias = "tst";
        DataObjectPage dataObjectPage = new DataObjectPage(0, 10, dosIds, dosName,
                null, null, dosDescription, dosAlias, null,
                null, null, null, null,
                pageExecutionContext);

        System.out.println(PageTokens.toDataObjectPageCursor(dataObjectPage));
        System.out.println(PageTokens.toDataObjectPageCursor(dataObjectPage.next()));

        Assert.assertEquals(PageTokens.toDataObjectPageCursor(dataObjectPage), PageTokens.toDataObjectPageCursor(dataObjectPage));
        Assert.assertNotEquals(PageTokens.toDataObjectPageCursor(dataObjectPage), PageTokens.toDataObjectPageCursor(dataObjectPage.next()));
    }

    @Test
    public void testDecodeDataObjectsPage(){


        int currentNodePoolNextPageNumber = 1;
        List<String> currentNodePoolIds = Arrays.asList("1","2","3","4","5");
        String currentNodeId = "1";
        int currentNodeOffset = 0;
        String currentNodePageToken = "";
        DataNodePage dataNodePage = new DataNodePage(0, 10, "tes", "est", "demo", null, null);
        PageExecutionContext pageExecutionContext
                = new PageExecutionContext(PageTokens.toDataNodePageCursor(dataNodePage.next()),
                currentNodePoolIds,
                currentNodeId,
                currentNodeOffset,
                currentNodePageToken);

        List<String> dosIds = Arrays.asList("aa","bb","cc");
        String dosName = "test";
        String dosDescription = "demo";
        String dosAlias = "tst";
        DataObjectPage dataObjectPage = new DataObjectPage(0, 10, dosIds, dosName,
                null, null, dosDescription, dosAlias, null,
                null, null, null, null,
                pageExecutionContext);
        String toDataObjectPageCursor = PageTokens.toDataObjectPageCursor(dataObjectPage.next());
        System.out.println(toDataObjectPageCursor);

        DataObjectPage fromCursorToDataObjectPage = PageTokens.fromCursorToDataObjectPage(toDataObjectPageCursor);

        Assert.assertEquals(1, fromCursorToDataObjectPage.getPageNumber());
        Assert.assertEquals(10, fromCursorToDataObjectPage.getPageSize());
        Assert.assertEquals(dosName, fromCursorToDataObjectPage.getDosName());

        Assert.assertNotNull(fromCursorToDataObjectPage.getPageExecutionContext());
        Assert.assertEquals(currentNodeOffset, fromCursorToDataObjectPage.getPageExecutionContext().getCurrentNodeOffset());
        Assert.assertEquals(currentNodeId, fromCursorToDataObjectPage.getPageExecutionContext().getCurrentNodeId());
        Assert.assertNotNull(fromCursorToDataObjectPage.getPageExecutionContext().getCurrentNodePoolNextPageToken());
        Assert.assertEquals(currentNodePoolIds, fromCursorToDataObjectPage.getPageExecutionContext().getCurrentNodePoolIds());
        Assert.assertEquals(currentNodePageToken, fromCursorToDataObjectPage.getPageExecutionContext().getCurrentNodePageToken());

    }


}