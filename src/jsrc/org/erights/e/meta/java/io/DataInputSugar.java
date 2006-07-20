// Copyright 2003 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.java.io;

import org.erights.e.meta.java.math.EInt;

import java.io.DataInput;
import java.io.IOException;
import java.math.BigInteger;

/**
 * A sweetener defining extra messages that may be e-sent to a DataInput
 *
 * @author Mark S. Miller
 */
public class DataInputSugar {

    /**
     * prevent instantiation
     */
    private DataInputSugar() {
    }

    /**
     * @return an {@link EInt}
     * @see DataOutputSugar#writeWholeNum
     */
    static public Number readWholeNum(DataInput self) throws IOException {
        BigInteger result = BigInteger.ZERO;
        while (true) {
            int byt = self.readByte();
            if (0 == (byt & 0x80)) {
                return EInt.normal(result.or(BigInteger.valueOf(byt)));
            } else {
                result = result.or(BigInteger.valueOf(byt & 0x7F));
                result = result.shiftLeft(7);
            }
        }
    }
}
