// Copyright 2004 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.uri;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URLDecoder;

/**
 * URI authority manipulation.
 *
 * @author Tyler
 */
public final class Authority {

    private Authority() {
    }

    /**
     * Extracts the <code>key-id</code> from a YURL authority.
     *
     * @param authority A YURL authority.
     * @return The <code>key-id</code>.
     */
    static public String fingerprint(final String authority)
      throws InvalidAuthority {
        final int end_userinfo = authority.indexOf('@');
        String userinfo = -1 != end_userinfo ?
          authority.substring(0, end_userinfo) :
          authority;
        try {
            userinfo = URLDecoder.decode(userinfo, "US-ASCII");
        } catch (final UnsupportedEncodingException e) {
            // Should never happen.
            throw new UndeclaredThrowableException(e);
        }
        if (!userinfo.startsWith("*")) {
            throw InvalidAuthority.make();
        }
        return userinfo.substring(1).toLowerCase();
    }

    /**
     * Extracts the <code>host:port</code> from a URI authority.
     *
     * @param authority A YURL authority.
     * @return The <code>host:port</code>.
     */
    static public String location(String authority) {
        final int end_userinfo = authority.indexOf('@');
        return -1 == end_userinfo ?
          authority :
          authority.substring(end_userinfo + 1);
    }

    /**
     * Extracts the <code>hints</code> from a YURL authority.
     *
     * @param authority A YURL authority.
     * @return The <code>[ host:port ]</code>.
     */
    static public String[] hint(String authority) {
        final int end_userinfo = authority.indexOf('@');
        return -1 != end_userinfo ?
          list(',', authority, end_userinfo + 1) :
          new String[]{};
    }

    static private String[] list(final char separator, final String s, int i) {
        String[] r = new String[1];
        int n = 0;
        while (true) {
            if (n == r.length) {
                System.arraycopy(r, 0, r = new String[2 * n], 0, n);
            }
            final int j = s.indexOf(separator, i);
            if (-1 != j) {
                r[n++] = s.substring(i, j);
                i = j + 1;
            } else {
                r[n++] = s.substring(i);
                break;
            }
        }
        if (n != r.length) {
            System.arraycopy(r, 0, r = new String[n], 0, n);
        }
        return r;
    }
}
