// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elang.visitors;

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elang.evm.AtomicExpr;
import org.erights.e.elang.evm.AuditorExprs;
import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.evm.EMatcher;
import org.erights.e.elang.evm.EMethod;
import org.erights.e.elang.evm.ENode;
import org.erights.e.elang.evm.EScript;
import org.erights.e.elang.evm.GuardedPattern;
import org.erights.e.elang.evm.LiteralExpr;
import org.erights.e.elang.evm.Pattern;
import org.erights.e.elib.oldeio.TextWriter;

import java.io.IOException;

/**
 * @author Mark S. Miller
 */
public class CapSlang2JVisitor implements ETreeVisitor {

    private final TextWriter myOut;

    public CapSlang2JVisitor(TextWriter out) {
        myOut = out;
    }

    public void run(Object obj) {
        if (obj instanceof ENode) {
            ((ENode)obj).welcome(this);
        } else {
            try {
                myOut.print((String)obj);
            } catch (IOException e) {
                throw ExceptionMgr.asSafe(e);
            }
        }
    }

    private void run(Object a, Object b) {
        run(a);
        run(b);
    }

    private void run(Object a, Object b, Object c) {
        run(a);
        run(b);
        run(c);
    }

    private void run(Object a, Object b, Object c, Object d) {
        run(a);
        run(b);
        run(c);
        run(d);
    }

    private void run(Object a, Object b, Object c, Object d, Object e) {
        run(a);
        run(b);
        run(c);
        run(d);
        run(e);
    }

    private void run(Object a,
                     Object b,
                     Object c,
                     Object d,
                     Object e,
                     Object f) {
        run(a);
        run(b);
        run(c);
        run(d);
        run(e);
        run(f);
    }

    private void runAll(ENode[] eNodes, String sep) {
        int len = eNodes.length;
        if (1 <= len) {
            run(eNodes[0]);
            for (int i = 1; i < len; i++) {
                run(sep, eNodes[i]);
            }
        }
    }

    private void block(Object obj) {
        run(" {\n");
        new CapSlang2JVisitor(myOut.indent()).run(obj);
        run(";\n}");
    }

    private void doco(String docComment) {
        run("/**");
        new CapSlang2JVisitor(myOut.indent(" * ")).run("\n", docComment);
        run("\n */");
    }

    private void typeExpr(EExpr optGuardExpr) {
        T.fail("XXX not yet implemented");
    }

    public Object visitAssignExpr(ENode optOriginal,
                                  AtomicExpr noun,
                                  EExpr rValue) {
        run("(", noun, " = ", rValue, ")");
        return null;
    }

    public Object visitCallExpr(ENode optOriginal,
                                EExpr recip,
                                String verb,
                                EExpr[] args) {
        run("(", recip, ".", verb, "(");
        runAll(args, ", ");
        run("))");
        return null;
    }

    public Object visitCatchExpr(ENode optOriginal,
                                 EExpr attempt,
                                 Pattern patt,
                                 EExpr catcher) {
        run("try");
        block(attempt);
        run(" catch(", patt, ")\n");
        block(catcher);
        return null;
    }

    public Object visitDefineExpr(ENode optOriginal,
                                  Pattern patt,
                                  EExpr optEjectorExpr,
                                  EExpr rValue) {
        if (null != optEjectorExpr) {
            T.fail("XXX Not yet implemented");
        }
        run(patt, " = ", rValue, ";");
        return null;
    }

    public Object visitEMethod(ENode optOriginal,
                               String docComment,
                               String verb,
                               Pattern[] patterns,
                               EExpr optResultGuard,
                               EExpr body) {
        doco(docComment);
        run("\npublic ");
        typeExpr(optResultGuard);
        run(verb, "(");
        runAll(patterns, ", ");
        run(")");
        block(body);
        return null;
    }

    public Object visitEscapeExpr(ENode optOriginal,
                                  Pattern hatch,
                                  EExpr body,
                                  Pattern optArgPattern,
                                  EExpr optCatcher) {
        T.fail("escape not (yet?) supported in CapSlang");
        return null;
    }

    public Object visitEScript(ENode optOriginal,
                               EMethod[] optMethods,
                               EMatcher[] matchers) {
        T.notNull(optMethods, "plumbing not (yet?) supported in CapSlang");
        for (int i = 0, len = optMethods.length; i < len; i++) {
            run(optMethods[i], "\n");
        }
        for (int i = 0, len = matchers.length; i < len; i++) {
            run(matchers[i], "\n");
        }
        return null;
    }

