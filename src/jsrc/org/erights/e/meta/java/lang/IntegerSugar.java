package org.erights.e.meta.java.lang;

import org.erights.e.meta.java.math.BigIntegerSugar;
import org.erights.e.meta.java.math.EInt;

import java.math.BigInteger;

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


/**
 * A sweetener defining extra messages that may be e-sent to a 32-bit integer.
 *
 * @author Mark S. Miller
 */
public class IntegerSugar {

    /**
     * prevent instantiation
     */
    private IntegerSugar() {
    }

    /**
     * Always gives back a Double This corresponds to the Java floating-point
     * "/" operator and the E "/" operator.
     */
    static public double approxDivide(int self, double arg) {
        return (double)self / arg;
    }

    /**
     * Returns the Unicode character with this character code.
     */
    static public char asChar(int self) {
        return CharacterMakerSugar.asChar(self);
    }

    /**
     * Returns the IEEE double precision floating point equivalent to this
     * number.
     */
    static public double asFloat64(int self) {
        return (double)self;
    }

    /**
     * Used in the expansion of E's ">" operator
     */
    static public boolean aboveZero(int self) {
        return self > 0;
    }

    /**
     * Used in the expansion of E's ">=" operator
     */
    static public boolean atLeastZero(int self) {
        return self >= 0;
    }

    /**
     * Used in the expansion of E's "<=" operator
     */
    static public boolean atMostZero(int self) {
        return self <= 0;
    }

    /**
     * Used in the expansion of E's "<" operator
     */
    static public boolean belowZero(int self) {
        return self < 0;
    }

    /**
     * Used for arithmetic equality
     */
    static public boolean isZero(int self) {
        return self == 0;
    }

    /**
     * All integers are numbers, so this is always false
     */
    static public boolean isNaN(int self) {
        return false;
    }

    /**
     * Defined both here and in DoubleSugar, so you can use this to get a
     * ceil'ed integer no matter what kind of number you start with. Similarly,
     * the "doubleValue" message will get you a floating point value no matter
     * what you start with.
     */
    static public int ceil(int self) {
        return self;
    }

    /**
     * This method supports the C-tradition expectation that unary "~" will
     * work as bit complement.
     * <p/>
     * However, the E style, supported by other set-like abstractions, is to
     * use unary "!" instead.
     *
     * @deprecated Use {@link #not(int)} instead.
     */
    static public int complement(int self) {
        return ~self;
    }

    /**
     * Defined both here and in DoubleSugar, so you can use this to get a
     * floor'ed integer no matter what kind of number you start with.
     * Similarly, the "doubleValue" message will get you a floating point value
     * no matter what you start with.
     */
    static public int floor(int self) {
        return self;
    }

    /**
     * Always gives an integer, resulting from rounding towards negative
     * infinity, ie, flooring. This corresponds to the E "//" operator.
     */
    static public Number floorDivide(int self, Number o) {
        if (EInt.intValueOk(o)) {
            int other = o.intValue();
            long numerator = self;
            if (other < 0) {
                if (numerator >= 0) {
                    // 5 // -3 == -2
                    // (5 - 1 - -3).truncDivide(3) == -2
                    numerator = numerator - 1 - other;
                }
                // else -5 // -3 == (-5).truncDivide(-3) == 1
            } else {
                if (numerator < 0) {
                    // -5 // 3 == -2
                    // (-5 + 1 - 3).truncDivide(3) == -2
                    numerator = numerator + 1 - other;
                }
                // else 5 // 3 == 5.truncDivide(3) == 1
            }
            return EInt.valueOf(numerator / other);
        } else {
            return BigIntegerSugar.floorDivide(BigInteger.valueOf(self), o);
        }
    }

    /**
     * In E, {@link java.math.BigInteger#andNot andNot} is called butNot()
     */
    static public Number butNot(int self, Number o) {
        if (EInt.isInInt32(o, null)) {
            int other = o.intValue();
            return EInt.valueOf(self & ~other);
        } else {
            return BigIntegerSugar.butNot(BigInteger.valueOf(self),
                                          EInt.big(o));
        }
    }

