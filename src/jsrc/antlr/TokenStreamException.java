package antlr;

/* ANTLR Translator Generator
 * Project led by Terence Parr at http://www.jGuru.com
 * Software rights: http://www.antlr.org/license.html
 *
 * $Id: //depot/code/org.antlr/release/antlr-2.7.5/antlr/TokenStreamException.java#1 $
 */

/**
 * Anything that goes wrong while generating a stream of tokens.
 */
public class TokenStreamException extends ANTLRException {

    static private final long serialVersionUID = 3848512683925000149L;

    public TokenStreamException() {
    }

    public TokenStreamException(String s) {
        super(s);
    }
}
