package org.erights.e.elang.evm;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elang.visitors.ETreeVisitor;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.tables.ConstMap;

/**
 * Those ParseNodes that--after expansion--define the kernel nodes evaluated by
 * the E Virtual Machine.
 *
 * @author Mark S. Miller
 */
public abstract class ENode extends ParseNode {

    private static final long serialVersionUID = -7476986257531757419L;

    private transient StaticScope myOptStaticScope = null;

    private final ScopeLayout myOptScopeLayout;

    private transient ConstMap myOptSynEnv = null;

    /**
     * @param optScopeLayout By convention, this parameter should remain last
     *                       in all subclass constructors, to make it
     *                       convenient for visitors to evaluate the
     *                       corresponding argument after other visitation is
     *                       done.
     */
    ENode(SourceSpan optSpan, ScopeLayout optScopeLayout) {
        super(optSpan);
        myOptScopeLayout = optScopeLayout;
    }

    /**
     * A node welcomes a visitor by asking the visitor to visit it in detail.
     * <p/>
     * For example, a FooNode with parts myBar and myBaz might welcome the
     * visitor by asking it to visitFooNode(this, myBar, myBaz). What this
     * visit request returns it what welcome should return. The starting point
     * for this pattern is the "Visitor" pattern from the "Design Patterns"
     * book.
     */
    public abstract Object welcome(ETreeVisitor visitor);

    /**
     * Return a static scope analysis of a subtree that doesn't depend on the
     * enclosing context.
     */
    public final StaticScope staticScope() {
        if (myOptStaticScope == null) {
            myOptStaticScope = computeStaticScope();
        }
        return myOptStaticScope;
    }

    /**
     * When staticScope() is first requested on a given node, it calls
     * computeStaticScope() to do the actual computation, which is then
     * remembered.
     */
    protected abstract StaticScope computeStaticScope();

    /**
     *
     */
    public final ScopeLayout getScopeLayout() {
        if (null == myOptScopeLayout) {
            if (0 == staticScope().namesUsed().size()) {
                return ScopeLayout.EMPTY;
            } else {
                T.notNull(myOptScopeLayout,
                          "internal: Missing ScopeLayout in ",
                          this);
            }
        }
        return myOptScopeLayout;
    }

    /**
     *
     */
    public final ScopeLayout getOptScopeLayout() {
        return myOptScopeLayout;
    }

    /**
     * Get the syntactic environment for this node.
     * <p/>
     * This query should only be asked of a {@link ETreeVisitor Bound-E} node.
     *
     * @return A mapping from each varName used freely in this node to the
     *         corresponding NounPattern.
     */
    public ConstMap getSynEnv() {
        if (null == myOptSynEnv) {
            T.notNull(myOptScopeLayout, "Must be a Bound-E node: ", this);
            ConstMap freeNames = staticScope().namesUsed();
            myOptSynEnv = myOptScopeLayout.getSynEnv().and(freeNames);
        }
        return myOptSynEnv;
    }
}
