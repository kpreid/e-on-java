package org.quasiliteral.text;

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
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.Twine;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.meta.java.math.EInt;
import org.quasiliteral.base.MatchMaker;
import org.quasiliteral.base.ValueMaker;

import java.io.StringWriter;

/**
 * The default -- and simplest -- of the quasi parsers
 *
 * @author Mark S. Miller
 */
public class Substituter implements ValueMaker, MatchMaker {

    /**
     * A template is "compiled" into an array of three kinds of
     * elements: <p>
     * A Twine represents a literal string segment. <p>
     * A positive Integer represents a position into which to
     * substitute an arg. <p>
     * A negative Integer represents a pattern position (XXX needs
     * a real explanation). <p>
     * <p/>
     * XXX this representation is a kludge
     */
    private Object[] myTemplate;

    private int myMatchSize;

    //XXX find a way to break this up cleanly

    Substituter(Twine template) {
        int len = template.size();
        FlexList t = FlexList.make();
        StringBuffer buffer = new StringBuffer();
        myMatchSize = 0;
        for (int i = 0; i < len; i++) {
            char c1 = template.charAt(i);
            if (c1 != '$' && c1 != '@') {
                //not a marker
                buffer.append(c1);
            } else if (i >= len - 1) {
                //terminal marker
                buffer.append(c1);
            } else {
                i++;
                char c2 = template.charAt(i);
                if (c1 == c2) {
                    //doubled marker character, drop one
                    buffer.append(c2);
                } else if (c2 != '{') {
                    //not special, so back up and act normal
                    i--;
                    buffer.append(c1);
                } else {
                    //Got one!
                    if (buffer.length() >= 1) {
                        //end of literal segment
                        t.push(Twine.fromString(buffer.toString()));
                        buffer.setLength(0);
                    }
                    int index = 0;
                    for (i++; i < len; i++) {
                        c2 = template.charAt(i);
                        if (c2 == '}') {
                            break;
                        } else if (isDigit(c2)) {
                            index = index * 10 + (c2 - '0');
                        } else {
                            throw new Error("missing '}': " + template);
                        }
                    }
                    if (c1 == '@') {
                        myMatchSize = StrictMath.max(myMatchSize, index + 1);
                        index = ~index;
                    }
                    t.push(EInt.valueOf(index));
                }
            }
        }
        if (buffer.length() >= 1) {
            //end of literal segment
            t.push(Twine.fromString(buffer.toString()));
        }
        myTemplate = (Object[])t.getArray(Object.class);
    }

    /**
     *
     */
    static private boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    /**
     * As a quasi-pattern, "first" replace all ${i}s with args[i]s,
     * then see if the speciman matches the pattern.
     * <p/>
     * If so, return an array in which the i'th element is the part of the
     * specimen that matched @{i}.
     */
    public ConstList matchBind(ConstList args,
                               Object specimen,
                               OneArgFunc optEjector) {
        Twine specstr =
          (Twine)ClassDesc.make(Twine.class).coerce(specimen, optEjector);
        int s = 0;  //index into specimen
        FlexList flex = FlexList.fromType(Twine.class, myMatchSize);
        int[] iPtr = {0};
        while (iPtr[0] < myTemplate.length) {
            Twine prefix = optSegments(iPtr, args);
            if (prefix != null) {
                if (specstr.startsWith(prefix.bare(), s)) {
                    //prefix matches. Skip it and go on
                    s += prefix.size();
                } else {
                    throw Thrower.toEject(optEjector,
                                          "Prefix doesn't match: " + prefix);
                }
            } else {
                //match everything until the *first* match of the next
                //segments. If the next segment is a pattern, match
                //the empty string.
                int index = ~((Integer)myTemplate[iPtr[0]]).intValue();
                iPtr[0]++;
                if (iPtr[0] >= myTemplate.length) {
                    //last segment, eat the rest
                    flex.put(index, specstr.run(s, specstr.size()));
                    s = specstr.size();
                } else {
                    Twine next = optSegments(iPtr, args);
                    if (next != null) {
                        int s2 = specstr.indexOf(next.bare(), s);
                        if (s2 == -1) {
                            //next segments won't match, may as well
                            //fail now
                            throw Thrower.toEject(optEjector,
                                                  "Mismatch: " + next);
                        } else {
                            //eat until after next
                            flex.put(index, specstr.run(s, s2));
                            s = s2 + next.size();
                        }
                    } else {
                        //next segment is also a pattern, so I abstain
                        flex.put(index, "");
                    }
                }
            }
        }
        if (s == specstr.size()) {
            return flex.snapshot();
        } else {
            //pattern fully matched, but with specimen left over
            throw Thrower.toEject(optEjector,
                                  "Excess unmatched: " +
                                  specstr.run(s, specstr.size()));
        }
    }

