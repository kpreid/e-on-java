package org.erights.e.elib.oldeio;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.Thunk;
import org.erights.e.meta.java.lang.CharacterMakerSugar;

import java.io.IOException;
import java.io.Reader;

/**
 * A non-blocking {@link Reader} for reading the text that appears in a shared
 * {@link StringBuffer}.
 * <p/>
 * This class in intended to be thread-safe, and to be used both from inside
 * and outside a vat. When used in conjunction with {@link StringWriter} or our
 * own {@link TextWriter}, the pair is like a non-blocking variant of {@link
 * java.io.PipedReader PipedReader}/{@link java.io.PipedWriter PipedWriter}.
 * The sharing must be set up at creation time, as a TextReader encapsulates
 * its StringBuffer.
 * <p/>
 * In order to be non-blocking, we make the following change to the Reader
 * contract: If a read(..) is attempted on a non-ready TextReader, it will
 * throw an IOException, rather than blocking or returning zero.
 * <p/>
 * In order to be thread-safe, it uses the StringBuffer as its {@link
 * Reader#lock}.
 * <p/>
 * This class does no newline conversion. This should normally be done by the
 * object that writes into the shared StringBuffer.
 *
 * @author Mark S. Miller
 * @author Terry Stanley
 */
public class TextReader extends Reader {

    /**
     * null if closed.
     * <p/>
     * The StringBuffer is still held on to as 'lock'.
     */
    private StringBuffer myOptBuf;

    /**
     * If null, then myOptBuf is all that remains.
     */
    private Thunk myOptFiller;

    /**
     * True when after a mark() and before a corresponding reset().
     */
    private boolean myIsMarked;

    /**
     * The next character to be read will be myOptBuf[myNext]. If not
     * myIsMarked, this will be 0.
     */
    private int myNext;

    /**
     * optFiller defaults to null
     */
    TextReader(StringBuffer buf) {
        this(buf, null);
    }

    /**
     * Makes a Reader for reading from 'buf' in a non-blocking fashion, and
     * calling 'optFiller' to refill it when it's empty.
     *
     * @param buf       The StringBuffer to read from, and to synchronize on.
     * @param optFiller When a read is attempted but 'buf' is empty, then
     *                  optFiller is called to refill 'buf' and to provide a
     *                  successor to itself, which may be null. The next refill
     *                  will be to this successor. If the buffer goes empty and
     *                  the current filler is null, then the TextReader reports
     *                  end-of-stream (ie, further reads will return -1).
     */
    TextReader(StringBuffer buf, Thunk optFiller) {
        super(buf); //use buf as the lock
        myOptBuf = buf;
        myOptFiller = optFiller;
        myIsMarked = false;
        myNext = 0;
    }

    /**
     *
     */
    public void close() throws IOException {
        myOptBuf = null;
    }

    /**
     *
     */
    private void ensureOpen() throws IOException {
        if (null == myOptBuf) {
            throw new IOException("Stream closed");
        }
    }

    /**
     * How many characters are immediately available?
     * <p/>
     * If the buffer is empty, then give the filler one chance and try again.
     * If instead you want to know the largest number of characters that are
     * available, and buffer then as a result, then call fill() first.
     * <p/>
     * Currently, this entire operation is performed with 'lock' held. XXX
     * Should we release 'lock' around the call to myOptFiller?
     */
    public int available() throws IOException {
        synchronized (lock) {
            ensureOpen();
            int result = myOptBuf.length() - myNext;
            if (result >= 1) {
                return result;
            }
            if (null == myOptFiller) {
                return 0;
            }
            int next = myNext;
            myOptFiller = (Thunk)myOptFiller.run();
            if (next != myNext) {
                T.fail("filler must not do stream ops");
            }
            return myOptBuf.length() - next;
        }
    }

    /**
     * Fill the buffer with all available characters, calling the filler as
     * many times as necessary.
     */
    public void fill() throws IOException {
        synchronized (lock) {
            ensureOpen();
            int end = myOptBuf.length();
            while (null != myOptFiller) {
                myOptFiller = (Thunk)myOptFiller.run();
                int newEnd = myOptBuf.length();
                if (newEnd <= end) {
                    return;
                }
                //as long as more characters were added, keep going.
                end = newEnd;
            }
        }
    }

