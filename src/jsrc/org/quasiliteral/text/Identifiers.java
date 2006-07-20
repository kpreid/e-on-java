package org.quasiliteral.text;

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

import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.tables.AssocFunc;
import org.erights.e.elib.tables.EIteratable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * For iterating thru a file an identifier at a time.
 *
 * @author Mark S. Miller
 */
public class Identifiers implements EIteratable {

    private final Reader myReader;

    /**
     *
     */
    static public Identifiers fromFile(File file)
      throws FileNotFoundException {
        return new Identifiers(new FileReader(file));
    }

    /**
     * @return
     */
    static public Identifiers fromString(String str) {
        return new Identifiers(new StringReader(str));
    }

    /**
     *
     */
    public Identifiers(Reader reader) {
        myReader = reader;
    }

    /**
     *
     */
    public void iterate(AssocFunc func) {
        try {
            int ch = myReader.read();
            StringBuffer delim = new StringBuffer();
            StringBuffer ident = new StringBuffer();
            while (ch != -1) {
                delim.setLength(0);
                ident.setLength(0);
                while (ch != -1 &&
                  !Character.isJavaIdentifierStart((char)ch)) {

                    delim.append((char)ch);
                    ch = myReader.read();
                }
                while (ch != -1 &&
                  Character.isJavaIdentifierPart((char)ch)) {

                    ident.append((char)ch);
                    ch = myReader.read();
                }
                func.run(delim.toString(), ident.toString());
            }
        } catch (IOException ioe) {
            throw ExceptionMgr.asSafe(ioe);
        }
    }
}
