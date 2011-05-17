package org.erights.e.elib.oldeio;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.base.Thunk;
import org.erights.e.elib.vat.Vat;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Moves characters from a Reader to a Writer in a blocking loop, which should
 * be run in a separate vat & runner.
 * 
 * The Writer is flushed whenever there does not appear to be more input
 * immediately arriving.
 * 
 * @author Mark S. Miller
 */
public class CharPipeAdapter implements Thunk {

    /**
     *
     */
    private Reader myReader;

    /**
     *
     */
    private Writer myWriter;

    /**
     * May be called from any thread.
     *
     * @param reader Assumed to be thread-safe.
     * @param writer Assumed to be thread-safe.
     */
    public CharPipeAdapter(Reader reader, Writer writer) {
        myReader = reader;
        myWriter = writer;
    }

    /**
     * Should only be called from the new vat.
     * <p/>
     * Normal users should only call {@link #startCopy(String) startCopy/1}
     */
    public Object run() {
        try {
            try {
                char[] buffer = new char[4096];
                int count;
                while (-1 != (count = myReader.read(buffer))) {
                    myWriter.write(buffer, 0, count);
                    if (count < buffer.length) {
                        myWriter.flush();
                    }
                }
                return Boolean.TRUE;
            } finally {
                myReader.close();
                myReader = null;
                myWriter = null;
            }
        } catch (IOException ioe) {
            throw ExceptionMgr.asSafe(ioe);
        }
    }

    /**
     * @param optName Names the newly created vat
     * @return A pair of the new vat and the outcomeVow for when the io is
     *         finished.
     * @deprecated Use {@link #startCopy(String) startCopy/1} instead, whose
     *             output result is in reverse order.
     */
    public Object[] start(String optName) {
        Object[] startCopyResult = startCopy(optName);
        Object[] result = {startCopyResult[1], startCopyResult[0]};
        return result;
    }

    /**
     * @param optName Names the newly created vat
     * @return A pair of the outcomeVow for when the io is finished, and the
     *         new vat in which the copy happens.
     */
    public Object[] startCopy(String optName) {
        Vat ioVat = Vat.make("headless", optName);
        Object outcomeVow = ioVat.seed(this);
        Object[] result = {outcomeVow, ioVat};
        return result;
    }
}
