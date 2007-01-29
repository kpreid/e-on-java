package org.erights.e.meta.java.math;

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
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.prim.E;
import org.erights.e.meta.java.lang.CharacterMakerSugar;
import org.erights.e.meta.java.lang.DoubleSugar;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A sweetener defining extra messages that may be e-sent to an integer.
 *
 * @author Mark S. Miller
 */
public class BigIntegerSugar {

    /**
     *
     */
    static final String BASE64 = "0123456789" // 10 characters
      + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"    // + 26 ==> 36
      + "abcdefghijklmnopqrstuvwxyz"    // + 26 ==> 62
      + "=_";                           // + 2 ==> 64

    /**
     *
     */
    static final String YURL32 = "abcdefghijklmnopqrstuvwxyz234567";

    /**
     * prevent instantiation
     */
    private BigIntegerSugar() {
    }

    /**
     * Always gives back a Double This corresponds to the Java floating-point
     * "/" operator and the E "/" operator.
     */
    static public double approxDivide(double self, double arg) {
        return self / arg;
    }

    /**
     * Returns the Unicode character with this character code.
     */
    static public char asChar(BigInteger self) {
        long code = (long)EInt.inRange(self, Long.MIN_VALUE, Long.MAX_VALUE);
        return CharacterMakerSugar.asChar(code);
    }

    /**
     * Returns the "best" IEEE double precision floating point equivalent to
     * this number. If this number is representable in IEEE double precision,
     * then that IEEE value is returned. Otherwise, convert by rounding to
     * even.
     */
    static public double asFloat64(double self) {
        return self;
    }

    /**
     * Used in the expansion of E's ">" operator
     */
    static public boolean aboveZero(BigInteger self) {
        return 0 < self.signum();
    }

    /**
     * Used in the expansion of E's ">=" operator
     */
    static public boolean atLeastZero(BigInteger self) {
        return 0 <= self.signum();
    }

    /**
     * Used in the expansion of E's "<=" operator
     */
    static public boolean atMostZero(BigInteger self) {
        return 0 >= self.signum();
    }

    /**
     * Used in the expansion of E's "<" operator
     */
    static public boolean belowZero(BigInteger self) {
        return 0 > self.signum();
    }

    /**
     * Used for arithmetic equality
     */
    static public boolean isZero(BigInteger self) {
        return 0 == self.signum();
    }

    /**
     * All integers are numbers, so this is always false
     */
    static public boolean isNaN(BigInteger self) {
        return false;
    }

    /**
     * Defined both here and in DoubleSugar, so you can use this to get a
     * ceil'ed integer no matter what kind of number you start with. Similarly,
     * the "doubleValue" message will get you a floating point value no matter
     * what you start with.
     */
    static public BigInteger ceil(BigInteger self) {
        return self;
    }

    /**
     * Overrides the inherited 'op__cmp' to coerce 'o' to an E number.
     * <p/>
     * If o is floating point, then. like other mixed operators, we coerce
     * ourselves to floating point and do a floating comparison.
     */
    static public double op__cmp(BigInteger self, Object o) {
        Number other = (Number)ClassDesc.make(Number.class).coerce(o, null);
        BigInteger optOther = EInt.optBig(other);
        if (null == optOther) {
            return DoubleSugar.op__cmp(self.doubleValue(), o);
        } else {
            return self.compareTo(optOther);
        }
    }

    /**
     * This method supports the C-tradition expectation that unary "~" will
     * work as bit complement.
     * <p/>
     * However, the E style, supported by other set-like abstractions, is to
     * use unary "!" instead.
     *
     * @deprecated Use {@link BigInteger#not()} instead.
     */
    static public BigInteger complement(BigInteger val) {
        return val.not();
    }

    /**
     * Defined both here and in DoubleSugar, so you can use this to get a
     * floor'ed integer no matter what kind of number you start with.
     * Similarly, the "doubleValue" message will get you a floating point value
     * no matter what you start with.
     */
    static public BigInteger floor(BigInteger self) {
        return self;
    }

