// Fixed by EYaccFixer to meet jvm size limits
//### This file created by BYACC 1.8(/Java extension  0.92)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//### Please send bug reports to rjamison@lincom-asg.com
//### static char yysccsid[] = "@(#)yaccpar       1.8 (Berkeley) 01/20/90";



//#line 30 "e.y"
package org.erights.e.elang.syntax;

import org.erights.e.develop.exception.NestedException;
import org.erights.e.develop.exception.PrintStreamWriter;
import org.erights.e.develop.assertion.T;
import org.erights.e.elang.evm.ENode;
import org.erights.e.elang.evm.NounExpr;
import org.erights.e.elang.evm.Pattern;
import org.erights.e.elib.base.ValueThunk;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.tables.IdentityCacheTable;
import org.erights.e.elib.tables.Memoizer;
import org.erights.e.elib.tables.Twine;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.elib.serial.DeepPassByCopy;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroSchema;
import org.quasiliteral.astro.BaseSchema;
import org.quasiliteral.syntax.SyntaxException;
import org.quasiliteral.syntax.LexerFace;
import org.quasiliteral.text.EYaccFixer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
//#line 40 "EParser.java"




//#####################################################################
// class: EParser
// does : encapsulates yacc() parser functionality in a Java
//        class for quick code development
//#####################################################################
public class EParser
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
public final static short LiteralFloat64=260;
public final static short LiteralChar=261;
public final static short LiteralString=262;
public final static short LiteralTwine=263;
public final static short ID=264;
public final static short VerbAssign=265;
public final static short QuasiOpen=266;
public final static short QuasiClose=267;
public final static short DollarIdent=268;
public final static short AtIdent=269;
public final static short DollarOpen=270;
public final static short AtOpen=271;
public final static short URI=272;
public final static short DocComment=273;
public final static short AS=274;
public final static short BIND=275;
public final static short BREAK=276;
public final static short CATCH=277;
public final static short CONTINUE=278;
public final static short DEF=279;
public final static short ELSE=280;
public final static short ESCAPE=281;
public final static short EXIT=282;
public final static short EXTENDS=283;
public final static short FINALLY=284;
public final static short FN=285;
public final static short FOR=286;
public final static short GUARDS=287;
public final static short IF=288;
public final static short IMPLEMENTS=289;
public final static short IN=290;
public final static short INTERFACE=291;
public final static short MATCH=292;
public final static short META=293;
public final static short METHOD=294;
public final static short PRAGMA=295;
public final static short RETURN=296;
public final static short SWITCH=297;
public final static short TO=298;
public final static short TRY=299;
public final static short VAR=300;
public final static short VIA=301;
public final static short WHEN=302;
public final static short WHILE=303;
public final static short _=304;
public final static short ACCUM=305;
public final static short INTO=306;
public final static short MODULE=307;
public final static short ON=308;
public final static short SELECT=309;
public final static short THROWS=310;
public final static short THUNK=311;
public final static short ABSTRACT=312;
public final static short AN=313;
public final static short ASSERT=314;
public final static short ATTRIBUTE=315;
public final static short BE=316;
public final static short BEGIN=317;
public final static short BEHALF=318;
public final static short BELIEF=319;
public final static short BELIEVE=320;
public final static short BELIEVES=321;
public final static short CASE=322;
public final static short CLASS=323;
public final static short CONST=324;
public final static short CONSTRUCTOR=325;
public final static short DATATYPE=326;
public final static short DECLARE=327;
public final static short DEFAULT=328;
public final static short DEFINE=329;
public final static short DEFMACRO=330;
public final static short DELEGATE=331;
public final static short DELICATE=332;
public final static short DEPRECATED=333;
public final static short DISPATCH=334;
public final static short DO=335;
public final static short ENCAPSULATE=336;
public final static short ENCAPSULATED=337;
public final static short ENCAPSULATES=338;
public final static short END=339;
public final static short ENSURE=340;
public final static short ENUM=341;
public final static short EVENTUAL=342;
public final static short EVENTUALLY=343;
public final static short EXPORT=344;
public final static short FACET=345;
public final static short FORALL=346;
public final static short FUN=347;
public final static short FUNCTION=348;
public final static short GIVEN=349;
public final static short HIDDEN=350;
public final static short HIDES=351;
public final static short INLINE=352;
public final static short KNOW=353;
public final static short KNOWS=354;
public final static short LAMBDA=355;
public final static short LET=356;
public final static short METHODS=357;
public final static short NAMESPACE=358;
public final static short NATIVE=359;
public final static short OBEYS=360;
public final static short OCTET=361;
public final static short ONEWAY=362;
public final static short OPERATOR=363;
public final static short PACKAGE=364;
public final static short PRIVATE=365;
public final static short PROTECTED=366;
public final static short PUBLIC=367;
public final static short RAISES=368;
public final static short RELIANCE=369;
public final static short RELIANT=370;
public final static short RELIES=371;
public final static short RELY=372;
public final static short REVEAL=373;
public final static short SAKE=374;
public final static short SIGNED=375;
public final static short STATIC=376;
public final static short STRUCT=377;
public final static short SUCHTHAT=378;
public final static short SUPPORTS=379;
public final static short SUSPECT=380;
public final static short SUSPECTS=381;
public final static short SYNCHRONIZED=382;
public final static short THIS=383;
public final static short TRANSIENT=384;
public final static short TRUNCATABLE=385;
public final static short TYPEDEF=386;
public final static short UNSIGNED=387;
public final static short UNUM=388;
public final static short USES=389;
public final static short USING=390;
public final static short UTF8=391;
public final static short UTF16=392;
public final static short VIRTUAL=393;
public final static short VOLATILE=394;
public final static short WSTRING=395;
public final static short OpLAnd=396;
public final static short OpLOr=397;
public final static short OpSame=398;
public final static short OpNSame=399;
public final static short OpButNot=400;
public final static short OpLeq=401;
public final static short OpABA=402;
public final static short OpGeq=403;
public final static short OpThru=404;
public final static short OpTill=405;
public final static short OpAsl=406;
public final static short OpAsr=407;
public final static short OpFlrDiv=408;
public final static short OpMod=409;
public final static short OpPow=410;
public final static short OpAss=411;
public final static short OpAssAdd=412;
public final static short OpAssAnd=413;
public final static short OpAssAprxDiv=414;
public final static short OpAssFlrDiv=415;
public final static short OpAssAsl=416;
public final static short OpAssAsr=417;
public final static short OpAssRemdr=418;
public final static short OpAssMod=419;
public final static short OpAssMul=420;
public final static short OpAssOr=421;
public final static short OpAssPow=422;
public final static short OpAssSub=423;
public final static short OpAssXor=424;
public final static short Send=425;
public final static short OpWhen=426;
public final static short MapsTo=427;
public final static short MatchBind=428;
public final static short MisMatch=429;
public final static short OpScope=430;
public final static short AssignExpr=431;
public final static short CallExpr=432;
public final static short DefineExpr=433;
public final static short EscapeExpr=434;
public final static short HideExpr=435;
public final static short IfExpr=436;
public final static short LiteralExpr=437;
public final static short NounExpr=438;
public final static short ObjectExpr=439;
public final static short QuasiLiteralExpr=440;
public final static short QuasiPatternExpr=441;
public final static short MetaStateExpr=442;
public final static short MetaContextExpr=443;
public final static short SeqExpr=444;
public final static short SlotExpr=445;
public final static short MetaExpr=446;
public final static short CatchExpr=447;
public final static short FinallyExpr=448;
public final static short FinalPattern=449;
public final static short SlotPattern=450;
public final static short ListPattern=451;
public final static short IgnorePattern=452;
public final static short QuasiLiteralPatt=453;
public final static short QuasiPatternPatt=454;
public final static short EScript=455;
public final static short EMethod=456;
public final static short EMatcher=457;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    0,    0,    5,    5,    5,    2,    6,    6,
    7,    7,    7,    9,    9,    9,    9,    9,    9,    9,
   10,   10,   10,   10,   10,   10,   10,   10,   10,   15,
   15,   15,   12,   12,   20,   20,   21,   21,   21,   21,
   21,   21,   21,   21,   21,   23,   23,   24,   24,   22,
   22,   22,   22,   22,   22,   22,   25,   25,   25,   27,
   27,   27,   28,   28,   28,   29,   29,   29,   29,   29,
   29,   30,   30,   31,   31,   31,   31,   31,   31,   31,
   18,   18,   18,   18,   18,   18,   18,   18,   18,   18,
   33,   33,   33,   33,   33,   33,   33,   33,   33,   33,
   33,   33,   33,   38,   38,   38,   38,   45,   45,   45,
   45,   32,   32,   32,   32,   32,   32,   32,   32,   32,
   32,   32,   32,   32,   32,   32,   32,   32,   60,   60,
   61,   61,   61,   61,   63,   63,   46,   46,   46,   71,
   71,   71,   72,   72,   49,   59,   59,   59,   53,   53,
   57,   57,   74,   47,   47,   48,   48,   76,   76,   77,
   77,   40,   37,   37,   19,   19,   50,   50,   50,   50,
   79,   79,   51,   51,   51,   80,   80,   80,   80,   80,
   73,   73,    4,    4,    4,    4,   81,   81,   81,   81,
   81,   81,   81,   81,   81,   81,   81,   81,   81,   86,
   84,   84,   89,   89,   90,   90,   91,   91,   83,   83,
   83,   82,   82,   82,   82,   82,   16,   16,   17,   17,
   92,   92,   92,   64,   64,   64,   64,   64,   43,   43,
   93,   88,   88,   94,   94,   87,   95,   95,   96,   96,
   96,   96,   96,   96,   97,   97,   98,   98,   62,   62,
   62,   68,   66,   99,   99,  102,  102,  104,  104,  100,
  100,  101,  101,  105,  105,  103,    3,    3,    3,    3,
  106,  106,  107,  107,   70,   70,   70,  109,  110,   26,
   26,   26,   26,   26,   85,   85,   85,   85,   85,  108,
  108,  108,  108,  111,  111,   58,   58,  112,  112,  112,
  113,  113,  113,  113,  114,  114,    1,    1,    8,    8,
  116,   78,   39,   39,   34,   34,   35,   35,   13,   13,
   13,   75,   75,   14,   14,   14,   14,   14,   14,   14,
   14,   14,   14,   14,   14,   14,  118,  118,  118,  118,
  118,  118,  118,  118,  118,  118,  118,  118,  118,   44,
   44,   41,  119,  119,  119,  120,  120,  120,   54,   69,
   69,  122,  122,  123,  121,  121,   55,   55,  115,   52,
   52,  124,   56,   56,   11,   65,   65,   67,   67,  126,
  126,  127,  127,  128,  128,  125,  125,  131,  131,  131,
  129,  129,  132,  132,  133,  133,  133,  130,  130,   36,
   36,   42,   42,   42,   42,   42,   42,   42,   42,   42,
   42,   42,   42,  117,  117,  117,  117,  117,  117,  117,
  117,  117,  117,  117,  117,  117,  117,  117,  117,  117,
  117,  117,  117,  117,  117,  117,  117,  117,  117,  117,
  117,  117,  117,  117,  117,  117,  117,  117,  117,  117,
  117,  117,  117,  117,  117,  117,  117,  117,  117,  117,
  117,  117,  117,  117,  117,  117,  117,  117,  117,  117,
  117,  117,  117,  117,  117,  117,  117,  117,  117,  117,
  117,  117,  117,  117,  117,  117,  117,  117,  117,  117,
  117,  117,  117,  117,  117,  117,  117,
};
final static short yylen[] = {                            2,
    1,    1,    5,    2,    1,    1,    1,    3,    1,    3,
    1,    2,    3,    1,    1,    3,    2,    2,    1,    2,
    1,    2,    3,    3,    3,    4,    3,    3,    6,    1,
    2,    5,    1,    3,    1,    3,    1,    3,    3,    1,
    1,    3,    3,    3,    3,    3,    3,    3,    3,    1,
    3,    3,    3,    3,    3,    3,    1,    3,    3,    1,
    3,    3,    1,    3,    3,    1,    3,    3,    3,    3,
    3,    1,    3,    1,    2,    2,    2,    2,    2,    2,
    1,    3,    3,    3,    4,    4,    3,    4,    5,    5,
    1,    4,    1,    4,    4,    4,    3,    2,    4,    4,
    4,    3,    1,    7,    4,    4,    6,    1,    1,    1,
    1,    1,    1,    1,    2,    1,    3,    3,    1,    4,
    4,    3,    4,    1,    2,    1,    3,    1,    1,    2,
    2,    8,    2,    3,    2,    2,    1,    1,    1,    3,
    2,    2,    3,    2,    3,    3,    5,    5,    1,    3,
    6,    5,    1,    0,    1,    1,    2,    2,    3,    1,
    3,    3,    1,    1,    1,    2,    1,    1,    3,    2,
    1,    3,    1,    3,    2,    3,    3,    4,    4,    4,
    1,    3,    1,    3,    3,    4,    1,    1,    2,    2,
    2,    2,    5,    7,    5,    1,    3,    3,    5,    3,
    1,    2,    2,    3,    1,    1,    1,    5,    1,    3,
    2,    3,    1,    1,    1,    1,    4,    2,    4,    2,
    4,    2,    2,    1,    1,    2,    2,    1,    1,    3,
    1,    1,    3,    1,    3,    3,    1,    3,    3,    5,
    5,    2,    4,    4,    1,    1,    0,    1,    2,    2,
    2,    3,    2,    0,    2,    0,    1,    2,    3,    0,
    2,    0,    1,    2,    3,    1,    1,    1,    2,    2,
    4,    4,    4,    5,    4,    6,    6,    3,    3,    1,
    1,    1,    4,    3,    1,    1,    1,    1,    1,    2,
    0,    4,    2,    1,    3,    6,    6,    6,    3,    1,
    2,    0,    4,    2,    3,    1,    0,    1,    1,    2,
    0,    1,    1,    1,    1,    1,    1,    1,    1,    2,
    2,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    3,
    3,    3,    5,    3,    3,    7,    8,    5,    4,    5,
    1,    1,    3,    1,    1,    3,    1,    1,    2,    0,
    1,    3,    0,    2,    3,    0,    2,    1,    2,    1,
    3,    1,    2,    7,    6,    1,    2,    4,    6,    6,
    2,    3,    1,    3,    2,    2,    2,    0,    2,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,
};
final static short yydefred[] = {                         0,
  309,    0,    0,    0,    2,    0,  322,    0,    0,    0,
    0,  400,  401,    0,    0,    0,  454,  414,  415,  416,
  417,  418,  419,  420,  421,  422,  423,  424,  425,  426,
  427,  428,  429,  430,  431,  432,  433,  434,  435,  436,
  437,  438,  439,  440,  441,  442,  443,  444,  445,  446,
  447,  448,  449,  450,  451,  452,  453,  455,  456,  457,
  458,  459,  460,  461,  462,  463,  464,  465,  466,  467,
  468,  469,  470,  471,  472,  473,  474,  475,  476,  477,
  478,  479,  480,  481,  482,  483,  484,  485,  486,  487,
  488,  489,  490,  491,  492,  493,  494,  495,  496,  497,
    0,    0,    0,  286,  287,  288,    0,    0,  285,  289,
    0,    0,    0,    0,    4,  137,  214,  215,    0,    0,
    0,  138,  139,    0,    0,  187,  188,    0,    0,  216,
  323,  108,  109,  110,  267,  268,  114,    0,    0,    5,
    6,    0,    0,    0,    0,    0,    0,    7,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   11,   14,   19,    0,    0,    0,    0,    0,   35,
    0,    0,    0,    0,    0,    0,    0,   66,    0,    0,
    0,    0,    0,  103,  119,  112,  113,    0,  116,  124,
  126,  128,  129,    0,  310,    0,    0,    0,  319,    0,
    0,    0,    0,  223,    0,    0,    0,    0,    0,  190,
  191,  321,  320,    0,  281,    0,  280,  282,    0,  232,
    0,    0,  141,  142,  144,    0,    0,    0,  201,  189,
    0,    0,  192,    0,   20,  130,    0,    0,    0,    0,
    0,    0,    0,    0,  249,    0,    0,    0,  229,    0,
    0,    0,    0,    0,    0,  225,  224,    0,    0,  311,
    0,    0,  125,    0,    0,    0,    0,    0,  133,    0,
    0,    0,   18,    0,   80,    0,    0,    0,    0,    0,
    0,    0,  167,    0,  173,    0,    0,  269,  270,    0,
   17,    8,    0,    0,    0,    0,    0,  324,  325,  326,
  327,  328,  329,  330,  331,  332,  333,  334,  335,  336,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   98,  411,  402,  403,  404,  405,  409,
  406,  407,  410,  408,  413,    0,  412,    0,  156,  115,
    0,    0,    0,    0,    0,  131,    0,    0,    0,  140,
  143,    0,    0,    0,    0,    0,  185,    0,  250,    0,
  251,    0,    0,    0,    0,  234,  246,  245,    0,    0,
  237,    0,    0,  200,    0,    0,  160,  207,    0,    0,
  205,  203,  206,    0,  202,  184,  198,    0,    0,    0,
    0,    0,    0,    0,  134,    0,    0,    0,    0,  226,
  227,    0,    0,    0,    0,    0,  375,    0,    0,  122,
    0,  368,  367,    0,    0,    0,    0,    0,    0,    0,
    0,  352,    0,  127,  145,    0,    0,  117,    0,  118,
    0,  350,  351,   16,    0,   13,    0,   30,   25,    0,
   23,   24,   27,   28,    0,    0,   83,    0,   97,    0,
    0,    0,    0,   84,  317,    0,   82,    0,  171,  164,
    0,  163,    0,   36,   38,   39,   43,   44,   45,   42,
   46,   48,   47,   49,    0,   52,   53,   54,   51,   55,
    0,    0,    0,    0,    0,    0,   69,   71,   67,   68,
   70,   73,    0,    0,    0,    0,  102,  313,   87,    0,
    0,    0,    0,    0,  158,    0,  157,  266,  255,    0,
    0,    0,    0,    0,  135,  361,  136,    0,    0,    0,
    0,    0,    0,    0,    0,  284,    0,    0,    0,  233,
    0,  236,    0,    0,  186,    0,    0,  204,    0,   26,
    0,  120,  371,  230,  182,  153,    0,  150,    0,  377,
    0,    0,    0,    0,    0,    0,    0,    0,  387,  311,
    0,  123,  369,    0,    0,  121,    0,    0,    0,    3,
    0,    0,    0,  177,  176,    0,    0,  174,    0,   31,
    0,    0,    0,   88,   96,   86,   85,   94,   95,    0,
  105,    0,  101,  162,   99,  100,    0,  106,  159,    0,
    0,    0,    0,  311,  261,    0,  252,    0,  193,    0,
  195,  283,    0,    0,  235,  238,    0,  199,  161,    0,
    0,    0,    0,  148,  147,    0,    0,    0,    0,  391,
    0,  393,    0,    0,  258,    0,  253,    0,  365,    0,
  374,    0,  152,    0,    0,  354,  355,  180,  179,  178,
    0,   90,   89,  172,    0,    0,    0,    0,    0,    0,
  275,    0,  279,  362,    0,  264,    0,    0,  244,  243,
    0,    0,    0,   29,  372,  151,    0,    0,  396,    0,
  395,    0,  392,  388,    0,    0,  259,  359,    0,    0,
    0,    0,    0,    0,  107,    0,    0,    0,    0,    0,
    0,  248,    0,    0,    0,    0,  265,  194,  241,  240,
  208,    0,    0,  394,    0,    0,    0,  378,    0,  380,
    0,  366,    0,  297,    0,  296,  300,  353,    0,    0,
   32,  104,  276,    0,    0,  278,  277,    0,    0,  363,
  360,  389,  390,  132,    0,  379,    0,  383,    0,    0,
    0,    0,    0,    0,  341,  342,  340,  344,  347,  349,
  338,  346,  337,  348,  345,  339,  343,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  381,  305,
    0,    0,    0,    0,  299,    0,    0,  358,    0,    0,
  272,  271,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  356,  273,    0,  385,    0,
  298,  357,  274,  384,
};
final static short yydgoto[] = {                          3,
  257,  499,  168,  260,  169,  170,  171,    6,  172,  173,
  174,  175,  116,  321,  479,  117,  118,  178,  500,  179,
  180,  181,  182,  183,  184,  737,  185,  186,  187,  188,
  189,  190,  191,  487,  494,  192,  501,  193,  814,  364,
  194,  376,  258,  195,  196,  197,  198,  380,  199,  291,
  292,  582,  263,  450,  451,  602,  200,  273,  201,  202,
  203,  204,  386,  255,  446,  596,  756,  387,  555,  388,
  122,  123,  261,  587,  124,  381,  421,  259,  503,  295,
  125,  126,  127,  240,  128,  129,  231,  232,  241,  422,
  423,  130,  433,  409,  410,  411,  412,  757,  389,  559,
  647,  597,  549,  598,  648,  744,  815,  700,  701,  729,
  738,  766,  794,  767,  452,  453,  131,  809,  462,  686,
  680,  705,  746,  583,  447,  759,  760,  761,  593,  719,
  448,  671,  672,
};
final static short yysindex[] = {                      -162,
    0,14844,    0,10155,    0, -168,    0, -130,  -59,13468,
15984,    0,    0,15984,  130,  263,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
15984,13187,13187,    0,    0,    0,17277,15984,    0,    0,
15851,   93,  -19,  124,    0,    0,    0,    0,  130,  355,
 -167,    0,    0,    0,  366,    0,    0,13187,  418,    0,
    0,    0,    0,    0,    0,    0,    0,  626,13468,    0,
    0,14198,14844,   93,14844,  130, 8031,    0,  130,  344,
15984,14708,  130,12685,  113,  130,  344,12685,   93,11312,
12685,13187,12685,12685,12685,   93,   93,  129,11676,   93,
  424,    0,    0,    0, 1231,  102,  119,  -16,   96,    0,
  453,  497,  419,  135,  297,  147,   77,    0,  122,  268,
  524,  -26,17143,    0,    0,    0,    0,  234,    0,    0,
    0,    0,    0,  175,    0,  437,  448,  520,    0,  288,
  528,14844,15851,    0,  643,13468, 8031, 8031,15984,    0,
    0,    0,    0,  530,    0,  -84,    0,    0,12823,    0,
  501,  504,    0,    0,    0,  173,15851,  486,    0,    0,
  305,13187,    0,15213,    0,    0,  520,13468,15984,  263,
  129, -119,    0,  355,    0,  344,14844,  344,    0,  197,
  300,  201,  344,13468,15984,    0,    0,   95,  509,    0,
  528,   93,    0,  359,  344,  478,  174,  509,    0,   -3,
11169,  597,    0,   -3,    0,   -3,   -3,   -3,10298,  214,
  553,   57,    0,  612,    0,10662,  543,    0,    0,   -4,
    0,    0,10805,11169,11819,12183,11312,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
11312,11312,11312,13979,16473,15851,14993,   93,12183,12183,
12183,12183,14844,14844,12183,12183,12183,12183,12183,12183,
12183,12183,12183,12183,12183,12183,12183,12183,12183,12183,
12183,12183,12183,12183,12183,12183,   93,   93,16743,14993,
   93,14993,   93,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   -5,    0,  183,    0,    0,
  309,12183,14993,   93,14993,    0,   26,  344,  400,    0,
    0,15851,   93,14993,   93,15851,    0,  -84,    0,  394,
    0,15851,14993,   93,15584,    0,    0,    0,   52,   59,
    0,  258,  565,    0,14844,  -84,    0,    0,   93,   93,
    0,    0,    0,  486,    0,    0,    0,  520,  528,12685,
11312,  438,   93,  662,    0,14844,12183,14844,  439,    0,
    0,14844,14993,   93,14993,  434,    0,  344,   93,    0,
 -192,    0,    0,11169,    0,  682,12183,  438,14844,  130,
  130,    0,   93,    0,    0,15717,   93,    0,   93,    0,
   93,    0,    0,    0,  424,    0,   21,    0,    0,   96,
    0,    0,    0,    0,16608,  129,    0,  686,    0,    0,
14993,14993,  129,    0,    0,  -84,    0,  686,    0,    0,
  634,    0,  687,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   -3,    0,    0,    0,    0,    0,
  200,  200,  147,  147,   77,   77,    0,    0,    0,    0,
    0,    0,  344,  700,  129,  686,    0,    0,    0,  701,
  686,  653,   93,  344,    0,  183,    0,    0,    0,  723,
  729,  727,14844,   93,    0,    0,    0,12183,  485,  -84,
  735,  737,  695,  -84,  -84,    0,  697, -267,14844,    0,
  260,    0,14844,15213,    0,  667,14844,    0,   32,    0,
14844,    0,    0,    0,    0,    0,  344,    0,  -46,    0,
  754,16338,  759,  766,12183,  685,  485,  767,    0,    0,
  344,    0,    0,   93,  344,    0,  523,  692,  692,    0,
13468,15984,15984,    0,    0,10298,  214,    0,    0,    0,
   66,14993,14993,    0,    0,    0,    0,    0,    0,   93,
    0,17143,    0,    0,    0,    0,  769,    0,    0,   93,
  -50,   93,  344,    0,    0,12183,    0,  772,    0,   93,
    0,    0,12183,12183,    0,    0, -246,    0,    0,   93,
11312,  344,  438,    0,    0,   93,  771,15851,  771,    0,
  104,    0,  771,   93,    0,   93,    0,12183,    0,    7,
    0,  393,    0,12183,   93,    0,    0,    0,    0,    0,
   93,    0,    0,    0,   93,  344,  783,15851,15851,  400,
    0,  790,    0,    0,  560,    0,12183,  795,    0,    0,
12183,12183,  712,    0,    0,    0,  798,15851,    0,  -84,
    0,16338,    0,    0,  799,  560,    0,    0,   93,15355,
  692,  408,  801,  344,    0,  -50,  -84,  803,  -72,  485,
  -50,    0,  125,   93,  558,  733,    0,    0,    0,    0,
    0,  771,  -84,    0,  771,  734,  570,    0,   93,    0,
  344,    0,    0,    0,  -30,    0,    0,    0,  893,   93,
    0,    0,    0,15851,15851,    0,    0,16877,16877,    0,
    0,    0,    0,    0,17011,    0, -140,    0,  438,  587,
15851,   93,15851,  344,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,14993,11312,  748,
  -84,  803,   93,  837,  344,  344,   93,  839,    0,    0,
  803,14844,  833,  -62,    0,  686,   93,    0,  846,   93,
    0,    0,  853,   93,  -25,15851,   93,  755,  -50,  854,
  771,  861,  344,  803,  781,    0,    0,  -50,    0,  771,
    0,    0,    0,    0,
};
final static short yyrindex[] = {                      1565,
    0,  387,    0,   31,    0, 1165,    0,    0,    0,    0,
    0,    0,    0,    0,    0, 4402,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  387,  387,    0,    0,    0,    0,    0,    0,    0,
    0,12321,    0,    0,    0,    0,    0,    0,    0, 6661,
    0,    0,    0, 1724, 7658,    0,    0,  387, 6703,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  387,  387,13319,  387,    0,    0,    0,    0,    0,
    0,  387,    0,  387,    0,    0,    0,  387, 9791,  387,
  387,  387,  387,  387,  387, 8216, 9105, 2427,   53,   11,
  139,    0,    0,    0,    5,    0,    0, 4619,  929,    0,
 1322, 2118, 2557, 6444,  761,  971, 5579,    0, 5403, 3479,
 4445,    0, 3743,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  307,    0,    0,    0, 6761,    0,    0,
 6878,  387,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0, 6936,    0, 6978,    0,    0,  -21,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  387,    0,  387,    0,    0,  858,    0,    0,  851,
  749,    0, 3571,  997,    0,    0,  348,    0,    0,  602,
    0,  784,    0,    0,    0,    0,    0,  374,    0,    0,
  943, 9266,    0,    0,    0,    0,    0,    0,    0, 4708,
  387,    0,    0, 4881,    0, 4951, 5134, 5345,  -21,  333,
    0,    0,    0,    0,    0,  387,    0,    0,    0, 9791,
    0,    0,  131,  834,  387,  387,  387,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  387,  387,  387,    0,    0,    0,    0, 9630,  387,  387,
  387,  387,  387,  387,  387,  387,  387,  387,  387,  387,
  387,  387,  387,  387,  387,  387,  387,  387,  387,  387,
  387,  387,  387,  387,  387,  387,13319, 9266,    0,    0,
 9266,    0, 9630,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,13319,    0,    0,    0,    0,
    0,  387,    0,13688,    0,    0,    0,    0,  -55,    0,
    0,    0,13688,    0,13829,    0,    0, 7146,    0,  374,
    0,    0,    0, 9630,    0,    0,    0,    0,   50,  816,
    0,    0, 7186,    0,  387, 7221,    0,    0, 9791,14339,
    0,    0,    0,    0,    0,    0,    0, 1023, 2043,  387,
  387, 2602,  324,   -7,    0,  387,  387,  387, 2866,    0,
    0,  387,    0,16206,    0,   43,    0,  249,  111,    0,
 3041,    0,    0,   -9,  325,    0,  387, 2602,  387,    0,
    0,    0, 9791,    0,    0,    0, 9791,    0, 7852,    0,
 8580,    0,    0,    0,  261,    0, 9791,    0,    0, 2996,
    0,    0,    0,    0,    0, 3918,    0,    0,    0, 4007,
    0,    0, 3304,    0,    0, 6483,    0,    0,    0,    0,
    0,    0,   86,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0, 4619,    0,    0,    0,    0,    0,
 6230, 6269, 6013, 6052, 5796, 5835,    0,    0,    0,    0,
    0,    0,    0,    0,  872,    0,    0,    0,    0,    0,
    0,    0, 9266,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  387,  276,    0,    0,    0,  387,   35, 7404,
    0,    0,    0, 7454, 7622,    0,    0,   27,  387,    0,
    0,    0,  387,  387,    0,    0,  387,    0,    0,    0,
  387,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  874,    0,    0,  387,    0,  796,   46,    0,    0,
    0,    0,    0,  487,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  387,    0,    0,  464,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0, 8741,
    0, 4182,    0,    0,    0,    0,    0,    0,    0,13688,
  257,13688,    0,    0,    0,  387,    0,   41,    0,13688,
    0,    0,  387,  387,    0,    0,   49,    0,    0,  817,
  387,    0, 2602,    0,    0,16206,    6,    0,    6,    0,
  879,    0,   15,16206,    0,  -80,    0,  387,    0,    0,
    0,    0,    0,  387,  494,    0,    0,    0,    0,    0,
 9791,    0,    0,    0,13319,    0,    0,    0,    0,   81,
    0,    0,    0,    0,  455,    0,  387,    0,    0,    0,
  387,  387,    0,    0,    0,    0,    0,    0,    0,   13,
    0,    0,    0,    0,    0,  -89,    0,    0,  111,    0,
    0,    0,    0,    0,    0,  257,   78,  321,  337,  796,
  257,    0,    0,  276,  819,    0,    0,    0,    0,    0,
    0,   15,   22,    0,   15,    0,    0,    0,  817,    0,
  -81,    0, 1988,    0,  800,    0,    0,    0,    0,  817,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  -67,    0,    0, 2163,
    0,13688,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  387,    0,
  166,  356,13688,    0,    0,    0,16206,    0,    0,    0,
  818,   28,    0,  823,    0,    0,  817,    0,    0,13688,
    0,    0,    0,16206,  800,    0,  817,    0,  824,    0,
  436,    0,    0,  825,    0,    0,    0,  824,    0,  436,
    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    1,   40,  631,  206,    0,    0,  621, -166,  646, -109,
  814,    0,   17,    0,    0,   34,  429, 2011,  262,  651,
 -303, 1636,    0,    0,  522,   -2,  313,  351,  388,  726,
  604,  562,    0,  635,  222,   74,  141,    0, -257, -311,
    0,  329,  172,  377, -226,   10,   39,    0,  158,  691,
    0, -419, -135,  689,  202,  178,    0,  239,  383,    0,
   67,    0,    0, -145,    0,    0,    0,    0,    0,    0,
    0,    0, -146, -441,  277,    0, -291,  -87,  -83,  508,
 -221,  574,    0,    0,    0,    0,    0,  188,    0,  568,
    0,    0,    0,  739,    0,  413,    0,  281,    0,  298,
 -377,    0,  163,    0,    0,    0,  218, -488, -254,  613,
 -685,    0,  168, -669,  238,   91, -193,    0,  272, -529,
  301,    0,    0, -442,    0,    0,  221,    0, -200,   75,
    0,    0,  283,
};
final static int YYTABLESIZE=17672;

