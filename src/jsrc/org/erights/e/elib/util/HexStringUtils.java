package org.erights.e.elib.util;

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

public class HexStringUtils {

    private HexStringUtils() {
    }

    //static private final String hexChars = "0123456789ABCDEF";

    /**
     * Compute a hexadecimal string based on the contents of a byte array.
     */

/*
    static public String byteArrayToHexString(byte[] byteArray) {

        StringBuffer buf = new StringBuffer(300);
        for (int i = 0; i < byteArray.length; i++) {
            int x = byteArray[i];
            if (x < 0) x += 256;
            buf.append(hexChars.charAt(x / 16));
            buf.append(hexChars.charAt(x % 16));
        }
        return buf.toString();
    }
*/
    /**

     * Specialized version of the above for CryptoHash.toString()

     * prefix must be a string (but may be empty).
     * byteArray is the byte array to dump out.
     * postfix may be null.

     */
/*
    static public String byteArrayToAbbreviatedHexString(String prefix,
                                                         byte[] byteArray,
                                                         String postfix) {
        StringBuffer buf = new StringBuffer(prefix);

        int limit = 4;
        if (limit > byteArray.length) limit = byteArray.length;

        for (int i = 0; i < limit; i++) {
            int x = byteArray[i];
            if (x < 0) x += 256;
            buf.append(hexChars.charAt(x / 16));
            buf.append(hexChars.charAt(x % 16));
        }
        if (postfix != null) buf.append(postfix);
        return buf.toString();
    }
*/

    /**
     * Convert a byte array to a formated hex string suitable for printing.
     * <p/>
     * The output is divided into lines. Each line has a 4 character hex offset
     * in string, followed by the hex representation of 16 bytes of the input,
     * grouped into 8 hex character groups, followed by an ascii interpretion
     * of the 16 bytes.
     *
     * @param msg The hex string.
     */
    static public String bytesToReadableHexStr(byte[] msg) {
        return bytesToReadableHexStr(msg, 0, msg.length);
    }

    /**
     * Convert a byte array to a formated hex string suitable for printing.
     * <p/>
     * The output is divided into lines. Each line has a 4 character hex offset
     * in string, followed by the hex representation of 16 bytes of the input,
     * grouped into 8 hex character groups, followed by an ascii interpretion
     * of the 16 bytes.
     *
     * @param msg The hex string.
     * @param off the offset in msg to start converting. The offsets included
     *            in the output will start with 0000 regardless of the value of
     *            off. &param len the number of bytes to convert.
     */
    static public String bytesToReadableHexStr(byte[] msg, int off, int len) {
        StringBuffer msgString = new StringBuffer(len * 59 + 2);
        msgString.append("\n");

        for (int line = 0; line < len; line += 16) {
            // put out the hex offset of the line
            msgString.append(Character.forDigit((line >> 12) & 15, 16));
            msgString.append(Character.forDigit((line >> 8) & 15, 16));
            msgString.append(Character.forDigit((line >> 4) & 15, 16));
            msgString.append(Character.forDigit(line & 15, 16));
            msgString.append(" ");
            // First put out the Hex
            for (int i = 0; 16 > i; i++) {
                if (0 == (i & 3)) {
                    // Space off each group of 4 bytes
                    msgString.append(" ");
                }
                if (line + i < len) {
                    byte b = msg[line + i + off];
                    msgString.append(Character.forDigit((b >> 4) & 15, 16));
                    msgString.append(Character.forDigit(b & 15, 16));
                } else {
                    msgString.append("  ");
                }
            }
            msgString.append(" ");

            // Now put out the character form
            for (int i = 0; 16 > i; i++) {
                if (line + i < len) {
                    byte b = msg[line + i + off];
                    msgString.append((' ' > b || 0x7f <= b) ? '.' : (char)b);
                }
            }
            msgString.append("\n"); // End of line of up to 16 bytes
        }
        return msgString.toString();
    }

    static public byte[] hexStrToBytes(String s) {
        if (s == null) {
            return null;
        }
        s = s.trim();
        if (s.startsWith("0x") || (s.startsWith("0X"))) {
            s = s.substring(2);
        }
        if (0 != s.length() % 2) {
            s = "0" + s;
        }

        int len = s.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i <= len; i++) {
            // Extract two chars from string
            String byteHex = s.substring(2 * i, 2 * i + 2);
            // parse them as a hex byte
            result[i] = Byte.parseByte(byteHex, 16);
        }
        return result;
    }
}
