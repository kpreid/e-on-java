package org.quasiliteral.astro;

import org.erights.e.elib.base.SourceSpan;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * An element of an {@link Astro} argument list.
 * <p/>
 * XXX Obsolete now that sequences are explicit:
 * Normally this is the same type as {@link Astro} (as in the AST and Term
 * trees). But this is made a separate type for QuasiTerm trees, where they
 * can represent a pattern or generator for some number of Astros. But it is
 * a supertype of Astro, since an Astro can always be provided where an
 * AstroArgs is required -- the Astro represents one AstroArg.
 *
 * @author Mark S. Miller
 */
public interface AstroArg {

    /**
     * Where was the source text that was originally lexed or parsed to
     * produce this node?
     */
    SourceSpan getOptSpan();
}
