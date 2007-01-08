package org.erights.e.elang.evm;

import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elib.base.SourceSpan;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * @author Mark S. Miller
 */
public abstract class AtomicExpr extends EExpr {

    static private final long serialVersionUID = -8698791542070289629L;

    /**
     *
     */
    AtomicExpr(SourceSpan optSpan, ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
    }

    /**
     *
     */
    public abstract NounExpr asNoun();
}
