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

import java.util.Date;

/**
 * This class describes a trace message. Its final destination could
 * be an on disk log (called "the log"), or an in-core buffer (called
 * "the buffer") to be retained until the user asks for it.
 */
class TraceMessage {

    // DANGER                                                   DANGER
    // If you add new data members, please note that TraceBuffer.java
    // constructs its own private TraceMessages (in unpostedTraceMessage).
    // That code may also need to be updated.
    // DANGER                                                   DANGER

    /**
     * The subsystem is the larger body of code the message applies to.
     */
    public String subsystem;

    /**
     * The Date at which the method was sent (approximately). The Date
     * is not attached to the message until the message leaves the vat.
     * (The vat does not let you find out the current time.)
     */
    public Date date;

    /**
     * The level ("error", "debug", etc.) at which the message was
     * sent. This is distinct from the priority threshold that
     * determines whether the message should be sent.
     */
    public int level;

    /**
     * The text itself.
     */
    public String message;

    /**
     * An arbitrary object may be attached to the message.
     * They are usually printed with toString(), but Throwables
     * are handled specially.
     */
    public Object object;

    /**
     * An exception is generated when the message is created.
     * This is used to discover the class name, method name,
     * file name, and line number.
     */
    public final Exception exception;

    /**
     * The next TraceMessage in a linked list.
     */
    public TraceMessage next;

    TraceMessage() {
        exception = new Exception();
    }

    /**
     * Default string representation displays all fields.
     */
    public String toString() {
        return new TraceMessageStringifier().toString(this);
    }
}
