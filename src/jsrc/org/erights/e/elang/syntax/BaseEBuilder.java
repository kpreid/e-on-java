package org.erights.e.elang.syntax;

import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.evm.EMatcher;
import org.erights.e.elang.evm.EMethod;
import org.erights.e.elang.evm.ListPattern;
import org.erights.e.elang.evm.LiteralExpr;
import org.erights.e.elang.evm.Pattern;
import org.erights.e.elang.evm.StaticScope;
import org.erights.e.elib.tables.FlexList;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.syntax.SyntaxException;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * @author Mark S. Miller
 */
public interface BaseEBuilder {

    /**
     * Used to mark places where we should be providing a poser (an object from
     * which source position info can be derived).
     */
    Object NO_POSER = null;

    /**
     * Terminates parsing/building with a thrown SyntaxException
     */
    void syntaxError(Object poser, String msg) throws SyntaxException;

    /**
     * Prints a diagnostic to the warning channel and continues.
     */
    void warning(Object poser, String msg);

    /**
     * Complain that syntax desc is reserved for possible future use.
     */
    void reserved(Object poser, String desc);

    /**
     * Complain unless this feature is enabled.
     * <p/>
     * If property <tt>e.enable.<i>pName</i></tt> is true, then return
     * silently. Warn if set to "warn". Otherwise, complain.
     */
    void pocket(Object poser, String pName);

    /**
     * Complain if this feature is enabled.
     * <p/>
     * If property <tt>e.enable.<i>pName</i></tt> is true, then complain. Warn
     * if set to "warn". Otherwise, return silently.
     */
    void antiPocket(Object poser, String pName);

    /**
     *
     */
    FlexList list();

    /**
     *
     */
    FlexList list(Object a);

    /**
     *
     */
    FlexList list(Object a, Object b);

    /**
     *
     */
    FlexList list(Object a, Object b, Object c);

    /**
     *
     */
    FlexList list(Object a, Object b, Object c, Object d);

    /**
     *
     */
    FlexList list(Object a, Object b, Object c, Object d, Object e);

    /**
     *
     */
    FlexList append(Object sofar, Object nexts);

    /**
     *
     */
    FlexList with(Object sofar, Object next);

    /**
     *
     */
    LiteralExpr literal(Object tokenOrData);

    /**
     * Either makes a kernel assignment expression, or expands a non-kernel
     * one, depending on the lValue.
     * <pre>
     *  x := z                is kernel if x is a variable name
     *  x.get(y) := z         expands approximately to    x.put(y, z)
     *  x.get(y1, y2) := z    expands approximately to    x.put(y1, y2, z)
     *  x.getName() := z      expands approximately to    x.setName(z)
     *  x.run(y) := z         expands approximately to    x.setRun(y, z)</pre>
     * The actual expansion introduces a temporary variable to capture the
     * value of z, and to have that be the value of the assignment expression
     * as a whole.
     * <p/>
     * Any lValue other than those listed above is rejected. Remember that
     * <pre>
     *  x[y]     expands to  x.get(y)
     *  x.name   expands to  x.getName()
     *  x(y)     expands to  x.run(y)</pre>
     * so all are valid lValues.
     */
    EExpr assign(Object lValue, Object rValue);

    /**
     * Makes a kernel immediate-call expression
     */
    EExpr call(Object recipientExpr, Object verb, Object args);

    /**
     *
     */
    EExpr kerneldef(Object pattern, Object optEjectorExpr, Object rValue);

    /**
     *
     */
    EExpr escape(Object pattern,
                 Object bodyExpr,
                 Object optArgPattern,
                 Object optCatcher);

    /**
     *
     */
    EExpr hide(Object body);

    /**
     *
     */
    EExpr ifx(Object condExpr, Object thenExpr, Object elseExpr);

    /**
     *
     */
    EMatcher matcher(Object matchHead, Object bodyExpr);

    /**
     *
     */
    EMethod method(Object doco, Object msgPatt, Object bodyExpr);

    /**
     *
     */
    MsgPatt methHead(Object verb, Object patts, Object optResultGuard);

    /**
     *
     */
    EScriptDecl vTable(Object optMethods, Object matchers);

    /**
     * XXX Currently, parts.length must be >= 1
     */
    EExpr sequence(EExpr[] parts);

    /**
     * XXX Here at the base, we should break this up into separate methods for
     * building a CatchExpr and a FinallyExpr.
     */
    EExpr tryx(Object eExpr, Object optCatchers, Object optFinally);

    /**
    *
    */
   EExpr slotExpr(Object poser, Object eExpr);

   /**
    *
    */
   EExpr bindingExpr(Object poser, Object eExpr);

    /**
     *
     */
    Pattern finalPattern(Object atom, Object optGuardExpr);

    /**
     *
     */
    Pattern varPattern(Object atom, Object optGuardExpr);

    /**
    *
    */
   Pattern slotPattern(Object atom, Object optGuardExpr);

   /**
    *
    */
   Pattern bindingPattern(Object atom);

    /**
     *
     */
    Pattern ignore(Object optGuardExpr);

    /**
     *
     */
    ListPattern listPattern(Object subs);

    /**
     *
     */
    Pattern via(Object viaExpr, Object subPattern);

    /**
     *
     */
    EExpr forValue(Object optExpr, StaticScope optUsed);

    /**
     *
     */
    EExpr forFxOnly(Object optExpr, StaticScope optUsed);

    /**
     * When expr is a {@link DelayedExpr}, then ask it. Otherwise, expand<pre>
     *    forControl(expr,ej)</pre>
     * to<pre>
     *    def _ :__Test exit ej := expr</pre>
     */
    EExpr forControl(Object optExpr, Astro optEj, StaticScope optUsed);

    /**
     *
     */
    EExpr[] optExprs(Object optVec);
}
