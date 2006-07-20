package org.quasiliteral.term;

/*
The contents of this file are subject to the Electric Communities E Open
Source Code License Version 1.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License
at http://www.communities.com/EL/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
the specific language governing rights and limitations under the License.

The Original Code is the Distributed E Language Implementation, released
July 20, 1998.

The Initial Developer of the Original Code is Electric Communities.
Copyright (C) 1998 Electric Communities. All Rights Reserved.

Contributor(s): ______________________________________.
*/

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.PrintStreamWriter;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.tables.Twine;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroBuilder;
import org.quasiliteral.syntax.BaseLexer;
import org.quasiliteral.syntax.FileFeeder;
import org.quasiliteral.syntax.LineFeeder;
import org.quasiliteral.syntax.SyntaxException;
import org.quasiliteral.syntax.TwineFeeder;
import org.quasiliteral.syntax.URIKit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Breaks textually input into a stream of tokens according to the Term
 * language's defined grammar.
 *
 * @author Mark S. Miller
 */
public class TermLexer extends BaseLexer {

    /**
     *
     */
    public TermLexer(LineFeeder input,
                     boolean partialFlag,
                     boolean quasiFlag,
                     boolean noTabsFlag)
      throws IOException {
        this(input,
             partialFlag,
             quasiFlag,
             noTabsFlag,
             TermBuilder.FOR_TERMS);
    }

    /**
     *
     */
    public TermLexer(LineFeeder input,
                     boolean partialFlag,
                     boolean quasiFlag,
                     boolean noTabsFlag,
                     AstroBuilder builder)
      throws IOException {
        super(input,
              TermParser.EOL,
              TermParser.EOTLU,
              partialFlag,
              quasiFlag,
              noTabsFlag,
              builder);
    }

    /**
     * @param sourceCode The source code itself
     * @param quasiFlag  Should doubled @ and $ be collapsed to singles?
     * @param noTabsFlag Should tabs be rejected as valid whitespace?
     *                   Assumed to correspond to the e.enable.notabs
     *                   property.
     */
    static public TermLexer make(Twine sourceCode,
                                 boolean quasiFlag,
                                 boolean noTabsFlag)
      throws IOException {
        LineFeeder lineFeeder = new TwineFeeder(sourceCode);
        return new TermLexer(lineFeeder,
                             false,
                             quasiFlag,
                             noTabsFlag);
    }

