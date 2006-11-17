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

/**
 * A SynchQueue is a thread-safe Queue, providing its own lock, and a blocking
 * dequeue() operation.
 * <p/>
 * Since it provides a blocking operation, it is not E-safe. For an E-safe
 * variant suitable for use inside one vat, see the superclass {@link Queue}.
 *
 * @author Mark S. Miller
 * @see org.erights.e.elib.vat.Vat
 */
public class SynchQueue extends Queue {

    /**
     *
     */
    private final Object myQLock;

    /**
     * Makes a SynchQueue that can hold any object (except null).
     */
    public SynchQueue() {
        this(Object.class, new Object());
    }

    /**
     * Makes a SynchQueue that can hold objects of the specified elementType.
     *
     * @param elementType may not be a primitive (ie, scalar) type.
     */
    public SynchQueue(Class elementType) {
        this(elementType, new Object());
    }

    /**
     * Provide the object to lock on.
     */
    public SynchQueue(Class elementType, Object qLock) {
        super(elementType);
        myQLock = qLock;
    }

    /**
     * Get the least-recently-added element off of the queue. If the queue is
     * currently empty, block until there is an element that can be dequeued.
     */
    public Object dequeue() {
        synchronized (myQLock) {
            while (true) {
                Object result = optDequeue();
                if (result != null) {
                    return result;
                }
                try {
                    myQLock.wait();
                } catch (InterruptedException ie) {
                    //ignored on purpose, but we do recheck the queue rather
                    //than just waiting again
                }
            }
        }
    }

    /**
     *
     */
    public void enqueue(Object newElement) {
        synchronized (myQLock) {
            super.enqueue(newElement);
            myQLock.notifyAll();
        }
    }

    /**
     * Check to see if the queue has more elements. This method allows a Queue
     * to be used as an Enumeration.
     *
     * @return is false if the queue is empty, otherwise true
     */
    public boolean hasMoreElements() {
        synchronized (myQLock) {
            return super.hasMoreElements();
        }
    }

    /**
     *
     */
    public Object optDequeue() {
        synchronized (myQLock) {
            return super.optDequeue();
        }
    }
}
