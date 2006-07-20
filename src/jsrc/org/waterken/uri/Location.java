// Copyright 2004-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.uri;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URLDecoder;

/**
 * URI location manipulation.
 *
 * @author Tyler
 */
public final class Location {

    private Location() {
    }

    /**
     * Extracts the <code>host</code>.
     *
     * @param location A URL location.
     * @return The <code>host</code>.
     */
    public static String host(final String location) {
        final int end_host = location.indexOf(':');
        try {
            return URLDecoder.decode(-1 == end_host ?
                                     location :
                                     location.substring(0, end_host),
                                     "UTF-8")
              .toLowerCase();
        } catch (final UnsupportedEncodingException e) {
            // Should never happen.
            throw new UndeclaredThrowableException(e);
        }
    }

    /**
     * Extracts the <code>port</code>.
     *
     * @param location A URL location.
     * @param standard The standard port number.
     * @return The <code>port</code>.
     */
    public static int port(final String location, final int standard) {
        final int end_host = location.indexOf(':');
        return -1 == end_host || location.length() == end_host + 1 ?
          standard :
          Integer.parseInt(location.substring(end_host + 1));
    }
}
