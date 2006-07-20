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

import java.io.IOException;

/**
 * A Twine containing only a non-empty String. <p>
 *
 * @author Mark S. Miller
 */
final class SimpleTwine extends AtomicTwine {

    static private final long serialVersionUID = -6842512130437927421L;

    /**
     *
     */
    SimpleTwine(String str) {
        super(str);
        T.require(str.length() >= 1,
                  "internal: SimpleTwine must be non-empty");
    }

    /**
     * '__makeTwine fromString(myStr)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {GetTwineMaker(), "fromString", myStr};
        return result;
    }

    /**
     * Bare Twine is <i>atomic</i> (representable by a literal expression), and
     * therefore, even though it's <i>transparent</i>, returns <tt>null</tt>.
     */
    public Object[] __optUncall() {
        return null;
    }

    /**
     *
     */
    public boolean isBare() {
        return true;
    }

    /**
     *
     */
    public ConstList run(int start, int bound) {
        return Twine.fromString(myStr.substring(start, bound));
    }

    /**
     *
     */
    ConstList mergedParts(AtomicTwine other) {
        if (other instanceof SimpleTwine) {
            Object[] parts = {new SimpleTwine(myStr + other.bare())};
            return ConstList.fromArray(parts);
        }
        Object[] parts = {this, other};
        return ConstList.fromArray(parts);
    }

    /**
     *
     */
    public SourceSpan getOptSpan() {
        return null;
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print(myStr);
    }
}
