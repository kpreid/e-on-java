package com.skyhunter.ex.swing.text.html.parser;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import javax.swing.text.html.parser.TagElement;

public interface EHTMLHandler {

    public void handleStartTag(TagElement elem);

    public void handleEndTag(TagElement elem);

    public void handleComment(String text);

    public void handleEmptyTag(TagElement tag);

    public void handleEOFInComment();

    public void handleError(int ln, String msg);

    public void handleText(String text);

    public void handleTitle(String text);
}
