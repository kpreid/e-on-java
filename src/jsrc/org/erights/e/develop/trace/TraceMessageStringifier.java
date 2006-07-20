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

import org.erights.e.develop.exception.ExceptionMgr;

import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * This class is used to convert a trace message into a string. It
 * is used (as opposed to toString()) when certain of the fields need
 * to be omitted.
 * <p/>
 * Future:  IFC might allow the objects contained in trace messages to
 * be displayed as special glyphs, even clickable buttons.
 * <p/>
 * By default, everything is shown.
 */
class TraceMessageStringifier {

    // See the setter methods for the meanings of these booleans.
    private boolean myShowDate = true;

    private boolean myShowTime = true;

    private boolean myShowLocation = true;

    private boolean myShowSubsystem = true;

    private boolean myShowLevel = true;

    private final String myLineSeparator = System.getProperty("line.separator");

    // A minor concession to efficiency, as this is critical path.
    // Used in toString.
    private final StringBuffer myBuffer = new StringBuffer(200);

    /**
     * Should the message include the date at which the
     * message was created?
     */

    void showDate(boolean value) {
        myShowDate = value;
    }

    /**
     * Should the message include an abbreviated description of
     * the level at which the message was posted?
     */

    void showLevel(boolean value) {
        myShowLevel = value;
    }

    /**
     * Should the message include the method name, file name, and
     * line number?
     */

    void showLocation(boolean value) {
        myShowLocation = value;
    }

    /**
     * Should the message include the subsystem name?
     */

    void showSubsystem(boolean value) {
        myShowSubsystem = value;
    }

    /**
     * Should the message include the time at which the
     * message was created?
     */

    void showTime(boolean value) {
        myShowTime = value;
    }

    /**
     * Convert the given message into a string, obeying 'show'
     * controls set earlier.
     */
    String toString(TraceMessage message) {
        myBuffer.setLength(0);
        myBuffer.append("=== ");
        //MSM: XXX should merge these flags now that we've merged their meaning
        if (myShowDate || myShowTime) {
            myBuffer.append(TraceDateToString.dateTimeString(message.date));
            myBuffer.append(' ');
        }
        if (myShowLocation) {
            if (TraceController.OurDebugTraceCaller) {
                System.err.println("****************************************");
                System.err.println(message.subsystem + ": " + message.message);
            }
            TraceCaller frameData = new TraceCaller(message.exception);
            myBuffer.append('(');
            myBuffer.append(frameData.methodName);
            myBuffer.append(':');
            myBuffer.append(frameData.fileName);
            myBuffer.append(':');
            myBuffer.append(frameData.lineNumber);
            myBuffer.append(") ");
        }

        if (myShowLevel) {
            myBuffer.append(TraceLevelTranslator.terse(message.level));
        }

        // Experimenting with multi-line output.
        if (myShowDate || myShowTime || myShowLocation) {
            myBuffer.append(myLineSeparator);
        }

        // note: subsystem must be delimited because it might
        // contain blanks.
        if (myShowSubsystem) {
            myBuffer.append(message.subsystem);
            myBuffer.append(": ");
        }
        myBuffer.append(message.message);

        //This guarding try/catch fixes. See
        // bugs.sieve.net/bugs/?func=detailbug&bug_id=125515&group_id=16380
        try {
            if (message.object != null) {
                if (message.object instanceof Throwable) {
                    StringWriter sw = new StringWriter();
                    PrintWriter stream = new PrintWriter(sw);
                    //XXX
                    ExceptionMgr.printStackTrace((Throwable)message.object,
                                                 stream);
                    String stack = sw.getBuffer().toString();
                    myBuffer.append(myLineSeparator);
                    myBuffer.append(stack);
                } else {
                    myBuffer.append(" : ");
                    myBuffer.append(message.object);
                }
            }
        } catch (Throwable nest) {
            myBuffer.append("*** nested exception " + nest.getClass() +
                          " tracing a " + message.object.getClass());
        }
        return myBuffer.toString();
    }
}
