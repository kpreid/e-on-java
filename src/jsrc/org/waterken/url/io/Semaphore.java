// Copyright 2002-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.io;

/**
 * A semaphore.
 *
 * @author Tyler
 */
public final class Semaphore {

    private int available;  // The number of available resources.
    private final Object lock = new Object();   // The synchronization lock.

    /**
     * Constructs a <code>Semaphore</code>.
     *
     * @param available The number of available resources.
     */
    public Semaphore(final int available) {
        this.available = available;
    }

    // Semaphore interface.

    /**
     * Acquires a resource.
     *
     * @return The resoure release action.
     */
    public Runnable acquire() {
        synchronized (lock) {
            if (0 == available) {
                try {
                    lock.wait();
                } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            --available;
        }
        return new Release(this);
    }

    /**
     * The resource release action.
     */
    static final class Release implements Runnable {

        private Semaphore parent;

        Release(final Semaphore parent) {
            this.parent = parent;
        }

        // Runnable interface.

        public void run() {
            if (null != parent) {
                synchronized (parent.lock) {
                    ++parent.available;
                    parent.lock.notify();
                }
                parent = null;
            }
        }
    }
}
