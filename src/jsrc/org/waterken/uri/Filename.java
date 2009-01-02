// Copyright 2004-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.uri;

import java.io.File;

/**
 * A filename domain.
 *
 * @author Tyler
 */
public final class Filename {

    private Filename() {
    }

    /**
     * Vouches for a filename.
     *
     * @param name The candidate name.
     * @return The vouched for name.
     */
    static public String vouch(final String name) throws InvalidFilename {

        // Check for disallowed characters.
        for (int i = name.length(); 0 != i--;) {
            if (-1 != "\\/:*?<>|\"".indexOf(name.charAt(i))) {
                throw new InvalidFilename();
            }
        }

        // Check for path separator char.
        if (-1 != name.indexOf(File.separatorChar)) {
            throw new InvalidFilename();
        }

        // Check for special names.
        if ("".equals(name) || ".".equals(name) || "..".equals(name)) {
            throw new InvalidFilename();
        }

        // Check for Windows special names.
        if ("nul".equals(name) || "con".equals(name)) {
            throw new InvalidFilename();
        }

        return name;
    }

    /**
     * Extracts the filename extension.
     *
     * @param otherwise The default value.
     * @param name      The filename.
     * @return The extension.
     */
    static public String ext(final String otherwise, final String name) {
        final int dot = name.lastIndexOf('.');
        return -1 != dot ? name.substring(dot + 1) : otherwise;
    }

    /**
     * Extracts the filename key.
     *
     * @param name The filename.
     * @return The filename, less any extension.
     */
    static public String key(final String name) {
        final int dot = name.lastIndexOf('.');
        return -1 != dot ? name.substring(0, dot) : name;
    }
}
