package org.quasiliteral.syntax;

import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.serial.PassByProxy;
import org.erights.e.elib.tables.Twine;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroBuilder;

import java.io.IOException;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * @author Mark S. Miller
 */
public interface LexerFace extends PassByProxy {

    /**
     *
     */
    char EOFCHAR = '\0';

    /**
     * Not provided for us by byaccj
     */
    short EOFTOK = 0;

    /**
     * Does not affect the partial flag
     */
    void setSource(Twine newSource);

    /**
     * Skip to the end of this line, so that the next character read will be
     * from the next line, forget any token we may have been in the midst of,
     * and reset our indentation tracker.
     */
    void reset();

    /**
     * XXX Making this public was convenient for ENodeBuilder.varName/1, but is
     * it inappropriate?
     */
    Astro composite(short tagCode, Object data, SourceSpan optSpan);

    /**
     * @return :Leaf[] (see {@link AstroBuilder})
     */
    Astro[] nextTopLevelUnit() throws IOException, SyntaxException;

    /**
     * @return :Leaf (see {@link AstroBuilder})
     */
    Astro nextToken() throws IOException, SyntaxException;

    /**
     * Throws a {@link SyntaxException} that also captures the current line and
     * position as the position of the error.
     */
    void syntaxError(String msg) throws SyntaxException;

    /**
     * Called when input was otherwise well formed, but ran out, so more is
     * needed.
     * <p/>
     * Normally, this just turns into an equivalent 'syntaxError(msg)'. But, if
     * the partial flag is set, meaning that the client wants to prompt
     * intelligently for more input, then this throws a {@link
     * NeedMoreException} that explains at what indentation the next input is
     * expected.
     */
    void needMore(String msg) throws NeedMoreException, SyntaxException;

    /**
     * pretty self explanatory
     */
    boolean isEndOfFile();
}
