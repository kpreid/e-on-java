// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package test.joee;

import org.erights.e.elib.prim.E;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.OneArgFunc;

/**
 * @author Mark S. Miller
 */
public class Observable {

    private Object myValue;
    private FlexList myListeners;

    public Observable(Object value) {
        myValue = value;
        myListeners = FlexList.make();
    }

    public void addListener(OneArgFunc listener) {
        myListeners.push(listener);
    }

    public Object get() {
        return myValue;
    }

    public void put(Object newValue) {
        for (int i = 0, len = myListeners.size(); i < len; i++) {
            Object listener = myListeners.get(i);
            E.sendOnly(listener, "valueChanged", newValue);
        }
        myValue = newValue;
    }
}
