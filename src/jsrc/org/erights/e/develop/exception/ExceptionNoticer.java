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
 * Interface to be implemented by the entity that is to be notified of all
 * exceptions reported to ExceptionMgr or which are uncaught.
 *
 * @see org.erights.e.develop.exception.ExceptionMgr
 */
public interface ExceptionNoticer {

    /**
     * Notification of a reported exception.
     *
     * @param msg The message that accompanied the exception report
     * @param t   The actual exception itself.
     */
    public void noticeReportedException(String msg, Throwable t);

    /**
     * Notification of an uncaught exception.
     *
     * @param msg Message describing the circumstances
     * @param t   The actual exception itself.
     */
    public void noticeUncaughtException(String msg, Throwable t);
}
