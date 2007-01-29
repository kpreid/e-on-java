package com.skyhunter.ex.swing.text.html.parser;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;

public interface EParserHandler {


    void handleStartTag(HTML.Tag elem, MutableAttributeSet m);

    void handleEndTag(HTML.Tag elem);

    void handleComment(String text);

    void handleSimpleTag(HTML.Tag tag, MutableAttributeSet m);

    void handleError(String msg);

    void handleText(String text);
}



