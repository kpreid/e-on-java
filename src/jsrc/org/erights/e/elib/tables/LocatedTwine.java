package org.erights.e.elib.tables;

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
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.ref.Ref;

import java.io.IOException;

/**
 * A Twine containing only a non-empty String and corresponding source-span
 * info.
 *
 * @author Mark S. Miller
 */
final class LocatedTwine extends AtomicTwine {

    static private final long serialVersionUID = 2201288242995979823L;

    /**
     *
     */
    private final SourceSpan mySpan;

    /**
     *
     */
    LocatedTwine(String str, SourceSpan span) {
        super(str);
        mySpan = span;
        if (span.isOneToOne() &&
          str.length() != span.getEndCol() - span.getStartCol() + 1) {
            T.fail("one to one must have matching size");
        }
    }

    /**
     * '__makeTwine fromString(myStr, mySpan)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {GetTwineMaker(), "fromString", myStr, mySpan};
        return result;
    }

    /**
     *
     */
    public boolean isBare() {
        return false;
    }

    /**
     * Returns a Twine which is either empty or is a LocatedTwine describing
     * where the extracted run came from.
     */
    public ConstList run(int start, int bound) {
        if (0 == start) {
            //special cases worth checking
            if (0 == bound) {
                return EmptyTwine.THE_ONE;
            } else if (size() == bound) {
                return this;
            }
        }
        String str = myStr.substring(start, bound);
        SourceSpan span;
        if (mySpan.isOneToOne()) {
            int startCol = mySpan.getStartCol() + start;
            int endCol = startCol + (bound - start) - 1;
            span = new SourceSpan(mySpan.getUrl(),
                                  true,
                                  mySpan.getStartLine(),
                                  startCol,
                                  mySpan.getEndLine(),
                                  endCol);
        } else {
            span = mySpan;
        }
        return Twine.fromString(str, span);
    }

    /**
     * Just prints the string part.
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print(myStr);
    }

    /**
     * Two LocatedTwines can merge if the joining of their spans looses no
     * information.
     */
    ConstList mergedParts(AtomicTwine other) {
        if (other instanceof LocatedTwine) {
            SourceSpan hisSpan = ((LocatedTwine)other).mySpan;
            if (mySpan.isOneToOne()) {
                SourceSpan optCover = SourceSpan.optCover(mySpan, hisSpan);
                if (null != optCover && optCover.isOneToOne()) {
                    String str = myStr + other.bare();
                    Object[] parts = {new LocatedTwine(str, optCover)};
                    return ConstList.fromArray(parts);
                }
            } else if (Ref.isSameEver(mySpan, hisSpan)) {
                String str = myStr + other.bare();
                Object[] parts = {new LocatedTwine(str, mySpan)};
                return ConstList.fromArray(parts);
            }
        }
        Object[] parts = {this, other};
        return ConstList.fromArray(parts);
    }

    /**
     *
     */
    public SourceSpan getOptSpan() {
        return mySpan;
    }
}
