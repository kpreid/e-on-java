package org.erights.e.elib.base;

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
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.serial.DeepPassByCopy;
import org.erights.e.elib.serial.Persistent;
import org.erights.e.meta.java.math.EInt;

/**
 * A description of where a particular piece of Twine came from, such as a
 * range of source code, suitable for use by a debugger.
 * <p/>
 * Woven into a piece of Twine so it remembers where it came from.
 *
 * @author Mark S. Miller
 */
public class SourceSpan implements Persistent, DeepPassByCopy {

    static private final long serialVersionUID = -7424472535435659022L;

    /**
     *
     */
    static public final StaticMaker SourceSpanMaker =
      StaticMaker.make(SourceSpan.class);

    private final String myUrl;

    private final boolean myIsOneToOne;

    private final int myStartLine;

    private final int myStartCol;

    private final int myEndLine;

    private final int myEndCol;

    /**
     *
     */
    public SourceSpan(String url,
                      boolean isOneToOne,
                      int startLine,
                      int startCol,
                      int endLine,
                      int endCol) {
        myUrl = url;
        myIsOneToOne = isOneToOne;
        myStartLine = startLine;
        myStartCol = startCol;
        myEndLine = endLine;
        myEndCol = endCol;
        if (isOneToOne && startLine != endLine) {
            T.fail("oneToOne must be on a line: " + this);
        }
    }

    /**
     * '__makeSourceSpan(myUrl, myIsOneToOne, myStartLine, myStartCol,
     * myEndLine, myEndCol)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {SourceSpanMaker,
          "run",
          myUrl,
          myIsOneToOne ? Boolean.TRUE : Boolean.FALSE,
          EInt.valueOf(myStartLine),
          EInt.valueOf(myStartCol),
          EInt.valueOf(myEndLine),
          EInt.valueOf(myEndCol)};
        return result;
    }

    /**
     * Where might (this version of) the source text be found?
     * <p/>
     * Users of SourceSpan should prevent or prepare for the possibility that
     * the text retrieved from a remembered Url may no longer be the original
     * version. When possible, version integrity information should be included
     * in the Url, like a cryptographic hash of the contents. However, such
     * issues are beyond the scope of SourceSpan or Twine by themselves. To
     * them, the Url is simply a String.
     */
    public String getUrl() {
        return myUrl;
    }

    /**
     * Does each character in that Twine map to the corresponding source
     * character position? <p>
     * <p/>
     * If so, then startLine must be the same as endLine, and the described
     * Twine's size should be 'endCol - startCol + 1'. Otherwise, all the
     * characters in the Twine map to all the characters described by this
     * SourceSpan.
     */
    public boolean isOneToOne() {
        return myIsOneToOne;
    }

    /**
     * Line number of beginning of span, in the text unit (file?) described by
     * url. Line numbers are counted starting at 1.
     */
    public int getStartLine() {
        return myStartLine;
    }

    /**
     * Position of first character of span within the first line. Column
     * numbers are couunted starting at 0.
     */
    public int getStartCol() {
        return myStartCol;
    }

    /**
     * Line number of line holding the last character of the span. Note, this
     * is inclusive.
     */
    public int getEndLine() {
        return myEndLine;
    }

    /**
     * Position of last character of span within this last line. Note, this is
     * inclusive.
     */
    public int getEndCol() {
        return myEndCol;
    }

    /**
     * Returns a new SourceSpan that covers the original two.
     * <p/>
     * Either input may be null, as may the output. If either input is null,
     * the result is null. If the two don't have the same Url, the result is
     * null. Finally, the result describes the minimal span that includes both
     * the originals. Iff the originals are both oneToOne and optB immediately
     * follows optA on the same line, then the result is also oneToOne.
     */
    static public SourceSpan optCover(SourceSpan optA, SourceSpan optB) {
        if (null == optA || null == optB || !optA.myUrl.equals(optB.myUrl)) {
            return null;
        }
        if (optA.myIsOneToOne && optB.myIsOneToOne &&
          optA.myEndLine == optB.myStartLine &&
          optA.myEndCol + 1 == optB.myStartCol) {
            return new SourceSpan(optA.myUrl,
                                  true,
                                  optA.myStartLine,
                                  optA.myStartCol,
                                  optB.myEndLine,
                                  optB.myEndCol);
        }

        int startLine;
        int startCol;
        int endLine;
        int endCol;

        if (optA.myStartLine < optB.myStartLine) {
            startLine = optA.myStartLine;
            startCol = optA.myStartCol;
        } else if (optA.myStartLine == optB.myStartLine) {
            startLine = optA.myStartLine;
            startCol = StrictMath.min(optA.myStartCol, optB.myStartCol);
        } else {
            startLine = optB.myStartLine;
            startCol = optB.myStartCol;
        }

        if (optA.myEndLine < optB.myEndLine) {
            endLine = optB.myEndLine;
            endCol = optB.myEndCol;
        } else if (optA.myEndLine == optB.myEndLine) {
            endLine = optA.myEndLine;
            endCol = StrictMath.max(optA.myEndCol, optB.myEndCol);
        } else {
            endLine = optA.myEndLine;
            endCol = optA.myEndCol;
        }

        return new SourceSpan(optA.myUrl,
                              false,
                              startLine,
                              startCol,
                              endLine,
                              endCol);
    }

    /**
     * Return a non-oneToOne form of this SourceSpan
     */
    public SourceSpan notOneToOne() {
        if (myIsOneToOne) {
            return new SourceSpan(myUrl,
                                  false,
                                  myStartLine,
                                  myStartCol,
                                  myEndLine,
                                  myEndCol);
        } else {
            return this;
        }
    }

    /**
     *
     */
    public String toString() {
        String fragType;
        if (myIsOneToOne) {
            fragType = "span";
        } else {
            fragType = "blob";
        }
        return "<" + myUrl + "#:" + fragType + "::" + myStartLine + ":" +
          myStartCol + "::" + myEndLine + ":" + myEndCol + ">";
    }
}
