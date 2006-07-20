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

import org.erights.e.develop.format.StringHelper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * A sweetener defining extra messages that may be e-sent to a Throwable
 *
 * @author Mark S. Miller
 */
public class ThrowableSugar {

    /**
     * the standard "problem: " prefix
     */
    static public final String ProblemPrefix = "problem: ";

    /**
     * Same number of spaces as in the ProblemPrefix
     */
    static public final String ProblemIndent =
      StringHelper.multiply(" ", ProblemPrefix.length());

    /**
     * prevents instantiation
     */
    private ThrowableSugar() {
    }

    /**
     * In general, a Throwable prints as 'problem: <type: msg>'.
     * <p/>
     * If the msg is null, then prints as just 'problem: <type>'. If msg
     * contains multiple lines, the remaining lines are indented to line up
     * with the "<type:...".
     * <p/>
     * If self is an instance of RuntimeException, but not of any subclass of
     * RuntimeException, then it prints as 'problem: msg' (since the type
     * RuntimeException isn't interesting). This behavior really should be
     * in a separate RuntimeExceptionSugar class, but is placed here to avoid
     * creating that sugar class for only this purpose.
     * <p/>
     * This really should be
     * {@link org.erights.e.elib.oldeio.EPrintable#__printOn __printOn}, but
     * because of layering issues, we rename it and make a special case in the
     * implementation of
     * {@link org.erights.e.elib.prim.MirandaMethods#__printOn
     * MirandaMethods.__printOn}.
     */
    static public void printThrowableOn(Throwable self, Writer out) {
        PrintWriter pw = new PrintWriter(out);
        pw.print(ProblemPrefix);

        self = leaf(self);
        Class type = self.getClass();
        String optMsg = self.getMessage();
        if (null != optMsg) {
            optMsg = StringHelper.replaceAll(optMsg,
                                             "\n",
                                             "\n" + ProblemIndent);
        }
        if (RuntimeException.class == type && null != optMsg) {
            pw.print(optMsg);
        } else {
            String name = type.getName();
            pw.print("<" + name.substring(name.lastIndexOf('.') + 1));
            if (null != optMsg) {
                pw.print(": " + optMsg);
            }
            pw.print(">");
        }
    }

    /**
     * Returns the Throwable wrapped by self.
     * <p/>
     * If self doesn't wrap a Throwable, return null.
     */
    static public Throwable unwrap(Throwable self) {
        if (self instanceof NestedThrowable) {
            return ((NestedThrowable)self).getNestedThrowable();
        } else if (self instanceof ExceptionInInitializerError) {
            return ((ExceptionInInitializerError)self).getException();
        } else if (self instanceof InvocationTargetException) {
            return ((InvocationTargetException)self).getTargetException();
        } else if (self instanceof UndeclaredThrowableException) {
            return ((UndeclaredThrowableException)self).
              getUndeclaredThrowable();
        } else {
            return null;
        }
    }

    /**
     * Return the non-wrapping throwable at the end of a wrapping chain
     */
    static public Throwable leaf(Throwable self) {
        while (true) {
            Throwable sub = unwrap(self);
            if (null == sub) {
                return self;
            }
            self = sub;
        }
    }

    /**
     * Returns the backtrace annotations wrapping the leaf exception, one per
     * line. Empty backtrace annotations are skipped.
     * Each line is *preceded* by a newline.
     */
    static public String eStack(Throwable self) {
        String result = "";
        while (true) {
            Throwable sub = unwrap(self);
            if (null == sub) {
                return result;
            }
            String msg = self.getMessage();
            if (msg != null && msg.length() >= 1) {
                result = "\n" + msg + result;
            }
            self = sub;
        }
    }

    /**
     * Returns the java backtrace stack of the leaf throwable with all
     * newlines as '\n's.
     */
    static public String javaStack(Throwable self) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        leaf(self).printStackTrace(pw);
        return StringHelper.canonical(sw.getBuffer().toString());
    }
}
