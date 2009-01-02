// Copyright 2004-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.redirectory;

import org.waterken.url.amp.AMP;

import java.io.IOException;

/**
 * A simple client interface to a redirectory.
 *
 * @author Tyler
 */
public final class Redirectory {

    private Redirectory() {
    }

    // org.waterken.url.redirectory.Redirectory interface.

    /**
     * Allocates a redirectory slot.
     *
     * @param cap  The <code>&lt;<a href="http://yurl.org/Expert-define">http://yurl.org/Expert-define</a>&gt;</code>
     *             capability URL.
     * @param mid  The message identifier for this request.
     * @param name The slot name.
     * @return The <code>&lt;<a href="http://yurl.org/Author-assign">http://yurl.org/Author-assign</a>&gt;</code>
     *         capability URL.
     */
    static public String define(final String cap,
                                final String mid,
                                final String name) throws IOException {
        final String args =
          "<doc schema=\"http://web-calculus.org/string/String\">" + name +
            "</doc>\n";
        return AMP.invoke(cap, mid, args.getBytes("UTF-8"));
    }

    /**
     * Updates a redirectory slot.
     *
     * @param cap      The <code>&lt;<a href="http://yurl.org/Author-assign">http://yurl.org/Author-assign</a>&gt;</code>
     *                 capability URL.
     * @param mid      The message identifier for this request.
     * @param location The new redirection URLs.
     */
    static public void assign(final String cap,
                              final String mid,
                              final String[] location) throws IOException {
        String args;
        if (0 == location.length) {
            args = "<doc schema=\"http://web-calculus.org/Void\"/>\n";
        } else if (1 == location.length) {
            args = "<doc schema=\"http://web-calculus.org/pointer/Embed\">\n" +
              "\t<target>" + location[0] + "</target>\n" + "</doc>\n";
        } else {
            args =
              "<doc schema=\"http://web-calculus.org/pointer/Balance\">\n" +
                "\t<super schema=\"http://web-calculus.org/pointer/Embed\">\n" +
                "\t\t<target>" + location[0] + "</target>\n" + "\t</super>\n";
            for (int i = 0; i != location.length; ++i) {
                args += "\t<option>" + location[i] + "</option>\n";
            }
            args += "</doc>\n";
        }
        AMP.invoke(cap, mid, args.getBytes("UTF-8"));
    }
}
