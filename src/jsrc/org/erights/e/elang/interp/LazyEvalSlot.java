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

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.scope.Scope;
import org.erights.e.elang.syntax.EParser;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.ref.Resolver;
import org.erights.e.elib.serial.Loader;
import org.erights.e.elib.slot.Slot;
import org.erights.e.elib.slot.BaseSlot;
import org.erights.e.elib.slot.FinalSlot;
import org.erights.e.elib.tables.Twine;

import java.io.IOException;

/**
 * Exists to allow delaying the work of really importing during ScopeSetup,
 * while also allowing the imported things to be in the scope they are
 * defining.
 * <p/>
 * For example, rx__quasiParser is in the safe scope, but is obtained lazily
 * from <elang:interp.makePerlMatchMaker>. For this import to work, it needs to
 * provide the safe scope to the evaluation of this module. Laziness allows the
 * safe scope to effectively include things that can't be defined until the
 * safe scope exists.
 * <p/>
 * Must be made thread-safe for the same reasons as {@link Loader}.
 *
 * @author Mark S. Miller
 */
public class LazyEvalSlot extends BaseSlot {
    /**
     * only meaningful when myOptSource != null
     */
    private Object myOptScope;

    /**
     * to be evaluated in myOptScope
     */
    private Twine myOptSource;

    /**
     * only meaningful when myOptSource == null
     */
    private Object myOptValue;

    /**
     * @param scope  The scope in which to evaluate the source text. It's
     *               declared as an Object rather than Scope so that it can be
     *               a promise for a Scope. This promise must become fulfilled
     *               before the first get() happens.
     * @param source The source text to be evaluated in the promised scope at
     *               the time of the first get().
     */
    LazyEvalSlot(Object scope, Twine source) {
        myOptScope = scope;
        myOptSource = source;
        myOptValue = null;
    }

    /**
     * Returns the result of evaluating my source text in my scope.
     * <p/>
     * This evaluation only happens the first time, after which the value is
     * cached and returned for later requests. If the value isn't
     * DeepPassByCopy, this has security implications, so be careful how you
     * use this class!
     */
    public Object get() {
        Object optScope;
        Twine optSource;
        Object optValue;

        optScope = myOptScope;
        optSource = myOptSource;
        optValue = myOptValue;

        if (null != optSource) {
            Object[] promise = Ref.promise();

            // If it's asked for while it's doing its own evaluation, it
            // returns a promise for what it'll evaluate to.
            myOptValue = promise[0];
            myOptScope = null;
            myOptSource = null;
            try {
                //System.err.println("Lazy eval: " + myOptSource);
                EExpr eExpr = (EExpr)EParser.run(optSource);
                Scope scope = (Scope)E.as(optScope, Scope.class);
                myOptValue = eExpr.eval(scope.withPrefix("__lazy."));
            } catch (Throwable problem) {
                myOptValue = optValue;
                myOptScope = optScope;
                myOptSource = optSource;
                ((Resolver)promise[1]).smash(problem);
                throw ExceptionMgr.asSafe(problem);
            }
            ((Resolver)promise[1]).resolve(myOptValue);
        }
        return myOptValue;
    }

    /**
     * Complains that the variable is immutable
     */
    public void put(Object newValue) {
        T.fail("A lazy Slot may not be changed");
    }

    /**
     * Returns whether its already been forced, in which case it acts just like
     * a {@link FinalSlot}.
     */
    public boolean isFinal() {
        return null == myOptSource;
    }

    /**
     * A LazyEvalSlot is read-only, and so returns itself.
     */
    public Slot readOnly() {
        return this;
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        if (null == myOptSource) {
            out.print("<has ");
            out.quote(myOptValue);
            out.print(">");
        } else {
            out.print("<lazy eval slot>");
        }
    }
}
