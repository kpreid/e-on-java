//### This file created by BYACC 1.8(/Java extension  0.92)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//### Please send bug reports to rjamison@lincom-asg.com
//### static char yysccsid[] = "@(#)yaccpar       1.8 (Berkeley) 01/20/90";



//#line 15 "grammar.y"

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
//#line 29 "GrammarParser.java"




//#####################################################################
// class: GrammarParser
// does : encapsulates yacc() parser functionality in a Java
//        class for quick code development
//#####################################################################
public class GrammarParser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.err.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[],stateptr;             //state stack
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
void state_push(int state)
{
  if (stateptr>=YYSTACKSIZE)         //overflowed?
    return;
  statestk[++stateptr]=state;
}
int state_pop()
{
  if (stateptr<0)                    //underflowed?
    return -1;
  return statestk[stateptr--];
}
void state_drop(int cnt)
{
int ptr;
  ptr=stateptr-cnt;
  if (ptr<0)
    return;
  stateptr = ptr;
}
int state_peek(int relative)
{
int ptr;
  ptr=stateptr-relative;
  if (ptr<0)
    return -1;
  return statestk[ptr];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
boolean init_stacks()
{
  statestk = new int[YYSTACKSIZE];
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.err.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.err.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.err.println("======================");
}


//########## SEMANTIC VALUES ##########
//## **user defined:Object
String   yytext;//user variable to return contextual strings
Object yyval; //used to return semantic vals from action routines
Object yylval;//the 'lval' (result) I got from yylex()
Object valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new Object[YYSTACKSIZE];
  yyval=new Object();
  yylval=new Object();
  valptr=-1;
}
void val_push(Object val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
Object val_pop()
{
  if (valptr<0)
    return null;
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
Object val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return null;
  return valstk[ptr];
}
//#### end semantic value section ####
public final static short Tag=257;
public final static short LiteralChar=258;
public final static short LiteralInteger=259;
public final static short LiteralFloat64=260;
public final static short LiteralString=261;
public final static short LiteralChars=262;
public final static short EOL=263;
public final static short EOTLU=264;
public final static short OpDef=265;
public final static short OpAction=266;
public final static short OpThru=267;
public final static short OpDoubleStar=268;
public final static short OpDoublePlus=269;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    2,    3,    3,    4,    6,    6,    7,    7,
    7,    7,    1,    1,    1,   11,   11,   12,   12,   13,
   13,   14,   14,   15,   15,   16,   16,   16,   16,   17,
   17,   17,    8,    8,    8,    8,    8,    8,    9,    9,
    9,    9,    5,   10,   10,   10,   10,   10,   10,   10,
   10,   10,
};
final static short yylen[] = {                            2,
    1,    1,    1,    1,    2,    4,    1,    3,    1,    1,
    2,    2,    1,    3,    3,    1,    3,    1,    3,    0,
    1,    1,    2,    1,    2,    1,    2,    3,    3,    1,
    1,    1,    1,    1,    3,    1,    2,    3,    1,    1,
    1,    1,    1,    4,    2,    2,    4,    2,    2,    4,
    2,    2,
};
final static short yydefred[] = {                         0,
   43,   39,   40,   41,   42,   36,    0,    0,    0,    0,
    0,    0,    2,    0,    4,    0,   33,    0,    0,    0,
    0,   16,    0,    0,   22,    0,    0,    0,    0,   11,
    0,   25,   37,    0,    0,    0,    5,    0,    0,   12,
    0,    0,    0,    0,    0,    0,   23,   30,   31,   32,
   27,   45,    0,   46,   48,    0,   49,   51,    0,   52,
   38,    0,    0,    0,    8,   28,   29,   35,   17,   19,
    0,    0,    0,    6,   44,   47,   50,
};
final static short yydgoto[] = {                         11,
   12,   13,   14,   15,   31,   17,   18,   19,   20,   40,
   21,   22,   23,   24,   25,   26,   51,
};
final static short yysindex[] = {                       -25,
    0,    0,    0,    0,    0,    0,  -32,  -25, -250,  -25,
    0,  -33,    0, -239,    0,  -10,    0,  -23, -213, -247,
   -8,    0, -229,  -25,    0,   10, -117,  -87,  -73,    0,
  -32,    0,    0,  -34,  -25,  -25,    0, -219,  -25,    0,
  -13,  -13,  -13, -149,  -25,  -25,    0,    0,    0,    0,
    0,    0, -202,    0,    0, -197,    0,    0, -189,    0,
    0,   -8,   -8,  -42,    0,    0,    0,    0,    0,    0,
  -48,  -47,  -45,    0,    0,    0,    0,
};
final static short yyrindex[] = {                         2,
    0,    0,    0,    0,    0,    0,   61,    0,    0,  -38,
    0,   92,    0,   93,    0,  118,    0,   89,  157,    1,
   87,    0,  265,   38,    0,  133,    0,    0,    0,    0,
   25,    0,    0,    0,  100,  100,    0,    0,  -28,    0,
    0,    0,    0,    0,  100,  297,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  230,  326,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    6,    0,    0,   84,  194,    0,    0,  -19,   62,   98,
   39,   68,   69,    0,    4,    0,    0,
};
final static int YYTABLESIZE=450;
final static short yytable[] = {                         20,
    9,   20,   20,   27,   36,   53,   61,    8,   20,   20,
   33,   32,   36,   36,   10,   34,   74,    1,   20,   44,
    7,   65,   66,   67,   10,   27,   10,   47,   29,   45,
   20,   28,    7,    9,   41,   56,   46,   21,    9,   20,
    9,    9,    9,    9,   64,   39,    9,    9,   20,   59,
   29,   50,   49,   28,   42,   43,   71,   10,    9,    9,
   34,   72,   10,    9,   10,   10,   10,   10,    9,   73,
   10,   10,   48,   62,   63,   21,   75,   76,   21,   77,
    9,   35,   10,   10,   21,   20,   13,   10,    7,   35,
   35,    1,    3,   34,    9,   20,   21,   37,   34,   20,
   34,   34,   34,   34,   30,   68,   34,   34,    2,    3,
    4,    5,   69,    0,   70,    0,    0,   10,   10,   34,
    0,    7,    0,   34,    9,   20,    7,   13,    7,    7,
    7,    7,   24,   13,    7,    7,    0,   20,    0,    1,
   20,   52,    0,    0,    0,   13,   20,    7,   10,    0,
   10,    7,    0,    0,   34,   10,   26,   10,   20,   10,
   10,   21,    0,   10,   10,   24,    0,    0,    0,    1,
   24,   55,   24,   24,    0,   10,    0,    0,   24,   24,
   10,    0,    7,    1,   34,   58,    0,    0,    0,   26,
    0,   24,    0,   16,   26,    0,   26,   26,   26,   26,
    0,    0,   26,   26,    0,    0,    0,   38,    0,    0,
   13,   10,    7,    0,    0,   26,    0,    0,    0,   26,
   54,   57,   60,   20,    0,    0,   24,   20,    0,   14,
    0,    1,    2,    3,    4,    5,    6,   20,    0,    0,
    0,   10,    0,    1,    2,    3,    4,    5,    6,    0,
   26,    0,    0,    0,   39,    0,   24,    9,    9,    9,
    9,    9,    9,    0,   18,    0,    9,   20,    9,    9,
   14,    0,    0,    0,    0,    0,   14,    0,    0,    0,
   26,   10,   10,   10,   10,   10,   10,    0,   14,    0,
   10,    0,   10,   10,    0,    0,   20,    0,    0,    0,
    0,    0,   18,   21,    0,   18,    0,    0,    0,    0,
    0,   18,    0,    0,    0,    0,    0,   34,   34,   34,
   34,   34,   34,   18,    0,   15,   34,    0,   34,   34,
    0,    0,    0,    0,   20,    0,    0,   20,    0,    0,
    0,    0,    0,   20,    0,    7,    7,    7,    7,    7,
    7,    0,    0,   14,    7,   20,    7,    7,    0,    0,
    0,    0,    0,    0,    0,   20,   15,    0,    0,    0,
    0,    0,   15,    0,   10,   10,   10,   10,   10,   10,
    0,    0,    0,   10,   15,   10,   10,    0,   18,   24,
   24,   24,   24,   24,   24,    0,    0,    0,   24,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   26,   26,   26,   26,   26,   26,    0,
   20,    0,   26,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   15,
};
final static short yycheck[] = {                         38,
    0,    0,   41,   36,   47,  123,   41,   33,   47,   38,
  261,    8,   47,   47,   40,   10,   59,  257,   47,  267,
   46,   41,   42,   43,    0,   36,   40,   24,   61,   38,
   59,   64,   46,   33,   58,  123,  266,    0,   38,   38,
   40,   41,   42,   43,   39,  265,   46,   47,   47,  123,
   61,   42,   43,   64,  268,  269,  259,   33,   58,   59,
    0,  259,   38,   63,   40,   41,   42,   43,   94,  259,
   46,   47,   63,   35,   36,   38,  125,  125,   41,  125,
   94,  124,   58,   59,   47,  124,    0,   63,    0,  124,
  124,    0,    0,   33,   94,  124,   59,   14,   38,    0,
   40,   41,   42,   43,    7,   44,   46,   47,  258,  259,
  260,  261,   45,   -1,   46,   -1,   -1,    0,   94,   59,
   -1,   33,   -1,   63,  124,  124,   38,   41,   40,   41,
   42,   43,    0,   47,   46,   47,   -1,   38,   -1,  257,
   41,  259,   -1,   -1,   -1,   59,   47,   59,  124,   -1,
   33,   63,   -1,   -1,   94,   38,    0,   40,   59,   42,
   43,  124,   -1,   46,   47,   33,   -1,   -1,   -1,  257,
   38,  259,   40,   41,   -1,   58,   -1,   -1,   46,   47,
   63,   -1,   94,  257,  124,  259,   -1,   -1,   -1,   33,
   -1,   59,   -1,    0,   38,   -1,   40,   41,   42,   43,
   -1,   -1,   46,   47,   -1,   -1,   -1,   14,   -1,   -1,
  124,   94,  124,   -1,   -1,   59,   -1,   -1,   -1,   63,
   27,   28,   29,  124,   -1,   -1,   94,  266,   -1,    0,
   -1,  257,  258,  259,  260,  261,  262,  266,   -1,   -1,
   -1,  124,   -1,  257,  258,  259,  260,  261,  262,   -1,
   94,   -1,   -1,   -1,  265,   -1,  124,  257,  258,  259,
  260,  261,  262,   -1,    0,   -1,  266,  266,  268,  269,
   41,   -1,   -1,   -1,   -1,   -1,   47,   -1,   -1,   -1,
  124,  257,  258,  259,  260,  261,  262,   -1,   59,   -1,
  266,   -1,  268,  269,   -1,   -1,    0,   -1,   -1,   -1,
   -1,   -1,   38,  266,   -1,   41,   -1,   -1,   -1,   -1,
   -1,   47,   -1,   -1,   -1,   -1,   -1,  257,  258,  259,
  260,  261,  262,   59,   -1,    0,  266,   -1,  268,  269,
   -1,   -1,   -1,   -1,   38,   -1,   -1,   41,   -1,   -1,
   -1,   -1,   -1,   47,   -1,  257,  258,  259,  260,  261,
  262,   -1,   -1,  124,  266,   59,  268,  269,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  266,   41,   -1,   -1,   -1,
   -1,   -1,   47,   -1,  257,  258,  259,  260,  261,  262,
   -1,   -1,   -1,  266,   59,  268,  269,   -1,  124,  257,
  258,  259,  260,  261,  262,   -1,   -1,   -1,  266,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  257,  258,  259,  260,  261,  262,   -1,
  124,   -1,  266,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  124,
};
final static short YYFINAL=11;
final static short YYMAXTOKEN=269;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,"'!'",null,null,"'$'",null,"'&'",null,"'('","')'","'*'","'+'",
null,null,"'.'","'/'",null,null,null,null,null,null,null,null,null,null,"':'",
"';'",null,"'='",null,"'?'","'@'",null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,"'^'",null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,"'{'","'|'","'}'",null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,"Tag","LiteralChar",
"LiteralInteger","LiteralFloat64","LiteralString","LiteralChars","EOL","EOTLU",
"OpDef","OpAction","OpThru","OpDoubleStar","OpDoublePlus",
};
final static String yyrule[] = {
"$accept : start",
"start : rhs",
"start : schema",
"schema : productions",
"productions : production",
"productions : productions production",
"production : id OpDef rhs ';'",
"term : functor",
"term : functor ':' prim",
"functor : literal",
"functor : id",
"functor : '.' functorHole",
"functor : id functorHole",
"rhs : interleave",
"rhs : rhs '|' interleave",
"rhs : rhs '/' interleave",
"interleave : action",
"interleave : interleave '&' action",
"action : argList",
"action : argList OpAction argList",
"argList :",
"argList : args",
"args : pred",
"args : args pred",
"pred : some",
"pred : '!' pred",
"some : prim",
"some : some quant",
"some : prim OpDoubleStar prim",
"some : prim OpDoublePlus prim",
"quant : '?'",
"quant : '+'",
"quant : '*'",
"prim : term",
"prim : '.'",
"prim : literal OpThru literal",
"prim : LiteralChars",
"prim : '^' LiteralString",
"prim : '(' rhs ')'",
"literal : LiteralChar",
"literal : LiteralInteger",
"literal : LiteralFloat64",
"literal : LiteralString",
"id : Tag",
"functorHole : '$' '{' LiteralInteger '}'",
"functorHole : '$' LiteralInteger",
"functorHole : '$' id",
"functorHole : '@' '{' LiteralInteger '}'",
"functorHole : '@' LiteralInteger",
"functorHole : '@' id",
"functorHole : '=' '{' LiteralInteger '}'",
"functorHole : '=' LiteralInteger",
"functorHole : '=' id",
};

