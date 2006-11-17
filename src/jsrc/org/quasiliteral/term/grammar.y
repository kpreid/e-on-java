// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html

/**
 * This represents approximately a subset of term.y, meant for the
 * subset of term.y's meaning that applies to sequences of symbols
 * rather than trees of symbols.
 * <p>
 * The two ways in which the grammar defined here is not a subset of
 * term.y is that grammar.y uses juxtaposition where term.y uses ',', and
 * juxtaposition in grammar.y bind much more tightly than ',' in term.y. This
 * allows the use of a more conventional bnf-like notation.
 */

%{
package org.quasiliteral.term;

import org.erights.e.develop.exception.NestedException;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.Twine;
import org.erights.e.meta.java.lang.CharacterMakerSugar;
import org.quasiliteral.astro.AstroSchema;
import org.quasiliteral.astro.BaseSchema;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroArg;
import org.quasiliteral.astro.AstroTag;
import org.quasiliteral.syntax.SyntaxException;
import org.quasiliteral.syntax.LineFeeder;
import org.quasiliteral.syntax.TwineFeeder;
import org.quasiliteral.syntax.LexerFace;

import java.io.IOException;
%}

%token Tag          //See http://www.erights.org/data/terml/terml-spec.html#Tag

%token LiteralChar      //like Java & E: unicode
%token LiteralInteger   //like Java & E: precision unlimited
%token LiteralFloat64   //like Java & E: IEEE double precision
%token LiteralString    //like Java & E: double quoted
%token LiteralChars     //for singly quoting more than one char

%token EOL              //eaten as whitespace at a higher level
%token EOTLU            //See EOTLU in org/erights/e/elang/syntax/e.y

%token OpDef            // ::=
%token OpAction         // ->
%token OpThru           // ..
%token OpDoubleStar     // **
%token OpDoublePlus     // ++

%%

start:
        rhs                     { myOptResult = b.start((AstroArg)$1); }
 |      schema                  { myOptResult = b.start((Astro)$1); }
 ;

schema:
        productions             { $$ = b.schema((AstroArg)$1); }
 ;

productions:
        production
 |      productions production  { $$ = b.seq((AstroArg)$1, (Astro)$2); }
 ;

production:
        id OpDef rhs ';'        { $$ = b.production((Astro)$1, (AstroArg)$3); }
 ;

term:
        functor                 { $$ = b.term((Astro)$1); }
 ;

functor:
        literal
 |      id
 |      '.' functorHole         { $$ = $2; }
 |      id functorHole          { $$ = b.taggedHole((Astro)$1, (Astro)$2); }
 ;

rhs:
        interleave
 |      rhs '|' interleave      { $$ = b.onlyChoice((AstroArg)$1,
                                                    (AstroArg)$3); }
 |      rhs '/' interleave      { $$ = b.firstChoice((AstroArg)$1,
                                                     (AstroArg)$3); }
 ;

interleave:
        action
 |      interleave '&' action   { $$ = b.interleave((AstroArg)$1,
                                                    (AstroArg)$3); }
 ;

action:
        argList
 |      argList OpAction argList { $$ = b.action((AstroArg)$1, (AstroArg)$3); }
 ;

argList:
        /* empty */             { $$ = b.empty(); }
 |      args
 ;

args:
        pred
 |      args pred               { $$ = b.seq((AstroArg)$1, (AstroArg)$2); }
 ;

pred:
        some
 |      '!' pred                 { $$ = b.not((AstroArg)$2); }
 ;

/**
 * Each some represents some number of Terms
 */
some:
        prim
 |      some quant              { $$ = b.some((AstroArg)$1,
                                              ((Character)$2).charValue()); }
 |      prim OpDoubleStar prim  { $$ = b.some((AstroArg)$1,
                                              '*',
                                              (AstroArg)$3); }
 |      prim OpDoublePlus prim  { $$ = b.some((AstroArg)$1,
                                              '+',
                                              (AstroArg)$3); }
 ;

quant:
        '?'                     { $$ = CharacterMakerSugar.valueOf('?'); }
 |      '+'                     { $$ = CharacterMakerSugar.valueOf('+'); }
 |      '*'                     { $$ = CharacterMakerSugar.valueOf('*'); }
 ;

prim:
        attr                    // An Astro is already a fine AstroArg
 |      '.'                     { $$ = b.any(); }
 |      literal OpThru literal  { $$ = b.range((Astro)$1, (Astro)$3); }
 |      LiteralChars            { $$ = b.unpack((Astro)$1); }
 |      '^' LiteralString       { $$ = b.anyOf((Astro)$2); }
 |      '(' rhs ')'             { $$ = $2; }
 ;

attr:
        term
 |      term ':' term           { $$ = b.attr((Astro)$1, (Astro)$3); }
 ;

literal:
        LiteralChar
 |      LiteralInteger
 |      LiteralFloat64
 |      LiteralString
 ;

id:
        Tag                     { $$ = untag((Astro)$1); }
 ;

/**
 * Starts off as a hole for a Functor, but may get promoted.
 */
functorHole:
        '$' '{' LiteralInteger '}'      { $$ = b.dollarHole((Astro)$3); }
 |      '$' LiteralInteger              { $$ = b.dollarHole((Astro)$2); }
 |      '$' id                          { $$ = b.dollarHole((Astro)$2); }
 |      '@' '{' LiteralInteger '}'      { $$ = b.atHole(    (Astro)$3); }
 |      '@' LiteralInteger              { $$ = b.atHole(    (Astro)$2); }
 |      '@' id                          { $$ = b.atHole(    (Astro)$2); }
 |      '=' '{' LiteralInteger '}'      { $$ = b.atHole(    (Astro)$3); }
 |      '=' LiteralInteger              { $$ = b.atHole(    (Astro)$2); }
 |      '=' id                          { $$ = b.atHole(    (Astro)$2); }
 ;


