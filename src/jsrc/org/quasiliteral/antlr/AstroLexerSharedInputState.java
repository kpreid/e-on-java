package org.quasiliteral.antlr;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import antlr.LexerSharedInputState;

import java.io.InputStream;

/**
 * Extension of {@link LexerSharedInputState} that is aware of file names and
 * can annotate {@link AstroToken}s with them and with end position
 * information.
 *
 * @author based on Dan Bornstein's ExtentLexerSharedInputState
 * @author Mark S. Miller
 */
public class AstroLexerSharedInputState extends LexerSharedInputState {

    /**
     * the name of the file this instance refers to
     */
    private final String mySourceURL;

    /**
     * Construct an instance.
     *
     * @param s   the input stream to use
     * @param url The file name to associate with this instance
     */
    public AstroLexerSharedInputState(InputStream s, String url) {
        super(s);
        mySourceURL = url;
    }

    /**
     * Construct an instance. The file name is set to <tt>null</tt> initially.
     *
     * @param s the input stream to use
     */
    public AstroLexerSharedInputState(InputStream s) {
        this(s, null);
    }

    public String toString() {
        if (null == mySourceURL) {
            return "<AstroLexerSharedInputState>";
        } else {
            return "<AstroLexerSharedInputState:" + mySourceURL + ">";
        }
    }

    /**
     * Get the current line of this instance.
     *
     * @return the current line number
     */
    public int getLine() {
        return line;
    }

    /**
     * Get the current column of this instance.
     *
     * @return the current column number
     */
    public int getColumn() {
        return column;
    }

    /**
     * Get the file name of this instance.
     *
     * @return The file name
     */
    public String getSourceURL() {
        return mySourceURL;
    }

    /**
     * Annotate an {@link AstroToken} based on this instance. It sets the end
     * position information as well as the file name.
     *
     * @param token non-null; the token to annotate
     */
    public void annotate(AstroToken token) {
        //XXX does nothing yet.
    }
}
