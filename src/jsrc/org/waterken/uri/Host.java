// Copyright 2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.uri;

import java.lang.reflect.UndeclaredThrowableException;
import java.net.URLDecoder;

/**
 * URI host manipulation.
 *
 * @author Tyler
 */
public final class Host {

    private Host() {
    }

    /**
     * Extracts the <code>reg-name</code>.
     *
     * @param host A URL host.
     * @return The <code>reg-name</code>.
     */
    public static String name(final String host) {
        try {
            return URLDecoder.decode(host, "US-ASCII").toLowerCase();
        } catch (final java.io.UnsupportedEncodingException e) {
            // Should never happen.
            // US-ASCII is a required charset.
            throw new UndeclaredThrowableException(e);
        }
    }

    /**
     * Is the <code>host</code> an IPv4 address literal?
     *
     * @param s A URL host.
     * @return The IPv4 address literal.
     */
    public static String ipv4(final String s) throws InvalidIPv4 {
        boolean r = true;
        for (int i = 0, q = 4; r && 0 != q--;) {
            int j = 0 == q ? s.length() : s.indexOf('.', i);
            switch (j - i) {
            case 3:
                switch (s.charAt(i)) {
                case'1':
                    r = '0' <= s.charAt(i + 1) && '9' >= s.charAt(i + 1) &&
                      '0' <= s.charAt(i + 2) && '9' >= s.charAt(i + 2);
                    break;
                case'2':
                    if ('5' == s.charAt(i + 1)) {
                        r = '0' <= s.charAt(i + 2) && '5' >= s.charAt(i + 2);
                    } else {
                        r = '0' <= s.charAt(i + 1) && '4' >= s.charAt(i + 1) &&
                          '0' <= s.charAt(i + 2) && '9' >= s.charAt(i + 2);
                    }
                    break;
                default:
                    r = false;
                }
                break;
            case 2:
                r = '1' <= s.charAt(i) && '9' >= s.charAt(i) &&
                  '0' <= s.charAt(i + 1) && '9' >= s.charAt(i + 1);
                break;
            case 1:
                r = '0' <= s.charAt(i) && '9' >= s.charAt(i);
                break;
            default:
                r = false;
            }
            i = j + 1;
        }
        if (!r) {
            throw InvalidIPv4.make();
        }
        return s;
    }
}