    /**
     * Always gives an integer, resulting from rounding towards negative
     * infinity, ie, flooring. This corresponds to the E "//" operator.
     */
    static public Number floorDivide(BigInteger self, Number o) {
        BigInteger optOther = EInt.optBig(o);
        if (null == optOther) {
            return DoubleSugar.floorDivide(self.doubleValue(),
                                           o.doubleValue());
        } else {
            BigInteger numerator = self;
            if ((0 > numerator.signum()) != (0 > optOther.signum())) {
                /*
                * Then the mathematical result is negative, and the two
                * have opposite signs. Since we don't care when arg is
                * zero (since we'll get an exception), we take it's
                * signum rather than numerator's.
                *
                * (a // b) == ((a + (b.signum()) - b).truncDivide(b))
                */
                BigInteger fiddle = BigInteger.valueOf(optOther.signum());
                numerator = numerator.add(fiddle).subtract(optOther);
            }
            return numerator.divide(optOther);
        }
    }

    /**
     * In E, andNot() is called butNot()
     */
    static public BigInteger butNot(BigInteger self, BigInteger arg) {
        return self.andNot(arg);
    }

    /**
     * Defined both here and in DoubleSugar, so you can use this to get
     * round'ed integer no matter what kind of number you start with.
     * Similarly, the "doubleValue" message will get you a floating point value
     * no matter what you start with.
     */
    static public BigInteger round(BigInteger self) {
        return self;
    }

    /**
     * Defined both here and in DoubleSugar, so you can use this to get
     * truncate'd integer no matter what kind of number you start with.
     * Similarly, the "doubleValue" message will get you a floating point value
     * no matter what you start with.
     */
    static public BigInteger truncate(BigInteger self) {
        return self;
    }

    /**
     * Always gives an integer resulting from rounding towards zero, ie,
     * truncating. This corresponds to the Java integer "/" operator.
     * BigInteger's existing 'remainder' gives the correct remainder from the
     * truncDivide operation. <p>
     * <p/>
     * <pre>
     *      (a truncDivide b)*b + (a remainder b) == a
     *      [ 5, 3]: ( 1* 3) +  2 ==  5
     *      [ 5,-3]: (-1*-3) +  2 ==  5
     *      [-5, 3]: (-1* 3) + -2 == -5
     *      [-5,-3]: ( 1*-3) + -2 == -5
     * </pre><p>
     * <p/>
     * Therefore, if the result is non-zero, the sign of the result must be the
     * same as the sign of a. This corresponds to the Java and E "%" operator.
     */
    static public Number truncDivide(BigInteger self, Number o) {
        BigInteger optOther = EInt.optBig(o);
        if (null == optOther) {
            return new Double(DoubleSugar.truncDivide(self.doubleValue(),
                                                      o.doubleValue()));
        } else {
            return self.divide(optOther);
        }
    }

    /**
     * The next higher integer
     */
    static public BigInteger next(BigInteger self) {
        return self.add(BigInteger.ONE);
    }

    /**
     * The next lower integer
     */
    static public BigInteger previous(BigInteger self) {
        return self.subtract(BigInteger.ONE);
    }

    /**
     * Convert an integer to a 6-bits-per-char string, with an optional leading
     * minus sign.
     * <p/>
     * A negative integer is encoded as a "-" followed by the encoding of the
     * absolute magnitude. For a positive integer, the encoded length will be
     * (4 * b.length)/3, rounded up to the next integral (non-fractional)
     * character length.
     * <p/>
     * Each 6-bit-unit is encoded by one of the characters <ul> <li>'0'..'9'
     * for 0..9 <li>'A'..'Z' for 10..35 <li>'a'..'z' for 37..61 <li>'='
     * for 62 <li>'_'      for 63 </pre>
     *
     * @return a string which represents the integer in 6-bits-per-char
     *         encoding.
     */
    static public String toString64(BigInteger self) {
        if (0 > self.signum()) {
            return "-" + toString64(self.negate());
        }
        //the base case is non-negative
        byte[] b = toBase2ByteArray(self);
        //The output length is (4 * b.length)/3 rounded up
        StringBuffer ob = new StringBuffer((b.length * 4 + 2) / 3);
        int i;  //The index in b

        for (i = 0; i + 2 < b.length; i += 3) {
            ob.append(BASE64.charAt((b[i] & 0xfc) >> 2));
            ob.append(BASE64.charAt(
              ((b[i] & 0x03) << 4) | ((b[i + 1] & 0xf0) >> 4)));
            ob.append(BASE64.charAt(
              ((b[i + 1] & 0x0f) << 2) | ((b[i + 2] & 0xC0) >> 6)));
            ob.append(BASE64.charAt((b[i + 2] & 0x3F)));
        }
        switch (b.length - i) {
        case 0:
            break;  //String has been encoded.
        case 1:         //One extra byte
            ob.append(BASE64.charAt((b[i] & 0xfc) >> 2));
            ob.append(BASE64.charAt(((b[i] & 0x03) << 4)));
            break;
        case 2:
            ob.append(BASE64.charAt((b[i] & 0xfc) >> 2));
            ob.append(BASE64.charAt(
              ((b[i] & 0x03) << 4) | ((b[i + 1] & 0xf0) >> 4)));
            ob.append(BASE64.charAt(((b[i + 1] & 0x0f) << 2)));
            break;
        default:
            T.fail("Can't have other than 0, 1, or 2");
        }
        return ob.toString();
    }

