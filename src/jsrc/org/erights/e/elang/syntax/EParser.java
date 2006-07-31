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
import org.erights.e.elang.evm.EExpr;
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
//#line 41 "EParser.java"




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
public final static short BIND=274;
public final static short BREAK=275;
public final static short CATCH=276;
public final static short CONTINUE=277;
public final static short DEF=278;
public final static short ELSE=279;
public final static short ESCAPE=280;
public final static short EXIT=281;
public final static short EXTENDS=282;
public final static short FINALLY=283;
public final static short FN=284;
public final static short FOR=285;
public final static short GUARDS=286;
public final static short IF=287;
public final static short IMPLEMENTS=288;
public final static short IN=289;
public final static short INTERFACE=290;
public final static short MATCH=291;
public final static short META=292;
public final static short METHOD=293;
public final static short PRAGMA=294;
public final static short RETURN=295;
public final static short SWITCH=296;
public final static short TO=297;
public final static short TRY=298;
public final static short VAR=299;
public final static short VIA=300;
public final static short WHEN=301;
public final static short WHILE=302;
public final static short _=303;
public final static short ACCUM=304;
public final static short INTO=305;
public final static short MODULE=306;
public final static short ON=307;
public final static short SELECT=308;
public final static short THROWS=309;
public final static short THUNK=310;
public final static short ABSTRACT=311;
public final static short AN=312;
public final static short AS=313;
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
   29,   30,   30,   31,   31,   31,   31,   31,   31,   18,
   18,   18,   18,   18,   18,   18,   18,   33,   33,   33,
   33,   33,   33,   33,   33,   33,   33,   33,   33,   33,
   38,   38,   38,   38,   45,   45,   45,   45,   32,   32,
   32,   32,   32,   32,   32,   32,   32,   32,   32,   32,
   32,   32,   32,   32,   32,   60,   60,   61,   61,   61,
   61,   63,   63,   46,   46,   46,   71,   71,   71,   72,
   72,   49,   59,   59,   59,   53,   53,   57,   57,   74,
   47,   47,   48,   48,   76,   76,   77,   77,   40,   37,
   37,   19,   19,   50,   50,   50,   50,   79,   79,   51,
   51,   51,   80,   80,   80,   80,   73,   73,    4,    4,
    4,    4,   81,   81,   81,   81,   81,   81,   81,   81,
   81,   81,   81,   81,   81,   86,   84,   84,   89,   89,
   90,   90,   91,   91,   83,   83,   83,   82,   82,   82,
   82,   82,   16,   16,   17,   17,   92,   92,   64,   64,
   64,   64,   64,   43,   43,   93,   88,   88,   94,   94,
   87,   95,   95,   96,   96,   96,   96,   96,   96,   97,
   97,   98,   98,   62,   62,   62,   68,   68,   68,   68,
   68,   66,   66,   66,   66,   66,   99,  101,  101,  100,
  100,  102,    3,    3,    3,    3,  103,  103,  104,  104,
   70,   70,   70,  106,   26,   26,   26,   26,   26,   85,
   85,   85,   85,   85,  105,  105,  105,  105,  107,  107,
   58,   58,  108,  108,  108,  109,  109,  109,  109,  110,
  110,    1,    1,    8,    8,  112,   78,   39,   39,   34,
   34,   35,   35,   13,   13,   13,   75,   75,   14,   14,
   14,   14,   14,   14,   14,   14,   14,   14,   14,   14,
   14,  114,  114,  114,  114,  114,  114,  114,  114,  114,
  114,  114,  114,  114,   44,   44,   41,  115,  115,  115,
  116,  116,  116,   54,   69,   69,  118,  118,  119,  117,
  117,   55,   55,  111,   52,   52,  120,   56,   56,   11,
   65,   65,   67,   67,  122,  122,  123,  123,  124,  124,
  121,  121,  127,  127,  127,  125,  125,  128,  128,  129,
  129,  129,  126,  126,   36,   36,   42,   42,   42,   42,
   42,   42,   42,   42,   42,   42,   42,   42,  113,  113,
  113,  113,  113,  113,  113,  113,  113,  113,  113,  113,
  113,  113,  113,  113,  113,  113,  113,  113,  113,  113,
  113,  113,  113,  113,  113,  113,  113,  113,  113,  113,
  113,  113,  113,  113,  113,  113,  113,  113,  113,  113,
  113,  113,  113,  113,  113,  113,  113,  113,  113,  113,
  113,  113,  113,  113,  113,  113,  113,  113,  113,  113,
  113,  113,  113,  113,  113,  113,  113,  113,  113,  113,
  113,  113,  113,  113,  113,  113,  113,  113,  113,  113,
  113,  113,  113,
};
final static short yylen[] = {                            2,
    1,    1,    5,    2,    1,    1,    1,    3,    1,    3,
    1,    2,    3,    1,    1,    3,    2,    2,    1,    2,
    1,    2,    3,    3,    3,    4,    3,    3,    6,    1,
    2,    5,    1,    3,    1,    3,    1,    3,    3,    1,
    1,    3,    3,    3,    3,    3,    3,    3,    3,    1,
    3,    3,    3,    3,    3,    3,    1,    3,    3,    1,
    3,    3,    1,    3,    3,    1,    3,    3,    3,    3,
    3,    1,    3,    1,    2,    2,    2,    2,    2,    1,
    3,    3,    3,    4,    3,    4,    5,    1,    4,    1,
    4,    4,    4,    3,    2,    4,    4,    4,    3,    1,
    7,    4,    4,    6,    1,    1,    1,    1,    1,    1,
    1,    2,    1,    3,    3,    1,    4,    4,    3,    4,
    1,    2,    1,    3,    1,    1,    2,    2,    8,    2,
    3,    2,    2,    1,    1,    1,    3,    2,    2,    3,
    2,    3,    3,    5,    5,    1,    3,    6,    5,    1,
    0,    1,    1,    2,    2,    3,    1,    3,    3,    1,
    1,    1,    2,    1,    1,    3,    2,    1,    3,    1,
    3,    2,    3,    3,    4,    4,    1,    3,    1,    3,
    3,    4,    1,    1,    2,    2,    2,    2,    5,    7,
    5,    1,    3,    3,    5,    3,    1,    2,    2,    3,
    1,    1,    1,    5,    1,    3,    2,    3,    1,    1,
    1,    1,    4,    2,    4,    2,    4,    2,    1,    1,
    2,    2,    1,    1,    3,    1,    1,    3,    1,    3,
    3,    1,    3,    3,    5,    5,    2,    4,    4,    1,
    1,    0,    1,    2,    2,    2,    0,    1,    1,    2,
    2,    0,    1,    1,    2,    2,    2,    2,    3,    2,
    3,    1,    1,    1,    2,    2,    4,    4,    4,    5,
    4,    6,    6,    3,    1,    1,    1,    4,    3,    1,
    1,    1,    1,    1,    2,    0,    4,    2,    1,    3,
    6,    6,    6,    3,    2,    2,    0,    4,    2,    3,
    1,    0,    1,    1,    2,    0,    1,    1,    1,    1,
    1,    1,    1,    1,    2,    2,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    3,    3,    3,    5,    3,    3,
    7,    8,    5,    4,    5,    1,    1,    3,    1,    1,
    3,    1,    1,    2,    0,    1,    3,    0,    2,    3,
    0,    2,    1,    2,    1,    3,    1,    2,    7,    6,
    1,    2,    4,    6,    6,    2,    3,    1,    3,    2,
    2,    2,    0,    2,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,
};
final static short yydefred[] = {                         0,
  304,    0,    0,    0,    2,    0,  317,    0,    0,    0,
    0,  395,  396,    0,    0,    0,  450,  409,  410,  411,
  412,  413,  414,  415,  416,  417,  418,  419,  420,  421,
  422,  423,  424,  425,  426,  427,  428,  429,  430,  431,
  432,  433,  434,  435,  436,  437,  438,  439,  440,  441,
  442,  443,  444,  445,  446,  447,  448,  449,  451,  452,
  453,  454,  455,  456,  457,  458,  459,  460,  461,  462,
  463,  464,  465,  466,  467,  468,  469,  470,  471,  472,
  473,  474,  475,  476,  477,  478,  479,  480,  481,  482,
  483,  484,  485,  486,  487,  488,  489,  490,  491,  492,
  493,    0,    0,  281,  282,  283,    0,    0,  280,  284,
    0,    0,    0,    0,    4,  134,  210,  211,    0,    0,
    0,  135,  136,    0,    0,  183,  184,    0,    0,  212,
  318,  105,  106,  107,  263,  264,  111,    0,    0,    5,
    6,    0,    0,    0,    0,    0,    0,    7,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   11,   14,   19,    0,    0,    0,    0,    0,   35,    0,
    0,    0,    0,    0,    0,    0,   66,    0,    0,    0,
    0,    0,  100,  116,  109,  110,    0,  113,  121,  123,
  125,  126,    0,  305,    0,    0,    0,  314,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  186,  187,  316,
  315,    0,  276,    0,  275,  277,    0,  227,    0,    0,
  138,  139,  141,    0,    0,    0,  197,  185,    0,    0,
  188,    0,   20,  127,    0,    0,    0,    0,    0,    0,
    0,    0,  244,    0,    0,    0,  224,    0,    0,    0,
    0,    0,    0,  220,  219,    0,    0,  306,    0,    0,
  122,    0,    0,    0,    0,    0,  130,    0,    0,   18,
    0,   79,    0,    0,    0,    0,    0,    0,    0,  164,
    0,  170,    0,    0,  265,  266,    0,   17,    8,    0,
    0,    0,    0,    0,  319,  320,  321,  322,  323,  324,
  325,  326,  327,  328,  329,  330,  331,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   95,  406,  397,  398,  399,  400,  404,  401,  402,  405,
  403,  408,    0,  407,    0,  153,  112,    0,    0,    0,
    0,    0,    0,  128,    0,    0,    0,    0,  137,  140,
    0,    0,    0,    0,    0,  181,    0,  245,    0,  246,
    0,    0,    0,    0,  229,  241,  240,    0,    0,  232,
    0,    0,  196,    0,    0,  157,  203,    0,    0,  201,
  199,  202,    0,  198,  180,  194,    0,    0,    0,    0,
    0,    0,    0,  131,    0,    0,    0,    0,  221,  222,
    0,    0,    0,    0,    0,  370,    0,    0,  119,    0,
  363,  362,    0,    0,    0,    0,    0,    0,    0,    0,
  347,    0,  124,  142,    0,    0,  114,    0,  115,    0,
  345,  346,   16,    0,   13,    0,   30,   25,    0,   23,
   24,   27,   28,    0,    0,   82,    0,   94,    0,    0,
    0,   83,  312,    0,   81,    0,  168,  161,    0,  160,
    0,   36,   38,   39,   43,   44,   45,   42,   46,   48,
   47,   49,    0,   52,   53,   54,   51,   55,    0,    0,
    0,    0,    0,    0,   69,   71,   67,   68,   70,   73,
    0,    0,    0,    0,   99,  308,   85,    0,    0,    0,
    0,    0,  155,    0,  154,  262,  257,  260,    0,    0,
    0,    0,    0,  132,  356,  133,    0,    0,  251,    0,
    0,    0,    0,    0,    0,  279,    0,    0,    0,  228,
    0,  231,    0,    0,  182,    0,    0,  200,    0,   26,
    0,  117,  366,  225,  178,  150,    0,  147,    0,  372,
    0,    0,    0,    0,    0,    0,    0,    0,  382,  306,
    0,  120,  364,    0,    0,  118,    0,    0,    0,    3,
    0,    0,  174,  173,    0,    0,  171,    0,   31,    0,
    0,   86,   93,   84,   91,   92,    0,  102,    0,   98,
  159,   96,   97,    0,  103,  156,    0,    0,    0,    0,
  306,  261,  189,    0,  191,  278,    0,    0,  230,  233,
    0,  195,  158,    0,    0,    0,    0,  145,  144,    0,
    0,    0,    0,  386,    0,  388,    0,    0,  258,    0,
    0,    0,    0,  360,    0,  369,    0,  149,    0,    0,
  349,  350,  176,  175,    0,   87,  169,    0,    0,    0,
    0,    0,  271,    0,  274,  357,    0,    0,  239,  238,
    0,    0,    0,   29,  367,  148,    0,    0,  391,    0,
  390,    0,  387,  383,    0,    0,  259,  354,    0,    0,
    0,    0,    0,    0,  104,    0,    0,    0,    0,    0,
  243,    0,    0,    0,    0,  190,  236,  235,  204,    0,
    0,  389,    0,    0,    0,  373,    0,  375,    0,  361,
    0,    0,  292,    0,  291,    0,  348,    0,    0,   32,
  101,  272,    0,    0,  273,    0,    0,  358,  355,  384,
  385,  129,    0,  374,    0,  378,    0,    0,    0,    0,
    0,  295,  336,  337,  335,  339,  342,  344,  333,  341,
  332,  343,  340,  334,  338,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  376,    0,    0,    0,
  294,    0,    0,    0,    0,  353,    0,    0,  268,  267,
    0,    0,    0,    0,  300,    0,    0,    0,    0,    0,
    0,    0,    0,  351,  269,    0,  380,    0,  293,  352,
  270,  379,
};
final static short yydgoto[] = {                          3,
  255,  497,  167,  258,  168,  169,  170,    6,  171,  172,
  173,  174,  116,  318,  478,  117,  118,  177,  498,  178,
  179,  180,  181,  182,  183,  727,  184,  185,  186,  187,
  188,  189,  190,  486,  492,  191,  499,  192,  802,  361,
  193,  373,  256,  194,  195,  196,  197,  377,  198,  288,
  289,  582,  261,  449,  450,  602,  199,  271,  200,  201,
  202,  203,  384,  253,  445,  596,  744,  385,  554,  386,
  122,  123,  259,  587,  124,  378,  420,  257,  501,  292,
  125,  126,  127,  238,  128,  129,  229,  230,  239,  421,
  422,  130,  432,  408,  409,  410,  411,  745,  387,  388,
  598,  547,  733,  803,  693,  719,  728,  755,  756,  782,
  451,  452,  131,  797,  461,  681,  675,  697,  735,  583,
  446,  747,  748,  749,  593,  709,  447,  665,  666,
};
final static short yysindex[] = {                      -213,
    0,15095,    0,10382,    0, -120,    0, -105,  -62,13207,
16184,    0,    0,16184,  220,  243,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,13066,13066,    0,    0,    0,17325,16184,    0,    0,
15824,  151,   -5,   48,    0,    0,    0,    0,  220,  364,
  281,    0,    0,    0,  362,    0,    0,13066,  387,    0,
    0,    0,    0,    0,    0,    0,    0,  441,13207,    0,
    0,14446,15095,  151,15095,  220, 8319,    0,  220,  336,
16184,14955,  220,12545,  298,  220,  336,  151,11535,12545,
13066,12545,12545,12545,  151,  151,  373,11898,  151,  414,
    0,    0,    0, 1343,   78,   81,   31,   98,    0,  -22,
  469,  400,   33,  270,  396,   44,    0,  103,  252,  501,
   -7,17057,    0,    0,    0,    0,  418,    0,    0,    0,
    0,    0,  489,    0,  433,  440,  498,    0,  430,  512,
15095,15824,  576,13207, 8319, 8319,16184,    0,    0,    0,
    0,  516,    0,  -64,    0,    0,13426,    0,  492,  497,
    0,    0,    0,  165,15824,  720,    0,    0,  423,  220,
    0,15464,    0,    0,  498,13207,16184,  243,  373, -211,
    0,  364,    0,  336,15095,  336,    0,  171,  314,  173,
  336,13207,16184,    0,    0,   87,  485,    0,  512,  151,
    0,  331,  336,  312,  283,  485,    0,11393,  584,    0,
  -42,    0,  -42,  -42,  -42,10524,  205,  551,   82,    0,
  590,    0,10888,  522,    0,    0,   42,    0,    0,11030,
11393,12040,12403,11535,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,11535,11535,11535,
12694, 3667,15824,17191,  151,12403,12403,12403,12403,15095,
15095,12403,12403,12403,12403,12403,12403,12403,12403,12403,
12403,12403,12403,12403,12403,12403,12403,12403,12403,12403,
12403,12403,12403,  151,  151,16657,17191,  151,17191,  151,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    6,    0,  212,    0,    0,  431,12403,12403,
17191,  151,17191,    0,  -87,  336,  365,   -3,    0,    0,
15824,  151,17191,  151,15824,    0,  -64,    0,  370,    0,
15824,17191,  151,15675,    0,    0,    0,   -2,   54,    0,
  240,  555,    0,15095,  -64,    0,    0,  151,  151,    0,
    0,    0,  720,    0,    0,    0,  498,  512,12545,11535,
  446,  151,  655,    0,15095,12403,15095,  428,    0,    0,
15095,17191,  151,17191, -178,    0,  336,  151,    0,   14,
    0,    0,11393,    0,  675,12403,  446,15095,  220,  220,
    0,  151,    0,    0,16035,  151,    0,  151,    0,  151,
    0,    0,    0,  414,    0,   53,    0,    0,   98,    0,
    0,    0,    0, 4099,  373,    0,  684,    0,    0,17191,
  373,    0,    0,  -64,    0,  684,    0,    0,  634,    0,
  699,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  -42,    0,    0,    0,    0,    0,  233,  233,
  396,  396,   44,   44,    0,    0,    0,    0,    0,    0,
  336,  703,  373,  684,    0,    0,    0,  704,  684,  654,
  151,  336,    0,  212,    0,    0,    0,    0,  712,  718,
  723,15095,  151,    0,    0,    0,  722,12403,    0,  -64,
  726,  729,  677,  -64,  -64,    0,  686, -229,15095,    0,
  -12,    0,15095,15464,    0,  656,15095,    0,   18,    0,
15095,    0,    0,    0,    0,    0,  336,    0,  -56,    0,
  742,16523,  751,  743,12403,  671,    7,  -10,    0,    0,
  336,    0,    0,  151,  336,    0,  506,  673,  673,    0,
13207,16184,    0,    0,10524,  205,    0,    0,    0,   75,
17191,    0,    0,    0,    0,    0,  151,    0,17057,    0,
    0,    0,    0,  759,    0,    0,  151,  -47,  151,  336,
    0,    0,    0,  151,    0,    0,12403,12403,    0,    0,
 -222,    0,    0,  151,11535,  336,  446,    0,    0,  151,
  744,15824,  744,    0,   80,    0,  744,  151,    0,  151,
  769,12403,  722,    0,  -71,    0,  394,    0,12403,  151,
    0,    0,    0,    0,  151,    0,    0,  151,  336,  773,
15824,15824,    0,  774,    0,    0,  544,  780,    0,    0,
12403,12403,  697,    0,    0,    0,  783,15824,    0,  -64,
    0,16523,    0,    0,  785,  544,    0,    0,  151,15315,
  673,  279,  788,  336,    0,  -47,  -64,  789,  -73,  -47,
    0,  124,  151,  547,  707,    0,    0,    0,    0,  744,
  -64,    0,  744,  715,  546,    0,  151,    0,  336,    0,
15824,15824,    0,    3,    0,  336,    0,  932,  151,    0,
    0,    0,15824,15824,    0,16791,16791,    0,    0,    0,
    0,    0,16925,    0,  -97,    0,  789,  -66,  151,  336,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,17191,11535,  727,  -64,  789,
  151,  808,  336,  336,  151,  813,    0,15824,15095,  814,
    0,  446,  573,  684,  151,    0,  816,  151,    0,    0,
  820,  151,  789,  -41,    0,  151,  746,  -47,  829,  744,
  831,  336,  749,    0,    0,  -47,    0,  744,    0,    0,
    0,    0,
};
final static short yyrindex[] = {                      1527,
    0,  447,    0,   37,    0, 1131,    0,    0,    0,    0,
    0,    0,    0,    0,    0, 6592,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  447,  447,    0,    0,    0,    0,    0,    0,    0,
    0,12905,    0,    0,    0,    0,    0,    0,    0, 6776,
    0,    0,    0, 1685, 7953,    0,    0,  447, 6809,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  447,  447,13568,  447,    0,    0,    0,    0,    0,
    0,  447,    0,  447,    0,    0,    0,10019,  447,  447,
  447,  447,  447,  447, 8503, 9372, 2380,   19,   76,  493,
    0,    0,    0,  372,    0,    0, 4707,  851,    0, 2202,
 2634, 3066, 6383, 6100, 5839,  956,    0, 5405, 3412, 4538,
    0, 3675,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  -58,    0,    0,    0, 6986,    0,    0, 7029,
  447,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0, 7084,    0, 7261,    0,    0,  245,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  447,    0,    0,  745,    0,    0,  262, 1997,    0,
  105,  795,    0,    0,  391,    0,    0,  587,    0,  754,
    0,    0,    0,    0,    0,  327,    0,    0, 1648, 9514,
    0,    0,    0,    0,    0,    0,    0,  447,    0,    0,
 4774,    0, 4970, 5169, 5236,  245,  393,    0,    0,    0,
    0,    0,  447,    0,    0,    0,10019,    0,    0,   15,
   88,  447,  447,  447,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  447,  447,  447,
    0,    0,    0,    0, 9877,  447,  447,  447,  447,  447,
  447,  447,  447,  447,  447,  447,  447,  447,  447,  447,
  447,  447,  447,  447,  447,  447,  447,  447,  447,  447,
  447,  447,  447,13568, 9514,    0,    0, 9514,    0, 9877,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,13568,    0,    0,    0,    0,    0,  447,  447,
    0,13937,    0,    0,    0,    0,  -38,   27,    0,    0,
    0,13937,    0,14077,    0,    0, 7296,    0,  327,    0,
    0,    0, 9877,    0,    0,    0,    0,  147,  791,    0,
    0, 7469,    0,  447, 7523,    0,    0,10019,14586,    0,
    0,    0,    0,    0,    0,    0, 1647, 1652,  447,  447,
 2549,   55,  397,    0,  447,  447,  447, 2812,    0,    0,
  447,    0,16391,    0,  758,    0,  790,   99,    0, 2981,
    0,    0,  272,   90,    0,  447, 2549,  447,    0,    0,
    0,10019,    0,    0,    0,10019,    0, 8139,    0, 8867,
    0,    0,    0,  867,    0,10019,    0,    0,  909,    0,
    0,    0,    0,    0, 3844,    0,    0,    0, 4107,    0,
 3243,    0,    0, 6556,    0,    0,    0,    0,    0,    0,
  334,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0, 4707,    0,    0,    0,    0,    0, 6307, 6346,
 5875, 5915, 5630, 5666,    0,    0,    0,    0,    0,    0,
    0,    0,  853,    0,    0,    0,    0,    0,    0,    0,
 9514,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  447,  303,    0,    0,    0,   91,  447,    0, 7566,
    0,    0,    0, 7735, 7777,    0,    0,   30,  447,    0,
    0,    0,  447,  447,    0,    0,  447,    0,    0,    0,
  447,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  855,    0,    0,  447,    0,  771,  776,    0,    0,
    0,    0,    0,  475,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  447,    0,    0,   84,    0,    0,
    0,    0,    0,    0,    0,    0, 9009,    0, 4276,    0,
    0,    0,    0,    0,    0,    0,13937,  779,13937,    0,
    0,    0,    0,13937,    0,    0,  447,  447,    0,    0,
   86,    0,    0,  782,  447,    0, 2549,    0,    0,16391,
   95,    0,   95,    0,  864,    0,  682,16391,    0,  -68,
  794,  447,  796,    0,    0,    0,    0,    0,  447,  308,
    0,    0,    0,    0,10019,    0,    0,13568,    0,    0,
    0,    0,    0,    0,    0,    0,  169,    0,    0,    0,
  447,  447,    0,    0,    0,    0,    0,    0,    0,  197,
    0,    0,    0,    0,    0,  -63,    0,    0,   99,  798,
    0,    0,    0,    0,    0,  779,   63,  800,  803,  779,
    0,    0,  303,  787,    0,    0,    0,    0,    0,  682,
  705,    0,  682,    0,    0,    0,  782,    0,  -25,    0,
    0,    0,    0,  798,    0,    0,    0,    0,  782,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   66,    0,  804,  805,13937,    0,
 1948,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  447,    0,   67,  807,
13937,    0,    0,    0,16391,    0,    0,    0,   56,    0,
    0,    0, 2117,    0,  782,    0,    0,13937,    0,    0,
    0,16391,  810,  798,    0,  782,    0,  779,    0,  402,
    0,    0,    0,    0,    0,  779,    0,  402,    0,    0,
    0,    0,
};
final static short yygindex[] = {                         0,
    1,  101,  523,   69,    0,    0,  613, -167,  630, -146,
  797,    0,   -4,    0,    0,   28,   49, 1799,  229,  635,
 -253, 1373,    0,    0,  471,  177,  320,  388,  389,  539,
  588,  593,    0,  622,  -93,   64,  -24,    0, -301, -316,
    0,  318, -344,  456, -197,   10,   50,    0,  377,  667,
    0, -422, -144,  676,  168,  138,    0,  234,  366,    0,
  -48,    0,    0, -147,    0,    0,    0,    0,    0,    0,
    0,    0, -138, -409,  541,    0, -300,  -86,  -51,  490,
 -235,  553,    0,    0,    0,    0,    0,  180,    0,  538,
    0,    0,    0,  708,    0,  395,    0,  267,  583, -342,
  375, -312,    0,  206,  -39,  595, -233,    0, -639, -638,
  200,   68, -189,    0,  260, -522,  287,    0,    0, -442,
    0,    0,  211,    0, -111,  -37,    0,    0,  284,
};
final static int YYTABLESIZE=17720;

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
        if (hash != 5597256968383022868L) {
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
"DocComment","BIND","BREAK","CATCH","CONTINUE","DEF","ELSE","ESCAPE","EXIT",
"EXTENDS","FINALLY","FN","FOR","GUARDS","IF","IMPLEMENTS","IN","INTERFACE",
"MATCH","META","METHOD","PRAGMA","RETURN","SWITCH","TO","TRY","VAR","VIA",
"WHEN","WHILE","_","ACCUM","INTO","MODULE","ON","SELECT","THROWS","THUNK",
"ABSTRACT","AN","AS","ASSERT","ATTRIBUTE","BE","BEGIN","BEHALF","BELIEF",
"BELIEVE","BELIEVES","CASE","CLASS","CONST","CONSTRUCTOR","DATATYPE","DECLARE",
"DEFAULT","DEFINE","DEFMACRO","DELEGATE","DELICATE","DEPRECATED","DISPATCH",
"DO","ENCAPSULATE","ENCAPSULATED","ENCAPSULATES","END","ENSURE","ENUM",
"EVENTUAL","EVENTUALLY","EXPORT","FACET","FORALL","FUN","FUNCTION","GIVEN",
"HIDDEN","HIDES","INLINE","KNOW","KNOWS","LAMBDA","LET","METHODS","NAMESPACE",
"NATIVE","OBEYS","OCTET","ONEWAY","OPERATOR","PACKAGE","PRIVATE","PROTECTED",
"PUBLIC","RAISES","RELIANCE","RELIANT","RELIES","RELY","REVEAL","SAKE","SIGNED",
"STATIC","STRUCT","SUCHTHAT","SUPPORTS","SUSPECT","SUSPECTS","SYNCHRONIZED",
"THIS","TRANSIENT","TRUNCATABLE","TYPEDEF","UNSIGNED","UNUM","USES","USING",
"UTF8","UTF16","VIRTUAL","VOLATILE","WSTRING","OpLAnd","OpLOr","OpSame",
"OpNSame","OpButNot","OpLeq","OpABA","OpGeq","OpThru","OpTill","OpAsl","OpAsr",
"OpFlrDiv","OpMod","OpPow","OpAss","OpAssAdd","OpAssAnd","OpAssAprxDiv",
"OpAssFlrDiv","OpAssAsl","OpAssAsr","OpAssRemdr","OpAssMod","OpAssMul",
"OpAssOr","OpAssPow","OpAssSub","OpAssXor","Send","OpWhen","MapsTo","MatchBind",
"MisMatch","OpScope","AssignExpr","CallExpr","DefineExpr","EscapeExpr",
"HideExpr","IfExpr","LiteralExpr","NounExpr","ObjectExpr","QuasiLiteralExpr",
"QuasiPatternExpr","MetaStateExpr","MetaContextExpr","SeqExpr","SlotExpr",
"MetaExpr","CatchExpr","FinallyExpr","FinalPattern","SlotPattern","ListPattern",
"IgnorePattern","QuasiLiteralPatt","QuasiPatternPatt","EScript","EMethod",
"EMatcher",
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
"prefix : '*' postfix",
"prefix : '-' prim",
"postfix : call",
"postfix : postfix '.' vcurry",
"postfix : postfix Send vcurry",
"postfix : postfix OpScope prop",
"postfix : postfix OpScope '&' prop",
"postfix : metaoid OpScope prop",
"postfix : postfix Send OpScope prop",
"postfix : postfix Send OpScope '&' prop",
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
"map : br MapsTo DEF noun",
"iterPattern : pattern",
"iterPattern : pattern MapsTo pattern",
"pattern : subPattern",
"pattern : subPattern '?' parenExpr",
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
"oDeclTail :",
"oDeclTail : extends",
"oDeclTail : impls",
"oDeclTail : extends impls",
"oDeclTail : impls extends",
"iDeclTail :",
"iDeclTail : iExtends",
"iDeclTail : impls",
"iDeclTail : iExtends impls",
"iDeclTail : impls iExtends",
"extends : EXTENDS base",
"iExtends : EXTENDS base",
"iExtends : iExtends ',' base",
"impls : IMPLEMENTS base",
"impls : impls ',' base",
"base : order",
"litString : LiteralString",
"litString : LiteralTwine",
"litString : litString LiteralString",
"litString : litString LiteralTwine",
"method : doco TO methHead body",
"method : doco METHOD methHead body",
"methHead : '(' paramList ')' resultGuard",
"methHead : verb '(' paramList ')' resultGuard",
"funcHead : '(' paramList ')' resultGuard",
"funcHead : TO verb '(' paramList ')' resultGuard",
"funcHead : '.' verb '(' paramList ')' resultGuard",
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
"whenTail : whenGuard whenBody",
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
"reserved : AS",
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

//#line 1541 "e.y"


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
    return ENodeBuilder.noun(name);
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
    TheTokens[AS]               = "as";
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
 *
 */
static private final ObjDecl ODECL = ObjDecl.EMPTY;

/**
 * Used to mark places where we should be providing a poser (an object
 * from which source position info can be derived).
 */
static private final Object NO_POSER = BaseEBuilder.NO_POSER;
//#line 5518 "EParser.java"
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
//#line 208 "e.y"
{ myOptResult = null; }
break;
case 2:
//#line 209 "e.y"
{ myOptResult = b.forValue(val_peek(0), null); }
break;
case 3:
//#line 212 "e.y"
{ b.reserved(val_peek(3),"module"); }
break;
case 4:
//#line 215 "e.y"
{ myOptResult = (Pattern)val_peek(0); }
break;
case 5:
//#line 222 "e.y"
{ yyval = b.get__BREAK(); }
break;
case 6:
//#line 223 "e.y"
{ yyval = b.get__CONTINUE(); }
break;
case 7:
//#line 224 "e.y"
{ yyval = b.get__RETURN(); }
break;
case 8:
//#line 237 "e.y"
{ yyval = val_peek(1); }
break;
case 10:
//#line 247 "e.y"
{ yyval = b.sequence(val_peek(2), val_peek(0)); }
break;
case 13:
//#line 256 "e.y"
{ yyval = b.sequence(val_peek(2), val_peek(0)); }
break;
case 15:
//#line 274 "e.y"
{ yyval = b.ejector(val_peek(0)); }
break;
case 16:
//#line 275 "e.y"
{ yyval = b.ejector(val_peek(2)); }
break;
case 17:
//#line 276 "e.y"
{ yyval = b.ejector(val_peek(1), val_peek(0)); }
break;
case 18:
//#line 277 "e.y"
{ b.pocket(val_peek(1),"smalltalk-return");
                                          yyval = b.ejector(b.get__RETURN(),val_peek(0));}
break;
case 19:
//#line 279 "e.y"
{ yyval = b.doco("",val_peek(0)); }
break;
case 20:
//#line 280 "e.y"
{ yyval = b.doco(val_peek(1),val_peek(0)); }
break;
case 22:
//#line 293 "e.y"
{ yyval = b.forward(val_peek(0)); }
break;
case 23:
//#line 294 "e.y"
{ yyval = b.assign(val_peek(2),     val_peek(0)); }
break;
case 24:
//#line 295 "e.y"
{ yyval = b.update(val_peek(2), val_peek(1), b.list(val_peek(0))); }
break;
case 25:
//#line 296 "e.y"
{ yyval = b.update(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 26:
//#line 298 "e.y"
{ yyval = b.define(val_peek(2), val_peek(0)); }
break;
case 27:
//#line 299 "e.y"
{ yyval = b.define(val_peek(2), val_peek(0)); }
break;
case 28:
//#line 300 "e.y"
{ yyval = b.define(val_peek(2), val_peek(0)); }
break;
case 29:
//#line 303 "e.y"
{ b.pocket(val_peek(3),"trinary-define");
                                          yyval = b.define(val_peek(4),val_peek(2),val_peek(0)); }
break;
case 30:
//#line 311 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 31:
//#line 312 "e.y"
{ yyval = b.list(); }
break;
case 32:
//#line 313 "e.y"
{ yyval = b.append(b.list(val_peek(3)),val_peek(1)); }
break;
case 34:
//#line 322 "e.y"
{ yyval = b.condOr(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 36:
//#line 331 "e.y"
{ yyval = b.condAnd(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 38:
//#line 343 "e.y"
{ yyval = b.same(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 39:
//#line 344 "e.y"
{ yyval = b.not(val_peek(1), b.same(val_peek(2), val_peek(1), val_peek(0))); }
break;
case 42:
//#line 347 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"xor", val_peek(0)); }
break;
case 43:
//#line 348 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"butNot", val_peek(0)); }
break;
case 44:
//#line 350 "e.y"
{ yyval = b.matchBind(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 45:
//#line 351 "e.y"
{ yyval = b.not(val_peek(1),
                                                     b.matchBind(val_peek(2),val_peek(1),val_peek(0))); }
break;
case 46:
//#line 356 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"and", val_peek(0)); }
break;
case 47:
//#line 357 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"and", val_peek(0)); }
break;
case 48:
//#line 360 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"or", val_peek(0)); }
break;
case 49:
//#line 361 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"or", val_peek(0)); }
break;
case 51:
//#line 373 "e.y"
{ yyval = b.lessThan(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 52:
//#line 374 "e.y"
{ yyval = b.leq(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 53:
//#line 375 "e.y"
{ yyval = b.asBigAs(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 54:
//#line 376 "e.y"
{ yyval = b.geq(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 55:
//#line 377 "e.y"
{ yyval = b.greaterThan(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 56:
//#line 381 "e.y"
{ b.pocket(val_peek(1),"cast");
                                          yyval = b.cast(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 58:
//#line 390 "e.y"
{ yyval = b.thru(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 59:
//#line 391 "e.y"
{ yyval = b.till(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 61:
//#line 400 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"shiftLeft", val_peek(0)); }
break;
case 62:
//#line 401 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"shiftRight",val_peek(0)); }
break;
case 64:
//#line 410 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"add", val_peek(0)); }
break;
case 65:
//#line 411 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"subtract",val_peek(0)); }
break;
case 67:
//#line 422 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"multiply", val_peek(0)); }
break;
case 68:
//#line 423 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"approxDivide", val_peek(0)); }
break;
case 69:
//#line 424 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"floorDivide", val_peek(0)); }
break;
case 70:
//#line 425 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"remainder", val_peek(0)); }
break;
case 71:
//#line 426 "e.y"
{ yyval = b.mod(val_peek(2), val_peek(1), b.list(val_peek(0))); }
break;
case 73:
//#line 435 "e.y"
{ yyval = b.binop(val_peek(2), val_peek(1),"pow", val_peek(0)); }
break;
case 75:
//#line 453 "e.y"
{ yyval = b.call(val_peek(0), val_peek(1),"not", b.list()); }
break;
case 76:
//#line 454 "e.y"
{ yyval = b.call(val_peek(0), val_peek(1),"complement", b.list());}
break;
case 77:
//#line 455 "e.y"
{ yyval = b.slotExpr(val_peek(1), val_peek(0)); }
break;
case 78:
//#line 456 "e.y"
{ b.pocket(val_peek(1),"unary-star");
                                  yyval = b.call(val_peek(0), val_peek(1),"getValue", b.list()); }
break;
case 79:
//#line 458 "e.y"
{ yyval = b.call(val_peek(0), val_peek(1),"negate", b.list()); }
break;
case 81:
//#line 467 "e.y"
{ yyval = b.callFacet(val_peek(2), val_peek(0)); }
break;
case 82:
//#line 468 "e.y"
{ yyval = b.sendFacet(val_peek(2), val_peek(0)); }
break;
case 83:
//#line 470 "e.y"
{ yyval = b.propValue(val_peek(2), val_peek(0)); }
break;
case 84:
//#line 471 "e.y"
{ yyval = b.propSlot(val_peek(3), val_peek(0)); }
break;
case 85:
//#line 472 "e.y"
{ yyval = b.doMetaProp(val_peek(2), val_peek(0)); }
break;
case 86:
//#line 474 "e.y"
{ yyval = b.sendPropValue(val_peek(3), val_peek(0)); }
break;
case 87:
//#line 475 "e.y"
{ yyval = b.sendPropSlot(val_peek(4), val_peek(0)); }
break;
case 89:
//#line 487 "e.y"
{ yyval = b.call(val_peek(3), val_peek(2),"run", val_peek(1)); }
break;
case 90:
//#line 488 "e.y"
{ b.pocket(NO_POSER,"lambda-args");
                                          yyval = b.call(val_peek(0),
                                                      "run__control",
                                                      b.list()); }
break;
case 91:
//#line 493 "e.y"
{ yyval = b.call(val_peek(3), val_peek(1), val_peek(0)); }
break;
case 92:
//#line 494 "e.y"
{ yyval = b.call(val_peek(3), val_peek(2),"get", val_peek(1)); }
break;
case 93:
//#line 495 "e.y"
{ yyval = b.send(val_peek(3), val_peek(1), val_peek(0)); }
break;
case 94:
//#line 496 "e.y"
{ yyval = b.send(val_peek(2), val_peek(1),"run", val_peek(0)); }
break;
case 95:
//#line 498 "e.y"
{ yyval = b.doMeta(val_peek(1), val_peek(1),"run", val_peek(0)); }
break;
case 96:
//#line 499 "e.y"
{ yyval = b.doMeta(val_peek(3), val_peek(1), val_peek(0)); }
break;
case 97:
//#line 500 "e.y"
{ yyval = b.doMeta(val_peek(3), val_peek(2),"get", val_peek(1)); }
break;
case 98:
//#line 501 "e.y"
{ yyval = b.doMetaSend(val_peek(3), val_peek(1), val_peek(0)); }
break;
case 99:
//#line 502 "e.y"
{ yyval = b.doMetaSend(val_peek(2), val_peek(1),"run", val_peek(0));}
break;
case 101:
//#line 509 "e.y"
{ yyval = b.control(val_peek(6), val_peek(2), val_peek(4), val_peek(1), val_peek(0)); }
break;
case 102:
//#line 510 "e.y"
{ yyval = b.control(val_peek(3), val_peek(2), val_peek(1), val_peek(0)); }
break;
case 103:
//#line 511 "e.y"
{ yyval = b.control(val_peek(3), val_peek(2), val_peek(1), val_peek(0)); }
break;
case 104:
//#line 513 "e.y"
{ yyval = b.control(val_peek(5), val_peek(4), val_peek(2),
                                                         b.list(), val_peek(0)); }
break;
case 105:
//#line 521 "e.y"
{ yyval = b.literal(val_peek(0)); }
break;
case 106:
//#line 522 "e.y"
{ yyval = b.literal(val_peek(0)); }
break;
case 107:
//#line 523 "e.y"
{ yyval = b.literal(val_peek(0)); }
break;
case 108:
//#line 524 "e.y"
{ yyval = b.literal(val_peek(0)); }
break;
case 111:
//#line 533 "e.y"
{ yyval = b.uriExpr(val_peek(0)); }
break;
case 112:
//#line 534 "e.y"
{ yyval = b.quasiExpr(val_peek(1),val_peek(0)); }
break;
case 114:
//#line 537 "e.y"
{ yyval = b.tuple(val_peek(1)); }
break;
case 115:
//#line 538 "e.y"
{ yyval = b.map(val_peek(1)); }
break;
case 116:
//#line 540 "e.y"
{ yyval = b.hide(val_peek(0)); }
break;
case 117:
//#line 541 "e.y"
{ yyval = b.escape(val_peek(2),val_peek(1),val_peek(0)); }
break;
case 118:
//#line 542 "e.y"
{ yyval = b.whilex(val_peek(2),val_peek(1),val_peek(0)); }
break;
case 119:
//#line 543 "e.y"
{ yyval = b.switchx(val_peek(1),val_peek(0)); }
break;
case 120:
//#line 544 "e.y"
{ yyval = b.tryx(val_peek(2),val_peek(1),val_peek(0)); }
break;
case 122:
//#line 547 "e.y"
{ yyval = val_peek(0); }
break;
case 124:
//#line 550 "e.y"
{ b.reserved(val_peek(2),"select"); }
break;
case 126:
//#line 558 "e.y"
{ yyval = b.doco("",val_peek(0)); }
break;
case 127:
//#line 559 "e.y"
{ yyval = b.doco(val_peek(1),val_peek(0)); }
break;
case 128:
//#line 569 "e.y"
{ yyval = ((ObjDecl)val_peek(0)).withOName(val_peek(1)); }
break;
case 129:
//#line 571 "e.y"
{ yyval = b.oType("",val_peek(6),b.list(),
                                                       val_peek(5),val_peek(4),val_peek(1)); }
break;
case 130:
//#line 573 "e.y"
{ /* doesn't bind __return */
                                          /* XXX We may deprecate this */
                                          yyval = b.fnDecl(val_peek(1), b.list(), val_peek(0)); }
break;
case 131:
//#line 577 "e.y"
{ b.pocket(NO_POSER,"anon-lambda");
                                          /* doesn't bind __return */
                                          yyval = b.fnDecl(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 132:
//#line 586 "e.y"
{ yyval = ((ObjDecl)val_peek(1)).withScript(val_peek(0)); }
break;
case 133:
//#line 587 "e.y"
{ /* binds __return */
                                          yyval = b.methDecl(val_peek(1), val_peek(0), true); }
break;
case 134:
//#line 596 "e.y"
{ yyval = noun(val_peek(0)); }
break;
case 135:
//#line 597 "e.y"
{ yyval = b.quasiLiteralExpr(val_peek(0)); }
break;
case 136:
//#line 598 "e.y"
{ yyval = b.quasiPatternExpr(val_peek(0)); }
break;
case 137:
//#line 602 "e.y"
{ yyval = val_peek(1); }
break;
case 138:
//#line 603 "e.y"
{ yyval = val_peek(0); }
break;
case 139:
//#line 604 "e.y"
{ yyval = null; }
break;
case 140:
//#line 608 "e.y"
{ yyval = val_peek(1); }
break;
case 141:
//#line 609 "e.y"
{ yyval = val_peek(0); }
break;
case 142:
//#line 616 "e.y"
{ yyval = val_peek(1); }
break;
case 143:
//#line 624 "e.y"
{ yyval = b.ifx(val_peek(1), val_peek(0)); }
break;
case 144:
//#line 625 "e.y"
{ yyval = b.ifx(val_peek(3), val_peek(2), val_peek(0)); }
break;
case 145:
//#line 626 "e.y"
{ yyval = b.ifx(val_peek(3), val_peek(2), val_peek(0)); }
break;
case 147:
//#line 631 "e.y"
{ b.reserved(val_peek(1),"if-match"); }
break;
case 148:
//#line 639 "e.y"
{ yyval = b.forx(val_peek(4),val_peek(2),val_peek(1),val_peek(0)); }
break;
case 149:
//#line 640 "e.y"
{ b.reserved(val_peek(2),"when-in"); }
break;
case 151:
//#line 652 "e.y"
{ yyval = noun("simple__quasiParser"); }
break;
case 152:
//#line 653 "e.y"
{ yyval = noun(b.mangle(val_peek(0),
                                                     "__quasiParser")); }
break;
case 153:
//#line 658 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 154:
//#line 659 "e.y"
{ yyval = b.with(val_peek(1), val_peek(0)); }
break;
case 155:
//#line 663 "e.y"
{ yyval = b.list(val_peek(1), val_peek(0)); }
break;
case 156:
//#line 664 "e.y"
{ yyval = b.with(b.with(val_peek(2), val_peek(1)), val_peek(0)); }
break;
case 157:
//#line 668 "e.y"
{ yyval = b.dollarNoun(val_peek(0)); }
break;
case 158:
//#line 669 "e.y"
{ yyval = val_peek(1); }
break;
case 159:
//#line 680 "e.y"
{ yyval = val_peek(1); }
break;
case 165:
//#line 706 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 166:
//#line 707 "e.y"
{ yyval = b.with(val_peek(2), val_peek(0)); }
break;
case 168:
//#line 711 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 169:
//#line 712 "e.y"
{ yyval = b.with(val_peek(2), val_peek(0)); }
break;
case 170:
//#line 717 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 171:
//#line 718 "e.y"
{ yyval = b.with(val_peek(2), val_peek(0)); }
break;
case 173:
//#line 722 "e.y"
{ yyval = b.assoc(val_peek(2), val_peek(0)); }
break;
case 174:
//#line 723 "e.y"
{ b.pocket(val_peek(1),"exporter");
                                          yyval = b.exporter(val_peek(0)); }
break;
case 175:
//#line 725 "e.y"
{ b.pocket(val_peek(2),"exporter");
                                          yyval = b.exporter(b.slotExpr(val_peek(1),val_peek(0))); }
break;
case 176:
//#line 727 "e.y"
{ b.pocket(val_peek(2),"exporter");
                                          b.reserved(val_peek(1),"Forward exporter"); }
break;
case 177:
//#line 742 "e.y"
{ yyval = b.assoc(b.ignore(), val_peek(0)); }
break;
case 178:
//#line 743 "e.y"
{ yyval = b.assoc(val_peek(2), val_peek(0)); }
break;
case 180:
//#line 749 "e.y"
{ yyval = b.suchThat(val_peek(2), val_peek(0)); }
break;
case 181:
//#line 750 "e.y"
{ yyval = b.via(val_peek(1),val_peek(0)); }
break;
case 182:
//#line 752 "e.y"
{ b.reserved(val_peek(1),"meta pattern"); }
break;
case 185:
//#line 758 "e.y"
{ yyval = b.quasiPattern(val_peek(1), val_peek(0)); }
break;
case 186:
//#line 759 "e.y"
{ yyval = b.patternEquals(val_peek(0)); }
break;
case 187:
//#line 760 "e.y"
{ b.reserved(val_peek(1),"not-same pattern"); }
break;
case 188:
//#line 761 "e.y"
{ b.reserved(val_peek(1),
                                                     "comparison pattern"); }
break;
case 189:
//#line 764 "e.y"
{ b.pocket(val_peek(4),"call-pattern");
                                              yyval = b.callPattern(val_peek(3), val_peek(2),"run", val_peek(1)); }
break;
case 190:
//#line 766 "e.y"
{ b.pocket(val_peek(6),"call-pattern"); 
                                                  yyval = b.callPattern(val_peek(5), val_peek(3), val_peek(1)); }
break;
case 191:
//#line 768 "e.y"
{ b.pocket(val_peek(4),"call-pattern");
                                              yyval = b.callPattern(val_peek(3), val_peek(2),"get", val_peek(1)); }
break;
case 192:
//#line 771 "e.y"
{ yyval = b.listPattern(val_peek(0)); }
break;
case 193:
//#line 772 "e.y"
{ yyval = b.mapPattern(val_peek(1),null); }
break;
case 194:
//#line 773 "e.y"
{ yyval = b.cdrPattern(val_peek(2), val_peek(0)); }
break;
case 195:
//#line 774 "e.y"
{ yyval = b.mapPattern(val_peek(3), val_peek(0)); }
break;
case 196:
//#line 778 "e.y"
{ yyval = val_peek(1); }
break;
case 197:
//#line 782 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 198:
//#line 783 "e.y"
{ yyval = b.with(val_peek(1), val_peek(0)); }
break;
case 199:
//#line 787 "e.y"
{ yyval = b.list(val_peek(1), val_peek(0)); }
break;
case 200:
//#line 788 "e.y"
{ yyval = b.with(b.with(val_peek(2), val_peek(1)),
                                                              val_peek(0)); }
break;
case 203:
//#line 798 "e.y"
{ yyval = b.atNoun(val_peek(0)); }
break;
case 204:
//#line 799 "e.y"
{ yyval = val_peek(2); }
break;
case 205:
//#line 812 "e.y"
{ yyval = b.ignore(); }
break;
case 206:
//#line 813 "e.y"
{ yyval = b.ignore(val_peek(0)); }
break;
case 207:
//#line 814 "e.y"
{ yyval = b.ignore(val_peek(0));}
break;
case 208:
//#line 818 "e.y"
{ yyval = b.finalPattern(val_peek(2),val_peek(0));}
break;
case 209:
//#line 819 "e.y"
{ b.antiPocket(val_peek(0),
                                                       "explicit-final-guard");
                                          yyval = b.finalPattern(val_peek(0)); }
break;
case 213:
//#line 828 "e.y"
{ yyval = b.bindDefiner(val_peek(2),val_peek(0)); }
break;
case 214:
//#line 829 "e.y"
{ b.antiPocket(val_peek(1),
                                                       "explicit-final-guard");
                                          yyval = b.bindDefiner(val_peek(0)); }
break;
case 215:
//#line 835 "e.y"
{ yyval = b.varPattern(val_peek(2),val_peek(0)); }
break;
case 216:
//#line 836 "e.y"
{ b.antiPocket(val_peek(1),
                                                       "explicit-var-guard");
                                          yyval = b.varPattern(val_peek(0)); }
break;
case 217:
//#line 842 "e.y"
{ yyval = b.slotPattern(val_peek(2),val_peek(0)); }
break;
case 218:
//#line 843 "e.y"
{ b.antiPocket(val_peek(1),
                                                       "explicit-slot-guard");
                                          yyval = b.slotPattern(val_peek(0)); }
break;
case 219:
//#line 854 "e.y"
{ yyval = b.finalPattern(val_peek(0)); }
break;
case 220:
//#line 855 "e.y"
{ yyval = b.ignore(); }
break;
case 221:
//#line 856 "e.y"
{ yyval = b.bindDefiner(val_peek(0)); }
break;
case 222:
//#line 857 "e.y"
{ yyval = b.varPattern(val_peek(0)); }
break;
case 223:
//#line 858 "e.y"
{ b.reserved(val_peek(0),
                                "literal qualified name no longer accepted"); }
break;
case 225:
//#line 871 "e.y"
{ yyval = val_peek(1); }
break;
case 228:
//#line 897 "e.y"
{ yyval = val_peek(1); }
break;
case 229:
//#line 901 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 230:
//#line 902 "e.y"
{ yyval = b.with(val_peek(2), val_peek(0)); }
break;
case 231:
//#line 906 "e.y"
{ yyval = val_peek(1); }
break;
case 232:
//#line 910 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 233:
//#line 911 "e.y"
{ yyval = b.with(val_peek(2), val_peek(0)); }
break;
case 234:
//#line 915 "e.y"
{ yyval = b.assoc(val_peek(2), val_peek(0)); }
break;
case 235:
//#line 916 "e.y"
{ b.pocket(val_peek(1),"pattern-default");
                                          yyval = b.assoc(val_peek(4), b.assoc(val_peek(0),val_peek(2))); }
break;
case 236:
//#line 918 "e.y"
{ b.pocket(val_peek(1),"pattern-default");
                                     b.reserved(val_peek(1),"default in map pattern"); }
break;
case 237:
//#line 920 "e.y"
{ b.pocket(val_peek(1),"importer");
                                          yyval = b.importer(val_peek(0)); }
break;
case 238:
//#line 922 "e.y"
{ b.pocket(val_peek(1),"pattern-default");
                                          b.pocket(val_peek(3),"importer");
                                          yyval = b.importer(b.assoc(val_peek(0),val_peek(2))); }
break;
case 239:
//#line 925 "e.y"
{ b.pocket(val_peek(1),"pattern-default");
                                          b.pocket(val_peek(3),"importer");
                                     b.reserved(val_peek(0),"default in map pattern"); }
break;
case 242:
//#line 946 "e.y"
{ yyval = ""; }
break;
case 244:
//#line 954 "e.y"
{ yyval = val_peek(0); }
break;
case 245:
//#line 955 "e.y"
{ yyval = b.bindDefiner(val_peek(0)); }
break;
case 246:
//#line 956 "e.y"
{ yyval = b.varPattern(val_peek(0)); }
break;
case 247:
//#line 963 "e.y"
{ yyval = ODECL; }
break;
case 248:
//#line 964 "e.y"
{ yyval = ODECL.withExtends(b,val_peek(0)); }
break;
case 249:
//#line 965 "e.y"
{ yyval = ODECL.withAuditors(b,val_peek(0));}
break;
case 250:
//#line 966 "e.y"
{ yyval = ODECL.withExtends(b,val_peek(1))
                                                          .withAuditors(b,val_peek(0));}
break;
case 251:
//#line 969 "e.y"
{ yyerror("'extends' must come before 'implements'"); }
break;
case 252:
//#line 976 "e.y"
{ yyval = ODECL; }
break;
case 253:
//#line 977 "e.y"
{ yyval = ODECL.withExtends(b,val_peek(0)); }
break;
case 254:
//#line 978 "e.y"
{ yyval = ODECL.withAuditors(b,val_peek(0));}
break;
case 255:
//#line 979 "e.y"
{ yyval = ODECL.withExtends(b,val_peek(1))
                                                          .withAuditors(b,val_peek(0));}
break;
case 256:
//#line 982 "e.y"
{ yyerror("'extends' must come before 'implements'"); }
break;
case 257:
//#line 991 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 258:
//#line 998 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 259:
//#line 999 "e.y"
{ yyval = b.with(val_peek(2), val_peek(0)); }
break;
case 260:
//#line 1006 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 261:
//#line 1007 "e.y"
{ yyval = b.with(val_peek(2), val_peek(0)); }
break;
case 265:
//#line 1021 "e.y"
{ b.reserved(val_peek(0),"literal concat"); }
break;
case 266:
//#line 1022 "e.y"
{ b.reserved(val_peek(0),"literal concat"); }
break;
case 267:
//#line 1031 "e.y"
{ /* binds __return */
                                               yyval = b.to(val_peek(3), val_peek(1), val_peek(0)); }
break;
case 268:
//#line 1033 "e.y"
{ /* doesn't bind __return */
                                               yyval = b.method(val_peek(3), val_peek(1), val_peek(0)); }
break;
case 269:
//#line 1061 "e.y"
{ yyval = b.methHead(val_peek(3),"run",val_peek(2),val_peek(0)); }
break;
case 270:
//#line 1062 "e.y"
{ yyval = b.methHead(val_peek(4),      val_peek(2),val_peek(0)); }
break;
case 271:
//#line 1071 "e.y"
{ yyval = b.methHead(val_peek(3),"run", val_peek(2), val_peek(0));}
break;
case 272:
//#line 1073 "e.y"
{ b.pocket(val_peek(5),"one-method-object");
                                          yyval = b.methHead(val_peek(4), val_peek(2), val_peek(0)); }
break;
case 273:
//#line 1076 "e.y"
{ b.pocket(val_peek(5),"one-method-object");
                                          yyval = b.methHead(val_peek(4), val_peek(2), val_peek(0)); }
break;
case 274:
//#line 1088 "e.y"
{ yyval = b.matcher(val_peek(1), val_peek(0)); }
break;
case 276:
//#line 1096 "e.y"
{ yyval = b.uriExpr(val_peek(0)); }
break;
case 278:
//#line 1098 "e.y"
{ yyval = b.call(val_peek(3), val_peek(2),"get", val_peek(1)); }
break;
case 279:
//#line 1099 "e.y"
{ yyval = b.propValue(val_peek(2), val_peek(0)); }
break;
case 285:
//#line 1111 "e.y"
{ yyval = val_peek(0); }
break;
case 286:
//#line 1112 "e.y"
{ yyval = b.defaultOptResultGuard(yylval); }
break;
case 287:
//#line 1113 "e.y"
{ b.reserved(val_peek(1),"throws"); }
break;
case 288:
//#line 1114 "e.y"
{ b.reserved(val_peek(1),"throws"); }
break;
case 289:
//#line 1117 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 290:
//#line 1118 "e.y"
{ yyval = b.with(val_peek(2),val_peek(0)); }
break;
case 291:
//#line 1126 "e.y"
{ yyval = b.when(val_peek(4), val_peek(1), val_peek(0)); }
break;
case 292:
//#line 1127 "e.y"
{ b.pocket(val_peek(2),"when-sequence");
                                              yyval = b.whenSeq(val_peek(4), val_peek(1), val_peek(0)); }
break;
case 293:
//#line 1136 "e.y"
{ /* binds __return */
                                  yyval = b.list(ODECL.withOName(val_peek(5)),
                                              val_peek(3), val_peek(1), val_peek(0),
                                              Boolean.TRUE); }
break;
case 294:
//#line 1141 "e.y"
{ /* XXX should this bind __return ?? */
                                  /* Currently, it does. */
                                  b.pocket(val_peek(1),"easy-when");
                                  yyval = b.list(ODECL.withOName(val_peek(2)),
                                              null, val_peek(1), val_peek(0),
                                              Boolean.TRUE); }
break;
case 295:
//#line 1148 "e.y"
{ /* XXX should this bind __return ?? */
                                  /* Currently, it does not. */
                                  b.pocket(val_peek(1),"easy-when");
                                  yyval = b.list(ODECL.withOName(b.ignore()),
                                              null, val_peek(1), val_peek(0),
                                              Boolean.FALSE); }
break;
case 296:
//#line 1157 "e.y"
{ yyval = val_peek(0); }
break;
case 297:
//#line 1158 "e.y"
{ yyval = b.defaultOptWhenGuard(yylval); }
break;
case 298:
//#line 1159 "e.y"
{ b.reserved(val_peek(1),"throws"); }
break;
case 299:
//#line 1160 "e.y"
{ b.reserved(val_peek(1),"throws"); }
break;
case 300:
//#line 1172 "e.y"
{ yyval = b.list(val_peek(2), val_peek(1), val_peek(0)); }
break;
case 301:
//#line 1173 "e.y"
{ b.pocket(val_peek(0),"easy-when");
                                     yyval = b.list(val_peek(0), null, null); }
break;
case 306:
//#line 1194 "e.y"
{ yyval = b.list(); }
break;
case 307:
//#line 1198 "e.y"
{ yyval = b.list(); }
break;
case 309:
//#line 1207 "e.y"
{ b.pocket(val_peek(0),"verb-string");
                                          yyval = val_peek(0); }
break;
case 310:
//#line 1215 "e.y"
{ b.pocket(val_peek(0),"verb-curry");
                                          yyval = val_peek(0); }
break;
case 311:
//#line 1217 "e.y"
{ b.pocket(val_peek(0),"verb-curry");
                                          b.pocket(val_peek(0),"verb-string");
                                          yyval = val_peek(0); }
break;
case 312:
//#line 1229 "e.y"
{ b.pocket(val_peek(0),"dot-props");
                                          yyval = val_peek(0); }
break;
case 313:
//#line 1231 "e.y"
{ b.pocket(val_peek(0),"dot-props");
                                          yyval = val_peek(0); }
break;
case 314:
//#line 1239 "e.y"
{ yyval = b.varName(val_peek(0)); }
break;
case 315:
//#line 1240 "e.y"
{ b.pocket(val_peek(1),"noun-string");
                                          yyval = b.varName(val_peek(0)); }
break;
case 316:
//#line 1242 "e.y"
{ b.pocket(val_peek(1),"noun-string");
                                          yyval = b.varName(val_peek(0)); }
break;
case 318:
//#line 1251 "e.y"
{ b.reserved(val_peek(0),"keyword \"" +
                                     ((Astro)val_peek(0)).getTag().getTagName() +
                                     "\""); }
break;
case 319:
//#line 1267 "e.y"
{ yyval = b.ident(val_peek(0), "add"); }
break;
case 320:
//#line 1268 "e.y"
{ yyval = b.ident(val_peek(0), "and"); }
break;
case 321:
//#line 1269 "e.y"
{ yyval = b.ident(val_peek(0), "approxDivide"); }
break;
case 322:
//#line 1270 "e.y"
{ yyval = b.ident(val_peek(0), "floorDivide"); }
break;
case 323:
//#line 1271 "e.y"
{ yyval = b.ident(val_peek(0), "shiftLeft"); }
break;
case 324:
//#line 1272 "e.y"
{ yyval = b.ident(val_peek(0), "shiftRight"); }
break;
case 325:
//#line 1273 "e.y"
{ yyval = b.ident(val_peek(0), "remainder"); }
break;
case 326:
//#line 1274 "e.y"
{ yyval = b.ident(val_peek(0), "mod"); }
break;
case 327:
//#line 1275 "e.y"
{ yyval = b.ident(val_peek(0), "multiply"); }
break;
case 328:
//#line 1276 "e.y"
{ yyval = b.ident(val_peek(0), "or"); }
break;
case 329:
//#line 1277 "e.y"
{ yyval = b.ident(val_peek(0), "pow"); }
break;
case 330:
//#line 1278 "e.y"
{ yyval = b.ident(val_peek(0), "subtract"); }
break;
case 331:
//#line 1279 "e.y"
{ yyval = b.ident(val_peek(0), "xor"); }
break;
case 332:
//#line 1290 "e.y"
{ yyval = b.ident(val_peek(0), "add"); }
break;
case 333:
//#line 1291 "e.y"
{ yyval = b.ident(val_peek(0), "and"); }
break;
case 334:
//#line 1292 "e.y"
{ yyval = b.ident(val_peek(0), "approxDivide"); }
break;
case 335:
//#line 1293 "e.y"
{ yyval = b.ident(val_peek(0), "floorDivide"); }
break;
case 336:
//#line 1294 "e.y"
{ yyval = b.ident(val_peek(0), "shiftLeft"); }
break;
case 337:
//#line 1295 "e.y"
{ yyval = b.ident(val_peek(0), "shiftRight"); }
break;
case 338:
//#line 1296 "e.y"
{ yyval = b.ident(val_peek(0), "remainder"); }
break;
case 339:
//#line 1297 "e.y"
{ yyval = b.ident(val_peek(0), "mod"); }
break;
case 340:
//#line 1298 "e.y"
{ yyval = b.ident(val_peek(0), "multiply"); }
break;
case 341:
//#line 1299 "e.y"
{ yyval = b.ident(val_peek(0), "or"); }
break;
case 342:
//#line 1300 "e.y"
{ yyval = b.ident(val_peek(0), "pow"); }
break;
case 343:
//#line 1301 "e.y"
{ yyval = b.ident(val_peek(0), "subtract"); }
break;
case 344:
//#line 1302 "e.y"
{ yyval = b.ident(val_peek(0), "xor"); }
break;
case 345:
//#line 1311 "e.y"
{ yyval = b.getNULL(); }
break;
case 346:
//#line 1312 "e.y"
{ yyval = val_peek(1); }
break;
case 347:
//#line 1320 "e.y"
{ b.pocket(val_peek(2),"accumulator");
                                                  yyval = b.accumulate(val_peek(1),val_peek(0)); }
break;
case 348:
//#line 1325 "e.y"
{ yyval = b.accumFor(val_peek(3),val_peek(1),val_peek(0)); }
break;
case 349:
//#line 1326 "e.y"
{ yyval = b.accumIf(val_peek(1),val_peek(0)); }
break;
case 350:
//#line 1327 "e.y"
{ yyval = b.accumWhile(val_peek(1),val_peek(0)); }
break;
case 351:
//#line 1331 "e.y"
{ yyval = b.accumBody(val_peek(3),
                                                                 b.list(val_peek(2))); }
break;
case 352:
//#line 1333 "e.y"
{ yyval = b.accumBody(val_peek(3),val_peek(2)); }
break;
case 353:
//#line 1334 "e.y"
{ yyval = val_peek(2); }
break;
case 354:
//#line 1339 "e.y"
{ yyval = val_peek(1); }
break;
case 355:
//#line 1343 "e.y"
{ yyval = b.vTable(val_peek(2), val_peek(1)); }
break;
case 356:
//#line 1344 "e.y"
{ b.pocket(NO_POSER,
                                                           "plumbing");
                                                  yyval = b.vTable(null,
                                                                b.list(val_peek(0))); }
break;
case 358:
//#line 1359 "e.y"
{ yyval = b.with(val_peek(2), val_peek(1)); }
break;
case 361:
//#line 1369 "e.y"
{ yyval = b.with(val_peek(2), val_peek(1)); }
break;
case 364:
//#line 1386 "e.y"
{ yyval = b.with(val_peek(1), val_peek(0)); }
break;
case 365:
//#line 1390 "e.y"
{ yyval = null; }
break;
case 366:
//#line 1391 "e.y"
{ b.pocket(NO_POSER,
                                                           "escape-handler");
                                                  yyval = val_peek(0); }
break;
case 367:
//#line 1397 "e.y"
{ yyval = b.matcher(val_peek(1), val_peek(0)); }
break;
case 368:
//#line 1404 "e.y"
{ yyval = null; }
break;
case 369:
//#line 1405 "e.y"
{ yyval = val_peek(0); }
break;
case 370:
//#line 1415 "e.y"
{ yyval = b.oType("", val_peek(1), b.list(),
                                                       b.list(val_peek(0))); }
break;
case 371:
//#line 1424 "e.y"
{ yyval = null; }
break;
case 372:
//#line 1425 "e.y"
{ yyval = val_peek(0); }
break;
case 375:
//#line 1434 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 376:
//#line 1435 "e.y"
{ yyval = b.with(val_peek(2),val_peek(0)); }
break;
case 378:
//#line 1443 "e.y"
{ b.reserved(NO_POSER,"causality"); }
break;
case 379:
//#line 1448 "e.y"
{ yyval = b.mType(val_peek(6), val_peek(4), val_peek(2), val_peek(0)); }
break;
case 380:
//#line 1450 "e.y"
{ yyval = b.mType(val_peek(5), "run", val_peek(2), val_peek(0));}
break;
case 382:
//#line 1458 "e.y"
{ b.reserved(NO_POSER,"causality"); }
break;
case 383:
//#line 1462 "e.y"
{ yyval = b.mType("", "run", val_peek(2), val_peek(0)); }
break;
case 384:
//#line 1463 "e.y"
{ b.pocket(val_peek(5),"one-method-object");
                                            yyval = b.mType("", val_peek(4),    val_peek(2), val_peek(0)); }
break;
case 385:
//#line 1465 "e.y"
{b.pocket(val_peek(5),"one-method-object");
                                            yyval = b.mType("", val_peek(4),    val_peek(2), val_peek(0)); }
break;
case 386:
//#line 1470 "e.y"
{ yyval = val_peek(0); }
break;
case 387:
//#line 1471 "e.y"
{ yyval = val_peek(1); }
break;
case 388:
//#line 1476 "e.y"
{ yyval = b.list(val_peek(0)); }
break;
case 389:
//#line 1477 "e.y"
{ yyval = b.with(val_peek(2),val_peek(0)); }
break;
case 390:
//#line 1484 "e.y"
{ yyval = b.pType(val_peek(1),val_peek(0)); }
break;
case 391:
//#line 1485 "e.y"
{ yyval = b.pType(null,val_peek(0)); }
break;
case 392:
//#line 1486 "e.y"
{ yyval = b.pType(null,val_peek(0)); }
break;
case 393:
//#line 1494 "e.y"
{ yyval = null; }
break;
case 394:
//#line 1495 "e.y"
{ yyval = val_peek(0); }
break;
case 408:
//#line 1511 "e.y"
{ yyval = "->"; }
break;
//#line 7026 "EParser.java"
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
