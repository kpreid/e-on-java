package org.quasiliteral.astro;

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.ValueThunk;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.serial.Persistent;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.FlexMap;
import org.erights.e.elib.tables.Twine;
import org.erights.e.meta.java.math.EInt;

import java.math.BigInteger;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * The default implementation (and default superclass) for implementing
 * AstroSchema simply, starting with an array of tag names.
 * <p/>
 * XXX Need to work on the passability of this class
 *
 * @author Mark S. Miller
 */
public class BaseSchema
  implements AstroSchema, Persistent /*, PassByConstruction */ {

    static private final long serialVersionUID = 3951039891178561818L;

    static private final String[] MinimalTagNames =
      {".char.", ".int.", ".float64.", ".String."};

    /**
     *
     */
    static public final AstroSchema MINIMAL =
      new BaseSchema("Minimal", ConstList.fromArray(MinimalTagNames));

    /**
     * For diagnostic purposes only, but included in canonical state.
     */
    private final String mySchemaName;

    /**
     *
     */
    private final ConstList myTagNames;

    /**
     * Indexed by tag (a short)
     */
    private final AstroTag[] myByTagCode;

    /**
     * String (not Twine) => AstroTag
     */
    private final ConstMap myByTagName;

    /**
     *
     */
    private AstroTag myLiteralCharTag;

    /**
     *
     */
    private AstroTag myLiteralIntegerTag;

    /**
     *
     */
    private AstroTag myLiteralFloat64Tag;

    /**
     *
     */
    private AstroTag myLiteralStringTag;

    /**
     * The literal tag names default to their canonical names
     */
    public BaseSchema(String schemaName, ConstList tagNames) {
        this(schemaName, tagNames, ".char.", ".int.", ".float64.", ".String.");
    }

    /**
     *
     */
    public BaseSchema(String schemaName,
                      ConstList tagNames,
                      String literalCharTagName,
                      String literalIntegerTagName,
                      String literalFloat64TagName,
                      String literalStringTagName) {
        mySchemaName = schemaName.intern();
        myTagNames = tagNames;

        FlexList byCodes = FlexList.fromType(AstroTag.class, tagNames.size());
        FlexMap byNames = FlexMap.interning(AstroTag.class, tagNames.size());

        literalCharTagName = literalCharTagName.intern();
        literalIntegerTagName = literalIntegerTagName.intern();
        literalFloat64TagName = literalFloat64TagName.intern();
        literalStringTagName = literalStringTagName.intern();

        for (short tagCode = 0; tagCode < tagNames.size(); tagCode++) {
            String optTagName =
              (String)E.as(tagNames.get(tagCode), String.class);
            if (null != optTagName) {
                optTagName = optTagName.intern();
                AstroTag tag;

                if (optTagName == literalCharTagName) {
                    tag = new AstroTag(tagCode, optTagName, Character.class);
                    myLiteralCharTag = tag;
                    literalCharTagName = null;

                } else if (optTagName == literalIntegerTagName) {
                    tag = new AstroTag(tagCode, optTagName, EInt.class);
                    myLiteralIntegerTag = tag;
                    literalIntegerTagName = null;

                } else if (optTagName == literalFloat64TagName) {
                    tag = new AstroTag(tagCode, optTagName, Double.class);
                    myLiteralFloat64Tag = tag;
                    literalFloat64TagName = null;

                } else if (optTagName == literalStringTagName) {
                    tag = new AstroTag(tagCode, optTagName, Twine.class);
                    myLiteralStringTag = tag;
                    literalStringTagName = null;

                } else {
                    tag = new AstroTag(tagCode, optTagName, null);
                }
                byCodes.ensureSize(tagCode);
                byCodes.put(tagCode, tag);
                byNames.put(optTagName, tag, true);
            }
        }
        T.require(null == literalCharTagName &&
          null == literalIntegerTagName && null == literalIntegerTagName &&
          null == literalIntegerTagName,
                  "Unmatched literal name for: ",
                  mySchemaName);
        myByTagCode = (AstroTag[])byCodes.getArray(AstroTag.class);
        myByTagName = byNames.snapshot();
    }

    /**
     *
     */
    public String toString() {
        return "<Schema for " + mySchemaName + ">";
    }

    /**
     *
     */
    public String getSchemaName() {
        return mySchemaName;
    }

    /**
     *
     */
    public AstroTag getOptTagForCode(short optTagCode) {
        if (-1 == optTagCode) {
            return null;
        } else {
            return myByTagCode[optTagCode];
        }
    }

    /**
     *
     */
    public AstroTag getTagForCode(short tagCode) {
        AstroTag optResult = getOptTagForCode(tagCode);
        T.requireSI(null != optResult, "No tag for tag code: ", tagCode);
        return optResult;
    }

    /**
     *
     */
    public AstroTag getOptTagForName(String tagName) {
        return (AstroTag)myByTagName.fetch(tagName, ValueThunk.NULL_THUNK);
    }

    /**
     *
     */
    public AstroTag obtainTagForName(String tagName) {
        AstroTag optResult = getOptTagForName(tagName);
        if (null != optResult) {
            return optResult;
        }
        return new AstroTag((short)-1, tagName, null);
    }

    /**
     *
     */
    public AstroTag getLiteralCharTag() {
        return myLiteralCharTag;
    }

    /**
     *
     */
    public AstroTag getLiteralIntegerTag() {
        return myLiteralIntegerTag;
    }

    /**
     *
     */
    public AstroTag getLiteralFloat64Tag() {
        return myLiteralFloat64Tag;
    }

    /**
     *
     */
    public AstroTag getLiteralStringTag() {
        return myLiteralStringTag;
    }

    /**
     *
     */
    public AstroTag getOptTypeTag(Class clazz) {
        if (Character.class == clazz) {
            return myLiteralCharTag;
        }
        if (Number.class.isAssignableFrom(clazz)) {
            if (Double.class == clazz || Float.class == clazz) {
                return myLiteralFloat64Tag;
            } else if (EInt.class == clazz || Integer.class == clazz ||
              BigInteger.class == clazz || Byte.class == clazz ||
              Short.class == clazz || Long.class == clazz) {
                return myLiteralIntegerTag;
            } else {
                T.fail("Unrecognized number type: " + clazz);
            }
        }
        if (String.class == clazz || Twine.class.isAssignableFrom(clazz)) {
            return myLiteralStringTag;
        }
        return null;
    }

    /**
     *
     */
    public AstroTag getTypeTag(Class clazz) {
        AstroTag optResult = getOptTypeTag(clazz);
        T.notNull(optResult, "Unrecognized type: ", clazz);
        return optResult;
    }
}
