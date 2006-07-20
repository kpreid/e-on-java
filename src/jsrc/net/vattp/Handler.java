// Copyright 2004-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package net.vattp;

/**
 * Handle records received on a {@link Connection}.
 *
 * @author Tyler
 */
public interface Handler {

    /**
     * Notification of a received record.
     *
     * @param record The record data.
     * @param len    The record length.
     */
    void run(byte[] record, int len);
}
