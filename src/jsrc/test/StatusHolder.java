// Copyright 2005 Mark S. Miller, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package test;

import java.util.ArrayList;
import java.util.Iterator;

interface Listener {

    void statusChanged(Object newStatus);
}

/**
 * @author Mark S. Miller
 */
public class StatusHolder {

    private Object myStatus;
    private final ArrayList/*<Listener>*/ myListeners =
      new ArrayList/*<Listener>*/();

    public StatusHolder(Object status) {
        myStatus = status;
    }

    public void addListener(Listener newListener) {
        myListeners.add(newListener);
    }

    public Object getStatus() {
        return myStatus;
    }

    public void setStatus(Object newStatus) {
        ArrayList/*<Listener>*/ listeners;
        synchronized (this) {
            myStatus = newStatus;
            listeners = (ArrayList/*<Listener>*/)myListeners.clone();
        }
//        for (Listener listener: listeners) {
        for (Iterator iter = listeners.iterator(); iter.hasNext();) {
            Listener listener = (Listener)iter.next();
            listener.statusChanged(newStatus);
        }
    }
}
