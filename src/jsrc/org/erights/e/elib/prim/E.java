package org.erights.e.elib.prim;

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

import org.erights.e.develop.exception.EBacktraceException;
import org.erights.e.develop.exception.ThrowableSugar;
import org.erights.e.develop.format.StringHelper;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.base.Ejection;
import org.erights.e.elib.base.Ejector;
import org.erights.e.elib.base.Script;
import org.erights.e.elib.base.Thunk;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.tables.Twine;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.elib.vat.Vat;

import java.io.StringWriter;

/**
 * This class is most of the <a href= "{@docroot}/../e/semantics.html" >normal
 * application programmer's API</a> to the ELib functionality. In particular,
 * the send methods below are what E's "<-" turn into. <p>
 * <p/>
 * NOTE!  All the methods of this class trust their callers to 1) only provide
 * interned strings as verbs, and 2) not modify an args array after passing it
 * in. Btw, all source code literal strings are automatically intern()ed. ELib
 * itself will never modify a passed-in args array. <p>
 * <p/>
 * This class is not intended to be used directly from E-language programs.
 * Rather, the name "E" in the safe scope contains a thin wrapper for this
 * class for the E language programmer to use instead.
 *
 * @author Mark S. Miller
 */
public class E {

    /*
     * The implementation reads in approximately bottom up order
     */

    /**
     *
     */
    static public final Object[] NO_ARGS = new Object[0];

    /**
     * prevent instantiation
     */
    private E() {
    }

    /**
     * A 0-argument immediate call
     *
     * @see #callAll(Object,String,Object[])
     */
    static public Object call(Object rec, String verb) {
        return callAll(rec, verb, NO_ARGS);
    }

    /**
     * A 1-argument immediate call
     *
     * @see #callAll(Object,String,Object[])
     */
    static public Object call(Object rec, String verb, Object arg1) {
        Object[] args = {arg1};
        return callAll(rec, verb, args);
    }

    /**
     * A 2-argument immediate call
     *
     * @see #callAll(Object,String,Object[])
     */
    static public Object call(Object rec,
                              String verb,
                              Object arg1,
                              Object arg2) {
        Object[] args = {arg1, arg2};
        return callAll(rec, verb, args);
    }

    /**
     * A 3-argument immediate call
     *
     * @see #callAll(Object,String,Object[])
     */
    static public Object call(Object rec,
                              String verb,
                              Object arg1,
                              Object arg2,
                              Object arg3) {
        Object[] args = {arg1, arg2, arg3};
        return callAll(rec, verb, args);
    }

    /**
     * A 4-argument immediate call
     *
     * @see #callAll(Object,String,Object[])
     */
    static public Object call(Object rec,
                              String verb,
                              Object arg1,
                              Object arg2,
                              Object arg3,
                              Object arg4) {
        Object[] args = {arg1, arg2, arg3, arg4};
        return callAll(rec, verb, args);
    }

    /**
     * A 5-argument immediate call
     *
     * @see #callAll(Object,String,Object[])
     */
    static public Object call(Object rec,
                              String verb,
                              Object arg1,
                              Object arg2,
                              Object arg3,
                              Object arg4,
                              Object arg5) {
        Object[] args = {arg1, arg2, arg3, arg4, arg5};
        return callAll(rec, verb, args);
    }

    /**
     * An E immediate call -- ask 'rec' to immediately do the request described
     * by 'verb' using the supplied 'args'. <p>
     *
     * @see <a href= "http://www.erights.org/elib/concurrency/msg-passing.html#call"
     *      >The Immediate Call</a>
     */
    static public Object callAll(Object rec, String verb, Object[] args) {
        Class recClass;
        if (rec == null) {
            recClass = Object.class;
        } else {
            recClass = rec.getClass();
        }
        Script script = ScriptMaker.THE_ONE.instanceScript(recClass);
        script = script.shorten(rec, verb, args.length);
        return script.execute(rec, verb, args);
    }

