package org.quasiliteral.syntax;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.tables.Twine;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroBuilder;
import org.quasiliteral.astro.AstroTag;

import java.io.IOException;

/**
 * The large number of bogus messages in this class is a symptom of a need to
 * refactor.
 *
 * @author Mark S. Miller
 */
public class ReplayLexer implements LexerFace {

    /**
     *
     */
    private final Astro[] myTokens;

    /**
     *
     */
    private int myNextToken;

    /**
     *
     */
    private final AstroBuilder myBuilder;

    /**
     *
     */
    public ReplayLexer(Astro[] tokens, AstroBuilder builder) {
        myTokens = tokens;
        myNextToken = 0;
        myBuilder = builder;
    }

    /**
     *
     */
    public void setSource(Twine newSource) {
        T.fail("Can't setSource on a ReplayLexer");
    }

    /**
     *
     */
    public void reset() {
//        T.fail("Can't reset a ReplayLexer");
        //ignore instead
    }

    /**
     *
     */
    public Astro composite(short tagCode, Object data, SourceSpan optSpan) {
        AstroTag tag = myBuilder.getSchema().getTagForCode(tagCode);
        return myBuilder.composite(tag, data, optSpan);
    }

    /**
     *
     */
    public Astro[] nextTopLevelUnit() throws IOException, SyntaxException {
        T.fail("XXX not yet implemented");
        return null; //make compiler happy
    }

    /**
     *
     */
    public Astro nextToken() throws IOException, SyntaxException {
        return myTokens[myNextToken++];
    }

    /**
     * XXX This really sucks compared to {@link BaseLexer#syntaxError} for
     * printing to a human, but at least it does report where the error is
     * adequately for programmatic use.
     */
    public void syntaxError(String msg) throws SyntaxException {
        int index = StrictMath.max(myNextToken - 1, 0);
        if (index < myTokens.length) {
            Astro token = myTokens[index];
            SourceSpan optSpan = token.getOptSpan();
            if (null != optSpan) {
                optSpan = optSpan.notOneToOne();
            }
            String printrep = token.toString();
            throw new SyntaxException(msg,
                                      null,
                                      Twine.fromString(printrep, optSpan),
                                      0,
                                      printrep.length());
        } else {
            throw new SyntaxException(msg, null, null, 0, 0);
        }
    }

    /**
     *
     */
    public void needMore(String msg)
      throws NeedMoreException, SyntaxException {
        syntaxError(msg);
    }

    /**
     *
     */
    public boolean isEndOfFile() {
        return myNextToken >= myTokens.length;
    }
}
