package org.quasiliteral.term;

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroArg;
import org.quasiliteral.astro.AstroSchema;
import org.quasiliteral.astro.AstroTag;
import org.quasiliteral.astro.BaseBuilder;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * Builds Term/Functor trees.
 * <p/>
 * The type parameters of AstroBuilder are bound as follows:<pre>
 *     Leaf -- {@link Term}
 *     Node -- {@link Term}
 *     Arg -- {@link Term}
 *     Args -- a {@link FlexList} of Term
 * </pre>
 *
 * @author Mark S. Miller
 */
public class TermBuilder extends BaseBuilder {

    /**
     * Builds Term trees according to the term.y grammar
     */
    static public final TermBuilder FOR_TERMS =
      new TermBuilder(TermParser.DEFAULT_SCHEMA);

//    /**
//     * Builds ASTs according to the term.y grammar
//     */
//    static public final ASTBuilder FOR_ASTS =
//      new ASTBuilder(TermParser.DEFAULT_SCHEMA);

    /**
     *
     */
    public TermBuilder(AstroSchema schema) {
        super(schema);
    }

    /**
     *
     */
    public String toString() {
        return "<building Term trees for " +
          getSchema().getSchemaName() + ">";
    }

    /**
     *
     */
    protected Astro leafInternal(AstroTag tag,
                                 Object optData,
                                 SourceSpan optSpan) {
        return new Term(tag, optData, optSpan, ConstList.EmptyList);
    }

    /**
     * For Terms, this is non-atomic
     */
    public Astro composite(AstroTag tag, Object data, SourceSpan optSpan) {
        return term(leafTag(tag, optSpan),
                    leafData(data, optSpan));
    }

    /**
     * @param functor :Term with no arguments
     * @param args    :Seq
     * @return :Term
     */
    public Astro term(Astro functor, AstroArg args) {
        Term func = (Term)functor;
        T.require(func.getArgs().size() == 0,
                  "To use as a functor, a Term must not have args: ",
                  func);
        return new Term(func.getTag(),
                        func.getOptData(),
                        func.getOptSpan(),
                        ((Termish)args).getTerms());
    }

    /**
     *
     */
    public AstroArg empty() {
        return EmptySeq.THE_ONE;
    }

    /**
     *
     */
    public AstroArg seq(AstroArg first, AstroArg second) {
        return PairSeq.run((Termish)first, (Termish)second);
    }
}