    /**
     * To make the backtrace be pretty
     */
    static private void abbrev(StringBuffer buf, String str, int[] budget) {

        str = StringHelper.replaceAll(str, "\r\n", "\\n");
        str = StringHelper.replaceAll(str, "\n", "\\n");
        str = StringHelper.replaceAll(str, "\r", "\\n");
        str = StringHelper.replaceAll(str, "\t", " ");

        budget[0] += budget[1];

        int strLen = str.length();
        int bufLen = buf.length();
        int okStrLen = StrictMath.max(budget[0] - bufLen, 9);

        if (okStrLen < strLen) {
            int segLen = okStrLen / 2 - 2;
            str = str.substring(0, segLen) + "..." +
              str.substring(strLen - segLen);
        }
        buf.append(str);
    }

    /**
     * To make backtraces be pretty.
     */
    static public String abbrevCall(Object rec,
                                    String callMarker,
                                    String verb,
                                    Object[] args) {
        int[] budget = {0, 120 / (args.length + 1)};
        StringBuffer buf = new StringBuffer();

        abbrev(buf, toQuote(rec).bare(), budget);

        if (!"run".equals(verb)) {
            abbrev(buf, callMarker + verb, budget);
        }
        buf.append('(');

        if (1 <= args.length) {
            for (int i = 0; i < args.length - 1; i++) {
                abbrev(buf, toQuote(args[i]).bare() + ", ", budget);
            }
            abbrev(buf, toQuote(args[args.length - 1]).bare(), budget);
        }
        buf.append(')');
        return buf.toString();
    }

    /**
     * XXX SECURITY ALERT: This is a semantically visible mutable static
     * variable, used to suppress infinite regress when the attempt to generate
     * a meaningful backtrace string results in another exception that needs to
     * be backtraced. <p>
     * <p/>
     * This is a rare case of something I don't know how to do as well in a
     * pure capability pattern.
     *
     * @noinspection StaticNonFinalField
     */
    static private boolean Backtracing = false;

    /**
     * Return 'ex' annotated with a compact description of 'rec.verb(args)'.
     *
     * @return a RuntimeException that wraps ex with a description of the call
     *         'rec.verb(args...)', without a preceding "***". the description
     *         is a line of text approximately in E call notation, but
     *         abbreviated to fit on one long line. Removed text is replaced
     *         with "...".
     */
    static public RuntimeException backtrace(Throwable ex,
                                             Object rec,
                                             String verb,
                                             Object[] args) {
        if (Backtracing) {
            return new EBacktraceException(ex, "# Problem reporting problem!");
        }

        try {
            Backtracing = true;

            Throwable leaf = ThrowableSugar.leaf(ex);
            if (leaf instanceof Ejection) {
                return (Ejection)leaf;
            }
            String msg = ". " + abbrevCall(rec, ".", verb, args);
            return new EBacktraceException(ex, msg);

        } catch (Throwable th) {
            return new EBacktraceException(ex, "# Problem reporting (" + th +
                ")");

        } finally {
            Backtracing = false;
        }
    }

    /**
     * A 0-argument eventual send
     *
     * @see #sendAll(Object,String,Object[])
     */
    static public Ref send(Object rec, String verb) {
        return sendAll(rec, verb, NO_ARGS);
    }

    /**
     * A 1-argument eventual send
     *
     * @see #sendAll(Object,String,Object[])
     */
    static public Ref send(Object rec, String verb, Object arg1) {
        Object[] args = {arg1};
        return sendAll(rec, verb, args);
    }

    /**
     * A 2-argument eventual send
     *
     * @see #sendAll(Object,String,Object[])
     */
    static public Ref send(Object rec, String verb, Object arg1, Object arg2) {
        Object[] args = {arg1, arg2};
        return sendAll(rec, verb, args);
    }

    /**
     * A 3-argument eventual send
     *
     * @see #sendAll(Object,String,Object[])
     */
    static public Ref send(Object rec,
                           String verb,
                           Object arg1,
                           Object arg2,
                           Object arg3) {
        Object[] args = {arg1, arg2, arg3};
        return sendAll(rec, verb, args);
    }

    /**
     * A 4-argument eventual send
     *
     * @see #sendAll(Object,String,Object[])
     */
    static public Ref send(Object rec,
                           String verb,
                           Object arg1,
                           Object arg2,
                           Object arg3,
                           Object arg4) {
        Object[] args = {arg1, arg2, arg3, arg4};
        return sendAll(rec, verb, args);
    }

