package org.erights.e.elib.slot;

import org.erights.e.elib.prim.E;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * This exception signals that an audit has failed.
 *
 * @author Ka-Ping Yee
 */
public class AuditFailedException extends RuntimeException {

    /**
     *
     */
    public AuditFailedException(Auditor auditor, Object objectExpr) {
        super("object expression " + E.toQuote(objectExpr) +
              " failed audit by " + E.toString(auditor));
    }
}
