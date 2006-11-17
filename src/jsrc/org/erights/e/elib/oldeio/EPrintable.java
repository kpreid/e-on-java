package org.erights.e.elib.oldeio;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import java.io.IOException;

/**
 * Implement this to be recognized as implementing the Miranda method {@link
 * org.erights.e.elib.prim.MirandaMethods#__printOn __printOn/1}.
 * <p/>
 * Throwables that do their own __printOn must implement EPrintable, so the
 * Miranda unwrapping happens right.
 *
 * @author Mark S. Miller
 */
public interface EPrintable {

    /**
     *
     */
    void __printOn(TextWriter out) throws IOException;
}
