package com.dnastack.dos.registry.util;

import com.dnastack.dos.registry.model.Page;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This class servers as ...
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
public class PageTokensTest {

    @Test
    public void testEncode(){
        Page page = new Page(1);

        System.out.println(PageTokens.toCursor(page));
        System.out.println(PageTokens.toCursor(page.next()));

        Assert.assertEquals(PageTokens.toCursor(page), PageTokens.toCursor(page));
        Assert.assertNotEquals(PageTokens.toCursor(page), PageTokens.toCursor(page.next()));
    }

    @Test
    public void testDecode(){
        Page page = new Page(2);
        String toCursor = PageTokens.toCursor(page);
        System.out.println(toCursor);

        Page fromCursor = PageTokens.fromCursor(toCursor);

        System.out.println(fromCursor.getPageNumber());

        Assert.assertEquals(Integer.valueOf(2), fromCursor.getPageNumber());
    }

}