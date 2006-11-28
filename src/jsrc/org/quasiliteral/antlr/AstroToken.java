package org.quasiliteral.antlr;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import antlr.Token;
import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.Twine;
import org.erights.e.meta.java.math.EInt;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroArg;
import org.quasiliteral.astro.AstroBuilder;
import org.quasiliteral.astro.AstroSchema;
import org.quasiliteral.astro.AstroTag;

/**
 * A Kind of Antlr {@link Token} that preserves all the information in a
 * functor.
 * <p/>
 * In keeping with the nature of an Antlr Token, and in order to be usable from
 * within Antlr itself, AstroToken is mutable, but just mutable enough to
 * accomodate Antlr. Specifically, the various values are settable once.
 *
 * @author Mark S. Miller
 * @author Based on ValueExtentToken by Danfuzz Bornstein
 * @noinspection CloneableClassInSecureContext
 */
public final class AstroToken extends Token implements Astro {

    /**
     *
     */
    private AstroSchema myOptSchema;

    /**
     *
     */
    private AstroTag myOptTag;

    /**
     * Must be a null, or something that promotes to a {@link Character},
     * {@link EInt}, {@link Double}, or {@link Twine}.
     * <p/>
     * If not null, then the type must correspond to the tag.
     */
    private Object myOptData;

    /**
     *
     */
    private final SourceSpan myOptSpan;

    /**
     * Construct an instance.
     * <p/>
     * The instance will have the tag code -1, null optData, and null optSpan.
     */
    public AstroToken() {
        super();
        myOptSchema = null;
        myOptTag = null;
        myOptData = null;
        myOptSpan = null;
    }

    /**
     * Construct an instance.
     * <p/>
     * The instance will have the tag code -1, null optData, and null optSpan.
     */
    public AstroToken(SourceSpan optSpan) {
        super();
        myOptSchema = null;
        myOptTag = null;
        myOptData = null;
        myOptSpan = optSpan;
    }

    /**
     * Makes an AstroToken that represents a token in some grammar.
     * <p/>
     * The invariants of an AstroToken are not checked here, but rather are
     * enforced by the callers in this class and in ASTBuilder.
     *
     * @param optSchema If provided, the AstroSchema in which tag is defined.
     *                  To use the composite representation, a schema must be
     *                  provided.
     * @param tag       Identifies a token type in a particular grammar or set
     *                  of related grammars.
     * @param optData   null, or something that promotes to a {@link
     *                  Character}, {@link EInt EInt}, {@link Double}, or
     *                  {@link Twine} presumably calculated from lexing this
     *                  token
     * @param optSpan   Where this token was extracted from.
     */
    AstroToken(AstroSchema optSchema,
               AstroTag tag,
               Object optData,
               SourceSpan optSpan) {
        super(AstroTag.tagCode2typeCode(tag.getOptTagCode()));
        myOptSchema = optSchema;
        myOptTag = tag;
        myOptData = optData;
        myOptSpan = optSpan;
    }

    public AstroArg withOptSpan(SourceSpan optSpan) {
        return new AstroToken(myOptSchema, myOptTag, myOptData, optSpan);
    }

    /**
     *
     */
    public Astro build(AstroBuilder builder) {
        if (null == myOptData) {
            return builder.leafTag(getTag(), myOptSpan);
        } else if (getTag().isTagForData(myOptData)) {
            return builder.leafData(myOptData, myOptSpan);
        } else {
            return builder.composite(getTag(), myOptData, myOptSpan);
        }
    }

    /**
     * Since not all {@link Token}s are AstroTokens, this static method
     * provides the equivalent of the build/1 instance method for Tokens in
     * general.
     * <p/>
     * In the understanding of non-Astro Tokens used here, their functor is
     * only according to the AST's type code, for the tag, and the AST's text,
     * for the data. This choice for data will often be a mistake, but there's
     * no generic way to make a better decision. For grammars where this is
     * wrong, you should build a grammar-specific converter.
     * <p/>
     * XXX This should probably be made into a sugar-instance-method of Token.
     */
    static public Astro build(Token self, AstroBuilder builder) {
        if (self instanceof AstroToken) {
            return ((AstroToken)self).build(builder);
        } else {
            SourceSpan span = new SourceSpan("<unknown>",
                                             false,
                                             self.getLine(),
                                             self.getColumn(),
                                             self.getLine(),
                                             self.getColumn());
            short tagCode = AstroTag.typeCode2tagCode(self.getType());
            AstroTag tag = builder.getSchema().getTagForCode(tagCode);
            return builder.composite(tag, self.getText(), span);
        }
    }

