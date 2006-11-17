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

import org.erights.e.elib.prim.Message;
import org.erights.e.elib.vat.Vat;

/**
 * Object representing a scheduled timeout event.
 */
public class Timeout extends TimerWatcher implements Runnable {

    private TimerThread myThread;

    private Vat myVat;

    private Object myTarget;

    private Message myMessage;

    /**
     * Creates a new Timeout object. <p>
     * <p/>
     * Captures the current Vat, so must be called from within the vat-thread
     * holding target.
     *
     * @param thread The timer thread responsible for us
     * @param target The object to be notified at timeout time
     */
    Timeout(TimerThread thread, Object target, Message msg) {
        myThread = thread;
        myVat = Vat.getCurrentVat();
        myTarget = target;
        myMessage = msg;
    }

    /**
     * Cancels this timeout. Note, however, that although a Timeout can be
     * cancelled, there is no guarantee that it has not already occured at the
     * time it is cancelled -- perhaps with the notification still in flight.
     */
    public void run() {
        if (myThread == null) {
            return;
        }
        myThread.cancelTimeout(this);
        myThread = null;
        myVat = null;
        myTarget = null;
        myMessage = null;
    }

    /**
     * Called by the timer thread when the timeout time comes.
     */
    void handleTimeout() {
        myVat.qSendMsg(myTarget, myMessage);
        //if sendMsg returns non-null, we don't care, since timed events
        //for a vat that's shut down can be ignored.
        myThread = null;
        myVat = null;
        myTarget = null;
        myMessage = null;
    }
}
