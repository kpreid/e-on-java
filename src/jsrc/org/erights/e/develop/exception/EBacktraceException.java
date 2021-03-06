package org.erights.e.develop.exception;

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
 * Backtrace version of the Java RuntimeException class.
 * 
 * This is identical to RuntimeException except that it indicates that this
 * exception should be unwrapped as part of the E backtrace-reporting
 * mechanisms, and its message follows the convention described in 
 * {@link EBacktraceThrowable}.
 */
public class EBacktraceException extends RuntimeException
  implements EBacktraceThrowable {

    static private final long serialVersionUID = 2636038350623448267L;

    /**
     * See {@link EBacktraceThrowable} for the convention that msg should
     * follow.
     */
    public EBacktraceException(Throwable t, String msg) {
        super(msg, t);
    }
}
