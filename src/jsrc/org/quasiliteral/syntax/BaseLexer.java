package org.quasiliteral.syntax;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.tables.EmptyTwine;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.Twine;
import org.erights.e.meta.java.math.EInt;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroBuilder;
import org.quasiliteral.astro.AstroTag;

import java.io.IOException;

/**
 * To be replaced with a lexer based on Antlr.
 * <p/>
 * Abstracts out common elements of lexers usable within the E quasi-parsing
 * framework.
 *
 * @author Mark S. Miller
 */
public abstract class BaseLexer implements LexerFace {

    /**
     * contains all lines after the current line
     */
    private LineFeeder myInput;

    /**
     * the current line, or null at end-of-file
     */
    protected Twine myOptLTwine = null;

    /**
     * the string part, but as an array for speed.
     * <p/>
     * Also null at end-of-file.
     */
    protected char[] myOptLData = null;

    /**
     * position in current line of candidate character
     */
    protected int myPos;

    /**
     * the candidate character, or EOFCHAR for end-of-file.
     */
    protected char myChar;

    /**
     * Where on the current line does the current token start?  If the token
     * starts before the current line, or if there is no current token, this is
     * -1.
     */
    protected int myOptStartPos = -1;

    /**
     * Accumulates all text of the current token from lines before the current
     * line, or null if no current token or if the current token starts on the
     * current line. The EOL token itself is not considered to be a line
     * spanning token.
     */
    protected Twine myOptStartText = null;

    /**
     * Is there a nextChar() that's been delayed?
     */
    protected boolean myDelayedNextChar = false;

    /**
     *
     */
    private final short myEolTok;

    /**
     *
     */
    private final short myEotluTok;

    /**
     *
     */
    private final boolean myPartialFlag;

    /**
     * Should doubled '@', and '$' in literals be collapsed to singles?
     */
    protected final boolean myQuasiFlag;

    /**
     * Should tabs be rejected as valid whitespace?
     */
    protected final boolean myNoTabsFlag;

    /**
     * Keeps track of indentation level
     */
    protected Indenter myIndenter;

    /**
     * Should the next line get extra indentation as a continuation line?
     * <p/>
     * If not, -1. If so, the number of spaces to indent.
     */
    protected int myContinueCount;

    /**
     *
     */
    protected final AstroBuilder myBuilder;


    /**
     * Like {@link Character#isJavaIdentifierStart(char)}
     */
    static public boolean isJavaIdStart(char c) {
        return Character.isJavaIdentifierStart(c);
    }

    /**
     * Like {@link Character#isJavaIdentifierPart(char)} but rejects EOFCHAR,
     * which happens to be a '\0', which isJavaIdentifierPart accepts as an
     * "ignorable control character".
     */
    static public boolean isJavaIdPart(char c) {
        return Character.isJavaIdentifierPart(c) && EOFCHAR != c;
    }


    /**
     * The first character of an E identifier may be anything accepted as the
     * first character of a Java identifier except '$'. I.e., a letter or '_'.
     *
     * @see Character#isJavaIdentifierStart
     */
    static public boolean isIdentifierStart(char ch) {
        return isJavaIdStart(ch) && '$' != ch;
    }

    /**
     * A non-first character of an E identifier may be anything accepted as a
     * non-first character of a Java identifier except '$'. The ascii subset
     * consists of letters, digits, and '_'. See {@link Character#isJavaIdentifierPart(char)}
     * for the full spec.
     */
    static public boolean isIdentifierPart(char ch) {
        return isJavaIdPart(ch) && '$' != ch;
    }

