package org.erights.e.meta.java.lang;

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
import org.erights.e.elib.prim.E;
import org.erights.e.meta.java.math.EInt;

/**
 * A sweetener defining extra messages that may be e-sent to a floating point
 * number.
 *
 * @author Mark S. Miller
 */
public class DoubleSugar {

    /**
     * prevent instantiation
     */
    private DoubleSugar() {
    }

    /**
     * Used in the expansion of E's ">" operator. NaN says false
     */
    static public boolean aboveZero(double self) {
        return self > 0.0;
    }

    /**
     *
     */
    static public double abs(double num) {
        if (num >= 0.0) {
            return num;
        } else {
            return -num;
        }
    }

    /**
     *
     */
    static public double acos(double self) {
        return StrictMath.acos(self);
    }

    /**
     *
     */
    static public double add(double self, double arg) {
        return self + arg;
    }

    /**
     * Always gives back a double
     * This corresponds to the Java floating-point "/" operator
     * and the E "/" operator.
     */
    static public double approxDivide(double self, double arg) {
        return self / arg;
    }

    /**
     *
     */
    static public double asin(double self) {
        return StrictMath.asin(self);
    }

    /**
     *
     */
    static public double atan(double self) {
        return StrictMath.atan(self);
    }

    /**
     *
     */
    static public double atan2(double self, double arg) {
        return StrictMath.atan2(self, arg);
    }

    /**
     * Used in the expansion of E's ">=" operator. NaN says false
     */
    static public boolean atLeastZero(double self) {
        return self >= 0.0;
    }

    /**
     * Used in the expansion of E's "<=" operator. NaN says false
     */
    static public boolean atMostZero(double self) {
        return self <= 0.0;
    }

    /**
     * Used in the expansion of E's "<" operator. NaN says false
     */
    static public boolean belowZero(double self) {
        return self < 0.0;
    }

    /**
     * @return An integer of some type
     */
    static public Number ceil(double self) {
        return EInt.valueOf((long)StrictMath.ceil(self));
    }

    /**
     * Used in the expansion of E's comparison operators. As is Java, is E,
     * NaNs are incomparable to everything else, and so yield a NaN as an
     * answer.
     */
    static public double op__cmp(double self, Object o) {
        double arg = E.asFloat64(o);
        //XXX would "return self - arg;" be sufficient?  I think so.
        if (self < arg) {
            return -1.0;
        } else if (self > arg) {
            return 1.0;
        } else if (self == arg) {
            return 0.0;
        } else {
            //NaN indicates incomparable
            return 0.0 / 0.0;
        }
    }

    /**
     *
     */
    static public double cos(double self) {
        return StrictMath.cos(self);
    }

    /**
     *
     */
    static public double exp(double self) {
        return StrictMath.exp(self);
    }

    /**
     * @return An integer of some type
     */
    static public Number floor(double self) {
        return EInt.valueOf((long)StrictMath.floor(self));
    }

    /**
     * Always gives an integer, resulting from rounding towards negative
     * infinity, ie, flooring. This corresponds to the E "//" operator.
     *
     * @return An integer of some type
     */
    static public Number floorDivide(double self, double arg) {
        return EInt.valueOf((long)StrictMath.floor(self / arg));
    }

    /**
     * Used for arithmetic equality. NaN says false
     */
    static public boolean isZero(double self) {
        return self == 0.0;
    }

    /**
     *
     */
    static public double log(double self) {
        return StrictMath.log(self);
    }

    /**
     *
     */
    static public double max(double self, double arg) {
        return StrictMath.max(self, arg);
    }

    /**
     *
     */
    static public double min(double self, double arg) {
        return StrictMath.min(self, arg);
    }

    /**
     * Remainder from the floorDivide operation. <p>
     * <pre>
     *     (a floorDivide b)*b + (a modulo b) == a
     *      [ 5, 3]: ( 1* 3) +  2 ==  5
     *      [ 5,-3]: (-2*-3) + -1 ==  5
     *      [-5, 3]: (-2* 3) +  1 == -5
     *      [-5,-3]: ( 1*-3) + -2 == -5
     * </pre><p>
     * Therefore, if the result is non-zero, the sign of the result must be
     * the same as the sign of b, and so the result ranges from 0 inclusive
     * to b exclusive. This corresponds to the E "%%" operator. When
     * b >= 0, it also corresponds to Java's BigInteger.mod().
     */
    static public double mod(double self, double arg) {
        double result = self % arg;
        if (((arg < 0.0) != (result < 0.0)) && result != 0.0) {
            return result + arg;
        } else {
            return result;
        }
    }