    public Object visitFinallyExpr(ENode optOriginal,
                                   EExpr attempt,
                                   EExpr unwinder) {
        run("try");
        block(attempt);
        run(" finally");
        block(unwinder);
        T.fail("XXX not yet implemented");
        return null;
    }

    public Object visitFinalPattern(ENode optOriginal,
                                    AtomicExpr nounExpr,
                                    EExpr optGuardExpr) {
        run("final ");
        run(" ", nounExpr);
        T.fail("XXX not yet implemented");
        return null;
    }

    public Object visitIgnorePattern(ENode optOriginal, EExpr optGuardExpr) {
        T.fail("Ignore pattern ('_') not (yet?) supported in CapSlang");
        T.fail("XXX not yet implemented");
        return null;
    }

    public Object visitViaPattern(ENode optOriginal,
                                  EExpr viaExpr,
                                  Pattern subPattern) {
        T.fail("XXX not yet implemented");
        return null; //make compiler happy
    }

    public Object visitHideExpr(ENode optOriginal, EExpr body) {
        block(body);
        return null;
    }

    public Object visitIfExpr(ENode optOriginal,
                              EExpr test,
                              EExpr then,
                              EExpr els) {
        run("if (", test, ")");
        block(then);
        run(" else");
        block(els);
        return null;
    }

    public Object visitListPattern(ENode optOriginal, Pattern[] subs) {
        T.fail("List pattern not (yet?) supported in CapSlang");
        return null;
    }

    public Object visitLiteralExpr(ENode optOriginal, Object value) {
        run(((LiteralExpr)optOriginal).printRep());
        return null;
    }

    public Object visitEMatcher(ENode optOriginal,
                                Pattern pattern,
                                EExpr body) {
        T.fail("EMatcher not yet supported in CapSlang");
        return null;
    }

    public Object visitNounExpr(ENode optOriginal, String varName) {
        run(varName);
        return null;
    }

    public Object visitObjectExpr(ENode optOriginal,
                                  String docComment,
                                  GuardedPattern oName,
                                  AuditorExprs auditors,
                                  EScript eScript) {
        doco(docComment);
        run("\npublic class ");
        //XXX last part of qualifiedName
        run(auditors);
        run(eScript);
        return null;
    }

    public Object visitAuditorExprs(ENode optOriginal,
                                    EExpr optAs,
                                    EExpr[] impls) {
        // XXX should optAs be treated as another impl?
        int numImpls = impls.length;
        if (1 <= numImpls) {
            run(" implements ");
            typeExpr(impls[0]);
            for (int i = 1; i < numImpls; i++) {
                run(", ");
                typeExpr(impls[i]);
            }
        }
        return null;
    }

    public Object visitQuasiLiteralExpr(ENode optOriginal, int index) {
        T.fail("Quasis not allowed here in CapSlang");
        return null;
    }

    public Object visitQuasiLiteralPatt(ENode optOriginal, int index) {
        T.fail("Quasis not allowed here in CapSlang");
        return null;
    }

    public Object visitQuasiPatternExpr(ENode optOriginal, int index) {
        T.fail("Quasis not allowed here in CapSlang");
        return null;
    }

    public Object visitQuasiPatternPatt(ENode optOriginal, int index) {
        T.fail("Quasis not allowed here in CapSlang");
        return null;
    }

    public Object visitMetaStateExpr(ENode optOriginal) {
        T.fail("meta.getState() not supported in CapSlang");
        return null;
    }

    public Object visitMetaContextExpr(ENode optOriginal) {
        T.fail("meta.context() not supported in CapSlang");
        return null;
    }

    public Object visitSeqExpr(ENode optOriginal, EExpr[] subs) {
        runAll(subs, ";\n");
        return null;
    }

    public Object visitSlotExpr(ENode optOriginal, AtomicExpr noun) {
        T.fail("SlotExpr ('&name') not supported in CapSlang");
        return null;
    }

    public Object visitBindingExpr(ENode optOriginal, AtomicExpr noun) {
        T.fail("BindingExpr ('&&name') not supported in CapSlang");
        return null;
    }

    public Object visitVarPattern(ENode optOriginal,
                                  AtomicExpr nounExpr,
                                  EExpr optGuardExpr) {
        T.fail("Not yet implemented");
        return null;
    }

    public Object visitSlotPattern(ENode optOriginal,
                                   AtomicExpr nounExpr,
                                   EExpr optGuardExpr) {
        T.fail("Not yet implemented");
        return null;
    }

    public Object visitBindingPattern(ENode optOriginal,
                                      AtomicExpr nounExpr) {
        T.fail("Not yet implemented");
        return null;
    }
}
