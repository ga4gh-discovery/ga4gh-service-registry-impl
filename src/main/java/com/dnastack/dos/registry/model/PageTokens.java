package com.dnastack.dos.registry.model;

import com.dnastack.dos.registry.util.Base64JsonCodec;

/**
 * This models page_token to/from a encode String
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
public class PageTokens {

    private PageTokens() {
    }

    public static Page fromCursor(String cursor) {
        return (Page) Base64JsonCodec.decode(cursor, Page.class);
    }

    public static String toCursor(Page page) {
        return Base64JsonCodec.encode(page);
    }
}
