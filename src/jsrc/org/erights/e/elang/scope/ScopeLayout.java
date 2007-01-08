package org.erights.e.elang.scope;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elang.evm.NounExpr;
import org.erights.e.elang.evm.NounPattern;
import org.erights.e.elang.evm.ParseNode;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.serial.DeepPassByCopy;
import org.erights.e.elib.tables.AssocFunc;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.tables.ConstSet;
import org.erights.e.elib.tables.EIteratable;
import org.erights.e.elib.tables.FlexSet;
import org.erights.e.elib.tables.SamenessHashCacher;

/**
 * Static information about the runtime representation of a {@link Scope}.
 * <p/>
 * A ScopeLayout and an {@link EvalContext} together form a Scope. The
 * ScopeLayout maps from names to their defining {@link NounPattern}s (or
 * equivalent) whose {@link NounExpr} which will retrieve the corresponding
 * {@link org.erights.e.elib.slot.Slot Slot} or value from a corresponding
 * EvalContext. Each EvalContext can be seen as an instantiation of a
 * ScopeLayout.
 * <p/>
 * A ScopeLayout is built out of layered binding contours, with the layers
 * separated by {@link ScopeLayoutContour}s. At the lowest layer is a {@link
 * ScopeLayoutBase} that may contain many bindings; each additional binding is
 * added in a {@link ScopeLayoutLink}.
 * <p/>
 * For example, if a, b, and c are extant in the outermost scope, and
 * declarations are added like this:
 * <pre>
 *     def d := ...
 *     {
 *         def e := ...
 *         def f := ...
 *         {
 *             def g := ...
 *         }
 *     }
 * </pre>
 * then the resulting structure would look like this:
 * <pre>
 *                         ____________________________________________
 *                        |                           _______________  |
 *                        |                          |               | |
 *     Base &lt;- Link &lt;- Contour &lt;- <font
 * ></font>Link &lt;- Link &lt;- Contour &lt;- Link    | |
 *   a, b, c     d        |         e       f        |         g     | |
 *                        |                          |_______________| |
 *                        |____________________________________________|
 * </pre>
 *
 * @author E. Dean Tribble
 * @author some mods by Mark S. Miller
 * @author more docs by Ka-Ping Yee
 */
