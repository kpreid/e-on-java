package org.erights.e.elib.tables;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * An object whose identity had not yet settled was used in a context that
 * requires a settled object. <p>
 * <p/>
 * For example, keys in an EMap must be settled. <p>
 *
 * @author Mark S. Miller
 * @see org.erights.e.elib.ref.Ref#isSettled
 * @see org.erights.e.elib.tables.EMap#get(java.lang.Object)
 * @see org.erights.e.elib.tables.FlexMap#put(Object,Object,boolean)
 */
public class NotSettledException extends Exception {

    public NotSettledException() {
        super();
    }

    public NotSettledException(String msg) {
        super(msg);
    }
}