    /**
     *
     */
    static public double modPow(double self, double exp, double modulus) {
        return mod(StrictMath.pow(self, exp), modulus);
    }

    /**
     *
     */
    static public double multiply(double self, double arg) {
        return self * arg;
    }

    /**
     *
     */
    static public double negate(double self) {
        return -self;
    }

    /**
     *
     */
    static public double pow(double self, double arg) {
        return StrictMath.pow(self, arg);
    }

    /**
     * Remainder from truncDivide operation. <p>
     * <pre>
     *      (a truncDivide b)*b + (a remainder b) == a
     *      [ 5, 3]: ( 1* 3) +  2 ==  5
     *      [ 5,-3]: (-1*-3) +  2 ==  5
     *      [-5, 3]: (-1* 3) + -2 == -5
     *      [-5,-3]: ( 1*-3) + -2 == -5
     * </pre><p>
     * Therefore, if the result is non-zero, the sign of the result must
     * be the same as the sign of a. This corresponds to the Java and
     * E "%" operator.
     */
    static public double remainder(double self, double arg) {
        return self % arg;
    }

    /**
     * @return An integer of some type
     */
    static public Number round(double self) {
        return EInt.valueOf(StrictMath.round(self));
    }

    /**
     *
     */
    static public double sin(double self) {
        return StrictMath.sin(self);
    }

    /**
     *
     */
    static public double sqrt(double self) {
        return StrictMath.sqrt(self);
    }

    /**
     *
     */
    static public double subtract(double self, double arg) {
        return self - arg;
    }

    /**
     *
     */
    static public double tan(double self) {
        return StrictMath.tan(self);
    }

    /**
     * @return An integer of some type
     */
    static public Number truncate(double self) {
        return EInt.valueOf((long)self);
    }

    /**
     * Always gives an integer resulting from rounding towards zero,
     * ie, truncating. This corresponds to the Java integer "/" operator.
     */
    static public long truncDivide(double self, double arg) {
        return (long)(self / arg);
    }

    /**
     * Detect whether this jvm has a known bug in reporting floating
     * point representation.
     */
    static private final long Minus3Bits = Double.doubleToLongBits(-3.0);

    /**
     *
     */
    static private final double AfterMinus3
      = Double.longBitsToDouble(Minus3Bits + 1);

    /**
     *
     */
    static private final boolean NegRepBug = AfterMinus3 < -3.0;

    /**
     * Infinity.next() == Infinity <p>
     * NaN.next() == NaN <p>
     * Double.getMAX_VALUE().next() == Infinity <p>
     * (-Infinity).next() == -(Double.getMAX_VALUE()) <p>
     * otherwise, *num*.next() == *next representable number*
     */
    static public double next(double self) {
        if (NegRepBug && self < 0.0) {
            T.fail("jvm internal bug: not IEEE representation");
        }
        if (Double.isNaN(self) || Double.POSITIVE_INFINITY == self) {
            return self;
        } else if (Double.MAX_VALUE == self) {
            return Double.POSITIVE_INFINITY;
        } else if (Double.NEGATIVE_INFINITY == self) {
            return -Double.MAX_VALUE;
        }
        long bits = Double.doubleToLongBits(self);
        bits++;
        double result = Double.longBitsToDouble(bits);
        if (result <= self) {
            T.fail("jvm IEEE representation bug: (" +
                   self + ").next() gave " + result +
                   ". Please report values.");
        }
        return result;
    }

    /**
     * (-Infinity).previous() == -Infinity <p>
     * NaN.previous() == NaN <p>
     * -(Double.getMAX_VALUE()).previous() == -Infinity <p>
     * Infinity.previous() == Double.getMAX_VALUE()<p>
     * otherwise, *num*.previous() == *previous representable number*
     */
    static public double previous(double self) {
        if (NegRepBug && self <= 0.0) {
            T.fail("jvm internal bug: not IEEE representation");
        }
        if (Double.isNaN(self) || Double.NEGATIVE_INFINITY == self) {
            return self;
        } else if (-Double.MAX_VALUE == self) {
            return Double.NEGATIVE_INFINITY;
        } else if (Double.POSITIVE_INFINITY == self) {
            return Double.MAX_VALUE;
        }
        long bits = Double.doubleToLongBits(self);
        bits--;
        double result = Double.longBitsToDouble(bits);
        if (result >= self) {
            T.fail("jvm IEEE representation bug: (" +
                   self + ").previous() gave " + result +
                   ". Please report values.");
        }
        return result;
    }
}