//These two tables are not statically initialized, but rather
//initialized on first use, so that a failure to initialize
//them can successfully report the problem.
static private short[] yytable = null;
static private short[] yycheck = null;
/** Ensures that yytable and yycheck are initialized. */
static private void initTables() {
    if (null != yycheck) {
        return;
    }
    try {
        String rName = "org/erights/e/elang/syntax/ParserTables.data";
        InputStream inp = 
          ClassLoader.getSystemResourceAsStream(rName);
        if (null == inp) {
            T.fail(rName + " not found");
        }
        ObjectInput obInp = new ObjectInputStream(inp);
        yytable = (short[])obInp.readObject();
        yycheck = (short[])obInp.readObject();
        long hash = EYaccFixer.checkhash(yytable, yycheck);
        if (hash != 6131933992676602845L) {
            T.fail(rName + " bad checkhash: " +
                                       hash);
        }
    } catch (Exception ex) {
        throw new NestedException(ex, "# initing parser");
    }
}

final static short YYFINAL=3;
final static short YYMAXTOKEN=457;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,"'!'",null,null,"'$'","'%'","'&'",null,"'('","')'","'*'","'+'",
"','","'-'","'.'","'/'",null,null,null,null,null,null,null,null,null,null,"':'",
"';'","'<'",null,"'>'","'?'","'@'",null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,"'['",null,"']'","'^'",null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,"'{'","'|'","'}'","'~'",null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,"EOL","EOTLU","LiteralInteger",
"LiteralFloat64","LiteralChar","LiteralString","LiteralTwine","ID","VerbAssign",
"QuasiOpen","QuasiClose","DollarIdent","AtIdent","DollarOpen","AtOpen","URI",
"DocComment","AS","BIND","BREAK","CATCH","CONTINUE","DEF","ELSE","ESCAPE",
"EXIT","EXTENDS","FINALLY","FN","FOR","GUARDS","IF","IMPLEMENTS","IN",
"INTERFACE","MATCH","META","METHOD","PRAGMA","RETURN","SWITCH","TO","TRY","VAR",
"VIA","WHEN","WHILE","_","ACCUM","INTO","MODULE","ON","SELECT","THROWS","THUNK",
"ABSTRACT","AN","ASSERT","ATTRIBUTE","BE","BEGIN","BEHALF","BELIEF","BELIEVE",
"BELIEVES","CASE","CLASS","CONST","CONSTRUCTOR","DATATYPE","DECLARE","DEFAULT",
"DEFINE","DEFMACRO","DELEGATE","DELICATE","DEPRECATED","DISPATCH","DO",
"ENCAPSULATE","ENCAPSULATED","ENCAPSULATES","END","ENSURE","ENUM","EVENTUAL",
"EVENTUALLY","EXPORT","FACET","FORALL","FUN","FUNCTION","GIVEN","HIDDEN",
"HIDES","INLINE","KNOW","KNOWS","LAMBDA","LET","METHODS","NAMESPACE","NATIVE",
"OBEYS","OCTET","ONEWAY","OPERATOR","PACKAGE","PRIVATE","PROTECTED","PUBLIC",
"RAISES","RELIANCE","RELIANT","RELIES","RELY","REVEAL","SAKE","SIGNED","STATIC",
"STRUCT","SUCHTHAT","SUPPORTS","SUSPECT","SUSPECTS","SYNCHRONIZED","THIS",
"TRANSIENT","TRUNCATABLE","TYPEDEF","UNSIGNED","UNUM","USES","USING","UTF8",
"UTF16","VIRTUAL","VOLATILE","WSTRING","OpLAnd","OpLOr","OpSame","OpNSame",
"OpButNot","OpLeq","OpABA","OpGeq","OpThru","OpTill","OpAsl","OpAsr","OpFlrDiv",
"OpMod","OpPow","OpAss","OpAssAdd","OpAssAnd","OpAssAprxDiv","OpAssFlrDiv",
"OpAssAsl","OpAssAsr","OpAssRemdr","OpAssMod","OpAssMul","OpAssOr","OpAssPow",
"OpAssSub","OpAssXor","Send","OpWhen","MapsTo","MatchBind","MisMatch","OpScope",
"AssignExpr","CallExpr","DefineExpr","EscapeExpr","HideExpr","IfExpr",
"LiteralExpr","NounExpr","ObjectExpr","QuasiLiteralExpr","QuasiPatternExpr",
"MetaStateExpr","MetaContextExpr","SeqExpr","SlotExpr","MetaExpr","CatchExpr",
"FinallyExpr","FinalPattern","SlotPattern","ListPattern","IgnorePattern",
"QuasiLiteralPatt","QuasiPatternPatt","EScript","EMethod","EMatcher",
};
final static String yyrule[] = {
"$accept : start",
"start : br",
"start : eExpr",
"start : br MODULE litString EOL eExpr",
"start : MatchBind pattern",
"ejector : BREAK",
"ejector : CONTINUE",
"ejector : RETURN",
"eExpr : br seqs br",
"seqs : seq",
"seqs : seqs EOLs seq",
"seq : chunk",
"seq : seq ';'",
"seq : seq ';' chunk",
"chunk : assign",
"chunk : ejector",
"chunk : ejector '(' ')'",
"chunk : ejector assign",
"chunk : '^' assign",
"chunk : oFuncType",
"chunk : DocComment oFuncType",
"assign : cond",
"assign : DEF noun",
"assign : cond OpAss assign",
"assign : cond assignop assign",
"assign : cond VerbAssign nAssign",
"assign : DEF pattern OpAss assign",
"assign : bindNamer OpAss assign",
"assign : varNamer OpAss assign",
"assign : DEF pattern EXIT postfix OpAss assign",
"nAssign : assign",
"nAssign : '(' ')'",
"nAssign : '(' eExpr ',' args ')'",
"cond : condAnd",
"cond : cond OpLOr condAnd",
"condAnd : comp",
"condAnd : condAnd OpLAnd comp",
"comp : order",
"comp : order OpSame order",
"comp : order OpNSame order",
"comp : conjunction",
"comp : disjunction",
"comp : order '^' order",
"comp : order OpButNot order",
"comp : order MatchBind pattern",
"comp : order MisMatch pattern",
"conjunction : order '&' order",
"conjunction : conjunction '&' order",
"disjunction : order '|' order",
"disjunction : disjunction '|' order",
"order : interval",
"order : interval '<' interval",
"order : interval OpLeq interval",
"order : interval OpABA interval",
"order : interval OpGeq interval",
"order : interval '>' interval",
"order : postfix ':' guard",
"interval : shift",
"interval : shift OpThru shift",
"interval : shift OpTill shift",
"shift : add",
"shift : shift OpAsl add",
"shift : shift OpAsr add",
"add : mult",
"add : add '+' mult",
"add : add '-' mult",
"mult : pow",
"mult : mult '*' pow",
"mult : mult '/' pow",
"mult : mult OpFlrDiv pow",
"mult : mult '%' pow",
"mult : mult OpMod pow",
"pow : prefix",
"pow : prefix OpPow prefix",
"prefix : postfix",
"prefix : '!' postfix",
"prefix : '~' postfix",
"prefix : '&' postfix",
"prefix : OpLAnd postfix",
"prefix : '*' postfix",
"prefix : '-' prim",
"postfix : call",
"postfix : postfix '.' vcurry",
"postfix : postfix Send vcurry",
"postfix : postfix OpScope prop",
"postfix : postfix OpScope '&' prop",
"postfix : postfix OpScope OpLAnd prop",
"postfix : metaoid OpScope prop",
"postfix : postfix Send OpScope prop",
"postfix : postfix Send OpScope '&' prop",
"postfix : postfix Send OpScope OpLAnd prop",
"call : prim",
"call : call '(' argList ')'",
"call : control",
"call : postfix '.' verb parenArgs",
"call : postfix '[' argList ']'",
"call : postfix Send verb parenArgs",
"call : postfix Send parenArgs",
"call : metaoid parenArgs",
"call : metaoid '.' verb parenArgs",
"call : metaoid '[' argList ']'",
"call : metaoid Send verb parenArgs",
"call : metaoid Send parenArgs",
"call : accumExpr",
"control : call '(' argList ')' sepWord paramList body",
"control : prim FN paramList body",
"control : control sepWord paramList body",
"control : control sepWord '(' argList ')' body",
"literal : LiteralInteger",
"literal : LiteralFloat64",
"literal : LiteralChar",
"literal : litString",
"prim : literal",
"prim : nounExpr",
"prim : URI",
"prim : quasiParser quasiExpr",
"prim : parenExpr",
"prim : '[' exprList ']'",
"prim : '[' maps ']'",
"prim : body",
"prim : ESCAPE pattern body optHandler",
"prim : WHILE test body optHandler",
"prim : SWITCH parenExpr caseList",
"prim : TRY body catchList finallyClause",
"prim : forExpr",
"prim : WHEN whenRest",
"prim : ifExpr",
"prim : SELECT parenExpr caseList",
"prim : docodef",
"docodef : postdocodef",
"docodef : DocComment postdocodef",
"postdocodef : defName script",
"postdocodef : INTERFACE oName guards iDeclTail '{' br mTypeList '}'",
"postdocodef : THUNK body",
"postdocodef : FN paramList body",
"script : oDeclTail vTable",
"script : funcHead body",
"nounExpr : noun",
"nounExpr : dollarHole",
"nounExpr : atHole",
"dollarHole : DollarOpen LiteralInteger '}'",
"dollarHole : '$' LiteralInteger",
"dollarHole : '$' '$'",
"atHole : AtOpen LiteralInteger '}'",
"atHole : '@' LiteralInteger",
"parenExpr : '(' eExpr ')'",
"ifExpr : IF test body",
"ifExpr : IF test body ELSE ifExpr",
"ifExpr : IF test body ELSE body",
"test : parenExpr",
"test : parenExpr MatchBind pattern",
"forExpr : FOR iterPattern IN iterable body optHandler",
"forExpr : WHEN iterPattern IN iterable body",
"iterable : comp",
"quasiParser :",
"quasiParser : ident",
"quasiExpr : QuasiClose",
"quasiExpr : innerExprs QuasiClose",
"innerExprs : QuasiOpen innerExpr",
"innerExprs : innerExprs QuasiOpen innerExpr",
"innerExpr : DollarIdent",
"innerExpr : DollarOpen eExpr '}'",
"parenArgs : '(' argList ')'",
"argList : emptyBr",
"argList : args",
"args : exprs",
"args : exprs ','",
"exprList : emptyBr",
"exprList : eExpr",
"exprList : exprs ',' eExpr",
"exprList : exprs ','",
"exprs : eExpr",
"exprs : exprs ',' eExpr",
"maps : map",
"maps : maps ',' map",
"maps : maps ','",
"map : eExpr MapsTo eExpr",
"map : br MapsTo nounExpr",
"map : br MapsTo '&' nounExpr",
"map : br MapsTo OpLAnd nounExpr",
"map : br MapsTo DEF noun",
"iterPattern : pattern",
"iterPattern : pattern MapsTo pattern",
"pattern : subPattern",
"pattern : subPattern '?' prim",
"pattern : VIA parenExpr pattern",
"pattern : metaoid parenExpr MapsTo pattern",
"subPattern : namer",
"subPattern : ignorer",
"subPattern : quasiParser quasiPattern",
"subPattern : OpSame prim",
"subPattern : OpNSame prim",
"subPattern : compOp prim",
"subPattern : IN nounExpr '(' paramList ')'",
"subPattern : IN nounExpr '.' verb '(' paramList ')'",
"subPattern : IN nounExpr '[' paramList ']'",
"subPattern : listPattern",
"subPattern : '[' mapPatternList ']'",
"subPattern : listPattern '+' subPattern",
"subPattern : '[' mapPatternList ']' '|' subPattern",
"listPattern : '[' patternList ']'",
"quasiPattern : QuasiClose",
"quasiPattern : innerThings QuasiClose",
"innerThings : QuasiOpen innerThing",
"innerThings : innerThings QuasiOpen innerThing",
"innerThing : innerExpr",
"innerThing : innerPattern",
"innerPattern : AtIdent",
"innerPattern : AtOpen br pattern br '}'",
"ignorer : _",
"ignorer : _ ':' guard",
"ignorer : ':' guard",
"namer : nounExpr ':' guard",
"namer : nounExpr",
"namer : bindNamer",
"namer : varNamer",
"namer : slotNamer",
"bindNamer : BIND noun ':' guard",
"bindNamer : BIND noun",
"varNamer : VAR nounExpr ':' guard",
"varNamer : VAR nounExpr",
"slotNamer : '&' nounExpr ':' guard",
"slotNamer : '&' nounExpr",
"slotNamer : OpLAnd nounExpr",
"oName : nounExpr",
"oName : _",
"oName : BIND noun",
"oName : VAR nounExpr",
"oName : litString",
"paramList : emptyBr",
"paramList : br params br",
"params : patterns",
"patternList : emptyBr",
"patternList : br patterns br",
"patterns : pattern",
"patterns : patterns ',' pattern",
"mapPatternList : br mapPatterns br",
"mapPatterns : mapPattern",
"mapPatterns : mapPatterns ',' mapPattern",
"mapPattern : key MapsTo pattern",
"mapPattern : key MapsTo pattern OpAss order",
"mapPattern : key MapsTo pattern DEFAULT order",
"mapPattern : MapsTo namer",
"mapPattern : MapsTo namer OpAss order",
"mapPattern : MapsTo namer DEFAULT order",
"key : parenExpr",
"key : literal",
"doco :",
"doco : DocComment",
"defName : DEF oName",
"defName : BIND noun",
"defName : VAR nounExpr",
"oDeclTail : extends optAs impls",
"iDeclTail : iExtends impls",
"extends :",
"extends : EXTENDS base",
"iExtends :",
"iExtends : iExtendsList",
"iExtendsList : EXTENDS base",
"iExtendsList : iExtendsList ',' base",
"optAs :",
"optAs : AS base",
"impls :",
"impls : implsList",
"implsList : IMPLEMENTS base",
"implsList : implsList ',' base",
"base : order",
"litString : LiteralString",
"litString : LiteralTwine",
"litString : litString LiteralString",
"litString : litString LiteralTwine",
"method : doco TO methHead body",
"method : doco METHOD methHead body",
"methHead : '(' paramList ')' resultGuard",
"methHead : verb '(' paramList ')' resultGuard",
"funcHead : '(' paramList ')' funcNeck",
"funcHead : TO verb '(' paramList ')' funcNeck",
"funcHead : '.' verb '(' paramList ')' funcNeck",
"funcNeck : resultGuard optAs impls",
"matcher : MATCH pattern body",
"guard : nounExpr",
"guard : URI",
"guard : parenExpr",
"guard : guard '[' argList ']'",
"guard : guard OpScope prop",
"compOp : '<'",
"compOp : OpLeq",
"compOp : OpABA",
"compOp : OpGeq",
"compOp : '>'",
"resultGuard : ':' guard",
"resultGuard :",
"resultGuard : ':' guard THROWS throws",
"resultGuard : THROWS throws",
"throws : guard",
"throws : throws ',' guard",
"whenRest : '(' exprList ')' br OpWhen whenTail",
"whenRest : '(' exprList ')' br OpWhen whenRest",
"whenTail : oName '(' patternList ')' whenGuard whenBody",
"whenTail : oName whenGuard whenBody",
"whenTail : whenBody",
"whenGuard : ':' guard",
"whenGuard :",
"whenGuard : ':' guard THROWS throws",
"whenGuard : THROWS throws",
"whenBody : body catches finallyClause",
"whenBody : body",
"br :",
"br : EOLs",
"EOLs : EOL",
"EOLs : EOLs EOL",
"emptyList :",
"emptyBr : br",
"verb : ident",
"verb : litString",
"vcurry : ident",
"vcurry : litString",
"prop : ident",
"prop : litString",
"noun : ident",
"noun : OpScope ident",
"noun : OpScope LiteralString",
"ident : ID",
"ident : reserved",
"assignop : OpAssAdd",
"assignop : OpAssAnd",
"assignop : OpAssAprxDiv",
"assignop : OpAssFlrDiv",
"assignop : OpAssAsl",
"assignop : OpAssAsr",
"assignop : OpAssRemdr",
"assignop : OpAssMod",
"assignop : OpAssMul",
"assignop : OpAssOr",
"assignop : OpAssPow",
"assignop : OpAssSub",
"assignop : OpAssXor",
"assignableop : '+'",
"assignableop : '&'",
"assignableop : '/'",
"assignableop : OpFlrDiv",
"assignableop : OpAsl",
"assignableop : OpAsr",
"assignableop : '%'",
"assignableop : OpMod",
"assignableop : '*'",
"assignableop : '|'",
"assignableop : OpPow",
"assignableop : '-'",
"assignableop : '^'",
"body : '{' br '}'",
"body : '{' eExpr '}'",
"accumExpr : ACCUM postfix accumulator",
"accumulator : FOR iterPattern IN iterable accumBody",
"accumulator : IF test accumBody",
"accumulator : WHILE test accumBody",
"accumBody : '{' br _ assignableop assign br '}'",
"accumBody : '{' br _ '.' verb parenArgs br '}'",
"accumBody : '{' br accumulator br '}'",
"caseList : '{' br matchList '}'",
"vTable : '{' br methodList vMatchList '}'",
"vTable : matcher",
"methodList : emptyList",
"methodList : methodList method br",
"vMatchList : matchList",
"matchList : emptyList",
"matchList : matchList matcher br",
"catchList : emptyList",
"catchList : catches",
"catches : catchList catcher",
"optHandler :",
"optHandler : catcher",
"catcher : CATCH pattern body",
"finallyClause :",
"finallyClause : FINALLY body",
"oFuncType : INTERFACE oName funcType",
"guards :",
"guards : GUARDS pattern",
"mTypeList : emptyList",
"mTypeList : mTypes br",
"mTypes : mType",
"mTypes : mTypes EOLs mType",
"mType : mTypeHead",
"mType : mTypeHead body",
"mTypeHead : doco TO verb '(' pTypeList ')' optType",
"mTypeHead : doco TO '(' pTypeList ')' optType",
"funcType : funcTypeHead",
"funcType : funcTypeHead body",
"funcTypeHead : '(' pTypeList ')' optType",
"funcTypeHead : TO verb '(' pTypeList ')' optType",
"funcTypeHead : '.' verb '(' pTypeList ')' optType",
"pTypeList : br emptyList",
"pTypeList : br pTypes br",
"pTypes : pType",
"pTypes : pTypes ',' pType",
"pType : noun optType",
"pType : _ optType",
"pType : ':' guard",
"optType :",
"optType : ':' guard",
"metaoid : META",
"metaoid : PRAGMA",
"sepWord : CATCH",
"sepWord : ELSE",
"sepWord : ESCAPE",
"sepWord : FINALLY",
"sepWord : GUARDS",
"sepWord : IN",
"sepWord : THUNK",
"sepWord : FN",
"sepWord : TRY",
"sepWord : ID",
"sepWord : reserved",
"sepWord : OpWhen",
"reserved : ABSTRACT",
"reserved : AN",
"reserved : ASSERT",
"reserved : ATTRIBUTE",
"reserved : BE",
"reserved : BEGIN",
"reserved : BEHALF",
"reserved : BELIEF",
"reserved : BELIEVE",
"reserved : BELIEVES",
"reserved : CASE",
"reserved : CLASS",
"reserved : CONST",
"reserved : CONSTRUCTOR",
"reserved : DATATYPE",
"reserved : DECLARE",
"reserved : DEFAULT",
"reserved : DEFINE",
"reserved : DEFMACRO",
"reserved : DELICATE",
"reserved : DEPRECATED",
"reserved : DISPATCH",
"reserved : DO",
"reserved : ENCAPSULATE",
"reserved : ENCAPSULATED",
"reserved : ENCAPSULATES",
"reserved : END",
"reserved : ENSURE",
"reserved : ENUM",
"reserved : EVENTUAL",
"reserved : EVENTUALLY",
"reserved : EXPORT",
"reserved : FACET",
"reserved : FORALL",
"reserved : FUN",
"reserved : FUNCTION",
"reserved : GIVEN",
"reserved : HIDDEN",
"reserved : HIDES",
"reserved : INLINE",
"reserved : INTO",
"reserved : KNOW",
"reserved : KNOWS",
"reserved : LAMBDA",
"reserved : LET",
"reserved : METHODS",
"reserved : NAMESPACE",
"reserved : NATIVE",
"reserved : OBEYS",
"reserved : OCTET",
"reserved : ONEWAY",
"reserved : OPERATOR",
"reserved : PACKAGE",
"reserved : PRIVATE",
"reserved : PROTECTED",
"reserved : PUBLIC",
"reserved : RAISES",
"reserved : RELIANCE",
"reserved : RELIANT",
"reserved : RELIES",
"reserved : RELY",
"reserved : REVEAL",
"reserved : SAKE",
"reserved : SIGNED",
"reserved : STATIC",
"reserved : STRUCT",
"reserved : SUCHTHAT",
"reserved : SUPPORTS",
"reserved : SUSPECT",
"reserved : SUSPECTS",
"reserved : SYNCHRONIZED",
"reserved : THIS",
"reserved : TRANSIENT",
"reserved : TRUNCATABLE",
"reserved : TYPEDEF",
"reserved : UNSIGNED",
"reserved : UNUM",
"reserved : USES",
"reserved : USING",
"reserved : UTF8",
"reserved : UTF16",
"reserved : VIRTUAL",
"reserved : VOLATILE",
"reserved : WSTRING",
};

