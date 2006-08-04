package org.erights.e.elang.interp;

/*
The contents of this file are subject to the Electric Communities E Open
Source Code License Version 1.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License
at http://www.communities.com/EL/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
the specific language governing rights and limitations under the License.

The Original Code is the Distributed E Language Implementation, released
July 20, 1998.

The Initial Developer of the Original Code is Electric Communities.
Copyright (C) 1998 Electric Communities. All Rights Reserved.

Contributor(s): ______________________________________.
*/

import org.erights.e.elang.scope.Scope;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.ConstMap;

/**
 * Defines the protocol of the object known as "interp" in the privileged
 * scope.
 * <p>
 * This is the interpreted program's interface to the read-eval-print loop
 * that's driving its evaluation. The spawner of an interpreter will
 * normally control that interpreter through a different interface, the
 * InterpLoop.
 *
 * @see "InterpLoop"
 * @see "org.erights.e.elang.cmdLoopMakerAuthor"
 * @author Mark S. Miller
 * @author Terry Stanley
 */
public interface CmdLoop extends ControlLoop {

    /**
     * A list of Strings that's assumed to be the command line args
     */
    ConstList getArgs();

    /**
     * A map from String (property names) to Strings (property values) that's
     * assumed to reflect System.getProperties(),
     * org/erights/e/elang/syntax/syntax-props-default.txt, the eprops.txt
     * file, the optional ~/.e/user-eprops.txt file, and any -Dprop=value
     * command line arguments.
     */
    ConstMap getProps();

    /**
     * Should an input expression be echoed as expanded to Kernel-E as well
     * as evaluated?
     * <p>
     * If so, the expanded form will be shown in a "# expand: ..." block
     */
    boolean getExpand();

    /**
     * Implementation specific internal diagnostic tool: Should we show
     * the implementation's expansion to Transformed-E?
     */
    boolean getShowTransformed();

    /**
     * Should problem reports show their Java stack trace as well as their E
     * stack trace?
     * <p>
     * This switch only makes sense while we're interpreting parse trees,
     * rather than compiling E to jvm byte codes. While we're interpreting,
     * the Java stack trace tends to large and uninformative. Once we're
     * compiling, the Java stack trace should be all there is, and should do
     * both jobs well. So, at that time, this flag will be ignored.
     */
    boolean getShowJStack();

    /**
     * When a problem is thrown, should we show the E stack trace?
     */
    boolean getShowEStack();

    /**
     * A function of a value and a TextWriter that prints the value by writing
     * onto the TextWriter.
     * <p>
     * The default printFunc is:
     * <pre>    def printFunc(value, out :TextWriter) :void {
     *         out.quote(value)
     *     }</pre>
     * <p>
     * Note that the printFunc itself may throw an execption. It is up to the
     * caller of the printFunc to protect itself from this possibility.
     * <p>
     * XXX We should define an interface to represent the result type.
     */
    Object getPrintFunc();

    /**
     * Is this a read-eval-print loop for an interactive command line?
     * <p>
     * If so, then the top scope should be mutable, each outcome should be
     * reported, prompts should be generated, and evaluation should continue
     * after errors.
     * <p>
     * "interactive" is an immutable property, since it's too hard to change.
     */
    boolean getInteractive();

    /**
     *
     */
    void setExpand(boolean flag);

    /**
     *
     */
    void setShowTransformed(boolean flag);

    /**
     *
     */
    void setShowJStack(boolean flag);

    /**
     *
     */
    void setShowEStack(boolean flag);

    /**
     *
     * @param newPF
     */
    void setPrintFunc(Object newPF);

    /**
     * What scope are top-level expressions evaluated in?
     */
    Scope getTopScope();

    /**
     * Sets the topScope and suppresses the next {@link #nextScope(Scope)}
     */
    void setTopScope(Scope newScope);

    /**
     * If {@link #setTopScope(Scope)} has not been called, nextScope/1 sets the
     * topScope, presumably for the next turn.
     * <p>
     * If {@link #setTopScope(Scope)} has been called, it sets a flag
     * suppressing the next call to nextScope/1. If nextScope/1 is called while
     * that flag is set, it only clears that flag, so the next call to
     * nextScope/1 will be independent.
     */
    void nextScope(Scope newScope);

    /**
     * Returns the result of evaluating i'th most recent command (top
     * level expression).
     * <p>
     * We may move this so that it's only available in an interactive
     * interp.
     */
    Object getResult(int i);

    /**
     *
     */
    void pushResult(Object result);

    /**
     * First clears temporary state (such as results), then does a System.gc()
     */
    void gc();
}
