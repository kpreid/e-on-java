package antlr;

/* ANTLR Translator Generator
 * Project led by Terence Parr at http://www.jGuru.com
 * Software rights: http://www.antlr.org/license.html
 *
 * $Id: //depot/code/org.antlr/release/antlr-2.7.5/antlr/CharStreamException.java#1 $
 */

/**
 * Anything that goes wrong while generating a stream of characters
 */
public class CharStreamException extends ANTLRException {

    static private final long serialVersionUID = -3447485037521955852L;

    /**
     * CharStreamException constructor comment.
     *
     * @param s java.lang.String
     */
    public CharStreamException(String s) {
        super(s);
    }
}
