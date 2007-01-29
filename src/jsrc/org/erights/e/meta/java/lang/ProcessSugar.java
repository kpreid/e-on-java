package org.erights.e.meta.java.lang;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.format.StringHelper;
import org.erights.e.elib.oldeio.CharPipeAdapter;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.vat.Vat;
import org.erights.e.meta.java.math.EInt;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * A sweetener defining extra messages that may be e-sent to Processes.
 *
 * @author Mark S. Miller
 */
public class ProcessSugar {

    /**
     * prevent instantiation
     */
    private ProcessSugar() {
    }

    /**
     * @return
     */
    static public TextWriter getStdin(Process self) {
        Writer osw = new OutputStreamWriter(self.getOutputStream());
        return new TextWriter(osw, "\n", false, true, null);
    }

    /**
     * @return
     */
    static public CharPipeAdapter attachStdin(Process self, Reader altin) {
        CharPipeAdapter result = new CharPipeAdapter(altin, getStdin(self));
        result.startCopy("stdin-adapter");
        return result;
    }

    /**
     * @return
     */
    static public BufferedReader getStdout(Process self) {
        Reader isr = new InputStreamReader(self.getInputStream());
        return new BufferedReader(isr);
    }

    /**
     * @return
     */
    static public CharPipeAdapter attachStdout(Process self, Writer altout) {
        CharPipeAdapter result = new CharPipeAdapter(getStdout(self), altout);
        result.startCopy("stdout-adapter");
        return result;
    }

    /**
     * @return
     */
    static public BufferedReader getStderr(Process self) {
        Reader isr = new InputStreamReader(self.getErrorStream());
        return new BufferedReader(isr);
    }

    /**
     * @return
     */
    static public CharPipeAdapter attachStderr(Process self, Writer alterr) {
        CharPipeAdapter result = new CharPipeAdapter(getStdout(self), alterr);
        result.startCopy("stderr-adapter");
        return result;
    }

    /**
     * Use only on prompt processes, as this will block the vat until the
     * process completes!
     * <p/>
     * Waits for the Process to exit.
     *
     * @return A triple of the exitCode, the text sent to stdout as a String,
     *         and the text sent to stderr as a String.
     */
    static public Object[] results(Process self) throws InterruptedException {
        InputStream stdout = self.getInputStream();
        StringBuffer bufout = new StringBuffer();
        Bufferer outBufferer = new Bufferer(stdout, bufout);
        new Thread(outBufferer, "stdout").start();

        InputStream stderr = self.getErrorStream();
        StringBuffer buferr = new StringBuffer();
        Bufferer errBufferer = new Bufferer(stderr, buferr);
        new Thread(errBufferer, "stderr").start();

        int exitCode = self.waitFor();
        if (0 == exitCode) {
            outBufferer.waitFor();
            errBufferer.waitFor();
        }
        String strout = StringHelper.canonical(bufout.toString());
        String strerr = StringHelper.canonical(buferr.toString());

        Object[] result = {EInt.valueOf(exitCode), strout, strerr};
        return result;
    }

    /**
     * @param self
     * @return A pair of a vow for the triple that will be returned by {@link
     *         #results(Process) results/0}, and the vat which is {@link
     *         Process#waitFor() waiting for} that process.
     */
    static public Object[] resultsVow(Process self) {
        Vat procVat = Vat.make("headless", "process");
        Object outcomeVow = procVat.seed(self, "results", E.NO_ARGS);
        Object[] result = {outcomeVow, procVat};
        return result;
    }
}
