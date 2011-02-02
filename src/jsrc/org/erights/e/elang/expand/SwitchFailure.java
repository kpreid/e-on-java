package org.erights.e.elang.expand;

// Copyright 2006-2007 Kevin Reid under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import java.io.IOException;
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;

/**
 * Exception thrown by a switch expression when no patterns match.
 *
 * @author Kevin Reid
 */
public final class SwitchFailure extends RuntimeException implements EPrintable {
    
    private Object mySpecimen;
    private Throwable[] myFailures;
    
    public SwitchFailure(Object specimen, Throwable[] failures) {
        super();
        mySpecimen = specimen;
        myFailures = failures;
    }
    
    public Object getSpecimen() {
        return mySpecimen;
    }
    
    public Throwable[] getFailures() {
        return myFailures;
    }
    
    public String getMessage() {
        return E.toString(this);
    }

    public void __printOn(TextWriter out) throws IOException {
        out.print(       "problem: ");
        out = out.indent("         ");
        out.quote(mySpecimen);
        out.print(" did not match any option:");
        out = out.indent();
        for (int i = 0; i < myFailures.length; i++) {
            out.lnPrint(myFailures[i]);
        }
    }
}
