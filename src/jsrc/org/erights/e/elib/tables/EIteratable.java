package org.erights.e.elib.tables;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * The operation assumed by E's for-loop.
 * <p/>
 * This should therefore be universal to all E collections.
 *
 * @author Mark S. Miller
 */
public interface EIteratable {

    /**
     *
     */
    void iterate(AssocFunc func);
}
