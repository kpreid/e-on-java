package org.erights.e.elib.tables;

import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;

import java.io.IOException;

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


/**
 * The canonical empty Twine.
 * <p/>
 * The EmptyTwine is always bare.
 *
 * @author Mark S. Miller
 */
public class EmptyTwine extends Twine {

    static private final long serialVersionUID = 8545207059080665278L;

    /**
     * The canonical instance
     */
    static public final Twine THE_ONE = new EmptyTwine();

    /**
     *
     */
    static private final Object[] TheSpreadUncall =
      {GetTwineMaker(), "fromString", ""};

    /**
     *
     */
    private EmptyTwine() {
    }

    /**
     * '__makeTwine fromString("")'
     */
    public Object[] getSpreadUncall() {
        return TheSpreadUncall;
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
    public char charAt(int index) throws IndexOutOfBoundsException {
        throw new IndexOutOfBoundsException("out of range: " + index);
    }

    /**
     *
     */
    public int size() {
        return 0;
    }

    /**
     *
     */
    public String bare() {
        return "";
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
        if (0 == start && 0 == bound) {
            return this;
        } else {
            throw new IndexOutOfBoundsException(
              "running on empty " + start + "..!" + bound);
        }
    }

    /**
     *
     */
    public ConstList getParts() {
        return ConstList.EmptyList;
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

    Twine infectOneToOne(String str) {
        return Twine.fromString(str, null);
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        //XXX is this the same as printing nothing?
        out.print("");
    }
}
