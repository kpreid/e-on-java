package org.erights.e.elib.slot;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.ref.Ref;

/**
 * This is a trusted class, and may only be directly subclassed by classes
 * trusted to play by the rules -- to only pass in a set of approvers that
 * truly do approve of the E-behavior of the instance.
 *
 * @author Mark S. Miller
 */
public class Auditable {

    /**
     *
     */
    private final Auditor[] myApprovers;

    /**
     *
     */
    protected Auditable(Auditor[] approvers) {
        myApprovers = approvers;
    }

    /**
     *
     */
    boolean isApprovedBy(Auditor auditor) {
        for (int i = 0, len = myApprovers.length; i < len; i++) {
            if (Ref.isSameEver(myApprovers[i], auditor)) {
                return true;
            }
        }
        return false;
    }
}
