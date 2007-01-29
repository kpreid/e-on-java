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

import org.erights.e.develop.trace.Trace;


/**
 * Use clock phase differences between 2 PC clocks to generate entropy.
 * <p/>
 * <p>This class provides a cryptographically strong random number generator
 * based on user provided sources of entropy and the MD5 hash algorithm. It
 * maintains an estimate of the amount of entropy supplied. If there is a
 * request for secure random data, a call on nextBytes(), when there is less
 * than 80 bits of randomness available, it will use super.getSeed() to bring
 * the level up to 80 bits.
 * <p/>
 * <p>The calls inherited from Random and SecureRandom are implemented in terms
 * of the strengthened functionality.
 *
 * @author Bill Frantz
 * @see java.util.Random
 * @see java.security.SecureRandom
 */
class TimerJitterEntropy extends Thread {

    private boolean theIsStarted = false;

    TimerJitterEntropy(String name) {
        super(name);
        if ("x86".equals(System.getProperty("os.arch")) &&
          MicroTime.isHiRes()) {
            setDaemon(true);
            setPriority(Thread.MAX_PRIORITY);
            theIsStarted = true;
        } else {
            Trace.entropy
              .eventm("Resources for TimeJitterEntropy not available " +
                ("x86".equals(System.getProperty("os.arch"))) + " " +
                (MicroTime.isHiRes()));
        }
        start();
    }

    public boolean isStarted() {
        return theIsStarted;
    }

    public final void run() {

        byte[] ran = new byte[1];
        int bitPtr = 0;
        long entropy = 0;

        int delay = 50;

        if (!theIsStarted) {
            return;
        }

        while (true) {
            try {
                Thread.sleep(delay);    // Cut the CPU overhead by waiting for
                // most of the PC tick interval.
            } catch (InterruptedException e) {
                return;
            }
            long last = System.currentTimeMillis();
            long now = System.currentTimeMillis();
            // Hang out until the TOD clock ticks
            while (now == last) {
                now = System.currentTimeMillis();
            }
            long nowHR = MicroTime.queryTimer();
            ran[0] |= ((nowHR & 1) << bitPtr);
            bitPtr++;
            if (8 == bitPtr) {
                // Assume 1/2 bit entropy/sample
                ESecureRandom.provideEntropy(ran, 4);
                entropy += 4;       // Incr entropy gotten
                if (160 == entropy) { // Slow down after 160 bits
                    Trace.entropy.eventm("Slow down point reached");
                    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                    delay = 500;
                }
                ran[0] = 0;
                bitPtr = 0;
            }
        }
    }
}
