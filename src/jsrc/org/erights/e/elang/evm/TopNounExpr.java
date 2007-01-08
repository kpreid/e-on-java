/*
 * Created by IntelliJ IDEA.
 * User: tribble
 * Date: Jan 23, 2002
 * Time: 8:28:49 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.erights.e.elang.evm;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elib.base.SourceSpan;

/**
 *
 */
abstract class TopNounExpr extends NounExpr {

    static private final long serialVersionUID = -900987138689954401L;

    /**
     *
     */
    TopNounExpr(SourceSpan optSpan, String name, ScopeLayout optScopeLayout) {
        super(optSpan, name, optScopeLayout);
    }

    /**
     *
     */
    public boolean isOuter() {
        return true;
    }
}
