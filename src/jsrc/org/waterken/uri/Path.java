// Copyright 2002-2004 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.uri;

import java.lang.reflect.UndeclaredThrowableException;
import java.net.URLDecoder;

/**
 * URI path manipulation.
 *
 * @author Tyler
 */
public final class Path {

    private Path() {
    }

    /**
     * Canonicalizes a path.
     *
     * @param path The candidate path.
     * @return The canonicalized path.
     */
    public static String vouch(String path) throws InvalidPath {

        // Check for disallowed characters.
        for (int i = path.length(); i-- != 0;) {
            final char c = path.charAt(i);
            if (c <= 0x20 || c >= 0x7F || "\"#<>?[\\]^`{|}".indexOf(c) != -1) {
                throw InvalidPath.make();
            }
        }

        // Remove any "./" segments.
        while (path.startsWith("./")) {
            path = path.substring("./".length());
        }
        while (true) {
            final int start_rel = path.indexOf("/./");
            if (start_rel == -1) {
                break;
            }
            path = path.substring(0, start_rel) +
              path.substring(start_rel + "/.".length());
        }
        if (path.endsWith("/.")) {
            path = path.substring(0, path.length() - ".".length());
        } else if (path.equals(".")) {
            path = "";
        }

        // Unwind any ".." segments.
        while (true) {
            if (path.equals("..") || path.startsWith("../")) {
                throw InvalidPath.make();
            }
            final int start_rel = path.indexOf("/../");
            if (start_rel == -1) {
                break;
            }
            path = path.substring(0,
                                  path.lastIndexOf('/', start_rel - 1) + 1) +
              path.substring(start_rel + "/../".length());
        }

        // Unwind a trailing "..".
        if (path.endsWith("/..")) {
            path = path.substring(0, path.lastIndexOf('/', path.length() -
                                                           "x/..".length()) + 1);
        }

        // Make sure it's not an absolute path.
        if (path.startsWith("/")) {
            throw InvalidPath.make();
        }

        return path;
    }

    /**
     * Gets the resource folder.
     *
     * @param path The canonicalized path.
     * @return The path, less the last segment.
     */
    public static String folder(final String path) {
        return path.substring(0, path.lastIndexOf('/') + 1);
    }

    /**
     * Extracts the decoded resource name.
     *
     * @param path The canonicalized path.
     * @return The last path segment.
     */
    public static String name(final String path) {
        try {
            return URLDecoder.decode(
              path.substring(path.lastIndexOf('/') + 1), "US-ASCII");
        } catch (final java.io.UnsupportedEncodingException e) {
            // Should never happen.
            // US-ASCII is a required charset.
            throw new UndeclaredThrowableException(e);
        }
    }
}
