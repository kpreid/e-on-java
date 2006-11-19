package org.erights.e.elang.syntax;

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
import org.erights.e.develop.exception.ThrowableSugar;
import org.erights.e.elang.interp.Rune;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.tables.Twine;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroBuilder;
import org.quasiliteral.astro.AstroTag;
import org.quasiliteral.syntax.BaseLexer;
import org.quasiliteral.syntax.FileFeeder;
import org.quasiliteral.syntax.LineFeeder;
import org.quasiliteral.syntax.ReplayLexer;
import org.quasiliteral.syntax.SyntaxException;
import org.quasiliteral.syntax.TwineFeeder;
import org.quasiliteral.syntax.URIKit;
import org.quasiliteral.term.TermBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Breaks textually input into a stream of tokens according to the E language's
 * defined grammar.
 *
 * @author Mark S. Miller
 */
public class ELexer extends BaseLexer {

//    /**
//     *
//     */
//    static public final AstroBuilder FOR_ASTS =
//      new ASTBuilder(EParser.DEFAULT_SCHEMA);

    /**
     *
     */
    static public final AstroBuilder FOR_TERMS =
      new TermBuilder(EParser.DEFAULT_SCHEMA);

    /**
     *
     */
    public ELexer(LineFeeder optLineFeeder,
                  boolean partialFlag,
                  boolean noTabsFlag) throws IOException {
        this(optLineFeeder, partialFlag, noTabsFlag, FOR_TERMS);
    }

    /**
     *
     */
    public ELexer(LineFeeder optLineFeeder,
                  boolean partialFlag,
                  boolean noTabsFlag,
                  AstroBuilder builder) throws IOException {
        super(optLineFeeder, EParser.EOL, EParser.EOTLU, partialFlag, false,
              //For E, the quasiFlag is handled instead by wrapping the
              //LineFeeder with a QuasiFeeder
              noTabsFlag, builder);
    }

