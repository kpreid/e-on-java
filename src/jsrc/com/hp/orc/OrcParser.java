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
import org.quasiliteral.term.TermLexer;

import java.io.IOException;
//#line 30 "OrcParser.java"




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
public final static short DEF=259;
public final static short WHERE=260;
public final static short IN=261;
public final static short ID=262;
public final static short INT=263;
public final static short STRING=264;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    1,    1,    3,    3,    5,    6,    6,    6,    2,
    7,    7,    4,    4,    9,    9,   10,    8,    8,   11,
   11,   13,   13,   13,   13,   12,   12,   12,   12,   12,
   12,   14,   17,   17,   18,   18,   15,   16,   16,
};
final static short yylen[] = {                            2,
    1,    1,    2,    1,    2,    5,    1,    2,    3,    0,
    1,    3,    1,    3,    1,    3,    3,    1,    3,    1,
    5,    1,    1,    1,    2,    1,    1,    1,    1,    1,
    1,    4,    1,    1,    1,    3,    3,    4,    4,
};
final static short yydefred[] = {                        10,
    0,    0,    2,    0,    0,   27,   28,    0,    0,    0,
    3,    4,    0,    0,    0,   20,   29,   30,   31,    0,
    0,    0,    0,    0,    5,    0,    0,    0,    0,    7,
    0,   33,   35,    0,    0,   37,    0,    0,    0,    0,
   15,    0,   23,    0,   22,    0,   11,    8,    0,    0,
   32,    0,   38,   39,    0,    0,   25,    0,    9,    0,
    6,   36,    0,   16,   21,   12,
};
final static short yydgoto[] = {                          1,
    2,    3,   11,   12,   13,   31,   49,   14,   40,   41,
   15,   16,   46,   17,   18,   19,   34,   35,
};
final static short yysindex[] = {                         0,
    0,  -31,    0, -258,  -34,    0,    0,  -31, -109, -105,
    0,    0,  -31, -121,  -43,    0,    0,    0,    0,  -19,
  -31, -103, -240, -238,    0, -235,  -28,  -33,  -39,    0,
  -32,    0,    0,  -13,  -14,    0,  -94,  -91, -226,  -21,
    0,  -43,    0, -223,    0,  -22,    0,    0,  -29,  -31,
    0,  -31,    0,    0,  -28, -235,    0,  -28,    0, -221,
    0,    0,  -80,    0,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,   46,    0,    0,    1,    0,    0,    0,    0,    0,
    0,    0,    0,   26,    7,    0,    0,    0,    0,   -9,
    6,    0,    0,    0,    0,    0,    0,  -12,    0,    0,
    0,    0,    0,    0,   17,    0,    0,    0,    0,   32,
    0,   13,    0,   -3,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   20,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    0,  -11,    3,    0,    0,    0,    0,   14,    0,   18,
   48,   22,    0,    0,    0,    0,    0,    0,
};
final static int YYTABLESIZE=296;
final static short yytable[] = {                         44,
   26,   48,   27,   20,    9,   21,   18,    9,   30,   32,
   22,   59,   19,   23,   60,   25,   45,   24,   28,   17,
   29,   36,   37,   33,   38,   13,   39,   51,   50,   52,
   53,   14,   10,   54,   55,   10,   26,   56,   57,   58,
   66,   26,   18,   27,   26,    1,   10,   18,   19,   10,
   18,   10,   61,   19,   62,   17,   19,   34,   24,   26,
   17,   13,   26,   17,   26,   18,   13,   14,   63,   13,
   18,   19,   14,   64,   42,   14,   19,    0,   17,   65,
    0,    0,    0,   17,    0,    0,    0,    0,    0,   13,
    0,    8,    0,    0,    8,   14,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   26,   26,   26,    0,    0,    0,   18,
   18,   18,    0,    0,    0,   19,   19,   19,   26,    0,
    0,    0,   17,    0,   17,    0,    0,    0,   13,    0,
   13,    0,    0,    0,   14,    0,   14,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   47,    0,    0,    0,    0,    4,   43,    0,
    5,    6,    7,    5,    6,    7,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   26,
   26,    0,   26,   26,   26,   18,   18,    0,   18,   18,
   18,   19,   19,    0,   19,   19,   19,    0,   17,    0,
    0,   17,   17,   17,   13,    0,    0,   13,   13,   13,
   14,    0,    0,   14,   14,   14,
};
final static short yycheck[] = {                         33,
    0,   41,  124,  262,   36,   40,    0,   36,   20,   21,
    8,   41,    0,  123,   44,   13,   28,  123,   62,    0,
   40,  125,  263,   21,  263,    0,  262,   41,   61,   44,
  125,    0,   64,  125,  261,   64,   36,   59,  262,   62,
  262,   41,   36,  124,   44,    0,   41,   41,   36,   62,
   44,   61,   50,   41,   52,   36,   44,   41,   62,   59,
   41,   36,   62,   44,   64,   59,   41,   36,   55,   44,
   64,   59,   41,   56,   27,   44,   64,   -1,   59,   58,
   -1,   -1,   -1,   64,   -1,   -1,   -1,   -1,   -1,   64,
   -1,  123,   -1,   -1,  123,   64,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  123,  124,  125,   -1,   -1,   -1,  123,
  124,  125,   -1,   -1,   -1,  123,  124,  125,  260,   -1,
   -1,   -1,  123,   -1,  125,   -1,   -1,   -1,  123,   -1,
  125,   -1,   -1,   -1,  123,   -1,  125,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  262,   -1,   -1,   -1,   -1,  259,  262,   -1,
  262,  263,  264,  262,  263,  264,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  259,
  260,   -1,  262,  263,  264,  259,  260,   -1,  262,  263,
  264,  259,  260,   -1,  262,  263,  264,   -1,  259,   -1,
   -1,  262,  263,  264,  259,   -1,   -1,  262,  263,  264,
  259,   -1,   -1,  262,  263,  264,
};
final static short YYFINAL=1;
final static short YYMAXTOKEN=264;
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
null,null,null,null,null,null,null,"EOL","EOTLU","DEF","WHERE","IN","ID","INT",
"STRING",
};
final static String yyrule[] = {
"$accept : start",
"start : exprs",
"exprs : empty",
"exprs : exprs expr",
"expr : goal",
"expr : def expr",
"def : DEF ID optFormals '=' expr",
"optFormals : empty",
"optFormals : '(' ')'",
"optFormals : '(' idList ')'",
"empty :",
"idList : ID",
"idList : idList ',' ID",
"goal : par",
"goal : par WHERE bindingList",
"bindingList : binding",
"bindingList : bindingList ';' binding",
"binding : ID IN par",
"par : seq",
"par : par '|' seq",
"seq : basic",
"seq : seq '>' pipeVar '>' basic",
"pipeVar : empty",
"pipeVar : ID",
"pipeVar : '!'",
"pipeVar : '!' ID",
"basic : ID",
"basic : INT",
"basic : STRING",
"basic : call",
"basic : block",
"basic : hole",
"call : ID '(' args ')'",
"args : empty",
"args : argList",
"argList : expr",
"argList : argList ',' expr",
"block : '{' expr '}'",
"hole : '$' '{' INT '}'",
"hole : '@' '{' INT '}'",
};

//#line 135 "orc.y"

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
        LexerFace lexer = new TermLexer(lineFeeder,
                                        false,
                                        builder.doesQuasis(),
                                        false,
                                        builder);
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

    /* Single-Character Tokens */
    TheTokens['(']              = "OpenParen";
    TheTokens[')']              = "CloseParen";
    TheTokens['=']              = "Equals";
    TheTokens[',']              = "Comma";
    TheTokens[';']              = "Semicolon";
    TheTokens['>']              = "RightAngle";
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
//#line 447 "OrcParser.java"
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
