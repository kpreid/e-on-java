package org.erights.e.elang.smallcaps;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elang.scope.EvalContext;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.base.Ejector;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.tables.EList;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.meta.java.lang.CharacterMakerSugar;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

/**
 * The state of one activation frame in a Smallcaps call stack.
 * <p/>
 * XXX This is an incomplete, broken reference implementation -- fix me!
 *
 * @author Mark S. Miller
 * @author Darius Bacon
 */
public class SmallcapsActivation implements SmallcapsOps {

    /**
     * Needed for list-pattern matching.
     */
    static private final ClassDesc EListGuard = ClassDesc.make(EList.class);

    /**
     * Needed from branching
     */
    static private final ClassDesc BooleanGuard = ClassDesc.make(Boolean.TYPE);

    /**
     *
     */
    private final byte[] myCode;

    /**
     *
     */
    private int myPC;

    /**
     *
     */
    private final Object[] myOperandStack;

    /**
     * The top of the operand stack.
     */
    private int myTOS;

    /**
     *
     */
//    private final SmallcapsHandler[] myHandlerStack;

    /**
     *
     */
    private int myHandlerTOS;

    /**
     *
     */
//    private final EvalContext myCtx;

    /**
     *
     */
    public SmallcapsActivation(byte[] code, int pc, EvalContext ctx) {
        myCode = code; //should clone to make immutable
        myPC = pc;
        myOperandStack = new Object[readWholeInt()];
        myTOS = -1;
//        myHandlerStack = new SmallcapsHandler[readWholeInt()];
        myHandlerTOS = -1;
//        myCtx = ctx;
    }

    /**
     *
     */
    private void push(Object operand) {
        myOperandStack[++myTOS] = operand;
    }

    /**
     *
     */
    private Object pop() {
        Object result = myOperandStack[myTOS];
        myOperandStack[myTOS] = null; // keep GC precise
        myTOS--;
        return result;
    }

    /**
     * @param n How many to pop
     * @return An n-element array of the popped elements, from bottom-most to
     *         top-most.
     */
    private Object[] popN(int n) {
        Object[] result = new Object[n];
        int i = myTOS - n + 1;
        System.arraycopy(myOperandStack, i, result, 0, n);
        // Is there a corresponding primitive for quickly clearing a span of an
        // array?
        for (; i <= myTOS; i++) {
            myOperandStack[i] = null; // keep GC precise
        }
        myTOS -= n;
        return result;
    }

    /**
     * Handle the problem using the top handler on the handler stack.
     *
     * @param problem
     */
    private void handle(Object problem) {
        T.fail("XXX unimplemented");
    }

    /**
     * Handle the problem using optEjector, if it's not null, or the top
     * handler on the handler stack if optEjector is null.
     * <p/>
     * optEjector may include user-defined behavior. If optEjector returns
     * normally, then invoke the top handler on the handler stack to handle
     * this inappropriate return.
     *
     * @param optEjector
     * @param problem
     */
    private void handle(OneArgFunc optEjector, Object problem) {
        T.fail("XXX unimplemented");
    }

    /**
     *
     */
    private void popHandler() {
        myHandlerTOS--;
        T.fail("XXX unimplemented");
    }

    /**
     *
     */
    private void pushHandler() {
        T.fail("XXX unimplemented");
    }

    /**
     * @param ejector
     */
    private void pushHandler(OneArgFunc ejector) {
        T.fail("XXX unimplemented");
    }

    /**
     * @param label
     */
    private void pushTryHandler(int label) {
        T.fail("XXX unimplemented");
    }

    /**
     * @return
     */
    private OneArgFunc makeEjectorOnly(int label) {
        T.fail("XXX unimplemented");
        return null;
    }

    /**
     * @return
     */
    private OneArgFunc makeEjector(int label) {
        T.fail("XXX unimplemented");
        return null;
    }