    /**
     * @param sourceCode The source code itself
     * @param quasiFlag  Should doubled @ and $ be collapsed to singles?
     * @param noTabsFlag Should tabs be rejected as valid whitespace? Assumed
     *                   to correspond to the e.enable.notabs property.
     */
    static public ELexer make(Twine sourceCode,
                              boolean quasiFlag,
                              boolean noTabsFlag) throws IOException {
        LineFeeder lineFeeder = new TwineFeeder(sourceCode);
        if (quasiFlag) {
            lineFeeder = new QuasiFeeder(lineFeeder);
        }
        return new ELexer(lineFeeder, false, noTabsFlag);
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
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @return :Leaf (see {@link AstroBuilder}
     */
    public Astro nextToken() throws IOException, SyntaxException {
        Astro result;
        try {
            result = getNextToken();
        } finally {
            myOptStartPos = -1;
            myOptStartText = null;
        }
        int count = EParser.continueCount(result.getOptTagCode());
        if (count >= 0 && isRestBlank(myPos)) {
            myContinueCount = count;
            skipLine();
        }
        return result;
    }

    /**
     *
     */
    protected Astro getNextToken() throws IOException, SyntaxException {
        if (myDelayedNextChar) {
            nextChar();
            myDelayedNextChar = false;
        }

        if ('`' == myIndenter.getCloser()) {
            //start token without skipping whitespace
            startToken();
            return quasiPart();
        }
        skipWhiteSpace();
        startToken();

        switch (myChar) {
        case';':
        case',':
        case'~':
        case'?': {
            char c = myChar;
            nextChar();
            return leafTag((short)c, endSpan());
        }
        case EOFCHAR: {
            return leafTag(EOFTOK, null);
        }
        case'\n': {
            myDelayedNextChar = true;
            return leafEOL();
        }
        case'(': {
            return openBracket(')');
        }
        case')': {
            return closeBracket();
        }
        case'{': {
            return openBracket('}');
        }
        case'}': {
            Astro result = closeBracket();
            myIndenter.popIf('$');
            return result;
        }
        case'[': {
            return openBracket(']');
        }
        case']': {
            return closeBracket();
        }
        case'$': {
            nextChar();
            if (myChar == '{') {
                //A '${' opens a '}'
                nextChar();
                return openBracket(EParser.DollarOpen, endToken(), '}');

            } else if (isIdentifierStart(myChar)) {

                //A '$<ident>' closes a '$' if there was one.
                do {
                    nextChar();
                } while (isIdentifierPart(myChar));
                Twine name = endToken();
                String key = name.bare().substring(1);
                if (-1 != optKeywordType(key)) {
                    syntaxError(key + " is a keyword");
                }
                myIndenter.popIf('$');
                return composite(EParser.DollarIdent, key, name.getOptSpan());
            }
            return leafTag((short)'$', endSpan());
        }
        case'@': {
            nextChar();
            if (myChar == '{') {
                //A '@{' opens a '}'
                nextChar();
                return openBracket(EParser.AtOpen, endToken(), '}');

            } else if (myChar == '_' && !isIdentifierPart(peekChar())) {

                //A '@_' closes a '$' if there was one.
                nextChar();
                Twine name = endToken();
                myIndenter.popIf('$');
                return composite(EParser.AtIdent, "_", name.getOptSpan());

            } else if (isIdentifierStart(myChar)) {

                //A '@<ident>' closes a '@' if there was one.
                do {
                    nextChar();
                } while (isIdentifierPart(myChar));
                Twine name = endToken();
                String key = name.bare().substring(1);
                if (-1 != optKeywordType(key)) {
                    syntaxError(key + " is a keyword");
                }
                myIndenter.popIf('$');
                return composite(EParser.AtIdent, key, name.getOptSpan());
            }
            return leafTag((short)'@', endSpan());
        }
        case'.': {
            nextChar();
            if (myChar == '.') {
                nextChar();
                if (myChar == '!') {
                    nextChar();
                    return leafTag(EParser.OpTill, endSpan());
                }
                return leafTag(EParser.OpThru, endSpan());
            }
            return leafTag((short)'.', endSpan());
        }
        case'^': {
            nextChar();
            if (myChar == '=') {
                nextChar();
                return leafTag(EParser.OpAssXor, endSpan());
            }
            return leafTag((short)'^', endSpan());
        }
        case'+': {
            nextChar();
            if (myChar == '=') {
                nextChar();
                return leafTag(EParser.OpAssAdd, endSpan());
            } else if (myChar == '+') {
                nextChar();
                syntaxError("token \"++\" is reserved");
                return null; //keep compiler happy
            }
            return leafTag((short)'+', endSpan());
        }
        case'-': {
            nextChar();
            if (myChar == '=') {
                nextChar();
                return leafTag(EParser.OpAssSub, endSpan());
            } else if (myChar == '>') {
                nextChar();
                return leafTag(EParser.OpWhen, endSpan());
            } else if (myChar == '-') {
                nextChar();
                syntaxError("token \"--\" is reserved");
                return null; //keep compiler happy
            }
            return leafTag((short)'-', endSpan());
        }
        case':': {
            nextChar();
            if (myChar == '=') {
                nextChar();
                return leafTag(EParser.OpAss, endSpan());
            } else if (myChar == ':') {
                nextChar();
                return leafTag(EParser.OpScope, endSpan());
            }
            return leafTag((short)':', endSpan());
        }
        case'<': {
            nextChar();
            if (myChar == '-') {
                nextChar();
                if (myChar == '*') {
                    nextChar();
                    syntaxError("token \"<-*\" is reserved");
                    return null; //keep compiler happy
                }
                return leafTag(EParser.Send, endSpan());
            } else if (myChar == '=') {
                nextChar();
                if (myChar == '>') {
                    nextChar();
                    return leafTag(EParser.OpABA, endSpan());
                }
                return leafTag(EParser.OpLeq, endSpan());
            } else if (myChar == '<') {
                nextChar();
                if (myChar == '=') {
                    nextChar();
                    return leafTag(EParser.OpAssAsl, endSpan());
                }
                return leafTag(EParser.OpAsl, endSpan());
            } else if (isIdentifierStart(myChar)) {
                Astro optResult = optUri();
                if (null != optResult) {
                    return optResult;
                }
            }
            return leafTag((short)'<', endSpan());
        }
        case'>': {
            nextChar();
            if (myChar == '=') {
                nextChar();
                return leafTag(EParser.OpGeq, endSpan());
            } else if (myChar == '>') {
                nextChar();
                if (myChar == '=') {
                    nextChar();
                    return leafTag(EParser.OpAssAsr, endSpan());
                }
                return leafTag(EParser.OpAsr, endSpan());
            }
            return leafTag((short)'>', endSpan());
        }
        case'*': {
            nextChar();
            if (myChar == '*') {
                nextChar();
                if (myChar == '=') {
                    nextChar();
                    return leafTag(EParser.OpAssPow, endSpan());
                }
                return leafTag(EParser.OpPow, endSpan());
            } else if (myChar == '=') {
                nextChar();
                return leafTag(EParser.OpAssMul, endSpan());
            } else if (myChar == '-' && peekChar('>')) {
                nextChar();
                nextChar();
                syntaxError("token \"*->\" is reserved");
                return null; //keep compiler happy
            } else if (myChar == '/') {
                nextChar();
                syntaxError("'/*..*/' comments are reserved. " +
                  "Use '#' on each line instead");
            }
            return leafTag((short)'*', endSpan());
        }
        case'/': {
            nextChar();
            if (myChar == '=') {
                nextChar();
                return leafTag(EParser.OpAssAprxDiv, endSpan());
            } else if (myChar == '/') {
                nextChar();
                if (myChar == '=') {
                    nextChar();
                    return leafTag(EParser.OpAssFlrDiv, endSpan());
                }
                return leafTag(EParser.OpFlrDiv, endSpan());
            } else if (myChar == '*') {
                nextChar();
                if (myChar == '*') {
                    nextChar();
                    return docComment(EParser.DocComment);
                }
                syntaxError("'/*..*/' comments are reserved. " +
                  "Use '#' on each line instead," +
                  " or '/**' for doc-comments.");
            }
            return leafTag((short)'/', endSpan());
        }
        case'#': {
            // Skip comment to end of line
            skipLine();
            return leafEOL();
        }
        case'\\': {
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
        case'%': {
            nextChar();
            if (myChar == '%') {
                nextChar();
                if (myChar == '=') {
                    // check for "%%="
                    nextChar();
                    return leafTag(EParser.OpAssMod, endSpan());
                }
                return leafTag(EParser.OpMod, endSpan());
            } else if (myChar == '=') {
                // check for "%="
                nextChar();
                return leafTag(EParser.OpAssRemdr, endSpan());
            }
            return leafTag((short)'%', endSpan());
        }
        case'!': {
            nextChar();
            if (myChar == '=') {
                nextChar();
                return leafTag(EParser.OpNSame, endSpan());
            } else if (myChar == '~') {
                nextChar();
                return leafTag(EParser.MisMatch, endSpan());
            }
            return leafTag((short)'!', endSpan());
        }
        case'=': {
            nextChar();
            if (myChar == '=') {
                nextChar();
                return leafTag(EParser.OpSame, endSpan());
            } else if (myChar == '>') {
                nextChar();
                return leafTag(EParser.MapsTo, endSpan());
            } else if (myChar == '~') {
                nextChar();
                return leafTag(EParser.MatchBind, endSpan());
            }
            syntaxError("use ':=' for assignment, or '==' for equality");
            return null; //keep compiler happy
        }
        case'&': {
            nextChar();
            if (myChar == '&') {
                nextChar();
                return leafTag(EParser.OpLAnd, endSpan());
            } else if (myChar == '=') {
                nextChar();
                return leafTag(EParser.OpAssAnd, endSpan());
            } else if (myChar == '!') {
                nextChar();
                return leafTag(EParser.OpButNot, endSpan());
            }
            return leafTag((short)'&', endSpan());
        }
        case'|': {
            nextChar();
            if (myChar == '|') {
                nextChar();
                return leafTag(EParser.OpLOr, endSpan());
            } else if (myChar == '=') {
                nextChar();
                return leafTag(EParser.OpAssOr, endSpan());
            }
            return leafTag((short)'|', endSpan());
        }
        case'\'': {
            return charLiteral();
        }
        case'"': {
            return stringLiteral();
        }
        case'`': {
            //eat the backquote here so quasiPart can also
            //be called when we're continuing after a hole, in which
            //case there is no leading backquote.
            nextChar();
            Twine openner = (Twine)myLTwine.run(myOptStartPos, myPos);
            myIndenter.push(openner, '`', 0);
            return quasiPart();
        }
        case'0':
        case'1':
        case'2':
        case'3':
        case'4':
        case'5':
        case'6':
        case'7':
        case'8':
        case'9': {
            return numberLiteral();
        }
        case'_': {
            if (isIdentifierPart(peekChar())) {
                return identifier();
            }
            nextChar();
            if (myChar == '/') {
                syntaxError("For division,\n" +
                  "use '//' to truncate to the least integer,\n" +
                  "'truncDivide' to truncate to the int " +
                  "nearest to zero (least magnitude),\n" +
                  "and '/' for a float64 approximation.");
            }
            return leafTag(EParser._, endSpan());
        }
        default: {
            if (isIdentifierStart(myChar)) {
                return identifier();
            } else {
                syntaxError("unrecognized character: '" + myChar + "' code: " +
                  (int)myChar);
                return null; //keep compiler happy
            }
        }
        }
    }

    /**
     * If 'name' is a keyword, return it's token tag code, else -1.
     * <p/>
     * Note that E keywords are case insensitive, so 'name' is first
     * toLowerCase()d.
     */
    private short optKeywordType(String name) {
        name = name.toLowerCase();
        AstroTag optTag = myBuilder.getSchema().getOptTagForName(name);
        if (null == optTag) {
            return -1;
        } else {
            return optTag.getOptTagCode();
        }
    }

    /**
     * If the verb is a {@link org.erights.e.elang.syntax.ELexer#isIdentifier
     * valid E identifier}, then print it, else print it as a quoted string.
     *
     * @param out
     * @throws java.io.IOException
     */
    public static void printVerbOn(String verb, TextWriter out)
      throws IOException {
        if (isIdentifier(verb)) {
            out.print(verb);
        } else {
            out.quote(verb);
        }
    }

    /**
     * If the noun is a {@link org.erights.e.elang.syntax.ELexer#isIdentifier
     * valid E identifier}, then print it, else print it as "::" followed by a
     * quoted string.
     *
     * @param out
     * @throws java.io.IOException
     */
    public static void printNounOn(String noun, TextWriter out)
      throws IOException {
        if (isIdentifier(noun)) {
            out.print(noun);
        } else {
            out.print("::");
            out.quote(noun);
        }
    }

    /**
     * If it {@link org.quasiliteral.syntax.BaseLexer#isIdentifierOrKeyword}
     * and is not a {@link #optKeywordType keyword}.
     */
    static public boolean isIdentifier(String str) {
        if (!isIdentifierOrKeyword(str)) {
            return false;
        }
        str = str.toLowerCase();
        return null == FOR_TERMS.getSchema().getOptTagForName(str);
    }

    /**
     * Called with myChar as the first character of the identifier.
     * <pre>
     *     &lt;identifier&gt; ::= &lt;idStart&gt; &lt;idPart&gt;*
     * </pre>
     */
    private Astro identifier() throws IOException, SyntaxException {
        do {
            nextChar();
        } while (isIdentifierPart(myChar));

        if ('=' == myChar) {
            char c = peekChar();
            if ("=>~".indexOf(c) == -1) {
                //'<ident>=' is a VerbAss, given that the '=' isn't
                //part of a '==', '=>', or '=~'.
                nextChar();
                Twine source = endToken();
                String name = source.bare().substring(0, source.size() - 1);
                if (-1 != optKeywordType(name)) {
                    syntaxError(name + " is a keyword");
                }
                return composite(EParser.VerbAssign,
                                 name,
                                 source.getOptSpan());
            }
        }
        Twine source = endToken();
        short tagCode = optKeywordType(source.bare());
        if (-1 == tagCode) {
            return composite(EParser.ID, source.bare(), source.getOptSpan());
        } else {
            //keyword
            return leafTag(tagCode, source.getOptSpan());
        }
    }

    /**
     * XXX In order to enable optValue to be recovered from tagCode and source,
     * we need four types rather than the current two: QuasiOpen and
     * QuasiClose.
     * <pre>
     *     &lt;nonender&gt; ::= any character but '$', '@', or '`'
     *     &lt;qconst&gt; ::= &lt;nonender&gt; | "$$" | "@@"
     *     |            "$\" &lt;charEscape&gt;
     *     &lt;quasiFull&gt; ::= "`" &lt;qconst&gt;* ("$" | "@")? "`"
     *     &lt;quasiOpen&gt; ::= "`" &lt;qconst&gt;* ("$" | "@")
     *     &lt;quasiMid&gt;  ::=     &lt;qconst&gt;* ("$" | "@")
     *     &lt;quasiClose&gt; ::=    &lt;qconst&gt;* ("$" | "@")? "`"
     * </pre>
     */
    private Astro quasiPart() throws IOException, SyntaxException {
        StringBuffer buf = new StringBuffer();
        while (true) {
            while ("$@`".indexOf(myChar) == -1) {
                if (myChar == EOFCHAR) {
                    needMore("File end inside quasi-string literal");
                }
                buf.append(myChar);
                nextChar();
            }
            //myChar is a '$', '@', or '`'

            if (peekChar(myChar)) {
                //A doubled $, @, or ` is uninterpreted
                buf.append(myChar);
                if (myChar == '$' || myChar == '@') {
                    //A doubled $ or @ stays double at this stage (to be
                    //turned into a single by the quasi-parser). Whereas a
                    //doubled ` becomes single immediately
                    buf.append(myChar);
                }
                nextChar();
                nextChar();

            } else if (myChar == '`') {
                //terminal backquote is eaten but not added to the
                //value of the resulting QuasiClose token.
                nextChar();
                Twine closer = endToken();
                myIndenter.pop('`', closer);
                return composite(EParser.QuasiClose,
                                 buf.toString(),
                                 closer.getOptSpan());

            } else if (peekChar('`')) {
                //terminal '$' or '@' before a '`' is eaten and added to the
                //value of the imminent QuasiClose token. Therefore a quasi
                //may end in "$`" or "@`" without needing to double the '$'
                //or '@'. This allows Perl regular expressions to have an
                //undoubled terminal $.
                buf.append(myChar);
                nextChar();

            } else if (myChar == '$' && peekChar('\\')) {
                //See
                // https://bugs.sieve.net/bugs/\
                // ?func=detailbug&bug_id=125408&group_id=16380
                nextChar();
                int ccode = charConstant();
                if (-1 != ccode) {
                    buf.append((char)ccode);
                }

            } else {
                Twine openner = endToken();
                //Pushes a '$' to protect the hole from the '`'
                myIndenter.nest(openner, '$');
                //interpolated '$' or '@' is neither eaten nor added to the
                //value of the resulting QuasiOpen token.
                return composite(EParser.QuasiOpen,
                                 buf.toString(),
                                 openner.getOptSpan());
            }
        }
    }

    /**
     * Eat a URI or a URIGetter.
     * <p/>
     * Assumes the '&lt;' is already eaten and myChar is the first character of
     * the protocol identifier. If the identifier is not immediately followed
     * by a ":" or ">", return null and cause no side effects -- in particular,
     * do not effect the current position.
     * <pre>
     *     &lt;uri&gt; ::= '&lt;' {@link #identifier &lt;identifier&gt;
     * } ':' {@link URIKit#isURIC &lt;uric&gt;}* '&gt;'
     *     &lt;uriGetter&gt; ::= '&lt;' &lt;identifier&gt; '&gt;'
     * </pre>
     */
    private Astro optUri() throws IOException, SyntaxException {
        int len = myLData.length;
        int pos = myPos + 1;
        while (pos < len && isIdentifierPart(myLData[pos])) {
            pos++;
        }
        if (pos >= len) {
            //false alarm
            return null;
        }
        if (myLData[pos] == '>') {
            myPos = pos;
            nextChar();
            Twine token = endToken();
            //the '<' and '>' aren't part of the URIGetter data
            Twine varName =
              (Twine)token.run(1, token.size() - 1).add("__uriGetter");
            varName = URIKit.normalize(varName);
            return composite(EParser.ID, varName, token.getOptSpan());
        }
        if (myLData[pos] != ':') {
            //false alarm
            return null;
        }
        //it is a colon. Commit.
        myPos = pos;
        nextChar();
        if (!URIKit.isURIC(myChar)) {
            if (Character.isWhitespace(myChar)) {
                syntaxError("calc-uri syntax is no longer supported. " +
                  "Use '<protocol>[expr]' instead.");
            } else {
                syntaxError(
                  "Can't use \"" + myChar + "\" to start a URI body");
            }
        }
        do {
            nextChar();
        } while (URIKit.isURIC(myChar));
        if (myChar != '>') {
            syntaxError("Can't use \"" + myChar + "\" in a URI body");
        }
        nextChar();
        Twine source = endToken();
        //the surrounding '<' and '>' aren't part of the URI data
        Twine uriText = (Twine)source.run(1, source.size() - 1);
        uriText = URIKit.normalize(uriText);
        return composite(EParser.URI, uriText, source.getOptSpan());
    }

    /**
     *
     */
    protected void skipWhiteSpace() throws IOException {
        //XXX should delegate most of the work to super.skipWhiteSpace()
        while (true) {
            if (myChar == EOFCHAR) {
                return;
            }
            if (Character.isWhitespace(myChar)) {
                if (myChar == '\n') {
                    return;
                }
                if (myChar == '\t') {
                    if (myNoTabsFlag) {
                        syntaxError("The optional e.enable.notabs" +
                          " feature " + Rune.SYN_PROPS_EXPLAIN +
                          " is currently on,\n" +
                          "so tabs are not considered valid whitespace");
                    }
                    //else, we should warn, but XXX we don't yet have a
                    //warning mechanism.
                }
                nextChar();
            } else {
                if ((myChar == '?' || myChar == '>') && isWhite(0, myPos)) {
                    //Is the first non-whitespace character on this line a
                    //'?' or '>'?
                    //If yes, so treat it as an updoc line
                    skipLine();
                } else {
                    //If not, treat it normally.
                    return;
                }
            }
        }
    }

    /**
     * Just for testing. Reads an input file and prints one token per line to
     * stdout.
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
        ELexer lex = new ELexer(lr, false, false);
        while (true) {
            try {

//                Astro t;
//                do {
//                    t = lex.nextToken();
//                    //XXX should print t as a Functor.
//                    stdout.println(t);
//                    if (t.getOptTagCode() == EParser.EOL) {
//                        stdout.print("stack: ",
//                                     lex.myIndenter.toString(),
//                                     "\n");
//                    }
//                } while (t.getOptTagCode() != EParser.EOFTOK);

                Astro[] t;
                do {
                    t = lex.nextTopLevelUnit();
                    //XXX should print t as a Functor.
                    stdout.println(t);
                    TextWriter warns =
                      new TextWriter(PrintStreamWriter.stderr());
                    ReplayLexer replayer = new ReplayLexer(t, FOR_TERMS);
                    EParser ep = EParser.make(replayer, warns);
                    try {
                        stdout.println(ep.optParse());
                    } catch (SyntaxException sex) {
                        stdout.println("oops: " + sex);
                    }
                } while (t.length >= 1);

                return;
            } catch (Throwable problem) {
                Throwable leaf = ThrowableSugar.leaf(problem);
                TextWriter err =
                  new TextWriter(PrintStreamWriter.stderr(), true);
                err.indent("# ").print("# ", leaf);
                err.println();
            }
        }
    }
}
