// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.java.math;

import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.util.OneArgFunc;

import java.math.BigInteger;
import java.util.Random;

/**
 * Represents the type of E integers, which can concretely be represented by
 * any of {@link Byte}, {@link Short}, {@link Integer}, {@link Long}, or {@link
 * BigInteger}.
 * <p/>
 * From the point of view of the E language programmer, none of these more
 * specific types exist as types. (As guards, all but BigInteger serve as
 * subranges of EInt.) From the point of view of the ELib coercion machinery,
 * EInt is a virtual supertype of these more specific types.
 * <p/>
 * An EInt in <i>normal</i> form:<ul> <li>is an Integer if it fits in an
 * Integer, i.e., if it's between {@link Integer#MIN_VALUE}..{@link
 * Integer#MAX_VALUE}. <li>Else it's a BigInteger. </ul> In addition, if the
 * integer's value fits in a byte, i.e., if it's between -128 and 127, then
 * EInt will avoid allocating a new one by returning a preallocated one.
 * However, a newly allocated Integer is still in normal form.
 * <p/>
 * EInt itself cannot be instantiated or subclassed.
 * <p/>
 * The 6-bits-per-char conversion code is adapted from the old Base64.java.
 *
 * @author Mark S. Miller
 * @author Bill Frantz
 */
public abstract class EInt extends Number {

    static private final long serialVersionUID = 1263208324852908793L;

    static private final BigInteger MIN_INTEGER =
      BigInteger.valueOf(Integer.MIN_VALUE);

    static private final BigInteger MAX_INTEGER =
      BigInteger.valueOf(Integer.MAX_VALUE);

    static private final Integer[] BYTES = new Integer[256];

    static {
        for (int i = 0; i < 256; i++) {
            BYTES[i] = new Integer(Byte.MIN_VALUE + i);
        }
    }

    /**
     *
     */
    static private final String BASE64 = BigIntegerSugar.BASE64;

    /**
     * prevent instantiation
     */
    private EInt() {
    }

    /**
     * Convert a 6-bits-per-char string to an EInt in normal form.
     *
     * @param s The string in 6-bits-per-char notation, with an optional
     *          leading '-', in the format produced by {@link
     *          BigIntegerSugar#toString64(BigInteger)}.
     * @return an EInt in normal form.
     * @throws NumberFormatException if there is an invalid 6-bits-per-char
     *                               character in the input string.
     */
    static public Number fromString64(String s) throws NumberFormatException {

        s = s.trim();
        if (s.length() >= 1 && s.charAt(0) == '-') {
            Number result = fromString64(s.substring(1));
            return normal(big(result).negate());
        }
        //base case has no leading '-'
        byte[] b = new byte[(s.length() * 3) / 4];
        int retIndex = 0;
        char[] in = s.toCharArray();
        for (int i = 0; ;) {
            int val1 = BASE64.indexOf(in[i]);
            if (val1 < 0) {
                throw new NumberFormatException("Invalid character '" + in[i] +
                  "' in 6-bits-per-char string");
            }
            if (++i == in.length) {
                break;
            }

            int val2 = BASE64.indexOf(in[i]);
            if (val2 < 0) {
                throw new NumberFormatException("Invalid character '" + in[i] +
                  "' in 6-bits-per-char string");
            }
            // 6 val1Bits + 2 val2Bits
            b[retIndex++] = (byte)((val1 << 2) + (val2 >> 4));

            if (++i == in.length) {
                break;
            }

            int val3 = BASE64.indexOf(in[i]);
            if (val3 < 0) {
                throw new NumberFormatException("Invalid character '" + in[i] +
                  "' in 6-bits-per-char string");
            }
            // 4 val2Bits + 4 val3Bits
            b[retIndex++] = (byte)(((val2 << 4) & 0xf0) + (val3 >> 2));

            if (++i == in.length) {
                break;
            }

            int val4 = BASE64.indexOf(in[i]);
            if (val4 < 0) {
                throw new NumberFormatException("Invalid character ' '" +
                  in[i] + "'' in 6-bits-per-char string");
            }
            // 2 val3Bits + 6 val4Bits
            b[retIndex++] = (byte)(((val3 << 6) & 0xc0) + val4);

            if (++i == in.length) {
                break;
            }
        }
        return EInt.run(1, b);
    }