    /**
     *
     */
    public AstroTag getTag() {
        T.notNull(myOptTag, "Tag must first be set: ", this);
        return myOptTag;
    }

    /**
     * For when the type-code is already set by generic Antlr code, while
     * Astro-specific knowledge of the schema comes along later.
     * <p/>
     * To use this, the type code must already be set, the tag must not yet be
     * set, and the schema must not be set to a different schema.
     */
    public void setSchema(AstroSchema schema) {
        T.require(INVALID_TYPE != getType(), "Type-code must be set: ", this);
        T.require(null == myOptTag, "Tag must not be set: ", this);
        T.require(null == myOptSchema || myOptSchema == schema,
                  "Schema conflict: ",
                  myOptSchema,
                  " vs ",
                  schema);
        myOptSchema = schema;
        myOptTag = schema.getTagForCode(getOptTagCode());
    }

    /**
     * Override to make set-once
     */
    public void setType(int t) {
        T.require(INVALID_TYPE == getType(), "Type-code already set");
        super.setType(t);
    }

    public short getOptTagCode() {
        return AstroTag.typeCode2tagCode(getType());
    }

    /**
     * If this token represents literal data, return that data, else null.
     */
    public Object getOptData() {
        if (getTag().isTagForData(myOptData)) {
            return myOptData;
        } else {
            //the data will be in args[0]. Or it can be directly accessed
            //with getArgData()
            return null;
        }
    }

    public String getOptString() {
        return ((Twine)getOptData()).bare();
    }

    /**
     *
     */
    public Object getOptArgData() {
        T.require(!(getTag().isTagForData(myOptData)),
                  "Not a composite: ",
                  this);
        return myOptData;
    }

    public Object getOptArgData(short tagCode) {
        T.require(getTag().getOptTagCode() == tagCode,
                  "Tag mismatch: ",
                  getTag(),
                  " vs " + tagCode);
        return getOptArgData();
    }

    public String getOptArgString(short tagCode) {
        return ((Twine)getOptArgData(tagCode)).bare();
    }

    /**
     *
     */
    public SourceSpan getOptSpan() {
        return myOptSpan;
    }

    /**
     *
     */
    public String getText() {
        if (null == myOptData) {
            return null;
        } else if (myOptData instanceof String) {
            return (String)myOptData;
        } else if (myOptData instanceof Twine) {
            return ((Twine)myOptData).bare();
        } else {
            return null;
        }
    }

    /**
     *
     */
    public void setText(String s) {
        myOptData = s;
    }

    /**
     *
     */
    public int getLine() {
        if (null == myOptSpan) {
            return 0;
        }
        return myOptSpan.getStartLine();
    }

    /**
     * Override to make set-once, and so set the source-span info of myOptSpan
     */
    public void setLine(int l) {
        T.require(0 == getLine(), "Line already set");
        T.fail("XXX setLine not yet implemented");
    }

    /**
     *
     */
    public int getColumn() {
        if (null == myOptSpan) {
            return 0;
        }
        return myOptSpan.getStartCol();
    }

    /**
     * Override to make set-once, and so set the source-span info of myOptSpan
     */
    public void setColumn(int c) {
        T.require(0 == getColumn(), "Column already set");
        T.fail("XXX setColumn not yet implemented");
    }

    /**
     *
     */
    public ConstList getArgs() {
        if (getTag().isTagForData(myOptData)) {
            return ConstList.EmptyList;
        } else {
            T.notNull(myOptSchema, "Must have a Schema first: ", this);
            AstroTag litTag = myOptSchema.getTypeTag(myOptData.getClass());
            Astro arg =
              new AstroToken(myOptSchema, litTag, myOptData, getOptSpan());
            return ConstList.EmptyList.with(arg);
        }
    }

    /**
     *
     */
    public Astro withoutArgs() {
        if (getTag().isTagForData(myOptData)) {
            return this;
        } else {
            return new AstroToken(myOptSchema, getTag(), null, getOptSpan());
        }
    }

//    /**
//     *
//     */
//    public Astro withArgs(ConstList args) {
//        return new AstroAST(this).withArgs(args);
//    }
}
