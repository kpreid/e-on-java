// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.eio;

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.prim.StaticMaker;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

/**
 * <b><i><u><font color="009000">Start Here:</font></u></i></b> Safe static
 * methods, typically for making EIO objects of various kinds.
 *
 * @author Mark S. Miller
 * @see <a href="http://www.erights.org/elib/concurrency/eio/index.html"
 *      >The EIO pages</a>
 */
public class EIO {

    static public final StaticMaker EIOMaker = StaticMaker.make(EIO.class);

    /**
     * ALL ({@link Integer#MAX_VALUE}) represents the number of remaining
     * elements left until termination (close or fail) will be hit.
     */
    static public final int ALL = Integer.MAX_VALUE;

    /**
     * prevents instantiation
     */
    private EIO() {
    }

//    /**
//     * Makes an intra-vat EIO pipe and returns its pair of facets -- an
//     * {@link InStream} and an {@link OutStream}.
//     * <p>
//     * Everything written to the OutStream is immediately available to from
//     * the InStream.
//     *
//     * @param elementType The type of elements that may be passed through the
//     *                    pipe. This is first normalized by
//     *                    {@link ArrayHelper#typeForArray(Class)}.
//     * @param bufferSize
//     * @return A pair of an {@link InStream} and {@link OutStream}.
//     */
//    static public Object[] pipe(int bufferSize, Class elementType) {
//        elementType = ArrayHelper.typeForArray(elementType);
//        EPipeReader reader = new EPipeReader(elementType, bufferSize);
//        Object[] result = { reader, reader.myWriter };
//        return result;
//    }

//    /**
//     * elementType defaults to Object.class.
//     * @param bufferSize
//     * @return
//     */
//    static public Object[] pipe(int bufferSize) {
//        return pipe(bufferSize, Object.class);
//    }
//
//    /**
//     * Makes a pipe that accepts and produces lists of bytes.
//     */
//    static public Object[] pipeBytes(int bufferSize) {
//        return pipe(bufferSize, Byte.TYPE);
//    }

//    /**
//     * Makes a pipe that accepts and produces Twine (the E equivalent of
//     * Strings).
//     */
//    static public Object[] pipeStrings(int bufferSize) {
//        Object[] result = pipe(bufferSize, Character.TYPE);
//        result[0] = new TwineReaderImpl((InStream)result[0]);
//        return result;
//    }
//
//    /**
//     * Start a CopyFilter that will continually read from reader and write to
//     * writer.
//     * <p>
//     * It will also propogate backpressure and termination.
//     */
//    static public void connect(InStream reader, OutStream writer) {
//        new CopyFilter(reader, writer);
//    }

//    /**
//     * Returns an InStream in which all elements of the list are immediately
//     * available.
//     */
//    static public InStream makeListReader(ConstList list) {
//        Object pipe[] = pipe(list.size(), list.valueType());
//        InStream result = (InStream)pipe[0];
//        OutStream writer = (OutStream)pipe[1];
//        try {
//            writer.write(list);
//        } catch (UnavailableException e) {
//            throw ExceptionMgr.asSafe(e);
//        } catch (IOException e) {
//            throw ExceptionMgr.asSafe(e);
//        }
//        writer.close();
//        return result;
//    }

    /**
     * Makes and returns a pair of an OutStream and a promise for the list
     * of all the elements written to that writer.
     * <p/>
     * Should the OutStream be closed, the promise resolves to the list of all
     * the element that have been written.
     * <p/>
     * Should the OutStream fail, the promise will break with the same terminal
     * problem (resolve to a reference broken by that problem).
     *
     * @return
     */
    static public Object[] makeListWriter(int sizeHint, Class elementType) {
        T.fail("XXX not yet implemented");
        return null; //make the compiler happy
    }

    /**
     * Like {@link #makeListWriter(int, Class)}, but the elementType is 'char',
     * and on success the promise resolves to a bare Twine of these characters.
     */
    static public Object[] makeStringWriter(int sizeHint) {
        T.fail("XXX not yet implemented");
        return null; //make the compiler happy
    }

    /**
     * Like {@link #makeListWriter(int, Class)}, but with elementType 'byte'.
     *
     * @return
     */
    static public Object[] makeBytesWriter(int sizeHint) {
        return makeListWriter(sizeHint, Byte.TYPE);
    }

//    /**
//     * Wraps a Java Reader of Strings with a TwineReader.
//     *
//     * @param jIns
//     * @return
//     */
//    static public TwineReader wrapJReader(Reader jIns) {
//        T.fail("XXX not yet implemented");
//        return null; //make the compiler happy
//    }

    /**
     * Wraps a Java InputStream with an InStream of bytes.
     *
     * @return
     */
    static public InStream wrapJInputStream(InputStream jIns) {
        T.fail("XXX not yet implemented");
        return null; //make the compiler happy
    }

    /**
     * Wraps a Java Writer of Strings with a OutStream of chars.
     *
     * @return
     */
    static public OutStream wrapJWriter(Writer jOuts) {
        T.fail("XXX not yet implemented");
        return null; //make the compiler happy
    }

    /**
     * Wraps a Java OutputStream with an OutStream of bytes.
     *
     * @return
     */
    static public OutStream wrapJOutputStream(OutputStream jOuts) {
        T.fail("XXX not yet implemented");
        return null; //make the compiler happy
    }
}