    /**
     * Convert a YURL32 string to an EInt in normal form.
     *
     * @param s The string in YURL32 notation, with an optional leading '-', in
     *          the format produced by {@link BigIntegerSugar#toYURL32(BigInteger)}.
     * @return an EInt in normal form.
     * @throws NumberFormatException if there is an invalid YURL32 character in
     *                               the input string.
     */
    static public Number fromYURL32(String s) throws NumberFormatException {

        s = s.trim();
        int numChars = s.length();
        if (numChars >= 1 && s.charAt(0) == '-') {
            Number result = fromYURL32(s.substring(1));
            return normal(big(result).negate());
        }
        int numBits = numChars * 5;
        int numBytes = numBits / 8; //Note: rounding down, not up!
        byte[] b = new byte[numBytes];
        for (int chari = 0; chari < numChars; chari++) {
            char c = Character.toLowerCase(s.charAt(chari));
            int val;
            if (c >= 'a' && c <= 'z') {
                val = c - 'a';
            } else if (c >= '2' && c <= '7') {
                val = 26 + c - '2';
            } else {
                throw new NumberFormatException(
                  "Invalid character '" + c + "' in 5-bits-per-char string");
            }
            int biti = chari * 5;
            int highBytei = biti >> 3;
            int offset = (16 - 5) - (biti & 0x07);
            val <<= offset;
            b[highBytei] |= (val >>> 8);
            int lowByte = val & 0xFF;
            if (lowByte != 0) {
                int lowBytei = highBytei + 1;
                if (lowBytei < numBytes) {
                    b[lowBytei] |= lowByte;
                } else {
                    throw new NumberFormatException(
                      "Trailing bits in in 5-bits-per-char string: " + s);
                }
            }
        }
        return EInt.run(1, b);
    }

    /**
     * Like {@link BigInteger#valueOf}, but returns an EInt in normal form.
     *
     * @return
     */
    static public Number valueOf(long val) {
        if (val >= Byte.MIN_VALUE && val <= Byte.MAX_VALUE) {
            return BYTES[(int)val - Byte.MIN_VALUE];
        }
        if (val >= Integer.MIN_VALUE && val <= Integer.MAX_VALUE) {
            return new Integer((int)val);
        }
        return BigInteger.valueOf(val);
    }

    /**
     * optEjector defaults to null.
     *
     * @return
     */
    static public Number normal(Number eInt) {
        return normal(eInt, null);
    }

    /**
     * Converts an EInt to normal form.
     * <p/>
     * If it's not an EInt, complain.
     *
     * @return
     */
    static public Number normal(Number eInt, OneArgFunc optEjector) {
        Class clazz = eInt.getClass();
        if (Integer.class == clazz) {
            // already normal form. Since it's already allocated, we don't
            // need to check whether we can preallocate it.
            return eInt;
        } else if (BigInteger.class == clazz) {
            BigInteger bigInt = (BigInteger)eInt;
            if (bigInt.compareTo(MIN_INTEGER) >= 0 &&
              bigInt.compareTo(MAX_INTEGER) <= 0) {

                return valueOf(bigInt.intValue());
            } else {
                return bigInt;
            }
        } else if (Byte.class == clazz) {
            return BYTES[eInt.byteValue() - Byte.MIN_VALUE];
        } else if (Long.class == clazz) {
            return valueOf(eInt.longValue());
        } else if (Short.class == clazz) {
            int val = eInt.shortValue();
            if (val >= Byte.MIN_VALUE && val <= Byte.MAX_VALUE) {
                return BYTES[val - Byte.MIN_VALUE];
            } else {
                return new Integer(val);
            }
        } else {
            ClassCastException prob = new ClassCastException(
              "Must be an EInt: " + clazz + "(" + eInt + ")");
            throw Thrower.toEject(optEjector, prob);
        }
    }

    /**
     * optEjector defaults to null.
     *
     * @return
     */
    static public BigInteger big(Number eInt) {
        return big(eInt, null);
    }

    /**
     * Converts an EInt to BigInteger form.
     * <p/>
     * If it's not an EInt, complain.
     *
     * @return
     */
    static public BigInteger big(Number eInt, OneArgFunc optEjector) {
        Class clazz = eInt.getClass();
        if (BigInteger.class == clazz) {
            return (BigInteger)eInt;
        } else if (Integer.class == clazz || Byte.class == clazz ||
          Long.class == clazz || Short.class == clazz) {

            return BigInteger.valueOf(eInt.longValue());
        } else {
            ClassCastException prob = new ClassCastException(
              "Must be an EInt: " + clazz + "(" + eInt + ")");
            throw Thrower.toEject(optEjector, prob);
        }
    }

    /**
     * optEjector defaults to null.
     *
     * @return
     */
    static public BigInteger optBig(Number num) {
        return optBig(num, null);
    }

    /**
     * If num is a primitive E number is an integer, coverts to BigInteger
     * form.
     * <p/>
     * If its an E floating point value, then return null, in which case the
     * caller knows they can use {@link Number#doubleValue} on it safely.
     * <p/>
     * If it's not a primitive E number, complain.
     *
     * @return
     */
    static public BigInteger optBig(Number num, OneArgFunc optEjector) {
        Class clazz = num.getClass();
        if (BigInteger.class == clazz) {
            return (BigInteger)num;
        } else if (Integer.class == clazz || Byte.class == clazz ||
          Long.class == clazz || Short.class == clazz) {

            return BigInteger.valueOf(num.longValue());
        } else if (Double.class == clazz || Float.class == clazz) {
            return null;
        } else {
            ClassCastException prob = new ClassCastException(
              "Must be a primitive E number: " + clazz + "(" + num + ")");
            throw Thrower.toEject(optEjector, prob);
        }
    }

