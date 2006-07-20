package com.skyhunter.ex.swing.text.html.parser;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;

public interface EParserHandler {


    public void handleStartTag(HTML.Tag elem, MutableAttributeSet m);

    public void handleEndTag(HTML.Tag elem);

    public void handleComment(String text);

    public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet m);

    public void handleError(String msg);

    public void handleText(String text);
}



