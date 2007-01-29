package org.erights.e.elib.oldeio;

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

import org.erights.e.develop.exception.ThrowableSugar;
import org.erights.e.develop.format.StringHelper;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.base.Ejection;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.tables.FlexMap;
import org.erights.e.elib.tables.TraversalKey;
import org.erights.e.elib.tables.Twine;
import org.erights.e.meta.java.lang.CharacterSugar;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Wraps an underlying Writer to add these enhancements: <p>
 * <p/>
 * Writes all strings with "\n" replaced with the 'newline' string (typically
 * "\n" followed by zero or more spaces). <p>
 * <p/>
 * If the *print*(obj) methods detect they are asked to print an object they
 * are already in the middle of printing, they print "***CYCLE***" instead.
 * <p>
 * <p/>
 * Asks the object to print itself with E.call(obj, "__printOn", this) rather
 * than String.valueOf(obj). <p>
 *
 * @author Mark S. Miller
 */
public final class TextWriter extends FilterWriter {

    static private final String DEFAULT_PREFIX = "    ";

    private final String myNewline;

    private final boolean myAutoflush;

    private final boolean myCloses;

    private final FlexMap myContext;

    /**
     * Returns a pair of a TextWriter and the StringBuffer it writes into.
     */
    static public Object[] makeBufferingPair() {
        StringWriter sw = new StringWriter();
        TextWriter tw = new TextWriter(sw);
        StringBuffer sb = sw.getBuffer();
        Object[] result = {tw, sb};
        return result;
    }

    /**
     * Initial newline defaults to "\n". autoflush and closes default to
     * false.
     */
    public TextWriter(Writer out) {
        this(out, "\n", false, false, null);
    }

    /**
     * Initial newline defaults to "\n". closes default to false.
     */
    public TextWriter(Writer out, boolean autoflush) {
        this(out, "\n", autoflush, false, null);
    }

    /**
     * Returns a writer that wraps 'out', and writes all strings with "\n"
     * replaced with the 'newline' string. <p>
     * <p/>
     * If autoflush, flush()es after outputting each newline.
     */
    public TextWriter(Writer out,
                      String newline,
                      boolean autoflush,
                      boolean closes,
                      FlexMap optContext) {
        super(out);
        myNewline = newline;
        myAutoflush = autoflush;
        myCloses = closes;
        if (null == optContext) {
            myContext = FlexMap.fromTypes(TraversalKey.class, Void.class);
        } else {
            myContext = optContext;
        }
    }

    /**
     * XXX Does NOT close the underlying stream, since that might be separately
     * accessed.
     * <p/>
     * Does flush and invalidate this TextWriter.
     */
    public void close() throws IOException {
        try {
            flush();
            if (myCloses) {
                out.close();
            }
        } finally {
            out = null;
        }
    }

    /**
     * morePrefix defaults to four spaces
     */
    public TextWriter indent() {
        return indent(DEFAULT_PREFIX);
    }

    /**
     * Returns a new TextWriter just like this one, but with morePrefix added
     * on. Note that these share the context used to break cycles.
     */
    public TextWriter indent(String morePrefix) {
        //Since none of the TextWriters do their own buffering, they
        //don't need to flush() to coordinate with each other.
        return new TextWriter(out,
                              myNewline + morePrefix,
                              myAutoflush,
                              false,
                              myContext);
    }

//    /**
//     * Is this TextWriter in the midst of printing obj?
//     */
//    public boolean isPrinting(Object obj) {
//        return myContext.maps(new TraversalKey(obj));
//    }

