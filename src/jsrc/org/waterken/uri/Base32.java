// Copyright 2002-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.uri;

/**
 * Base32 encoding.
 *
 * @author Tyler
 */
public final class Base32 {

    private Base32() {
    }

    // org.waterken.uri.Base32 interface.

    /**
     * Creates the base32 encoding.
     *
     * @param b The binary data.
     * @return The base32 encoding.
     */
    public static String encode(byte[] b) {
        final StringBuffer r = new StringBuffer(b.length * 8 / 5 + 1);
        long buffer = 0;
        for (int i = 0; i != b.length;) {
            buffer <<= 8;
            buffer |= b[i] & 0x000000FF;
            if (0 == ++i % 5) {
                r.append(_encode(((int)(buffer >>> 35)) & 0x1F));
                r.append(_encode(((int)(buffer >>> 30)) & 0x1F));
                r.append(_encode(((int)(buffer >>> 25)) & 0x1F));
                r.append(_encode(((int)(buffer >>> 20)) & 0x1F));
                r.append(_encode(((int)(buffer >>> 15)) & 0x1F));
                r.append(_encode(((int)(buffer >>> 10)) & 0x1F));
                r.append(_encode(((int)(buffer >>> 5)) & 0x1F));
                r.append(_encode(((int)buffer) & 0x1F));
                buffer = 0;
            }
        }
        switch (b.length % 5) {
        case 0:
            break;
        case 1:
            buffer <<= 2;
            r.append(_encode(((int)(buffer >>> 5)) & 0x1F));
            r.append(_encode(((int)buffer) & 0x1F));
            break;
        case 2:
            buffer <<= 4;
            r.append(_encode(((int)(buffer >>> 15)) & 0x1F));
            r.append(_encode(((int)(buffer >>> 10)) & 0x1F));
            r.append(_encode(((int)(buffer >>> 5)) & 0x1F));
            r.append(_encode(((int)buffer) & 0x1F));
            break;
        case 3:
            buffer <<= 1;
            r.append(_encode(((int)(buffer >>> 20)) & 0x1F));
            r.append(_encode(((int)(buffer >>> 15)) & 0x1F));
            r.append(_encode(((int)(buffer >>> 10)) & 0x1F));
            r.append(_encode(((int)(buffer >>> 5)) & 0x1F));
            r.append(_encode(((int)buffer) & 0x1F));
            break;
        case 4:
            buffer <<= 3;
            r.append(_encode(((int)(buffer >>> 30)) & 0x1F));
            r.append(_encode(((int)(buffer >>> 25)) & 0x1F));
            r.append(_encode(((int)(buffer >>> 20)) & 0x1F));
            r.append(_encode(((int)(buffer >>> 15)) & 0x1F));
            r.append(_encode(((int)(buffer >>> 10)) & 0x1F));
            r.append(_encode(((int)(buffer >>> 5)) & 0x1F));
            r.append(_encode(((int)buffer) & 0x1F));
            break;
        }
        return r.toString();
    }

    private static char _encode(final int v) {
        return (char)(26 > v ? v + 'a' : v - 26 + '2');
    }
}
