package org.quasiliteral.syntax;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.tables.ArrayHelper;
import org.erights.e.elib.tables.Twine;

import java.lang.reflect.Array;

/**
 * Keeps track of indentation info for BaseLexer's subclasses and clients.
 *
 * @author Mark S. Miller
 * @author Terry Stanley
 */
public class Indenter {

    /**
     * How many block-levels deep am I?
     * <p/>
     * Each open-bracketing character at the end of a line introduces a new
     * nesting level. Each nesting level counts for four spaces.
     */
    private int myNest;

    /**
     * My top-of-stack is the same as the number of unclosed open bracketing
     * characters.
     * <p/>
     * The stack keeps track of unclosed open brackets, and what indent should
     * be assigned to new lines within that bracket (should it be the most
     * recent -- the top of stack).
     */
    private int myTOS;

    /**
     * The openners are Twine for reporting located errors at close time
     */
    private Twine[] myOpennerStack;

    /**
     * The closers -- close bracketing characters that would close the
     * currently unclosed open brackets
     * <p/>
     * We start the stack off with an indent level of zero and a dummy closer
     * character.
     */
    private char[] myCloserStack;

    /**
     * The indentation level associated with each unclosed open bracket
     */
    private int[] myIndentStack;

    /**
     * Was this open bracket also a nesting?
     * <p/>
     * I.e., was the openner the last character on its line?  If so, then its
     * popping should also decrement myNest.
     */
    private boolean[] myNestStack;

    /**
     *
     */
    public Indenter() {
        myNest = 0;
        myTOS = 0;
        myOpennerStack = new Twine[16];
        myOpennerStack[0] = null;       //dummy initial openner
        myCloserStack = new char[16];
        myCloserStack[myTOS] = 'x';     // dummy initial closer
        myIndentStack = new int[16];
        myIndentStack[myTOS] = 0;
        myNestStack = new boolean[16];
        myNestStack[myTOS] = true; //shouldn't matter, can't be popped
    }

    /**
     *
     */
    private Object grow(Object array) {
        int length = Array.getLength(array);
        return ArrayHelper.resize(array, length * 2);
    }

    /**
     * Internal push
     */
    private void push(Twine openner,
                      char closerChar,
                      int indent,
                      boolean isNest) {
        myTOS++;
        if (myTOS >= myCloserStack.length) {
            myOpennerStack = (Twine[])grow(myOpennerStack);
            myCloserStack = (char[])grow(myCloserStack);
            myIndentStack = (int[])grow(myIndentStack);
            myNestStack = (boolean[])grow(myNestStack);
        }
        myOpennerStack[myTOS] = openner;
        myCloserStack[myTOS] = closerChar;
        myIndentStack[myTOS] = indent;
        myNestStack[myTOS] = isNest;
    }

    /**
     * Push a nester
     */
    public void nest(Twine openner, char closerChar) {
        myNest++;
        push(openner, closerChar, myNest * 4, true);
    }

    /**
     * Push a non-nester
     */
    public void push(Twine openner, char closerChar, int indent) {
        push(openner, closerChar, indent, false);
    }

    /**
     * Process a closing bracket by popping the stacks.
     * <p/>
     * If the opening was also a nesting, this decrements the nest level.
     *
     * @param closerChar As an error check, 'closerChar' must be the closing
     *                   bracket character needed to close the most recent
     *                   unclosed bracket.
     * @param closer     Used by some syntax errors to report where the
     *                   erronous closing text occurs.
     */
    public void pop(char closerChar, Twine closer) {
        if (myTOS <= 0) {
            throw new SyntaxException(
              "unmatched closing bracket: " + closerChar,
              null,
              closer,
              0,
              closer.size());
        }
        if (myCloserStack[myTOS] != closerChar) {
            Twine openner = myOpennerStack[myTOS];
            throw new SyntaxException(
              "mismatch: " + myCloserStack[myTOS] + " vs " + closerChar,
              openner,
              closer,
              0,
              closer.size());
        }
        int oldTOS = myTOS;
        myTOS--;
        if (myNestStack[oldTOS]) {
            myNest--;
        }
    }

    /**
     *
     */
    public void requireEmpty(String msg) {
        if (myTOS <= 0) {
            //all's fine
            return;
        }
        throw new SyntaxException(msg + ", unmatched openning bracket: ",
                                  myOpennerStack[myTOS]);
    }

    /**
     * If the tos is closerChar, then process a closing bracket by popping the
     * stacks.
     * <p/>
     * If the opening was also a nesting, this decrements the nest level.
     *
     * @param closerChar Checks whether 'closerChar' is the closing bracket
     *                   character needed to close the most recent unclosed
     *                   bracket.
     */
    public void popIf(char closerChar) {
        if (myTOS <= 0) {
            //no stack, do nothing
            return;
        }
        if (myCloserStack[myTOS] != closerChar) {
            //doesn't match, do nothing.
            return;
        }
        int oldTOS = myTOS;
        myTOS--;
        if (myNestStack[oldTOS]) {
            myNest--;
        }
    }

    /**
     * How indented should a vanilla new line be in this context?
     * <p/>
     * A vanilla new line is one that doesn't begin with a close bracketing
     * character (for these, use pop()'s return value), and one that's not
     * continuing a previous line by virtue of a binary operator or a
     * backslash
     */
    public int getIndent() {
        return myIndentStack[myTOS];
    }

    /**
     * Which character would close the most recent open bracket?
     */
    public char getCloser() {
        return myCloserStack[myTOS];
    }

    /**
     * If the character that would close the most recently open bracket
     * (getCloser()) were typed, where should it be indented?
     */
    public int getCloseIndent() {
        if (myNestStack[myTOS]) {
            return (myNest - 1) * 4;
        } else if ('*' == myCloserStack[myTOS]) {
            //XXX kludge: docComments are an exception
            return myIndentStack[myTOS];
        } else {
            return myIndentStack[myTOS] - 1;
        }
    }

    /**
     * Show closing stack
     */
    public String toString() {
        return new String(myCloserStack, 1, myTOS);
    }
}
