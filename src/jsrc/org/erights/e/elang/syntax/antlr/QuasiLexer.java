// $ANTLR 2.7.5rc2 (2005-01-08): "quasi.g" -> "QuasiLexer.java"$

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

public class QuasiLexer extends antlr.SwitchingLexer implements QuasiLexerTokenTypes {
public QuasiLexer(InputStream in) {
	this(new ByteBuffer(in));
}
public QuasiLexer(Reader in) {
	this(new CharBuffer(in));
}
public QuasiLexer(InputBuffer ib) {
	this(new LexerSharedInputState(ib));
}
public QuasiLexer(LexerSharedInputState state) {
	super(state);
	caseSensitiveLiterals = false;
	setCaseSensitive(true);
	literals = new Hashtable();
	literals.put(new ANTLRHashString("}", this), new Integer(8));
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
				if ((('\u0003' <= LA(1) && '\u00ff' >= LA(1)))) {
					mQUASIBODY(true);
					theRetToken=_returnToken;
				}
				else {
					if (EOF_CHAR == LA(1)) {uponEOF(); _returnToken = makeToken(Token.EOF_TYPE);}
				else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}

				if ( _returnToken==null ) continue tryAgain; // found SKIP token
				_ttype = _returnToken.getType();
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

	public final void mQUASIBODY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = QUASIBODY;
		int _saveIndex;

		if (('$' == LA(1)) && ('{' == LA(2))) {
			match("${");
			if (0 == inputState.guessing) {
				_ttype = DOLLARCURLY; selector.push("e");
			}
		}
		else if (('$' == LA(1)) && (_tokenSet_0.member(LA(2)))) {
			_saveIndex=text.length();
			match('$');
			text.setLength(_saveIndex);
			mIDENT(false);
			if (0 == inputState.guessing) {
				_ttype = DOLLARHOLE;
			}
		}
		else if (('@' == LA(1)) && ('{' == LA(2))) {
			match("@{");
			if (0 == inputState.guessing) {
				_ttype = ATCURLY; selector.push("e");
			}
		}
		else if (('@' == LA(1)) && (_tokenSet_0.member(LA(2)))) {
			_saveIndex=text.length();
			match('@');
			text.setLength(_saveIndex);
			mIDENT(false);
			if (0 == inputState.guessing) {
				_ttype = ATHOLE;
			}
		}
		else if (('$' == LA(1)) && ('$' == LA(2))) {
			match("$$");
			mQUASIn(false);
		}
		else if (('$' == LA(1)) && ('\\' == LA(2))) {
			match("$\\");
			mQUASIn(false);
		}
		else if (('@' == LA(1)) && ('@' == LA(2))) {
			match("@@");
			mQUASIn(false);
		}
		else if (('@' == LA(1)) && ('\\' == LA(2))) {
			match("@\\");
			mQUASIn(false);
		}
		else {
			boolean synPredMatched3 = false;
			if ((('`' == LA(1)) && ('`' == LA(2)))) {
				int _m3 = mark();
				synPredMatched3 = true;
				inputState.guessing++;
				try {
					{
					match("``");
					}
				}
				catch (RecognitionException pe) {
					synPredMatched3 = false;
				}
				rewind(_m3);
				inputState.guessing--;
			}
			if ( synPredMatched3 ) {
				match("``");
				mQUASIn(false);
			}
			else if ((_tokenSet_1.member(LA(1)))) {
				mQUASI1(false);
				mQUASIn(false);
			}
			else if (('`' == LA(1)) && (true)) {
				match('`');
				if (0 == inputState.guessing) {
					_ttype = QUASICLOSE;
				}
				if (0 == inputState.guessing) {
					selector.pop();
				}
			}
			else {
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			if ( _createToken && _token==null &&
                          Token.SKIP != _ttype) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		}

	protected final void mIDENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
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
		_loop15:
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
				break _loop15;
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

	protected final void mQUASIn(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = QUASIn;
		int _saveIndex;

		{
		_loop8:
		do {
			if (('$' == LA(1)) && ('$' == LA(2))) {
				match("$$");
			}
			else if (('$' == LA(1)) && ('\\' == LA(2))) {
				match("$\\");
			}
			else if (('@' == LA(1)) && ('@' == LA(2))) {
				match("@@");
			}
			else if (('@' == LA(1)) && ('\\' == LA(2))) {
				match("@\\");
			}
			else if ((_tokenSet_1.member(LA(1)))) {
				mQUASI1(false);
			}
			else {
				boolean synPredMatched7 = false;
				if ((('`' == LA(1)))) {
					int _m7 = mark();
					synPredMatched7 = true;
					inputState.guessing++;
					try {
						{
						match("``");
						}
					}
					catch (RecognitionException pe) {
						synPredMatched7 = false;
					}
					rewind(_m7);
					inputState.guessing--;
				}
				if ( synPredMatched7 ) {
					match("``");
				}
				else {
					break _loop8;
				}
				}
			} while (true);
			}
			if ( _createToken && _token==null &&
                          Token.SKIP != _ttype) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		}

	protected final void mQUASI1(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = QUASI1;
		int _saveIndex;

		if ((_tokenSet_2.member(LA(1)))) {
			{
			match(_tokenSet_2);
			}
		}
		else if (('\n' == LA(1) || '\r' == LA(1))) {
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
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}

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
		case '\'':
		{
			match('\'');
			break;
		}
		case '@':
		{
			match('@');
			break;
		}
		case '$':
		{
			match('$');
			break;
		}
		case '`':
		{
			match('`');
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
			int _cnt19=0;
			_loop19:
			do {
				if (('u' == LA(1))) {
					match('u');
				}
				else {
					if (1 <= _cnt19) { break _loop19; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}

				_cnt19++;
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
			if ((('0' <= LA(1) && '7' >= LA(1)))) {
				matchRange('0','7');
				{
				if ((('0' <= LA(1) && '7' >= LA(1)))) {
					matchRange('0','7');
				}
				else {
				}

				}
			}
			else {
			}

			}
			break;
		}
		case '4':  case '5':  case '6':  case '7':
		{
			matchRange('4','7');
			{
			if ((('0' <= LA(1) && '7' >= LA(1)))) {
				matchRange('0','7');
			}
			else {
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


	private static long[] mk_tokenSet_0() {
		long[] data = { 0L, 576460745995190270L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static long[] mk_tokenSet_1() {
		long[] data = new long[8];
		data[0]=-68719476744L;
		data[1]=-4294967298L;
		for (int i = 2; 3 >= i; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static long[] mk_tokenSet_2() {
		long[] data = new long[8];
		data[0]=-68719485960L;
		data[1]=-4294967298L;
		for (int i = 2; 3 >= i; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());

	}
