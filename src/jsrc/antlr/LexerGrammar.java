package antlr;

/* ANTLR Translator Generator
 * Project led by Terence Parr at http://www.jGuru.com
 * Software rights: http://www.antlr.org/license.html
 *
 * $Id: //depot/code/org.antlr/release/antlr-2.7.5/antlr/LexerGrammar.java#1 $
 */

import antlr.collections.impl.BitSet;

import java.io.IOException;

/**
 * Lexer-specific grammar subclass
 */
class LexerGrammar extends Grammar {

    // character set used by lexer
    protected BitSet charVocabulary;
    // true if the lexer generates literal testing code for nextToken
    protected boolean testLiterals = true;
    // true if the lexer generates case-sensitive LA(k) testing
    protected boolean caseSensitiveLiterals = true;
    /**
     * true if the lexer generates case-sensitive literals testing
     */
    protected boolean caseSensitive = true;
    /**
     * true if lexer is to ignore all unrecognized tokens
     */
    protected boolean filterMode = false;

    /**
     * if filterMode is true, then filterRule can indicate an optional rule to
     * use as the scarf language.  If null, programmer used plain "filter=true"
     * not "filter=rule".
     */
    protected String filterRule = null;

    LexerGrammar(String className_, Tool tool_, String superClass) {
        super(className_, tool_, superClass);
        // by default, use 0..127 for ASCII char vocabulary
        BitSet cv = new BitSet();
        for (int i = 0; 127 >= i; i++) {
            cv.add(i);
        }
        setCharVocabulary(cv);

        // Lexer usually has no default error handling
        defaultErrorHandler = false;
    }

    /**
     * Top-level call to generate the code
     */
    public void generate() throws IOException {
        generator.gen(this);
    }

    public String getSuperClass() {
        // If debugging, use debugger version of scanner
        if (debuggingOutput) {
            return "debug.DebuggingCharScanner";
        }
        return "CharScanner";
    }

    // Get the testLiterals option value
    public boolean getTestLiterals() {
        return testLiterals;
    }

    /**
     * Process command line arguments. -trace			have all rules call
     * traceIn/traceOut -traceLexer		have lexical rules call traceIn/traceOut
     * -debug			generate debugging output for parser debugger
     */
    public void processArguments(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if ("-trace".equals(args[i])) {
                traceRules = true;
                antlrTool.setArgOK(i);
            } else if ("-traceLexer".equals(args[i])) {
                traceRules = true;
                antlrTool.setArgOK(i);
            } else if ("-debug".equals(args[i])) {
                debuggingOutput = true;
                antlrTool.setArgOK(i);
            }
        }
    }

    /**
     * Set the character vocabulary used by the lexer
     */
    public void setCharVocabulary(BitSet b) {
        charVocabulary = b;
    }

    /**
     * Set lexer options
     */
    public boolean setOption(String key, Token value) {
        String s = value.getText();
        if ("buildAST".equals(key)) {
            antlrTool.warning("buildAST option is not valid for lexer",
                              getFilename(),
                              value.getLine(),
                              value.getColumn());
            return true;
        }
        if ("testLiterals".equals(key)) {
            if ("true".equals(s)) {
                testLiterals = true;
            } else if ("false".equals(s)) {
                testLiterals = false;
            } else {
                antlrTool.warning("testLiterals option must be true or false",
                                  getFilename(),
                                  value.getLine(),
                                  value.getColumn());
            }
            return true;
        }
        if ("interactive".equals(key)) {
            if ("true".equals(s)) {
                interactive = true;
            } else if ("false".equals(s)) {
                interactive = false;
            } else {
                antlrTool.error("interactive option must be true or false",
                                getFilename(),
                                value.getLine(),
                                value.getColumn());
            }
            return true;
        }
        if ("caseSensitive".equals(key)) {
            if ("true".equals(s)) {
                caseSensitive = true;
            } else if ("false".equals(s)) {
                caseSensitive = false;
            } else {
                antlrTool.warning("caseSensitive option must be true or false",
                                  getFilename(),
                                  value.getLine(),
                                  value.getColumn());
            }
            return true;
        }
        if ("caseSensitiveLiterals".equals(key)) {
            if ("true".equals(s)) {
                caseSensitiveLiterals = true;
            } else if ("false".equals(s)) {
                caseSensitiveLiterals = false;
            } else {
                antlrTool.warning(
                  "caseSensitiveLiterals option must be true or false",
                  getFilename(),
                  value.getLine(),
                  value.getColumn());
            }
            return true;
        }
        if ("filter".equals(key)) {
            if ("true".equals(s)) {
                filterMode = true;
            } else if ("false".equals(s)) {
                filterMode = false;
            } else if (ANTLRTokenTypes.TOKEN_REF == value.getType()) {
                filterMode = true;
                filterRule = s;
            } else {
                antlrTool.warning(
                  "filter option must be true, false, or a lexer rule name",
                  getFilename(),
                  value.getLine(),
                  value.getColumn());
            }
            return true;
        }
        if ("longestPossible".equals(key)) {
            antlrTool.warning(
              "longestPossible option has been deprecated; ignoring it...",
              getFilename(),
              value.getLine(),
              value.getColumn());
            return true;
        }
        if ("className".equals(key)) {
            super.setOption(key, value);
            return true;
        }
        if (super.setOption(key, value)) {
            return true;
        }
        antlrTool.error("Invalid option: " + key,
                        getFilename(),
                        value.getLine(),
                        value.getColumn());
        return false;
    }
}
