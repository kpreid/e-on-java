package org.erights.e.elib.debug;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;

import java.io.IOException;

/**
 * Things that can be pushed and popped with great rapidity that represent
 * items on an E call stack.
 *
 * @author Mark S. Miller
 * @see org.erights.e.elib.vat.Runner#pushEStackItem(EStackItem)
 */
public interface EStackItem {

    /**
     * Like __printOn, but for contributing a line to an E stack traceback.
     */
    void traceOn(TextWriter out) throws IOException;

    /**
     * @return
     */
    SourceSpan getOptSpan();
}
