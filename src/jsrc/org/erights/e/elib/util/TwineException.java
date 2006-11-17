package org.erights.e.elib.util;

/*
The contents of this file are subject to the Electric Communities E Open
Source Code License Version 1.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License
at http://www.communities.com/EL/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
the specific language governing rights and limitations under the License.

The Original Code is the Distributed E Language Implementation, released
July 20, 1998.

The Initial Developer of the Original Code is Electric Communities.
Copyright (C) 1998 Electric Communities. All Rights Reserved.

Contributor(s): ______________________________________.
*/

import org.erights.e.develop.exception.ThrowableSugar;
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.tables.Twine;

import java.io.IOException;

/**
 * If an E program throws a non-Exception object, a TwineException or
 * RuntimeException is actually thrown using E.toTwine(problem) to convert the
 * object into the exception's message.
 *
 * @author Mark S. Miller
 */
public class TwineException extends RuntimeException implements EPrintable {

    private final Twine myTwineMsg;

    /**
     *
     */
    private TwineException(String bareMsg, Twine msg) {
        super(bareMsg);
        myTwineMsg = msg;
    }

    /**
     * If the message is a simple String, then just use a RuntimeException.
     * Otherwise, use a TwineException in order to keep track of source
     * position information.
     */
    static public RuntimeException make(Twine msg) {
        String bareMsg = msg.bare();
        if (msg.isBare()) {
            return new RuntimeException(bareMsg);
        } else {
            return new TwineException(bareMsg, msg);
        }
    }

    /**
     * A TwineException just prints as 'problem: <bare message>'
     */
    public void __printOn(TextWriter out) throws IOException {
        out.indent(ThrowableSugar.ProblemIndent)
          .print(ThrowableSugar.ProblemPrefix, getMessage());
    }

    /**
     *
     */
    public String toString() {
        return E.toString(this);
    }
}
