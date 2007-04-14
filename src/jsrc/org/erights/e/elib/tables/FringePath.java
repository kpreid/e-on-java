// Copyright 2007 Kevin Reid, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.tables;

/**
 * The location of a promise in a TraversalKey's fringe, expressed as a series
 * of integers. See {@link Equalizer} for the meaning of the integers (which 
 * should not be depended upon).
 * 
 * @author Kevin Reid
 */
class FringePath {
    private int myPosition;
    private FringePath myNext;
    
    FringePath(int position, FringePath next) {
        myPosition = position;
        myNext = next;
    }
    
    public boolean equals(Object obj) {
        return obj instanceof FringePath && equals(this, (FringePath)obj);
    }
    
    public int hashCode() {
        return hashCode(this);
    }
    
    /**
     * Non-recursive comparison of two possibly-null FringePaths.
     */
    public static boolean equals(FringePath a, FringePath b) {
        while (a != null) {
            if (b == null || a.myPosition != b.myPosition) {
                return false;
            }
            a = a.myNext;
            b = b.myNext;
        }
        if (b != null) {
            // b is longer than a
            return false;
        }
        return true;
    }
    
    /**
     * Non-recursive hashing of a possibly-null FringePath.
     */
    public static int hashCode(FringePath a) {
        int h = 0;
        int shift = 0;
        while (a != null) {
            h ^= a.myPosition << shift;
            shift = (shift + 4) % 32; // XXX find out if this is any good
            a = a.myNext;
        }
        return h;
    }
}