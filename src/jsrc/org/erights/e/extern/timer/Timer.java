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

import org.erights.e.elib.base.Thunk;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.Message;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.ref.Resolver;

/**
 * The master control object for timers and clocks.
 * <p/>
 * Conceptually, there's one ephemeral (non-persistent) Timer per vat, but
 * actually there's just one per JVM, where each delayed event remembers which
 * vat it came from, and, when its time comes due, queues that event on that
 * vat. Conceptually, when a vat is shut down, its per-vat-Timer is destroyed
 * with it, freeing all its delayed events. Actually, nothing in the
 * per-jvm-Timer is effected by a vat shutdown, but when these events come due,
 * the attempt to queue them on their originating (and now, shut down) vat will
 * silently fail.
 * <p/>
 * If you build a virtual persistent Timer on top of the ephemeral Timer +
 * {@link org.erights.e.extern.persist.SturdyRefMaker#onRevival}, be sure to
 * cancel the onRevival when the timer event is <i>received</i>, not when it is
 * <i>sent</i>. If you cancel the onRevival when the timer event is sent, and
 * if your vat checkpoints after it is sent and before its received, and if
 * your vat then revives from that checkpoint, then your event will be
 * undelivered and lost. To do the cancellation on reception, your
 * virtualization should wrap the real event with a persistent cancelling
 * event.
 *
 * @author Chip Morningstar
 * @author Mark S. Miller
 */
public class Timer {

    /**
     * The single permitted instance of this class
     */
    static private final Timer TheTimer = new Timer();

    /**
     * Our timer thread
     */
    private TimerThread myThread = null;

    /**
     * Private constructor. Just start the timer thread.
     */
    private Timer() {
        myThread = new TimerThread();
        myThread.start();
    }

    /**
     * The current time in absolute millis
     */
    public long now() {
        return System.currentTimeMillis();
    }

    /**
     * Sets an alarm to occur after the specified time, unless cancelled.
     * <p/>
     * After the alarm expires, target is eventually sent the run() message.
     *
     * @param absMillis When should the alarm go off?
     * @param target    Object to be informed when the time comes
     * @return A Timeout object that can be used to cancel or identify the
     *         timeout.
     */
    public Timeout whenAlarm(long absMillis, Runnable target) {
        Timeout newTimeout = new Timeout(myThread,
                                         target,
                                         new Message(null,
                                                     "run",
                                                     E.NO_ARGS));
        myThread.setAlarm(absMillis, newTimeout);
        return newTimeout;
    }

    /**
     * When the current time is >= absMillis, evaluate<br>
     * <tt>target&nbsp;&lt;-&nbsp;verb(args...)</tt>.
     * <p/>
     * Remembers the
     * {@link org.erights.e.elib.vat.SendingContext SendingContext} info.
     *
     * @return a promise for the result of the send.
     */
    public Ref whenPast(long absMillis,
                        Object target,
                        String verb,
                        Object[] args) {
        Object[] pair = Ref.promise();
        Timeout newTimeout = new Timeout(myThread,
                                         target,
                                         new Message((Resolver)pair[1],
                                                     verb,
                                                     args));
        myThread.setAlarm(absMillis, newTimeout);
        return (Ref)pair[0];
    }

    /**
     * Sets a timeout to occur after the specified time.
     * <p/>
     * After the timer expires, target is eventually sent the run() message.
     *
     * @param absMillis When to time out
     * @param target    A thunk to be informed when the time comes
     * @return A promise for the result of evaluating the thunk.
     */
    public Ref whenPast(long absMillis, Thunk target) {
        return whenPast(absMillis, target, "run", E.NO_ARGS);
    }

    /**
     * Creates a new clock. The new clock begins life stopped with its tick
     * count at zero.
     *
     * @param resolution The clock tick interval
     * @param target     Object to be sent tick notifications
     * @return A new Clock object according to the given parameters
     * @see org.erights.e.extern.timer.TickReactor
     */
    public Clock every(long resolution, TickReactor target) {
        return new Clock(myThread, resolution, target);
    }

    /**
     * XXX Return the single permitted Timer object. <p>
     * <p/>
     * There shouldn't be a single one.
     */
    static public Timer theTimer() {
        return TheTimer;
    }
}
