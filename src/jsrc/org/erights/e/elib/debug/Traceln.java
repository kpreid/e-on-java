// Copyright 2006 Hewlett Packard, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.debug;

import org.erights.e.elib.vat.StackContext;
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;

import java.io.IOException;
import java.util.logging.Logger;

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
    private final String myHeader;
    private final Logger myLogger;

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
        myLogger = Logger.getLogger(header);
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
        // TODO: check that level >= warning before formatting?

        StackContext sc = new StackContext(myHeader, onlyOnePosFlag, true);

        myLogger.warning(message + "\n" + sc);
    }

    public void __printOn(TextWriter out) throws IOException {
        out.print("<tracing \"", myHeader, "\">");
    }
}