    /**
     * Remainder from the floorDivide operation. <p>
     * <pre>
     *     (a // b)*b + (a %% b) == a
     *      [ 5, 3]: ( 1* 3) +  2 ==  5
     *      [ 5,-3]: (-2*-3) + -1 ==  5
     *      [-5, 3]: (-2* 3) +  1 == -5
     *      [-5,-3]: ( 1*-3) + -2 == -5
     * </pre><p>
     * Therefore, if the result is non-zero, the sign of the result must be the
     * same as the sign of b, and so the result ranges from 0 inclusive to b
     * exclusive. This corresponds to the E "%%" operator. When b >= 0, it also
     * corresponds to Java's int.mod().
     */
    static public Number mod(int self, Number o) {
        if (EInt.intValueOk(o)) {
            long other = o.intValue();
            long result = self % other;
            if (((other < 0) != (result < 0)) && result != 0) {
                result += other;
            }
            return EInt.valueOf(result);
            // Should be equivalent, but is more expensive
//            return subtract(self,
//                            multiply(floorDivide(self, o).intValue(),
//                                     o));
        } else {
            return BigIntegerSugar.mod(BigInteger.valueOf(self), o);
        }
    }

    /**
     * Defined both here and in DoubleSugar, so you can use this to get
     * round'ed integer no matter what kind of number you start with.
     * Similarly, the "doubleValue" message will get you a floating point value
     * no matter what you start with.
     */
    static public int round(int self) {
        return self;
    }

    /**
     * Defined both here and in DoubleSugar, so you can use this to get
     * truncate'd integer no matter what kind of number you start with.
     * Similarly, the "doubleValue" message will get you a floating point value
     * no matter what you start with.
     */
    static public int truncate(int self) {
        return self;
    }

    /**
     * Always gives an integer resulting from rounding towards zero, ie,
     * truncating. This corresponds to the Java integer "/" operator. int's
     * existing 'remainder' gives the correct remainder from the truncDivide
     * operation. <p>
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
    static public Number truncDivide(int self, Number o) {
        if (EInt.intValueOk(o)) {
            int other = o.intValue();
            return EInt.valueOf(self / other);
        } else {
            return BigIntegerSugar.truncDivide(BigInteger.valueOf(self), o);
        }
    }

    /**
     * The next higher integer
     */
    static public Number next(int self) {
        return EInt.valueOf(1 + (long)self);
    }

    /**
     * The next lower integer
     */
    static public Number previous(int self) {
        return EInt.valueOf(-1 + (long)self);
    }

    /**
     * @return
     */
    static public String toString64(BigInteger self) {
        return BigIntegerSugar.toString64(self);
    }

    /**
     * @param self
     * @return
     */
    static public String toYURL32(BigInteger self) {
        return BigIntegerSugar.toYURL32(self);
    }

    ////////////////////////// Methods from BigInteger ////////////////////

    /**
     * @param self
     * @return
     */
    static public byte[] toBase2ByteArray(BigInteger self) {
        return BigIntegerSugar.toBase2ByteArray(self);
    }

    /**
     * Actually, a SHA hash
     *
     * @param self
     * @return
     */
    static public BigInteger cryptoHash(BigInteger self) {
        return BigIntegerSugar.cryptoHash(self);
    }

    /**
     *
     */
    static public Number abs(int self) {
        if (self < 0) {
            return EInt.valueOf(-(long)self);
        } else {
            return EInt.valueOf(self);
        }
    }

    /**
     *
     */
    static public Number add(int self, Number o) {
        if (EInt.intValueOk(o)) {
            int other = o.intValue();
            return EInt.valueOf((long)self + other);
        } else {
            return BigIntegerSugar.add(BigInteger.valueOf(self), o);
        }
    }

    /**
     * @return
     */
    static public int bitCount(BigInteger self) {
        return self.bitCount();
    }

    /**
     * @return
     */
    static public int bitLength(BigInteger self) {
        return self.bitLength();
    }

    /**
     *
     */
    static public double op__cmp(int self, Object o) {
        if (o instanceof Integer) {
            int other = ((Integer)o).intValue();
            if (self < other) {
                return -1.0;
            } else if (self == other) {
                return 0.0;
            } else {
                return 1.0;
            }
        } else {
            return BigIntegerSugar.op__cmp(BigInteger.valueOf(self), o);
        }
    }

