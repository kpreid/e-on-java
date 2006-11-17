package org.erights.e.elang.evm;

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

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.NestedException;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.serial.DeepPassByCopy;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.SamenessHashCacher;
import org.erights.e.elib.util.OneArgFunc;
import org.quasiliteral.base.MatchMaker;

import java.io.IOException;

/**
 * A ParseNode of a program written in Expanded-E, Kernel-E, etc.
 * <p/>
 * A program written in E is immediately expanded to Expanded-E, hopefully
 * passing source position through successfully.
 *
 * @author Mark S. Miller
 */
public abstract class ParseNode extends SamenessHashCacher
  implements MatchMaker, EPrintable, DeepPassByCopy {

    /**
     * lowest priority
     */
    protected static final int PR_START = 0;

    /**
     * lowest priority expression
     */
    protected static final int PR_EEXPR = 0;

    protected static final int PR_ASSIGN = 1;

    protected static final int PR_COMP = 2;

    protected static final int PR_ORDER = 3;

    protected static final int PR_CALL = 4;

    /**
     * highest priority expression
     */
    protected static final int PR_PRIM = 5;

    /**
     * lowest priority pattern
     */
    protected static final int PR_PATTERN = 0;

    protected static final int PR_LISTPATT = 1;

//    /** Number of priority levels */
//    static public final int NUM_PR = 6;

    private final SourceSpan myOptSpan;

    /**
     * A bit of a kludge, but we initialize the source after construction to
     * avoid propogating source-tracking logic through all subclasses.
     */
    protected ParseNode(SourceSpan optSpan) {
        myOptSpan = optSpan;
    }

    /**
     * priority defaults to PR_START
     */
    public void lnPrintOn(TextWriter out) throws IOException {
        lnPrintOn(out, PR_START);
    }

    /**
     * Onto out, first print a newline, then spaces to the designated indent
     * level, then pretty print this parse node. "subPrintOn" vs "lnPrintOn" is
     * much like the conventional disctinction between "print" and "println",
     * except that the newlines come first (hence the weird spelling), and the
     * newline is followed by indentation.
     */
    public void lnPrintOn(TextWriter out, int priority) throws IOException {
        out.println();
        subPrintOn(out, priority);
    }

    /**
     * For when we already know specimen isn't null.
     */
    protected abstract void subMatchBind(ConstList args,
                                         Object specimen,
                                         OneArgFunc optEjector,
                                         FlexList bindings);

    /**
     * For when optSpecimen may be null.
     *
     * @param args
     * @param optSpecimen
     * @param optEjector
     * @param bindings
     */
    public void matchBind(ConstList args,
                          Object optSpecimen,
                          OneArgFunc optEjector,
                          FlexList bindings) {
        optSpecimen = Ref.resolution(optSpecimen);
        if (null == optSpecimen) {
            throw Thrower.toEject(optEjector, "Must not be null");
        }
        subMatchBind(args, optSpecimen, optEjector, bindings);
    }

    /**
     *
     */
    public ConstList matchBind(ConstList args,
                               Object specimen,
                               OneArgFunc optEjector) {
        FlexList bindings = FlexList.make();
        matchBind(args, specimen, optEjector, bindings);
        return bindings.snapshot();
    }

    /**
     * Convenience method for matching corresponding arrays. null members must
     * match exactly.
     */
    static void matchBind(ParseNode[] templates,
                          ConstList args,
                          ParseNode[] specimens,
                          OneArgFunc optEjector,
                          FlexList bindings) {
        int len = templates.length;
        if (len != specimens.length) {
            throw Thrower.toEject(optEjector,
                                  "Arity mismatch: " + E.toQuote(templates) +
                                    " vs " + E.toQuote(specimens));
        }
        for (int i = 0; i < len; i++) {
            matchBind(templates[i], args, specimens[i], optEjector, bindings);
        }
    }

    /**
     * Convenience method for handling nulls. nulls must match exactly.
     */
    static void matchBind(ParseNode optTemplate,
                          ConstList args,
                          ParseNode optSpecimen,
                          OneArgFunc optEjector,
                          FlexList bindings) {
        if (null == optTemplate) {
            if (null != optSpecimen) {
                throw Thrower.toEject(optEjector,
                                      "Must be null: " + optSpecimen);
            }
        } else {
            optTemplate.matchBind(args, optSpecimen, optEjector, bindings);
        }
    }

    /**
     * Where is the source code this syntactic construct was parsed from?
     */
    public SourceSpan getOptSpan() {
        return myOptSpan;
    }

    /**
     * Like {@link T#fail}, but adds the poser's source position info, if any,
     * to the backtrace.
     */
    static public void fail(String explanation, ParseNode optPoser) {
        RuntimeException problem =
          new RuntimeException("Failed: " + explanation);
        if (null != optPoser) {
            SourceSpan optSpan = optPoser.myOptSpan;
            if (null != optSpan) {
                problem = new NestedException(problem, "@ " + optSpan);
            }
        }
        throw problem;
    }

    /**
     * Print the left bracket, then the nodes separated by sep, and then the
     * right bracket
     */
    static public void printListOn(String left,
                                   ParseNode[] nodes,
                                   String sep,
                                   String right,
                                   TextWriter out,
                                   int priority) throws IOException {
        out.print(left);
        if (nodes.length >= 1) {
            int last = nodes.length - 1;
            for (int i = 0; i < last; i++) {
                nodes[i].subPrintOn(out, priority);
                out.print(sep);
            }
            nodes[last].subPrintOn(out, priority);
        }
        out.print(right);
    }

    /**
     * Pretty print this syntactic construct assuming the specified ambient
     * indent level. The convention is that any leading or trailing whitespace
     * (newlines, indentation, etc...) is handled by my caller. I just print
     * from my first printing character to my last one, indenting as
     * appropriate for internal newlines.
     */
    public abstract void subPrintOn(TextWriter out, int priority)
      throws IOException;

    /**
     * Overridden in EExpr & Pattern
     *
     * @see #subPrintOn
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("e??`");
        subPrintOn(out, PR_START);
        out.print("`");
    }

    /**
     * For use in IDEs that invoke toString() from inside the debugger (at
     * least VisualAge and IntelliJ IDEA).
     */
    public String toString() {
        return asText();
    }

    /**
     * Without the surrounding "e`" and "`".
     */
    public String asText() {
        Object[] pair = TextWriter.makeBufferingPair();
        TextWriter tw = (TextWriter)pair[0];
        StringBuffer sb = (StringBuffer)pair[1];
        try {
            subPrintOn(tw, PR_START);
        } catch (IOException iox) {
            throw new NestedException(iox, "# in ParseNode.asText()");
        }
        return sb.toString();
    }
}