//#line 175 "grammar.y"


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
//#line 552 "GrammarParser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}



//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  char:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]+"");
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 55 "grammar.y"
{ myOptResult = b.start((AstroArg)val_peek(0)); }
break;
case 2:
//#line 56 "grammar.y"
{ myOptResult = b.start((Astro)val_peek(0)); }
break;
case 3:
//#line 60 "grammar.y"
{ yyval = b.schema((AstroArg)val_peek(0)); }
break;
case 5:
//#line 65 "grammar.y"
{ yyval = b.seq((AstroArg)val_peek(1), (Astro)val_peek(0)); }
break;
case 6:
//#line 69 "grammar.y"
{ yyval = b.production((Astro)val_peek(3), (AstroArg)val_peek(1)); }
break;
case 7:
//#line 73 "grammar.y"
{ yyval = b.term((Astro)val_peek(0)); }
break;
case 8:
//#line 74 "grammar.y"
{ yyval = b.attr((Astro)val_peek(2), (AstroArg)val_peek(0)); }
break;
case 11:
//#line 80 "grammar.y"
{ yyval = val_peek(0); }
break;
case 12:
//#line 81 "grammar.y"
{ yyval = b.taggedHole((Astro)val_peek(1), (Astro)val_peek(0)); }
break;
case 14:
//#line 86 "grammar.y"
{ yyval = b.onlyChoice((AstroArg)val_peek(2),
                                                    (AstroArg)val_peek(0)); }
