//### This file created by BYACC 1.8(/Java extension  0.92)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//### Please send bug reports to rjamison@lincom-asg.com
//### static char yysccsid[] = "@(#)yaccpar       1.8 (Berkeley) 01/20/90";

//#line 9 "term.y"
package org.quasiliteral.term;

import org.erights.e.develop.exception.NestedException;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.Twine;
import org.erights.e.meta.java.lang.CharacterMakerSugar;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroArg;
import org.quasiliteral.astro.AstroSchema;
import org.quasiliteral.astro.AstroTag;
import org.quasiliteral.astro.BaseSchema;
import org.quasiliteral.syntax.LexerFace;
import org.quasiliteral.syntax.LineFeeder;
import org.quasiliteral.syntax.SyntaxException;
import org.quasiliteral.syntax.TwineFeeder;

import java.io.IOException;
//#line 28 "TermParser.java"

//#####################################################################
// class: TermParser
// does : encapsulates yacc() parser functionality in a Java
//        class for quick code development

//#####################################################################
public class TermParser {

    boolean yydebug;        //do I want debug output?
    int yynerrs;            //number of errors so far
    int yyerrflag;          //was there an error?
    int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug

    //###############################################################
    void debug(String msg) {
        if (yydebug) {
            System.err.println(msg);
        }
    }

    //########## STATE STACK ##########
    final static int YYSTACKSIZE = 500;  //maximum stack size
    int statestk[], stateptr;             //state stack
//###############################################################
// methods: state stack push,pop,drop,peek

    //###############################################################
    void state_push(int state) {
        if (stateptr >= YYSTACKSIZE)         //overflowed?
        {
            return;
        }
        statestk[++stateptr] = state;
    }

    int state_pop() {
        if (stateptr < 0)                    //underflowed?
        {
            return -1;
        }
        return statestk[stateptr--];
    }

    void state_drop(int cnt) {
        int ptr;
        ptr = stateptr - cnt;
        if (ptr < 0) {
            return;
        }
        stateptr = ptr;
    }

    int state_peek(int relative) {
        int ptr;
        ptr = stateptr - relative;
        if (ptr < 0) {
            return -1;
        }
        return statestk[ptr];
    }
//###############################################################
// method: init_stacks : allocate and prepare stacks

    //###############################################################
    boolean init_stacks() {
        statestk = new int[YYSTACKSIZE];
        stateptr = -1;
        val_init();
        return true;
    }
//###############################################################
// method: dump_stacks : show n levels of the stacks

    //###############################################################
    void dump_stacks(int count) {
        int i;
        System.err
          .println(
            "=index==state====value=     s:" + stateptr + "  v:" + valptr);
        for (i = 0; i < count; i++) {
            System.err
              .println(" " + i + "    " + statestk[i] + "      " + valstk[i]);
        }
        System.err.println("======================");
    }

//########## SEMANTIC VALUES ##########
    //## **user defined:Object
    String yytext;//user variable to return contextual strings
    Object yyval; //used to return semantic vals from action routines
    Object yylval;//the 'lval' (result) I got from yylex()
    Object valstk[];
    int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.

    //###############################################################
    void val_init() {
        valstk = new Object[YYSTACKSIZE];
        yyval = new Object();
        yylval = new Object();
        valptr = -1;
    }

    void val_push(Object val) {
        if (valptr >= YYSTACKSIZE) {
            return;
        }
        valstk[++valptr] = val;
    }

    Object val_pop() {
        if (valptr < 0) {
            return null;
        }
        return valstk[valptr--];
    }

    void val_drop(int cnt) {
        int ptr;
        ptr = valptr - cnt;
        if (ptr < 0) {
            return;
        }
        valptr = ptr;
    }

    Object val_peek(int relative) {
        int ptr;
        ptr = valptr - relative;
        if (ptr < 0) {
            return null;
        }
        return valstk[ptr];
    }

