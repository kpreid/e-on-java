package antlr;

/**
 * Combines and interleaves multiple streams of tokens together into a single
 * stream. Actions during token recognition change the token stream to be
 * provided.  This simultaneously supports mark/rewind across changes in the
 * token stream, recursive use of token streams (for languages that nest in
 * each other), and tracking of such nesting (e.g., if a nested language is
 * terminated by a bracket that is also used in an outer language).
 * <p/>
 * <p/>
 *
 * @author Dean Tribble
 *         <p/>
 *         Software rights: http://www.antlr.org/license.html
 * @see antlr.TokenBuffer
 * @see antlr.Token
 * @see antlr.TokenQueue
 */
public class TokenMultiBuffer extends TokenBuffer {

    protected String[] myNames;
    protected TokenStream[] myInputs;
    protected MarkRecord myLayers = null;

    protected int myEnterCount = 0;

    /**
     * Create a token buffer
     */
    public TokenMultiBuffer(TokenStream input_) {
        this(new String[]{"base"}, new TokenStream[]{input_});
    }

    /**
     * Create a token buffer will multiple token streams.
     */
    public TokenMultiBuffer(String[] names, TokenStream[] inputs) {
        myNames = names;
        myInputs = inputs;
        input = myInputs[0];
    }

    /**
     * Nest into a named lexer.
     */
    public void push(String name) {
        myLayers = new MarkRecord(myEnterCount, input, myLayers);
        myEnterCount = 0;
        select(findInput(name));
        trace("push");
    }

    /**
     * Exit the current lexer and continue lexing with the outer lexer.
     */
    public void pop() {
        trace("pop");
        //TODO check for myNesting==0?
        if (null == myLayers) {
            return;
        }
        input = myLayers.stream;
        myEnterCount = myLayers.enterCount;
        myLayers = myLayers.next;
    }

    /**
     * Record that we entered the kind of brace that will cause this lexer to
     * exit. This could be called when parsing multiple brace-typed, or
     * generalized to enforce specific matching (e.g., so that if both paren
     * and bracket were supported, it would throw a parsing exception if the
     * matching brace was not on the top of the brace stack).
     */
    public void enterBrace() {
        trace("enter");
        myEnterCount++;
    }

    /**
     * Exit from a nested brace.  If the corresponding open brace was not lexed
     * by this lexer (or more specifically, by the lexing of this layer if the
     * grammars recursively nest), then this lexer is also finished lexing, so
     * pop it and continue lexing in the outer lexer.
     */
    public void exitBrace() {
        trace("exit");
        if (myEnterCount <= 0) {
            pop();
            myEnterCount = 0;
        } else {
            myEnterCount--;
        }
    }

    public void select(int streamNum) {
        input = myInputs[streamNum];
    }

    public void select(String name) {
        input = myInputs[findInput(name)];
        trace("select");
    }

    private int findInput(String name) {
        for (int i = 0, max = myInputs.length; i < max; i++) {
            if (myNames[i].equals(name)) {
                return i;
            }
        }
        return -1; //not a great default, but ensures an error
    }

    private String findName(TokenStream ts) {
        for (int i = 0, max = myInputs.length; i < max; i++) {
            if (myInputs[i] == ts) {
                return myNames[i];
            }
        }
        return "unknown";
    }

    private class MarkRecord {

        final int enterCount;
        final TokenStream stream;
        final MarkRecord next;

        MarkRecord(int count, TokenStream s, MarkRecord n) {
            enterCount = count;
            stream = s;
            next = n;
        }
    };

    private void trace(String header) {
//        System.err.print(header + " [" + findName(input));
//        for (MarkRecord r = myLayers; r != null; r = r.next) {
//            System.err.print(", " + findName(r.stream));
//        }
//        System.err.println("]");
//        Thread.dumpStack();
    }

}
