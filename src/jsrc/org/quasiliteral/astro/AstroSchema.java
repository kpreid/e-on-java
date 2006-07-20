package org.quasiliteral.astro;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * Used to describe an Antlr grammar.
 * <p/>
 * Each token-type-code in an Antlr grammar corresponds to an
 * {@link AstroTag}.
 *
 * @author Mark S. Miller
 */
public interface AstroSchema {

    /**
     * The name of the language this is a Schema for, for diagnostic purposes
     * only.
     */
    String getSchemaName();

    /**
     * Returns the tag represented by this tag code in the grammar described
     * by this schema, or null.
     */
    AstroTag getOptTagForCode(short tagCode);

    /**
     * Returns the tag represented by this tag code in the grammar described
     * by this schema, or throw an exception.
     */
    AstroTag getTagForCode(short tagCode);

    /**
     * Returns the tag represented by this tag name in the grammar described
     * by this schema, or null if this schema doesn't define this tagName.
     */
    AstroTag getOptTagForName(String tagName);

    /**
     * Returns the tag represented by this tag name in the grammar described
     * by this schema.
     * <p/>
     * If there is no tag defined in this schema for this name, this still
     * returns a (typically newly allocated) tag with this tagName and this
     * schema, but with a tagCode of -1. Even in this case, the tagName
     * of the resulting tag will be interned.
     */
    AstroTag obtainTagForName(String tagName);

    /**
     * Gets the tag for representing character literals.
     * <p/>
     * The literal data is canonically represented by a Character object.
     */
    AstroTag getLiteralCharTag();

    /**
     * Gets the tag for representing integer literals.
     * <p/>
     * The literal data is canonically represented by a
     * {@link org.erights.e.meta.java.math.EInt EInt} in normal form.
     */
    AstroTag getLiteralIntegerTag();

    /**
     * Gets the tag for representing floating point literals.
     * <p/>
     * The literal data is canonically represented by a Double object.
     */
    AstroTag getLiteralFloat64Tag();

    /**
     * Gets the tag for representing String literals.
     * <p/>
     * The literal data is canonically represented by a Twine object.
     */
    AstroTag getLiteralStringTag();

    /**
     * Gets the tag for literally representing instances of clazz, or
     * null if clazz is the wrong kind.
     */
    AstroTag getOptTypeTag(Class clazz);

    /**
     * Gets the tag for literally representing instances of clazz, or
     * throws if clazz is the wrong kind.
     */
    AstroTag getTypeTag(Class clazz);
}
