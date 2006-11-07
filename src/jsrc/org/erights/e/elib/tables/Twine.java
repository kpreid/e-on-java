package org.erights.e.elib.tables;

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
import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.serial.DeepPassByCopy;
import org.erights.e.elib.vat.StackContext;
import org.erights.e.elib.ref.Ref;
import org.erights.e.meta.java.io.InputStreamSugar;
import org.erights.e.meta.java.lang.CharacterMakerSugar;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

/**
 * Like a String, except that it keeps track of original source positions.
 * <p/>
 * Twine has exactly three immediate subclasses -- EmptyTwine, AtomicTwine
 * and CompositeTwine. These will give their contents as a list of
 * AtomicTwine. Only AtomicTwine gives accurate source positions, so to be
 * accurate you have to enumerate these. (EmptyTwine has no source
 * position.)
 * <p/>
 * To the E programmer, Twine will act like a String and automagically coerce
 * to String (the String part of the twine). A String will also
 * automagically coerce to a SimpleTwine (an AtomicTwine with no source
 * info) or an EmptyTwine. As a ConstList, Twine acts just like a list of
 * the Characters from the String part.
 * <p/>
 * Twine and its subclasses replace the old ConstString and StringSugar while
 * doing a lot more.
 * <p/>
 * Implementation note: The Equalizer's simplification rules depend on Twine
 * that's the same() as a String actually being a SimpleTwine or EmptyTwine.
 *
 * @author Mark S. Miller
 */
public abstract class Twine extends ConstList implements DeepPassByCopy {

    static private final long serialVersionUID = 5436911677426527143L;

    /**
     * Initialized lazily to avoid a circular initialization problem
     * @noinspection StaticNonFinalField
     */
    static private StaticMaker OptTwineMaker = null;

    /**
     * Package scoped to deter the creation of any other immediate subclasses
     * besides AtomicTwine and CompositeTwine.
     * @noinspection ConstructorNotProtectedInAbstractClass
     */
    Twine() {
    }

    /**
     *
     */
    static public StaticMaker GetTwineMaker() {
        if (null == OptTwineMaker) {
            //noinspection NonThreadSafeLazyInitialization
            OptTwineMaker = StaticMaker.make(Twine.class);
        }
        return OptTwineMaker;
    }

    /**
     * Make a Twine whose parts are this list of AtomicTwines. <p>
     * <p/>
     * The list of parts is assumed to already be maximally merged. If it's
     * an empty list, return the EmptyTwine. If it's
     * a singleton list, return that part. Otherwise return a CompositeTwine
     * on these parts.
     */
    static public Twine fromParts(ConstList parts) {
        if (0 == parts.size()) {
            return EmptyTwine.THE_ONE;
        } else if (1 == parts.size()) {
            return (Twine)parts.get(0);
        } else {
            AtomicTwine[] atoms =
              (AtomicTwine[])parts.getArray(AtomicTwine.class);
            return new CompositeTwine(atoms);
        }
    }

    /**
     * Makes a Twine with no information besides str. <p>
     * <p/>
     * If str is empty, returns the empty CompositeTwine. Otherwise, returns
     * a new SimpleTwine on str.
     */
    static public Twine fromString(String str) {
        return fromString(str, null);
    }

    /**
     * Makes a Twine on str from optSourceSpan. <p>
     * <p/>
     * If str is empty, returns the EmptyTwine. Otherwise, returns
     * a new SimpleTwine on str or a LocatedTwine on both args.
     */
    static public Twine fromString(String str, SourceSpan optSourceSpan) {
        if (0 == str.length()) {
            return EmptyTwine.THE_ONE;
        } else if (null == optSourceSpan) {
            return new SimpleTwine(str);
        } else {
            return new LocatedTwine(str, optSourceSpan);
        }
    }

    /**
     *
     */
    static public Twine fromChars(ConstList chars, SourceSpan optSourceSpan) {
        char[] data = (char[])chars.getArray(Character.TYPE);
        return fromString(String.valueOf(data), optSourceSpan);
    }

    /**
     * startLine defaults to 1 and startCol defaults to 0
     */
    public Twine asFrom(String url) {
        return asFrom(url, 1, 0);
    }

