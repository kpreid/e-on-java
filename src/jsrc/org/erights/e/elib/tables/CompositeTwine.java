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

import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;

import java.io.IOException;

/**
 * A Twine composed of a sequence of two or more AtomicTwines. <p>
 *
 * @author Mark S. Miller
 */
final class CompositeTwine extends Twine {

    static private final long serialVersionUID = -4692386437819865069L;

    private transient int mySizeCache = -1;

    /**
     *
     */
    private final AtomicTwine[] myParts;

    /**
     * Accepts array without copying, so don't make public
     */
    CompositeTwine(AtomicTwine[] parts) {
        myParts = parts;
    }

    /**
     * '__makeTwine fromParts(myParts)'
     */
    public Object[] getSpreadUncall() {
        return new Object[]{GetTwineMaker(), "fromParts", getParts()};
    }

    /**
     *
     */
    public char charAt(int index) throws IndexOutOfBoundsException {
        int[] pair = getPartAt(index);
        int partIndex = pair[0];
        int offset = pair[1];
        return myParts[partIndex].charAt(offset);
    }

    /**
     *
     */
    public ConstList run(int start, int bound)
      throws IndexOutOfBoundsException {

        int len = size();
        if (start < 0 || bound > len || start > bound) {
            throw new IndexOutOfBoundsException("twine run");
        }
        if (start == bound) {
            return EmptyTwine.THE_ONE;
        }

        int[] leftPair = getPartAt(start);
        int leftIndex = leftPair[0];
        int leftOffset = leftPair[1];
        Twine left = myParts[leftIndex];

        int[] rightPair = getPartAt(bound - 1);
        int rightIndex = rightPair[0];
        int rightOffset = rightPair[1];

        if (leftIndex == rightIndex) {
            return left.run(leftOffset, rightOffset + 1);
        } else {
            Twine result = (Twine)left.run(leftOffset, left.size());
            ConstList middle = getParts().run(leftIndex + 1, rightIndex);
            result = (Twine)result.add(Twine.fromParts(middle));
            Twine right = myParts[rightIndex];
            right = (Twine)right.run(0, rightOffset + 1);
            result = (Twine)result.add(right);
            return result;
        }
    }

    /**
     *
     */
    public int size() {
        if (-1 == mySizeCache) {
            int result = 0;
            for (int i = 0; i < myParts.length; i++) {
                result += myParts[i].size();
            }
            mySizeCache = result;
        }
        return mySizeCache;
    }

    /**
     *
     */
    public String bare() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < myParts.length; i++) {
            buf.append(myParts[i].bare());
        }
        return buf.toString();
    }

    /**
     *
     */
    public boolean isBare() {
        return false;
    }

    /**
     *
     */
    public ConstList getParts() {
        return ConstList.fromArray(myParts);
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        int len = myParts.length;
        for (int i = 0; i < len; i++) {
            out.print(myParts[i]);
        }
    }

    /**
     *
     */
    public SourceSpan getOptSpan() {
        if (myParts.length == 0) {
            return null;
        }
        SourceSpan optResult = myParts[0].getOptSpan();
        for (int i = 1; i < myParts.length; i++) {
            if (null == optResult) {
                return null;
            }
            SourceSpan optSourceSpan = myParts[i].getOptSpan();
            optResult = SourceSpan.optCover(optResult, optSourceSpan);
        }
        return optResult;
    }

    /**
     *
     */
    Twine infectOneToOne(String str) {
        Twine result = EmptyTwine.THE_ONE;
        int pos = 0;
        for (int i = 0; i < myParts.length; i++) {
            Twine part = myParts[i];
            int len = part.size();
            String segment = str.substring(pos, pos + len);
            result = (Twine)result.add(part.infectOneToOne(segment));
            pos += len;
        }
        return result;
    }
}