%%

/**
 * contains all the tokens after yylval
 */
private final LexerFace myLexer;

/**
 *
 */
private final QuasiBuilder b;

/**
 *
 */
private AstroArg myOptResult;

/**
 *
 */
public GrammarParser(LexerFace lexer, QuasiBuilder builder) {
    myLexer = lexer;
    b = builder;
    myOptResult = null;
}

/**
 * builder defaults to the quasi-adapted, non-quasi builder for
 * building Term trees.
 */
static public Term run(Twine source) {
    return (Term)run(source, QuasiBuilderAdaptor.FOR_TERMS);
}

/**
 *
 */
static public AstroArg run(Twine source, QuasiBuilder builder) {
    try {
        LineFeeder lineFeeder = new TwineFeeder(source);
        LexerFace lexer = new TermLexer(lineFeeder,
                                        false,
                                        builder.doesQuasis(),
                                        false,
                                        builder);
        GrammarParser parser = new GrammarParser(lexer, builder);
        return parser.parse();
    } catch (IOException iox) {
        throw new NestedException(iox, "# parsing a string?!");
    }
}

/**
 *
 */
public AstroArg parse() {
    if (yyparse() != 0) {
        yyerror("couldn't parse term");
    }
    return myOptResult;
}

/**
 * Skips EOLs and EOTLUs.
 * <p>
 * Note that yacc uses tag-codes, while Antlr uses type-codes.
 */
private short yylex() {
    while (true) {
        if (myLexer.isEndOfFile()) {
            yylval = null;
            return LexerFace.EOFTOK;
        }
        Astro token = null;
        try {
            token = myLexer.nextToken();
        } catch (IOException ex) {
            yyerror("io: " + ex);
        }
        short code = token.getOptTagCode();
        if (EOL != code && EOTLU != code) {
            yylval = token;
            return code;
        }
    }
}

/**
 *
 */
private void reserved(String s) throws SyntaxException {
    yyerror("reserved: " + s);
}

/**
 *
 */
private void yyerror(String s) throws SyntaxException {
    if ("syntax error".equals(s)) {
        if (null == yylval) {
            myLexer.needMore("Unexpected EOF");
            return; //give the compiler better info
        }
        short tagCode = ((Astro)yylval).getOptTagCode();
        if (LexerFace.EOFTOK == tagCode) {
            myLexer.needMore("Unexpected EOF");
            return; //give the compiler better info
        }
    }
    myLexer.syntaxError(s);
}

/**
 * Transform '.Tag.("foo")' into 'foo'.
 * <p>
 * Otherwise, is an identity function. This is needed for the
 * GrammarParser specifically, since identifiers in the input must all be
 * of token tag-name .Tag., while each of these represents a unique
 * keyword, and therefore token-tag, in the grammar being described.
 */
private Astro untag(Astro token) {
    if (token.getTag().getTagName() == ".Tag.") {
        //the above test is valid, since tagNames are guaranteed to be
        //interned.
        String name = ((Twine)token.getOptArgData()).bare();
        AstroTag nameTag = b.getSchema().obtainTagForName(name);
        return b.leafTag(nameTag, token.getOptSpan());
    } else {
        return token;
    }
}


/*********************************/

/**
 *
 */
static private final String[] TheTokens = new String[yyname.length];

static {
    System.arraycopy(yyname, 0, TheTokens, 0, yyname.length);

    /* Since this language has no keywords, it's not a conflict
     * for the names to be identifiers
     */

    TheTokens[LexerFace.EOFTOK] = "EndOfFile";

    TheTokens[Tag]              = ".Tag.";

    TheTokens[LiteralInteger]   = ".int.";
    TheTokens[LiteralFloat64]   = ".float64.";
    TheTokens[LiteralChar]      = ".char.";
    TheTokens[LiteralString]    = ".String.";
    TheTokens[LiteralChars]     = "LiteralChars";

    /* Eaten as whitespace at a higher level */
    TheTokens[EOL]              = "EOL";
    TheTokens[EOTLU]            = "EOTLU";

    /* multi-character operators */
    TheTokens[OpDef]            = "OpDef";        // ::=
    TheTokens[OpAction]         = "OpAction";     // ->
    TheTokens[OpThru]           = "OpThru";       // ..
    TheTokens[OpDoubleStar]     = "OpDoubleStar"; // **
    TheTokens[OpDoublePlus]     = "OpDoublePlus"; // ++

    /* Single-Character Tokens */
    TheTokens['&']              = "Ampersand";
    TheTokens['|']              = "VerticalBar";
    TheTokens['^']              = "Caret";
    TheTokens['+']              = "Plus";
    TheTokens['*']              = "Star";
    TheTokens[',']              = "Comma";
    TheTokens[';']              = "Semicolon";
    TheTokens['!']              = "Bang";

    TheTokens['?']              = "Question";
    TheTokens['/']              = "Slash";
    TheTokens[':']              = "Colon";
    TheTokens['.']              = "Dot";
    TheTokens['$']              = "Dollar";
    TheTokens['@']              = "At";
    TheTokens['=']              = "Equals";

    TheTokens['[']              = "OpenBracket";
    TheTokens[']']              = "CloseBracket";
    TheTokens['(']              = "OpenParen";
    TheTokens[')']              = "CloseParen";
    TheTokens['{']              = "OpenBrace";
    TheTokens['}']              = "CloseBrace";
}

/**
 * Builds ASTs according to the term.y grammar
 */
static public final AstroSchema DEFAULT_SCHEMA =
  new BaseSchema("Term-Tree-Language", ConstList.fromArray(TheTokens));
