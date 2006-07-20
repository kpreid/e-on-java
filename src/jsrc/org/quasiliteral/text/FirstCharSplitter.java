package org.quasiliteral.text;

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
 * Splits a string on the first occurrence of any of a set of "special"
 * characters.
 *
 * @author Mark S. Miller
 */
public class FirstCharSplitter {

    /**
     * optimize the Ascii subset into an array lookup
     */
    private final boolean[] mySpecialFlags = new boolean[128];

    /**
     * do a linear lookup on the rest of the special
     */
    private final String myRestChars;

    /**
     *
     */
    public FirstCharSplitter(String specials) {
        StringBuffer restChars = new StringBuffer();
        for (int i = 0; i < specials.length(); i++) {
            char c = specials.charAt(i);
            if (c < 128) {
                mySpecialFlags[c] = true;
            } else {
                restChars.append(c);
            }
        }
        myRestChars = restChars.toString();
    }

    /**
     * Return the index of the first occurrence of any of the special chars
     * in str, or -1 if none are found.
     */
    public int findIn(String str) {
        int len = str.length();
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (c < 128) {
                if (mySpecialFlags[c]) {
                    return i;
                }
            } else if (myRestChars.indexOf(c) != -1) {
                return i;
            }
        }
        return -1;
    }
}
