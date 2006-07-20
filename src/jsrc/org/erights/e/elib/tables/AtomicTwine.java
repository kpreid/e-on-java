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


/**
 * A Twine which can describe itself without breaking itself up into smaller
 * Twines. <p>
 *
 * @author Mark S. Miller
 */
abstract class AtomicTwine extends Twine implements ArrayedList {

    static private final long serialVersionUID = 2310317145102038297L;

    /**
     *
     */
    final String myStr;

    /**
     *
     */
    AtomicTwine(String str) {
        myStr = str;
    }

    /**
     *
     */
    public char charAt(int index) throws IndexOutOfBoundsException {
        return myStr.charAt(index);
    }

    /**
     *
     */
    public int size() {
        return myStr.length();
    }

    /**
     *
     */
    public String bare() {
        return myStr;
    }

    /**
     *
     */
    public ConstList getParts() {
        AtomicTwine[] parts = {this};
        return ConstList.fromArray(parts);
    }

    /**
     *
     */
    Twine infectOneToOne(String str) {
        return Twine.fromString(str, getOptSpan());
    }

    /**
     *
     */
    abstract ConstList mergedParts(AtomicTwine other);

    /**
     * An array of my characters.
     *
     * @return
     */
    public Object getSecretArray() {
        return myStr.toCharArray();
    }
}
