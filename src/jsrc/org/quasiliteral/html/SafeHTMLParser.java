package org.quasiliteral.html;

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.develop.exception.NestedException;
import org.erights.e.develop.format.StringHelper;
import org.erights.e.elib.base.ValueThunk;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.tables.ConstSet;
import org.erights.e.elib.tables.FlexMap;
import org.erights.e.elib.tables.FlexSet;

import javax.swing.text.ChangedCharSetException;
import javax.swing.text.html.parser.AttributeList;
import javax.swing.text.html.parser.DTD;
import javax.swing.text.html.parser.Parser;
import javax.swing.text.html.parser.TagElement;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Vector;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * Parses html and throws an exception if the html contains any <i>unsafe</i>
 * tags.
 * <p/>
 *
 * @author Mark S. Miller
 * @see JEditorPane#setText(String)
 */
public class SafeHTMLParser extends Parser {

    /**
     * Each row is a safe tag followed by its allowed safe attributes.
     * <p/>
     * This list is expected to grow over time.
     */
    static private final String[][] SafeTags = {
        {"html", "version"},
        {"head"},
        {"title"},
        {"body"},
        {"h1", "align"},
        {"h2", "align"},
        {"h3", "align"},
        {"h4", "align"},
        {"h5", "align"},
        {"h6", "align"},
        {"h7", "align"},
        {"h8", "align"},
        {"h9", "align"},
        {"p", "align"},
        {"b"},
        {"i"}
    };

    /**
     *
     */
    static public final ConstMap SafeTagMap;

    static {
        FlexMap map = FlexMap.fromTypes(String.class,
                                        ConstSet.class,
                                        SafeTags.length);
        for (int i = 0, ilen = SafeTags.length; i < ilen; i++) {
            String[] row = SafeTags[i];
            FlexSet set = FlexSet.fromType(String.class, row.length - 1);
            for (int j = 1, jlen = row.length; j < jlen; j++) {
                set.addElement(row[j]);
            }
            map.put(row[0], set.snapshot(), true);
        }
        SafeTagMap = map.snapshot();
    }

    private final String myText;

    /**
     *
     */
    private SafeHTMLParser(DTD dtd, String text) {
        super(dtd);
        myText = text;
    }

    static private SafeHTMLParser make(String text) {
        DTD dtd;
        try {
            dtd = DTD.getDTD("html32.bdtd");
            InputStream inp = ClassLoader.getSystemResourceAsStream
              ("javax/swing/text/html/parser/html32.bdtd");
            DataInputStream dis = new DataInputStream(inp);
            dtd.read(dis);
        } catch (IOException ioe) {
            throw ExceptionMgr.asSafe(ioe);
        }
        return new SafeHTMLParser(dtd, text);
    }

    /**
     *
     */
    static public void parseText(String text) {
        try {
            make(text).parse(new StringReader(text));
        } catch (Throwable th) {
            throw new NestedException(th, "# unsafe html: " + text);
        }
    }

    /**
     * Called when an empty tag is encountered.
     */
    protected void handleEmptyTag(TagElement tag)
      throws ChangedCharSetException {
        handleStartTag(tag);
    }

    /**
     * Called when a start tag is encountered.
     */
    protected void handleStartTag(TagElement tag) {
        String tagName = tag.getElement().getName();
        ConstSet optAttrs = (ConstSet)SafeTagMap.fetch(tagName,
                                                       ValueThunk.NULL_THUNK);
        T.require(null != optAttrs,
                  "Tag: '", tagName, "' not declared safe");
        for (AttributeList attr = tag.getElement().atts;
             null != attr;
             attr = attr.next) {

            String name = attr.getName();
            String optValue = attr.value;
            Vector optValues = attr.values;
            if (null != optValue || null != optValues) {
                T.require(optAttrs.contains(name),
                          "Attr: '", name,
                          "' of tag: '", tagName, "' not declared safe");
            }
        }
    }

    /**
     * An error has occurred.
     */
    protected void handleError(int ln, String msg) {
        System.err.println("ignored: " + msg + " on line: " + ln + " of");
        System.err.println(StringHelper.quote(myText));
    }
}
