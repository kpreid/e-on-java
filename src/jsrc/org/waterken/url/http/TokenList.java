// Copyright 2002-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.http;

/**
 * Parses a <code>token</code> list.
 *
 * @author Tyler
 */
public final class TokenList {

    private TokenList() {
    }

    /**
     * Encodes a list of <code>token</code>.
     *
     * @param token The <code>token</code> array.
     * @return The encoded <code>token</code> list.
     */
    static public String encode(final String[] token) {
        final StringBuffer buffer = new StringBuffer();
        for (int i = 0; i != token.length; ++i) {
            if (0 != i) {
                buffer.append(", ");
            }
            buffer.append(token[i]);
        }
        return buffer.toString();
    }

    /**
     * Decodes a <code>token</code> list.
     *
     * @param list The <code>token</code> list.
     * @return The <code>token</code> array.
     */
    static public String[] decode(final String list) {
        String[] r = new String[1];
        int n = 0;

        final int len = list.length();
        for (int i = 0; true; ++i) {
            // Eat whitespace.
            while (i != len && -1 != " \t\r\n".indexOf(list.charAt(i))) {
                ++i;
            }

            // Empty list permitted.
            if (i == len) {
                break;
            }

            // Null elements permitted.
            if (',' == list.charAt(i)) {
                continue;
            }

            // Parse the token.
            int start_token = i;
            while (i != len && -1 == " ,;\t\r\n".indexOf(list.charAt(i))) {
                ++i;
            }
            final String token = list.substring(start_token, i);

            // Drop the parameters.
            while (true) {
                // Eat whitespace.
                while (i != len && -1 != " \t\r\n".indexOf(list.charAt(i))) {
                    ++i;
                }

                // Check for token delimiter.
                if (i == len || ',' == list.charAt(i)) {
                    break;
                }

                // Start parameter.
                if (';' != list.charAt(i)) {
                    throw new InvalidTokenListFormat("(" + i + "): " + list);
                }
                ++i;

                // Eat whitespace.
                while (-1 != " \t\r\n".indexOf(list.charAt(i))) {
                    ++i;
                }

                // Parse the name.
                final int start_name = i;
                while (-1 == "= \t\r\n".indexOf(list.charAt(i))) {
                    ++i;
                }
                final String name = list.substring(start_name, i);

                // Start the value.
                if ('=' != list.charAt(i)) {
                    throw new InvalidTokenListFormat("(" + i + "): " + list);
                }
                ++i;

                // Parse the value.
                String value;
                if ('\"' == list.charAt(i)) {
                    // quoted-string.
                    value = "";
                    int start_value = ++i;
                    while (true) {
                        final char c = list.charAt(i);
                        if ('\"' == c) {
                            value += list.substring(start_value, i);
                            ++i;
                            break;
                        } else if ('\\' == c) {
                            value += list.substring(start_value, i);
                            start_value = ++i;
                        } else {
                            ++i;
                        }
                    }
                } else {
                    // token.
                    final int start_value = i;
                    while (i != len && -1 == " ,\t\r\n".indexOf(list.charAt(i))) {
                        ++i;
                    }
                    value = list.substring(start_value, i);
                }
            }

            // Save the token.
            if (n == r.length) {
                System.arraycopy(r, 0, r = new String[2 * n], 0, n);
            }
            r[n++] = token;

            // Check for end of list.
            if (i == len) {
                break;
            }
        }

        // Fit the array to the list length.
        if (n != r.length) {
            System.arraycopy(r, 0, r = new String[n], 0, n);
        }

        return r;
    }
}
