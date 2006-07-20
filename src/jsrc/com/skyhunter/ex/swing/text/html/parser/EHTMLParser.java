package com.skyhunter.ex.swing.text.html.parser;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import javax.swing.text.html.parser.DTD;
import javax.swing.text.html.parser.Parser;
import javax.swing.text.html.parser.TagElement;

public class EHTMLParser extends Parser {

    private EHTMLHandler eHandler = null;

    public EHTMLParser(DTD dtd, EHTMLHandler eHTMLHandler) {
        super(dtd);
        eHandler = eHTMLHandler;
    }


    protected void handleStartTag(TagElement elem) {
        eHandler.handleStartTag(elem);
    }

    protected void handleEndTag(TagElement elem) {
        eHandler.handleEndTag(elem);
    }

    protected void handleComment(char[] text) {
        eHandler.handleComment(new String(text));
    }

    protected void handleEmptyTag(TagElement elem) {
        eHandler.handleEmptyTag(elem);
    }

    protected void handleEOFInComment() {
        eHandler.handleEOFInComment();
    }

    protected void handleError(int ln, String msg) {
        eHandler.handleError(ln, msg);
    }

    protected void handleText(char[] text) {
        eHandler.handleText(new String(text));
    }

    protected void handleTitle(char[] text) {
        eHandler.handleTitle(new String(text));
    }
}



