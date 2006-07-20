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
 * This interface represents objects that accept messages and do
 * something useful with them. A TraceMessageAcceptor lives in two
 * main states: prior to its starting environment being set up, and
 * after that environment is set up. A TraceMessageAcceptor <i>may</i>
 * choose to accept messages prior to the completion of setup, but it
 * may not make them available to a user.
 */

interface TraceMessageAcceptor {

    /**
     * Accept a message and do whatever is appropriate to make it
     * visible to a user, either now or later.
     * <p/>
     * Note that this method should be called AFTER the message passes
     * a priority threshold check. MessageAcceptors don't know about
     * priorities.
     * <p/>
     * The TraceMessageAcceptor is allowed to unilaterally discard
     * the message. Generally, this is done only if it was turned off
     * by another thread.
     */
    void accept(TraceMessage message);

    /**
     * After this call, the TraceMessageAcceptor must obey settings from
     * the environment. Before this call, it must defer taking any
     * visible action, because it can't yet know what action is appropriate.
     * Note that the message acceptor may (is encouraged to) accept messages
     * before setup is complete, because some of those trace messages might
     * be useful.
     * <p/>
     * It is an error to call this method more than once.
     */
    void setupIsComplete();
}
