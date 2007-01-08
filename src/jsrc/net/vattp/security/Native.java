package net.vattp.security;

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

import org.erights.e.develop.exception.PrintStreamWriter;

import java.text.DecimalFormat;

//import org.erights.e.elib.util.URLLaunchThread;

/**
 * A class to hold all the miscellaneous system-dependent utilities Microcosm
 * might need.
 *
 * @author Walendo
 */
class Native {

    /** @noinspection StaticNonFinalField*/
    static private boolean isLinked = false;

    static {
        //noinspection ErrorNotRethrown
        try {
            System.loadLibrary("ecutil");
            isLinked = true;
        } catch (UnsatisfiedLinkError e) {
            PrintStreamWriter.stderr()
              .println("ecutil linkage error: " + e.getMessage());
        }
        //noinspection NonFinalStaticVariableUsedInClassInitialization
        if (isLinked) {
            initializeTimer();
        }

        // We no longer register the PID at initialization.
        // uCosm should do it explicitly.
    }

    static private final DecimalFormat myDecimalFormat =
      new DecimalFormat("0000000000000000");

    private Native() {
    }

    static public long deltaTimerMSec(long start) {
        return (queryTimer() - start) / 1000;
    }

    static public long deltaTimerMSec(long start, long end) {
        return (end - start) / 1000;
    }

    static public long deltaTimerSec(long start) {
        return (queryTimer() - start) / 1000000;
    }

    static public long deltaTimerSec(long start, long end) {
        return (end - start) / 1000000;
    }

    // Immediate Utils (use the current clock as stoptimed)
    static public long deltaTimerUSec(long start) {
        return queryTimer() - start;
    }

    // Utils
    static public long deltaTimerUSec(long start, long end) {
        return end - start;
    }

    /**
     * Set the working set of this process to 0. This causes the OS to reload
     * pages as they're touched. Only for NT/95.
     */
    static private native int flushWorkingSet();

    /**
     * Return a long in a preformatted way, suitable for sorting.
     */
    static public String format(long time) {
        return myDecimalFormat.format(time);
    }

    /**
     * Return the time in a preformatted way, suitable for sorting.
     */
    static public String formatNow() {
        return myDecimalFormat.format(queryTimer());
    }

    static private native Object fromWeakCell(int weakcell);

    static public int getPhysicalMemorySize() {
        return getPhysicalMemorySizeNative();
    }

    static private native int getPhysicalMemorySizeNative();

    /**
     * Dump the threads. Equivalent to hitting Ctrl-Pause (or Break).
     */
    //    static public synchronized native void dumpThreads();

    // high-res timer code

    /**
     * Initialize the high-res timer.
     */
    static private native void initializeTimer();

    /**
     * Get the current value of the Pentium Performance counter.
     *
     * @return the current value of the Pentium Performance counter.
     */
    static private native long queryPerformanceCounter();

    /**
     * Get the current value of the high-res timer in microseconds.
     *
     * @return the current value of the high-res timer in microseconds.
     */
    static private native long queryTimer();

    /**
     *
     */
    static private native int toWeakCell(Object obj);
}
