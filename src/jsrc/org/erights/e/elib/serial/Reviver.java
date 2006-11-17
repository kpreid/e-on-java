// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.serial;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Used to parameterize an {@link UnserializationStream}.
 * <p/>
 * UnserializationStream will probably need more parameters, but we should add
 * them as we find we need them, rather than create another subclass.
 * <p/>
 * Likely additional parameters: <ul> <li>Name of format and version (a string)
 * <li>optional ClassLoader </ul>
 *
 * @author Mark S. Miller
 */
public abstract class Reviver {

    /**
     * @param ref The object that was reconstructed from the serialization
     *            stream.
     * @return The object to return in its stead -- as its revival.
     */
    protected abstract Object substitute(Object ref);

    /**
     * @return
     */
    public UnserializationStream getUnserializationStream(InputStream inp)
      throws IOException {
        return new UnserializationStream(inp, this);
    }

    /**
     *
     */
    public Object play(byte[] recording)
      throws IOException, ClassNotFoundException {
        ByteArrayInputStream inp = new ByteArrayInputStream(recording);
        UnserializationStream uns = getUnserializationStream(inp);
        Object result = uns.readObject();
        uns.close();
        return result;
    }

    /**
     *
     */
    public Object playFile(File file)
      throws IOException, ClassNotFoundException {

        InputStream inp = new FileInputStream(file);
        UnserializationStream uns = getUnserializationStream(inp);
        try {
            return uns.readObject();
        } finally {
            inp.close();
        }
    }
}
