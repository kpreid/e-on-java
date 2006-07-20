package org.erights.e.meta.java.util;

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

import org.erights.e.elib.tables.AssocFunc;

import java.util.Dictionary;
import java.util.Enumeration;


/**
 * @author Mark S. Miller
 */
public class DictionarySugar {

    static public final Class OTHER_CLASS = Dictionary.class;

    /**
     * prevent instantiation
     */
    private DictionarySugar() {
    }

    /**
     * Enumerates each of the key-value pairs in the Dictionary. XXX
     * Should enumerate a snapshot of the dictionary, so we're
     * insulated from side effects to the dictionary during
     * enumeration.
     */
    static public void iterate(Dictionary self, AssocFunc func) {
        Enumeration keys = self.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = self.get(key);
            func.run(key, value);
        }
    }
}
