package com.dnastack.dos.registry.util;

import com.dnastack.dos.registry.model.DataNodePage;
import org.junit.Assert;
import org.junit.Test;

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

}