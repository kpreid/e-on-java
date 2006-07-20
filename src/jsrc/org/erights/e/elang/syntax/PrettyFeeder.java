package org.erights.e.elang.syntax;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.exception.PrintStreamWriter;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.Twine;
import org.quasiliteral.syntax.LineFeeder;
import org.quasiliteral.syntax.NeedMoreException;
import org.quasiliteral.syntax.TwineFeeder;

import java.io.IOException;

/**
 * For prettifying source using indentation info.
 *
 * @author Mark S. Miller
 */
public class PrettyFeeder implements LineFeeder {

    private final TwineFeeder myWrapped;

    private final FlexList myBuf;

    /**
     *
     */
    public PrettyFeeder(Twine sourceCode) {
        myWrapped = new TwineFeeder(sourceCode);
        myBuf = FlexList.fromType(Character.TYPE, sourceCode.size());
    }

    /**
     *
     */
    static public Twine pretty(Twine sourceCode) throws IOException {
        PrettyFeeder feeder = new PrettyFeeder(sourceCode);
        ELexer lexer = new ELexer(feeder, true, false);

        //XXX now that we've gotten rid of parser-to-lexer feedback, pretty()
        //should only require the lexer.
        TextWriter warns = new TextWriter(PrintStreamWriter.stderr());
        EParser parser = EParser.make(lexer, warns);
        try {
            parser.parse(); //don't care about the result
        } catch (NeedMoreException nme) {
            //ignore
        }
        return feeder.getPrettyCode();
    }

    /**
     *
     */
    static private final Twine SPACE = Twine.fromString(" ");

    /**
     *
     */
    public Twine optNextLine(boolean atTop,
                             boolean quoted,
                             int indent,
                             char closer,
                             int closeIndent) {
        Twine result = myWrapped.optNextLine(atTop,
                                             quoted,
                                             indent,
                                             closer,
                                             closeIndent);
        if (null == result) {
            return null;
        }
        if (!quoted && !result.startsWith("# ")) {
            //Starting with "# " is the convention for commented-out code,
            //so we don't indent it.
            result = (Twine)result.trim().add("\n");
            if (result.startsWith(String.valueOf(closer))) {
                indent = closeIndent;
            }
            Twine indentation = (Twine)SPACE.multiply(indent);
            result = (Twine)indentation.add(result);
        }
        myBuf.append(result);
        return result;
    }

    /**
     *
     */
    public Twine getPrettyCode() {
        return (Twine)myBuf.snapshot();
    }
}
