// Copyright 2006 Hewlett Packard, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.prim;

import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.slot.BaseSlot;

import java.io.IOException;

/**
 * @author Mark S. Miller
 */
class PropertySlot extends BaseSlot {

    private final Object mySelf;
    private final String myGetterVerb;
    private final String mySetterVerb;
    private final String myPropName;

    PropertySlot(Object self,
                 String getterVerb,
                 String setterVerb,
                 String propName) {
        mySelf = self;
        myGetterVerb = getterVerb;
        mySetterVerb = setterVerb;
        myPropName = propName;
    }

    public Object get() {
        return E.call(mySelf, myGetterVerb);
    }

    public void put(Object newValue) {
        E.call(mySelf, mySetterVerb, newValue);
    }

    public boolean isFinal() {
        return false;
    }

    public void __printOn(TextWriter out) throws IOException {
        out.print("::&", myPropName);
    }
}
