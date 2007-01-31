//### This file created by BYACC 1.8(/Java extension  0.92)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//### Please send bug reports to rjamison@lincom-asg.com
//### static char yysccsid[] = "@(#)yaccpar       1.8 (Berkeley) 01/20/90";



//#line 5 "orc.y"
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
//#line 29 "OrcParser.java"




//#####################################################################
// class: OrcParser
// does : encapsulates yacc() parser functionality in a Java
//        class for quick code development
//#####################################################################
public class OrcParser
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
public final static short EOL=257;
public final static short EOTLU=258;
public final static short LiteralInteger=259;
public final static short LiteralString=260;
public final static short ID=261;
public final static short LiteralFloat64=262;
public final static short LiteralChar=263;
public final static short DEF=264;
public final static short WHERE=265;
public final static short IN=266;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    1,    1,    3,    4,    4,    6,    8,    8,    8,
    2,    9,    9,    5,    5,   11,   11,   12,   10,   10,
   13,   13,   15,   15,   15,   15,   14,   14,   14,   14,
   14,   14,   17,   20,   20,   21,   21,   18,    7,   16,
   22,   19,   19,
};
final static short yylen[] = {                            2,
    1,    1,    2,    2,    1,    2,    5,    1,    2,    3,
    0,    1,    3,    1,    3,    1,    3,    3,    1,    3,
    1,    5,    1,    1,    1,    2,    1,    1,    1,    1,
    1,    1,    4,    1,    1,    1,    3,    3,    1,    1,
    1,    4,    4,
};
final static short yydefred[] = {                        11,
    0,    0,    2,    5,    3,    0,   28,   29,   41,    0,
   11,    0,    0,    4,    6,    0,    0,   21,    0,   30,
   31,   32,   40,    0,   39,    0,    0,    0,    0,    0,
    0,   11,    0,    8,    0,   38,    0,    0,    0,    0,
   16,    0,    0,   23,   24,    0,    0,   36,    0,    0,
    9,   12,    0,   11,   42,   43,    0,    0,   26,    0,
   33,   11,   10,    0,    7,    0,   17,   22,   37,   13,
};
final static short yydgoto[] = {                          1,
    2,    4,    5,    6,   14,   15,   39,   35,   53,   16,
   40,   41,   17,   18,   46,   19,   20,   21,   22,   49,
   50,   25,
};
final static short yysindex[] = {                         0,
    0,    0,    0,    0,    0,  -30,    0,    0,    0, -252,
    0, -112, -110,    0,    0, -121,  -48,    0,  -23,    0,
    0,    0,    0,  -22,    0, -109, -240, -239, -252,  -24,
  -29,    0,  -39,    0,  -40,    0, -103, -102, -242,  -28,
    0,  -48, -252,    0,    0,  -33,    0,    0,  -11,   -9,
    0,    0,  -34,    0,    0,    0,  -24, -252,    0,  -24,
    0,    0,    0, -252,    0,  -91,    0,    0,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,   46,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   32,    8,    0,    1,    0,
    0,    0,    0,  -25,    0,    0,    0,    0,    0,    0,
  -21,    0,    0,    0,    0,    0,    0,    0,    0,   39,
    0,   15,  -19,    0,    0,    0,  -36,    0,    0,   -3,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   25,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    0,   26,   16,    0,    0,    0,   71,    0,    0,   -4,
    0,    4,   24,   -5,    0,    0,    0,    0,    0,    0,
    0,   41,
};
final static int YYTABLESIZE=310;
final static short yytable[] = {                          5,
   27,   51,   30,   43,   34,   12,   63,   19,    9,   64,
   27,   12,   28,   31,   20,   36,   32,   33,   37,   38,
   54,   55,   56,   57,   18,    3,   26,    5,   60,   61,
   58,   14,   30,   13,   62,   11,   27,   35,   15,   13,
   11,   27,   25,   19,   27,    1,   23,   48,   19,   34,
   20,   19,   66,   42,   68,   20,   44,   47,   20,   27,
   18,   67,   27,    0,   27,   18,   19,   14,   18,   65,
   23,   19,   14,   20,   15,   14,    0,   69,   20,   15,
   24,   11,   15,   18,    0,    0,    5,    0,   18,    0,
    0,    0,   11,    0,    0,   14,    0,   23,   11,    0,
   23,   45,   15,   52,    0,    0,    0,    0,    0,   11,
    0,    0,    0,   59,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   27,   27,   27,    0,    0,    0,    0,
   19,   19,   19,    0,   70,    0,    0,   20,   20,   20,
    0,    0,    0,   29,    0,    0,    0,   18,    0,   18,
    0,    0,    0,    0,   14,    0,   14,    0,    0,    0,
    0,   15,    0,   15,    0,    0,    0,    0,   11,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    9,    5,    5,    5,    0,    0,    5,    7,    8,
    9,    9,    0,   10,    7,    8,    9,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   27,
   27,   27,    0,    0,   27,   27,   19,   19,   19,    0,
    0,   19,   19,   20,   20,   20,    0,    0,   20,   20,
    0,    0,    0,   18,   18,   18,    0,    0,   18,    0,
   14,   14,   14,    0,    0,   14,    0,   15,   15,   15,
    0,    0,   15,    0,   11,   11,   11,    0,    0,   11,
};
final static short yycheck[] = {                         36,
    0,   41,  124,   33,   41,   36,   41,    0,  261,   44,
  123,   36,  123,   62,    0,  125,   40,   40,  259,  259,
   61,  125,  125,  266,    0,    0,   11,   64,   62,   41,
   59,    0,  124,   64,   44,   61,   36,   41,    0,   64,
   62,   41,   62,   36,   44,    0,    6,   32,   41,   24,
   36,   44,   57,   30,   60,   41,   31,   32,   44,   59,
   36,   58,   62,   -1,   64,   41,   59,   36,   44,   54,
   30,   64,   41,   59,   36,   44,   -1,   62,   64,   41,
   10,   36,   44,   59,   -1,   -1,  123,   -1,   64,   -1,
   -1,   -1,  123,   -1,   -1,   64,   -1,   57,  123,   -1,
   60,   31,   64,   33,   -1,   -1,   -1,   -1,   -1,   64,
   -1,   -1,   -1,   43,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  123,  124,  125,   -1,   -1,   -1,   -1,
  123,  124,  125,   -1,   64,   -1,   -1,  123,  124,  125,
   -1,   -1,   -1,  265,   -1,   -1,   -1,  123,   -1,  125,
   -1,   -1,   -1,   -1,  123,   -1,  125,   -1,   -1,   -1,
   -1,  123,   -1,  125,   -1,   -1,   -1,   -1,  123,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  261,  259,  260,  261,   -1,   -1,  264,  259,  260,
  261,  261,   -1,  264,  259,  260,  261,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  259,
  260,  261,   -1,   -1,  264,  265,  259,  260,  261,   -1,
   -1,  264,  265,  259,  260,  261,   -1,   -1,  264,  265,
   -1,   -1,   -1,  259,  260,  261,   -1,   -1,  264,   -1,
  259,  260,  261,   -1,   -1,  264,   -1,  259,  260,  261,
   -1,   -1,  264,   -1,  259,  260,  261,   -1,   -1,  264,
};
final static short YYFINAL=1;
final static short YYMAXTOKEN=266;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,"'!'",null,null,"'$'",null,null,null,"'('","')'",null,null,"','",
null,null,null,null,null,null,null,null,null,null,null,null,null,null,"';'",
null,"'='","'>'",null,"'@'",null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
"'{'","'|'","'}'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,"EOL","EOTLU","LiteralInteger",
"LiteralString","ID","LiteralFloat64","LiteralChar","DEF","WHERE","IN",
};
final static String yyrule[] = {
"$accept : start",
"start : exprs",
"exprs : empty",
"exprs : exprs expr",
"expr : defs goal",
"defs : empty",
"defs : defs def",
"def : DEF varID optFormals '=' expr",
"optFormals : empty",
"optFormals : '(' ')'",
"optFormals : '(' idList ')'",
"empty :",
"idList : varID",
"idList : idList ',' varID",
"goal : par",
"goal : par WHERE bindingList",
"bindingList : binding",
"bindingList : bindingList ';' binding",
"binding : varID IN par",
"par : seq",
"par : par '|' seq",
"seq : basic",
"seq : seq '>' pipeVar '>' basic",
"pipeVar : empty",
"pipeVar : varID",
"pipeVar : '!'",
"pipeVar : '!' varID",
"basic : useID",
"basic : LiteralInteger",
"basic : LiteralString",
"basic : call",
"basic : block",
"basic : hole",
"call : useID '(' args ')'",
"args : empty",
"args : argList",
"argList : expr",
"argList : argList ',' expr",
"block : '{' expr '}'",
"varID : id",
"useID : id",
"id : ID",
"hole : '$' '{' LiteralInteger '}'",
"hole : '@' '{' LiteralInteger '}'",
};

