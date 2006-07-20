package net.vattp.data;

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
 * Abstract class for transforming messages before they are sent. This class is
 * a skeleton of what may be needed to transform a message for HTTP tunnelling.
 * It could also be used to implement a dynamic table dependent compressiong
 * scheme.
 *
 * @author Bill Frantz
 */
abstract class MsgTransformer {

    /**
     * Get any information which will be required to resume a suspended
     * connection.
     */
    abstract byte[] getSuspendInfo();

    /**
     * Initiate a transformation. This method must be called before the first
     * transformation is called.
     */
    abstract void init();

    /**
     * Transform the data.
     *
     * @param buffer is the data to be transformed.
     */
    void transform(byte[] buffer) {
        transform(buffer, 0, buffer.length);
    }

    /**
     * Transform the data.
     *
     * @param buffer is the data to be transformed.
     * @param off    is the offset in buffer to start the transformation
     * @param len    is the length to transform.
     */
    abstract void transform(byte[] buffer, int off, int len);
}
