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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;


/**
 * This class shouldn't need to exist, but {@link System#out} and
 * {@link System#err} are both {@link PrintStream}s, and PrintStream is
 * deprecated in favor of {@link PrintWriter}.
 * <p/>
 * XXX How should one obtain a Writer or PrintWriter to stdout and stderr?
 * As long as we're at it, we also provide a static method for accessing
 * System.in as a non-buffering BufferedReader (so that readline() is
 * available).
 * <p/>
 * Note that there is no reason to use this class as a type in a type
 * declaration. No one should ever see any instances of it. Only its static
 * methods are externally useful.
 * <p/>
 * Note: As of some recent version of Java &lt;= 1.3, PrintStream is no longer
 * officially deprecated, probably because of System.out and System.err.
 * However, E's TextWriters still need to wrap Writers, not PrintStreams, so
 * we still need PrintStreamWriter.
 *
 * @author Mark S. Miller
 */
public final class PrintStreamWriter extends Writer {

    static private PrintStream lastOutStream = null;

    static private PrintWriter lastOutWriter = null;

    static private PrintStream lastErrStream = null;

    static private PrintWriter lastErrWriter = null;

    static private InputStream lastInStream = null;

    static private BufferedReader lastInReader = null;

    private final PrintStream myPS;

    private PrintStreamWriter(PrintStream ps) {
        super(ps);
        myPS = ps;
    }

    /**
     * Wrap the PrintStream in an adaptor so it seems to be a PrintWriter.
     */
    static public PrintWriter make(PrintStream ps) {
        return new PrintWriter(new PrintStreamWriter(ps));
    }

    /**
     * System.out as a PrintWriter. It's a function rather than a variable in
     * case System.out gets changed.
     */
    static public PrintWriter stdout() {
        if (System.out != lastOutStream) {
            lastOutStream = System.out;
            lastOutWriter = PrintStreamWriter.make(lastOutStream);
        }
        return lastOutWriter;
    }

    /**
     * System.err as a PrintWriter. It's a function rather than a variable in
     * case System.err gets changed.
     */
    static public PrintWriter stderr() {
        if (System.err != lastErrStream) {
            lastErrStream = System.err;
            lastErrWriter = PrintStreamWriter.make(lastErrStream);
        }
        return lastErrWriter;
    }

    /**
     *
     */
    static public BufferedReader stdin() {
        if (System.in != lastInStream) {
            lastInStream = System.in;
            lastInReader
            = new BufferedReader(new InputStreamReader(lastInStream), 1);
        }
        return lastInReader;
    }


    public void close() {
        myPS.close();
    }

    public void flush() {
        myPS.flush();
    }

    public void write(char[] cbuf, int off, int len) {
        myPS.print(new String(cbuf, off, len));
    }
}