    //#### end semantic value section ####
    public final static short Tag = 257;
    public final static short LiteralChar = 258;
    public final static short LiteralInteger = 259;
    public final static short LiteralFloat64 = 260;
    public final static short LiteralString = 261;
    public final static short LiteralChars = 262;
    public final static short EOL = 263;
    public final static short EOTLU = 264;
    public final static short OpDef = 265;
    public final static short OpAction = 266;
    public final static short OpThru = 267;
    public final static short OpDoubleStar = 268;
    public final static short OpDoublePlus = 269;
    public final static short YYERRCODE = 256;
    final static short yylhs[] = {-1,
      0,
      0,
      2,
      3,
      3,
      4,
      6,
      6,
      6,
      6,
      6,
      6,
      9,
      7,
      7,
      7,
      7,
      7,
      7,
      1,
      1,
      1,
      12,
      12,
      13,
      13,
      13,
      14,
      14,
      15,
      15,
      16,
      16,
      17,
      17,
      17,
      17,
      17,
      18,
      18,
      18,
      8,
      8,
      8,
      8,
      8,
      8,
      10,
      10,
      10,
      10,
      5,
      11,
      11,
      11,
      11,
      11,
      11,
      11,
      11,
      11,};
    final static short yylen[] = {2,
      1,
      1,
      1,
      1,
      2,
      4,
      1,
      4,
      3,
      3,
      1,
      2,
      3,
      1,
      1,
      1,
      2,
      2,
      2,
      0,
      1,
      2,
      1,
      3,
      1,
      3,
      3,
      1,
      3,
      1,
      3,
      1,
      2,
      1,
      2,
      1,
      3,
      3,
      1,
      1,
      1,
      1,
      1,
      3,
      1,
      2,
      3,
      1,
      1,
      1,
      1,
      1,
      4,
      2,
      2,
      4,
      2,
      2,
      4,
      2,
      2,};
    final static short yydefred[] = {0,
      52,
      48,
      49,
      50,
      51,
      45,
      0,
      0,
      0,
      0,
      0,
      39,
      40,
      41,
      0,
      0,
      0,
      0,
      0,
      1,
      2,
      0,
      4,
      0,
      42,
      0,
      0,
      11,
      0,
      0,
      0,
      0,
      0,
      28,
      0,
      0,
      36,
      0,
      0,
      0,
      0,
      17,
      33,
      46,
      54,
      0,
      55,
      57,
      0,
      58,
      60,
      0,
      61,
      5,
      0,
      0,
      18,
      0,
      0,
      12,
      0,
      0,
      0,
      19,
      0,
      0,
      0,
      0,
      0,
      35,
      47,
      9,
      13,
      0,
      0,
      0,
      0,
      0,
      10,
      37,
      38,
      44,
      0,
      0,
      0,
      29,
      31,
      53,
      56,
      59,
      6,
      8,};
    final static short yydgoto[] = {19,
      20,
      21,
      22,
      23,
      39,
      25,
      26,
      27,
      28,
      29,
      30,
      31,
      32,
      33,
      34,
      35,
      36,
      37,};
    final static short yysindex[] = {162,
      0,
      0,
      0,
      0,
      0,
      0,
      162,
      162,
      162,
      -32,
      162,
      0,
      0,
      0,
      -244,
      -116,
      -69,
      -68,
      0,
      0,
      0,
      -238,
      0,
      -34,
      0,
      -40,
      -211,
      0,
      -242,
      -32,
      -13,
      -42,
      -2,
      0,
      -232,
      -30,
      0,
      -3,
      -32,
      -53,
      -79,
      0,
      0,
      0,
      0,
      -197,
      0,
      0,
      -193,
      0,
      0,
      -186,
      0,
      0,
      -191,
      162,
      0,
      162,
      198,
      0,
      198,
      198,
      -237,
      0,
      162,
      162,
      162,
      162,
      162,
      0,
      0,
      0,
      0,
      -39,
      -37,
      -35,
      26,
      50,
      0,
      0,
      0,
      0,
      -42,
      -2,
      -2,
      0,
      0,
      0,
      0,
      0,
      0,
      0,};
    final static short yyrindex[] = {92,
      0,
      0,
      0,
      0,
      0,
      0,
      52,
      5,
      -28,
      76,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      99,
      0,
      112,
      0,
      104,
      140,
      0,
      1,
      9,
      28,
      239,
      214,
      0,
      243,
      127,
      0,
      0,
      37,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      42,
      0,
      52,
      0,
      0,
      0,
      0,
      0,
      0,
      241,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      260,
      227,
      250,
      0,
      0,
      0,
      0,
      0,
      0,
      0,};
    final static short yygindex[] =
      {0, 7, 0, 0, 81, 89, 0, 0, -51, 82, 46, -4, 0, 45, 4, 47, -8, 0, 77,};
    final static int YYTABLESIZE = 460;
    final static short yytable[] = {58,
      14,
      16,
      43,
      16,
      67,
      42,
      46,
      79,
      16,
      80,
      81,
      14,
      13,
      38,
      40,
      41,
      44,
      59,
      1,
      57,
      2,
      3,
      4,
      5,
      63,
      64,
      18,
      21,
      18,
      17,
      65,
      17,
      12,
      69,
      57,
      68,
      15,
      71,
      14,
      72,
      14,
      14,
      14,
      14,
      14,
      73,
      16,
      14,
      16,
      16,
      16,
      16,
      16,
      49,
      52,
      16,
      61,
      62,
      14,
      14,
      87,
      74,
      77,
      14,
      78,
      75,
      16,
      16,
      21,
      84,
      85,
      16,
      76,
      56,
      15,
      43,
      15,
      15,
      15,
      15,
      15,
      66,
      9,
      15,
      91,
      88,
      21,
      89,
      24,
      90,
      92,
      20,
      20,
      14,
      15,
      15,
      20,
      20,
      3,
      15,
      20,
      16,
      54,
      7,
      47,
      50,
      53,
      60,
      82,
      83,
      55,
      15,
      70,
      43,
      86,
      0,
      43,
      43,
      43,
      43,
      21,
      0,
      43,
      14,
      14,
      14,
      32,
      0,
      0,
      15,
      0,
      16,
      16,
      16,
      43,
      0,
      0,
      0,
      43,
      34,
      1,
      7,
      45,
      0,
      7,
      7,
      7,
      7,
      0,
      15,
      7,
      15,
      21,
      15,
      15,
      15,
      0,
      0,
      15,
      15,
      15,
      15,
      7,
      0,
      32,
      0,
      7,
      32,
      43,
      15,
      32,
      0,
      0,
      32,
      15,
      0,
      0,
      34,
      0,
      0,
      34,
      34,
      34,
      34,
      0,
      32,
      34,
      1,
      1,
      48,
      51,
      0,
      0,
      0,
      11,
      0,
      7,
      16,
      34,
      43,
      43,
      7,
      34,
      14,
      13,
      0,
      0,
      10,
      0,
      0,
      0,
      0,
      0,
      25,
      0,
      0,
      0,
      0,
      0,
      32,
      0,
      0,
      18,
      0,
      12,
      17,
      26,
      7,
      7,
      0,
      56,
      0,
      34,
      16,
      15,
      15,
      0,
      7,
      23,
      0,
      22,
      0,
      30,
      10,
      0,
      0,
      0,
      0,
      0,
      27,
      32,
      32,
      8,
      0,
      25,
      15,
      0,
      25,
      18,
      24,
      25,
      17,
      0,
      34,
      34,
      0,
      14,
      26,
      14,
      14,
      26,
      0,
      25,
      26,
      16,
      0,
      16,
      16,
      0,
      23,
      30,
      22,
      23,
      30,
      9,
      26,
      30,
      0,
      8,
      30,
      27,
      15,
      0,
      27,
      0,
      0,
      27,
      23,
      0,
      22,
      24,
      30,
      15,
      24,
      15,
      15,
      25,
      0,
      27,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      24,
      26,
      9,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      23,
      0,
      22,
      0,
      30,
      0,
      25,
      25,
      0,
      0,
      43,
      27,
      43,
      43,
      0,
      0,
      0,
      0,
      0,
      26,
      26,
      24,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      23,
      0,
      22,
      30,
      30,
      0,
      7,
      0,
      7,
      7,
      27,
      27,
      0,
      0,
      15,
      0,
      15,
      15,
      0,
      0,
      0,
      24,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      32,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      34,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      1,
      2,
      3,
      4,
      5,
      6,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      1,
      2,
      3,
      4,
      5,
      6,};
    final static short yycheck[] = {40,
      0,
      36,
      11,
      36,
      47,
      10,
      123,
      59,
      0,
      61,
      62,
      42,
      43,
      7,
      8,
      9,
      261,
      58,
      257,
      24,
      258,
      259,
      260,
      261,
      267,
      30,
      61,
      0,
      61,
      64,
      44,
      64,
      63,
      266,
      39,
      38,
      0,
      41,
      38,
      93,
      40,
      41,
      42,
      43,
      44,
      125,
      38,
      47,
      40,
      41,
      42,
      43,
      44,
      123,
      123,
      47,
      268,
      269,
      58,
      59,
      69,
      259,
      56,
      63,
      58,
      259,
      58,
      59,
      41,
      66,
      67,
      63,
      259,
      265,
      38,
      0,
      40,
      41,
      42,
      43,
      44,
      124,
      123,
      47,
      59,
      125,
      59,
      125,
      0,
      125,
      41,
      0,
      41,
      93,
      58,
      59,
      125,
      93,
      0,
      63,
      59,
      93,
      22,
      0,
      16,
      17,
      18,
      26,
      63,
      65,
      22,
      0,
      36,
      38,
      68,
      -1,
      41,
      42,
      43,
      44,
      93,
      -1,
      47,
      123,
      124,
      125,
      0,
      -1,
      -1,
      93,
      -1,
      123,
      124,
      125,
      59,
      -1,
      -1,
      -1,
      63,
      0,
      257,
      38,
      259,
      -1,
      41,
      42,
      43,
      44,
      -1,
      38,
      47,
      40,
      125,
      42,
      43,
      44,
      -1,
      -1,
      47,
      123,
      124,
      125,
      59,
      -1,
      38,
      -1,
      63,
      41,
      93,
      58,
      44,
      -1,
      -1,
      47,
      63,
      -1,
      -1,
      38,
      -1,
      -1,
      41,
      42,
      43,
      44,
      -1,
      59,
      47,
      257,
      257,
      259,
      259,
      -1,
      -1,
      -1,
      33,
      -1,
      93,
      36,
      59,
      124,
      125,
      40,
      63,
      42,
      43,
      -1,
      -1,
      46,
      -1,
      -1,
      -1,
      -1,
      -1,
      0,
      -1,
      -1,
      -1,
      -1,
      -1,
      93,
      -1,
      -1,
      61,
      -1,
      63,
      64,
      0,
      124,
      125,
      -1,
      265,
      -1,
      93,
      36,
      123,
      124,
      -1,
      40,
      0,
      -1,
      0,
      -1,
      0,
      46,
      -1,
      -1,
      -1,
      -1,
      -1,
      0,
      124,
      125,
      91,
      -1,
      41,
      94,
      -1,
      44,
      61,
      0,
      47,
      64,
      -1,
      124,
      125,
      -1,
      266,
      41,
      268,
      269,
      44,
      -1,
      59,
      47,
      266,
      -1,
      268,
      269,
      -1,
      41,
      38,
      41,
      44,
      41,
      123,
      59,
      44,
      -1,
      91,
      47,
      41,
      94,
      -1,
      44,
      -1,
      -1,
      47,
      59,
      -1,
      59,
      41,
      59,
      266,
      44,
      268,
      269,
      93,
      -1,
      59,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      59,
      93,
      123,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      93,
      -1,
      93,
      -1,
      93,
      -1,
      124,
      125,
      -1,
      -1,
      266,
      93,
      268,
      269,
      -1,
      -1,
      -1,
      -1,
      -1,
      124,
      125,
      93,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      125,
      -1,
      125,
      124,
      125,
      -1,
      266,
      -1,
      268,
      269,
      124,
      125,
      -1,
      -1,
      266,
      -1,
      268,
      269,
      -1,
      -1,
      -1,
      125,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      266,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      266,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      257,
      258,
      259,
      260,
      261,
      262,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      257,
      258,
      259,
      260,
      261,
      262,};
    final static short YYFINAL = 19;
    final static short YYMAXTOKEN = 269;
    final static String yyname[] = {"end-of-file",
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      "'!'",
      null,
      null,
      "'$'",
      null,
      "'&'",
      null,
      "'('",
      "')'",
      "'*'",
      "'+'",
      "','",
      null,
      "'.'",
      "'/'",
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      "':'",
      "';'",
      null,
      "'='",
      null,
      "'?'",
      "'@'",
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      "'['",
      null,
      "']'",
      "'^'",
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      "'{'",
      "'|'",
      "'}'",
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      "Tag",
      "LiteralChar",
      "LiteralInteger",
      "LiteralFloat64",
      "LiteralString",
      "LiteralChars",
      "EOL",
      "EOTLU",
      "OpDef",
      "OpAction",
      "OpThru",
      "OpDoubleStar",
      "OpDoublePlus",};
    final static String yyrule[] = {"$accept : start",
      "start : rhs",
      "start : schema",
      "schema : productions",
      "productions : production",
      "productions : productions production",
      "production : id OpDef rhs ';'",
      "term : functor",
      "term : functor '(' rhs ')'",
      "term : '[' rhs ']'",
      "term : functor ':' prim",
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
      "functorHole : '=' id",};

//#line 225 "term.y"

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
     * builder defaults to the quasi-adapted, non-quasi builder for building Term
     * trees.
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
     * <p/>
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
     * <p/>
     * Otherwise, is an identity function. This is needed for the TermParser
     * specifically, since identifiers in the input must all be of token tag-name
     * .Tag., while each of these represents a unique keyword, and therefore
     * token-tag, in the grammar being described.
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