    /**
     * Consider '#' comments to be blank as well.
     */
    protected boolean isRestBlank(int start) {
        for (int i = start, len = myLData.length; i < len; i++) {
            char ch = myLData[i];
            if (!Character.isWhitespace(ch)) {
                if ('#' == ch) {
                    return true;
                } else if ('/' == ch && i + 1 < len && '/' == myLData[i + 1]) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *
     */
    protected Astro getNextToken() throws IOException, SyntaxException {
        if (myDelayedNextChar) {
            nextChar();
            myDelayedNextChar = false;
        }

        skipWhiteSpace();
        startToken();

        switch (myChar) {
        case '&':
        case '|':
        case '^':
        case ',':
        case ';':
        case '!':
        case '?':
            {
                char c = myChar;
                nextChar();
                return leafTag((short)c, endSpan());
            }
        case '*':
            {
                nextChar();
                if ('*' == myChar) {
                    nextChar();
                    return leafTag(TermParser.OpDoubleStar, endSpan());
                }
                return leafTag((short)'*', endSpan());
            }
        case '+':
            {
                nextChar();
                if ('+' == myChar) {
                    nextChar();
                    return leafTag(TermParser.OpDoublePlus, endSpan());
                }
                return leafTag((short)'+', endSpan());
            }
        case '=':
            {
                nextChar();
                if ('>' == myChar) {
                    nextChar();
                    syntaxError("'=>' is reserved");
                }
                return leafTag((short)'=', endSpan());
            }
        case '-':
            {
                if (peekChar('>')) {
                    nextChar();
                    nextChar();
                    return leafTag(TermParser.OpAction, endSpan());
                }
                return numberLiteral();
            }
        case '/':
            {
                nextChar();
                if ('/' == myChar) {
                    skipLine();
                    return leafEOL();
                } else if ('*' == myChar) {
                    nextChar();
                    skipBlockComment();
                    return getNextToken();
                }
                return leafTag((short)'/', endSpan());
            }
        case ':':
            {
                nextChar();
                if (':' == myChar) {
                    nextChar();
                    if ('=' == myChar) {
                        nextChar();
                        return leafTag(TermParser.OpDef, endSpan());
                    }
                    return tag();
                }
                return leafTag((short)':', endSpan());
            }
        case '.':
            {
                if (isSegStart(peekChar())) {
                    return tag();
                }
                nextChar();
                if ('.' == myChar) {
                    nextChar();
                    return leafTag(TermParser.OpThru, endSpan());
                }
                return leafTag((short)'.', endSpan());
            }
        case '$':
            {
                if (myQuasiFlag) {
                    if (peekChar('$')) {
                        //If we're quasi-parsing, then a doubled '$'
                        //is actually a single '$' beginning a tag.
                        return tag();
                    } else {
                        //If we're quasi-parsing, then a single '$' is
                        //its own token.
                        nextChar();
                        return leafTag((short)'$', endSpan());
                    }
                } else {
                    //If we're not quasi-parsing, then a single '$' is
                    //a single '$' beginning a tag.
                    return tag();
                }
            }
        case '@':
            {
                if (myQuasiFlag) {
                    if (peekChar('@')) {
                        //If we're quasi-parsing, then a doubled '@'
                        //is actually a single '@', which doesn't begin
                        //any tokens in this grammar.
                        syntaxError("Unexpected @@");
                    } else {
                        //If we're quasi-parsing, then a single '@' is
                        //its own token.
                        nextChar();
                        return leafTag((short)'@', endSpan());
                    }
                } else {
                    //If we're not quasi-parsing, then a single '@' is
                    //a single '@', which doesn't begin
                    //any tokens in this grammar.
                    syntaxError("Unexpected @");
                }
            }

        case '[':
            {
                return openBracket(']');
            }
        case ']':
            {
                return closeBracket();
            }
        case '(':
            {
                return openBracket(')');
            }
        case ')':
            {
                return closeBracket();
            }
        case '{':
            {
                return openBracket('}');
            }
        case '}':
            {
                return closeBracket();
            }

        case EOFCHAR:
            {
                return leafTag(EOFTOK, null);
            }
        case '\n':
            {
                myDelayedNextChar = true;
                return leafEOL();
            }
        case '#':
            {
                skipLine();
                return leafEOL();
            }
        case '\\':
            {
                nextChar();
                if (myChar == 'u' || myChar == 'U') {
                    syntaxError("\\u... not yet implemented");
                    return null; //keep compiler happy
                }
                //an escaped newline is insensitive to trailing
                //whitespace, since that's invisible anyway.
                skipWhiteSpace();
                if (myChar == '\n') {
                    myContinueCount = 2;
                    skipLine();
                    stopToken();
                    Astro result = getNextToken();
                    if (result.getOptTagCode() == EOFTOK) {
                        needMore("continued line");
                        return null; //make compiler happy
                    } else {
                        return result;
                    }
                }
                syntaxError("unrecognized escape");
                return null; //keep compiler happy
            }
        case '\'':
            {
                return charsLiteral();
            }
        case '"':
            {
                return stringLiteral();
            }
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            {
                return numberLiteral();
            }
        case '<':
            {
                return tag();
            }
        default:
            {
                if (isSegStart(myChar)) {
                    return tag();
                } else {
                    syntaxError("unrecognized character: '" +
                                myChar +
                                "' code: " + (int)myChar);
                    return null; //keep compiler happy
                }
            }
        }
    }

    /**
     * Assumes that myChar is the first single quote.
     * <pre>
     * &lt;charsLiteral&gt; ::= "'" &lt;{@link #charConstant}&gt;+ "'"
     * </pre>
     */
    protected Astro charsLiteral() throws IOException, SyntaxException {
        nextChar();
        Twine openner = (Twine)myLTwine.run(myOptStartPos, myPos);
        myIndenter.push(openner, '\'', 0);
        StringBuffer buf = new StringBuffer();
        while (myChar != '\'') {
            if (myChar == EOFCHAR) {
                needMore("File ends inside string literal");
            }
            int ccode = charConstant();
            if (-1 != ccode) {
                buf.append((char)ccode);
            }
        }
        nextChar();
        Twine closer = endToken();
        myIndenter.pop('\'', closer);
        SourceSpan optSpan = closer.getOptSpan();
        int len = buf.length();
        if (0 == len) {
            syntaxError("Chars literal must have at least one char");
            return null; // make compiler happy
        } else if (1 == len) {
            return myBuilder.leafChar(buf.charAt(0), optSpan);
        } else {
            return composite(TermParser.LiteralChars,
                             buf.toString(),
                             optSpan);
        }
    }

    /**
     * Any character that may
     * {@link java.lang.Character#isJavaIdentifierStart(char) start}
     * a Java identifier.
     * <p/>
     * Note that a Java identifier may start with a '_' or '$'.
     * <p/>
     * This includes all the characters that may start an
     * <a href="http://www.w3.org/TR/REC-xml-names/#NT-NCName"
     * >XML-NS-NCName</a> as well as '$'.
     * <p/>
     * This also includes all the characters that may start a Java or E
     * fully qualified name.
     */
    static public boolean isSegStart(char c) {
        return isJavaIdStart(c);
    }

    /**
     * Any character that may
     * {@link java.lang.Character#isJavaIdentifierPart(char) be a part of}
     * a Java identifier, or a '.' or '-'.
     * <p/>
     * Note that a Java identifier may contain '_'s and '$'s.
     * <p/>
     * Not yet dealt with are the
     * <a href=
     * "http://www.w3.org/TR/2000/REC-xml-20001006#NT-CombiningChar"
     * >XML CombiningChar</a>s or the <a href=
     * "http://www.w3.org/TR/2000/REC-xml-20001006#NT-Extender"
     * >XML Extender</a>s, both of which should be included in isSegPart.
     * Once these are included, then this will include all the characters
     * that can be part of an <a href=
     * "http://www.w3.org/TR/REC-xml-names/#NT-NCNameChar"
     * >XML-NS-NCNameChar</a> as well as '$'.
     * <p/>
     * This also includes all the characters that may be a part of a Java or
     * E fully qualified name.
     */
    static public boolean isSegPart(char c) {
        return isJavaIdPart(c) || '.' == c || '-' == c;
    }

    /**
     * A tag is a sequence of tag-segments separated by double-colons.
     * <pre>
     *     &lt;tag&gt;     ::= &lt;segment&gt; ('::' &lt;sos&gt;)*
     *     |             ('::' &lt;sos&gt;)+;
     *     &lt;sos&gt;     := &lt;segment&gt; | &lt;string&gt;
     *     &lt;segment&gt; ::= &lt;segStart&gt; &lt;segPart&gt;*
     *     |             '.' &lt;segStart&gt; &lt;segPart&gt;*
     *     |             '&lt;' &lt;uric&gt;* '&gt;';
     * </pre>
     * Because of the second segment production, a segment may also begin
     * with a '.', though neither an <a href=
     * "http://www.w3.org/TR/REC-xml-names/#NT-NCName"
     * >XML-NS-NCName</a> nor a Java or E fully qualified name may begin with
     * a '.'. Therefore, names that begin with a '.' (like ".bag.") can be
     * used in a keyword-like way without conflicting with these other
     * namespaces. Out of Fortran nostalgia, by convention such names end
     * with a '.' as well.
     * <p>
     * If there was a leading double colon, it's already been eaten by the time
     * we get here. Otherwise, we're still looking at the first character, so
     * in all cases we start with the first character of the first &lt;sos&gt;
     * (segment or string). If the first character is a double-quote, we can
     * assume that a double-colon has already been eaten, as we wouldn't have
     * gotten here otherwise.
     *
     * @see <a href=
     * "http://www.eros-os.org/pipermail/e-lang/2005-January/010355.html"
     * >(Section at end:) Quasi-JSON back from the dead</a>
     */
    private Astro tag() throws IOException, SyntaxException {
        while (true) {
            if ('<' == myChar) {
                do {
                    if (myQuasiFlag && ('$' == myChar || '@' == myChar)) {
                        if (peekChar(myChar)) {
                            //If we're quasi-parsing, then a double '$' or '@'
                            //in a tag's uri is included as a single '$' or
                            //'@'. We collapse doubles to singles separately
                            //below.
                            nextChar();
                            nextChar();
                        } else {
                            //If we're quasi-parsing, then a single '$' or '@'
                            //terminates the tag without being included.
                            break;
                        }
                    } else {
                        //If we're not quasi-parsing, or the next uric
                        //character isn't a '$' or '@', then it's just
                        //included in the tag.
                        nextChar();
                    }
                    if (EOFCHAR == myChar) {
                        needMore("end of input in middle of tag");
                    }
                } while (URIKit.isURIC(myChar));
                T.require('>' == myChar,
                          "missing '>' inside tag");
                nextChar();
            } else if ('"' == myChar) {
                // XXX When we do implement this, we need to fix the
                // normalizations of '\\' and '|' below so that they operate
                // only within uris and not quoted strings
                syntaxError("XXX Not yet implemented: quoted strings in tags");
                return null; // make compiler happy
            } else {
                if ('.' == myChar) {
                    nextChar();
                }
                T.require(isSegStart(myChar),
                          "tag segment expected: '" + myChar + "'");
                do {
                    if (myQuasiFlag && '$' == myChar) {
                        if (peekChar('$')) {
                            //If we're quasi-parsing, then a double '$' in a
                            //tag is included as a single '$'. We collapse
                            //doubles to singles separately below.
                            nextChar();
                            nextChar();
                        } else {
                            //If we're quasi-parsing, then a single '$'
                            //terminates the tag without being included.
                            break;
                        }
                    } else {
                        //If we're not quasi-parsing, or the next
                        //segStart/segPart character isn't a '$', then it's
                        //just included in the tag.
                        nextChar();
                    }
                } while (isSegPart(myChar));
            }

            if (':' == myChar && peekChar(':')) {
                nextChar();
                nextChar();
            } else {
                break;
            }
        }
        Twine source = endToken();
        SourceSpan optSpan = source.getOptSpan();
        if (myQuasiFlag) {
            source = source.replaceAll("$$", "$").replaceAll("@@", "@");
        }
        //The term grammar has no keywords, so all apparent tags are
        //actual tags
        //We open code the normalization here so that if URIKit.normalize/1
        //is upgraded to do '%' processing, we don't apply it naively, since
        //we'd then need to apply it only within uric* strings.
        // XXX Bug to be: When we do accept quoted strings (see above), then
        // the following normalization needs to be changed to apply only
        // within uris and not quoted strings.
        return composite(TermParser.Tag,
                         source.bare().replace('\\', '/').replace('|', ':'),
                         optSpan);
    }

    /**
     * Assumes the initial '/*' has already been eaten.
     */
    private void skipBlockComment()
      throws IOException, SyntaxException {

        //The openner is the initial '/*'
        Twine openner = (Twine)myLTwine.run(myOptStartPos, myPos);
        // line it up with the first '*' of '/*'
        myIndenter.push(openner, '*', myPos - 2);

        String line = myLTwine.bare();
        int bound;
        while ((bound = line.indexOf("*/", myPos)) == -1) {
            skipLine();
            nextChar();
            if (myChar == EOFCHAR) {
                needMore("File ends inside block-comment");
            }
            skipWhiteSpace();
            if (myChar == '*' && peekChar() != '/') {
                //skip leading whitespace and initial '*'
                nextChar();
            }
            line = myLTwine.bare();
        }
        //skip the closing '*/'
        myPos = bound;
        nextChar();
        nextChar();
        Twine closer = endToken();
        myIndenter.pop('*', closer);
    }

    /**
     * Just for testing. Reads an input file and prints one token per line
     * to stdout.
     */
    static public void main(String[] args)
      throws IOException, SyntaxException {

        TextWriter stdout = new TextWriter(PrintStreamWriter.stdout(), true);
        String url;
        BufferedReader ins;
        if (0 == args.length) {
            url = "stdin";
            ins = PrintStreamWriter.stdin();
        } else if (1 == args.length) {
            url = args[0];
            ins = new BufferedReader(new FileReader(args[0]));
        } else {
            T.fail("usage: java org.erights.e.elang.syntax.ELexer file");
            return; //make compiler happy
        }
        LineFeeder lr = new FileFeeder(url, ins, stdout);
        TermLexer lex = new TermLexer(lr, false, false, false);
        while (true) {
            try {
                Astro t;
                do {
                    t = lex.nextToken();
                    stdout.println(t);
                    short code = t.getOptTagCode();
                    if (code == TermParser.EOL || code == TermParser.EOTLU) {
                        stdout.print("stack: ",
                                     lex.myIndenter.toString(),
                                     "\n");
                    }
                } while (t.getOptTagCode() != EOFTOK);
                return;
            } catch (SyntaxException sex) {
                TextWriter err = new TextWriter(PrintStreamWriter.stderr(),
                                                true);
                err.indent("# ").print("# ", sex);
                err.println();
            }
        }
    }
}
