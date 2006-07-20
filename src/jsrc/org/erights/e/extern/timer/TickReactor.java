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

/**
 * The TickReactor interface is implemented by classes that want to handle
 * repeated ticks from a Clock.
 *
 * @see org.erights.e.extern.timer.Timer#every
 * @see org.erights.e.extern.timer.Clock
 */
public interface TickReactor {

    /**
     * Called by Clocks on their targets after each tick.
     *
     * @param ticks Number of ticks since this clock was started
     */
    void run(long tick);
}
