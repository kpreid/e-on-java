package org.erights.e.elang.syntax;

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
 * For a Parser temporary
 *
 * @author Mark S. Miller
 */


class Assoc {

    private final Object myKey;

    private final Object myValue;

    Assoc(Object key, Object value) {
        myKey = key;
        myValue = value;
    }

    public Object key() {
        return myKey;
    }

    public Object value() {
        return myValue;
    }
}
