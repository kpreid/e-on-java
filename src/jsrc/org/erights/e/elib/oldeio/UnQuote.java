package org.erights.e.elib.oldeio;

import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.serial.DeepPassByCopy;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............


/**
 * Wraps a string, and prints/quotes as the string, rather than the quoted form
 * of the string.
 *
 * @author Mark S. Miller
 */
public class UnQuote implements DeepPassByCopy {

    static private final long serialVersionUID = -494178202048285051L;

    static public final StaticMaker UnQuoteMaker =
      StaticMaker.make(UnQuote.class);

    private final String myStr;

    /**
     *
     */
    public UnQuote(String str) {
        myStr = str;
    }

    /**
     *
     */
    public String toString() {
        return myStr;
    }

    /**
     * makeUnquote(myStr)
     */
    public Object[] getSpreadUncall() {
        Object[] result = {UnQuoteMaker, "run", myStr};
        return result;
    }
}
