// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.eio;

import java.io.IOException;

/**
 * Thrown to indicate that the required number of elements or holes were not
 * yet available.
 */
public class UnavailableException extends IOException {

    static private final long serialVersionUID = -5424351887747876354L;

    public UnavailableException() {
    }

    public UnavailableException(String s) {
        super(s);
    }
}
