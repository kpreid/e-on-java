package com.skyhunter.ex.swing.text.html.parser;


import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

public class EParserCallback extends HTMLEditorKit.ParserCallback {

    private EParserHandler eHandler = null;

    public EParserCallback(EParserHandler eHTMLHandler) {
        eHandler = eHTMLHandler;
    }


    public void handleStartTag(HTML.Tag t, MutableAttributeSet m, int pos) {
        eHandler.handleStartTag(t, m);
    };
    public void handleEndTag(HTML.Tag t, int pos) {
        eHandler.handleEndTag(t);
    };
    public void handleComment(char[] text, int pos) {
        eHandler.handleComment(new String(text));
    };
    public void handleSimpleTag(HTML.Tag elem,
                                MutableAttributeSet m,
                                int pos) {
        eHandler.handleSimpleTag(elem, m);
    };
    public void handleError(String msg, int pos) {
        eHandler.handleError(msg);
    };
    public void handleText(char[] text, int pos) {
        eHandler.handleText(new String(text));
    };
}