    /**
     * True if either at least one character is available, or we're at the
     * end-of-stream.
     */
    public boolean ready() throws IOException {
        synchronized (lock) {
            return available() >= 1 || null == myOptFiller;
        }
    }

    /**
     * Are we at end-of-stream?
     */
    public boolean isDone() throws IOException {
        synchronized (lock) {
            return available() == 0 && null == myOptFiller;
        }
    }


    /**
     *
     */
    private void ensureReady() throws IOException {
        if (!ready()) {
            throw new IOException("would block");
        }
    }

    /**
     * Record that len characters have just been read.
     */
    private void consumed(int len) {
        if (myIsMarked) {
            myNext += len;
        } else {
            if (0 != myNext) {
                throw new RuntimeException("internal: unmarked next must be 0");
            }
            myOptBuf.delete(0, len);
        }
    }

    /**
     * Reads up to len chars, as many as it can without blocking.
     * <p/>
     * If it can't read even a single char without blocking, throw an
     * IOException rather than blocking or returning 0.
     */
    public int read(char[] cbuf, int off, int len) throws IOException {
        synchronized (lock) {
            int result = 0;
            while (true) {
                int avail = available();
                if (0 == avail) {
                    if (result >= 1) {
                        return result;
                    } else if (null == myOptFiller) {
                        return -1;
                    } else {
                        throw new IOException("would block");
                    }
                } else if (0 == len) {
                    return result;
                }
                int rest = StrictMath.min(len, avail);
                myOptBuf.getChars(myNext, myNext + rest, cbuf, off);
                consumed(rest);
                result += rest;
            }
        }
    }

    /**
     * readAheadLimit is ignored
     */
    public void mark(int readAheadLimit) throws IOException {
        synchronized (lock) {
            ensureOpen();
            myOptBuf.delete(0, myNext);
            myNext = 0;
            myIsMarked = true;
        }
    }

    /**
     *
     */
    public void reset() throws IOException {
        synchronized (lock) {
            ensureOpen();
            if (!myIsMarked) {
                throw new IOException("not marked");
            }
            myNext = 0;
            myIsMarked = false;
        }
    }

    /**
     * Returns the next available character, or null at end of file.
     * <p/>
     * If nothing is currently available, throws rather than blocking or
     * returning -1.
     */
    public Character readChar() throws IOException {
        int result = read();
        if (result == -1) {
            return null;
        } else {
            return CharacterMakerSugar.valueOf((char)result);
        }
    }

    /**
     * Reads no more than 'size' characters from the file, and return them as a
     * String. If at end-of-file, return null.
     */
    public String readString(int size) throws IOException {
        char[] cbuf = new char[size];
        int numRead = read(cbuf);
        if (numRead == -1) {
            //end of file
            return null;
        }
        return new String(cbuf, 0, numRead);
    }

    /**
     * Returns as much as is available, or null at end-of-stream.
     * <p/>
     * If nothing is currently available, return "" rather than throwing.
     */
    public String readReady() throws IOException {
        synchronized (lock) {
            if (isDone()) {
                return null;
            }
            fill();
            String result = myOptBuf.substring(myNext);
            consumed(result.length());
            return myOptBuf.toString();
        }
    }

    /**
     * Reads everything else and returns it.
     * <p/>
     * If everything else isn't yet available, then throws rather than blocking
     * or returning something funny, but characters have still been consumed
     * anyway. If this doesn't suit you, use mark() and reset() to protect
     * yourself.
     */
    public String readText() throws IOException {
        synchronized (lock) {
            String result = readReady();
            if (!isDone()) {
                if (ready()) {
                    throw new RuntimeException(
                      "internal: readReady must finish the job");
                } else {
                    throw new IOException("would block");
                }
            }
            return result;
        }
    }

    /**
     *
     */
    public String toString() {
        return "<TextReader>";
    }
}
