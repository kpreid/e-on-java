//### This file created by BYACC 1.8(/Java extension  0.92)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//### Please send bug reports to rjamison@lincom-asg.com
//### static char yysccsid[] = "@(#)yaccpar       1.8 (Berkeley) 01/20/90";



//#line 5 "orc.y"
package com.hp.orc;

import org.erights.e.develop.exception.EBacktraceException;
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
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    1,    1,    3,    4,    4,    6,    8,    8,    8,
    2,    9,    9,    5,    5,   10,   10,   12,   12,   11,
   11,   13,   13,   13,   13,   13,   13,   15,   18,   18,
   19,   19,   16,    7,   14,   20,   17,   17,
};
final static short yylen[] = {                            2,
    1,    1,    2,    2,    1,    2,    5,    1,    2,    3,
    0,    1,    3,    1,    5,    1,    3,    1,    5,    1,
    1,    1,    1,    1,    1,    1,    1,    4,    1,    1,
    1,    3,    3,    1,    1,    1,    4,    4,
};
final static short yydefred[] = {                        11,
    0,    0,    2,    5,    3,    0,   23,   24,   36,    0,
   11,    0,    0,    4,    6,    0,    0,   18,    0,   25,
   26,   27,   35,    0,   34,    0,    0,    0,    0,    0,
    0,   11,    0,    8,    0,   33,    0,    0,   20,   21,
    0,    0,    0,    0,   31,    0,    0,    9,   12,    0,
   11,   37,   38,    0,    0,   28,   11,   10,    0,    7,
   15,   19,   32,   13,
};
final static short yydgoto[] = {                          1,
    2,    4,    5,    6,   14,   15,   40,   35,   50,   16,
   41,   17,   18,   19,   20,   21,   22,   46,   47,   25,
};
final static short yysindex[] = {                         0,
    0,    0,    0,    0,    0,  -26,    0,    0,    0, -256,
    0, -111, -109,    0,    0,  -58,  -47,    0,  -23,    0,
    0,    0,    0,  -22,    0, -105, -238, -237, -256,  -20,
 -256,    0,  -41,    0,  -38,    0, -101, -100,    0,    0,
  -34,  -47,  -32,    0,    0,  -12,  -11,    0,    0,  -35,
    0,    0,    0,  -20,  -20,    0,    0,    0, -256,    0,
    0,    0,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,   19,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   28,    7,    0,    1,    0,
    0,    0,    0,  -29,    0,    0,    0,    0,  -24,    0,
  -28,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   13,    0,  -33,    0,    0,   -2,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    0,   70,   30,    0,  -14,    0,   -6,    0,    0,    0,
   15,   17,   -5,    0,    0,    0,    0,    0,    0,    5,
};
final static int YYTABLESIZE=292;
final static short yytable[] = {                         48,
   22,   29,    5,   24,    9,   58,   16,   29,   59,   12,
   23,   27,   17,   28,   31,   12,   32,   33,    1,   36,
   37,   38,   51,   52,   53,   54,   49,   14,   56,   55,
    5,   11,   57,   11,   23,   11,   22,   13,   30,   61,
   26,   22,   16,   13,   22,   43,   42,   16,   17,   62,
   16,    0,   64,   17,   11,    0,   17,    0,   23,   23,
   22,   45,   22,   14,   22,   30,   16,    0,   14,    3,
   16,   14,   17,    0,    0,    0,   17,    0,    0,    0,
   60,    0,   11,    0,    0,    0,   63,    0,    0,    5,
    0,   14,    0,   34,    0,    0,   11,    0,   39,    0,
   39,   44,   11,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   22,   22,   22,    0,    0,    0,   16,
   16,   16,    0,    0,    0,   17,   17,   17,    0,    0,
    0,   11,    0,    0,    0,    0,    0,    0,    0,    0,
   14,    0,   14,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    9,
    0,    0,    0,    0,    0,    5,    5,    5,    0,    0,
    5,    0,    7,    8,    9,    0,    0,   10,    7,    8,
    9,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   22,
   22,   22,    0,    0,   22,   16,   16,   16,    0,    0,
   16,   17,   17,   17,    0,    0,   17,   11,   11,   11,
    0,    0,   11,    0,    0,    0,   14,   14,   14,    0,
    0,   14,
};
final static short yycheck[] = {                         41,
    0,   60,   36,   10,  261,   41,    0,   41,   44,   36,
    6,  123,    0,  123,   62,   36,   40,   40,    0,  125,
  259,  259,   61,  125,  125,   60,   33,    0,   41,   62,
   64,   61,   44,   62,   30,   60,   36,   64,   41,   54,
   11,   41,   36,   64,   44,   31,   30,   41,   36,   55,
   44,   -1,   59,   41,   36,   -1,   44,   -1,   54,   55,
   60,   32,   62,   36,   64,  124,   60,   -1,   41,    0,
   64,   44,   60,   -1,   -1,   -1,   64,   -1,   -1,   -1,
   51,   -1,   64,   -1,   -1,   -1,   57,   -1,   -1,  123,
   -1,   64,   -1,   24,   -1,   -1,  123,   -1,   29,   -1,
   31,   32,  123,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  123,  124,  125,   -1,   -1,   -1,  123,
  124,  125,   -1,   -1,   -1,  123,  124,  125,   -1,   -1,
   -1,  123,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  123,   -1,  125,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  261,
   -1,   -1,   -1,   -1,   -1,  259,  260,  261,   -1,   -1,
  264,   -1,  259,  260,  261,   -1,   -1,  264,  259,  260,
  261,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  259,
  260,  261,   -1,   -1,  264,  259,  260,  261,   -1,   -1,
  264,  259,  260,  261,   -1,   -1,  264,  259,  260,  261,
   -1,   -1,  264,   -1,   -1,   -1,  259,  260,  261,   -1,
   -1,  264,
};
final static short YYFINAL=1;
final static short YYMAXTOKEN=264;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,"'$'",null,null,null,"'('","')'",null,null,"','",
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
"'<'","'='","'>'",null,"'@'",null,null,null,null,null,null,null,null,null,null,
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
"LiteralString","ID","LiteralFloat64","LiteralChar","DEF",
};
final static String yyrule[] = {
"$accept : start",
"start : exprs",
"exprs : empty",
"exprs : exprs expr",
"expr : defs prune",
"defs : empty",
"defs : defs def",
"def : DEF varID optFormals '=' expr",
"optFormals : empty",
"optFormals : '(' ')'",
"optFormals : '(' idList ')'",
"empty :",
"idList : varID",
"idList : idList ',' varID",
"prune : par",
"prune : par '<' optVar '<' prune",
"par : seq",
"par : par '|' seq",
"seq : basic",
"seq : seq '>' optVar '>' basic",
"optVar : empty",
"optVar : varID",
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

//#line 169 "orc.y"

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
        throw new EBacktraceException(iox, "# parsing a string?!");
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
//#line 455 "OrcParser.java"
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
{ yyval = b.namedTerm("prune",
                                                       b.seq((AstroArg)val_peek(4), 
                                                             (AstroArg)val_peek(2),
							     (AstroArg)val_peek(0))); }
break;
case 17:
//#line 97 "orc.y"
{ yyval = b.namedTerm("par",
                                                       b.seq((AstroArg)val_peek(2), 
                                                             (AstroArg)val_peek(0))); }
break;
case 19:
//#line 104 "orc.y"
{ yyval = b.namedTerm("pipe",
                                                       b.seq((AstroArg)val_peek(4), 
                                                             (AstroArg)val_peek(2),
                                                             (AstroArg)val_peek(0))); }
break;
case 28:
//#line 125 "orc.y"
{ AstroArg args = b.tuple((AstroArg)val_peek(1));
                                      yyval = b.namedTerm("call",
                                                       b.seq((AstroArg)val_peek(3),
                                                             args)); }
break;
case 32:
//#line 138 "orc.y"
{ yyval = b.seq((AstroArg)val_peek(2), (AstroArg)val_peek(0)); }
break;
case 33:
//#line 142 "orc.y"
{ yyval = val_peek(1); }
break;
case 34:
//#line 146 "orc.y"
{ yyval = b.namedTerm("var", (AstroArg)val_peek(0)); }
break;
case 35:
//#line 150 "orc.y"
{ yyval = b.namedTerm("use", (AstroArg)val_peek(0)); }
break;
case 36:
//#line 154 "orc.y"
{ String id = ((Astro)val_peek(0)).getOptArgString(ID);
                                  yyval = b.leafString(id, null); }
break;
case 37:
//#line 160 "orc.y"
{ yyval = b.namedTerm(".DollarHole.",
                                                       (Astro)val_peek(1)); }
break;
case 38:
//#line 163 "orc.y"
{ yyval = b.namedTerm(".AtHole.",
                                                       (Astro)val_peek(1)); }
break;
//#line 699 "OrcParser.java"
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