    /**
     * Returns a new Twine on the same underlying bare String as this one,
     * but marked as being contiguous text starting at the position
     * described by the argument.
     */
    public Twine asFrom(String url, int startLine, int startCol) {
        FlexList parts = FlexList.fromType(AtomicTwine.class);
        String str = bare();
        int len = str.length();
        int i = 0; //beginning of next line
        int j;     //last char of next line
        for (; i < len; i = j + 1) {
            j = str.indexOf('\n', i);
            if (-1 == j) {
                j = len - 1; //the last char isn't necessarily a '\n'
                //This also means the next i will be len, and so we'll exit
                //the loop.
            }
            int endCol = startCol + j - i;
            SourceSpan span = new SourceSpan(url, true,
                                             startLine, startCol,
                                             startLine, endCol);
            parts.push(fromString(str.substring(i, j + 1), span));
            startLine++;
            startCol = 0;
        }
        return fromParts(parts.snapshot());
    }

    /**
     * @return A list of AtomicTwines. An empty Twine returns the empty
     *         list.
     */
    public abstract ConstList getParts();

    /**
     * If pos is a position within this Twine, return a pair consisting of
     * the index in the getParts() list of the AtomicTwine containing this
     * position, and the offset into this part of this position.
     */
    public int[] getPartAt(int pos)
      throws IndexOutOfBoundsException {
        if (-1 >= pos) {
            throw new IndexOutOfBoundsException(Integer.toString(pos));
        }
        ConstList parts = getParts();
        int sofar = 0;
        for (int i = 0; i < parts.size(); i++) {
            AtomicTwine atom = (AtomicTwine)parts.get(i);
            int size = atom.size();
            if (pos < sofar + size) {
                return new int[]{i, pos - sofar};
            }
            sofar += size;
        }
        throw new IndexOutOfBoundsException(
          Integer.toString(pos) + " bigger than " + sofar);
    }

    /**
     * Returns a mapping from intervals in the Twine to spans showing where
     * they came from. <p>
     * <p/>
     * Unfortunately, because of the way the E implementation is layered, the
     * keys in this mapping cannot be int-regions (as these are
     * implemented in the E language, which is in a layer that depends on
     * this layer, and we're trying to avoid inter-layer cyclic
     * dependencies). Instead, the keys are represented as a pair of ints
     * representing start..!bound. Note that this is inclusive-exclusive,
     * while the info in the spans are inclusive-inclusive. <p>
     * <p/>
     * The mapping is ordered to facilitate binary search. Intervals with no
     * SourceSpan information are left out of the map.
     */
    public ConstMap getSourceMap() {
        ConstList parts = getParts();
        int numParts = parts.size();
        FlexMap result = FlexMap.fromTypes(int[].class,
                                           SourceSpan.class,
                                           numParts);
        int offset = 0;
        for (int i = 0; i < numParts; i++) {
            AtomicTwine part = (AtomicTwine)parts.get(i);
            int partSize = part.size();
            SourceSpan span = part.getOptSpan();
            if (null != span) {
                int[] key = {offset, offset + partSize};
                result.put(key, span);
            }
            offset += partSize;
        }
        return result.snapshot();
    }

    /**
     * Explicitly gets the String part of the twine. <p>
     * <p/>
     * When using this call, this call itself will be the top level construct
     * for breaking cycles. XXX where & what do we stabilize?
     */
    public abstract String bare();

    /**
     * Is the bare string all the info there is?
     */
    public abstract boolean isBare();

    /**
     * Gets the sourceSpan part of the twine, if it's there. <p>
     * <p/>
     * If this is an AtomicTwine, then, if the SourceSpan is there, it's as
     * accurate as you're going to get. If this is a CompositeTwine, then,
     * if the SourceSpan is there, it describes a span that includes all the
     * individual spans. If a CompositeTwine returns null, there may still
     * be SourceSpans on the atomic parts, but they couldn't all be
     * summarized into one covering span.
     */
    public abstract SourceSpan getOptSpan();

    /**
     * A SHA hash of the bare content of the string.
     */
    public BigInteger getBareCryptoHash()
      throws NoSuchAlgorithmException, IOException {
        ByteArrayInputStream bais =
          new ByteArrayInputStream(bare().getBytes());
        return InputStreamSugar.getCryptoHash(bais);
    }


