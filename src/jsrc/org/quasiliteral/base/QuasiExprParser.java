package org.quasiliteral.base;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.tables.Twine;

/**
 * Given a template string in the language this quasi parser understands, parse
 * it into a ValueMaker which will generate objects of the form the template
 * describes.
 * <p/>
 * The template language is a value description language augmented with
 * $-holes, representing values to be provided at runtime to the ValueMaker.
 *
 * @author Mark S. Miller
 */
public interface QuasiExprParser {

    /**
     * For the i'th $-hole, dlrHoles[i] is the position of that hole in
     * template, and the character at that position in template must be '$'.
     * <p/>
     * '$' characters that don't correspond to positions in dlrHoles are
     * treated as part of the parser's normal language rather than indicating
     * $-holes.
     */
    ValueMaker valueMaker(Twine template, int[] dlrHoles);

    /**
     * In this old format, each $-hole is represented by a substring like
     * '${3}' for $-hole number 3 (the fourth hole).
     * <p/>
     * '$' and '@' characters that are not holes must be doubled.
     *
     * @deprecated
     */
    ValueMaker valueMaker(Twine template);
}