    /**
     * A legal E identifier or keyword is a string whose first character
     * isIdentifierStart, the rest of whose characters are isIdentifierPart.
     * <pre>
     *     &lt;identifier&gt; ::= &lt;idStart&gt; &lt;idPart&gt;*
     * </pre>
     */
    static public boolean isIdentifierOrKeyword(String str) {
        int len = str.length();
        if (0 == len) {
            return false;
        } else if (!isIdentifierStart(str.charAt(0))) {
            return false;
        } else {
            for (int i = 1; i < len; i++) {
                if (!isIdentifierPart(str.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * @param eolTok   "End Of Line"
     * @param eotluTok "End Of Top Level Unit"
     */
    protected BaseLexer(LineFeeder optLineFeeder,
                        short eolTok,
                        short eotluTok,
                        boolean partialFlag,
                        boolean quasiFlag,
                        boolean noTabsFlag,
                        AstroBuilder builder) throws IOException {

        if (null == optLineFeeder) {
            //XXX kludge
            myInput = new TwineFeeder(EmptyTwine.THE_ONE);
        } else {
            myInput = optLineFeeder;
        }
        myPos = -1;
        myEolTok = eolTok;
        myEotluTok = eotluTok;
        myPartialFlag = partialFlag;
        //XXX postpone first line-read till first token-read.
        setLine(myInput.optNextLine(true, false, 0, 'x', 0));
        nextChar();
        myQuasiFlag = quasiFlag;
        myNoTabsFlag = noTabsFlag;
        myIndenter = new Indenter();
        myContinueCount = -1;
        myBuilder = builder;
    }

    private void setLine(Twine optLine) {
        if (null == optLine) {
            myOptLTwine = null;
            myOptLData = null;
        } else {
            myOptLTwine = optLine;
            myOptLData = optLine.bare().toCharArray();
        }
    }

    /**
     *
     */
    public String toString() {
        return "<Lexing " + myBuilder.getSchema().getSchemaName() + ">";
    }

    /**
     *
     */
    public void setSource(Twine newSource) {
        TwineFeeder feeder = new TwineFeeder(newSource);

        myInput = feeder;
        myPos = -1;
        // myPartialFlag = ??;
        setLine(feeder.optNextLine(true, false, 0, 'x', 0));
        myOptStartPos = -1;
        myOptStartText = null;
        myDelayedNextChar = true; //rather than doing nextChar() ourselves
        myIndenter = new Indenter();
        myContinueCount = -1;
    }

    /**
     *
     */
    public void reset() {
        if (!isEndOfFile()) {
            myPos = myOptLData.length;
        }
        myOptStartPos = -1;
        myOptStartText = null;
        myDelayedNextChar = true;
        myIndenter = new Indenter();
        myContinueCount = -1;
    }

    /**
     *
     */
    private void nextLine() throws IOException {
        if (isEndOfFile()) {
            myChar = EOFCHAR;
            return;
        }
        if (-1 == myOptStartPos) {
            if (null == myOptStartText) {
                //no current token, do nothing
            } else {
                //current token already started
                myOptStartText = (Twine)myOptStartText.add(myOptLTwine);
            }
        } else {
            //current token started on this line at myOptStartPos
            myOptStartText = getSpan(myOptStartPos,
                                     myOptLTwine.size(),
                                     "Internal: unexpected end-of-file");
            myOptStartPos = -1;
        }
        myPos = -1;

        //This is irritatingly redundant with needMore(..).
        //XXX Need to refactor, but how?
        char closer = myIndenter.getCloser();
        int indent = myIndenter.getIndent();
        int closeIndent = myIndenter.getCloseIndent();
        if (0 <= myContinueCount) {
            indent += myContinueCount;
            //If we're after a continuer, then a closing bracket shouldn't be
            //allowed as the next character.
            closeIndent = indent;
        }
        boolean quoted = ('"' == closer || '`' == closer);
        boolean atTop = !quoted && 0 == indent && -1 == myContinueCount;
        setLine(myInput.optNextLine(atTop,
                                    quoted,
                                    indent,
                                    closer,
                                    closeIndent));
        if (isEndOfFile()) {
            //don't clear myContinueCount on end-of-file
            //XXX why not? (I don't remember why I wrote this.)
        } else {
            if (0 <= myContinueCount) {
                if (isRestBlank(0)) {
                    //If we're after a continuation operator, then keep
                    //eating blank lines until we get a non-blank line.
                    //XXX funny control flow. Perhaps we should restructure
                    //nextLine() as a loop
                    nextLine();
                    return;
                } else {
                    //Only clear myContinueCount if there's non-whitespace on
                    //the new line
                    myContinueCount = -1;
                }
            }
        }
        myChar = '\n';
    }

    /**
     *
     */
    protected final void nextChar() throws IOException {
        while (true) {
            if (isEndOfFile()) {
                myChar = EOFCHAR;
                return;
            }
            myPos++;
            int len = myOptLData.length;
            if (myPos < len) {
                myChar = myOptLData[myPos];
                return;
            } else {
                nextLine();
            }
        }
    }

    /**
     *
     */
    public Astro[] nextTopLevelUnit() throws IOException, SyntaxException {
        FlexList result = FlexList.fromType(Astro.class);
        Astro tok;
        short tagCode;
        do {
            tok = nextToken();
            result.push(tok);
            tagCode = tok.getOptTagCode();
        } while (tagCode != myEotluTok && LexerFace.EOFTOK != tagCode);
        return (Astro[])result.getArray(Astro.class);
    }

    /**
     *
     */
    public Astro nextToken() throws IOException, SyntaxException {
        try {
            return getNextToken();
        } finally {
            myOptStartPos = -1;
            myOptStartText = null;
        }
    }

    /**
     *
     */
    public void syntaxError(String msg) throws SyntaxException {
        int start = myOptStartPos;
        if (-1 == start) {
            start = myPos - 1;
        }
        start = StrictMath.max(StrictMath.min(start, myPos - 1), 0);
        int bound = StrictMath.max(myPos, start + 1);
        SyntaxException sex =
          new SyntaxException(msg, null, myOptLTwine, start, bound);
        reset();
        throw sex;
    }

    /**
     *
     */
    public void needMore(String msg)
      throws NeedMoreException, SyntaxException {
        if (myPartialFlag) {

            //This is irritatingly redundant with nextLine().
            //XXX Need to refactor, but how?
            char closer = myIndenter.getCloser();
            int indent = myIndenter.getIndent();
            if (0 <= myContinueCount) {
                indent += myContinueCount;
                myContinueCount = -1;
            }
            boolean quoted = ('"' == closer || '`' == closer);
            boolean atTop = !quoted && 0 == indent && -1 == myContinueCount;
            T.require(!atTop, "Internal: confused about top level", this);
            NeedMoreException nme = new NeedMoreException(msg,
                                                          quoted,
                                                          indent,
                                                          closer,
                                                          myIndenter.getCloseIndent());
            reset();
            throw nme;
        } else {
            myIndenter.requireEmpty(msg);
            syntaxError(msg);
        }
    }

    /**
     * Is <tt>c</tt> a {@link Character#digit(char,int) digit} in base
     * <tt>radix</tt>?
     */
    private boolean isDigitStart(char c, int radix) {
        return -1 != Character.digit(c, radix);
    }

    /**
     * Eat a digit in base radix and its optionally preceding "_".
     * <pre>
     *     &lt;digitPart(n)&gt; ::=  "_"? &lt;{@link #isDigitStart
     *                                         digitStart(n)
     *                               }&gt;
     * </pre>
     */
    private boolean digitPart(int radix) throws IOException {
        if (isDigitStart(myChar, radix)) {
            nextChar();
            return true;
        } else if ('_' == myChar && isDigitStart(peekChar(), radix)) {
            nextChar();
            nextChar();
            return true;
        } else {
            return false;
        }
    }

    /**
     * If myChar {@link #isDigitStart} in base <tt>radix</tt>, then eat a
     * sequence of {@link #digitPart}s in base <tt>radix</tt>.
     * <pre>
     *     &lt;digits(n)&gt; ::= &lt;{@link #isDigitStart
     *                                      digitStart(n)
     *                               }&gt; &lt;{@link #digitPart
     *                                                digitPart(n)}&gt;*
     * </pre>
     */
    private boolean digits(int radix) throws IOException {
        if (isDigitStart(myChar, radix)) {
            nextChar();
            while (digitPart(radix)) {
                // do nothing
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Skips {@link Character#isWhitespace(char) whitespace} characters except
     * for newlines.
     */
    protected void skipWhiteSpace() throws IOException {
        while (true) {
            if (isEndOfFile()) {
                return;
            }
            if (Character.isWhitespace(myChar)) {
                if ('\n' == myChar) {
                    return;
                }
                if ('\t' == myChar) {
                    if (myNoTabsFlag) {
                        syntaxError("The optional e.enable.notabs" +
                          " feature (see " + "org/erights/e/elang/syntax/" +
                          "syntax-props-default.txt" + ") is currently on,\n" +
                          "so tabs are not considered valid whitespace");
                    }
                    //else, we should warn, but XXX we don't yet have a
                    //warning mechanism.
                }
                nextChar();
            } else {
                return;
            }
        }
    }

    /**
     * Are all the characters on the current line from start inclusive to bound
     * exclusive whitespace characters?
     */
    protected boolean isWhite(int start, int bound) {
        for (int i = start; i < bound; i++) {
            if (!Character.isWhitespace(myOptLData[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Starting at <tt>start</tt>, is the rest of the current line "blank"?
     * <p/>
     * This just defaults to <tt>isWhite(start,myOptLData.length)</tt>, but
     * should be overridden by subclasses to also consider the remainder of a
     * line blank if the only thing it contains is a rest-of-line comment (such
     * as a "#" comment in E or a "//" comment in Java).
     */
    protected boolean isRestBlank(int start) {
        if (isEndOfFile()) {
            return true;
        }
        return isWhite(start, myOptLData.length);
    }

    /**
     *
     */
    protected Astro leafTag(short tagCode, SourceSpan optSpan) {
        AstroTag tag = myBuilder.getSchema().getTagForCode(tagCode);
        return myBuilder.leafTag(tag, optSpan);
    }

    /**
     * Output either an EOL or, if we're at top level, an EOTLU
     */
    protected Astro leafEOL() {
        short tok = myEolTok;
        if (0 == myIndenter.getIndent() && -1 == myContinueCount) {
            tok = myEotluTok;
        }
        return leafTag(tok, endSpan());
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
    protected abstract Astro getNextToken()
      throws IOException, SyntaxException;

    /**
     *
     */
    protected Astro openBracket(char closer) throws IOException {
        short tagCode = (short)myChar;
        nextChar();
        Twine openner = endToken();
        return openBracket(tagCode, openner, closer);
    }

    /**
     *
     */
    protected Astro openBracket(short tagCode, Twine openner, char closer) {
        if (isRestBlank(myPos)) {
            myIndenter.nest(openner, closer);
        } else {
            //Indent the next line to right after the open.
            myIndenter.push(openner, closer, myPos);
        }
        return leafTag(tagCode, openner.getOptSpan());
    }

    /**
     *
     */
    protected Astro closeBracket() throws IOException {
        char closerChar = myChar;
        nextChar();
        Twine closer = endToken();
        //on mismatched close, throws SyntaxError at openner
        //on unmatched close, throws Syntax error at closer
        myIndenter.pop(closerChar, closer);
        return leafTag((short)closerChar, closer.getOptSpan());
    }

    /**
     * Leaves myChar at the last character instead of the next one.
     */
    private int charConstantInternal() throws IOException, SyntaxException {
        if ('\\' == myChar) {
            nextChar();
            //Since all the non-error cases below don't involve any
            //quasi-enders, we can ignore myQuasiFlag during the switch.
            switch (myChar) {
            case'b':
                return (int)'\b';
            case't':
                return (int)'\t';
            case'n':
                return (int)'\n';
            case'f':
                return (int)'\f';
            case'r':
                return (int)'\r';
            case'"':
                return (int)'"';
            case'\'':
                return (int)'\'';
            case'\\':
                return (int)'\\';
            case'/': // XXX This was added for JSON compatibility in the
                     // TermLexer.
                     // Consider a refactoring so that the particular set of
                     // backslash escapes can be controlled in subclasses.
                     // -- kpreid 2009-01-14
                return (int)'/';
            case'\n':
                return -1;
            case EOFCHAR:
                needMore("End of file in middle of literal");
                return -1; // make compiler happy
            case'u': {
                // XXX We need to decide if this is the job of the lexer or,
                // as in Java, of a decoding stage prior to lexing. Currently
                // we handle this during lexing, and only within literal
                // character data.
                String hexStr = "";
                for (int i = 0; 4 > i; i++) {
                    nextChar();
                    if (-1 == Character.digit(myChar, 16)) {
                        syntaxError("\\u escape must be four hex digits");
                    }
                    hexStr += myChar;
                }
                return Integer.parseInt(hexStr, 16);
            }
            default: {
                if (isDigitStart(myChar, 10)) {
                    syntaxError("XXX escaped char codes not yet implemented");
                } else {
                    syntaxError("Unrecognized escaped character");
                }
            }
            }
        } else if (isEndOfFile()) {
            needMore("End of file in middle of literal");
        } else if (myQuasiFlag && ('$' == myChar || '@' == myChar)) {
            char c = myChar;
            nextChar();
            if (c != myChar) {
                syntaxError("When quasi-parsing, '" + c + "' must be doubled");
            }
            return (int)c;
        } else if ('\t' == myChar) {
            syntaxError("Quoted tabs must be written as \\t");
        } else {
            return (int)myChar;
        }
        return 'x'; //keep compiler happy
    }

    /**
     * Used to eat the encoding of a single character as it would appear inside
     * a quoted character or string constant.
     * <p/>
     * Backslash escapes are interpreted according to the Java standard, except
     * that escaped character codes are not yet implemented.
     * <p/>
     * If we're quasi-parsing, then any literal '$' or '@' characters must
     * appear doubled, in which case a single such character will be included
     * in the literal.
     *
     * @return encoded character, or -1 if a backslash-newline was seen, since
     *         this encodes no characters.
     */
    protected int charConstant() throws IOException, SyntaxException {
        int result = charConstantInternal();
        nextChar();
        return result;
    }

    /**
     * Assumes that myChar is the first single quote.
     * <pre>
     * &lt;charLiteral&gt; ::= "'" &lt;{@link #charConstant}&gt; "'"
     * </pre>
     */
    protected Astro charLiteral() throws IOException, SyntaxException {
        nextChar();
        int ccode;
        do {
            ccode = charConstant();
        } while (-1 == ccode);
        if ('\'' != myChar) {
            syntaxError("char constant must end in \"'\"");
        }
        nextChar();
        //noinspection NumericCastThatLosesPrecision
        return myBuilder.leafChar((char)ccode, endSpan());
    }

    /**
     * Assumes that myChar is the first double quote.
     * <pre>
     * &lt;stringLiteral&gt; ::= '"' &lt;{@link #charConstant}&gt;* '"'
     * </pre>
     */
    protected Astro stringLiteral() throws IOException, SyntaxException {
        nextChar();
        Twine openner =
          getSpan(myOptStartPos, myPos, "File ends inside string literal");
        myIndenter.push(openner, '"', 0);
        StringBuffer buf = new StringBuffer();
        while ('"' != myChar) {
            if (isEndOfFile()) {
                needMore("File ends inside string literal");
            }
            int ccode = charConstant();
            if (-1 != ccode) {
                //noinspection NumericCastThatLosesPrecision
                buf.append((char)ccode);
            }
        }
        nextChar();
        Twine closer = endToken();
        myIndenter.pop('"', closer);
        return myBuilder.leafString(buf.toString(), closer.getOptSpan());
    }

    protected Twine getSpan(int start, int bound, String complaint) {
        if (isEndOfFile()) {
            needMore(complaint);
        }
        Twine openner = (Twine)myOptLTwine.run(start, bound);
        return openner;
    }

    /**
     * Assumes the initial '/**' has already been eaten.
     * <p/>
     * The docComment syntax is as documented in the Java Language
     * Specification.
     */
    protected Astro docComment(short tagCode)
      throws IOException, SyntaxException {

        //The openner is the initial '/**'
        Twine openner =
          getSpan(myOptStartPos, myPos, "File ends inside doc-comment");
        // line it up with the first '*' of '/**'
        myIndenter.push(openner, '*', myPos - 2);
        StringBuffer buf = new StringBuffer();

        String line = myOptLTwine.bare();
        int bound;
        while (-1 == (bound = line.indexOf("*/", myPos))) {
            buf.append(line.substring(myPos));
            skipLine();
            nextChar();
            if (isEndOfFile()) {
                needMore("File ends inside doc-comment");
            }
            skipWhiteSpace();
            if ('*' == myChar && '/' != peekChar()) {
                //skip leading whitespace and initial '*'
                nextChar();
            }
            line = myOptLTwine.bare();
        }
        buf.append(line.substring(myPos, bound));
        //skip the closing '*/'
        myPos = bound;
        nextChar();
        nextChar();
        Twine closer = endToken();
        myIndenter.pop('*', closer);
        return composite(tagCode, buf.toString(), closer.getOptSpan());
    }

    /**
     * Note that E never calls this with a leading minus sign, prefering
     * instead to treat the minus as an operator.
     * <pre>
     *     &lt;numberLiteral&gt; ::= "-"? "0x" &lt;{@link #digits
     *                                                    digits(16)}&gt;
     *     |                   "-"? "0" &lt;{@link #digitPart
     *                                             digitPart(8)}&gt;*
     *     |                   "-"? &lt;{@link #digits digits(10)}&gt;
     *                             ("." &lt;{@link #digits digits(10)}&gt;)?
     *                             (("e"|"E") "-"? &lt;{@link #digits
     * <p/>
     * digits(10)}&gt;)?
     * </pre>
     * A floating point number must have at least a "." or a ("e"|"E"). A
     * leading "0" on a floating point number doesn't affect the base. A
     * leading "0" on an integer means octal (base 8).
     */
    protected Astro numberLiteral() throws IOException, SyntaxException {
        // Handles floating point numbers as well as integers
        boolean floating = false;
        int radix = 10;
        if ('-' == myChar) {
            nextChar();
        }
        if ('0' == myChar) {
            radix = 8;
            nextChar();
            if ('x' == myChar || 'X' == myChar) {
                radix = 16;
                nextChar();
            }
        }
        if (16 == radix) {
            digits(16);
        } else {
            //even if radix == 8, we may instead have a floating point literal
            //XXX BUG: This will reject '0_3'
            digits(10);
            // If we have a decimal point and a digit, go for the fractional
            // part
            if ('.' == myChar && isDigitStart(peekChar(), 10)) {
                nextChar();
                floating = true;
                digits(10);
            }

            if (('E' == myChar) || ('e' == myChar)) {
                nextChar();
                floating = true;
                if ('-' == myChar) {
                    nextChar();
                }
                if (!digits(10)) {
                    syntaxError("Missing exponent");
                }
            }
        }
        Twine tok = endToken();
        String str = tok.replaceAll("_", "").bare();
        if (floating) {
            return myBuilder.leafFloat64(Double.parseDouble(str),
                                         tok.getOptSpan());
        } else {
            if (16 == radix) {
                //remove the leading "0x" to make BigInteger happy
                if ('-' == str.charAt(0)) {
                    str = "-" + str.substring(3);
                } else {
                    str = str.substring(2);
                }
            }
            if (8 == radix && 2 <= str.length()) {
                //As suggested by Ping or Dean
                syntaxError("Octal is no longer supported: " + str);
            }
            return myBuilder.leafInteger(EInt.run(str, radix),
                                         tok.getOptSpan());
        }
    }

    /**
     * XXX Get rid of peekChar/0 or make it work
     */
    protected char peekChar() {
        if (isEndOfFile()) {
            needMore("internal: can't peek here");
        }
        if ('\n' == myChar) {
            T.fail("internal: can't peek here");
        }
        int last = myOptLData.length - 1;

        if (myPos < last) {
            return myOptLData[myPos + 1];
        } else {
            return EOFCHAR;
        }
    }

    /**
     * Is the next character c?
     */
    protected boolean peekChar(char c) {
        if (isEndOfFile()) {
            needMore("internal: can't peek here");
        }
        if ('\n' == myChar) {
            T.fail("internal: can't peek here");
        }
        int last = myOptLData.length - 1;

        return myPos < last && c == myOptLData[myPos + 1];
    }

    /**
     * Skip the rest of this line.
     */
    protected void skipLine() {
        if (!isEndOfFile()) {
            myPos = myOptLData.length - 1;
            myChar = myOptLData[myPos];
            T.require('\n' == myChar,
                      "Internal: missing terminal newline: ",
                      myOptLTwine);
        }
        myDelayedNextChar = true;
    }

    /**
     *
     */
    protected void startToken() {
        if (-1 != myOptStartPos || null != myOptStartText) {
            throw new Error("internal: token already started");
        }
        myOptStartPos = myPos;
    }

    /**
     * Cancels a started token
     */
    protected void stopToken() {
        myOptStartPos = -1;
        myOptStartText = null;
    }

    /**
     *
     */
    protected Twine endToken() {
        Twine result;
        int pos = myPos;
        if (myDelayedNextChar) {
            pos++;
        }
        if (-1 == myOptStartPos) {
            if (null == myOptStartText) {
                throw new Error("internal: no current token");
            } else {
                //started on previous line
                result = myOptStartText;
                if (!isEndOfFile()) {
                    result = (Twine)result.add(myOptLTwine.run(0, pos));
                }
            }
        } else {
            //starts on this line
            result =
              getSpan(myOptStartPos, pos, "Internal: unexpected end-of-file");
        }
        stopToken();
        return result;
    }

    /**
     *
     */
    protected SourceSpan endSpan() {
        return endToken().getOptSpan();
    }

    /**
     *
     */
    public final boolean isEndOfFile() {
        return null == myOptLTwine;
    }
}