    /**
     *
     */
    abstract Twine infectOneToOne(String str);

    /**
     * oneToOne defaults to false
     */
    public Twine infect(String str) {
        return infect(str, false);
    }

    /**
     * Returns a Twine with str as the string part, and the source info from
     * this Twine.
     * <p/>
     *
     * @param str      The string to annotate with source-span info from this
     *                 Twine.
     * @param oneToOne If true, the two strings must be the same size, in
     *                 which case this Twine's source-span info is mapped
     *                 one to one onto the new string. If false, then the
     *                 new string will only get a non-oneToOne form of this
     *                 Twine's overall source-span.
     */
    public Twine infect(String str, boolean oneToOne) {
        if (oneToOne) {
            if (str.length() == size()) {
                return infectOneToOne(str);
            } else {
                T.fail(E.toQuote(str) + " and " +
                       quote() + " must be the " +
                       "same size");
                return null; //make compiler happy
            }
        } else {
            SourceSpan optSpan = getOptSpan();
            if (null != optSpan) {
                optSpan = optSpan.notOneToOne();
            }
            return fromString(str, optSpan);
        }
    }

    /**
     * Return a new Twine that represents a concatenation of the parts of
     * this and other. <p>
     * <p/>
     * The last part of this may be merged with the first part of other.
     */
    public ConstList add(Object other) {
        other = Ref.resolution(other);
        Twine otherTwine;
        if (other instanceof Twine) {
            otherTwine = (Twine)other;
        } else if (other instanceof String) {
            otherTwine = fromString((String)other);
        } else if (other instanceof EList) {
            // Must honor the EList contract when it would succeed
            ConstList result = super.add(other);
            if (Trace.eruntime.warning && Trace.ON) {
                StackContext sc = new StackContext(E.toQuote(result).bare(),
                                                   true, true);
                Trace.eruntime.warningm("Twine + non-Twine EList", sc);
            }
            return result;
        } else {
            // Convert only when super.add/1 would fail.
            otherTwine = fromString(E.toString(other));
            if (Trace.eruntime.warning && Trace.ON) {
                StackContext sc =
                  new StackContext(otherTwine.bare(), true, true);
                Trace.eruntime.warningm("Stringifying with '+'", sc);
            }
        }
        ConstList mine = getParts();
        int mineSize = mine.size();
        ConstList his = otherTwine.getParts();
        int hisSize = his.size();

        if (1 <= mineSize && 1 <= hisSize) {
            mineSize--;
            AtomicTwine lastMine = (AtomicTwine)mine.get(mineSize);
            mine = mine.run(0, mineSize);
            AtomicTwine firstHis = (AtomicTwine)his.get(0);
            his = his.run(1, hisSize);
            // hisSize--;

            mine = mine.add(lastMine.mergedParts(firstHis));
        }
        return fromParts(mine.add(his));
    }

    /**
     * The result will be a Twine
     */
    public ConstList multiply(int reps) {
        Twine result = EmptyTwine.THE_ONE;
        for (int i = 0; i < reps; i++) {
            result = (Twine)result.add(this);
        }
        return result;
    }

    /**
     * Each crlf is turned into an lf to deal with MSWindows, and then each
     * remaining cr is turned into an lf to deal with Mac.
     */
    public Twine canonical() {
        if (-1 == indexOf("\r")) {
            return this;
        } else {
            return replaceAll("\r\n", "\n").replaceAll("\r", "\n");
        }
    }

    /**
     * Returns a Twine.
     */
    public abstract ConstList run(int start, int bound);

