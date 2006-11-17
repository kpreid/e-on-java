package org.erights.e.elib.vat;

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
 * Makes the current Vat into a Thread-scoped variable.
 * <p/>
 * Since this is a thread, it must not be persistently reachable.
 * <p/>
 * XXX should not be public, we put {@link org.erights.e.ui.swt.SWTRunner} in a
 * separate package.
 *
 * @author Mark S. Miller
 */
public class RunnerThread extends Thread {

    /**
     *
     */
    final Runner myRunner;

    /**
     * @param runner Must also be a Runnable
     */
    public RunnerThread(Runner runner, String name) {
        super((Runnable)runner, name);
        myRunner = runner;
    }
}
