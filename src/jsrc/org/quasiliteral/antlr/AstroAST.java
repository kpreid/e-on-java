package org.quasiliteral.antlr;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import antlr.BaseAST;
import antlr.CommonToken;
import antlr.Token;
import antlr.collections.AST;
import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.Twine;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroArg;
import org.quasiliteral.astro.AstroBuilder;
import org.quasiliteral.astro.AstroSchema;
import org.quasiliteral.astro.AstroTag;

/**
 * AST node implementation which stores tokens explicitly.
 * <p/>
 * This is handy if you'd rather derive information from tokens on an as-needed
 * basis instead of snarfing data from a token as an AST is being built.
 * <p/>
 * In keeping with the nature of Antlr ASTs, AstroASTs are mutable. The
 * corresponding immutable, Persistent, & PassByCopy type is the {@link
 * org.quasiliteral.term.Term}.
 *
 * @author Mark S. Miller
 * @author Based on Danfuzz Bornstein's TokenAST
 */
public final class AstroAST extends BaseAST implements Astro {

    /**
     *
     */
    private AstroToken myOptToken;

    /**
     * Construct an instance which (at least initially) is not associated with
     * a token.
     */
    public AstroAST() {
        myOptToken = null;
    }

    /**
     * Construct an instance which is associated with the given token.
     *
     * @param optToken :AstroToken The (optional) token to associate this
     *                 instance with
     */
    public AstroAST(Token optToken) {
        initialize(optToken);
    }

    public AstroArg withOptSpan(SourceSpan optSpan) {
        T.fail("XXX not yet implemented");
        return null; //make compiler happy
    }

    /**
     *
     */
    public String toString() {
        return "" + myOptToken;
    }

    /**
     * Builds an equivalent of this AST using the building methods of
     * 'builder'.
     * <p/>
     * Pattern-wise, the schema functions here as both a visitor and as a
     * factory.
     *
     * @return :Node
     */
    public Astro build(AstroBuilder builder) {
        Astro func = myOptToken.build(builder);

        AstroArg args = builder.empty();
        for (AstroAST optSub = (AstroAST)getFirstChild();
             null != optSub;
             optSub = (AstroAST)optSub.getNextSibling()) {

            args = builder.seq(args, optSub.build(builder));
        }
        return builder.term(func, args);
    }

    /**
     * Since not all {@link AST}s are Astros, this static method provides the
     * equivalent of the build/1 instance method for ASTs in general.
     * <p/>
     * In the understanding of non-Astro ASTs used here, their functor is only
     * according to the AST's type code, for the tag, the optSpan is null
     * (since a generic AST doesn't provide an API for source position, and the
     * implementation generally don't record it either), and the data is the
     * AST's getText(). This choice for data will often be a mistake, but
     * there's no generic way to make a better decision. For grammars where
     * this is wrong, you should build a grammar-specific converter.
     * <p/>
     * XXX This should probably be made into a sugar-instance-method of AST.
     */
    static public Astro build(AST self, AstroBuilder builder) {
        if (self instanceof AstroAST) {
            return ((AstroAST)self).build(builder);
        } else {
            AstroSchema schema = builder.getSchema();
            short tagCode = AstroTag.typeCode2tagCode(self.getType());
            Astro func = builder.composite(schema.getOptTagForCode(tagCode),
                                           self.getText(),
                                           null);
            AstroArg args = builder.empty();
            for (AST optSub = self.getFirstChild();
                 null != optSub;
                 optSub = optSub.getNextSibling()) {

                args = builder.seq(args, build(optSub, builder));
            }
            return builder.term(func, args);
        }
    }

    /**
     * Get the token text for this instance. If there is no token associated
     * with this instance, then this returns the empty string (<tt>""</tt>),
     * not <tt>null</tt>.
     *
     * @return non-null; the token text
     */
    public String getText() {
        if (myOptToken == null) {
            return "";
        } else {
            return myOptToken.getText();
        }
    }

