package com.hp.orc;

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
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.tables.Twine;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroBuilder;
import org.quasiliteral.astro.AstroTag;
import org.quasiliteral.syntax.BaseLexer;
import org.quasiliteral.syntax.FileFeeder;
import org.quasiliteral.syntax.LineFeeder;
import org.quasiliteral.syntax.SyntaxException;
import org.quasiliteral.syntax.TwineFeeder;
import org.quasiliteral.term.TermBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Breaks textually input into a stream of tokens according to the Orc
 * language's defined grammar.
 *
 * @author Mark S. Miller
 */
public class OrcLexer extends BaseLexer {

    /**
     *
     */
    static public final AstroBuilder FOR_TERMS =
      new TermBuilder(OrcParser.DEFAULT_SCHEMA);

    /**
     *
     */
    public OrcLexer(LineFeeder input,
                    boolean partialFlag,
                    boolean quasiFlag,
                    boolean noTabsFlag) throws IOException {
        super(input,
              OrcParser.EOL,
              OrcParser.EOTLU,
              partialFlag,
              quasiFlag,
              noTabsFlag,
              FOR_TERMS);
    }

    /**
     * @param sourceCode The source code itself
     * @param quasiFlag  Should doubled @ and $ be collapsed to singles?
     * @param noTabsFlag Should tabs be rejected as valid whitespace? Assumed
     *                   to correspond to the e.enable.notabs property.
     */
    static public OrcLexer make(Twine sourceCode,
                                boolean quasiFlag,
                                boolean noTabsFlag) throws IOException {
        LineFeeder lineFeeder = new TwineFeeder(sourceCode);
        return new OrcLexer(lineFeeder, false, quasiFlag, noTabsFlag);
    }

    /**
     * Consider '#' comments to be blank as well as '//' comments.
     */
    protected boolean isRestBlank(int start) {
        if (isEndOfFile()) { return true; }
        for (int i = start, len = myOptLData.length; i < len; i++) {
            char ch = myOptLData[i];
            if (!Character.isWhitespace(ch)) {
                if ('#' == ch) {
                    return true;
                }
                return '/' == ch && i + 1 < len &&
                  '/' == myOptLData[i + 1];
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
        case'=':
        case',':
        case';':
        case'>':
        case'!':
        case'|':
        case'$':
        case'@': {
            char c = myChar;
            nextChar();
            return leafTag((short)c, endSpan());
        }
        case'-': {
            if (peekChar('-')) {
                skipLine();
                return leafEOL();
            }
            return numberLiteral();
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
            return closeBracket();
        }

        case EOFCHAR: {
            return leafTag(EOFTOK, null);
        }
        case'\n': {
            myDelayedNextChar = true;
            return leafEOL();
        }
        case'#': {
            skipLine();
            return leafEOL();
        }
        case'\\': {
            nextChar();
            if ('u' == myChar || 'U' == myChar) {
                syntaxError("\\u... not yet implemented");
                return null; //keep compiler happy
            }
            //an escaped newline is insensitive to trailing
            //whitespace, since that's invisible anyway.
            skipWhiteSpace();
            if ('\n' == myChar) {
                myContinueCount = 2;
                skipLine();
                stopToken();
                Astro result = getNextToken();
                if (EOFTOK == result.getOptTagCode()) {
                    needMore("continued line");
                    return null; //make compiler happy
                }
                return result;
            }
            syntaxError("unrecognized escape");
            return null; //keep compiler happy
        }
        case'"': {
            return stringLiteral();
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
        default: {
            if (isIdentifierStart(myChar)) {
                return identifier();
            }
            syntaxError("unrecognized character: '" + myChar + "' code: " +
              (int)myChar);
            return null; //keep compiler happy
        }
        }
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

        Twine source = endToken();
        short tagCode = optKeywordType(source.bare());
        if (-1 == tagCode) {
            return composite(OrcParser.ID, source.bare(), source.getOptSpan());
        }
        //keyword
        return leafTag(tagCode, source.getOptSpan());
    }

    /**
     * If 'name' is a keyword, return it's token tag code, else -1.
     * <p/>
     * We assume here that Orc keywords (like E keywords) are case insensitive,
     * so 'name' is first toLowerCase()d.
     */
    private short optKeywordType(String name) {
        name = name.toLowerCase();
        AstroTag optTag = myBuilder.getSchema().getOptTagForName(name);
        if (null == optTag) {
            return (short)-1;
        }
        return optTag.getOptTagCode();
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
        OrcLexer lex = new OrcLexer(lr, false, false, false);
        while (true) {
            try {
                Astro t;
                do {
                    t = lex.nextToken();
                    stdout.println(t);
                    short code = t.getOptTagCode();
                    if (OrcParser.EOL == code || OrcParser.EOTLU == code) {
                        stdout.print("stack: ",
                                     lex.myIndenter.toString(),
                                     "\n");
                    }
                } while (EOFTOK != t.getOptTagCode());
                return;
            } catch (SyntaxException sex) {
                TextWriter err =
                  new TextWriter(PrintStreamWriter.stderr(), true);
                err.indent("# ").print("# ", sex);
                err.println();
            }
        }
    }
}
