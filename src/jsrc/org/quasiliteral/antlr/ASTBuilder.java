package org.quasiliteral.antlr;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.base.SourceSpan;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroArg;
import org.quasiliteral.astro.AstroSchema;
import org.quasiliteral.astro.AstroTag;
import org.quasiliteral.astro.BaseBuilder;
import org.quasiliteral.term.TermParser;

/**
 * The default implementation (and default superclass) for implementing
 * AstroBuilder simply, for building AstroAST-trees with AstroToken leaves.
 * <p/>
 * The type parameterization of AstroBuilder is: <pre>
 *     Leaf is AstroToken
 *     Node is AstroAST
 *     Arg is AstroAST
 *     Args is null (for empty list) or AstroAST (for it and its siblings).
 * </pre>
 *
 * @author Mark S. Miller
 */
public class ASTBuilder extends BaseBuilder {

    /**
     * Builds ASTs according to the term.y grammar
     */
    static public final ASTBuilder FOR_ASTS =
      new ASTBuilder(TermParser.DEFAULT_SCHEMA);

    /**
     *
     */
    public ASTBuilder(AstroSchema schema) {
        super(schema);
    }

    /**
     *
     */
    public String toString() {
        return "<building ASTs for " + getSchema().getSchemaName() + ">";
    }

    /**
     * @return :AstroToken
     */
    protected Astro leafInternal(AstroTag tag,
                                 Object optData,
                                 SourceSpan optSpan) {
        return new AstroToken(getSchema(), tag, optData, optSpan);
    }

    /**
     * @param functor :(AstroAST | AstroToken)
     * @param optArgs :(AstroAST | null)
     * @return :AstroAST
     */
    public Astro term(Astro functor, AstroArg optArgs) {
        AstroAST result;
        if (functor instanceof AstroAST) {
            result = (AstroAST)functor;
        } else {
            result = new AstroAST((AstroToken)functor);
        }
        //If optArgs is null, this does the right thing.
        //setChild preserves optArgs's right-sibling-chain, as it should
        result.setFirstChild((AstroAST)optArgs);
        return result;
    }

    /**
     * @return null
     */
    public AstroArg empty() {
        return null;
    }

    /**
     * Modifies the last sibling in 'first' to be 'second', which itself is XXX
     * (probably wrongly) modified to not have any further siblings.
     *
     * @param first  :(AstroAST | null)
     * @param second :AstroAST
     * @return :AstroAST
     */
    public AstroArg seq(AstroArg first, AstroArg second) {
        AstroAST optList = (AstroAST)first;
        AstroAST nextNode = (AstroAST)second;
        nextNode.setNextSibling(null);
        if (null == optList) {
            return nextNode;
        }
        AstroAST sib = optList;
        while (true) {
            AstroAST optNextSib = (AstroAST)sib.getNextSibling();
            if (null == optNextSib) {
                sib.setNextSibling(nextNode);
                return optList;
            }
            sib = optNextSib;
        }
    }
}