    /**
     * Get the token type for this instance. If there is no token associated
     * with this instance, then this returns {@link Token#INVALID_TYPE}.
     *
     * @return the token type
     */
    public int getType() {
        if (myOptToken == null) {
            return Token.INVALID_TYPE;
        } else {
            return myOptToken.getType();
        }
    }

    /**
     * Get the token associated with this instance. If there is no token
     * associated with this instance, then this returns <tt>null</tt>.
     *
     * @return The token associated with this instance, or <tt>mull</tt> if
     *         there is no associated token
     */
    public AstroToken getOptToken() {
        return myOptToken;
    }

    /**
     * Set the token associated with this instance.
     *
     * @param optToken The new token (or null) to associate with this
     *                 instance.
     */
    public void setOptToken(AstroToken optToken) {
        myOptToken = optToken;
    }

    /**
     * Initialize this instance with the given non-composite token.
     *
     * @param optToken :AstroToken the non-composite token to associate with
     *                 this instance
     */
    public void initialize(Token optToken) {
        myOptToken = (AstroToken)optToken;
        if (null == myOptToken) {
            return;
        }
        T.require(myOptToken.getTag().isTagForData(myOptToken.getOptData()),
                  "Must break apart composites: ",
                  myOptToken);
    }

    /**
     * Initialize this instance with the given token type and text. This will
     * construct a new {@link CommonToken} with the given parameters and
     * associate this instance with it.
     *
     * @param type    the token type
     * @param optText The token text, or null
     */
    public void initialize(int type, String optText) {
        initialize(new CommonToken(type, optText));
    }

    /**
     * Initialize this instance based on the given {@link AST}. If the given
     * <tt>AST</tt> is in fact an instance of <tt>AstroAST</tt>, then this
     * instance will be initialized to point at the same token as the given
     * one. If not, then this instance will be initialized with the same token
     * type and text as the given one.
     *
     * @param ast non-null; the <tt>AST</tt> to base this instance on
     */
    public void initialize(AST ast) {
        if (ast instanceof AstroAST) {
            initialize(((AstroAST)ast).getOptToken());
        } else {
            initialize(ast.getType(), ast.getText());
        }
    }

    /**
     * Set the token text for this node. If this instance is already associated
     * with a token, then that token is destructively modified by this
     * operation. If not, then a new token is constructed with the type {@link
     * Token#INVALID_TYPE} and the given text.
     *
     * @param text the new token text
     */
    public void setText(String text) {
        if (myOptToken == null) {
            initialize(Token.INVALID_TYPE, text);
        } else {
            myOptToken.setText(text);
        }
    }

    /**
     * Set the token type for this node. If this instance is already associated
     * with a token, then that token is destructively modified by this
     * operation. If not, then a new token is constructed with the given type
     * and an empty (<tt>""</tt>, not <tt>null</tt>) text string.
     *
     * @param type the new token type
     */
    public void setType(int type) {
        if (myOptToken == null) {
            initialize(type, "");
        } else {
            myOptToken.setType(type);
        }
    }

    public AstroTag getTag() {
        return myOptToken.getTag();
    }

    public short getOptTagCode() {
        return myOptToken.getOptTagCode();
    }

    public Object getOptData() {
        return myOptToken.getOptData();
    }

    public String getOptString() {
        return ((Twine)getOptData()).bare();
    }

    public Object getOptArgData() {
        return ((AstroAST)getFirstChild()).getOptData();
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

    public SourceSpan getOptSpan() {
        return myOptToken.getOptSpan();
    }

    public ConstList getArgs() {
        return myOptToken.getArgs();
    }

    public Astro withoutArgs() {
        if (null == getFirstChild()) {
            return this;
        } else {
            return new AstroAST(myOptToken);
        }
    }

//    public Astro withArgs(ConstList args) {
//        T.require(null == getFirstChild(),
//                  "Must be a leaf: ", this);
//        int len = args.size();
//        if (0 == len) {
//            return this;
//        } else {
//            AstroAST result = new AstroAST(myOptToken);
//            for (int i = 0; i < len; i++) {
//                result.addChild((AstroAST)args.get(i));
//            }
//            return result;
//        }
//    }
}