        TheTokens[Tag] = ".Tag.";

        TheTokens[LiteralInteger] = ".int.";
        TheTokens[LiteralFloat64] = ".float64.";
        TheTokens[LiteralChar] = ".char.";
        TheTokens[LiteralString] = ".String.";
        TheTokens[LiteralChars] = "LiteralChars";

        /* Eaten as whitespace at a higher level */
        TheTokens[EOL] = "EOL";
        TheTokens[EOTLU] = "EOTLU";

        /* multi-character operators */
        TheTokens[OpDef] = "OpDef";        // ::=
        TheTokens[OpAction] = "OpAction";     // ->
        TheTokens[OpThru] = "OpThru";       // ..
        TheTokens[OpDoubleStar] = "OpDoubleStar"; // **
        TheTokens[OpDoublePlus] = "OpDoublePlus"; // ++

        /* Single-Character Tokens */
        TheTokens['&'] = "Ampersand";
        TheTokens['|'] = "VerticalBar";
        TheTokens['^'] = "Caret";
        TheTokens['+'] = "Plus";
        TheTokens['*'] = "Star";
        TheTokens[','] = "Comma";
        TheTokens[';'] = "Semicolon";
        TheTokens['!'] = "Bang";

        TheTokens['?'] = "Question";
        TheTokens['/'] = "Slash";
        TheTokens[':'] = "Colon";
        TheTokens['.'] = "Dot";
        TheTokens['$'] = "Dollar";
        TheTokens['@'] = "At";
        TheTokens['='] = "Equals";

