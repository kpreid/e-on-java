package org.capml.quasi;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.ValueThunk;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.IdentityCacheTable;
import org.erights.e.elib.tables.Twine;
import org.erights.e.meta.java.math.EInt;

/**
 * A StaticMaker on this class is bound to "xml__quasiParser" in the
 * universal namespace, enabling sml`...` expressions in E containing
 * quasi-literal Minimal-XML (which used to be known as SML). <p>
 * <p/>
 * Each parse is actually performed by an instance of this class.
 *
 * @author Mark S. Miller
 * @deprecated Use Term trees instead.
 */
public final class XMLQuasiParser {

    /**
     *
     */
    static public final StaticMaker XMLQuasiParserMaker =
      StaticMaker.make(XMLQuasiParser.class);

    /**
     * Caches previous simple parses (as is used for quasi-parsing)
     */
    static private final IdentityCacheTable OurCache
      = new IdentityCacheTable(QuasiContent.class, 100);

    /**
     * Given a string representing a template, written in post-extraction
     * quasi-literal XML syntax, return a ValueMaker which will substitute
     * arguments into the template in order to construct a concrete XML
     * content (list of Nodes), based on that template.
     */
    static public QuasiContent valueMaker(Twine template) {
        return matchMaker(template);
    }

    /**
     * Given a string representing a pattern, written in post-extraction
     * quasi-literal XML syntax, return a MatchMaker which will matchBind
     * against concrete XML trees that match that pattern.
     */
    static public QuasiContent matchMaker(Twine pattern) {
        QuasiContent result =
          (QuasiContent)OurCache.fetch(pattern, ValueThunk.NULL_THUNK);
        if (null == result) {
            result = new XMLQuasiParser(pattern.bare().toCharArray()).parse();
            OurCache.put(pattern, result);
        }
        return result;
    }

    /**
     * The entire source as a single char array for speed.
     */
    private final char[] mySource;

    /**
     * The position of the *next* character
     */
    private int myPos;

    /**
     * Should eventually provide source filename and pos mappings for
     * debugging info.
     */
    private XMLQuasiParser(char[] source) {
        mySource = source;
        myPos = 0;
    }

    /**
     *
     */
    private QuasiContent parse() {
        QuasiContent result = quasiContent();
        skipWS();
        if (myPos != mySource.length) {
            syntaxError("text left over");
        }
        return result;
    }

    /**
     *
     */
    private void syntaxError(String err) {
        T.fail("syntax error: " + err +
               " at " + myPos);
    }

    /**
     * If there's any text in textBuf, make a QuasiText out of it, push it
     * on qcList, and reset textBuf.
     */
    static private void gatherText(FlexList qcList,
                                   StringBuffer textBuf) {
        if (textBuf.length() >= 1) {
            qcList.push(new QuasiText(textBuf.toString()));
            textBuf.setLength(0);
        }
    }

    /**
     *
     */
    static private QuasiContent gatherContent(FlexList qcList,
                                              StringBuffer textBuf) {
        gatherText(qcList, textBuf);
        if (qcList.size() == 1) {
            return (QuasiContent)qcList.get(0);
        } else {
            return new QuasiContentList(qcList.snapshot());
        }
    }


