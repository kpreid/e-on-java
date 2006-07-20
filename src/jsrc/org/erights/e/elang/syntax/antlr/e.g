// Copyright 2004-2005 Dean Tribble under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html

// @author Dean Tribble

header {
package org.erights.e.elang.syntax.antlr;
}

// A pattern to produce a possibly empty list of xItem separated and optionally
// followed by commas, we need to use a special construction:
//   xItemList   :   (xItem (","! xItemList)?)? ;
// note that this cannot be an empty list with a comma.

// A caret after a terminal pulls that terminal out and makes it the parent
// node of the tree currently being built. An exclamation after a element
// suppresses generation of a node for that terminal

// Actions in the parse are enclosed in curlies.  Antlr provides some special
// syntax for tree construcion:
// ## refers to the result node of this production.
// Assigning to ## specifies the tree that will be produced.  All following
// terminals will be appended as siblings to the children of that node.
// #name refers to the named AST node

// Two patterns are commonly used to create the tree below:
// 1) add a caret to a terminal that would otherwise be suppressed, then change
//    the resulting root's type:
//           assign : cond ":="^ assign   {##.setType(AssignExpr);} ;
//    elevates the ":=" to be the parent of the "cond" and "assign" nodes, then
//    sets its type to AssignExpr
// 2) create a new node using Antlr's creation syntax:
//           assign : cond ":="! assign   {##=#([AssignExpr],##);} ;
//    suppressed the ":=" node, then assigns the result node to a newly created
//    tree with a newly created AssignExpr node at the root, and all the
//    previous nodes (cond and assign) as children

// '\' followed by only whitespace suppressed the whitespace and the newline
//      ? should this also apply to quasiquote
// all updoc test case are ignored before E lexing.  See the lexer for details.

// TODO:
// anonymous lambda syntax
// generate correct trees
// meta and pragma


class EParser extends Parser;

options {
    k = 2;                           // number of token lookahead
    importVocab=Common;
    exportVocab=E;
    codeGenMakeSwitchThreshold = 2;  // Some optimizations
    codeGenBitsetTestThreshold = 3;
    buildAST = true;
}

tokens {
    AssignExpr;
    CallExpr;
    IntoExpr;
    EscapeExpr;
    HideExpr;
    IfExpr;
    ForExpr;
    WhenExpr;
    AndExpr;
    OrExpr;
    CoerceExpr;
    LiteralExpr;
    MatchBindExpr;
    NounExpr;
    ObjectExpr;
    InterfaceExpr;
    QuasiLiteralExpr;
    QuasiPatternExpr;
    MetaStateExpr;
    MetaContextExpr;
    SeqExpr;
    SlotExpr;
    MetaExpr;
    CatchExpr;
    FinallyExpr;

    ReturnExpr;
    ContinueExpr;
    BreakExpr;
    WhileExpr;
    SwitchExpr;
    TryExpr;
    MapPattern;
    LiteralPattern;
    TupleExpr;
    MapExpr;
    BindPattern;
    SendExpr;
    CurryExpr;

    FinalPattern;
    VarPattern;
    SlotPattern;
    ListPattern;
    CdrPattern;
    IgnorePattern;
    SuchThatPattern;
    QuasiLiteralPattern;
    QuasiPatternPattern;
    URI;
    URIStart;
    URIGetter;
    URIExpr;
    LambdaExpr;

    EScript;
    EMethod;
    EMatcher;
    List;
    WhenFn;

    //for lexer
    HEX;
    OCTAL;
    WS;
    LINESEP;
    SL_COMMENT;
    DOC_COMMENT;
    CHAR_LITERAL;
    STRING;
    ESC;
    HEX_DIGIT;
    IDENT;
    INT;
    POSINT;
    FLOAT64;
    EXPONENT;
}

{
private void warn(String msg) {
    reportWarning(msg);
}

public void reportWarning(String s) {
    try {
        int line = LT(0).getLine();
        int col = LT(0).getColumn();
        if (getFilename() == null) {
            System.err.println("@" + line + "warning: " + s);
        }
        else {
            System.err.println(getFilename() + "@" + line + ": warning: " + s);
        }
    } catch (TokenStreamException t) {
        System.err.println("warning: " + s);
    }
}

// pocket mechanisms: add a boolean, and test in the grammar with {foo}?
private boolean pocketNounString = false;
private boolean pocketDotProps = false;
}

start:
        (pragma (";"! | LINESEP!) | LINESEP!)* (seq)?;