break;
case 15:
//#line 88 "grammar.y"
{ yyval = b.firstChoice((AstroArg)val_peek(2),
                                                     (AstroArg)val_peek(0)); }
break;
case 17:
//#line 94 "grammar.y"
{ yyval = b.interleave((AstroArg)val_peek(2),
                                                    (AstroArg)val_peek(0)); }
break;
case 19:
//#line 100 "grammar.y"
{ yyval = b.action((AstroArg)val_peek(2), (AstroArg)val_peek(0)); }
break;
case 20:
//#line 104 "grammar.y"
{ yyval = b.empty(); }
break;
case 23:
//#line 110 "grammar.y"
{ yyval = b.seq((AstroArg)val_peek(1), (AstroArg)val_peek(0)); }
break;
case 25:
//#line 115 "grammar.y"
{ yyval = b.not((AstroArg)val_peek(0)); }
break;
case 27:
//#line 123 "grammar.y"
{ yyval = b.some((AstroArg)val_peek(1),
                                              ((Character)val_peek(0)).charValue()); }
break;
case 28:
//#line 125 "grammar.y"
{ yyval = b.some((AstroArg)val_peek(2),
                                              '*',
                                              (AstroArg)val_peek(0)); }
break;
case 29:
//#line 128 "grammar.y"
{ yyval = b.some((AstroArg)val_peek(2),
                                              '+',
                                              (AstroArg)val_peek(0)); }
