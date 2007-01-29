package com.skyhunter.ex.swing.text.html.parser;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import javax.swing.text.html.parser.TagElement;

public interface EHTMLHandler {

    void handleStartTag(TagElement elem);

    void handleEndTag(TagElement elem);

    void handleComment(String text);

    void handleEmptyTag(TagElement tag);

    void handleEOFInComment();

    void handleError(int ln, String msg);

    void handleText(String text);

    void handleTitle(String text);
}
