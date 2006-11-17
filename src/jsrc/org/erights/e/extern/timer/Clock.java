package org.erights.e.extern.timer;

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

import org.erights.e.elib.vat.Vat;
import org.erights.e.meta.java.math.EInt;

/**
 * A Clock is an Object which sends the message run(tick) to a target Object
 * every 'n' milliseconds. Clocks can only be created by calling the method
 * every() on a Timer instance. <p>
 * <p/>
 * A Clock can be started and stopped, and also terminated. If a Clock is
 * terminated, it is stopped and can no longer be started.
 *
 * @author Chip Morningstar
 * @see org.erights.e.extern.timer.TickReactor
 */
public class Clock extends TimerWatcher {

    /**
     *
     */
    private final Object myLock = new Object();

    /**
     * The timer thread that is our source of tick events
     */
    private final TimerThread myThread;

    /** */
    private final Vat myVat;

    /**
     * Target EObject
     */
    private final TickReactor myTarget;

    /**
     * Current run state
     */
    private boolean amTicking;

    /**
     * Tick resolution in milliseconds
     */
    private final long myResolution;

    /**
     * Current tick number
     */
    private int myTicks;

    /**
     * Called by the Timer.every() method. <p>
     * <p/>
     * Captures the current Vat, so must be called from within the vat-thread
     * holding target.
     *
     * @param thread     The timer thread we are running with
     * @param resolution How often we are to get tick events
     * @param arg        An arbitrary object to be passed along with the tick
     *                   events
     */
    Clock(TimerThread thread, long resolution, TickReactor target) {
        myThread = thread;
        myResolution = resolution;
        amTicking = false;
        myTicks = 0;
        myVat = Vat.getCurrentVat();
        myTarget = target;
    }

    /**
     * Gets the current tick
     *
     * @return The current tick setting.
     */
    public int getTicks() {
        return myTicks;
    }

    /**
     * Called by the timer thread at clock tick time.
     */
    void handleTimeout() {
        if (!amTicking) {
            return; // Toss it, we're stopped
        }
        myTicks++;
        Object[] args = {EInt.valueOf(myTicks)};
        myVat.qSendAllOnly(myTarget, false, "run", args);
        //if sendAllOnly returns non-null, we don't care, since timed events
        //for a vat that's shut down can be ignored.
    }

    /**
     * Starts the Clock from the current tick.
     */
    public void start() {
        synchronized (myLock) {
            amTicking = true;
            myThread.setClock(this, myResolution);
        }
    }

    /**
     * Stops the clock from ticking. The clock can be restarted with start().
     */
    public void stop() {
        if (!amTicking) {
            return;
        }
        myThread.cancelTimeout(this);
        amTicking = false;
    }
}