        TheTokens['['] = "OpenBracket";
        TheTokens[']'] = "CloseBracket";
        TheTokens['('] = "OpenParen";
        TheTokens[')'] = "CloseParen";
        TheTokens['{'] = "OpenBrace";
        TheTokens['}'] = "CloseBrace";
    }

    /**
     * Builds ASTs according to the term.y grammar
     */
    static public final AstroSchema DEFAULT_SCHEMA =
      new BaseSchema("Term-Tree-Language", ConstList.fromArray(TheTokens));
//#line 569 "TermParser.java"
//###############################################################
// method: yylexdebug : check lexer state

    //###############################################################
    void yylexdebug(int state, int ch) {
        String s = null;
        if (ch < 0) {
            ch = 0;
        }
        if (ch <= YYMAXTOKEN) //check index bounds
        {
            s = yyname[ch];    //now get it
        }
        if (s == null) {
            s = "illegal-symbol";
        }
        debug("state " + state + ", reading " + ch + " (" + s + ")");
    }

//###############################################################
// method: yyparse : parse input and execute indicated items

    //###############################################################
    int yyparse() {
        int yyn;       //next next thing to do
        int yym;       //
        int yystate;   //current parsing state from state table
        String yys;    //current token string
        boolean doaction;
        init_stacks();
        yynerrs = 0;
        yyerrflag = 0;
        yychar = -1;          //impossible char forces a read
        yystate = 0;            //initial state
        state_push(yystate);  //save it
        while (true) //until parsing is done, either correctly, or w/error
        {
            doaction = true;
            if (yydebug) {
                debug("loop");
            }
            //#### NEXT ACTION (from reduction table)
            for (yyn = yydefred[yystate]; yyn == 0; yyn = yydefred[yystate]) {
                if (yydebug) {
                    debug("yyn:" + yyn + "  state:" + yystate + "  char:" +
                      yychar);
                }
                if (yychar < 0)      //we want a char?
                {
                    yychar = yylex();  //get next token
                    //#### ERROR CHECK ####
                    if (yychar < 0)    //it it didn't work/error
                    {
                        yychar = 0;      //change it to default string (no -1!)
                        if (yydebug) {
                            yylexdebug(yystate, yychar);
                        }
                    }
                }//yychar<0
                yyn =
                  yysindex[yystate];  //get amount to shift by (shift index)
                if ((yyn != 0) && (yyn += yychar) >= 0 && yyn <= YYTABLESIZE &&
                  yycheck[yyn] == yychar) {
                    if (yydebug) {
                        debug("state " + yystate + ", shifting to state " +
                          yytable[yyn] + "");
                    }
                    //#### NEXT STATE ####
                    yystate = yytable[yyn];//we are in a new state
                    state_push(yystate);   //save it
                    val_push(yylval);      //push our lval as the input for next rule
                    yychar =
                      -1;           //since we have 'eaten' a token, say we need another
                    if (yyerrflag > 0)     //have we recovered an error?
                    {
                        --yyerrflag;        //give ourselves credit
                    }
                    doaction = false;        //but don't process yet
                    break;   //quit the yyn=0 loop
                }

                yyn = yyrindex[yystate];  //reduce
                if ((yyn != 0) && (yyn += yychar) >= 0 && yyn <= YYTABLESIZE &&
                  yycheck[yyn] == yychar) {   //we reduced!
                    if (yydebug) {
                        debug("reduce");
                    }
                    yyn = yytable[yyn];
                    doaction = true; //get ready to execute
                    break;         //drop down to actions
                } else //ERROR RECOVERY
                {
                    if (yyerrflag == 0) {
                        yyerror("syntax error");
                        yynerrs++;
                    }
                    if (yyerrflag < 3) //low error count?
                    {
                        yyerrflag = 3;
                        while (true)   //do until break
                        {
                            if (stateptr <
                              0)   //check for under & overflow here
                            {
                                yyerror("stack underflow. aborting...");  //note lower case 's'
                                return 1;
                            }
                            yyn = yysindex[state_peek(0)];
                            if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                              yyn <= YYTABLESIZE &&
                              yycheck[yyn] == YYERRCODE) {
                                if (yydebug) {
                                    debug("state " + state_peek(0) +
                                      ", error recovery shifting to state " +
                                      yytable[yyn] + " ");
                                }
                                yystate = yytable[yyn];
                                state_push(yystate);
                                val_push(yylval);
                                doaction = false;
                                break;
                            } else {
                                if (yydebug) {
                                    debug("error recovery discarding state " +
                                      state_peek(0) + " ");
                                }
                                if (stateptr <
                                  0)   //check for under & overflow here
                                {
                                    yyerror("Stack underflow. aborting...");  //capital 'S'
                                    return 1;
                                }
                                state_pop();
                                val_pop();
                            }
                        }
                    } else            //discard this token
                    {
                        if (yychar == 0) {
                            return 1; //yyabort
                        }
                        if (yydebug) {
                            yys = null;
                            if (yychar <= YYMAXTOKEN) {
                                yys = yyname[yychar];
                            }
                            if (yys == null) {
                                yys = "illegal-symbol";
                            }
                            debug("state " + yystate +
                              ", error recovery discards token " + yychar +
                              " (" + yys + ")");
                        }
                        yychar = -1;  //read another
                    }
                }//end error recovery
            }//yyn=0 loop
            if (!doaction)   //any reason not to proceed?
            {
                continue;      //skip action
            }
            yym = yylen[yyn];          //get count of terminals on rhs
            if (yydebug) {
                debug("state " + yystate + ", reducing " + yym + " by rule " +
                  yyn + " (" + yyrule[yyn] + ")");
            }
            if (yym > 0)                 //if count of rhs not 'nil'
            {
                yyval = val_peek(yym - 1); //get current semantic value
            }
            switch (yyn) {
//########## USER-SUPPLIED ACTIONS ##########
            case 1:
//#line 90 "term.y"
            {
                myOptResult = b.start((AstroArg)val_peek(0));
            }
            break;
            case 2:
//#line 91 "term.y"
            {
                myOptResult = b.start((Astro)val_peek(0));
            }
            break;
            case 3:
//#line 95 "term.y"
            {
                yyval = b.schema((AstroArg)val_peek(0));
            }
            break;
            case 5:
//#line 100 "term.y"
            {
                yyval = b.seq((AstroArg)val_peek(1), (Astro)val_peek(0));
            }
            break;
            case 6:
//#line 104 "term.y"
            {
                yyval =
                  b.production((Astro)val_peek(3), (AstroArg)val_peek(1));
            }
            break;
            case 7:
//#line 109 "term.y"
            {
                yyval = b.term((Astro)val_peek(0));
            }
            break;
            case 8:
//#line 110 "term.y"
            {
                yyval = b.term((Astro)val_peek(3), (AstroArg)val_peek(1));
            }
            break;
            case 9:
//#line 111 "term.y"
            {
                yyval = b.tuple((AstroArg)val_peek(1));
            }
            break;
            case 10:
//#line 112 "term.y"
            {
                yyval = b.attr((Astro)val_peek(2), (AstroArg)val_peek(0));
            }
            break;
            case 12:
//#line 114 "term.y"
            {
                yyval = b.term((Astro)val_peek(1), (Astro)val_peek(0));
            }
            break;
            case 13:
//#line 118 "term.y"
            {
                yyval = b.bag((AstroArg)val_peek(1));
            }
            break;
            case 17:
//#line 125 "term.y"
            {
                yyval = val_peek(0);
            }
            break;
            case 18:
//#line 126 "term.y"
            {
                yyval = b.taggedHole((Astro)val_peek(1), (Astro)val_peek(0));
            }
            break;
            case 19:
//#line 127 "term.y"
            {
                reserved("hole-tagged-hole");
            }
            break;
            case 20:
//#line 131 "term.y"
            {
                yyval = b.empty();
            }
            break;
            case 24:
//#line 138 "term.y"
            {
                yyval = b.seq((AstroArg)val_peek(2), (AstroArg)val_peek(0));
            }
            break;
            case 26:
//#line 143 "term.y"
            {
                yyval =
                  b.onlyChoice((AstroArg)val_peek(2), (AstroArg)val_peek(0));
            }
            break;
            case 27:
//#line 145 "term.y"
            {
                yyval =
                  b.firstChoice((AstroArg)val_peek(2), (AstroArg)val_peek(0));
            }
            break;
            case 29:
//#line 151 "term.y"
            {
                yyval =
                  b.interleave((AstroArg)val_peek(2), (AstroArg)val_peek(0));
            }
            break;
            case 31:
//#line 157 "term.y"
            {
                yyval = b.action((AstroArg)val_peek(2), (AstroArg)val_peek(0));
            }
            break;
            case 33:
//#line 162 "term.y"
            {
                yyval = b.not((AstroArg)val_peek(0));
            }
            break;
            case 35:
//#line 170 "term.y"
            {
                yyval = b.some((AstroArg)val_peek(1),
                               ((Character)val_peek(0)).charValue());
            }
            break;
            case 36:
//#line 172 "term.y"
            {
                yyval = b.some(null, ((Character)val_peek(0)).charValue());
            }
            break;
            case 37:
//#line 174 "term.y"
            {
                yyval =
                  b.some((AstroArg)val_peek(2), '*', (AstroArg)val_peek(0));
            }
            break;
            case 38:
//#line 177 "term.y"
            {
                yyval =
                  b.some((AstroArg)val_peek(2), '+', (AstroArg)val_peek(0));
            }
            break;
            case 39:
//#line 183 "term.y"
            {
                yyval = CharacterMakerSugar.valueOf('?');
            }
            break;
            case 40:
//#line 184 "term.y"
            {
                yyval = CharacterMakerSugar.valueOf('+');
            }
            break;
            case 41:
//#line 185 "term.y"
            {
                yyval = CharacterMakerSugar.valueOf('*');
            }
            break;
            case 43:
//#line 190 "term.y"
            {
                yyval = b.any();
            }
            break;
            case 44:
//#line 191 "term.y"
            {
                yyval = b.range((Astro)val_peek(2), (Astro)val_peek(0));
            }
            break;
            case 45:
//#line 192 "term.y"
            {
                yyval = b.unpack((Astro)val_peek(0));
            }
            break;
            case 46:
//#line 193 "term.y"
            {
                yyval = b.anyOf((Astro)val_peek(0));
            }
            break;
            case 47:
//#line 194 "term.y"
            {
                yyval = val_peek(1);
            }
            break;
            case 52:
//#line 205 "term.y"
            {
                yyval = untag((Astro)val_peek(0));
            }
            break;
            case 53:
//#line 212 "term.y"
            {
                yyval = b.dollarHole((Astro)val_peek(1));
            }
            break;
            case 54:
//#line 213 "term.y"
            {
                yyval = b.dollarHole((Astro)val_peek(0));
            }
            break;
            case 55:
//#line 214 "term.y"
            {
                yyval = b.dollarHole((Astro)val_peek(0));
            }
            break;
            case 56:
//#line 215 "term.y"
            {
                yyval = b.atHole((Astro)val_peek(1));
            }
            break;
            case 57:
//#line 216 "term.y"
            {
                yyval = b.atHole((Astro)val_peek(0));
            }
            break;
            case 58:
//#line 217 "term.y"
            {
                yyval = b.atHole((Astro)val_peek(0));
            }
            break;
            case 59:
//#line 218 "term.y"
            {
                yyval = b.atHole((Astro)val_peek(1));
            }
            break;
            case 60:
//#line 219 "term.y"
            {
                yyval = b.atHole((Astro)val_peek(0));
            }
            break;
            case 61:
//#line 220 "term.y"
            {
                yyval = b.atHole((Astro)val_peek(0));
            }
            break;
//#line 893 "TermParser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
            }//switch
            //#### Now let's reduce... ####
            if (yydebug) {
                debug("reduce");
            }
            state_drop(yym);             //we just reduced yylen states
            yystate = state_peek(0);     //get new state
            val_drop(yym);               //corresponding value drop
            yym = yylhs[yyn];            //select next TERMINAL(on lhs)
            if (yystate == 0 &&
              yym == 0)//done? 'rest' state and at first TERMINAL
            {
                debug("After reduction, shifting from state 0 to state " +
                  YYFINAL + "");
                yystate = YYFINAL;         //explicitly say we're done
                state_push(YYFINAL);       //and save it
                val_push(yyval);           //also save the semantic value of parsing
                if (yychar < 0)            //we want another character?
                {
                    yychar = yylex();        //get next character
                    if (yychar < 0) {
                        yychar = 0;  //clean, if necessary
                    }
                    if (yydebug) {
                        yylexdebug(yystate, yychar);
                    }
                }
                if (yychar == 0)          //Good exit (if lex returns 0 ;-)
                {
                    break;                 //quit the loop--all DONE
                }
            }//if yystate
            else                        //else not done yet
            {                         //get next state and push, for next yydefred[]
                yyn = yygindex[yym];      //find out where to go
                if ((yyn != 0) && (yyn += yystate) >= 0 &&
                  yyn <= YYTABLESIZE && yycheck[yyn] == yystate) {
                    yystate = yytable[yyn]; //get new state
                } else {
                    yystate = yydgoto[yym]; //else go to new defred
                }
                debug("after reduction, shifting from state " + state_peek(0) +
                  " to state " + yystate + "");
                state_push(yystate);     //going again, so push state & val...
                val_push(yyval);         //for next action
            }
        }//main loop
        return 0;//yyaccept!!
    }
//## end of method parse() ######################################


}
//################### END OF CLASS yaccpar ######################
