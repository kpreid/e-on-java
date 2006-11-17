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

import java.io.CharArrayWriter;
import java.io.PrintWriter;

/**
 * This class finds the user method that posted a trace message and provides
 * accessors to useful information.
 * <p/>
 * DANGER: it is HIGHLY dependent on the particular way the implementation
 * prints stack traces. The installation tests ($ROOT/Install) help you check
 * if your VM does things differently. If you change this code, please update
 * the tests (=Tests/TestTraceCaller.java).
 * <p/>
 * For reference, here's the current expected format. (There's more about this
 * in the code.)
 * <pre>
 * java.lang.Exception
 *  at Trace.debugm(Trace.java:217)
 *  at Test.go(Test.java:25)
 *  at Test.main(Test.java:20)
 * </pre>
 * <p/>
 * Stack frames from jit-ed code (compiled on the fly) look like: at
 * Trace.debugm(Compiled Code) or at Trace.debugm(TraceBuffer.java, Compiled
 * Code)
 * <p/>
 * Some VMs use <> pairs instead of parentheses. Some seem to use tabs (?)
 * <p/>
 * If the implementation runs into an odd format, it should leave accessors
 * it's not sure of with their initial values.
 */
public class TraceCaller {

    /**
     * The name of the method running in the targeted frame. It is partly
     * qualified, consisting of the last element of the classname plus the
     * method name (for example, "String.indexOf").
     */
    public String methodName = "method?";

    /**
     * The file that method is in. The full pathname is not available.
     */
    public String fileName = "file?";

    /**
     * The line number the trace call is on.
     */
    public String lineNumber = "line?";

    /**
     * Collect an earlier frame's data based on data in the Exception. If
     * aboveCount is 0, the data from the frame that created the exception is
     * collected. If 1, the data is from the frame above that, and so on.
     * <p/>
     * Leaves fields set to their to original "?" values if it can't parse the
     * stack.
     */
    public TraceCaller(Exception exception) {
        String dump = getStackDump(exception);
        if (TraceController.OurDebugTraceCaller) {
            System.err.println(dump);
        }
        parse(dump);
    }

    /**
     * String dump is a stack dump as retrieved with getStackDump. Construct
     * the frame data as in previous constructor. This is used for testing
     * (which is why it's public).
     */
    public TraceCaller(String dump) {
        parse(dump);
    }

    /*
     * Return the stack dump contained in an exception as a String.
     */
    private String getStackDump(Exception exception) {
        // I could make this static, but then I'd have to worry
        // about multiple threads.
        CharArrayWriter charSink = new CharArrayWriter(400);

        PrintWriter writer = new PrintWriter(charSink);

        exception.printStackTrace(writer);
        return charSink.toString();
    }

    /**
     * Delimit the beginning and end of a line in a larger string.
     */
    private class Line {

        private int start = -1;

        private int pastEnd = -1;
    }

    private void parse(String dump) {
        try {
            // System.err.println(dump);
            parseLine(dump, findTargetLineBounds(dump));
        } catch (Throwable e) {
            // Most likely, the stack frame format has changed in an
            // unexpected way. Leave what hasn't been set yet with
            // question marks.
        }
    }

    /*
    * Find the boundaries of the line corresponding to the
    * stack frame we care about in stackDump. Return as a Line object.
    * Throw exception if parsing fails.
    *
    * The target frame is two above the first one containing the string
    * recordTraceMessageXyzzY. EXCEPT: at least one VM will mislabel
    * the frame below the target like this:
    *     recordTraceMessageXyzzY  // not really its name
    *     recordTraceMessageXyzzY  // the one we really want.
    *     errorm                   // or some other trace call.
    *     callerOfErrorm
    *
    * That same VM sometimes produces THIS:
    *  recordTraceMessageXyzzY
    *  java.lang.Exception.<init>
    *  TraceMessage.<init>
    *  recordTraceMessageXyzzY
    *  Trace.worldm
    *  Threadzilla.run
    *
    * Doesn't this totally lose? For more examples of known lossage,
    * see the unit tests.
    */

    private final String targetMethod = "recordTraceMessageXyzzY";

    private Line findTargetLineBounds(String stackDump) throws Exception {
        int middle = stackDump.indexOf(targetMethod);
        quitIf(middle == -1);

        Line line = lineFromPoint(stackDump, middle);

        // Check if the targetMethod frame appears closer to the top
        // (newest frame) of the stack than the TraceMessage
        // constructor. "1.2.2" Classic VM (build JDK-1.2.2-001,
        // native threads, symcjit) on Windows does that
        // sometimes. Since the constructor calls nothing, such a
        // frame is totally bogus and should be skipped.

        int possibleDuplicate =
          stackDump.indexOf("TraceMessage.<init>", middle);
        if (possibleDuplicate != -1) {
            middle = stackDump.indexOf(targetMethod, possibleDuplicate);
            quitIf(middle == -1);
            line = lineFromPoint(stackDump, middle);
        }

        // Look for immediate duplicate of the targetMethod. In the
        // 1.2.2 Classic VM, the frame for the TraceMessage
        // constructor is mislabelled as another instance of the
        // targetMethod.
        possibleDuplicate = stackDump.indexOf(targetMethod, line.pastEnd + 1);
        if (possibleDuplicate != -1 &&
          stackDump.lastIndexOf('\n', possibleDuplicate) == line.pastEnd) {
            line = lineFromPoint(stackDump, possibleDuplicate);
        }

        advance(stackDump, line);
        advance(stackDump, line);

        return line;
    }

