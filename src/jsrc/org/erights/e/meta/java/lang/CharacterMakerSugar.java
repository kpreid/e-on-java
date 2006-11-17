// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.java.lang;

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.tables.FlexMap;

/**
 * @author Mark S. Miller
 */
public final class CharacterMakerSugar {

    static final String[][] CHAR_CAT_PAIRS = {{"Cn", "UNASSIGNED"},
      {"Lu", "UPPERCASE_LETTER"},
      {"Ll", "LOWERCASE_LETTER"},
      {"Lt", "TITLECASE_LETTER"},
      {"Lm", "MODIFIER_LETTER"},
      {"Lo", "OTHER_LETTER"},
      {"Mn", "NON_SPACING_MARK"},
      {"Me", "ENCLOSING_MARK"},
      {"Mc", "COMBINING_SPACING_MARK"},
      {"Nd", "DECIMAL_DIGIT_NUMBER"},
      {"Nl", "LETTER_NUMBER"},
      {"No", "OTHER_NUMBER"},
      {"Zs", "SPACE_SEPARATOR"},
      {"Zl", "LINE_SEPARATOR"},
      {"Zp", "PARAGRAPH_SEPARATOR"},
      {"Cc", "CONTROL"},
      {"Cf", "FORMAT"},
      null,
      {"Co", "PRIVATE_USE"},
      {"Cs", "SURROGATE"},
      {"Pd", "DASH_PUNCTUATION"},
      {"Ps", "START_PUNCTUATION"},
      {"Pe", "END_PUNCTUATION"},
      {"Pc", "CONNECTOR_PUNCTUATION"},
      {"Po", "OTHER_PUNCTUATION"},
      {"Sm", "MATH_SYMBOL"},
      {"Sc", "CURRENCY_SYMBOL"},
      {"Sk", "MODIFIER_SYMBOL"},
      {"So", "OTHER_SYMBOL"},
      {"Pi", "INITIAL_QUOTE_PUNCTUATION"},
      {"Pf", "FINAL_QUOTE_PUNCTUATION"}};


    /**
     * Maps from the <a href="http://www.unicode.org/glossary/#general_category"
     * >two letter standard general category name</a> to the name used by
     * Java's {@link Character Character} class.
     */
    static public final ConstMap categories;

    static {
        FlexMap result = FlexMap.make(CHAR_CAT_PAIRS.length);
        for (int i = 0; i < CHAR_CAT_PAIRS.length; i++) {
            Object[] pair = CHAR_CAT_PAIRS[i];
            if (null != pair) {
                if (pair.length != 2) {
                    throw new IllegalArgumentException("must be a pair");
                }
                result.put(pair[0], pair[1], true);
            }
        }
        categories = result.snapshot();
    }

    static private final Character[] LATIN1 = new Character[256];

    static {
        for (char c = 0; c < 256; c++) {
            LATIN1[c] = new Character(c);
        }
    }

    /**
     * prevent instantiation
     */
    private CharacterMakerSugar() {
    }

    /**
     * @return
     */
    static public Character valueOf(char c) {
        if (c < 256) {
            return LATIN1[c];
        } else {
            return new Character(c);
        }
    }

    /**
     * Returns the Unicode character with this character code.
     */
    static public char asChar(long code) {
        if (code < 0) {
            T.fail("Code points must be positive: " + code);
        }
        if (code > 0xFFFF) {
            if (code > 0x10FFFF) {
                T.fail("Code point too large: " + code);
            }
            T.fail("Supplementary code points not yet supported: " + code);
        }
        return (char)code;
    }
}
