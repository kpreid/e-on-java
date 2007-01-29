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

import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.tables.FlexList;

/**
 * Thread to handle timeouts and clocks.
 *
 * @author Chip Morningstar
 */
class TimerThread extends Thread {

    /**
     *
     */
    private final Object myLock = new Object();

    /**
     * queue of pending timer events
     */
    private TimerQEntry myTopEntry = null;

    /**
     * Flag to control execution
     */
    private boolean myRunning = true;

    private final static int FUDGE = 5; // Get more than 5 repeating timeouts

    /**
     * Package level constructor
     */
    TimerThread() {
        super("TimerThread");
        setPriority(MAX_PRIORITY);
        //XXX what to do about daemon-ness?
    }

    /**
     * Cancel a previously scheduled timer event.
     *
     * @param target Object whose event this is.
     */
    boolean cancelTimeout(TimerWatcher target) {
        synchronized (myLock) {
            TimerQEntry entry;
            TimerQEntry previous;
            if (myTopEntry == null) {
                return false;
            }
            if (myTopEntry.myTarget == target) {
                entry = myTopEntry;
                myTopEntry = myTopEntry.myNext;
            } else {
                previous = myTopEntry;
                entry = myTopEntry.myNext;
                while (entry != null) {
                    if (entry.myTarget == target) {
                        previous.myNext = entry.myNext;
                        break;
                    }
                    previous = entry;
                    entry = entry.myNext;
                }
            }
            if (entry != null) {
                entry.myNext = null;
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Insert a new event into the timer queue (in order).
     *
     * @param newEntry A TimerQEntry describing the new event.
     */
    private void insertEntry(TimerQEntry newEntry) {
        synchronized (myLock) {
            TimerQEntry previous = null;
            TimerQEntry entry = myTopEntry;

            while (entry != null) {
                if (newEntry.myWhen <= entry.myWhen) {
                    break;
                }
                previous = entry;
                entry = entry.myNext;
            }
            if (previous == null) {
                newEntry.myNext = myTopEntry;
                myTopEntry = newEntry;
            } else {
                newEntry.myNext = previous.myNext;
                previous.myNext = newEntry;
            }
        }
    }

    /**
     * Sort the timer queue entries in order of time. Called when one or more
     * entries are altered.
     */
    private void orderEntries() {
        TimerQEntry newEntry = null;
        synchronized (myLock) {
            if (myTopEntry == null) {
                return;
            }
            while (myTopEntry != null) {
                TimerQEntry winner = myTopEntry;
                TimerQEntry entry = winner.myNext;
                TimerQEntry before = null;
                TimerQEntry prev = winner;
                while (entry != null) {
                    if (entry.myWhen > winner.myWhen) {
                        before = prev;
                        winner = entry;
                    }
                    prev = entry;
                    entry = entry.myNext;
                }
                if (before != null) {
                    before.myNext = winner.myNext;
                } else {
                    myTopEntry = winner.myNext;
                }
                winner.myNext = newEntry;
                newEntry = winner;
            }
        }
        myTopEntry = newEntry;
    }
    // behind, check them

    /**
     * Run the timer thread until told to stop.
     */
    public void run() {
        while (myRunning) {
            runloop();
        }
        myTopEntry = null;
    }

    /**
     * The actual guts of the timer thread: Look for the next event on the
     * timer queue. Wait until the indicated time. Process the event and any
     * others that may now be relevent. Repeat.
     */
    private void runloop() {
        long time;
        boolean reorder;
        FlexList notifies = null;
        TimerQEntry entry;
        TimerQEntry previous;

        synchronized (myLock) {
            if (myTopEntry != null) {
                time = (myTopEntry.myWhen - System.currentTimeMillis()) | 1;
                // Avoid 0 since will wait forever
            } else {
                time = 0;
            }
        }
        synchronized (myLock) {
            try {
                if (0 <= time) {
                    myLock.wait(time);
                }
            } catch (Exception e) {
                // No problem - something added or cancelled from queue
            }
        }

        synchronized (myLock) {
            // Only do the next bunch 'o stuff if this timer is still running
            if (myRunning) {
                // Timer fired, check each element to see if it is time
                long now = System.currentTimeMillis();
                boolean needToReorder = false;
                entry = myTopEntry;
                previous = null;
                while (entry != null) {
                    if (entry.myWhen <= now) {
                        if (notifies == null) {
                            notifies = FlexList.make(5);
                        }
                        notifies.push(entry);
                        if (entry.myRepeat) {
                            needToReorder = true;
                            entry.myWhen = entry.myWhen + entry.myDelta;
                            long deltoid = entry.myDelta * FUDGE;
                            if ((entry.myWhen + deltoid) < now) {
                                // Round up in increments of entry.myDelta to
                                // maintain timebase, but myDelta from "now"
                                // "now" being rounded up to the timebase
                                long dist =
                                  (now - entry.myWhen) + entry.myDelta;
                                dist = (dist / entry.myDelta) * entry.myDelta;
                                entry.myWhen = entry.myWhen + dist;
                            }
                            previous = entry;
                        } else {
                            // Remove it
                            if (previous == null) {
                                myTopEntry = entry.myNext;
                            } else {
                                previous.myNext = entry.myNext;
                            }
                        }
                        entry = entry.myNext;
                    } else {
                        break;
                    }
                }
                if (needToReorder) {
                    orderEntries();
                }
            }
        }

        if (!myRunning) {
            return;
        }

        // Enumerate over notifies and notify them
        if (notifies != null) {
            int count = notifies.size();
            int i = 0;
            while (myRunning && i < count) {
                entry = (TimerQEntry)notifies.get(i++);
                TimerWatcher target = entry.myTarget;
                try {
                    target.handleTimeout();
                } catch (Exception e) {
                    System.err.println("Exception in HandleTimeout meth");
                    ExceptionMgr.reportException(e);
                }
            }
        }
    }

    /**
     * Set a timeout event to happen.
     *
     * @param absMillis When the event should happen
     * @param target    Object which will handle the timeout event when it
     *                  occurs
     */
    void setAlarm(long absMillis, TimerWatcher target) {
        synchronized (myLock) {
            TimerQEntry entry = new TimerQEntry(absMillis, target);
            insertEntry(entry);
            if (myTopEntry == entry) {
                wakeup();
            }
        }
    }

    /**
     * Set a timeout event to happen.
     *
     * @param target      Object which will handle the timeout event when it
     *                    occurs
     * @param deltaMillis Distance into the future for event to happen
     */
    void setClock(TimerWatcher target, long deltaMillis) {
        synchronized (myLock) {
            TimerQEntry entry = new TimerQEntry(target, deltaMillis);
            insertEntry(entry);
            if (myTopEntry == entry) {
                wakeup();
            }
        }
    }

    /**
     * Stop the thread.
     */
    void shutdown() {
        synchronized (myLock) {
            myRunning = false;
            wakeup();
        }
    }

    /**
     * Wake up the sleeping runloop.
     */
    private void wakeup() {
        synchronized (myLock) {
            try {
                myLock.notify();
            } catch (Throwable t) {
                ExceptionMgr.reportException(t,
                                             "TimerThread.wakeup() caught exception on notify");
            }
        }
    }
}
