// Copyright 2003 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.java.io;

import org.erights.e.develop.assertion.T;

import java.io.DataOutput;
import java.io.IOException;
import java.math.BigInteger;

/**
 * A sweetener defining extra messages that may be e-sent to a DataOutput
 *
 * @author Mark S. Miller
 */
public class DataOutputSugar {

    /**
     * prevent instantiation
     */
    private DataOutputSugar() {
    }

    /**
     * Encodes a whole number (a non-negative integer) using the <a href=
     * "http://www.waterken.com/dev/Doc/code/#ExtensionNumber"
     * >Waterken Doc ExtensionNumber</a> format.
     */
    static public int writeWholeNum(DataOutput self, BigInteger wholeNum)
      throws IOException {
        return writeWholeNum(self, wholeNum, 0x00);
    }

    /**
     * Because this needs to write in big-endian order, but the natural
     * algorithm generates in little endian order, this uses recursion to
     * do the reversal.
     *
     * @param highbit Either 0x00 or 0x80, which is or-ed into the low order
     *                byte.
     * @return
     */
    static private int writeWholeNum(DataOutput self,
                                     BigInteger wholeNum,
                                     int highbit)
      throws IOException {

        int signum = wholeNum.signum();
        T.require(signum >= 0,
                  "Non-negative integer expected: ", wholeNum);

        int result = 1;
        int lowByte = (wholeNum.intValue() & 0x7F) | highbit;
        BigInteger highRest = wholeNum.shiftRight(7);
        if (highRest.signum() >= 1) {
            result += writeWholeNum(self, highRest, 0x80);
        }
        self.writeByte(lowByte);
        return result;
    }
}