    /**
     * @see SmallcapsEncoder#writeWholeNum(BigInteger)
     */
    private BigInteger readWholeNum() {
        BigInteger result = BigInteger.ZERO;
        while (true) {
            int byt = myCode[myPC++];
            if (0 == (byt & 0x80)) {
                return result.or(BigInteger.valueOf(byt));
            } else {
                result = result.or(BigInteger.valueOf(byt & 0x7F));
                result = result.shiftLeft(7);
            }
        }
    }

    /**
     * @see SmallcapsEncoder#writeWholeNum(int)
     */
    private int readWholeInt() {
        int result = 0;
        while (true) {
            int byt = myCode[myPC++];
            if (0 == (byt & 0x80)) {
                return result | byt;
            } else {
                result |= byt & 0x7F;

                // The high 7 bits must be zero, because they're about to fall
                // off. The next highest bit must be zero because otherwise
                // it'll turn the number negative.
                T.requireSI(0 == (result & 0xFF000000), "Overflow at: ", myPC);
                result <<= 7;
            }
        }
    }

    /**
     * @return
     */
    private int readLabel() {
        return readWholeInt();
    }

    /**
     * @return
     */
    private String readUTF() {
        int length = readWholeInt();
        String str;
        try {
            str = new String(myCode, myPC, length, "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            throw ExceptionMgr.asSafe(uee);
        }
        myPC += length;
        return str;
    }

    /**
     * @return
     */
    private char readChar() {
        int hi = 0xFF & myCode[myPC];
        int lo = 0xFF & myCode[myPC + 1];
        myPC += 2;
        return (char)((hi << 8) + lo);
    }

    /**
     * @return
     */
    private double readFloat64() {
        long bits = 0;
        for (int i = 0; 8 > i; ++i) {
            bits = (bits << 8) | (0xFF & myCode[myPC + i]);
        }
        myPC += 8;
        return Double.longBitsToDouble(bits);
    }

    /**
     *
     */
    public Object eval() {
        while (true) {
            switch (myCode[myPC++]) {

            case OP_DUP:
                // [x],[] => OP_DUP => [x, x],[]
            {
                Object x = pop();
                push(x);
                push(x);
                break;
            }

            case OP_POP:
                // [x],[] => OP_POP => [],[]
            {
                pop();
                break;
            }

            case OP_SWAP:
                // [x, y],[] => OP_SWAP => [y, x],[]
            {
                Object y = pop();
                Object x = pop();
                push(y);
                push(x);
                break;
            }

            case OP_ROT:
                // [x, y, z],[] => OP_ROT => [y, z, x],[]
            {
                Object z = pop();
                Object y = pop();
                Object x = pop();
                push(y);
                push(z);
                push(x);
                break;
            }

            case OP_RETURN:
                // [x],[] => OP_RETURN
            {
                return pop();
            }

            case OP_JUMP:
                // [],[] => OP_JUMP(label) => [],[]
            {
                int offset = readLabel();
                myPC += offset;
                break;
            }

            case OP_BRANCH:
                // [optEjector, flag],[] => OP_BRANCH => [],[]
            {
                Object flag = pop();
                OneArgFunc optEjector = (OneArgFunc)pop();
                Ejector ej = new Ejector("op_branch");
                Boolean bb;
                try {
                    // This is a proper E coercion, and so may call user
                    // code.
                    bb = (Boolean)BooleanGuard.coerce(flag, ej);
                } catch (Throwable th) {
                    Object problem = ej.result(th);
                    handle(problem);
                    break;
                }
                if (!bb.booleanValue()) {
                    handle(optEjector, "not true");
                }
                break;
            }

            case OP_CALL_ONLY:
                // [recip, args...],[] =>OP_CALL_ONLY(verb, arity)=> [],[]
            {
                String verb = readUTF();
                int arity = readWholeInt();
                Object[] args = popN(arity);
                Object recip = pop();
                E.callAll(recip, verb, args);
                break;
            }

            case OP_CALL:
                // [recip, args...],[] =>OP_CALL(verb, arity)=> [result],[]
            {
                String verb = readUTF();
                int arity = readWholeInt();
                Object[] args = popN(arity);
                Object recip = pop();
                Object result = E.callAll(recip, verb, args);
                push(result);
                break;
            }

            case OP_EJECTOR_ONLY:
                // [],[] => OP_EJECTOR_ONLY(label) => [ejector],[handler]
            {
                int label = readLabel();
                OneArgFunc ejector = makeEjectorOnly(label);
                pushHandler(ejector);
                push(ejector);
                break;
            }

            case OP_EJECTOR:
                // [],[] => OP_EJECTOR(label) => [ejector],[handler]
            {
                int label = readLabel();
                OneArgFunc ejector = makeEjector(label);
                pushHandler(ejector);
                push(ejector);
                break;
            }

            case OP_TRY:
                // [],[] => OP_TRY(label) => [],[handler]
            {
                int label = readLabel();
                pushTryHandler(label);
                break;
            }

            case OP_UNWIND:
                // [],[] => OP_UNWIND(label) => [],[handler]
                // [...],[...] => handler(arg) => [rethrower(arg)],[]
                // [...],[...] => handler.drop(pc) => [returner(pc)],[]
            {
                int label = readLabel();
                T.fail("XXX unimplemented");
                break;
            }

            case OP_END_HANDLER:
                // [],[handler] => OP_END_HANDLER => [],[]
            {
                popHandler();
                break;
            }

            case OP_WHOLE_NUM:
                // [],[] => OP_WHOLE_NUM(wholeNum) => [wholeNum],[]
            {
                push(readWholeNum());
                break;
            }

            case OP_NEG_INT:
                // [],[] => OP_NEG_INT(wholeNum) => [-wholeNum],[]
            {
                push(readWholeNum().negate());
                break;
            }

            case OP_FLOAT64:
                // [],[] => OP_FLOAT64(float64) => [float64],[]
            {
                push(new Double(readFloat64()));
                break;
            }

            case OP_CHAR:
                // [],[] => OP_CHAR(chr) => [chr],[]
            {
                push(CharacterMakerSugar.valueOf(readChar()));
                break;
            }

            case OP_STRING:
                // [],[] => OP_STRING(str) => [str],[]
            {
                push(readUTF());
                break;
            }

            case OP_TWINE:
                // [],[] => OP_TWINE(twine) => [twine],[]
            {
                T.fail("XXX Reserved instruction");
                break;
            }

            case OP_TRUE:
                // [],[] => OP_TRUE => [true],[]
            {
                push(Boolean.TRUE);
                break;
            }

            case OP_FALSE:
                // [],[] => OP_FALSE => [false],[]
            {
                push(Boolean.FALSE);
                break;
            }

            case OP_NULL:
                // [],[] => OP_NULL => [null],[]
            {
                push(null);
                break;
            }

            case OP_SCOPE:
                // [],[] => OP_SCOPE => [scope],[]
            {
                T.fail("XXX unimplemented");
                break;
            }

            case OP_OBJECT:
                // [ivars..., auditors...],[]
                // => OP_OBJECT(<i>seeBelow</i>) => [object],[]
            {
                // XXX to be written
                T.fail("XXX unimplemented");
                break;
            }

            case OP_LIST_PATT:
                // [optEjector, specimen],[] =>OP_LIST(n)=>
                // [optEjector, specimen[n-1], ... optEjector, specimen[0]],[]
            {
                int n = readWholeInt();
                Object specimen = pop();
                OneArgFunc optEjector = (OneArgFunc)pop();
                EList list = (EList)EListGuard.coerce(specimen, optEjector);
                int len = list.size();
                if (len != n) {
                    throw Thrower.toEject(optEjector,
                                          "a " + len +
                                            " size list doesn't match a " + n +
                                            " size list pattern");
                }
                for (int i = n - 1; 0 <= i; i--) {
                    push(optEjector);
                    push(list.get(i));
                }
                break;
            }

            case OP_CDR_PATT:
                // [optEjector, specimen],[] =>OP_CDR_PATT(n)=>
                // [optEjector, specimen(n,specimen.size()),
                //  optEjector, specimen[n-1], ... optEjector, specimen[0]],[]
            {
                int n = readWholeInt();
                Object specimen = pop();
                OneArgFunc optEjector = (OneArgFunc)pop();
                EList list = (EList)EListGuard.coerce(specimen, optEjector);
                int len = list.size();
                String problem = "a " + len +
                  " size list doesn't match a >= " + n + " size list pattern";
                if (len < n) {
                    throw Thrower.toEject(optEjector, problem);
                }
                push(optEjector);
                push(list.run(n, len));
                for (int i = n - 1; 0 <= i; i--) {
                    push(optEjector);
                    push(list.get(i));
                }
                break;
            }

            case OP_NOUN + ADDR_FRAME:
            case OP_NOUN + ADDR_FRAME_SLOT:
            case OP_NOUN + ADDR_LOCAL:
            case OP_NOUN + ADDR_LOCAL_SLOT:
            case OP_NOUN + ADDR_LITERAL: // XXX what's this for?
            case OP_NOUN + ADDR_OUTER_SLOT:
                // [],[] => (OP_NOUN+addrMode)(index) => [value],[]
            {
                T.fail("XXX unimplemented");
                break;
            }

            case OP_SLOT + ADDR_FRAME:
            case OP_SLOT + ADDR_FRAME_SLOT:
            case OP_SLOT + ADDR_LOCAL:
            case OP_SLOT + ADDR_LOCAL_SLOT:
            case OP_SLOT + ADDR_LITERAL: // XXX what's this for?
            case OP_SLOT + ADDR_OUTER_SLOT:
                // [],[] => (OP_SLOT+addrMode)(index) => [slot],[]
            {
                T.fail("XXX unimplemented");
                break;
            }

            case OP_ASSIGN + ADDR_FRAME:
            case OP_ASSIGN + ADDR_FRAME_SLOT:
            case OP_ASSIGN + ADDR_LOCAL:
            case OP_ASSIGN + ADDR_LOCAL_SLOT:
            case OP_ASSIGN + ADDR_LITERAL: // XXX what's this for?
            case OP_ASSIGN + ADDR_OUTER_SLOT:
                // [rValue],[] => (OP_ASSIGN+addrMode)(index) => [],[]
            {
                T.fail("XXX unimplemented");
                break;
            }

            case OP_BIND + ADDR_FRAME:
            case OP_BIND + ADDR_FRAME_SLOT:
            case OP_BIND + ADDR_LOCAL:
            case OP_BIND + ADDR_LOCAL_SLOT:
            case OP_BIND + ADDR_LITERAL: // XXX what's this for?
            case OP_BIND + ADDR_OUTER_SLOT:
                // [rValue],[] => (OP_BIND+addrMode)(index) => [],[]
            {
                T.fail("XXX unimplemented");
                break;
            }

            case OP_BIND_SLOT + ADDR_FRAME:
            case OP_BIND_SLOT + ADDR_FRAME_SLOT:
            case OP_BIND_SLOT + ADDR_LOCAL:
            case OP_BIND_SLOT + ADDR_LOCAL_SLOT:
            case OP_BIND_SLOT + ADDR_LITERAL: // XXX what's this for?
            case OP_BIND_SLOT + ADDR_OUTER_SLOT:
                // [rValue],[] => (OP_BIND_SLOT+addrMode)(index) => [],[]
            {
                T.fail("XXX unimplemented");
                break;
            }

            default:
                T.fail("Unknown instruction");
            }
        }
    }
}
