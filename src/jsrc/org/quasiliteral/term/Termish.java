// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.quasiliteral.term;

import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.SamenessHashCacher;
import org.quasiliteral.astro.AstroArg;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Represents a sequence of Terms.
 * <p/>
 * The subclass Term represents a singleton sequence of Terms. The other
 * subclasses are only parse-time intermediate results.
 *
 * @author Mark S. Miller
 */
public abstract class Termish extends SamenessHashCacher
  implements AstroArg, EPrintable {

    /**
     * @return
     */
    static public Termish run(ConstList tList) {
        Termish result = EmptySeq.THE_ONE;
        for (int i = 0, len = tList.size(); i < len; i++) {
            result = PairSeq.run(result, (Termish)tList.get(i));
        }
        return result;
    }

    /**
     * With the surrounding "term`" and "`".
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("term`");
        prettyPrintOn(out.indent("     "), true);
        out.print("`");
    }

    /**
     * With the surrounding "term`" and "`".
     */
    public String toString() {
        return E.toString(this);
    }

    /**
     * Without the surrounding "term`" and "`".
     * <p/>
     * quasiFlag defaults to false.
     */
    public String asText() {
        return asText(false);
    }

    /**
     * Without the surrounding "term`" and "`".
     */
    public String asText(boolean quasiFlag) {
        StringWriter strWriter = new StringWriter();
        try {
            prettyPrintOn(new TextWriter(strWriter), quasiFlag);
        } catch (Throwable th) {
            throw ExceptionMgr.asSafe(th);
        }
        return strWriter.getBuffer().toString();
    }

    /**
     *
     */
    public abstract void prettyPrintOn(TextWriter out, boolean quasiFlag)
      throws IOException;

    /**
     * The sequence of Terms represented by this Termish
     */
    abstract ConstList getTerms();

    /**
     * Add the sequence of Terms represented by this Termish to the end of the
     * list.
     */
    abstract void getTerms(FlexList list);

    /**
     * What this.getTerms().size() would be.
     */
    abstract int getNumTerms();
}