    /* This method is given an int32 pointing to the middle of a
     * line. It finds the beginning and "past end" - using newlines as
     * the delimiters.
     */
    private Line lineFromPoint(String stackDump, int middle) throws Exception {
        Line retval = new Line();

        retval.start = stackDump.lastIndexOf('\n', middle);
        quitIf(retval.start == -1);
        retval.start++;

        retval.pastEnd = stackDump.indexOf('\n', middle);
        quitIf(retval.pastEnd == -1);

        return retval;
    }

    /**
     * Destructively move the line forward to the next line.
     */
    private void advance(String stackDump, Line line) throws Exception {
        line.start = line.pastEnd + 1;
        quitIf(line.start >= stackDump.length());

        line.pastEnd = stackDump.indexOf('\n', line.pastEnd + 1);
        // It's OK for the very last frame in the stack not to have a
        // newline terminator.
        if (line.pastEnd == -1) {
            line.pastEnd = stackDump.length();
        }
    }


    /*
     * Copy out the constituent parts from the line delimited by the
     * Line. Throw Exception on parse failure. Parsing is not very
     * sophisticated. I expect installers to check their own stack
     * frame format and fix this up as needed.
     */
    private void parseLine(String stackDump, Line line) throws Exception {
        /* We're looking at:
           at Trace.Test.main(Test.java:21)
        */
        int start = line.start + 4;        // skip "\tat "
        quitIf(start >= line.pastEnd);

        /* We're looking at:
           Trace.Test.main(Test.java:21)
           First, find the parens and embedded colon (or equivalent,
           depending on your VM.
        */

        int openParen = stackDump.indexOf('(', start);
        // XXX Hack for Metrowerks and Microsoft VM -bs
        if (openParen == -1 || openParen >= line.pastEnd) {
            openParen = stackDump.indexOf('<', start);
        }
        if (openParen == -1 || openParen >= line.pastEnd) {
            openParen = stackDump.indexOf('\t', start);
        }
        quitIf(openParen == -1 || openParen >= line.pastEnd);

        int colon = stackDump.indexOf(':', openParen);
        if (colon == -1 || colon >= line.pastEnd) {
            colon = stackDump.indexOf(',', openParen);
        }
        quitIf(colon == -1 || colon >= line.pastEnd);

        int closeParen = stackDump.indexOf(')', colon);
        // XXX Hack for Metrowerks and Microsoft VM -bs
        if (closeParen == -1 || closeParen >= line.pastEnd) {
            closeParen = stackDump.indexOf('>', colon);
        }
        if (closeParen == -1 || closeParen >= line.pastEnd) {
            closeParen = stackDump.indexOf('\t', colon);
        }
        // If we have an open delimiter but no close one, it's
        // probably friendliest just to suck up the rest of the line.
        if (closeParen == -1 || closeParen >= line.pastEnd) {
            closeParen = line.pastEnd;
        }

        // Note in the above that if the filename/linenumber are
        // screwed up, you lose the chance to collect the
        // methodname. Since the parsing is simple-minded, a
        // detected error in the filename might well accompany an
        // UNdetected error in the methodname. Better just to be
        // silent.
        //
        // So, now that the line looks reasonably sane, start picking
        // out components.

        methodName = stackDump.substring(start, openParen); // fully qualified.

        /* Fetch fully qualified classname. */
        int classMethodDot = methodName.lastIndexOf(".");
        // no longer used...
        // className = methodName.substring(0, classMethodDot);

        /* Strip methodname down to last element of classname + method. */
        int classDot = methodName.lastIndexOf(".", classMethodDot - 1);
        methodName = methodName.substring(classDot + 1);

        if (colon > 0) {
            // normal bytecodes.
            fileName = stackDump.substring(openParen + 1, colon);

            // A jit might leave the linenumber as "Compiled Code".
            String maybeLineNumber =
              stackDump.substring(colon + 1, closeParen);
            if (maybeLineNumber.charAt(0) > '0' &&
              maybeLineNumber.charAt(0) <= '9') {
                lineNumber = maybeLineNumber;
            }
        }
        // else jit-compiled:  leave fileName and lineNumber
        // with question marks.
    }

    // Throwing an exception in the middle of parsing will leave
    // public instance variables in their 'line?' form.
    // Please inline this, Mr. Compiler.
    private void quitIf(boolean b) throws Exception {
        if (b) {
            throw new Exception();
        }
    }

    public String toString() {
        return fileName + ":" + methodName + ":" + lineNumber;
    }
}
