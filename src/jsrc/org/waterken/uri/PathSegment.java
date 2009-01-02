// Copyright 2002-2004 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.uri;

/**
 * URI path segment manipulation.
 *
 * @author Tyler
 */
public final class PathSegment {

    private PathSegment() {
    }

    /**
     * Vouches for a path segment.
     *
     * @param candidate The path segment.
     * @return The vouched for path segment.
     */
    static public String vouch(final String candidate) throws InvalidPath {

        // Check for disallowed characters.
        for (int i = candidate.length(); 0 != i--;) {
            final char c = candidate.charAt(i);
            if (0x20 >= c || 0x7F <= c || -1 != "\"#/<>?[\\]^`{|}".indexOf(c)) {
                throw InvalidPath.make();
            }
        }

        // Check for special segments.
        if ("".equals(candidate) || ".".equals(candidate) ||
          "..".equals(candidate)) {
            throw InvalidPath.make();
        }

        return candidate;
    }
}
