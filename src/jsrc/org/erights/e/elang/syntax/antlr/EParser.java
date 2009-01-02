// $ANTLR 2.7.5rc2 (2005-01-08): "e.g" -> "EParser.java"$

package org.erights.e.elang.syntax.antlr;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.collections.AST;
import antlr.ASTFactory;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;

public class EParser extends antlr.LLkParser       implements ETokenTypes
 {

private void warn(String msg) {
    reportWarning(msg);
}

public void reportWarning(String s) {
    try {
        int line = LT(0).getLine();
        int col = LT(0).getColumn();
        if (getFilename() == null) {
            System.err.println("@" + line + "warning: " + s);
        }
        else {
            System.err.println(getFilename() + "@" + line + ": warning: " + s);
        }
    } catch (TokenStreamException t) {
        System.err.println("warning: " + s);
    }
}

// pocket mechanisms: add a boolean, and test in the grammar with {foo}?
private boolean pocketNounString = false;
//private boolean pocketDotProps = false;

protected EParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public EParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected EParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public EParser(TokenStream lexer) {
  this(lexer,2);
}

public EParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final void start() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST start_AST = null;

		try {      // for error handling
			{
			_loop4:
			do {
				switch ( LA(1)) {
				case LITERAL_pragma:
				{
					pragma();
					astFactory.addASTChild(currentAST, returnAST);
					{
					switch ( LA(1)) {
					case 86:
					{
						match(86);
						break;
					}
					case LINESEP:
					{
						match(LINESEP);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					break;
				}
				case LINESEP:
				{
					match(LINESEP);
					break;
				}
				default:
				{
					break _loop4;
				}
				}
			} while (true);
			}
			{
			switch ( LA(1)) {
			case QUASIOPEN:
			case URI:
			case URIGetter:
			case HEX:
			case OCTAL:
			case DOC_COMMENT:
			case CHAR_LITERAL:
			case STRING:
			case IDENT:
			case INT:
			case FLOAT64:
			case LITERAL_meta:
			case LITERAL_if:
			case LITERAL_for:
			case LITERAL_accum:
			case LITERAL_while:
			case 97:
			case 99:
			case 100:
			case 101:
			case LITERAL_when:
			case LITERAL_escape:
			case LITERAL_thunk:
			case LITERAL_fn:
			case LITERAL_switch:
			case LITERAL_try:
			case LITERAL_bind:
			case LITERAL_var:
			case LITERAL_def:
			case 116:
			case LITERAL_interface:
			case LITERAL_break:
			case LITERAL_continue:
			case LITERAL_return:
			case 145:
			case 162:
			case 168:
			case 169:
			case 170:
			case 173:
			case 176:
			case 177:
			case 178:
			case 179:
			case 180:
			{
				seq();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			start_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = start_AST;
	}

	public final void pragma() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST pragma_AST = null;

		try {      // for error handling
			AST tmp4_AST = null;
			tmp4_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp4_AST);
			match(LITERAL_pragma);
			AST tmp5_AST = null;
			tmp5_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp5_AST);
			match(88);
			message();
			astFactory.addASTChild(currentAST, returnAST);
			pragma_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_1);
			} else {
			  throw ex;
			}
		}
		returnAST = pragma_AST;
	}

	public final void seq() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST seq_AST = null;

		try {      // for error handling
			eExpr();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case LINESEP:
			case 86:
			{
				{
				int _cnt16=0;
				_loop16:
				do {
					if ((LA(1)==LINESEP||LA(1)==86)) {
						{
						switch ( LA(1)) {
						case 86:
						{
							match(86);
							break;
						}
						case LINESEP:
						{
							match(LINESEP);
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						{
						switch ( LA(1)) {
						case QUASIOPEN:
						case URI:
						case URIGetter:
						case HEX:
						case OCTAL:
						case DOC_COMMENT:
						case CHAR_LITERAL:
						case STRING:
						case IDENT:
						case INT:
						case FLOAT64:
						case LITERAL_meta:
						case LITERAL_if:
						case LITERAL_for:
						case LITERAL_accum:
						case LITERAL_while:
						case 97:
						case 99:
						case 100:
						case 101:
						case LITERAL_when:
						case LITERAL_escape:
						case LITERAL_thunk:
						case LITERAL_fn:
						case LITERAL_switch:
						case LITERAL_try:
						case LITERAL_bind:
						case LITERAL_var:
						case LITERAL_def:
						case 116:
						case LITERAL_interface:
						case LITERAL_break:
						case LITERAL_continue:
						case LITERAL_return:
						case 145:
						case 162:
						case 168:
						case 169:
						case 170:
						case 173:
						case 176:
						case 177:
						case 178:
						case 179:
						case 180:
						{
							eExpr();
							astFactory.addASTChild(currentAST, returnAST);
							break;
						}
						case EOF:
						case RCURLY:
						case LINESEP:
						case 86:
						case 118:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
					}
					else {
						if ( _cnt16>=1 ) { break _loop16; } else {throw new NoViableAltException(LT(1), getFilename());}
					}

					_cnt16++;
				} while (true);
				}
				if ( inputState.guessing==0 ) {
					seq_AST = (AST)currentAST.root;
					seq_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(SeqExpr)).add(seq_AST));
					currentAST.root = seq_AST;
					currentAST.child = seq_AST!=null &&seq_AST.getFirstChild()!=null ?
						seq_AST.getFirstChild() : seq_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case EOF:
			case RCURLY:
			case 118:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			seq_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_2);
			} else {
			  throw ex;
			}
		}
		returnAST = seq_AST;
	}

	public final void message() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST message_AST = null;

		try {      // for error handling
			verb();
			astFactory.addASTChild(currentAST, returnAST);
			{
			boolean synPredMatched190 = false;
			if (((LA(1)==116) && (_tokenSet_3.member(LA(2))))) {
				int _m190 = mark();
				synPredMatched190 = true;
				inputState.guessing++;
				try {
					{
					match(116);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched190 = false;
				}
				rewind(_m190);
				inputState.guessing--;
			}
			if ( synPredMatched190 ) {
				parenArgs();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((_tokenSet_4.member(LA(1))) && (_tokenSet_5.member(LA(2)))) {
				if ( inputState.guessing==0 ) {
					message_AST = (AST)currentAST.root;
					message_AST.setType(CurryExpr);
				}
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}

			}
			message_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		returnAST = message_AST;
	}

	public final void metaExpr() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST metaExpr_AST = null;

		try {      // for error handling
			AST tmp8_AST = null;
			tmp8_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp8_AST);
			match(LITERAL_meta);
			AST tmp9_AST = null;
			tmp9_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp9_AST);
			match(88);
			message();
			astFactory.addASTChild(currentAST, returnAST);
			metaExpr_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		returnAST = metaExpr_AST;
	}

	public final void br() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST br_AST = null;

		try {      // for error handling
			{
			_loop10:
			do {
				if ((LA(1)==LINESEP)) {
					match(LINESEP);
				}
				else {
					break _loop10;
				}

			} while (true);
			}
			br_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_6);
			} else {
			  throw ex;
			}
		}
		returnAST = br_AST;
	}

	public final void eExpr() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST eExpr_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case QUASIOPEN:
			case URI:
			case URIGetter:
			case HEX:
			case OCTAL:
			case DOC_COMMENT:
			case CHAR_LITERAL:
			case STRING:
			case IDENT:
			case INT:
			case FLOAT64:
			case LITERAL_meta:
			case LITERAL_if:
			case LITERAL_for:
			case LITERAL_accum:
			case LITERAL_while:
			case 97:
			case 99:
			case 100:
			case 101:
			case LITERAL_when:
			case LITERAL_escape:
			case LITERAL_thunk:
			case LITERAL_fn:
			case LITERAL_switch:
			case LITERAL_try:
			case LITERAL_bind:
			case LITERAL_var:
			case LITERAL_def:
			case 116:
			case LITERAL_interface:
			case 162:
			case 168:
			case 169:
			case 170:
			case 173:
			case 176:
			case 177:
			case 178:
			case 179:
			case 180:
			{
				assign();
				astFactory.addASTChild(currentAST, returnAST);
				eExpr_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_break:
			case LITERAL_continue:
			case LITERAL_return:
			case 145:
			{
				ejector();
				astFactory.addASTChild(currentAST, returnAST);
				eExpr_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_7);
			} else {
			  throw ex;
			}
		}
		returnAST = eExpr_AST;
	}

	public final void assign() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST assign_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case QUASIOPEN:
			case URI:
			case URIGetter:
			case HEX:
			case OCTAL:
			case CHAR_LITERAL:
			case STRING:
			case IDENT:
			case INT:
			case FLOAT64:
			case LITERAL_meta:
			case LITERAL_if:
			case LITERAL_for:
			case LITERAL_accum:
			case LITERAL_while:
			case 97:
			case 99:
			case 100:
			case 101:
			case LITERAL_when:
			case LITERAL_escape:
			case LITERAL_switch:
			case LITERAL_try:
			case 116:
			case 162:
			case 168:
			case 169:
			case 170:
			case 173:
			case 176:
			case 177:
			case 178:
			case 179:
			case 180:
			{
				cond();
				astFactory.addASTChild(currentAST, returnAST);
				{
				switch ( LA(1)) {
				case 115:
				{
					AST tmp11_AST = null;
					tmp11_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp11_AST);
					match(115);
					assign();
					astFactory.addASTChild(currentAST, returnAST);
					if ( inputState.guessing==0 ) {
						assign_AST = (AST)currentAST.root;
						assign_AST.setType(AssignExpr);
					}
					break;
				}
				case 129:
				case 130:
				case 131:
				case 132:
				case 133:
				case 134:
				case 135:
				case 136:
				case 137:
				case 138:
				case 139:
				case 140:
				case 141:
				{
					assignOp();
					astFactory.addASTChild(currentAST, returnAST);
					assign();
					astFactory.addASTChild(currentAST, returnAST);
					if ( inputState.guessing==0 ) {
						assign_AST = (AST)currentAST.root;
						assign_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AssignExpr)).add(assign_AST));
						currentAST.root = assign_AST;
						currentAST.child = assign_AST!=null &&assign_AST.getFirstChild()!=null ?
							assign_AST.getFirstChild() : assign_AST;
						currentAST.advanceChildToEnd();
					}
					break;
				}
				case STRING:
				case IDENT:
				{
					verb();
					astFactory.addASTChild(currentAST, returnAST);
					match(128);
					{
					boolean synPredMatched135 = false;
					if (((LA(1)==116) && (_tokenSet_3.member(LA(2))))) {
						int _m135 = mark();
						synPredMatched135 = true;
						inputState.guessing++;
						try {
							{
							match(116);
							}
						}
						catch (RecognitionException pe) {
							synPredMatched135 = false;
						}
						rewind(_m135);
						inputState.guessing--;
					}
					if ( synPredMatched135 ) {
						parenArgs();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else if ((_tokenSet_8.member(LA(1))) && (_tokenSet_9.member(LA(2)))) {
						assign();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						throw new NoViableAltException(LT(1), getFilename());
					}

					}
					if ( inputState.guessing==0 ) {
						assign_AST = (AST)currentAST.root;
						assign_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(AssignExpr)).add(assign_AST));
						currentAST.root = assign_AST;
						currentAST.child = assign_AST!=null &&assign_AST.getFirstChild()!=null ?
							assign_AST.getFirstChild() : assign_AST;
						currentAST.advanceChildToEnd();
					}
					break;
				}
				case EOF:
				case RCURLY:
				case LINESEP:
				case 86:
				case 94:
				case 97:
				case 117:
				case 118:
				case 171:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				assign_AST = (AST)currentAST.root;
				break;
			}
			case DOC_COMMENT:
			case LITERAL_thunk:
			case LITERAL_fn:
			case LITERAL_bind:
			case LITERAL_var:
			case LITERAL_def:
			case LITERAL_interface:
			{
				docoDef();
				astFactory.addASTChild(currentAST, returnAST);
				assign_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_10);
			} else {
			  throw ex;
			}
		}
		returnAST = assign_AST;
	}

	public final void ejector() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ejector_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case LITERAL_break:
			case LITERAL_continue:
			case LITERAL_return:
			{
				{
				switch ( LA(1)) {
				case LITERAL_break:
				{
					AST tmp13_AST = null;
					tmp13_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp13_AST);
					match(LITERAL_break);
					if ( inputState.guessing==0 ) {
						ejector_AST = (AST)currentAST.root;
						ejector_AST.setType(BreakExpr);
					}
					break;
				}
				case LITERAL_continue:
				{
					AST tmp14_AST = null;
					tmp14_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp14_AST);
					match(LITERAL_continue);
					if ( inputState.guessing==0 ) {
						ejector_AST = (AST)currentAST.root;
						ejector_AST.setType(ContinueExpr);
					}
					break;
				}
				case LITERAL_return:
				{
					AST tmp15_AST = null;
					tmp15_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp15_AST);
					match(LITERAL_return);
					if ( inputState.guessing==0 ) {
						ejector_AST = (AST)currentAST.root;
						ejector_AST.setType(ReturnExpr);
					}
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				{
				boolean synPredMatched141 = false;
				if (((LA(1)==116) && (LA(2)==118))) {
					int _m141 = mark();
					synPredMatched141 = true;
					inputState.guessing++;
					try {
						{
						match(116);
						match(118);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched141 = false;
					}
					rewind(_m141);
					inputState.guessing--;
				}
				if ( synPredMatched141 ) {
					AST tmp16_AST = null;
					tmp16_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp16_AST);
					match(116);
					AST tmp17_AST = null;
					tmp17_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp17_AST);
					match(118);
				}
				else if ((_tokenSet_8.member(LA(1))) && (_tokenSet_9.member(LA(2)))) {
					assign();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else if ((_tokenSet_7.member(LA(1)))) {
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}

				}
				ejector_AST = (AST)currentAST.root;
				break;
			}
			case 145:
			{
				AST tmp18_AST = null;
				tmp18_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp18_AST);
				match(145);
				assign();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					ejector_AST = (AST)currentAST.root;
					ejector_AST.setType(ReturnExpr);
				}
				ejector_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_7);
			} else {
			  throw ex;
			}
		}
		returnAST = ejector_AST;
	}

	public final void basic() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST basic_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case LITERAL_if:
			{
				ifExpr();
				astFactory.addASTChild(currentAST, returnAST);
				basic_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_for:
			{
				forExpr();
				astFactory.addASTChild(currentAST, returnAST);
				basic_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_while:
			{
				whileExpr();
				astFactory.addASTChild(currentAST, returnAST);
				basic_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_switch:
			{
				switchExpr();
				astFactory.addASTChild(currentAST, returnAST);
				basic_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_try:
			{
				tryExpr();
				astFactory.addASTChild(currentAST, returnAST);
				basic_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_escape:
			{
				escapeExpr();
				astFactory.addASTChild(currentAST, returnAST);
				basic_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_when:
			{
				whenExpr();
				astFactory.addASTChild(currentAST, returnAST);
				basic_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_meta:
			{
				metaExpr();
				astFactory.addASTChild(currentAST, returnAST);
				basic_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_accum:
			{
				accumExpr();
				astFactory.addASTChild(currentAST, returnAST);
				basic_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		returnAST = basic_AST;
	}

	public final void ifExpr() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ifExpr_AST = null;

		try {      // for error handling
			AST tmp19_AST = null;
			tmp19_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp19_AST);
			match(LITERAL_if);
			parenExpr();
			astFactory.addASTChild(currentAST, returnAST);
			body();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case LITERAL_else:
			{
				match(LITERAL_else);
				{
				switch ( LA(1)) {
				case LITERAL_if:
				{
					ifExpr();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case 97:
				{
					body();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				break;
			}
			case EOF:
			case RCURLY:
			case LINESEP:
			case STRING:
			case IDENT:
			case 86:
			case 88:
			case LITERAL_if:
			case LITERAL_for:
			case LITERAL_in:
			case 94:
			case LITERAL_while:
			case 97:
			case 99:
			case 100:
			case 101:
			case 102:
			case 106:
			case 115:
			case 116:
			case 117:
			case 118:
			case LITERAL_extends:
			case LITERAL_implements:
			case 129:
			case 130:
			case 131:
			case 132:
			case 133:
			case 134:
			case 135:
			case 136:
			case 137:
			case 138:
			case 139:
			case 140:
			case 141:
			case 145:
			case 146:
			case 147:
			case 148:
			case 149:
			case 150:
			case 151:
			case 152:
			case 153:
			case 154:
			case 155:
			case 156:
			case 157:
			case 158:
			case 159:
			case 160:
			case 161:
			case 162:
			case 163:
			case 164:
			case 165:
			case 166:
			case 167:
			case 170:
			case 171:
			case 172:
			case 173:
			case 175:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				ifExpr_AST = (AST)currentAST.root;
				ifExpr_AST.setType(IfExpr);
			}
			ifExpr_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		returnAST = ifExpr_AST;
	}

	public final void forExpr() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST forExpr_AST = null;

		try {      // for error handling
			AST tmp21_AST = null;
			tmp21_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp21_AST);
			match(LITERAL_for);
			forPatt();
			astFactory.addASTChild(currentAST, returnAST);
			match(LITERAL_in);
			br();
			astFactory.addASTChild(currentAST, returnAST);
			assign();
			astFactory.addASTChild(currentAST, returnAST);
			body();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case LITERAL_catch:
			{
				catcher();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case RCURLY:
			case LINESEP:
			case STRING:
			case IDENT:
			case 86:
			case 88:
			case LITERAL_if:
			case LITERAL_for:
			case LITERAL_in:
			case 94:
			case LITERAL_while:
			case 97:
			case 99:
			case 100:
			case 101:
			case 102:
			case 106:
			case 115:
			case 116:
			case 117:
			case 118:
			case LITERAL_extends:
			case LITERAL_implements:
			case 129:
			case 130:
			case 131:
			case 132:
			case 133:
			case 134:
			case 135:
			case 136:
			case 137:
			case 138:
			case 139:
			case 140:
			case 141:
			case 145:
			case 146:
			case 147:
			case 148:
			case 149:
			case 150:
			case 151:
			case 152:
			case 153:
			case 154:
			case 155:
			case 156:
			case 157:
			case 158:
			case 159:
			case 160:
			case 161:
			case 162:
			case 163:
			case 164:
			case 165:
			case 166:
			case 167:
			case 170:
			case 171:
			case 172:
			case 173:
			case 175:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				forExpr_AST = (AST)currentAST.root;
				forExpr_AST.setType(ForExpr);
			}
			forExpr_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		returnAST = forExpr_AST;
	}

	public final void whileExpr() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST whileExpr_AST = null;

		try {      // for error handling
			AST tmp23_AST = null;
			tmp23_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp23_AST);
			match(LITERAL_while);
			parenExpr();
			astFactory.addASTChild(currentAST, returnAST);
			body();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case LITERAL_catch:
			{
				catcher();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case RCURLY:
			case LINESEP:
			case STRING:
			case IDENT:
			case 86:
			case 88:
			case LITERAL_if:
			case LITERAL_for:
			case LITERAL_in:
			case 94:
			case LITERAL_while:
			case 97:
			case 99:
			case 100:
			case 101:
			case 102:
			case 106:
			case 115:
			case 116:
			case 117:
			case 118:
			case LITERAL_extends:
			case LITERAL_implements:
			case 129:
			case 130:
			case 131:
			case 132:
			case 133:
			case 134:
			case 135:
			case 136:
			case 137:
			case 138:
			case 139:
			case 140:
			case 141:
			case 145:
			case 146:
			case 147:
			case 148:
			case 149:
			case 150:
			case 151:
			case 152:
			case 153:
			case 154:
			case 155:
			case 156:
			case 157:
			case 158:
			case 159:
			case 160:
			case 161:
			case 162:
			case 163:
			case 164:
			case 165:
			case 166:
			case 167:
			case 170:
			case 171:
			case 172:
			case 173:
			case 175:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				whileExpr_AST = (AST)currentAST.root;
				whileExpr_AST.setType(WhileExpr);
			}
			whileExpr_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		returnAST = whileExpr_AST;
	}

	public final void switchExpr() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST switchExpr_AST = null;

		try {      // for error handling
			AST tmp24_AST = null;
			tmp24_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp24_AST);
			match(LITERAL_switch);
			parenExpr();
			astFactory.addASTChild(currentAST, returnAST);
			match(97);
			{
			_loop45:
			do {
				if ((LA(1)==LITERAL_match)) {
					matcher();
					astFactory.addASTChild(currentAST, returnAST);
					br();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop45;
				}

			} while (true);
			}
			match(RCURLY);
			if ( inputState.guessing==0 ) {
				switchExpr_AST = (AST)currentAST.root;
				switchExpr_AST.setType(SwitchExpr);
			}
			switchExpr_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		returnAST = switchExpr_AST;
	}

	public final void tryExpr() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST tryExpr_AST = null;

		try {      // for error handling
			AST tmp27_AST = null;
			tmp27_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp27_AST);
			match(LITERAL_try);
			body();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop48:
			do {
				if ((LA(1)==LITERAL_catch)) {
					catcher();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop48;
				}

			} while (true);
			}
			{
			switch ( LA(1)) {
			case LITERAL_finally:
			{
				match(LITERAL_finally);
				body();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case RCURLY:
			case LINESEP:
			case STRING:
			case IDENT:
			case 86:
			case 88:
			case LITERAL_if:
			case LITERAL_for:
			case LITERAL_in:
			case 94:
			case LITERAL_while:
			case 97:
			case 99:
			case 100:
			case 101:
			case 102:
			case 106:
			case 115:
			case 116:
			case 117:
			case 118:
			case LITERAL_extends:
			case LITERAL_implements:
			case 129:
			case 130:
			case 131:
			case 132:
			case 133:
			case 134:
			case 135:
			case 136:
			case 137:
			case 138:
			case 139:
			case 140:
			case 141:
			case 145:
			case 146:
			case 147:
			case 148:
			case 149:
			case 150:
			case 151:
			case 152:
			case 153:
			case 154:
			case 155:
			case 156:
			case 157:
			case 158:
			case 159:
			case 160:
			case 161:
			case 162:
			case 163:
			case 164:
			case 165:
			case 166:
			case 167:
			case 170:
			case 171:
			case 172:
			case 173:
			case 175:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				tryExpr_AST = (AST)currentAST.root;
				tryExpr_AST.setType(TryExpr);
			}
			tryExpr_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		returnAST = tryExpr_AST;
	}

	public final void escapeExpr() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST escapeExpr_AST = null;

		try {      // for error handling
			AST tmp29_AST = null;
			tmp29_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp29_AST);
			match(LITERAL_escape);
			pattern();
			astFactory.addASTChild(currentAST, returnAST);
			body();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case LITERAL_catch:
			{
				catcher();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case RCURLY:
			case LINESEP:
			case STRING:
			case IDENT:
			case 86:
			case 88:
			case LITERAL_if:
			case LITERAL_for:
			case LITERAL_in:
			case 94:
			case LITERAL_while:
			case 97:
			case 99:
			case 100:
			case 101:
			case 102:
			case 106:
			case 115:
			case 116:
			case 117:
			case 118:
			case LITERAL_extends:
			case LITERAL_implements:
			case 129:
			case 130:
			case 131:
			case 132:
			case 133:
			case 134:
			case 135:
			case 136:
			case 137:
			case 138:
			case 139:
			case 140:
			case 141:
			case 145:
			case 146:
			case 147:
			case 148:
			case 149:
			case 150:
			case 151:
			case 152:
			case 153:
			case 154:
			case 155:
			case 156:
			case 157:
			case 158:
			case 159:
			case 160:
			case 161:
			case 162:
			case 163:
			case 164:
			case 165:
			case 166:
			case 167:
			case 170:
			case 171:
			case 172:
			case 173:
			case 175:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				escapeExpr_AST = (AST)currentAST.root;
				escapeExpr_AST.setType(EscapeExpr);
			}
			escapeExpr_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		returnAST = escapeExpr_AST;
	}

	public final void whenExpr() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST whenExpr_AST = null;

		try {      // for error handling
			AST tmp30_AST = null;
			tmp30_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp30_AST);
			match(LITERAL_when);
			parenArgs();
			astFactory.addASTChild(currentAST, returnAST);
			match(104);
			whenFn();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop34:
			do {
				if ((LA(1)==LITERAL_catch)) {
					catcher();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop34;
				}

			} while (true);
			}
			{
			switch ( LA(1)) {
			case LITERAL_finally:
			{
				AST tmp32_AST = null;
				tmp32_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp32_AST);
				match(LITERAL_finally);
				body();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case RCURLY:
			case LINESEP:
			case STRING:
			case IDENT:
			case 86:
			case 88:
			case LITERAL_if:
			case LITERAL_for:
			case LITERAL_in:
			case 94:
			case LITERAL_while:
			case 97:
			case 99:
			case 100:
			case 101:
			case 102:
			case 106:
			case 115:
			case 116:
			case 117:
			case 118:
			case LITERAL_extends:
			case LITERAL_implements:
			case 129:
			case 130:
			case 131:
			case 132:
			case 133:
			case 134:
			case 135:
			case 136:
			case 137:
			case 138:
			case 139:
			case 140:
			case 141:
			case 145:
			case 146:
			case 147:
			case 148:
			case 149:
			case 150:
			case 151:
			case 152:
			case 153:
			case 154:
			case 155:
			case 156:
			case 157:
			case 158:
			case 159:
			case 160:
			case 161:
			case 162:
			case 163:
			case 164:
			case 165:
			case 166:
			case 167:
			case 170:
			case 171:
			case 172:
			case 173:
			case 175:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				whenExpr_AST = (AST)currentAST.root;
				whenExpr_AST.setType(WhenExpr);
			}
			whenExpr_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		returnAST = whenExpr_AST;
	}

	public final void accumExpr() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST accumExpr_AST = null;

		try {      // for error handling
			AST tmp33_AST = null;
			tmp33_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp33_AST);
			match(LITERAL_accum);
			call();
			astFactory.addASTChild(currentAST, returnAST);
			accumulator();
			astFactory.addASTChild(currentAST, returnAST);
			accumExpr_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		returnAST = accumExpr_AST;
	}

	public final void parenExpr() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST parenExpr_AST = null;

		try {      // for error handling
			match(116);
			seq();
			astFactory.addASTChild(currentAST, returnAST);
			match(118);
			parenExpr_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_11);
			} else {
			  throw ex;
			}
		}
		returnAST = parenExpr_AST;
	}

	public final void body() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST body_AST = null;

		try {      // for error handling
			match(97);
			{
			switch ( LA(1)) {
			case QUASIOPEN:
			case URI:
			case URIGetter:
			case HEX:
			case OCTAL:
			case DOC_COMMENT:
			case CHAR_LITERAL:
			case STRING:
			case IDENT:
			case INT:
			case FLOAT64:
			case LITERAL_meta:
			case LITERAL_if:
			case LITERAL_for:
			case LITERAL_accum:
			case LITERAL_while:
			case 97:
			case 99:
			case 100:
			case 101:
			case LITERAL_when:
			case LITERAL_escape:
			case LITERAL_thunk:
			case LITERAL_fn:
			case LITERAL_switch:
			case LITERAL_try:
			case LITERAL_bind:
			case LITERAL_var:
			case LITERAL_def:
			case 116:
			case LITERAL_interface:
			case LITERAL_break:
			case LITERAL_continue:
			case LITERAL_return:
			case 145:
			case 162:
			case 168:
			case 169:
			case 170:
			case 173:
			case 176:
			case 177:
			case 178:
			case 179:
			case 180:
			{
				seq();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case RCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(RCURLY);
			body_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_12);
			} else {
			  throw ex;
			}
		}
		returnAST = body_AST;
	}

	public final void forPatt() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST forPatt_AST = null;

		try {      // for error handling
			pattern();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case 94:
			{
				match(94);
				pattern();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					forPatt_AST = (AST)currentAST.root;
					forPatt_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ListPattern,"=>")).add(forPatt_AST));
					currentAST.root = forPatt_AST;
					currentAST.child = forPatt_AST!=null &&forPatt_AST.getFirstChild()!=null ?
						forPatt_AST.getFirstChild() : forPatt_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case LITERAL_in:
			{
				if ( inputState.guessing==0 ) {
					forPatt_AST = (AST)currentAST.root;
					forPatt_AST=(AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ListPattern,"=>")).add(astFactory.create(IgnorePattern,"")).add(forPatt_AST));
					currentAST.root = forPatt_AST;
					currentAST.child = forPatt_AST!=null &&forPatt_AST.getFirstChild()!=null ?
						forPatt_AST.getFirstChild() : forPatt_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			forPatt_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_13);
			} else {
			  throw ex;
			}
		}
		returnAST = forPatt_AST;
	}

	public final void catcher() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST catcher_AST = null;

		try {      // for error handling
			AST tmp39_AST = null;
			tmp39_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp39_AST);
			match(LITERAL_catch);
			pattern();
			astFactory.addASTChild(currentAST, returnAST);
			body();
			astFactory.addASTChild(currentAST, returnAST);
			catcher_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_14);
			} else {
			  throw ex;
			}
		}
		returnAST = catcher_AST;
	}

	public final void pattern() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST pattern_AST = null;

		try {      // for error handling
			subPattern();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case 175:
			{
				AST tmp40_AST = null;
				tmp40_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp40_AST);
				match(175);
				parenExpr();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					pattern_AST = (AST)currentAST.root;
					pattern_AST.setType(SuchThatPattern);
					warn("such-that deprecated");
				}
				break;
			}
			case EOF:
			case RCURLY:
			case LINESEP:
			case STRING:
			case IDENT:
			case 86:
			case LITERAL_in:
			case 94:
			case 97:
			case 115:
			case 117:
			case 118:
			case LITERAL_extends:
			case LITERAL_implements:
			case 129:
			case 130:
			case 131:
			case 132:
			case 133:
			case 134:
			case 135:
			case 136:
			case 137:
			case 138:
			case 139:
			case 140:
			case 141:
			case 146:
			case 147:
			case 171:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			pattern_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_15);
			} else {
			  throw ex;
			}
		}
		returnAST = pattern_AST;
	}

	public final void call() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST call_AST = null;
		AST p_AST = null;
		AST a_AST = null;
		AST l_AST = null;

		try {      // for error handling
			prim();
			p_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop186:
			do {
				switch ( LA(1)) {
				case 116:
				{
					parenArgs();
					a_AST = (AST)returnAST;
					if ( inputState.guessing==0 ) {
						call_AST = (AST)currentAST.root;
						call_AST=(AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(CallExpr,"run")).add(p_AST).add(astFactory.create(STRING,"run")).add(a_AST));
						currentAST.root = call_AST;
						currentAST.child = call_AST!=null &&call_AST.getFirstChild()!=null ?
							call_AST.getFirstChild() : call_AST;
						currentAST.advanceChildToEnd();
					}
					break;
				}
				case 88:
				{
					AST tmp41_AST = null;
					tmp41_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp41_AST);
					match(88);
					message();
					astFactory.addASTChild(currentAST, returnAST);
					if ( inputState.guessing==0 ) {
						call_AST = (AST)currentAST.root;
						call_AST.setType(CallExpr);
					}
					break;
				}
				case 170:
				{
					match(170);
					argList();
					l_AST = (AST)returnAST;
					match(171);
					if ( inputState.guessing==0 ) {
						call_AST = (AST)currentAST.root;
						call_AST=(AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(CallExpr,"get")).add(p_AST).add(astFactory.create(STRING,"get")).add(l_AST));
						currentAST.root = call_AST;
						currentAST.child = call_AST!=null &&call_AST.getFirstChild()!=null ?
							call_AST.getFirstChild() : call_AST;
						currentAST.advanceChildToEnd();
					}
					break;
				}
				case 172:
				{
					AST tmp44_AST = null;
					tmp44_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp44_AST);
					match(172);
					{
					switch ( LA(1)) {
					case 116:
					{
						parenArgs();
						astFactory.addASTChild(currentAST, returnAST);
						if ( inputState.guessing==0 ) {
							warn("use '<- run(...')");
						}
						break;
					}
					case STRING:
					case IDENT:
					{
						message();
						astFactory.addASTChild(currentAST, returnAST);
						break;
					}
					case 173:
					{
						AST tmp45_AST = null;
						tmp45_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp45_AST);
						match(173);
						{
						switch ( LA(1)) {
						case 101:
						{
							AST tmp46_AST = null;
							tmp46_AST = astFactory.create(LT(1));
							astFactory.addASTChild(currentAST, tmp46_AST);
							match(101);
							break;
						}
						case STRING:
						case IDENT:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						prop();
						astFactory.addASTChild(currentAST, returnAST);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					if ( inputState.guessing==0 ) {
						call_AST = (AST)currentAST.root;
						call_AST.setType(SendExpr);
					}
					break;
				}
				case 173:
				{
					AST tmp47_AST = null;
					tmp47_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp47_AST);
					match(173);
					{
					switch ( LA(1)) {
					case 101:
					{
						AST tmp48_AST = null;
						tmp48_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp48_AST);
						match(101);
						break;
					}
					case STRING:
					case IDENT:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					prop();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default:
				{
					break _loop186;
				}
				}
			} while (true);
			}
			call_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_16);
			} else {
			  throw ex;
			}
		}
		returnAST = call_AST;
	}

	public final void accumulator() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST accumulator_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case LITERAL_for:
			{
				AST tmp49_AST = null;
				tmp49_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp49_AST);
				match(LITERAL_for);
				forPatt();
				astFactory.addASTChild(currentAST, returnAST);
				match(LITERAL_in);
				logical();
				astFactory.addASTChild(currentAST, returnAST);
				accumBody();
				astFactory.addASTChild(currentAST, returnAST);
				accumulator_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_if:
			{
				AST tmp51_AST = null;
				tmp51_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp51_AST);
				match(LITERAL_if);
				parenExpr();
				astFactory.addASTChild(currentAST, returnAST);
				accumBody();
				astFactory.addASTChild(currentAST, returnAST);
				accumulator_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_while:
			{
				AST tmp52_AST = null;
				tmp52_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp52_AST);
				match(LITERAL_while);
				parenExpr();
				astFactory.addASTChild(currentAST, returnAST);
				accumBody();
				astFactory.addASTChild(currentAST, returnAST);
				accumulator_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		returnAST = accumulator_AST;
	}

	public final void logical() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logical_AST = null;

		try {      // for error handling
			order();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case 148:
			case 149:
			case 150:
			case 151:
			case 152:
			{
				{
				switch ( LA(1)) {
				case 148:
				{
					AST tmp53_AST = null;
					tmp53_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp53_AST);
					match(148);
					order();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case 149:
				{
					AST tmp54_AST = null;
					tmp54_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp54_AST);
					match(149);
					order();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case 150:
				{
					AST tmp55_AST = null;
					tmp55_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp55_AST);
					match(150);
					order();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case 151:
				{
					AST tmp56_AST = null;
					tmp56_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp56_AST);
					match(151);
					pattern();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case 152:
				{
					AST tmp57_AST = null;
					tmp57_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp57_AST);
					match(152);
					pattern();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				if ( inputState.guessing==0 ) {
					logical_AST = (AST)currentAST.root;
					logical_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CallExpr)).add(logical_AST));
					currentAST.root = logical_AST;
					currentAST.child = logical_AST!=null &&logical_AST.getFirstChild()!=null ?
						logical_AST.getFirstChild() : logical_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case 145:
			{
				{
				int _cnt152=0;
				_loop152:
				do {
					if ((LA(1)==145)) {
						if ( inputState.guessing==0 ) {
							logical_AST = (AST)currentAST.root;
							logical_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CallExpr)).add(logical_AST));
							currentAST.root = logical_AST;
							currentAST.child = logical_AST!=null &&logical_AST.getFirstChild()!=null ?
								logical_AST.getFirstChild() : logical_AST;
							currentAST.advanceChildToEnd();
						}
						AST tmp58_AST = null;
						tmp58_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp58_AST);
						match(145);
						order();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						if ( _cnt152>=1 ) { break _loop152; } else {throw new NoViableAltException(LT(1), getFilename());}
					}

					_cnt152++;
				} while (true);
				}
				break;
			}
			case 101:
			{
				{
				int _cnt154=0;
				_loop154:
				do {
					if ((LA(1)==101)) {
						if ( inputState.guessing==0 ) {
							logical_AST = (AST)currentAST.root;
							logical_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CallExpr)).add(logical_AST));
							currentAST.root = logical_AST;
							currentAST.child = logical_AST!=null &&logical_AST.getFirstChild()!=null ?
								logical_AST.getFirstChild() : logical_AST;
							currentAST.advanceChildToEnd();
						}
						AST tmp59_AST = null;
						tmp59_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp59_AST);
						match(101);
						order();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						if ( _cnt154>=1 ) { break _loop154; } else {throw new NoViableAltException(LT(1), getFilename());}
					}

					_cnt154++;
				} while (true);
				}
				break;
			}
			case 102:
			{
				{
				int _cnt156=0;
				_loop156:
				do {
					if ((LA(1)==102)) {
						if ( inputState.guessing==0 ) {
							logical_AST = (AST)currentAST.root;
							logical_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CallExpr)).add(logical_AST));
							currentAST.root = logical_AST;
							currentAST.child = logical_AST!=null &&logical_AST.getFirstChild()!=null ?
								logical_AST.getFirstChild() : logical_AST;
							currentAST.advanceChildToEnd();
						}
						AST tmp60_AST = null;
						tmp60_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp60_AST);
						match(102);
						order();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						if ( _cnt156>=1 ) { break _loop156; } else {throw new NoViableAltException(LT(1), getFilename());}
					}

					_cnt156++;
				} while (true);
				}
				break;
			}
			case EOF:
			case RCURLY:
			case LINESEP:
			case STRING:
			case IDENT:
			case 86:
			case 94:
			case 97:
			case 115:
			case 117:
			case 118:
			case 129:
			case 130:
			case 131:
			case 132:
			case 133:
			case 134:
			case 135:
			case 136:
			case 137:
			case 138:
			case 139:
			case 140:
			case 141:
			case 146:
			case 147:
			case 171:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			logical_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_17);
			} else {
			  throw ex;
			}
		}
		returnAST = logical_AST;
	}

	public final void accumBody() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST accumBody_AST = null;

		try {      // for error handling
			match(97);
			{
			switch ( LA(1)) {
			case LITERAL__:
			{
				AST tmp62_AST = null;
				tmp62_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp62_AST);
				match(LITERAL__);
				{
				switch ( LA(1)) {
				case 99:
				case 100:
				case 101:
				case 102:
				{
					{
					switch ( LA(1)) {
					case 99:
					{
						AST tmp63_AST = null;
						tmp63_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp63_AST);
						match(99);
						break;
					}
					case 100:
					{
						AST tmp64_AST = null;
						tmp64_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp64_AST);
						match(100);
						break;
					}
					case 101:
					{
						AST tmp65_AST = null;
						tmp65_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp65_AST);
						match(101);
						break;
					}
					case 102:
					{
						AST tmp66_AST = null;
						tmp66_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp66_AST);
						match(102);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					assign();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case 88:
				{
					AST tmp67_AST = null;
					tmp67_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp67_AST);
					match(88);
					verb();
					astFactory.addASTChild(currentAST, returnAST);
					parenArgs();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				break;
			}
			case LITERAL_if:
			case LITERAL_for:
			case LITERAL_while:
			{
				accumulator();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			br();
			astFactory.addASTChild(currentAST, returnAST);
			match(RCURLY);
			accumBody_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		returnAST = accumBody_AST;
	}

	public final void verb() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST verb_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case IDENT:
			{
				AST tmp69_AST = null;
				tmp69_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp69_AST);
				match(IDENT);
				verb_AST = (AST)currentAST.root;
				break;
			}
			case STRING:
			{
				AST tmp70_AST = null;
				tmp70_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp70_AST);
				match(STRING);
				verb_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_18);
			} else {
			  throw ex;
			}
		}
		returnAST = verb_AST;
	}

	public final void parenArgs() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST parenArgs_AST = null;

		try {      // for error handling
			match(116);
			argList();
			astFactory.addASTChild(currentAST, returnAST);
			match(118);
			parenArgs_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_19);
			} else {
			  throw ex;
			}
		}
		returnAST = parenArgs_AST;
	}

	public final void whenFn() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST whenFn_AST = null;

		try {      // for error handling
			objName();
			astFactory.addASTChild(currentAST, returnAST);
			params();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case 106:
			{
				match(106);
				guard();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case 97:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			body();
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				whenFn_AST = (AST)currentAST.root;
				whenFn_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(WhenFn)).add(whenFn_AST));
				currentAST.root = whenFn_AST;
				currentAST.child = whenFn_AST!=null &&whenFn_AST.getFirstChild()!=null ?
					whenFn_AST.getFirstChild() : whenFn_AST;
				currentAST.advanceChildToEnd();
			}
			whenFn_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_14);
			} else {
			  throw ex;
			}
		}
		returnAST = whenFn_AST;
	}

	public final void objName() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST objName_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case URIGetter:
			case IDENT:
			case 173:
			case 176:
			case 177:
			case 178:
			case 179:
			case 180:
			{
				nounExpr();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					objName_AST = (AST)currentAST.root;
					objName_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FinalPattern)).add(objName_AST));
					currentAST.root = objName_AST;
					currentAST.child = objName_AST!=null &&objName_AST.getFirstChild()!=null ?
						objName_AST.getFirstChild() : objName_AST;
					currentAST.advanceChildToEnd();
				}
				objName_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL__:
			{
				AST tmp74_AST = null;
				tmp74_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp74_AST);
				match(LITERAL__);
				if ( inputState.guessing==0 ) {
					objName_AST = (AST)currentAST.root;
					objName_AST.setType(IgnorePattern);
				}
				objName_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_bind:
			{
				AST tmp75_AST = null;
				tmp75_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp75_AST);
				match(LITERAL_bind);
				noun();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					objName_AST = (AST)currentAST.root;
					objName_AST.setType(BindPattern);
				}
				objName_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_var:
			{
				AST tmp76_AST = null;
				tmp76_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp76_AST);
				match(LITERAL_var);
				nounExpr();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					objName_AST = (AST)currentAST.root;
					objName_AST.setType(VarPattern);
				}
				objName_AST = (AST)currentAST.root;
				break;
			}
			case 101:
			{
				AST tmp77_AST = null;
				tmp77_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp77_AST);
				match(101);
				nounExpr();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					objName_AST = (AST)currentAST.root;
					objName_AST.setType(SlotPattern);
				}
				objName_AST = (AST)currentAST.root;
				break;
			}
			case STRING:
			{
				AST tmp78_AST = null;
				tmp78_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp78_AST);
				match(STRING);
				if ( inputState.guessing==0 ) {
					objName_AST = (AST)currentAST.root;
					objName_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(LiteralPattern)).add(objName_AST));
					currentAST.root = objName_AST;
					currentAST.child = objName_AST!=null &&objName_AST.getFirstChild()!=null ?
						objName_AST.getFirstChild() : objName_AST;
					currentAST.advanceChildToEnd();
				}
				objName_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_20);
			} else {
			  throw ex;
			}
		}
		returnAST = objName_AST;
	}

	public final void params() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST params_AST = null;

		try {      // for error handling
			match(116);
			paramList();
			astFactory.addASTChild(currentAST, returnAST);
			br();
			astFactory.addASTChild(currentAST, returnAST);
			match(118);
			if ( inputState.guessing==0 ) {
				params_AST = (AST)currentAST.root;
				params_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(List)).add(params_AST));
				currentAST.root = params_AST;
				currentAST.child = params_AST!=null &&params_AST.getFirstChild()!=null ?
					params_AST.getFirstChild() : params_AST;
				currentAST.advanceChildToEnd();
			}
			params_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_21);
			} else {
			  throw ex;
			}
		}
		returnAST = params_AST;
	}

	public final void guard() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST guard_AST = null;

		try {      // for error handling
			{
			switch ( LA(1)) {
			case URIGetter:
			case IDENT:
			case 173:
			case 176:
			case 177:
			case 178:
			case 179:
			case 180:
			{
				nounExpr();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case URI:
			{
				AST tmp81_AST = null;
				tmp81_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp81_AST);
				match(URI);
				break;
			}
			case 116:
			{
				parenExpr();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			_loop223:
			do {
				if ((LA(1)==170)) {
					AST tmp82_AST = null;
					tmp82_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp82_AST);
					match(170);
					argList();
					astFactory.addASTChild(currentAST, returnAST);
					match(171);
				}
				else {
					break _loop223;
				}

			} while (true);
			}
			guard_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_22);
			} else {
			  throw ex;
			}
		}
		returnAST = guard_AST;
	}

	public final void lambdaExpr() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lambdaExpr_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case LITERAL_thunk:
			{
				AST tmp84_AST = null;
				tmp84_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp84_AST);
				match(LITERAL_thunk);
				body();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					lambdaExpr_AST = (AST)currentAST.root;
					lambdaExpr_AST.setType(LambdaExpr);
				}
				lambdaExpr_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_fn:
			{
				AST tmp85_AST = null;
				tmp85_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp85_AST);
				match(LITERAL_fn);
				paramList();
				astFactory.addASTChild(currentAST, returnAST);
				body();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					lambdaExpr_AST = (AST)currentAST.root;
					lambdaExpr_AST.setType(LambdaExpr);
				}
				lambdaExpr_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_10);
			} else {
			  throw ex;
			}
		}
		returnAST = lambdaExpr_AST;
	}

	public final void paramList() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST paramList_AST = null;

		try {      // for error handling
			boolean synPredMatched94 = false;
			if (((_tokenSet_23.member(LA(1))) && (_tokenSet_24.member(LA(2))))) {
				int _m94 = mark();
				synPredMatched94 = true;
				inputState.guessing++;
				try {
					{
					{
					switch ( LA(1)) {
					case HEX:
					case OCTAL:
					case CHAR_LITERAL:
					case STRING:
					case INT:
					case FLOAT64:
					case 116:
					{
						key();
						break;
					}
					case 94:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					match(94);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched94 = false;
				}
				rewind(_m94);
				inputState.guessing--;
			}
			if ( synPredMatched94 ) {
				mapPattList();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					warn("map-tail");
				}
				paramList_AST = (AST)currentAST.root;
			}
			else if ((_tokenSet_25.member(LA(1))) && (_tokenSet_26.member(LA(2)))) {
				{
				switch ( LA(1)) {
				case QUASIOPEN:
				case URIGetter:
				case IDENT:
				case LITERAL__:
				case 101:
				case 106:
				case LITERAL_bind:
				case LITERAL_var:
				case 148:
				case 149:
				case 153:
				case 154:
				case 155:
				case 156:
				case 157:
				case 170:
				case 173:
				case 176:
				case 177:
				case 178:
				case 179:
				case 180:
				{
					pattern();
					astFactory.addASTChild(currentAST, returnAST);
					{
					switch ( LA(1)) {
					case 117:
					{
						match(117);
						paramList();
						astFactory.addASTChild(currentAST, returnAST);
						break;
					}
					case LINESEP:
					case 97:
					case 118:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					break;
				}
				case LINESEP:
				case 97:
				case 118:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				paramList_AST = (AST)currentAST.root;
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}

		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_27);
			} else {
			  throw ex;
			}
		}
		returnAST = paramList_AST;
	}

	public final void matcher() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST matcher_AST = null;

		try {      // for error handling
			AST tmp87_AST = null;
			tmp87_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp87_AST);
			match(LITERAL_match);
			pattern();
			astFactory.addASTChild(currentAST, returnAST);
			body();
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				matcher_AST = (AST)currentAST.root;
				matcher_AST.setType(EMatcher);
			}
			matcher_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_28);
			} else {
			  throw ex;
			}
		}
		returnAST = matcher_AST;
	}

	public final void bindNamer() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST bindNamer_AST = null;

		try {      // for error handling
			AST tmp88_AST = null;
			tmp88_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp88_AST);
			match(LITERAL_bind);
			noun();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case 106:
			{
				match(106);
				guard();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case RCURLY:
			case LINESEP:
			case STRING:
			case IDENT:
			case 86:
			case LITERAL_in:
			case 94:
			case 97:
			case 115:
			case 117:
			case 118:
			case LITERAL_extends:
			case LITERAL_implements:
			case 129:
			case 130:
			case 131:
			case 132:
			case 133:
			case 134:
			case 135:
			case 136:
			case 137:
			case 138:
			case 139:
			case 140:
			case 141:
			case 146:
			case 147:
			case 171:
			case 175:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				bindNamer_AST = (AST)currentAST.root;
				bindNamer_AST.setType(BindPattern);
			}
			bindNamer_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_29);
			} else {
			  throw ex;
			}
		}
		returnAST = bindNamer_AST;
	}

	public final void noun() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST noun_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case IDENT:
			{
				AST tmp90_AST = null;
				tmp90_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp90_AST);
				match(IDENT);
				noun_AST = (AST)currentAST.root;
				break;
			}
			case URIGetter:
			{
				AST tmp91_AST = null;
				tmp91_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp91_AST);
				match(URIGetter);
				if ( inputState.guessing==0 ) {
					noun_AST = (AST)currentAST.root;
					noun_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(URIExpr)).add(noun_AST));
					currentAST.root = noun_AST;
					currentAST.child = noun_AST!=null &&noun_AST.getFirstChild()!=null ?
						noun_AST.getFirstChild() : noun_AST;
					currentAST.advanceChildToEnd();
				}
				noun_AST = (AST)currentAST.root;
				break;
			}
			case 173:
			{
				AST tmp92_AST = null;
				tmp92_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp92_AST);
				match(173);
				if (!(pocketNounString))
				  throw new SemanticException("pocketNounString");
				{
				switch ( LA(1)) {
				case STRING:
				{
					AST tmp93_AST = null;
					tmp93_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp93_AST);
					match(STRING);
					break;
				}
				case IDENT:
				{
					AST tmp94_AST = null;
					tmp94_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp94_AST);
					match(IDENT);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				noun_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_30);
			} else {
			  throw ex;
			}
		}
		returnAST = noun_AST;
	}

	public final void varNamer() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST varNamer_AST = null;

		try {      // for error handling
			AST tmp95_AST = null;
			tmp95_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp95_AST);
			match(LITERAL_var);
			nounExpr();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case 106:
			{
				match(106);
				guard();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case RCURLY:
			case LINESEP:
			case STRING:
			case IDENT:
			case 86:
			case LITERAL_in:
			case 94:
			case 97:
			case 115:
			case 117:
			case 118:
			case LITERAL_extends:
			case LITERAL_implements:
			case 129:
			case 130:
			case 131:
			case 132:
			case 133:
			case 134:
			case 135:
			case 136:
			case 137:
			case 138:
			case 139:
			case 140:
			case 141:
			case 146:
			case 147:
			case 171:
			case 175:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				varNamer_AST = (AST)currentAST.root;
				varNamer_AST.setType(VarPattern);
			}
			varNamer_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_29);
			} else {
			  throw ex;
			}
		}
		returnAST = varNamer_AST;
	}

	public final void nounExpr() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST nounExpr_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case URIGetter:
			case IDENT:
			case 173:
			{
				noun();
				astFactory.addASTChild(currentAST, returnAST);
				nounExpr_AST = (AST)currentAST.root;
				break;
			}
			case 176:
			case 177:
			case 178:
			{
				dollarHole();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					nounExpr_AST = (AST)currentAST.root;
					nounExpr_AST.setType(QuasiLiteralPattern);
				}
				nounExpr_AST = (AST)currentAST.root;
				break;
			}
			case 179:
			case 180:
			{
				atHole();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					nounExpr_AST = (AST)currentAST.root;
					nounExpr_AST.setType(QuasiPatternPattern);
				}
				nounExpr_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_30);
			} else {
			  throw ex;
			}
		}
		returnAST = nounExpr_AST;
	}

	public final void slotNamer() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST slotNamer_AST = null;

		try {      // for error handling
			AST tmp97_AST = null;
			tmp97_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp97_AST);
			match(101);
			nounExpr();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case 106:
			{
				match(106);
				guard();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case RCURLY:
			case LINESEP:
			case STRING:
			case IDENT:
			case 86:
			case LITERAL_in:
			case 94:
			case 97:
			case 115:
			case 117:
			case 118:
			case LITERAL_extends:
			case LITERAL_implements:
			case 129:
			case 130:
			case 131:
			case 132:
			case 133:
			case 134:
			case 135:
			case 136:
			case 137:
			case 138:
			case 139:
			case 140:
			case 141:
			case 146:
			case 147:
			case 171:
			case 175:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				slotNamer_AST = (AST)currentAST.root;
				slotNamer_AST.setType(SlotPattern);
			}
			slotNamer_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_29);
			} else {
			  throw ex;
			}
		}
		returnAST = slotNamer_AST;
	}

	public final void docoDef() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docoDef_AST = null;

		try {      // for error handling
			doco();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case LITERAL_bind:
			case LITERAL_var:
			case LITERAL_def:
			{
				defExpr();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case LITERAL_interface:
			{
				interfaceExpr();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case LITERAL_thunk:
			case LITERAL_fn:
			{
				lambdaExpr();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			docoDef_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_10);
			} else {
			  throw ex;
			}
		}
		returnAST = docoDef_AST;
	}

	public final void doco() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST doco_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case DOC_COMMENT:
			{
				AST tmp99_AST = null;
				tmp99_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp99_AST);
				match(DOC_COMMENT);
				doco_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_thunk:
			case LITERAL_fn:
			case LITERAL_bind:
			case LITERAL_var:
			case LITERAL_def:
			case LITERAL_to:
			case LITERAL_method:
			case LITERAL_on:
			case LITERAL_interface:
			{
				if ( inputState.guessing==0 ) {
					doco_AST = (AST)currentAST.root;
					doco_AST=(AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(DOC_COMMENT)));
					currentAST.root = doco_AST;
					currentAST.child = doco_AST!=null &&doco_AST.getFirstChild()!=null ?
						doco_AST.getFirstChild() : doco_AST;
					currentAST.advanceChildToEnd();
				}
				doco_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_31);
			} else {
			  throw ex;
			}
		}
		returnAST = doco_AST;
	}

	public final void defExpr() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST defExpr_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case LITERAL_def:
			{
				AST tmp100_AST = null;
				tmp100_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp100_AST);
				match(LITERAL_def);
				{
				boolean synPredMatched61 = false;
				if (((_tokenSet_32.member(LA(1))) && (_tokenSet_33.member(LA(2))))) {
					int _m61 = mark();
					synPredMatched61 = true;
					inputState.guessing++;
					try {
						{
						objectPredict();
						}
					}
					catch (RecognitionException pe) {
						synPredMatched61 = false;
					}
					rewind(_m61);
					inputState.guessing--;
				}
				if ( synPredMatched61 ) {
					objName();
					astFactory.addASTChild(currentAST, returnAST);
					objectExpr();
					astFactory.addASTChild(currentAST, returnAST);
					if ( inputState.guessing==0 ) {
						defExpr_AST = (AST)currentAST.root;
						defExpr_AST.setType(ObjectExpr);
					}
				}
				else {
					boolean synPredMatched63 = false;
					if (((_tokenSet_34.member(LA(1))) && (_tokenSet_35.member(LA(2))))) {
						int _m63 = mark();
						synPredMatched63 = true;
						inputState.guessing++;
						try {
							{
							pattern();
							match(115);
							}
						}
						catch (RecognitionException pe) {
							synPredMatched63 = false;
						}
						rewind(_m63);
						inputState.guessing--;
					}
					if ( synPredMatched63 ) {
						pattern();
						astFactory.addASTChild(currentAST, returnAST);
						match(115);
						rValue();
						astFactory.addASTChild(currentAST, returnAST);
						if ( inputState.guessing==0 ) {
							defExpr_AST = (AST)currentAST.root;
							defExpr_AST.setType(IntoExpr);
						}
					}
					else if ((_tokenSet_36.member(LA(1))) && (_tokenSet_37.member(LA(2)))) {
						bindName();
						astFactory.addASTChild(currentAST, returnAST);
						if ( inputState.guessing==0 ) {
							defExpr_AST = (AST)currentAST.root;
							defExpr_AST.setType(IntoExpr);
						}
					}
					else {
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					defExpr_AST = (AST)currentAST.root;
					break;
				}
				case LITERAL_bind:
				{
					match(LITERAL_bind);
					bindExpr();
					astFactory.addASTChild(currentAST, returnAST);
					defExpr_AST = (AST)currentAST.root;
					break;
				}
				case LITERAL_var:
				{
					match(LITERAL_var);
					bindExpr();
					astFactory.addASTChild(currentAST, returnAST);
					defExpr_AST = (AST)currentAST.root;
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_10);
				} else {
				  throw ex;
				}
			}
			returnAST = defExpr_AST;
		}

	public final void interfaceExpr() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST interfaceExpr_AST = null;

		try {      // for error handling
			AST tmp104_AST = null;
			tmp104_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp104_AST);
			match(LITERAL_interface);
			objName();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case 97:
			case LITERAL_extends:
			case LITERAL_implements:
			case LITERAL_guards:
			{
				{
				switch ( LA(1)) {
				case LITERAL_guards:
				{
					AST tmp105_AST = null;
					tmp105_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp105_AST);
					match(LITERAL_guards);
					br();
					astFactory.addASTChild(currentAST, returnAST);
					pattern();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case 97:
				case LITERAL_extends:
				case LITERAL_implements:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				{
				switch ( LA(1)) {
				case LITERAL_extends:
				{
					AST tmp106_AST = null;
					tmp106_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp106_AST);
					match(LITERAL_extends);
					br();
					astFactory.addASTChild(currentAST, returnAST);
					order();
					astFactory.addASTChild(currentAST, returnAST);
					{
					_loop111:
					do {
						if ((LA(1)==117)) {
							AST tmp107_AST = null;
							tmp107_AST = astFactory.create(LT(1));
							astFactory.addASTChild(currentAST, tmp107_AST);
							match(117);
							order();
							astFactory.addASTChild(currentAST, returnAST);
						}
						else {
							break _loop111;
						}

					} while (true);
					}
					break;
				}
				case 97:
				case LITERAL_implements:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				{
				switch ( LA(1)) {
				case LITERAL_implements:
				{
					AST tmp108_AST = null;
					tmp108_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp108_AST);
					match(LITERAL_implements);
					br();
					astFactory.addASTChild(currentAST, returnAST);
					order();
					astFactory.addASTChild(currentAST, returnAST);
					{
					_loop114:
					do {
						if ((LA(1)==117)) {
							AST tmp109_AST = null;
							tmp109_AST = astFactory.create(LT(1));
							astFactory.addASTChild(currentAST, tmp109_AST);
							match(117);
							order();
							astFactory.addASTChild(currentAST, returnAST);
						}
						else {
							break _loop114;
						}

					} while (true);
					}
					break;
				}
				case 97:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				AST tmp110_AST = null;
				tmp110_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp110_AST);
				match(97);
				{
				_loop116:
				do {
					if ((_tokenSet_38.member(LA(1)))) {
						imethod();
						astFactory.addASTChild(currentAST, returnAST);
						br();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop116;
					}

				} while (true);
				}
				match(RCURLY);
				break;
			}
			case 116:
			{
				mtypes();
				astFactory.addASTChild(currentAST, returnAST);
				{
				switch ( LA(1)) {
				case 106:
				{
					match(106);
					guard();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case EOF:
				case RCURLY:
				case LINESEP:
				case 86:
				case 94:
				case 97:
				case 117:
				case 118:
				case 171:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				interfaceExpr_AST = (AST)currentAST.root;
				interfaceExpr_AST.setType(InterfaceExpr);
			}
			interfaceExpr_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_10);
			} else {
			  throw ex;
			}
		}
		returnAST = interfaceExpr_AST;
	}

	public final void objectPredict() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST objectPredict_AST = null;

		try {      // for error handling
			objName();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case LITERAL_extends:
			{
				AST tmp113_AST = null;
				tmp113_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp113_AST);
				match(LITERAL_extends);
				break;
			}
			case LITERAL_implements:
			{
				AST tmp114_AST = null;
				tmp114_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp114_AST);
				match(LITERAL_implements);
				break;
			}
			case 97:
			{
				AST tmp115_AST = null;
				tmp115_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp115_AST);
				match(97);
				break;
			}
			case 116:
			{
				AST tmp116_AST = null;
				tmp116_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp116_AST);
				match(116);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			objectPredict_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = objectPredict_AST;
	}

	public final void objectExpr() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST objectExpr_AST = null;

		try {      // for error handling
			{
			switch ( LA(1)) {
			case 97:
			case LITERAL_extends:
			case LITERAL_implements:
			{
				{
				switch ( LA(1)) {
				case LITERAL_extends:
				{
					AST tmp117_AST = null;
					tmp117_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp117_AST);
					match(LITERAL_extends);
					br();
					astFactory.addASTChild(currentAST, returnAST);
					order();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case 97:
				case LITERAL_implements:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				{
				switch ( LA(1)) {
				case LITERAL_implements:
				{
					AST tmp118_AST = null;
					tmp118_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp118_AST);
					match(LITERAL_implements);
					br();
					astFactory.addASTChild(currentAST, returnAST);
					order();
					astFactory.addASTChild(currentAST, returnAST);
					{
					_loop76:
					do {
						if ((LA(1)==117)) {
							AST tmp119_AST = null;
							tmp119_AST = astFactory.create(LT(1));
							astFactory.addASTChild(currentAST, tmp119_AST);
							match(117);
							order();
							astFactory.addASTChild(currentAST, returnAST);
						}
						else {
							break _loop76;
						}

					} while (true);
					}
					break;
				}
				case 97:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				script();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case 116:
			{
				params();
				astFactory.addASTChild(currentAST, returnAST);
				resultGuard();
				astFactory.addASTChild(currentAST, returnAST);
				body();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			objectExpr_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_10);
			} else {
			  throw ex;
			}
		}
		returnAST = objectExpr_AST;
	}

	public final void rValue() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST rValue_AST = null;

		try {      // for error handling
			boolean synPredMatched66 = false;
			if (((LA(1)==116) && (_tokenSet_39.member(LA(2))))) {
				int _m66 = mark();
				synPredMatched66 = true;
				inputState.guessing++;
				try {
					{
					match(116);
					eExpr();
					br();
					match(117);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched66 = false;
				}
				rewind(_m66);
				inputState.guessing--;
			}
			if ( synPredMatched66 ) {
				AST tmp120_AST = null;
				tmp120_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp120_AST);
				match(116);
				eExpr();
				astFactory.addASTChild(currentAST, returnAST);
				br();
				astFactory.addASTChild(currentAST, returnAST);
				AST tmp121_AST = null;
				tmp121_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp121_AST);
				match(117);
				eExpr();
				astFactory.addASTChild(currentAST, returnAST);
				br();
				astFactory.addASTChild(currentAST, returnAST);
				AST tmp122_AST = null;
				tmp122_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp122_AST);
				match(118);
				rValue_AST = (AST)currentAST.root;
			}
			else if ((_tokenSet_8.member(LA(1))) && (_tokenSet_9.member(LA(2)))) {
				assign();
				astFactory.addASTChild(currentAST, returnAST);
				rValue_AST = (AST)currentAST.root;
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}

		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_10);
			} else {
			  throw ex;
			}
		}
		returnAST = rValue_AST;
	}

	public final void bindName() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST bindName_AST = null;

		try {      // for error handling
			{
			switch ( LA(1)) {
			case URIGetter:
			case IDENT:
			case 173:
			case 176:
			case 177:
			case 178:
			case 179:
			case 180:
			{
				nounExpr();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					bindName_AST = (AST)currentAST.root;
					bindName_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FinalPattern)).add(bindName_AST));
					currentAST.root = bindName_AST;
					currentAST.child = bindName_AST!=null &&bindName_AST.getFirstChild()!=null ?
						bindName_AST.getFirstChild() : bindName_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case 101:
			{
				AST tmp123_AST = null;
				tmp123_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp123_AST);
				match(101);
				nounExpr();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					bindName_AST = (AST)currentAST.root;
					bindName_AST.setType(SlotPattern);
				}
				break;
			}
			case STRING:
			{
				AST tmp124_AST = null;
				tmp124_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp124_AST);
				match(STRING);
				if ( inputState.guessing==0 ) {
					bindName_AST = (AST)currentAST.root;
					bindName_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(LiteralPattern)).add(bindName_AST));
					currentAST.root = bindName_AST;
					currentAST.child = bindName_AST!=null &&bindName_AST.getFirstChild()!=null ?
						bindName_AST.getFirstChild() : bindName_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case 106:
			{
				match(106);
				guard();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case RCURLY:
			case LINESEP:
			case 86:
			case 94:
			case 97:
			case 115:
			case 116:
			case 117:
			case 118:
			case LITERAL_extends:
			case LITERAL_implements:
			case 171:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			bindName_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_40);
			} else {
			  throw ex;
			}
		}
		returnAST = bindName_AST;
	}

	public final void bindExpr() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST bindExpr_AST = null;

		try {      // for error handling
			bindName();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case 115:
			{
				match(115);
				assign();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					bindExpr_AST = (AST)currentAST.root;
					bindExpr_AST.setType(IntoExpr);
				}
				break;
			}
			case 97:
			case 116:
			case LITERAL_extends:
			case LITERAL_implements:
			{
				objectExpr();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					bindExpr_AST = (AST)currentAST.root;
					bindExpr_AST.setType(ObjectExpr);
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			bindExpr_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_10);
			} else {
			  throw ex;
			}
		}
		returnAST = bindExpr_AST;
	}

	public final void order() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST order_AST = null;

		try {      // for error handling
			interval();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case 153:
			case 154:
			case 155:
			case 156:
			case 157:
			{
				compareOp();
				astFactory.addASTChild(currentAST, returnAST);
				interval();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					order_AST = (AST)currentAST.root;
					order_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CallExpr)).add(order_AST));
					currentAST.root = order_AST;
					currentAST.child = order_AST!=null &&order_AST.getFirstChild()!=null ?
						order_AST.getFirstChild() : order_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case 106:
			{
				AST tmp127_AST = null;
				tmp127_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp127_AST);
				match(106);
				guard();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					order_AST = (AST)currentAST.root;
					order_AST.setType(CoerceExpr);
				}
				break;
			}
			case EOF:
			case RCURLY:
			case LINESEP:
			case STRING:
			case IDENT:
			case 86:
			case 94:
			case 97:
			case 101:
			case 102:
			case 115:
			case 117:
			case 118:
			case LITERAL_implements:
			case 129:
			case 130:
			case 131:
			case 132:
			case 133:
			case 134:
			case 135:
			case 136:
			case 137:
			case 138:
			case 139:
			case 140:
			case 141:
			case 145:
			case 146:
			case 147:
			case 148:
			case 149:
			case 150:
			case 151:
			case 152:
			case 171:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			order_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_41);
			} else {
			  throw ex;
			}
		}
		returnAST = order_AST;
	}

	public final void script() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST script_AST = null;

		try {      // for error handling
			AST tmp128_AST = null;
			tmp128_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp128_AST);
			match(97);
			{
			_loop83:
			do {
				if ((_tokenSet_38.member(LA(1)))) {
					method();
					astFactory.addASTChild(currentAST, returnAST);
					br();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop83;
				}

			} while (true);
			}
			{
			_loop85:
			do {
				if ((LA(1)==LITERAL_match)) {
					matcher();
					astFactory.addASTChild(currentAST, returnAST);
					br();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop85;
				}

			} while (true);
			}
			match(RCURLY);
			if ( inputState.guessing==0 ) {
				script_AST = (AST)currentAST.root;
				script_AST.setType(EScript);
			}
			script_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_10);
			} else {
			  throw ex;
			}
		}
		returnAST = script_AST;
	}

	public final void resultGuard() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST resultGuard_AST = null;

		try {      // for error handling
			{
			switch ( LA(1)) {
			case 106:
			{
				match(106);
				guard();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case RCURLY:
			case LINESEP:
			case DOC_COMMENT:
			case 97:
			case LITERAL_to:
			case LITERAL_method:
			case LITERAL_on:
			case LITERAL_throws:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case LITERAL_throws:
			{
				AST tmp131_AST = null;
				tmp131_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp131_AST);
				match(LITERAL_throws);
				guardList();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case RCURLY:
			case LINESEP:
			case DOC_COMMENT:
			case 97:
			case LITERAL_to:
			case LITERAL_method:
			case LITERAL_on:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			resultGuard_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_42);
			} else {
			  throw ex;
			}
		}
		returnAST = resultGuard_AST;
	}

	public final void method() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST method_AST = null;

		try {      // for error handling
			doco();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case LITERAL_to:
			{
				AST tmp132_AST = null;
				tmp132_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp132_AST);
				match(LITERAL_to);
				methHead();
				astFactory.addASTChild(currentAST, returnAST);
				body();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case LITERAL_method:
			{
				AST tmp133_AST = null;
				tmp133_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp133_AST);
				match(LITERAL_method);
				methHead();
				astFactory.addASTChild(currentAST, returnAST);
				body();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case LITERAL_on:
			{
				AST tmp134_AST = null;
				tmp134_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp134_AST);
				match(LITERAL_on);
				methHead();
				astFactory.addASTChild(currentAST, returnAST);
				body();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				method_AST = (AST)currentAST.root;
				method_AST.setType(EMethod);
			}
			method_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_43);
			} else {
			  throw ex;
			}
		}
		returnAST = method_AST;
	}

	public final void methHead() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST methHead_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case 116:
			{
				params();
				astFactory.addASTChild(currentAST, returnAST);
				resultGuard();
				astFactory.addASTChild(currentAST, returnAST);
				methHead_AST = (AST)currentAST.root;
				break;
			}
			case STRING:
			case IDENT:
			{
				verb();
				astFactory.addASTChild(currentAST, returnAST);
				params();
				astFactory.addASTChild(currentAST, returnAST);
				resultGuard();
				astFactory.addASTChild(currentAST, returnAST);
				methHead_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_44);
			} else {
			  throw ex;
			}
		}
		returnAST = methHead_AST;
	}

	public final void key() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST key_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case 116:
			{
				parenExpr();
				astFactory.addASTChild(currentAST, returnAST);
				key_AST = (AST)currentAST.root;
				break;
			}
			case HEX:
			case OCTAL:
			case CHAR_LITERAL:
			case STRING:
			case INT:
			case FLOAT64:
			{
				literal();
				astFactory.addASTChild(currentAST, returnAST);
				key_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_45);
			} else {
			  throw ex;
			}
		}
		returnAST = key_AST;
	}

	public final void mapPattList() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST mapPattList_AST = null;

		try {      // for error handling
			{
			switch ( LA(1)) {
			case HEX:
			case OCTAL:
			case CHAR_LITERAL:
			case STRING:
			case INT:
			case FLOAT64:
			case 94:
			case 116:
			{
				mapPattern();
				astFactory.addASTChild(currentAST, returnAST);
				{
				switch ( LA(1)) {
				case 117:
				{
					match(117);
					mapPattList();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case LINESEP:
				case 97:
				case 118:
				case 171:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				break;
			}
			case LINESEP:
			case 97:
			case 118:
			case 171:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			mapPattList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_46);
			} else {
			  throw ex;
			}
		}
		returnAST = mapPattList_AST;
	}

	public final void patternList() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST patternList_AST = null;

		try {      // for error handling
			{
			switch ( LA(1)) {
			case QUASIOPEN:
			case URIGetter:
			case IDENT:
			case LITERAL__:
			case 101:
			case 106:
			case LITERAL_bind:
			case LITERAL_var:
			case 148:
			case 149:
			case 153:
			case 154:
			case 155:
			case 156:
			case 157:
			case 170:
			case 173:
			case 176:
			case 177:
			case 178:
			case 179:
			case 180:
			{
				pattern();
				astFactory.addASTChild(currentAST, returnAST);
				{
				switch ( LA(1)) {
				case 117:
				{
					match(117);
					patternList();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case LINESEP:
				case 171:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				break;
			}
			case LINESEP:
			case 171:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			patternList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_47);
			} else {
			  throw ex;
			}
		}
		returnAST = patternList_AST;
	}

	public final void guardList() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST guardList_AST = null;

		try {      // for error handling
			guard();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop105:
			do {
				if ((LA(1)==117)) {
					match(117);
					guard();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop105;
				}

			} while (true);
			}
			guardList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_42);
			} else {
			  throw ex;
			}
		}
		returnAST = guardList_AST;
	}

	public final void imethod() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST imethod_AST = null;

		try {      // for error handling
			doco();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case LITERAL_to:
			{
				AST tmp138_AST = null;
				tmp138_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp138_AST);
				match(LITERAL_to);
				imethHead();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case LITERAL_method:
			{
				AST tmp139_AST = null;
				tmp139_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp139_AST);
				match(LITERAL_method);
				imethHead();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case LITERAL_on:
			{
				AST tmp140_AST = null;
				tmp140_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp140_AST);
				match(LITERAL_on);
				imethHead();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				imethod_AST = (AST)currentAST.root;
				imethod_AST.setType(EMethod);
			}
			imethod_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_48);
			} else {
			  throw ex;
			}
		}
		returnAST = imethod_AST;
	}

	public final void mtypes() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST mtypes_AST = null;

		try {      // for error handling
			match(116);
			typeList();
			astFactory.addASTChild(currentAST, returnAST);
			br();
			astFactory.addASTChild(currentAST, returnAST);
			match(118);
			if ( inputState.guessing==0 ) {
				mtypes_AST = (AST)currentAST.root;
				mtypes_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(List)).add(mtypes_AST));
				currentAST.root = mtypes_AST;
				currentAST.child = mtypes_AST!=null &&mtypes_AST.getFirstChild()!=null ?
					mtypes_AST.getFirstChild() : mtypes_AST;
				currentAST.advanceChildToEnd();
			}
			mtypes_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_49);
			} else {
			  throw ex;
			}
		}
		returnAST = mtypes_AST;
	}

	public final void imethHead() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST imethHead_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case 116:
			{
				mtypes();
				astFactory.addASTChild(currentAST, returnAST);
				resultGuard();
				astFactory.addASTChild(currentAST, returnAST);
				imethHead_AST = (AST)currentAST.root;
				break;
			}
			case STRING:
			case IDENT:
			{
				verb();
				astFactory.addASTChild(currentAST, returnAST);
				mtypes();
				astFactory.addASTChild(currentAST, returnAST);
				resultGuard();
				astFactory.addASTChild(currentAST, returnAST);
				imethHead_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_48);
			} else {
			  throw ex;
			}
		}
		returnAST = imethHead_AST;
	}

	public final void ptype() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ptype_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case URIGetter:
			case IDENT:
			case 173:
			case 176:
			case 177:
			case 178:
			case 179:
			case 180:
			{
				nounExpr();
				astFactory.addASTChild(currentAST, returnAST);
				{
				switch ( LA(1)) {
				case 106:
				{
					match(106);
					guard();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case LINESEP:
				case 117:
				case 118:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				if ( inputState.guessing==0 ) {
					ptype_AST = (AST)currentAST.root;
					ptype_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FinalPattern)).add(ptype_AST));
					currentAST.root = ptype_AST;
					currentAST.child = ptype_AST!=null &&ptype_AST.getFirstChild()!=null ?
						ptype_AST.getFirstChild() : ptype_AST;
					currentAST.advanceChildToEnd();
				}
				ptype_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL__:
			{
				AST tmp144_AST = null;
				tmp144_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp144_AST);
				match(LITERAL__);
				{
				switch ( LA(1)) {
				case 106:
				{
					match(106);
					guard();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case LINESEP:
				case 117:
				case 118:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				if ( inputState.guessing==0 ) {
					ptype_AST = (AST)currentAST.root;
					ptype_AST.setType(IgnorePattern);
				}
				ptype_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_50);
			} else {
			  throw ex;
			}
		}
		returnAST = ptype_AST;
	}

	public final void typeList() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeList_AST = null;

		try {      // for error handling
			{
			switch ( LA(1)) {
			case URIGetter:
			case IDENT:
			case LITERAL__:
			case 173:
			case 176:
			case 177:
			case 178:
			case 179:
			case 180:
			{
				ptype();
				astFactory.addASTChild(currentAST, returnAST);
				{
				switch ( LA(1)) {
				case 117:
				{
					match(117);
					typeList();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case LINESEP:
				case 118:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				break;
			}
			case LINESEP:
			case 118:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			typeList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_51);
			} else {
			  throw ex;
			}
		}
		returnAST = typeList_AST;
	}

	public final void cond() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cond_AST = null;

		try {      // for error handling
			condAnd();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop144:
			do {
				if ((LA(1)==146)) {
					AST tmp147_AST = null;
					tmp147_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp147_AST);
					match(146);
					condAnd();
					astFactory.addASTChild(currentAST, returnAST);
					if ( inputState.guessing==0 ) {
						cond_AST = (AST)currentAST.root;
						cond_AST.setType(OrExpr);
					}
				}
				else {
					break _loop144;
				}

			} while (true);
			}
			cond_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_52);
			} else {
			  throw ex;
			}
		}
		returnAST = cond_AST;
	}

	public final void assignOp() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST assignOp_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case 129:
			{
				AST tmp148_AST = null;
				tmp148_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp148_AST);
				match(129);
				assignOp_AST = (AST)currentAST.root;
				break;
			}
			case 130:
			{
				AST tmp149_AST = null;
				tmp149_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp149_AST);
				match(130);
				assignOp_AST = (AST)currentAST.root;
				break;
			}
			case 131:
			{
				AST tmp150_AST = null;
				tmp150_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp150_AST);
				match(131);
				assignOp_AST = (AST)currentAST.root;
				break;
			}
			case 132:
			{
				AST tmp151_AST = null;
				tmp151_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp151_AST);
				match(132);
				assignOp_AST = (AST)currentAST.root;
				break;
			}
			case 133:
			{
				AST tmp152_AST = null;
				tmp152_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp152_AST);
				match(133);
				assignOp_AST = (AST)currentAST.root;
				break;
			}
			case 134:
			{
				AST tmp153_AST = null;
				tmp153_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp153_AST);
				match(134);
				assignOp_AST = (AST)currentAST.root;
				break;
			}
			case 135:
			{
				AST tmp154_AST = null;
				tmp154_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp154_AST);
				match(135);
				assignOp_AST = (AST)currentAST.root;
				break;
			}
			case 136:
			{
				AST tmp155_AST = null;
				tmp155_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp155_AST);
				match(136);
				assignOp_AST = (AST)currentAST.root;
				break;
			}
			case 137:
			{
				AST tmp156_AST = null;
				tmp156_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp156_AST);
				match(137);
				assignOp_AST = (AST)currentAST.root;
				break;
			}
			case 138:
			{
				AST tmp157_AST = null;
				tmp157_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp157_AST);
				match(138);
				assignOp_AST = (AST)currentAST.root;
				break;
			}
			case 139:
			{
				AST tmp158_AST = null;
				tmp158_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp158_AST);
				match(139);
				assignOp_AST = (AST)currentAST.root;
				break;
			}
			case 140:
			{
				AST tmp159_AST = null;
				tmp159_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp159_AST);
				match(140);
				assignOp_AST = (AST)currentAST.root;
				break;
			}
			case 141:
			{
				AST tmp160_AST = null;
				tmp160_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp160_AST);
				match(141);
				assignOp_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_8);
			} else {
			  throw ex;
			}
		}
		returnAST = assignOp_AST;
	}

	public final void condAnd() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST condAnd_AST = null;

		try {      // for error handling
			logical();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop147:
			do {
				if ((LA(1)==147)) {
					AST tmp161_AST = null;
					tmp161_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp161_AST);
					match(147);
					logical();
					astFactory.addASTChild(currentAST, returnAST);
					if ( inputState.guessing==0 ) {
						condAnd_AST = (AST)currentAST.root;
						condAnd_AST.setType(AndExpr);
					}
				}
				else {
					break _loop147;
				}

			} while (true);
			}
			condAnd_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_53);
			} else {
			  throw ex;
			}
		}
		returnAST = condAnd_AST;
	}

	public final void interval() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST interval_AST = null;

		try {      // for error handling
			shift();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case 158:
			case 159:
			{
				if ( inputState.guessing==0 ) {
					interval_AST = (AST)currentAST.root;
					interval_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CallExpr)).add(interval_AST));
					currentAST.root = interval_AST;
					currentAST.child = interval_AST!=null &&interval_AST.getFirstChild()!=null ?
						interval_AST.getFirstChild() : interval_AST;
					currentAST.advanceChildToEnd();
				}
				{
				switch ( LA(1)) {
				case 158:
				{
					AST tmp162_AST = null;
					tmp162_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp162_AST);
					match(158);
					break;
				}
				case 159:
				{
					AST tmp163_AST = null;
					tmp163_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp163_AST);
					match(159);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				shift();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case RCURLY:
			case LINESEP:
			case STRING:
			case IDENT:
			case 86:
			case 94:
			case 97:
			case 101:
			case 102:
			case 106:
			case 115:
			case 117:
			case 118:
			case LITERAL_implements:
			case 129:
			case 130:
			case 131:
			case 132:
			case 133:
			case 134:
			case 135:
			case 136:
			case 137:
			case 138:
			case 139:
			case 140:
			case 141:
			case 145:
			case 146:
			case 147:
			case 148:
			case 149:
			case 150:
			case 151:
			case 152:
			case 153:
			case 154:
			case 155:
			case 156:
			case 157:
			case 171:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			interval_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_54);
			} else {
			  throw ex;
			}
		}
		returnAST = interval_AST;
	}

	public final void compareOp() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST compareOp_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case 153:
			{
				AST tmp164_AST = null;
				tmp164_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp164_AST);
				match(153);
				compareOp_AST = (AST)currentAST.root;
				break;
			}
			case 154:
			{
				AST tmp165_AST = null;
				tmp165_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp165_AST);
				match(154);
				compareOp_AST = (AST)currentAST.root;
				break;
			}
			case 155:
			{
				AST tmp166_AST = null;
				tmp166_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp166_AST);
				match(155);
				compareOp_AST = (AST)currentAST.root;
				break;
			}
			case 156:
			{
				AST tmp167_AST = null;
				tmp167_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp167_AST);
				match(156);
				compareOp_AST = (AST)currentAST.root;
				break;
			}
			case 157:
			{
				AST tmp168_AST = null;
				tmp168_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp168_AST);
				match(157);
				br();
				astFactory.addASTChild(currentAST, returnAST);
				compareOp_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_55);
			} else {
			  throw ex;
			}
		}
		returnAST = compareOp_AST;
	}

	public final void shift() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST shift_AST = null;

		try {      // for error handling
			add();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop166:
			do {
				if ((LA(1)==160||LA(1)==161)) {
					if ( inputState.guessing==0 ) {
						shift_AST = (AST)currentAST.root;
						shift_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CallExpr)).add(shift_AST));
						currentAST.root = shift_AST;
						currentAST.child = shift_AST!=null &&shift_AST.getFirstChild()!=null ?
							shift_AST.getFirstChild() : shift_AST;
						currentAST.advanceChildToEnd();
					}
					{
					switch ( LA(1)) {
					case 160:
					{
						AST tmp169_AST = null;
						tmp169_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp169_AST);
						match(160);
						break;
					}
					case 161:
					{
						AST tmp170_AST = null;
						tmp170_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp170_AST);
						match(161);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					add();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop166;
				}

			} while (true);
			}
			shift_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_56);
			} else {
			  throw ex;
			}
		}
		returnAST = shift_AST;
	}

	public final void add() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST add_AST = null;

		try {      // for error handling
			mult();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop170:
			do {
				if ((LA(1)==99||LA(1)==162)) {
					if ( inputState.guessing==0 ) {
						add_AST = (AST)currentAST.root;
						add_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CallExpr)).add(add_AST));
						currentAST.root = add_AST;
						currentAST.child = add_AST!=null &&add_AST.getFirstChild()!=null ?
							add_AST.getFirstChild() : add_AST;
						currentAST.advanceChildToEnd();
					}
					{
					switch ( LA(1)) {
					case 99:
					{
						AST tmp171_AST = null;
						tmp171_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp171_AST);
						match(99);
						break;
					}
					case 162:
					{
						AST tmp172_AST = null;
						tmp172_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp172_AST);
						match(162);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					mult();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop170;
				}

			} while (true);
			}
			add_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_57);
			} else {
			  throw ex;
			}
		}
		returnAST = add_AST;
	}

	public final void mult() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST mult_AST = null;

		try {      // for error handling
			pow();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop174:
			do {
				if ((_tokenSet_58.member(LA(1)))) {
					if ( inputState.guessing==0 ) {
						mult_AST = (AST)currentAST.root;
						mult_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CallExpr)).add(mult_AST));
						currentAST.root = mult_AST;
						currentAST.child = mult_AST!=null &&mult_AST.getFirstChild()!=null ?
							mult_AST.getFirstChild() : mult_AST;
						currentAST.advanceChildToEnd();
					}
					{
					switch ( LA(1)) {
					case 100:
					{
						AST tmp173_AST = null;
						tmp173_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp173_AST);
						match(100);
						break;
					}
					case 163:
					{
						AST tmp174_AST = null;
						tmp174_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp174_AST);
						match(163);
						break;
					}
					case 164:
					{
						AST tmp175_AST = null;
						tmp175_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp175_AST);
						match(164);
						break;
					}
					case 165:
					{
						AST tmp176_AST = null;
						tmp176_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp176_AST);
						match(165);
						break;
					}
					case 166:
					{
						AST tmp177_AST = null;
						tmp177_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp177_AST);
						match(166);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					pow();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop174;
				}

			} while (true);
			}
			mult_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_59);
			} else {
			  throw ex;
			}
		}
		returnAST = mult_AST;
	}

	public final void pow() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST pow_AST = null;

		try {      // for error handling
			prefix();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case 167:
			{
				AST tmp178_AST = null;
				tmp178_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp178_AST);
				match(167);
				prefix();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					pow_AST = (AST)currentAST.root;
					pow_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CallExpr)).add(pow_AST));
					currentAST.root = pow_AST;
					currentAST.child = pow_AST!=null &&pow_AST.getFirstChild()!=null ?
						pow_AST.getFirstChild() : pow_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case EOF:
			case RCURLY:
			case LINESEP:
			case STRING:
			case IDENT:
			case 86:
			case 94:
			case 97:
			case 99:
			case 100:
			case 101:
			case 102:
			case 106:
			case 115:
			case 117:
			case 118:
			case LITERAL_implements:
			case 129:
			case 130:
			case 131:
			case 132:
			case 133:
			case 134:
			case 135:
			case 136:
			case 137:
			case 138:
			case 139:
			case 140:
			case 141:
			case 145:
			case 146:
			case 147:
			case 148:
			case 149:
			case 150:
			case 151:
			case 152:
			case 153:
			case 154:
			case 155:
			case 156:
			case 157:
			case 158:
			case 159:
			case 160:
			case 161:
			case 162:
			case 163:
			case 164:
			case 165:
			case 166:
			case 171:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			pow_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_60);
			} else {
			  throw ex;
			}
		}
		returnAST = pow_AST;
	}

	public final void prefix() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST prefix_AST = null;
		AST op_AST = null;
		AST a_AST = null;
		Token  neg = null;
		AST neg_AST = null;
		AST b_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case QUASIOPEN:
			case URI:
			case URIGetter:
			case HEX:
			case OCTAL:
			case CHAR_LITERAL:
			case STRING:
			case IDENT:
			case INT:
			case FLOAT64:
			case LITERAL_meta:
			case LITERAL_if:
			case LITERAL_for:
			case LITERAL_accum:
			case LITERAL_while:
			case 97:
			case LITERAL_when:
			case LITERAL_escape:
			case LITERAL_switch:
			case LITERAL_try:
			case 116:
			case 170:
			case 173:
			case 176:
			case 177:
			case 178:
			case 179:
			case 180:
			{
				postfix();
				astFactory.addASTChild(currentAST, returnAST);
				prefix_AST = (AST)currentAST.root;
				break;
			}
			case 99:
			case 100:
			case 101:
			case 168:
			case 169:
			{
				prefixOp();
				op_AST = (AST)returnAST;
				postfix();
				a_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					prefix_AST = (AST)currentAST.root;
					prefix_AST=(AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(CallExpr)).add(a_AST).add(op_AST));
					currentAST.root = prefix_AST;
					currentAST.child = prefix_AST!=null &&prefix_AST.getFirstChild()!=null ?
						prefix_AST.getFirstChild() : prefix_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case 162:
			{
				neg = LT(1);
				neg_AST = astFactory.create(neg);
				match(162);
				prim();
				b_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					prefix_AST = (AST)currentAST.root;
					prefix_AST=(AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(CallExpr)).add(b_AST).add(neg_AST));
					currentAST.root = prefix_AST;
					currentAST.child = prefix_AST!=null &&prefix_AST.getFirstChild()!=null ?
						prefix_AST.getFirstChild() : prefix_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_61);
			} else {
			  throw ex;
			}
		}
		returnAST = prefix_AST;
	}

	public final void postfix() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST postfix_AST = null;

		try {      // for error handling
			call();
			astFactory.addASTChild(currentAST, returnAST);
			postfix_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_61);
			} else {
			  throw ex;
			}
		}
		returnAST = postfix_AST;
	}

	public final void prefixOp() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST prefixOp_AST = null;

		try {      // for error handling
			{
			switch ( LA(1)) {
			case 168:
			{
				AST tmp179_AST = null;
				tmp179_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp179_AST);
				match(168);
				break;
			}
			case 169:
			{
				AST tmp180_AST = null;
				tmp180_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp180_AST);
				match(169);
				break;
			}
			case 101:
			{
				AST tmp181_AST = null;
				tmp181_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp181_AST);
				match(101);
				break;
			}
			case 100:
			{
				AST tmp182_AST = null;
				tmp182_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp182_AST);
				match(100);
				break;
			}
			case 99:
			{
				AST tmp183_AST = null;
				tmp183_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp183_AST);
				match(99);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			prefixOp_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_62);
			} else {
			  throw ex;
			}
		}
		returnAST = prefixOp_AST;
	}

	public final void prim() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST prim_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case HEX:
			case OCTAL:
			case CHAR_LITERAL:
			case STRING:
			case INT:
			case FLOAT64:
			{
				literal();
				astFactory.addASTChild(currentAST, returnAST);
				prim_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_meta:
			case LITERAL_if:
			case LITERAL_for:
			case LITERAL_accum:
			case LITERAL_while:
			case LITERAL_when:
			case LITERAL_escape:
			case LITERAL_switch:
			case LITERAL_try:
			{
				basic();
				astFactory.addASTChild(currentAST, returnAST);
				prim_AST = (AST)currentAST.root;
				break;
			}
			case URIGetter:
			case IDENT:
			case 173:
			case 176:
			case 177:
			case 178:
			case 179:
			case 180:
			{
				nounExpr();
				astFactory.addASTChild(currentAST, returnAST);
				{
				switch ( LA(1)) {
				case QUASIOPEN:
				{
					quasiString();
					astFactory.addASTChild(currentAST, returnAST);
					if ( inputState.guessing==0 ) {
						prim_AST = (AST)currentAST.root;
						prim_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(QuasiLiteralExpr)).add(prim_AST));
						currentAST.root = prim_AST;
						currentAST.child = prim_AST!=null &&prim_AST.getFirstChild()!=null ?
							prim_AST.getFirstChild() : prim_AST;
						currentAST.advanceChildToEnd();
					}
					break;
				}
				case EOF:
				case RCURLY:
				case LINESEP:
				case STRING:
				case IDENT:
				case 86:
				case 88:
				case LITERAL_if:
				case LITERAL_for:
				case LITERAL_in:
				case 94:
				case LITERAL_while:
				case 97:
				case 99:
				case 100:
				case 101:
				case 102:
				case 106:
				case 115:
				case 116:
				case 117:
				case 118:
				case LITERAL_extends:
				case LITERAL_implements:
				case 129:
				case 130:
				case 131:
				case 132:
				case 133:
				case 134:
				case 135:
				case 136:
				case 137:
				case 138:
				case 139:
				case 140:
				case 141:
				case 145:
				case 146:
				case 147:
				case 148:
				case 149:
				case 150:
				case 151:
				case 152:
				case 153:
				case 154:
				case 155:
				case 156:
				case 157:
				case 158:
				case 159:
				case 160:
				case 161:
				case 162:
				case 163:
				case 164:
				case 165:
				case 166:
				case 167:
				case 170:
				case 171:
				case 172:
				case 173:
				case 175:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				prim_AST = (AST)currentAST.root;
				break;
			}
			case 116:
			{
				parenExpr();
				astFactory.addASTChild(currentAST, returnAST);
				{
				switch ( LA(1)) {
				case QUASIOPEN:
				{
					quasiString();
					astFactory.addASTChild(currentAST, returnAST);
					if ( inputState.guessing==0 ) {
						prim_AST = (AST)currentAST.root;
						prim_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(QuasiLiteralExpr)).add(prim_AST));
						warn("***We may deprecate this***");
						currentAST.root = prim_AST;
						currentAST.child = prim_AST!=null &&prim_AST.getFirstChild()!=null ?
							prim_AST.getFirstChild() : prim_AST;
						currentAST.advanceChildToEnd();
					}
					break;
				}
				case EOF:
				case RCURLY:
				case LINESEP:
				case STRING:
				case IDENT:
				case 86:
				case 88:
				case LITERAL_if:
				case LITERAL_for:
				case LITERAL_in:
				case 94:
				case LITERAL_while:
				case 97:
				case 99:
				case 100:
				case 101:
				case 102:
				case 106:
				case 115:
				case 116:
				case 117:
				case 118:
				case LITERAL_extends:
				case LITERAL_implements:
				case 129:
				case 130:
				case 131:
				case 132:
				case 133:
				case 134:
				case 135:
				case 136:
				case 137:
				case 138:
				case 139:
				case 140:
				case 141:
				case 145:
				case 146:
				case 147:
				case 148:
				case 149:
				case 150:
				case 151:
				case 152:
				case 153:
				case 154:
				case 155:
				case 156:
				case 157:
				case 158:
				case 159:
				case 160:
				case 161:
				case 162:
				case 163:
				case 164:
				case 165:
				case 166:
				case 167:
				case 170:
				case 171:
				case 172:
				case 173:
				case 175:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				prim_AST = (AST)currentAST.root;
				break;
			}
			case QUASIOPEN:
			{
				quasiString();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					prim_AST = (AST)currentAST.root;
					prim_AST=(AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(QuasiLiteralExpr,"simple")).add(astFactory.create(STRING,"simple")).add(prim_AST));
					currentAST.root = prim_AST;
					currentAST.child = prim_AST!=null &&prim_AST.getFirstChild()!=null ?
						prim_AST.getFirstChild() : prim_AST;
					currentAST.advanceChildToEnd();
				}
				prim_AST = (AST)currentAST.root;
				break;
			}
			case URI:
			{
				AST tmp184_AST = null;
				tmp184_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp184_AST);
				match(URI);
				if ( inputState.guessing==0 ) {
					prim_AST = (AST)currentAST.root;
					prim_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(URIExpr)).add(prim_AST));
					currentAST.root = prim_AST;
					currentAST.child = prim_AST!=null &&prim_AST.getFirstChild()!=null ?
						prim_AST.getFirstChild() : prim_AST;
					currentAST.advanceChildToEnd();
				}
				prim_AST = (AST)currentAST.root;
				break;
			}
			case 170:
			{
				AST tmp185_AST = null;
				tmp185_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp185_AST);
				match(170);
				{
				boolean synPredMatched209 = false;
				if (((_tokenSet_63.member(LA(1))) && (_tokenSet_64.member(LA(2))))) {
					int _m209 = mark();
					synPredMatched209 = true;
					inputState.guessing++;
					try {
						{
						{
						switch ( LA(1)) {
						case QUASIOPEN:
						case URI:
						case URIGetter:
						case HEX:
						case OCTAL:
						case DOC_COMMENT:
						case CHAR_LITERAL:
						case STRING:
						case IDENT:
						case INT:
						case FLOAT64:
						case LITERAL_meta:
						case LITERAL_if:
						case LITERAL_for:
						case LITERAL_accum:
						case LITERAL_while:
						case 97:
						case 99:
						case 100:
						case 101:
						case LITERAL_when:
						case LITERAL_escape:
						case LITERAL_thunk:
						case LITERAL_fn:
						case LITERAL_switch:
						case LITERAL_try:
						case LITERAL_bind:
						case LITERAL_var:
						case LITERAL_def:
						case 116:
						case LITERAL_interface:
						case LITERAL_break:
						case LITERAL_continue:
						case LITERAL_return:
						case 145:
						case 162:
						case 168:
						case 169:
						case 170:
						case 173:
						case 176:
						case 177:
						case 178:
						case 179:
						case 180:
						{
							eExpr();
							break;
						}
						case 94:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						match(94);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched209 = false;
					}
					rewind(_m209);
					inputState.guessing--;
				}
				if ( synPredMatched209 ) {
					mapList();
					astFactory.addASTChild(currentAST, returnAST);
					if ( inputState.guessing==0 ) {
						prim_AST = (AST)currentAST.root;
						prim_AST.setType(MapExpr);
					}
				}
				else if ((_tokenSet_65.member(LA(1))) && (_tokenSet_64.member(LA(2)))) {
					exprList();
					astFactory.addASTChild(currentAST, returnAST);
					if ( inputState.guessing==0 ) {
						prim_AST = (AST)currentAST.root;
						prim_AST.setType(TupleExpr);
					}
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}

				}
				match(171);
				prim_AST = (AST)currentAST.root;
				break;
			}
			case 97:
			{
				body();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					prim_AST = (AST)currentAST.root;
					prim_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(HideExpr)).add(prim_AST));
					currentAST.root = prim_AST;
					currentAST.child = prim_AST!=null &&prim_AST.getFirstChild()!=null ?
						prim_AST.getFirstChild() : prim_AST;
					currentAST.advanceChildToEnd();
				}
				prim_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		returnAST = prim_AST;
	}

	public final void argList() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST argList_AST = null;

		try {      // for error handling
			boolean synPredMatched200 = false;
			if (((_tokenSet_66.member(LA(1))) && (_tokenSet_67.member(LA(2))))) {
				int _m200 = mark();
				synPredMatched200 = true;
				inputState.guessing++;
				try {
					{
					{
					switch ( LA(1)) {
					case QUASIOPEN:
					case URI:
					case URIGetter:
					case HEX:
					case OCTAL:
					case DOC_COMMENT:
					case CHAR_LITERAL:
					case STRING:
					case IDENT:
					case INT:
					case FLOAT64:
					case LITERAL_meta:
					case LITERAL_if:
					case LITERAL_for:
					case LITERAL_accum:
					case LITERAL_while:
					case 97:
					case 99:
					case 100:
					case 101:
					case LITERAL_when:
					case LITERAL_escape:
					case LITERAL_thunk:
					case LITERAL_fn:
					case LITERAL_switch:
					case LITERAL_try:
					case LITERAL_bind:
					case LITERAL_var:
					case LITERAL_def:
					case 116:
					case LITERAL_interface:
					case LITERAL_break:
					case LITERAL_continue:
					case LITERAL_return:
					case 145:
					case 162:
					case 168:
					case 169:
					case 170:
					case 173:
					case 176:
					case 177:
					case 178:
					case 179:
					case 180:
					{
						eExpr();
						break;
					}
					case 94:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					match(94);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched200 = false;
				}
				rewind(_m200);
				inputState.guessing--;
			}
			if ( synPredMatched200 ) {
				mapList();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					warn("map-tail");
				}
				argList_AST = (AST)currentAST.root;
			}
			else if ((_tokenSet_68.member(LA(1))) && (_tokenSet_67.member(LA(2)))) {
				{
				switch ( LA(1)) {
				case QUASIOPEN:
				case URI:
				case URIGetter:
				case HEX:
				case OCTAL:
				case DOC_COMMENT:
				case CHAR_LITERAL:
				case STRING:
				case IDENT:
				case INT:
				case FLOAT64:
				case LITERAL_meta:
				case LITERAL_if:
				case LITERAL_for:
				case LITERAL_accum:
				case LITERAL_while:
				case 97:
				case 99:
				case 100:
				case 101:
				case LITERAL_when:
				case LITERAL_escape:
				case LITERAL_thunk:
				case LITERAL_fn:
				case LITERAL_switch:
				case LITERAL_try:
				case LITERAL_bind:
				case LITERAL_var:
				case LITERAL_def:
				case 116:
				case LITERAL_interface:
				case LITERAL_break:
				case LITERAL_continue:
				case LITERAL_return:
				case 145:
				case 162:
				case 168:
				case 169:
				case 170:
				case 173:
				case 176:
				case 177:
				case 178:
				case 179:
				case 180:
				{
					eExpr();
					astFactory.addASTChild(currentAST, returnAST);
					br();
					astFactory.addASTChild(currentAST, returnAST);
					{
					switch ( LA(1)) {
					case 117:
					{
						match(117);
						argList();
						astFactory.addASTChild(currentAST, returnAST);
						break;
					}
					case 118:
					case 171:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					break;
				}
				case 118:
				case 171:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				argList_AST = (AST)currentAST.root;
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}

		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_69);
			} else {
			  throw ex;
			}
		}
		returnAST = argList_AST;
	}

	public final void prop() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST prop_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case IDENT:
			{
				AST tmp188_AST = null;
				tmp188_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp188_AST);
				match(IDENT);
				prop_AST = (AST)currentAST.root;
				break;
			}
			case STRING:
			{
				AST tmp189_AST = null;
				tmp189_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp189_AST);
				match(STRING);
				prop_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_70);
			} else {
			  throw ex;
			}
		}
		returnAST = prop_AST;
	}

	public final void lambdaArgs() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lambdaArgs_AST = null;

		try {      // for error handling
			match(116);
			argList();
			astFactory.addASTChild(currentAST, returnAST);
			match(118);
			{
			switch ( LA(1)) {
			case IDENT:
			case LITERAL_else:
			case 97:
			case 104:
			case LITERAL_finally:
			case LITERAL_escape:
			case LITERAL_thunk:
			case LITERAL_fn:
			case LITERAL_try:
			case LITERAL_guards:
			case LITERAL_catch:
			case LITERAL_abstract:
			case LITERAL_an:
			case LITERAL_as:
			case LITERAL_assert:
			case LITERAL_attribute:
			case LITERAL_be:
			case LITERAL_begin:
			case LITERAL_behalf:
			case LITERAL_belief:
			case LITERAL_believe:
			case LITERAL_believes:
			case LITERAL_case:
			case LITERAL_class:
			case LITERAL_const:
			case LITERAL_constructor:
			case LITERAL_datatype:
			case LITERAL_declare:
			case LITERAL_default:
			case LITERAL_define:
			case LITERAL_defmacro:
			case LITERAL_delicate:
			case LITERAL_deprecated:
			case LITERAL_dispatch:
			case LITERAL_do:
			case LITERAL_encapsulate:
			case LITERAL_encapsulated:
			case LITERAL_encapsulates:
			case LITERAL_end:
			case LITERAL_ensure:
			case LITERAL_enum:
			case LITERAL_eventual:
			case LITERAL_eventually:
			case LITERAL_export:
			case LITERAL_facet:
			case LITERAL_forall:
			case LITERAL_fun:
			case LITERAL_function:
			case LITERAL_given:
			case LITERAL_hidden:
			case LITERAL_hides:
			case LITERAL_inline:
			case LITERAL_know:
			case LITERAL_knows:
			case LITERAL_lambda:
			case LITERAL_let:
			case LITERAL_methods:
			case LITERAL_namespace:
			case LITERAL_native:
			case LITERAL_obeys:
			case LITERAL_octet:
			case LITERAL_oneway:
			case LITERAL_operator:
			case LITERAL_package:
			case LITERAL_private:
			case LITERAL_protected:
			case LITERAL_public:
			case LITERAL_raises:
			case LITERAL_reliance:
			case LITERAL_reliant:
			case LITERAL_relies:
			case LITERAL_rely:
			case LITERAL_reveal:
			case LITERAL_sake:
			case LITERAL_signed:
			case LITERAL_static:
			case LITERAL_struct:
			case LITERAL_suchthat:
			case LITERAL_supports:
			case LITERAL_suspect:
			case LITERAL_suspects:
			case LITERAL_synchronized:
			case LITERAL_this:
			case LITERAL_transient:
			case LITERAL_truncatable:
			case LITERAL_typedef:
			case LITERAL_unsigned:
			case LITERAL_unum:
			case LITERAL_uses:
			case LITERAL_using:
			case 260:
			case 261:
			case LITERAL_virtual:
			case LITERAL_volatile:
			case LITERAL_wstring:
			{
				sepword();
				body();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			lambdaArgs_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = lambdaArgs_AST;
	}

	public final void sepword() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST sepword_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case LITERAL_catch:
			{
				AST tmp192_AST = null;
				tmp192_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp192_AST);
				match(LITERAL_catch);
				sepword_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_else:
			{
				AST tmp193_AST = null;
				tmp193_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp193_AST);
				match(LITERAL_else);
				sepword_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_escape:
			{
				AST tmp194_AST = null;
				tmp194_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp194_AST);
				match(LITERAL_escape);
				sepword_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_finally:
			{
				AST tmp195_AST = null;
				tmp195_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp195_AST);
				match(LITERAL_finally);
				sepword_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_guards:
			{
				AST tmp196_AST = null;
				tmp196_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp196_AST);
				match(LITERAL_guards);
				sepword_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_thunk:
			{
				AST tmp197_AST = null;
				tmp197_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp197_AST);
				match(LITERAL_thunk);
				sepword_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_fn:
			{
				AST tmp198_AST = null;
				tmp198_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp198_AST);
				match(LITERAL_fn);
				sepword_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_try:
			{
				AST tmp199_AST = null;
				tmp199_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp199_AST);
				match(LITERAL_try);
				sepword_AST = (AST)currentAST.root;
				break;
			}
			case IDENT:
			{
				AST tmp200_AST = null;
				tmp200_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp200_AST);
				match(IDENT);
				sepword_AST = (AST)currentAST.root;
				break;
			}
			case 97:
			case LITERAL_abstract:
			case LITERAL_an:
			case LITERAL_as:
			case LITERAL_assert:
			case LITERAL_attribute:
			case LITERAL_be:
			case LITERAL_begin:
			case LITERAL_behalf:
			case LITERAL_belief:
			case LITERAL_believe:
			case LITERAL_believes:
			case LITERAL_case:
			case LITERAL_class:
			case LITERAL_const:
			case LITERAL_constructor:
			case LITERAL_datatype:
			case LITERAL_declare:
			case LITERAL_default:
			case LITERAL_define:
			case LITERAL_defmacro:
			case LITERAL_delicate:
			case LITERAL_deprecated:
			case LITERAL_dispatch:
			case LITERAL_do:
			case LITERAL_encapsulate:
			case LITERAL_encapsulated:
			case LITERAL_encapsulates:
			case LITERAL_end:
			case LITERAL_ensure:
			case LITERAL_enum:
			case LITERAL_eventual:
			case LITERAL_eventually:
			case LITERAL_export:
			case LITERAL_facet:
			case LITERAL_forall:
			case LITERAL_fun:
			case LITERAL_function:
			case LITERAL_given:
			case LITERAL_hidden:
			case LITERAL_hides:
			case LITERAL_inline:
			case LITERAL_know:
			case LITERAL_knows:
			case LITERAL_lambda:
			case LITERAL_let:
			case LITERAL_methods:
			case LITERAL_namespace:
			case LITERAL_native:
			case LITERAL_obeys:
			case LITERAL_octet:
			case LITERAL_oneway:
			case LITERAL_operator:
			case LITERAL_package:
			case LITERAL_private:
			case LITERAL_protected:
			case LITERAL_public:
			case LITERAL_raises:
			case LITERAL_reliance:
			case LITERAL_reliant:
			case LITERAL_relies:
			case LITERAL_rely:
			case LITERAL_reveal:
			case LITERAL_sake:
			case LITERAL_signed:
			case LITERAL_static:
			case LITERAL_struct:
			case LITERAL_suchthat:
			case LITERAL_supports:
			case LITERAL_suspect:
			case LITERAL_suspects:
			case LITERAL_synchronized:
			case LITERAL_this:
			case LITERAL_transient:
			case LITERAL_truncatable:
			case LITERAL_typedef:
			case LITERAL_unsigned:
			case LITERAL_unum:
			case LITERAL_uses:
			case LITERAL_using:
			case 260:
			case 261:
			case LITERAL_virtual:
			case LITERAL_volatile:
			case LITERAL_wstring:
			{
				reserved();
				astFactory.addASTChild(currentAST, returnAST);
				sepword_AST = (AST)currentAST.root;
				break;
			}
			case 104:
			{
				AST tmp201_AST = null;
				tmp201_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp201_AST);
				match(104);
				sepword_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_44);
			} else {
			  throw ex;
			}
		}
		returnAST = sepword_AST;
	}

	public final void exprList() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST exprList_AST = null;

		try {      // for error handling
			{
			switch ( LA(1)) {
			case QUASIOPEN:
			case URI:
			case URIGetter:
			case HEX:
			case OCTAL:
			case DOC_COMMENT:
			case CHAR_LITERAL:
			case STRING:
			case IDENT:
			case INT:
			case FLOAT64:
			case LITERAL_meta:
			case LITERAL_if:
			case LITERAL_for:
			case LITERAL_accum:
			case LITERAL_while:
			case 97:
			case 99:
			case 100:
			case 101:
			case LITERAL_when:
			case LITERAL_escape:
			case LITERAL_thunk:
			case LITERAL_fn:
			case LITERAL_switch:
			case LITERAL_try:
			case LITERAL_bind:
			case LITERAL_var:
			case LITERAL_def:
			case 116:
			case LITERAL_interface:
			case LITERAL_break:
			case LITERAL_continue:
			case LITERAL_return:
			case 145:
			case 162:
			case 168:
			case 169:
			case 170:
			case 173:
			case 176:
			case 177:
			case 178:
			case 179:
			case 180:
			{
				eExpr();
				astFactory.addASTChild(currentAST, returnAST);
				br();
				astFactory.addASTChild(currentAST, returnAST);
				{
				switch ( LA(1)) {
				case 117:
				{
					match(117);
					exprList();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case 171:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				break;
			}
			case 171:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			exprList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_71);
			} else {
			  throw ex;
			}
		}
		returnAST = exprList_AST;
	}

	public final void mapList() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST mapList_AST = null;

		try {      // for error handling
			{
			switch ( LA(1)) {
			case QUASIOPEN:
			case URI:
			case URIGetter:
			case HEX:
			case OCTAL:
			case DOC_COMMENT:
			case CHAR_LITERAL:
			case STRING:
			case IDENT:
			case INT:
			case FLOAT64:
			case LITERAL_meta:
			case LITERAL_if:
			case LITERAL_for:
			case 94:
			case LITERAL_accum:
			case LITERAL_while:
			case 97:
			case 99:
			case 100:
			case 101:
			case LITERAL_when:
			case LITERAL_escape:
			case LITERAL_thunk:
			case LITERAL_fn:
			case LITERAL_switch:
			case LITERAL_try:
			case LITERAL_bind:
			case LITERAL_var:
			case LITERAL_def:
			case 116:
			case LITERAL_interface:
			case LITERAL_break:
			case LITERAL_continue:
			case LITERAL_return:
			case 145:
			case 162:
			case 168:
			case 169:
			case 170:
			case 173:
			case 176:
			case 177:
			case 178:
			case 179:
			case 180:
			{
				map();
				astFactory.addASTChild(currentAST, returnAST);
				br();
				astFactory.addASTChild(currentAST, returnAST);
				{
				switch ( LA(1)) {
				case 117:
				{
					match(117);
					mapList();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case 118:
				case 171:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				break;
			}
			case 118:
			case 171:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			mapList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_69);
			} else {
			  throw ex;
			}
		}
		returnAST = mapList_AST;
	}

	public final void literal() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST literal_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case STRING:
			{
				AST tmp204_AST = null;
				tmp204_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp204_AST);
				match(STRING);
				literal_AST = (AST)currentAST.root;
				break;
			}
			case CHAR_LITERAL:
			{
				AST tmp205_AST = null;
				tmp205_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp205_AST);
				match(CHAR_LITERAL);
				literal_AST = (AST)currentAST.root;
				break;
			}
			case INT:
			{
				AST tmp206_AST = null;
				tmp206_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp206_AST);
				match(INT);
				literal_AST = (AST)currentAST.root;
				break;
			}
			case FLOAT64:
			{
				AST tmp207_AST = null;
				tmp207_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp207_AST);
				match(FLOAT64);
				literal_AST = (AST)currentAST.root;
				break;
			}
			case HEX:
			{
				AST tmp208_AST = null;
				tmp208_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp208_AST);
				match(HEX);
				literal_AST = (AST)currentAST.root;
				break;
			}
			case OCTAL:
			{
				AST tmp209_AST = null;
				tmp209_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp209_AST);
				match(OCTAL);
				literal_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		returnAST = literal_AST;
	}

	public final void quasiString() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST quasiString_AST = null;

		try {      // for error handling
			match(QUASIOPEN);
			{
			_loop253:
			do {
				switch ( LA(1)) {
				case DOLLARCURLY:
				{
					exprHole();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case ATCURLY:
				{
					pattHole();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case QUASIBODY:
				{
					AST tmp211_AST = null;
					tmp211_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp211_AST);
					match(QUASIBODY);
					break;
				}
				case DOLLARHOLE:
				{
					AST tmp212_AST = null;
					tmp212_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp212_AST);
					match(DOLLARHOLE);
					break;
				}
				case ATHOLE:
				{
					AST tmp213_AST = null;
					tmp213_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp213_AST);
					match(ATHOLE);
					break;
				}
				default:
				{
					break _loop253;
				}
				}
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				quasiString_AST = (AST)currentAST.root;
				quasiString_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(QuasiContent)).add(quasiString_AST));
				currentAST.root = quasiString_AST;
				currentAST.child = quasiString_AST!=null &&quasiString_AST.getFirstChild()!=null ?
					quasiString_AST.getFirstChild() : quasiString_AST;
				currentAST.advanceChildToEnd();
			}
			match(QUASICLOSE);
			quasiString_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		returnAST = quasiString_AST;
	}

	public final void map() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST map_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case QUASIOPEN:
			case URI:
			case URIGetter:
			case HEX:
			case OCTAL:
			case DOC_COMMENT:
			case CHAR_LITERAL:
			case STRING:
			case IDENT:
			case INT:
			case FLOAT64:
			case LITERAL_meta:
			case LITERAL_if:
			case LITERAL_for:
			case LITERAL_accum:
			case LITERAL_while:
			case 97:
			case 99:
			case 100:
			case 101:
			case LITERAL_when:
			case LITERAL_escape:
			case LITERAL_thunk:
			case LITERAL_fn:
			case LITERAL_switch:
			case LITERAL_try:
			case LITERAL_bind:
			case LITERAL_var:
			case LITERAL_def:
			case 116:
			case LITERAL_interface:
			case LITERAL_break:
			case LITERAL_continue:
			case LITERAL_return:
			case 145:
			case 162:
			case 168:
			case 169:
			case 170:
			case 173:
			case 176:
			case 177:
			case 178:
			case 179:
			case 180:
			{
				eExpr();
				astFactory.addASTChild(currentAST, returnAST);
				AST tmp215_AST = null;
				tmp215_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp215_AST);
				match(94);
				eExpr();
				astFactory.addASTChild(currentAST, returnAST);
				map_AST = (AST)currentAST.root;
				break;
			}
			case 94:
			{
				AST tmp216_AST = null;
				tmp216_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp216_AST);
				match(94);
				{
				switch ( LA(1)) {
				case URIGetter:
				case IDENT:
				case 173:
				case 176:
				case 177:
				case 178:
				case 179:
				case 180:
				{
					nounExpr();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case 101:
				{
					AST tmp217_AST = null;
					tmp217_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp217_AST);
					match(101);
					nounExpr();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case LITERAL_def:
				{
					AST tmp218_AST = null;
					tmp218_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp218_AST);
					match(LITERAL_def);
					nounExpr();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				map_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_72);
			} else {
			  throw ex;
			}
		}
		returnAST = map_AST;
	}

	public final void subPattern() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST subPattern_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case URIGetter:
			case IDENT:
			case 173:
			case 176:
			case 177:
			case 178:
			case 179:
			case 180:
			{
				nounExpr();
				astFactory.addASTChild(currentAST, returnAST);
				{
				switch ( LA(1)) {
				case EOF:
				case RCURLY:
				case LINESEP:
				case STRING:
				case IDENT:
				case 86:
				case LITERAL_in:
				case 94:
				case 97:
				case 106:
				case 115:
				case 117:
				case 118:
				case LITERAL_extends:
				case LITERAL_implements:
				case 129:
				case 130:
				case 131:
				case 132:
				case 133:
				case 134:
				case 135:
				case 136:
				case 137:
				case 138:
				case 139:
				case 140:
				case 141:
				case 146:
				case 147:
				case 171:
				case 175:
				{
					{
					switch ( LA(1)) {
					case 106:
					{
						match(106);
						guard();
						astFactory.addASTChild(currentAST, returnAST);
						break;
					}
					case EOF:
					case RCURLY:
					case LINESEP:
					case STRING:
					case IDENT:
					case 86:
					case LITERAL_in:
					case 94:
					case 97:
					case 115:
					case 117:
					case 118:
					case LITERAL_extends:
					case LITERAL_implements:
					case 129:
					case 130:
					case 131:
					case 132:
					case 133:
					case 134:
					case 135:
					case 136:
					case 137:
					case 138:
					case 139:
					case 140:
					case 141:
					case 146:
					case 147:
					case 171:
					case 175:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					if ( inputState.guessing==0 ) {
						subPattern_AST = (AST)currentAST.root;
						subPattern_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FinalPattern)).add(subPattern_AST));
						currentAST.root = subPattern_AST;
						currentAST.child = subPattern_AST!=null &&subPattern_AST.getFirstChild()!=null ?
							subPattern_AST.getFirstChild() : subPattern_AST;
						currentAST.advanceChildToEnd();
					}
					break;
				}
				case QUASIOPEN:
				{
					quasiString();
					astFactory.addASTChild(currentAST, returnAST);
					if ( inputState.guessing==0 ) {
						subPattern_AST = (AST)currentAST.root;
						subPattern_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(QuasiLiteralPattern)).add(subPattern_AST));
						currentAST.root = subPattern_AST;
						currentAST.child = subPattern_AST!=null &&subPattern_AST.getFirstChild()!=null ?
							subPattern_AST.getFirstChild() : subPattern_AST;
						currentAST.advanceChildToEnd();
					}
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				subPattern_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL__:
			{
				AST tmp220_AST = null;
				tmp220_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp220_AST);
				match(LITERAL__);
				{
				switch ( LA(1)) {
				case 106:
				{
					match(106);
					guard();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case EOF:
				case RCURLY:
				case LINESEP:
				case STRING:
				case IDENT:
				case 86:
				case LITERAL_in:
				case 94:
				case 97:
				case 115:
				case 117:
				case 118:
				case LITERAL_extends:
				case LITERAL_implements:
				case 129:
				case 130:
				case 131:
				case 132:
				case 133:
				case 134:
				case 135:
				case 136:
				case 137:
				case 138:
				case 139:
				case 140:
				case 141:
				case 146:
				case 147:
				case 171:
				case 175:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				if ( inputState.guessing==0 ) {
					subPattern_AST = (AST)currentAST.root;
					subPattern_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(IgnorePattern)).add(subPattern_AST));
					currentAST.root = subPattern_AST;
					currentAST.child = subPattern_AST!=null &&subPattern_AST.getFirstChild()!=null ?
						subPattern_AST.getFirstChild() : subPattern_AST;
					currentAST.advanceChildToEnd();
				}
				subPattern_AST = (AST)currentAST.root;
				break;
			}
			case 106:
			{
				AST tmp222_AST = null;
				tmp222_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp222_AST);
				match(106);
				guard();
				astFactory.addASTChild(currentAST, returnAST);
				subPattern_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_bind:
			{
				bindNamer();
				astFactory.addASTChild(currentAST, returnAST);
				subPattern_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_var:
			{
				varNamer();
				astFactory.addASTChild(currentAST, returnAST);
				subPattern_AST = (AST)currentAST.root;
				break;
			}
			case 101:
			{
				slotNamer();
				astFactory.addASTChild(currentAST, returnAST);
				subPattern_AST = (AST)currentAST.root;
				break;
			}
			case QUASIOPEN:
			{
				quasiString();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					subPattern_AST = (AST)currentAST.root;
					subPattern_AST=(AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(QuasiLiteralPattern,"simple")).add(astFactory.create(STRING,"simple")).add(subPattern_AST));
					currentAST.root = subPattern_AST;
					currentAST.child = subPattern_AST!=null &&subPattern_AST.getFirstChild()!=null ?
						subPattern_AST.getFirstChild() : subPattern_AST;
					currentAST.advanceChildToEnd();
				}
				subPattern_AST = (AST)currentAST.root;
				break;
			}
			case 148:
			{
				AST tmp223_AST = null;
				tmp223_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp223_AST);
				match(148);
				prim();
				astFactory.addASTChild(currentAST, returnAST);
				subPattern_AST = (AST)currentAST.root;
				break;
			}
			case 149:
			{
				AST tmp224_AST = null;
				tmp224_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp224_AST);
				match(149);
				prim();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					warn("reserved: != pattern");
				}
				subPattern_AST = (AST)currentAST.root;
				break;
			}
			case 153:
			case 154:
			case 155:
			case 156:
			case 157:
			{
				compareOp();
				astFactory.addASTChild(currentAST, returnAST);
				prim();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					warn("reserved: compareOp pattern");
				}
				subPattern_AST = (AST)currentAST.root;
				break;
			}
			default:
				boolean synPredMatched233 = false;
				if (((LA(1)==170) && (_tokenSet_73.member(LA(2))))) {
					int _m233 = mark();
					synPredMatched233 = true;
					inputState.guessing++;
					try {
						{
						match(170);
						{
						switch ( LA(1)) {
						case HEX:
						case OCTAL:
						case CHAR_LITERAL:
						case STRING:
						case INT:
						case FLOAT64:
						case 116:
						{
							key();
							break;
						}
						case 94:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						match(94);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched233 = false;
					}
					rewind(_m233);
					inputState.guessing--;
				}
				if ( synPredMatched233 ) {
					AST tmp225_AST = null;
					tmp225_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp225_AST);
					match(170);
					mapPattList();
					astFactory.addASTChild(currentAST, returnAST);
					br();
					astFactory.addASTChild(currentAST, returnAST);
					match(171);
					{
					switch ( LA(1)) {
					case 102:
					{
						AST tmp227_AST = null;
						tmp227_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp227_AST);
						match(102);
						subPattern();
						astFactory.addASTChild(currentAST, returnAST);
						break;
					}
					case EOF:
					case RCURLY:
					case LINESEP:
					case STRING:
					case IDENT:
					case 86:
					case LITERAL_in:
					case 94:
					case 97:
					case 115:
					case 117:
					case 118:
					case LITERAL_extends:
					case LITERAL_implements:
					case 129:
					case 130:
					case 131:
					case 132:
					case 133:
					case 134:
					case 135:
					case 136:
					case 137:
					case 138:
					case 139:
					case 140:
					case 141:
					case 146:
					case 147:
					case 171:
					case 175:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					if ( inputState.guessing==0 ) {
						subPattern_AST = (AST)currentAST.root;
						subPattern_AST.setType(MapPattern);
					}
					subPattern_AST = (AST)currentAST.root;
				}
				else if ((LA(1)==170) && (_tokenSet_74.member(LA(2)))) {
					AST tmp228_AST = null;
					tmp228_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp228_AST);
					match(170);
					patternList();
					astFactory.addASTChild(currentAST, returnAST);
					br();
					astFactory.addASTChild(currentAST, returnAST);
					match(171);
					{
					switch ( LA(1)) {
					case EOF:
					case RCURLY:
					case LINESEP:
					case STRING:
					case IDENT:
					case 86:
					case LITERAL_in:
					case 94:
					case 97:
					case 106:
					case 115:
					case 117:
					case 118:
					case LITERAL_extends:
					case LITERAL_implements:
					case 129:
					case 130:
					case 131:
					case 132:
					case 133:
					case 134:
					case 135:
					case 136:
					case 137:
					case 138:
					case 139:
					case 140:
					case 141:
					case 146:
					case 147:
					case 171:
					case 175:
					{
						{
						switch ( LA(1)) {
						case 106:
						{
							match(106);
							guard();
							astFactory.addASTChild(currentAST, returnAST);
							break;
						}
						case EOF:
						case RCURLY:
						case LINESEP:
						case STRING:
						case IDENT:
						case 86:
						case LITERAL_in:
						case 94:
						case 97:
						case 115:
						case 117:
						case 118:
						case LITERAL_extends:
						case LITERAL_implements:
						case 129:
						case 130:
						case 131:
						case 132:
						case 133:
						case 134:
						case 135:
						case 136:
						case 137:
						case 138:
						case 139:
						case 140:
						case 141:
						case 146:
						case 147:
						case 171:
						case 175:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						if ( inputState.guessing==0 ) {
							subPattern_AST = (AST)currentAST.root;
							subPattern_AST.setType(ListPattern);
						}
						break;
					}
					case 99:
					{
						{
						match(99);
						subPattern();
						astFactory.addASTChild(currentAST, returnAST);
						}
						if ( inputState.guessing==0 ) {
							subPattern_AST = (AST)currentAST.root;
							subPattern_AST.setType(CdrPattern);
						}
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					subPattern_AST = (AST)currentAST.root;
				}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_29);
			} else {
			  throw ex;
			}
		}
		returnAST = subPattern_AST;
	}

	public final void namePatt() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST namePatt_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case URIGetter:
			case IDENT:
			case 173:
			case 176:
			case 177:
			case 178:
			case 179:
			case 180:
			{
				nounExpr();
				astFactory.addASTChild(currentAST, returnAST);
				{
				switch ( LA(1)) {
				case 106:
				{
					match(106);
					guard();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case LINESEP:
				case 97:
				case 115:
				case 117:
				case 118:
				case 171:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				if ( inputState.guessing==0 ) {
					namePatt_AST = (AST)currentAST.root;
					namePatt_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FinalPattern)).add(namePatt_AST));
					currentAST.root = namePatt_AST;
					currentAST.child = namePatt_AST!=null &&namePatt_AST.getFirstChild()!=null ?
						namePatt_AST.getFirstChild() : namePatt_AST;
					currentAST.advanceChildToEnd();
				}
				namePatt_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_bind:
			{
				bindNamer();
				astFactory.addASTChild(currentAST, returnAST);
				namePatt_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_var:
			{
				varNamer();
				astFactory.addASTChild(currentAST, returnAST);
				namePatt_AST = (AST)currentAST.root;
				break;
			}
			case 101:
			{
				slotNamer();
				astFactory.addASTChild(currentAST, returnAST);
				namePatt_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_75);
			} else {
			  throw ex;
			}
		}
		returnAST = namePatt_AST;
	}

	public final void dollarHole() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dollarHole_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case 176:
			{
				AST tmp233_AST = null;
				tmp233_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp233_AST);
				match(176);
				AST tmp234_AST = null;
				tmp234_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp234_AST);
				match(POSINT);
				AST tmp235_AST = null;
				tmp235_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp235_AST);
				match(RCURLY);
				dollarHole_AST = (AST)currentAST.root;
				break;
			}
			case 177:
			{
				AST tmp236_AST = null;
				tmp236_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp236_AST);
				match(177);
				AST tmp237_AST = null;
				tmp237_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp237_AST);
				match(POSINT);
				dollarHole_AST = (AST)currentAST.root;
				break;
			}
			case 178:
			{
				AST tmp238_AST = null;
				tmp238_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp238_AST);
				match(178);
				dollarHole_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_30);
			} else {
			  throw ex;
			}
		}
		returnAST = dollarHole_AST;
	}

	public final void atHole() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST atHole_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case 179:
			{
				AST tmp239_AST = null;
				tmp239_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp239_AST);
				match(179);
				AST tmp240_AST = null;
				tmp240_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp240_AST);
				match(POSINT);
				match(RCURLY);
				atHole_AST = (AST)currentAST.root;
				break;
			}
			case 180:
			{
				AST tmp242_AST = null;
				tmp242_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp242_AST);
				match(180);
				AST tmp243_AST = null;
				tmp243_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp243_AST);
				match(POSINT);
				atHole_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_30);
			} else {
			  throw ex;
			}
		}
		returnAST = atHole_AST;
	}

	public final void mapPattern() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST mapPattern_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case HEX:
			case OCTAL:
			case CHAR_LITERAL:
			case STRING:
			case INT:
			case FLOAT64:
			case 116:
			{
				key();
				astFactory.addASTChild(currentAST, returnAST);
				AST tmp244_AST = null;
				tmp244_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp244_AST);
				match(94);
				pattern();
				astFactory.addASTChild(currentAST, returnAST);
				{
				switch ( LA(1)) {
				case 115:
				{
					AST tmp245_AST = null;
					tmp245_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp245_AST);
					match(115);
					order();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case LINESEP:
				case 97:
				case 117:
				case 118:
				case 171:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				mapPattern_AST = (AST)currentAST.root;
				break;
			}
			case 94:
			{
				AST tmp246_AST = null;
				tmp246_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp246_AST);
				match(94);
				namePatt();
				astFactory.addASTChild(currentAST, returnAST);
				{
				switch ( LA(1)) {
				case 115:
				{
					AST tmp247_AST = null;
					tmp247_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp247_AST);
					match(115);
					order();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case LINESEP:
				case 97:
				case 117:
				case 118:
				case 171:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				mapPattern_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_76);
			} else {
			  throw ex;
			}
		}
		returnAST = mapPattern_AST;
	}

	public final void exprHole() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST exprHole_AST = null;

		try {      // for error handling
			AST tmp248_AST = null;
			tmp248_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp248_AST);
			match(DOLLARCURLY);
			br();
			astFactory.addASTChild(currentAST, returnAST);
			eExpr();
			astFactory.addASTChild(currentAST, returnAST);
			br();
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				exprHole_AST = (AST)currentAST.root;
				exprHole_AST.setType(DOLLARHOLE);
			}
			match(RCURLY);
			exprHole_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_77);
			} else {
			  throw ex;
			}
		}
		returnAST = exprHole_AST;
	}

	public final void pattHole() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST pattHole_AST = null;

		try {      // for error handling
			match(ATCURLY);
			br();
			astFactory.addASTChild(currentAST, returnAST);
			pattern();
			astFactory.addASTChild(currentAST, returnAST);
			br();
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				pattHole_AST = (AST)currentAST.root;
				pattHole_AST.setType(ATHOLE);
			}
			match(RCURLY);
			pattHole_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_77);
			} else {
			  throw ex;
			}
		}
		returnAST = pattHole_AST;
	}

	public final void reserved() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST reserved_AST = null;

		try {      // for error handling
			switch ( LA(1)) {
			case 97:
			{
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_abstract:
			{
				AST tmp252_AST = null;
				tmp252_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp252_AST);
				match(LITERAL_abstract);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_an:
			{
				AST tmp253_AST = null;
				tmp253_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp253_AST);
				match(LITERAL_an);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_as:
			{
				AST tmp254_AST = null;
				tmp254_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp254_AST);
				match(LITERAL_as);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_assert:
			{
				AST tmp255_AST = null;
				tmp255_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp255_AST);
				match(LITERAL_assert);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_attribute:
			{
				AST tmp256_AST = null;
				tmp256_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp256_AST);
				match(LITERAL_attribute);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_be:
			{
				AST tmp257_AST = null;
				tmp257_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp257_AST);
				match(LITERAL_be);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_begin:
			{
				AST tmp258_AST = null;
				tmp258_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp258_AST);
				match(LITERAL_begin);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_behalf:
			{
				AST tmp259_AST = null;
				tmp259_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp259_AST);
				match(LITERAL_behalf);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_belief:
			{
				AST tmp260_AST = null;
				tmp260_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp260_AST);
				match(LITERAL_belief);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_believe:
			{
				AST tmp261_AST = null;
				tmp261_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp261_AST);
				match(LITERAL_believe);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_believes:
			{
				AST tmp262_AST = null;
				tmp262_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp262_AST);
				match(LITERAL_believes);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_case:
			{
				AST tmp263_AST = null;
				tmp263_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp263_AST);
				match(LITERAL_case);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_class:
			{
				AST tmp264_AST = null;
				tmp264_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp264_AST);
				match(LITERAL_class);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_const:
			{
				AST tmp265_AST = null;
				tmp265_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp265_AST);
				match(LITERAL_const);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_constructor:
			{
				AST tmp266_AST = null;
				tmp266_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp266_AST);
				match(LITERAL_constructor);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_datatype:
			{
				AST tmp267_AST = null;
				tmp267_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp267_AST);
				match(LITERAL_datatype);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_declare:
			{
				AST tmp268_AST = null;
				tmp268_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp268_AST);
				match(LITERAL_declare);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_default:
			{
				AST tmp269_AST = null;
				tmp269_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp269_AST);
				match(LITERAL_default);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_define:
			{
				AST tmp270_AST = null;
				tmp270_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp270_AST);
				match(LITERAL_define);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_defmacro:
			{
				AST tmp271_AST = null;
				tmp271_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp271_AST);
				match(LITERAL_defmacro);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_delicate:
			{
				AST tmp272_AST = null;
				tmp272_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp272_AST);
				match(LITERAL_delicate);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_deprecated:
			{
				AST tmp273_AST = null;
				tmp273_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp273_AST);
				match(LITERAL_deprecated);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_dispatch:
			{
				AST tmp274_AST = null;
				tmp274_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp274_AST);
				match(LITERAL_dispatch);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_do:
			{
				AST tmp275_AST = null;
				tmp275_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp275_AST);
				match(LITERAL_do);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_encapsulate:
			{
				AST tmp276_AST = null;
				tmp276_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp276_AST);
				match(LITERAL_encapsulate);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_encapsulated:
			{
				AST tmp277_AST = null;
				tmp277_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp277_AST);
				match(LITERAL_encapsulated);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_encapsulates:
			{
				AST tmp278_AST = null;
				tmp278_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp278_AST);
				match(LITERAL_encapsulates);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_end:
			{
				AST tmp279_AST = null;
				tmp279_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp279_AST);
				match(LITERAL_end);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_ensure:
			{
				AST tmp280_AST = null;
				tmp280_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp280_AST);
				match(LITERAL_ensure);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_enum:
			{
				AST tmp281_AST = null;
				tmp281_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp281_AST);
				match(LITERAL_enum);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_eventual:
			{
				AST tmp282_AST = null;
				tmp282_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp282_AST);
				match(LITERAL_eventual);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_eventually:
			{
				AST tmp283_AST = null;
				tmp283_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp283_AST);
				match(LITERAL_eventually);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_export:
			{
				AST tmp284_AST = null;
				tmp284_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp284_AST);
				match(LITERAL_export);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_facet:
			{
				AST tmp285_AST = null;
				tmp285_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp285_AST);
				match(LITERAL_facet);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_forall:
			{
				AST tmp286_AST = null;
				tmp286_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp286_AST);
				match(LITERAL_forall);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_fun:
			{
				AST tmp287_AST = null;
				tmp287_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp287_AST);
				match(LITERAL_fun);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_function:
			{
				AST tmp288_AST = null;
				tmp288_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp288_AST);
				match(LITERAL_function);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_given:
			{
				AST tmp289_AST = null;
				tmp289_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp289_AST);
				match(LITERAL_given);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_hidden:
			{
				AST tmp290_AST = null;
				tmp290_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp290_AST);
				match(LITERAL_hidden);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_hides:
			{
				AST tmp291_AST = null;
				tmp291_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp291_AST);
				match(LITERAL_hides);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_inline:
			{
				AST tmp292_AST = null;
				tmp292_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp292_AST);
				match(LITERAL_inline);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_know:
			{
				AST tmp293_AST = null;
				tmp293_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp293_AST);
				match(LITERAL_know);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_knows:
			{
				AST tmp294_AST = null;
				tmp294_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp294_AST);
				match(LITERAL_knows);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_lambda:
			{
				AST tmp295_AST = null;
				tmp295_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp295_AST);
				match(LITERAL_lambda);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_let:
			{
				AST tmp296_AST = null;
				tmp296_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp296_AST);
				match(LITERAL_let);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_methods:
			{
				AST tmp297_AST = null;
				tmp297_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp297_AST);
				match(LITERAL_methods);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_namespace:
			{
				AST tmp298_AST = null;
				tmp298_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp298_AST);
				match(LITERAL_namespace);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_native:
			{
				AST tmp299_AST = null;
				tmp299_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp299_AST);
				match(LITERAL_native);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_obeys:
			{
				AST tmp300_AST = null;
				tmp300_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp300_AST);
				match(LITERAL_obeys);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_octet:
			{
				AST tmp301_AST = null;
				tmp301_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp301_AST);
				match(LITERAL_octet);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_oneway:
			{
				AST tmp302_AST = null;
				tmp302_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp302_AST);
				match(LITERAL_oneway);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_operator:
			{
				AST tmp303_AST = null;
				tmp303_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp303_AST);
				match(LITERAL_operator);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_package:
			{
				AST tmp304_AST = null;
				tmp304_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp304_AST);
				match(LITERAL_package);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_private:
			{
				AST tmp305_AST = null;
				tmp305_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp305_AST);
				match(LITERAL_private);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_protected:
			{
				AST tmp306_AST = null;
				tmp306_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp306_AST);
				match(LITERAL_protected);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_public:
			{
				AST tmp307_AST = null;
				tmp307_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp307_AST);
				match(LITERAL_public);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_raises:
			{
				AST tmp308_AST = null;
				tmp308_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp308_AST);
				match(LITERAL_raises);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_reliance:
			{
				AST tmp309_AST = null;
				tmp309_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp309_AST);
				match(LITERAL_reliance);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_reliant:
			{
				AST tmp310_AST = null;
				tmp310_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp310_AST);
				match(LITERAL_reliant);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_relies:
			{
				AST tmp311_AST = null;
				tmp311_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp311_AST);
				match(LITERAL_relies);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_rely:
			{
				AST tmp312_AST = null;
				tmp312_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp312_AST);
				match(LITERAL_rely);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_reveal:
			{
				AST tmp313_AST = null;
				tmp313_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp313_AST);
				match(LITERAL_reveal);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_sake:
			{
				AST tmp314_AST = null;
				tmp314_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp314_AST);
				match(LITERAL_sake);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_signed:
			{
				AST tmp315_AST = null;
				tmp315_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp315_AST);
				match(LITERAL_signed);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_static:
			{
				AST tmp316_AST = null;
				tmp316_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp316_AST);
				match(LITERAL_static);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_struct:
			{
				AST tmp317_AST = null;
				tmp317_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp317_AST);
				match(LITERAL_struct);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_suchthat:
			{
				AST tmp318_AST = null;
				tmp318_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp318_AST);
				match(LITERAL_suchthat);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_supports:
			{
				AST tmp319_AST = null;
				tmp319_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp319_AST);
				match(LITERAL_supports);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_suspect:
			{
				AST tmp320_AST = null;
				tmp320_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp320_AST);
				match(LITERAL_suspect);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_suspects:
			{
				AST tmp321_AST = null;
				tmp321_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp321_AST);
				match(LITERAL_suspects);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_synchronized:
			{
				AST tmp322_AST = null;
				tmp322_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp322_AST);
				match(LITERAL_synchronized);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_this:
			{
				AST tmp323_AST = null;
				tmp323_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp323_AST);
				match(LITERAL_this);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_transient:
			{
				AST tmp324_AST = null;
				tmp324_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp324_AST);
				match(LITERAL_transient);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_truncatable:
			{
				AST tmp325_AST = null;
				tmp325_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp325_AST);
				match(LITERAL_truncatable);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_typedef:
			{
				AST tmp326_AST = null;
				tmp326_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp326_AST);
				match(LITERAL_typedef);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_unsigned:
			{
				AST tmp327_AST = null;
				tmp327_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp327_AST);
				match(LITERAL_unsigned);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_unum:
			{
				AST tmp328_AST = null;
				tmp328_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp328_AST);
				match(LITERAL_unum);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_uses:
			{
				AST tmp329_AST = null;
				tmp329_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp329_AST);
				match(LITERAL_uses);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_using:
			{
				AST tmp330_AST = null;
				tmp330_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp330_AST);
				match(LITERAL_using);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case 260:
			{
				AST tmp331_AST = null;
				tmp331_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp331_AST);
				match(260);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case 261:
			{
				AST tmp332_AST = null;
				tmp332_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp332_AST);
				match(261);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_virtual:
			{
				AST tmp333_AST = null;
				tmp333_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp333_AST);
				match(LITERAL_virtual);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_volatile:
			{
				AST tmp334_AST = null;
				tmp334_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp334_AST);
				match(LITERAL_volatile);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_wstring:
			{
				AST tmp335_AST = null;
				tmp335_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp335_AST);
				match(LITERAL_wstring);
				reserved_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_44);
			} else {
			  throw ex;
			}
		}
		returnAST = reserved_AST;
	}


	static public final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"QUASIOPEN",
		"QUASICLOSE",
		"QUASIBODY",
		"QuasiContent",
		"\"}\"",
		"DOLLARHOLE",
		"ATHOLE",
		"DOLLARCURLY",
		"ATCURLY",
		"DOLLARESC",
		"AssignExpr",
		"CallExpr",
		"IntoExpr",
		"EscapeExpr",
		"HideExpr",
		"IfExpr",
		"ForExpr",
		"WhenExpr",
		"AndExpr",
		"OrExpr",
		"CoerceExpr",
		"LiteralExpr",
		"MatchBindExpr",
		"NounExpr",
		"ObjectExpr",
		"InterfaceExpr",
		"QuasiLiteralExpr",
		"QuasiPatternExpr",
		"MetaStateExpr",
		"MetaContextExpr",
		"SeqExpr",
		"SlotExpr",
		"MetaExpr",
		"CatchExpr",
		"FinallyExpr",
		"ReturnExpr",
		"ContinueExpr",
		"BreakExpr",
		"WhileExpr",
		"SwitchExpr",
		"TryExpr",
		"MapPattern",
		"LiteralPattern",
		"TupleExpr",
		"MapExpr",
		"BindPattern",
		"SendExpr",
		"CurryExpr",
		"FinalPattern",
		"VarPattern",
		"SlotPattern",
		"ListPattern",
		"CdrPattern",
		"IgnorePattern",
		"SuchThatPattern",
		"QuasiLiteralPattern",
		"QuasiPatternPattern",
		"URI",
		"URIStart",
		"URIGetter",
		"URIExpr",
		"LambdaExpr",
		"EScript",
		"EMethod",
		"EMatcher",
		"List",
		"WhenFn",
		"HEX",
		"OCTAL",
		"WS",
		"LINESEP",
		"SL_COMMENT",
		"DOC_COMMENT",
		"CHAR_LITERAL",
		"STRING",
		"ESC",
		"HEX_DIGIT",
		"IDENT",
		"INT",
		"POSINT",
		"FLOAT64",
		"EXPONENT",
		"\";\"",
		"\"pragma\"",
		"\".\"",
		"\"meta\"",
		"\"if\"",
		"\"else\"",
		"\"for\"",
		"\"in\"",
		"\"=>\"",
		"\"accum\"",
		"\"while\"",
		"\"{\"",
		"\"_\"",
		"\"+\"",
		"\"*\"",
		"\"&\"",
		"\"|\"",
		"\"when\"",
		"\"->\"",
		"\"finally\"",
		"\":\"",
		"\"escape\"",
		"\"thunk\"",
		"\"fn\"",
		"\"switch\"",
		"\"try\"",
		"\"bind\"",
		"\"var\"",
		"\"def\"",
		"\":=\"",
		"\"(\"",
		"\",\"",
		"\")\"",
		"\"extends\"",
		"\"implements\"",
		"\"to\"",
		"\"method\"",
		"\"on\"",
		"\"match\"",
		"\"throws\"",
		"\"interface\"",
		"\"guards\"",
		"\"=\"",
		"\"//=\"",
		"\"+=\"",
		"\"-=\"",
		"\"*=\"",
		"\"/=\"",
		"\"%=\"",
		"\"%%=\"",
		"\"**=\"",
		"\">>=\"",
		"\"<<=\"",
		"\"&=\"",
		"\"^=\"",
		"\"|=\"",
		"\"break\"",
		"\"continue\"",
		"\"return\"",
		"\"^\"",
		"\"||\"",
		"\"&&\"",
		"\"==\"",
		"\"!=\"",
		"\"&!\"",
		"\"=~\"",
		"\"!~\"",
		"\"<\"",
		"\"<=\"",
		"\"<=>\"",
		"\">=\"",
		"\">\"",
		"\"..\"",
		"\"..!\"",
		"\"<<\"",
		"\">>\"",
		"\"-\"",
		"\"/\"",
		"\"//\"",
		"\"%\"",
		"\"%%\"",
		"\"**\"",
		"\"!\"",
		"\"~\"",
		"\"[\"",
		"\"]\"",
		"\"<-\"",
		"\"::\"",
		"\"catch\"",
		"\"?\"",
		"\"${\"",
		"\"$\"",
		"\"$$\"",
		"\"@{\"",
		"\"@\"",
		"\"abstract\"",
		"\"an\"",
		"\"as\"",
		"\"assert\"",
		"\"attribute\"",
		"\"be\"",
		"\"begin\"",
		"\"behalf\"",
		"\"belief\"",
		"\"believe\"",
		"\"believes\"",
		"\"case\"",
		"\"class\"",
		"\"const\"",
		"\"constructor\"",
		"\"datatype\"",
		"\"declare\"",
		"\"default\"",
		"\"define\"",
		"\"defmacro\"",
		"\"delicate\"",
		"\"deprecated\"",
		"\"dispatch\"",
		"\"do\"",
		"\"encapsulate\"",
		"\"encapsulated\"",
		"\"encapsulates\"",
		"\"end\"",
		"\"ensure\"",
		"\"enum\"",
		"\"eventual\"",
		"\"eventually\"",
		"\"export\"",
		"\"facet\"",
		"\"forall\"",
		"\"fun\"",
		"\"function\"",
		"\"given\"",
		"\"hidden\"",
		"\"hides\"",
		"\"inline\"",
		"\"know\"",
		"\"knows\"",
		"\"lambda\"",
		"\"let\"",
		"\"methods\"",
		"\"namespace\"",
		"\"native\"",
		"\"obeys\"",
		"\"octet\"",
		"\"oneway\"",
		"\"operator\"",
		"\"package\"",
		"\"private\"",
		"\"protected\"",
		"\"public\"",
		"\"raises\"",
		"\"reliance\"",
		"\"reliant\"",
		"\"relies\"",
		"\"rely\"",
		"\"reveal\"",
		"\"sake\"",
		"\"signed\"",
		"\"static\"",
		"\"struct\"",
		"\"suchthat\"",
		"\"supports\"",
		"\"suspect\"",
		"\"suspects\"",
		"\"synchronized\"",
		"\"this\"",
		"\"transient\"",
		"\"truncatable\"",
		"\"typedef\"",
		"\"unsigned\"",
		"\"unum\"",
		"\"uses\"",
		"\"using\"",
		"\"utf8\"",
		"\"utf16\"",
		"\"virtual\"",
		"\"volatile\"",
		"\"wstring\""
	};

	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap=null;
	};

	static private final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	static private final long[] mk_tokenSet_1() {
		long[] data = { 0L, 4195328L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	static private final long[] mk_tokenSet_2() {
		long[] data = { 258L, 18014398509481984L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	static private final long[] mk_tokenSet_3() {
		long[] data = { -6917529027641081840L, 4636447827035582848L, 8768622411628544L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	static private final long[] mk_tokenSet_4() {
		long[] data = { 258L, 141868316556936192L, 207807697534974L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	static private final long[] mk_tokenSet_5() {
		long[] data = new long[10];
		data[0]=-6917529027641073806L;
		data[1]=-2722432L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		data[4]=511L;
		return data;
	}
	static public final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	static private final long[] mk_tokenSet_6() {
		long[] data = { -6917529027641081584L, 6807187261580800384L, 8777419547983872L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	static private final long[] mk_tokenSet_7() {
		long[] data = { 258L, 27021598842160128L, 8796093022208L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	static private final long[] mk_tokenSet_8() {
		long[] data = { -6917529027641081840L, 4618433427452359040L, 8768622411382784L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	static private final long[] mk_tokenSet_9() {
		long[] data = { -6917529027641073806L, 4647711516229793152L, 8796093022207998L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	static private final long[] mk_tokenSet_10() {
		long[] data = { 258L, 27021607432094720L, 8796093022208L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	static private final long[] mk_tokenSet_11() {
		long[] data = { 274L, 3456517642301625344L, 207807697534974L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	static private final long[] mk_tokenSet_12() {
		long[] data = { 258L, 2303598336852251648L, 278176441712638L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	static private final long[] mk_tokenSet_13() {
		long[] data = { 0L, 536870912L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	static private final long[] mk_tokenSet_14() {
		long[] data = { 258L, 141870515580191744L, 278176441712638L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	static private final long[] mk_tokenSet_15() {
		long[] data = { 258L, 137359798839690240L, 8796093825022L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	static private final long[] mk_tokenSet_16() {
		long[] data = { 258L, 101335919356953600L, 9895604535294L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	static private final long[] mk_tokenSet_17() {
		long[] data = { 258L, 29273407245927424L, 8796093825022L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	static private final long[] mk_tokenSet_18() {
		long[] data = { 258L, 141868316556936192L, 207807697534975L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
	static private final long[] mk_tokenSet_19() {
		long[] data = { 258L, 141869416068563968L, 207807697534974L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());
	static private final long[] mk_tokenSet_20() {
		long[] data = { 0L, -9110782037580578816L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_20 = new BitSet(mk_tokenSet_20());
	static private final long[] mk_tokenSet_21() {
		long[] data = { 0L, 2305847415850139648L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_21 = new BitSet(mk_tokenSet_21());
	static private final long[] mk_tokenSet_22() {
		long[] data = { 258L, 3456513136528610304L, 149533614817278L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_22 = new BitSet(mk_tokenSet_22());
	static private final long[] mk_tokenSet_23() {
		long[] data = { 0L, 22518007801865600L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_23 = new BitSet(mk_tokenSet_23());
	static private final long[] mk_tokenSet_24() {
		long[] data = { -6917529027641081584L, 6942295234295788928L, 8768622411628544L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_24 = new BitSet(mk_tokenSet_24());
	static private final long[] mk_tokenSet_25() {
		long[] data = { -9223372036854775792L, 18863384695014400L, 8765307739963392L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_25 = new BitSet(mk_tokenSet_25());
	static private final long[] mk_tokenSet_26() {
		long[] data = { -6917529027641073808L, 6951302450730923392L, 8918157036339200L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_26 = new BitSet(mk_tokenSet_26());
	static private final long[] mk_tokenSet_27() {
		long[] data = { 0L, 18014407099417600L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_27 = new BitSet(mk_tokenSet_27());
	static private final long[] mk_tokenSet_28() {
		long[] data = { 256L, 1152921504606848000L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_28 = new BitSet(mk_tokenSet_28());
	static private final long[] mk_tokenSet_29() {
		long[] data = { 258L, 137359798839690240L, 149533582180350L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_29 = new BitSet(mk_tokenSet_29());
	static private final long[] mk_tokenSet_30() {
		long[] data = { 274L, -5766854394553150464L, 207807697534974L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_30 = new BitSet(mk_tokenSet_30());
	static private final long[] mk_tokenSet_31() {
		long[] data = { 0L, 5622515436353486848L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_31 = new BitSet(mk_tokenSet_31());
	static private final long[] mk_tokenSet_32() {
		long[] data = { -9223372036854775808L, 844579549102080L, 8760908650119168L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_32 = new BitSet(mk_tokenSet_32());
	static private final long[] mk_tokenSet_33() {
		long[] data = { -9223372036854775808L, 112589999274868736L, 8760908650119168L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_33 = new BitSet(mk_tokenSet_33());
	static private final long[] mk_tokenSet_34() {
		long[] data = { -9223372036854775792L, 848977595596800L, 8765307739963392L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_34 = new BitSet(mk_tokenSet_34());
	static private final long[] mk_tokenSet_35() {
		long[] data = { -6917529027641074064L, 7824845595108736L, 8914841321340928L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_35 = new BitSet(mk_tokenSet_35());
	static private final long[] mk_tokenSet_36() {
		long[] data = { -9223372036854775808L, 137439100928L, 8760908650119168L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_36 = new BitSet(mk_tokenSet_36());
	static private final long[] mk_tokenSet_37() {
		long[] data = { -9223372036854775550L, 27026005479277568L, 8769704743141376L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_37 = new BitSet(mk_tokenSet_37());
	static private final long[] mk_tokenSet_38() {
		long[] data = { 0L, 1008806316530995200L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_38 = new BitSet(mk_tokenSet_38());
	static private final long[] mk_tokenSet_39() {
		long[] data = { -6917529027641081840L, 4618433427452359040L, 8768622411628544L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_39 = new BitSet(mk_tokenSet_39());
	static private final long[] mk_tokenSet_40() {
		long[] data = { 258L, 141863397930042368L, 8796093022208L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_40 = new BitSet(mk_tokenSet_40());
	static private final long[] mk_tokenSet_41() {
		long[] data = { 258L, 101331413600715776L, 8796126461950L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_41 = new BitSet(mk_tokenSet_41());
	static private final long[] mk_tokenSet_42() {
		long[] data = { 256L, 1008806325120930816L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_42 = new BitSet(mk_tokenSet_42());
	static private final long[] mk_tokenSet_43() {
		long[] data = { 256L, 2161727821137843200L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_43 = new BitSet(mk_tokenSet_43());
	static private final long[] mk_tokenSet_44() {
		long[] data = { 0L, 8589934592L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_44 = new BitSet(mk_tokenSet_44());
	static private final long[] mk_tokenSet_45() {
		long[] data = { 0L, 1073741824L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_45 = new BitSet(mk_tokenSet_45());
	static private final long[] mk_tokenSet_46() {
		long[] data = { 0L, 18014407099417600L, 8796093022208L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_46 = new BitSet(mk_tokenSet_46());
	static private final long[] mk_tokenSet_47() {
		long[] data = { 0L, 1024L, 8796093022208L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_47 = new BitSet(mk_tokenSet_47());
	static private final long[] mk_tokenSet_48() {
		long[] data = { 256L, 1008806316530996224L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_48 = new BitSet(mk_tokenSet_48());
	static private final long[] mk_tokenSet_49() {
		long[] data = { 258L, 3341675331223294976L, 8796093022208L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_49 = new BitSet(mk_tokenSet_49());
	static private final long[] mk_tokenSet_50() {
		long[] data = { 0L, 27021597764224000L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_50 = new BitSet(mk_tokenSet_50());
	static private final long[] mk_tokenSet_51() {
		long[] data = { 0L, 18014398509483008L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_51 = new BitSet(mk_tokenSet_51());
	static private final long[] mk_tokenSet_52() {
		long[] data = { 258L, 29273407245927424L, 8796093038590L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_52 = new BitSet(mk_tokenSet_52());
	static private final long[] mk_tokenSet_53() {
		long[] data = { 258L, 29273407245927424L, 8796093300734L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_53 = new BitSet(mk_tokenSet_53());
	static private final long[] mk_tokenSet_54() {
		long[] data = { 258L, 101335811647226880L, 8797166649342L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_54 = new BitSet(mk_tokenSet_54());
	static private final long[] mk_tokenSet_55() {
		long[] data = { -6917529027641081840L, 4724307629859200L, 8768622411382784L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_55 = new BitSet(mk_tokenSet_55());
	static private final long[] mk_tokenSet_56() {
		long[] data = { 258L, 101335811647226880L, 8800387874814L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_56 = new BitSet(mk_tokenSet_56());
	static private final long[] mk_tokenSet_57() {
		long[] data = { 258L, 101335811647226880L, 8813272776702L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_57 = new BitSet(mk_tokenSet_57());
	static private final long[] mk_tokenSet_58() {
		long[] data = { 0L, 68719476736L, 515396075520L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_58 = new BitSet(mk_tokenSet_58());
	static private final long[] mk_tokenSet_59() {
		long[] data = { 258L, 101335846006965248L, 8830452645886L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_59 = new BitSet(mk_tokenSet_59());
	static private final long[] mk_tokenSet_60() {
		long[] data = { 258L, 101335914726441984L, 9345848721406L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_60 = new BitSet(mk_tokenSet_60());
	static private final long[] mk_tokenSet_61() {
		long[] data = { 258L, 101335914726441984L, 9895604535294L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_61 = new BitSet(mk_tokenSet_61());
	static private final long[] mk_tokenSet_62() {
		long[] data = { -6917529027641081840L, 4724067111690624L, 8765306696630272L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_62 = new BitSet(mk_tokenSet_62());
	static private final long[] mk_tokenSet_63() {
		long[] data = { -6917529027641081840L, 4618433428526100864L, 8777418504650752L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_63 = new BitSet(mk_tokenSet_63());
	static private final long[] mk_tokenSet_64() {
		long[] data = { -6917529027641073806L, 4755797907823555968L, 8936830510563326L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_64 = new BitSet(mk_tokenSet_64());
	static private final long[] mk_tokenSet_65() {
		long[] data = { -6917529027641081840L, 4618433427452359040L, 8777418504650752L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_65 = new BitSet(mk_tokenSet_65());
	static private final long[] mk_tokenSet_66() {
		long[] data = { -6917529027641081840L, 4636447827035582848L, 8777418504650752L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_66 = new BitSet(mk_tokenSet_66());
	static private final long[] mk_tokenSet_67() {
		long[] data = new long[10];
		data[0]=-6917529027641073806L;
		data[1]=-1152921504617433728L;
		data[2]=-2L;
		data[3]=-1L;
		data[4]=511L;
		return data;
	}
	static public final BitSet _tokenSet_67 = new BitSet(mk_tokenSet_67());
	static private final long[] mk_tokenSet_68() {
		long[] data = { -6917529027641081840L, 4636447825961841024L, 8777418504650752L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_68 = new BitSet(mk_tokenSet_68());
	static private final long[] mk_tokenSet_69() {
		long[] data = { 0L, 18014398509481984L, 8796093022208L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_69 = new BitSet(mk_tokenSet_69());
	static private final long[] mk_tokenSet_70() {
		long[] data = { 258L, 105839519001101312L, 67070209179646L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_70 = new BitSet(mk_tokenSet_70());
	static private final long[] mk_tokenSet_71() {
		long[] data = { 0L, 0L, 8796093022208L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_71 = new BitSet(mk_tokenSet_71());
	static private final long[] mk_tokenSet_72() {
		long[] data = { 0L, 27021597764224000L, 8796093022208L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_72 = new BitSet(mk_tokenSet_72());
	static private final long[] mk_tokenSet_73() {
		long[] data = { 0L, 4503600702449024L, 8796093022208L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_73 = new BitSet(mk_tokenSet_73());
	static private final long[] mk_tokenSet_74() {
		long[] data = { -9223372036854775792L, 848977595597824L, 8774103832985600L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_74 = new BitSet(mk_tokenSet_74());
	static private final long[] mk_tokenSet_75() {
		long[] data = { 0L, 29273406167843840L, 8796093022208L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_75 = new BitSet(mk_tokenSet_75());
	static private final long[] mk_tokenSet_76() {
		long[] data = { 0L, 27021606354158592L, 8796093022208L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_76 = new BitSet(mk_tokenSet_76());
	static private final long[] mk_tokenSet_77() {
		long[] data = { 7776L, 0L, 0L, 0L, 0L};
		return data;
	}
	static public final BitSet _tokenSet_77 = new BitSet(mk_tokenSet_77());

	}