pragma:
        "pragma"^ "."^ message;

metaExpr:
        "meta"^ "."^ message;

br:
        (LINESEP!)*;

seq:
        eExpr (((";"! | LINESEP!) (eExpr)?)+    {##=#([SeqExpr],##);}
              )?;

eExpr:
        assign | ejector;

basic:
        ifExpr | forExpr | whileExpr | switchExpr | tryExpr
 |      escapeExpr | whenExpr | metaExpr | accumExpr;

ifExpr:
        "if"^ parenExpr body ("else"! (ifExpr | body))?
                                                {##.setType(IfExpr);};

forExpr:
        "for"^ forPatt "in"! br assign body (catcher)?
                                                {##.setType(ForExpr);};

// the first pattern is actually the optional one. If it is missing, include an
// empty ignore pattern for it.
forPatt:
        pattern (  "=>"! pattern                {##=#([ListPattern,"=>"],##);}
                 |                              {##=#([ListPattern,"=>"],
                                                      [IgnorePattern, ""],##);}
                );

accumExpr:
        "accum"^ call accumulator;

accumulator:
        "for"^ forPatt "in"! logical accumBody
 |      "if"^ parenExpr            accumBody
 |      "while"^ parenExpr         accumBody;

accumBody:
        "{"! (  "_" (  ("+"^ | "*"^ | "&"^ | "|"^) assign
                     | "."^ verb parenArgs)
              | accumulator
             ) br "}"!;


whenExpr:
        "when"^ parenArgs "->"!  whenFn (catcher)* ("finally" body)?
                                                {##.setType(WhenExpr);};

whenFn:
        objName params (":"! guard)? body       {##=#([WhenFn],##);};

whileExpr:
        "while"^ parenExpr body (catcher)?      {##.setType(WhileExpr);};

escapeExpr:
        "escape"^ pattern body (catcher)?       {##.setType(EscapeExpr);};

lambdaExpr:
        "thunk"^ body                           {##.setType(LambdaExpr);}
 |      "fn"^ paramList body                    {##.setType(LambdaExpr);};

switchExpr:
        "switch"^ parenExpr "{"! (matcher br)* "}"!
                                                {##.setType(SwitchExpr);};

tryExpr:
        "try"^ body (catcher)* ("finally"! body)?
                                                {##.setType(TryExpr);};

bindNamer:
        "bind"^ noun (":"! guard)?              {##.setType(BindPattern);};

varNamer:
        "var"^ nounExpr (":"! guard)?           {##.setType(VarPattern);};

slotNamer:
        "&"^ nounExpr (":"! guard)?             {##.setType(SlotPattern);};

// should forward declaration allow types?
docoDef:
        doco (defExpr | interfaceExpr | lambdaExpr);

defExpr:
        "def"^ (  (objectPredict)  => objName objectExpr
                                                {##.setType(ObjectExpr);}
                | (pattern ":=") => pattern ":="! rValue
                                                {##.setType(IntoExpr);}
                | bindName //forward declaration
                                                {##.setType(IntoExpr);}
               )
 |      "bind"! bindExpr
 |      "var"! bindExpr;

rValue:
        ("(" eExpr br ",") => "(" eExpr br "," eExpr br ")"
                          // Can we support trinary-define with less lookahead?
 |      assign;

bindExpr:
        bindName //XXX Is bindName what I want here?
                 (  ":="! assign                {##.setType(IntoExpr);}
                  | objectExpr                  {##.setType(ObjectExpr);}
                 );

// defPatt:
//      (                                       {##.setType(FinalPattern);}
//       | "var"                                {##.setType(VarPattern);}
//       | "bind"                               {##.setType(BindPattern);}
//      ) (nounExpr | "_" | STRING);

// minimize the look-ahead for objectExpr
objectPredict:
        objName ("extends" | "implements" | "{"| "(" );

objectExpr:
        //(":"! guard)?
        (  ("extends" br order)?
           ("implements" br order ("," order)*)?
                             // trailing comma would be ambiguous with HideExpr
           script
         | params resultGuard body      // function
        );

// The name pattern, or literal name, for an object definition
bindName:
        (  nounExpr                             {##=#([FinalPattern],##);}
         | "&"^ nounExpr                        {##.setType(SlotPattern);}
         | STRING                               {##=#([LiteralPattern],##);}
        ) (":"! guard)?;

objName:
        nounExpr                                {##=#([FinalPattern],##);}
 |      "_"^                                    {##.setType(IgnorePattern);}
 |      "bind"^ noun                            {##.setType(BindPattern);}
 |      "var"^ nounExpr                         {##.setType(VarPattern);}
 |      "&"^ nounExpr                           {##.setType(SlotPattern);}
 |      STRING                                  {##=#([LiteralPattern],##);};

script:
        "{"^ (method br)* (matcher br)* "}"!    {##.setType(EScript);};

method:
        doco (  "to"^     methHead body
              | "method"^ methHead body
              | "on"^     methHead body
             )                                  {##.setType(EMethod);};

methHead:
             params resultGuard
 |      verb params resultGuard;

matcher:
        "match"^ pattern body                   {##.setType(EMatcher);};

params:
        "("! paramList br ")"!                  {##=#([List],##);};

paramList:
        ((key)? "=>") => mapPattList            {warn("map-tail");}
 |      (pattern (","! paramList)?)?;

patternList:
        (pattern (","! patternList)?)?;

resultGuard:
        (":"! guard)? ("throws" guardList)?;

guardList:
        guard (","! guard)*;             // requires at least one guard. cannot
                                         // end with comma

interfaceExpr:
        "interface"^ objName //(":"! guard)?
        (  ("guards" br pattern)?
           ("extends" br order ("," order)*)?
                             // trailing comma would be ambiguous with HideExpr
           ("implements" br order ("," order)*)?
                             // trailing comma would be ambiguous with HideExpr
           "{"^ (imethod br)* "}"!
         | mtypes (":"! guard)?   // function
        )                                       {##.setType(InterfaceExpr);};

imethod:
        doco (  "to"^ imethHead //(body)?
              | "method"^ imethHead //(body)?
              | "on"^ imethHead //(body)?
             )                                  {##.setType(EMethod);};

ptype:
        nounExpr (":"! guard)?                  {##=#([FinalPattern],##);}
 |      "_"^ (":"! guard)?                      {##.setType(IgnorePattern);};

typeList:
        (ptype (","! typeList)?)?;
mtypes:
        "("! typeList br ")"!                   {##=#([List],##);};

imethHead:
             mtypes resultGuard
 |      verb mtypes resultGuard;

// The current E grammar only let's you put these in a few places.
doco:
        DOC_COMMENT | {##=#([DOC_COMMENT]);};

body:
        "{"! (seq)? "}"! ;

// rules for expressions follow the pattern:
//   thisLevelExpression :  nextHigherPrecedenceExpression
//   (OPERATOR nextHigherPrecedenceExpression)*
// which is a standard recursive definition for a parsing an expression.
// The legal assignment syntax are:
//  x := ...
//  x op= ...       converts to x."op"(...)
//  x verb= ...     converts to x.verb(...)
//  x[i...] := ...  converts to x.put(i..., ...)
//  x::name := ...  converts to x.setName(...)
//  x(i...) := ...  converts to x.setRun(i..., ...)
// Based on the above patterns, only nounExpr, nounExpr
assign:
        cond (  ":="^ assign                    {##.setType(AssignExpr);}
              | assignOp assign                 {##=#([AssignExpr], ##);}
              | verb "="! (("(")=> parenArgs | assign)
                           // deal with deprecated single case
                                                {##=#([AssignExpr], ##);}
             )?
 |      docoDef;

assignOp:
        "//=" | "+=" | "-=" | "*=" | "/="
 |      "%=" | "%%=" | "**=" | ">>=" | "<<=" | "&="
 |      "^=" | "|=";

ejector:
        (  "break"^                             {##.setType(BreakExpr);}
         | "continue"^                          {##.setType(ContinueExpr);}
         | "return"^                            {##.setType(ReturnExpr);}
        ) (("(" ")") => "(" ")" | assign | )
 |      "^"^ assign                             {##.setType(ReturnExpr);};

// || is don't-care associative
cond:
        condAnd ("||"^ condAnd                  {##.setType(OrExpr);}
                )*;

// && is don't-care associative
condAnd:
        logical ("&&"^ logical                  {##.setType(AndExpr);}
                )*;

// ==, !=, &, |, ^, =~, and !~ are all non associative with each
// other.  & and |, normally used for associative operations, are each
// made associative with themselves. None of the others even associate
// with themselves. Perhaps ^ should?
logical:
        order (  (  "==" order
                  |   "!=" order
                  |   "&!" order
                  |   "=~" pattern
                  |   "!~" pattern
                 )                              {##=#([CallExpr],##);}
               | ({##=#([CallExpr],##);} "^" order)+
               | ({##=#([CallExpr],##);} "&" order)+
               | ({##=#([CallExpr],##);} "|" order)+
              )?;   //optional

order:
        interval (  compareOp interval          {##=#([CallExpr], ##);}
                  | ":"^ guard                  {##.setType(CoerceExpr);}
                  |  // empty
                 );

// The br for ">" is because it is used to close URIs, where it cannot have a
// br.
compareOp:
        "<" | "<=" | "<=>" | ">=" | ">" br;

// .. and ..! are both non-associative (no plural form)
interval:
        shift ({##=#([CallExpr],##);} (".." | "..!") shift)?;

// << and >> are left-associative (no plural form)
shift:
        add ({##=#([CallExpr],##);} ("<<" | ">>") add)*;

//+ and - are left associative
add:
        mult ({##=#([CallExpr],##);} ("+" | "-") mult)*;

// *, /, //, %, and %% are left associative
mult:
        pow ({##=#([CallExpr],##);} ("*" | "/" | "//" | "%" | "%%") pow)*;

// ** is non-associative
pow:
        prefix ("**" prefix                     {##=#([CallExpr], ##);}
               )?;

// Unary prefix !, ~, &, *, and - are non-associative.
// Unary prefix !, ~, &, and * bind less tightly than unary postfix.
// Unary prefix -, because it will often be mistaken for part of a literal
// number rather than an operator, is not combinable with unary postfix, in
// order to avoid the following surprise:
//      -3.pow(2) ==> -9
// If -3 were a literal, the answer would be 9. So, in E, you must say either
//      (-3).pow(2)  or -(3.pow(2))
// to disambiguate which you mean.
prefix:
        postfix
 |!     op:prefixOp  a:postfix                  {##=#([CallExpr],a,op);}
 |!     neg:"-" b:prim                          {##=#([CallExpr],b,neg);};

prefixOp:
        ("!" | "~" | "&" | "*" | "+");

// Calls and sends are left associative.
postfix:
        call;
            // TODO deal with properties

call:
        p:prim (!  a:parenArgs                  { ##=#([CallExpr,"run"],p,
                                                       [STRING,"run"],a); }
                |  "."^ message                 { ##.setType(CallExpr); }
                |! "["^ l:argList "]"!          { ##=#([CallExpr,"get"],p,
                                                       [STRING,"get"],l); }
                |  "<-"^ (  parenArgs           { warn("use '<- run(...')"); }
                          | message
                          | "::" ("&")? prop
                         )                      { ##.setType(SendExpr); }
                |   "::" ("&")? prop
               )*;

//message:
//      v:verb (  ("(") => parenArgs
//              | /*curry*/                     {#v.setType(CurryExpr);}
//             );


message:
        verb (  ("(") => parenArgs
              | /*curry*/                       {##.setType(CurryExpr);}
             );

parenArgs:
        "("! argList ")"!;

lambdaArgs:
        "("! argList ")"! (sepword! body)?; //(body)? | body;

exprList:
        (eExpr br (","! exprList)?)?;

argList:
        ((eExpr)? "=>") => mapList              {warn("map-tail");}
 |      (eExpr br (","! argList)?)?;

prim:
        literal
 |      basic
 |      nounExpr (quasiString                   {##=#([QuasiLiteralExpr],##);}
                 )?
 |      parenExpr (quasiString                  {##=#([QuasiLiteralExpr],##);
                                          warn("***We may deprecate this***");}
                  )?
 |      quasiString                             {##=#([QuasiLiteralExpr,
                                                       "simple"],
                                                      [STRING,"simple"],##);}
 |      URI                                     {##=#([URIExpr],##);}
 |      "["^ (  ((eExpr)? "=>") => mapList      {##.setType(MapExpr);}
              | exprList                        {##.setType(TupleExpr);}
             ) "]"!
 |      body                                    {##=#([HideExpr],##);};

mapList:
        (map br (","! mapList)?)?;

map:
        eExpr "=>"^ eExpr
 |      "=>"^ (  nounExpr
               | "&"nounExpr
               | "def" nounExpr
              );

//Property names for use e.g., with the :: syntax.
// Should the "&" handling be here?
prop:
        IDENT | STRING;

// a method selector
verb:
        IDENT | STRING;

literal:
        STRING | CHAR_LITERAL | INT | FLOAT64 | HEX | OCTAL;

noun:
        IDENT
 |      URIGetter                               {##=#([URIExpr],##);}
 |      "::"^ {pocketNounString}? (STRING | IDENT);

// A guard is a nounExpr, a URI, a parenExpr, or a guard followed by [argList].
guard:
        (nounExpr | URI | parenExpr) ("[" argList "]"!)*;

catcher:
        "catch"^ pattern body;

// Patterns
pattern:
        subPattern ("?"^ parenExpr              {##.setType(SuchThatPattern);
                                                 warn("such-that deprecated");}
                   )?;

subPattern:
        nounExpr (  (":"! guard)?               {##=#([FinalPattern],##);}
                  | quasiString         {##=#([QuasiLiteralPattern],##);}
                                                       // was IDENT quasiString
                 )
 |      "_"^ (":"! guard)?                      {##=#([IgnorePattern],##);}
 |      ":" guard
 |      bindNamer
 |      varNamer
 |      slotNamer
 |      quasiString                     {##=#([QuasiLiteralPattern,"simple"],
                                              [STRING,"simple"],##);}
 |      "=="^ prim
    |      "!="^ prim                   {warn("reserved: != pattern");}
 |      compareOp prim                  {warn("reserved: compareOp pattern");}
 |      ("[" (key)? "=>") => "["^ mapPattList br "]"! ("|" subPattern)?
                                                {##.setType(MapPattern);}
 |      "["^ patternList br "]"! (  (":"! guard)?
                                                {##.setType(ListPattern);}
                                  | ("+"! subPattern)
                                                {##.setType(CdrPattern);}
                                 );

// namePatts are patterns that bind exactly one name.
// this is expanded inline into eqPatt, but used directly elsewhere
namePatt:
        nounExpr (":"! guard)?                  {##=#([FinalPattern],##);}
 |      bindNamer
 |      varNamer
 |      slotNamer;

nounExpr:
        noun
 |      dollarHole                      {##.setType(QuasiLiteralPattern);}
 |      atHole                          {##.setType(QuasiPatternPattern);};

dollarHole:
        "${" POSINT "}"
 |      "$" POSINT
 |      "$$";

atHole:
        "@{"^ POSINT "}"!
 |      "@" POSINT;

key:
        parenExpr | literal;

parenExpr:
        "("! seq ")"!;
//args:
//      "("! br seq ")"!  ;

mapPattList:
        (mapPattern (","! mapPattList)?)?;

mapPattern:
        key "=>"^ pattern  (":=" order)?
 |          "=>"^ namePatt (":=" order)?;

// QUASI support
quasiString:
        QUASIOPEN! (  exprHole
                    | pattHole
                    | QUASIBODY
                    | DOLLARHOLE
                    | ATHOLE
                   )*                           {##=#([QuasiContent],##);}
        QUASICLOSE!;  // NOTE: '`' is the QUASICLOSE token in the quasi lexer

exprHole:
        DOLLARCURLY^ br eExpr br                {##.setType(DOLLARHOLE);}
        "}"!;

pattHole:
        ATCURLY! br pattern br                  {##.setType(ATHOLE);}
        "}"!;

sepword:
        "catch" | "else" | "escape" | "finally" | "guards"
 |      "thunk" | "fn" | "try" | IDENT | reserved
 |      "->";

reserved:
 |      "abstract" | "an" | "as" | "assert" | "attribute"
 |      "be" | "begin" | "behalf" | "belief" | "believe" | "believes"
 |      "case" | "class" | "const" | "constructor"
 |      "datatype" | "declare" | "default" | "define" | "defmacro"
 |      "delicate" | "deprecated" | "dispatch" | "do"
 |      "encapsulate" | "encapsulated" | "encapsulates"
 |      "end" | "ensure" | "enum" | "eventual" | "eventually"
 |      "export" | "facet" | "forall" | "fun" | "function" | "given"
 |      "hidden" | "hides" | "inline"
 |      "know" | "knows" | "lambda" | "let" | "methods"
 |      "namespace" | "native"
 |      "obeys" | "octet" | "oneway" | "operator"
 |      "package" | "private" | "protected" | "public"
 |      "raises" | "reliance" | "reliant" | "relies" | "rely" | "reveal"
 |      "sake" | "signed" | "static" | "struct"
 |      "suchthat" | "supports" | "suspect" | "suspects" | "synchronized"
 |      "this" | "transient" | "truncatable" | "typedef"
 |      "unsigned" | "unum" | "uses" | "using" | "utf8" | "utf16"
 |      "virtual" | "volatile" | "wstring";

/*/**/