    /**
     * optEjector defaults to null.
     */
    static public long inRange(Number eInt, long minValue, long maxValue) {
        return inRange(eInt, minValue, maxValue, null);
    }

    /**
     * Given that eInt is between minValue and maxValue, return it as a long.
     * <p/>
     * Otherwise, complain.
     *
     * @return
     */
    static public long inRange(Number eInt,
                               long minValue,
                               long maxValue,
                               OneArgFunc optEjector) {
        if (isInRange(eInt, minValue, maxValue, optEjector)) {
            return eInt.longValue();
        } else {
            throw Thrower.toEject(optEjector,
                                  "" + eInt + " must be in " + minValue +
                                    ".." + maxValue);
        }
    }

    /**
     * optEjector defaults to null.
     *
     * @return
     */
    static public boolean isInRange(Number eInt,
                                    long minValue,
                                    long maxValue) {
        return isInRange(eInt, minValue, maxValue, null);
    }

    /**
     * Does eInt represent an EInt between minValue and maxValue?
     * <p/>
     * If it represents an EInt, return whether it's in range. If it doesn't
     * represent an EInt, complain according to optEjector.
     *
     * @return
     */
    static public boolean isInRange(Number eInt,
                                    long minValue,
                                    long maxValue,
                                    OneArgFunc optEjector) {
        Class clazz = eInt.getClass();
        if (BigInteger.class == clazz) {
            BigInteger bigInt = (BigInteger)eInt;
            return bigInt.compareTo(BigInteger.valueOf(minValue)) >= 0 &&
              bigInt.compareTo(BigInteger.valueOf(maxValue)) <= 0;

        } else if (Integer.class == clazz || Byte.class == clazz ||
          Long.class == clazz || Short.class == clazz) {

            long val = eInt.longValue();
            return val >= minValue && val <= maxValue;
        } else {
            ClassCastException prob = new ClassCastException(
              "Must be an EInt: " + clazz + "(" + eInt + ")");
            throw Thrower.toEject(optEjector, prob);
        }
    }

    /**
     * Like {@link #isInInt32(Number,OneArgFunc) isInInt32(num, null)}, except
     * that if num isn't an EInt, this returns false rather than complaining.
     *
     * @return
     */
    static public boolean intValueOk(Number num) {
        Class clazz = num.getClass();
        if (Integer.class == clazz || Byte.class == clazz ||
          Short.class == clazz) {
            return true;

        } else if (BigInteger.class == clazz) {
            BigInteger bigInt = (BigInteger)num;

            return bigInt.compareTo(MIN_INTEGER) >= 0 &&
              bigInt.compareTo(MAX_INTEGER) <= 0;

        } else if (Long.class == clazz) {
            long val = num.longValue();
            return val >= Integer.MIN_VALUE && val <= Integer.MAX_VALUE;
        } else {
            return false;
        }
    }

    /**
     * Does eInt represent an EInt between {@link Integer#MIN_VALUE} and {@link
     * Integer#MAX_VALUE}?
     * <p/>
     * If it represents an EInt, return whether it's in range, in which case
     * {@link Number#intValue eInt.intValue()} will give a valid answer. If it
     * doesn't represent an EInt, complain according to optEjector.
     *
     * @return
     */
    static public boolean isInInt32(Number eInt, OneArgFunc optEjector) {
        Class clazz = eInt.getClass();
        if (Integer.class == clazz || Byte.class == clazz ||
          Short.class == clazz) {
            return true;

        } else if (BigInteger.class == clazz) {
            BigInteger bigInt = (BigInteger)eInt;

            return bigInt.compareTo(MIN_INTEGER) >= 0 &&
              bigInt.compareTo(MAX_INTEGER) <= 0;

        } else if (Long.class == clazz) {
            long val = eInt.longValue();
            return val >= Integer.MIN_VALUE && val <= Integer.MAX_VALUE;

        } else {
            ClassCastException prob = new ClassCastException(
              "Must be an EInt: " + clazz + "(" + eInt + ")");
            throw Thrower.toEject(optEjector, prob);
        }
    }

    /**
     * Like {@link BigInteger#BigInteger(String)}, but returns an EInt in
     * normal form.
     *
     * @return
     */
    static public Number run(String val) {
        return normal(new BigInteger(val));
    }

    /**
     * Like {@link BigInteger#BigInteger(String,int)}, but returns an EInt in
     * normal form.
     *
     * @return
     */
    static public Number run(String val, int radix) {
        return normal(new BigInteger(val, radix));
    }

    /**
     * Like {@link BigInteger#BigInteger(int,byte[])}, but returns an EInt in
     * normal form.
     *
     * @return
     */
    static public Number run(int signum, byte[] magnitude) {
        return normal(new BigInteger(signum, magnitude));
    }

    /**
     * Like {@link BigInteger#BigInteger(int,Random)}, but returns an EInt in
     * normal form.
     *
     * @return
     */
    static public Number run(int numBits, Random rnd) {
        return normal(new BigInteger(numBits, rnd));
    }
}
