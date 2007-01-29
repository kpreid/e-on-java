package net.captp.tables;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.ref.EProxyHandler;
import org.erights.e.elib.ref.EProxyResolver;
import org.erights.e.elib.ref.Ref;

/**
 *
 */
public class ImportsTable extends CommTable {

    /**
     *
     */
    public ImportsTable() {
    }

    /**
     *
     */
    public void store(int index, EProxyResolver value) {
        if (!isFree(index)) {
            EProxyHandler optHandler = optHandler(index);
            if (null != optHandler) {
                optHandler.mustBeDisposable();
            }
        }
        put(index, value);
    }

    /**
     *
     */
    public void smash(Throwable problem) {
        for (int i = 1; i < myCapacity; i++) {
            if (!isFree(i)) {
                EProxyResolver pr = (EProxyResolver)myStuff[i];
                pr.smash(problem);
            }
        }
        super.smash(problem);
    }

    /**
     *
     */
    public EProxyResolver getProxyResolver(int index) {
        return (EProxyResolver)get(index);
    }

    /**
     *
     */
    public Ref getProxy(int index) {
        return getProxyResolver(index).getProxy();
    }

    /**
     *
     */
    public EProxyHandler optHandler(int index) {
        return getProxyResolver(index).optHandler();
    }
}
