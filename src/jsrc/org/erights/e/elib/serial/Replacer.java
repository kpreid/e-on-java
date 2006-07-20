// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.serial;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Used to parameterize a {@link SerializationStream}.
 * <p/>
 * Replacer will probably need more parameters, but we should add
 * them as we find we need them, rather than create another subclass.
 * <p/>
 * Likely additional parameters: <ul>
 * <li>Name of format and version (a string)
 * <li>optional ClassLoader
 * </ul>
 *
 * @author Mark S. Miller
 */
public abstract class Replacer {

    /**
     * Called by {@link SerializationStream} to change what gets serialized.
     *
     * @param ref The object that needs to somehow get serialized.
     * @return The object to actually serialize in its stead -- its
     *         representative.
     */
    protected abstract Object substitute(Object ref);

    /**
     * @return
     */
    public SerializationStream getSerializationStream(OutputStream out)
      throws IOException {
        return new SerializationStream(out, this);
    }

    /**
     * Record the seriaized form of the specimen and return the resulting byte
     * array.
     * <p/>
     * Such recordings can be play()ed by a corresponding Reviver.
     */
    public byte[] record(Object specimen) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SerializationStream ser = getSerializationStream(out);
        ser.writeObject(specimen);
        ser.close();
        return out.toByteArray();
    }

    /**
     *
     */
    public void recordFile(File file, Object specimen) throws IOException {
        OutputStream out = new FileOutputStream(file);
        SerializationStream ser = getSerializationStream(out);
        try {
            ser.writeObject(specimen);
        } finally {
            ser.close();
        }
    }
}
