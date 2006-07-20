package org.quasiliteral.astro;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.serial.DeepPassByCopy;
import org.erights.e.elib.serial.Persistent;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.Twine;
import org.erights.e.meta.java.math.EInt;

import java.math.BigInteger;

/**
 * Represents both a grammar's tag code (corresponds to the enumerated token
 * type code specific to an Antlr grammar -1), and a corresponding tag name
 * (the name of that enumerated code in that grammar).
 * <p/>
 * The notion of "specific to a grammar" is here abstracted into the notion
 * of an {@link AstroSchema}, within which these tag-codes/tag-names are
 * defined, and that provides the means for translating between the two.
 * <i>Starting with E 0.8.20 an AstroTag no longer knows its Schema.</i>
 * <p/>
 * An AstroTag always has a tag-name, but not necessarily a tag-code or a
 * schema.
 * <p/>
 * To convert between Astro tag-codes and Antlr type-codes, a type code is a
 * tag code +1. This also results in the non-tag-code (-1) being converted
 * to {@link antlr.Token#INVALID_TYPE}, which happens to be 0.
 *
 * @author Mark S. Miller
 */
public class AstroTag implements Persistent, DeepPassByCopy {

    static final long serialVersionUID = -7783370054885847533L;

    static private StaticMaker AstroTagMaker = null;

    /**
     * @serial The enumerated tag code, or -1.
     */
    private final short myOptTagCode;

    /**
     * @serial The interned name of the enumerated tag code.
     */
    private final String myTagName;

    /**
     * @serial If this tag is a normal symbolic tag, this is null. If it's
     * a tag for a literal data type, this is the canonical class for that
     * data type -- one of Character.class, EInt.class, Double.class,
     * or Twine.class.
     */
    private final Class myOptDataType;

    /**
     * @param optTagCode  The enumerated tag code, or -1.
     * @param tagName     The name of the enumerated tag code.
     * @param optDataType Either null, or the kind of data labeled by this
     *                    tag.
     */
    public AstroTag(short optTagCode,
                    String tagName,
                    Class optDataType) {
        myOptTagCode = optTagCode;
        myTagName = tagName.intern();
        myOptDataType = optPromoteType(optDataType);
        //XXX commented out because we need to instead check whether it's
        //a legal tag name
//        T.require(BaseLexer.isIdentifierOrKeyword(myTagName),
//                  "Must be identifier: ", myTagName);
    }

    /**
     *
     */
    static public StaticMaker getAstroTagMaker() {
        if (null == AstroTagMaker) {
            AstroTagMaker = StaticMaker.make(AstroTag.class);
        }
        return AstroTagMaker;
    }

    /**
     * The type-code for use by Antlr, which is the tag-code +1.
     * <p/>
     * If the tag-code is -1, the returned type code will be
     * {@link antlr.Token#INVALID_TYPE}
     */
    static public int tagCode2typeCode(short tagCode) {
        return tagCode + 1;
    }

    /**
     *
     */
    static public short typeCode2tagCode(int typeCode) {
        return (short)(typeCode - 1);
    }

    /**
     * If optDataType is a class of data that's promotable to one that's
     * representable literally, then return the class that represents that
     * literally-representable data type; otherwise null.
     *
     * @param optDataType null or the data type of some data we wish to
     *                    promote to a literal representation.
     * @return null, or one of the classes {@link Character},
     * @{link EInt}, {@link Double}, or {@link Twine}
     */
    static public Class optPromoteType(Class optDataType) {
        if (null == optDataType) {
            return null;
        }
        if (Character.class == optDataType) {
            return Character.class;
        }
        if (String.class == optDataType ||
          Twine.class.isAssignableFrom(optDataType)) {
            return Twine.class;
        }
        if (Number.class.isAssignableFrom(optDataType)) {
            if (Double.class == optDataType || Float.class == optDataType) {
                return Double.class;

            } else if (EInt.class == optDataType ||
              Integer.class == optDataType ||
              BigInteger.class == optDataType ||
              Byte.class == optDataType ||
              Short.class == optDataType ||
              Long.class == optDataType) {
                return EInt.class;

            } else {
                T.fail("Unrecognized number type: " +
                       optDataType);
            }
        }
        return null;
    }

