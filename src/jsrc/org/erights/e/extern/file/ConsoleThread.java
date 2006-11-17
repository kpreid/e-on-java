package org.erights.e.extern.file;

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

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.prim.E;

import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * Implements a seperate thread to manage the user console (i.e., the keyboard
 * and tty). This needs to be in its own thread so that the user pondering what
 * to type next doesn't block the entire E runQ! <p>
 * <p/>
 * The console is given an input handler when it is created, which is an object
 * that implements the ConsoleInputHandler interface. Each line of input typed
 * on the console is sent as a String in an E-message to the input handler. End
 * of file is signalled to the input handler by sending it a null. If a problem
 * is thrown while reading, the handler is asked to handle it as well.
 */
public class ConsoleThread implements Runnable {

    /**
     *
     */
    private final Object myLock = new Object();

    private final ConsoleInputHandler myHandler;

    private BufferedReader myIn;

    private PrintWriter myOptOut;

    private boolean nowait;

    /**
     * Constructs a new console object given a handler and the console input
     * and output, then starts the thread.
     *
     * @param handler The E object that is to receive input lines.
     * @param in      The console input source.
     * @param out     The console output sink
     */
    ConsoleThread(ConsoleInputHandler handler,
                  BufferedReader in,
                  PrintWriter optOut) {
        myHandler = handler;

        T.notNull(in, "must have input Reader");
        myIn = in;
        /* On Solaris, need to check available() before reading or
            else the whole process will block! */
        String osname = System.getProperty("os.name");
        if (osname.equals("Solaris")) {
            nowait = false;
        } else {
            nowait = true;
        }

        myOptOut = optOut;

        Thread theThread = new Thread(this);
        theThread.setDaemon(true);
        theThread.start();
    }

    /**
     * This is the actual thread code for the ConsoleThread.
     */
    public void run() {
        /*
         * Read lines from myIn in a loop, only exiting on error or EOF.
         * <p>
         * Lines are echoed on myOptOut if myOptOut is not null, and
         * then sent to the object held by myHandler in an E-message.
         * EOF is signalled to the handler by sending it null instead
         * of a line, which, *sigh*, appears to be the way
         * DataInputStream.readLine() handles EOF.
         * <p>
         * If the constructor had determined that myIn is something that can
         * block the entire process and not just this thread (which,
         * appallingly, can actually happen due to a Solaris bug), we poll
         * every 200ms rather than just blindly reading.
         */
        try {
            while (true) {
                if (nowait || myIn.ready()) {
                    String line = myIn.readLine();
                    if (myOptOut != null) {
                        myOptOut.println(line);
                    }
                    E.sendOnly(myHandler, "handleInput", line);
                    if (line == null) {
                        return;
                    } else {
                        Thread.yield();
                    }
                } else {
                    synchronized (myLock) {
                        try {
                            myLock.wait(200);
                        } catch (Exception e) {
                            /* If we're tickled, just keep going */
                        }
                    }
                }
            }
        } catch (Throwable ex) {
            /* XXX Arguably we should do something smarter here. */
            E.sendOnly(myHandler, "handleProblem", ex);
        }
    }
}
