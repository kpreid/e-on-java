//### This file created by BYACC 1.8(/Java extension  0.92)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//### Please send bug reports to rjamison@lincom-asg.com
//### static char yysccsid[] = "@(#)yaccpar       1.8 (Berkeley) 01/20/90";



//#line 8 "term.y"

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
//#line 29 "TermParser.java"




//#####################################################################
// class: TermParser
// does : encapsulates yacc() parser functionality in a Java
//        class for quick code development
//#####################################################################
public class TermParser
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
    0,    0,    2,    3,    3,    4,    6,    6,    6,    6,
    6,    8,    7,    7,    7,    7,    7,    7,    1,    1,
    1,   11,   11,   12,   12,   12,   13,   13,   14,   14,
   15,   15,   16,   16,   16,   16,   16,   18,   18,   18,
   17,   17,   17,   17,   17,   17,   19,   19,    9,    9,
    9,    9,    5,   10,   10,   10,   10,   10,   10,   10,
   10,   10,
};
final static short yylen[] = {                            2,
    1,    1,    1,    1,    2,    4,    1,    4,    3,    1,
    2,    3,    1,    1,    1,    2,    2,    2,    0,    1,
    2,    1,    3,    1,    3,    3,    1,    3,    1,    3,
    1,    2,    1,    2,    1,    3,    3,    1,    1,    1,
    1,    1,    3,    1,    2,    3,    1,    3,    1,    1,
    1,    1,    1,    4,    2,    2,    4,    2,    2,    4,
    2,    2,
};
final static short yydefred[] = {                         0,
   53,   49,   50,   51,   52,   44,    0,    0,    0,    0,
    0,   38,   39,   40,    0,    0,    0,    0,    0,    1,
    2,    0,    4,    0,    0,    0,   10,    0,    0,    0,
    0,    0,   27,    0,    0,    0,   35,   41,    0,    0,
    0,    0,   16,   32,   45,   55,    0,   56,   58,    0,
   59,   61,    0,   62,    5,    0,    0,   17,    0,    0,
   11,    0,   18,    0,    0,    0,    0,    0,   34,    0,
    0,   46,    9,   12,    0,    0,    0,    0,    0,   48,
   13,    0,   43,    0,    0,    0,   28,   30,   36,   37,
   54,   57,   60,    6,    8,
};
final static short yydgoto[] = {                         19,
   20,   21,   22,   23,   40,   25,   26,   27,   28,   29,
   30,   31,   32,   33,   34,   35,   36,   37,   38,
};
final static short yysindex[] = {                       210,
    0,    0,    0,    0,    0,    0,  210,  210,  210,  -26,
  210,    0,    0,    0, -250, -119, -115,  -68,    0,    0,
    0, -240,    0,  -33,  -36,  -40,    0, -237,  -26,    2,
  -42,   -2,    0, -212,  -23, -242,    0,    0,   20,  -26,
  -27,  -56,    0,    0,    0,    0, -189,    0,    0, -186,
    0,    0, -185,    0,    0, -176,  210,    0,   67,  210,
    0, -173,    0,  210,  210,  210,  210,  210,    0,  246,
  246,    0,    0,    0,  -35,  -34,  -28,   33,  -26,    0,
    0,   52,    0,  -42,   -2,   -2,    0,    0,    0,    0,
    0,    0,    0,    0,    0,
};
final static short yyrindex[] = {                        98,
    0,    0,    0,    0,    0,    0,   58,    8,  -21,   76,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  106,    0,  136,  112,  105,    0,    1,    9,  186,
  129,  272,    0,  291,  172,  165,    0,    0,    0,   37,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   48,    0,    0,   58,
    0,    0,    0,  192,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  318,  302,  341,    0,    0,    0,    0,
    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    5,    0,    0,   86,    7,   50,    0,   84,  -44,   -8,
    0,   47,  -32,   49,   -5,    0,  -13,   80,    0,
};
final static int YYTABLESIZE=508;
final static short yytable[] = {                         60,
   13,   43,   16,   47,   66,   44,   24,   50,   15,   16,
   45,   39,   41,   42,   81,   58,    1,   83,   14,   13,
   63,   59,   48,   51,   54,   70,   71,   18,   56,   62,
   17,   58,   85,   86,   18,   67,   14,   17,   13,   12,
   13,   13,   13,   13,   13,   64,   15,   13,   15,   15,
   15,   15,   15,   68,   53,   15,   89,   90,   13,   13,
   72,   78,   88,   13,   82,   73,   15,   15,   74,   75,
   43,   15,   76,   77,   14,   42,   14,   14,   14,   14,
   14,   65,    9,   14,    2,    3,    4,    5,   57,   91,
   92,   94,   95,   13,   14,   14,   93,   19,   19,   14,
   19,   15,   16,   19,    7,    3,   19,   55,   80,   61,
   84,   47,   79,   42,   69,   87,   42,   42,   42,   42,
    0,    0,   42,   13,   13,   13,    0,   18,   22,   14,
   17,   15,   15,   15,   42,   14,    0,    1,   42,   46,
    0,    1,    7,   49,    0,    7,    7,    7,    7,   47,
    0,    7,   47,   47,   47,   47,    0,    8,   47,   14,
   14,   14,    7,    7,   33,    0,    0,    7,   42,   22,
   47,   31,   22,   14,   47,   14,    0,   14,   14,   14,
    0,    0,   14,    0,    0,   20,    0,   22,    1,    9,
   52,   21,    0,   14,    0,    0,    0,    7,   14,   42,
   42,    0,   33,    0,   47,   33,   33,   33,   33,   31,
    0,   33,   31,    0,    0,   31,    0,    0,   31,    0,
    0,   22,    0,   33,    0,    0,   20,   33,    7,    7,
   31,   57,   21,    0,    0,   47,   47,    0,    0,    0,
    0,    0,   11,    0,   20,   16,    0,    0,    0,    7,
   21,   14,   13,   22,    0,   10,    0,   33,   14,   14,
    0,    0,    0,    0,   31,    0,   13,    0,   13,   13,
   18,   24,   12,   17,   15,    0,   15,   15,   20,    0,
    0,   16,    0,    0,   21,    7,    0,    0,   33,   33,
   29,   10,    0,    0,    0,   31,   31,    0,    0,    0,
    8,   25,   14,   15,   14,   14,   18,    0,    0,   17,
   20,    0,   24,    0,    0,   24,   21,   23,   24,    0,
    0,    0,    0,    1,    2,    3,    4,    5,   29,    0,
   24,   29,    9,    0,   29,    0,    8,   29,    0,   15,
   26,   42,   25,   42,   42,   25,    0,    0,   25,   29,
    0,    0,    0,    0,    0,    0,    0,    0,   23,    0,
   25,   23,    0,    0,   24,    0,    0,    0,    9,    0,
    7,    0,    7,    7,    0,    0,   23,   47,    0,   47,
   47,   26,    0,   29,   26,    0,    0,   26,    0,    0,
    0,    0,    0,    0,   25,   24,   24,    0,    0,   26,
    0,   14,    0,   14,   14,    0,    0,    0,    0,    0,
   23,    0,    0,    0,   29,   29,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   25,   25,    0,    0,    0,
   33,    0,    0,   26,    0,    0,    0,   31,    0,    0,
    0,    0,   23,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   26,   26,    1,    2,    3,    4,
    5,    6,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    1,    2,    3,    4,    5,    6,
};
final static short yycheck[] = {                         40,
    0,   10,   36,  123,   47,   11,    0,  123,    0,   36,
  261,    7,    8,    9,   59,   24,  257,   62,   42,   43,
   29,   58,   16,   17,   18,  268,  269,   61,   22,  267,
   64,   40,   65,   66,   61,   38,    0,   64,   38,   63,
   40,   41,   42,   43,   44,   44,   38,   47,   40,   41,
   42,   43,   44,  266,  123,   47,   70,   71,   58,   59,
   41,   57,   68,   63,   60,   93,   58,   59,  125,  259,
   79,   63,  259,  259,   38,    0,   40,   41,   42,   43,
   44,  124,  123,   47,  258,  259,  260,  261,  265,  125,
  125,   59,   41,   93,   58,   59,  125,    0,   41,   63,
   93,   93,   36,  125,    0,    0,   59,   22,   59,   26,
   64,    0,   46,   38,   35,   67,   41,   42,   43,   44,
   -1,   -1,   47,  123,  124,  125,   -1,   61,    0,   93,
   64,  123,  124,  125,   59,    0,   -1,  257,   63,  259,
   -1,  257,   38,  259,   -1,   41,   42,   43,   44,   38,
   -1,   47,   41,   42,   43,   44,   -1,   91,   47,  123,
  124,  125,   58,   59,    0,   -1,   -1,   63,   93,   41,
   59,    0,   44,   38,   63,   40,   -1,   42,   43,   44,
   -1,   -1,   47,   -1,   -1,    0,   -1,   59,  257,  123,
  259,    0,   -1,   58,   -1,   -1,   -1,   93,   63,  124,
  125,   -1,   38,   -1,   93,   41,   42,   43,   44,   38,
   -1,   47,   41,   -1,   -1,   44,   -1,   -1,   47,   -1,
   -1,   93,   -1,   59,   -1,   -1,   41,   63,  124,  125,
   59,  265,   41,   -1,   -1,  124,  125,   -1,   -1,   -1,
   -1,   -1,   33,   -1,   59,   36,   -1,   -1,   -1,   40,
   59,   42,   43,  125,   -1,   46,   -1,   93,  123,  124,
   -1,   -1,   -1,   -1,   93,   -1,  266,   -1,  268,  269,
   61,    0,   63,   64,  266,   -1,  268,  269,   93,   -1,
   -1,   36,   -1,   -1,   93,   40,   -1,   -1,  124,  125,
    0,   46,   -1,   -1,   -1,  124,  125,   -1,   -1,   -1,
   91,    0,  266,   94,  268,  269,   61,   -1,   -1,   64,
  125,   -1,   41,   -1,   -1,   44,  125,    0,   47,   -1,
   -1,   -1,   -1,  257,  258,  259,  260,  261,   38,   -1,
   59,   41,  123,   -1,   44,   -1,   91,   47,   -1,   94,
    0,  266,   41,  268,  269,   44,   -1,   -1,   47,   59,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   41,   -1,
   59,   44,   -1,   -1,   93,   -1,   -1,   -1,  123,   -1,
  266,   -1,  268,  269,   -1,   -1,   59,  266,   -1,  268,
  269,   41,   -1,   93,   44,   -1,   -1,   47,   -1,   -1,
   -1,   -1,   -1,   -1,   93,  124,  125,   -1,   -1,   59,
   -1,  266,   -1,  268,  269,   -1,   -1,   -1,   -1,   -1,
   93,   -1,   -1,   -1,  124,  125,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  124,  125,   -1,   -1,   -1,
  266,   -1,   -1,   93,   -1,   -1,   -1,  266,   -1,   -1,
   -1,   -1,  125,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  124,  125,  257,  258,  259,  260,
  261,  262,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  257,  258,  259,  260,  261,  262,
};
final static short YYFINAL=19;
final static short YYMAXTOKEN=269;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,"'!'",null,null,"'$'",null,"'&'",null,"'('","')'","'*'","'+'",
"','",null,"'.'","'/'",null,null,null,null,null,null,null,null,null,null,"':'",
"';'",null,"'='",null,"'?'","'@'",null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,"'['",null,"']'","'^'",null,null,null,null,null,null,null,null,null,null,
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
"term : functor '(' rhs ')'",
"term : '[' rhs ']'",
"term : bag",
"term : functor bag",
"bag : '{' rhs '}'",
"functor : literal",
"functor : id",
"functor : functorHole",
"functor : '.' functorHole",
"functor : id functorHole",
"functor : functorHole functorHole",
"rhs :",
"rhs : args",
"rhs : args ','",
"args : arg",
"args : args ',' arg",
"arg : interleave",
"arg : arg '|' interleave",
"arg : arg '/' interleave",
"interleave : action",
"interleave : interleave '&' action",
"action : pred",
"action : pred OpAction pred",
"pred : some",
"pred : '!' pred",
"some : prim",
"some : some quant",
"some : quant",
"some : prim OpDoubleStar prim",
"some : prim OpDoublePlus prim",
"quant : '?'",
"quant : '+'",
"quant : '*'",
"prim : attr",
"prim : '.'",
"prim : literal OpThru literal",
"prim : LiteralChars",
"prim : '^' LiteralString",
"prim : '(' rhs ')'",
"attr : term",
"attr : term ':' term",
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

