package org.erights.e.elib.oldeio;

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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 *
 */
public class NonBlockingInputStream extends FilterInputStream {

    public NonBlockingInputStream(InputStream inp) {
        super(inp);
    }

    /**
     * Overridden to throw an IOException if no data is available()
     */
    public int read() throws IOException {
        if (available() >= 1) {
            return super.read();
        } else {
            throw new IOException("not available");
        }
    }

    /**
     * Overridden to read at most available() bytes.
     * <p/>
     * read(byte[]) isn't explicitly overridden since FilterInputStream defines
     * it in terms of read(byte[], int, int).
     */
    public int read(byte b[], int off, int len) throws IOException {
        return super.read(b, off, StrictMath.min(available(), len));
    }

    /**
     * Overridden to skip at most available() bytes.
     */
    public long skip(long n) throws IOException {
        return super.skip(StrictMath.min((long)available(), n));
    }
}