    /**
     * Returns a string that, when interpreted as a literal, represents the
     * original string.
     */
    public Twine quote() {
        Twine result = fromString("\"");
        int len = size();
        int p1 = 0;
        for (int p2 = 0; p2 < len; p2++) {
            char c = charAt(p2);
            String newStr = null;
            //XXX Mostly redundant with CharacterSugar.escaped(c).
            switch (c) {
            case '\b':
                {
                    newStr = "\\b";
                    break;
                }
            case '\t':
                {
                    newStr = "\\t";
                    break;
                }
            case '\n':
                {
                    //Output an actual newline, which is legal in a
                    //literal string in E.
                    newStr = "\n";
                    break;
                }
            case '\f':
                {
                    newStr = "\\f";
                    break;
                }
            case '\r':
                {
                    newStr = "\\r";
                    break;
                }
            case '\"':
                {
                    newStr = "\\\"";
                    break;
                }
//            case '\'':
//                {
//                    newStr = "\\\'";
//                    break;
//                }
            case '\\':
                {
                    newStr = "\\\\";
                    break;
                }
            default:
                {
                    if (32 > (int)c || 255 < (int)c) {
                        String num = "0000" + Integer.toHexString(c);
                        int numlen = num.length();
                        num = num.substring(numlen - 4, numlen);
                        newStr = "\\u" + num;
                    }
                }
            }
            if (null != newStr) {
                result = (Twine)result.add(run(p1, p2));
                Twine oldStr = (Twine)run(p2, p2 + 1);
                result = (Twine)result.add(oldStr.infect(newStr, false));
                p1 = p2 + 1;
            }
        }
        result = (Twine)result.add(run(p1, len)).add("\"");
        return result;
    }

    /******************** ConstList methods *********************/

    /**
     *
     */
    public Object get(int index) throws IndexOutOfBoundsException {
        //It's surprising there's no "Character.valueOf(char)", so there
        //could be a cache, as with BigInteger.
        return CharacterMakerSugar.valueOf(charAt(index));
    }

    /**
     * Compares the bare strings.
     * <p/>
     * Optimized for the case where both are Twine. Otherwise should be
     * identical.
     */
    public double op__cmp(ConstList other) {
        if (other instanceof Twine) {
            return (double)bare().compareTo(((Twine)other).bare());
        } else {
            return super.op__cmp(other);
        }
    }

    /**
     * @return Character.class
     */
    public Class valueType() {
        return Character.class;
    }

    /******************** Imitation of String methods *********************/

    /**
     * From the E language, this is identical to get/1. But we provide it so
     * the ELib programmer can avoid boxing the character.
     *
     * @see String#charAt
     */
    public abstract char charAt(int index) throws IndexOutOfBoundsException;

    /**
     * Compares the bare strings ignoring case differences
     *
     * @see String#compareToIgnoreCase
     */
    public int compareToIgnoreCase(String other) {
        return bare().compareToIgnoreCase(other);
    }

    /**
     * Compares the bare strings ignoring case differences
     *
     * @see String#equalsIgnoreCase
     */
    public boolean equalsIgnoreCase(String other) {
        return bare().equalsIgnoreCase(other);
    }

    /**
     * @see String#startsWith
     */
    public boolean startsWith(String prefix) {
        return bare().startsWith(prefix);
    }

    /**
     * @see String#startsWith
     */
    public boolean startsWith(String prefix, int offset) {
        return bare().startsWith(prefix, offset);
    }

    /**
     * @see String#endsWith
     */
    public boolean endsWith(String suffix) {
        return bare().endsWith(suffix);
    }

    /**
     * In E, the encoding always defaults to "UTF-8", period.
     *
     * @see String#getBytes
     */
    public byte[] getBytes() throws UnsupportedEncodingException {
        return getBytes("UTF-8");
    }

    /**
     * @see String#getBytes
     */
    public byte[] getBytes(String enc) throws UnsupportedEncodingException {
        return bare().getBytes(enc);
    }

    /**
     * Just like {@link #startOf(EList)}, but with a String argument for
     * convenience of the Java programmer.
     *
     * @deprecated Use {@link #startOf(EList)} instead.
     */
    public int indexOf(String str) {
        return bare().indexOf(str);
    }

    /**
     * Just like {@link #startOf(EList, int)}, but with a String argument for
     * convenience of the Java programmer.
     *
     * @deprecated Use {@link #startOf(EList, int)} instead.
     */
    public int indexOf(String str, int fromIndex) {
        return bare().indexOf(str, fromIndex);
    }

    /**
     * Overridden for performance
     */
    public int startOf(EList candidate, int start) {
        if (candidate instanceof Twine) {
            // Just an optimization
            return bare().indexOf(((Twine)candidate).bare(), start);
        } else {
            return super.startOf(candidate, start);
        }
    }