    /**
     * Like PrintWriter.print(), but doesn't suppress IOExceptions
     */
    public void print(Object original) throws IOException {
        Object obj = Ref.resolution(original);
        if (null == obj) {
            write("null");

        } else if (obj instanceof String) {
            write((String)obj);

        } else {
            TraversalKey key = new TraversalKey(original);
            if (myContext.maps(key)) {
                write("<***CYCLE***>");
                return;
            }
            myContext.put(key, null);
            TextWriter sub =
              new TextWriter(out, myNewline, myAutoflush, false, myContext);
            try {
                if (obj instanceof Throwable) {
                    Throwable leaf = ThrowableSugar.leaf((Throwable)obj);
                    if (leaf instanceof EPrintable) {
                        ((EPrintable)leaf).__printOn(sub);
                    } else {
                        //XXX we engage in this horrible kludge because
                        //ThrowableSugar exists in a layer prior to TextWriter
                        ThrowableSugar.printThrowableOn((Throwable)obj, out);
                    }
                } else if (obj instanceof EPrintable) {
                    // If both Object#toString and EPrintable#__printOn are
                    // overridden, then prefer __printOn
                    ((EPrintable)obj).__printOn(sub);
                } else {
                    // typically goes to Miranda.
                    E.call(obj, "__printOn", sub);
                }
            } catch (Throwable problem) {
                Throwable leaf = ThrowableSugar.leaf(problem);
                if (leaf instanceof Ejection) {
                    throw (Ejection)leaf;
                }
                String oClassName = ClassDesc.simpleSig(obj.getClass());
                String pClassName = ClassDesc.simpleSig(leaf.getClass());
                print("<***",
                      StringHelper.aan(oClassName),
                      " throws ",
                      StringHelper.aan(pClassName),
                      " when printed***>");
            } finally {
                sub.close();
                myContext.removeKey(key);
            }
        }
    }

    /**
     * quote(obj) differs from print(obj) in that the quoted form of the object
     * is printed. Currently, the quoted & non quoted printed forms differ only
     * for String and Twine.
     */
    public void quote(Object original) throws IOException {
        Object obj = Ref.resolution(original);
        if (null != obj) {
            if (obj instanceof String) {
                obj = StringHelper.quote((String)obj);

            } else if (obj instanceof Twine) {
                obj = ((Twine)obj).quote();
            } else if (obj instanceof Character) {
                obj = CharacterSugar.quote(((Character)obj).charValue());
            }
        }
        print(obj);
    }

    /**
     *
     */
    public void printAll(Object[] objs) throws IOException {
        for (int i = 0; i < objs.length; i++) {
            print(objs[i]);
        }
    }

    /**
     *
     */
    public void print(Object a, Object b) throws IOException {
        print(a);
        print(b);
    }

    /**
     *
     */
    public void print(Object a, Object b, Object c) throws IOException {
        print(a);
        print(b);
        print(c);
    }

    /**
     *
     */
    public void print(Object a, Object b, Object c, Object d)
      throws IOException {
        print(a);
        print(b);
        print(c);
        print(d);
    }

    /**
     *
     */
    public void print(Object a, Object b, Object c, Object d, Object e)
      throws IOException {
        print(a);
        print(b);
        print(c);
        print(d);
        print(e);
    }

    /**
     *
     */
    public void print(Object a,
                      Object b,
                      Object c,
                      Object d,
                      Object e,
                      Object f) throws IOException {
        print(a);
        print(b);
        print(c);
        print(d);
        print(e);
        print(f);
    }

    /**
     *
     */
    public void print(Object a,
                      Object b,
                      Object c,
                      Object d,
                      Object e,
                      Object f,
                      Object g) throws IOException {
        print(a);
        print(b);
        print(c);
        print(d);
        print(e);
        print(f);
        print(g);
    }

    /**
     * Like println(), but does the newline & prefix first
     */
    public void lnPrint(Object obj) throws IOException {
        println();
        print(obj);
    }

    /**
     * Like PrintWriter.println(), but uses our own newline, and doesn't
     * suppress IOExceptions.
     */
    public void println() throws IOException {
        out.write(myNewline);
        if (myAutoflush) {
            out.flush();
        }
    }

    /**
     * Like PrintWriter.println(), but doesn't suppress IOExceptions
     */
    public void println(Object obj) throws IOException {
        print(obj);
        println();
    }

    /**
     *
     */
    public void write(char[] cbuf, int off, int len) throws IOException {
        write(new String(cbuf, off, len));
    }

    /**
     *
     */
    public void write(int c) throws IOException {
        if ('\n' == c) {
            println();
        } else {
            out.write(c);
        }
    }

    /**
     *
     */
    public void write(String str, int off, int len) throws IOException {
        int bound = off + len;
        while (off < bound) {
            int nl = str.indexOf('\n', off);
            if (-1 == nl || nl > bound) {
                nl = bound;
            }
            //would be more efficient, but hits a bug in java.io.Writer
            //XXX out.write(str, off, nl - off);
            out.write(str.substring(off, nl));
            if (nl == bound) {
                return;
            }
            println();
            off = nl + 1;
        }
    }
}
