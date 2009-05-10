// Copyright 2006 Hewlett-Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

%{
package com.hp.orc;

import org.erights.e.develop.exception.NestedException;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.Twine;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroArg;
import org.quasiliteral.astro.AstroSchema;
import org.quasiliteral.astro.BaseSchema;
import org.quasiliteral.syntax.LexerFace;
import org.quasiliteral.syntax.LineFeeder;
import org.quasiliteral.syntax.SyntaxException;
import org.quasiliteral.syntax.TwineFeeder;
import org.quasiliteral.term.QuasiBuilder;
import org.quasiliteral.term.Term;
import org.quasiliteral.term.QuasiBuilderAdaptor;

import java.io.IOException;
%}

%token EOL              //eaten as whitespace at a higher level
%token EOTLU            //See EOTLU in org/erights/e/elang/syntax/e.y

%token LiteralInteger   /* precision unlimited */
%token LiteralString    /* Double quoted */
%token ID
%token LiteralFloat64   /* Unused by Orc */
%token LiteralChar      /* Unused by Orc */


/* keywords */
%token DEF


%%

start:
        exprs                       { myOptResult = b.namedTerm("expr",
                                                                (AstroArg)$1); }
 ;

exprs:
        empty
 |      exprs expr                  { $$ = b.seq((AstroArg)$1, (AstroArg)$2); }
 ;

expr:
        defs prune                  { $$ = b.namedTerm("expr",
                                                       b.seq((AstroArg)$1,
                                                             (AstroArg)$2)); }
 ;

defs:
        empty
 |      defs def                    { $$ = b.seq((AstroArg)$1, (AstroArg)$2); }
 ;

def:
        DEF varID optFormals '=' expr   
                                    { AstroArg params = b.tuple((AstroArg)$3);
                                      $$ = b.namedTerm("def",
                                                       b.seq((AstroArg)$2,
                                                             params,
                                                             (AstroArg)$5)); }
 ;

optFormals:
        empty
 |      '(' ')'                     // not allowed by official orc 0.5, but ok
                                    { $$ = b.empty(); }
 |      '(' idList ')'              { $$ = $2; }
 ;

empty:
        /*empty*/                   { $$ = b.empty(); }
 ;

idList:
        varID
 |      idList ',' varID            { $$ = b.seq((AstroArg)$1, (AstroArg)$3); }
 ;

prune:
        par
 |      par '<' optVar '<' prune    { $$ = b.namedTerm("prune",
                                                       b.seq((AstroArg)$1, 
                                                             (AstroArg)$3,
							     (AstroArg)$5)); }
 ;

par:
        seq
 |      par '|' seq                 { $$ = b.namedTerm("par",
                                                       b.seq((AstroArg)$1, 
                                                             (AstroArg)$3)); }
 ;

seq:
        basic
 |      seq '>' optVar '>' basic    { $$ = b.namedTerm("pipe",
                                                       b.seq((AstroArg)$1, 
                                                             (AstroArg)$3,
                                                             (AstroArg)$5)); }
 ;

optVar:
        empty
 |      varID
 ;

basic:
        useID
 |      LiteralInteger
 |      LiteralString
 |      call
 |      block
 |      hole
 ;

call:
        useID '(' args ')'          { AstroArg args = b.tuple((AstroArg)$3);
                                      $$ = b.namedTerm("call",
                                                       b.seq((AstroArg)$1,
                                                             args)); }
 ;

args:
        empty           // not allowed by official orc 0.5, but seems necessary
 |      argList
 ;

argList:
        expr
 |      argList ',' expr            { $$ = b.seq((AstroArg)$1, (AstroArg)$3); }
 ;

block:
        '{' expr '}'                { $$ = $2; }
 ;

varID:
        id                          { $$ = b.namedTerm("var", (AstroArg)$1); }
 ;

useID:
        id                          { $$ = b.namedTerm("use", (AstroArg)$1); }
 ;

id:
        ID                      { String id = ((Astro)$1).getOptArgString(ID);
                                  $$ = b.leafString(id, null); }
 ;

hole:
        '$' '{' LiteralInteger '}'  //{ $$ = b.dollarHole((Astro)$3); }
                                    { $$ = b.namedTerm(".DollarHole.",
                                                       (Astro)$3); }
 |      '@' '{' LiteralInteger '}'  //{ $$ = b.atHole(    (Astro)$3); }
                                    { $$ = b.namedTerm(".AtHole.",
                                                       (Astro)$3); }
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
public OrcParser(LexerFace lexer, QuasiBuilder builder) {
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
        LexerFace lexer = new OrcLexer(lineFeeder,
                                       false,
                                       builder.doesQuasis(),
                                       false);
        OrcParser parser = new OrcParser(lexer, builder);
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


/*********************************/

/**
 *
 */
static private final String[] TheTokens = new String[yyname.length];

static {
    System.arraycopy(yyname, 0, TheTokens, 0, yyname.length);

    TheTokens[LexerFace.EOFTOK] = "EndOfFile";

    /* Eaten as whitespace at a higher level */
    TheTokens[EOL]              = "EOL";
    TheTokens[EOTLU]            = "EOTLU";

    TheTokens[LiteralInteger]   = ".int.";
    TheTokens[LiteralString]    = ".String.";
    TheTokens[ID]               = "ID";

    /* Unused by Orc */
    TheTokens[LiteralFloat64]   = ".float64.";
    TheTokens[LiteralChar]      = ".char.";

    /* Keywords */
    TheTokens[DEF]              = "def";

    /* Single-Character Tokens */
    TheTokens['(']              = "OpenParen";
    TheTokens[')']              = "CloseParen";
    TheTokens['=']              = "Equals";
    TheTokens[',']              = "Comma";
    TheTokens[';']              = "Semicolon";
    TheTokens['>']              = "CloseAngle";
    TheTokens['<']              = "OpenAngle";
    TheTokens['|']              = "VerticalBar";
    TheTokens['{']              = "OpenBrace";
    TheTokens['}']              = "CloseBrace";

    TheTokens['$']              = "Dollar";
    TheTokens['@']              = "At";
}

/**
 * Builds ASTs according to the orc.y grammar
 */
static public final AstroSchema DEFAULT_SCHEMA =
  new BaseSchema("Orc-Language", ConstList.fromArray(TheTokens));