//#line 1577 "e.y"


/**
 *
 */
static public final StaticMaker EParserMaker =
    StaticMaker.make(EParser.class);

/**
 * caches previous simple parses (as is used for quasi-parsing)
 */
static private final IdentityCacheTable OurCache =
  new IdentityCacheTable(ENode.class, 100);

static private final class ParseFunc implements OneArgFunc, DeepPassByCopy {

    static private final long serialVersionUID = 8761482410783169702L;

    ParseFunc() {}

    public Object run(Object arg) {
        return EParser.run((Twine)arg, false);
    }

    /**
     * XXX We only say we implement this, but don't really, since we really
     * only need to be DeepFrozen; but currently the only way to declare
     * ourselves to be DeepFrozen is to claim to be DeepPassByCopy.
     */
    public Object[] getSpreadUncall() {
        T.fail("XXX not yet implemented");
        return new Object[0]; //make compiler happy
    }
}

static private final Memoizer OurMemoizer = new Memoizer(new ParseFunc(), 100);

/**
 *
 */
static private final ConstMap DefaultProps =
  ConstMap.fromProperties(System.getProperties());



/** contains all the tokens after yylval */
private final LexerFace myLexer;

/**
 * Do we escape after parsing only one expression, or do we parse the
 * entire input?
 */
