// Copyright 2003 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.extern.timer;

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.serial.Persistent;
import org.erights.e.elib.tables.FlexMap;

/**
 * Used as a timed self-removing persistent key in a map.
 * <p/>
 * After construction or unserialization, the Retainer isn't in a valid state
 * until init() is called. We don't automate this with readResolve() in order
 * to avoid a circular unserialization bug.
 *
 * @author Mark S. Miller
 */
public class Retainer implements Persistent, Runnable {

    private transient FlexMap myOptRetainers;

    protected Timer myOptTimer;

    private Timeout myOptTimeout;

    protected final long myOptExpirationDate;

    /**
     * optExpirationDate defaults to forever
     * ({@link java.lang.Long#MAX_VALUE}), so optTimer also defaults to null.
     */
    public Retainer() {
        this(null, Long.MAX_VALUE);
    }

    /**
     * @param optTimer May be null ONLY if optExpirationDate is Long.MAX_VALUE.
     */
    public Retainer(Timer optTimer, long optExpirationDate) {
        if (Long.MAX_VALUE == optExpirationDate) {
            myOptTimer = null; // unneeded in this case
        } else {
            myOptTimer = optTimer;
        }
        myOptExpirationDate = optExpirationDate;
    }

    /**
     * After construction or unserialization, the Retainer isn't in a valid
     * state until init() is called. We don't automate this with
     * readResolve() in order to avoid a circular unserialization bug.
     *
     * @param retainers This retainer will remove itself as a key from
     *                  retainers when it expires or is invoked.
     */
    protected void init(FlexMap retainers) {
        T.require(null == myOptRetainers,
                  "Must initialize a Retainer exactly once per incarnation");
        myOptRetainers = retainers;
        if (Long.MAX_VALUE == myOptExpirationDate) {
            myOptTimeout = null;
        } else {
            myOptTimeout = myOptTimer.whenAlarm(myOptExpirationDate, this);
        }
    }

    /**
     *
     */
    public void run() {
        if (null == myOptRetainers) {
            return;
        }
        myOptRetainers.removeKey(this);
        if (null != myOptTimeout) {
            myOptTimeout.run();
        }
        myOptTimer = null;
        myOptTimeout = null;
        myOptRetainers = null;
    }
}
