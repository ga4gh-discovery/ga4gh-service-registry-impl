package com.dnastack.dos.registry.util;

import com.dnastack.dos.registry.model.DataNodePage;

/**
 * This models page_token to/from a encode String
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
public class PageTokens {

    private PageTokens() {
    }

    public static DataNodePage fromCursorToDataNodePage(String cursor) {
        return (DataNodePage) Base64JsonCodec.decode(cursor, DataNodePage.class);
    }

    public static String toDataNodePageCursor(DataNodePage page) {
        return Base64JsonCodec.encode(page);
    }
}
