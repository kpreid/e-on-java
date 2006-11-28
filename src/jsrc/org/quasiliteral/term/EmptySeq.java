// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.quasiliteral.term;

import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.develop.assertion.T;
import org.quasiliteral.astro.AstroArg;

import java.io.IOException;

/**
 * Represents an empty sequence of Terms.
 * <p/>
 * Should only be needed as an intermediate result during parsing.
 *
 * @author Mark S. Miller
 */
public final class EmptySeq extends Termish {

    /**
     *
     */
    static public final EmptySeq THE_ONE = new EmptySeq();

    /**
     *
     */
    private EmptySeq() {
    }

    public AstroArg withOptSpan(SourceSpan optSpan) {
        T.fail("XXX not yet implemented");
        return THE_ONE; //make compiler happy
    }

    /**
     * @return An empty list
     */
    ConstList getTerms() {
        return ConstList.EmptyList;
    }

    /**
     * Do nothing
     */
    void getTerms(FlexList list) {
    }

    /**
     * @return 0
     */
    int getNumTerms() {
        return 0; //make compiler happy
    }

    /**
     * @return null
     */
    public SourceSpan getOptSpan() {
        return null;
    }

    /**
     * Prints as "()"
     */
    public void prettyPrintOn(TextWriter out, boolean quasiFlag)
      throws IOException {
        out.print("()");
    }
}
