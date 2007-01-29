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

import org.erights.e.develop.exception.NestedException;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.util.ClassCache;

/**
 * Wraps the microsecond timer aspect of Native, but works in a degenerate
 * fashion, using pure Java's System.currentTimeMillis()*1000, if native code
 * is unavailable.
 *
 * @author Mark S. Miller
 */
public class MicroTime {

    static private Object nativeStatics = null;

    private MicroTime() {
    }

    static {
        //noinspection ErrorNotRethrown
        try {
            System.loadLibrary("ecutil");
            nativeStatics =
              StaticMaker.make(ClassCache.forName("net.vattp.security.Native"));
        } catch (UnsatisfiedLinkError ule) {
            //PrintStreamWriter.err().println("not using native timers: " +
            //                                ule);
        } catch (ClassNotFoundException cnf) {
            throw new NestedException(cnf, "# no Native");
        }
        initializeTimer();
    }

    /**
     * Initialize the high-res timer.
     */
    static public void initializeTimer() {
        if (null != nativeStatics) {
            E.call(nativeStatics, "initializeTimer");
        }
    }

    /**
     * Find whether we have a high-res timer.
     *
     * @return true if we have a high-res timer, false if we are simulating.
     */
    static public boolean isHiRes() {
        return null != nativeStatics;
    }

    /**
     * Get the current value of the high-res timer in microseconds.
     *
     * @return the current value of the high-res timer in microseconds.
     */
    static public long queryTimer() {
        if (null != nativeStatics) {
            return ((Number)E.call(nativeStatics, "queryTimer")).longValue();
        } else {
            return System.currentTimeMillis() * 1000;
        }
    }
}
