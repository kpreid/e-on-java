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

import java.io.PrintWriter;

/**
 *
 */
public class ExceptionMgr {

    /**
     *
     */
    static private ExceptionNoticer OurNoticer = null;

    private ExceptionMgr() {
    }

    /**
     * Returns 'th' as a RuntimeException.
     * <p/>
     * Wraps 'th' if necessary so that the caller can do a
     * <pre>    throw ExceptionMgr.asSafe(th);</pre>
     * without having to declare any new "throws" cases. The caller does the
     * "throw" rather than this construct so that the Java compiler will have
     * better control flow information.
     * <p/>
     * Equivalent to 'ThrowableSugar.backtrace(th, "");'
     */
    static public RuntimeException asSafe(Throwable th) {
        if (th instanceof RuntimeException) {
            return (RuntimeException)th;
        }
        return new NestedException(th, "");
    }

    /**
     *
     */
    static public void printStackTrace(Throwable t, PrintWriter out) {
        Throwable leaf = ThrowableSugar.leaf(t);
        out.println("--vvvv--");
        out.println(leaf.getMessage());
        out.println(ThrowableSugar.eStack(t));
        out.println();
        out.println(ThrowableSugar.javaStack(leaf));
        out.println("--^^^^--");
    }

    /**
     *
     */
    static public void reportException(Throwable t) {
        reportException(t, "");
    }

    /**
     *
     */
    static public void reportException(Throwable t, String msg) {
        if (OurNoticer != null) {
            OurNoticer.noticeReportedException(msg, t);
        } else {
            System.err.println(msg);
            printStackTrace(t, PrintStreamWriter.stderr());
        }
    }

    /**
     *
     */
    static public void setExceptionNoticer(ExceptionNoticer noticer) {
        if (null != OurNoticer) {
            throw new SecurityException(
              "cannot reset ExceptionMgr exception noticer");
        }
        OurNoticer = noticer;
    }

    /**
     *
     */
    static public void uncaughtException(Thread t, Throwable e) {
        if (!(e instanceof ThreadDeath)) {
            String msg = "Uncaught exception in thread " + t.getName();
            reportException(e, msg);
            if (OurNoticer != null) {
                OurNoticer.noticeUncaughtException(msg, e);
            }
        }
    }
}
