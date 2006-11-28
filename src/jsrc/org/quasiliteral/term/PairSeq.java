// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.quasiliteral.term;

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.quasiliteral.astro.AstroArg;

import java.io.IOException;

/**
 * Represents a sequence of two or more Terms.
 * <p/>
 * Should only be needed as an intermediate result during parsing.
 *
 * @author Mark S. Miller
 */
public final class PairSeq extends Termish {

    private final Termish myLeft;

    private final Termish myRight;

    /**
     * @param left  May not be an EmptySeq
     * @param right May not be an EmptySeq
     */
    public PairSeq(Termish left, Termish right) {
        myLeft = left;
        myRight = right;
        T.require(!(myLeft instanceof EmptySeq), "May not be empty");
        T.require(!(myRight instanceof EmptySeq), "May not be empty");
    }

    public AstroArg withOptSpan(SourceSpan optSpan) {
        T.fail("XXX not yet implemented");
        return null; //make compiler happy
    }

    /**
     * @return
     */
    static public Termish run(Termish left, Termish right) {
        if (left instanceof EmptySeq) {
            return right;
        } else if (right instanceof EmptySeq) {
            return left;
        } else {
            return new PairSeq(left, right);
        }
    }

    /**
     * @return
     */
    ConstList getTerms() {
        int len = getNumTerms();
        FlexList result = FlexList.fromType(Term.class, len);
        getTerms(result);
        return result.snapshot();
    }

    /**
     * @param list
     */
    void getTerms(FlexList list) {
        myLeft.getTerms(list);
        myRight.getTerms(list);
    }

    /**
     * @return
     */
    int getNumTerms() {
        return myLeft.getNumTerms() + myRight.getNumTerms();
    }

    /**
     * @return null
     */
    public SourceSpan getOptSpan() {
        return null;
    }

    /**
     * @param out
     * @param quasiFlag
     * @throws java.io.IOException
     */
    public void prettyPrintOn(TextWriter out, boolean quasiFlag)
      throws IOException {
        ConstList terms = getTerms();
        //print each child lined up.
        out.print("(");
        if (terms.size() >= 1) {
            TextWriter sub = out.indent(" ");

            ((Term)terms.get(0)).prettyPrintOn(sub, quasiFlag);
            for (int i = 1; i < terms.size(); i++) {
                sub.println(",");
                ((Term)terms.get(i)).prettyPrintOn(sub, quasiFlag);
            }
        }
        out.print(")");
    }
}
