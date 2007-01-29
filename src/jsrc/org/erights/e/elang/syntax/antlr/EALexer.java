// $ANTLR 2.7.5rc2 (2005-01-08): "elex.g" -> "EALexer.java"$

package org.erights.e.elang.syntax.antlr;

import java.io.InputStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.ANTLRException;
import java.io.Reader;
import java.util.Hashtable;
import antlr.CharScanner;
import antlr.InputBuffer;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.Token;
import antlr.CommonToken;
import antlr.RecognitionException;
import antlr.NoViableAltForCharException;
import antlr.MismatchedCharException;
import antlr.TokenStream;
import antlr.ANTLRHashString;
import antlr.LexerSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.SemanticException;

public class EALexer extends antlr.SwitchingLexer implements EALexerTokenTypes {

    // set isFirstInLine whenever we produce a token, and reset it at the
    // beginning of every line
    protected boolean isFirstInLine = true;
    protected Token lastToken = null;
    protected Token makeToken(int t) {
        if (LINESEP != t) { isFirstInLine = false; }
        return lastToken = super.makeToken(t);
    }
    public void newline() {
        isFirstInLine = true;
        lastToken = null;
        super.newline();
    }
    //public void traceIn(String rname) throws CharStreamException {    }
public EALexer(InputStream in) {
	this(new ByteBuffer(in));
}
public EALexer(Reader in) {
	this(new CharBuffer(in));
}
public EALexer(InputBuffer ib) {
	this(new LexerSharedInputState(ib));
}
public EALexer(LexerSharedInputState state) {
	super(state);
	caseSensitiveLiterals = false;
	setCaseSensitive(true);
	literals = new Hashtable();
	literals.put(new ANTLRHashString("/=", this), new Integer(133));
	literals.put(new ANTLRHashString("suspects", this), new Integer(250));
	literals.put(new ANTLRHashString(":", this), new Integer(106));
	literals.put(new ANTLRHashString("encapsulated", this), new Integer(206));
	literals.put(new ANTLRHashString("define", this), new Integer(199));
	literals.put(new ANTLRHashString("abstract", this), new Integer(181));
	literals.put(new ANTLRHashString("facet", this), new Integer(214));
	literals.put(new ANTLRHashString("!", this), new Integer(168));
	literals.put(new ANTLRHashString("+=", this), new Integer(130));
	literals.put(new ANTLRHashString("defmacro", this), new Integer(200));
	literals.put(new ANTLRHashString("suchthat", this), new Integer(247));
	literals.put(new ANTLRHashString("**", this), new Integer(167));
	literals.put(new ANTLRHashString("escape", this), new Integer(107));
	literals.put(new ANTLRHashString("static", this), new Integer(245));
	literals.put(new ANTLRHashString("rely", this), new Integer(241));
	literals.put(new ANTLRHashString("break", this), new Integer(142));
	literals.put(new ANTLRHashString("fn", this), new Integer(109));
	literals.put(new ANTLRHashString("reliant", this), new Integer(239));
	literals.put(new ANTLRHashString("^=", this), new Integer(140));
	literals.put(new ANTLRHashString("constructor", this), new Integer(195));
	literals.put(new ANTLRHashString("continue", this), new Integer(143));
	literals.put(new ANTLRHashString("uses", this), new Integer(258));
	literals.put(new ANTLRHashString("catch", this), new Integer(174));
	literals.put(new ANTLRHashString("for", this), new Integer(92));
	literals.put(new ANTLRHashString("else", this), new Integer(91));
	literals.put(new ANTLRHashString(">>", this), new Integer(161));
	literals.put(new ANTLRHashString("truncatable", this), new Integer(254));
	literals.put(new ANTLRHashString("begin", this), new Integer(187));
	literals.put(new ANTLRHashString("interface", this), new Integer(126));
	literals.put(new ANTLRHashString("as", this), new Integer(183));
	literals.put(new ANTLRHashString("typedef", this), new Integer(255));
	literals.put(new ANTLRHashString("bind", this), new Integer(112));
	literals.put(new ANTLRHashString("**=", this), new Integer(136));
	literals.put(new ANTLRHashString("throws", this), new Integer(125));
	literals.put(new ANTLRHashString("@{", this), new Integer(179));
	literals.put(new ANTLRHashString("delicate", this), new Integer(201));
	literals.put(new ANTLRHashString("forall", this), new Integer(215));
	literals.put(new ANTLRHashString("reliance", this), new Integer(238));
	literals.put(new ANTLRHashString("<=>", this), new Integer(155));
	literals.put(new ANTLRHashString("&&", this), new Integer(147));
	literals.put(new ANTLRHashString(">=", this), new Integer(156));
	literals.put(new ANTLRHashString("signed", this), new Integer(244));
	literals.put(new ANTLRHashString(":=", this), new Integer(115));
	literals.put(new ANTLRHashString("var", this), new Integer(113));
	literals.put(new ANTLRHashString("method", this), new Integer(122));
	literals.put(new ANTLRHashString("private", this), new Integer(234));
	literals.put(new ANTLRHashString("pragma", this), new Integer(87));
	literals.put(new ANTLRHashString("oneway", this), new Integer(231));
	literals.put(new ANTLRHashString("~", this), new Integer(169));
	literals.put(new ANTLRHashString("*=", this), new Integer(132));
	literals.put(new ANTLRHashString("function", this), new Integer(217));
	literals.put(new ANTLRHashString("&=", this), new Integer(139));
	literals.put(new ANTLRHashString("${", this), new Integer(176));
	literals.put(new ANTLRHashString("}", this), new Integer(8));
	literals.put(new ANTLRHashString("octet", this), new Integer(230));
	literals.put(new ANTLRHashString("supports", this), new Integer(248));
	literals.put(new ANTLRHashString("declare", this), new Integer(197));
	literals.put(new ANTLRHashString(">>=", this), new Integer(137));
	literals.put(new ANTLRHashString("ensure", this), new Integer(209));
	literals.put(new ANTLRHashString("try", this), new Integer(111));
	literals.put(new ANTLRHashString("methods", this), new Integer(226));
	literals.put(new ANTLRHashString("lambda", this), new Integer(224));
	literals.put(new ANTLRHashString("know", this), new Integer(222));
	literals.put(new ANTLRHashString("|", this), new Integer(102));
	literals.put(new ANTLRHashString("belief", this), new Integer(189));
	literals.put(new ANTLRHashString("::", this), new Integer(173));
	literals.put(new ANTLRHashString("in", this), new Integer(93));
	literals.put(new ANTLRHashString("<-", this), new Integer(172));
	literals.put(new ANTLRHashString("operator", this), new Integer(232));
	literals.put(new ANTLRHashString("hides", this), new Integer(220));
	literals.put(new ANTLRHashString("an", this), new Integer(182));
	literals.put(new ANTLRHashString("{", this), new Integer(97));
	literals.put(new ANTLRHashString("switch", this), new Integer(110));
	literals.put(new ANTLRHashString("meta", this), new Integer(89));
	literals.put(new ANTLRHashString("encapsulates", this), new Integer(207));
	literals.put(new ANTLRHashString("reveal", this), new Integer(242));
	literals.put(new ANTLRHashString("&!", this), new Integer(150));
	literals.put(new ANTLRHashString("utf8", this), new Integer(260));
	literals.put(new ANTLRHashString("=>", this), new Integer(94));
	literals.put(new ANTLRHashString("virtual", this), new Integer(262));
	literals.put(new ANTLRHashString("this", this), new Integer(252));
	literals.put(new ANTLRHashString("believe", this), new Integer(190));
	literals.put(new ANTLRHashString("relies", this), new Integer(240));
	literals.put(new ANTLRHashString("when", this), new Integer(103));
	literals.put(new ANTLRHashString("attribute", this), new Integer(185));
	literals.put(new ANTLRHashString("/", this), new Integer(163));
	literals.put(new ANTLRHashString("sake", this), new Integer(243));
	literals.put(new ANTLRHashString("==", this), new Integer(148));
	literals.put(new ANTLRHashString("->", this), new Integer(104));
	literals.put(new ANTLRHashString("public", this), new Integer(236));
	literals.put(new ANTLRHashString("be", this), new Integer(186));
	literals.put(new ANTLRHashString(".", this), new Integer(88));
	literals.put(new ANTLRHashString("thunk", this), new Integer(108));
	literals.put(new ANTLRHashString("extends", this), new Integer(119));
	literals.put(new ANTLRHashString("<<=", this), new Integer(138));
	literals.put(new ANTLRHashString("%%", this), new Integer(166));
	literals.put(new ANTLRHashString("datatype", this), new Integer(196));
	literals.put(new ANTLRHashString("_", this), new Integer(98));
	literals.put(new ANTLRHashString("-=", this), new Integer(131));
	literals.put(new ANTLRHashString("|=", this), new Integer(141));
	literals.put(new ANTLRHashString("unum", this), new Integer(257));
	literals.put(new ANTLRHashString("-", this), new Integer(162));
	literals.put(new ANTLRHashString("to", this), new Integer(121));
	literals.put(new ANTLRHashString("synchronized", this), new Integer(251));
	literals.put(new ANTLRHashString("%=", this), new Integer(134));
	literals.put(new ANTLRHashString("case", this), new Integer(192));
	literals.put(new ANTLRHashString("let", this), new Integer(225));
	literals.put(new ANTLRHashString("!=", this), new Integer(149));
	literals.put(new ANTLRHashString("export", this), new Integer(213));
	literals.put(new ANTLRHashString("^", this), new Integer(145));
	literals.put(new ANTLRHashString("guards", this), new Integer(127));
	literals.put(new ANTLRHashString("eventually", this), new Integer(212));
	literals.put(new ANTLRHashString("//", this), new Integer(164));
	literals.put(new ANTLRHashString(",", this), new Integer(117));
	literals.put(new ANTLRHashString("behalf", this), new Integer(188));
	literals.put(new ANTLRHashString("transient", this), new Integer(253));
	literals.put(new ANTLRHashString("do", this), new Integer(204));
	literals.put(new ANTLRHashString("given", this), new Integer(218));
	literals.put(new ANTLRHashString("]", this), new Integer(171));
	literals.put(new ANTLRHashString("+", this), new Integer(99));
	literals.put(new ANTLRHashString("unsigned", this), new Integer(256));
	literals.put(new ANTLRHashString("eventual", this), new Integer(211));
	literals.put(new ANTLRHashString("enum", this), new Integer(210));
	literals.put(new ANTLRHashString("implements", this), new Integer(120));
	literals.put(new ANTLRHashString("*", this), new Integer(100));
	literals.put(new ANTLRHashString("deprecated", this), new Integer(202));
	literals.put(new ANTLRHashString("%%=", this), new Integer(135));
	literals.put(new ANTLRHashString("protected", this), new Integer(235));
	literals.put(new ANTLRHashString("[", this), new Integer(170));
	literals.put(new ANTLRHashString("finally", this), new Integer(105));
	literals.put(new ANTLRHashString("if", this), new Integer(90));
	literals.put(new ANTLRHashString("hidden", this), new Integer(219));
	literals.put(new ANTLRHashString("const", this), new Integer(194));
	literals.put(new ANTLRHashString(")", this), new Integer(118));
	literals.put(new ANTLRHashString("utf16", this), new Integer(261));
	literals.put(new ANTLRHashString("return", this), new Integer(144));
	literals.put(new ANTLRHashString("match", this), new Integer(124));
	literals.put(new ANTLRHashString("<=", this), new Integer(154));
	literals.put(new ANTLRHashString("//=", this), new Integer(129));
	literals.put(new ANTLRHashString("(", this), new Integer(116));
	literals.put(new ANTLRHashString("default", this), new Integer(198));
	literals.put(new ANTLRHashString("native", this), new Integer(228));
	literals.put(new ANTLRHashString("suspect", this), new Integer(249));
	literals.put(new ANTLRHashString("<<", this), new Integer(160));
	literals.put(new ANTLRHashString("dispatch", this), new Integer(203));
	literals.put(new ANTLRHashString("@", this), new Integer(180));
	literals.put(new ANTLRHashString("assert", this), new Integer(184));
	literals.put(new ANTLRHashString("fun", this), new Integer(216));
	literals.put(new ANTLRHashString("believes", this), new Integer(191));
	literals.put(new ANTLRHashString("$$", this), new Integer(178));
	literals.put(new ANTLRHashString("end", this), new Integer(208));
	literals.put(new ANTLRHashString("wstring", this), new Integer(264));
	literals.put(new ANTLRHashString("knows", this), new Integer(223));
	literals.put(new ANTLRHashString("?", this), new Integer(175));
	literals.put(new ANTLRHashString("raises", this), new Integer(237));
	literals.put(new ANTLRHashString("&", this), new Integer(101));
	literals.put(new ANTLRHashString("accum", this), new Integer(95));
	literals.put(new ANTLRHashString("on", this), new Integer(123));
	literals.put(new ANTLRHashString("=~", this), new Integer(151));
	literals.put(new ANTLRHashString("def", this), new Integer(114));
	literals.put(new ANTLRHashString("using", this), new Integer(259));
	literals.put(new ANTLRHashString(">", this), new Integer(157));
	literals.put(new ANTLRHashString("%", this), new Integer(165));
	literals.put(new ANTLRHashString("namespace", this), new Integer(227));
	literals.put(new ANTLRHashString("class", this), new Integer(193));
	literals.put(new ANTLRHashString("..", this), new Integer(158));
	literals.put(new ANTLRHashString("struct", this), new Integer(246));
	literals.put(new ANTLRHashString("=", this), new Integer(128));
	literals.put(new ANTLRHashString("inline", this), new Integer(221));
	literals.put(new ANTLRHashString("$", this), new Integer(177));
	literals.put(new ANTLRHashString("while", this), new Integer(96));
	literals.put(new ANTLRHashString("encapsulate", this), new Integer(205));
	literals.put(new ANTLRHashString("!~", this), new Integer(152));
	literals.put(new ANTLRHashString("package", this), new Integer(233));
	literals.put(new ANTLRHashString("<", this), new Integer(153));
	literals.put(new ANTLRHashString("..!", this), new Integer(159));
	literals.put(new ANTLRHashString(";", this), new Integer(86));
	literals.put(new ANTLRHashString("volatile", this), new Integer(263));
	literals.put(new ANTLRHashString("obeys", this), new Integer(229));
	literals.put(new ANTLRHashString("||", this), new Integer(146));
}

public Token nextToken() throws TokenStreamException {
	Token theRetToken=null;
tryAgain:
	for (;;) {
		Token _token = null;
		int _ttype = Token.INVALID_TYPE;
		resetText();
		try {   // for char stream error handling
			try {   // for lexical error handling
				switch ( LA(1)) {
				case '`':
				{
					mQUASIOPEN(true);
					theRetToken=_returnToken;
					break;
				}
				case '(':
				{
					mLPAREN(true);
					theRetToken=_returnToken;
					break;
				}
				case ')':
				{
					mRPAREN(true);
					theRetToken=_returnToken;
					break;
				}
				case '[':
				{
					mLBRACK(true);
					theRetToken=_returnToken;
					break;
				}
				case ']':
				{
					mRBRACK(true);
					theRetToken=_returnToken;
					break;
				}
				case '{':
				{
					mLCURLY(true);
					theRetToken=_returnToken;
					break;
				}
				case '}':
				{
					mRCURLY(true);
					theRetToken=_returnToken;
					break;
				}
				case '$':
				{
					mDOLLARCURLY(true);
					theRetToken=_returnToken;
					break;
				}
				case '?':
				{
					mQUESTION(true);
					theRetToken=_returnToken;
					break;
				}
				case ',':
				{
					mCOMMA(true);
					theRetToken=_returnToken;
					break;
				}
				case '~':
				{
					mBNOT(true);
					theRetToken=_returnToken;
					break;
				}
				case ';':
				{
					mSEMI(true);
					theRetToken=_returnToken;
					break;
				}
				case '>':
				{
					mGT(true);
					theRetToken=_returnToken;
					break;
				}
				case 'A':  case 'B':  case 'C':  case 'D':
				case 'E':  case 'F':  case 'G':  case 'H':
				case 'I':  case 'J':  case 'K':  case 'L':
				case 'M':  case 'N':  case 'O':  case 'P':
				case 'Q':  case 'R':  case 'S':  case 'T':
				case 'U':  case 'V':  case 'W':  case 'X':
				case 'Y':  case 'Z':  case '_':  case 'a':
				case 'b':  case 'c':  case 'd':  case 'e':
				case 'f':  case 'g':  case 'h':  case 'i':
				case 'j':  case 'k':  case 'l':  case 'm':
				case 'n':  case 'o':  case 'p':  case 'q':
				case 'r':  case 's':  case 't':  case 'u':
				case 'v':  case 'w':  case 'x':  case 'y':
				case 'z':
				{
					mIDENT(true);
					theRetToken=_returnToken;
					break;
				}
				case '\t':  case '\u000c':  case ' ':  case '\\':
				{
					mWS(true);
					theRetToken=_returnToken;
					break;
				}
				case '\n':  case '\r':
				{
					mLINESEP(true);
					theRetToken=_returnToken;
					break;
				}
				case '#':
				{
					mSL_COMMENT(true);
					theRetToken=_returnToken;
					break;
				}
				case '\'':
				{
					mCHAR_LITERAL(true);
					theRetToken=_returnToken;
					break;
				}
				case '"':
				{
					mSTRING(true);
					theRetToken=_returnToken;
					break;
				}
				case '0':  case '1':  case '2':  case '3':
				case '4':  case '5':  case '6':  case '7':
				case '8':  case '9':
				{
					mINT(true);
					theRetToken=_returnToken;
					break;
				}
				default:
					if (('.' == LA(1)) && ('.' == LA(2)) && (
                                          '!' == LA(3))) {
						mTILL(true);
						theRetToken=_returnToken;
					}
					else if (('<' == LA(1)) && ('=' ==
                                          LA(2)) && ('>' == LA(3))) {
						mABA(true);
						theRetToken=_returnToken;
					}
					else if (('/' == LA(1)) && ('/' ==
                                          LA(2)) && ('=' == LA(3))) {
						mFLOORDIV_ASSIGN(true);
						theRetToken=_returnToken;
					}
					else if (('%' == LA(1)) && ('%' ==
                                          LA(2)) && ('=' == LA(3))) {
						mMOD_ASSIGN(true);
						theRetToken=_returnToken;
					}
					else if (('*' == LA(1)) && ('*' ==
                                          LA(2)) && ('=' == LA(3))) {
						mPOW_ASSIGN(true);
						theRetToken=_returnToken;
					}
					else if (('<' == LA(1)) && ('<' ==
                                          LA(2)) && ('=' == LA(3))) {
						mSL_ASSIGN(true);
						theRetToken=_returnToken;
					}
					else if ((':' == LA(1)) && (':' ==
                                          LA(2)) && ('&' == LA(3))) {
						mSCOPESLOT(true);
						theRetToken=_returnToken;
					}
					else if (('@' == LA(1)) && ('{' ==
                                          LA(2))) {
						mATCURLY(true);
						theRetToken=_returnToken;
					}
					else if (('.' == LA(1)) && ('.' ==
                                          LA(2)) && (true)) {
						mTHRU(true);
						theRetToken=_returnToken;
					}
					else if (('=' == LA(1)) && ('=' ==
                                          LA(2))) {
						mSAME(true);
						theRetToken=_returnToken;
					}
					else if (('!' == LA(1)) && ('=' ==
                                          LA(2))) {
						mNOTSAME(true);
						theRetToken=_returnToken;
					}
					else if (('/' == LA(1)) && ('/' ==
                                          LA(2)) && (true)) {
						mFLOORDIV(true);
						theRetToken=_returnToken;
					}
					else if (('+' == LA(1)) && ('+' ==
                                          LA(2))) {
						mINC(true);
						theRetToken=_returnToken;
					}
					else if (('-' == LA(1)) && ('-' ==
                                          LA(2))) {
						mDEC(true);
						theRetToken=_returnToken;
					}
					else if (('%' == LA(1)) && ('%' ==
                                          LA(2)) && (true)) {
						mMOD(true);
						theRetToken=_returnToken;
					}
					else if (('<' == LA(1)) && ('<' ==
                                          LA(2)) && (true)) {
						mSL(true);
						theRetToken=_returnToken;
					}
					else if (('<' == LA(1)) && ('=' ==
                                          LA(2)) && (true)) {
						mLE(true);
						theRetToken=_returnToken;
					}
					else if (('|' == LA(1)) && ('|' ==
                                          LA(2))) {
						mLOR(true);
						theRetToken=_returnToken;
					}
					else if (('&' == LA(1)) && ('!' ==
                                          LA(2))) {
						mBUTNOT(true);
						theRetToken=_returnToken;
					}
					else if (('&' == LA(1)) && ('&' ==
                                          LA(2))) {
						mLAND(true);
						theRetToken=_returnToken;
					}
					else if (('*' == LA(1)) && ('*' ==
                                          LA(2)) && (true)) {
						mPOW(true);
						theRetToken=_returnToken;
					}
					else if ((':' == LA(1)) && ('=' ==
                                          LA(2))) {
						mASSIGN(true);
						theRetToken=_returnToken;
					}
					else if (('/' == LA(1)) && ('=' ==
                                          LA(2))) {
						mDIV_ASSIGN(true);
						theRetToken=_returnToken;
					}
					else if (('+' == LA(1)) && ('=' ==
                                          LA(2))) {
						mPLUS_ASSIGN(true);
						theRetToken=_returnToken;
					}
					else if (('-' == LA(1)) && ('=' ==
                                          LA(2))) {
						mMINUS_ASSIGN(true);
						theRetToken=_returnToken;
					}
					else if (('*' == LA(1)) && ('=' ==
                                          LA(2))) {
						mSTAR_ASSIGN(true);
						theRetToken=_returnToken;
					}
					else if (('%' == LA(1)) && ('=' ==
                                          LA(2))) {
						mREM_ASSIGN(true);
						theRetToken=_returnToken;
					}
					else if (('^' == LA(1)) && ('=' ==
                                          LA(2))) {
						mBXOR_ASSIGN(true);
						theRetToken=_returnToken;
					}
					else if (('|' == LA(1)) && ('=' ==
                                          LA(2))) {
						mBOR_ASSIGN(true);
						theRetToken=_returnToken;
					}
					else if (('&' == LA(1)) && ('=' ==
                                          LA(2))) {
						mBAND_ASSIGN(true);
						theRetToken=_returnToken;
					}
					else if (('<' == LA(1)) && ('-' ==
                                          LA(2))) {
						mSEND(true);
						theRetToken=_returnToken;
					}
					else if (('-' == LA(1)) && ('>' ==
                                          LA(2))) {
						mWHEN(true);
						theRetToken=_returnToken;
					}
					else if (('=' == LA(1)) && ('>' ==
                                          LA(2))) {
						mMAPSTO(true);
						theRetToken=_returnToken;
					}
					else if (('=' == LA(1)) && ('~' ==
                                          LA(2))) {
						mMATCHBIND(true);
						theRetToken=_returnToken;
					}
					else if (('!' == LA(1)) && ('~' ==
                                          LA(2))) {
						mMISMATCH(true);
						theRetToken=_returnToken;
					}
					else if ((':' == LA(1)) && (':' ==
                                          LA(2)) && (true)) {
						mSCOPE(true);
						theRetToken=_returnToken;
					}
					else if (('/' == LA(1)) && ('*' ==
                                          LA(2))) {
						mDOC_COMMENT(true);
						theRetToken=_returnToken;
					}
					else if (('@' == LA(1)) && (true)) {
						mAT(true);
						theRetToken=_returnToken;
					}
					else if ((':' == LA(1)) && (true)) {
						mCOLON(true);
						theRetToken=_returnToken;
					}
					else if (('.' == LA(1)) && (true)) {
						mDOT(true);
						theRetToken=_returnToken;
					}
					else if (('=' == LA(1)) && (true)) {
						mEQ(true);
						theRetToken=_returnToken;
					}
					else if (('!' == LA(1)) && (true)) {
						mLNOT(true);
						theRetToken=_returnToken;
					}
					else if (('/' == LA(1)) && (true)) {
						mDIV(true);
						theRetToken=_returnToken;
					}
					else if (('+' == LA(1)) && (true)) {
						mPLUS(true);
						theRetToken=_returnToken;
					}
					else if (('-' == LA(1)) && (true)) {
						mMINUS(true);
						theRetToken=_returnToken;
					}
					else if (('*' == LA(1)) && (true)) {
						mSTAR(true);
						theRetToken=_returnToken;
					}
					else if (('%' == LA(1)) && (true)) {
						mREM(true);
						theRetToken=_returnToken;
					}
					else if (('^' == LA(1)) && (true)) {
						mBXOR(true);
						theRetToken=_returnToken;
					}
					else if (('|' == LA(1)) && (true)) {
						mBOR(true);
						theRetToken=_returnToken;
					}
					else if (('&' == LA(1)) && (true)) {
						mBAND(true);
						theRetToken=_returnToken;
					}
					else if (('<' == LA(1)) && (true)) {
						mLT(true);
						theRetToken=_returnToken;
					}
				else {
					if (EOF_CHAR == LA(1)) {uponEOF(); _returnToken = makeToken(Token.EOF_TYPE);}
				else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				}
				if ( _returnToken==null ) continue tryAgain; // found SKIP token
				_ttype = _returnToken.getType();
				_ttype = testLiteralsTable(_ttype);
				_returnToken.setType(_ttype);
				return _returnToken;
			}
			catch (RecognitionException e) {
				throw new TokenStreamRecognitionException(e);
			}
		}
		catch (CharStreamException cse) {
			if ( cse instanceof CharStreamIOException ) {
				throw new TokenStreamIOException(((CharStreamIOException)cse).io);
			}
			else {
				throw new TokenStreamException(cse.getMessage());
			}
		}
	}
}

	public final void mQUASIOPEN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = QUASIOPEN;
		int _saveIndex;

		match('`');
		if (0 == inputState.guessing) {
			selector.push("quasi");
		}
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mLPAREN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LPAREN;
		int _saveIndex;

		match('(');
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	protected final void mBR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = BR;
		int _saveIndex;

		{
		_saveIndex=text.length();
		}
		{
		_loop157:
		do {
			switch ( LA(1)) {
			case ' ':
			{
				match(' ');
				break;
			}
			case '\t':
			{
				match('\t');
				break;
			}
			case '#':
			{
				match("#");
				{
				_loop156:
				do {
					if ((_tokenSet_0.member(LA(1))) && (true) && (true)) {
						{
						{
						match(_tokenSet_0);
						}
						}
					}
					else {
						break _loop156;
					}

				} while (true);
				}
				break;
			}
			case '\n':  case '\r':
			{
				mEOL(false);
				break;
			}
			default:
			{
				break _loop157;
			}
			}
		} while (true);
		}
		{
		text.setLength(_saveIndex);
		}
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mRPAREN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = RPAREN;
		int _saveIndex;

		match(')');
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mLBRACK(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LBRACK;
		int _saveIndex;

		match('[');
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mRBRACK(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = RBRACK;
		int _saveIndex;

		match(']');
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mLCURLY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LCURLY;
		int _saveIndex;

		match('{');
		mBR(false);
		if (0 == inputState.guessing) {
			selector.enterBrace();
		}
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mRCURLY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = RCURLY;
		int _saveIndex;

		match('}');
		if (0 == inputState.guessing) {
			selector.exitBrace();
		}
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mAT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = AT;
		int _saveIndex;

		match('@');
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mATCURLY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ATCURLY;
		int _saveIndex;

		match("@{");
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mDOLLARCURLY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DOLLARCURLY;
		int _saveIndex;

		match("${");
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mQUESTION(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = QUESTION;
		int _saveIndex;

		match('?');
		{
		if (( true )&&(isFirstInLine)) {
			mUPDOC(false);
			if (0 == inputState.guessing) {
				_ttype = Token.SKIP;
			}
		}
		else {
			mBR(false);
		}

		}
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	protected final void mUPDOC(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = UPDOC;
		int _saveIndex;

		{
		_loop94:
		do {
			if ((_tokenSet_1.member(LA(1)))) {
				{
				match(_tokenSet_1);
				}
			}
			else {
				break _loop94;
			}

		} while (true);
		}
		{
		if (('\n' == LA(1) || '\r' == LA(1))) {
			mEOL(false);
			{
			_loop101:
			do {
				switch ( LA(1)) {
				case ' ':
				{
					match(' ');
					break;
				}
				case '\t':
				{
					match('\t');
					break;
				}
				case '\u000c':
				{
					match('\f');
					break;
				}
				case '#':  case '>':  case '?':
				{
					{
					switch ( LA(1)) {
					case '?':
					{
						match('?');
						break;
					}
					case '#':
					{
						match('#');
						break;
					}
					case '>':
					{
						match('>');
						break;
					}
					default:
					{
						throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
					}
					}
					}
					{
					_loop100:
					do {
						if ((_tokenSet_1.member(LA(1))) && (true) && (true)) {
							{
							match(_tokenSet_1);
							}
						}
						else {
							break _loop100;
						}

					} while (true);
					}
					break;
				}
				case '\n':  case '\r':
				{
					mEOL(false);
					break;
				}
				default:
				{
					break _loop101;
				}
				}
			} while (true);
			}
		}
		else {
		}

		}
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mCOLON(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = COLON;
		int _saveIndex;

		match(':');
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mCOMMA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = COMMA;
		int _saveIndex;

		match(',');
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mDOT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DOT;
		int _saveIndex;

		match('.');
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mTHRU(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = THRU;
		int _saveIndex;

		match("..");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mTILL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TILL;
		int _saveIndex;

		match("..!");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mSAME(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SAME;
		int _saveIndex;

		match("==");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mEQ(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = EQ;
		int _saveIndex;

		match('=');
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mLNOT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LNOT;
		int _saveIndex;

		match('!');
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mBNOT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = BNOT;
		int _saveIndex;

		match('~');
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mNOTSAME(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NOTSAME;
		int _saveIndex;

		match("!=");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mDIV(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DIV;
		int _saveIndex;

		match('/');
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mFLOORDIV(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = FLOORDIV;
		int _saveIndex;

		match("//");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mPLUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PLUS;
		int _saveIndex;

		match('+');
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mMINUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MINUS;
		int _saveIndex;

		match('-');
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mINC(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = INC;
		int _saveIndex;

		match("++");
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mDEC(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DEC;
		int _saveIndex;

		match("--");
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mSTAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = STAR;
		int _saveIndex;

		match('*');
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mREM(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = REM;
		int _saveIndex;

		match('%');
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mMOD(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MOD;
		int _saveIndex;

		match("%%");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mSL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SL;
		int _saveIndex;

		match("<<");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mLE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LE;
		int _saveIndex;

		match("<=");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mABA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ABA;
		int _saveIndex;

		match("<=>");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mBXOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = BXOR;
		int _saveIndex;

		match('^');
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mBOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = BOR;
		int _saveIndex;

		match('|');
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mLOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LOR;
		int _saveIndex;

		match("||");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mBAND(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = BAND;
		int _saveIndex;

		match('&');
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mBUTNOT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = BUTNOT;
		int _saveIndex;

		match("&!");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mLAND(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LAND;
		int _saveIndex;

		match("&&");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mSEMI(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SEMI;
		int _saveIndex;

		match(';');
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mPOW(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = POW;
		int _saveIndex;

		match("**");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ASSIGN;
		int _saveIndex;

		match(":=");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mFLOORDIV_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = FLOORDIV_ASSIGN;
		int _saveIndex;

		match("//=");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mDIV_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DIV_ASSIGN;
		int _saveIndex;

		match("/=");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mPLUS_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PLUS_ASSIGN;
		int _saveIndex;

		match("+=");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mMINUS_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MINUS_ASSIGN;
		int _saveIndex;

		match("-=");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mSTAR_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = STAR_ASSIGN;
		int _saveIndex;

		match("*=");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mREM_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = REM_ASSIGN;
		int _saveIndex;

		match("%=");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mMOD_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MOD_ASSIGN;
		int _saveIndex;

		match("%%=");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mPOW_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = POW_ASSIGN;
		int _saveIndex;

		match("**=");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mSL_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SL_ASSIGN;
		int _saveIndex;

		match("<<=");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mBXOR_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = BXOR_ASSIGN;
		int _saveIndex;

		match("^=");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mBOR_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = BOR_ASSIGN;
		int _saveIndex;

		match("|=");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mBAND_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = BAND_ASSIGN;
		int _saveIndex;

		match("&=");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mSEND(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SEND;
		int _saveIndex;

		match("<-");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mWHEN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = WHEN;
		int _saveIndex;

		match("->");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mMAPSTO(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MAPSTO;
		int _saveIndex;

		match("=>");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mMATCHBIND(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MATCHBIND;
		int _saveIndex;

		match("=~");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mMISMATCH(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MISMATCH;
		int _saveIndex;

		match("!~");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mSCOPE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SCOPE;
		int _saveIndex;

		match("::");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mSCOPESLOT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SCOPESLOT;
		int _saveIndex;

		match("::&");
		mBR(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mGT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = GT;
		int _saveIndex;

		match('>');
		{
		if (('>' == LA(1)) && ('=' == LA(2))) {
			match(">=");
			mBR(false);
			if (0 == inputState.guessing) {
				_ttype = SR_ASSIGN;
			}
		}
		else if (('>' == LA(1)) && (true)) {
			match('>');
			mBR(false);
			if (0 == inputState.guessing) {
				_ttype = SR;
			}
		}
		else if (('=' == LA(1))) {
			match('=');
			mBR(false);
			if (0 == inputState.guessing) {
				_ttype = GE;
			}
		}
		else {
		}

		}
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mLT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LT;
		int _saveIndex;

		boolean synPredMatched68 = false;
		if ((('<' == LA(1)) && (_tokenSet_2.member(LA(2))))) {
			int _m68 = mark();
			synPredMatched68 = true;
			inputState.guessing++;
			try {
				{
				match('<');
				mIDENT(false);
				{
				switch ( LA(1)) {
				case '>':
				{
					match('>');
					break;
				}
				case ':':
				{
					match(':');
					break;
				}
				default:
				{
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
				}
				}
				}
			}
			catch (RecognitionException pe) {
				synPredMatched68 = false;
			}
			rewind(_m68);
			inputState.guessing--;
		}
		if ( synPredMatched68 ) {
			_saveIndex=text.length();
			match('<');
			text.setLength(_saveIndex);
			mIDENT(false);
			{
			switch ( LA(1)) {
			case '>':
			{
				_saveIndex=text.length();
				match('>');
				text.setLength(_saveIndex);
				if (0 == inputState.guessing) {
					_ttype = URIGetter;
				}
				break;
			}
			case ':':
			{
				match(':');
				{
				if ((_tokenSet_3.member(LA(1))) && (_tokenSet_4.member(LA(2))) && (true)) {
					mURI(false);
					_saveIndex=text.length();
					match('>');
					text.setLength(_saveIndex);
					if (0 == inputState.guessing) {
						_ttype = URI;
					}
				}
				else {
					boolean synPredMatched72 = false;
					if (( true )) {
						int _m72 = mark();
						synPredMatched72 = true;
						inputState.guessing++;
						try {
							{
							mANYWS(false);
							}
						}
						catch (RecognitionException pe) {
							synPredMatched72 = false;
						}
						rewind(_m72);
						inputState.guessing--;
					}
					if ( synPredMatched72 ) {
						mBR(false);
						if (0 == inputState.guessing) {
							_ttype = URIStart;
						}
					}
					else {
						throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
					}
					}
					}
					break;
				}
				default:
				{
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
				}
				}
			}
			else if (('<' == LA(1)) && (true)) {
				match('<');
				mBR(false);
			}
			else {
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}

			if ( _createToken && _token==null &&
                          Token.SKIP != _ttype) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		}

	public final void mIDENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = IDENT;
		int _saveIndex;

		{
		switch ( LA(1)) {
		case 'a':  case 'b':  case 'c':  case 'd':
		case 'e':  case 'f':  case 'g':  case 'h':
		case 'i':  case 'j':  case 'k':  case 'l':
		case 'm':  case 'n':  case 'o':  case 'p':
		case 'q':  case 'r':  case 's':  case 't':
		case 'u':  case 'v':  case 'w':  case 'x':
		case 'y':  case 'z':
		{
			matchRange('a','z');
			break;
		}
		case 'A':  case 'B':  case 'C':  case 'D':
		case 'E':  case 'F':  case 'G':  case 'H':
		case 'I':  case 'J':  case 'K':  case 'L':
		case 'M':  case 'N':  case 'O':  case 'P':
		case 'Q':  case 'R':  case 'S':  case 'T':
		case 'U':  case 'V':  case 'W':  case 'X':
		case 'Y':  case 'Z':
		{
			matchRange('A','Z');
			break;
		}
		case '_':
		{
			match('_');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		{
		_loop128:
		do {
			switch ( LA(1)) {
			case 'a':  case 'b':  case 'c':  case 'd':
			case 'e':  case 'f':  case 'g':  case 'h':
			case 'i':  case 'j':  case 'k':  case 'l':
			case 'm':  case 'n':  case 'o':  case 'p':
			case 'q':  case 'r':  case 's':  case 't':
			case 'u':  case 'v':  case 'w':  case 'x':
			case 'y':  case 'z':
			{
				matchRange('a','z');
				break;
			}
			case 'A':  case 'B':  case 'C':  case 'D':
			case 'E':  case 'F':  case 'G':  case 'H':
			case 'I':  case 'J':  case 'K':  case 'L':
			case 'M':  case 'N':  case 'O':  case 'P':
			case 'Q':  case 'R':  case 'S':  case 'T':
			case 'U':  case 'V':  case 'W':  case 'X':
			case 'Y':  case 'Z':
			{
				matchRange('A','Z');
				break;
			}
			case '_':
			{
				match('_');
				break;
			}
			case '0':  case '1':  case '2':  case '3':
			case '4':  case '5':  case '6':  case '7':
			case '8':  case '9':
			{
				matchRange('0','9');
				break;
			}
			default:
			{
				break _loop128;
			}
			}
		} while (true);
		}
		_ttype = testLiteralsTable(_ttype);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	protected final void mANYWS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ANYWS;
		int _saveIndex;

		switch ( LA(1)) {
		case ' ':
		{
			match(' ');
			break;
		}
		case '\t':
		{
			match('\t');
			break;
		}
		case '\u000c':
		{
			match('\f');
			break;
		}
		case '\r':
		{
			match('\r');
			break;
		}
		case '\n':
		{
			match('\n');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	protected final void mURI(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = URI;
		int _saveIndex;

		{
		int _cnt163=0;
		_loop163:
		do {
			switch ( LA(1)) {
			case 'a':  case 'b':  case 'c':  case 'd':
			case 'e':  case 'f':  case 'g':  case 'h':
			case 'i':  case 'j':  case 'k':  case 'l':
			case 'm':  case 'n':  case 'o':  case 'p':
			case 'q':  case 'r':  case 's':  case 't':
			case 'u':  case 'v':  case 'w':  case 'x':
			case 'y':  case 'z':
			{
				matchRange('a','z');
				break;
			}
			case 'A':  case 'B':  case 'C':  case 'D':
			case 'E':  case 'F':  case 'G':  case 'H':
			case 'I':  case 'J':  case 'K':  case 'L':
			case 'M':  case 'N':  case 'O':  case 'P':
			case 'Q':  case 'R':  case 'S':  case 'T':
			case 'U':  case 'V':  case 'W':  case 'X':
			case 'Y':  case 'Z':
			{
				matchRange('A','Z');
				break;
			}
			case '_':
			{
				match('_');
				break;
			}
			case '0':  case '1':  case '2':  case '3':
			case '4':  case '5':  case '6':  case '7':
			case '8':  case '9':
			{
				matchRange('0','9');
				break;
			}
			case ';':
			{
				match(';');
				break;
			}
			case '/':
			{
				match('/');
				break;
			}
			case '?':
			{
				match('?');
				break;
			}
			case ':':
			{
				match(':');
				break;
			}
			case '@':
			{
				match('@');
				break;
			}
			case '&':
			{
				match('&');
				break;
			}
			case '=':
			{
				match('=');
				break;
			}
			case '+':
			{
				match('+');
				break;
			}
			case '$':
			{
				match('$');
				break;
			}
			case ',':
			{
				match(',');
				break;
			}
			case '-':
			{
				match('-');
				break;
			}
			case '.':
			{
				match('.');
				break;
			}
			case '!':
			{
				match('!');
				break;
			}
			case '~':
			{
				match('~');
				break;
			}
			case '*':
			{
				match('*');
				break;
			}
			case '\'':
			{
				match('\'');
				break;
			}
			case '(':
			{
				match('(');
				break;
			}
			case ')':
			{
				match(')');
				break;
			}
			case '%':
			{
				match('%');
				break;
			}
			case '\\':
			{
				match('\\');
				break;
			}
			case '|':
			{
				match('|');
				break;
			}
			case '#':
			{
				match('#');
				break;
			}
			default:
			{
				if (1 <= _cnt163) { break _loop163; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			}
			_cnt163++;
		} while (true);
		}
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mWS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = WS;
		int _saveIndex;

		{
		int _cnt75=0;
		_loop75:
		do {
			switch ( LA(1)) {
			case ' ':
			{
				match(' ');
				break;
			}
			case '\t':
			{
				match('\t');
				break;
			}
			case '\u000c':
			{
				match('\f');
				break;
			}
			case '\\':
			{
				mESCWS(false);
				break;
			}
			default:
			{
				if (1 <= _cnt75) { break _loop75; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			}
			_cnt75++;
		} while (true);
		}
		if (0 == inputState.guessing) {
			_ttype = Token.SKIP;
		}
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	protected final void mESCWS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ESCWS;
		int _saveIndex;

		match('\\');
		{
		_loop78:
		do {
			switch ( LA(1)) {
			case ' ':
			{
				match(' ');
				break;
			}
			case '\t':
			{
				match('\t');
				break;
			}
			case '\u000c':
			{
				match('\f');
				break;
			}
			default:
			{
				break _loop78;
			}
			}
		} while (true);
		}
		mEOL(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	protected final void mEOL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = EOL;
		int _saveIndex;

		{
		if (('\r' == LA(1)) && ('\n' == LA(2)) && (true)) {
			match("\r\n");
		}
		else if (('\r' == LA(1)) && (true) && (true)) {
			match('\r');
		}
		else if (('\n' == LA(1))) {
			match('\n');
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}

		}
		if (0 == inputState.guessing) {
			newline();
		}
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mLINESEP(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LINESEP;
		int _saveIndex;

		{
		int _cnt82=0;
		_loop82:
		do {
			if (('\n' == LA(1) || '\r' == LA(1))) {
				mEOL(false);
			}
			else {
				if (1 <= _cnt82) { break _loop82; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}

			_cnt82++;
		} while (true);
		}
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mSL_COMMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SL_COMMENT;
		int _saveIndex;

		match("#");
		{
		_loop86:
		do {
			if ((_tokenSet_1.member(LA(1)))) {
				{
				match(_tokenSet_1);
				}
			}
			else {
				break _loop86;
			}

		} while (true);
		}
		if (0 == inputState.guessing) {
			_ttype = Token.SKIP;
		}
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	protected final void mSKIPLINE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SKIPLINE;
		int _saveIndex;

		{
		_loop90:
		do {
			if ((_tokenSet_1.member(LA(1)))) {
				{
				match(_tokenSet_1);
				}
			}
			else {
				break _loop90;
			}

		} while (true);
		}
		mEOL(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mDOC_COMMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DOC_COMMENT;
		int _saveIndex;

		match("/**");
		{
		_loop105:
		do {
			if ((('*' == LA(1)) && (('\u0003' <= LA(2) &&
                          '\u00ff' >= LA(2))) && (('\u0003' <= LA(3) &&
                          '\u00ff' >= LA(3))))&&('/' != LA(2))) {
				match('*');
			}
			else if (('\n' == LA(1) || '\r' == LA(1))) {
				mEOL(false);
			}
			else if ((_tokenSet_5.member(LA(1)))) {
				{
				match(_tokenSet_5);
				}
			}
			else {
				break _loop105;
			}

		} while (true);
		}
		match('*');
		match('/');
		mBR(false);
		if (0 == inputState.guessing) {
			text.setLength(_begin); text.append("**comment hidden**");
		}
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mCHAR_LITERAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CHAR_LITERAL;
		int _saveIndex;

		match('\'');
		{
		if (('\\' == LA(1))) {
			mESC(false);
		}
		else if ((_tokenSet_6.member(LA(1)))) {
			{
			match(_tokenSet_6);
			}
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}

		}
		match('\'');
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	protected final void mESC(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ESC;
		int _saveIndex;
		int ifWhitespace = text.length();

		match('\\');
		{
		switch ( LA(1)) {
		case 'n':
		{
			match('n');
			break;
		}
		case 'r':
		{
			match('r');
			break;
		}
		case 't':
		{
			match('t');
			break;
		}
		case 'b':
		{
			match('b');
			break;
		}
		case 'f':
		{
			match('f');
			break;
		}
		case '"':
		{
			match('"');
			break;
		}
		case '?':
		{
			match('?');
			break;
		}
		case '\'':
		{
			match('\'');
			break;
		}
		case '\\':
		{
			match('\\');
			break;
		}
		case 'u':
		{
			{
			int _cnt116=0;
			_loop116:
			do {
				if (('u' == LA(1))) {
					match('u');
				}
				else {
					if (1 <= _cnt116) { break _loop116; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}

				_cnt116++;
			} while (true);
			}
			mHEX_DIGIT(false);
			mHEX_DIGIT(false);
			mHEX_DIGIT(false);
			mHEX_DIGIT(false);
			break;
		}
		case '0':  case '1':  case '2':  case '3':
		{
			matchRange('0','3');
			{
			if ((('0' <= LA(1) && '7' >= LA(1))) && ((
                          '\u0003' <= LA(2) && '\u00ff' >= LA(2))) && (true)) {
				matchRange('0','7');
				{
				if ((('0' <= LA(1) && '7' >= LA(1))) && ((
                                  '\u0003' <= LA(2) && '\u00ff' >= LA(2))) && (true)) {
					matchRange('0','7');
				}
				else if ((('\u0003' <= LA(1) &&
                                  '\u00ff' >= LA(1))) && (true) && (true)) {
				}
				else {
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}

				}
			}
			else if ((('\u0003' <= LA(1) && '\u00ff' >= LA(1))) && (true) && (true)) {
			}
			else {
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}

			}
			break;
		}
		case '4':  case '5':  case '6':  case '7':
		{
			matchRange('4','7');
			{
			if ((('0' <= LA(1) && '7' >= LA(1))) && ((
                          '\u0003' <= LA(2) && '\u00ff' >= LA(2))) && (true)) {
				matchRange('0','7');
			}
			else if ((('\u0003' <= LA(1) && '\u00ff' >= LA(1))) && (true) && (true)) {
			}
			else {
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}

			}
			break;
		}
		case '\t':  case '\n':  case '\u000c':  case '\r':
		case ' ':
		{
			{
			_loop121:
			do {
				switch ( LA(1)) {
				case ' ':
				{
					match(' ');
					break;
				}
				case '\t':
				{
					match('\t');
					break;
				}
				case '\u000c':
				{
					match('\f');
					break;
				}
				default:
				{
					break _loop121;
				}
				}
			} while (true);
			}
			mEOL(false);
			{
			text.setLength(ifWhitespace);
			}
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mSTRING(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = STRING;
		int _saveIndex;

		match('"');
		{
		_loop112:
		do {
			switch ( LA(1)) {
			case '\\':
			{
				mESC(false);
				break;
			}
			case '\n':  case '\r':
			{
				mEOL(false);
				break;
			}
			default:
				if ((_tokenSet_7.member(LA(1)))) {
					{
					match(_tokenSet_7);
					}
				}
			else {
				break _loop112;
			}
			}
		} while (true);
		}
		match('"');
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	protected final void mHEX_DIGIT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = HEX_DIGIT;
		int _saveIndex;

		{
		switch ( LA(1)) {
		case '0':  case '1':  case '2':  case '3':
		case '4':  case '5':  case '6':  case '7':
		case '8':  case '9':
		{
			matchRange('0','9');
			break;
		}
		case 'A':  case 'B':  case 'C':  case 'D':
		case 'E':  case 'F':
		{
			matchRange('A','F');
			break;
		}
		case 'a':  case 'b':  case 'c':  case 'd':
		case 'e':  case 'f':
		{
			matchRange('a','f');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	public final void mINT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = INT;
		int _saveIndex;

		boolean synPredMatched140 = false;
		if (((('0' <= LA(1) && '9' >= LA(1))) && ('.' == LA(2) ||
                  '0' == LA(2) || '1' == LA(2) || '2' == LA(2) || '3' == LA(2) ||
                  '4' == LA(2) || '5' == LA(2) || '6' == LA(2) || '7' == LA(2) ||
                  '8' == LA(2) || '9' == LA(2) || 'E' == LA(2) || '_' == LA(2) ||
                  'e' == LA(2)) && ('+' == LA(3) || '-' == LA(3) ||
                  '.' == LA(3) || '0' == LA(3) || '1' == LA(3) || '2' == LA(3) ||
                  '3' == LA(3) || '4' == LA(3) || '5' == LA(3) || '6' == LA(3) ||
                  '7' == LA(3) || '8' == LA(3) || '9' == LA(3) || 'E' == LA(3) ||
                  '_' == LA(3) || 'e' == LA(3)))) {
			int _m140 = mark();
			synPredMatched140 = true;
			inputState.guessing++;
			try {
				{
				mFLOAT64(false);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched140 = false;
			}
			rewind(_m140);
			inputState.guessing--;
		}
		if ( synPredMatched140 ) {
			mFLOAT64(false);
			if (0 == inputState.guessing) {
				_ttype = FLOAT64;
			}
		}
		else {
			boolean synPredMatched131 = false;
			if ((('0' == LA(1)) && ('x' == LA(2)))) {
				int _m131 = mark();
				synPredMatched131 = true;
				inputState.guessing++;
				try {
					{
					match("0x");
					}
				}
				catch (RecognitionException pe) {
					synPredMatched131 = false;
				}
				rewind(_m131);
				inputState.guessing--;
			}
			if ( synPredMatched131 ) {
				match("0x");
				{
				int _cnt133=0;
				_loop133:
				do {
					if ((_tokenSet_8.member(LA(1)))) {
						mHEX_DIGIT(false);
					}
					else {
						if (1 <= _cnt133) { break _loop133; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
					}

					_cnt133++;
				} while (true);
				}
				if (0 == inputState.guessing) {
					_ttype = HEX;
				}
			}
			else {
				boolean synPredMatched136 = false;
				if (((('0' <= LA(1) && '7' >= LA(1))) && (true) && (true))) {
					int _m136 = mark();
					synPredMatched136 = true;
					inputState.guessing++;
					try {
						{
						match('0');
						{
						matchRange('0','9');
						}
						}
					}
					catch (RecognitionException pe) {
						synPredMatched136 = false;
					}
					rewind(_m136);
					inputState.guessing--;
				}
				if ( synPredMatched136 ) {
					{
					int _cnt138=0;
					_loop138:
					do {
						if ((('0' <= LA(1) &&
                                                  '7' >= LA(1)))) {
							matchRange('0','7');
						}
						else {
							if (1 <= _cnt138) { break _loop138; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
						}

						_cnt138++;
					} while (true);
					}
					if (0 == inputState.guessing) {
						_ttype = OCTAL;
					}
				}
				else if ((('0' <= LA(1) && '9' >= LA(1))) && (true) && (true)) {
					mPOSINT(false);
				}
				else {
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
				}}
				if ( _createToken && _token==null &&
                                  Token.SKIP != _ttype) {
					_token = makeToken(_ttype);
					_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
				}
				_returnToken = _token;
			}

	protected final void mFLOAT64(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = FLOAT64;
		int _saveIndex;

		mPOSINT(false);
		{
		switch ( LA(1)) {
		case '.':
		{
			match('.');
			mPOSINT(false);
			break;
		}
		case 'E':  case 'e':
		{
			{
			switch ( LA(1)) {
			case 'e':
			{
				match('e');
				break;
			}
			case 'E':
			{
				match('E');
				break;
			}
			default:
			{
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			}
			mEXPONENT(false);
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	protected final void mPOSINT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = POSINT;
		int _saveIndex;

		{
		matchRange('0','9');
		}
		{
		_loop144:
		do {
			switch ( LA(1)) {
			case '0':  case '1':  case '2':  case '3':
			case '4':  case '5':  case '6':  case '7':
			case '8':  case '9':
			{
				matchRange('0','9');
				break;
			}
			case '_':
			{
				_saveIndex=text.length();
				match('_');
				text.setLength(_saveIndex);
				break;
			}
			default:
			{
				break _loop144;
			}
			}
		} while (true);
		}
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}

	protected final void mEXPONENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = EXPONENT;
		int _saveIndex;

		{
		switch ( LA(1)) {
		case '+':
		{
			match('+');
			break;
		}
		case '-':
		{
			match('-');
			break;
		}
		case '0':  case '1':  case '2':  case '3':
		case '4':  case '5':  case '6':  case '7':
		case '8':  case '9':
		{
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		mPOSINT(false);
		if ( _createToken && _token==null && Token.SKIP != _ttype) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}


	private static long[] mk_tokenSet_0() {
		long[] data = new long[8];
		data[0]=-34359747592L;
		for (int i = 1; 3 >= i; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static long[] mk_tokenSet_1() {
		long[] data = new long[8];
		data[0]=-9224L;
		for (int i = 1; 3 >= i; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static long[] mk_tokenSet_2() {
		long[] data = { 0L, 576460745995190270L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static long[] mk_tokenSet_3() {
		long[] data = { -5764607548804038656L, 6341068269297860607L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static long[] mk_tokenSet_4() {
		long[] data = { -1152921530376650752L, 6341068269297860607L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static long[] mk_tokenSet_5() {
		long[] data = new long[8];
		data[0]=-4398046520328L;
		for (int i = 1; 3 >= i; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static long[] mk_tokenSet_6() {
		long[] data = new long[8];
		data[0]=-549755823112L;
		data[1]=-268435457L;
		for (int i = 2; 3 >= i; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static long[] mk_tokenSet_7() {
		long[] data = new long[8];
		data[0]=-17179878408L;
		data[1]=-268435457L;
		for (int i = 2; 3 >= i; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static long[] mk_tokenSet_8() {
		long[] data = { 287948901175001088L, 541165879422L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());

	}
