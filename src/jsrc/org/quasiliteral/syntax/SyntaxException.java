package org.quasiliteral.syntax;

/*
The contents of this file are subject to the Electric Communities E Open
Source Code License Version 1.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License
at http://www.communities.com/EL/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
the specific language governing rights and limitations under the License.

The Original Code is the Distributed E Language Implementation, released
July 20, 1998.

The Initial Developer of the Original Code is Electric Communities.
Copyright (C) 1998 Electric Communities. All Rights Reserved.

Contributor(s): ______________________________________.
*/

import org.erights.e.develop.format.StringHelper;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.tables.Twine;

import java.io.IOException;

/**
 * Thrown if there's a grammatical error in the input.
 *
 * @author Mark S. Miller
 */
public class SyntaxException
  extends RuntimeException implements EPrintable {

    private final Twine myOptOpenner;

    private final Twine myOptLine;

    private final int myStart;

    private final int myBound;

    /**
     * @param optOpenner optional start of syntax problem
     * @param optLine    line containing error
     * @param start      index into optLine
     * @param bound      index into optLine
     */
    public SyntaxException(String msg,
                           Twine optOpenner,
                           Twine optLine,
                           int start,
                           int bound) {
        super(msg);
        myOptOpenner = optOpenner;
        myOptLine = optLine;
        myStart = start;
        myBound = bound;
    }

    /**
     *
     */
    public SyntaxException(String msg, Twine optLine) {
        super(msg);
        myOptOpenner = null;
        myOptLine = optLine;
        myStart = 0;
        myBound = optLine == null ? 0 : optLine.size();
    }

    /**
     * @return
     */
    public Twine getOptOpenner() {
        return myOptOpenner;
    }

    /**
     *
     */
    public Twine optLine() {
        return myOptLine;
    }

    /**
     *
     */
    public int getStart() {
        return myStart;
    }

    /**
     *
     */
    public int getBound() {
        return myBound;
    }

    /**
     *
     */
    public Twine optDamage() {
        if (null == myOptLine) {
            if (null == myOptOpenner) {
                return null;
            } else {
                return myOptOpenner;
            }
        } else {
            Twine result = (Twine)myOptLine.run(myStart, myBound);
            if (null == myOptOpenner) {
                return result;
            } else {
                return (Twine)myOptOpenner.add(result);
            }
        }
    }

    static private final String ErrPrefix = "syntax error: ";

    /**
     * Same number of spaces as in the ErrPrefix
     */
    static private final String ErrIndent =
      StringHelper.multiply(" ", ErrPrefix.length());

    /**
     * Prints as "syntax error: " followed by an optional message, and then
     * an optional indication of the location of the error.
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print(ErrPrefix);
        String msg = getMessage();
        if (!msg.equals("syntax error")) {
            out.indent(ErrIndent).print(msg);
        }
        if (null != myOptLine) {
            out = out.indent("  ");
            out.lnPrint(myOptLine.replaceAll("\t", " "));
            if (!myOptLine.endsWith("\n")) {
                out.println();
            }
            out.print(StringHelper.multiply(" ", myStart));
            out.print(StringHelper.multiply("^", myBound - myStart));
            SourceSpan optSpan = optDamage().getOptSpan();
            if (null != optSpan) {
                out.lnPrint("@ " + optSpan);
            }
        }
    }

    /**
     *
     */
    public String toString() {
        return E.toString(this);
    }
}