break;
case 30:
//#line 134 "grammar.y"
{ yyval = CharacterMakerSugar.valueOf('?'); }
break;
case 31:
//#line 135 "grammar.y"
{ yyval = CharacterMakerSugar.valueOf('+'); }
break;
case 32:
//#line 136 "grammar.y"
{ yyval = CharacterMakerSugar.valueOf('*'); }
break;
case 34:
//#line 141 "grammar.y"
{ yyval = b.any(); }
break;
case 35:
//#line 142 "grammar.y"
{ yyval = b.range((Astro)val_peek(2), (Astro)val_peek(0)); }
break;
case 36:
//#line 143 "grammar.y"
{ yyval = b.unpack((Astro)val_peek(0)); }
break;
case 37:
//#line 144 "grammar.y"
{ yyval = b.anyOf((Astro)val_peek(0)); }
break;
case 38:
//#line 145 "grammar.y"
{ yyval = val_peek(1); }
break;
case 43:
//#line 156 "grammar.y"
{ yyval = untag((Astro)val_peek(0)); }
break;
case 44:
//#line 163 "grammar.y"
{ yyval = b.dollarHole((Astro)val_peek(1)); }
break;
case 45:
//#line 164 "grammar.y"
{ yyval = b.dollarHole((Astro)val_peek(0)); }
break;
case 46:
//#line 165 "grammar.y"
{ yyval = b.dollarHole((Astro)val_peek(0)); }
break;
case 47:
//#line 166 "grammar.y"
{ yyval = b.atHole(    (Astro)val_peek(1)); }
break;
case 48:
//#line 167 "grammar.y"
{ yyval = b.atHole(    (Astro)val_peek(0)); }
break;
case 49:
//#line 168 "grammar.y"
{ yyval = b.atHole(    (Astro)val_peek(0)); }
break;
case 50:
//#line 169 "grammar.y"
{ yyval = b.atHole(    (Astro)val_peek(1)); }
break;
case 51:
//#line 170 "grammar.y"
{ yyval = b.atHole(    (Astro)val_peek(0)); }
break;
case 52:
//#line 171 "grammar.y"
{ yyval = b.atHole(    (Astro)val_peek(0)); }
break;
//#line 851 "GrammarParser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



}
//################### END OF CLASS yaccpar ######################
