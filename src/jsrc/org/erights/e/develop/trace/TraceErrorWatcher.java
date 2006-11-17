package org.erights.e.develop.trace;

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
/*
 *  Trace and Logging Package. Written by Brian Marick,
 *  July-September 1997, for Electric Communities, Inc.
 */


/**
 * The interface for any object that wishes to be informed when a program error
 * has been logged. To register, the caller should call:
 * <p/>
 * TraceController.errorWatcher(this, true);
 * <p/>
 * To stop being informed, call:
 * <p/>
 * TraceController.errorWatcher(this, false);
 */
public interface TraceErrorWatcher {

    /**
     * Notify a user that a fatal error has happened. Tell her how to report
     * the bug. Does not return.
     */
    public void notifyFatal();

    /**
     * If the user wants to hear about nonfatal bugs, notify her. Does return.
     */
    public void notifyOptional();
}
