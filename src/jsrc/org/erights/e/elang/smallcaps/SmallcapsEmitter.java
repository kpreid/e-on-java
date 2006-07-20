package org.erights.e.elang.smallcaps;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.evm.EScript;
import org.erights.e.elib.base.SourceSpan;

import java.math.BigInteger;

/**
 * A code generator driven by abstract Smallcaps instructions.
 * <p>
 * Besides the obvious direct realization, a JVM code generator should
 * be feasible.
 *
 * @author Mark S. Miller
 * @author Darius Bacon
 */
public interface SmallcapsEmitter {

    /**
     * Return a value representing the current position in the code
     * buffer.
     * <p>
     * This is *not* an address; it's an opaque value used as an argument to
     * emitJump(), etc.
     */
    int getLabel();

    /**
     * Record that `span' in source produced lowLabel..!highLabel.
     */
    void associateSourceLocation(int lowLabel, int highLabel, SourceSpan span);

    /**
     * Emit the header preceding the top of an allocation contour, such as a
     * top-level expression, a method head & body, or a matcher head & body.
     */
    void emitContourTop();

    // Each remaining emit method prepends one Smallcaps instruction
    // to the current instruction buffer. The instructions should be
    // emitted from back to front.

    void emitDup();

    void emitPop();

    void emitSwap();

    void emitRot();

    void emitJump(int label);

    void emitBranch();

    void emitCall(String verb, int arity, boolean only);

    void emitEjectorOnly(int label);

    void emitEjector(int label);

    void emitTry(int label);

    void emitUnwind(int label);

    void emitEndHandler();

    void emitInteger(BigInteger integer); // OP_WHOLE_NUM and OP_NEG_INT

    void emitFloat64(double float64);

    void emitChar(char chr);

    void emitString(String str);

    //    void emitTwine(Twine twine); XXX fill in
    void emitTrue();

    void emitFalse();

    void emitNull();

    // XXX for ADDR_LITERAL. But what's that for, exactly?
    void emitLiteral(Object obj);

    void emitScope();

    // XXX fill in
    void emitObject(String docComment,
                    String fqName,
                    EExpr[] auditors,
                    EScript eScript);

    void emitListPatt(int n);

    void emitCdrPatt(int n);

    // XXX or something
    void emitNoun(int addressingMode, int index, boolean slot);

    void emitAssign(int addressingMode, int index);

    void emitBind(int addressingMode, int index, boolean slot);
}