//#line 181 "orc.y"

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
    TheTokens[WHERE]            = "where";
    TheTokens[IN]               = "in";

    /* Single-Character Tokens */
    TheTokens['(']              = "OpenParen";
    TheTokens[')']              = "CloseParen";
    TheTokens['=']              = "Equals";
    TheTokens[',']              = "Comma";
    TheTokens[';']              = "Semicolon";
    TheTokens['>']              = "CloseAngle";
    TheTokens['!']              = "Bang";
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
//#line 470 "OrcParser.java"
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
//#line 42 "orc.y"
{ myOptResult = b.namedTerm("expr",
                                                            (AstroArg)val_peek(0)); }
break;
case 3:
//#line 48 "orc.y"
{ yyval = b.seq((AstroArg)val_peek(1), (AstroArg)val_peek(0)); }
break;
case 4:
//#line 52 "orc.y"
{ yyval = b.namedTerm("expr",
                                                   b.seq((AstroArg)val_peek(1),
                                                         (AstroArg)val_peek(0))); }
break;
case 6:
//#line 59 "orc.y"
{ yyval = b.seq((AstroArg)val_peek(1), (AstroArg)val_peek(0)); }
break;
case 7:
//#line 64 "orc.y"
{ AstroArg params = b.tuple((AstroArg)val_peek(2));
                                  yyval = b.namedTerm("def",
                                                   b.seq((AstroArg)val_peek(3),
                                                         params,
                                                         (AstroArg)val_peek(0))); }
