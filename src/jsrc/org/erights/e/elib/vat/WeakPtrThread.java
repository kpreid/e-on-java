package org.erights.e.elib.vat;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import java.lang.ref.ReferenceQueue;

/**
 * @author Mark S. Miller
 */
class WeakPtrThread extends Thread {

    static final WeakPtrThread THE_ONE = new WeakPtrThread();

    private final ReferenceQueue myWeakPtrQueue;

    /**
     *
     */
    private WeakPtrThread() {
        super("global WeakPtrThread");
        myWeakPtrQueue = new ReferenceQueue();
        setDaemon(true);
        start();
    }

    /**
     *
     */
    public ReferenceQueue getWeakPtrQueue() {
        return myWeakPtrQueue;
    }

    /**
     *
     */
    public void run() {
        while (true) {
            WeakPtr weakPtr;
            try {
                weakPtr = (WeakPtr)myWeakPtrQueue.remove();
            } catch (InterruptedException ie) {
                continue;
            }
            weakPtr.myVat.qSendMsg(weakPtr.myReactor, weakPtr.myMessage);
        }
    }
}