    /**
     * The 5-bit-per-char encoding to be used in "captp://..." and other YURL
     * schemes.
     * <p/>
     * Negative numbers are shown with an optional leading minus sign.
     *
     * @return
     * @see <a href="http://www.waterken.com/dev/Enc/base32/" >Waterken<font
     *      size="-1"><sup>TM</sup></font> Enc base32 Encoding</a>
     */
    static public String toYURL32(BigInteger self) {
        if (0 > self.signum()) {
            return "-" + toYURL32(self.negate());
        }
        //the base case is non-negative
        byte[] b = toBase2ByteArray(self);
        int numBytes = b.length;
        int biti = 0;
        int numBits = numBytes * 8;
        int numChars = (numBits + 4) / 5;
        StringBuffer ob = new StringBuffer(numChars);
        for (; biti < numBits; biti += 5) {
            int highBytei = biti >> 3;
            int val = (b[highBytei] & 0xFF) << 8;
            int lowBytei = highBytei + 1;
            if (lowBytei < numBytes) {
                val |= (b[lowBytei] & 0xFF);
            }
            int offset = (16 - 5) - (biti & 0x07);
            val >>>= offset;
            int digit32 = val & 0x1F;
            ob.append(YURL32.charAt(digit32));
        }
        return ob.toString();
    }

    /**
     * Returns the base-2 magnitude of self, which must be non-negative.
     * <p/>
     * The returned form is compatible with the {@link BigInteger#BigInteger(int,
     *byte[]) BigInteger(1, byteArray)} constructor
     *
     * @return
     */
    static public byte[] toBase2ByteArray(BigInteger self) {
        T.require(0 <= self.signum(), "Must be non-negative: ", self);
        byte[] result = self.toByteArray();
        int numBytes = result.length;
        if (2 <= numBytes && 0 == result[0]) {
            //In this case, the first byte is an artifact of toByteArray()
            // returning two's complement rather than magnitude, so we remove
            // it.
            byte[] newResult = new byte[numBytes - 1];
            System.arraycopy(result, 1, newResult, 0, numBytes - 1);
            return newResult;
        } else {
            return result;
        }
    }

    /**
     * Actually, a SHA hash.
     * <p/>
     * Hashes the two's complement representation, with the hash as the
     * magnitude of a non-negative number.
     */
    static public BigInteger cryptoHash(BigInteger self) {
        MessageDigest sha = null; // javac can't see notifyFatal won't return
        try {
            sha = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            Trace.comm.errorm("Unable to build SHA", e);
            Trace.comm.notifyFatal();
        }
        return new BigInteger(1, sha.digest(self.toByteArray()));
    }

    ///// Override from BigInteger in order to coerce if arg is float64.///////

    /**
     * Override {@link BigInteger#add} in order to coerce if arg is float64,
     *
     * @return
     */
    static public Number add(BigInteger self, Number o) {
        BigInteger optOther = EInt.optBig(o);
        if (null == optOther) {
            return new Double(DoubleSugar.add(self.doubleValue(),
                                              o.doubleValue()));
        } else {
            return self.add(optOther);
        }
    }