    /**
     * Just like {@link #lastStartOf(EList)}, but with a String argument for
     * convenience of the Java programmer.
     */
    public int lastIndexOf(String str) {
        return bare().lastIndexOf(str);
    }

    /**
     * Just like {@link #lastStartOf(EList, int)}, but with a String argument
     * for convenience of the Java programmer.
     */
    public int lastIndexOf(String str, int fromIndex) {
        return bare().lastIndexOf(str, fromIndex);
    }

    /**
     * Overridden for performance
     */
    public int lastStartOf(EList candidate, int start) {
        if (candidate instanceof Twine) {
            // Just an optimization
            return bare().lastIndexOf(((Twine)candidate).bare(), start);
        } else {
            return super.lastStartOf(candidate, start);
        }
    }

    /**
     * In E, we have the string-based replaceAll() rather than the
     * character-based replace(). <p>
     * <p/>
     * For each match, the source info from the twine matching oldStr infects
     * the substituted newStr. Ignoring the source info,
     * 'str replaceAll(oldStr, newStr)' should be equivalent to
     * 'newStr rjoin(str split(oldStr))'. <p>
     * <p/>
     * If oldStr is the null string (""), replaceAll() throws an
     * IllegalArgumentException.
     *
     * @see String#replace
     */
    public Twine replaceAll(String oldStr, String newStr) {
        Twine result = EmptyTwine.THE_ONE;
        int oldLen = oldStr.length();
        if (0 == oldLen) {
            throw new IllegalArgumentException
              ("oldStr must not be the null string");
        }
        int p1 = 0;
        for (int p2 = indexOf(oldStr); -1 != p2; p2 = indexOf(oldStr, p1)) {
            Twine left = (Twine)run(p1, p2);
            Twine oldTwine = (Twine)run(p2, p2 + oldLen);
            result = (Twine)result.add(left)
              .add(oldTwine.infect(newStr, false));
            p1 = p2 + oldLen;
        }
        result = (Twine)result.add(run(p1, size()));
        return result;
    }

    /**
     * Like Python's splitFields(), this returns a list of the "fields" of
     * this twine (substrings of this twine), using 'sep' as a separator. <p>
     * <p/>
     * The returned list will have one more element than the number of
     * non-overlapping occurrences of 'sep'. <p>
     * <p/>
     * Unlike Python, if sep is the null string (""), split() throws an
     * IllegalArgumentException.
     */
    public ConstList split(String sep) {
        FlexList result = FlexList.fromType(Twine.class);
        int sepLen = sep.length();
        if (0 == sepLen) {
            throw new IllegalArgumentException
              ("sep must not be the null string");
        }
        int p1 = 0;
        for (int p2 = indexOf(sep); -1 != p2; p2 = indexOf(sep, p1)) {
            result.push(run(p1, p2));
            p1 = p2 + sepLen;
        }
        result.push(run(p1, size()));
        return result.snapshot();
    }

    /**
     * Like Python's joinFields(), but with the receiver and argument
     * reversed (hence the initial "r").
     * <p/>
     * Concatenates the fields with this twine as the intervening separator.
     * Ignoring source info, and if 'sep' is not the null string,
     * 'sep.rjoin(str.split(sep))' should be equivalent to 'str'.
     */
    public Twine rjoin(Twine[] fields) {
        if (0 == fields.length) {
            return EmptyTwine.THE_ONE;
        }
        Twine result = fields[0];
        for (int i = 1; i < fields.length; i++) {
            result = (Twine)result.add(this).add(fields[i]);
        }
        return result;
    }

    /**
     *
     */
    public String toString() {
        return bare();
    }

    /**
     *
     */
    public Twine toLowerCase() {
        return infect(bare().toLowerCase(), true);
    }

    /**
     *
     */
    public Twine toUpperCase() {
        return infect(bare().toUpperCase(), true);
    }

    /**
     *
     */
    public Twine trim() {
        //XXX should be made more efficient
        String str = bare();
        String trimmed = str.trim();
        int pos = str.indexOf(trimmed);
        return (Twine)run(pos, pos + trimmed.length());
    }
}
