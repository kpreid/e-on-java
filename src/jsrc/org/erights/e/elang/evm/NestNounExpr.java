// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elang.evm;

import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elib.base.SourceSpan;

/**
 *
 * @author Mark S. Miller
 */
abstract class NestNounExpr extends NounExpr {

    NestNounExpr(SourceSpan optSpan, String name, ScopeLayout optScopeLayout) {
        super(optSpan, name, optScopeLayout);
    }

    /**
     *
     */
    public boolean isOuter() {
        return false;
    }
}