    /**
     * Override {@link BigInteger#max} in order to coerce if arg is float64.
     *
     * @return
     */
    static public Number max(BigInteger self, Number o) {
        BigInteger optOther = EInt.optBig(o);
        if (null == optOther) {
            return new Double(DoubleSugar.max(self.doubleValue(),
                                              o.doubleValue()));
        } else {
            return self.max(optOther);
        }
    }

    /**
     * Override {@link BigInteger#min} in order to coerce if arg is float64.
     *
     * @return
     */
    static public Number min(BigInteger self, Number o) {
        BigInteger optOther = EInt.optBig(o);
        if (null == optOther) {
            return new Double(DoubleSugar.min(self.doubleValue(),
                                              o.doubleValue()));
        } else {
            return self.min(optOther);
        }
    }

    /**
     * Override {@link BigInteger#multiply} in order to coerce if arg is
     * float64.
     *
     * @return
     */
    static public Number multiply(BigInteger self, Number o) {
        BigInteger optOther = EInt.optBig(o);
        if (null == optOther) {
            return new Double(DoubleSugar.multiply(self.doubleValue(),
                                                   o.doubleValue()));
        } else {
            return self.multiply(optOther);
        }
    }

    /**
     * Override {@link BigInteger#pow} in order to coerce if arg is float64.
     *
     * @return
     */
    static public Number pow(BigInteger self, Number o) {
        BigInteger optOther = EInt.optBig(o);
        if (null == optOther) {
            return new Double(DoubleSugar.pow(self.doubleValue(),
                                              o.doubleValue()));
        } else {
            return self.pow(E.asInt(optOther));
        }
    }

    /**
     * Override {@link BigInteger#remainder} in order to coerce if arg is
     * float64.
     *
     * @return
     */
    static public Number remainder(BigInteger self, Number o) {
        BigInteger optOther = EInt.optBig(o);
        if (null == optOther) {
            return new Double(DoubleSugar.remainder(self.doubleValue(),
                                                    o.doubleValue()));
        } else {
            return self.remainder(optOther);
        }
    }

    /**
     * Override {@link BigInteger#subtract} in order to coerce if arg is
     * float64.
     *
     * @return
     */
    static public Number subtract(BigInteger self, Number o) {
        BigInteger optOther = EInt.optBig(o);
        if (null == optOther) {
            return new Double(DoubleSugar.subtract(self.doubleValue(),
                                                   o.doubleValue()));
        } else {
            return self.subtract(optOther);
        }
    }

    /**
     * Remainder from the floorDivide operation.
     * <p><pre>
     *     (a // b)*b + (a %% b) == a
     *      [ 5, 3]: ( 1* 3) +  2 ==  5
     *      [ 5,-3]: (-2*-3) + -1 ==  5
     *      [-5, 3]: (-2* 3) +  1 == -5
     *      [-5,-3]: ( 1*-3) + -2 == -5
     * </pre><p>
     * Therefore, if the result is non-zero, the sign of the result must be the
     * same as the sign of b, and so the result ranges from 0 inclusive to b
     * exclusive. This corresponds to the E "%%" operator. When b >= 0, it also
     * corresponds to Java's BigInteger.mod().
     */
    static public Number mod(BigInteger self, Number o) {
        BigInteger optOther = EInt.optBig(o);
        if (null == optOther) {
            return new Double(DoubleSugar.mod(self.doubleValue(),
                                              o.doubleValue()));
        } else {
            BigInteger result = self.remainder(optOther);
            int otherSig = optOther.signum();
            int resultSig = result.signum();
            if (((0 > otherSig) != (0 > resultSig)) && 0 != resultSig) {
                return result.add(optOther);
            }
            return result;
            // Should be equivalent, but is more expensive
//            return subtract(self,
//                            multiply((BigInteger)floorDivide(self, o),
//                                     o));
        }
    }

    /**
     * Override {@link BigInteger#modPow} in order to coerce if arg is
     * float64.
     *
     * @return
     */
    static public Number modPow(BigInteger self, Number exp, Number modulus) {
        BigInteger optExp = EInt.optBig(exp);
        BigInteger optModulus = EInt.optBig(modulus);
        if (null == optExp || null == optModulus) {
            return new Double(DoubleSugar.modPow(self.doubleValue(),
                                                 exp.doubleValue(),
                                                 modulus.doubleValue()));
        } else {
            return self.modPow(optExp, optModulus);
        }
    }
}