    /**
     * Queue the described delivery.
     * <p/>
     * Rather than providing an explicit Resolver as input argument, this
     * method returns a promise that represents the outcome. When the actual
     * outcome is determined, the returned reference will reflect that
     * outcome.
     * <p/>
     * This should only synchronously throw an exception if invoked while
     * there's no current Vat.
     *
     * @see <a href= "http://www.erights.org/elib/concurrency/msg-passing.html#pipe-send"
     *      >The Pipelined Send</a>
     */
    static public Ref sendAll(Object rec, String verb, Object[] args) {
        if (rec instanceof Ref) {
            return ((Ref)rec).sendAll(verb, args);
        } else {
            return Vat.sendAll(rec, verb, args);
        }
    }

    /**
     * A 0-argument eventual sendOnly
     *
     * @see #sendAllOnly(Object,String,Object[])
     */
    static public Throwable sendOnly(Object rec, String verb) {
        return sendAllOnly(rec, verb, NO_ARGS);
    }

    /**
     * A 1-argument eventual sendOnly
     *
     * @see #sendAllOnly(Object,String,Object[])
     */
    static public Throwable sendOnly(Object rec, String verb, Object arg1) {
        Object[] args = {arg1};
        return sendAllOnly(rec, verb, args);
    }

    /**
     * A 2-argument eventual sendOnly
     *
     * @see #sendAllOnly(Object,String,Object[])
     */
    static public Throwable sendOnly(Object rec,
                                     String verb,
                                     Object arg1,
                                     Object arg2) {
        Object[] args = {arg1, arg2};
        return sendAllOnly(rec, verb, args);
    }

    /**
     * A 3-argument eventual sendOnly
     *
     * @see #sendAllOnly(Object,String,Object[])
     */
    static public Throwable sendOnly(Object rec,
                                     String verb,
                                     Object arg1,
                                     Object arg2,
                                     Object arg3) {
        Object[] args = {arg1, arg2, arg3};
        return sendAllOnly(rec, verb, args);
    }

    /**
     * A 4-argument eventual sendOnly
     *
     * @see #sendAllOnly(Object,String,Object[])
     */
    static public Throwable sendOnly(Object rec,
                                     String verb,
                                     Object arg1,
                                     Object arg2,
                                     Object arg3,
                                     Object arg4) {
        Object[] args = {arg1, arg2, arg3, arg4};
        return sendAllOnly(rec, verb, args);
    }

    /**
     * Like sendAll(rec, verb, args), except that "Only" means we don't care
     * about the result (including whether it succeeded), as is appropriate for
     * event notifications.
     *
     * @return Why wasn't this event queued?  It isn't queued if this vat or
     *         comm connection is shut down, in which case the returned problem
     *         explains why. If null is returned, then the event was queued,
     *         though it may still not arrive.
     * @see <a href= "http://www.erights.org/elib/concurrency/msg-passing.html#sendOnly"
     *      >The Eventual Send: sendOnly</a>
     */
    static public Throwable sendAllOnly(Object rec,
                                        String verb,
                                        Object[] args) {
        if (rec instanceof Ref) {
            return ((Ref)rec).sendAllOnly(verb, args);
        } else {
            return Vat.sendAllOnly(rec, verb, args);
        }
    }

    /**
     * Like E.toTwine(obj), but returns a String.
     */
    static public String toString(Object obj) {
        StringWriter strWriter = new StringWriter();
        try {
            new TextWriter(strWriter).print(obj);
        } catch (Throwable th) {
            throw Ejection.backtrace(th,
                                     "# While printing an " + obj.getClass());
        }
        return strWriter.getBuffer().toString();
    }

    /**
     * Prints obj into a Twine if necessary, and returns this printed form.
     * <p/>
     * <p/>
     * E programmers should generally call "E.toTwine(obj)", rather than
     * "obj.toString()". This applies both to E language programmers and to
     * ELib programmers. However, E programmers should not generally call
     * E.toTwine(obj) from within __printOn(TextWriter) methods, as that will
     * circumvent the cycle-breaking logic in TextWriter. Instead, you should
     * usually call a TextWriter.*print*(obj) method, as it will check for a
     * cycle before calling __printOn(TextWriter) on its argument.
     */
    static public Twine toTwine(Object obj) {
        if (obj instanceof Twine) {
            return (Twine)obj;
        } else {
            return Twine.fromString(toString(obj));
        }
    }

