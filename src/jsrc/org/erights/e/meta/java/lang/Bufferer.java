package org.erights.e.meta.java.lang;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.exception.ExceptionMgr;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Run in a separate Thread to keep a Process from deadlocking.
 * <p/>
 * This could be generalized into something useful from E.
 *
 * @author Mark S. Miller
 */
class Bufferer implements Runnable {

    /**
     *
     */
    private final Object myLock = new Object();

    /**
     *
     */
    private InputStream myIns;

    /**
     *
     */
    private StringBuffer myBuf;

    /**
     *
     */
    Bufferer(InputStream ins, StringBuffer buf) {
        myIns = ins;
        myBuf = buf;
    }

    /**
     *
     */
    public void run() {
        Reader reader = new InputStreamReader(myIns);
        try {
            try {
                int c;
                while ((c = reader.read()) != -1) {
                    synchronized (myBuf) {
                        myBuf.append((char)c);
                    }
                }
            } finally {
                myIns = null;
                myBuf = null;
                synchronized (myLock) {
                    myLock.notifyAll();
                }
                reader.close();
            }
        } catch (IOException ioe) {
            throw ExceptionMgr.asSafe(ioe);
        }
    }

    /**
     *
     */
    void waitFor() {
        try {
            while (null != myIns) {
                synchronized (myLock) {
                    myLock.wait();
                }
            }
        } catch (InterruptedException ie) {
            throw ExceptionMgr.asSafe(ie);
        }
    }
}