public abstract class ScopeLayout extends SamenessHashCacher
  implements EIteratable, DeepPassByCopy {

    static private final long serialVersionUID = 418195544301121170L;

    static public final StaticMaker ScopeLayoutMaker =
      StaticMaker.make(ScopeLayout.class);

    static public final ScopeLayout EMPTY =
      make(0, ConstMap.EmptyMap, "__empty$");

    final int myOuterCount;

    /**
     * @param outerCount
     * @param synEnv     must be a map in which each association is
     *                   <pre>    "varName =&gt; {@link NounPattern}</pre>
     * @param fqnPrefix
     * @return The ScopeLayout representing exactly this mapping as a single
     *         contour
     */
    static public ScopeLayout make(int outerCount,
                                   ConstMap synEnv,
                                   String fqnPrefix) {
        return new ScopeLayoutBase(outerCount, synEnv, fqnPrefix);
    }

    /**
     *
     */
    static void ensureValidFQNPrefix(String fqnPrefix) {
        T.notNull(fqnPrefix, "fqnPrefix may not be null");
        T.require("".equals(fqnPrefix) || fqnPrefix.endsWith(".") ||
          fqnPrefix.endsWith("$"), "unrecognized prefix: ", fqnPrefix);
    }

    /**
     * @param outerCount
     */
    ScopeLayout(int outerCount) {
        myOuterCount = outerCount;
    }

    /**
     * A map in which each association is
     * <pre>    varName =&gt; {@link NounPattern}</pre>
     */
    public ConstMap getSynEnv() {
        return ConstMap.fromIteratable(this, true);
    }

    /**
     * The optional fully qualified prefix.
     * <p/>
     * If not null, a fully qualified prefix must end with a "." or "$".
     */
    public abstract String getFQNPrefix();

    /**
     * If this layout is for representing an outer Scope, then returns the
     * number of outers; else -1.
     */
    public int getOuterCount() {
        return myOuterCount;
    }

    /**
     * withPrefix/1 may only be used on an outer Scope (and therefore a
     * ScopeLayout representing an outer Scope).
     */
    public abstract ScopeLayout withPrefix(String fqnPrefix);

    /**
     * Gets the NounPattern representing the defining occurrence of this
     * variable name in this scope.
     */
    public abstract NounPattern getOptPattern(String varName);

    /**
     * Gets the NounPattern representing the defining occurrence of this
     * variable name in this scope.
     */
    public NounPattern getPattern(String varName) {
        NounPattern optResult = getOptPattern(varName);
        T.notNull(optResult,
                  "Internal: Variable definition not found: ",
                  varName);
        return optResult;
    }

    /**
     * Gets the NounExpr which will retrieve the Slot/value of the variable
     * named 'varName' from a corresponding EvalContext.
     */
    public NounExpr getOptNoun(String varName) {
        NounPattern optPattern = getOptPattern(varName);
        if (null == optPattern) {
            return null;
        } else {
            return optPattern.getNoun();
        }
    }

    /**
     * Gets the NounExpr which will retrieve the Slot/value of the variable
     * named 'varName' from a corresponding EvalContext.
     */
    public NounExpr getNoun(String varName) {
        return getPattern(varName).getNoun();
    }

    /**
     * Is 'varName' in scope?
     */
    abstract boolean contains(String varName);

    /**
     * Returns a ScopeLayout just like this one, but with a new varName =>
     * NounPattern mapping added to the innermost contour.
     * <p/>
     * This does not create a new contour, so this operation is rejected if
     * varName is already defined in the innermost contour.
     */
    public ScopeLayout with(String varName, NounPattern namer) {
        requireShadowable(varName, namer.getNoun());
        int outerCount = -1 == myOuterCount ? -1 : myOuterCount + 1;
        return new ScopeLayoutLink(outerCount, this, varName, namer);
    }

    /**
     * Return the set of all varNames mapped by this ScopeLayout.
     */
    public ConstSet getVarNameSet() {
        FlexSet result = FlexSet.fromType(String.class);
        addNamesTo(result);
        return result.snapshot();
    }

    /**
     * Enumerates the visible <tt>varName =&gt; {@link NounPattern}</tt>
     * associations for the syntactic environment represented by this
     * ScopeLayout.
     */
    public void iterate(final AssocFunc func) {
        getVarNameSet().iterate(new AssocFunc() {
            public void run(Object i, Object name) {
                String varName = (String)name;
                func.run(varName, getOptPattern(varName));
            }
        });
    }

    /**
     * Adds all the names mapped by this ScopeLayout to 'names'
     */
    abstract void addNamesTo(FlexSet names);

    /**
     * Makes a new ScopeLayout just like this one, but with a new empty
     * innermost prefix contour added.
     */
    public ScopeLayout nest(String optName) {
        if (null == optName) {
            // XXX Should assign non-conflicting numbers somehow.
            optName = "_";
        }
        return new ScopeLayoutPrefixContour(this,
                                            getFQNPrefix() + optName + "$");
    }

    /**
     * Makes a new ScopeLayout just like this one, but with a new empty
     * innermost contour added.
     */
    public ScopeLayout nest() {
        return new ScopeLayoutInnerContour(this);
    }

    /**
     *
     */
    public ScopeLayout nestOuter() {
        return new ScopeLayoutOuterContour(this);
    }

    /**
     * Throw an exception if the varName may not be shadowed because it is
     * already defined in the current (i.e. innermost) contour.
     * <p/>
     * If varName may not be shadowed because it is reserved, this is caught in
     * {@link NounPattern} rather than here.
     */
    public abstract void requireShadowable(String varName, ParseNode optPoser);
}
