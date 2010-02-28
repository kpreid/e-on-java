// Copyright 2006 Hewlett Packard, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.debug;

import org.erights.e.develop.format.StringHelper;
import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.vat.StackContext;
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;

import java.io.IOException;

/*
 ? def makeTraceln := <unsafe:org.erights.e.elib.debug.makeTraceln>
 ? def tl := makeTraceln("foo.bar.makeZip")
 ? tl("foo\nbar")
 */

/**
 * Creates the objects bound to "traceln" in the safeScope.
 * <p/>
 * This class itself is authority bearing, and so must be declared unsafe.
 * However, its instances are also authority bearing, but by special
 * dispensation we allow them to appear in the safeScope.
 *
 * @author Mark S. Miller
 */
public class Traceln implements EPrintable {

    static private final int LIMIT = 76;

    private final String myHeader;
    private final Trace myTracer;

    /**
     * The header is for the purpose of secure labeling.
     * <p/>
     * Relied-upon code should provide a header like
     * <pre>"foo.bar.makeZip"</pre>
     * that says which module we hold responsible for the trace output. Using
     * the fully qualified name as the header also allows tracing controls to
     * be expressed in terms of which mosules to allow.
     */
    public Traceln(String header) {
        myHeader = header;
        myTracer = new Trace(header);
    }

    /**
     * Outputs into the trace log an attributed message using email quoting
     * conventions.
     */
    public void run(String message) {
        traceit(message, true);
    }

    /**
     * Outputs into the trace log an attributed message using email quoting
     * conventions.
     */
    public void full(String message) {
        traceit(message, false);
    }

    private void traceit(String message, boolean onlyOnePosFlag) {
        if (myTracer.warning) {
            StackContext sc =
              new StackContext("traceln", onlyOnePosFlag, true);

            // The following logic should behave like makeQuoteln.emaker

            StringBuffer buf = new StringBuffer(message.length() * 2);
            buf.append("\n> ");
            int lineStart = buf.length();
            for (int i = 0, len = message.length(); i < len; i++) {
                char c = message.charAt(i);
                if ('\n' == c) {
                    buf.append("\n> ");
                    lineStart = buf.length();
                } else if (LIMIT <= buf.length() - lineStart) {
                    buf.append("\\\n> ");
                    lineStart = buf.length();
                    StringHelper.escapedInto(c, buf);
                } else {
                    StringHelper.escapedInto(c, buf);
                }
            }
            buf.append('\n');
            myTracer.warningm(buf.toString(), sc);
        }
    }

    public void __printOn(TextWriter out) throws IOException {
        out.print("<tracing \"", myHeader, "\">");
    }
}
