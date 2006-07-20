package net.captp.tables;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.prim.E;

/**
 *
 */
public class AnswersTable extends CommTable {

    /**
     *
     */
    public AnswersTable() {
    }

    /**
     *
     */
    public void smash(Throwable problem) {
        for (int i = 1; i < myCapacity; i++) {
            if (!isFree(i)) {
                E.sendOnly(myStuff[i], "__reactToLostClient", problem);
            }
        }
        super.smash(problem);
    }
}
