package org.erights.e.elang.smallcaps;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.evm.EScript;
import org.erights.e.elib.base.SourceSpan;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

/**
 * A code generator that just emits Smallcaps instructions directly.
 *
 * @author Mark S. Miller
 * @author Darius Bacon
 */
public class SmallcapsEncoder implements SmallcapsOps, SmallcapsEmitter {

    /**
     * Encoded bytes in reverse order.
     */
    private byte[] myBuf;

    /**
     *
     */
    private int myCodeSize;

    private int myNumOperands;
    private int myMaxOperands;
    private int myNumHandlers;
    private int myMaxHandlers;

    /**
     *
     */
    SmallcapsEncoder() {
        myBuf = new byte[128];
        myCodeSize = 0;
    }


    public void associateSourceLocation(int lowLabel,
                                        int highLabel,
                                        SourceSpan span) {
        // XXX stub
    }

    /**
     * Return a value representing the current position in the code buffer.
     * This is *not* an address; it's an opaque value used as an argument to
     * emitJump(), etc.
     */
    public int getLabel() {
        return myCodeSize;
    }

    public void emitContourTop() {
        writeWholeNum(myMaxHandlers);
        writeWholeNum(myMaxOperands);

        // Reset for the next contour
        myNumOperands = myMaxOperands = myNumHandlers = myMaxHandlers = 0;
    }

    public void emitDup() {
        writeOpcode(OP_DUP, 1, 0);
    }

    public void emitPop() {
        writeOpcode(OP_POP, -1, 0);
    }

    public void emitSwap() {
        writeOpcode(OP_SWAP, 0, 0);
    }

    public void emitRot() {
        writeOpcode(OP_ROT, 0, 0);
    }

    public void emitJump(int label) {
        writeWholeNum(myCodeSize - label);
        writeOpcode(OP_JUMP, 0, 0);
    }

    public void emitBranch() {
        writeOpcode(OP_BRANCH, -2, 0);
    }

    public void emitCall(String verb, int arity, boolean only) {
        if (only) {
            emitInvoke(OP_CALL_ONLY, verb, arity, 0);
        } else {
            emitInvoke(OP_CALL, verb, arity, 1);
        }
    }

    private void emitInvoke(int opcode, String verb, int arity, int pushed) {
        writeWholeNum(arity);
        writeUTF(verb);
        writeOpcode(opcode, -arity - 1 + pushed, 0);
    }

    public void emitEjectorOnly(int label) {
        writeWholeNum(myCodeSize - label);
        writeOpcode(OP_EJECTOR_ONLY, 1, 1);
    }

    public void emitEjector(int label) {
        writeWholeNum(myCodeSize - label);
        writeOpcode(OP_EJECTOR, 1, 1);
    }

    public void emitTry(int label) {
        writeWholeNum(myCodeSize - label);
        writeOpcode(OP_TRY, 0, 1);
    }

    public void emitUnwind(int label) {
        writeWholeNum(myCodeSize - label);
        writeOpcode(OP_UNWIND, 0, 1);
    }

    public void emitEndHandler() {
        writeOpcode(OP_END_HANDLER, 0, -1);
    }

    public void emitInteger(BigInteger num) {
        if (num.signum() >= 0) {
            writeWholeNum(num);
            writeOpcode(OP_WHOLE_NUM, 1, 0);
        } else {
            writeWholeNum(num.negate());
            writeOpcode(OP_NEG_INT, 1, 0);
        }
    }

    public void emitFloat64(double float64) {
        writeFloat64(float64);
        writeOpcode(OP_FLOAT64, 1, 0);
    }

    public void emitChar(char chr) {
        writeChar(chr);
        writeOpcode(OP_CHAR, 1, 0);
    }

    public void emitString(String str) {
        writeUTF(str);
        writeOpcode(OP_STRING, 1, 0);
    }

    public void emitTrue() {
        writeOpcode(OP_TRUE, 1, 0);
    }

    public void emitFalse() {
        writeOpcode(OP_FALSE, 1, 0);
    }

    public void emitNull() {
        writeOpcode(OP_NULL, 1, 0);
    }

    public void emitLiteral(Object obj) {
        // XXX fill in
    }

    public void emitScope() {
        writeOpcode(OP_SCOPE, 1, 0);
    }

    public void emitObject(String docComment,
                           String fqName,
                           EExpr[] auditors,
                           EScript eScript) {
        // XXX fill in
    }

    public void emitListPatt(int n) {
        writeWholeNum(n);
        writeOpcode(OP_LIST_PATT, -2 + 2 * n, 0);
    }

    public void emitCdrPatt(int n) {
        writeWholeNum(n);
        writeOpcode(OP_CDR_PATT, -2 + 2 * n, 0);
    }

    public void emitNoun(int addressingMode, int index, boolean slot) {
        writeWholeNum(index);
        int baseOpcode = (slot ? OP_SLOT : OP_NOUN);
        writeOpcode(baseOpcode + addressingMode, 1, 0);
    }

    public void emitAssign(int addressingMode, int index) {
        writeWholeNum(index);
        writeOpcode(OP_ASSIGN + addressingMode, -1, 0);
    }

    public void emitBind(int addressingMode, int index, boolean slot) {
        writeWholeNum(index);
        int baseOpcode = (slot ? OP_BIND_SLOT : OP_BIND);
        writeOpcode(baseOpcode + addressingMode, -1, 0);
    }