    /**
     * If optData is a data that's promotable to data that's representable
     * literally, then return that literally representable form;
     * otherwise null.
     * <p/>
     * A correctness criteria must hold between optPromoteData and
     * optPromoteType:  If <tt>p == optPromoteData(d)</tt>, then <pre>
     *     optPromoteType(p.getClass()) == optPromoteType(d.getClass())
     * </pre>
     *
     * @param optData null or some data we with to promote to a literal
     *                representation.
     * @return null, or a {@link Character}, @{link EInt}, {@link Double}, or
     *         {@link Twine}
     */
    static public Object optPromoteData(Object optData) {
        if (null == optData) {
            return null;
        }
        Class clazz = optData.getClass();
        if (Character.class == clazz ||
          Integer.class == clazz ||
          Double.class == clazz ||
          Twine.class.isAssignableFrom(clazz)) {

            // All these cases are already in canonical form.
            return optData;
        }
        if (String.class == clazz) {
            return Twine.fromString((String)optData);
        }
        if (Float.class == clazz) {
            return new Double(((Float)optData).doubleValue());
        }
        if (Number.class.isAssignableFrom(clazz)) {
            return EInt.normal((Number)optData);
        }
        return null;
    }

    /**
     * Uses 'AstroTagMaker(myOptTagCode, myTagName, myOptDataType)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {
            getAstroTagMaker(),
            "run",
            new Short(myOptTagCode),
            myTagName,
            myOptDataType
        };
        return result;
    }

    /**
     *
     */
    public String toString() {
        String result = "<" + myTagName;
        if (-1 != myOptTagCode) {
            result += ":" + myOptTagCode;
        }
        if (null != myOptDataType) {
            result += ":" + myOptDataType.getName();
        }
        result += ">";
        return result;
    }

    /**
     * The enumerated tag code, or -1.
     */
    public short getOptTagCode() {
        return myOptTagCode;
    }

    /**
     * The interned name of the enumerated tag code.
     */
    public String getTagName() {
        return myTagName;
    }

    /**
     * Indicates the type of data literally represented by this tag.
     * <p/>
     * If this tag is a normal symbolic tag, this is null. If it's
     * a tag for a literal data type, this is the canonical class for that
     * data type -- one of Character.class, EInt.class, Double.class,
     * or Twine.class.
     */
    public Class getOptDataType() {
        return myOptDataType;
    }

    /**
     * Is this the tag implied by the type of optData?
     * <p/>
     * If optData is null, the answer is true.
     */
    public boolean isTagForData(Object optData) {
        if (null == optData) {
            return true;
        }
        if (null == myOptDataType) {
            return false;
        }
        return optPromoteType(optData.getClass()) == myOptDataType;
    }

    /**
     * Compare on tag names regardless of schema or tag codes
     */
    public double op__cmp(AstroTag other) {
        return (double)myTagName.compareTo(other.myTagName);
    }

    /**
     * Given that root is tagged with rootName, get its first argument that's
     * tagged with attrName
     */
    static public Astro optAttribute(Astro root,
                                     String rootName,
                                     String attrName) {
        //Hard code Term-tree walking in order to avoid having elib depend
        //on quasi-Terms
        T.require(root.getTag().getTagName() == rootName,
                  "Mismatch: ", root, " vs ", rootName);
        attrName = attrName.intern();
        ConstList args = root.getArgs();
        for (int i = 0, len = args.size(); i < len; i++) {
            Astro arg = (Astro)args.get(i);
            if (arg.getTag().getTagName() == attrName) {
                return arg;
            }
        }
        return null;
    }

    /**
     * Like optAttribute/3, but only checks args[0].
     * <p/>
     * If you know the arg you're looking for may only be first or absent,
     * this is a nice little optimization. But it does make your format
     * more position dependent, and therefore more brittle.
     */
    static public Astro optFirstAttribute(Astro root,
                                          String rootName,
                                          String attrName) {
        //Hard code Term-tree walking in order to avoid having elib depend
        //on quasi-Terms
        T.require(root.getTag().getTagName() == rootName,
                  "Mismatch: ", root, " vs ", rootName);
        attrName = attrName.intern();
        ConstList args = root.getArgs();
        Astro arg = (Astro)args.get(0);
        if (arg.getTag().getTagName() == attrName) {
            return arg;
        } else {
            return null;
        }
    }
}
