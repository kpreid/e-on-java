package org.erights.e.elib.vat;

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

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.tables.ArrayHelper;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * A conventional fifo queue in which dequeued items are removed in
 * the same order they were enqueued.
 * <p/>
 * An untyped queue can hold any object (except null). A queue can be
 * created with a dynamic type, in which case, at no extra overhead,
 * enqueue will only enqueue objects of that type (or a subtype, but
 * not null). This check imposes no *extra* overhead, since java
 * always makes us pay for a dynamic type check on array store anyway.
 * <p/>
 * The class Queue itself is no longer thread safe, instead being
 * optimized for using inside one vat. See the subclass {@link
 * SynchQueue} for a thread-safe variant with a blocking operation.
 *
 * @author Mark S. Miller
 * @see org.erights.e.elib.vat.Vat
 */
public class Queue implements Enumeration {

    static private final int INITIAL_SIZE = 400;

    private Object[] myStuff;

    private int myMaxSize;

    private int myCurSize;

    private int myOut;

    private int myIn;

    /**
     * Makes a Queue that can hold any object.
     */
    public Queue() {
        this(Object.class);
    }

    /**
     * Makes a Queue that can hold objects of the specified elementType.
     *
     * @param elementType may not be a primitive (ie, scalar) type.
     */
    public Queue(Class elementType) {
        if (elementType.isPrimitive()) {
            throw new IllegalArgumentException("must be reference type: " +
                                               elementType);
        }
        myStuff = (Object[])ArrayHelper.newArray(elementType, INITIAL_SIZE);
        myMaxSize = INITIAL_SIZE;
        myCurSize = 0;
        myOut = 0;
        myIn = 0;
    }

    /**
     * Add a new element to the queue.
     *
     * @param newElement the object to be added to the end of the queue.
     * @throws NullPointerException thrown if newElement is null
     * @throws ArrayStoreException  thrown if newElement does not coerce
     *                              to the elementType specified in the Queue constructor.
     */
    public void enqueue(Object newElement) {
        T.notNull(newElement, "cannot enqueue a null");
        // grow array if necessary
        if (myCurSize == myMaxSize) {
            int newSize = (myMaxSize * 3) / 2 + 10;
            Class elementType = myStuff.getClass().getComponentType();
            Object[] stuff = (Object[])ArrayHelper.newArray(elementType,
                                                            newSize);

            // note: careful code to avoid inadvertantly
            // reordrering messages
            System.arraycopy(myStuff, myOut, stuff, 0, myMaxSize - myOut);
            if (myOut != 0) {
                System.arraycopy(myStuff, 0, stuff, myMaxSize - myOut,
                                 myOut);
            }
            myOut = 0;
            myIn = myMaxSize;
            myStuff = stuff;
            myMaxSize = newSize;
        }
        //will throw ArrayStoreException if newElement's type doesn't
        //conform to elementType, so coerce it and give it a last chance.
        try {
            myStuff[myIn] = newElement;
        } catch (ArrayStoreException ase) {
            Class type = myStuff.getClass().getComponentType();
            newElement = E.as(newElement, type);
            myStuff[myIn] = newElement;
        }
        myIn++;
        if (myIn == myMaxSize) {
            myIn = 0;
        }
        myCurSize++;
    }

    /**
     * Check to see if the queue has more elements. This method
     * allows a Queue to be used as an Enumeration.
     *
     * @return is false if the queue is empty, otherwise true
     */
    public boolean hasMoreElements() {
        return myCurSize != 0;
    }

    /**
     * Get the least-recently-added element off of the queue. If the queue
     * is currently empty, throw NoSuchElementException. This method
     * allows a Queue to be used as an Enumeration.
     */
    public Object nextElement() throws NoSuchElementException {
        Object result = optDequeue();
        if (result == null) {
            throw new NoSuchElementException("queue is currently empty");
        }
        return result;
    }

    /**
     * Get the least-recently-added element off of the queue, or null
     * if the queue is currently empty.
     */
    public Object optDequeue() {
        //NOTE: This depends on the java & jvm guarantee that int read
        //and write is atomic.
        if (myCurSize == 0) {
            return null;
        }

        Object result = myStuff[myOut];

        myStuff[myOut] = null;
        myOut++;
        if (myOut == myMaxSize) {
            myOut = 0;
        }
        myCurSize--;

        return result;
    }
}
