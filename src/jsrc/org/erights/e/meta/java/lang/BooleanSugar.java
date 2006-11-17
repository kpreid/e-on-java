package org.erights.e.meta.java.lang;

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
 * A sweetener defining extra messages that may be e-sent to booleans.
 *
 * @author Mark S. Miller
 */
public class BooleanSugar {

    /**
     * prevent instantiation
     */
    private BooleanSugar() {
    }

    static public boolean and(boolean self, boolean arg) {
        return self && arg;
    }

    static public boolean not(boolean self) {
        return !self;
    }

    static public boolean or(boolean self, boolean arg) {
        return self || arg;
    }

    static public Object pick(boolean self,
                              Object trueChoice,
                              Object falseChoice) {
        if (self) {
            return trueChoice;
        } else {
            return falseChoice;
        }
    }

    static public boolean xor(boolean self, boolean arg) {
        return self ^ arg;
    }

    /**
     * false &lt; true
     * <p/>
     * Therefore, (a &lt= b) === (a implies b). It's a shame that the arrow
     * goes in the wrong direction, but this is probably less confusing than
     * having false &gt; true.
     *
     * @param self
     * @param arg
     * @return
     */
    static public int op__cmp(boolean self, boolean arg) {
        if (self) {
            if (arg) {
                return 0;
            } else {
                return 1;
            }
        } else {
            if (arg) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
