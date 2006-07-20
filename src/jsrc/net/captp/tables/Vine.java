package net.captp.tables;

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
import org.erights.e.elib.serial.PassByProxy;


/**
 * An object to hold onto a remote reference so it will be held onto until we
 * grab the next "vine".
 * <p/>
 * When you're swinging through the jungle, it's important to grab the new
 * vine before you let go of the old one. Part of the 3-party live introduction
 * mechanism.
 */
public class Vine implements PassByProxy, Runnable {

    /**
     * The remote reference we hold onto.
     * <p/>
     * All we do is hold onto it. Really.
     */
    private final Object myRemote;

    /**
     * Constructor.
     */
    public Vine(Object remote) {
        if (Trace.captp.debug && Trace.ON) {
            Trace.captp.debugm(this + " created to hold " + remote);
        }
        myRemote = remote;
    }

    /**
     * When asked to run(), we do nothing; really. <p>
     * <p/>
     * This way we can be scheduled to hold on to myRemote until we're run()
     * and dropped.
     */
    public void run() {
    }
}