    /**
     * Like toTwine, but gets the quoted form.
     *
     * @see TextWriter#quote
     */
    static public Twine toQuote(Object obj) {
        StringWriter strWriter = new StringWriter();
        try {
            new TextWriter(strWriter).quote(obj);
        } catch (Throwable th) {
            throw Ejection.backtrace(th,
                                     "# While quoting an " + obj.getClass());
        }
        String str = strWriter.getBuffer().toString();
        return Twine.fromString(str);
    }

    /**
     * These are E's implicit coercions to Java Class types, using Java's (not
     * E's) null and non-near acceptance behavior.
     * <p/>
     * Given that specimen is NEAR, when targType isPrimitive(), then
     * <pre>    E.as(specimen, targType)</pre>
     * is like
     * <pre>    targType.coerce(specimen, null)</pre>
     * Otherwise
     * <pre>    E.as(specimen, targType)</pre>
     * is like
     * <pre>    nullOk[targType].coerce(specimen, null)</pre>
     * If specimen is not near and targType isn't a rubber stamping interface,
     * then either of the above <tt>coerce</tt> calls will fail, but
     * <tt>E.as</tt> will succeed if the specimen is a Java-instanceof the
     * targType.
     */
    static public Object as(Object specimen, Class targType) {
        return as(specimen, targType, null);
    }

    /**
     *
     */
    static public Object as(Object specimen,
                            Class targType,
                            OneArgFunc optEjector) {
        if (targType.isInstance(specimen)) {
            // Ref.resolution removes Deflectors. This check saves removing the Deflector
            // and creating a new one. This is not just an optimisation; it's required
            // if we need the same object (e.g. addListener / removeListener pairs).
            return specimen;
        }
        specimen = Ref.resolution(specimen);
        if (null == specimen && !targType.isPrimitive()) {
            return null;
        }
        if (targType.isInstance(specimen)) {
            //Deals with the non-near case. It's a shame about the redundancy
            //with similar checks in ClassDesc.coerce/2. We should probably
            //refactor.
            return specimen;
        }
        return ClassDesc.make(targType).coerce(specimen, optEjector);
    }

    /**
     * As a "boolean" scalar.
     */
    static public boolean asBoolean(Object specimen) {
        return ((Boolean)as(specimen, Boolean.TYPE)).booleanValue();
    }

    /**
     * As an "int" scalar
     */
    static public int asInt(Object specimen) {
        return ((Integer)as(specimen, Integer.TYPE)).intValue();
    }

    /**
     * As a "double" scalar
     */
    static public double asFloat64(Object specimen) {
        return ((Double)as(specimen, Double.TYPE)).doubleValue();
    }

    /**
     * As a "char" scalar
     */
    static public char asChar(Object specimen) {
        return ((Character)as(specimen, Character.TYPE)).charValue();
    }

    /**
     * As a RuntimeException
     */
    static public RuntimeException asRTE(Object problem) {
        return (RuntimeException)as(problem, RuntimeException.class);
    }

    /**
     * 'problem' defaults to "required condition failed"
     */
    static public void require(boolean cond) {
        require(cond, "required condition failed");
    }

    /**
     * If cond isn't true, report problem or problem().
     * <p/>
     * If problem coerces to a Thunk, then reassign problem to be the result of
     * calling this thunk with 'run()'. Then, in either case, coerce this
     * problem to a RuntimeException and throw it.
     * <p/>
     * This is used by the implementation of the 'require' function in the E
     * language's universal scope.
     */
    static public void require(boolean cond, Object problem) {
        if (cond) {
            return;
        }
        //****** This is a good place to breakpoint *******
        Thunk optThunk = null;
        Ejector ej = new Ejector("For E.require/2");
        try {
            optThunk = (Thunk)as(problem, Thunk.class, ej);
        } catch (Throwable t) {
            //rethrows if not this Ejector's Ejection
            ej.result(t);
            //do nothing
        } finally {
            ej.disable();
        }
        if (null != optThunk) {
            problem = optThunk.run();
        }
        throw asRTE(problem);
    }
}
