package org.erights.e.meta.java.awt;

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
import org.erights.e.elib.tables.AssocFunc;
import org.erights.e.meta.java.math.EInt;

import java.awt.Component;
import java.awt.Container;

/**
 * A sweetener for AWT containers that make them friendly to E
 *
 * @author Mark S. Miller
 */
public class ContainerSugar {

    /**
     * prevent instantiation
     */
    private ContainerSugar() {
    }

    /**
     * A Container enumerates its components
     */
    static public void iterate(Container self, AssocFunc func) {
        Component[] comps = self.getComponents();
        for (int i = 0; i < comps.length; i++) {
            func.run(EInt.valueOf(i), comps[i]);
        }
    }

    /**
     * So container[n] will work in E.
     */
    static public Component get(Container self, int n) {
        return self.getComponent(n);
    }

    /**
     * An unfortunate ambiguity.
     */
    static public int size(Container self) {
        T.fail("use Component getSize()");
        return -1; //make compiler happy
    }
}