    /**
     * @return
     */
    static public BigInteger gcd(BigInteger self, BigInteger other) {
        return self.gcd(other);
    }

    /**
     * @return
     */
    static public int getLowestSetBit(BigInteger self) {
        return self.getLowestSetBit();
    }

    /**
     *
     */
    static public Number max(int self, Number o) {
        if (EInt.intValueOk(o)) {
            int other = o.intValue();
            if (self >= other) {
                return EInt.valueOf(self);
            } else {
                return o;
            }
        } else {
            return BigIntegerSugar.max(BigInteger.valueOf(self), o);
        }
    }

    /**
     *
     */
    static public Number min(int self, Number o) {
        if (EInt.intValueOk(o)) {
            int other = o.intValue();
            if (self <= other) {
                return EInt.valueOf(self);
            } else {
                return o;
            }
        } else {
            return BigIntegerSugar.min(BigInteger.valueOf(self), o);
        }
    }

    /**
     * @return
     */
    static public BigInteger modInverse(BigInteger self, BigInteger m) {
        return self.modInverse(m);
    }

    /**
     * @return
     */
    static public Number modPow(BigInteger self, Number exponent, Number m) {
        return BigIntegerSugar.modPow(self, exponent, m);
    }

    /**
     *
     */
    static public Number multiply(int self, Number o) {
        if (EInt.intValueOk(o)) {
            int other = o.intValue();
            return EInt.valueOf((long)self * (long)other);
        } else {
            return BigIntegerSugar.multiply(BigInteger.valueOf(self), o);
        }
    }

    /**
     *
     */
    static public Number negate(int self) {
        return EInt.valueOf(-(long)self);
    }

    /**
     * Bit complement
     */
    static public int not(int self) {
        return ~self;
    }

    /**
     *
     */
    static public Number and(int self, Number o) {
        if (EInt.isInInt32(o, null)) {
            int other = o.intValue();
            return EInt.valueOf(self & other);
        } else {
            return BigInteger.valueOf(self).and(EInt.big(o));
        }
    }

    /**
     *
     */
    static public Number or(int self, Number o) {
        if (EInt.isInInt32(o, null)) {
            int other = o.intValue();
            return EInt.valueOf(self | other);
        } else {
            return BigInteger.valueOf(self).or(EInt.big(o));
        }
    }

    /**
     * @return
     */
    static public Number pow(BigInteger self, Number exponent) {
        return BigIntegerSugar.pow(self, exponent);
    }

    /**
     *
     */
    static public Number remainder(int self, Number o) {
        if (EInt.intValueOk(o)) {
            int other = o.intValue();
            return EInt.valueOf(self % other);
        } else {
            return BigIntegerSugar.remainder(BigInteger.valueOf(self), o);
        }
    }

    /**
     * @return
     */
    static public Number shiftLeft(BigInteger self, int n) {
        return EInt.normal(self.shiftLeft(n));
    }

    /**
     * @return
     */
    static public Number shiftRight(BigInteger self, int n) {
        return EInt.normal(self.shiftRight(n));
    }

    /**
     *
     */
    static public int signum(int self) {
        if (self < 0) {
            return -1;
        } else if (self == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     *
     */
    static public Number subtract(int self, Number o) {
        if (EInt.intValueOk(o)) {
            int other = o.intValue();
            return EInt.valueOf((long)self - (long)other);
        } else {
            return BigIntegerSugar.subtract(BigInteger.valueOf(self), o);
        }
    }

    /**
     * @return
     */
    static public byte[] toByteArray(BigInteger self) {
        return self.toByteArray();
    }

    /**
     * @return
     */
    static public String toString(BigInteger self, int radix) {
        return self.toString(radix);
    }

    /**
     *
     */
    static public Number xor(int self, Number o) {
        if (EInt.isInInt32(o, null)) {
            int other = o.intValue();
            return EInt.valueOf(self ^ other);
        } else {
            return BigInteger.valueOf(self).xor(EInt.big(o));
        }
    }
}
