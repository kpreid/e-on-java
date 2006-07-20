package org.erights.e.meta.javax.swing;

import org.quasiliteral.html.SafeHTMLParser;

import javax.swing.JEditorPane;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * @author Mark S. Miller
 */
public class JEditorPaneSugar {


    /**
     * prevent instantiation
     */
    private JEditorPaneSugar() {
    }

    /**
     * This has been overridden to accept only <i>safe</i> html, ie, no tags
     * that imply following a URL.
     * <p/>
     * JEditorPane is declared 'safe', so it must not provide any implicit
     * authority. The underlying html renderer will follow link and image
     * URLs in the set text. Therefore, from E, we prevent the setting of
     * text that would cause the renderer to employ these authorities which
     * have never been granted to it.
     *
     * @see SafeHTMLParser
     */
    static public void setText(JEditorPane self, String t) {
        SafeHTMLParser.parseText(t);
        self.setText(t);
    }
}
