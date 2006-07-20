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
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.meta.java.math.EInt;

import java.util.Enumeration;


/**
 * A sweetener defining extra messages that may be e-sent to an Enumeration.
 *
 * @author Mark S. Miller
 */
public class EnumerationSugar {

    /**
     * prevents instantiation
     */
    private EnumerationSugar() {
    }

    /**
     * Enumerates the enumeration. For each element, calls func with the
     * count as key and the element as value.
     */
    static public void iterate(Enumeration self, AssocFunc func) {
        int i = 0;
        while (self.hasMoreElements()) {
            func.run(EInt.valueOf(i), self.nextElement());
            i++;
        }
    }

    /**
     *
     */
    static public ConstList asList(Enumeration self) {
        FlexList buf = FlexList.make();
        while (self.hasMoreElements()) {
            buf.push(self.nextElement());
        }
        return buf.snapshot();
    }
}