private final boolean myOnlyOneExprFlag;

/**
 * Where the result is stored by the top-level productions
 */
private ENode myOptResult;

/** receives parsing events */
private final EBuilder b;


/**
 *
 */
static public EParser make(LexerFace lexer, TextWriter warns) {
    return make(null, lexer, warns, false, false);
}

/**
 *
 */
static public EParser make(ConstMap props, LexerFace lexer, TextWriter warns) {
    return make(props, lexer, warns, false, false);
}

/**
 *
 */
static public EParser make(ConstMap optProps,
                           LexerFace lexer,
                           TextWriter warns,
                           boolean debugFlag,
                           boolean onlyOneExprFlag) {
    if (null == optProps) {
        optProps = DefaultProps;
    }
    return new EParser(new ENodeBuilder(optProps, lexer, warns),
                       lexer,
                       debugFlag,
                       onlyOneExprFlag);
}

/**
 *
 */
public EParser(ENodeBuilder builder,
               LexerFace lexer,
               boolean debugFlag,
               boolean onlyOneExprFlag) {
    b = builder;
    initTables();
    myLexer = lexer;
    yydebug = debugFlag;
    myOnlyOneExprFlag = onlyOneExprFlag;
    myOptResult = null;
}

/**
 * For use as from E as a quasi-literal parser.
 * <p>
 * Transparently caches the result
 *
 * @param sourceCode The source code itself, not the location of
 * the source code
 */
static public ENode valueMaker(Twine sourceCode) {
    ENode result = (ENode)OurCache.fetch(sourceCode, ValueThunk.NULL_THUNK);
    if (null == result) {
        result = run(sourceCode, true);
        OurCache.put(sourceCode, result);
    }
    return result;
}

/**
 * For use from E as a quasi-pattern parser.
 * <p>
 * Just delegates to valueMaker/1.
 *
 * @param sourceCode The source code itself, not the location of
 * the source code
 */
static public ENode matchMaker(Twine sourceCode) {
    return valueMaker(sourceCode);
}


/**
 * For simple string -> expression parsing, especially for use from E
 *
 * @param sourceCode The source code itself, not the location of
 * the source code
 */
static public ENode run(Twine sourceCode) {
    return (ENode)OurMemoizer.run(sourceCode);
}

/**
 *
 */
static public ENode run(Twine sourceCode, boolean quasiFlag) {
    TextWriter warns = new TextWriter(PrintStreamWriter.stderr());
    return run(sourceCode, quasiFlag, null, warns);
}

/**
 * No longer does the caching.
 */
static public ENode run(Twine sourceCode,
                        boolean quasiFlag,
                        ConstMap optProps,
                        TextWriter warns) {
    try {
        if (null == optProps) {
            optProps = DefaultProps;
        }
        LexerFace lexer = ELexer.make(sourceCode,
                                      quasiFlag,
                                      ConstMap.testProp(optProps,
                                                        "e.enable.notabs"));
        EParser parser = EParser.make(optProps,
                                      lexer,
                                      warns,
                                      false,
                                      false);
        return parser.parse();

    } catch (IOException iox) {
        throw new NestedException(iox, "# parsing a string?!");
    }
}

/**
 *
 */
public ENode optParse() {
    if (yyparse() != 0) {
        yyerror("couldn't parse expression");
    }
    return myOptResult;
}

