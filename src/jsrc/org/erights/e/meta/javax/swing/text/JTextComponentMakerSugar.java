package org.erights.e.meta.javax.swing.text;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;

/**
 * Methods for sweetening the JTextComponent maker (the tamed static methods of
 * {@link JTextComponent}).
 *
 * @author Mark S. Miller
 */
public class JTextComponentMakerSugar {

    /**
     * Privately remember the default Keymap
     */
    static private final Keymap DEFAULT_MAP = new JTextArea().getKeymap();

    /**
     * prevent instantiation
     */
    private JTextComponentMakerSugar() {
    }

    /**
     * Safe replacement for the suppressed static addKeymap/2.
     */
    static public Keymap addKeymap() {
        return JTextComponent.addKeymap(null, DEFAULT_MAP);
    }
}
