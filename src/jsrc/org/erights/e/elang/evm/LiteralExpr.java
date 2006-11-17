package org.erights.e.elang.evm;

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
import org.erights.e.develop.format.StringHelper;
import org.erights.e.elang.scope.EvalContext;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elang.visitors.ETreeVisitor;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.Twine;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;


/**
 * BNF: LiteralInteger | LiteralFloat64 | LiteralChar | LiteralString<br> #
 * (perhaps someday): | LiteralTwine
 * <p/>
 * Evaluates to the literal's value, which may be any of<ul> <li><tt>int</tt>
 * -- any integer without any size limit <li><tt>float64</tt> -- any value in
 * the Java subset of the IEEE double precision standard. The set of values is
 * the same as in the IEEE standard except that there's only one NaN value.
 * <li><tt>char</tt> -- any unicode character representable in Java. I think
 * this is the same as the 16-bit subset of Unicode. <li><tt>String</tt> -- a
 * ConstList of any sequence of chars. In E, a String is really a <i>bare
 * Twine</i>. <li>(perhaps someday) <tt>{@link Twine}</tt> -- a ConstList of
 * chars + source position information recording where these characters
 * allegedly came from. A <i>bare Twine</i> (ie, a String) is a Twine without
 * any annotations. </ul> Note that not all E scalars are written as literals.
 * In particular, <tt>null</tt>, <tt>false</tt>, and <tt>true</tt> are instead
 * the names of non-shadowable variables in the universal scope holding the
 * corresponding values. These scalars can thereby be written "literally
 * enough" by writing these variable names. The similar cases of
 * <tt>Infinity</tt> and <tt>NaN</tt> are explained below.
 * <p/>
 * A LiteralExpr object may be programmatically created to hold any int or
 * float64 value. However, the following ints and float64s cannot be written
 * <i><b>as literals</b></i> in the E source language:<ul> <li>Negative
 * integers. For example, the E expression <tt>-3</tt> expands to the Kernel-E
 * <tt>3.negate()</tt>. A LiteralExpr holding a <tt>-3</tt> {@link #subPrintOn
 * prints} as <tt>-3</tt>, which does express the original value in E (by using
 * a call expression). <li>Likewise for negative float64s. <tt>-3.1</tt>
 * expands to <tt>3.1.negate()</tt>. A LiteralExpr on <tt>-3.1</tt> prints as
 * <tt>-3.1</tt>, which does express the original value in E. Likewise for
 * <tt>-0.0</tt>, which has <i>the same magnitude</i> (&lt;=&gt;) as
 * <tt>0.0</tt> but is not <i>the same</i> (==) as 0.0. <li>float64 positive
 * infinity. This is the result of, for example, the expression
 * <tt>1.0/0.0</tt>. A LiteralExpr on this value prints as <tt>Infinity</tt>.
 * This is the name of an unshadowable variable in the universal scope holding
 * this value, so the printed form does express the original value as a
 * variable reference. <li>float64 negative infinity, as results from the
 * expression <tt>-1.0/0.0</tt>. This prints as -Infinity, which expresses the
 * original value. <li>The one float64 Nan, as results from the expression
 * <tt>0.0/0.0</tt>. This prints as NaN, which is an unshadowable variable in
 * the universal scope. The printed form thereby expresses the original value.
 * </ul> Because these literals have the same meaning as the E programs they
 * print as, an optimizer operating on Kernel-E ASTs may, perhaps as part of
 * constant folding, turn such constant expressions into LiteralExprs holding
 * these values.
 *
 * @author Mark S. Miller
 */
public class LiteralExpr extends EExpr {

    private final Object myValue;

    /**
     * Makes an EExpr that will evaluate to 'value'.
     *
     * @param value          If 'value' is a String (though not a Twine), then
     *                       it is interned during construction. This makes no
     *                       difference to E's semantics, but 1) may be more
     *                       efficient, and 2) helps the E language and
     *                       ELib/Java work together more smootly, since Java
     *                       also interns all String literals. When compiling E
     *                       to JVM bytecodes, an E literal will turn into a
     *                       Java literal anyway, so this anticipates the
     *                       resulting Java-level semantics.
     *                       <p/>
     *                       Currently, 'value' may also be a Character, EInt,
     *                       or Double. We expect to eventually allow 'value'
     *                       to be a Twine
     * @param optScopeLayout
     */
    public LiteralExpr(SourceSpan optSpan,
                       Object value,
                       ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        if (value instanceof String) {
            myValue = ((String)value).intern();
        } else {
            myValue = value;
            T.require(Ref.isDeepPassByCopy(value),
                      "Must be DeepPassByCopy: ",
                      value);
        }
    }

    /**
     * Uses XXX 'makeFoo(...)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {StaticMaker.make(LiteralExpr.class),
          "run",
          getOptSpan(),
          myValue,
          getOptScopeLayout()};
        return result;
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitLiteralExpr(this, myValue);
    }

    /**
     *
     */
    protected StaticScope computeStaticScope() {
        return StaticScope.EmptyScope;
    }

    /**
     *
     */
    protected Object subEval(EvalContext ctx, boolean forValue) {
        return myValue;
    }

    /**
     *
     */
    public void subMatchBind(ConstList args,
                             Object specimen,
                             OneArgFunc optEjector,
                             FlexList bindings) {
        LiteralExpr other;
        try {
            other = (LiteralExpr)Ref.resolution(specimen);
        } catch (ClassCastException cce) {
            //using a try/catch since success is typical and we have
            //to pay for the test in the cast anyway
            throw Thrower.toEject(optEjector, cce);
        }
        if (!Ref.isSameEver(myValue, other.myValue)) {
            throw Thrower.toEject(optEjector,
                                  "Mismatch: " + myValue + " vs " + other
                                    .myValue);
        }
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        out.print(printRep());
    }

    /**
     *
     */
    public String printRep() {
        if (myValue instanceof Character) {
            String str = StringHelper.quote(myValue.toString());
            return "\'" + str.substring(1, str.length() - 1) + "\'";

        } else if (myValue instanceof String) {
            return StringHelper.quote((String)myValue);

        } else if (myValue instanceof Twine) {
            return StringHelper.quote(((Twine)myValue).bare());

        } else if (myValue instanceof Number) {
            return myValue.toString();

        } else {
            return "<unknown literal " + myValue + ">";
        }
    }

    /**
     * @deprecated Use {@link #getValue()}
     */
    public Object value() {
        return myValue;
    }

    /**
     * The actual data value
     */
    public Object getValue() {
        return myValue;
    }
}