    /**
     * If the iPtr[0]'th segment is a pattern, then return null and
     * leave iPtr alone.
     * <p/>
     * Otherwise, return the list of consecutive non-pattern segments starting
     * with the iPtr[0]'th segment, and modify iPtr[0] to be the following
     * segment number.
     * <p/>
     * Note that if we're at the end, then we return an empty list rather than
     * null, since the iPtr[0]'th segment is indeed not a pattern.
     */
    private ConstList optSequence(int[] iPtr, ConstList args) {
        FlexList flex = FlexList.make();
        int startPos = iPtr[0];
        if (myTemplate.length == startPos) {
            return ConstList.EmptyList;
        }
        for (; iPtr[0] < myTemplate.length; iPtr[0]++) {
            Object seg = myTemplate[iPtr[0]];
            if (seg instanceof Twine) {
                flex.push(seg);
            } else {
                int index = ((Integer)seg).intValue();
                if (index >= 0) {
                    flex.push(args.get(index));
                } else {
                    break;
                }
            }
        }
        if (startPos == iPtr[0]) {
            // The end case was already handled above
            return null;
        } else {
            return flex.snapshot();
        }
    }

    /**
     * If the iPtr[0]'th segment is a pattern, then return null and
     * leave iPtr alone.
     * <p/>
     * Otherwise, return the string gotten by appending the consecutive
     * non-pattern segments starting with the iPtr[0]'th segment, and modify
     * iPtr[0] to be the following segment number.
     * <p/>
     * Note that if we're at the end, then we return an empty string rather
     * than null, since the iPtr[0]'th segment is indeed not a pattern. (This
     * may not be useful, but it is consistent.) Note further that this isn't
     * the only circumstance in which we return an empty string: A dollar
     * hole may be filled by an expression whose value is the empty string.
     *
     * @see <a href=
     *      "http://bugs.sieve.net/bugs/?func=detailbug&bug_id=125589&group_id=16380
     *      ">simple__quasiParser fails on empty quasi string (``)</a>
     * @see <a href=
     *      "http://bugs.sieve.net/bugs/?func=detailbug&bug_id=125595&group_id=16380
     *      ">CapDesk fails on new simple__quasiParser bug</a>
     */
    private Twine optSegments(int[] iPtr, ConstList args) {
        ConstList sequence = optSequence(iPtr, args);
        if (null == sequence) {
            return null;
        }
        StringBuffer buf = new StringBuffer();
        int len = sequence.size();
        for (int i = 0; i < len; i++) {
            buf.append(E.toString(sequence.get(i)));
        }
        return Twine.fromString(buf.toString());
    }

    /**
     * Evaluate as a quasi-literal by replacing ${i} with args[i], for all i
     */
    public Object substitute(ConstList args) {
        int[] iPtr = {0};
        ConstList list = optSequence(iPtr, args);
        if (null == list || iPtr[0] != myTemplate.length) {
            T.fail("can't substitute() with a pattern: " + this);
        }
        StringWriter strWriter = new StringWriter();
        try {
            new TextWriter(strWriter).printAll((Object[])list.getArray());
        } catch (Throwable th) {
            throw ExceptionMgr.asSafe(th);
        }
        return strWriter.getBuffer().toString();
    }

    /**
     * The number of arguments that must be provided to substitute() or
     * matchBind()
     */
    public int numArgs() {
        int result = 0;
        for (int i = 0; i < myTemplate.length; i++) {
            if (myTemplate[i] instanceof Integer
              && ((Integer)myTemplate[i]).intValue() >= 0) {

                result++;
            }
        }
        return result;
    }

    /**
     * The number of bindings that will be returned by matchBind()
     */
    public int numPatterns() {
        int result = 0;
        for (int i = 0; i < myTemplate.length; i++) {
            if (myTemplate[i] instanceof Integer
              && ((Integer)myTemplate[i]).intValue() < 0) {

                result++;
            }
        }
        return result;
    }

    /**
     * Prints using the template string
     */
    public String toString() {
        StringBuffer result =
          new StringBuffer().append("simple__quasiParser.*Maker(\"");
        for (int i = 0; i < myTemplate.length; i++) {
            if (myTemplate[i] instanceof Twine) {
                //XXX should double internal marker characters
                result.append(myTemplate[i]);
            } else {
                int index = ((Integer)myTemplate[i]).intValue();
                if (index >= 0) {
                    result.append("${" + index + "}");
                } else {
                    result.append("@{" + ~index + "}");
                }
            }
        }
        result.append("\")");
        return result.toString();
    }
}