//#line 232 "term.y"


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
public TermParser(LexerFace lexer, QuasiBuilder builder) {
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
        TermParser parser = new TermParser(lexer, builder);
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
 * TermParser specifically, since identifiers in the input must all be
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
//#line 582 "TermParser.java"
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
//#line 94 "term.y"
{ myOptResult = b.start((AstroArg)val_peek(0)); }
break;
case 2:
//#line 95 "term.y"
{ myOptResult = b.start((Astro)val_peek(0)); }
break;
case 3:
//#line 99 "term.y"
{ yyval = b.schema((AstroArg)val_peek(0)); }
break;
case 5:
//#line 104 "term.y"
{ yyval = b.seq((AstroArg)val_peek(1), (Astro)val_peek(0)); }
break;
case 6:
//#line 108 "term.y"
{ yyval = b.production((Astro)val_peek(3), (AstroArg)val_peek(1)); }
break;
case 7:
//#line 113 "term.y"
{ yyval = b.term((Astro)val_peek(0)); }
break;
case 8:
//#line 114 "term.y"
{ yyval = b.term((Astro)val_peek(3), (AstroArg)val_peek(1)); }
break;
case 9:
//#line 115 "term.y"
{ yyval = b.tuple((AstroArg)val_peek(1)); }
break;
case 11:
//#line 117 "term.y"
{ yyval = b.term((Astro)val_peek(1), (Astro)val_peek(0)); }
break;
case 12:
//#line 121 "term.y"
{ yyval = b.bag((AstroArg)val_peek(1)); }
break;
case 16:
//#line 128 "term.y"
{ yyval = val_peek(0); }
break;
case 17:
//#line 129 "term.y"
{ yyval = b.taggedHole((Astro)val_peek(1), (Astro)val_peek(0)); }
break;
case 18:
//#line 130 "term.y"
{ reserved("hole-tagged-hole"); }
break;
case 19:
//#line 134 "term.y"
{ yyval = b.empty(); }
break;
case 23:
//#line 141 "term.y"
{ yyval = b.seq((AstroArg)val_peek(2), (AstroArg)val_peek(0)); }
break;
case 25:
//#line 146 "term.y"
{ yyval = b.onlyChoice((AstroArg)val_peek(2),
                                                    (AstroArg)val_peek(0)); }
break;
case 26:
//#line 148 "term.y"
{ yyval = b.firstChoice((AstroArg)val_peek(2),
                                                     (AstroArg)val_peek(0)); }
break;
case 28:
//#line 154 "term.y"
{ yyval = b.interleave((AstroArg)val_peek(2),
                                                    (AstroArg)val_peek(0)); }
break;
case 30:
//#line 160 "term.y"
{ yyval = b.action((AstroArg)val_peek(2), (AstroArg)val_peek(0)); }
break;
case 32:
//#line 165 "term.y"
{ yyval = b.not((AstroArg)val_peek(0)); }
break;
case 34:
//#line 173 "term.y"
{ yyval = b.some((AstroArg)val_peek(1),
                                              ((Character)val_peek(0)).charValue()); }
break;
case 35:
//#line 175 "term.y"
{ yyval = b.some(null,
                                              ((Character)val_peek(0)).charValue()); }
break;
case 36:
//#line 177 "term.y"
{ yyval = b.some((AstroArg)val_peek(2),
                                              '*',
                                              (AstroArg)val_peek(0)); }
break;
case 37:
//#line 180 "term.y"
{ yyval = b.some((AstroArg)val_peek(2),
                                              '+',
                                              (AstroArg)val_peek(0)); }
break;
case 38:
//#line 186 "term.y"
{ yyval = CharacterMakerSugar.valueOf('?'); }
break;
case 39:
//#line 187 "term.y"
{ yyval = CharacterMakerSugar.valueOf('+'); }
break;
case 40:
//#line 188 "term.y"
{ yyval = CharacterMakerSugar.valueOf('*'); }
break;
case 42:
//#line 193 "term.y"
{ yyval = b.any(); }
break;
case 43:
//#line 194 "term.y"
{ yyval = b.range((Astro)val_peek(2), (Astro)val_peek(0)); }
break;
case 44:
//#line 195 "term.y"
{ yyval = b.unpack((Astro)val_peek(0)); }
break;
case 45:
//#line 196 "term.y"
{ yyval = b.anyOf((Astro)val_peek(0)); }
break;
case 46:
//#line 197 "term.y"
{ yyval = val_peek(1); }
break;
case 48:
//#line 202 "term.y"
{ yyval = b.attr((Astro)val_peek(2), (Astro)val_peek(0)); }
break;
case 53:
//#line 213 "term.y"
{ yyval = untag((Astro)val_peek(0)); }
break;
case 54:
//#line 220 "term.y"
{ yyval = b.dollarHole((Astro)val_peek(1)); }
break;
case 55:
//#line 221 "term.y"
{ yyval = b.dollarHole((Astro)val_peek(0)); }
break;
case 56:
//#line 222 "term.y"
{ yyval = b.dollarHole((Astro)val_peek(0)); }
break;
case 57:
//#line 223 "term.y"
{ yyval = b.atHole(    (Astro)val_peek(1)); }
break;
case 58:
//#line 224 "term.y"
{ yyval = b.atHole(    (Astro)val_peek(0)); }
break;
case 59:
//#line 225 "term.y"
{ yyval = b.atHole(    (Astro)val_peek(0)); }
break;
case 60:
//#line 226 "term.y"
{ yyval = b.atHole(    (Astro)val_peek(1)); }
break;
case 61:
//#line 227 "term.y"
{ yyval = b.atHole(    (Astro)val_peek(0)); }
break;
case 62:
//#line 228 "term.y"
{ yyval = b.atHole(    (Astro)val_peek(0)); }
break;
//#line 906 "TermParser.java"
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