    /**
     *
     */
    byte[] copyBytes() {
        byte[] result = new byte[myCodeSize];
        int r = 0;
        for (int i = myCodeSize - 1; 0 <= i; i--) {
            result[r++] = myBuf[i];
        }
        return result;
    }

    /**
     *
     */
    private void writeByte(int byt) {
        int oldLen = myBuf.length;
        if (myCodeSize >= oldLen) {
            byte[] newBuf = new byte[oldLen * 2];
            System.arraycopy(myBuf, 0, newBuf, 0, oldLen);
            myBuf = newBuf;
        }
        myBuf[myCodeSize++] = (byte)byt;
    }

    /**
     *
     */
    void writeOpcode(int opcode, int deltaOperands, int deltaHandlers) {
        writeByte(opcode);
        myNumOperands += deltaOperands;
        if (myNumOperands > myMaxOperands) {
            myMaxOperands = myNumOperands;
        }
        myNumHandlers += deltaHandlers;
        if (myNumHandlers > myMaxHandlers) {
            myMaxHandlers = myNumHandlers;
        }
    }

    /**
     *
     */
    void writeBytes(byte[] bytes) {
        int oldLen = myBuf.length;
        int newNextByte = myCodeSize + bytes.length;
        if (newNextByte > oldLen) {
            byte[] newBuf = new byte[StrictMath.max(newNextByte, oldLen * 2)];
            System.arraycopy(newBuf, 0, myBuf, 0, oldLen);
            myBuf = newBuf;
        }
        for (int i = bytes.length - 1; 0 <= i; i--) {
            myBuf[myCodeSize++] = bytes[i];
        }
    }

    /**
     * How many bytes are needed to represent the whole number wholeNum in <a
     * href= "http://waterken.com/dev/Doc/doc-code/index.html#ExtensionNumber"
     * >Waterken Doc ExtensionNumber</a> format?
     */
    static int numDocBytes(BigInteger wholeNum) {
        int signum = wholeNum.signum();
        T.require(signum >= 0, "Non-negative integer expected: ", wholeNum);
        if (signum == 0) {
            return 1;
        }
        int numBits = wholeNum.bitLength();
        return (numBits + 6) % 7;
    }

    /**
     * Encodes a whole number (a non-negative integer) using the <a href=
     * "http://waterken.com/dev/Doc/doc-code/index.html#ExtensionNumber"
     * >Waterken Doc ExtensionNumber</a> format, written using just enough
     * bytes.
     */
    void writeWholeNum(BigInteger wholeNum) {
        writeWholeNum(wholeNum, numDocBytes(wholeNum));
    }

    /**
     * Encodes a whole number (a non-negative integer) using the <a href=
     * "http://www.waterken.com/dev/Doc/code/#ExtensionNumber" >Waterken Doc
     * ExtensionNumber</a> format, zero-extended to fit into numBytes bytes.
     * <p/>
     * numBytes must be large enough, and therefore must be &gt;= 1 even if
     * wholeNum == 0. By "zero extended", we mean extended with leading 0x80
     * bytes, in keeping with the ExtensionNumber format.
     */
    void writeWholeNum(BigInteger wholeNum, int numBytes) {
        int signum = wholeNum.signum();
        T.require(signum >= 0, "Non-negative integer expected: ", wholeNum);
        T.require(numBytes >= numDocBytes(wholeNum),
                  "Can't fit ",
                  wholeNum,
                  " into " + numBytes,
                  " 7-bit bytes");

        //Just write the least significant 7 bits
        writeByte(wholeNum.intValue() & 0x7F);

        for (int i = 1; i < numBytes; ++i) {
            //it's not the least significant one, so shift, mask, and
            //or-in a high bit.
            int shifted = wholeNum.shiftRight(i * 7).intValue();
            writeByte((shifted & 0x7F) | 0x80);
        }
    }

    /**
     * Just like {@link #writeWholeNum(BigInteger)}, but when the argument
     * already fits in a Java <tt>int</tt>.
     */
    void writeWholeNum(int wholeNum) {
        writeWholeNum(BigInteger.valueOf(wholeNum));
    }

    /**
     * Just like {@link #writeWholeNum(BigInteger,int)}, but when the argument
     * already fits in a Java <tt>int</tt>.
     */
    void writeWholeNum(int wholeNum, int numBytes) {
        writeWholeNum(BigInteger.valueOf(wholeNum), numBytes);
    }

    /**
     * Writes out a String of Unicode characters in UTF-8.
     */
    void writeUTF(String str) {
        byte[] bytes;
        try {
            bytes = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException uee) {
            throw ExceptionMgr.asSafe(uee);
        }
        writeBytes(bytes);
        writeWholeNum(bytes.length);
    }

    /**
     * Writes out a unicode character as a high-order byte followed by a low
     * order byte.
     */
    void writeChar(char c) {
        writeByte(c & 0xFF);
        writeByte((c >> 8) & 0xFF);
    }

    /**
     * Writes out a float64 as 8 bytes according to {@link
     * Double#doubleToLongBits(double)}, written out most significant byte
     * first.
     */
    void writeFloat64(double float64) {
        long bits = Double.doubleToLongBits(float64);
        for (int i = 0; i < 8; ++i) {
            writeByte((int)(bits >> (i * 8)) & 0xFF);
        }
    }
}