/**
 * If the input is empty, returns the null expression e`null`, rather
 * than null.
 */
public ENode parse() {
    ENode result = optParse();
    if (result == null) {
        return b.getNULL();
    } else {
        return result;
    }
}

/**
 * Converts EOTLUs to either EOLs or EOFTOKs according to myOnlyOneExprFlag
 * <p>
 * Note that yacc uses tag-codes, while Antlr uses type-codes.
 */
private short yylex() {
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
    yylval = token;
    short code = token.getOptTagCode();
    if (EOTLU == code) {
        if (myOnlyOneExprFlag) {
            return LexerFace.EOFTOK;
        } else {
            return EOL;
        }
    } else {
        return code;
    }
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
 *
 */
public void setSource(Twine newSource) {
    myLexer.setSource(newSource);
}

/**
 *
 */
public boolean isEndOfFile() {
    return myLexer.isEndOfFile();
}

/**
 *
 */
static NounExpr noun(Object name) {
    return BaseENodeBuilder.noun(name);
}

/**
 *
 */
static private boolean isTokenKind(Object tok, int[] members) {
    if (! (tok instanceof Astro)) {
        return false;
    }
    short tagCode = ((Astro)tok).getOptTagCode();
    for (int i = 0; i < members.length; i++) {
        if (tagCode == members[i]) {
            return true;
        }
    }
    return false;
}

static private final int[] LiteralTypes = {
    LiteralInteger,
    LiteralFloat64,
    LiteralChar,
    LiteralString,
    LiteralTwine
};

/**
 *
 */
static boolean isLiteralToken(Object tok) {
    return isTokenKind(tok, LiteralTypes);
}

static private final int[] QuasiTypes = {
    QuasiOpen,
    QuasiClose
};

/**
 *
 */
static boolean isQuasiPart(Object tok) {
    return isTokenKind(tok, QuasiTypes);
}



/*********************************/


/**
 *
 */
static private final String[] TheTokens = new String[yyname.length];

/**
 * For all the names below, if the name == name.toLowerCase(), then
 * the name must be a keyword. Else it must not be a keyword. The
 * names themselves must be legal Term tags.
 */
static {
    System.arraycopy(yyname, 0, TheTokens, 0, yyname.length);

    TheTokens[LexerFace.EOFTOK] = "EOFTOK";
    /* The magical end-of-line token, not considered whitespace */
    TheTokens[EOL]              = "EOL";
    TheTokens[EOTLU]            = "EOTLU";

    TheTokens[LiteralInteger]   = ".int.";
    TheTokens[LiteralFloat64]   = ".float64.";
    TheTokens[LiteralChar]      = ".char.";
    TheTokens[LiteralString]    = ".String.";
    TheTokens[LiteralTwine]     = ".Twine.";

    TheTokens[ID]               = "ID";
    TheTokens[VerbAssign]       = "VerbAssign";
    TheTokens[QuasiOpen]        = "QuasiOpen";
    TheTokens[QuasiClose]       = "QuasiClose";
    TheTokens[DollarIdent]      = "DollarIdent";
    TheTokens[AtIdent]          = "AtIdent";
    TheTokens[DollarOpen]       = "DollarOpen";
    TheTokens[AtOpen]           = "AtOpen";
    TheTokens[URI]              = "URI";
    TheTokens[DocComment]       = "DocComment";

    /* Keywords */
    TheTokens[AS]               = "as";
    TheTokens[BIND]             = "bind";
    TheTokens[BREAK]            = "break";
    TheTokens[CATCH]            = "catch";
    TheTokens[CONTINUE]         = "continue";
    TheTokens[DEF]              = "def";
    TheTokens[ELSE]             = "else";
    TheTokens[ESCAPE]           = "escape";
    TheTokens[EXIT]             = "exit";
    TheTokens[EXTENDS]          = "extends";
    TheTokens[FINALLY]          = "finally";
    TheTokens[FN]               = "fn";
    TheTokens[FOR]              = "for";
    TheTokens[GUARDS]           = "guards";
    TheTokens[IF]               = "if";
    TheTokens[IMPLEMENTS]       = "implements";
    TheTokens[IN]               = "in";
    TheTokens[INTERFACE]        = "interface";
    TheTokens[MATCH]            = "match";
    TheTokens[META]             = "meta";
    TheTokens[METHOD]           = "method";
    TheTokens[PRAGMA]           = "pragma";
    TheTokens[RETURN]           = "return";
    TheTokens[SWITCH]           = "switch";
    TheTokens[TO]               = "to";
    TheTokens[TRY]              = "try";
    TheTokens[VAR]              = "var";
    TheTokens[VIA]              = "via";
    TheTokens[WHEN]             = "when";
    TheTokens[WHILE]            = "while";
    TheTokens[_]                = "_";

    /* pseudo-reserved keywords */
    TheTokens[ACCUM]            = "accum";
    TheTokens[MODULE]           = "module";
    TheTokens[ON]               = "on";
    TheTokens[SELECT]           = "select";
    TheTokens[THROWS]           = "throws";
    TheTokens[THUNK]            = "thunk";

    /* reserved keywords */
    TheTokens[ABSTRACT]         = "abstract";
    TheTokens[AN]               = "an";
    TheTokens[ASSERT]           = "assert";
    TheTokens[ATTRIBUTE]        = "attribute";
    TheTokens[BE]               = "be";
    TheTokens[BEGIN]            = "begin";
    TheTokens[BEHALF]           = "behalf";
    TheTokens[BELIEF]           = "belief";
    TheTokens[BELIEVE]          = "believe";
    TheTokens[BELIEVES]         = "believes";
    TheTokens[CASE]             = "case";
    TheTokens[CLASS]            = "class";
    TheTokens[CONST]            = "const";
    TheTokens[CONSTRUCTOR]      = "constructor";
    TheTokens[DATATYPE]         = "datatype";
    TheTokens[DECLARE]          = "declare";
    TheTokens[DEFAULT]          = "default";
    TheTokens[DEFINE]           = "define";
    TheTokens[DEFMACRO]         = "defmacro";
    TheTokens[DELEGATE]         = "delegate";
    TheTokens[DELICATE]         = "delicate";
    TheTokens[DEPRECATED]       = "deprecated";
    TheTokens[DISPATCH]         = "dispatch";
    TheTokens[DO]               = "do";
    TheTokens[ENCAPSULATE]      = "encapsulate";
    TheTokens[ENCAPSULATED]     = "encapsulated";
    TheTokens[ENCAPSULATES]     = "encapsulates";
    TheTokens[END]              = "end";
    TheTokens[ENSURE]           = "ensure";
    TheTokens[ENUM]             = "enum";
    TheTokens[EVENTUAL]         = "eventual";
    TheTokens[EVENTUALLY]       = "eventually";
    TheTokens[EXPORT]           = "export";
    TheTokens[FACET]            = "facet";
    TheTokens[FORALL]           = "forall";
    TheTokens[FUN]              = "fun";
    TheTokens[FUNCTION]         = "function";
    TheTokens[GIVEN]            = "given";
    TheTokens[HIDDEN]           = "hidden";
    TheTokens[HIDES]            = "hides";
    TheTokens[INLINE]           = "inline";
    TheTokens[INTO]             = "into";
    TheTokens[KNOW]             = "know";
    TheTokens[KNOWS]            = "knows";
    TheTokens[LAMBDA]           = "lambda";
    TheTokens[LET]              = "let";
    TheTokens[METHODS]          = "methods";
    TheTokens[NAMESPACE]        = "namespace";
    TheTokens[NATIVE]           = "native";
    TheTokens[OBEYS]            = "obeys";
    TheTokens[OCTET]            = "octet";
    TheTokens[ONEWAY]           = "oneway";
    TheTokens[OPERATOR]         = "operator";
    TheTokens[PACKAGE]          = "package";
    TheTokens[PRIVATE]          = "private";
    TheTokens[PROTECTED]        = "protected";
    TheTokens[PUBLIC]           = "public";
    TheTokens[RAISES]           = "raises";
    TheTokens[RELIANCE]         = "reliance";
    TheTokens[RELIANT]          = "reliant";
    TheTokens[RELIES]           = "relies";
    TheTokens[RELY]             = "rely";
    TheTokens[REVEAL]           = "reveal";
    TheTokens[SAKE]             = "sake";
    TheTokens[SIGNED]           = "signed";
    TheTokens[STATIC]           = "static";
    TheTokens[STRUCT]           = "struct";
    TheTokens[SUCHTHAT]         = "suchthat";
    TheTokens[SUPPORTS]         = "supports";
    TheTokens[SUSPECT]          = "suspect";
    TheTokens[SUSPECTS]         = "suspects";
    TheTokens[SYNCHRONIZED]     = "synchronized";
    TheTokens[THIS]             = "this";
    TheTokens[TRANSIENT]        = "transient";
    TheTokens[TRUNCATABLE]      = "truncatable";
    TheTokens[TYPEDEF]          = "typedef";
    TheTokens[UNSIGNED]         = "unsigned";
    TheTokens[UNUM]             = "unum";
    TheTokens[USES]             = "uses";
    TheTokens[USING]            = "using";
    TheTokens[UTF8]             = "utf8";
    TheTokens[UTF16]            = "utf16";
    TheTokens[VIRTUAL]          = "virtual";
    TheTokens[VOLATILE]         = "volatile";
    TheTokens[WSTRING]          = "wstring";

    /* Single-Character Tokens */
    TheTokens[';']              = "SemiColon";
    TheTokens['&']              = "Ampersand";
    TheTokens['|']              = "VerticalBar";
    TheTokens['^']              = "Caret";
    TheTokens['+']              = "Plus";
    TheTokens['-']              = "Minus";
    TheTokens['*']              = "Star";
    TheTokens['/']              = "Slash";
    TheTokens['%']              = "Percent";
    TheTokens['!']              = "Bang";
    TheTokens['~']              = "Tilde";
    TheTokens['$']              = "Dollar";
    TheTokens['@']              = "At";
    TheTokens[',']              = "Comma";
    TheTokens['?']              = "Question";
    TheTokens[':']              = "Colon";
    TheTokens['.']              = "Dot";

    TheTokens['(']              = "OpenParen";
    TheTokens[')']              = "CloseParen";
    TheTokens['[']              = "OpenBracket";
    TheTokens[']']              = "CloseBracket";
    TheTokens['{']              = "OpenBrace";
    TheTokens['}']              = "CloseBrace";
    TheTokens['<']              = "OpenAngle";
    TheTokens['>']              = "CloseAngle";


    /* Multi-Character Operators */
    TheTokens[OpLAnd]           = "OpLAnd";
    TheTokens[OpLOr]            = "OpLOr";
    TheTokens[OpSame]           = "OpSame";
    TheTokens[OpNSame]          = "OpNSame";
    TheTokens[OpButNot]         = "OpButNot";
    TheTokens[OpLeq]            = "OpLeq";
    TheTokens[OpABA]            = "OpABA";
    TheTokens[OpGeq]            = "OpGeq";
    TheTokens[OpThru]           = "OpThru";
    TheTokens[OpTill]           = "OpTill";
    TheTokens[OpAsl]            = "OpAsl";
    TheTokens[OpAsr]            = "OpAsr";
    TheTokens[OpFlrDiv]         = "OpFlrDiv";
    TheTokens[OpMod]            = "OpMod";
    TheTokens[OpPow]            = "OpPow";

    TheTokens[OpAss]            = "OpAss";
    TheTokens[OpAssAdd]         = "OpAssAdd";
    TheTokens[OpAssAnd]         = "OpAssAnd";
    TheTokens[OpAssAprxDiv]     = "OpAssAprxDiv";
    TheTokens[OpAssFlrDiv]      = "OpAssFlrDiv";
    TheTokens[OpAssAsl]         = "OpAssAsl";
    TheTokens[OpAssAsr]         = "OpAssAsr";
    TheTokens[OpAssRemdr]       = "OpAssRemdr";
    TheTokens[OpAssMod]         = "OpAssMod";
    TheTokens[OpAssMul]         = "OpAssMul";
    TheTokens[OpAssOr]          = "OpAssOr";
    TheTokens[OpAssPow]         = "OpAssPow";
    TheTokens[OpAssSub]         = "OpAssSub";
    TheTokens[OpAssXor]         = "OpAssXor";

    /* Other funky tokens */
    TheTokens[Send]             = "Send";
    TheTokens[OpWhen]           = "OpWhen";
    TheTokens[MapsTo]           = "MapsTo";
    TheTokens[MatchBind]        = "MatchBind";
    TheTokens[MisMatch]         = "MisMatch";
    TheTokens[OpScope]          = "OpScope";

    /* Non-token Kernel-E Term-tree tag names (ie, functor names) */

    TheTokens[AssignExpr]       = "AssignExpr";
    TheTokens[CallExpr]         = "CallExpr";
    TheTokens[DefineExpr]       = "DefineExpr";
    TheTokens[EscapeExpr]       = "EscapeExpr";
    TheTokens[HideExpr]         = "HideExpr";
    TheTokens[IfExpr]           = "IfExpr";
    TheTokens[LiteralExpr]      = "LiteralExpr";
    TheTokens[NounExpr]         = "NounExpr";
    TheTokens[ObjectExpr]       = "ObjectExpr";
    TheTokens[QuasiLiteralExpr] = "QuasiLiteralExpr";
    TheTokens[QuasiPatternExpr] = "QuasiPatternExpr";
    TheTokens[MetaStateExpr]    = "MetaStateExpr";
    TheTokens[MetaContextExpr]  = "MetaContextExpr";
    TheTokens[SeqExpr]          = "SeqExpr";
    TheTokens[SlotExpr]         = "SlotExpr";
    TheTokens[MetaExpr]         = "MetaExpr";
    TheTokens[CatchExpr]        = "CatchExpr";
    TheTokens[FinallyExpr]      = "FinallyExpr";

    TheTokens[FinalPattern]     = "FinalPattern";
    TheTokens[SlotPattern]      = "SlotPattern";
    TheTokens[ListPattern]      = "ListPattern";
    TheTokens[IgnorePattern]    = "IgnorePattern";
    TheTokens[QuasiLiteralPatt] = "QuasiLiteralPatt";
    TheTokens[QuasiPatternPatt] = "QuasiPatternPatt";

    TheTokens[EScript]          = "EScript";
    TheTokens[EMethod]          = "EMethod";
    TheTokens[EMatcher]         = "EMatcher";
}

/**
 *
 */
static public final AstroSchema DEFAULT_SCHEMA =
  new BaseSchema("E-Language", ConstList.fromArray(TheTokens));

/**
 *
 */
static private final int DEFAULT_CONTINUE_INDENT = 2;

/**
 * These are the tokens that may appear at the end of a line, in which
 * case the next line is a (to be indented) continuation of the
 * expression.
 */
static private final int[][] TheContinuerOps = {
    { '!',          DEFAULT_CONTINUE_INDENT },
    { '%',          DEFAULT_CONTINUE_INDENT },
    { '&',          DEFAULT_CONTINUE_INDENT },
    { '*',          DEFAULT_CONTINUE_INDENT },
    { '+',          DEFAULT_CONTINUE_INDENT },
    { '-',          DEFAULT_CONTINUE_INDENT },
    { '/',          DEFAULT_CONTINUE_INDENT },
    { ':',          DEFAULT_CONTINUE_INDENT },
    { '<',          DEFAULT_CONTINUE_INDENT },
    { '>',          DEFAULT_CONTINUE_INDENT },
    { '?',          DEFAULT_CONTINUE_INDENT },
    { '^',          DEFAULT_CONTINUE_INDENT },
    { '|',          DEFAULT_CONTINUE_INDENT },
    { '~',          DEFAULT_CONTINUE_INDENT },
    { '.',          DEFAULT_CONTINUE_INDENT },

    { VerbAssign,   DEFAULT_CONTINUE_INDENT },    // ID"="

    { EXTENDS,      DEFAULT_CONTINUE_INDENT },
    { IMPLEMENTS,   DEFAULT_CONTINUE_INDENT },
    { IN,           DEFAULT_CONTINUE_INDENT },
    { EXTENDS,      DEFAULT_CONTINUE_INDENT },

    { OpABA,        DEFAULT_CONTINUE_INDENT },    // <=>
    { OpAsl,        DEFAULT_CONTINUE_INDENT },    // <<
    { OpAsr,        DEFAULT_CONTINUE_INDENT },    // >>
    { OpAss,        DEFAULT_CONTINUE_INDENT },    // :=
    { OpAssAdd,     DEFAULT_CONTINUE_INDENT },    // +=
    { OpAssAnd,     DEFAULT_CONTINUE_INDENT },    // &=
    { OpAssAprxDiv, DEFAULT_CONTINUE_INDENT },    // /=
    { OpAssAsl,     DEFAULT_CONTINUE_INDENT },    // <<=
    { OpAssAsr,     DEFAULT_CONTINUE_INDENT },    // >>=
    { OpAssFlrDiv,  DEFAULT_CONTINUE_INDENT },    // //=
    { OpAssMod,     DEFAULT_CONTINUE_INDENT },    // %%=
    { OpAssMul,     DEFAULT_CONTINUE_INDENT },    // *=
    { OpAssOr,      DEFAULT_CONTINUE_INDENT },    // |=
    { OpAssPow,     DEFAULT_CONTINUE_INDENT },    // **=
    { OpAssRemdr,   DEFAULT_CONTINUE_INDENT },    // %=
    { OpAssSub,     DEFAULT_CONTINUE_INDENT },    // -=
    { OpAssXor,     DEFAULT_CONTINUE_INDENT },    // ^=
    { OpButNot,     DEFAULT_CONTINUE_INDENT },    // &!
    { OpFlrDiv,     DEFAULT_CONTINUE_INDENT },    // //
    { OpGeq,        DEFAULT_CONTINUE_INDENT },    // >=
    { OpLAnd,       DEFAULT_CONTINUE_INDENT },    // &&
    { OpLOr,        DEFAULT_CONTINUE_INDENT },    // ||
    { OpLeq,        DEFAULT_CONTINUE_INDENT },    // <=
    { OpMod,        DEFAULT_CONTINUE_INDENT },    // %%
    { OpNSame,      DEFAULT_CONTINUE_INDENT },    // !=
    { OpPow,        DEFAULT_CONTINUE_INDENT },    // **
    { OpSame,       DEFAULT_CONTINUE_INDENT },    // ==
    { OpThru,       DEFAULT_CONTINUE_INDENT },    // ..
    { OpTill,       DEFAULT_CONTINUE_INDENT },    // ..!

    { Send,         DEFAULT_CONTINUE_INDENT },    // <-
    { OpWhen,       DEFAULT_CONTINUE_INDENT },    // ->
    { MapsTo,       DEFAULT_CONTINUE_INDENT },    // =>
    { MatchBind,    DEFAULT_CONTINUE_INDENT },    // =~
    { MisMatch,     DEFAULT_CONTINUE_INDENT },    // !~
    { OpScope,      DEFAULT_CONTINUE_INDENT },    // ::

    { ',',          0 },
    { DocComment,   0 }                           // /**..*/
};

/**
 * TheContinuers[tagCode] says whether this is a continuation
 * operator, and if so, how much to indent by.
 * <p>
 * If this isn't a continuation operator, then -1.
 */
static private final int[] TheContinuers = new int[yyname.length];

static {
    for (int i = 0, len = TheContinuers.length; i < len; i++) {
        TheContinuers[i] = -1;
    }
    for (int i = 0; i < TheContinuerOps.length; i++) {
        TheContinuers[TheContinuerOps[i][0]] = TheContinuerOps[i][1];
    }
}

/**
 * If this token appears at the end of a line, does that make the next
 * line a (to be indented) continuation line?
 * <p>
 * -1 if not. The number of spaces to indent if so.
 */
static public int continueCount(int tagCode) {
    return TheContinuers[tagCode];
}

/**
 * Used to mark places where we should be providing a poser (an object
 * from which source position info can be derived).
 */
static private final Object NO_POSER = BaseEBuilder.NO_POSER;
//#line 5514 "EParser.java"
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
//#line 207 "e.y"
{ myOptResult = null; }
break;
case 2:
//#line 208 "e.y"
{ myOptResult = b.forValue(val_peek(0), null); }
break;
case 3:
//#line 211 "e.y"
{ b.reserved(val_peek(3),"module"); }
break;
case 4:
//#line 214 "e.y"
{ myOptResult = (Pattern)val_peek(0); }
break;
case 5:
//#line 221 "e.y"
{ yyval = b.get__BREAK(); }
break;
case 6:
//#line 222 "e.y"
{ yyval = b.get__CONTINUE(); }
break;
case 7:
//#line 223 "e.y"
{ yyval = b.get__RETURN(); }
break;
case 8:
//#line 236 "e.y"
{ yyval = val_peek(1); }
break;
case 10:
//#line 246 "e.y"
{ yyval = b.sequence(val_peek(2), val_peek(0)); }
break;
case 13:
//#line 255 "e.y"
{ yyval = b.sequence(val_peek(2), val_peek(0)); }
break;
case 15:
//#line 273 "e.y"
{ yyval = b.ejector(val_peek(0)); }
break;
case 16:
//#line 274 "e.y"
{ yyval = b.ejector(val_peek(2)); }
break;
case 17:
//#line 275 "e.y"
{ yyval = b.ejector(val_peek(1), val_peek(0)); }
break;
case 18:
//#line 276 "e.y"
{ b.pocket(val_peek(1),"smalltalk-return");
                                          yyval = b.ejector(b.get__RETURN(),val_peek(0));}
break;
case 19:
//#line 278 "e.y"
{ yyval = b.doco("",val_peek(0)); }
break;
case 20:
//#line 279 "e.y"
{ yyval = b.doco(val_peek(1),val_peek(0)); }
break;
case 22:
//#line 292 "e.y"
{ yyval = b.forward(val_peek(0)); }
break;
case 23:
//#line 293 "e.y"
{ yyval = b.assign(val_peek(2),     val_peek(0)); }
break;
case 24:
//#line 294 "e.y"
{ yyval = b.update(val_peek(2), val_peek(1), b.list(val_peek(0))); }
break;
case 25:
//#line 295 "e.y"
{ yyval = b.update(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 26:
//#line 297 "e.y"
{ yyval = b.define(val_peek(2), val_peek(0)); }
break;
case 27:
//#line 298 "e.y"
{ yyval = b.define(val_peek(2), val_peek(0)); }
break;
case 28:
//#line 299 "e.y"
{ yyval = b.define(val_peek(2), val_peek(0)); }
break;
case 29:
//#line 302 "e.y"
{ b.pocket(val_peek(3),"trinary-define");
                                          yyval = b.define(val_peek(4),val_peek(2),val_peek(0)); }
break;
case 30:
//#line 310 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 31:
//#line 311 "e.y"
{ yyval = b.list(); }
break;
case 32:
//#line 312 "e.y"
{ yyval = b.append(b.list(val_peek(3)),val_peek(1)); }
break;
case 34:
//#line 321 "e.y"
{ yyval = b.condOr(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 36:
//#line 330 "e.y"
{ yyval = b.condAnd(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 38:
//#line 342 "e.y"
{ yyval = b.same(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 39:
//#line 343 "e.y"
{ yyval = b.not(val_peek(1), b.same(val_peek(2), val_peek(1), val_peek(0))); }
break;
case 42:
//#line 346 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"xor", val_peek(0)); }
break;
case 43:
//#line 347 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"butNot", val_peek(0)); }
break;
case 44:
//#line 349 "e.y"
{ yyval = b.matchBind(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 45:
//#line 350 "e.y"
{ yyval = b.not(val_peek(1),
                                                     b.matchBind(val_peek(2),val_peek(1),val_peek(0))); }
break;
case 46:
//#line 355 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"and", val_peek(0)); }
break;
case 47:
//#line 356 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"and", val_peek(0)); }
break;
case 48:
//#line 359 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"or", val_peek(0)); }
break;
case 49:
//#line 360 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"or", val_peek(0)); }
break;
case 51:
//#line 372 "e.y"
{ yyval = b.lessThan(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 52:
//#line 373 "e.y"
{ yyval = b.leq(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 53:
//#line 374 "e.y"
{ yyval = b.asBigAs(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 54:
//#line 375 "e.y"
{ yyval = b.geq(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 55:
//#line 376 "e.y"
{ yyval = b.greaterThan(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 56:
//#line 380 "e.y"
{ b.pocket(val_peek(1),"cast");
                                          yyval = b.cast(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 58:
//#line 389 "e.y"
{ yyval = b.thru(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 59:
//#line 390 "e.y"
{ yyval = b.till(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 61:
//#line 399 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"shiftLeft", val_peek(0)); }
break;
case 62:
//#line 400 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"shiftRight",val_peek(0)); }
break;
case 64:
//#line 409 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"add", val_peek(0)); }
break;
case 65:
//#line 410 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"subtract",val_peek(0)); }
break;
case 67:
//#line 421 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"multiply", val_peek(0)); }
break;
case 68:
//#line 422 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"approxDivide", val_peek(0)); }
break;
case 69:
//#line 423 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"floorDivide", val_peek(0)); }
break;
case 70:
//#line 424 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"remainder", val_peek(0)); }
break;
case 71:
//#line 425 "e.y"
{ yyval = b.mod(val_peek(2), val_peek(1), b.list(val_peek(0))); }
break;
case 73:
//#line 434 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"pow", val_peek(0)); }
break;
case 75:
//#line 452 "e.y"
{ yyval = b.call(val_peek(0), val_peek(1),"not", b.list()); }
break;
case 76:
//#line 453 "e.y"
{ yyval = b.call(val_peek(0), val_peek(1),"complement", b.list());}
break;
case 77:
//#line 454 "e.y"
{ yyval = b.slotExpr(val_peek(1), val_peek(0)); }
break;
case 78:
//#line 455 "e.y"
{ yyval = b.bindingExpr(val_peek(1), val_peek(0)); }
break;
case 79:
//#line 456 "e.y"
{ b.pocket(val_peek(1),"unary-star");
                                  yyval = b.call(val_peek(0), val_peek(1),"get", b.list()); }
break;
case 80:
//#line 458 "e.y"
{ yyval = b.call(val_peek(0), val_peek(1),"negate", b.list()); }
break;
case 82:
//#line 467 "e.y"
{ yyval = b.callFacet(val_peek(2), val_peek(0)); }
break;
case 83:
//#line 468 "e.y"
{ yyval = b.sendFacet(val_peek(2), val_peek(0)); }
break;
case 84:
//#line 470 "e.y"
{ yyval = b.propValue(val_peek(2), val_peek(0)); }
break;
case 85:
//#line 471 "e.y"
{ yyval = b.propSlot(val_peek(3), val_peek(0)); }
break;
case 86:
//#line 472 "e.y"
{ b.reserved(val_peek(1),"::&"); }
break;
case 87:
//#line 473 "e.y"
{ yyval = b.doMetaProp(val_peek(2), val_peek(0)); }
break;
case 88:
//#line 475 "e.y"
{ yyval = b.sendPropValue(val_peek(3), val_peek(0)); }
break;
case 89:
//#line 476 "e.y"
{ yyval = b.sendPropSlot(val_peek(4), val_peek(0)); }
break;
case 90:
//#line 477 "e.y"
{ b.reserved(val_peek(1),"<-::&"); }
break;
case 92:
//#line 489 "e.y"
{ yyval = b.call(val_peek(3), val_peek(2),"run", val_peek(1)); }
break;
case 93:
//#line 490 "e.y"
{ b.pocket(NO_POSER,"lambda-args");
                                          yyval = b.call(val_peek(0),
                                                      "run__control",
                                                      b.list()); }
break;
case 94:
//#line 495 "e.y"
{ yyval = b.call(val_peek(3), val_peek(1), val_peek(0)); }
break;
case 95:
//#line 496 "e.y"
{ yyval = b.call(val_peek(3), val_peek(2),"get", val_peek(1)); }
break;
case 96:
//#line 497 "e.y"
{ yyval = b.send(val_peek(3), val_peek(1), val_peek(0)); }
break;
case 97:
//#line 498 "e.y"
{ yyval = b.send(val_peek(2), val_peek(1),"run", val_peek(0)); }
break;
case 98:
//#line 500 "e.y"
{ yyval = b.doMeta(val_peek(1), val_peek(1),"run", val_peek(0)); }
break;
case 99:
//#line 501 "e.y"
{ yyval = b.doMeta(val_peek(3), val_peek(1), val_peek(0)); }
break;
case 100:
//#line 502 "e.y"
{ yyval = b.doMeta(val_peek(3), val_peek(2),"get", val_peek(1)); }
break;
case 101:
//#line 503 "e.y"
{ yyval = b.doMetaSend(val_peek(3), val_peek(1), val_peek(0)); }
break;
case 102:
//#line 504 "e.y"
{ yyval = b.doMetaSend(val_peek(2), val_peek(1),"run", val_peek(0));}
break;
case 104:
//#line 511 "e.y"
{ yyval = b.control(val_peek(6), val_peek(2), val_peek(4), val_peek(1), val_peek(0)); }
break;
case 105:
//#line 512 "e.y"
{ yyval = b.control(val_peek(3), val_peek(2), val_peek(1), val_peek(0)); }
break;
case 106:
//#line 513 "e.y"
{ yyval = b.control(val_peek(3), val_peek(2), val_peek(1), val_peek(0)); }
break;
case 107:
//#line 515 "e.y"
{ yyval = b.control(val_peek(5), val_peek(4), val_peek(2),
                                                         b.list(), val_peek(0)); }
break;
case 108:
//#line 523 "e.y"
{ yyval = b.literal(val_peek(0)); }
break;
case 109:
//#line 524 "e.y"
{ yyval = b.literal(val_peek(0)); }
break;
case 110:
//#line 525 "e.y"
{ yyval = b.literal(val_peek(0)); }
break;
case 111:
//#line 526 "e.y"
{ yyval = b.literal(val_peek(0)); }
break;
case 114:
//#line 535 "e.y"
{ yyval = b.uriExpr(val_peek(0)); }
break;
case 115:
//#line 536 "e.y"
{ yyval = b.quasiExpr(val_peek(1),val_peek(0)); }
break;
case 117:
//#line 539 "e.y"
{ yyval = b.tuple(val_peek(1)); }
break;
case 118:
//#line 540 "e.y"
{ yyval = b.map(val_peek(1)); }
break;
case 119:
//#line 542 "e.y"
{ yyval = b.hide(val_peek(0)); }
break;
case 120:
//#line 543 "e.y"
{ yyval = b.escape(val_peek(2),val_peek(1),val_peek(0)); }
break;
case 121:
//#line 544 "e.y"
{ yyval = b.whilex(val_peek(2),val_peek(1),val_peek(0)); }
break;
case 122:
//#line 545 "e.y"
{ yyval = b.switchx(val_peek(1),val_peek(0)); }
break;
case 123:
//#line 546 "e.y"
{ yyval = b.tryx(val_peek(2),val_peek(1),val_peek(0)); }
break;
case 125:
//#line 549 "e.y"
{ yyval = val_peek(0); }
break;
case 127:
//#line 552 "e.y"
{ b.reserved(val_peek(2),"select"); }
break;
case 129:
//#line 560 "e.y"
{ yyval = b.doco("",val_peek(0)); }
break;
case 130:
//#line 561 "e.y"
{ yyval = b.doco(val_peek(1),val_peek(0)); }
break;
case 131:
//#line 571 "e.y"
{ yyval = ((ConstMap)val_peek(0)).with(
					         "oName", val_peek(1), true); }
break;
case 132:
//#line 574 "e.y"
{ yyval = b.oType("",val_peek(6),b.list(),
                                                       val_peek(5),val_peek(4),val_peek(1)); }
break;
case 133:
//#line 576 "e.y"
{ b.pocket(NO_POSER,"thunk");
                                          /* doesn't bind __return */
                                          yyval = b.fnDecl(val_peek(1), b.list(), val_peek(0)); }
break;
case 134:
//#line 580 "e.y"
{ b.pocket(NO_POSER,"anon-lambda");
                                          /* doesn't bind __return */
                                          yyval = b.fnDecl(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 135:
//#line 589 "e.y"
{ yyval = ((ConstMap)val_peek(1)).with(
						 "script", val_peek(0), true); }
break;
case 136:
//#line 591 "e.y"
{ /* binds __return */
                                          yyval = b.methDecl(val_peek(1), val_peek(0), true); }
break;
case 137:
//#line 600 "e.y"
{ yyval = noun(val_peek(0)); }
break;
case 138:
//#line 601 "e.y"
{ yyval = b.quasiLiteralExpr(val_peek(0)); }
break;
case 139:
//#line 602 "e.y"
{ yyval = b.quasiPatternExpr(val_peek(0)); }
break;
case 140:
//#line 606 "e.y"
{ yyval = val_peek(1); }
break;
case 141:
//#line 607 "e.y"
{ yyval = val_peek(0); }
break;
case 142:
//#line 608 "e.y"
{ yyval = null; }
break;
case 143:
//#line 612 "e.y"
{ yyval = val_peek(1); }
break;
case 144:
//#line 613 "e.y"
{ yyval = val_peek(0); }
break;
case 145:
//#line 620 "e.y"
{ yyval = val_peek(1); }
break;
case 146:
//#line 628 "e.y"
{ yyval = b.ifx(val_peek(1), val_peek(0)); }
break;
case 147:
//#line 629 "e.y"
{ yyval = b.ifx(val_peek(3), val_peek(2), val_peek(0)); }
break;
case 148:
//#line 630 "e.y"
{ yyval = b.ifx(val_peek(3), val_peek(2), val_peek(0)); }
break;
case 150:
//#line 635 "e.y"
{ b.reserved(val_peek(1),"if-match"); }
break;
case 151:
//#line 643 "e.y"
{ yyval = b.forx(val_peek(4),val_peek(2),val_peek(1),val_peek(0)); }
break;
case 152:
//#line 644 "e.y"
{ b.reserved(val_peek(2),"when-in"); }
break;
case 154:
//#line 656 "e.y"
{ yyval = noun("simple__quasiParser"); }
break;
case 155:
//#line 657 "e.y"
{ yyval = noun(b.mangle(val_peek(0),
                                                     "__quasiParser")); }
break;
case 156:
//#line 662 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 157:
//#line 663 "e.y"
{ yyval = b.with(val_peek(1), val_peek(0)); }
break;
case 158:
//#line 667 "e.y"
{ yyval = b.list(val_peek(1), val_peek(0)); }
break;
case 159:
//#line 668 "e.y"
{ yyval = b.with(b.with(val_peek(2), val_peek(1)), val_peek(0)); }
break;
case 160:
//#line 672 "e.y"
{ yyval = b.dollarNoun(val_peek(0)); }
break;
case 161:
//#line 673 "e.y"
{ yyval = val_peek(1); }
break;
case 162:
//#line 684 "e.y"
{ yyval = val_peek(1); }
break;
case 168:
//#line 710 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 169:
//#line 711 "e.y"
{ yyval = b.with(val_peek(2), val_peek(0)); }
break;
case 171:
//#line 715 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 172:
//#line 716 "e.y"
{ yyval = b.with(val_peek(2), val_peek(0)); }
break;
case 173:
//#line 721 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 174:
//#line 722 "e.y"
{ yyval = b.with(val_peek(2), val_peek(0)); }
break;
case 176:
//#line 726 "e.y"
{ yyval = b.assoc(val_peek(2), val_peek(0)); }
break;
case 177:
//#line 727 "e.y"
{ b.pocket(val_peek(1),"exporter");
                                          yyval = b.exporter(val_peek(0)); }
break;
case 178:
//#line 729 "e.y"
{ b.pocket(val_peek(2),"exporter");
                                          yyval = b.exporter(b.slotExpr(val_peek(1),val_peek(0))); }
break;
case 179:
//#line 731 "e.y"
{ b.pocket(val_peek(2),"exporter");
                                          yyval=b.exporter(b.bindingExpr(val_peek(1),val_peek(0)));}
break;
case 180:
//#line 733 "e.y"
{ b.pocket(val_peek(2),"exporter");
                                          b.reserved(val_peek(1),"Forward exporter"); }
break;
case 181:
//#line 748 "e.y"
{ yyval = b.assoc(b.ignore(), val_peek(0)); }
break;
case 182:
//#line 749 "e.y"
{ yyval = b.assoc(val_peek(2), val_peek(0)); }
break;
case 184:
//#line 755 "e.y"
{ yyval = b.suchThat(val_peek(2), val_peek(0)); }
break;
case 185:
//#line 756 "e.y"
{ yyval = b.via(val_peek(1),val_peek(0)); }
break;
case 186:
//#line 758 "e.y"
{ b.reserved(val_peek(1),"meta pattern"); }
break;
case 189:
//#line 764 "e.y"
{ yyval = b.quasiPattern(val_peek(1), val_peek(0)); }
break;
case 190:
//#line 765 "e.y"
{ yyval = b.patternEquals(val_peek(0)); }
break;
case 191:
//#line 766 "e.y"
{ b.reserved(val_peek(1),"not-same pattern"); }
break;
case 192:
//#line 767 "e.y"
{ b.reserved(val_peek(1),
                                                     "comparison pattern"); }
break;
case 193:
//#line 770 "e.y"
{ b.pocket(val_peek(4),"call-pattern");
                                                 yyval = b.callPattern(val_peek(3), val_peek(2),"run", val_peek(1));}
break;
case 194:
//#line 772 "e.y"
{ b.pocket(val_peek(6),"call-pattern");
                                                 yyval = b.callPattern(val_peek(5), val_peek(3), val_peek(1)); }
break;
case 195:
//#line 774 "e.y"
{ b.pocket(val_peek(4),"call-pattern");
                                                 yyval = b.callPattern(val_peek(3), val_peek(2),"get", val_peek(1));}
break;
case 196:
//#line 777 "e.y"
{ yyval = b.listPattern(val_peek(0)); }
break;
case 197:
//#line 778 "e.y"
{ yyval = b.mapPattern(val_peek(1),null); }
break;
case 198:
//#line 779 "e.y"
{ yyval = b.cdrPattern(val_peek(2), val_peek(0)); }
break;
case 199:
//#line 780 "e.y"
{ yyval = b.mapPattern(val_peek(3), val_peek(0)); }
break;
case 200:
//#line 784 "e.y"
{ yyval = val_peek(1); }
break;
case 201:
//#line 788 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 202:
//#line 789 "e.y"
{ yyval = b.with(val_peek(1), val_peek(0)); }
break;
case 203:
//#line 793 "e.y"
{ yyval = b.list(val_peek(1), val_peek(0)); }
break;
case 204:
//#line 794 "e.y"
{ yyval = b.with(b.with(val_peek(2), val_peek(1)),
                                                              val_peek(0)); }
break;
case 207:
//#line 804 "e.y"
{ yyval = b.atNoun(val_peek(0)); }
break;
case 208:
//#line 805 "e.y"
{ yyval = val_peek(2); }
break;
case 209:
//#line 818 "e.y"
{ yyval = b.ignore(); }
break;
case 210:
//#line 819 "e.y"
{ yyval = b.ignore(val_peek(0)); }
break;
case 211:
//#line 820 "e.y"
{ yyval = b.ignore(val_peek(0));}
break;
case 212:
//#line 824 "e.y"
{ yyval = b.finalPattern(val_peek(2),val_peek(0));}
break;
case 213:
//#line 825 "e.y"
{ b.antiPocket(val_peek(0),
                                                       "explicit-final-guard");
                                          yyval = b.finalPattern(val_peek(0)); }
break;
case 217:
//#line 834 "e.y"
{ yyval = b.bindDefiner(val_peek(2),val_peek(0)); }
break;
case 218:
//#line 835 "e.y"
{ b.antiPocket(val_peek(1),
                                                       "explicit-final-guard");
                                          yyval = b.bindDefiner(val_peek(0)); }
break;
case 219:
//#line 841 "e.y"
{ yyval = b.varPattern(val_peek(2),val_peek(0)); }
break;
case 220:
//#line 842 "e.y"
{ b.antiPocket(val_peek(1),
                                                       "explicit-var-guard");
                                          yyval = b.varPattern(val_peek(0)); }
break;
case 221:
//#line 848 "e.y"
{ yyval = b.slotPattern(val_peek(2),val_peek(0)); }
break;
case 222:
//#line 849 "e.y"
{ b.antiPocket(val_peek(1),
                                                       "explicit-slot-guard");
                                          yyval = b.slotPattern(val_peek(0)); }
break;
case 223:
//#line 852 "e.y"
{ yyval = b.bindingPattern(val_peek(0)); }
break;
case 224:
//#line 861 "e.y"
{ yyval = b.finalOName(val_peek(0)); }
break;
case 225:
//#line 862 "e.y"
{ yyval = b.ignoreOName(); }
break;
case 226:
//#line 863 "e.y"
{ yyval = b.bindOName(val_peek(0)); }
break;
case 227:
//#line 864 "e.y"
{ yyval = b.varOName(val_peek(0)); }
break;
case 228:
//#line 865 "e.y"
{ b.reserved(val_peek(0),
                                "literal qualified name no longer accepted"); }
break;
case 230:
//#line 878 "e.y"
{ yyval = val_peek(1); }
break;
case 233:
//#line 904 "e.y"
{ yyval = val_peek(1); }
break;
case 234:
//#line 908 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 235:
//#line 909 "e.y"
{ yyval = b.with(val_peek(2), val_peek(0)); }
break;
case 236:
//#line 913 "e.y"
{ yyval = val_peek(1); }
break;
case 237:
//#line 917 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 238:
//#line 918 "e.y"
{ yyval = b.with(val_peek(2), val_peek(0)); }
break;
case 239:
//#line 922 "e.y"
{ yyval = b.assoc(val_peek(2), val_peek(0)); }
break;
case 240:
//#line 923 "e.y"
{ b.pocket(val_peek(1),"pattern-default");
                                          yyval = b.assoc(val_peek(4), b.assoc(val_peek(0),val_peek(2))); }
break;
case 241:
//#line 925 "e.y"
{ b.pocket(val_peek(1),"pattern-default");
                                     b.reserved(val_peek(1),"default in map pattern"); }
break;
case 242:
//#line 927 "e.y"
{ b.pocket(val_peek(1),"importer");
                                          yyval = b.importer(val_peek(0)); }
break;
case 243:
//#line 929 "e.y"
{ b.pocket(val_peek(1),"pattern-default");
                                          b.pocket(val_peek(3),"importer");
                                          yyval = b.importer(b.assoc(val_peek(0),val_peek(2))); }
break;
case 244:
//#line 932 "e.y"
{ b.pocket(val_peek(1),"pattern-default");
                                          b.pocket(val_peek(3),"importer");
                                     b.reserved(val_peek(0),"default in map pattern"); }
break;
case 247:
//#line 953 "e.y"
{ yyval = ""; }
break;
case 249:
//#line 961 "e.y"
{ yyval = val_peek(0); }
break;
case 250:
//#line 962 "e.y"
{ yyval = b.bindOName(val_peek(0)); }
break;
case 251:
//#line 963 "e.y"
{ yyval = b.varOName(val_peek(0)); }
break;
case 252:
//#line 970 "e.y"
{yyval=ConstMap.fromPairs(new Object[][]{
                                              { "extends", val_peek(2) },
                                              { "as", val_peek(1) },
                                              { "impls", b.optExprs(val_peek(0)) }}); }
break;
case 253:
//#line 980 "e.y"
{yyval=ConstMap.fromPairs(new Object[][]{
                                              { "supers", b.optExprs(val_peek(1)) },
                                              { "impls", b.optExprs(val_peek(0)) }}); }
break;
case 254:
//#line 991 "e.y"
{ yyval = null; }
break;
case 255:
//#line 992 "e.y"
{ yyval = val_peek(0); }
break;
case 256:
//#line 999 "e.y"
{ yyval = b.list(); }
break;
case 258:
//#line 1003 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 259:
//#line 1004 "e.y"
{ yyval = b.with(val_peek(2), val_peek(0)); }
break;
case 260:
//#line 1011 "e.y"
{ yyval = null; }
break;
case 261:
//#line 1012 "e.y"
{ yyval = val_peek(0); }
break;
case 262:
//#line 1015 "e.y"
{ yyval = b.list(); }
break;
case 264:
//#line 1019 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 265:
//#line 1020 "e.y"
{ yyval = b.with(val_peek(2), val_peek(0)); }
break;
case 269:
//#line 1034 "e.y"
{ b.reserved(val_peek(0),"literal concat"); }
break;
case 270:
//#line 1035 "e.y"
{ b.reserved(val_peek(0),"literal concat"); }
break;
case 271:
//#line 1044 "e.y"
{ /* binds __return */
                                               yyval = b.to(val_peek(3), val_peek(1), val_peek(0)); }
break;
case 272:
//#line 1046 "e.y"
{ /* doesn't bind __return */
                                               yyval = b.method(val_peek(3), val_peek(1), val_peek(0)); }
break;
case 273:
//#line 1075 "e.y"
{ yyval = b.methHead(val_peek(3),"run",val_peek(2),val_peek(0)); }
break;
case 274:
//#line 1076 "e.y"
{ yyval = b.methHead(val_peek(4),      val_peek(2),val_peek(0)); }
break;
case 275:
//#line 1085 "e.y"
{ yyval = b.methHead(val_peek(3),"run", val_peek(2), val_peek(0));}
break;
case 276:
//#line 1087 "e.y"
{ b.pocket(val_peek(5),"one-method-object");
                                          yyval = b.methHead(val_peek(4), val_peek(2), val_peek(0)); }
break;
case 277:
//#line 1090 "e.y"
{ b.pocket(val_peek(5),"one-method-object");
                                          yyval = b.methHead(val_peek(4), val_peek(2), val_peek(0)); }
break;
case 279:
//#line 1105 "e.y"
{ yyval = b.matcher(val_peek(1), val_peek(0)); }
break;
case 281:
//#line 1113 "e.y"
{ yyval = b.uriExpr(val_peek(0)); }
break;
case 283:
//#line 1115 "e.y"
{ yyval = b.call(val_peek(3), val_peek(2),"get", val_peek(1)); }
break;
case 284:
//#line 1116 "e.y"
{ yyval = b.propValue(val_peek(2), val_peek(0)); }
break;
case 290:
//#line 1128 "e.y"
{ yyval = val_peek(0); }
break;
case 291:
//#line 1129 "e.y"
{ yyval = b.defaultOptResultGuard(yylval); }
break;
case 292:
//#line 1130 "e.y"
{ b.reserved(val_peek(1),"throws"); }
break;
case 293:
//#line 1131 "e.y"
{ b.reserved(val_peek(1),"throws"); }
break;
case 294:
//#line 1134 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 295:
//#line 1135 "e.y"
{ yyval = b.with(val_peek(2),val_peek(0)); }
break;
case 296:
//#line 1143 "e.y"
{ yyval = b.when(val_peek(4), val_peek(1), val_peek(0)); }
break;
case 297:
//#line 1144 "e.y"
{ b.pocket(val_peek(1),"when-sequence");
                                              yyval = b.whenSeq(val_peek(4), val_peek(1), val_peek(0)); }
break;
case 298:
//#line 1160 "e.y"
{ /* binds __return */
                                  b.pocket(val_peek(5),"hard-when");
                                  yyval = ConstMap.fromPairs(new Object[][]{
                                         { "oName", val_peek(5) },
                                         { "whenParams", val_peek(3) },
				         { "whenGuard", b.forValue(val_peek(1), null) },
                                         { "bindReturn", Boolean.TRUE }
				       }).or((ConstMap)val_peek(0), true); }
break;
case 299:
//#line 1169 "e.y"
{ /* XXX should this bind __return ?? */
                                  /* Currently, it does. */
                                  b.pocket(val_peek(2),"easy-when");
                                  b.pocket(val_peek(2),"hard-when");
                                  yyval = ConstMap.fromPairs(new Object[][]{
                                         { "oName", val_peek(2) },
				         { "whenGuard", b.forValue(val_peek(1), null) },
                                         { "bindReturn", Boolean.TRUE }
				       }).or((ConstMap)val_peek(0), true); }
break;
case 300:
//#line 1179 "e.y"
{ /* Doesn't bind __return */
                                  b.pocket(val_peek(0),"easy-when");
                                  yyval = val_peek(0); }
break;
case 301:
//#line 1185 "e.y"
{ b.pocket(val_peek(1),"hard-when");
                                          yyval = val_peek(0); }
break;
case 302:
//#line 1187 "e.y"
{ yyval = b.defaultOptWhenGuard(yylval); }
break;
case 303:
//#line 1188 "e.y"
{ b.reserved(val_peek(1),"throws"); }
break;
case 304:
//#line 1189 "e.y"
{ b.reserved(val_peek(1),"throws"); }
break;
case 305:
//#line 1203 "e.y"
{ yyval = ConstMap.fromPairs(new Object[][]{
	                                    { "whenBody", val_peek(2) },
		                            { "whenCatches", val_peek(1) },
		                            { "whenFinally", val_peek(0) }}); }
break;
case 306:
//#line 1207 "e.y"
{ b.pocket(val_peek(0),"easy-when");
                                     yyval = ConstMap.fromPairs(new Object[][]{
	                                    { "whenBody", val_peek(0) }}); }
break;
case 311:
//#line 1229 "e.y"
{ yyval = b.list(); }
break;
case 312:
//#line 1233 "e.y"
{ yyval = b.list(); }
break;
case 314:
//#line 1242 "e.y"
{ b.pocket(val_peek(0),"verb-string");
                                          yyval = val_peek(0); }
break;
case 315:
//#line 1250 "e.y"
{ b.pocket(val_peek(0),"verb-curry");
                                          yyval = val_peek(0); }
break;
case 316:
//#line 1252 "e.y"
{ b.pocket(val_peek(0),"verb-curry");
                                          b.pocket(val_peek(0),"verb-string");
                                          yyval = val_peek(0); }
break;
case 317:
//#line 1265 "e.y"
{ b.pocket(val_peek(0),"dot-props");
                                          yyval = val_peek(0); }
break;
case 318:
//#line 1267 "e.y"
{ b.pocket(val_peek(0),"dot-props");
                                          yyval = val_peek(0); }
break;
case 319:
//#line 1275 "e.y"
{ yyval = b.varName(val_peek(0)); }
break;
case 320:
//#line 1276 "e.y"
{ b.pocket(val_peek(1),"noun-string");
                                          yyval = b.varName(val_peek(0)); }
break;
case 321:
//#line 1278 "e.y"
{ b.pocket(val_peek(1),"noun-string");
                                          yyval = b.varName(val_peek(0)); }
break;
case 323:
//#line 1287 "e.y"
{ b.reserved(val_peek(0),"keyword \"" +
                                     ((Astro)val_peek(0)).getTag().getTagName() +
                                     "\""); }
break;
case 324:
//#line 1303 "e.y"
{ yyval = b.ident(val_peek(0), "add"); }
break;
case 325:
//#line 1304 "e.y"
{ yyval = b.ident(val_peek(0), "and"); }
break;
case 326:
//#line 1305 "e.y"
{ yyval = b.ident(val_peek(0), "approxDivide"); }
break;
case 327:
//#line 1306 "e.y"
{ yyval = b.ident(val_peek(0), "floorDivide"); }
break;
case 328:
//#line 1307 "e.y"
{ yyval = b.ident(val_peek(0), "shiftLeft"); }
break;
case 329:
//#line 1308 "e.y"
{ yyval = b.ident(val_peek(0), "shiftRight"); }
break;
case 330:
//#line 1309 "e.y"
{ yyval = b.ident(val_peek(0), "remainder"); }
break;
case 331:
//#line 1310 "e.y"
{ yyval = b.ident(val_peek(0), "mod"); }
break;
case 332:
//#line 1311 "e.y"
{ yyval = b.ident(val_peek(0), "multiply"); }
break;
case 333:
//#line 1312 "e.y"
{ yyval = b.ident(val_peek(0), "or"); }
break;
case 334:
//#line 1313 "e.y"
{ yyval = b.ident(val_peek(0), "pow"); }
break;
case 335:
//#line 1314 "e.y"
{ yyval = b.ident(val_peek(0), "subtract"); }
break;
case 336:
//#line 1315 "e.y"
{ yyval = b.ident(val_peek(0), "xor"); }
break;
case 337:
//#line 1326 "e.y"
{ yyval = b.ident(val_peek(0), "add"); }
break;
case 338:
//#line 1327 "e.y"
{ yyval = b.ident(val_peek(0), "and"); }
break;
case 339:
//#line 1328 "e.y"
{ yyval = b.ident(val_peek(0), "approxDivide"); }
break;
case 340:
//#line 1329 "e.y"
{ yyval = b.ident(val_peek(0), "floorDivide"); }
break;
case 341:
//#line 1330 "e.y"
{ yyval = b.ident(val_peek(0), "shiftLeft"); }
break;
case 342:
//#line 1331 "e.y"
{ yyval = b.ident(val_peek(0), "shiftRight"); }
break;
case 343:
//#line 1332 "e.y"
{ yyval = b.ident(val_peek(0), "remainder"); }
break;
case 344:
//#line 1333 "e.y"
{ yyval = b.ident(val_peek(0), "mod"); }
break;
case 345:
//#line 1334 "e.y"
{ yyval = b.ident(val_peek(0), "multiply"); }
break;
case 346:
//#line 1335 "e.y"
{ yyval = b.ident(val_peek(0), "or"); }
break;
case 347:
//#line 1336 "e.y"
{ yyval = b.ident(val_peek(0), "pow"); }
break;
case 348:
//#line 1337 "e.y"
{ yyval = b.ident(val_peek(0), "subtract"); }
break;
case 349:
//#line 1338 "e.y"
{ yyval = b.ident(val_peek(0), "xor"); }
break;
case 350:
//#line 1347 "e.y"
{ yyval = b.getNULL(); }
break;
case 351:
//#line 1348 "e.y"
{ yyval = val_peek(1); }
break;
case 352:
//#line 1356 "e.y"
{ b.pocket(val_peek(2),"accumulator");
                                                  yyval = b.accumulate(val_peek(1),val_peek(0)); }
break;
case 353:
//#line 1361 "e.y"
{ yyval = b.accumFor(val_peek(3),val_peek(1),val_peek(0)); }
break;
case 354:
//#line 1362 "e.y"
{ yyval = b.accumIf(val_peek(1),val_peek(0)); }
break;
case 355:
//#line 1363 "e.y"
{ yyval = b.accumWhile(val_peek(1),val_peek(0)); }
break;
case 356:
//#line 1367 "e.y"
{ yyval = b.accumBody(val_peek(3),
                                                                 b.list(val_peek(2))); }
break;
case 357:
//#line 1369 "e.y"
{ yyval = b.accumBody(val_peek(3),val_peek(2)); }
break;
case 358:
//#line 1370 "e.y"
{ yyval = val_peek(2); }
break;
case 359:
//#line 1375 "e.y"
{ yyval = val_peek(1); }
break;
case 360:
//#line 1379 "e.y"
{ yyval = b.vTable(val_peek(2), val_peek(1)); }
break;
case 361:
//#line 1380 "e.y"
{ b.pocket(NO_POSER,
                                                           "plumbing");
                                                  yyval = b.vTable(null,
                                                                b.list(val_peek(0))); }
break;
case 363:
//#line 1395 "e.y"
{ yyval = b.with(val_peek(2), val_peek(1)); }
break;
case 366:
//#line 1405 "e.y"
{ yyval = b.with(val_peek(2), val_peek(1)); }
break;
case 369:
//#line 1422 "e.y"
{ yyval = b.with(val_peek(1), val_peek(0)); }
break;
case 370:
//#line 1426 "e.y"
{ yyval = null; }
break;
case 371:
//#line 1427 "e.y"
{ b.pocket(NO_POSER,
                                                           "escape-handler");
                                                  yyval = val_peek(0); }
break;
case 372:
//#line 1433 "e.y"
{ yyval = b.matcher(val_peek(1), val_peek(0)); }
break;
case 373:
//#line 1440 "e.y"
{ yyval = null; }
break;
case 374:
//#line 1441 "e.y"
{ yyval = val_peek(0); }
break;
case 375:
//#line 1451 "e.y"
{ yyval = b.oType("", val_peek(1), b.list(),
                                                       b.list(val_peek(0))); }
break;
case 376:
//#line 1460 "e.y"
{ yyval = null; }
break;
case 377:
//#line 1461 "e.y"
{ yyval = val_peek(0); }
break;
case 380:
//#line 1470 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 381:
//#line 1471 "e.y"
{ yyval = b.with(val_peek(2),val_peek(0)); }
break;
case 383:
//#line 1479 "e.y"
{ b.reserved(NO_POSER,"causality"); }
break;
case 384:
//#line 1484 "e.y"
{ yyval = b.mType(val_peek(6), val_peek(4), val_peek(2), val_peek(0)); }
break;
case 385:
//#line 1486 "e.y"
{ yyval = b.mType(val_peek(5), "run", val_peek(2), val_peek(0));}
break;
case 387:
//#line 1494 "e.y"
{ b.reserved(NO_POSER,"causality"); }
break;
case 388:
//#line 1498 "e.y"
{ yyval = b.mType("", "run", val_peek(2), val_peek(0)); }
break;
case 389:
//#line 1499 "e.y"
{ b.pocket(val_peek(5),"one-method-object");
                                            yyval = b.mType("", val_peek(4),    val_peek(2), val_peek(0)); }
break;
case 390:
//#line 1501 "e.y"
{b.pocket(val_peek(5),"one-method-object");
                                            yyval = b.mType("", val_peek(4),    val_peek(2), val_peek(0)); }
break;
case 391:
//#line 1506 "e.y"
{ yyval = val_peek(0); }
break;
case 392:
//#line 1507 "e.y"
{ yyval = val_peek(1); }
break;
case 393:
//#line 1512 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 394:
//#line 1513 "e.y"
{ yyval = b.with(val_peek(2),val_peek(0)); }
break;
case 395:
//#line 1520 "e.y"
{ yyval = b.pType(val_peek(1),val_peek(0)); }
break;
case 396:
//#line 1521 "e.y"
{ yyval = b.pType(null,val_peek(0)); }
break;
case 397:
//#line 1522 "e.y"
{ yyval = b.pType(null,val_peek(0)); }
break;
case 398:
//#line 1530 "e.y"
{ yyval = null; }
break;
case 399:
//#line 1531 "e.y"
{ yyval = val_peek(0); }
break;
case 413:
//#line 1547 "e.y"
{ yyval = "->"; }
break;
//#line 7045 "EParser.java"
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
