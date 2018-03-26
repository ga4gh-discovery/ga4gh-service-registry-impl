package com.dnastack.dos.registry.util;

import com.dnastack.dos.registry.model.DataNodePage;
import com.dnastack.dos.registry.model.DataObjectPage;

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

    public static DataObjectPage fromCursorToDataObjectPage(String cursor) {
        return (DataObjectPage) Base64JsonCodec.decode(cursor, DataObjectPage.class);
    }

    public static String toDataObjectPageCursor(DataObjectPage page) {
        return Base64JsonCodec.encode(page);
    }
}