break;
case 9:
//#line 74 "orc.y"
{ yyval = b.empty(); }
break;
case 10:
//#line 75 "orc.y"
{ yyval = val_peek(1); }
break;
case 11:
//#line 79 "orc.y"
{ yyval = b.empty(); }
break;
case 13:
//#line 84 "orc.y"
{ yyval = b.seq((AstroArg)val_peek(2), (AstroArg)val_peek(0)); }
break;
case 15:
//#line 89 "orc.y"
{ yyval = b.namedTerm("where",
                                                   b.seq((AstroArg)val_peek(2), 
                                                         (AstroArg)val_peek(0))); }
break;
case 17:
//#line 96 "orc.y"
{ yyval = b.seq((AstroArg)val_peek(2), (AstroArg)val_peek(0)); }
break;
case 18:
//#line 100 "orc.y"
{ yyval = b.namedTerm("in",
                                                   b.seq((AstroArg)val_peek(2), 
                                                         (AstroArg)val_peek(0))); }
break;
case 20:
//#line 107 "orc.y"
{ yyval = b.namedTerm("par",
                                                   b.seq((AstroArg)val_peek(2), 
                                                         (AstroArg)val_peek(0))); }
break;
case 22:
//#line 114 "orc.y"
{ yyval = b.namedTerm("pipe",
                                                       b.seq((AstroArg)val_peek(4), 
                                                             (AstroArg)val_peek(2),
                                                             (AstroArg)val_peek(0))); }
break;
case 25:
//#line 123 "orc.y"
{ reserved("What does '!' mean?"); }
break;
case 26:
//#line 124 "orc.y"
{ reserved("What does '!' mean?"); }
break;
case 33:
//#line 137 "orc.y"
{ AstroArg args = b.tuple((AstroArg)val_peek(1));
                                  yyval = b.namedTerm("call",
                                                   b.seq((AstroArg)val_peek(3),
                                                         args)); }
break;
case 37:
//#line 150 "orc.y"
{ yyval = b.seq((AstroArg)val_peek(2), (AstroArg)val_peek(0)); }
break;
case 38:
//#line 154 "orc.y"
{ yyval = val_peek(1); }
break;
case 39:
//#line 158 "orc.y"
{ yyval = b.namedTerm("var", (AstroArg)val_peek(0)); }
break;
case 40:
//#line 162 "orc.y"
{ yyval = b.namedTerm("use", (AstroArg)val_peek(0)); }
break;
case 41:
//#line 166 "orc.y"
{ String id = ((Astro)val_peek(0)).getOptArgString(ID);
                                  yyval = b.leafString(id, null); }
break;
case 42:
//#line 172 "orc.y"
{ yyval = b.namedTerm(".DollarHole.",
                                                       (Astro)val_peek(1)); }
break;
case 43:
//#line 175 "orc.y"
{ yyval = b.namedTerm(".AtHole.",
                                                       (Astro)val_peek(1)); }
break;
//#line 731 "OrcParser.java"
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