    /**
     * Top of recursive descent parser. Gets a list of content until it runs
     * into an unmatched outer endTag or the end of the source.
     */
    private QuasiContent quasiContent() {
        FlexList qcList = FlexList.fromType(QuasiContent.class);
        StringBuffer textBuf = new StringBuffer();

        //This while loop has two characters of head room, which matches well
        //our cases. Once we fall out of this one, we need a separate
        //dispatch on the last character.
        while (myPos + 1 < mySource.length) {
            char c1 = mySource[myPos];
            char c2 = mySource[myPos + 1];

            if ('<' == c1) {
                if ('/' == c2) {
                    //We have an unmatched outer end tag, so we leave it
                    //be and return what we've got
                    return gatherContent(qcList, textBuf);
                } else {
                    //We apparently have a start tag, so eat a QuasiElement
                    //and continue
                    gatherText(qcList, textBuf);
                    qcList.push(quasiElement());
                }
            } else if ('&' == c1) {
                if ('#' == c2) {
                    myPos += 2;
                    textBuf.append((char)number(';'));
                } else {
                    syntaxError("expected \"&#\" but got \"&" + c2 + "\"");
                }
            } else if ('$' == c1) {
                if ('{' == c2) {
                    //We apparently have a $-hole
                    gatherText(qcList, textBuf);
                    myPos += 2;
                    qcList.push(new QuasiContentExprHole(number('}')));
                } else if ('$' == c2) {
                    //$$ becomes an uninterpreted $
                    textBuf.append('$');
                    myPos += 2;
                } else {
                    syntaxError("bare '$'");
                }
            } else if ('@' == c1) {
                if ('{' == c2) {
                    //We apparently have a @-hole
                    gatherText(qcList, textBuf);
                    myPos += 2;
                    qcList.push(new QuasiContentPattHole(number('}')));
                } else if ('@' == c2) {
                    //@@ becomes an uninterpreted @
                    textBuf.append('@');
                    myPos += 2;
                } else {
                    syntaxError("bare '@'");
                }
            } else if ('>' == c1) {
                syntaxError("bare '>'");
            } else {
                //We have a text character, so eat it and continue
                textBuf.append(c1);
                myPos++;
            }
        }
        if (myPos < mySource.length) {
            char c1 = mySource[myPos];
            if ("<&>".indexOf(c1) != -1) {
                syntaxError("bare '" + c1 + "'");
            }
            //Note: a terminal bare $ or @ is a normal character
            textBuf.append(c1);
        }
        return gatherContent(qcList, textBuf);
    }

    /**
     * Eat digits followed by terminator. Return the resulting int.
     */
    private int number(char terminator) {
        int start = myPos;
        int result = 0;
        for (; myPos < mySource.length; myPos++) {
            char c1 = mySource[myPos];
            if ('0' <= c1 && c1 <= '9') {
                result = result * 10 + (c1 - '0');
            } else if (terminator == c1) {
                if (start == myPos) {
                    syntaxError("no digits");
                }
                myPos++;
                return result;
            } else {
                syntaxError("expected '" + terminator +
                            "', not '" + c1 + "'");
            }
        }
        syntaxError("ended without '" + terminator + "'");
        return 0; //keep compiler happy
    }

    /**
     * Either c is the next char and we eat it, or we die.
     */
    private void expect(char c) {
        if (myPos >= mySource.length) {
            syntaxError("ended without '" + c + "'");
        }
        char other = mySource[myPos];
        if (other == c) {
            myPos++;
        } else {
            syntaxError("expected '" + c + "' but got '" + other + "'");
        }
    }

    /**
     *
     */
    private QuasiElement quasiElement() {
        Object startTag = startTag();
        QuasiContent children = quasiContent();
        endTag(startTag);
        return new QuasiElement(startTag, children);
    }

    /**
     *
     */
    private void skipWS() {
        while (myPos < mySource.length) {
            char c1 = mySource[myPos];
            if (!Character.isWhitespace(c1)) {
                return;
            }
            myPos++;
        }
    }

    /**
     *
     */
    private Object startTag() {
        expect('<');
        skipWS();
        //we can justify two characters head room
        if (myPos + 1 >= mySource.length) {
            syntaxError("ended in start tag");
        }
        char c1 = mySource[myPos];
        char c2 = mySource[myPos + 1];
        Object result;
        if ('$' == c1 && '{' == c2) {
            myPos += 2;
            result = EInt.valueOf(number('}'));
        } else if ('@' == c1 && '{' == c2) {
            myPos += 2;
            result = EInt.valueOf(~number('}'));
        } else {
            result = name();
        }
        skipWS();
        expect('>');
        return result;
    }

    /**
     *
     */
    private String name() {
        StringBuffer result = new StringBuffer();
        while (myPos < mySource.length) {
            char c1 = mySource[myPos];
            if (Character.isWhitespace(c1) || "<>&/$@".indexOf(c1) != -1) {
                if (result.length() == 0) {
                    syntaxError("identifier expected");
                } else {
                    return result.toString();
                }
            }
            result.append(c1);
            myPos++;
        }
        return result.toString();
    }

    /**
     *
     */
    private void endTag(Object startTag) {
        expect('<');
        expect('/');
        skipWS();
        if (startTag instanceof String) {
            String tag = name();
            if (!tag.equals(startTag)) {
                syntaxError("'" + startTag + "' doesn't match '" + tag);
            }
        }
        skipWS();
        expect('>');
    }
}
