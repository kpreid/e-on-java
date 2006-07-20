// Copyright 2004-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.uri;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URLDecoder;

/**
 * URI query manipulation.
 *
 * @author Tyler
 */
public final class Query {

    private Query() {
    }

    /**
     * Extracts a query argument.
     *
     * @param otherwise The default value.
     * @param query     The query string.
     * @param name      The parameter name.
     * @return The argument.
     */
    public static String arg(final String otherwise,
                             final String query,
                             final String name) {
        int start;
        if (query.startsWith(name + "=")) {
            start = name.length() + "=".length();
        } else {
            start = query.indexOf("&" + name + "=");
            if (-1 == start) {
                return otherwise;
            }
            start += "&".length() + name.length() + "=".length();
        }
        int end = query.indexOf('&', start);
        if (-1 == end) {
            end = query.length();
        }
        try {
            return URLDecoder.decode(query.substring(start, end), "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            // Should never happen.
            throw new UndeclaredThrowableException(e);
        }
    }
}
