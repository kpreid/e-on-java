package org.erights.e.elib.util;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 *
 */
public class ConditionLock {

    private boolean myFlag = false;

    private final Object myLock = new Object();

    /**
     *
     */
    public ConditionLock() {
    }

    /**
     *
     */
    public void on() {
        myFlag = true;
    }

    /**
     *
     */
    public void off() {
        synchronized (myLock) {
            myFlag = false;
            myLock.notifyAll();
        }
    }

    /**
     *
     */
    public void waitTillOff() {
        if (myFlag) {
            synchronized (myLock) {
                while (myFlag) {
                    try {
                        myLock.wait();
                    } catch (InterruptedException ie) {
                        //ignore
                    }
                }
            }
        }
    }
}
